package org.joshy.gfx.test.drawing;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.TransformNode;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.control.Textbox;
import org.joshy.gfx.node.layout.Panel;
import org.joshy.gfx.node.shape.Oval;
import org.joshy.gfx.stage.Stage;

public class OverlayTest implements Runnable {

    public static void main(String ... args) throws Exception {
        Core.init();
        Core.getShared().defer(new OverlayTest());
    }

    @Override
    public void run() {
        Node n1 = new TransformNode().setRotate(15).setContent(new Button("Button")).setTranslateX(50);
        Node n2 = new TransformNode().setRotate(30).setContent(new Oval().setWidth(100).setHeight(30).setFill(FlatColor.GREEN.deriveWithAlpha(0.5))).setTranslateX(80);
        Node n3 = new TransformNode().setRotate(45).setContent(new Textbox("text box")).setTranslateX(100);
        Stage stage = Stage.createStage();
        stage.setContent(new Panel().add(n1,n2,n3));

    }
}
