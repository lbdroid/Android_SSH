package ml.rabidbeaver.ssh;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/*
 * Example activity to request a hold on an ssh tunnel.
 * The tunnel is uniquely identified by its Uuid.
 * 
 * To use this service, you must request permission in your manifest;
 * <uses-permission android:name="ml.rabidbeaver.TUNNELBINDER" />
 */

public class ExampleBinder extends Activity {
    private Messenger mService = null;
    private boolean mBound = false;
    private String exampleUuid = "40ce9059-d60e-4ca6-ba2d-80f223b92ff0";
	
    // There is no need to bind until an actual tunnel is required, but the service may not be
    // available immediately when requested. Create the message, and if the service is not bound
    // store it to be sent when the service really *is* bound.
	public void bindToService(){
		if (!mBound){
			Intent intent = new Intent();
			intent.setComponent(new ComponentName("ml.rabidbeaver.ssh", "ml.rabidbeaver.ssh.TunnelService"));
			bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
			// NOTE: bindService is NOT BLOCKING, the service will not already be bound
			// when the call completes.
		}
	}
	
	public void requestTunnel(){
		if (mBound){
			// NOTE: cannot send a string through a Messenger since it is not Parcelable.
			Bundle bundle = new Bundle();
            bundle.putString("uuid", exampleUuid);
			Message msg = Message.obtain(null, 1 /*TunnelService.MSG_HOLDOPEN_TUNNEL*/, android.os.Process.myPid(), 0, bundle);
			try {
				mService.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void dropTunnel(){
		if (mBound){
			Bundle bundle = new Bundle();
            bundle.putString("uuid", exampleUuid);
			Message msg = Message.obtain(null, 2 /*TunnelService.MSG_DROP_TUNNEL*/, android.os.Process.myPid(), 0, bundle);
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
    		Message msg = Message.obtain(null, 3 /*TunnelService.MSG_DROP_ALL_TUNNELS*/, android.os.Process.myPid(), 0);
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
        	// this is where the actual binding happens.
        	Log.d("TUNNELFRAGMENT","service is bound");
            mService = new Messenger(service);
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            mBound = false;
        }
    };
    
    
    /*
     * The below two functions are used to obtain details about a tunnel.
     * It loads a Dialog Activity that presents the list of defined tunnels.
     * The user may choose one and press OK. The onActivityResult function
     * receives the returned data in the form of an intent with three variables
     * defined;
     * 
     * String extra "name",
     * String extra "uuid",
     * int extra "port".
     * 
     * The name is for user recognition.
     * The uuid is unique for every tunnel and must be sent to the tunnel service
     * The port is the local port number that the tunnel will listen on.
     * 
     * To use the tunnel picker dialog requires that you request permission in your
     * manifest:
     * 
     * <uses-permission android:name="ml.rabidbeaver.TUNNELCHOOSER" />
     */
    
    @SuppressWarnings("unused")
	private void getTunnels(){
		Intent intent = new Intent();
		intent.setComponent(new ComponentName("ml.rabidbeaver.ssh", "ml.rabidbeaver.ssh.TunnelChooser"));
		startActivityForResult(intent,0);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && data != null && data.hasExtra("name") && data.hasExtra("uuid") && data.hasExtra("port")) {
			String tunnelName = data.getStringExtra("name");
			String tunnelUuid = data.getStringExtra("uuid");
			int tunnelPort = data.getIntExtra("port", -1);
			Log.d("PRINTEREDIT","Name:"+tunnelName+", Uuid:"+tunnelUuid+", Port:"+Integer.toString(tunnelPort));
		}
    }
}
