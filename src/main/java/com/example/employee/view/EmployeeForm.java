package com.example.employee.view;

import com.example.employee.model.tables.records.DepartmentRecord;
import com.example.employee.model.tables.records.EmployeeRecord;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.jooq.DSLContext;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static com.example.employee.model.tables.Department.DEPARTMENT;

@UIScope
@SpringComponent
public class EmployeeForm extends FormLayout {

    private final DSLContext dslContext;
    private final TransactionTemplate transactionTemplate;

    private final List<DepartmentRecord> departments;

    private TextField id = new TextField("Id");
    private TextField name = new TextField("Name");
    private TextField age = new TextField("Age");

    private ComboBox<DepartmentRecord> departmentId = new ComboBox<>("Department");

    private Binder<EmployeeRecord> binder = new Binder<>(EmployeeRecord.class);
    private Button save = new Button("Save");
    private Button delete = new Button("Delete");

    private EmployeeRecord employee;

    private ChangeHandler changeHandler;

    public EmployeeForm(DSLContext dslContext, TransactionTemplate transactionTemplate) {
        this.dslContext = dslContext;

        departments = dslContext.selectFrom(DEPARTMENT).orderBy(DEPARTMENT.NAME).fetch();
        this.transactionTemplate = transactionTemplate;

        createUI();
    }

    private void createUI() {
        name.setRequired(true);

        departmentId.setLabel("Departments");
        departmentId.setItemLabelGenerator(DepartmentRecord::getName);
        departmentId.setRequired(true);

        departmentId.addValueChangeListener(event -> {
            DepartmentRecord department = departmentId.getValue();
            if (department != null) {
                employee.setDepartmentId(department.getId());
            }
        });

        HorizontalLayout buttons = new HorizontalLayout(save, delete);

        add(id, name, age, departmentId, buttons);

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        binder.forField(id)
                .withNullRepresentation("")
                .withConverter(new StringToIntegerConverter(0, "Integers only"))
                .withValidator((value, context) -> value != null && value > 0 ? ValidationResult.ok() : ValidationResult.error("Value must be greater than 0"))
                .bind(EmployeeRecord::getId, null);

        binder.forField(age)
                .withNullRepresentation("")
                .withConverter(new StringToIntegerConverter(0, "Integers only"))
                .withValidator((value, context) -> value != null && value > 0 ? ValidationResult.ok() : ValidationResult.error("Value must be greater than 0"))
                .bind(EmployeeRecord::getAge, EmployeeRecord::setAge);

        binder.forField(departmentId)
                .withConverter(new Converter<DepartmentRecord, Integer>() {
                    @Override
                    public Result<Integer> convertToModel(DepartmentRecord departmentRecord, ValueContext valueContext) {
                        if (departmentRecord == null) {
                            return Result.ok(0);
                        } else {
                            return Result.ok(departmentRecord.getId());
                        }
                    }

                    @Override
                    public DepartmentRecord convertToPresentation(Integer integer, ValueContext valueContext) {
                        if (integer == null) {
                            return new DepartmentRecord(null, "");
                        } else {
                            return departments.stream().filter(departmentRecord -> departmentRecord.getId().equals(integer)).findFirst().get();
                        }
                    }
                })
                .bind(EmployeeRecord::getDepartmentId, EmployeeRecord::setDepartmentId);

        binder.bindInstanceFields(this);

        setVisible(false);
    }

    public void setEmployee(EmployeeRecord employee) {
        departmentId.setItems(departments);

        this.employee = employee;
        binder.setBean(employee);

        name.focus();

        save.addClickListener(this::save);
        delete.addClickListener(this::delete);
    }

    public void setChangeHandler(ChangeHandler changeHandler) {
        this.changeHandler = changeHandler;
    }

    private void save(ClickEvent<Button> event) {
        transactionTemplate.execute(transactionStatus -> {
            dslContext.attach(employee);
            employee.store();

            changeHandler.onChange();
            return null;
        });
    }

    private void delete(ClickEvent<Button> event) {
        transactionTemplate.execute(transactionStatus -> {
            dslContext.attach(employee);
            employee.delete();

            changeHandler.onChange();
            return null;
        });
    }


    public interface ChangeHandler {
        void onChange();
    }
}
