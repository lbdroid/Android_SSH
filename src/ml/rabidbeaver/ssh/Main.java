package ml.rabidbeaver.ssh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootShell.execution.Shell;
import com.stericson.RootShell.execution.Shell.ShellContext;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends AppCompatActivity {
	private Toolbar toolbar;
	private EditText authorized_keys;
	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerl;
	private FragmentManager fManager;
	private AboutFragment aboutFragment;
	private SettingsFragment settingsFragment;
	private TunnelFragment tunnelsFragment;
	private RelativeLayout optionsList;
	private boolean pollstate = true;
	private Shell rshell = null;
	private Command cmmd;
	
	private static final int ABOUT = 0;
	private static final int SETTINGS = 1;
	private static final int TUNNELS = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		fManager = getSupportFragmentManager();

		toolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        
        drawerl = (DrawerLayout)findViewById(R.id.main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this,drawerl,toolbar,R.string.drawer_open,R.string.drawer_close){
            public void onDrawerClosed(View view){
                invalidateOptionsMenu(); //creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView){
                invalidateOptionsMenu(); //creates call to onPrepareOptionsMenu()
            }
        };
        drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				fManager.popBackStack();
				if(toolbar != null)toolbar.setTitle(R.string.app_name);
				drawerl.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
				drawerToggle.setDrawerIndicatorEnabled(true);
			}
        });
        
        drawerl.setDrawerListener(drawerToggle);

        optionsList = (RelativeLayout)findViewById(R.id.options_drawer);
        
		LinearLayout aboutOption = (LinearLayout)optionsList.findViewById(R.id.about_drawer);
		aboutOption.setOnClickListener(new LinearLayout.OnClickListener(){
			public void onClick(View v){
				selectDrawerItem(ABOUT);
			}
		});
        
		LinearLayout settingsOption = (LinearLayout)optionsList.findViewById(R.id.settings_drawer);
		settingsOption.setOnClickListener(new LinearLayout.OnClickListener(){
			public void onClick(View v){
				selectDrawerItem(SETTINGS);
			}
		});
		
		LinearLayout tunnelsOption = (LinearLayout)optionsList.findViewById(R.id.tunnels_drawer);
		tunnelsOption.setOnClickListener(new LinearLayout.OnClickListener(){
			public void onClick(View v){
				selectDrawerItem(TUNNELS);
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
		authorized_keys.setText(FileIO.readFromFile(getApplicationContext(),"authorized_keys"));
		
		((Button)findViewById(R.id.set_keys)).setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v){
				FileIO.writeToFile(getApplicationContext(),"authorized_keys",authorized_keys.getText().toString());
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
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    pollstate=false;
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    pollstate=true;
	    
	    if (RootShell.isAccessGiven()){
	    	cmmd = new Command(0,"if ( busybox ps | grep \"/system/bin/sshd\" | grep -v \"grep\" >/dev/null ); then echo \"running\"; else echo \"stopped\"; fi"){
	    		@Override
				public void commandOutput(int id, String line){
	    			Log.d("RabidBeaverSSH-Main",line);
	    			final TextView runview = (TextView) findViewById(R.id.sshd_state_running);
					final TextView stopview = (TextView) findViewById(R.id.sshd_state_stopped);
	    			if (line.contains("running")){
	    				runOnUiThread(new Runnable(){
							public void run() {
								runview.setVisibility(View.VISIBLE);
								stopview.setVisibility(View.GONE);
							}
						});
	    			} else {
	    				runOnUiThread(new Runnable(){
							public void run() {
								runview.setVisibility(View.GONE);
								stopview.setVisibility(View.VISIBLE);
							}
						});
	    			}
				}
	    	};
		new Thread(new Runnable() {
			public void run(){
				
				final EditText ipfield = (EditText)findViewById(R.id.ip_field);
				while (pollstate){
				runOnUiThread(new Runnable(){
					public void run(){
						ipfield.setText(shellgetips());
					}
				});
				try {
					RootShell.getShell(true).add(cmmd);
					RootShell.closeShell(true);
				} catch (Exception e) {}
				//shellsshdrunning();
				
				try {
					Thread.sleep(5000); // sleep for 5 seconds
				} catch (Exception e){}
				}
			}
		}).start();
	    }
	}
	
	public String shellgetips() {
		StringBuffer output = new StringBuffer();
		Process p;
		try {
			p = Runtime.getRuntime().exec("busybox ifconfig");
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
	
	public boolean shellsshdrunning() {
		Process p;
		try {
			//TODO this is getting selinux blocked, so will have to be as root.
			// probably should stop polling, and replace with a "check status" button.
			p = Runtime.getRuntime().exec("busybox ps | grep sshd 2>/dev/null");
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			while ((line = reader.readLine())!= null) {
				if (line.contains("/system/bin/sshd")) return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private void selectDrawerItem(int option){
		drawerl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		drawerToggle.setDrawerIndicatorEnabled(false);
		
		if(fManager != null){
			if(option == ABOUT){
				if (aboutFragment == null) aboutFragment = new AboutFragment();
				fManager.beginTransaction().add(R.id.content_container,aboutFragment).addToBackStack(null).commit();
				toolbar.setTitle(R.string.about);
			} else if(option == SETTINGS){
				if (settingsFragment == null) settingsFragment = new SettingsFragment();
				fManager.beginTransaction().add(R.id.content_container,settingsFragment).addToBackStack(null).commit();
				toolbar.setTitle(R.string.settings);
			} else if(option == TUNNELS){
				if (tunnelsFragment == null) tunnelsFragment = new TunnelFragment();
				fManager.beginTransaction().add(R.id.content_container,tunnelsFragment).addToBackStack(null).commit();
				toolbar.setTitle(R.string.tunnels);
			}
		}
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
		if(toolbar != null)toolbar.setTitle(R.string.app_name);
		drawerl.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		drawerToggle.setDrawerIndicatorEnabled(true);
	}
}