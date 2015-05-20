package ml.rabidbeaver.ssh;

public class Tunnel {
	private int id, sshport, localport, hostport;
	private boolean reverse;
	private String name, sshhost, host, username, id_public_path, id_public, id_private_path, id_private, uuid;	
	
	public Tunnel (int id, String name, String sshhost, int sshport, int localport, String host,
			int hostport, String username, boolean reverse, String id_public_path, String id_public,
			String id_private_path, String id_private, String uuid){
		this.id=id;
		this.sshport=sshport;
		this.localport=localport;
		this.hostport=hostport;
		this.reverse=reverse;
		this.name=name;
		this.sshhost=sshhost;
		this.host=host;
		this.username=username;
		this.id_public_path=id_public_path;
		this.id_public=id_public;
		this.id_private_path=id_private_path;
		this.id_private=id_private;
		this.uuid=uuid;
	}
	
	protected boolean connect(){
		/* TODO!!!!!
		 * Probably involve creating a new thread to run the tunnel, maintaining a handle as
		 * a private variable to this class, return the success/failure state of the thread. 
		 */
		
		return true;
	}
	
	protected void disconnect(){
		/* TODO!!!!!
		 * Kill the thread with the tunnel in it.
		 */
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
	protected String getIdPubPath(){ return id_public_path; }
	protected String getIdPub(){ return id_public; }
	protected String getIdPriPath(){ return id_private_path; }
	protected String getIdPri(){ return id_private; }
	protected String getUuid(){ return uuid; }
	
}
