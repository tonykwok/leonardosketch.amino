package org.joshy.gfx.css;

import org.joshy.gfx.css.values.BaseValue;
import org.joshy.gfx.css.values.LinearGradientValue;
import org.joshy.gfx.css.values.URLValue;
import org.joshy.gfx.draw.*;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Insets;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.control.Scrollbar;

import java.net.URI;

/**
 * Implements drawing Controls using the CSS box model.
 * This class is usually a singleton and should hold no state. All state
 * comes from the BoxStage instance passed in along with the control.
 */
public class CSSSkin {
    protected CSSRuleSet set;
    private Font defaultFont = Font.name("Arial").size(13).resolve();

    /* good functions */
    public CSSRuleSet getCSSSet() {
        return this.set;
    }

    public enum State {
        Pressed, Hover, Selected, Disabled, Focused, None
    }

    public StyleInfo getStyleInfo(Control control, Font realFont) {
        return getStyleInfo(control,realFont,"");
    }
    public StyleInfo getStyleInfo(Control control, Font realFont, String prefix) {
        CSSMatcher matcher = createMatcher(control, CSSSkin.State.None);
        StyleInfo info = new StyleInfo();
        info.margin = getMargin(matcher,prefix);
        info.padding = getPadding(matcher,prefix);
        info.borderWidth = getBorderWidth(matcher,prefix);
        info.font = getFont(matcher);
        if(realFont != null) {
            info.font = realFont;
        }
        info.contentBaseline = info.font.getAscender();
        return info;
    }

    public SizeInfo getSizeInfo(Control control, StyleInfo style, String content) {
        return getSizeInfo(control,style,content,"");
    }

    public SizeInfo getSizeInfo(Control control, StyleInfo style, String content, String prefix) {
        CSSMatcher matcher = createMatcher(control, State.None);
        SizeInfo size = new SizeInfo();
        size.contentWidth = control.getWidth()-style.margin.getLeft()-style.margin.getRight()-style.padding.getLeft()-style.padding.getRight();
        size.contentHeight = control.getHeight()-style.margin.getTop()-style.margin.getBottom()-style.padding.getTop()-style.padding.getBottom();

        Image icon = getIcon(matcher);
        //calc the sizes
        if("true".equals(set.findStringValue(matcher,"shrink-to-fit"))) {
            size.contentWidth = style.font.calculateWidth(content);
            size.contentHeight = style.font.calculateHeight(content);
            if(icon != null) {
                size.contentWidth += icon.getWidth();
                size.contentHeight = Math.max(size.contentHeight,icon.getHeight());
            }
            size.width = style.margin.getLeft()+style.margin.getRight()+style.borderWidth.getLeft()+style.borderWidth.getRight()+style.padding.getLeft()+style.padding.getRight()+size.contentWidth;
            size.height = style.margin.getTop()+style.margin.getBottom()+style.borderWidth.getTop()+style.borderWidth.getBottom()+style.padding.getTop()+style.padding.getBottom()+size.contentHeight;
            double fh = style.font.calculateHeight(content);
            size.contentBaseline = (size.contentHeight-fh)/2 + fh;
        } else {
            size.contentBaseline = size.contentHeight;
            size.width = set.findIntegerValue(matcher,"width");
            size.height = set.findIntegerValue(matcher,"height");
        }
        return size;
    }

    public BoxPainter createBoxPainter(Control control, StyleInfo style, SizeInfo size, String text, CSSSkin.State state) {
        return createBoxPainter(control,style,size,text,state,"");
    }

