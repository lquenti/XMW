package xmw.exa.responses;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String name;
    private String firstname;
    private String faculty;
    private List<Group> groups = new ArrayList<>();

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public void addGroup(String groupId) {
        Group group = new Group(groupId, "");
        groups.add(group);
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getFaculty() {
        return faculty;
    }

    public List<Group> getGroups() {
        return groups;
    }
}
