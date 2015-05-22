package ml.rabidbeaver.ssh;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/*
 * Example activity to request a hold on an ssh tunnel.
 * The tunnel is uniquely identified by its Uuid.
 */

public class ExampleBinder extends Activity {
    private Messenger mService = null;
    private boolean mBound = false;
    private Message mMessage = null;
    private String exampleUuid = "40ce9059-d60e-4ca6-ba2d-80f223b92ff0";
	
    // There is no need to bind until an actual tunnel is required, but the service may not be
    // available immediately when requested. Create the message, and if the service is not bound
    // store it to be sent when the service really *is* bound.
	public void bindToService(){
		Message msg = Message.obtain(null, TunnelService.MSG_HOLDOPEN_TUNNEL, android.os.Process.myPid(), 0, exampleUuid);
		if (!mBound){
			bindService(new Intent(this, TunnelService.class), mConnection, Context.BIND_AUTO_CREATE);
			mMessage = msg;
		} else {
			try {
				mService.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	// We are going to make a request to the Service that it drop all of our tunnel holds.
	// This step is technically not required, since the service will perform housecleaning
	// periodically, but in the interest of being friendly neighbors, we will say that we
	// are leaving first.
	public void unBindFromService(){
		if (mBound){			
    		Message msg = Message.obtain(null, TunnelService.MSG_DROP_ALL_TUNNELS, android.os.Process.myPid(), 0);
    		try {
				mService.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    		unbindService(mConnection);
    		mBound=false;
    	}
	}
	
	// actual connection to the service. Note that it sends that message that was waiting.
	private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
        	Log.d("TUNNELFRAGMENT","setting up service");
            mService = new Messenger(service);
            mBound = true;
            if (mMessage != null){
            	try {
					mService.send(mMessage);
					mMessage = null;
				} catch (RemoteException e) {
					e.printStackTrace();
				}
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            mBound = false;
        }
    };
}
