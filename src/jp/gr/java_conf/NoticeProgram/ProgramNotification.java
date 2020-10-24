package jp.gr.java_conf.NoticeProgram;


import android.content.Context;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.util.Log;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.net.Uri;
import android.widget.Button;
import java.util.Calendar;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.support.v4.app.NotificationCompat;


public class ProgramNotification extends BroadcastReceiver {  
	
	private NotificationManager mManager;
  
    @Override  
    public void onReceive(Context context, Intent intent) {  
    	
		Bundle extras = intent.getExtras();
		if (extras != null) {
			Program program = (Program)intent.getSerializableExtra("Program");
			int hour = program.getCalendar().get(Calendar.HOUR);
			int min = program.getCalendar().get(Calendar.MINUTE);
			boolean vib = (boolean)intent.getBooleanExtra("Vib",false);
		    Notification n = new Notification(); // Notification�̐���
		    String s = hour+"��"+min+"������w"+program.getTitle()+"�x���J�n���܂�";
		    n.icon = R.drawable.ic_launcher; // �A�C�R���̐ݒ�
		    n.tickerText = s; // ���b�Z�[�W�̐ݒ�
		 
		    Intent i = new Intent(context,Main.class);
	        //�A���[���̏㏑����h��
	        intent.setType(program.getTitle());  
		    PendingIntent pi = PendingIntent.getActivity(context, 0, i, 0);

		    n.setLatestEventInfo(context.getApplicationContext(), "�ԑg�A���[��",s, pi);
		    if(vib){
		        Log.d("vib","true");  
		    	n.defaults = Notification.DEFAULT_VIBRATE;
		    }else{
		        Log.d("vib","false");  
		    	n.vibrate = new long[]{0,0,0};
		    }
		 
		    // NotificationManager�̃C���X�^���X�擾
		    NotificationManager nm =
		            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		    int number = intent.getExtras().getInt("Number");
		    nm.notify(number, n); // �ݒ肵��Notification��ʒm���� ///////////////////////////�ʒm���㏑������Ȃ�

	        Log.d("number",number + "");  
	        Log.d("AlarmReceiver",hour+"��"+min+"������w"+program.getTitle()+"�x���J�n���܂�");  
		}
    }
    
	//vib�����o��
    public boolean loadVib( Context context ){
        // �v���t�@�����X�̏��� //
        SharedPreferences pref = context.getSharedPreferences("Preferences",Context.MODE_PRIVATE );
        boolean vib= pref.getBoolean("vib",false);
        return vib;
    }

}  