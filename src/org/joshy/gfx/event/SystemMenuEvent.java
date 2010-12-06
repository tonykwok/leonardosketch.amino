package org.joshy.gfx.event;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Dec 6, 2010
 * Time: 9:54:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class SystemMenuEvent extends Event {
    public static final EventType About = new EventType("SystemMenuEvent.About");
    public static final EventType Quit = new EventType("SystemMenuEvent.Quit");
    public static final EventType Preferences = new EventType("SystemMenuEvent.Preferences");
    public SystemMenuEvent(EventType type) {
        super(type);
    }
}
