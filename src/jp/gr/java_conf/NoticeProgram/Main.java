package jp.gr.java_conf.NoticeProgram;

import com.google.ads.*;
import com.google.ads.AdView;
import java.io.File;
import java.io.Serializable;
import android.content.Context;
import android.content.SharedPreferences;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.widget.Button;
import java.util.Calendar;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.widget.ListView;
import java.util.ArrayList;
import android.content.DialogInterface;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.util.*;



public class Main extends Activity implements Serializable{
	
	private AdView adView;
	private ProgramList pl;
	private Settings se;
	List<String> pllist;
	int p = 0;
	int number = 0;
	AlarmManager alarmManager;
	static private String ProgramListFileName = "ProgramList.bat";
	static private String SettingsListFileName = "Setting.bat";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//番組リスト読み込み
		loadProgramList();
		//Settings読み込み
		loadSettings();
				
		//番組リストビュー作成
		RefreshList();
		
		//新規作成ボタン
		Button NewoneButton = (Button)findViewById(R.id.Newone);
		NewoneButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),Newone.class);
				startActivityForResult(intent,0);
			}
		});
		
	    // adView を作成する
	    adView = new AdView(this, AdSize.BANNER, "a151811ddce17e2");

	    // 属性 android:id="@+id/mainLayout" が与えられているものとして
	    // LinearLayout をルックアップする
	    LinearLayout layout = (LinearLayout)findViewById(R.id.AdView);

	    // adView を追加
	    layout.addView(adView);

	    // 一般的なリクエストを行って広告を読み込む
	    AdRequest adRequest = new AdRequest();
