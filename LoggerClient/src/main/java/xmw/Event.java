package xmw;

public class Event {
    private String service, user, type, desc;

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

