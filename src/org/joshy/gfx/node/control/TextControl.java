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
    protected TextSelection selection = new TextSelection(this);
    TextLayoutModel _layout_model;
    private CursorPosition cursor;
    protected StyleInfo styleInfo;


    protected TextControl() {
        cursor = new CursorPosition();

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
                    setDrawingDirty();
                }
                if(event.getType() == FocusEvent.Gained && event.getSource() == TextControl.this) {
                    focused = true;
                    setDrawingDirty();
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
            
            //split, then remove text in the middle
            String[] parts = cursor.splitText();
            parts[0] = parts[0].substring(0,cursor.getIndex()-1);
            setText(parts[0]+parts[1]);
            cursor.moveLeft(1);
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
//            if(selection.isActive() && !event.isShiftPressed()) {
//                selection.clear();
//                return;
//            }
//            if(event.isShiftPressed() && !selection.isActive()) {
//                selection.setStart(currentCursorPoint);
//            }
            cursor.moveLeft(1);
            setDrawingDirty();
//            if(cursorCharX > 0) {
//                cursorCharX--;
//                currentCursorPoint = cursorCharToCursorPoint(cursorCharX,text);
//                setDrawingDirty();
//            }
//            if(event.isShiftPressed() && selection.isActive()) {
//                selection.setEnd(currentCursorPoint);
//            }
        }
        if(event.getKeyCode() == KeyEvent.KeyCode.KEY_RIGHT_ARROW) {
//            if(selection.isActive() && !event.isShiftPressed()) {
//                selection.clear();
//                return;
//            }
//            if(event.isShiftPressed() && !selection.isActive()) {
//                selection.setStart(currentCursorPoint);
//            }
            cursor.moveRight(1);
            setDrawingDirty();
//            if(event.isShiftPressed() && selection.isActive()) {
//                selection.setEnd(currentCursorPoint);
//            }
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
        /*

        if(event.getKeyCode() == KeyEvent.KeyCode.KEY_UP_ARROW && allowMultiLine) {
            if(selection.isActive() && !event.isShiftPressed()) {
                selection.clear();
                return;
            }
            if(event.isShiftPressed() && !selection.isActive()) {
                selection.setStart(currentCursorPoint);
            }
            CursorPoint cp = cursorCharToCursorPoint(cursorCharX, getText());
            if(cp.row > 0) {
                cp.row--;
            }
            cursorCharX = cursorPointToCursorChar(cp);
            currentCursorPoint = cursorCharToCursorPoint(cursorCharX,text);
            setDrawingDirty();
            if(event.isShiftPressed() && selection.isActive()) {
                selection.setEnd(currentCursorPoint);
            }
        }

        if(event.getKeyCode() == KeyEvent.KeyCode.KEY_DOWN_ARROW && allowMultiLine) {
            if(selection.isActive() && !event.isShiftPressed()) {
                selection.clear();
                return;
            }
            if(event.isShiftPressed() && !selection.isActive()) {
                selection.setStart(currentCursorPoint);
            }
            CursorPoint cp = cursorCharToCursorPoint(cursorCharX, getText());
            if(cp.row < cp.rowCount-1) {
                cp.row++;
            }
            cursorCharX = cursorPointToCursorChar(cp);
            currentCursorPoint = cursorCharToCursorPoint(cursorCharX,text);
            setDrawingDirty();
            if(event.isShiftPressed() && selection.isActive()) {
                selection.setEnd(currentCursorPoint);
            }
        }
       */
        
        if(event.getKeyCode() == KeyEvent.KeyCode.KEY_TAB) {
            if(event.isShiftPressed()) {
                Core.getShared().getFocusManager().gotoPrevFocusableNode();
            } else {
                Core.getShared().getFocusManager().gotoNextFocusableNode();
            }
        }

}

    private void insertText(String generatedText) {
//        if(selection.isActive() && text.length() >= 1) {
//            replaceAndClearSelectionWith(generatedText);
//            return;
//        }
        u.p("inserting text: " + generatedText);
        String[] parts = cursor.splitText();
        setText(parts[0]+generatedText+parts[1]);
        cursor.moveRight(1);
        EventBus.getSystem().publish(new ChangedEvent(ChangedEvent.StringChanged,text,TextControl.this));
        setDrawingDirty();
    }
