package ml.rabidbeaver.ssh;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.execution.Command;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Main extends ActionBarActivity {
	private Toolbar toolbar;
	
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
						Command command = new Command(0, "busybox chmod 755 "+binpath+"/*", "busybox mount -o remount,rw /system",
								"busybox cp -f "+binpath+"/* /system/bin/", "busybox cp -f "+libpath+"/libssh.so /system/lib/",
								"busybox chmod 755 /system/bin/gzip /system/bin/openssl /system/bin/scp /system/bin/sftp /system/bin/sftp-server /system/bin/ssh /system/bin/ssh-keygen /system/bin/sshd /system/bin/start-ssh",
								"busybox chmod 644 /system/lib/libssh.so"){
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
		
		((DrawerLayout)findViewById(R.id.main_drawer)).setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
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