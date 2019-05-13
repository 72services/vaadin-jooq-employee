package com.example.employee.view;

import com.example.employee.model.tables.records.EmployeeRecord;
import com.example.employee.model.tables.records.VEmployeeRecord;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import io.seventytwo.vaadinjooq.ui.RecordGrid;
import org.jooq.DSLContext;

import java.util.Map;

import static com.example.employee.model.tables.Employee.EMPLOYEE;
import static com.example.employee.model.tables.VEmployee.V_EMPLOYEE;

@Route
public class EmployeeView extends VerticalLayout {

    private final EmployeeForm employeeForm;
    private final DSLContext dslContext;

    private RecordGrid<VEmployeeRecord> grid;
    private TextField filterText = new TextField();

    public EmployeeView(EmployeeForm employeeForm, DSLContext dslContext) {
        this.employeeForm = employeeForm;
        this.dslContext = dslContext;

        createUI();
    }

    private void createUI() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setValueChangeMode(ValueChangeMode.EAGER);
        filterText.addValueChangeListener(e ->
                grid.filter(V_EMPLOYEE.EMPLOYEE_NAME.lower().like("%" + e.getValue().toLowerCase() + "%")));

        Button clearFilterTextBtn = new Button(VaadinIcon.CLOSE_CIRCLE.create());
        clearFilterTextBtn.addClickListener(e -> filterText.clear());

        HorizontalLayout filtering = new HorizontalLayout(filterText, clearFilterTextBtn);

        Button addEmployeeButton = new Button("Add new employee");
        addEmployeeButton.addClickListener(e -> {
            grid.asSingleSelect().clear();
            employeeForm.setEmployee(new EmployeeRecord());
            employeeForm.setVisible(true);
        });

        HorizontalLayout toolbar = new HorizontalLayout(filtering, addEmployeeButton);

        RecordGrid.Builder<VEmployeeRecord> recordGridBuilder = new RecordGrid.Builder<>(V_EMPLOYEE, dslContext);
        recordGridBuilder.withColumns(V_EMPLOYEE.EMPLOYEE_ID, V_EMPLOYEE.EMPLOYEE_NAME, V_EMPLOYEE.DEPARTMENT_NAME);
        recordGridBuilder.withSort(Map.of(V_EMPLOYEE.EMPLOYEE_NAME, true));

        grid = recordGridBuilder.build();

        this.grid.setSizeFull();

        this.grid.asSingleSelect().addValueChangeListener(event -> {
            EmployeeRecord employee = dslContext
                    .selectFrom(EMPLOYEE)
                    .where(EMPLOYEE.ID.eq(event.getValue().getEmployeeId()))
                    .fetchOne();
            employeeForm.setEmployee(employee);
            employeeForm.setVisible(true);
        });

        employeeForm.setChangeHandler(() -> {
            employeeForm.setVisible(false);
            grid.refresh();
        });

        HorizontalLayout main = new HorizontalLayout(this.grid, employeeForm);
        main.setSizeFull();

        add(toolbar, main);

        setSizeFull();
    }

}
