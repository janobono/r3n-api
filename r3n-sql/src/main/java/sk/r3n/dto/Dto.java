/* 
 * Copyright 2016 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.dto;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sk.r3n.sql.Column;

/**
 * A utility to help mapping object to object based on annotations.
 */
public class Dto {

    private static final Logger LOGGER = Logger.getLogger(Dto.class.getCanonicalName());

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
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "objToObj({0},{1})", new Object[]{source, target});
        }
    }

    /**
     * Transforms object memebers to array of objects. Only annotated class members are used and getters are needed.
     *
     * @param object Annotated object instance.
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

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "toArray({0},{1}) = {2}", new Object[]{object, Arrays.toString(columns), Arrays.toString(result.toArray())});
        }
        return result.toArray();
    }

    /**
     * Sets members of object with array of objects. Only annotated class members are used and setters are needed.
     *
     * @param object Annotated object instance.
     * @param values Array of values.
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
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "fill({0},{1},{2})", new Object[]{object, Arrays.toString(values), Arrays.toString(columns)});
        }
    }

    private void setValue(Object object, Column column, Object value, List<Field> fieldList) {
        Field field = getField(column.getTable().getName(), column.getName(), fieldList);
        if (field != null) {
            setValue(object, field, value);
        }
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "setValue({0},{1},{2},{3})", new Object[]{object, column, value, fieldList});
        }
    }

    private void setValue(Object object, Field field, Object value) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("set").append(Character.toString(field.getName().charAt(0)).toUpperCase()).append(field.getName().substring(1));
            Method method = object.getClass().getMethod(sb.toString(), field.getType());
            method.invoke(object, value);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "setValue({0},{1},{2})", new Object[]{object, field, value});
        }
    }

    private Object getValue(Object object, Column column, List<Field> fieldList) {
        Object result = null;
        Field field = getField(column.getTable().getName(), column.getName(), fieldList);
        if (field != null) {
            result = getValue(object, field);
        }
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "getValue({0},{1},{2}) = {3}", new Object[]{object, field, fieldList, result});
        }
        return result;
    }

    private Object getValue(Object object, Field field) {
        Object result = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("get").append(Character.toString(field.getName().charAt(0)).toUpperCase()).append(field.getName().substring(1));
            Method method = object.getClass().getMethod(sb.toString());
            result = method.invoke(object);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "getValue({0},{1}) = {2}", new Object[]{object, field, result});
        }
        return result;
    }

    private void fillFieldList(Class aClass, List<Field> fieldList) {
        Field[] declaredFields = aClass.getDeclaredFields();
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "declaredFields: {0}", Arrays.toString(declaredFields));
        }
        for (Field field : declaredFields) {
            ColumnId columnId = field.getAnnotation(ColumnId.class);
            if (columnId != null) {
                fieldList.add(field);
            }
        }
        if (aClass.getSuperclass() != null) {
            fillFieldList(aClass.getSuperclass(), fieldList);
        }
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "fillFieldList({0},{1})", new Object[]{aClass, fieldList});
        }
    }

    private Field getField(String table, String column, List<Field> fieldList) {
        for (Field field : fieldList) {
            ColumnId columnId = field.getAnnotation(ColumnId.class);
            if (columnId.table().equals(table) && columnId.column().equals(column)) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "getField({0},{1},{2}) = {3}", new Object[]{table, column, fieldList, field});
                }
                return field;
            }
        }
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "getField({0},{1},{2}) = {3}", new Object[]{table, column, fieldList, null});
        }
        return null;
    }

}
