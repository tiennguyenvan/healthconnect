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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbTable<T> extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 12;
    private static final String DATABASE_NAME = "HealthConnect.db";
    private final Class<T> entityType;
    private final String tableName;

    // Map to hold singleton instances per entity type
    private static Map<Class<?>, DbTable<?>> instances = new HashMap<>();

    private DbTable(Context context, Class<T> entityType) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.entityType = entityType;
        this.tableName = entityType.getSimpleName();
    }

    public static synchronized <T> DbTable<T> getInstance(Context context, Class<T> entityType) {
        @SuppressWarnings("unchecked")
        DbTable<T> instance = (DbTable<T>) instances.get(entityType);
        if (instance == null) {
            instance = new DbTable<>(context.getApplicationContext(), entityType);
            instances.put(entityType, instance);
        }
        return instance;
    }

        @Override
    public void onCreate(SQLiteDatabase db) {
//        createTable(db);
//        insertDemoData(db);
    }
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        // Check if the table exists
        if (!isTableExists(db, tableName)) {
            // Create the table if it doesn't exist
            createTable(db);
            // Insert demo data if the table was just created
            insertDemoData(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        onCreate(db);
    }

    public void closeDatabase() {
        DbTable<?> instance = instances.get(entityType);
        if (instance != null && instance.getWritableDatabase().isOpen()) {
            instance.getWritableDatabase().close();
            instances.remove(entityType);
        }
    }

    private boolean isTableExists(SQLiteDatabase db, String tableName) {
        try (Cursor cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                new String[]{tableName})) {
            return cursor.moveToFirst();
        }
    }


    private void createTable(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + " (");
        sql.append("id INTEGER PRIMARY KEY AUTOINCREMENT, ");
        Field[] fields = entityType.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getName().equals("id")) {
                continue; // Skip the 'id' field
            }
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

    public long add(T entity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = getContentValues(entity);
//        System.out.println("ContentValues: " + values.toString());
        return db.insert(tableName, null, values);
    }

    public int update(long id, T entity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = getContentValues(entity);
        return db.update(tableName, values, "id = ?", new String[]{String.valueOf(id)});
    }

    public List<T> getAll() {
        List<T> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try (Cursor cursor = db.query(tableName, null, null, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    T entity = entityType.getDeclaredConstructor().newInstance();
                    setEntityFields(cursor, entity);
                    list.add(entity);
                } while (cursor.moveToNext());
            }
        } catch (InstantiationException | IllegalAccessException |
                 InvocationTargetException | NoSuchMethodException e) {
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

    private void setEntityFields(Cursor cursor, T entity) throws IllegalAccessException {
        for (Field field : entityType.getDeclaredFields()) {
            field.setAccessible(true);
            int columnIndex = cursor.getColumnIndex(field.getName());
            if (columnIndex != -1) {
                if (field.getType() == String.class)
                    field.set(entity, cursor.getString(columnIndex));
                else if (field.getType() == int.class || field.getType() == Integer.class)
                    field.set(entity, cursor.getInt(columnIndex));
                else if (field.getType() == long.class || field.getType() == Long.class)
                    field.set(entity, cursor.getLong(columnIndex));
                else if (field.getType() == double.class || field.getType() == Double.class)
                    field.set(entity, cursor.getDouble(columnIndex));
            }
        }
    }

    public List<T> getBy(String fieldName, Object value) {
        List<T> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try (Cursor cursor = db.query(
                tableName,
                null,
                fieldName + " = ?",
                new String[]{String.valueOf(value)},
                null,
                null,
                null)) {

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    T entity = entityType.getDeclaredConstructor().newInstance();
                    setEntityFields(cursor, entity);
                    list.add(entity);
                } while (cursor.moveToNext());
            }
        } catch (InstantiationException | IllegalAccessException |
                 InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Error retrieving data by " + fieldName, e);
        }
        return list;
    }

    public T getById(Object value) {
        List<T> results = getBy("id", value);
        return results.isEmpty() ? null : results.get(0);
    }

    public List<T> searchBy(String fieldName, String value) {
        List<T> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Prepare the value for the LIKE clause by adding '%' wildcards
        String queryValue = "%" + value + "%";

        try (Cursor cursor = db.query(
                tableName,
                null,
                fieldName + " LIKE ?",
                new String[]{queryValue},
                null,
                null,
                null)) {

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    T entity = entityType.getDeclaredConstructor().newInstance();
                    setEntityFields(cursor, entity);
                    list.add(entity);
                } while (cursor.moveToNext());
            }
        } catch (InstantiationException | IllegalAccessException |
                 InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Error searching data by " + fieldName, e);
        }
        return list;
    }

    ////////////////
    ////////////////
    // for demo data
    private ContentValues getContentValues(T entity) {
        ContentValues values = new ContentValues();
        for (Field field : entityType.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (field.getName().equals("id")) {
                    continue; // Skip the 'id' field
                }
                Object value = field.get(entity);
//                System.out.println("Field: " + field.getName() + ", Value: " + value);

                if (value != null) {
                    if (field.getType() == String.class)
                        values.put(field.getName(), (String) value);
                    else if (field.getType() == int.class || field.getType() == Integer.class)
                        values.put(field.getName(), (Integer) value);
                    else if (field.getType() == long.class || field.getType() == Long.class)
                        values.put(field.getName(), (Long) value);
                    else if (field.getType() == double.class || field.getType() == Double.class)
                        values.put(field.getName(), (Double) value);
                    // Handle other types as needed
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error accessing field", e);
            }
        }
        return values;
    }

    private void insertDemoData(SQLiteDatabase db) {
        try {
            // Check if the entity class has a static 'demoData' method
            Method demoDataMethod = entityType.getMethod("demoData");
            // Invoke the 'demoData' method
            @SuppressWarnings("unchecked")
            List<T> demoDataList = (List<T>) demoDataMethod.invoke(null);
            if (demoDataList == null) {
                return;
            }
            for (T entity : demoDataList) {
                ContentValues values = getContentValues(entity);
//                System.out.println("ContentValues Demo: " + values.toString());
                db.insert(tableName, null, values);
            }
        } catch (NoSuchMethodException e) {
            // The 'demoData' method doesn't exist; do nothing
        } catch (Exception e) {
            throw new RuntimeException("Error inserting demo data", e);
        }
    }
}
