package ml.rabidbeaver.ssh;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

public class SettingsFragment extends Fragment {
	private CheckBox cbox;

	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.settings,container,false);
		
        ((Button)v.findViewById(R.id.install_safe)).setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v){
				FileIO.install(v.getContext(), false);
			}
		});
		
		((Button)v.findViewById(R.id.install_dangerous)).setOnClickListener(new Button.OnClickListener(){//.setEnabled(false);
			@Override
			public void onClick(final View v) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
				alertDialogBuilder.setTitle(R.string.danger_heading);
				alertDialogBuilder.setMessage(R.string.danger_warning);

				alertDialogBuilder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						FileIO.install(v.getContext(), true);
					}
				});

				alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) { dialog.cancel(); }
				});
				alertDialogBuilder.create().show();
			}
		});
		
	    String s = FileIO.readFromFile(v.getContext(),"onboot.conf");
	    cbox = (CheckBox)v.findViewById(R.id.launch);
	    if (s.contains("true")) cbox.setChecked(true);
	    cbox.setOnClickListener(new CheckBox.OnClickListener(){
			@Override
			public void onClick(View v) {
				CheckBox c = (CheckBox)v;
				if (c.isChecked()) FileIO.writeToFile(v.getContext(),"onboot.conf","true");
				else FileIO.writeToFile(v.getContext(),"onboot.conf","false");
			}
	    });
	    
	    ((Button)v.findViewById(R.id.check_install)).setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(final View v){
				new Thread(new Runnable() {
					public void run(){
						if (FileIO.assetsInstalled(v.getContext(),"bin") && FileIO.assetsInstalled(v.getContext(),"etc") && FileIO.libsInstalled(v.getContext())){
							if (FileIO.assetsCurrent(v.getContext(),"bin") && FileIO.libsCurrent(v.getContext())){
								// Assets installed and current
								getActivity().runOnUiThread(new Runnable(){
									public void run() {
										getActivity().findViewById(R.id.stateoutput).setVisibility(View.VISIBLE);
										getActivity().findViewById(R.id.system_state_uptodate).setVisibility(View.VISIBLE);
										getActivity().findViewById(R.id.system_state_outofdate).setVisibility(View.GONE);
										getActivity().findViewById(R.id.system_state_notinstalled).setVisibility(View.GONE);
									}
								});
							} else {
								// Assets installed, but out of date
								getActivity().runOnUiThread(new Runnable(){
									public void run() {
										getActivity().findViewById(R.id.stateoutput).setVisibility(View.VISIBLE);
										getActivity().findViewById(R.id.system_state_uptodate).setVisibility(View.GONE);
										getActivity().findViewById(R.id.system_state_outofdate).setVisibility(View.VISIBLE);
										getActivity().findViewById(R.id.system_state_notinstalled).setVisibility(View.GONE);
									}
								});
							}
						} else {
							// Assets not installed
							getActivity().runOnUiThread(new Runnable(){
								public void run() {
									getActivity().findViewById(R.id.stateoutput).setVisibility(View.VISIBLE);
									getActivity().findViewById(R.id.system_state_uptodate).setVisibility(View.GONE);
									getActivity().findViewById(R.id.system_state_outofdate).setVisibility(View.GONE);
									getActivity().findViewById(R.id.system_state_notinstalled).setVisibility(View.VISIBLE);
								}
							});
						}
					}
				}).start();
			}
	    });
		
		return v;
	}
}