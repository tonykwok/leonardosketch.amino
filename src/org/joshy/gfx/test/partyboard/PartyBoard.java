package org.joshy.gfx.test.partyboard;

import com.joshondesign.xml.Doc;
import com.joshondesign.xml.Elem;
import org.joshy.gfx.Core;
import org.joshy.gfx.animation.Animateable;
import org.joshy.gfx.animation.AnimationDriver;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.ActionEvent;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.PeriodicTask;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.control.Label;
import org.joshy.gfx.node.control.Textbox;
import org.joshy.gfx.node.layout.HFlexBox;
import org.joshy.gfx.node.layout.VFlexBox;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.u;
import org.joshy.gfx.util.xml.XMLRequest;

import java.net.MalformedURLException;
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
    private AnimationDriver anim;
    private double width;
    private double height;
    private XMLRequest twitterFeed;

    public static void main(String ... args) throws Exception {
        Core.init();
        Core.getShared().defer(new PartyBoard());
    }

    @Override
    public void run() {
        width = 1100;
        height = 800;

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
        fullscreen.setWidth(width);
        fullscreen.setHeight(height);
        fullscreen.setFullScreen(true);
        width = fullscreen.getWidth();
        height = fullscreen.getHeight();
        sim = new ParticleSimulator(this);
        fullscreen.setContent(sim);
        anim = new AnimationDriver(new Animateable() {

            @Override
            public void onStart(long current) {

            }

            @Override
            public void update(long currentTime) {
                sim.update();
            }

            @Override
            public void onStop(long currentTime) {

            }

            @Override
            public void loop() {

            }

            @Override
            public boolean isDone() {
                return false;
            }
        });
        anim.setFPS(40);
        anim.start();
        final String hashTag = "#webos";

        new PeriodicTask(10*1000)
                .call(new Callback(){
                    @Override
                    public void call(Object event) throws Exception {
                        searchTweet(hashTag);                        
                    }
                }).start();

        searchTweet(hashTag);
    }

    private void searchTweet(String hashTag) {
        if(twitterFeed == null || twitterFeed.isDone()) {
            try {

                twitterFeed = new XMLRequest()
                        .setURL("http://search.twitter.com/search.atom")
                        .setParameter("q",hashTag)
                        .setMethod(XMLRequest.METHOD.GET)
                        .onComplete(new Callback<Doc>(){
                            @Override
                            public void call(Doc doc) throws Exception {
                                String firstTweet = "";
                                for(Elem e : doc.xpath("/feed/entry")) {
                                    //u.p("entry = " + e.xpathString("title/text()"));
                                    firstTweet = e.xpathString("title/text()");
                                    break;
                                }
                                u.p("first tweet = " + firstTweet);
                            }
                        })
                        .onError(new Callback<Throwable>(){
                            @Override
                            public void call(Throwable event) throws Exception {
                                u.p("error!: " + event);
                                event.printStackTrace();
                            }
                        })
                        ;
                twitterFeed.start();
            } catch (MalformedURLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public static class ParticleSimulator extends Node {
        private int tick;
        private double grav = 0;
        private PartyBoard main;

        public ParticleSimulator(PartyBoard main) {
            this.main = main;
        }

        @Override
        public void draw(GFX g) {
            g.setPaint(FlatColor.BLACK);
            g.fillRect(0,0,main.width,main.height);
            for(Particle p : parts) {
                p.draw(g);
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

        private List<Particle> parts = new ArrayList<Particle>();
        public void update() {
            this.tick++;
            grav = Math.sin(Math.toRadians(tick)/2)*0.8;

            if(parts.size() < 20) {
                Particle particle = new Particle();
                particle.setFill(FlatColor.GREEN);
                particle.x = main.width/2;
                particle.y = main.height/2;
                particle.vx = (Math.random()*2.0-1.0)*1;
                particle.vy = (Math.random()*2.0-1.0)*1;
                particle.radius = 30+Math.random()*10;
                particle.setIndex(parts.size());
                parts.add(particle);
            }
            for(Particle part : parts) {
                part.collide(parts,0.05,main.width,main.height);
                part.move(grav,main.width,main.height);
                part.updateColor();
            }
            setDrawingDirty();
            
        }
    }

}
