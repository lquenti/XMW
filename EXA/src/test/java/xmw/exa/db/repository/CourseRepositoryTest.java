package xmw.exa.db.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import xmw.exa.models.courses.Course;
import xmw.exa.models.courses.CourseRepository;
import xmw.exa.db.DB;

class CourseRepositoryTest {
    private CourseRepository repository;
    private DB db;

    @BeforeEach
    void setUp() {
        db = DB.getInstance();
        repository = db.courses();
    }

    @AfterEach
    void tearDown() {
        DB.close(db.getContext());
    }

    @Test
    void testGetAll() {
        List<Course> courses = repository.all();
        assertNotNull(courses);
        assertFalse(courses.isEmpty());

        // Verify first course from mock data
        Course firstCourse = courses.get(0);
        assertEquals(1, firstCourse.getId());
        assertEquals("omnis", firstCourse.getFaculty());
        assertEquals(10, firstCourse.getLecturerId());
        assertEquals(76, firstCourse.getMaxStudents());
        assertEquals("rerum", firstCourse.getName());
        assertEquals(1, firstCourse.getSemesterId());
    }

    @Test
    void testGetById() {
        Course course = repository.getById(1L);
        assertNotNull(course);
        assertEquals(1, course.getId());
        assertEquals("omnis", course.getFaculty());
        assertEquals(10, course.getLecturerId());
        assertEquals(76, course.getMaxStudents());
        assertEquals("rerum", course.getName());
        assertEquals(1, course.getSemesterId());
    }

    @Test
    void testGetByIdNonExistent() {
        Course course = repository.getById(999);
        assertNull(course);
    }

    @Test
    void testCreate() {
        // Create a new course
        Course newCourse = new Course();
        newCourse.setName("Test Course");
        newCourse.setFaculty("Test Faculty");
        newCourse.setLecturerId(1);
        newCourse.setMaxStudents(50);
        newCourse.setSemesterId(1);

        // Get initial count
        int initialCount = repository.all().size();

        // Create the course
        boolean result = repository.create(newCourse);
        assertTrue(result);
        assertTrue(newCourse.getId() > 0);

        try {
            // Reinitialize DB to persist changes
            db.reinitialize();

            // Verify the course was added
            List<Course> courses = repository.all();
            assertEquals(initialCount + 1, courses.size());

            // Get the newly created course
            Course createdCourse = repository.getById(newCourse.getId());
            assertNotNull(createdCourse);
            assertEquals(newCourse.getName(), createdCourse.getName());
            assertEquals(newCourse.getFaculty(), createdCourse.getFaculty());
            assertEquals(newCourse.getLecturerId(), createdCourse.getLecturerId());
            assertEquals(newCourse.getMaxStudents(), createdCourse.getMaxStudents());
            assertEquals(newCourse.getSemesterId(), createdCourse.getSemesterId());
        } catch (Exception e) {
            fail("Failed to reinitialize database: " + e.getMessage());
        }
    }

    @Test
    void testCreateAutoIncrementId() {
        // Create first course
        Course course1 = new Course();
        course1.setName("Test Course 1");
        course1.setFaculty("Test Faculty");
        course1.setLecturerId(1);
        course1.setMaxStudents(50);
        course1.setSemesterId(1);

        // Create second course
        Course course2 = new Course();
        course2.setName("Test Course 2");
        course2.setFaculty("Test Faculty");
        course2.setLecturerId(1);
        course2.setMaxStudents(50);
        course2.setSemesterId(1);

        // Create both courses
        repository.create(course1);
        repository.create(course2);

        try {
            db.reinitialize();
            assertTrue(course2.getId() > course1.getId());
        } catch (Exception e) {
            fail("Failed to reinitialize database: " + e.getMessage());
        }
    }
}