package ganz7.hash.paneview;

import ganz7.hash.database.LogsDataSource;
import ganz7.hash.database.SQLiteHandler;
import ganz7.hash.database.SimpleCursorLoader;
import ganz7.main.activity.R;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;

public class TagListFragment extends SherlockListFragment implements LoaderCallbacks<Cursor> {
	private LogsDataSource DBobj;
	Cursor sampleCursor;
	SimpleCursorLoader simpleLoad;
	ListAdapter notes;
	SimpleCursorAdapter adapter;
	
	private static final int COLOR1 = 0x30fefefe;
	private static final int COLOR2 = 0x30cfcfcf;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getLoaderManager().initLoader(0, null, this);
		//DBobj = new LogsDataSource(getActivity());//obj Initialization
		//DBobj.openReadableDB();
	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		//if(DBobj != null)
			//DBobj.close();
	}
	
	/*public void onPause()
	{
		super.onPause();
		if(DBobj != null)
			DBobj.close();
	}*/
	
	@Override
	public void onResume()
	{
		super.onResume();
		/*if(DBobj == null)
		{
			DBobj = new LogsDataSource(getActivity());//obj Initialization
			DBobj.openReadableDB();
		}*/
		
		getLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		
		
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) 
	{
		Cursor selectedCursor = (Cursor) notes.getItem(position);
		
		String item = (String) selectedCursor.getString(1);
		
	
		DetailFragment fragment = (DetailFragment) getFragmentManager()
				.findFragmentById(R.id.detailFragment);
		
		
		
		if (fragment != null && fragment.isInLayout()) {
			
			
		l.setSelection(position);
			DetailFragment.showFragment = 1;
			fragment.setText(item);
			
		} else {
			Intent intent = new Intent(getActivity().getApplicationContext(),
					DetailActivity.class);
			intent.putExtra("value", item);
			DetailFragment.showFragment = 0;
			
			startActivity(intent);

		}

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
				
				sampleCursor = DBobj.returnTag();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
				return sampleCursor;	
			}
			
		};
		
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
	
		/*String[] from = new String[] {SQLiteHandler.COLUMN_TITLE};
		int[] to = new int[]{R.id.text1};*/
		
		
				
			String[] from = new String[] {SQLiteHandler.TAG_NAME,SQLiteHandler.TAG_COUNT};
			int[] to = new int[]{R.id.text1,R.id.text2};
		
		notes = new SimpleCursorAdapter(getActivity(),
			R.layout.tags_list, arg1, from, to, 0)
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
		
		//sampleCursor.close();
		//if(DBobj!=null)
			//DBobj.close();
		
		
		
		
		//notes.swapCursor();
		//if (isResumed()) {
	       // setListShown(true);
	   // } else {
	   //     setListShownNoAnimation(true);
	   // }
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		
		//sampleCursor.close();
	}
	
}
