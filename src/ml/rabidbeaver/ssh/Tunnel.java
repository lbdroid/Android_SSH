package ml.rabidbeaver.ssh;

import java.io.DataOutputStream;

import android.content.Context;
import android.util.Log;

public class Tunnel {
	private int id, sshport, localport, hostport;
	private boolean reverse;
	private String name, sshhost, host, username, id_public, id_private_path, id_private, uuid;
	private Process tunnelrunner = null;
	
	public Tunnel (int id, String name, String sshhost, int sshport, int localport, String host,
			int hostport, String username, boolean reverse, String id_public, String id_private_path,
			String id_private, String uuid){
		this.id=id;
		this.sshport=sshport;
		this.localport=localport;
		this.hostport=hostport;
		this.reverse=reverse;
		this.name=name;
		this.sshhost=sshhost;
		this.host=host;
		this.username=username;
		this.id_public=id_public;
		this.id_private_path=id_private_path;
		this.id_private=id_private;
		this.uuid=uuid;
	}
	
	protected boolean connect(int monport, Context ctx){
		if (FileIO.readFromFile(ctx, id_private_path).length() < 10)
			FileIO.writeToFile(ctx, id_private_path, id_private);
		String cmd = "/system/bin/autossh -M "+Integer.toString(monport)+" -NL "+Integer.toString(localport)+":"+host+":"+Integer.toString(hostport)+" "+username+"@"+sshhost+" -p"+Integer.toString(sshport)+" -i "+ctx.getApplicationInfo().dataDir+"/files/"+id_private_path+" -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no\n";
		Log.d("TUNNEL",cmd);
			
		try {
			tunnelrunner = Runtime.getRuntime().exec("su --context u:r:system_app:s0");
			DataOutputStream outputStream = new DataOutputStream(tunnelrunner.getOutputStream());
		    outputStream.writeBytes(cmd);
		    outputStream.flush();
		    
			//tunnelrunner.waitFor();
		} catch (Exception e) {
			Log.d("TUNNELFRAGMENT-EXCEPTION",e.getLocalizedMessage());
		}
		
		return true;
	}
	
	protected void disconnect(){
		/* TODO!!!!!
		 * Kill the thread with the tunnel in it.
		 */
		//try {
		//	RootShell.closeShell(true);
		//} catch (IOException e) {}
		if (tunnelrunner != null) tunnelrunner.destroy();
	}
	
	protected int getId(){ return id; }
	protected String getName(){ return name; }
	protected String getSSHHost(){ return sshhost; }
	protected int getSSHPort(){ return sshport; }
	protected int getLocalPort(){ return localport; }
	protected String getHost(){ return host; }
	protected int getHostPort(){ return hostport; }
	protected String getUserName(){ return username; }
	protected boolean isReverse(){ return reverse; }
	protected String getIdPub(){ return id_public; }
	protected String getIdPriPath(){ return id_private_path; }
	protected String getIdPri(){ return id_private; }
	protected String getUuid(){ return uuid; }
	
}
