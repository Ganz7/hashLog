package ganz7.hash.view;

import ganz7.hash.database.LogsDataSource;
import ganz7.hash.database.SQLiteHandler;
import ganz7.main.activity.HashLogActivity;
import ganz7.main.activity.R;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
/**
 * Searches by tags and lists the logs
 * @author Ganz7
 *
 */
public class SearchActivity extends SherlockListActivity 
{
	private String searchTag;
	private LogsDataSource DBobj;
	private static final int DELETE_ID = 2;
	private static final int SHARE_ID = 3;
	private static final int COLOR1 = 0x309f9f9f;
	private static final int COLOR2 = 0x30dfdfdf;//146A75;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_view);
		DBobj = new LogsDataSource(this);//obj Initialization
		DBobj.open();
		//searchResults();
		registerForContextMenu(getListView());
		
		
	}
	/**
	 * Gets the search arg and starts searching
	 * @param view
	 */
	public void searchHandler(View view)
	{
		switch(view.getId())
		{
			case R.id.buttonSearch:
				EditText search_value = (EditText) findViewById(R.id.searchText);
				searchTag = search_value.getText().toString();
				if(searchTag.length() == 0)
				{
					Toast.makeText(getApplicationContext(),"Oops! Looks like you didn't type a tag!", Toast.LENGTH_SHORT).show();
				}
				else if(countOccurrences(searchTag, '#') > 3)
				{
					Toast.makeText(getApplicationContext(),"Only three tags can be searched at a time", Toast.LENGTH_LONG).show();
					search_value.setText("");
				}
				else
				{
					searchResults(searchTag);
					//search_value.setText("");
				}
			break;
			case R.id.hashButton:
				EditText addHash = (EditText) findViewById(R.id.searchText);
				int pos = addHash.getSelectionStart();
				String currentContents = addHash.getText().toString();
				//currentContents = StringBuffer(currentContents).insert(pos, "C").toString();
				currentContents = currentContents.substring(0,pos) + "#" + currentContents.substring(pos,currentContents.length());
				addHash.setText(currentContents);
				addHash.setSelection(currentContents.length());
		}
	}
	/**
	 * Populates the list with the returned logs
	 * @param search
	 */
	void searchResults(String search)
	{
		Cursor cursor = DBobj.returnSearch(search);

		startManagingCursor(cursor);
		
		if(cursor.getCount()==0)
		{
			Toast.makeText(getApplicationContext(),"Sorry. No matches found!", Toast.LENGTH_SHORT).show();
		}
		
		else
		{
			
		
			String[] from = new String[] {SQLiteHandler.COLUMN_TITLE,SQLiteHandler.COLUMN_LOG,SQLiteHandler.COLUMN_TIME};
			int[] to = new int[]{R.id.text1,R.id.text2,R.id.text3};
		
			ListAdapter notes = new SimpleCursorAdapter(this,
				R.layout.note_list, cursor, from, to)
			{
				@Override
				public View getView(int position, View convertView, ViewGroup parent)
				{
					final View row = super.getView(position, convertView, parent);
					if (position % 2 == 1)
						row.setBackgroundColor(COLOR1);
					else
						row.setBackgroundColor(COLOR2);
					return row;
				}
			};
			setListAdapter(notes);
		
		}
	}
	@Override
	//Creates the Context menu
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
		menu.add(1,SHARE_ID,1,R.string.menu_share);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case DELETE_ID:
			
			DBobj.deleteLog(info.id);
			
			searchResults(searchTag);
			Toast.makeText(getApplicationContext(),"Log deleted!", Toast.LENGTH_SHORT).show();
			return true;
		case SHARE_ID:
			//Toast.makeText(getApplicationContext(),"Sharingg...", Toast.LENGTH_SHORT).show();
			Cursor log = DBobj.fetchLog(info.id);
			startManagingCursor(log);
			String logString = log.getString(log.getColumnIndexOrThrow(SQLiteHandler.COLUMN_LOG));
			
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, logString);
			sendIntent.setType("text/plain");
			startActivity(Intent.createChooser(sendIntent, "Share via"));
			overridePendingTransition (R.anim.right_in, R.anim.right_out);
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(v.getContext(), EditLogActivity.class);
		i.putExtra("row_id", id);
		startActivityForResult(i, 2); // 2 is just a code to know which activity is executing.
		overridePendingTransition (R.anim.right_in, R.anim.right_out);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		
		switch(requestCode)
		{
		
		case 2:
			searchResults(searchTag);
			break;
		}
	}
	/**
	 * Only 3 tags are allowed to be searched for at a time.
	 * This method checks for '#'symbols and counts them 
	 * @param text
	 * @param findIt
	 * @return count
	 */
	
	public static int countOccurrences(String text, char findIt)
	{
	    int count = 0;
	    for (int i=0; i < text.length(); i++)
	    {
	        if (text.charAt(i) == findIt)
	        {
	             count++;
	        }
	    }
	    return count;
	}

	
	@Override
	/**
	 * Creating the menu
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.searchmenu, menu);
		return true;
	}

	// This method is called once the menu is selected
	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		
		case R.id.home:
			returnToActivity();
			overridePendingTransition (R.anim.right_in, R.anim.right_out);
			break;
		case R.id.newlog:
			Intent myIntent = new Intent(this,CreateLogActivity.class);
			startActivityForResult(myIntent,2);
			overridePendingTransition (R.anim.right_in, R.anim.right_out);
			break;
		}
		return true;
	}
	
	
	public void onBackPressed()
	{
		super.onBackPressed();
		Intent intent = new Intent(this, HashLogActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition (R.anim.right_in, R.anim.right_out);
	}
	
	public void returnToActivity()
	{
		Intent intent = new Intent(this, HashLogActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition (R.anim.right_in, R.anim.right_out);
	}
	
	public void onDestroy()
	{
		super.onDestroy();
		DBobj.close();
	}
	
	
}
