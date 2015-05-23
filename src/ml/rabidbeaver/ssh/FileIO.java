package ml.rabidbeaver.ssh;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.execution.Command;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class FileIO {
	public static String readFromFile(Context ctx, String filename) {
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
	
	public static String readFromFile2(Context ctx, String filename){
		FileInputStream fis;
		StringBuffer fileContent = new StringBuffer("");
		byte[] buffer = new byte[1024];
		int n;
		try {
			fis = ctx.openFileInput(filename);
			while ((n = fis.read(buffer)) != -1)
				fileContent.append(new String(buffer, 0, n)); 
		} catch (Exception e) {
			Log.d("FileIO-Exception",e.getMessage());
		}

		return fileContent.toString();
	}

	public static void writeToFile(Context ctx, String filename, String data) {
	    try {
	        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(ctx.openFileOutput(filename, Context.MODE_PRIVATE));
	        outputStreamWriter.write(data);
	        outputStreamWriter.close();
	    }
	    catch (IOException e) {
	        Log.e("Exception", "File write failed: " + e.toString());
	    } 
	}
	
	public static void copyAssets(Context ctx, String dir) {
		AssetManager assetManager = ctx.getAssets();
	    String[] files = null;
	    try {
	        files = assetManager.list(dir);
	    } catch (IOException e) {
	        Log.e("tag", "Failed to get asset file list.", e);
	        return;
	    }
	    File path = new File(ctx.getApplicationInfo().dataDir+"/"+dir);
	    if (!path.exists()) path.mkdirs();
	    for(String filename : files) {
	        InputStream in = null;
	        OutputStream out = null;
	        File outFile = new File(ctx.getApplicationInfo().dataDir+"/"+dir+"/"+filename);
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
	
	public static boolean streamsMatch(InputStream inputStream1, InputStream inputStream2) {
		return calculateMD5(inputStream1).equalsIgnoreCase(calculateMD5(inputStream2));
	}

	private static String calculateMD5(InputStream inputStream) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}

		byte[] buffer = new byte[8192];
		int read;
		try {
			while ((read = inputStream.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}
			byte[] md5sum = digest.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			String output = bigInt.toString(16);
			//Fill to 32 chars
			output = String.format("%32s", output).replace(' ', '0');
			return output;
		} catch (IOException e) {
			throw new RuntimeException("Unable to process file for MD5", e);
		}
	}
	
	public static boolean assetsInstalled(Context ctx, String subpath){
		boolean installed=true;
		AssetManager assetManager = ctx.getAssets();
	    String[] files = null;
	    try {
	        files = assetManager.list(subpath);
	    } catch (IOException e) {
	        Log.e("tag", "Failed to get asset file list.", e);
	        return false;
	    }
	    for(String filename : files) {
	    	File outFile;
	    	if (subpath.equalsIgnoreCase("etc")){
	    		boolean fileExists = false;
	    		
	    		// HACK. Seems to be a defect in the RootShell.exists() function, it only returns true half the time if the file does exists.
	    		// So don't depend on it giving a good answer right away. Run it 5 times, and if any ONE of those runs returns true, use
	    		// that value.
	    		for (int i=0; i<5; i++) if(RootShell.exists("/data/ssh/sshd_config")) fileExists=true;
	    		if (!fileExists) installed=false;
	    	}
	    	else{
	    		outFile = new File("/system/"+subpath+"/"+filename);
	    		if (!outFile.exists() && !outFile.getAbsolutePath().contains("sshd_config")){
	    			installed=false;
	    			break;
	    		}
	    	}
	    }
		return installed;
	}
	
	public static boolean assetsCurrent(Context ctx, String subpath){
		boolean match=false;
		AssetManager assetManager = ctx.getAssets();
	    String[] files = null;
	    try {
	        files = assetManager.list(subpath);
	    } catch (IOException e) {
	        Log.e("tag", "Failed to get asset file list.", e);
	        return false;
	    }
	    for(String filename : files) {
	        InputStream in1 = null;
	        InputStream in2 = null;
	        File inFile2 = new File(ctx.getApplicationInfo().dataDir+"/"+subpath+"/"+filename);
        	try {
        		in1 = assetManager.open(subpath+"/"+filename);
        		in2 = new FileInputStream(inFile2);
        		match = streamsMatch(in1, in2);
        	} catch(IOException e) {
        		Log.e("tag", "Failed to open asset file: " + filename, e);
        	} finally {
        		if (in1 != null) {
        			try {
        				in1.close();
        			} catch (IOException e) {}
        		}
        		if (in2 != null) {
        			try {
        				in2.close();
        			} catch (IOException e) {}
        		}
	        }
	    }

		return match;
	}
	
	public static boolean libsInstalled(Context ctx){
		File libsshFile = new File(ctx.getApplicationInfo().dataDir+"/lib/libssh.so");
		return libsshFile.exists();
	}
	
	public static boolean libsCurrent(Context ctx){
		boolean match=false;
		try {
			InputStream sshIS1 = new FileInputStream(new File(ctx.getApplicationInfo().dataDir+"/lib/libssh.so"));
			InputStream sshIS2 = new FileInputStream(new File("/system/lib/libssh.so"));
			if (streamsMatch(sshIS1, sshIS2)) match=true;
			sshIS1.close();
			sshIS2.close();
		} catch (Exception e) {}
		return match;
	}
	
	public static void install(final Context ctx, boolean danger){
		FileIO.copyAssets(ctx,"bin");
		FileIO.copyAssets(ctx,"etc");
		
		if (RootShell.isAccessGiven()){
			// do root stuff here
			if (RootShell.isBusyboxAvailable()){
				String binpath=ctx.getApplicationInfo().dataDir+"/bin";
				String libpath=ctx.getApplicationInfo().dataDir+"/lib";
				String etcpath=ctx.getApplicationInfo().dataDir+"/etc";
				String copylibs, modlibs;
				if (danger){
					copylibs = "busybox cp -f "+libpath+"/* /system/lib/";
					modlibs = "busybox chmod 644 /system/lib/libssh.so /system/lib/libcrypto.so /system/lib/libssl.so /system/lib/libz.so";
				} else {
					copylibs = "busybox cp -f "+libpath+"/libssh.so /system/lib/";
					modlibs = "busybox chmod 644 /system/lib/libssh.so";
				}
				
				Command command = new Command(0,
						"busybox chmod 755 "+binpath+"/*",
						"busybox mount -o remount,rw /system", // remount system rw
						"busybox rm -rf /data/ssh /data/.ssh", // remove old crap
						"busybox cp -f "+binpath+"/* /system/bin/", // copy binaries to system
						copylibs, // copy shared objects to system
						"busybox chmod 755 /system/bin/gzip /system/bin/openssl"+ // set binary executable permissions
								" /system/bin/scp /system/bin/sftp /system/bin/sftp-server"+
								" /system/bin/ssh /system/bin/ssh-keygen /system/bin/sshd"+
								" /system/bin/start-ssh /system/bin/autossh",
						modlibs, // set shared object read permissions
						"busybox mkdir -p /data/ssh/empty", // create privilege separation directory
						"busybox mkdir /data/.ssh", // create root .ssh directory
						"if [ ! -f /data/ssh/sshd_config ]; then busybox cp "+etcpath+"/sshd_config /data/ssh/; fi", // copy config file to ssh directory
						"if [ ! -f /data/ssh/ssh_host_rsa_key ]; then ssh-keygen -f /data/ssh/ssh_host_rsa_key -N '' -t rsa; fi", // create host rsa key
						"if [ ! -f /data/ssh/ssh_host_dsa_key ]; then ssh-keygen -f /data/ssh/ssh_host_dsa_key -N '' -t dsa; fi", // create host dsa key
						"if [ ! -f /data/.ssh/id_rsa ]; then ssh-keygen -f /data/.ssh/id_rsa -N '' -t rsa; fi" // create root client rsa key
				){
					@Override
					public void commandOutput(int id, String line){
						Log.d("MAIN SU",line);
					}
				};
				try {
					RootShell.getShell(true).add(command);
					((Activity)ctx).runOnUiThread(new Runnable(){
						public void run() {
							((Activity)ctx).findViewById(R.id.stateoutput).setVisibility(View.GONE);
							((Activity)ctx).findViewById(R.id.system_state_uptodate).setVisibility(View.GONE);
							((Activity)ctx).findViewById(R.id.system_state_outofdate).setVisibility(View.GONE);
							((Activity)ctx).findViewById(R.id.system_state_notinstalled).setVisibility(View.GONE);
						}
					});
				} catch (Exception e) {}
			} else {
				Toast.makeText(ctx, "Unable to proceed, busybox not installed.", Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(ctx, "Unable to proceed, root not available.", Toast.LENGTH_LONG).show();
		}
	}
}
