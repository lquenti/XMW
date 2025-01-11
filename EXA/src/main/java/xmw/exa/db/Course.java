package xmw.exa.db;

public class Course {
    private int id;
    private String name; // 'n' in XML
    private String faculty;
    private int lecturerId;
    private int maxStudents;
    private int semesterId;

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public int getLecturerId() {
        return lecturerId;
    }

    public Lecturer getLecturer() {
        return DB.getInstance().getAllLecturers().stream().filter(
                lecturer -> lecturer.getId() == lecturerId).findFirst().orElse(null
        );
    }

    public void setLecturerId(int lecturerId) {
        this.lecturerId = lecturerId;
    }

    public int getMaxStudents() {
        return maxStudents;
    }

    public void setMaxStudents(int maxStudents) {
        this.maxStudents = maxStudents;
    }

    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", faculty='" + faculty + '\'' +
                ", lecturerId=" + lecturerId +
                ", maxStudents=" + maxStudents +
                ", semesterId=" + semesterId +
                '}';
    }
}