package ml.rabidbeaver.ssh;

import java.io.File;

import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.execution.Command;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Outline;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class TunnelsFragment extends Fragment {
	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter<TunnelsAdapter.ViewHolder> mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TunnelManager tunnelManager;
    private Tunnel[] mTunnelArray;
	
	public View onCreateView(final LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.tunnels,container,false);
		tunnelManager = new TunnelManager(v.getContext());
		
		ImageButton fab = (ImageButton) v.findViewById(R.id.add_tunnel_button);
		fab.setOutlineProvider(new ViewOutlineProvider() {
	        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
	        @Override
	        public void getOutline(View view, Outline outline) {
	            int diameter = getResources().getDimensionPixelSize(R.dimen.diameter);
	            outline.setOval(0, 0, diameter, diameter);
	        }
	    });
	    fab.setClipToOutline(true);
	    fab.setOnClickListener(new ImageButton.OnClickListener(){
			@Override
			public void onClick(final View v) {
				// What to do to create a new ssh tunnel.
				// create dialog/form for the tunnel details.
				final View tunnel_form = inflater.inflate(R.layout.tunnel_add_edit, null);
				final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
				builder.setTitle("New Tunnel");
				builder.setPositiveButton("Add Tunnel", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String tunnel_name = ((EditText) tunnel_form.findViewById(R.id.tunnel_name)).getText().toString();
						String tunnel_server = ((EditText) tunnel_form.findViewById(R.id.tunnel_server)).getText().toString();
						int tunnel_server_port = Integer.parseInt(((EditText) tunnel_form.findViewById(R.id.tunnel_server_port)).getText().toString());
						String tunnel_user = ((EditText) tunnel_form.findViewById(R.id.tunnel_user)).getText().toString();
						String tunnel_pubkey = ((EditText) tunnel_form.findViewById(R.id.tunnel_pubkey)).getText().toString();
						String tunnel_prikey = ((EditText) tunnel_form.findViewById(R.id.tunnel_prikey)).getText().toString();
						String tunnel_host = ((EditText) tunnel_form.findViewById(R.id.tunnel_host)).getText().toString();
						int tunnel_host_port = Integer.parseInt(((EditText) tunnel_form.findViewById(R.id.tunnel_host_port)).getText().toString());
						int tunnel_local_port = Integer.parseInt(((EditText) tunnel_form.findViewById(R.id.tunnel_local_port)).getText().toString());
						boolean tunnel_reverse = ((CheckBox) tunnel_form.findViewById(R.id.tunnel_reverse)).isChecked();
						
						if (tunnel_name.length() > 0 && tunnel_server.length() > 0 && tunnel_server_port > 0
								&& tunnel_user.length() > 0 && tunnel_host.length() > 0 && tunnel_host_port > 0
								&& tunnel_local_port > 0 && tunnel_prikey.length() > 0){ // if everything checks out...
							
							tunnelManager.addOrUpdateTunnel(tunnel_name, tunnel_server, tunnel_server_port, tunnel_local_port, tunnel_host,
									tunnel_host_port, tunnel_user, tunnel_reverse, null, tunnel_pubkey, null, tunnel_prikey, null);
							
							// refresh the TunnelsFragment.
							TunnelsFragment.this.refresh();
							
							// copy public key to clipboard
							ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
							ClipData clip = ClipData.newPlainText("Public Key", tunnel_pubkey);
							clipboard.setPrimaryClip(clip);
							
							//close dialog
							dialog.dismiss();
						}
					}
				});
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

				builder.setView(tunnel_form);
				Button genkeys = (Button) tunnel_form.findViewById(R.id.tunnel_genkeys);
				genkeys.setOnClickListener(new Button.OnClickListener(){
					@Override
					public void onClick(final View v) {
						Log.d("TUNNELSFRAGMENT","Pressed GENERATE KEYS button");
						final String tmppath=v.getContext().getApplicationInfo().dataDir+"/files";
						if (RootShell.isAccessGiven()){
							String cmd1 = "/system/bin/ssh-keygen -f "+tmppath+"/tmp-id_rsa -N '' -t rsa";
							String cmd2 = "chmod 666 "+tmppath+"/tmp-id_*";
							String cmd3 = "OWNER=`ls -lad tmppath`";
							String cmd4 = "chown $OWNER.$OWNER "+tmppath+"/*";
							Command command = new Command(0, cmd1, cmd2, cmd3, cmd4){
								@Override
							    public void commandCompleted(int id, int exitcode) {
									String prikey = FileIO.readFromFile(v.getContext(), "tmp-id_rsa");
									Log.d("PRIKEY",prikey);
									String pubkey = FileIO.readFromFile(v.getContext(), "tmp-id_rsa.pub");
									Log.d("PUBKEY",pubkey);
									new File(tmppath+"/tmp-id_rsa").delete();
									new File(tmppath+"/tmp-id_rsa.pub").delete();
									EditText pub_key = (EditText) tunnel_form.findViewById(R.id.tunnel_pubkey);
									EditText pri_key = (EditText) tunnel_form.findViewById(R.id.tunnel_prikey);
									pub_key.setText(pubkey);
									pub_key.setEnabled(false);
									pri_key.setText(prikey);
									pri_key.setEnabled(false);
								}
							};
							try {
								// I don't want to run this as root, but something weird happens and it crashes
								// hard on nexus 9 when run as a normal user. Could be selinux related?
								RootShell.getShell(true).add(command);
								RootShell.closeShell(true);
							} catch (Exception e) { Log.d("EXCEPTION",e.getLocalizedMessage()); }
						}
					}
				});
				
				builder.create().show();
			}
	    });
		
		mRecyclerView = (RecyclerView) v.findViewById(R.id.tunnels_list);
		mRecyclerView.setHasFixedSize(true);
		
		mLayoutManager = new LinearLayoutManager(v.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        
        mTunnelArray = tunnelManager.getTunnels();
        mAdapter = new TunnelsAdapter(mTunnelArray);
        
        mRecyclerView.setAdapter(mAdapter);
		
		return v;
	}
	
	private void refresh(){
		mTunnelArray = tunnelManager.getTunnels();
		((TunnelsAdapter) mAdapter).updateDataSet(mTunnelArray);
	}
	
	private class TunnelsAdapter extends RecyclerView.Adapter<TunnelsAdapter.ViewHolder> {
	    private Tunnel[] tunnels;
	    public class ViewHolder extends RecyclerView.ViewHolder {
	        public CardView mCardView;
	        public ViewHolder(CardView v) {
	            super(v);
	            mCardView = v;
	        }
	    }
	    
	    public TunnelsAdapter(Tunnel[] tunnels) {
	        this.tunnels = tunnels;
	    }
	    
	    public void updateDataSet(Tunnel[] tunnels){
	    	this.tunnels = tunnels;
	    	this.notifyDataSetChanged();
	    }

	    @Override
	    public TunnelsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
	        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tunnel_view, parent, false);
	        ViewHolder vh = new ViewHolder((CardView)v);
	        return vh;
	    }

	    @Override
	    public void onBindViewHolder(ViewHolder holder, int position) {
	    	((TextView)holder.mCardView.findViewById(R.id.card_text)).setText(tunnels[position].getName());
	    	// TODO: setup the cardview here
	    }

	    @Override
	    public int getItemCount() {
	        return tunnels.length;
	    }
	}  
}