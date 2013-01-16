package ganz7.hash.paneview;

import ganz7.hash.database.LogsDataSource;
import ganz7.hash.database.SQLiteHandler;
import ganz7.hash.view.EditLogActivity;
import ganz7.main.activity.R;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class DetailActivity extends SherlockFragmentActivity 
{
	ListView lv;
	View vList;
	private LogsDataSource DBobj;
	private static final int EDIT_ID = 1;
	private static final int DELETE_ID = 2;
	private static final int SHARE_ID = 3;
	private static final int COLOR1 = 0x307f7f7f;
	private static final int COLOR2 = 0x30cfcfcf;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.w("Detail", "ACTIVITY oncreate");
		// Need to check if Activity has been switched to landscape mode
		// If yes, finished and go back to the start Activity
		/*if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
				&& Build.VERSION.SDK_INT > 10) {
			finish();
			return;
		}*/
		setContentView(R.layout.details_activity_layout);
		DBobj = new LogsDataSource(this);//obj Initialization
		DBobj.open();
		
		lv = (ListView)findViewById(R.id.list1);
		registerForContextMenu(lv);
				
		fillNote();
		
		
		
	}
	public void fillNote()
	{
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String s = extras.getString("value");
			
			Cursor logs = DBobj.returnTagLogs(s);
			startManagingCursor(logs);
			
			String[] from = new String[] {SQLiteHandler.COLUMN_TITLE,SQLiteHandler.COLUMN_LOG,SQLiteHandler.COLUMN_TIME};
			int[] to = new int[]{R.id.text1,R.id.text2,R.id.text3};
			
			
			ListAdapter notes = new SimpleCursorAdapter(this,
					R.layout.titleless_list, logs, from, to, 0)
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
				lv.setAdapter(notes);
			TextView view = (TextView) findViewById(R.id.detailsText);
			view.setText(s);
		}
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		vList = v;
		menu.add(0,EDIT_ID,0,R.string.menu_edit);
		menu.add(0,SHARE_ID,1,R.string.menu_share);
		menu.add(0, DELETE_ID, 2, R.string.menu_delete);
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
				break;
			case EDIT_ID:
				Intent i = new Intent(vList.getContext(), EditLogActivity.class);
				i.putExtra("row_id", info.id);
				startActivityForResult(i, 2); // 2 is just a code to know which activity is executing.
				overridePendingTransition (R.anim.right_in, R.anim.right_out);
				break;
				
				
		}
		return super.onContextItemSelected(item);
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
	
	public void onDestroy()
	{
		super.onDestroy();
		if(DBobj != null)
			DBobj.close();
	}
}