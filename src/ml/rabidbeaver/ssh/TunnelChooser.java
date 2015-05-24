package ml.rabidbeaver.ssh;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class TunnelChooser extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.tunnel_chooser);
		
		final RadioGroup rg = (RadioGroup) findViewById(R.id.tunnelrg);
		TunnelManager tm = new TunnelManager(this);
		final Tunnel[] tunnels = tm.getTunnels();
		RadioButton rdbtn = new RadioButton(this);
		rdbtn.setId(0);
		rdbtn.setText("None");
		rg.addView(rdbtn);
		rdbtn.setChecked(true);
        for (int i = 0; i < tunnels.length; i++) {
            rdbtn = new RadioButton(this);
            rdbtn.setId(i+1);
            rdbtn.setText(tunnels[i].getName());
            rg.addView(rdbtn);
        }

        Button b = (Button) findViewById(R.id.okbutton);
		b.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
				int checked = rg.getCheckedRadioButtonId();
				if (checked == 0){
					intent.putExtra("name","");
					intent.putExtra("uuid","");
					intent.putExtra("port",-1);
				} else {
					intent.putExtra("name",tunnels[checked-1].getName());
					intent.putExtra("uuid",tunnels[checked-1].getUuid());
					intent.putExtra("port", tunnels[checked-1].getLocalPort());
				}
                setResult(RESULT_OK,intent);  
                finish();
			}
		});
	}
}
