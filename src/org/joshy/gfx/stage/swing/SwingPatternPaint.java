package org.joshy.gfx.stage.swing;

import org.joshy.gfx.draw.Paint;
import org.joshy.gfx.draw.PatternPaint;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 27, 2010
 * Time: 11:05:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class SwingPatternPaint extends PatternPaint {
    BufferedImage image;

    public SwingPatternPaint(File file) throws IOException {
        image = ImageIO.read(file);
    }

    public SwingPatternPaint(URL resource) throws IOException {
        image = ImageIO.read(resource);
    }

    private SwingPatternPaint(BufferedImage img) {
        this.image = img;
    }

    @Override
    public Paint duplicate() {
        return new SwingPatternPaint(image);
    }
}
