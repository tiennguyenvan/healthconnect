package com.example.healthconnect.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DbTable<T> extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 15;
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

    }
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!isTableExists(db) || !isTableSchemaMatching(db)) {
            Log.d( "MMMM" , "DROP TABLE " + tableName);
            dropTable(db);
            createTable(db);
            insertDemoData(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void closeDatabase() {
        DbTable<?> instance = instances.get(entityType);
        if (instance != null && instance.getWritableDatabase().isOpen()) {
            instance.getWritableDatabase().close();
            instances.remove(entityType);
        }
    }

    private boolean isTableExists(SQLiteDatabase db) {
        try (Cursor cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                new String[]{tableName})) {
            return cursor.moveToFirst();
        }
    }

    private boolean isTableSchemaMatching(SQLiteDatabase db) {
        List<String> dbColumns = getDatabaseColumns(db);
        List<String> entityColumns = getEntityColumns();
        return dbColumns.containsAll(entityColumns) && entityColumns.containsAll(dbColumns);
    }


    // Get columns from the existing table in the database
    private List<String> getDatabaseColumns(SQLiteDatabase db) {
        List<String> columns = new ArrayList<>();
        try (Cursor cursor = db.rawQuery("PRAGMA table_info(" + tableName + ")", null)) {
            while (cursor.moveToNext()) {
                int nameIndex = cursor.getColumnIndex("name");
                int typeIndex = cursor.getColumnIndex("type");

                if (nameIndex != -1 && typeIndex != -1) {
                    String columnName = cursor.getString(nameIndex);
                    String columnType = cursor.getString(typeIndex);
                    columns.add(columnName + " " + columnType);
                }
            }
        }
        Log.d("MMMMMMMMMMM",  String.join(",", columns));

        return columns;
    }

    // Get columns from the entity class
    private List<String> getEntityColumns() {
        List<String> columns = new ArrayList<>();
        Field[] fields = entityType.getDeclaredFields();
        for (Field field : fields) {
            columns.add(field.getName() + " " + getSQLiteType(field));
        }
        Log.d("NNNNNNNNNNNN",  String.join(",", columns));
        return columns;
    }

    private void dropTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
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

    ////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////
    // GET data
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
    public List<T> searchByColumnInValues(String columnName, List<?> values) {
        if (values == null || values.isEmpty()) return Collections.emptyList();

        SQLiteDatabase db = this.getReadableDatabase();
        List<T> list = new ArrayList<>();

        // Create a query with placeholders for the `IN` clause
        String placeholders = String.join(",", Collections.nCopies(values.size(), "?"));
        String query = columnName + " IN (" + placeholders + ")";
        String[] args = values.stream().map(String::valueOf).toArray(String[]::new);

        try (Cursor cursor = db.query(
                tableName,
                null,
                query,
                args,
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
            throw new RuntimeException("Error searching data by " + columnName + " in values", e);
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

    ////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////
    // mapping converters
    public List<T> idsStringToObjects(String ids) {
        return idsStringToSortedObjects(ids, null);
    }

    public List<T> idsStringToSortedObjects(String ids, Comparator<T> sortCallback) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();

        // Split and parse IDs
        String[] idArray = ids.split(",");
        List<T> list = new ArrayList<>();
        for (String idStr : idArray) {
            try {
                long id = Long.parseLong(idStr.trim());
                T entity = getById(id); // Load each entity
                if (entity != null) {
                    list.add(entity);
                }
            } catch (NumberFormatException e) {
                Log.e("DbTable", "Invalid ID format: " + idStr, e);
            }
        }

        // Sort if a sortCallback is provided
        if (sortCallback != null) {
            list.sort(sortCallback);
        }
        return list;
    }

    public <R> List<R> idsStringToObjectFields(String ids, Function<T, R> returnCallback) {
        return idsStringToSortedObjectFields(ids, returnCallback, null); // Call the overloaded method with no sorting
    }

    public <R> List<R> idsStringToSortedObjectFields(String ids, Function<T, R> returnCallback, Comparator<R> sortCallback) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();

        // Split and parse IDs
        String[] idArray = ids.split(",");
        List<R> resultList = new ArrayList<>();
        for (String idStr : idArray) {
            try {
                long id = Long.parseLong(idStr.trim());
                T entity = getById(id); // Load each entity
                if (entity != null && returnCallback != null) {
                    // Apply the returnCallback to extract the desired field
                    R result = returnCallback.apply(entity);
                    resultList.add(result);
                }
            } catch (NumberFormatException e) {
                Log.e("DbTable", "Invalid ID format: " + idStr, e);
            }
        }

        // Sort if a sortCallback is provided
        if (sortCallback != null) {
            resultList.sort(sortCallback);
        }
        return resultList;
    }

    // Method to convert a list of objects to a comma-separated string of their IDs
    public String objectsToIdsString(List<T> objects) {
        if (objects == null || objects.isEmpty()) return "";

        return objects.stream()
                .map(this::objectToId)
                .filter(id -> id != null)  // Filter out nulls if any ID is not retrievable
                .map(String::valueOf)      // Convert each ID to a string
                .collect(Collectors.joining(","));
    }

    // Helper method to retrieve the ID of an object using reflection
    private Long objectToId(T object) {
        try {
            Field idField = object.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            return (Long) idField.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e("DbTable", "Error accessing ID field", e);
            return null; // Return null if the ID can't be accessed
        }
    }

    // Method to convert a list of objects to a list of specific field values
    public <R> List<R> objectsToFields(List<T> objects, Function<T, R> fieldExtractor) {
        return objectsToSortedFields(objects, fieldExtractor, null); // Call the overloaded method with no sorting
    }

    // Method to convert a list of objects to a sorted list of specific field values
    public <R> List<R> objectsToSortedFields(List<T> objects, Function<T, R> fieldExtractor, Comparator<R> sortCallback) {
        if (objects == null || objects.isEmpty()) return Collections.emptyList();

        // Extract field values using fieldExtractor
        List<R> fieldValues = objects.stream()
                .map(fieldExtractor)
                .collect(Collectors.toList());

        // Sort if a sortCallback is provided
        if (sortCallback != null) {
            fieldValues.sort(sortCallback);
        }

        return fieldValues;
    }

    // Method to match field values to objects and return a comma-separated string of IDs
    // loop through fieldValues, for each fieldValue,
    // search by looping through the objects to find the object that has the same value as fieldValue
    // (use fieldExtractor to extract a value from each object)
    public <R> String objectFieldsToIdsString(List<T> objects, List<R> fieldValues, Function<T, R> fieldExtractor) {
        if (fieldValues == null || fieldValues.isEmpty() || objects == null || objects.isEmpty()) {
            return "";
        }

        return fieldValues.stream()
                .map(fieldValue -> findIdByField(objects, fieldValue, fieldExtractor))
                .filter(id -> id != null) // Filter out nulls if no matching object was found
                .map(String::valueOf)      // Convert each ID to a string
                .collect(Collectors.joining(","));
    }

    public Map<Long, String> objectsToIdsNames(List<T> objects, Function<T, String> nameExtractor) {
        return objects.stream()
                .collect(Collectors.toMap(this::objectToId, nameExtractor));
    }

    // New method: objectsToIdsObjects
    public Map<Long, T> objectsToIdsObjects(List<T> objects) {
        return objects.stream()
                .collect(Collectors.toMap(this::objectToId, Function.identity()));
    }



    // Helper method to find the ID of the first object that matches the given field value
    private <R> Long findIdByField(List<T> objects, R fieldValue, Function<T, R> fieldExtractor) {
        for (T object : objects) {
            // Check if the object's field matches the desired field value
            if (fieldExtractor.apply(object).equals(fieldValue)) {
                return objectToId(object); // Retrieve the object's ID
            }
        }
        return null; // Return null if no match was found
    }

    public <R, S> List<S> objectFieldsToObjectFields(
            List<T> objects,
            List<R> fieldValues,
            Function<T, R> sourceFieldExtractor,
            Function<T, S> targetFieldExtractor
    ) {
        if (fieldValues == null || fieldValues.isEmpty() || objects == null || objects.isEmpty()) {
            return Collections.emptyList();
        }

        // Find objects whose `sourceField` matches any value in `fieldValues` and extract their `targetField`
        return fieldValues.stream()
                .map(fieldValue -> findFieldByField(objects, fieldValue, sourceFieldExtractor, targetFieldExtractor))
                .filter(Objects::nonNull) // Filter out nulls if no matching object was found
                .collect(Collectors.toList());
    }

    private <R, S> S findFieldByField(
            List<T> objects,
            R sourceFieldValue,
            Function<T, R> sourceFieldExtractor,
            Function<T, S> targetFieldExtractor
    ) {
        for (T object : objects) {
            // Check if the object's sourceField matches the given sourceFieldValue
            if (sourceFieldExtractor.apply(object).equals(sourceFieldValue)) {
                return targetFieldExtractor.apply(object); // Extract the targetField value
            }
        }
        return null; // Return null if no match was found
    }

    // Helper method to delete appointment from table
    public void delete(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Appointment", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

}
