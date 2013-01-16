package ganz7.main.activity;

import ganz7.hash.database.SQLiteHandler;
import ganz7.hash.paneview.PaneHomeActivity;
import ganz7.hash.view.CreateLogActivity;
import ganz7.hash.view.RecentsActivity;
import ganz7.hash.view.SearchActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
/**
 * The main activity. Called when the app is started.
 * Has links to all other activities.
 * @author Ganz7
 *
 */
public class HashLogActivity extends SherlockActivity 
{
	SQLiteHandler DBHelper;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    
    	
    	super.onCreate(savedInstanceState);
        //setHomeButtonEnabled(true);
        
        
        setContentView(R.layout.dash_home);
        
        
        
        //Toast.makeText(getApplicationContext(),"Oops"+theme, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Handles the click on the various icons present
     * @param view
     */
    public void myClickHandler(View view)
    {
    	
    	switch(view.getId())
    	{
    			case R.id.imageview1:
    			
    				Intent myIntent = new Intent(view.getContext(), CreateLogActivity.class);
    	    		startActivityForResult(myIntent, 0);
    	    		overridePendingTransition (R.anim.right_in, R.anim.right_out);
    			break;
    			
    			case R.id.imageview3:
        			
    				Intent myIntent3 = new Intent(view.getContext(), SearchActivity.class);
    	    		startActivity(myIntent3);
    	    		overridePendingTransition (R.anim.right_in, R.anim.right_out);
    	    	
    	    		 
    			break;
    			case R.id.imageview2:
    			{
    				
    			
    				Intent myIntent1 = new Intent(view.getContext(), RecentsActivity.class);
    	    		startActivity(myIntent1);
    	    		overridePendingTransition (R.anim.right_in, R.anim.right_out);
    	    		
    	    		break;
    			}
    			case R.id.imageview4:
    			{
    				
    			
    				Intent myIntent2 = new Intent(view.getContext(), PaneHomeActivity.class);
    	    		startActivity(myIntent2);
    	    		overridePendingTransition (R.anim.right_in, R.anim.right_out);
    	    		
    	    		break;
    			}
    			
    			/*case R.id.imageview5:
        			
    				Intent myIntent4 = new Intent(view.getContext(), .class);
    	    		startActivity(myIntent4);
    	    		overridePendingTransition (R.anim.right_in, R.anim.right_out);
    			break;*/
    		
    	}
    }
    
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.homemenu, menu);
		return true;
	}

	// This method is called once the menu is selected
	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		// We have 3 menu options
		case R.id.backup:
			backUp();
			break;
		case R.id.restore:
			restore();
			break;
		case R.id.about:
			Intent mySearch = new Intent(this,InfoActivity.class);
			startActivity(mySearch);
			overridePendingTransition (R.anim.right_in, R.anim.right_out);
			break;

		}
		return true;
	}
    
    
    @Override
    public void onDestroy()
    {
    	super.onDestroy();
    	System.runFinalizersOnExit(true);
    }
    @Override
    public void onBackPressed()
    {
    	super.onBackPressed();
    	System.runFinalizersOnExit(true);
    }
    void restore()
    {
    	
    	try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canRead()) {
            	//File saveDir = new File("//data//hashLogBackup");
            	
                String currentDBPath = "//data//"+ "ganz7.main.activity" +"//databases//"+"hashlogdb.db";
                String backupDBPath = "//hashlogdb.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                    FileChannel src = new FileInputStream(backupDB).getChannel();
                    FileChannel dst = new FileOutputStream(currentDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Toast.makeText(getBaseContext(), "Woo Hoo! Database Restored!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getBaseContext(), "Exiting application now to update database!", Toast.LENGTH_SHORT).show();
                    finish();

            }
        } catch (Exception e) {
        	e.printStackTrace();
            Toast.makeText(getBaseContext(), "Oops! Database Restore - Failed!", Toast.LENGTH_LONG).show();


        }
    	
    }
    void backUp()
    {
    	try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
            	File saveDir = new File("//hashLogBackUp");
            	if(!saveDir.exists())
            	{
            		saveDir.mkdirs();
            	}
                String currentDBPath = "//data//"+ "ganz7.main.activity" +"//databases//"+"hashlogdb.db";
                String backupDBPath = "//hashlogdb.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Toast.makeText(getBaseContext(), "Woo Hoo! Database Backed Up!", Toast.LENGTH_LONG).show();
                    
            }
            else{
            	Toast.makeText(getBaseContext(), "Oh oh! Not able to write to SD Card.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
        	e.printStackTrace();
            Toast.makeText(getBaseContext(),"Oops! Seems something is wrong.", Toast.LENGTH_LONG).show();


        }
	
    }
    
    
    
}