package com.example.employee.data;

import com.example.employee.model.tables.records.DepartmentRecord;
import com.example.employee.service.DepartmentService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DepartmentPopulator {

    private final DepartmentService departmentService;

    public DepartmentPopulator(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void insertItDepartment() {
        DepartmentRecord departmentRecord = new DepartmentRecord(null, "IT");
        departmentService.save(departmentRecord);
    }
}
