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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import xmw.exa.db.DB;
import xmw.exa.models.courses.Course;
import xmw.exa.models.courses.CourseRepository;

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
    }

    @Test
    void testGet() {
        List<Course> courseList = repository.all();
        Course course = repository.all().get(0);
        assertNotNull(course);
        assertTrue(courseList.contains(course));
    }

    @Test
    void testGetNonExistent() {
        Course course = repository.get(999);
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
            Course createdCourse = repository.get(newCourse.getId());
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

    @Disabled
    @Test
    void testDelete() {
        // Get initial count
        int initialCount = repository.all().size();

        Course firstCourse = repository.all().get(0);
        repository.delete(firstCourse.getId());

        try {
            // Reinitialize DB to persist changes
            db.reinitialize();

            // Verify course was deleted
            List<Course> courses = repository.all();
            assertEquals(initialCount - 1, courses.size());

            // Verify course no longer exists
            Course course = repository.get(1L);
            assertNull(course);
        } catch (Exception e) {
            fail("Failed to reinitialize database: " + e.getMessage());
        }
    }

    @Test
    void testDeleteNonExistent() {
        // Get initial count
        int initialCount = repository.all().size();

        // Try to delete non-existent course
        repository.delete(9099L);

        try {
            // Reinitialize DB to persist changes
            db.reinitialize();

            // Verify no courses were deleted
            List<Course> courses = repository.all();
            assertEquals(initialCount, courses.size());
        } catch (Exception e) {
            fail("Failed to reinitialize database: " + e.getMessage());
        }
    }

    @Test
    void testUpdate() {
        // Get an existing course
        Course existingCourse = repository.all().get(0);
        long originalId = existingCourse.getId();

        // Modify the course
        String newName = "Updated Course Name";
        String newFaculty = "Updated Faculty";
        existingCourse.setName(newName);
        existingCourse.setFaculty(newFaculty);

        // Update the course
        Course updatedCourse = repository.update(existingCourse);

        try {

            // Verify the update
            assertNotNull(updatedCourse);
            assertEquals(originalId, updatedCourse.getId());
            assertEquals(newName, updatedCourse.getName());
            assertEquals(newFaculty, updatedCourse.getFaculty());

            // Verify the update persisted
            Course retrievedCourse = repository.get(originalId);
            assertNotNull(retrievedCourse);
            assertEquals(newName, retrievedCourse.getName());
            assertEquals(newFaculty, retrievedCourse.getFaculty());
        } catch (Exception e) {
            fail("Failed to reinitialize database: " + e.getMessage());
        }
    }

    @Test
    void testUpdateNonExistent() {
        // Create a course with non-existent ID
        Course nonExistentCourse = new Course();
        nonExistentCourse.setId(999);
        nonExistentCourse.setName("Non-existent Course");
        nonExistentCourse.setFaculty("Test Faculty");
        nonExistentCourse.setLecturerId(1);
        nonExistentCourse.setMaxStudents(50);
        nonExistentCourse.setSemesterId(1);

        // Try to update non-existent course
        Course result = repository.update(nonExistentCourse);

        try {
            // Reinitialize DB to persist changes
            db.reinitialize();

            // Verify the update failed
            assertNull(result);

            // Verify the course doesn't exist
            Course retrievedCourse = repository.get(999);
            assertNull(retrievedCourse);
        } catch (Exception e) {
            fail("Failed to reinitialize database: " + e.getMessage());
        }
    }
}
