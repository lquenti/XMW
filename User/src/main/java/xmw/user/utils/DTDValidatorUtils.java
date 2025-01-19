package xmw.user.utils;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import xmw.ClientLogger;
import xmw.Event;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

public class DTDValidatorUtils {
    public static final String DTD_WITH_PASSWORD = """
            <!DOCTYPE User [
                  <!ELEMENT User (name, firstname, password, faculty, group*)>
            
                  <!ATTLIST User
                    username CDATA #REQUIRED
                  >
            
                  <!ELEMENT name (#PCDATA)>
                  <!ELEMENT firstname (#PCDATA)>
                  <!ELEMENT password (#PCDATA)>
                  <!ELEMENT faculty (#PCDATA)>
            
                  <!ELEMENT group (#PCDATA)>
                  <!ATTLIST group
                    id CDATA #REQUIRED
                  >
            ]>
            """;
    public static final String DTD_WITHOUT_PASSWORD = """
            <!DOCTYPE User [
                  <!ELEMENT User (name, firstname, password, faculty, group*)>
            
                  <!ATTLIST User
                    username CDATA #REQUIRED
                  >
            
                  <!ELEMENT name (#PCDATA)>
                  <!ELEMENT firstname (#PCDATA)>
                  <!ELEMENT faculty (#PCDATA)>
            
                  <!ELEMENT group (#PCDATA)>
                  <!ATTLIST group
                    id CDATA #REQUIRED
                  >
            ]>
            """;

    public static final String DTD_BULK_REQUEST = """
            <!DOCTYPE Users [
                <!ELEMENT Users (User*)>
                <!ELEMENT User EMPTY>
                <!ATTLIST User
                  username CDATA #REQUIRED
                >
            ]>
            """;

    // Default does not throw anything...
    private static final ErrorHandler WARNINGS_OKAY_HANDLER = new ErrorHandler() {
        @Override
        public void warning(SAXParseException exception) throws SAXException {
            System.out.println("Warning: " + exception.getMessage());
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            throw exception;
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            throw exception;
        }
    };

    private static boolean validateXMLSnippet(String xml, String doctypeWithInlineDTD) throws IOException {
        String xmlWithDoctype = doctypeWithInlineDTD + xml;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true); // DTD
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(WARNINGS_OKAY_HANDLER);
        } catch (ParserConfigurationException e) {
            // This should never happen
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        try {
            builder.parse(new InputSource(new StringReader(xmlWithDoctype)));
        } catch (SAXException e) {
            return false;
        }
        return true;
    }

    public static boolean validateWithPassword(String xml) throws IOException {
        ClientLogger.getInstance().addEvent(new Event("User", "root", "DTDWithPassword", "Doing DTD validation w/ password"));
        return validateXMLSnippet(xml, DTD_WITH_PASSWORD);
    }

    public static boolean validateWithoutPassword(String xml) throws IOException {
        ClientLogger.getInstance().addEvent(new Event("User", "root", "DTDWithoutPassword", "Doing DTD validation w/o password"));
        return validateXMLSnippet(xml, DTD_WITHOUT_PASSWORD);
    }

    public static boolean validateBulkRequest(String xml) throws IOException {
        ClientLogger.getInstance().addEvent(new Event("User", "root", "DTDBulk", "Doing DTD validation for bulk request"));
        return validateXMLSnippet(xml, DTD_BULK_REQUEST);
    }
}
