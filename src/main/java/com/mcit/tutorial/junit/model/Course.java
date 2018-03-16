package com.mcit.tutorial.junit.model;

import com.sun.istack.internal.NotNull;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "course")
public class Course {
    @Id
    @SequenceGenerator(initialValue = 1, allocationSize = 100, name = "course_sequence", sequenceName = "course_sequence")
    @GeneratedValue(generator = "course_sequence")
    private Long id;

    @NotNull
    @Column(name = "name")
    private String name;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "courses", fetch = FetchType.EAGER)
    private Set<Student> students;

    public Course() { }
    public Course(Long id, String name, Department department, Set<Student> students) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.students = students;
    }

    public void setId(long id) { this.id = id; }
    public Long getId() { return this.id; }
    public void setName(String name) { this.name = name; }
    public String getName() { return this.name; }
    public void setDepartment(Department department) { this.department = department; }
    public Department getDepartment() { return this.department; }
    public void setStudents(Set<Student> students) { this.students = students; }
    public Set<Student> getStudents() {
        if (students == null) {
            students = new HashSet<>();
        }
        return students;
    }
}
