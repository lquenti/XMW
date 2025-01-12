package xmw.user.utils;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class BulkUsernameExtractor {

    // Assumes prior DTD validation
    public static List<String> extractUsernames(String xmlInput) throws XMLStreamException {
        List<String> usernames = new ArrayList<>();
        StringReader stringReader = new StringReader(xmlInput);

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = factory.createXMLEventReader(stringReader);

        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();

            // We only care about start events
            if (!event.isStartElement()) {
                continue;
            }

            StartElement startElement = event.asStartElement();
            if (startElement.getName().getLocalPart().equals("User")) {
                Attribute attr = startElement.getAttributeByName(javax.xml.namespace.QName.valueOf("username"));
                if (attr != null) {
                    usernames.add(attr.getValue());
                }
            }
        }
        eventReader.close();
        return usernames;
    }
}
