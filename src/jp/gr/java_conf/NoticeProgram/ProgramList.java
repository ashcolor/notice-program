package jp.gr.java_conf.NoticeProgram;

import java.io.*;
import java.util.HashMap;
import android.util.Log;
import android.content.Context;

class ProgramList extends HashMap<String,Program> implements Serializable{

	static final String FILE_NAME = "ProgramList.bat";

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
	public ProgramList Load(Context context){
		ProgramList data = this;
		try {
			//FileInputStream inFile = new FileInputStream(FILE_NAME);
			FileInputStream inFile = context.openFileInput(FILE_NAME);
			ObjectInputStream inObject = new ObjectInputStream(inFile);
			data = (ProgramList)inObject.readObject();
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