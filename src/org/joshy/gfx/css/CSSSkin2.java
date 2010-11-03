package org.joshy.gfx.css;

import org.joshy.gfx.css.values.BaseValue;
import org.joshy.gfx.css.values.LinearGradientValue;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.Image;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.control.Control;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Oct 25, 2010
 * Time: 7:20:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class CSSSkin2 extends CSSSkin {
    public StyleInfo getStyleInfo(Control control, Font realFont) {
        CSSMatcher matcher = createMatcher(control, CSSSkin.State.None);
        StyleInfo info = new StyleInfo();
        info.margin = getMargin(matcher);
        info.padding = getPadding(matcher);
        info.borderWidth = getBorderWidth(matcher,"");
        info.font = getFont(matcher);
        if(realFont != null) {
            info.font = realFont;
        }
        info.contentBaseline = info.font.getAscender();
        return info;
    }

    public SizeInfo getSizeInfo(Control control, StyleInfo style, String content) {
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
        }
        return size;
    }
    public BoxPainter createBoxPainter(Control control, StyleInfo style, SizeInfo size, String text, CSSSkin.State state) {
        CSSMatcher matcher = createMatcher(control, state);
        BoxPainter boxPainter = new BoxPainter();
        String prefix = "";
        boxPainter.borderRadius = set.findIntegerValue(matcher,prefix+"border-radius");
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
        boxPainter.margin = getMargin(matcher);
        boxPainter.borderWidth = getBorderWidth(matcher,"");
        if(!boxPainter.borderWidth.allEquals(0)) {
            boxPainter.border_color = (new FlatColor(set.findColorValue(matcher,prefix+"border-color")));
        }

        //content stuff
        boxPainter.icon = getIcon(matcher);
        boxPainter.font = style.font;
        boxPainter.textAlign = set.findStringValue(matcher.element,"text-align");
        boxPainter.color = new FlatColor(set.findColorValue(matcher,"color"));
        boxPainter.text_shadow = set.findValue(matcher, "text-shadow");

        return boxPainter;
    }
}
