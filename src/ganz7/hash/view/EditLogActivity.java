package ganz7.hash.view;

import ganz7.hash.database.LogsDataSource;
import ganz7.hash.database.SQLiteHandler;
import ganz7.main.activity.R;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
/**
 * Opens from either the RecentsActivity or ListAllActivity or SearchACtivity
 * For editing an already created note.
 * @author Ganz7
 *
 */
public class EditLogActivity extends SherlockActivity
{
	private LogsDataSource dataaccess;
	private long row_id;
	private EditText logText;
	String savedContent;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_edit);
		
		//The database handler object of MySQLiteHelper
		dataaccess = new LogsDataSource(this);
		dataaccess.open();
		
		logText = (EditText) findViewById(R.id.logmake);
		logText.setText("");
		
		Bundle extras = getIntent().getExtras(); 
		row_id = extras.getLong("row_id");
		
		//row_id = getIntent().getExtras().getInt("row_id");
		
		populateLog();
	}
	/**
	 * populates the text field with the contents of the selected log
	 */
	private void populateLog()
	{
		String logDate = "Last Modified - ";
		Cursor log = dataaccess.fetchLog(row_id);
		startManagingCursor(log);
		savedContent = log.getString(log.getColumnIndexOrThrow(SQLiteHandler.COLUMN_LOG));
		
		logText.setText(savedContent);
		logText.setSelection(savedContent.length());
		
		
		android.widget.TextView logModify = (android.widget.TextView) findViewById(R.id.lastModified);
		logDate += log.getString(log.getColumnIndexOrThrow(SQLiteHandler.COLUMN_TIME));
		logModify.setText(logDate);
		
		
	}
	/**
	 * saves the edited log to the db
	 */
	private void saveLog()
	{
		String log = logText.getText().toString();
		if(log.length() == 0)
		{
			Toast.makeText(getApplicationContext(),"Restoring saved content...", Toast.LENGTH_SHORT).show();
		}
		else if(log.equals(savedContent))
		{
			
		}
		else //if(log.length() !=0 )
		{
			
			dataaccess.updateNote(row_id, log, savedContent);
			savedContent = log;
		}
		
	}
	
	
	
	@Override
	protected void onStop() {
		super.onStop();
		
		saveLog();
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		populateLog();
	}
	
	@Override
	/**
	 * Cleans up
	 */
	protected void onDestroy() {
		super.onDestroy();
		dataaccess.close();
	}
	/**
	 * Handles whether the note is to be saved or not.
	 * @param view
	 */
	public void myClickHandler(View view)
	{
		switch(view.getId())
		{
			case R.id.addlog:
				
				saveLog();
					
				
				Intent myIntent = new Intent();
				setResult(RESULT_OK, myIntent);
				finish();
				overridePendingTransition (R.anim.right_in, R.anim.right_out);
				
				break;
			case R.id.cancellog:
				
				setResult(RESULT_OK);
				finish();
				
				overridePendingTransition (R.anim.right_in, R.anim.right_out);
				
				break;
			case R.id.addhash:
				EditText addHash = (EditText) findViewById(R.id.logmake);
				int pos = addHash.getSelectionStart();
				String currentContents = addHash.getText().toString();
				currentContents = currentContents.substring(0,pos) + "#" + currentContents.substring(pos,currentContents.length());
				addHash.setText(currentContents);
				addHash.setSelection(currentContents.length());
				
				break;
		}
		
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.sharemenu, menu);
		return true;
	}

	// This method is called once the menu is selected
	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		
		case R.id.ic_share:
			
			String logString = logText.getText().toString();
			
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, logString);
			sendIntent.setType("text/plain");
			startActivity(Intent.createChooser(sendIntent, "Share via"));
			
			overridePendingTransition (R.anim.right_in, R.anim.right_out);
			break;
		}
		return true;
	}
	
	/**
	 * For transition effects
	 */
	public void onBackPressed()
	{
		super.onBackPressed();
		overridePendingTransition (R.anim.right_in, R.anim.right_out);
	}
	
	
}
