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
		    Notification n = new Notification(); // Notificationの生成
		    String s = hour+"時"+min+"分から『"+program.getTitle()+"』が開始します";
		    n.icon = R.drawable.ic_launcher; // アイコンの設定
		    n.tickerText = s; // メッセージの設定
		 
		    Intent i = new Intent(context,Main.class);
	        //アラームの上書きを防ぐ
	        intent.setType(program.getTitle());  
		    PendingIntent pi = PendingIntent.getActivity(context, 0, i, 0);

		    n.setLatestEventInfo(context.getApplicationContext(), "番組アラーム",s, pi);
		    if(vib){
		        Log.d("vib","true");  
		    	n.defaults = Notification.DEFAULT_VIBRATE;
		    }else{
		        Log.d("vib","false");  
		    	n.vibrate = new long[]{0,0,0};
		    }
		 
		    // NotificationManagerのインスタンス取得
		    NotificationManager nm =
		            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		    int number = intent.getExtras().getInt("Number");
		    nm.notify(number, n); // 設定したNotificationを通知する ///////////////////////////通知が上書きされない

	        Log.d("number",number + "");  
	        Log.d("AlarmReceiver",hour+"時"+min+"分から『"+program.getTitle()+"』が開始します");  
		}
    }
    
	//vibを取り出す
    public boolean loadVib( Context context ){
        // プリファレンスの準備 //
        SharedPreferences pref = context.getSharedPreferences("Preferences",Context.MODE_PRIVATE );
        boolean vib= pref.getBoolean("vib",false);
        return vib;
    }

}  