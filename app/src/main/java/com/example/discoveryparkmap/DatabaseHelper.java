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

    private static final String DATABASE_NAME = "DiscoveryParkMap.db";
    private static final int DATABASE_VERSION = 2; // Bumped version to handle recitation and notes

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
    private static final String KEY_CLASS_RECITATION_ROOM = "recitation_room";
    private static final String KEY_CLASS_RECITATION_START_TIME = "recitation_start_time";
    private static final String KEY_CLASS_RECITATION_END_TIME = "recitation_end_time";
    private static final String KEY_CLASS_NOTES = "notes";

    // Room Table Columns
    private static final String KEY_ROOM_ID = "id";
    private static final String KEY_ROOM_NUMBER = "room_number";
    private static final String KEY_ROOM_FLOOR = "floor";
    private static final String KEY_ROOM_WING = "wing";
    private static final String KEY_ROOM_X = "x_coordinate";
    private static final String KEY_ROOM_Y = "y_coordinate";
    private static final String KEY_ROOM_ACCESSIBLE = "is_accessible";
    private static final String KEY_ROOM_BOOKMARKED = "is_bookmarked";

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
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
                KEY_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_USER_NAME + " TEXT," +
                KEY_USER_EMAIL + " TEXT," +
                KEY_USER_USERNAME + " TEXT UNIQUE," +
                KEY_USER_PASSWORD + " TEXT" +
                ")";

        String CREATE_ROOMS_TABLE = "CREATE TABLE " + TABLE_ROOMS + " (" +
                KEY_ROOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_ROOM_NUMBER + " TEXT UNIQUE," +
                KEY_ROOM_FLOOR + " INTEGER," +
                KEY_ROOM_WING + " TEXT," +
                KEY_ROOM_X + " REAL," +
                KEY_ROOM_Y + " REAL," +
                KEY_ROOM_ACCESSIBLE + " INTEGER," +
                KEY_ROOM_BOOKMARKED + " INTEGER" +
                ")";

        String CREATE_CLASSES_TABLE = "CREATE TABLE " + TABLE_CLASSES + " (" +
                KEY_CLASS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_CLASS_USER_ID_FK + " INTEGER REFERENCES " + TABLE_USERS + "," +
                KEY_CLASS_CODE + " TEXT," +
                KEY_CLASS_NAME + " TEXT," +
                KEY_CLASS_ROOM_ID_FK + " INTEGER REFERENCES " + TABLE_ROOMS + "," +
                KEY_CLASS_DAYS + " TEXT," +
                KEY_CLASS_START_TIME + " TEXT," +
                KEY_CLASS_END_TIME + " TEXT," +
                KEY_CLASS_RECITATION_ROOM + " TEXT," +
                KEY_CLASS_RECITATION_START_TIME + " TEXT," +
                KEY_CLASS_RECITATION_END_TIME + " TEXT," +
                KEY_CLASS_NOTES + " TEXT" +
                ")";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_ROOMS_TABLE);
        db.execSQL(CREATE_CLASSES_TABLE);

        populateInitialRooms(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    private void populateInitialRooms(SQLiteDatabase db) {
        addRoomDirect(db, "B155", 1, "B", 100, 150, 1, 0);
        addRoomDirect(db, "E130", 1, "E", 250, 200, 1, 0);
        addRoomDirect(db, "A151", 1, "A", 50, 100, 1, 0);
        addRoomDirect(db, "C175", 1, "C", 150, 300, 1, 0);
        addRoomDirect(db, "D110", 1, "D", 200, 100, 1, 0);
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

    /* USER METHODS */

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
            Log.e("DB_ERROR", "Error adding user", e);
        } finally {
            db.endTransaction();
        }
        return userId;
    }

    public int getUserIdByCredentials(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        int userId = -1;

        String hashedPassword = hashPassword(password);

        String query = "SELECT " + KEY_USER_ID + " FROM " + TABLE_USERS +
                " WHERE " + KEY_USER_USERNAME + " = ? AND " +
                KEY_USER_PASSWORD + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{username, hashedPassword});
        try {
            if (cursor.moveToFirst()) {
                userId = cursor.getInt(0);
            }
        } finally {
            cursor.close();
        }
        return userId;
    }

    public boolean isUsernameTaken(String username) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_USERS +
                " WHERE " + KEY_USER_USERNAME + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{username});
        try {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0) > 0;
            }
        } finally {
            cursor.close();
        }
        return false;
    }

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
            Log.e("DB_ERROR", "Error hashing password", e);
            return null;
        }
    }

    /* ROOM METHODS */

    public Room getRoomByNumber(String roomNumber) {
        SQLiteDatabase db = getReadableDatabase();
        Room room = null;

        String query = "SELECT * FROM " + TABLE_ROOMS +
                " WHERE " + KEY_ROOM_NUMBER + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{roomNumber});
        try {
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
        } finally {
            cursor.close();
        }
        return room;
    }

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
            Log.e("DB_ERROR", "Error adding room", e);
        } finally {
            db.endTransaction();
        }
        return roomId;
    }

    /* CLASS METHODS */

    public long addClass(int userId, ClassInfo classInfo) {
        SQLiteDatabase db = getWritableDatabase();
        long classId = -1;

        db.beginTransaction();
        try {
            long roomId = addOrGetRoom(classInfo.getRoom());

            ContentValues values = new ContentValues();
            values.put(KEY_CLASS_USER_ID_FK, userId);
            values.put(KEY_CLASS_CODE, classInfo.getClassCode());
            values.put(KEY_CLASS_NAME, classInfo.getClassName());
            values.put(KEY_CLASS_ROOM_ID_FK, roomId);
            values.put(KEY_CLASS_DAYS, classInfo.getDays());
            values.put(KEY_CLASS_START_TIME, classInfo.getStartTime());
            values.put(KEY_CLASS_END_TIME, classInfo.getEndTime());
            values.put(KEY_CLASS_RECITATION_ROOM, classInfo.getRecitationRoom());
            values.put(KEY_CLASS_RECITATION_START_TIME, classInfo.getRecitationStartTime());
            values.put(KEY_CLASS_RECITATION_END_TIME, classInfo.getRecitationEndTime());
            values.put(KEY_CLASS_NOTES, classInfo.getNotes());

            classId = db.insertOrThrow(TABLE_CLASSES, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error adding class", e);
        } finally {
            db.endTransaction();
        }

        return classId;
    }

    public List<ClassInfo> getClassesForUser(int userId) {
        List<ClassInfo> classes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT c.*, r.* FROM " + TABLE_CLASSES + " c " +
                "JOIN " + TABLE_ROOMS + " r ON c." + KEY_CLASS_ROOM_ID_FK + " = r." + KEY_ROOM_ID +
                " WHERE c." + KEY_CLASS_USER_ID_FK + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        try {
            if (cursor.moveToFirst()) {
                do {
                    Room room = new Room();
                    room.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ROOM_ID)));
                    room.setRoomNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ROOM_NUMBER)));
                    room.setFloor(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ROOM_FLOOR)));
                    room.setWing(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ROOM_WING)));
                    room.setX(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_ROOM_X)));
                    room.setY(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_ROOM_Y)));
                    room.setAccessible(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ROOM_ACCESSIBLE)) == 1);
                    room.setBookmarked(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ROOM_BOOKMARKED)) == 1);

                    ClassInfo classInfo = new ClassInfo();
                    classInfo.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CLASS_ID)));
                    classInfo.setClassCode(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CLASS_CODE)));
                    classInfo.setClassName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CLASS_NAME)));
                    classInfo.setRoom(room);
                    classInfo.setDays(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CLASS_DAYS)));
                    classInfo.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CLASS_START_TIME)));
                    classInfo.setEndTime(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CLASS_END_TIME)));
                    classInfo.setRecitationRoom(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CLASS_RECITATION_ROOM)));
                    classInfo.setRecitationStartTime(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CLASS_RECITATION_START_TIME)));
                    classInfo.setRecitationEndTime(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CLASS_RECITATION_END_TIME)));
                    classInfo.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CLASS_NOTES)));

                    classes.add(classInfo);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return classes;
    }

    public boolean deleteClass(int classId) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_CLASSES, KEY_CLASS_ID + "=?", new String[]{String.valueOf(classId)});
        return rows > 0;
    }
}
