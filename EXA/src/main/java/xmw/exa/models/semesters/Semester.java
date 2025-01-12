package xmw.exa.models.semesters;

import xmw.exa.models.courses.Course;
import xmw.exa.db.DB;

import java.time.LocalDateTime;
import java.util.List;

public class Semester {
    private int id;
    private String name;
    private LocalDateTime start;
    private LocalDateTime end;

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

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    // Relationship methods
    public List<Course> getCourses() {
        return DB.getInstance().courses().all().stream()
                .filter(course -> course.getSemesterId() == id)
                .toList();
    }

    @Override
    public String toString() {
        return "Semester{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}