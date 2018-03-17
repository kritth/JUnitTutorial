package com.mcit.tutorial.junit.service;

import com.mcit.tutorial.junit.config.HibernateConfiguration;
import com.mcit.tutorial.junit.dao.CourseDao;
import com.mcit.tutorial.junit.dao.DepartmentDao;
import com.mcit.tutorial.junit.dao.StudentDao;
import com.mcit.tutorial.junit.model.Course;
import com.mcit.tutorial.junit.model.Department;
import com.mcit.tutorial.junit.model.Student;
import org.hibernate.ObjectNotFoundException;
import org.junit.*;

import static org.junit.Assert.*;

public class AdminServiceTest {
    /*
     ****************************************************************
     * Class set up and teardown
     ****************************************************************
     */

    // This runs only once at the beginning
    @BeforeClass
    public static void initialize() {
        HibernateConfiguration.startup();
    }

    // This runs only once at the very end after all tests
    @AfterClass
    public static void teardown() {
        HibernateConfiguration.shutdown();
    }

    /*
     ****************************************************************
     * Test set up and teardown
     ****************************************************************
     */

    // What we focus on testing
    private AdminService service;

    // DAO which is involved in the service
    private final StudentDao studentDao = StudentDao.getInstance();
    private final CourseDao courseDao = CourseDao.getInstance();
    private final DepartmentDao departmentDao = DepartmentDao.getInstance();

    // Custom object variables which we can access in every tests
    private Student testStudent;
    private Course testCourse;
    private Department testDepartment;

    // Constant which we can access in every tests
    private final String testStudentName = "TEST STUDENT";
    private final String testCourseName = "TEST COURSE";
    private final String testDepartmentName = "TEST DEPARTMENT";

    // Variables we received from the database in set up
    private Long testStudentId;
    private Long testCourseId;
    private Long testDepartmentId;

    // This runs before every test
    @Before
    public void setup() {
        service = new AdminService(); // We want fresh copy before every test
        service.setStudentDao(studentDao);
        service.setCourseDao(courseDao);
        service.setDepartmentDao(departmentDao);

        // Create test department into db
        testDepartment = new Department();
        testDepartment.setName(testDepartmentName);
        testDepartment = departmentDao.save(testDepartment);
        testDepartmentId = testDepartment.getId();

        // Create test course associated to test department into db
        testCourse = new Course();
        testCourse.setName(testCourseName);
        testCourse.setDepartment(testDepartment);
        testCourse = courseDao.save(testCourse);
        testCourseId = testCourse.getId();

        // Create test student into db
        testStudent = new Student();
        testStudent.setName(testStudentName);
        testStudent = studentDao.save(testStudent);
        testStudentId = testStudent.getId();
    }

    // This runs after every test
    @After
    public void cleanup() {
        studentDao.deleteAll();
        courseDao.deleteAll();
        departmentDao.deleteAll();
    }

    /*
     ****************************************************************
     * Student
     ****************************************************************
     */

