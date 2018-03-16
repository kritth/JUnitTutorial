package com.mcit.tutorial.junit.dao;

import com.mcit.tutorial.junit.model.Student;

import java.util.Comparator;
import java.util.List;

/**
 * Data access for student
 *
 * @author Kasidit
 */
public class StudentDao extends HibernateUtil<Student, Long> {
    private static StudentDao instance = null;

    public static StudentDao getInstance() {
        if (instance == null) {
            instance = new StudentDao();
        }
        return instance;
    }

    private StudentDao() { }

    public Student findById(Long id) {
        return this.findById(Student.class, id);
    }

    public List<Student> findAll() {
        return this.findAll(Student.class);
    }

    public Student save(Student student) {
        Student newStudent = super.save(student);
        if (newStudent.getId() == null) {
            List<Student> studentList = this.findAll();
            return studentList.stream().max(Comparator.comparingInt(s -> s.getId().intValue())).get();
        } else {
            return newStudent;
        }
    }

    public void deleteAll() {
        this.deleteAll(Student.class);
    }
}
