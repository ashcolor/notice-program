package jp.gr.java_conf.NoticeProgram;

import java.util.*;
import java.io.Serializable;
import java.sql.Time;
import java.util.Calendar;

class Program implements Serializable{
	private String title;
	private boolean once;
	private boolean[] dayofweek;//0‚ª“ú—j
	private Calendar calendar = Calendar.getInstance();
	private String category;
	private String station;
	private boolean active = true;
	
	//‚P‰ñ
	Program(String title,Calendar calendar,String category,String station){
		this.title = title;
		once = true;
		this.calendar = calendar;
		this.category = category;
		this.station = station;
	}
	
	//ŒJ‚è•Ô‚µ
	Program(String title,boolean dow[],Calendar calendar,String category,String station){
		this.title = title;
		once = false;
		dayofweek = new boolean[7];
		for(int i=0;i<7;i++){
			dayofweek[i]=dow[i];
		}
		this.calendar = calendar;
		this.category = category;
		this.station = station;
	}
	

	public String getTitle() {
		return title;
	}

	public boolean isOnce() {
		return once;
	}


	public boolean isDayofweek(int i) {
		return dayofweek[i];
	}


	public boolean[] getDayofweek() {
		return dayofweek;
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public String getCategory() {
		return category;
	}

	public String getStation() {
		return station;
	}

	public boolean isActive() {
		return active;
	}
	
	void setActive(boolean b){
		active = b;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setOnce(boolean once) {
		this.once = once;
	}

	public void setDayofweek(boolean[] dayofweek) {
		this.dayofweek = dayofweek;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setStation(String station) {
		this.station = station;
	}
}