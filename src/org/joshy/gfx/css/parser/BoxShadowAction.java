package org.joshy.gfx.css.parser;

import org.joshy.gfx.css.CSSProperty;
import org.joshy.gfx.css.values.ShadowValue;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.util.u;
import org.parboiled.Action;
import org.parboiled.Context;
import org.parboiled.support.Var;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: 1/16/11
* Time: 7:05 PM
* To change this template use File | Settings | File Templates.
*/
public class BoxShadowAction implements Action {
    private Var propName;
    private Var propValue;
    private Var<String> hexValue;
    private Var<String> inset;

    public BoxShadowAction(Var propName, Var propValue, Var<String> hexValue, Var<String> inset) {
        this.propName = propName;
        this.propValue = propValue;
        this.hexValue = hexValue;
        this.inset = inset;
    }
    @Override
    public boolean run(Context context) {
        if(!propName.get().equals("box-shadow")) return false;
        u.p("box shadow action called: " + propName.get() + " " + propValue.get());

        u.p("hex = " + hexValue.get());
        String[] vals = (""+propValue.get()).split(" ");
        u.p(vals);
        CSSProperty prop = new CSSProperty();
        int xoff = intFromPx(vals[0]);
        int yoff = intFromPx(vals[1]);
        int blur = 0;
        if(vals.length >= 3) {
            blur = intFromPx(vals[2]);
        }
        int spread = 0;
        if(vals.length >= 4) {
            spread = intFromPx(vals[3]);
        }

        FlatColor color = FlatColor.BLACK;
        if(hexValue.get() != null) {
            color = FlatColor.fromHexString(hexValue.get());
        }
        boolean insets = false;
        u.p("inset = " +inset.get());
        insets = "inset".equals(inset.get());
        prop.value = new ShadowValue(color,xoff,yoff,blur,spread,insets);
        prop.name = "box-shadow";
        context.setNodeValue(prop);
        return true;
    }

    private int intFromPx(String val) {
        val = val.trim();
        return Integer.parseInt(val.substring(0,val.length()-"px".length()));
    }
}
