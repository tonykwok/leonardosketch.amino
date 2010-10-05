package org.joshy.gfx.node.control;

import org.joshy.gfx.SkinManager;
import org.joshy.gfx.css.CSSMatcher;
import org.joshy.gfx.css.CSSSkin;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.BackgroundTask;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.ProgressUpdate;
import org.joshy.gfx.node.Bounds;

/**  The ProgressBar is a control which shows the progress of some process, usually a background
 * task. It shows the progress as a bar of increasing width.
 * It was designed with background tasks in mind and can be easily attached to a background
 * task. For example, the following code will attach a progress bar to a background task.
 *
 * <pre><code>
 BackgroundTask task = new BackgroundTask<String, String>() {
     protected String onWork(String data) {
        ... do some work in the background
        ... call update gui to show progress from 0.0 to 1.0
         updateGUI(data,result,i/100.0);
        ... return result when done
         return result;
     }
 };

 //create progress bar and attach to the task
 ProgressBar pb = new ProgressBar();
 pb.setTask(task);

 //start the task
 task.start(); 
 *
 * </code></pre>
 */
public class ProgressBar extends Control {
    double percentage = 0.0;
    private CSSSkin.BoxState size;

    public ProgressBar() {
    }

    @Override
    public void doSkins() {
        cssSkin = SkinManager.getShared().getCSSSkin();
        setLayoutDirty();
    }

    @Override
    public void doLayout() {
    }
    
    @Override
    public void doPrefLayout() {
        if(cssSkin != null) {
            size = cssSkin.getSize(this,"ASDFASDFASDF");
            if(prefWidth != CALCULATED) {
                setWidth(prefWidth);
                size.width = prefWidth;
            } else {
                setWidth(size.width);
            }
            setHeight(size.height);
        }
    }

    @Override
    public void draw(GFX g) {
        if(!isVisible()) return;
        if(cssSkin != null) {
            if(size == null) {
                doPrefLayout();
            }
            CSSMatcher matcher = new CSSMatcher("ProgressBar");
            cssSkin.drawBackground(g, matcher, "", new Bounds(0,0,getWidth(), getHeight()));
            cssSkin.drawBorder(g, matcher, "", new Bounds(0,0,getWidth(), getHeight()));
            cssSkin.drawBackground(g, matcher, "bar-", new Bounds(0,0,getWidth()*percentage, getHeight()));
            cssSkin.drawBorder(g, matcher, "bar-", new Bounds(0,0,getWidth()*percentage, getHeight()));
        }
    }



    public void setTask(BackgroundTask task) {
        EventBus.getSystem().addListener(task, ProgressUpdate.TYPE, new Callback<ProgressUpdate>() {
            public void call(ProgressUpdate event) {
                percentage = event.getPercentage();
                setDrawingDirty();
            }
        });
        setDrawingDirty();
    }

}
