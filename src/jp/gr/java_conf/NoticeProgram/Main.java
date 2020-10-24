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
		
		//�ԑg���X�g�ǂݍ���
		loadProgramList();
		//Settings�ǂݍ���
		loadSettings();
				
		//�ԑg���X�g�r���[�쐬
		RefreshList();
		
		//�V�K�쐬�{�^��
		Button NewoneButton = (Button)findViewById(R.id.Newone);
		NewoneButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),Newone.class);
				startActivityForResult(intent,0);
			}
		});
		
	    // adView ���쐬����
	    adView = new AdView(this, AdSize.BANNER, "a151811ddce17e2");

	    // ���� android:id="@+id/mainLayout" ���^�����Ă�����̂Ƃ���
	    // LinearLayout �����b�N�A�b�v����
	    LinearLayout layout = (LinearLayout)findViewById(R.id.AdView);

	    // adView ��ǉ�
	    layout.addView(adView);

	    // ��ʓI�ȃ��N�G�X�g���s���čL����ǂݍ���
	    AdRequest adRequest = new AdRequest();
//	    adRequest.addTestDevice(AdRequest.TEST_EMULATOR);               // �G�~�����[�^
	    adRequest.addTestDevice("9BFD94EC73B95B1151803FD737D8AA5B");    // Android �[�����e�X�g

	    adView.loadAd(adRequest);
		
		//Notification���폜
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
        mNotificationManager.cancelAll();
	}

	//Newone����߂����Ƃ��̓���
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
	    super.onActivityResult(requestCode, resultCode, intent);
	    
	    if(resultCode == RESULT_OK){
		    Bundle extras = intent.getExtras();  
			if (extras != null) {
			    Program program = (Program)intent.getSerializableExtra("Program");
    	    	setProgram(program);
    			pl.Save(this);
    			loadProgramList();
				//�ԑg���X�g�r���[�쐬
    	    	RefreshList();
			}
	    }
	}
	
	//�ԑg���X�g�ǂݍ���
	private void loadProgramList(){
		pl = new ProgramList();
		File file = this.getFileStreamPath(ProgramListFileName);  
		if(file.exists()){
			pl = pl.Load(this);
		}else{
			pl.Save(this);
		}
	}
	
	//Settings�ǂݍ���
	private void loadSettings(){
		se = new Settings();
		File file = this.getFileStreamPath(SettingsListFileName);  
		if(file.exists()){
			se = se.Load(this);
		}else{
			se.Save(this);
		}
	}
	
	//�ԑg���X�g�ɔԑg���Z�b�g
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
	
	//�ԑg���X�g����ԑg���폜
	private void deleteProgram(Program program){
    	if(pl.containsKey(program.getTitle())){
    		pl.remove(program.getTitle());
    	}
		pl.Save(this);
		loadProgramList();
	}
	
	//�ߋ��̔ԑg���폜
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
	
	//�ԑg���X�g��������߂����Ń\�[�g���ă��X�g��Ԃ�
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

	//�ԑg���X�g�ĕ`��
	private void RefreshList(){
		//�ߋ��̔ԑg���폜
		deletePastProgram();
		//�ԑg���X�g��������߂����Ń\�[�g���ă��X�g���擾
		pllist = getListbyNear();
		Log.d("tes",pllist+"");
		Log.d("tes",pllist.size()+"");
		//���X�g�r���[�ݒ�
		ListView listView = (ListView)findViewById(R.id.listView);
		ProgramListAdapter adapter = new ProgramListAdapter(this,R.layout.programlist_layout);
		for(int i=0;i < pllist.size();i++){
			adapter.add(pl.get(pllist.get(i)));
			Log.d("tes",i+"");
		}
        listView.setAdapter(adapter);


        
        //���X�g�N���b�N��
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            	Intent intent = new Intent(Main.this, Newone.class);
	            intent.putExtra("Program",pl.get(pllist.get(position)));
	            startActivityForResult(intent, 0);
            }
        });
        
        //���X�g������
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){
            	p = position;
            	new AlertDialog.Builder(Main.this)
            	.setTitle("�폜���܂����H")
            	.setPositiveButton("�͂�", new DialogInterface.OnClickListener() {
            	    public void onClick(DialogInterface dialog, int whichButton) {
            	        deleteNotificationAlarm(pllist.get(p));
            	        deleteProgram(pl.get(pllist.get(p)));
            	    	RefreshList();
            	    }
            	})
            	.setNegativeButton("������", new DialogInterface.OnClickListener() {
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
			// ����̍s(position)�̃f�[�^�𓾂�
			final Program item = pro.get(position);
			//�C���f�b�N�X�����邩
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


			//�^�C�g��
			TextView titleView;
			titleView = (TextView)view.findViewById(R.id.title);
			titleView.setText(item.getTitle());
			
			//���g
			TextView dataView;
			dataView = (TextView)view.findViewById(R.id.data);
			if(item.isOnce()){
				dataView.setText("�P��");
			}else{
				dataView.setText(AdvancedCalendar.printWeek(item.getDayofweek()) + "/"
						+ AdvancedCalendar.printTime(item.getCalendar(),0));				
			}
			
			//����
			TextView nextView;
			nextView = (TextView)view.findViewById(R.id.next);
			nextView.setText("����:" + AdvancedCalendar.printCalendar(item.getCalendar(), 0));
			
			//ONOFF�{�^��
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
	
	//�g�O���{�^���������ꂽ�Ƃ�
	public void pushActiveButton(Program item,boolean isChecked){
    	(pl.get(item.getTitle())).setActive(isChecked);
        Log.d("ToggleButton",""+isChecked);
		pl.Save(this);
		loadProgramList();
		setNotificationAlarm();
	}
	
	//�ʒm�ݒ�
	public void setNotificationAlarm(){
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		for(int i=0;i<pllist.size();i++){
			Program program = pl.get(pllist.get(i));
			Intent intent = new Intent(Main.this, ProgramNotification.class);
	        intent.putExtra("Program",program);
	        //�A���[���̏㏑����h��
	        intent.setType(program.getTitle());  
	        intent.putExtra("Number",number++);
	        intent.putExtra("Vib",loadVib(this));
	        PendingIntent sender = PendingIntent.getBroadcast(Main.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);  
			Calendar calendar = AdvancedCalendar.getNextAlarmDay(program.getCalendar(),program.getDayofweek(),program.isOnce());
	        //�ʒm�^�C�~���O�������Ȃ�
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
		
	//pretime�����o��
    public int loadPretime( Context context ){
        // �v���t�@�����X�̏��� //
        SharedPreferences pref = context.getSharedPreferences( "Preferences", Context.MODE_PRIVATE );
        String str = pref.getString("pretime","-1");
        return Integer.parseInt( str );
    }
	//vib�����o��
    public boolean loadVib( Context context ){
        // �v���t�@�����X�̏��� //
        SharedPreferences pref = context.getSharedPreferences("Preferences",Context.MODE_PRIVATE );
        boolean vib= pref.getBoolean("vib",false);
        return vib;
    }
    
	//�ʒm����
	public void deleteNotificationAlarm(String title){
		Intent intent = new Intent(Main.this, ProgramNotification.class);
        intent.setType(title);  
        PendingIntent sender = PendingIntent.getBroadcast(Main.this, 0, intent, 0);  
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
	}
	
	//���j���[�{�^���ݒ�
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean ret = super.onCreateOptionsMenu(menu);
		menu.add(0,Menu.FIRST,Menu.NONE,"�ݒ�");
		return ret;
	}
	
	//�ݒ���������Ƃ�
	public boolean onOptionsItemSelected(MenuItem item) {
    	Intent intent = new Intent(Main.this, SettingsActivity.class);
        startActivityForResult(intent, 0);
		return super.onOptionsItemSelected(item);
	}
}







