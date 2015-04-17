package ml.rabidbeaver.ssh;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.execution.Command;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Main extends ActionBarActivity {
	private Toolbar toolbar;
	private EditText authorized_keys;
	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerl;
	private FragmentManager fManager;
	private AboutFragment aboutFragment;
	private SettingsFragment settingsFragment;
	private RelativeLayout optionsList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		fManager = getSupportFragmentManager();
		
		toolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        
        drawerl = (DrawerLayout)findViewById(R.id.main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this,drawerl,toolbar,R.string.drawer_open,R.string.drawer_close){
            public void onDrawerClosed(View view){
                invalidateOptionsMenu(); //creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView){
                invalidateOptionsMenu(); //creates call to onPrepareOptionsMenu()
            }
        };
        //drawerToggle.setDrawerIndicatorEnabled(true);

/*        drawerl.post(new Runnable() {
            @Override
            public void run() {
                drawerToggle.syncState();
            }
        });*/
        
        drawerl.setDrawerListener(drawerToggle);

        optionsList = (RelativeLayout)findViewById(R.id.options_drawer);
        
		LinearLayout aboutOption = (LinearLayout)optionsList.findViewById(R.id.about_drawer);
		aboutOption.setOnClickListener(new LinearLayout.OnClickListener(){
			public void onClick(View v){
				selectDrawerItem(0);
			}
		});
        
		LinearLayout settingsOption = (LinearLayout)optionsList.findViewById(R.id.settings_drawer);
		settingsOption.setOnClickListener(new LinearLayout.OnClickListener(){
			public void onClick(View v){
				selectDrawerItem(1);
			}
		});
		
		((Button)findViewById(R.id.startsshd)).setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v){
				if (RootShell.isAccessGiven()){
					// do root stuff here
					if (RootShell.isBusyboxAvailable()){
						Command command = new Command(0,
								"killall -9 sshd",
								"sleep 1",
								"/system/bin/sshd"
						){
							@Override
							public void commandOutput(int id, String line){
								Log.d("MAIN STARTSSHD",line);
							}
						};
						try {
							RootShell.getShell(true).add(command);
							RootShell.closeShell(true);
						} catch (Exception e) {}
					} else {
						Toast.makeText(getApplicationContext(), "Unable to proceed, busybox not installed.", Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(getApplicationContext(), "Unable to proceed, root not available.", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		((Button)findViewById(R.id.stopsshd)).setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v){
				if (RootShell.isAccessGiven()){
					// do root stuff here
					if (RootShell.isBusyboxAvailable()){
						Command command = new Command(0,
								"killall -9 sshd"
						){
							@Override
							public void commandOutput(int id, String line){
								Log.d("MAIN STOPSSHD",line);
							}
						};
						try {
							RootShell.getShell(true).add(command);
							RootShell.closeShell(true);
						} catch (Exception e) {}
					} else {
						Toast.makeText(getApplicationContext(), "Unable to proceed, busybox not installed.", Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(getApplicationContext(), "Unable to proceed, root not available.", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		((Button)findViewById(R.id.restartsshd)).setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v){
				if (RootShell.isAccessGiven()){
					// do root stuff here
					if (RootShell.isBusyboxAvailable()){
						Command command = new Command(0,
								"killall -9 sshd",
								"sleep 2",
								"/system/bin/sshd"
						){
							@Override
							public void commandOutput(int id, String line){
								Log.d("MAIN RESTARTSSHD",line);
							}
						};
						try {
							RootShell.getShell(true).add(command);
							RootShell.closeShell(true);
						} catch (Exception e) {}
					} else {
						Toast.makeText(getApplicationContext(), "Unable to proceed, busybox not installed.", Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(getApplicationContext(), "Unable to proceed, root not available.", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		authorized_keys = (EditText)findViewById(R.id.auth_keys);
		authorized_keys.setText(readFromFile("authorized_keys"));
		
		((Button)findViewById(R.id.set_keys)).setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v){
				writeToFile("authorized_keys",authorized_keys.getText().toString());
				String filespath=getApplicationInfo().dataDir+"/files";
				if (RootShell.isAccessGiven()){
					// do root stuff here
					if (RootShell.isBusyboxAvailable()){
						Command command = new Command(0,
								"busybox rm -f /data/.ssh/authorized_keys",
								"busybox cp "+filespath+"/authorized_keys /data/.ssh/",
								"busybox chown 0.0 /data/.ssh/authorized_keys",
								"busybox chmod 600  /data/.ssh/authorized_keys"
						){
							@Override
							public void commandOutput(int id, String line){
								Log.d("MAIN INSTALL AUTHORIZED_KEYS",line);
							}
						};
						try {
							RootShell.getShell(true).add(command);
							RootShell.closeShell(true);
						} catch (Exception e) {}
					} else {
						Toast.makeText(getApplicationContext(), "Unable to proceed, busybox not installed.", Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(getApplicationContext(), "Unable to proceed, root not available.", Toast.LENGTH_LONG).show();
				}
			}
		});
		EditText ipfield = (EditText)findViewById(R.id.ip_field);
		ipfield.setText(shellexec("busybox ifconfig"));
	}
	
	public String shellexec(String command) {
		StringBuffer output = new StringBuffer();
		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			boolean firstline = true;
			while ((line = reader.readLine())!= null) {
				if (line.contains("inet addr") && !line.contains("127.0.0.1")){ // grep
					if (!firstline) output.append("\n");
					output.append(line.split(":")[1].split(" ")[0]);// + "\n"); // cut
					firstline=false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String response = output.toString();
		return response;
	}
	
	private String readFromFile(String filename) {
	    String ret = "";
	    try {
	    	InputStream inputStream = openFileInput(filename);

	        if ( inputStream != null ) {
	            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	            String receiveString = "";
	            StringBuilder stringBuilder = new StringBuilder();
	            while ( (receiveString = bufferedReader.readLine()) != null ) {
	                stringBuilder.append(receiveString);
	            }
	            inputStream.close();
	            ret = stringBuilder.toString();
	        }
	    }
	    catch (FileNotFoundException e) {
	        Log.e("login activity", "File not found: " + e.toString());
	    } catch (IOException e) {
	        Log.e("login activity", "Can not read file: " + e.toString());
	    }

	    return ret;
	}

	private void writeToFile(String filename, String data) {
	    try {
	        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(filename, Context.MODE_PRIVATE));
	        outputStreamWriter.write(data);
	        outputStreamWriter.close();
	    }
	    catch (IOException e) {
	        Log.e("Exception", "File write failed: " + e.toString());
	    } 
	}
	
	private void selectDrawerItem(int option){
		
		Log.d("SELECTDRAWERITEM",Integer.toString(option));
		drawerl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		drawerToggle.setDrawerIndicatorEnabled(false);
		
		if(fManager != null){
			if(option == 0){
				if (aboutFragment == null) aboutFragment = new AboutFragment();
				FragmentTransaction transaction = fManager.beginTransaction();
				transaction.add(R.id.content_container,aboutFragment).addToBackStack(null).commit();
				
				toolbar.setTitle("About");
			} else if(option == 1){
				if (settingsFragment == null) settingsFragment = new SettingsFragment();
				FragmentTransaction transaction = fManager.beginTransaction();
				transaction.add(R.id.content_container,settingsFragment).addToBackStack(null).commit();
				
				toolbar.setTitle("Settings");
			}
		}
		
		//drawerl.closeDrawer(optionsList);
    }
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        if(drawerToggle != null){
        	drawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        if(drawerToggle != null){
        	drawerToggle.onConfigurationChanged(newConfig);
        }
    }
    
	@Override
	public void onBackPressed(){
		super.onBackPressed();
		if(toolbar != null)toolbar.setTitle("RabidBeaver SSH");
		drawerl.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		drawerToggle.setDrawerIndicatorEnabled(true);
	}
}