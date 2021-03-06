/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.r3n.sql.Column;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility to help mapping object to object, object to array and array to object based on annotations. DTO is shortcut
 * for Data Transfer Object in r3n represented with simpl POJO objects with {@link ColumnId} annotations.
 *
 * @author janobono
 * @since 22 August 2014
 */
public class Dto {

    private static final Logger LOGGER = LoggerFactory.getLogger(Dto.class);

    /**
     * Maps values from source to target object. Only annotated class members are used and getters and setters are
     * needed.
     *
     * @param source Source object.
     * @param target Target object.
     */
    public void objToObj(Object source, Object target) {
        List<Field> sourceFieldList = new ArrayList<>();
        fillFieldList(source.getClass(), sourceFieldList);

        List<Field> targetFieldList = new ArrayList<>();
        fillFieldList(target.getClass(), targetFieldList);

        targetFieldList.forEach((targetField) -> {
            ColumnId targetColumnId = targetField.getAnnotation(ColumnId.class);
            Field sourceField = getField(targetColumnId.table(), targetColumnId.column(), sourceFieldList);
            if (sourceField != null) {
                setValue(target, targetField, getValue(source, sourceField));
            }
        });
        LOGGER.debug("objToObj({},{})", source, target);
    }

    /**
     * Transforms object members to array of objects. Only annotated class members are used and getters are needed.
     *
     * @param object  Annotated object instance.
     * @param columns Columns witch will be searched by <code>sk.r3n.dto.ColumnId</code> annotation.
     * @return Array of values.
     */
    public Object[] toArray(Object object, Column... columns) {
        List<Object> result = new ArrayList<>();

        List<Field> fieldList = new ArrayList<>();
        fillFieldList(object.getClass(), fieldList);

        for (Column column : columns) {
            result.add(getValue(object, column, fieldList));
        }

        LOGGER.debug("toArray({},{}) = {}", object, columns, result);
        return result.toArray();
    }

    /**
     * Sets members of object with array of objects. Only annotated class members are used and setters are needed.
     *
     * @param object  Annotated object instance.
     * @param values  Array of values.
     * @param columns Columns witch will be searched by <code>sk.r3n.dto.ColumnId</code> annotation.
     */
    public void fill(Object object, Object[] values, Column... columns) {
        List<Field> fieldList = new ArrayList<>();
        fillFieldList(object.getClass(), fieldList);

        int index = 0;
        for (Column column : columns) {
            Object value = values[index++];
            setValue(object, column, value, fieldList);
        }
        LOGGER.debug("fill({},{},{})", object, values, columns);
    }

    private void setValue(Object object, Column column, Object value, List<Field> fieldList) {
        Field field = getField(column.getTable().getName(), column.getName(), fieldList);
        if (field != null) {
            setValue(object, field, value);
        }
        LOGGER.debug("setValue({},{},{},{})", object, column, value, fieldList);
    }

    private void setValue(Object object, Field field, Object value) {
        try {
            Method method = object.getClass().getMethod(
                    "set" + Character.toString(field.getName().charAt(0)).toUpperCase() + field.getName().substring(1), field.getType()
            );
            method.invoke(object, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        LOGGER.debug("setValue({},{},{})", object, field, value);
    }

    private Object getValue(Object object, Column column, List<Field> fieldList) {
        Object result = null;
        Field field = getField(column.getTable().getName(), column.getName(), fieldList);
        if (field != null) {
            result = getValue(object, field);
        }
        LOGGER.debug("getValue({},{},{}) = {}", object, field, fieldList, result);
        return result;
    }

    private Object getValue(Object object, Field field) {
        Object result;
        try {
            Method method = object.getClass().getMethod(
                    "get" + Character.toString(field.getName().charAt(0)).toUpperCase() + field.getName().substring(1)
            );
            result = method.invoke(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        LOGGER.debug("getValue({},{}) = {}", object, field, result);
        return result;
    }

    private void fillFieldList(Class aClass, List<Field> fieldList) {
        Field[] declaredFields = aClass.getDeclaredFields();
        LOGGER.debug("declaredFields: {}", declaredFields);
        for (Field field : declaredFields) {
            ColumnId columnId = field.getAnnotation(ColumnId.class);
            if (columnId != null) {
                fieldList.add(field);
            }
        }
        if (aClass.getSuperclass() != null) {
            fillFieldList(aClass.getSuperclass(), fieldList);
        }
        LOGGER.debug("fillFieldList({},{})", aClass, fieldList);
    }

    private Field getField(String table, String column, List<Field> fieldList) {
        for (Field field : fieldList) {
            ColumnId columnId = field.getAnnotation(ColumnId.class);
            if (columnId.table().equals(table) && columnId.column().equals(column)) {
                LOGGER.debug("getField({},{},{}) = {}", table, column, fieldList, field);
                return field;
            }
        }
        LOGGER.debug("getField({},{},{}) = {}", table, column, fieldList, null);
        return null;
    }
}
