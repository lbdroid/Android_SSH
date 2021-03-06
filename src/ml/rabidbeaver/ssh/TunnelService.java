package ml.rabidbeaver.ssh;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.execution.Command;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

public class TunnelService extends Service {
	
	public static final int MSG_HOLDOPEN_TUNNEL = 1;
	public static final int MSG_DROP_TUNNEL = 2;
	public static final int MSG_DROP_ALL_TUNNELS = 3;
	
	public static final int COLOR_RED = 0;
	public static final int COLOR_YELLOW = 1;
	public static final int COLOR_GREEN = 2;
	
	public int monport = 20000;
	
	private List<Tunnel> tunnels = new ArrayList<Tunnel>();
	private List<BoundProcess> processes = new ArrayList<BoundProcess>();
	private List<String> lockedTunnels = new ArrayList<String>();
	private final IncomingHandler mHandler = new IncomingHandler(this);
	private final Messenger mMessenger = new Messenger(mHandler);
	
	private static boolean isBound = false;
	private static boolean isStarted = false;
	private static TunnelService runningInstance = null;
	
	private static class IncomingHandler extends Handler {
		private final WeakReference<TunnelService> mService;
		IncomingHandler(TunnelService service) {
	        mService = new WeakReference<TunnelService>(service);
	    }
		
		@Override
        public void handleMessage(Message msg) {
			TunnelService service = mService.get();
			if (service != null) service.handleMessage(msg);
			else super.handleMessage(msg);
		}
	}
	
	private void handleMessage(Message msg){
		String uuid;
		int pid;
		switch(msg.what){
		case MSG_HOLDOPEN_TUNNEL:
			uuid = ((Bundle)msg.obj).getString("uuid");
			pid = msg.arg1;
			
			// If this process is already holding this tunnel, don't add again.
			for (int i=0; i<processes.size(); i++){
				if (processes.get(i).uuid.equals(uuid) && processes.get(i).pid == pid && processes.get(i).uid == msg.sendingUid) break;
			}
			
			// Add this process to the hold list;
			processes.add(new BoundProcess(uuid, pid, msg.sendingUid));
			
			startTunnel(uuid);

			break;
		case MSG_DROP_TUNNEL:
			uuid = ((Bundle)msg.obj).getString("uuid");
			pid = msg.arg1;
			int count = 0, pos = -1;
			for (int i=0; i<processes.size(); i++){
				if (processes.get(i).uuid.equals(uuid)){
					count++;
					if (processes.get(i).pid == pid && processes.get(i).uid == msg.sendingUid) pos=i;
				}
			}
			if (pos >= 0) processes.remove(pos);
			if (count == 1 && !isLocked(uuid)) stopTunnel(uuid);
			break;
		case MSG_DROP_ALL_TUNNELS:
			pid = msg.arg1;
			for (int i=0; i<processes.size(); i++)
				if (processes.get(i).pid == pid && processes.get(i).uid == msg.sendingUid){
					processes.remove(i);
					i--;
				}
			cleanupTunnels(false);
			break;
		}
	}
	
	private void startTunnel(String uuid){
		// If this tunnel is already present, return.
		for (int i=0; i<tunnels.size(); i++)
			if (tunnels.get(i).getUuid().equals(uuid)) return;
		TunnelManager tm = new TunnelManager(this);
		Tunnel t = tm.getTunnel(uuid);
		tm.close();
		tunnels.add(t);
		t.connect(monport, this);
		monport += 2;
	}
	
	private void stopTunnel(String uuid){
		for (int i=0; i<tunnels.size(); i++){
			Tunnel t = tunnels.get(i);
			if (t.getUuid().equals(uuid)){
				t.disconnect(this);
				tunnels.remove(i);
				break;
			}
		}
	}
	
	private void cleanupTunnels(boolean killall){
		for (int i=0; i<tunnels.size(); i++){
			boolean found = false;
			for (int j=0; j<processes.size(); j++)
				if (processes.get(j).uuid.equals(tunnels.get(i).getUuid())){
					found = true;
					break;
				}
			if (!found && (killall || !isLocked(tunnels.get(i).getUuid()))){
				stopTunnel(tunnels.get(i).getUuid());
				i--;
			}
		}
	}
	
	private class BoundProcess {
		public String uuid;
		public int pid;
		public int uid;
		public BoundProcess(String uuid, int pid, int uid){
			this.uuid=uuid; this.pid=pid; this.uid=uid;
		}
	}
	
	public static boolean isRunning(){
		return isBound||isStarted;
	}
	
	public static TunnelService getInstance(){
		return runningInstance;
	}
	
	public void lockTunnel(String Uuid){
		Log.d("TUNNELSERVICE","LOCKING TUNNEL:"+Uuid);
		lockedTunnels.add(Uuid);
		startTunnel(Uuid);
		for (int i=0; i<tunnels.size(); i++)
			if (tunnels.get(i).equals(Uuid))
				tunnels.get(i).manual = true;
	}
	
	public void unlockTunnel(String Uuid){
		Log.d("TUNNELSERVICE","UNLOCKING TUNNEL:"+Uuid);
		for (int i=0; i<lockedTunnels.size(); i++)
			if (lockedTunnels.get(i).equals(Uuid)){
				lockedTunnels.remove(i);
				i--;
			}
		for (int i=0; i<tunnels.size(); i++)
			if (tunnels.get(i).equals(Uuid))
				tunnels.get(i).manual = false;
		stopTunnel(Uuid);
	}
	
	public boolean isLocked(String Uuid){
		boolean isLocked = false;
		for (int i=0; i<lockedTunnels.size(); i++)
			if (lockedTunnels.get(i).equals(Uuid)){
				isLocked=true;
				break;
			}
		if (isLocked) Log.d("TUNNELSERVICE","LOCKED:"+Uuid);
		else Log.d("TUNNELSERVICE","NOTLOCKED:"+Uuid);
		return isLocked;
	}
	
	public boolean isManual(){
		return lockedTunnels.size() > 0;
	}
	
	public int getColor(String Uuid){
		for (int i=0; i<lockedTunnels.size(); i++)
			if (lockedTunnels.get(i).equals(Uuid))
				return COLOR_GREEN;
		for (int i=0; i<tunnels.size(); i++)
			if (tunnels.get(i).getUuid().equals(Uuid))
				return COLOR_YELLOW;
		return COLOR_RED;
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d("TUNNELSERVICE","BINDING");
		isBound = true;
		runningInstance = this;
		return mMessenger.getBinder();
	}
	
	public void killAllTunnels(){
		if (RootShell.isAccessGiven()){
			Command command = new Command(0,"for FILE in "+this.getApplicationInfo().dataDir+"/files/*.pid; do PID=`cat \"$FILE\"`; kill -SIGINT $PID; done");
			try {
				RootShell.getShell(true).add(command);
				RootShell.closeShell(true);
			} catch (Exception e) {}
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		// there shouldn't be any tunnels, but if there are it means something
		// bad happened, kill them all.
		killAllTunnels();
	}

	@Override
    public void onDestroy() {
		super.onDestroy();
		Log.d("TUNNELSERVICE","Running onDestroy()");
		cleanupTunnels(true);
		// for good measure, kill all the tunnels
		killAllTunnels();
    }
	
	@Override
	public boolean onUnbind(Intent intent){
		Log.d("TUNNELSERVICE","unbinding");
		isBound = false;
		if (!isStarted) runningInstance = null;
		return false;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
		isStarted = true;
		runningInstance = this;
		return START_STICKY;
	}
}
