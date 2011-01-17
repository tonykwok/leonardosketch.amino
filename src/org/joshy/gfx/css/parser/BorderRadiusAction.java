package org.joshy.gfx.css.parser;

import org.joshy.gfx.css.CSSParser;
import org.joshy.gfx.css.CSSProperty;
import org.joshy.gfx.css.CSSPropertySet;
import org.joshy.gfx.css.values.IntegerPixelValue;
import org.parboiled.Action;
import org.parboiled.Context;
import org.parboiled.support.Var;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: 1/16/11
* Time: 7:28 PM
* To change this template use File | Settings | File Templates.
*/
public class BorderRadiusAction implements Action {
    private Var propName;
    private Var propValue;
    private CSSParser cssParser;

    public BorderRadiusAction(CSSParser cssParser, Var propName, Var propValue) {
        this.cssParser = cssParser;
        this.propName = propName;
        this.propValue = propValue;
    }

    @Override
    public boolean run(Context context) {
        String pn = propName.get()+"";
        //u.p("propname = " + pn);
        if(!pn.endsWith("border-radius")) return false;

        //u.p("doing border radius expansion");
        String[] parts = (""+propValue.get()).split(" ");
        String prefix = pn.substring(0,pn.indexOf("border-radius"));
        //u.p("prefix = " + prefix);

        CSSProperty tl = new CSSProperty();
        CSSProperty tr = new CSSProperty();
        CSSProperty br = new CSSProperty();
        CSSProperty bl = new CSSProperty();
        tl.name = prefix+"border-top-left-radius";
        tr.name = prefix+"border-top-right-radius";
        br.name = prefix+"border-bottom-right-radius";
        bl.name = prefix+"border-bottom-left-radius";
        if(parts.length == 1) {
            tl.value = new IntegerPixelValue(parts[0]);
            tr.value = new IntegerPixelValue(parts[0]);
            br.value = new IntegerPixelValue(parts[0]);
            bl.value = new IntegerPixelValue(parts[0]);
        }
        if(parts.length == 2) {
            tl.value = new IntegerPixelValue(parts[0]);
            tr.value = new IntegerPixelValue(parts[1]);
            br.value = new IntegerPixelValue(parts[0]);
            bl.value = new IntegerPixelValue(parts[1]);
        }
        if(parts.length == 3) {
            tl.value = new IntegerPixelValue(parts[0]);
            tr.value = new IntegerPixelValue(parts[1]);
            br.value = new IntegerPixelValue(parts[2]);
            bl.value = new IntegerPixelValue(parts[1]);
        }
        if(parts.length == 4) {
            tl.value = new IntegerPixelValue(parts[0]);
            tr.value = new IntegerPixelValue(parts[1]);
            br.value = new IntegerPixelValue(parts[2]);
            bl.value = new IntegerPixelValue(parts[3]);
        }
        CSSPropertySet set = new CSSPropertySet();
        set.add(tl,tr,br,bl);
        context.setNodeValue(set);
        cssParser.set(set);
        return true;

    }
}
