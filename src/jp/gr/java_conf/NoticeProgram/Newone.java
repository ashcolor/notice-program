package jp.gr.java_conf.NoticeProgram;

import com.google.ads.*;
import com.google.ads.AdView;
import android.content.Intent;
import java.sql.Time;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.util.Log;
import java.util.Calendar;
import android.widget.Toast;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.content.Context;
import android.view.View;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.view.View.OnKeyListener;
import android.view.View;
import android.view.KeyEvent;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class Newone extends Activity {
	
	private AdView adView;
	private Program program;
	EditText editTitle;
	ToggleButton onceButton;
	ToggleButton[] dayofweekButton;
	TimePicker timePicker;
	DatePicker datePicker;
	private Calendar calendar = Calendar.getInstance();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		setContentView(R.layout.activity_newone);
		
    	editTitle = (EditText)findViewById(R.id.title);
        editTitle.setOnKeyListener(new OnKeyListener() {
        	@Override
        	public boolean onKey(View v, int keyCode, KeyEvent event) {

                       //EnterKey�������ꂽ���𔻒�
        		if (event.getAction() == KeyEvent.ACTION_DOWN
        				&& keyCode == KeyEvent.KEYCODE_ENTER) {

                                //�\�t�g�L�[�{�[�h�����
        			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        			inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        			return true;
        		}
        		return false;
        	}
        });
    	onceButton = (ToggleButton)findViewById(R.id.once);
    	onceButton.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View v) {
        		if(onceButton.isChecked()){
                	for(int i=0;i<7;i++){
                		dayofweekButton[i].setEnabled(false);
                	}
        			datePicker.setEnabled(true);
        		}else{
                	for(int i=0;i<7;i++){
                		dayofweekButton[i].setEnabled(true);
                	}
        			datePicker.setEnabled(false);
        		}
			}	 
		});
    	dayofweekButton = new ToggleButton[7];
    		dayofweekButton[0] = (ToggleButton)findViewById(R.id.mon);
    		dayofweekButton[1] = (ToggleButton)findViewById(R.id.tue);
    		dayofweekButton[2] = (ToggleButton)findViewById(R.id.wed);
    		dayofweekButton[3] = (ToggleButton)findViewById(R.id.thu);
    		dayofweekButton[4] = (ToggleButton)findViewById(R.id.fri);
    		dayofweekButton[5] = (ToggleButton)findViewById(R.id.sat);
    		dayofweekButton[6] = (ToggleButton)findViewById(R.id.sun);
        datePicker = (DatePicker)findViewById(R.id.datepicker);
    	timePicker = (TimePicker)findViewById(R.id.timepicker);
    	
    	//���X�g����̑I���̏ꍇ�l���Z�b�g
    	Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		
		if (extras != null) {
			program = (Program)intent.getSerializableExtra("Program");
        	editTitle.setText(program.getTitle());
        	onceButton.setChecked(program.isOnce());
       		if(onceButton.isChecked()){
        	    datePicker.updateDate(program.getCalendar().get(Calendar.YEAR),program.getCalendar().get(Calendar.MONTH),program.getCalendar().get(Calendar.DATE));       			
       		}else{
            	for(int i=0;i<7;i++){
            		dayofweekButton[i].setChecked(program.isDayofweek(i));
            	}
       		}
    	    timePicker.setCurrentHour(program.getCalendar().get(Calendar.HOUR_OF_DAY));
    	    timePicker.setCurrentMinute(program.getCalendar().get(Calendar.MINUTE));
        }
		//�g�p���Ȃ����ڂ𖳌���
   		if(onceButton.isChecked()){
        	for(int i=0;i<7;i++){
        		dayofweekButton[i].setEnabled(false);
        	}
   		}else{
			datePicker.setEnabled(false);
   		}
		
		//��ʐݒ�
        Button button = (Button) findViewById(R.id.set);    
        button.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View v) {
	        	int year = datePicker.getYear();
	        	int month = datePicker.getMonth();
	        	int day = datePicker.getDayOfMonth();
	        	int hour = timePicker.getCurrentHour();
	        	int min = timePicker.getCurrentMinute();
	        	//�ߋ��̎����ɂȂ��Ă��Ȃ���
        		Calendar calendar1 = Calendar.getInstance();
        		Calendar calendar2 = Calendar.getInstance();
	        	calendar2.set(year,month,day,hour,min);
            	int diff = calendar1.compareTo(calendar2);
            	//��������Ƀ`�F�b�N�������Ă��邩
	        	boolean miss = true;
	        	boolean once = onceButton.isChecked();		
	        	if(onceButton.isChecked()){
	        		miss = false;
	        	}else{
		    		for(int i=0;i<7;i++){
		    			if(dayofweekButton[i].isChecked())miss = false;
		    		}
	        	}
            	if((diff>=0&&onceButton.isChecked())||miss){
                    Toast.makeText(Newone.this, "�ݒ�Ɍ�肪����܂�", Toast.LENGTH_SHORT).show();
            	}else{
		        	String title = editTitle.getText().toString();

		        	String category = "";
		        	String station = "test";
		        	if(once){
		        		program = new Program(title,calendar2,category,station);
		        	}else{
			        	boolean[] dayofweek = new boolean[7];
			    		for(int i=0;i<7;i++){
			    			dayofweek[i]=dayofweekButton[i].isChecked();
			    		}
		        		program = new Program(title,dayofweek,calendar2,category,station);
		        	}
		        	Intent intent = new Intent();
		        	intent.putExtra("Program",program);
		        	setResult(RESULT_OK, intent);
		        	finish();	        	
            	}
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
	}
	

    
    @Override
    public void onDestroy() {
      super.onDestroy();
    }
	
}