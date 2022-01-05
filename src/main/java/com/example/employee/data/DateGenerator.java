package com.example.employee.data;

import com.example.employee.model.tables.records.DepartmentRecord;
import com.example.employee.model.tables.records.EmployeeRecord;
import org.jooq.DSLContext;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DateGenerator {

    private final DSLContext dslContext;

    public DateGenerator(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void insertData() {
        var itDepartment = new DepartmentRecord(null, "IT");
        dslContext.attach(itDepartment);
        itDepartment.store();

        var hermioneCompton = new EmployeeRecord(null, "Hermione Compton", itDepartment.getId(), 49);
        dslContext.attach(hermioneCompton);
        hermioneCompton.store();

        var lysandraStevens = new EmployeeRecord(null, "Lysandra Stevens", itDepartment.getId(), 22);
        dslContext.attach(lysandraStevens);
        lysandraStevens.store();
    }
}
