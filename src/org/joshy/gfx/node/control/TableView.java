package org.joshy.gfx.node.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.SkinManager;
import org.joshy.gfx.css.CSSMatcher;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.draw.GradientFill;
import org.joshy.gfx.event.*;
import org.joshy.gfx.event.Event;
import org.joshy.gfx.node.Bounds;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

/** A table with columns and rows. Used for displaying tabular data. it has a model
 * for the columns and a model for the actual data. Styling can be done with
 * item renderers and text renderers just like the ListView. 
 */
public class TableView extends Control implements Focusable, ScrollPane.ScrollingAware, SelectableControl {
    private static final int HEADER_HEIGHT = 20;
    private static final double RESIZE_THRESHOLD = 10;
    private TableModel model;
    private DataRenderer renderer;
    private int selectedRow = -1;
    private HeaderRenderer headerRenderer;
    private int selectedColumn = -1;
    private boolean focused;
    private double defaultColumnWidth = 50;
    private double scrollY = 0;
    private double scrollX = 0;
    final double rowHeight = 20;
    private ScrollPane scrollPane;
    private Font font;
    private Map<Integer, Double> columnSizes = new HashMap<Integer,Double>();

    public static enum ResizeMode {
        Proportional,
        Manual
    }
    private static ResizeMode resizeMode = ResizeMode.Proportional;

    private void setCursor(Cursor cursor) {
        Frame frame = (Frame) getParent().getStage().getNativeWindow();
        frame.setCursor(cursor);
    }

    private void setDefaultCursor() {
        Frame frame = (Frame) getParent().getStage().getNativeWindow();
        frame.setCursor(Cursor.getDefaultCursor());
    }

    private Cursor resizeCursor = Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
    private Callback<? extends Event> mouseListener = new Callback<MouseEvent>(){
        public int resizeColumnLeft = -1;
        public Point2D start;

        public void call(MouseEvent event) {
            if(event.getType() == MouseEvent.MouseMoved) {
                if(event.getY() < HEADER_HEIGHT) {
                    if(isOverLeftColumnEdge(event) || isOverRightColumnEdge(event)) {
                        setCursor(resizeCursor);
                    } else {
                        setDefaultCursor();
                    }
                }
            }

            if(event.getType() == MouseEvent.MousePressed) {
                if(event.getY() < HEADER_HEIGHT) {
                    setSelectedColumn(mouseToColumn(event));
                    if(isOverLeftColumnEdge(event)) {
                        resizeColumnLeft = mouseToColumn(event)-1;
                        start = event.getPointInNodeCoords(TableView.this);
                    }
                    if(isOverRightColumnEdge(event)) {
                        resizeColumnLeft = mouseToColumn(event);
                        start = event.getPointInNodeCoords(TableView.this);
                    }
                } else {
                    int startRow = (int)(-scrollY/rowHeight);
                    setSelectedRow((int)((event.getY()- HEADER_HEIGHT)/getRowHeight())+startRow);
                }
                Core.getShared().getFocusManager().setFocusedNode(TableView.this);
            }

            if(event.getType() == MouseEvent.MouseDragged && resizeColumnLeft != -1) {
                Point2D current = event.getPointInNodeCoords(TableView.this);
                double dx = current.getX()-start.getX();
                start = current;
                columnSizes.put(resizeColumnLeft,columnSizes.get(resizeColumnLeft)+dx);
                setDrawingDirty();
            }
            
            if(event.getType() == MouseEvent.MouseReleased) {
                resizeColumnLeft = -1;
            }
        }

        private int mouseToColumn(MouseEvent event) {
            if(resizeMode == ResizeMode.Proportional) {
                double columnWidth = getWidth() / model.getColumnCount();
                int column = (int)(event.getX()/columnWidth);
                return column;
            }
            if(resizeMode == ResizeMode.Manual) {
                double x = 0;
                for(int col = 0; col< getModel().getColumnCount(); col++) {
                    double w = getColumnWidth(col);
                    if(event.getX()>=x && event.getX()<x+w) {
                        return col;
                    }
                    x+=w;
                }                   
            }
            return -1;
        }
    };


