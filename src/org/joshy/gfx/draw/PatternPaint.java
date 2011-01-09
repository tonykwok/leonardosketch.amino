package org.joshy.gfx.draw;

import org.joshy.gfx.Core;
import org.joshy.gfx.stage.jogl.JOGLPatternPaint;
import org.joshy.gfx.stage.swing.SwingPatternPaint;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 26, 2010
 * Time: 9:16:24 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class PatternPaint implements Paint {

    protected PatternPaint() {
    }

    public static PatternPaint create(File file) throws IOException {
        if(Core.getShared().isUseJOGL()) {
            return new JOGLPatternPaint(file);
        } else {
            return new SwingPatternPaint(file);
        }
    }

    public static PatternPaint create(URL resource) throws IOException {
        if(Core.getShared().isUseJOGL()) {
            return new JOGLPatternPaint(resource);
        } else {
            return new SwingPatternPaint(resource);
        }
    }

}
