package ganz7.hash.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHandler extends SQLiteOpenHelper {

	public static final String TABLE_LOGS = "logs_table";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_LOG = "log";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_TIME = "time";
	public static final String COLUMN_ORDER = "_order";
	
	public static final String TABLE_TAGS = "tags_table";
	public static final String TAG_ID = "_id";
	public static final String TAG_NAME = "tag";
	public static final String TAG_COUNT = "recurrence";
	
	
	

	private static final String DATABASE_NAME = "hashlogdb.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String TABLE_1_CREATE = "create table "
			+ TABLE_LOGS + "( " + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_LOG
			+ " text not null," + COLUMN_TITLE
			+ " text not null,"+ COLUMN_TIME
			+ " text not null,"+ COLUMN_ORDER
			+ " text not null);";
	
	
	public static final String TABLE_2_CREATE = "create table "
			+ TABLE_TAGS + "( " + TAG_ID
			+ " integer primary key autoincrement, " + TAG_NAME
			+ " text not null," + TAG_COUNT
			+ " integer);";

	public SQLiteHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(TABLE_1_CREATE);
		database.execSQL(TABLE_2_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(SQLiteHandler.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAGS);
		onCreate(db);
	}

}