    private boolean isOverRightColumnEdge(MouseEvent event) {
        if(resizeMode == ResizeMode.Proportional){
            double columnWidth = getWidth() / model.getColumnCount();
            double my = event.getX() % columnWidth;
            if(my > columnWidth-5 && my <= columnWidth) {
                return true;
            }
            return false;
        }
        if(resizeMode == ResizeMode.Manual) {
            double x = 0;
            for(int col = 0; col< getModel().getColumnCount(); col++) {
                double w = getColumnWidth(col);
                if(event.getX()>x+w- RESIZE_THRESHOLD && event.getX()<x+w) {
                    return true;
                }
                x+=w;
            }
        }
        return false;
    }

    private boolean isOverLeftColumnEdge(MouseEvent event) {
        if(resizeMode == ResizeMode.Proportional){
            double columnWidth = getWidth() / model.getColumnCount();
            double my = event.getX() % columnWidth;
            if(my >= 0 && my < 10) {
                return true;
            }
            return false;
        }
        if(resizeMode == ResizeMode.Manual) {
            double x = 0;
            for(int col = 0; col< getModel().getColumnCount(); col++) {
                double w = getColumnWidth(col);
                if(event.getX()>=x && event.getX()<x+ RESIZE_THRESHOLD) {
                    return true;
                }
                x+=w;
            }
        }
        return false;
    }

    public TableView() {
        setWidth(300);
        setHeight(200);

        //set default model
        setModel(new TableModel() {
            public int getRowCount() {
                return 20;
            }

            public int getColumnCount() {
                return 3;
            }

            public Object getColumnHeader(int column) {
                return "Column " + column;
            }

            public Object get(int row, int column) {
                return "Data " + row + "," + column;
            }
        });

        //set default renderer
        setRenderer(new DataRenderer() {
            public void draw(GFX g, TableView table, Object cell, int row, int column, double x, double y, double width, double height) {
                if(cssSkin != null) {
                    CSSMatcher matcher = new CSSMatcher(table);
                    Bounds bounds = new Bounds(x,y,width,height);
                    String prefix = "item-";
                    if(getSelectedIndex() == row) {
                        prefix = "selected-item-";
                    }
                    cssSkin.drawBackground(g,matcher,prefix,bounds);
                    cssSkin.drawBorder(g,matcher,prefix,bounds);
                    int col = cssSkin.getCSSSet().findColorValue(matcher, prefix + "color");
                    g.setPaint(new FlatColor(col));
                    if(cell != null) {
                        String s = cell.toString();
                        Font.drawCenteredVertically(g, s, font, x+2, y, width, height, true);
                    }
                    return;
                }

                g.setPaint(FlatColor.WHITE);
                if(row % 2 == 0) {
                    g.setPaint(new FlatColor("#eeeeee"));
                }
                if(row == table.getSelectedRow()) {
                    if(table.focused) {
                        g.setPaint(new FlatColor("#ddddff"));
                    } else {
                        g.setPaint(new FlatColor("#dddddd"));
                    }
                }
                g.fillRect(x,y,width,height);
                g.setPaint(FlatColor.BLACK);
                if(cell != null) {
                    Font.drawCenteredVertically(g, cell.toString(), Font.DEFAULT, x+2, y, width, height, true);
                }
                g.setPaint(new FlatColor("#d0d0d0"));
                g.drawLine(x+width-1,y, x+width-1,y+height);
            }
        });

        setHeaderRenderer(new HeaderRenderer() {
            public void draw(GFX g, TableView table, Object header, int column, double x, double y, double width, double height) {
                if(column == table.getSelectedColumn()) {
                    GradientFill grad = new GradientFill(new FlatColor(0xf0f0ff),new FlatColor(0x6060ff),
                            0,true,0,0,0,height);
                    g.setPaint(grad);
                } else {
                    GradientFill grad = new GradientFill(new FlatColor(0xffffff),new FlatColor(0xa0a0a0),
                            0,true,0,0,0,height);
                    g.setPaint(grad);
                }
                g.fillRect(x,y,width,height);
                g.setPaint(new FlatColor(0x808080));
                g.drawRect(x,y,width,height);
                g.setPaint(FlatColor.BLACK);
                if(header != null) {
                    Font.drawCenteredVertically(g, header.toString(), Font.DEFAULT, x+2, y, width, height, true);
                }
            }
        });

        // click listener
        EventBus.getSystem().addListener(this, MouseEvent.MouseAll, mouseListener);

        EventBus.getSystem().addListener(FocusEvent.All, new Callback<FocusEvent>(){
            public void call(FocusEvent event) {
                if(event.getType() == FocusEvent.Lost && event.getSource() == TableView.this) {
                    focused = false;
                    setDrawingDirty();
                }
                if(event.getType() == FocusEvent.Gained && event.getSource() == TableView.this) {
                    focused = true;
                    setDrawingDirty();
                }
            }
        });

        //keyboard listener
        EventBus.getSystem().addListener(this, KeyEvent.KeyPressed, new Callback<KeyEvent>() {
            public void call(KeyEvent event) {
                //check for focus changes
                if(event.getKeyCode() == KeyEvent.KeyCode.KEY_TAB) {
                    if(event.isShiftPressed()) {
                        Core.getShared().getFocusManager().gotoPrevFocusableNode();
                    } else {
                        Core.getShared().getFocusManager().gotoNextFocusableNode();
                    }
                }
                //check for arrow up and down
                if(event.getKeyCode() == KeyEvent.KeyCode.KEY_DOWN_ARROW) {
                    int index = getSelectedRow()+1;
                    if(index < getModel().getRowCount()) {
                        setSelectedRow(index);
                    }
                }
                if(event.getKeyCode() == KeyEvent.KeyCode.KEY_UP_ARROW) {
                    int index = getSelectedRow()-1;
                    if(index >= 0) {
                        setSelectedRow(index);
                    }
                }
            }
        });

        setDefaultColumnWidth(100);
        setResizeMode(ResizeMode.Manual);
    }

