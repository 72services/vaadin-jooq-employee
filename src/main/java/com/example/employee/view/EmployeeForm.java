package com.example.employee.view;

import com.example.employee.model.tables.records.EmployeeRecord;
import com.example.employee.service.DepartmentService;
import com.example.employee.service.EmployeeService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import java.util.stream.Collectors;

@UIScope
@SpringComponent
public class EmployeeForm extends FormLayout {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    private TextField id = new TextField("Id");
    private TextField name = new TextField("Name");
    private ComboBox<Integer> departmentId = new ComboBox<>("Integer");

    private Binder<EmployeeRecord> binder = new Binder<>(EmployeeRecord.class);
    private Button save = new Button("Save");
    private Button delete = new Button("Delete");

    private EmployeeRecord employee;

    private ChangeHandler changeHandler;

    public EmployeeForm(EmployeeService employeeService, DepartmentService departmentService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;

        createUI();
    }

    private void createUI() {
        HorizontalLayout buttons = new HorizontalLayout(save, delete);

        add(id, name, departmentId, buttons);

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        binder.forField(id)
                .withNullRepresentation("")
                .withConverter(new StringToIntegerConverter(0, "integers only"))
                .bind(EmployeeRecord::getId, EmployeeRecord::setId);

        binder.bindInstanceFields(this);

        setVisible(false);
    }

    public void setEmployee(EmployeeRecord employee) {
        departmentId.setItems(
                departmentService.findAll().stream()
                        .map(departmentRecord -> departmentRecord.getId())
                        .collect(Collectors.toList()));

        this.employee = employee;
        binder.setBean(employee);

        name.focus();

        save.addClickListener(e -> this.save());
        delete.addClickListener(e -> this.delete());
    }

    public void setChangeHandler(ChangeHandler changeHandler) {
        this.changeHandler = changeHandler;
    }

    private void delete() {
        employeeService.delete(employee);

        changeHandler.onChange();
    }

    private void save() {
        employeeService.save(employee);

        changeHandler.onChange();
    }

    public interface ChangeHandler {
        void onChange();
    }
}
