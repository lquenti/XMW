package xmw.exa.models.lectures;

import xmw.exa.models.courses.Course;
import xmw.exa.db.DB;

import java.time.LocalDateTime;

public class Lecture {
    private int id;
    private int courseId;
    private LocalDateTime start;
    private LocalDateTime end;
    private String roomOrLink;

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
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

    public String getRoomOrLink() {
        return roomOrLink;
    }

    public void setRoomOrLink(String roomOrLink) {
        this.roomOrLink = roomOrLink;
    }

    // Relationship methods
    public Course getCourse() {
        return DB.getInstance().courses().all().stream()
                .filter(course -> course.getId() == courseId)
                .findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return "Lecture{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", start=" + start +
                ", end=" + end +
                ", roomOrLink='" + roomOrLink + '\'' +
                '}';
    }
}