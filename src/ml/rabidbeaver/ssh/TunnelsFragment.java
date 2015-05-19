package ml.rabidbeaver.ssh;

import android.annotation.TargetApi;
import android.graphics.Outline;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageButton;
import android.widget.TextView;

public class TunnelsFragment extends Fragment {
	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter<TunnelsAdapter.ViewHolder> mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
	
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.tunnels,container,false);
		
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
			public void onClick(View v) {
				// TODO What to do to create a new ssh tunnel.
			}
	    });
		
		mRecyclerView = (RecyclerView) v.findViewById(R.id.tunnels_list);
		mRecyclerView.setHasFixedSize(true);
		
		mLayoutManager = new LinearLayoutManager(v.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        
        //TODO: change this string array to something like an object list
        String[] mStringArray = {"a","b","c"};
        mAdapter = new TunnelsAdapter(mStringArray);
        
        mRecyclerView.setAdapter(mAdapter);
		
		return v;
	}
	
	private class TunnelsAdapter extends RecyclerView.Adapter<TunnelsAdapter.ViewHolder> {
	    private String[] mDataset;
	    public class ViewHolder extends RecyclerView.ViewHolder {
	        public CardView mCardView;
	        public ViewHolder(CardView v) {
	            super(v);
	            mCardView = v;
	        }
	    }
	    
	    // TODO: obviously, this will have to be updated when we switch to object list.
	    public TunnelsAdapter(String[] myDataset) {
	        mDataset = myDataset;
	    }

	    @Override
	    public TunnelsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
	        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tunnel_view, parent, false);
	        ViewHolder vh = new ViewHolder((CardView)v);
	        return vh;
	    }

	    @Override
	    public void onBindViewHolder(ViewHolder holder, int position) {
	    	((TextView)holder.mCardView.findViewById(R.id.card_text)).setText(mDataset[position]);
	    	// TODO: setup the cardview here
	    }

	    @Override
	    public int getItemCount() {
	        return mDataset.length;
	    }
	}  
}