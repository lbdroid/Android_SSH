package ml.rabidbeaver.ssh;

import android.app.Activity;
import android.os.Bundle;

public class TunnelChooser extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.tunnel_chooser);

		//Intent intent = getIntent();

		//final PrintJobInfo jobInfo = (PrintJobInfo) intent.getParcelableExtra("android.intent.extra.print.PRINT_JOB_INFO");
		
		
		
		/* TODO:
		 * 1) This activity is to be started "for result".
		 * 2) This activity is a chooser -- pick item out of a list.
		 *   -- specifically, pick from a list of ssh tunnels.
		 * 3) return the numerical ID corresponding to the selected ssh tunnel.
		 * 4) disable "click out" method of dismissing dialog.
		 */
	}
}
