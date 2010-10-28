package org.joshy.gfx.test.drawing;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.TransformNode;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.ChangedEvent;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.control.Slider;
import org.joshy.gfx.node.layout.Panel;
import org.joshy.gfx.node.layout.VFlexBox;
import org.joshy.gfx.stage.Stage;

public class TransformTest implements Runnable {
    public static void main(String ... args) throws Exception {
        Core.init();
        Core.getShared().defer(new TransformTest());
    }

    @Override
    public void run() {

        final TransformNode trans = new TransformNode();
//        trans.setScaleX(1.5).setScaleY(1.5);
        trans.setTranslateX(0).setTranslateY(10);
//        trans.setRotate(15.0);
        trans.setContent(new Button("Do it!"));
        //trans.setContent(new Rectangle().setWidth(100).setHeight(100).setFill(FlatColor.RED));

        final Slider scaleSlider = new Slider(false);
        scaleSlider.setMin(1.0).setMax(4.0).setValue(1.0);
        scaleSlider.setPrefWidth(150);

        final Slider rotateSlider = new Slider(false).setMin(0).setMax(360.0).setValue(0.0);
        rotateSlider.setPrefWidth(150);

        final Slider translateSlider = new Slider(false).setMin(0).setMax(200).setValue(0.0);
        translateSlider.setPrefWidth(150);

        EventBus.getSystem().addListener(ChangedEvent.DoubleChanged, new Callback<ChangedEvent>(){
            @Override
            public void call(ChangedEvent event) throws Exception {
                if(event.getSource() == scaleSlider) {
                    trans.setScaleX(scaleSlider.getValue());
                    trans.setScaleY(scaleSlider.getValue());
                }
                if(event.getSource() == rotateSlider) {
                    trans.setRotate(rotateSlider.getValue());
                }
                if(event.getSource() == translateSlider) {
                    trans.setTranslateX(translateSlider.getValue());
                }
            }
        });

        Stage stage = Stage.createStage();
        stage.setContent(new Panel()
            .add(trans)
            .add(new VFlexBox()
                .add(scaleSlider)
                .add(rotateSlider)
                .add(translateSlider)
            )
        );
    }
}
