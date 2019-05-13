package com.example.employee.data;

import com.example.employee.model.tables.records.DepartmentRecord;
import org.jooq.DSLContext;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DepartmentPopulator {

    private final DSLContext dslContext;

    public DepartmentPopulator(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void insertItDepartment() {
        DepartmentRecord departmentRecord = new DepartmentRecord(null, "IT");
        dslContext.attach(departmentRecord);
        departmentRecord.store();
    }
}
