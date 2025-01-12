package xmw.exa.models.courses;

import java.util.List;
import java.util.Objects;

import xmw.exa.db.DB;
import xmw.exa.models.exams.Exam;
import xmw.exa.models.exams.ExamRepository;
import xmw.exa.models.lectureres.Lecturer;
import xmw.exa.models.lectureres.LecturerRepository;
import xmw.exa.models.lectures.Lecture;
import xmw.exa.models.lectures.LectureRepository;
import xmw.exa.models.semesters.Semester;
import xmw.exa.models.semesters.SemesterRepository;

public class Course {
    private long id;
    private String name; // 'n' in XML
    private String faculty;
    private int lecturerId;
    private int maxStudents;
    private int semesterId;

    // Getters and setters
    public long getId() {
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
        var repo = new LecturerRepository(DB.getInstance().getContext());
        return repo.all().stream().filter(
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
        var repo = new SemesterRepository(DB.getInstance().getContext());
        return repo.all().stream().filter(
                semester -> semester.getId() == semesterId).findFirst().orElse(null);
    }

    public List<Exam> getExams() {
        var repo = new ExamRepository(DB.getInstance().getContext());
        return repo.all().stream()
                .filter(exam -> exam.getCourseId() == id)
                .toList();
    }

    // Relationship methods
    public List<Lecture> getLectures() {
        var repo = new LectureRepository(DB.getInstance().getContext());
        return repo.all().stream()
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return id == course.id && lecturerId == course.lecturerId && maxStudents == course.maxStudents && semesterId == course.semesterId && Objects.equals(name, course.name) && Objects.equals(faculty, course.faculty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, faculty, lecturerId, maxStudents, semesterId);
    }
}