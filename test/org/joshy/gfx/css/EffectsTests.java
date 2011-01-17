package org.joshy.gfx.css;

import org.joshy.gfx.Core;
import org.joshy.gfx.css.values.ShadowValue;
import org.joshy.gfx.util.u;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.parboiled.Node;
import org.parboiled.Parboiled;
import org.parboiled.ReportingParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: 1/16/11
 * Time: 5:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class EffectsTests {
    private CSSRuleSet set;

    @Before
    public void setUp() throws Exception {
        Core.setTesting(true);
        Core.init();
        URI uri = MainCSSTest.class.getResource("effects.css").toURI();
        InputStream css = MainCSSTest.class.getResourceAsStream("effects.css");
        ParsingResult<?> result = parseCSS(css);
        set = new CSSRuleSet();
        condense(result.parseTreeRoot,set,uri);
    }


    @Test
    public void boxShadow() throws IOException, URISyntaxException {
        ShadowValue shad;
        CSSMatcher m = new CSSMatcher();
        m.id = "boxshadow1";
        u.p("value = " + set.findValue(m, "box-shadow"));
        assertTrue(set.findValue(m, "box-shadow") instanceof ShadowValue);
        //List shads = (List) set.findValue(m,"box-shadow");
        //assertTrue(shads.size() == 1);
        //assertTrue(shads.get(0) instanceof ShadowValue);
        shad = (ShadowValue) set.findValue(m,"box-shadow");
        assertTrue(shad.getXOffset() == 10);
        assertTrue(shad.getYOffset() == 10);


        m.id = "boxshadow2";
        //assertTrue(set.findValue(m,"box-shadow") instanceof List);
        //shads = (List) set.findValue(m,"box-shadow");
        //assertTrue(shads.size() == 1);
        //assertTrue(shads.get(0) instanceof BoxShadow);
        shad = (ShadowValue) set.findValue(m,"box-shadow");
        assertTrue(shad.getXOffset() == 12);
        assertTrue(shad.getYOffset() == 13);
        assertTrue(shad.getRadius() == 5);
        assertTrue(shad.isInset() == false);
        assertTrue(shad.getColor() == 0xff00ff00);

        m.id = "boxshadow3";
        //assertTrue(set.findValue(m,"box-shadow") instanceof List);
        //shads = (List) set.findValue(m,"box-shadow");
        //assertTrue(shads.size() == 1);
        //assertTrue(shads.get(0) instanceof BoxShadow);
        shad = (ShadowValue) set.findValue(m,"box-shadow");
        assertTrue(shad.isInset() == true);
        assertTrue(shad.getRadius() == 2);
        assertTrue(shad.getSpread() == 2);
        assertTrue(shad.getColor()== 0xff000000);

        /*
        m.id = "boxshadow4";
        assertTrue(set.findValue(m,"box-shadow") instanceof List);
        shads = (List) set.findValue(m,"box-shadow");
        assertTrue(shads.size() == 3);
        assertTrue(shads.get(0) instanceof BoxShadow);
        assertTrue(((BoxShadow)shads.get(0)).getXOffset() == 10);
        assertTrue(((BoxShadow)shads.get(0)).getYOffset() == 20);
        assertTrue(((BoxShadow)shads.get(0)).getColor().getRGBA() == 0xffff0000);

        assertTrue(shads.get(1) instanceof BoxShadow);
        assertTrue(((BoxShadow)shads.get(1)).getXOffset() == -10);
        assertTrue(((BoxShadow)shads.get(1)).getYOffset() == -30);
        assertTrue(((BoxShadow)shads.get(1)).getColor().getRGBA() == 0xfff4f4f4);

        assertTrue(shads.get(2) instanceof BoxShadow);
        assertTrue(((BoxShadow)shads.get(2)).getXOffset() == 0);
        assertTrue(((BoxShadow)shads.get(2)).getYOffset() == 0);
        assertTrue(((BoxShadow)shads.get(2)).getBlurRadius() == 5);
        assertTrue(((BoxShadow)shads.get(2)).getSpreadRadius() == 5);
        assertTrue(((BoxShadow)shads.get(2)).getColor().getRGBA() == 0xffcc6600);
*/
    }

    /*
    @Test
    public void textShadow() {
        CSSMatcher m = new CSSMatcher();
        m.id = "textshadow1";
        assertTrue(set.findValue(m,"text-shadow") instanceof List);
        List shads = (List) set.findValue(m,"text-shadow");
        assertTrue(shads.size() == 1);
        assertTrue(shads.get(0) instanceof TextShadow);
        assertTrue(((TextShadow)shads.get(0)).getXOffset() == 10);
        assertTrue(((TextShadow)shads.get(0)).getYOffset() == 10);

        m.id = "textshadow2";
        assertTrue(set.findValue(m,"text-shadow") instanceof List);
        shads = (List) set.findValue(m,"text-shadow");
        assertTrue(shads.size() == 1);
        assertTrue(shads.get(0) instanceof TextShadow);
        assertTrue(((TextShadow)shads.get(0)).getXOffset() == 12);
        assertTrue(((TextShadow)shads.get(0)).getYOffset() == 13);
        assertTrue(((TextShadow)shads.get(0)).getRadius() == 5);
        assertTrue(((TextShadow)shads.get(0)).getColor().getRGBA() == 0xff00ff00);

        m.id = "textshadow3";
        assertTrue(set.findValue(m,"text-shadow") instanceof List);
        shads = (List) set.findValue(m,"text-shadow");
        assertTrue(shads.size() == 1);
        assertTrue(shads.get(0) instanceof TextShadow);
        assertTrue(((TextShadow)shads.get(0)).getRadius() == 2);
        assertTrue(((TextShadow)shads.get(0)).getSpreadRadius() == 2);
        assertTrue(((TextShadow)shads.get(0)).getColor().getRGBA() == 0xff000000);

        m.id = "textshadow4";
        assertTrue(set.findValue(m,"text-shadow") instanceof List);
        shads = (List) set.findValue(m,"text-shadow");
        assertTrue(shads.size() == 3);
        assertTrue(shads.get(0) instanceof TextShadow);
        assertTrue(((TextShadow)shads.get(0)).getXOffset() == 10);
        assertTrue(((TextShadow)shads.get(0)).getYOffset() == 20);
        assertTrue(((TextShadow)shads.get(0)).getColor().getRGBA() == 0xffff0000);

        assertTrue(shads.get(1) instanceof TextShadow);
        assertTrue(((TextShadow)shads.get(1)).getXOffset() == -10);
        assertTrue(((TextShadow)shads.get(1)).getYOffset() == -30);
        assertTrue(((TextShadow)shads.get(1)).getColor().getRGBA() == 0xfff4f4f4);

        assertTrue(shads.get(2) instanceof TextShadow);
        assertTrue(((TextShadow)shads.get(2)).getXOffset() == 0);
        assertTrue(((TextShadow)shads.get(2)).getYOffset() == 0);
        assertTrue(((TextShadow)shads.get(2)).getRadius() == 5);
        assertTrue(((TextShadow)shads.get(2)).getSpreadRadius() == 5);
        assertTrue(((TextShadow)shads.get(2)).getColor().getRGBA() == 0xffcc6600);

    }

    @Test
    public void gradients() {
        CSSMatcher m = new CSSMatcher();
        m.id = "lineargrad1";
        assertTrue(set.findValue(m,"background") instanceof List);
        List grads = (List) set.findValue(m,"background");
        assertTrue(grads.size() == 1);
        assertTrue(grads.get(0) instanceof LinearGradientValue);
        LinearGradientValue grad = (LinearGradientValue) grads.get(0);
        assertTrue(grad.getPosition1().equals("left"));
        assertTrue(grad.getPosition2().equals("center"));
        assertTrue(grad.getStops().size() == 2);
        assertTrue(grad.getStop(0).getColor() == 0xFFffffff);
        assertTrue(grad.getStop(0).getPosition() == 0);
        assertTrue(grad.getStop(1).getColor() == 0xFF808080);
        assertTrue(grad.getStop(1).getPosition() == 1);

        m.id = "lineargrad2";
        grads = (List) set.findValue(m,"background");
        assertTrue(grads.size() == 1);
        grad = (LinearGradientValue) grads.get(0);
        assertTrue(grad.getPosition1().equals("top"));
        assertTrue(grad.getStops().size() == 3);
        assertTrue(grad.getStop(0).getColor() == 0xFFffffff);
        assertTrue(grad.getStop(1).getColor() == 0xffff0000);
        assertTrue(grad.getStop(1).getPosition() == 0.4);
        assertTrue(grad.getStop(2).getPosition() == 1.0);


        m.id = "radialgrad2";
        grads = (List) set.findValue(m,"background");
        assertTrue(grads.size() == 1);
        RadialGradientValue rad = (RadialGradientValue) grads.get(0);
        assertTrue(rad.getStops().size() == 3);
        assertTrue(rad.getStop(0).getColor() == 0xFF000000);
        assertTrue(rad.getStop(1).getColor() == 0xFFff0000);
        assertTrue(rad.getStop(2).getColor() == 0xFFffffff);
        assertTrue(rad.getStop(0).getPosition() == 0);
        assertTrue(rad.getStop(1).getPosition() == 0.5);
        assertTrue(rad.getStop(2).getPosition() == 1.0);



    }

    @Test
    public void borderImage() {
        CSSMatcher m = new CSSMatcher();
        m.id = "borderimage1";
        assertTrue(set.findURIValue(m,"border-image-source") instanceof URLValue);
        assertTrue(set.findURIValue(m,"border-image-source").toString().equals("foo.png"));
        assertTrue(set.findIntegerValue(m,"border-width") == 10);
        assertTrue(set.findValue(m,"border-image-slice") instanceof InsetsValue);
        InsetsValue iv = set.findValue(m,"border-image-slice");
        assertTrue(iv.getLeft() == 10);
        assertTrue(iv.getBottom() == 20);

    }

    @Test
    public void backgroundImage() {
        CSSMatcher matcher = new CSSMatcher();
        matcher.id = "imagebackground1";
        assertTrue(set.findValue(matcher,"background-image") instanceof List);
        List list = (List) set.findValue(matcher, "background-image");
        assertTrue(list.size() == 1);
        assertTrue(list.get(0) instanceof BackgroundImageValue);
        assertTrue(((BackgroundImageValue)list.get(0)).getURL().equals("foo.png"));

        matcher.id = "imagebackground2";
        assertTrue(set.findValue(matcher,"background-image") instanceof List);
        list = (List) set.findValue(matcher, "background-image");
        assertTrue(list.size() == 2);
        assertTrue(list.get(1) instanceof BackgroundImageValue);
        assertTrue(((BackgroundImageValue)list.get(1)).getURL().equals("bar.png"));


    }
      */

    private static void condense(Node<?> node, CSSRuleSet set, URI uri) {
        if(node == null) return;
        if("CSSRule".equals(node.getLabel())) {
            CSSRule rule = (CSSRule) node.getValue();
            rule.setBaseURI(uri);
            set.append(rule);
        }
        for(Node<?> n : node.getChildren()) {
            condense(n,set,uri);
        }
    }

    private static ParsingResult<?> parseCSS(InputStream css) throws IOException {
        String cssString = toString(css);
        CSSParser parser = Parboiled.createParser(CSSParser.class);
        //System.out.println("string = " + cssString);
        ParsingResult<?> result = ReportingParseRunner.run(parser.RuleSet(), cssString);
        String parseTreePrintOut = ParseTreeUtils.printNodeTree(result);
        //System.out.println(parseTreePrintOut);
        //u.p("other value = " + result.parseTreeRoot.getLabel());
        return result;
    }

    private static String toString(InputStream css) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buff = new byte[256];
        while(true) {
            int n = css.read(buff);
            if(n < 0) break;
            out.write(buff,0,n);
        }
        css.close();
        out.close();
        return new String(out.toByteArray());
    }

    @After
    public void tearDown() throws Exception {
    }


}
