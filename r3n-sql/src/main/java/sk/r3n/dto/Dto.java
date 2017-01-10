package sk.r3n.dto;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import sk.r3n.sql.Column;

public class Dto {

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
    }

    public Object[] toArray(Object object, Column... columns) {
        List<Object> result = new ArrayList<>();

        List<Field> fieldList = new ArrayList<>();
        fillFieldList(object.getClass(), fieldList);

        for (Column column : columns) {
            result.add(getValue(object, column, fieldList));
        }

        return result.toArray();
    }

    public void fill(Object object, Object[] values, Column... columns) {
        List<Field> fieldList = new ArrayList<>();
        fillFieldList(object.getClass(), fieldList);

        int index = 0;
        for (Column column : columns) {
            Object value = values[index++];
            setValue(object, column, value, fieldList);
        }

    }

    private void setValue(Object object, Column column, Object value, List<Field> fieldList) {
        Field field = getField(column.getTable().getName(), column.getName(), fieldList);
        if (field != null) {
            setValue(object, field, value);
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
    }

    private Object getValue(Object object, Column column, List<Field> fieldList) {
        Object result = null;
        Field field = getField(column.getTable().getName(), column.getName(), fieldList);
        if (field != null) {
            result = getValue(object, field);
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
        return result;
    }

    private void fillFieldList(Class aClass, List<Field> fieldList) {
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field field : declaredFields) {
            ColumnId columnId = field.getAnnotation(ColumnId.class);
            if (columnId != null) {
                fieldList.add(field);
            }
        }
        if (aClass.getSuperclass() != null) {
            fillFieldList(aClass.getSuperclass(), fieldList);
        }
    }

    private Field getField(String table, String column, List<Field> fieldList) {
        for (Field field : fieldList) {
            ColumnId columnId = field.getAnnotation(ColumnId.class);
            if (columnId.table().equals(table) && columnId.column().equals(column)) {
                return field;
            }
        }
        return null;
    }

}
