package mx.app.ambassador.ui.dialogs;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import mx.app.ambassador.R;

public class ProgressDialog extends Dialog {
	
	public ProgressDialog(Context context) {
		super(context);
	}

	public ProgressDialog(Context context, int theme) {
		super(context, theme);
	}


	public void onWindowFocusChanged(boolean hasFocus){
		ImageView imageView = (ImageView) findViewById(R.id.img_spinner);
        AnimationDrawable spinner = (AnimationDrawable) imageView.getBackground();
        spinner.start();
    }
	
	public void setMessage(CharSequence message) {
		if(message != null && message.length() > 0) {
			findViewById(R.id.txt_message).setVisibility(View.VISIBLE);			
			TextView txt = (TextView)findViewById(R.id.txt_message);
			txt.setText(message);
			txt.invalidate();
		}
	}
	
	public static ProgressDialog show(Context context, CharSequence message, boolean indeterminate, boolean cancelable,
			OnCancelListener cancelListener) {
		
		ProgressDialog dialog = new ProgressDialog(context, R.style.dialog_progress);
		dialog.setTitle("");
		dialog.setContentView(R.layout.dialog_progress);
		if(message == null || message.length() == 0) {
			dialog.findViewById(R.id.txt_message).setVisibility(View.GONE);			
		} else {
			TextView txt = (TextView)dialog.findViewById(R.id.txt_message);
			txt.setText(message);
		}
		dialog.setCancelable(cancelable);
		dialog.setOnCancelListener(cancelListener);
		dialog.getWindow().getAttributes().gravity=Gravity.CENTER;
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();  
		lp.dimAmount=0.2f;
		dialog.getWindow().setAttributes(lp); 
		dialog.show();
		return dialog;
	}
	
}
