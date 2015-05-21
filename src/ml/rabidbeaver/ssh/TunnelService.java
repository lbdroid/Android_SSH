package ml.rabidbeaver.ssh;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

public class TunnelService extends Service {
	
	public static final int MSG_HOLDOPEN_TUNNEL = 1;
	public static final int MSG_DROP_TUNNEL = 2;
	public static final int MSG_DROP_ALL_TUNNELS = 3;
	
	private List<Tunnel> tunnels = new ArrayList<Tunnel>();
	private List<BoundProcess> processes = new ArrayList<BoundProcess>();
	private final Messenger mMessenger = new Messenger(new IncomingHandler(this));
	
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
			uuid = (String) msg.obj;
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
			uuid = (String) msg.obj;
			pid = msg.arg1;
			int count = 0, pos = -1;
			for (int i=0; i<processes.size(); i++){
				if (processes.get(i).uuid.equals(uuid)){
					count++;
					if (processes.get(i).pid == pid && processes.get(i).uid == msg.sendingUid) pos=i;
				}
			}
			if (pos >= 0) processes.remove(pos);
			if (count == 1) stopTunnel(uuid);
			break;
		case MSG_DROP_ALL_TUNNELS:
			pid = msg.arg1;
			for (int i=0; i<processes.size(); i++)
				if (processes.get(i).pid == pid && processes.get(i).uid == msg.sendingUid){
					processes.remove(i);
					i--;
				}
			cleanupTunnels();
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
		t.connect();
	}
	
	private void stopTunnel(String uuid){
		for (int i=0; i<tunnels.size(); i++){
			Tunnel t = tunnels.get(i);
			if (t.getUuid().equals(uuid)){
				t.disconnect();
				tunnels.remove(i);
				break;
			}
		}
	}
	
	private void cleanupTunnels(){
		for (int i=0; i<tunnels.size(); i++){
			boolean found = false;
			for (int j=0; j<processes.size(); j++)
				if (processes.get(j).uuid.equals(tunnels.get(i).getUuid())){
					found = true;
					break;
				}
			if (!found){
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

	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}

	@Override
    public void onDestroy() {
        Toast.makeText(this, "TunnelService ending.", Toast.LENGTH_SHORT).show();
    }
}
