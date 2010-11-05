package org.joshy.gfx.node.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.SkinManager;
import org.joshy.gfx.css.StyleInfo;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.event.*;
import org.joshy.gfx.node.Insets;
import org.joshy.gfx.util.OSUtil;
import org.joshy.gfx.util.u;

import java.util.Date;

/*
 redesign text control to fix all of the bugs
 first, there should be a text model which holds the actual strings of text
 and converts between the various locations

 convert an x y pixel coordinate to a character point
 convertXYToCharPoint()

 convert a character point to the line or createColumn
 getLine(cp)
 getColumn(cp)

 delete or insert a char by typing:
 cursor == current char point
 model.insertChar(cursor,char);
 model.deleteChar(cursor);

 move cursor
 cursor = current char point
 cursor.advanceColumn()

 cursor.getX()
 cursor.getY()

 the text model simply stores lines of text and allows their editing
 the char point converts between screen coords to row & createColumn
 the textcontrol implements the actual drawing and input handleing 
*/


public abstract class TextControl extends Control implements Focusable {
    protected boolean focused;
    protected String text = "";

    protected boolean allowMultiLine = false;
    private Font realFont;
    protected TextSelection selection;
    TextLayoutModel _layout_model;
    private CursorPosition cursor;
    protected StyleInfo styleInfo;


    protected TextControl() {
        cursor = new CursorPosition();
        selection = new TextSelection();

        EventBus.getSystem().addListener(this, MouseEvent.MousePressed, new Callback<MouseEvent>(){
            public void call(MouseEvent event) {
                Core.getShared().getFocusManager().setFocusedNode(TextControl.this);
                if(selection.isActive() && !event.isShiftPressed()) {
                    selection.clear();
                }
                /*
                if(text.length() >= 1) {
                    double ex = filterMouseX(event.getX());
                    double ey = filterMouseY(event.getY());
                    currentCursorPoint = mouseXYToCursorPoint(ex,ey,text);
                } else {
                    Font font = getFont();
                    double cursorH = font.getAscender() + font.getDescender();
                    currentCursorPoint = new CursorPoint(0,0,1,cursorH,0,0,0);
                }
                if(!selection.isActive()) {
                    selection.setStart(currentCursorPoint);
                }
                if(event.isShiftPressed() && selection.isActive()) {
                    selection.setEnd(currentCursorPoint);
                } */
            }
        });
/*
        EventBus.getSystem().addListener(this, MouseEvent.MouseDragged, new Callback<MouseEvent>(){
            public void call(MouseEvent event) {
                Core.getShared().getFocusManager().setFocusedNode(TextControl.this);
                if(text.length() >= 1) {
                    currentCursorPoint = mouseXYToCursorPoint(event.getX(),event.getY(),text);
                } else {
                    currentCursorPoint = new CursorPoint(0,0,1,20,0,0,0);
                }
                if(selection.isActive()) {
                    selection.setEnd(currentCursorPoint);
                }
            }
        });
  */
        EventBus.getSystem().addListener(FocusEvent.All, new Callback<FocusEvent>(){
            public void call(FocusEvent event) {
                if(event.getType() == FocusEvent.Lost && event.getSource() == TextControl.this) {
                    focused = false;
                    setLayoutDirty();
                }
                if(event.getType() == FocusEvent.Gained && event.getSource() == TextControl.this) {
                    focused = true;
                    setLayoutDirty();
                }
            }
        });
        
        EventBus.getSystem().addListener(this, KeyEvent.KeyPressed, new Callback<KeyEvent>() {
            public void call(KeyEvent event) {
                processKeyEvent(event);
            }
        });

        EventBus.getSystem().addListener(this, MouseEvent.MouseReleased, new Callback<MouseEvent>() {
            public long lastRelease = 0;
            public int clickCount = 0;

            public void call(MouseEvent event) {
                long now = new Date().getTime();
                if(now-lastRelease < 400) {
                } else {
                    clickCount = 0;
                }
                clickCount++;
                lastRelease = now;
                if(clickCount == 3) {
                    selectAll();
                }
            }
        });
    }

    protected double filterMouseY(double y) {
        return y;
    }

    protected double filterMouseX(double x) {
        return x;
    }

