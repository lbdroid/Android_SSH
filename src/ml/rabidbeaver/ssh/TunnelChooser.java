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
		
        for (int i = 0; i < tunnels.length; i++) {
            RadioButton rdbtn = new RadioButton(this);
            rdbtn.setId(i);
            rdbtn.setText(tunnels[i].getName());
            rg.addView(rdbtn);
            if (i==0) rdbtn.setChecked(true);
        }

        Button b = (Button) findViewById(R.id.okbutton);
		b.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
				int checked = rg.getCheckedRadioButtonId();
                intent.putExtra("name",tunnels[checked].getName());
                intent.putExtra("uuid",tunnels[checked].getUuid());
                setResult(RESULT_OK,intent);  
                finish();
			}
		});
	}
}
