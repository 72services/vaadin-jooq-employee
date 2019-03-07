package com.example.employee.service;

import com.example.employee.model.tables.records.EmployeeRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.employee.model.tables.Employee.EMPLOYEE;

@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private final DSLContext dsl;

    public EmployeeService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public List<EmployeeRecord> findAll(String value) {
        return dsl
                .selectFrom(EMPLOYEE)
                .where(EMPLOYEE.NAME.like("%" + value + "%"))
                .orderBy(EMPLOYEE.NAME)
                .fetch();
    }

    @Transactional
    public void delete(EmployeeRecord employeeRecord) {
        dsl.attach(employeeRecord);
        employeeRecord.delete();
    }

    @Transactional
    public void save(EmployeeRecord employeeRecord) {
        dsl.attach(employeeRecord);
        if (employeeRecord.getId() == null) {
            employeeRecord.insert();
        } else {
            employeeRecord.update();
        }
    }
}
