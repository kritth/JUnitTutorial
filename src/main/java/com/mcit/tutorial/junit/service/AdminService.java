package com.mcit.tutorial.junit.service;

import com.mcit.tutorial.junit.dao.CourseDao;
import com.mcit.tutorial.junit.dao.DepartmentDao;
import com.mcit.tutorial.junit.config.HibernateConfiguration;
import com.mcit.tutorial.junit.dao.StudentDao;
import com.mcit.tutorial.junit.model.Course;
import com.mcit.tutorial.junit.model.Department;
import com.mcit.tutorial.junit.model.Student;

import java.util.List;

/**
 * Administrator service
 *
 * @author Kasidit
 */
public class AdminService {
    private CourseDao courseDao;
    private StudentDao studentDao;
    private DepartmentDao departmentDao;

    public void setCourseDao(CourseDao courseDao) { this.courseDao = courseDao; }
    public void setStudentDao(StudentDao studentDao) { this.studentDao = studentDao; }
    public void setDepartmentDao(DepartmentDao departmentDao) { this.departmentDao = departmentDao; }

    /*
     ****************************************************************
     * Main program
     ****************************************************************
     */

    public static void main(String[] args) {
        try {
            // Set up application
            HibernateConfiguration.startup();
            AdminService service = new AdminService(); // Load service
            service.setCourseDao(CourseDao.getInstance());
            service.setStudentDao(StudentDao.getInstance());
            service.setDepartmentDao(DepartmentDao.getInstance());

            // Set up school
            Department department = service.createDepartment("computer");
            Course course = service.createCourse("introduction", department);
            Course course2 = service.createCourse("advanced", department);

            // Run simulation
            Student student = service.enroll("test person");
            student = service.findStudentById(student.getId());
            student = service.joinDepartment(department.getId(), student);
            student = service.joinCourse(course.getId(), student);
            student = service.joinCourse(course2.getId(), student);
            student = service.leaveCourse(course.getId(), student);
            student = service.leaveCourse(course2.getId(), student);
            student = service.leaveDepartment(student.getDepartment().getId(), student);
            service.quit(student);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            HibernateConfiguration.shutdown();
        }
    }

    /*
     ****************************************************************
     * Student
     ****************************************************************
     */

    public Student enroll(String studentName) {
        List<Student> studentList = studentDao.findAll();
        if (studentList.stream().noneMatch(student -> student.getName().equalsIgnoreCase(studentName))) {
            Student student = studentDao.save(new Student(null, studentName, null, null));
            System.out.println("Student: " + studentName + " has joined the school");
            return student;
        } else {
            throw new IllegalArgumentException("Student has already enrolled");
        }
    }

    public Student findStudentById(Long id) {
        Student student = studentDao.findById(id);
        System.out.println("Student: " + student.getName() + " is confirmed to be registered in the school");
        return student;
    }

    public Student quit(Student student) {
        student = studentDao.delete(student);
        System.out.println("Student: " + student.getName() + " has quited the school");
        return student;
    }

    /*
     ****************************************************************
     * Department
     ****************************************************************
     */

    public Department createDepartment(String departmentName) {
        List<Department> departmentList = departmentDao.findAll();
        if (departmentList.stream().noneMatch(department -> department.getName().equalsIgnoreCase(departmentName))) {
            Department department = departmentDao.save(new Department(null, departmentName, null, null));
            System.out.println("Department: " + departmentName + " has been created");
            return department;
        } else {
            throw new IllegalArgumentException("Department has already existed");
        }
    }

    public Student joinDepartment(Long departmentId, Student student) {
        if (student.getCourses().isEmpty()) {
            Department department = departmentDao.findById(departmentId);
            if (department != null) {
                student.setDepartment(department);
                student = studentDao.save(student);
                System.out.println("Student: " + student.getName() + " has joined department: " + department.getName());
                return student;
            } else {
                throw new IllegalArgumentException("Given department does not exist");
            }
        } else {
            throw new IllegalArgumentException("Student is not allow to join other department with active courses");
        }
    }

    public Student leaveDepartment(Long departmentId, Student student) {
        if (student.getDepartment().getId().equals(departmentId)) {
            if (student.getCourses().isEmpty()) {
                Department department = student.getDepartment();
                student.setDepartment(null);
                student = studentDao.save(student);
                System.out.println("Student: " + student.getName() + " has left department: " + department.getName());
                return student;
            } else {
                throw new IllegalArgumentException("Student cannot leave department when there is at least one active course");
            }
        } else {
            throw new IllegalArgumentException("Student is not in this department");
        }
    }

    /*
     ****************************************************************
     * Course
     ****************************************************************
     */

    public Course createCourse(String courseName, Department department) {
        Department checkDepartment = departmentDao.findById(department.getId());
        List<Course> courseList = courseDao.findAll();
        if (checkDepartment != null && courseList.stream().noneMatch(course -> course.getName().equalsIgnoreCase(courseName))) {
            Course course = courseDao.save(new Course(null, courseName, department, null));
            System.out.println("Course: " + courseName + " has been created");
            return course;
        } else {
            throw new IllegalArgumentException("Course has already existed");
        }
    }

    public Student joinCourse(Long courseId, Student student) {
        Course course = courseDao.findById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Course does not exist");
        } else if (!course.getDepartment().getId().equals(student.getDepartment().getId())) {
            throw new IllegalArgumentException("Student is not in the same department with the course");
        } else {
            student.getCourses().add(course);
            student = studentDao.save(student);
            System.out.println("Student: " + student.getName() + " has joined course: " + course.getName());
            return student;
        }
    }

    public Student leaveCourse(Long courseId, Student student) {
        Course course = courseDao.findById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Student is not in the same department with course");
        } else {
            // Only leave course if student is registered to course
            if (student.getCourses().stream().anyMatch(c -> c.getId().equals(courseId))) {
                student.getCourses().removeIf(c -> c.getId().equals(course.getId()));
                student = studentDao.save(student);
                System.out.println("Student: " + student.getName() + " has left course: " + course.getName());
                return student;
            } else {
                throw new IllegalArgumentException("Student is not registered to course");
            }
        }
    }
}