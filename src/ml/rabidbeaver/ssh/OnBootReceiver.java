package ml.rabidbeaver.ssh;

import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.execution.Command;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OnBootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String s = FileIO.readFromFile(context,"onboot.conf");
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
							Log.d("STARTING SSHD",line);
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
}
