package ml.rabidbeaver.ssh;

import java.io.IOException;

import android.content.Context;
import android.util.Log;

import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootShell.execution.Shell;

public class Tunnel {
	private int id, sshport, localport, hostport;
	private boolean reverse;
	private String name, sshhost, host, username, id_public, id_private_path, id_private, uuid;
	private Shell autossh;
	
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
		
		if (RootShell.isAccessGiven()){
			String cmd = "autossh -M"+Integer.toString(monport)+" -NL "+Integer.toString(localport)+":"+host+":"+Integer.toString(hostport)+" "+username+"@"+sshhost+" -p"+Integer.toString(sshport)+" -i "+ctx.getApplicationInfo().dataDir+"/files/"+id_private_path;
			Log.d("TUNNEL",cmd);
			Command command = new Command(0, cmd){
				@Override
			    public void commandCompleted(int id, int exitcode) {
					// command is completed....
					Log.d("TUNNEL","command completed");
				}
				@Override
			    public void commandTerminated(int id, String reason) {
					// command is terminated....
					Log.d("TUNNEL","command terminated");
				}
			};
			try {
				autossh = RootShell.getShell(true);
				autossh.add(command);
			} catch (Exception e) { Log.d("EXCEPTION",e.getLocalizedMessage()); return false; }
		}
		
		return true;
	}
	
	protected void disconnect(){
		/* TODO!!!!!
		 * Kill the thread with the tunnel in it.
		 */
		try {
			autossh.close();
		} catch (IOException e) {}
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
