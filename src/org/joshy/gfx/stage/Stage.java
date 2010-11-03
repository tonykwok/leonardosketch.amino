package org.joshy.gfx.stage;

import org.joshy.gfx.Core;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.layout.Container;
import org.joshy.gfx.stage.jogl.JOGLStage;
import org.joshy.gfx.stage.swing.SwingStage;

public abstract class Stage {

    protected Stage() {
    }


    public static Stage createStage() {
        if(!Core.isUseJOGL()) {
            return new SwingStage();
        } else {
            return new JOGLStage();
        }
    }
    public static Stage create3DStage() {
        if(!Core.isUseJOGL()) {
            throw new UnsupportedOperationException("JOGL must be active to create a 3D stage");
        } else {
            return new JOGLStage(true);
        }
    }

    public abstract void setContent(Node node);
    public abstract Node getContent();
    public abstract void setCamera(Camera camera);

    public abstract void setWidth(double width);
    public abstract double getWidth();


    public abstract void setHeight(double height);

    public abstract double getHeight();

    public abstract double getX();
    public abstract double getY();


    public abstract void setMinimumWidth(double width);
    public abstract void setMinimumHeight(double height);

    public abstract Container getPopupLayer();

    public abstract Object getNativeWindow();

    public abstract void setTitle(String title);

    public abstract void setUndecorated(boolean undecorated);

    public abstract void hide();

    public abstract void raiseToTop();

    public abstract Stage setId(String id);
}

