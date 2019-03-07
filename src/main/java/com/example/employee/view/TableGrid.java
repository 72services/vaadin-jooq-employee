package com.example.employee.view;

import com.google.common.base.CaseFormat;
import com.vaadin.flow.component.grid.Grid;
import org.jooq.TableField;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TableGrid<T> extends Grid<T> {

    public TableGrid(Class<T> clazz) {
        super(clazz);
    }

    public void setColumns(TableField<?, ?>... fields) {
        List<String> propertyNames = Arrays.stream(fields).map(field -> getPropertyName(field)).collect(Collectors.toList());
        super.setColumns(propertyNames.toArray(new String[propertyNames.size()]));
    }

    private String getPropertyName(TableField<?, ?> field) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, field.getName());
    }

}
