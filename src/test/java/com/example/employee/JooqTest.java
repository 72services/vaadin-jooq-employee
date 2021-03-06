package com.example.employee;

import com.example.employee.model.tables.records.DepartmentRecord;
import com.example.employee.model.tables.records.EmployeeRecord;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Result;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.example.employee.model.tables.Department.DEPARTMENT;
import static com.example.employee.model.tables.Employee.EMPLOYEE;

@SpringBootTest
@RunWith(SpringRunner.class)
public class JooqTest {

    @Autowired
    private DSLContext dslContext;

    @Test
    public void select() {
        Result<EmployeeRecord> records = dslContext
                .selectFrom(EMPLOYEE)
                .fetch();

        Assert.assertEquals(1, records.size());
    }

    @Test
    public void projection() {
        Result<Record1<String>> records = dslContext
                .select(EMPLOYEE.NAME)
                .from(EMPLOYEE)
                .join(DEPARTMENT).on(DEPARTMENT.ID.eq(EMPLOYEE.DEPARTMENT_ID))
                .where(DEPARTMENT.NAME.eq("IT"))
                .fetch();

        Assert.assertEquals(1, records.size());
    }

    @Before
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
