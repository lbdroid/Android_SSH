package ml.rabidbeaver.ssh;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AboutFragment extends Fragment {

	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.about,container,false);
		
		return v;
	}
}