    public BoxPainter createBoxPainter(Control control, StyleInfo style, SizeInfo size, String text, State state, String prefix) {
        CSSMatcher matcher = createMatcher(control, state);
        BoxPainter boxPainter = new BoxPainter();
        boxPainter.borderRadius = getBorderRadius(matcher,prefix);
        boxPainter.transparent = "transparent".equals(set.findStringValue(matcher,prefix+"background-color"));
        if(!boxPainter.transparent) {
            boxPainter.background_color = new FlatColor(set.findColorValue(matcher,prefix+"background-color"));
        } else {
            boxPainter.background_color = FlatColor.BLACK;
        }
        BaseValue background = set.findValue(matcher,prefix+"background");


        double backWidth = size.width-style.margin.getLeft()-style.margin.getRight();
        double backHeight = size.height-style.margin.getTop()-style.margin.getBottom();
        Bounds bounds = new Bounds(style.margin.getLeft(),style.margin.getTop(),backWidth,backHeight);
        if(background instanceof LinearGradientValue) {
            boxPainter.gradient = true;
            boxPainter.gradientFill = toGradientFill((LinearGradientValue)background,bounds.getWidth(),bounds.getHeight());
        }

        //border stuff
        boxPainter.margin = getMargin(matcher,prefix);
        boxPainter.borderWidth = getBorderWidth(matcher,prefix);
        if(!boxPainter.borderWidth.allEquals(0)) {
            boxPainter.border_color = (new FlatColor(set.findColorValue(matcher,prefix+"border-color")));
        }

        //content stuff
        boxPainter.icon = getIcon(matcher);
        boxPainter.font = style.font;
        boxPainter.textAlign = set.findStringValue(matcher.element,"text-align");
        boxPainter.color = new FlatColor(set.findColorValue(matcher,"color"));
        boxPainter.text_shadow = set.findValue(matcher, "text-shadow");
        boxPainter.box_shadow = set.findValue(matcher, "box-shadow");

        return boxPainter;
    }

    protected Font getFont(CSSMatcher matcher) {
        int fontSize = set.findIntegerValue(matcher, "font-size");
        Font font = Font.name("Arial").size(fontSize).resolve();
        return font;
    }

    /*
    private void drawDebugOverlay(GFX gfx, CSSMatcher matcher, String prefix, OldStyleInfo box) {
        Insets borderWidth = getBorderWidth(matcher,"");

        //debugging
        if("true".equals(set.findStringValue(matcher,"debug-margin"))) {
            gfx.setPaint(FlatColor.RED);
            gfx.drawRect(0,0,box.width,box.height);
        }
        if("true".equals(set.findStringValue(matcher,"debug-border"))) {
            gfx.setPaint(FlatColor.GREEN);
            gfx.drawRect(box.margin.getLeft(),
                    box.margin.getTop(),
                    box.width-box.margin.getLeft()-box.margin.getRight(),
                    box.height-box.margin.getTop()-box.margin.getBottom());
        }
        if("true".equals(set.findStringValue(matcher,"debug-padding"))) {
            gfx.setPaint(FlatColor.BLUE);
            gfx.drawRect(
                    box.margin.getLeft()+borderWidth.getLeft(),
                    box.margin.getTop()+borderWidth.getTop(),
                    box.width-box.margin.getLeft()-box.margin.getRight()-borderWidth.getLeft()-borderWidth.getRight(),
                    box.height-box.margin.getTop()-box.margin.getBottom()-borderWidth.getTop()-borderWidth.getBottom());
        }
    }
    */


    public void drawBackground(GFX g, CSSMatcher matcher, String prefix, Bounds bounds) {
        g.translate(bounds.getX(),bounds.getY());
        Insets margin = getMargin(matcher,prefix);
        BaseValue background = set.findValue(matcher,prefix+"background");
        Insets radius = getBorderRadius(matcher,prefix);

        if(!"transparent".equals(set.findStringValue(matcher,prefix+"background-color"))) {
            g.setPaint(new FlatColor(set.findColorValue(matcher,prefix+"background-color")));
            if(background instanceof LinearGradientValue) {
                g.setPaint(toGradientFill((LinearGradientValue)background,bounds.getWidth(),bounds.getHeight()));
            }
            if(radius.allEquals(0)) {
                g.fillRect(
                        0+margin.getLeft(),
                        0+margin.getTop(),
                        bounds.getWidth()-margin.getLeft()-margin.getRight(),
                        bounds.getHeight()-margin.getTop()-margin.getBottom()
                        );
            } else if(radius.allEqual()) {
                g.fillRoundRect(
                        0+margin.getLeft(),
                        0+margin.getTop(),
                        bounds.getWidth()-margin.getLeft()-margin.getRight(),
                        bounds.getHeight()-margin.getTop()-margin.getBottom(),
                        radius.getLeft(),
                        radius.getRight());
            } else {
                g.fillCustomRoundRect(
                        0+margin.getLeft(),
                        0+margin.getTop(),
                        bounds.getWidth()-margin.getLeft()-margin.getRight(),
                        bounds.getHeight()-margin.getTop()-margin.getBottom(),
                        radius.getTop(),
                        radius.getTop(),
                        radius.getRight(),
                        radius.getRight(),
                        radius.getBottom(),
                        radius.getBottom(),
                        radius.getLeft(),
                        radius.getLeft()
                        );
            }
        }
        g.translate(-bounds.getX(),-bounds.getY());
    }

