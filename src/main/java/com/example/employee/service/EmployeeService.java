package com.example.employee.service;

import com.example.employee.model.tables.records.EmployeeRecord;
import com.example.employee.model.tables.records.VEmployeeRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.employee.model.tables.Employee.EMPLOYEE;
import static com.example.employee.model.tables.VEmployee.V_EMPLOYEE;

@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private final DSLContext dsl;

    public EmployeeService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public List<VEmployeeRecord> findAll(String value) {
        return dsl
                .selectFrom(V_EMPLOYEE)
                .where(V_EMPLOYEE.EMPLOYEE_NAME.like("%" + value + "%"))
                .orderBy(V_EMPLOYEE.EMPLOYEE_NAME)
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

    public EmployeeRecord findById(Integer id) {
        return dsl
                .selectFrom(EMPLOYEE)
                .where(EMPLOYEE.ID.eq(id))
                .fetchOne();
    }
}
