package xmw;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Event {
    private final String service;
    private final String user;
    private final String type;
    private final String desc;
    private final String timestamp;

    public Event(String service, String user, String type, String desc) {
        this.service = service;
        this.user = user;
        this.type = type;
        this.desc = desc;
        this.timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
    }

    public String toXML() {
        return String.format("<Event service=\"%s\" user=\"%s\" type=\"%s\" timestamp=\"%s\">%s</Event>",
                service, user, type, timestamp, desc);
    }

    @Override
    public String toString() {
        return String.format("{service: %s, user: %s, type: %s, desc: %s, timestamp: %s}",
                service, user, type, desc, timestamp);
    }
}