    public void drawBorder(GFX gfx, CSSMatcher matcher, String prefix, Bounds bounds) {
        Insets margin = getMargin(matcher,prefix);
        Insets borderWidth = getBorderWidth(matcher,prefix);
        Insets radius = getBorderRadius(matcher,prefix);
        if(!borderWidth.allEquals(0)) {
            gfx.setPaint(new FlatColor(set.findColorValue(matcher,prefix+"border-color")));
            if(radius.allEquals(0)) {
                if(borderWidth.allEqual()) {
                    if(borderWidth.getLeft() >0) {
                        gfx.setStrokeWidth(borderWidth.getLeft());
                        gfx.drawRect(
                                bounds.getX()+margin.getLeft(),
                                bounds.getY()+margin.getTop(),
                                bounds.getWidth()-margin.getLeft()-margin.getRight(),
                                bounds.getHeight()-margin.getTop()-margin.getBottom()
                        );
                    }
                    gfx.setStrokeWidth(1);
                } else {
                    double x = bounds.getX()+margin.getLeft();
                    double y = bounds.getY()+margin.getTop();
                    double w = bounds.getWidth()-margin.getLeft()-margin.getRight()-1;
                    double h = bounds.getHeight()-margin.getTop()-margin.getBottom()-1;
                    if(borderWidth.getLeft()>0) {
                        gfx.setStrokeWidth(borderWidth.getLeft());
                        gfx.drawLine(x,y,x,y+h);
                    }
                    if(borderWidth.getTop()>0) {
                        gfx.setStrokeWidth(borderWidth.getTop());
                        gfx.drawLine(x,y,x+w,y);
                    }
                    if(borderWidth.getRight()>0) {
                        gfx.setStrokeWidth(borderWidth.getRight());
                        gfx.drawLine(x+w,y,x+w,y+h);
                    }
                    if(borderWidth.getBottom()>0) {
                        gfx.setStrokeWidth(borderWidth.getBottom());
                        gfx.drawLine(x,y+h,  x+w,y+h);
                    }
                }
            } else {
                if(radius.allEqual()) {
                    gfx.drawRoundRect(
                            bounds.getX()+margin.getLeft(),
                            bounds.getY()+margin.getTop(),
                            bounds.getWidth()-margin.getLeft()-margin.getRight(),
                            bounds.getHeight()-margin.getTop()-margin.getBottom(),
                        radius.getLeft(),
                        radius.getRight());
                } else {
                    gfx.drawCustomRoundRect(
                            bounds.getX()+margin.getLeft(),
                            bounds.getY()+margin.getTop(),
                            bounds.getWidth()-margin.getLeft()-margin.getRight(),
                            bounds.getHeight()-margin.getTop()-margin.getBottom(),
                        radius.getTop(),
                        radius.getTop(),
                        radius.getRight(),
                        radius.getRight(),
                        radius.getBottom(),
                        radius.getBottom(),
                        radius.getLeft(),
                        radius.getLeft()
                        );
                }
            }
            gfx.setStrokeWidth(1);
        }
    }




    protected Insets getPadding(CSSMatcher matcher, String prefix) {
        int padding_left = set.findIntegerValue(matcher,prefix+"padding-left");
        int padding_right = set.findIntegerValue(matcher,prefix+"padding-right");
        int padding_top = set.findIntegerValue(matcher,prefix+"padding-top");
        int padding_bottom = set.findIntegerValue(matcher,prefix+"padding-bottom");
        return new Insets(padding_top,padding_right,padding_bottom,padding_left);
    }

    protected Insets getMargin(CSSMatcher matcher) {
        return getMargin(matcher, "");
    }

    protected Insets getMargin(CSSMatcher matcher, String prefix) {
        int margin_left = set.findIntegerValue(matcher,prefix+"margin-left");
        int margin_right = set.findIntegerValue(matcher,prefix+"margin-right");
        int margin_top = set.findIntegerValue(matcher,prefix+"margin-top");
        int margin_bottom = set.findIntegerValue(matcher,prefix+"margin-bottom");
        return new Insets(margin_top,margin_right,margin_bottom,margin_left);
    }

