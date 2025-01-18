package xmw.logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.PrintWriter;

class LogHandler extends DefaultHandler {
    private String service, user, type, desc;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if ("Event".equals(qName)) {
            service = attributes.getValue("service");
            user = attributes.getValue("user");
            type = attributes.getValue("type");
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        desc = new String(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if ("Event".equals(qName)) {
            EventRingBuffer.getInstance().push(new EventRingBuffer.Event(service, user, type, desc));
        }
    }
}

