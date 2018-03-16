package com.mcit.tutorial.junit.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "student")
public class Student {
    @Id
    @SequenceGenerator(initialValue = 1, allocationSize = 100, name = "student_sequence", sequenceName = "student_sequence")
    @GeneratedValue(generator = "student_sequence")
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "student_course",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private Set<Course> courses;

    public Student() { }
    public Student(Long id, String name, Department department, Set<Course> courses) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.courses = courses;
    }

    public void setId(long id) { this.id = id; }
    public Long getId() { return this.id; }
    public void setName(String name) { this.name = name; }
    public String getName() { return this.name; }
    public void setDepartment(Department department) { this.department = department; }
    public Department getDepartment() { return this.department; }
    public void setCourses(Set<Course> courses) { this.courses = courses; }
    public Set<Course> getCourses() {
        if (courses == null) {
            courses = new HashSet<>();
        }
        return courses;
    }
}
