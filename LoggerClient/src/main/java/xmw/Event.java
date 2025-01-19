package xmw;

public class Event {
    private final String service;
    private final String user;
    private final String type;
    private final String desc;

    public Event(String service, String user, String type, String desc) {
        this.service = service;
        this.user = user;
        this.type = type;
        this.desc = desc;
    }

    public String toXML() {
        return String.format("<Event service=\"%s\" user=\"%s\" type=\"%s\">%s</Event>", service, user, type, desc);
    }

    @Override
    public String toString() {
        return String.format("{service: %s, user: %s, type: %s, desc: %s}", service, user, type, desc);
    }
}

