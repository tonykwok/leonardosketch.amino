package org.joshy.gfx.test.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.Event;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.SystemMenuEvent;
import org.joshy.gfx.node.control.Textarea;
import org.joshy.gfx.node.layout.Panel;
import org.joshy.gfx.stage.Stage;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Nov 5, 2010
 * Time: 1:28:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class TextareaTest implements Runnable {
    public static void main(String ... args) throws Exception, InterruptedException {
        Core.setUseJOGL(false);
        Core.init();
        Core.getShared().defer(new TextareaTest());
    }

    @Override
    public void run() {
        EventBus.getSystem().addListener(SystemMenuEvent.Quit, new Callback<Event>() {
            @Override
            public void call(Event event) throws Exception {
                System.exit(0);
            }
        });
        Stage stage = Stage.createStage();
        //stage.setContent(g);
        final Textarea ta = new Textarea("foo");
        ta.setSizeToText(true);
        ta.setFont(Font.name("Arial").size(50).resolve());
        //stage.setContent(new ScrollPane(ta));
        Panel pa = new Panel();
        pa.setFill(FlatColor.RED);
        pa.add(ta);
        stage.setContent(pa);
    }
}
