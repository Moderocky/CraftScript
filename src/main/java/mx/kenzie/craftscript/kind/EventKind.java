package mx.kenzie.craftscript.kind;

import mx.kenzie.craftscript.emitter.Event;

public class EventKind extends Kind<Event> {

    public EventKind() {
        super(Event.class);
    }

    @Override
    public Object getProperty(Event thing, String property) {
        if (thing == null) return null;
        return thing.getProperty(property);
    }

    @Override
    public Event fromString(String string) {
        return null;
    }

    @Override
    public String toString(Event event) {
        return event.key().toString();
    }

}