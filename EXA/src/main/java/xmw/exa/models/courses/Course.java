package xmw.exa.models.courses;

import java.util.ArrayList;
import java.util.List;

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
    private int id;
    private String name; // Name of the course
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
        var repo = new LecturerRepository(DB.getInstance().getContext(), DB.DB_NAME);
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
        var repo = new SemesterRepository(DB.getInstance().getContext(), DB.DB_NAME);
        return repo.all().stream().filter(
                semester -> semester.getId() == semesterId).findFirst().orElse(null);
    }

    public List<Exam> getExams() {
        var repo = new ExamRepository(DB.getInstance().getContext(), DB.DB_NAME);
        return repo.all().stream()
                .filter(exam -> exam.getCourseId() == id)
                .toList();
    }

    // Relationship methods
    public List<Lecture> getLectures() {
        var repo = new LectureRepository(DB.getInstance().getContext(), DB.DB_NAME);
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

    public static Course parseXml(String xml) {
        Course course = new Course();
        try {
            // Extract values using regex patterns
            String idPattern = "<id>([^<]*)</id>";
            String namePattern = "<name>([^<]*)</name>";
            String facultyPattern = "<faculty>([^<]*)</faculty>";
            String lecturerIdPattern = "<lecturer_id>([^<]*)</lecturer_id>";
            String maxStudentsPattern = "<max_students>([^<]*)</max_students>";
            String semesterIdPattern = "<semester_id>([^<]*)</semester_id>";

            java.util.regex.Pattern idRegex = java.util.regex.Pattern.compile(idPattern);
            java.util.regex.Pattern nameRegex = java.util.regex.Pattern.compile(namePattern);
            java.util.regex.Pattern facultyRegex = java.util.regex.Pattern.compile(facultyPattern);
            java.util.regex.Pattern lecturerIdRegex = java.util.regex.Pattern.compile(lecturerIdPattern);
            java.util.regex.Pattern maxStudentsRegex = java.util.regex.Pattern.compile(maxStudentsPattern);
            java.util.regex.Pattern semesterIdRegex = java.util.regex.Pattern.compile(semesterIdPattern);

            java.util.regex.Matcher idMatcher = idRegex.matcher(xml);
            java.util.regex.Matcher nameMatcher = nameRegex.matcher(xml);
            java.util.regex.Matcher facultyMatcher = facultyRegex.matcher(xml);
            java.util.regex.Matcher lecturerIdMatcher = lecturerIdRegex.matcher(xml);
            java.util.regex.Matcher maxStudentsMatcher = maxStudentsRegex.matcher(xml);
            java.util.regex.Matcher semesterIdMatcher = semesterIdRegex.matcher(xml);

            if (idMatcher.find()) {
                course.setId(Integer.parseInt(idMatcher.group(1)));
            }
            if (nameMatcher.find()) {
                course.setName(nameMatcher.group(1));
            }
            if (facultyMatcher.find()) {
                course.setFaculty(facultyMatcher.group(1));
            }
            if (lecturerIdMatcher.find()) {
                course.setLecturerId(Integer.parseInt(lecturerIdMatcher.group(1)));
            }
            if (maxStudentsMatcher.find()) {
                course.setMaxStudents(Integer.parseInt(maxStudentsMatcher.group(1)));
            }
            if (semesterIdMatcher.find()) {
                course.setSemesterId(Integer.parseInt(semesterIdMatcher.group(1)));
            }

            return course;
        } catch (Exception e) {
            System.err.println("Error processing course element: " + xml);
            e.printStackTrace();
            return null;
        }
    }

    public static List<Course> parseXmlList(String xml) {
        List<Course> courses = new ArrayList<>();
        String[] courseElements = xml.split("</Course>");
        for (String element : courseElements) {
            if (element.trim().isEmpty()) {
                continue;
            }
            Course course = parseXml(element);
            if (course != null) {
                courses.add(course);
            }
        }
        return courses;
    }
}