package org.joshy.gfx.node.control.complex;

import org.joshy.gfx.event.Event;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: 1/16/11
* Time: 8:30 PM
* To change this template use File | Settings | File Templates.
*/
public class ListEvent extends Event {
    public static final EventType Updated = new EventType("ListEventUpdated");
    public ListEvent(EventType type, ListModel model) {
        super(type);
        this.source = model;
    }
}
