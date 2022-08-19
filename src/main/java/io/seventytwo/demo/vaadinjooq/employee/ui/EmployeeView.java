package io.seventytwo.demo.vaadinjooq.employee.ui;

import io.seventytwo.demo.vaadinjooq.employee.model.tables.records.EmployeeRecord;
import io.seventytwo.demo.vaadinjooq.employee.model.tables.records.VEmployeeRecord;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import io.seventytwo.vaadinjooq.ui.RecordGrid;
import org.jooq.DSLContext;

import java.util.Map;

import static io.seventytwo.demo.vaadinjooq.employee.model.tables.Employee.EMPLOYEE;
import static io.seventytwo.demo.vaadinjooq.employee.model.tables.VEmployee.V_EMPLOYEE;
import static com.vaadin.flow.data.provider.SortDirection.ASCENDING;
import static org.jooq.impl.DSL.lower;

@PageTitle("Employees")
@Route
@RouteAlias("")
public class EmployeeView extends VerticalLayout {

    private RecordGrid<VEmployeeRecord> grid;
    private final TextField filter = new TextField();

    public EmployeeView(EmployeeForm employeeForm, DSLContext dslContext) {
        filter.setPlaceholder("Filter by name...");
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e ->
                grid.filter(
                        lower(V_EMPLOYEE.EMPLOYEE_FIRST_NAME).like("%" + e.getValue().toLowerCase() + "%")
                                .or(lower(V_EMPLOYEE.EMPLOYEE_LAST_NAME).like("%" + e.getValue().toLowerCase() + "%"))));

        var clear = new Button(VaadinIcon.CLOSE_CIRCLE.create());
        clear.addClickListener(e -> filter.clear());

        var add = new Button("Add new employee");
        add.addClickListener(e -> {
            grid.asSingleSelect().clear();
            employeeForm.setEmployee(new EmployeeRecord());
        });

        var toolbar = new HorizontalLayout(filter, clear, add);

        grid = new RecordGrid.Builder<>(V_EMPLOYEE, dslContext)
                .withColumns(V_EMPLOYEE.EMPLOYEE_ID, V_EMPLOYEE.EMPLOYEE_FIRST_NAME, V_EMPLOYEE.EMPLOYEE_LAST_NAME, V_EMPLOYEE.DEPARTMENT_NAME)
                .withHeaders("ID", "First Name", "Last Name", "Department Name")
                .withIdColumns(V_EMPLOYEE.EMPLOYEE_ID)
                .withSort(Map.of(V_EMPLOYEE.EMPLOYEE_FIRST_NAME, ASCENDING, V_EMPLOYEE.EMPLOYEE_LAST_NAME, ASCENDING))
                .build();

        grid.setSizeFull();

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                EmployeeRecord employee = dslContext
                        .selectFrom(EMPLOYEE)
                        .where(EMPLOYEE.ID.eq(event.getValue().getEmployeeId()))
                        .fetchOne();
                employeeForm.setEmployee(employee);
            }
        });

        employeeForm.setChangeHandler(operation -> {
            switch (operation) {
                case INSERT, DELETE -> grid.refreshAll();
                case UPDATE -> {
                    VEmployeeRecord selectedRecord = grid.asSingleSelect().getValue();
                    VEmployeeRecord updatedRecord = dslContext
                            .selectFrom(V_EMPLOYEE)
                            .where(V_EMPLOYEE.EMPLOYEE_ID.eq(selectedRecord.getEmployeeId()))
                            .fetchOne();
                    grid.refreshItem(updatedRecord);
                }
            }
        });

        var main = new HorizontalLayout(grid, employeeForm);
        main.setSizeFull();

        add(toolbar, main);

        setSizeFull();
    }

}
