package com.example.healthconnect.controllers;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DbTable extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "HealthConnect.db";
    private final Class<?> entityType;
    private final String tableName;

    // Singleton instance
    private static DbTable instance;

    public DbTable(Context context, Class<?> entityType) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.entityType = entityType;
        this.tableName = entityType.getSimpleName();
    }

    public static synchronized DbTable getInstance(Context context, Class<?> entityType) {
        if (instance == null) {
            instance = new DbTable(context.getApplicationContext(), entityType);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        onCreate(db);
    }

    private void createTable(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + " (");
        sql.append("id INTEGER PRIMARY KEY AUTOINCREMENT, ");
        Field[] fields = entityType.getFields();
        for (Field field : fields) {
            sql.append(field.getName()).append(" ").append(getSQLiteType(field)).append(", ");
        }
        sql.setLength(sql.length() - 2);
        sql.append(")");
        db.execSQL(sql.toString());
    }

    private String getSQLiteType(Field field) {
        Class<?> type = field.getType();
        if (type == String.class) return "TEXT";
        else if (type == int.class || type == Integer.class) return "INTEGER";
        else if (type == long.class || type == Long.class) return "INTEGER";
        else if (type == double.class || type == Double.class) return "REAL";
        else throw new IllegalArgumentException("Unsupported field type: " + type.getSimpleName());
    }

    public long add(Object entity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        for (Field field : entityType.getFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(entity);
                if (value != null) {
                    if (field.getType() == String.class) values.put(field.getName(), (String) value);
                    else if (field.getType() == int.class || field.getType() == Integer.class) values.put(field.getName(), (Integer) value);
                    else if (field.getType() == long.class || field.getType() == Long.class) values.put(field.getName(), (Long) value);
                    else if (field.getType() == double.class || field.getType() == Double.class) values.put(field.getName(), (Double) value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error accessing field", e);
            }
        }
        return db.insert(tableName, null, values);
    }

    public int update(long id, Object entity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        for (Field field : entityType.getFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(entity);
                if (value != null) {
                    if (field.getType() == String.class) values.put(field.getName(), (String) value);
                    else if (field.getType() == int.class || field.getType() == Integer.class) values.put(field.getName(), (Integer) value);
                    else if (field.getType() == long.class || field.getType() == Long.class) values.put(field.getName(), (Long) value);
                    else if (field.getType() == double.class || field.getType() == Double.class) values.put(field.getName(), (Double) value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error accessing field", e);
            }
        }
        return db.update(tableName, values, "id = ?", new String[]{String.valueOf(id)});
    }

    @SuppressWarnings("unchecked") // Suppress unchecked cast warning
    public <T> List<T> getAll() {
        List<T> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try (Cursor cursor = db.query(tableName, null, null, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    T entity = (T) entityType.getDeclaredConstructor().newInstance();
                    setEntityFields(cursor, entity);
                    list.add(entity);
                } while (cursor.moveToNext());
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Error retrieving data", e);
        }
        return list;
    }
    public int size() {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;

        // Use a cursor to count rows in the table
        try (Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName, null)) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0); // Get the count from the first column
            }
        }
        return count;
    }

    private void setEntityFields(Cursor cursor, Object entity) throws IllegalAccessException {
        for (Field field : entityType.getFields()) {
            field.setAccessible(true);
            int columnIndex = cursor.getColumnIndex(field.getName());
            if (columnIndex != -1) {
                if (field.getType() == String.class) field.set(entity, cursor.getString(columnIndex));
                else if (field.getType() == int.class) field.set(entity, cursor.getInt(columnIndex));
                else if (field.getType() == long.class) field.set(entity, cursor.getLong(columnIndex));
                else if (field.getType() == double.class) field.set(entity, cursor.getDouble(columnIndex));
            }
        }
    }

    @SuppressWarnings("unchecked") // Suppress unchecked cast warning
    private <T> List<T> getByField(String fieldName, Object value) {
        List<T> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try (Cursor cursor = db.query(tableName, null, fieldName + " = ?", new String[]{String.valueOf(value)}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    T entity = (T) entityType.getDeclaredConstructor().newInstance();
                    setEntityFields(cursor, entity);
                    list.add(entity);
                } while (cursor.moveToNext());
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Error retrieving data by " + fieldName, e);
        }
        return list;
    }

    private String getFieldNameFromGetter(Function<Object, ?> fieldGetter) {
        try {
            Method method = fieldGetter.getClass().getDeclaredMethods()[0];
            String getterName = method.getName();
            if (getterName.startsWith("get") && getterName.length() > 3) {
                return Character.toLowerCase(getterName.charAt(3)) + getterName.substring(4);
            } else {
                throw new IllegalArgumentException("Invalid getter method reference");
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not extract field name from getter", e);
        }
    }

    public <T> T getById(long id) {
        List<T> results = getByField("id", id);
        return results.isEmpty() ? null : results.get(0);
    }
}
