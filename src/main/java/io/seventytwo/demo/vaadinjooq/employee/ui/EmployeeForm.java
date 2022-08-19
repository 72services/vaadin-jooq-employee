package io.seventytwo.demo.vaadinjooq.employee.ui;

import io.seventytwo.demo.vaadinjooq.employee.model.tables.records.DepartmentRecord;
import io.seventytwo.demo.vaadinjooq.employee.model.tables.records.EmployeeRecord;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.jooq.DSLContext;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static io.seventytwo.demo.vaadinjooq.employee.model.tables.Department.DEPARTMENT;
import static io.seventytwo.demo.vaadinjooq.employee.ui.EmployeeForm.ChangeHandler.Operation.DELETE;
import static io.seventytwo.demo.vaadinjooq.employee.ui.EmployeeForm.ChangeHandler.Operation.INSERT;
import static io.seventytwo.demo.vaadinjooq.employee.ui.EmployeeForm.ChangeHandler.Operation.UPDATE;

@UIScope
@SpringComponent
public class EmployeeForm extends FormLayout {

    private final DSLContext dslContext;
    private final TransactionTemplate transactionTemplate;

    private final List<DepartmentRecord> departments;

    private final TextField firstName;

    private final ComboBox<DepartmentRecord> departmentId = new ComboBox<>("Department");

    private final Binder<EmployeeRecord> binder = new Binder<>(EmployeeRecord.class);
    private final Button save;
    private final Button delete;

    private EmployeeRecord employee;

    private ChangeHandler changeHandler;

    public EmployeeForm(DSLContext dslContext, TransactionTemplate transactionTemplate) {
        this.dslContext = dslContext;

        setResponsiveSteps(new ResponsiveStep("0", 1));

        departments = dslContext.selectFrom(DEPARTMENT).orderBy(DEPARTMENT.NAME).fetch();
        this.transactionTemplate = transactionTemplate;

        departmentId.setLabel("Department");
        departmentId.setItemLabelGenerator(DepartmentRecord::getName);
        departmentId.setRequired(true);

        departmentId.addValueChangeListener(event -> {
            DepartmentRecord department = departmentId.getValue();
            if (department != null) {
                employee.setDepartmentId(department.getId());
            }
        });

        var id = new TextField("Id");
        firstName = new TextField("First Name");
        var lastName = new TextField("Last Name");

        binder.forField(id)
                .withNullRepresentation("")
                .withConverter(new StringToIntegerConverter(0, "Integers only"))
                .bind(EmployeeRecord::getId, null);

        binder.forField(firstName)
                .asRequired()
                .withValidator(s -> s != null && s.length() > 1, "Must be at least 1 character")
                .bind(EmployeeRecord::getFirstName, EmployeeRecord::setFirstName);

        binder.forField(lastName)
                .asRequired()
                .withValidator(s -> s != null && s.length() > 1, "Must be at least 1 character")
                .bind(EmployeeRecord::getLastName, EmployeeRecord::setLastName);

        binder.forField(departmentId)
                .withConverter(new Converter<DepartmentRecord, Integer>() {
                    @Override
                    public Result<Integer> convertToModel(DepartmentRecord departmentRecord, ValueContext valueContext) {
                        if (departmentRecord == null) {
                            return Result.ok(null);
                        } else {
                            return Result.ok(departmentRecord.getId());
                        }
                    }

                    @Override
                    public DepartmentRecord convertToPresentation(Integer integer, ValueContext valueContext) {
                        return departments.stream()
                                .filter(departmentRecord -> departmentRecord.getId().equals(integer))
                                .findFirst()
                                .orElse(new DepartmentRecord(null, ""));
                    }
                })
                .bind(EmployeeRecord::getDepartmentId, EmployeeRecord::setDepartmentId);

        save = new Button("Save");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(this::save);

        delete = new Button("Delete");
        delete.addClickListener(this::delete);

        add(id, firstName, lastName, departmentId, new HorizontalLayout(save, delete));
    }

    public void setEmployee(EmployeeRecord employee) {
        departmentId.setItems(departments);

        this.employee = employee;
        binder.setBean(employee);

        firstName.focus();
    }

    public void setChangeHandler(ChangeHandler changeHandler) {
        this.changeHandler = changeHandler;
    }

    private void save(ClickEvent<Button> event) {
        transactionTemplate.executeWithoutResult(ts -> {
            Integer id = employee.getId();

            dslContext.attach(employee);
            employee.store();

            changeHandler.onChange(id == null ? INSERT : UPDATE);
        });
    }

    private void delete(ClickEvent<Button> event) {
        transactionTemplate.executeWithoutResult(ts -> {
            dslContext.attach(employee);
            employee.delete();

            changeHandler.onChange(DELETE);
        });
    }

    public interface ChangeHandler {
        void onChange(Operation operation);

        enum Operation {
            INSERT, UPDATE, DELETE
        }
    }
}
