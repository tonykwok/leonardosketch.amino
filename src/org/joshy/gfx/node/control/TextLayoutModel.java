package org.joshy.gfx.node.control;

import org.joshy.gfx.draw.Font;
import org.joshy.gfx.util.u;

import java.util.ArrayList;
import java.util.List;


public class TextLayoutModel {
    private String originalText;
    private Font font;
    private List<LayoutLine> lines;
    private boolean allowMultiline = true;

    public TextLayoutModel(Font font, String text, boolean allowMultiLine) {
        lines = new ArrayList<LayoutLine>();
        this.font = font;
        this.originalText = text;
        this.allowMultiline = allowMultiLine;
    }

    public int lineCount() {
        return lines.size();
    }

    public LayoutLine line(int i) {
        return lines.get(i);
    }

    public void layout(double width, double height) {
//        u.p("doing layout ( "+width+" x " + height + " ) on text: " + originalText);
        lines.clear();

        StringBuffer buf = new StringBuffer();
        int wordStart = 0;
        for(int i=0; i<originalText.length(); i++) {
            double w = font.calculateWidth(buf.toString());
//            u.p("width = " + w + "   " + wordStart + " of " + buf.toString());
            //break if line is too long, unless still just one word
            if(w > width && wordStart > 0 && allowMultiline) {
                //back up to previous word boundary
                LayoutLine line = new LayoutLine();
                String s = buf.toString();
                line.string = s.substring(0,wordStart);
                lines.add(line);
                buf = new StringBuffer(s.substring(wordStart));
                wordStart = 0;
            }

            char ch = originalText.charAt(i);
            if(isWordChar(ch)) {
                buf.append(ch);
            }
            if(isWhitespaceChar(ch)) {
                wordStart = buf.length();
            }
            //if hard coded newline, save and start the next line
            if(isNewLine(ch) && allowMultiline) {
                LayoutLine line = new LayoutLine();
                line.string = buf.toString();
                lines.add(line);
                buf = new StringBuffer();
                wordStart = 0;
            }
        }
        //put anything left over into it's own line
        LayoutLine line = new LayoutLine();
        line.string = buf.toString();
        lines.add(line);

        for(LayoutLine l : lines) {
            u.p("line = " + l.getString());
        }
    }

    private boolean isWhitespaceChar(char ch) {
        if(ch == ' ') return true;
        return false;
    }

    private boolean isNewLine(char ch) {
        if(ch == '\n') return true;
        return false;
    }

    private boolean isWordChar(char ch) {
        if(ch == '\n') return false;
        return true;
    }

    public double calculatedHeight() {
        return lineCount()*(font.getAscender()+font.getDescender());
    }

    public Iterable<? extends LayoutLine> lines() {
        return lines;
    }

    public class LayoutLine {
        public String string;

        public int letterCount() {
            return string.length();
        }

        public String getString() {
            return string;
        }

        public double getHeight() {
            return font.getAscender()+font.getDescender();
        }
    }
}