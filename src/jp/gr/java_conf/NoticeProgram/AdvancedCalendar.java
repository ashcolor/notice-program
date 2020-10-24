package jp.gr.java_conf.NoticeProgram;

import java.util.Calendar;

import android.util.Log;

public class AdvancedCalendar{
	static String[] dayofweek = {"","“ú","Œ","‰Î","…","–Ø","‹à","“y"};
	
	static String printCalendar(Calendar calendar){
    	String s = calendar.get(Calendar.YEAR)+"”N"
    			+(calendar.get(Calendar.MONTH)+1)+"Œ"
    			+calendar.get(Calendar.DAY_OF_MONTH)+"“ú"
    			+calendar.get(Calendar.HOUR)+""+
    			calendar.get(Calendar.MINUTE)+"•ª"+
    			dayofweek[calendar.get(Calendar.DAY_OF_WEEK)]+"—j“ú";
		return s;
	}
	
	static String printDate(Calendar c,long pretime){
    	String s =
    			c.get(Calendar.YEAR)+"”N"
    			+(c.get(Calendar.MONTH)+1)+"/"
    			+c.get(Calendar.DAY_OF_MONTH)+"("
    			+dayofweek[c.get(Calendar.DAY_OF_WEEK)]+")";
    	return s;
	}
	
	static String printTime(Calendar c,long pretime){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(c.getTimeInMillis() - pretime);
		
		String ampm = printAMPM(c);
		
		int h = calendar.get(Calendar.HOUR);
		String hour = h + "";
		if(h < 10)hour = "0" + h;
		
		int m = calendar.get(Calendar.MINUTE);
		String minute = m + "";
		if(m < 10)minute = "0" + m;
		
    	String s = ampm + hour +":" + minute;
		return s;
	}
	
	static String printCalendar(Calendar c,long pretime){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(c.getTimeInMillis() - pretime);
    	String s =
//    			calendar.get(Calendar.YEAR)+"”N"
    			(calendar.get(Calendar.MONTH)+1)+"/"
    			+calendar.get(Calendar.DAY_OF_MONTH)+"("
    			+dayofweek[calendar.get(Calendar.DAY_OF_WEEK)]+") "
    			+printTime(c,pretime);
		return s;
	}
	
	static String printAMPM(Calendar c){
		if(c.get(Calendar.AM_PM)==Calendar.AM){
			return "Œß‘O";
		}else{
			return "ŒßŒã";
		}
	}
	
	static String printWeek(boolean[] activeday){
		String s = "";
		for(int i=0;i<7;i++){
			if(activeday[i]){
				s = s + dayofweek[((i+1)%7)+1];
			}
		}
		return s;
	}
	
	public static Calendar getNextAlarmDay(Calendar calendar,boolean[] activedayofweek,boolean once){
		Calendar calendarNow = Calendar.getInstance();
		Calendar c = calendar;
		if(!once){
			c.set(calendarNow.get(Calendar.YEAR), calendarNow.get(Calendar.MONTH),calendarNow.get(Calendar.DAY_OF_MONTH));
			int nextday = AdvancedCalendar.checkNextDay(c,activedayofweek);
			c.add(Calendar.DAY_OF_MONTH,nextday);
		}
		return c;
	}
	
	
	public static int checkNextDay(Calendar calendar,boolean[] activedayofweek){
        Calendar calendarNow = Calendar.getInstance();
        for(int i=0;i<8;i++){
        	if(activedayofweek[(calendarNow.get(Calendar.DAY_OF_WEEK)+5+i)%7]){
        		//¡“ú‚ÌŠÔ‚ğ‰ß‚¬‚Ä‚¢‚½‚ç
                int diff = calendarNow.compareTo(calendar);
                //¡“ú‚ªİ’è—j“ú‚Å‚àŠÔ‚ğ‰ß‚¬‚Ä‚½‚ç‚Æ‚Î‚·
        		if(i==0&&diff>=0){}
        		else{return i;}
        	}
        }
        return -1;
	}
}