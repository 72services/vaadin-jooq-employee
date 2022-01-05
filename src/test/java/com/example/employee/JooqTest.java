package com.example.employee;

import com.example.employee.model.tables.records.DepartmentRecord;
import com.example.employee.model.tables.records.EmployeeRecord;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.example.employee.model.tables.Department.DEPARTMENT;
import static com.example.employee.model.tables.Employee.EMPLOYEE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class JooqTest {

    @Autowired
    private DSLContext dslContext;

    @Test
    public void select() {
        Result<EmployeeRecord> records = dslContext
                .selectFrom(EMPLOYEE)
                .fetch();

        assertEquals(1, records.size());
    }

    @Test
    public void projection() {
        Result<Record1<String>> records = dslContext
                .select(EMPLOYEE.NAME)
                .from(EMPLOYEE)
                .join(DEPARTMENT).on(DEPARTMENT.ID.eq(EMPLOYEE.DEPARTMENT_ID))
                .where(DEPARTMENT.NAME.eq("IT"))
                .fetch();

        assertEquals(1, records.size());
    }

    @BeforeEach
    public void insertData() {
        DepartmentRecord department = dslContext
                .selectFrom(DEPARTMENT)
                .where(DEPARTMENT.NAME.eq("IT"))
                .fetchOne();

        if (department == null) {
            dslContext
                    .insertInto(DEPARTMENT)
                    .columns(DEPARTMENT.NAME)
                    .values("IT")
                    .execute();
        }

        EmployeeRecord employee = dslContext
                .selectFrom(EMPLOYEE)
                .where(EMPLOYEE.NAME.eq("Peter Muster"))
                .fetchOne();

        if (employee == null) {
            dslContext
                    .insertInto(EMPLOYEE)
                    .columns(EMPLOYEE.NAME, EMPLOYEE.DEPARTMENT_ID)
                    .values("Peter Muster", 1)
                    .execute();
        }
    }
}
