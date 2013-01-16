/*
 * @author Ganz7
 * Handles all the functions relating to the accessing of the database
 * Through the SQLIteHandler Class
 */

package ganz7.hash.database;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class LogsDataSource {

	// Database fields declaration
	private SQLiteDatabase database;
	private SQLiteHandler dbHelper;
	private String[] allColumns = { SQLiteHandler.COLUMN_ID,
			SQLiteHandler.COLUMN_LOG,SQLiteHandler.COLUMN_TITLE,SQLiteHandler.COLUMN_TIME,SQLiteHandler.COLUMN_ORDER};
	public int spinnerVal;
	public LogsDataSource(Context context) {
		dbHelper = new SQLiteHandler(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void openReadableDB() throws SQLException {
		database = dbHelper.getReadableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	/**
	 * Method for title extraction from the log
	 * @param comment
	 * @return
	 */
	public static String extractTags(String text)
	{
		
		String temp, noHash = "#noHash ";
		String currentTagString;
		boolean hashPresent = false;
		text = text + " "; 
		
		char[] buffer = new char[50];
		char[] currentTag = new char[50];
		
		int length = text.length();
		int i, j, tagIndex, temp_index;
		temp_index=0;
		i=0;
		
		while(i<length)
		{
			if(text.charAt(i)=='#')
			{
				j=i;
				hashPresent = true;
				tagIndex = 0;
				while(text.charAt(j)!=32 && text.charAt(j) != 10 )
				{
					buffer[temp_index] = text.charAt(j);
					currentTag[tagIndex] = text.charAt(j);
					temp_index++;
					tagIndex++;
					j++;
				}
				buffer[temp_index++]=' ';
				i=j;
				currentTagString = new String(currentTag);
				currentTagString.trim();
				
			}
			else
				i++;
		}
		if(hashPresent == false)
		{
			for(int pos=0; pos < noHash.length(); pos++)
			{
				buffer[pos] = noHash.charAt(pos);
			}
		}
		temp = new String(buffer);
		temp.trim();
		return temp;
	}
	
	public void extractTagsForList(String text, int operation)
	{
		final int DELETE = 1;
		final int CREATE = 0;
		
		String currentTagString;
		boolean hashPresent = false;
		text = text + " "; 
		
		char[] currentTag = new char[50];
		for(int k=0; k<50; k++)
			currentTag[k] = 32;
		int length = text.length();
		int i, j, tagIndex;
		i=0;
		
		while(i<length)
		{
			if(text.charAt(i)=='#')
			{
				j=i;
				tagIndex = 0;
				hashPresent = true;
				while(text.charAt(j)!=32 && text.charAt(j) != 10 )
				{
					
					currentTag[tagIndex] = text.charAt(j);
					tagIndex++;
					j++;
				}
				i=j;
				
				currentTagString = new String(currentTag);
				
				if(currentTagString.length()!=0)
				{
					currentTagString = currentTagString.replaceAll(" ", "");
				}
				
				if(operation == CREATE)
				{
					tagsForList(currentTagString);
				}
				else if (operation == DELETE)
				{
					deleteTagList(currentTagString);
				}
				for(int k=0; k<20; k++)
					currentTag[k] = 32;
				
				
			}
			else
				i++;
			
		}

		if(hashPresent==false)
		{
			currentTagString = new String("#noHash");
			if(operation == CREATE)
			{
				tagsForList(currentTagString);
			}
			else if (operation == DELETE)
			{
				deleteTagList(currentTagString);
			}
		}
	}
	void tagsForList(String tag)
	{
		ContentValues args = new ContentValues();
		tag = tag + " ";
		
		Cursor cursor = database.rawQuery("SELECT * FROM "+SQLiteHandler.TABLE_TAGS +
				" WHERE "+SQLiteHandler.TAG_NAME+" like '%"+tag+"%'" ,null);
	
		if(cursor.getCount() != 0)
		{
			
			if( cursor.moveToFirst())
			{
				
				
				int id = cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteHandler.TAG_ID));
				int count = cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteHandler.TAG_COUNT));
				count++;
				args.put(SQLiteHandler.TAG_COUNT, count);
				database.update(SQLiteHandler.TABLE_TAGS, args, SQLiteHandler.TAG_ID + "=" + id, null);
			
			}
		}
		else
		{
			
			args.put(SQLiteHandler.TAG_NAME, tag);
			args.put(SQLiteHandler.TAG_COUNT, 1);
			database.insert(SQLiteHandler.TABLE_TAGS, null, args);
		}
		
	}
	
	void deleteTagList(String tag)
	{
		ContentValues args = new ContentValues();
		
		Cursor cursor = database.rawQuery("SELECT * FROM "+SQLiteHandler.TABLE_TAGS +
				" WHERE "+SQLiteHandler.TAG_NAME+" like '%"+tag+"%'" ,null);
		
		if(cursor.getCount() != 0)
		{
			
			if( cursor.moveToFirst())
			{
				
				int id = cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteHandler.TAG_ID));
				int count = cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteHandler.TAG_COUNT));
				if(count == 1)
				{
					database.delete(SQLiteHandler.TABLE_TAGS, SQLiteHandler.TAG_ID
							+ " = " + id, null);
				}
				else
				{
					count--;
					args.put(SQLiteHandler.TAG_COUNT, count);
					database.update(SQLiteHandler.TABLE_TAGS, args, SQLiteHandler.TAG_ID + "=" + id, null);
				}
				
			
			}
		}
		else
		{
			//Log.w("DB","Illegal Call!");
			
		}
	}
	
	public Cursor fetchTagList()
	{
		
		
		String[] tags_counts = {SQLiteHandler.TAG_NAME}; 
		Cursor cursor = database.query(SQLiteHandler.TABLE_TAGS,
				tags_counts, null, null, null, null, SQLiteHandler.TAG_NAME);
		
		return cursor;
	}
	
	/**
	 *  Method which creates the log and puts it in the db
	 * @param log
	 */
	public void createLog(String log) {
		ContentValues values = new ContentValues();
		String title = extractTags(log);
		values.put(SQLiteHandler.COLUMN_LOG, log);
		values.put(SQLiteHandler.COLUMN_TITLE, title);
		
		/*String currentTimeString = DateFormat.getDateTimeInstance().format(new Date());
		
		values.put(MySQLiteHelper.COLUMN_TIME, currentTimeString);*/
		
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("HH:mm   MMM dd, ''yy");
        String formattedDate = df.format(c.getTime());
        SimpleDateFormat rev = new SimpleDateFormat("yyMMddHHmm");
        String orderDate = rev.format(c.getTime());
        values.put(SQLiteHandler.COLUMN_TIME, formattedDate);
        values.put(SQLiteHandler.COLUMN_ORDER, orderDate);
        
		database.insert(SQLiteHandler.TABLE_LOGS, null,
				values);
		
	}

	
	/**
	 * For removing a log based on its ID
	 * @param id
	 */
	public void deleteLog(long id) {
		
		Cursor cursor =
				database.query(true, SQLiteHandler.TABLE_LOGS, allColumns, SQLiteHandler.COLUMN_ID + "=" + id, null, null, null, null,
				null);
		if (cursor != null) {
			cursor.moveToFirst();
			String tag= cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHandler.COLUMN_TITLE));
			extractTagsForList(tag,1);
		}
	
		database.delete(SQLiteHandler.TABLE_LOGS, SQLiteHandler.COLUMN_ID
				+ " = " + id, null);
		
	}

	/**
	 * returns a cursor containing 5 of the most recent logs
	 * @return Cursor
	 */
	
	
	public Cursor returnLogs(String count)
	{
		
			Cursor cursor = database.query(SQLiteHandler.TABLE_LOGS,
				allColumns, null, null, null, null, SQLiteHandler.COLUMN_ORDER + " DESC ",count);
			return cursor;
		
	}
	
	/**
	 * returns a cursor containing all of the logs.
	 * @return Cursor
	 */
	public Cursor returnAll()
	{
		
		Cursor cursor = database.query(SQLiteHandler.TABLE_LOGS,
				allColumns, null, null, null, null, SQLiteHandler.COLUMN_ID + " DESC ");
		
		return cursor;
	}
	public Cursor returnTag()
	{
		String temp[] = {SQLiteHandler.TAG_ID, SQLiteHandler.TAG_NAME,SQLiteHandler.TAG_COUNT};
		Cursor cursor = database.query(SQLiteHandler.TABLE_TAGS,
				temp, null, null, null, null,SQLiteHandler.TAG_NAME);
		if(cursor!=null)
			cursor.moveToFirst();
		return cursor;
	}
	public Cursor returnTagLogs(String tag)
	{
		Cursor cursor = database.rawQuery("SELECT * FROM "+SQLiteHandler.TABLE_LOGS +
				" WHERE "+SQLiteHandler.COLUMN_TITLE+" like '%"+tag+"%' ORDER BY "+SQLiteHandler.COLUMN_ORDER+" DESC" ,null);
		return cursor;
	}
	/**
	 * 
	 * returns a cursor containing the searched tags.
	 * @param search The tags to search for
	 * @return
	 */
	public Cursor returnSearch(String search)
	{	
		String[] searchTags = {" ", " "," "};
		
		searchTags = createSearchTags(search);
		
		Cursor cursor = database.rawQuery("SELECT * FROM "+SQLiteHandler.TABLE_LOGS +
				" WHERE title like '%"+searchTags[0]+" %'" +
						" and title like '%"+searchTags[1]+" %'" +
								" and title like '%"+searchTags[2]+" %'"+"ORDER BY "+SQLiteHandler.COLUMN_TIME+" DESC",null);
		
		return cursor;
	}
	/**
	 * Parses the search string and returns each search tag as a separate entity.
	 * @param text
	 * @return StringArray
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public static String[] createSearchTags(String text) throws ArrayIndexOutOfBoundsException
	{
		text = text+" ";
		String[] searchTags = {"", "",""};
		
		int length = text.length();
		int i , start, end, count;
		//String eol = System.getProperty("line.separator");
		i=0;
		count = 0;
		start=0;
		end=0;
		while(i<length)
		{
			if(text.charAt(i)=='#')
			{
				start = i;
			}
			else if(text.charAt(i)==32)
			{
				end = i;
				searchTags[count] = text.substring(start,end);
				count++;
			}
			
			i++;
		
		}
		
		return searchTags;
	}
	
	

	
	/**
	 * Fetches just  a single log on _id
	 * @param row_id
	 * @return Cursor
	 * @throws SQLException
	 */
	
	public Cursor fetchLog(long row_id) throws SQLException 
	{

		Cursor cursor =
				database.query(true, SQLiteHandler.TABLE_LOGS, allColumns, SQLiteHandler.COLUMN_ID + "=" + row_id, null, null, null, null,
				null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;

	}

	/**
	 * Updates a modified log
	 * @param rowId
	 * @param log
	 * @return boolean
	 */
	public boolean updateNote(long rowId, String log, String oldLog) {
		ContentValues args = new ContentValues();
		
		String title = extractTags(log);
		String oldTitle = extractTags(oldLog);
		
		extractTagsForList(title,0);
		extractTagsForList(oldTitle,1);
		
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("HH:mm   MMM dd, ''yy");
        String formattedDate = df.format(c.getTime());
        SimpleDateFormat rev = new SimpleDateFormat("yyMMddHHmm");
        String orderDate = rev.format(c.getTime());
		
		args.put(SQLiteHandler.COLUMN_LOG, log);
		args.put(SQLiteHandler.COLUMN_TITLE, title);
		args.put(SQLiteHandler.COLUMN_TIME, formattedDate);
		args.put(SQLiteHandler.COLUMN_ORDER, orderDate);

		return database.update(SQLiteHandler.TABLE_LOGS, args, SQLiteHandler.COLUMN_ID + "=" + rowId, null)>0;
	}
	
	/*public List<Comment> getAllComments() {
		List<Comment> comments = new ArrayList<Comment>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS,
				allColumns, null, null, null, null, MySQLiteHelper.COLUMN_ID + " DESC ");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) 
		{
			//This method is defined below--- Over there,10 lines below?!
			Comment comment = cursorToComment(cursor);
			comments.add(comment);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return comments;
	}
	
	private Comment cursorToComment(Cursor cursor) {
		Comment comment = new Comment();
		comment.setId(cursor.getLong(0));
		comment.setComment(cursor.getString(1));
		comment.setTitle(cursor.getString(2));
		return comment;
	}*/
	/*public static String[] createSearchTags(String text)
	{
		text = text+" ";
		//String[] searchTags = {" ", " "," "};
		String[] searchTags = new String[3];
		char[][] buffer = new char[3][20];
		int length = text.length();
		int i , j , temp_index, count;
		i=0;
		count = 0;
		while(i<length)
		{
			temp_index=0;
			if(text.charAt(i)=='#' && count < 3)
			{
				j=i;
				while(text.charAt(j)!=32 )
				{
					buffer[count][temp_index] = text.charAt(j);
					temp_index++;
					j++;
				}
				//buffer[count][temp_index++]=' ';
				searchTags[count] = new String(buffer[count]);
				searchTags[count].trim();
				i=j;
				count++;
			}
			else
				i++;
		}
		
		return searchTags;
	}*/
}