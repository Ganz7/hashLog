package ganz7.hash.paneview;

import ganz7.hash.database.LogsDataSource;
import ganz7.main.activity.R;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;


public class PaneHomeActivity extends SherlockFragmentActivity
{
	public LogsDataSource DBObj;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pane_home);
		DBObj = new LogsDataSource(this);//obj Initialization
		DBObj.openReadableDB();
	}
	LogsDataSource getDB()
	{
		return DBObj;
	}
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		overridePendingTransition (R.anim.right_in, R.anim.right_out);
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if(DBObj != null)
			DBObj.close();
	}
}
