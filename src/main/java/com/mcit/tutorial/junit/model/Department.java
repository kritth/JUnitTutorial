package com.mcit.tutorial.junit.model;

import com.sun.istack.internal.NotNull;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "department")
public class Department {
    @Id
    @SequenceGenerator(initialValue = 1, allocationSize = 100, name = "department_sequence", sequenceName = "department_sequence")
    @GeneratedValue(generator = "department_sequence")
    private Long id;

    @NotNull
    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "department", fetch = FetchType.EAGER)
    private Set<Student> students;

    @OneToMany(mappedBy = "department", fetch = FetchType.EAGER)
    private Set<Course> courses;

    public Department() { }
    public Department(Long id, String name, Set<Student> students, Set<Course> courses) {
        this.id = id;
        this.name = name;
        this.students = students;
        this.courses = courses;
    }

    public void setId(long id) { this.id = id; }
    public Long getId() { return this.id; }
    public void setName(String name) { this.name = name; }
    public String getName() { return this.name; }
    public void setCourses(Set<Course> courses) { this.courses = courses; }
    public Set<Course> getCourses() {
        if (courses == null) {
            courses = new HashSet<>();
        }
        return courses;
    }
    public void setStudents(Set<Student> students) { this.students = students; }
    public Set<Student> getStudents() {
        if (students == null) {
            students = new HashSet<>();
        }
        return students;
    }
}
