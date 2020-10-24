package jp.gr.java_conf.NoticeProgram;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.HashMap;
import android.content.Context;

class Settings implements Serializable{
	
	static final String FILE_NAME = "Setting.bat";
	int serviceNumber = 0;
	int pretime = 0;
	boolean vib;

	public int getServiceNumber() {
		return serviceNumber;
	}

	public void setServiceNumber(int serviceNumber) {
		this.serviceNumber = serviceNumber;
	}

	public int getPretime() {
		return pretime;
	}

	public boolean isVib() {
		return vib;
	}

	public void setPretime(int pretime) {
		this.pretime = pretime;
	}

	public void setVib(boolean vib) {
		this.vib = vib;
	}

	//データ保存
	public int Save(Context context){
		try {
			//FileOutputStream outFile = new FileOutputStream(FILE_NAME);
			FileOutputStream outFile = context.openFileOutput(FILE_NAME, 0);
			ObjectOutputStream outObject = new ObjectOutputStream(outFile);
			outObject.writeObject(this);
			outObject.close();
			outFile.close();
		} catch (FileNotFoundException e) {
//			LogMng.PrintException(e, "");
		} catch (IOException e) {
//			LogMng.PrintException(e, "");
		}
		return 0;
	}
	
	//データ読み込み
	public Settings Load(Context context){
		Settings data = this;
		try {
			//FileInputStream inFile = new FileInputStream(FILE_NAME);
			FileInputStream inFile = context.openFileInput(FILE_NAME);
			ObjectInputStream inObject = new ObjectInputStream(inFile);
			data = (Settings)inObject.readObject();
			inObject.close();
			inFile.close();
		} catch (FileNotFoundException e) {
//			LogMng.PrintException(e, "");
		} catch (StreamCorruptedException e) {
//			LogMng.PrintException(e, "");
		} catch (IOException e) {
//			LogMng.PrintException(e, "");
		} catch (ClassNotFoundException e) {
//			LogMng.PrintException(e, "");
		}	
		return data;
	}
}