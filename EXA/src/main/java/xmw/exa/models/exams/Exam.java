package xmw.exa.models.exams;

import xmw.exa.models.courses.Course;
import xmw.exa.db.DB;

import java.time.LocalDateTime;

public class Exam {
    private int id;
    private int courseId;
    private LocalDateTime date;
    private boolean isOnline;
    private boolean isWritten;
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean isWritten() {
        return isWritten;
    }

    public void setWritten(boolean written) {
        isWritten = written;
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
        return "Exam{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", date=" + date +
                ", isOnline=" + isOnline +
                ", isWritten=" + isWritten +
                ", roomOrLink='" + roomOrLink + '\'' +
                '}';
    }
}