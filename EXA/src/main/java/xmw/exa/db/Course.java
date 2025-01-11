package xmw.exa.db;

import java.util.List;

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
                lecturer -> lecturer.getId() == lecturerId).findFirst().orElse(null);
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

    public Semester getSemester() {
        return DB.getInstance().getAllSemesters().stream().filter(
                semester -> semester.getId() == semesterId).findFirst().orElse(null);
    }

    public List<Exam> getExams() {
        return DB.getInstance().getAllExams().stream()
                .filter(exam -> exam.getCourseId() == id)
                .toList();
    }

    // Relationship methods
    public List<Lecture> getLectures() {
        return DB.getInstance().getAllLectures().stream()
                .filter(lecture -> lecture.getCourseId() == id)
                .sorted((l1, l2) -> l1.getStart().compareTo(l2.getStart()))
                .toList();
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