//	    adRequest.addTestDevice(AdRequest.TEST_EMULATOR);               // エミュレータ
	    adRequest.addTestDevice("9BFD94EC73B95B1151803FD737D8AA5B");    // Android 端末をテスト

	    adView.loadAd(adRequest);
		
		//Notificationを削除
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
        mNotificationManager.cancelAll();
	}

	//Newoneから戻ったときの動作
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
	    super.onActivityResult(requestCode, resultCode, intent);
	    
	    if(resultCode == RESULT_OK){
		    Bundle extras = intent.getExtras();  
			if (extras != null) {
			    Program program = (Program)intent.getSerializableExtra("Program");
    	    	setProgram(program);
    			pl.Save(this);
    			loadProgramList();
				//番組リストビュー作成
    	    	RefreshList();
			}
	    }
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
	
	//番組リストに番組をセット
	private void setProgram(Program program){
		boolean active;
    	if(pl.containsKey(program.getTitle())){
    		program.setActive(pl.get(program.getTitle()).isActive());
    		pl.remove(program.getTitle());
    	}
		pl.put(program.getTitle(),program);
		pl.Save(this);
		loadProgramList();
	}
	
	//番組リストから番組を削除
	private void deleteProgram(Program program){
    	if(pl.containsKey(program.getTitle())){
    		pl.remove(program.getTitle());
    	}
		pl.Save(this);
		loadProgramList();
	}
	
	//過去の番組を削除
	private void deletePastProgram(){
        Set<String> set = pl.keySet();
        List<String> list = new ArrayList<String>(set);
        for(int i=0;i<list.size();i++){
        	Program program = pl.get(list.get(i));
        	Calendar calendarNow = Calendar.getInstance();
        	Calendar calendar = program.getCalendar();
        	int diff = calendarNow.compareTo(calendar);
        	if(diff>=0&&program.isOnce()){
        		pl.remove(list.get(i));
        	}
        }
	}
	
	//番組リストを放送が近い順でソートしてリストを返す
	private List<String> getListbyNear(){
		Set<String> set = pl.keySet();
		List<String> list = new ArrayList<String>(set);
        Collections.sort(list, new Comparator<String>() {
        	public int compare(String s1, String s2) {
        		Program p1 = pl.get(s1);
        		Calendar c1 = AdvancedCalendar.getNextAlarmDay(p1.getCalendar(),p1.getDayofweek(),p1.isOnce());
        		Program p2 = pl.get(s2);
        		Calendar c2 = AdvancedCalendar.getNextAlarmDay(p2.getCalendar(),p2.getDayofweek(),p2.isOnce());            
                return c1.compareTo(c2);
                }
        });
        return list;
	}

	//番組リスト再描画
	private void RefreshList(){
		//過去の番組を削除
		deletePastProgram();
		//番組リストを放送が近い順でソートしてリストを取得
		pllist = getListbyNear();
		Log.d("tes",pllist+"");
		Log.d("tes",pllist.size()+"");
		//リストビュー設定
		ListView listView = (ListView)findViewById(R.id.listView);
		ProgramListAdapter adapter = new ProgramListAdapter(this,R.layout.programlist_layout);
		for(int i=0;i < pllist.size();i++){
			adapter.add(pl.get(pllist.get(i)));
			Log.d("tes",i+"");
		}
        listView.setAdapter(adapter);


        
        //リストクリック時
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            	Intent intent = new Intent(Main.this, Newone.class);
	            intent.putExtra("Program",pl.get(pllist.get(position)));
	            startActivityForResult(intent, 0);
            }
        });
        
        //リスト長押し
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){
            	p = position;
            	new AlertDialog.Builder(Main.this)
            	.setTitle("削除しますか？")
            	.setPositiveButton("はい", new DialogInterface.OnClickListener() {
            	    public void onClick(DialogInterface dialog, int whichButton) {
            	        deleteNotificationAlarm(pllist.get(p));
            	        deleteProgram(pl.get(pllist.get(p)));
            	    	RefreshList();
            	    }
            	})
            	.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
            	    public void onClick(DialogInterface dialog, int whichButton) {
            	    }
            	})
            	.show();
            	return true;
            }
        });
        setNotificationAlarm();
        startService(new Intent(Main.this,NotificationService.class));
	}
	
	//CustomAdapter
	public class ProgramListAdapter extends ArrayAdapter<Program> {
		private ArrayList<Program> pro = new ArrayList<Program>();
		private LayoutInflater inflater;
	    private int layout;
	    private Calendar indexCalendar = Calendar.getInstance();
	    Calendar c;

		public ProgramListAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
			this.inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
			this.layout = textViewResourceId;
			indexCalendar.set(Calendar.YEAR,2000);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (convertView == null) {
				view = this.inflater.inflate(this.layout, null);
			}
			// 特定の行(position)のデータを得る
			final Program item = pro.get(position);
			//インデックスをつけるか
			LinearLayout layout = (LinearLayout)view.findViewById(R.id.index);
			layout.setVisibility(View.GONE);
			Calendar calendar = AdvancedCalendar.getNextAlarmDay(item.getCalendar(), item.getDayofweek(), item.isOnce());
//			Log.d("",AdvancedCalendar.printCalendar(calendar));
			if(position > 0){
				final Program preitem = pro.get(position-1);
				Calendar precalendar = AdvancedCalendar.getNextAlarmDay(preitem.getCalendar(), preitem.getDayofweek(), preitem.isOnce());

				if(calendar.get(Calendar.YEAR) != precalendar.get(Calendar.YEAR) ||
						calendar.get(Calendar.MONTH) != precalendar.get(Calendar.MONTH) ||
						calendar.get(Calendar.DAY_OF_MONTH) != precalendar.get(Calendar.DAY_OF_MONTH) ){
					Log.d("test",AdvancedCalendar.printCalendar(calendar));
					layout = (LinearLayout)view.findViewById(R.id.index);
					layout.setVisibility(View.VISIBLE);

					TextView indexDate;
					indexDate = (TextView)view.findViewById(R.id.indexDate);
					indexDate.setText(AdvancedCalendar.printDate(calendar,0));
				}
			}else if(position == 0){
				layout = (LinearLayout)view.findViewById(R.id.index);
				layout.setVisibility(View.VISIBLE);

				TextView indexDate;
				indexDate = (TextView)view.findViewById(R.id.indexDate);
				indexDate.setText(AdvancedCalendar.printDate(calendar,0));
			}


			//タイトル
			TextView titleView;
			titleView = (TextView)view.findViewById(R.id.title);
			titleView.setText(item.getTitle());
			
			//中身
			TextView dataView;
			dataView = (TextView)view.findViewById(R.id.data);
			if(item.isOnce()){
				dataView.setText("１回");
			}else{
				dataView.setText(AdvancedCalendar.printWeek(item.getDayofweek()) + "/"
						+ AdvancedCalendar.printTime(item.getCalendar(),0));				
			}
			
			//次回
			TextView nextView;
			nextView = (TextView)view.findViewById(R.id.next);
			nextView.setText("次回:" + AdvancedCalendar.printCalendar(item.getCalendar(), 0));
			
			//ONOFFボタン
			ToggleButton active;
			active = (ToggleButton)view.findViewById(R.id.active);
			active.setChecked(item.isActive());
		    active.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		            Log.d("ToggleButton",item.getTitle());
					pushActiveButton(item,isChecked);
		            Log.d("ToggleButton","call OnCheckdChangeListener");
		        }
		    });
			return view;
		}
		
		@Override
		public void add(Program object) {
			super.add(object);
			this.pro.add(object);
			
		}
		
	}
	
	//トグルボタンが押されたとき
	public void pushActiveButton(Program item,boolean isChecked){
    	(pl.get(item.getTitle())).setActive(isChecked);
        Log.d("ToggleButton",""+isChecked);
		pl.Save(this);
		loadProgramList();
		setNotificationAlarm();
	}
	
	//通知設定
	public void setNotificationAlarm(){
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		for(int i=0;i<pllist.size();i++){
			Program program = pl.get(pllist.get(i));
			Intent intent = new Intent(Main.this, ProgramNotification.class);
	        intent.putExtra("Program",program);
	        //アラームの上書きを防ぐ
	        intent.setType(program.getTitle());  
	        intent.putExtra("Number",number++);
	        intent.putExtra("Vib",loadVib(this));
	        PendingIntent sender = PendingIntent.getBroadcast(Main.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);  
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
		
	//pretimeを取り出す
    public int loadPretime( Context context ){
        // プリファレンスの準備 //
        SharedPreferences pref = context.getSharedPreferences( "Preferences", Context.MODE_PRIVATE );
        String str = pref.getString("pretime","-1");
        return Integer.parseInt( str );
    }
	//vibを取り出す
    public boolean loadVib( Context context ){
        // プリファレンスの準備 //
        SharedPreferences pref = context.getSharedPreferences("Preferences",Context.MODE_PRIVATE );
        boolean vib= pref.getBoolean("vib",false);
        return vib;
    }
    
	//通知消去
	public void deleteNotificationAlarm(String title){
		Intent intent = new Intent(Main.this, ProgramNotification.class);
        intent.setType(title);  
        PendingIntent sender = PendingIntent.getBroadcast(Main.this, 0, intent, 0);  
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
	}
	
	//メニューボタン設定
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean ret = super.onCreateOptionsMenu(menu);
		menu.add(0,Menu.FIRST,Menu.NONE,"設定");
		return ret;
	}
	
	//設定を押したとき
	public boolean onOptionsItemSelected(MenuItem item) {
    	Intent intent = new Intent(Main.this, SettingsActivity.class);
        startActivityForResult(intent, 0);
		return super.onOptionsItemSelected(item);
	}
}







