package com.mcit.tutorial.junit.dao;

import com.mcit.tutorial.junit.model.Course;

import java.util.Comparator;
import java.util.List;

/**
 * Data access for course
 *
 * @author Kasidit
 */
public class CourseDao extends HibernateUtil<Course, Long> {
    private static CourseDao instance = null;

    public static CourseDao getInstance() {
        if (instance == null) {
            instance = new CourseDao();
        }
        return instance;
    }

    private CourseDao() { }

    // To hide class parameter
    public Course findById(Long id) {
        return this.findById(Course.class, id);
    }

    public List<Course> findAll() {
        return this.findAll(Course.class);
    }

    public Course save(Course course) {
        Course newCourse = super.save(course);
        if (newCourse.getId() == null) {
            List<Course> courseList = this.findAll();
            return courseList.stream().max(Comparator.comparingInt(c -> c.getId().intValue())).get();
        } else {
            return newCourse;
        }
    }

    public void deleteAll() {
        this.deleteAll(Course.class);
    }
}
