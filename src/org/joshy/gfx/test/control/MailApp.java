package org.joshy.gfx.test.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.node.control.*;
import org.joshy.gfx.node.layout.*;
import org.joshy.gfx.stage.Stage;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Oct 4, 2010
 * Time: 9:10:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class MailApp implements Runnable {
    public static void main(String ... args) throws Exception {
        Core.init();
        Core.getShared().defer(new MailApp());
    }

    @Override
    public void run() {
        Control content = null;
        content = test1();
        //content = test2();
        Stage stage = Stage.createStage();
        stage.setContent(content);
        stage.setWidth(850);
        stage.setHeight(600);

    }

    private FlexBox test1() {
        FlexBox content = new VFlexBox()
            .setBoxAlign(VFlexBox.Align.Stretch)
            .add(
                    new HFlexBox()
                        .setBoxAlign(VFlexBox.Align.Top)
                        .add(new Button("Mail"))
                        .add(new Spacer(), 1)
                        .add(new Button("Delete"))
                        .add(new Button("Junk"))
                        .add(new Spacer(), 1)
                        .add(new Button("Reply"))
                        .add(new Button("Reply All"))
                        .add(new Button("Forward"))
                        .add(new Spacer(), 1)
                        .add(new Button("New Message"))
                        .add(new Spacer(), 1)
                        .add(new Button("Note"))
                        .add(new Button("Todo"))
                        .add(new Spacer(),1)
                        .add(new Textbox("Search").setPrefWidth(150))
                 , 0)
            .add(new HFlexBox()
                .setBoxAlign(HFlexBox.Align.Stretch)
                .add(new VFlexBox()
                        .setBoxAlign(FlexBox.Align.Stretch)
                        .add(new ScrollPane(new ListView())
                                .setHorizontalVisiblePolicy(ScrollPane.VisiblePolicy.Never)
                                ,1)
                        .add(new HFlexBox()
                            .setBoxAlign(HFlexBox.Align.Stretch)
                            .add(new Button("+"))
                            .add(new Button("^"))
                            .add(new Button("*")),0)
                        .setPrefWidth(200)
                    ,0)

                .add(new SplitPane(true)
                    .setFirst(new ScrollPane(new TableView())
                            .setHorizontalVisiblePolicy(ScrollPane.VisiblePolicy.Never))
                    .setSecond(new Panel()
                        .setFill(FlatColor.BLUE)
                        .setWidth(100)
                        .setHeight(100))
                    .setPosition(200)
                ,1)
            ,1)
                ;
        return content;
    }

    private FlexBox test2() {
    FlexBox content = new VFlexBox()
        .setBoxAlign(VFlexBox.Align.Stretch)
        .add(new Button("asdf"))
        .add(new VFlexBox()
            //.add(new Button("asdf"))
            .setBoxAlign(VFlexBox.Align.Stretch)

                .add(new ListView().setWidth(200),1)
                //.add(new Button().setWidth(200),1)
            /*.add(new HFlexBox()
                .setBoxAlign(HFlexBox.Align.Stretch)
                .add(new Button("+"))
                .add(new Button("^"))
                .add(new Button("*")),0)*/
        ,1);
        return content;
    }

}
