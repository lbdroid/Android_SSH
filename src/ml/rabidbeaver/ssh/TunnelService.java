package ml.rabidbeaver.ssh;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/*
 * This service will be brought up and down as needed (self-terminating). The purpose is to manage 3rd party
 * requests and holds on SSH tunnels.
 */
public class TunnelService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
