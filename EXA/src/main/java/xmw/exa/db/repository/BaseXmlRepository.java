package xmw.exa.db.repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.basex.core.Context;

public abstract class BaseXmlRepository<T> implements Repository<T> {
    protected final Context context;
    protected static final String DB_NAME = "exa";

    protected BaseXmlRepository(Context context) {
        this.context = context;
    }

    protected String extractValue(String xml, String tag) {
        String pattern = String.format("<%s>([^<]*)</%s>", tag, tag);
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(xml);
        return m.find() ? m.group(1).trim() : "";
    }

    protected void updateXMLFile(String fileName, String parentTag, String childXml) throws IOException {
        String resourcePath = "/mockData/" + fileName;
        InputStream is = getClass().getResourceAsStream(resourcePath);
        if (is == null) {
            // Try without leading slash if first attempt fails
            resourcePath = "mockData/" + fileName;
            is = getClass().getResourceAsStream(resourcePath);
        }

        if (is == null) {
            throw new IOException("Could not find resource: " + fileName);
        }

        try (InputStream input = is) {
            // Read the existing XML content
            String xmlContent = new String(input.readAllBytes(), StandardCharsets.UTF_8);

            // Create output factory
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLEventFactory eventFactory = XMLEventFactory.newInstance();
            StringWriter updatedXml = new StringWriter();
            XMLEventWriter writer = outputFactory.createXMLEventWriter(updatedXml);

            // Create input factory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLEventReader reader = inputFactory.createXMLEventReader(new StringReader(xmlContent));

            // Track if we've found the parent tag
            boolean inParentTag = false;

            // Process events
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    String elementName = startElement.getName().getLocalPart();

                    if (elementName.equals(parentTag)) {
                        inParentTag = true;
                    }

                    writer.add(event);
                } else if (event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    String elementName = endElement.getName().getLocalPart();

                    if (inParentTag && elementName.equals(parentTag)) {
                        // Add the new child element before closing the parent
                        reader.close();
                        writer.add(eventFactory.createCharacters("\n    ")); // Indentation
                        writer.add(eventFactory.createCharacters(childXml));
                        writer.add(eventFactory.createCharacters("\n  ")); // Indentation
                        inParentTag = false;
                    }

                    writer.add(event);
                } else {
                    writer.add(event);
                }
            }

            writer.close();
            reader.close();

            // Write the updated XML back to the file
            String absolutePath = getClass().getResource(resourcePath).getPath();
            java.nio.file.Path filePath = java.nio.file.Paths.get(absolutePath);
            java.nio.file.Files.writeString(filePath, updatedXml.toString(), StandardCharsets.UTF_8);

        } catch (XMLStreamException e) {
            throw new IOException("Failed to update XML file: " + e.getMessage(), e);
        }
    }

}