package ml.rabidbeaver.ssh;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.execution.Command;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Main extends ActionBarActivity {
	private Toolbar toolbar;
	private EditText authorized_keys;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		toolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		
		((Button)findViewById(R.id.install_safe)).setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v){
				copyAssets("bin");
				copyAssets("etc");
				
				if (RootShell.isAccessGiven()){
					// do root stuff here
					if (RootShell.isBusyboxAvailable()){
						String binpath=getApplicationInfo().dataDir+"/bin";
						String libpath=getApplicationInfo().dataDir+"/lib";
						String etcpath=getApplicationInfo().dataDir+"/etc";
						Command command = new Command(0,
								"busybox chmod 755 "+binpath+"/*",
								"busybox mount -o remount,rw /system", // remount system rw
								"busybox rm -rf /data/ssh /data/.ssh", // remove old crap
								"busybox cp -f "+binpath+"/* /system/bin/", // copy binaries to system
								"busybox cp -f "+libpath+"/libssh.so /system/lib/", // copy shared objects to system
								"busybox chmod 755 /system/bin/gzip /system/bin/openssl"+ // set binary executable permissions
										" /system/bin/scp /system/bin/sftp /system/bin/sftp-server"+
										" /system/bin/ssh /system/bin/ssh-keygen /system/bin/sshd"+
										" /system/bin/start-ssh",
								"busybox chmod 644 /system/lib/libssh.so", // set shared object read permissions
								"busybox mkdir -p /data/ssh/empty", // create privilege separation directory
								"busybox cp "+etcpath+"/sshd_config /data/ssh/", // copy config file to ssh directory
								"ssh-keygen -f /data/ssh/ssh_host_rsa_key -N '' -t rsa", // create host rsa key
								"ssh-keygen -f /data/ssh/ssh_host_dsa_key -N '' -t dsa", // create host dsa key
								"ssh-keygen -f /data/.ssh/id_rsa -N '' -t rsa" // create root client rsa key
						){
							@Override
							public void commandOutput(int id, String line){
								Log.d("MAIN SU",line);
							}
						};
						try {
							RootShell.getShell(true).add(command);
						} catch (Exception e) {}
					} else {
						Toast.makeText(getApplicationContext(), "Unable to proceed, busybox not installed.", Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(getApplicationContext(), "Unable to proceed, root not available.", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		((Button)findViewById(R.id.install_dangerous)).setEnabled(false);
		
		
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
		
		((DrawerLayout)findViewById(R.id.main_drawer)).setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		
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
	
	private void copyAssets(String dir) {
	    AssetManager assetManager = getAssets();
	    String[] files = null;
	    try {
	        files = assetManager.list(dir);
	    } catch (IOException e) {
	        Log.e("tag", "Failed to get asset file list.", e);
	    }
	    File path = new File(getApplicationInfo().dataDir+"/"+dir);
	    if (!path.exists()) path.mkdirs();
	    for(String filename : files) {
	        InputStream in = null;
	        OutputStream out = null;
	        File outFile = new File(getApplicationInfo().dataDir+"/"+dir+"/"+filename);
	        if (!outFile.exists()){
	        	try {
	        		in = assetManager.open(dir+"/"+filename);
	        		out = new FileOutputStream(outFile);
	        		byte[] buffer = new byte[1024];
	        		int read;
	        		while((read = in.read(buffer)) != -1){
	        			out.write(buffer, 0, read);
	        		}
	        	} catch(IOException e) {
	        		Log.e("tag", "Failed to copy asset file: " + filename, e);
	        	} finally {
	        		if (in != null) {
	        			try {
	        				in.close();
	        			} catch (IOException e) {}
	        		}
	        		if (out != null) {
	        			try {
	        				out.close();
	        			} catch (IOException e) {}
	        		}
	        	}
	        }
	    }
	}	
}