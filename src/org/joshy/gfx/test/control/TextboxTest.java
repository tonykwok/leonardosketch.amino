package org.joshy.gfx.test.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.event.ActionEvent;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.KeyEvent;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.control.Textarea;
import org.joshy.gfx.node.control.Textbox;
import org.joshy.gfx.node.layout.VFlexBox;
import org.joshy.gfx.stage.Stage;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 26, 2010
 * Time: 8:46:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class TextboxTest implements Runnable {
    public static void main(String ... args) throws Exception, InterruptedException {
        Core.setUseJOGL(false);
        Core.init();
        Core.getShared().defer(new TextboxTest());
        EventBus.getSystem().addListener(KeyEvent.KeyPressed, new Callback<KeyEvent>() {
            public void call(KeyEvent event) {
//                u.p("key pressed " + event + " on node " + event.getSource());
            }
        });
    }

    public void run() {
        Stage stage = Stage.createStage();
        //stage.setContent(g);
        final Textarea ta = new Textarea("hello there mister man how are you doing todayz?");
        ta.setPrefWidth(100);
        ta.setPrefHeight(100);
        //Textbox ta = new Textbox("hello there");
        //ta.setFont(Font.name("Helvetica").size(50).resolve());
        stage.setContent(new VFlexBox()
                .add(ta)
                .add(new Button("select all").onClicked(new Callback<ActionEvent>(){
                        @Override
                        public void call(ActionEvent event) throws Exception {
                            ta.selectAll();
                        }
                    }))
                .add(new Button("clear text").onClicked(new Callback<ActionEvent>(){
                        @Override
                        public void call(ActionEvent event) throws Exception {
                            ta.setText("");
                        }
                    }))
                .add(new Textbox("foo").setPrefWidth(100))
                .add(new Textbox("bar").setPrefWidth(100))
                .add(new Textbox("large font")
                        .setFont(Font.name("Arial").size(50).resolve())
                        .setPrefWidth(300)
                )
                .add(new Textbox("")
                        .setHintText("hint text")
                        .setPrefWidth(100))
        );

    }
}
