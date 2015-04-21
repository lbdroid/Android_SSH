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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class SettingsFragment extends Fragment {
	private CheckBox cbox;

	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.settings,container,false);
		
        ((Button)v.findViewById(R.id.install_safe)).setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v){
				copyAssets("bin");
				copyAssets("etc");
				
				if (RootShell.isAccessGiven()){
					// do root stuff here
					if (RootShell.isBusyboxAvailable()){
						String binpath=getActivity().getApplication().getApplicationInfo().dataDir+"/bin";
						String libpath=getActivity().getApplication().getApplicationInfo().dataDir+"/lib";
						String etcpath=getActivity().getApplication().getApplicationInfo().dataDir+"/etc";
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
						Toast.makeText(getActivity().getApplication().getApplicationContext(), "Unable to proceed, busybox not installed.", Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(getActivity().getApplication().getApplicationContext(), "Unable to proceed, root not available.", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		((Button)v.findViewById(R.id.install_dangerous)).setEnabled(false);
		
	    String s = readFromFile("onboot.conf",v.getContext());
	    cbox = (CheckBox)v.findViewById(R.id.launch);
	    if (s.contains("true")) cbox.setChecked(true);
	    cbox.setOnClickListener(new CheckBox.OnClickListener(){
			@Override
			public void onClick(View v) {
				CheckBox c = (CheckBox)v;
				if (c.isChecked()) writeToFile("onboot.conf","true",v.getContext());
				else writeToFile("onboot.conf","false",v.getContext());
			}
	    });
		
		return v;
	}
	
	private String readFromFile(String filename, Context ctx) {
	    String ret = "";
	    try {
	    	InputStream inputStream = ctx.openFileInput(filename);

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

	private void writeToFile(String filename, String data, Context ctx) {
	    try {
	        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(ctx.openFileOutput(filename, Context.MODE_PRIVATE));
	        outputStreamWriter.write(data);
	        outputStreamWriter.close();
	    }
	    catch (IOException e) {
	        Log.e("Exception", "File write failed: " + e.toString());
	    } 
	}
	
	private void copyAssets(String dir) {
	    AssetManager assetManager = getActivity().getApplication().getAssets();
	    String[] files = null;
	    try {
	        files = assetManager.list(dir);
	    } catch (IOException e) {
	        Log.e("tag", "Failed to get asset file list.", e);
	    }
	    File path = new File(getActivity().getApplication().getApplicationInfo().dataDir+"/"+dir);
	    if (!path.exists()) path.mkdirs();
	    for(String filename : files) {
	        InputStream in = null;
	        OutputStream out = null;
	        File outFile = new File(getActivity().getApplication().getApplicationInfo().dataDir+"/"+dir+"/"+filename);
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