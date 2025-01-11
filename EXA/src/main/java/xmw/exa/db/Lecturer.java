package xmw.exa.db;

import java.util.List;

public class Lecturer {
    private String username;
    private String faculty;
    private String name;
    private String firstname;
    private int id;

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    // Helper method to get full name
    public String getFullName() {
        return firstname + " " + name;
    }

    // Relationship methods
    public List<Course> getCourses() {
        return DB.getInstance().getAllCourses().stream()
                .filter(course -> course.getLecturerId() == id)
                .toList();
    }

    @Override
    public String toString() {
        return "Lecturer{" +
                "username='" + username + '\'' +
                ", faculty='" + faculty + '\'' +
                ", name='" + name + '\'' +
                ", firstname='" + firstname + '\'' +
                ", id=" + id +
                '}';
    }
}