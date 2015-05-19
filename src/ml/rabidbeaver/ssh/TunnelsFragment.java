package ml.rabidbeaver.ssh;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class TunnelsFragment extends Fragment {
	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter<TunnelsAdapter.ViewHolder> mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
	
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.tunnels,container,false);
		
		mRecyclerView = (RecyclerView) v.findViewById(R.id.tunnels_list);
		mRecyclerView.setHasFixedSize(true);
		
		mLayoutManager = new LinearLayoutManager(v.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        
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
	    public TunnelsAdapter(String[] myDataset) {
	        mDataset = myDataset;
	    }

	    // Create new views (invoked by the layout manager)
	    @Override
	    public TunnelsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
	        // create a new view
	        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tunnel_view, parent, false);
	        // set the view's size, margins, paddings and layout parameters
	        //...
	        ViewHolder vh = new ViewHolder((CardView)v);
	        return vh;
	    }

	    // Replace the contents of a view (invoked by the layout manager)
	    @Override
	    public void onBindViewHolder(ViewHolder holder, int position) {
	        // - get element from your dataset at this position
	        // - replace the contents of the view with that element

	    	((TextView)holder.mCardView.findViewById(R.id.card_text)).setText(mDataset[position]);

	    }

	    @Override
	    public int getItemCount() {
	        return mDataset.length;
	    }
	}  
}