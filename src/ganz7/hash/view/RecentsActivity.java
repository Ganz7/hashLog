package ganz7.hash.view;

import ganz7.hash.database.LogsDataSource;
import ganz7.hash.database.SQLiteHandler;
import ganz7.main.activity.R;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
/**
 * Lists the recent logs
 * @author Ganz7
 *
 */

public class RecentsActivity extends SherlockListActivity 
{
	private LogsDataSource DBobj;
	private static final int DELETE_ID = 2;
	private static final int SHARE_ID = 3;
	private Spinner listSpinner;
	public static volatile int globalItemID = 0;
	private static final int COLOR1 = 0x307f7f7f;
	private static final int COLOR2 = 0x30cfcfcf;//146A75;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_view);
		DBobj = new LogsDataSource(this);//Database obj Initialization
		DBobj.open();
		
		fillNote();
		
		registerForContextMenu(getListView());
		
		addItemsToListSpinner();
		
		Spinner mySpinner = (Spinner) findViewById(R.id.IcsSpinner1);
		mySpinner.setSelection(DBobj.spinnerVal);
		
		
		
	}
	
	/**
	 * Populates the list
	 */
	public void fillNote()
	{
		String count = "";
		TextView header = (TextView) findViewById(R.id.RecentHeader);
		switch(globalItemID)
		{
			case 0: count = "5"; 
					header.setText("RECENT 5");
					DBobj.spinnerVal = 0;
					break;
			case 1: count = "10"; 
					header.setText("RECENT 10");
					DBobj.spinnerVal = 1;
					break;
			case 2: count = ""; 
					header.setText("ALL LOGS");
					DBobj.spinnerVal = 2;
					break;
		}
		Cursor cursor = DBobj.returnLogs(count);

		startManagingCursor(cursor);

		String[] from = new String[] {SQLiteHandler.COLUMN_TITLE,SQLiteHandler.COLUMN_LOG,SQLiteHandler.COLUMN_TIME};
		int[] to = new int[]{R.id.text1,R.id.text2,R.id.text3};
		
		if(Build.VERSION.SDK_INT == 10)
		{
			ListAdapter notes = new SimpleCursorAdapter(this,
					R.layout.note_list_23, cursor, from, to)
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
		else
		{
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
	/**
	 * Creates the Context Menu
	 * Options:Delete
	 * Options:Share
	 */
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
		menu.add(1,SHARE_ID,1,R.string.menu_share);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) 
		{
			case DELETE_ID:
				
				DBobj.deleteLog(info.id);
				fillNote();
			
				Toast.makeText(getApplicationContext(),"Log  deleted!", Toast.LENGTH_SHORT).show();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

	// This method is called once the menu is selected
	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		// We have 3 menu options
		case R.id.home:
			
			returnToActivity();
			overridePendingTransition (R.anim.right_in, R.anim.right_out);
			break;
		case R.id.newlog:
			Intent myIntent = new Intent(this,CreateLogActivity.class);
			startActivityForResult(myIntent,2);
			overridePendingTransition (R.anim.right_in, R.anim.right_out);
			break;
		case R.id.search:
			Intent mySearch = new Intent(this,SearchActivity.class);
			startActivity(mySearch);
			overridePendingTransition (R.anim.right_in, R.anim.right_out);
			break;

		}
		return true;
	}
	
	@Override
	/**
	 * Calls the EditLogAct On ListItemClick
	 */
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
			fillNote();
			break;
		
			
		}
	}
	
	protected void addItemsToListSpinner()
	{
		listSpinner = (Spinner)findViewById(R.id.IcsSpinner1);
		List<String> spinnerItems = new ArrayList<String>();
		spinnerItems.add("Recent 5");
		spinnerItems.add("Recent 10");
		spinnerItems.add("All logs");
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, spinnerItems);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		listSpinner.setAdapter(spinnerAdapter);
	}
	
	
	public void updateLogs(View view)
	{
		if(view.getId() == R.id.spinnerButton)
		{
			globalItemID = listSpinner.getSelectedItemPosition();
			fillNote();
		}
	}
	
	public void onStop()
	{
		super.onStop();
	}
	
	public void onBackPressed()
	{
		super.onBackPressed();
		overridePendingTransition (R.anim.right_in, R.anim.right_out);
	}
	
	public void returnToActivity()
	{
		Intent myIntent = new Intent();
		setResult(RESULT_OK, myIntent);
		finish();
	}
	public void onDestroy()
	{
		super.onDestroy();
		DBobj.close();
		
	}
	
	
}