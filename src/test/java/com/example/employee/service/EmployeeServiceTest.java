package com.example.employee.service;

import com.example.employee.model.tables.records.EmployeeRecord;
import com.example.employee.model.tables.records.VEmployeeRecord;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Transactional
@SpringBootTest
@RunWith(SpringRunner.class)
public class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;

    private int employeeId;

    @Before
    public void insertEmployee() {
        EmployeeRecord employee = createEmployee();
        employeeId = employee.getId();
    }

    @Test
    public void findAll() {
        List<VEmployeeRecord> employees = employeeService.findAll("Simon");

        assertEquals(0, employees.size());
    }

    @Test
    public void delete() {
        EmployeeRecord employee = findEmployeeById();

        employeeService.delete(employee);
    }

    @Test
    public void save() {
        createEmployee();
    }

    @Test
    public void findById() {
        findEmployeeById();
    }

    private EmployeeRecord createEmployee() {
        EmployeeRecord employee = new EmployeeRecord(null, "Peter", 1);
        employeeService.save(employee);

        assertNotNull(employee.getId());

        return employee;
    }

    private EmployeeRecord findEmployeeById() {
        EmployeeRecord employee = employeeService.findById(employeeId);

        assertNotNull(employee);

        return employee;
    }
}