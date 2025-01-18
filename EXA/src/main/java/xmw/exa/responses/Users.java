package xmw.exa.responses;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class Users {
    private Users() {
    }

    public static List<User> getUsers(String responseXML) {
        List<User> users = new ArrayList<>();
        User currentUser = null;
        String currentElement = "";

        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(responseXML));

            while (reader.hasNext()) {
                int event = reader.next();

                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        currentElement = reader.getLocalName();
                        if ("User".equals(currentElement)) {
                            currentUser = new User();
                            currentUser.setUsername(reader.getAttributeValue(null, "username"));
                        } else if ("group".equals(currentElement)) {
                            String groupId = reader.getAttributeValue(null, "id");
                            if (currentUser != null) {
                                currentUser.addGroup(groupId);
                            }
                        }
                        break;

                    case XMLStreamConstants.CHARACTERS:
                        String text = reader.getText().trim();
                        if (text.isEmpty() || currentUser == null) {
                            continue;
                        }

                        switch (currentElement) {
                            case "name":
                                currentUser.setName(text);
                                break;
                            case "firstname":
                                currentUser.setFirstname(text);
                                break;
                            case "faculty":
                                currentUser.setFaculty(text);
                                break;
                        }
                        break;

                    case XMLStreamConstants.END_ELEMENT:
                        if ("User".equals(reader.getLocalName()) && currentUser != null) {
                            users.add(currentUser);
                            currentUser = null;
                        }
                        break;
                }
            }
            reader.close();
        } catch (XMLStreamException e) {
            throw new RuntimeException("Failed to parse XML", e);
        }

        return users;
    }
}