    protected void processKeyEvent(KeyEvent event) {

        if(event.getKeyCode().equals(KeyEvent.KeyCode.KEY_V) && event.isSystemPressed()) {
            insertText(OSUtil.getClipboardAsString());
            return;
        }
        
        if(event.isTextKey()) {
            insertText(event.getGeneratedText());
            return;
        }
        /*
        if(event.getKeyCode() == KeyEvent.KeyCode.KEY_ENTER && allowMultiLine) {
            if(selection.isActive()) {
                replaceAndClearSelectionWith("\n");
            } else {
                insertAtCursor("\n");
            }
            return;
        }
        */

        if(event.getKeyCode() == KeyEvent.KeyCode.KEY_BACKSPACE) {
            //if at start, do nothing
            if(cursor.getRow() == 0 && cursor.getCol()==0) return;
            String t = getText();
            //if text is empty do nothing
            if(t.length() == 0) return;

            if(selection.isActive()) {
                String[] parts = cursor.splitSelectedText();
                setText(parts[0]+parts[2]);
                cursor.setIndex(parts[0].length());
                selection.clear();
            } else {
                //split, then remove text in the middle
                String[] parts = cursor.splitText();
                parts[0] = parts[0].substring(0,cursor.getIndex()-1);
                setText(parts[0]+parts[1]);
                cursor.moveLeft(1);
            }
            return;
        }
        /*
        if(event.getKeyCode() == KeyEvent.KeyCode.KEY_DELETE) {
            if(text.length() > 0 && (cursorCharX < text.length()-1)) {
                if(selection.isActive()) {
                    replaceAndClearSelectionWith("");
                } else {
                    String beforeString = text.substring(0, cursorCharX);
                    String afterString = text.substring(cursorCharX+1,text.length());
                    text = beforeString + afterString;
                    currentCursorPoint = cursorCharToCursorPoint(cursorCharX,text);
                    EventBus.getSystem().publish(new ChangedEvent(ChangedEvent.StringChanged,text,TextControl.this));
                    setDrawingDirty();
                }
            }
            return;
        }
        */

        if(event.getKeyCode() == KeyEvent.KeyCode.KEY_LEFT_ARROW) {
            if(event.isShiftPressed()) {
                if(!selection.isActive()) {
                    selection.clear();
                    selection.direction = TextSelection.LEFT;
                    selection.setStart(cursor.getIndex());
                    selection.setEnd(cursor.getIndex());
                }
                //extend selection only if this wouldn't make us wrap backward
                if(!cursor.atStartOfLine()) {
                    selection.addLeft(1);
                }
            } else {
                selection.clear();
            }
            cursor.moveLeft(1);
            setDrawingDirty();
        }
        
        if(event.getKeyCode() == KeyEvent.KeyCode.KEY_RIGHT_ARROW) {
            if(event.isShiftPressed()) {
                if(!selection.isActive()) {
                    selection.clear();
                    selection.direction = TextSelection.RIGHT;
                    selection.setStart(cursor.getIndex());
                    selection.setEnd(cursor.getIndex());
                }
                //extend selection only it this wouldn't make us wrap forward
                if(!cursor.atEndOfLine()) {
                    selection.addRight(1);
                }
            } else {
                selection.clear();
            }
            cursor.moveRight(1);
            setDrawingDirty();
        }
        
        if(event.getKeyCode() == KeyEvent.KeyCode.KEY_UP_ARROW) {
            if(!allowMultiLine) {
                //just move to start
                cursor.moveStart();
                setDrawingDirty();
            }
        }

        if(event.getKeyCode() == KeyEvent.KeyCode.KEY_DOWN_ARROW) {
            if(!allowMultiLine) {
                //just move to end
                cursor.moveEnd();
                setDrawingDirty();
            }
        }

        if(event.getKeyCode() == KeyEvent.KeyCode.KEY_TAB) {
            if(event.isShiftPressed()) {
                Core.getShared().getFocusManager().gotoPrevFocusableNode();
            } else {
                Core.getShared().getFocusManager().gotoNextFocusableNode();
            }
        }

}

    private void insertText(String generatedText) {
        if(selection.isActive()) {
            String[] parts = cursor.splitSelectedText();
            setText(parts[0]+generatedText+parts[2]);
            cursor.setIndex(parts[0].length() + generatedText.length());
            selection.clear();
        } else {
            String[] parts = cursor.splitText();
            setText(parts[0]+generatedText+parts[1]);
            cursor.moveRight(1);
        }
    }

    public TextControl setText(String text) {
        this.text = text;
        EventBus.getSystem().publish(new ChangedEvent(ChangedEvent.StringChanged,text,TextControl.this));
        setLayoutDirty();
        setDrawingDirty();
        return this;
    }

    protected void layoutText(double width, double height) {
        if(getFont() == null) return;
        int index = cursor.getIndex();
        _layout_model = new TextLayoutModel(getFont(),getText(),allowMultiLine);
        _layout_model.layout(width,height);
        Insets insets = styleInfo.calcContentInsets();
        setHeight(_layout_model.calculatedHeight()+insets.getTop()+insets.getBottom());
        cursor.setIndex(index);
    }

    public TextControl setFont(Font font) {
        this.realFont = font;
        setSkinDirty();
        return this;
    }

    public Font getFont() {
        if(realFont != null) {
            return realFont;
        }
        return styleInfo.font;
    }

    public boolean isFocused() {
        return focused;
    }

    @Override
    public void doSkins() {
        cssSkin = SkinManager.getShared().getCSSSkin();
        styleInfo = cssSkin.getStyleInfo(this,realFont);
        setLayoutDirty();
    }

    @Override
    public void doPrefLayout() {
        //noop
    }

    @Override
    public void doLayout() {
        //noop
    }


    public String getText() {
        return text;
    }

    public CursorPosition getCursor() {
        return cursor;
    }

    public  class TextSelection {
        private boolean active;
        public int startCol;
        public int endCol;

        private TextSelection() {
        }

        public static final int LEFT = 1;
        public static final int RIGHT = 2;
        int direction = LEFT;