    private void setResizeMode(ResizeMode newMode) {
        if(resizeMode == ResizeMode.Proportional && newMode == ResizeMode.Manual) {
            columnSizes.clear();// = new HashMap<Integer,Double>();
        }
        resizeMode = newMode;
        setDrawingDirty();
    }

    private void setSelectedColumn(int selectedColumn) {
        this.selectedColumn = selectedColumn;
        setDrawingDirty();
    }

    public int getSelectedColumn() {
        return selectedColumn;
    }

    private void setHeaderRenderer(HeaderRenderer headerRenderer) {
        this.headerRenderer = headerRenderer;
    }

    private void setSelectedRow(int row) {
        if(row >= 0 && row < getModel().getRowCount()) {
            selectedRow = row;
        } else {
            selectedRow = -1;
        }
        if(scrollPane != null) {
            Bounds bounds = new Bounds(0,
                    getSelectedRow()*rowHeight,
                    getWidth(),
                    rowHeight+ HEADER_HEIGHT);
            scrollPane.scrollToShow(bounds);
        }
        EventBus.getSystem().publish(new SelectionEvent(SelectionEvent.Changed,this));
        setDrawingDirty();        
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public TableModel getModel() {
        return model;
    }

    public void setRenderer(DataRenderer renderer) {
        this.renderer = renderer;
    }

    public void setModel(TableModel model) {
        this.model = model;
    }


    @Override
    public void doSkins() {
        cssSkin = SkinManager.getShared().getCSSSkin();
        font = cssSkin.getDefaultFont();
        setLayoutDirty();
    }

    @Override
    public void doPrefLayout() {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public void doLayout() {
        //do nothing. width and height are purely controlled by the container
    }

    @Override
    public void draw(GFX g) {
        if(!isVisible()) return;
        CSSMatcher matcher = new CSSMatcher(this);

        Bounds clip = g.getClipRect();
        g.setClipRect(new Bounds(0,0,width,height));

        //draw bg
        if(cssSkin != null) {
            cssSkin.drawBackground(g,matcher,"",new Bounds(0,0,width,height));
        } else {
            g.setPaint(FlatColor.WHITE);
            g.fillRect(0,0,width,height);
        }

        //draw headers
        if(resizeMode == ResizeMode.Proportional) {
            double columnWidth = getWidth()/model.getColumnCount();
            for(int col = 0; col<model.getColumnCount(); col++) {
                Object header = model.getColumnHeader(col);
                headerRenderer.draw(g, this, header, col, col*columnWidth+scrollX, 0, columnWidth, HEADER_HEIGHT);
            }
        }
        if(resizeMode == ResizeMode.Manual) {
            double x = 0;
            for(int col = 0; col<model.getColumnCount(); col++) {
                Object header = model.getColumnHeader(col);
                double columnWidth = getColumnWidth(col);
                headerRenderer.draw(g, this, header, col, x+scrollX, 0, columnWidth, HEADER_HEIGHT);
                x += columnWidth;
            }
        }

        //draw data

        int startRow = (int)(-scrollY/rowHeight);
        for(int row=0; row*rowHeight+ HEADER_HEIGHT < getHeight(); row++) {
            double x = 0;
            for(int col=0; col<model.getColumnCount(); col++) {
                Object item = null;
                double columnWidth = getColumnWidth(col);
                if(row+startRow < model.getRowCount()) {
                    item = model.get(row+startRow,col);
                }
                renderer.draw(g, this, item,
                        row+startRow, col,
                        (int)(x+scrollX),
                        (int)(row*rowHeight+1+HEADER_HEIGHT),
                        (int)columnWidth, rowHeight);
                x+=columnWidth;
            }
        }


        //draw border
        g.setClipRect(clip);
        if(cssSkin != null) {
            cssSkin.drawBorder(g,matcher,"",new Bounds(0,0,width,height));
        }
    }

    private double getColumnWidth(int col) {
        if(resizeMode == ResizeMode.Proportional) {
            return getWidth()/getModel().getColumnCount();
        }
        if(!columnSizes.containsKey(col)) {
            columnSizes.put(col,defaultColumnWidth);
        }
        return columnSizes.get(col);
    }

    public boolean isFocused() {
        return focused;
    }

    public void setDefaultColumnWidth(double defaultColumnWidth) {
        this.defaultColumnWidth = defaultColumnWidth;
        setWidth(getModel().getColumnCount()*defaultColumnWidth);
    }

    public double getFullWidth(double width, double height) {
        return getWidth();
    }

    public double getFullHeight(double width, double height) {
        return Math.max(getModel().getRowCount()*rowHeight,height);
    }

    public void setScrollX(double value) {
        this.scrollX = value;
    }

    public void setScrollY(double value) {
        this.scrollY = value;
    }

    public void setScrollParent(ScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    public int getSelectedIndex() {
        return getSelectedRow();
    }

    public double getRowHeight() {
        return rowHeight;
    }


    public static interface TableModel<D,H> {
        public int getRowCount();
        public int getColumnCount();
        public H getColumnHeader(int column);

        /**
         * Return the data item at the specified row and createColumn. May return null.
         * @param row
         * @param column
         * @return the data at the specified row and createColumn. May be null.
         */
        public D get(int row, int column);
    }

    public static interface HeaderRenderer<H> {
        public void draw(GFX g, TableView table, H header, int column, double x, double y, double width, double height);
    }
    
    public static interface DataRenderer<D> {
        /**
         *
         * @param g graphics context
         * @param table the table view
         * @param cellData the cell data, may be null
         * @param row
         * @param column
         * @param x
         * @param y
         * @param width
         * @param height
         */
        public void draw(GFX g, TableView table, D cellData, int row, int column, double x, double y, double width, double height);
    }
}
