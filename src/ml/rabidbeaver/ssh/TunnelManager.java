package ml.rabidbeaver.ssh;

import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TunnelManager extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "tunnels.db";
	
	public TunnelManager(Context context){
    	super(context, DATABASE_NAME, null, DATABASE_VERSION);
    	this.getWritableDatabase();
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		/*
		 * id: autoincrement id to keep database synchronization,
		 * name: user set "readable" name for the connection,
		 * sshhost: remote ssh server,
		 * sshport: port remote ssh server is listening on, default 22,
		 * localport: port on localhost to tunnel to remote,
		 * host: host on remote network to tunnel to, must be accessible
		 *   by ssh server,
		 * hostport: port on target host to tunnel to,
		 * username: obvious,
		 * reverse: whether this tunnel should open a listening port on
		 *   remote host and tunnel back to here.
		 * id_public_path: the path to the public key file,
		 * id_public: backup copy of the public key in case the file is
		 *   destroyed or corrupted.
		 * uuid: this will be used to make sure that client applications
		 *   always use the expected tunnel. 
		 */
		String create = "CREATE TABLE tunnels (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ "name VARCHAR, sshhost VARCHAR, sshport INTEGER, localport INTEGER, host VARCHAR, "
				+ "hostport INTEGER, username VARCHAR, reverse INTEGER, id_public_path VARCHAR, "
				+ "id_public VARCHAR, id_private_path VARCHAR, id_private VARCHAR, uuid VARCHAR);";
		db.execSQL(create);
		// later on in a later version, we can add in a second table "tunnel_auth" to indicate which
		// processes are authorized to open and hold which tunnels.
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch (oldVersion){
		// nothing to do yet, since this is the first version.
		}
	}
	
	protected Tunnel getTunnel(String uuid){
		SQLiteDatabase db = this.getReadableDatabase();
		Tunnel t = null;
		// SELECT all_the_things FROM tunnels WHERE uuid=uuid
		Cursor c = db.query("tunnels", new String[]{"id", "name", "sshhost", "sshport", "localport",
				"host", "hostport", "username", "reverse", "id_public_path", "id_public", "id_private_path", "id_private"}, "uuid = ?",
				new String[]{uuid}, null, null, null);
		if (c != null && c.getCount() > 0){
			c.moveToFirst();
			t = new Tunnel(c.getInt(0), c.getString(1), c.getString(2), c.getInt(3), c.getInt(4), c.getString(5),
					c.getInt(6), c.getString(7), c.getInt(8)>0, c.getString(9), c.getString(10), c.getString(11), c.getString(12), uuid);
			c.close();
		}
		return t;
	}
	
	protected Tunnel[] getTunnels(){
		SQLiteDatabase db = this.getReadableDatabase();
		Tunnel[] t = new Tunnel[0];
		// SELECT all_the_things FROM tunnels
		Cursor c = db.query("tunnels", new String[]{"id", "name", "sshhost", "sshport", "localport",
				"host", "hostport", "username", "reverse", "id_public_path", "id_public", "id_private_path", "id_private", "uuid"}, null,
				null, null, null, null);
		if (c != null && c.getCount() > 0){
			t = new Tunnel[c.getCount()];
			c.moveToFirst();
			do {
				t[c.getPosition()] = new Tunnel(c.getInt(0), c.getString(1), c.getString(2), c.getInt(3), c.getInt(4), c.getString(5),
						c.getInt(6), c.getString(7), c.getInt(8)>0, c.getString(9), c.getString(10), c.getString(11), c.getString(12), c.getString(13));
			} while (c.moveToNext());
			c.close();
		}
		return t;
	}
	
	protected Tunnel addOrUpdateTunnel(String name, String sshhost, int sshport, int localport, String host,
			int hostport, String username, boolean reverse, String id_public_path, String id_public,
			String id_private_path, String id_private, String uuid){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("name", name);
		values.put("sshhost", sshhost);
		values.put("sshport", sshport);
		values.put("localport", localport);
		values.put("host", host);
		values.put("hostport", hostport);
		values.put("username", username);
		values.put("reverse", reverse?1:0);
		values.put("id_public", id_public);
		values.put("id_private", id_private);
		if (uuid == null){
			// add new tunnel
			String newuuid = UUID.randomUUID().toString();
			values.put("uuid", newuuid);
			values.put("id_public_path", newuuid+".pub");
			values.put("id_private_path", newuuid);
			if (db.insert("tunnels", null, values) >= 0) return getTunnel(newuuid);
		} else {
			// update tunnel with uuid
			values.put("id_public_path", id_public_path);
			values.put("id_private_path", id_private_path);
			db.update("tunnels", values, "uuid = ?", new String[]{uuid});
			return getTunnel(uuid);
		}
		return null;
	}
}
