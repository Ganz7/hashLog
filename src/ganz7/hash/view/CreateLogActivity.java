/**
 * This activity creates a log.
 * @author Ganz7
 *
 */
package ganz7.hash.view;

import ganz7.hash.database.LogsDataSource;
import ganz7.main.activity.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class CreateLogActivity extends Activity
{
	private LogsDataSource dataaccess;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_edit);
		
		//The database handler object of MySQLiteHelper
		dataaccess = new LogsDataSource(this);
		dataaccess.open();
		
		
	}
	/**
	 * Calls save function or just discards the note depending on the button clicked
	 * @param view
	 */
	public void myClickHandler(View view)
	{
		switch(view.getId())
		{
			case R.id.addlog:
				EditText Comment_value = (EditText) findViewById(R.id.logmake);
				if(Comment_value.getText().toString().length() == 0)
				{
					Toast.makeText(getApplicationContext(),"Seriously?", Toast.LENGTH_SHORT).show();
				}
				else
				{
					String comments = Comment_value.getText().toString();
					//comment = 
							dataaccess.createLog(comments);
							dataaccess.extractTagsForList(comments,0);
					
				}
				Comment_value.setText("");
				returnActivity();
				overridePendingTransition (R.anim.right_in, R.anim.right_out);
				
				break;
			case R.id.cancellog:
				EditText Clear_field = (EditText) findViewById(R.id.logmake);
				Clear_field.setText("");
				returnActivity();
				overridePendingTransition (R.anim.right_in, R.anim.right_out);
				
				break;
			case R.id.addhash:
				EditText addHash = (EditText) findViewById(R.id.logmake);
				int pos = addHash.getSelectionStart();
				String currentContents = addHash.getText().toString();
				//currentContents = StringBuffer(currentContents).insert(pos, "C").toString();
				currentContents = currentContents.substring(0,pos) + "#" + currentContents.substring(pos,currentContents.length());
				addHash.setText(currentContents);
				addHash.setSelection(currentContents.length());
				
				break;
		}
		
	}
	/**
	 * returns activity result
	 */
	public void returnActivity()
	{
		Intent myIntent = new Intent();
		setResult(RESULT_OK, myIntent);
		if(dataaccess != null)
			dataaccess.close();
		finish();
	}
	/*
	 * For animation effects
	 * (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	public void onBackPressed()
	{
		super.onBackPressed();
		overridePendingTransition (R.anim.right_in, R.anim.right_out);
	}
	/**
	 * Closes the db on exit
	 */
	public void onDestroy()
	{
		super.onDestroy();
		if(dataaccess != null)
			dataaccess.close();
	}
	
}
