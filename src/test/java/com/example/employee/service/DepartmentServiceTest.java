package com.example.employee.service;

import com.example.employee.model.tables.Department;
import com.example.employee.model.tables.records.DepartmentRecord;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@SpringBootTest
@RunWith(SpringRunner.class)
public class DepartmentServiceTest {

    @Autowired
    private DepartmentService departmentService;

    @Test
    public void findAll() {
        List<DepartmentRecord> departments = departmentService.findAll();

        Assert.assertEquals(1, departments.size());
    }

    @Test
    public void save() {
        DepartmentRecord department = new DepartmentRecord(null, "HR");

        departmentService.save(department);
    }
}