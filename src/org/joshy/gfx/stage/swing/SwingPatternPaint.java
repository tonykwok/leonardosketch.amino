package org.joshy.gfx.stage.swing;

import org.joshy.gfx.draw.Paint;
import org.joshy.gfx.draw.PatternPaint;

import javax.imageio.ImageIO;
import java.awt.geom.Point2D;
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
    private Point2D start = new Point2D.Double(0,0);
    private Point2D end;

    public SwingPatternPaint(File file) throws IOException {
        this(ImageIO.read(file));
    }

    public SwingPatternPaint(URL resource) throws IOException {
        this(ImageIO.read(resource));
    }

    private SwingPatternPaint(BufferedImage img) {
        this.image = img;
        this.start = new Point2D.Double(0,0);
        this.end = new Point2D.Double(image.getWidth(),image.getHeight());
    }

    public SwingPatternPaint(BufferedImage image, Point2D start, Point2D end) {
        this.image = image;
        this.start = start;
        this.end = end;
    }

    @Override
    public Paint duplicate() {
        return new SwingPatternPaint(image,getStart(),getEnd());
    }

    @Override
    public Point2D getStart() {
        return start;
    }

    @Override
    public Point2D getEnd() {
        return end;
    }

    @Override
    public PatternPaint deriveNewStart(Point2D newPoint) {
        SwingPatternPaint p = new SwingPatternPaint(this.image);
        p.start = newPoint;
        p.end = this.end;
        return p;
    }

    @Override
    public PatternPaint deriveNewEnd(Point2D newPoint) {
        SwingPatternPaint p = new SwingPatternPaint(this.image);
        p.start = this.start;
        p.end = newPoint;
        return p;
    }
}
