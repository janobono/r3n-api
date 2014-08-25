package sk.r3n.dto;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sk.r3n.sql.Column;

public class Dto {

    public void objToObj(Object source, Object target) {
        Map<String, Field> sourceFieldMap = new HashMap<String, Field>();
        fillFieldMap(source.getClass(), sourceFieldMap);

        Map<String, Field> targetFieldMap = new HashMap<String, Field>();
        fillFieldMap(target.getClass(), targetFieldMap);

        for (String key : sourceFieldMap.keySet()) {
            Field sourceField = sourceFieldMap.get(key);
            Field targetField = targetFieldMap.get(key);
            if (targetField != null) {
                setValue(target, targetField, getValue(source, sourceField));
            }
        }
    }

    public Object[] toArray(Object object, Column... columns) {
        List<Object> result = new ArrayList<Object>();

        TableId tableId = object.getClass().getAnnotation(TableId.class);

        Map<String, Field> fieldMap = new HashMap<String, Field>();
        fillFieldMap(object.getClass(), tableId, fieldMap);

        for (Column column : columns) {
            result.add(getValue(object, column, fieldMap));
        }

        return result.toArray();
    }

    public void fill(Object object, Object[] values, Column... columns) {
        TableId tableId = object.getClass().getAnnotation(TableId.class);

        Map<String, Field> fieldMap = new HashMap<String, Field>();
        fillFieldMap(object.getClass(), tableId, fieldMap);

        int index = 0;
        for (Column column : columns) {
            Object value = values[index++];
            setValue(object, column, value, fieldMap);
        }

    }

    private void fillFieldMap(Class aClass, Map<String, Field> map) {
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field field : declaredFields) {
            ColumnId columnId = field.getAnnotation(ColumnId.class);
            if (columnId != null) {
                map.put(columnId.name(), field);
            }
        }
        if (aClass.getSuperclass() != null) {
            fillFieldMap(aClass.getSuperclass(), map);
        }
    }

    private void fillFieldMap(Class aClass, TableId tableId, Map<String, Field> map) {
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field field : declaredFields) {
            ColumnId columnId = field.getAnnotation(ColumnId.class);
            if (columnId != null) {
                map.put(tableId.name() + "." + columnId.name(), field);
            }
        }
        if (aClass.getSuperclass() != null) {
            fillFieldMap(aClass.getSuperclass(), tableId, map);
        }
    }

    private void setValue(Object object, Column column, Object value, Map<String, Field> fieldMap) {
        String key = column.getTable().getName() + "." + column.getName();
        Field field = fieldMap.get(key);
        if (field != null) {
            setValue(object, field, value);
        }
    }

    private void setValue(Object object, Field field, Object value) {
        try {
            String methodName = "set" + Character.toString(field.getName().charAt(0)).toUpperCase() + field.getName().substring(1);
            Method method = object.getClass().getMethod(methodName, object.getClass());
            method.invoke(object, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object getValue(Object object, Column column, Map<String, Field> fieldMap) {
        Object result = null;

        String key = column.getTable().getName() + "." + column.getName();
        Field field = fieldMap.get(key);
        if (field != null) {
            result = getValue(object, field);
        }
        return result;
    }

    private Object getValue(Object object, Field field) {
        Object result = null;
        try {
            String methodName = "get" + Character.toString(field.getName().charAt(0)).toUpperCase() + field.getName().substring(1);
            Method method = object.getClass().getMethod(methodName);
            result = method.invoke(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

}