    protected Insets getBorderWidth(CSSMatcher matcher, String prefix) {
        int border_left = set.findIntegerValue(matcher,prefix+"border-left-width");
        int border_right = set.findIntegerValue(matcher,prefix+"border-right-width");
        int border_top = set.findIntegerValue(matcher,prefix+"border-top-width");
        int border_bottom = set.findIntegerValue(matcher,prefix+"border-bottom-width");
        return new Insets(border_top,border_right,border_bottom,border_left);
    }

    protected Insets getBorderRadius(CSSMatcher matcher, String prefix) {
        int border_top_left = set.findIntegerValue(matcher,prefix+"border-top-left-radius");
        int border_top_right = set.findIntegerValue(matcher,prefix+"border-top-right-radius");
        int border_bottom_right = set.findIntegerValue(matcher,prefix+"border-bottom-right-radius");
        int border_bottom_left = set.findIntegerValue(matcher,prefix+"border-bottom-left-radius");
        //TODO: we really should use a class other than Insets for this
        return new Insets(border_top_left,border_top_right,border_bottom_right,border_bottom_left);
    }

    public void drawText(GFX g, CSSMatcher matcher, String prefix, Bounds b, String text) {
        g.translate(b.getX(),b.getY());
        Insets margin = getMargin(matcher,prefix);
        Insets borderWidth = getBorderWidth(matcher,prefix);
        Insets padding = getPadding(matcher,prefix);
        g.setPaint(new FlatColor(set.findColorValue(matcher,prefix+"color")));
        double x = margin.getLeft() + borderWidth.getLeft() + padding.getLeft();
        String textAlign = set.findStringValue(matcher,"text-align");
        double contentX = margin.getLeft()+borderWidth.getLeft()+padding.getLeft();
        double contentY = margin.getTop()+borderWidth.getTop()+padding.getTop();
        double textX = contentX;
        Font font = getFont(matcher);
        if("center".equals(textAlign)) {
            Font.drawCentered(g,text,font,textX,contentY,b.getWidth(),b.getHeight(),true);
        } else {
            Font.drawCenteredVertically(g,text,font,textX,contentY,b.getWidth(),b.getHeight(),true);
        }
        g.translate(-b.getX(),-b.getY());
    }

    public Insets getInsets(Control control) {
        CSSMatcher matcher = createMatcher(control,null);
        Insets margin = getMargin(matcher, "");
        Insets border = getBorderWidth(matcher, "");
        Insets padding = getPadding(matcher,"");
        return new Insets(margin,border,padding);
    }

    public void setRuleSet(CSSRuleSet set) {
        this.set = set;
    }

    public CSSRuleSet getRuleSet() {
        return this.set;
    }

    protected Image getIcon(CSSMatcher matcher) {
        Image icon = null;
        URLValue uv = set.findURIValue(matcher, "icon");
        try {
            if(uv != null) {
                URI uri = uv.getFullURI();
                icon = Image.getImageFromCache(uri.toURL());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return icon;
    }

    public static CSSMatcher createMatcher(Control control, State state) {
        CSSMatcher matcher = new CSSMatcher(control);
        if(state == State.Disabled) {
            matcher.pseudo = "disabled";
        }
        if(state == State.Hover) {
            matcher.pseudo = "hover";
        }
        if(state == State.Pressed) {
            matcher.pseudo = "pressed";
        }
        if(state == State.Selected) {
            matcher.pseudo = "selected";
        }
        if(state == State.Focused) {
            matcher.pseudo = "focused";
        }
        if(control instanceof Scrollbar) {
            if(((Scrollbar)control).isVertical()) {
                matcher.pseudo = "vertical";
            }
        }
        return matcher;
    }

    protected GradientFill toGradientFill(LinearGradientValue grad, double backWidth, double backHeight) {
        GradientFill gf = new GradientFill(
                new FlatColor(grad.getStop(0).getColor()),
                new FlatColor(grad.getStop(1).getColor()),
                90, false
        );
        gf.startX = 0;
        gf.endX = 0;
        gf.startY = 0;
        gf.endY = backHeight;
        if("left".equals(grad.getPosition1())) {
            gf.endX = backWidth;
            gf.endY = 0;
        }
        return gf;
    }

    public Font getDefaultFont() {
        return defaultFont;
    }
}
