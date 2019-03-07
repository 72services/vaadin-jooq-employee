package com.example.employee.service;

import com.example.employee.model.tables.records.DepartmentRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.employee.model.tables.Department.DEPARTMENT;

@Service
@Transactional(readOnly = true)
public class DepartmentService {

    private final DSLContext dsl;

    public DepartmentService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public List<DepartmentRecord> findAll() {
        return dsl
                .selectFrom(DEPARTMENT)
                .orderBy(DEPARTMENT.NAME)
                .fetch();
    }

    @Transactional
    public void save(DepartmentRecord departmentRecord) {
        dsl.attach(departmentRecord);

        if (departmentRecord.getId() == null) {
            departmentRecord.insert();
        } else {
            departmentRecord.update();
        }
    }
}
