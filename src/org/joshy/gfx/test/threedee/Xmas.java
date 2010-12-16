package org.joshy.gfx.test.threedee;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.draw.Transform;
import org.joshy.gfx.draw.TransformNode;
import org.joshy.gfx.node.Group;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.shape.Rectangle;
import org.joshy.gfx.stage.PerspectiveCamera;
import org.joshy.gfx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: 12/15/10
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class Xmas implements Runnable {
    private Group parts;

    public static void main(String ... args) throws Exception, InterruptedException {
        Core.setUseJOGL(true);
        Core.init();
        Core.getShared().defer(new Xmas());
    }

    @Override
    public void run() {
        parts = new Group() {
            @Override
            public void draw(GFX g) {
                updateParticles();
                super.draw(g);
            }
        };
        Stage s = Stage.create3DStage();
        s.setCamera(new PerspectiveCamera());
        s.setContent(parts);
        s.setWidth(1024);
        s.setHeight(768);
    }

    int maxFlakes = 200;

    double minX = -200;
    double maxX = 200;

    double startY = 250;
    double endY = -200;

    double minZ = -50;
    double maxZ = 50;

    double minYv = -0.5;
    double maxYv = -1.5;
    double minRotV = -3;
    double maxRotV = 3;
    private void updateParticles() {
        if(parts.getChildCount() < maxFlakes) {
            Flake p = new Flake();
            p.setTranslateX(rand(minX,maxX));
            p.setTranslateY(startY);
            p.setTranslateZ(rand(minZ, maxZ));
            p.setAxis(Transform.Y_AXIS);
            p.yV = rand(minYv,maxYv);
            p.rotV = rand(minRotV,maxRotV);
            parts.add(p);
        }

        List<Flake> dead = new ArrayList<Flake>();
        for(Node node : parts.children()) {
            Flake f = (Flake) node;
            //update values
            f.setTranslateY(f.getTranslateY()+f.yV);
            f.setRotate(f.getRotate()+f.rotV);

            //clear the dead ones
            if(f.getTranslateY() < endY) {
                dead.add(f);
            }
        }
        for(Flake f : dead) {
            parts.remove(f);
        }
    }

    private double rand(double minX, double maxX) {
        return minX + Math.random()*(maxX-minX);
    }

    private static class Flake extends TransformNode {
        double yV = 1.0;
        public double rotV;

        private Flake() {
            super();
            setContent(new Rectangle()
                    .setWidth(10)
                    .setHeight(10)
                    .setFill(FlatColor.RED)
                    .setTranslateX(-5)
                    .setTranslateY(-5));
        }
    }
}
