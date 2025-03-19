package com.example.discoveryparkmap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Info
    private static final String DATABASE_NAME = "DiscoveryParkMap.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "DatabaseHelper";

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_CLASSES = "classes";
    private static final String TABLE_ROOMS = "rooms";

    // User Table Columns
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USER_NAME = "name";
    private static final String KEY_USER_EMAIL = "email";
    private static final String KEY_USER_USERNAME = "username";
    private static final String KEY_USER_PASSWORD = "password";

    // Class Table Columns
    private static final String KEY_CLASS_ID = "id";
    private static final String KEY_CLASS_USER_ID_FK = "user_id";
    private static final String KEY_CLASS_CODE = "class_code";
    private static final String KEY_CLASS_NAME = "class_name";
    private static final String KEY_CLASS_ROOM_ID_FK = "room_id";
    private static final String KEY_CLASS_DAYS = "days";
    private static final String KEY_CLASS_START_TIME = "start_time";
    private static final String KEY_CLASS_END_TIME = "end_time";

    // Room Table Columns
    private static final String KEY_ROOM_ID = "id";
    private static final String KEY_ROOM_NUMBER = "room_number";
    private static final String KEY_ROOM_FLOOR = "floor";
    private static final String KEY_ROOM_WING = "wing";
    private static final String KEY_ROOM_X = "x_coordinate";
    private static final String KEY_ROOM_Y = "y_coordinate";
    private static final String KEY_ROOM_ACCESSIBLE = "is_accessible";
    private static final String KEY_ROOM_BOOKMARKED = "is_bookmarked";

    // Singleton pattern
    private static DatabaseHelper sInstance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
                "(" +
                KEY_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_USER_NAME + " TEXT," +
                KEY_USER_EMAIL + " TEXT," +
                KEY_USER_USERNAME + " TEXT UNIQUE," +
                KEY_USER_PASSWORD + " TEXT" +
                ")";

        String CREATE_ROOMS_TABLE = "CREATE TABLE " + TABLE_ROOMS +
                "(" +
                KEY_ROOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_ROOM_NUMBER + " TEXT UNIQUE," +
                KEY_ROOM_FLOOR + " INTEGER," +
                KEY_ROOM_WING + " TEXT," +
                KEY_ROOM_X + " REAL," +
                KEY_ROOM_Y + " REAL," +
                KEY_ROOM_ACCESSIBLE + " INTEGER," +
                KEY_ROOM_BOOKMARKED + " INTEGER" +
                ")";

        String CREATE_CLASSES_TABLE = "CREATE TABLE " + TABLE_CLASSES +
                "(" +
                KEY_CLASS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_CLASS_USER_ID_FK + " INTEGER REFERENCES " + TABLE_USERS + "," +
                KEY_CLASS_CODE + " TEXT," +
                KEY_CLASS_NAME + " TEXT," +
                KEY_CLASS_ROOM_ID_FK + " INTEGER REFERENCES " + TABLE_ROOMS + "," +
                KEY_CLASS_DAYS + " TEXT," +
                KEY_CLASS_START_TIME + " TEXT," +
                KEY_CLASS_END_TIME + " TEXT" +
                ")";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_ROOMS_TABLE);
        db.execSQL(CREATE_CLASSES_TABLE);

        // Populate some initial room data
        populateInitialRooms(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Drop older tables
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASSES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOMS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

            // Recreate tables
            onCreate(db);
        }
    }

    private void populateInitialRooms(SQLiteDatabase db) {
        // First floor rooms
        addRoomDirect(db, "B155", 1, "B", 100, 150, 1, 0);
        addRoomDirect(db, "E130", 1, "E", 250, 200, 1, 0);
        addRoomDirect(db, "A151", 1, "A", 50, 100, 1, 0);
        addRoomDirect(db, "C175", 1, "C", 150, 300, 1, 0);
        addRoomDirect(db, "D110", 1, "D", 200, 100, 1, 0);

        // Second floor rooms
        addRoomDirect(db, "F231", 2, "F", 300, 200, 1, 0);
        addRoomDirect(db, "K250", 2, "K", 350, 250, 1, 0);
        addRoomDirect(db, "B266", 2, "B", 100, 175, 1, 0);
        addRoomDirect(db, "A210", 2, "A", 75, 125, 1, 0);
        addRoomDirect(db, "M220", 2, "M", 400, 300, 1, 0);
    }

    private void addRoomDirect(SQLiteDatabase db, String roomNumber, int floor, String wing,
                               double x, double y, int isAccessible, int isBookmarked) {
        ContentValues values = new ContentValues();
        values.put(KEY_ROOM_NUMBER, roomNumber);
        values.put(KEY_ROOM_FLOOR, floor);
        values.put(KEY_ROOM_WING, wing);
        values.put(KEY_ROOM_X, x);
        values.put(KEY_ROOM_Y, y);
        values.put(KEY_ROOM_ACCESSIBLE, isAccessible);
        values.put(KEY_ROOM_BOOKMARKED, isBookmarked);

        db.insert(TABLE_ROOMS, null, values);
    }

    // Hash password using SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Error hashing password", e);
            return null;
        }
    }

    /* USER METHODS */

    // Add a new user
    public long addUser(String name, String email, String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        long userId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_NAME, name);
            values.put(KEY_USER_EMAIL, email);
            values.put(KEY_USER_USERNAME, username);
            values.put(KEY_USER_PASSWORD, hashPassword(password));

            userId = db.insertOrThrow(TABLE_USERS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error adding user", e);
        } finally {
            db.endTransaction();
        }

        return userId;
    }

    // Check if user exists with given username and password
    public int getUserIdByCredentials(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        int userId = -1;

        String hashedPassword = hashPassword(password);
        String SELECT_USER_QUERY =
                "SELECT " + KEY_USER_ID + " FROM " + TABLE_USERS +
                        " WHERE " + KEY_USER_USERNAME + " = ? AND " +
                        KEY_USER_PASSWORD + " = ?";

        try (Cursor cursor = db.rawQuery(SELECT_USER_QUERY, new String[]{username, hashedPassword})) {
            if (cursor.moveToFirst()) {
                userId = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking user credentials", e);
        }

        return userId;
    }

    // Get user name by ID
    public String getUserNameById(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        String name = "";

        String SELECT_QUERY =
                "SELECT " + KEY_USER_NAME + " FROM " + TABLE_USERS +
                        " WHERE " + KEY_USER_ID + " = ?";

        try (Cursor cursor = db.rawQuery(SELECT_QUERY, new String[]{String.valueOf(userId)})) {
            if (cursor.moveToFirst()) {
                name = cursor.getString(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user name", e);
        }

        return name;
    }

    /* ROOM METHODS */

    // Get room by room number
    public Room getRoomByNumber(String roomNumber) {
        SQLiteDatabase db = getReadableDatabase();
        Room room = null;

        String SELECT_QUERY =
                "SELECT * FROM " + TABLE_ROOMS +
                        " WHERE " + KEY_ROOM_NUMBER + " = ?";

        try (Cursor cursor = db.rawQuery(SELECT_QUERY, new String[]{roomNumber})) {
            if (cursor.moveToFirst()) {
                room = new Room();
                room.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ROOM_ID)));
                room.setRoomNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ROOM_NUMBER)));
                room.setFloor(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ROOM_FLOOR)));
                room.setWing(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ROOM_WING)));
                room.setX(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_ROOM_X)));
                room.setY(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_ROOM_Y)));
                room.setAccessible(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ROOM_ACCESSIBLE)) == 1);
                room.setBookmarked(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ROOM_BOOKMARKED)) == 1);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting room", e);
        }

        return room;
    }

    // Add new room or get existing room ID
    public long addOrGetRoom(Room room) {
        Room existingRoom = getRoomByNumber(room.getRoomNumber());
        if (existingRoom != null) {
            return existingRoom.getId();
        }

        SQLiteDatabase db = getWritableDatabase();
        long roomId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_ROOM_NUMBER, room.getRoomNumber());
            values.put(KEY_ROOM_FLOOR, room.getFloor());
            values.put(KEY_ROOM_WING, room.getWing());
            values.put(KEY_ROOM_X, room.getX());
            values.put(KEY_ROOM_Y, room.getY());
            values.put(KEY_ROOM_ACCESSIBLE, room.isAccessible() ? 1 : 0);
            values.put(KEY_ROOM_BOOKMARKED, room.isBookmarked() ? 1 : 0);

            roomId = db.insertOrThrow(TABLE_ROOMS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error adding room", e);
        } finally {
            db.endTransaction();
        }

        return roomId;
    }

    /* CLASS METHODS */

    // Add a class for a user
    public long addClass(int userId, ClassInfo classInfo) {
        SQLiteDatabase db = getWritableDatabase();
        long classId = -1;

        db.beginTransaction();
        try {
            // Add room or get existing room ID
            long roomId = addOrGetRoom(classInfo.getRoom());

            // Add class
            ContentValues values = new ContentValues();
            values.put(KEY_CLASS_USER_ID_FK, userId);
            values.put(KEY_CLASS_CODE, classInfo.getClassCode());
            values.put(KEY_CLASS_NAME, classInfo.getClassName());
            values.put(KEY_CLASS_ROOM_ID_FK, roomId);
            values.put(KEY_CLASS_DAYS, classInfo.getDays());
            values.put(KEY_CLASS_START_TIME, classInfo.getStartTime());
            values.put(KEY_CLASS_END_TIME, classInfo.getEndTime());

            classId = db.insertOrThrow(TABLE_CLASSES, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error adding class", e);
        } finally {
            db.endTransaction();
        }

        return classId;
    }

    // Get all classes for a user
    public List<ClassInfo> getClassesForUser(int userId) {
        List<ClassInfo> classes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String SELECT_QUERY =
                "SELECT c.*, r.* FROM " + TABLE_CLASSES + " c " +
                        "JOIN " + TABLE_ROOMS + " r ON c." + KEY_CLASS_ROOM_ID_FK + " = r." + KEY_ROOM_ID +
                        " WHERE c." + KEY_CLASS_USER_ID_FK + " = ?";

        try (Cursor cursor = db.rawQuery(SELECT_QUERY, new String[]{String.valueOf(userId)})) {
            if (cursor.moveToFirst()) {
                do {
                    // Create room object
                    Room room = new Room();
                    room.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ROOM_ID)));
                    room.setRoomNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ROOM_NUMBER)));
                    room.setFloor(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ROOM_FLOOR)));
                    room.setWing(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ROOM_WING)));
                    room.setX(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_ROOM_X)));
                    room.setY(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_ROOM_Y)));
                    room.setAccessible(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ROOM_ACCESSIBLE)) == 1);
                    room.setBookmarked(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ROOM_BOOKMARKED)) == 1);

                    // Create class object
                    ClassInfo classInfo = new ClassInfo();
                    classInfo.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CLASS_ID)));
                    classInfo.setClassCode(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CLASS_CODE)));
                    classInfo.setClassName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CLASS_NAME)));
                    classInfo.setRoom(room);
                    classInfo.setDays(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CLASS_DAYS)));
                    classInfo.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CLASS_START_TIME)));
                    classInfo.setEndTime(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CLASS_END_TIME)));

                    classes.add(classInfo);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting classes", e);
        }

        return classes;
    }

    // Delete a class by ID
    public boolean deleteClass(int classId) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsAffected = 0;

        db.beginTransaction();
        try {
            rowsAffected = db.delete(TABLE_CLASSES, KEY_CLASS_ID + " = ?",
                    new String[]{String.valueOf(classId)});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error deleting class", e);
        } finally {
            db.endTransaction();
        }

        return rowsAffected > 0;
    }

    // Check if username exists
    public boolean isUsernameTaken(String username) {
        SQLiteDatabase db = getReadableDatabase();
        String SELECT_QUERY = "SELECT COUNT(*) FROM " + TABLE_USERS +
                " WHERE " + KEY_USER_USERNAME + " = ?";

        try (Cursor cursor = db.rawQuery(SELECT_QUERY, new String[]{username})) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0) > 0;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking username", e);
        }

        return false;
    }
}