package io.seventytwo.demo.vaadinjooq.employee.data;

import io.seventytwo.demo.vaadinjooq.employee.model.tables.records.DepartmentRecord;
import io.seventytwo.demo.vaadinjooq.employee.model.tables.records.EmployeeRecord;
import com.vaadin.exampledata.DataType;
import com.vaadin.exampledata.ExampleDataGenerator;
import org.jooq.DSLContext;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DateGenerator implements ApplicationRunner {

    private final DSLContext dslContext;

    public DateGenerator(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        var itDepartment = new DepartmentRecord();
        itDepartment.setName("Information Technology");
        dslContext.attach(itDepartment);
        itDepartment.store();

        var hrDepartment = new DepartmentRecord();
        hrDepartment.setName("Human Resources");
        dslContext.attach(hrDepartment);
        hrDepartment.store();

        ExampleDataGenerator<EmployeeRecord> dataGenerator = new ExampleDataGenerator<>(EmployeeRecord.class, LocalDateTime.now());
        dataGenerator.setData(EmployeeRecord::setFirstName, DataType.FIRST_NAME);
        dataGenerator.setData(EmployeeRecord::setLastName, DataType.LAST_NAME);

        List<EmployeeRecord> employees = dataGenerator.create(100, 123);
        for (int i = 0; i < employees.size(); i++) {
            EmployeeRecord employeeRecord = employees.get(i);
            if (i % 7 == 0) {
                employeeRecord.setDepartmentId(hrDepartment.getId());
            } else {
                employeeRecord.setDepartmentId(itDepartment.getId());
            }
            dslContext.attach(employeeRecord);
            employeeRecord.store();
        }
    }
}
