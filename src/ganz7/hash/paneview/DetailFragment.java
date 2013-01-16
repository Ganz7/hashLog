/**
 * Lists the logs in the right pane in case of a supported screen
 *@author Ganz7
 */
package ganz7.hash.paneview;

import ganz7.hash.database.LogsDataSource;
import ganz7.hash.database.SQLiteHandler;
import ganz7.hash.database.SimpleCursorLoader;
import ganz7.hash.view.EditLogActivity;
import ganz7.main.activity.R;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class DetailFragment extends SherlockFragment implements LoaderCallbacks<Cursor> {
	
	private LogsDataSource DBobj;
	Cursor tagCursor;
	ListAdapter notes;
	ListView lv;
	View logView;
	String tag;
	static int showFragment ;
	private static final int EDIT_ID = 1;
	private static final int DELETE_ID = 2;
	private static final int SHARE_ID = 3;
	
	private static final int COLOR1 = 0x307f7f7f;
	private static final int COLOR2 = 0x30cfcfcf;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getLoaderManager().initLoader(0, null, this);

		//DBobj = new LogsDataSource(getActivity());//obj Initialization
		//DBobj.openReadableDB();
	}
	@Override
	public void onResume()
	{
		super.onResume();
		if(DBobj == null)
		{
			//DBobj = new LogsDataSource(getActivity());//obj Initialization
			//DBobj.openReadableDB();
		}
	}
	

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		//if(DBobj != null)
			//DBobj.close();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if(showFragment == 1)
		{
			lv = (ListView) getView().findViewById(R.id.list1);
			//registerForContextMenu(lv);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.details, container, false);
		return view;
		
		
	}

	
	public void setText(String item) {
		
		tag = item;
		
		lv = (ListView) getView().findViewById(R.id.list1);
		registerForContextMenu(lv);
		getLoaderManager().restartLoader(0, null, this);
		TextView view = (TextView) getView().findViewById(R.id.detailsText);
		view.setText(item);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		
		return new SimpleCursorLoader(getActivity())
		{
			
			@Override
			public Cursor loadInBackground()
			{
				//if(DBobj == null)
				try{
				
					DBobj = new LogsDataSource(getActivity());
					DBobj.openReadableDB();
				
					tagCursor = DBobj.returnTagLogs(tag);
				}catch(Exception e)
				{
					e.printStackTrace();
				}
				return tagCursor;
			}
			
		};
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		
		
		if(showFragment == 1 )
		{
			String[] from = new String[] {SQLiteHandler.COLUMN_TITLE,SQLiteHandler.COLUMN_LOG,SQLiteHandler.COLUMN_TIME};
			int[] to = new int[]{R.id.text1,R.id.text2,R.id.text3};
		
			
			notes = new SimpleCursorAdapter(getActivity(),
				R.layout.titleless_list, arg1, from, to, 0)
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
			//tagCursor.close();
			//DBobj.close();
						
		}
		
		/*if (isResumed()) {
	        setListShown(true);
	    } else {
	        setListShownNoAnimation(true);
	    }*/
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) 
	{
		
		if(showFragment == 1)
		{
			
			//tagCursor.close();
			//((SimpleCursorAdapter) notes).swapCursor(null);
		}
	}
	
	
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		logView = v;
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
				getLoaderManager().restartLoader(0, null, this);
			
				return true;
			case SHARE_ID:
			
				Cursor log = DBobj.fetchLog(info.id);
				log.moveToFirst();
				
				String logString = log.getString(log.getColumnIndexOrThrow(SQLiteHandler.COLUMN_LOG));
				
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT, logString);
				sendIntent.setType("text/plain");
				startActivity(Intent.createChooser(sendIntent, "Share via"));
				//overridePendingTransition (R.anim.right_in, R.anim.right_out);
				break;
			case EDIT_ID:
				//View vlist = (View) FragmentActivity.findViewById(R.id.details);
				Intent i = new Intent(logView.getContext(), EditLogActivity.class);
				i.putExtra("row_id", info.id);
				startActivityForResult(i, 2); // 2 is just a code to know which activity is executing.
				//getActivity().overridePendingTransition (R.anim.right_in, R.anim.right_out);
				break;
				
		}
		return super.onContextItemSelected(item);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode,Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		
		switch(requestCode)
		{
		
		case 2:
			getLoaderManager().restartLoader(0, null, this);
			break;
		
			
		}
	}
}