        public void clear() {
            active = false;
            setDrawingDirty();
        }

        protected boolean isActive() {
            return active;
        }

        public void setStart(int index) {
            active = true;
            startCol = index;
            u.p(this);
        }
        public void setEnd(int index) {
            endCol = index;
            u.p(this);
        }

        public void selectAll() {
            active = true;
            startCol = 0;
            startCol = text.length();
        }

        public int getLeadingColumn() {
            return startCol;
        }

        public int getTrailingColumn() {
            return endCol;
        }

        public void addRight(int i) {
            if(endCol == startCol) {
                //flip direction
                direction = RIGHT;
            }
            if(direction == RIGHT) {
                endCol++;
                if(endCol > getText().length()) {
                    endCol = getText().length();
                }
            }
            if(direction == LEFT) {
                startCol = getCursor().moveLeft(startCol,-i);
            }
            u.p(this);
        }

        public void addLeft(int i) {
            if(endCol == startCol) {
                //flip direction
                direction = LEFT;
            }
            if(direction == LEFT) {
                startCol = getCursor().moveLeft(startCol,i);
            }
            if(direction == RIGHT) {
                endCol--;
                if(endCol <0) endCol=0;
            }
            u.p(this);
        }

        public String toString() {
            return "selection: " + startCol + " -> " + endCol;
        }

    }

    public void selectAll() {
        //To change body of created methods use File | Settings | File Templates.
        selection.selectAll();
        setDrawingDirty();
    }

    protected class CursorPosition {
        private int index = 0;
        private int col;
        private int row;

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }

        public int getIndex() {
            return index;
        }

        public int[] indexToRowCol(int i) {
            int[] rowCol = new int[2];
            int n = i;
            rowCol[0] = 0;
            rowCol[1] = 0;
            for(TextLayoutModel.LayoutLine line : _layout_model.lines()) {
                if(line.letterCount() >= n) {
                    break;
                }
                rowCol[0]++;
                n -= line.letterCount();
            }
            rowCol[1] = n;

            return rowCol;
        }

        public void setIndex(int i) {
            this.index = i;
            int[] rowCol = indexToRowCol(i);
            row = rowCol[0];
            col = rowCol[1];
            u.p(this);
        }

        public void moveLeft(int i) {
            index -= i;
            col -= i;
            TextLayoutModel.LayoutLine rowLine = _layout_model.line(row);
            //wrap back to prev row?
            if(col < 0 && row > 0) {
                row--;
                col = _layout_model.line(row).letterCount();
                index+=1;
            }
            if(index <0) {
                index = 0;
                col = 0;
                row = 0;
            }
            u.p(this);
        }

        public int moveLeft(int n, int i) {
            n -= i;
            if(n <0) {
                n = 0;
            }
            return n;
        }

        public void moveRight(int i) {
            index+=i;
            col+=i;

            TextLayoutModel.LayoutLine rowLine = _layout_model.line(row);
            //wrap to next row?
            if(col > rowLine.letterCount()) {
                if(row < _layout_model.lineCount()-1) {
                    row++;
                    col=0;
                    index-=1;
                }
            }
            
            String t = getText();
            if(index > t.length()) {
                index = t.length();
                col = _layout_model.line(row).letterCount();
            }
            u.p(this);
        }
        
        public boolean atEndOfLine() {
            TextLayoutModel.LayoutLine rowLine = _layout_model.line(row);
            if(col == rowLine.letterCount()) {
                return true;
            }
            return false;
        }

        public boolean atStartOfLine() {
            TextLayoutModel.LayoutLine rowLine = _layout_model.line(row);
            if(col == 0) {
                return true;
            }
            return false;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("cursor: " + col + "," + row + "  index = " + index);
            if(getText().length() > index) {
                sb.append(" char = " + getText().substring(index,index+1));
            }
            return sb.toString();
        }

        public String[] splitText() {
            String t = getText();
            String[] parts =  new String[2];
            parts[0] = t.substring(0,index);
            parts[1] = t.substring(index);
            return parts;
        }

        public String[] splitSelectedText() {
            String t = getText();
            String[] parts =  new String[3];
            parts[0] = t.substring(0,selection.getLeadingColumn());
            parts[1] = t.substring(selection.getLeadingColumn(),selection.getTrailingColumn());
            parts[2] = t.substring(selection.getTrailingColumn());
            return parts;
        }

        public void moveStart() {
            index = 0;
            col = 0;
            row = 0;
            u.p(this);
        }

        public void moveEnd() {
            String t = getText();
            index = t.length();
            col = index;
            u.p(this);
        }

        public double calculateX() {
            TextLayoutModel.LayoutLine line = _layout_model.line(row);
            String t = line.getString().substring(0,col);
            return getFont().calculateWidth(t);
        }

        public double calculateX(int row, int col) {
            TextLayoutModel.LayoutLine line = _layout_model.line(row);
            String t = line.getString().substring(0,col);
            return getFont().calculateWidth(t);
        }

        public double calculateY() {
            return (getFont().getAscender()+getFont().getDescender())*row;
        }


    }
}
