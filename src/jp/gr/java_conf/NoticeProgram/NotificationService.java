package jp.gr.java_conf.NoticeProgram;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import java.io.File;
import android.app.PendingIntent;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import android.app.AlarmManager;
import java.util.*;
import android.os.Handler;
import android.app.Service;
import android.os.IBinder;

/**
 * This is an example of service that will update its status bar balloon 
 * every 5 seconds for a minute.
 * 
 */
public class NotificationService extends Service {

	static private String ProgramListFileName = "ProgramList.bat";
	static private String SettingsListFileName = "Setting.bat";
	private Settings se;
	private ProgramList pl;
	List<String> pllist;
	int p = 0;
	int number = -1; 
	private Timer timer;
	private boolean active = false;
	AlarmManager alarmManager ;
	final static String TAG = "MyService";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");
		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(!active){
			active = true;
			timer = new Timer(true);
			final Handler handler = new Handler();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					handler.post(new Runnable() {
						public void run() {
							loadProgramList();
							loadSettings();
							getData();
							setNotificationAlarm();
						}
					});
				}
			}, 10000, AlarmManager.INTERVAL_HOUR);
			//super.onStart(intent, startId);
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
	}

	//番組リスト読み込み
	private void loadProgramList(){
		pl = new ProgramList();
		File file = this.getFileStreamPath(ProgramListFileName);  
		if(file.exists()){
			pl = pl.Load(this);
		}else{
			pl.Save(this);
		}
	}

	//Settings読み込み
	private void loadSettings(){
		se = new Settings();
		File file = this.getFileStreamPath(SettingsListFileName);  
		if(file.exists()){
			se = se.Load(this);
		}else{
			se.Save(this);
		}
	}

	public void getData(){
		Set<String> set = pl.keySet();
		set = pl.keySet();
		pllist = new ArrayList<String>(set);
		Collections.sort(pllist, new Comparator<String>() {
			public int compare(String s1, String s2) {
				return s1.compareTo(s2);
			}
		});
	}

	//通知設定
	public void setNotificationAlarm(){
		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		for(int i=0;i<pllist.size();i++){
			Program program = pl.get(pllist.get(i));
			Intent intent = new Intent(this, ProgramNotification.class);
			intent.putExtra("Program",program);
			//アラームの上書きを防ぐ
			intent.setType(program.getTitle());  
			intent.putExtra("Number",number++);
			intent.putExtra("Vib",loadVib(this));
			PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);  
			Calendar calendar = AdvancedCalendar.getNextAlarmDay(program.getCalendar(),program.getDayofweek(),program.isOnce());
			//通知タイミングが未来なら
			Calendar calendarNow = Calendar.getInstance();
			if(calendarNow.getTimeInMillis()<(calendar.getTimeInMillis()-loadPretime(this))){
				deleteNotificationAlarm(program.getTitle());
		        if(program.isActive()){
			        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()-loadPretime(this), sender);  
		        	Log.d("Start Alarm!",pl.get(pllist.get(i)).getTitle()+"/"+AdvancedCalendar.printCalendar(calendar,loadPretime(this)));
		        }else{
		        	Log.d("Start Alarm!",pl.get(pllist.get(i)).getTitle()+"/isnotActive");       	
		        }
			}
		}
	}

	//通知消去
	public void deleteNotificationAlarm(String title){
		Intent intent = new Intent(this, ProgramNotification.class);
		intent.setType(title);  
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);  
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}

	//pretimeを取り出す
	public int loadPretime( Context context ){
		// プリファレンスの準備 //
		SharedPreferences pref = context.getSharedPreferences( "Preferences", Context.MODE_PRIVATE );
		String strAge = pref.getString("pretime","-1");
		return Integer.parseInt( strAge );
	}

	//vibを取り出す
	public boolean loadVib( Context context ){
		// プリファレンスの準備 //
		SharedPreferences pref = context.getSharedPreferences("Preferences",Context.MODE_PRIVATE );
		boolean vib= pref.getBoolean("vib",false);
		return vib;
	}

}