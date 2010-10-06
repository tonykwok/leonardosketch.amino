package org.joshy.gfx.test.partyboard;

import org.joshy.gfx.Core;
import org.joshy.gfx.animation.AnimationDriver;
import org.joshy.gfx.animation.KeyFrameAnimator;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.ActionEvent;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.control.Label;
import org.joshy.gfx.node.control.Textbox;
import org.joshy.gfx.node.layout.HFlexBox;
import org.joshy.gfx.node.layout.VFlexBox;
import org.joshy.gfx.node.shape.Oval;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.u;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Oct 5, 2010
 * Time: 7:34:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class PartyBoard implements Runnable {
    private Callback<ActionEvent> quitHandler;
    private Callback<ActionEvent> startHandler;
    private ParticleSimulator sim;

    public static void main(String ... args) throws Exception {
        Core.init();
        Core.setDebugCSS(new File("/Users/joshmarinacci/projects/personal/amino/test.css"));
        Core.getShared().defer(new PartyBoard());
    }

    @Override
    public void run() {

        quitHandler = new Callback<ActionEvent>() {
            @Override
            public void call(ActionEvent event) {
                System.exit(0);
            }
        };

        startHandler = new Callback<ActionEvent>() {
            @Override
            public void call(ActionEvent event) {
                startSim();
            }
        };


        Stage setup = Stage.createStage();
        setup.setContent(new VFlexBox()
                .setBoxAlign(VFlexBox.Align.Stretch)
                .add(new HFlexBox()
                    .setBoxAlign(HFlexBox.Align.Stretch)
                    .add(new Label("Twitter Hashcode"))
                    .add(new Textbox("#webos"),1)
                )
                .add(new HFlexBox()
                    .setBoxAlign(HFlexBox.Align.Stretch)
                    .add(new Label("message"))
                    .add(new Textbox("Welcome to the Pleasure Dome"),1))
                .add(new HFlexBox()
                    .add(new Button("quit").onClicked(quitHandler))
                    .add(new Button("start").onClicked(startHandler)))

        );
    }

    private void startSim() {
        Stage fullscreen = Stage.createStage();
        fullscreen.setUndecorated(true);
        fullscreen.setWidth(1024);
        fullscreen.setHeight(768);
        sim = new ParticleSimulator();
        fullscreen.setContent(sim);
        AnimationDriver.start(
            KeyFrameAnimator.create(sim,"update")
                .repeat(KeyFrameAnimator.INFINITE)
                .keyFrame(0,0)
                .keyFrame(10,30000)
        );
    }

    public static class ParticleSimulator extends Node {
        @Override
        public void draw(GFX g) {
            for(Particle p : parts) {
                g.translate(p.getTranslateX(),p.getTranslateY());
                p.draw(g);
                g.translate(-p.getTranslateX(),-p.getTranslateY());
            }
        }

        @Override
        public Bounds getVisualBounds() {
            return new Bounds(0,0,100,100);
        }

        @Override
        public Bounds getInputBounds() {
            return getVisualBounds();
        }

        class Particle extends Oval {
            public double vx;
            public double vy;
        }
        private List<Particle> parts = new ArrayList<Particle>();
        public void setUpdate(double value) {
            if(parts.size() < 20) {
                Particle particle = new Particle();
                particle.setFill(FlatColor.GREEN)
                        .setTranslateX(100)
                        .setTranslateY(100);
                particle.vx = (Math.random()*2.0-1.0)*4;
                particle.vy = (Math.random()*2.0-1.0)*4;
                parts.add(particle);
            }
            u.p("size = " + parts.size());

            for(Particle part : parts) {
                double x = part.getTranslateX()+part.vx;
                //flip direction
                if(x < 0 || x > 1024) {
                    x -= part.vx;
                    part.vx *= -1;
                }
                part.setTranslateX(x);
                part.setTranslateY(part.getTranslateY()+part.vy);
            }

            setDrawingDirty();
            
        }
    }
}
