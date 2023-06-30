package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.emitter.Event;
import mx.kenzie.craftscript.variable.Wrapper;

public class EventKind extends Kind<Event> {

    public EventKind() {
        super(Event.class);
    }

    @Override
    public Object getProperty(Event thing, String property) {
        if (thing == null) return null;
        return Wrapper.of(thing.getProperty(property));
    }

    @Override
    public String toString(Event event) {
        return event.key().toString();
    }

}
