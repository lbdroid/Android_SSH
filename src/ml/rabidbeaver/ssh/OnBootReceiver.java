package ml.rabidbeaver.ssh;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.execution.Command;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OnBootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String s = readFromFile("onboot.conf",context);
		if (s.contains("true")){
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
				}
			}
		}
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
}