    @Test
    public void testEnroll() {
        String expectedResult = "JUNIT TEST NAME";
        Student insertedStudent = service.enroll(expectedResult);
        String actualResult = insertedStudent.getName();

        assertEquals(expectedResult, actualResult);
        assertNotNull(insertedStudent.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEnrollNotAllowDuplicateName() {
        service.enroll(testStudentName); // Because it is inserted in the set up
    }

    @Test
    public void testFindStudentById() {
        Student loadStudent = service.findStudentById(testStudentId);
        String actualResult = loadStudent.getName();
        Long actualId = loadStudent.getId();

        assertEquals(testStudentName, actualResult);
        assertEquals(testStudentId, actualId);
    }

    @Test(expected = NullPointerException.class)
    public void testFindStudentByIdWithInvalidId() {
        Long invalidId = -1L;
        service.findStudentById(invalidId); // Expected null pointer at system.out.println
    }

    @Test
    public void testQuit() {
        service.quit(testStudent);
        assertTrue(studentDao.findAll().isEmpty()); // Because we delete it from database
    }

    @Test(expected = NullPointerException.class)
    public void testQuitInvalidStudent() {
        Student invalidStudent = new Student();
        invalidStudent.setId(-1L); // Set invalid id to make it try to find in database
        service.quit(invalidStudent); // Expected null pointer at system.out.println
    }

    @Test(expected = NullPointerException.class)
    public void testQuitMultipleTimes() {
        service.quit(testStudent);
        service.quit(testStudent); // Expected null pointer at system.out.println
    }

    /*
     ****************************************************************
     * Department
     ****************************************************************
     */

    @Test
    public void testCreateDepartment() {
        String expectedResult = "DEPARTMENT JUNIT NAME";
        Department newDepartment = service.createDepartment(expectedResult);
        String actualResult = newDepartment.getName();

        assertEquals(expectedResult, actualResult);
        assertNotNull(newDepartment.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateDuplicateDepartment() {
        service.createDepartment(testDepartmentName); // Because it is inserted in the set up
    }

    @Test
    public void testJoinDepartment() {
        Student updatedStudent = service.joinDepartment(testDepartmentId, testStudent);
        assertEquals(testDepartmentId, updatedStudent.getDepartment().getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJoinDepartmentWithNonEmptyCourse() {
        testStudent.getCourses().add(testCourse);
        service.joinDepartment(testDepartmentId, testStudent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJoinInvalidDepartment() {
        service.joinDepartment(-1L, testStudent);
    }

    @Test
    public void testLeaveDepartment() {
        testStudent.setDepartment(testDepartment);
        Student updatedStudent = service.leaveDepartment(testDepartmentId, testStudent);
        assertNull(updatedStudent.getDepartment());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLeaveIncorrectDepartment() {
        testStudent.setDepartment(testDepartment);
        service.leaveDepartment(-1L, testStudent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLeaveDepartmentWithNonEmptyCourse() {
        testStudent.getCourses().add(testCourse);
        testStudent.setDepartment(testDepartment);
        service.leaveDepartment(testDepartmentId, testStudent);
    }

    /*
     ****************************************************************
     * Course
     ****************************************************************
     */

    @Test
    public void testCreateCourse() {
        String expectedResult = "NEW COURSE NAME";
        Course newCourse = service.createCourse(expectedResult, testDepartment);
        String actualResult = newCourse.getName();
        Long actualDepartmentId = newCourse.getDepartment().getId();

        assertEquals(expectedResult, actualResult);
        assertEquals(testDepartmentId, actualDepartmentId);
    }

    // We split this if condition into two because it has two conditions to check

    @Test(expected = IllegalArgumentException.class)
    public void testCreateCourseWithInvalidDepartment() {
        String newCourseName = "NEW COURSE NAME";
        Department invalidDepartment = new Department();
        invalidDepartment.setId(-1);
        service.createCourse(newCourseName, invalidDepartment);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateCourseWithDuplicateCourseNameAndValidDepartment() {
        service.createCourse(testCourseName, testDepartment);
    }

    @Test
    public void testJoinCourse() {
        testStudent.setDepartment(testDepartment);
        Student updatedStudent = service.joinCourse(testCourseId, testStudent);
        assertEquals(1, updatedStudent.getCourses().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJoinInvalidCourse() {
        service.joinCourse(-1L, testStudent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJoinCourseWithMismatchDepartment() {
        Department newDepartment = new Department();
        newDepartment.setName("NEW DEPARTMENT");
        newDepartment = departmentDao.save(newDepartment);
        testStudent.setDepartment(newDepartment);
        service.joinCourse(testCourseId, testStudent);
    }

    @Test
    public void testLeaveCourse() {
        testStudent.getCourses().add(testCourse);
        Student updatedStudent = service.leaveCourse(testCourseId, testStudent);
        assertTrue(updatedStudent.getCourses().isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLeaveInvalidCourse() {
        service.leaveCourse(-1L, testStudent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLeaveCourseWithoutBeingRegistered() {
        service.leaveCourse(testCourseId, testStudent);
    }
}