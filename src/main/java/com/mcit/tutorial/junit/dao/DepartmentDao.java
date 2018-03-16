package com.mcit.tutorial.junit.dao;

import com.mcit.tutorial.junit.model.Department;

import java.util.Comparator;
import java.util.List;

/**
 * Data access for department
 *
 * @author Kasidit
 */
public class DepartmentDao extends HibernateUtil<Department, Long> {
    private static DepartmentDao instance = null;

    public static DepartmentDao getInstance() {
        if (instance == null) {
            instance = new DepartmentDao();
        }
        return instance;
    }

    private DepartmentDao() { }

    // To hide class parameter
    public Department findById(Long id) {
        return this.findById(Department.class, id);
    }

    public List<Department> findAll() {
        return this.findAll(Department.class);
    }

    public Department save(Department department) {
        Department newDepartment = super.save(department);
        if (newDepartment.getId() == null) {
            List<Department> departmentList = this.findAll();
            return departmentList.stream().max(Comparator.comparingInt(d -> d.getId().intValue())).get();
        } else {
            return newDepartment;
        }
    }

    public void deleteAll() {
        this.deleteAll(Department.class);
    }
}
