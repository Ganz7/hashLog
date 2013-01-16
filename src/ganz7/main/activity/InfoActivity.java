/**
 * Activity for displaying the about page of the app
 */

package ganz7.main.activity;

import ganz7.hash.view.CreateLogActivity;
import ganz7.hash.view.SearchActivity;
import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

public class InfoActivity extends SherlockActivity
{
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

	// This method is called once the menu item is selected
	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		// We have only one menu option
		case R.id.home:
			returnToActivity();
			overridePendingTransition (R.anim.right_in, R.anim.right_out);
			break;
		case R.id.newlog:
			Intent myIntent = new Intent(this,CreateLogActivity.class);
			startActivity(myIntent);
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
	
	public void returnToActivity()
	{
		Intent myIntent = new Intent();
		setResult(RESULT_OK, myIntent);
		
		finish();
	}
	
	public void onBackPressed()
	{
		super.onBackPressed();
		overridePendingTransition (R.anim.right_in, R.anim.right_out);
	}
}