/*
    private void insertAtCursor(String string) {
        int cursorCharX = cursorPointToCursorChar(currentCursorPoint);
        if(text.length() >= 1) {
            text = text.substring(0, cursorCharX) +
                "\n"+
                text.substring(cursorCharX,text.length());
        } else {
            text = "\n";
        }
        if(selection.isActive()) {
            selection.clear();
        }
        cursorCharX++;
        currentCursorPoint = cursorCharToCursorPoint(cursorCharX,text);
        EventBus.getSystem().publish(new ChangedEvent(ChangedEvent.StringChanged,text,TextControl.this));
        setDrawingDirty();
    }
  */
    /*
    private void replaceAndClearSelectionWith(String replacementText) {
        int cursorCharX = cursorPointToCursorChar(currentCursorPoint);
        if(text.length() < 1) {
            text = replacementText;
        } else {
            String beforeString = text.substring(0, rowColumnToCursorChar(selection.getLeadingRow(),selection.getLeadingColumn()));
            String afterString = text.substring(rowColumnToCursorChar(selection.getTrailingRow(),selection.getTrailingColumn()),text.length());
            cursorCharX = beforeString.length();
            cursorCharX++;
            text = beforeString + replacementText + afterString;
        }
        currentCursorPoint = cursorCharToCursorPoint(cursorCharX,text);

        selection.clear();

        EventBus.getSystem().publish(new ChangedEvent(ChangedEvent.StringChanged,text,TextControl.this));
        setDrawingDirty();
    }
    */
    public TextControl setText(String text) {
        this.text = text;
        EventBus.getSystem().publish(new ChangedEvent(ChangedEvent.StringChanged,text,TextControl.this));
        setLayoutDirty();
        setDrawingDirty();
        return this;
    }

    protected void layoutText() {
        if(getFont() == null) return;
        _layout_model = new TextLayoutModel(getFont(),getText(),allowMultiLine);
        _layout_model.layout(getWidth(),getHeight());
        Insets insets = styleInfo.calcContentInsets();
        setHeight(_layout_model.calculatedHeight()+insets.getTop()+insets.getBottom());
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

    public static class TextSelection {
        private TextControl textControl;
        private boolean active;
        public int startRow;
        public int startCol;
        private int endRow;
        public int endCol;

        private TextSelection(TextControl textControl) {
            this.textControl = textControl;
        }

        public void clear() {
            active = false;
            textControl.setDrawingDirty();
        }

        protected boolean isActive() {
            return active;
        }
        /*
        public void setStart(CursorPoint cp) {
            active = true;
            startRow = cp.row;
            endRow = cp.row;
            startCol = cp.col;
            endCol = cp.col;
        }

        public void setEnd(CursorPoint cp) {
            endRow = cp.row;
            endCol = cp.col;
        }
        */
        public void selectAll() {
            active = true;
            startRow = 0;
            startCol = 0;
            String[] lines = textControl.text.split("\n");
            endRow = lines.length-1;
            endCol = lines[lines.length-1].length()-1;
        }

        public int getLeadingRow() {
            if(endRow < startRow) {
                return endRow;
            } else {
                return startRow;
            }
        }

        public int getTrailingRow() {
            if(endRow < startRow) {
                return startRow;
            } else {
                return endRow;
            }
        }
        
        public int getLeadingColumn() {
            if(endRow < startRow) return endCol;
            if(endCol < startCol && startRow == endRow) return endCol;
            return startCol;
        }

        public int getTrailingColumn() {
            if(endRow < startRow) return startCol;
            if(endCol < startCol && startRow == endRow) return startCol;
            return endCol;
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

        public void moveLeft(int i) {
            index--;
            col--;
            if(index <0) {
                index = 0;
                col = 0;
                row = 0;
            }
            u.p(this);
        }

        public void moveRight(int i) {
            index++;
            col++;
            String t = getText();
            if(index > t.length()) {
                index = t.length();
                col = index;
            }
            u.p(this);
        }
        public String toString() {
            return "cursor: " + col + "," + row + "  index = " + index;
        }

        public String[] splitText() {
            String t = getText();
            String[] parts =  new String[2];
            parts[0] = t.substring(0,index);
            parts[1] = t.substring(index);
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
            String t = getText();
            t = t.substring(0,index);
            return getFont().calculateWidth(t);
        }
    }
}
