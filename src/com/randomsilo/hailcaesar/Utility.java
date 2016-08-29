package com.randomsilo.hailcaesar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import android.widget.Spinner;

import com.randomsilo.hailcaesar.algorithm.DawsonCipher;
import com.randomsilo.hailcaesar.model.Key;
import com.randomsilo.hailcaesar.model.Lock;
import com.randomsilo.hailcaesar.model.MasterKey;
import com.randomsilo.hailcaesar.model.MasterLock;
import com.randomsilo.hailcaesar.ui.adapter.KeyListSpinnerAdapter;
import com.randomsilo.hailcaesar.ui.adapter.LockListSpinnerAdapter;

public class Utility {
	private static Random random = new Random();
	
	public static String GetTimePrefix() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd_HHmm ", Locale.US);
    	String prefix = sdf.format(Calendar.getInstance().getTime());
    	return prefix;
	}
	
	public static void copy(File src, File dst) throws IOException {
	    InputStream in = new FileInputStream(src);
	    OutputStream out = new FileOutputStream(dst);

	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	}
	
	public static boolean ContainsOnlyPrintableValues(String content) {
		boolean printable = true;
		
		for(int i=0; i<content.length(); i++) {
			char c = content.charAt(i);
			int asciiCode = (int)c;
			if(asciiCode < GlobalSettings.AsciiMin || asciiCode > GlobalSettings.AsciiMax) {
				printable = false;
				break;
			}
		}
		
		return printable;
	}
	
	public static boolean AsciiValidPrintChar(char c) {
		int asciiCode = (int)c;
		return AsciiValidPrintChar(asciiCode);
	}
	
	public static boolean AsciiValidPrintChar(int asciiCode) {
		boolean printable = true;
		
		if(asciiCode < GlobalSettings.AsciiMin || asciiCode > GlobalSettings.AsciiMax) {
			printable = false;
		}
		
		return printable;
	}
	
	public static int RandomAsciiCode() throws Exception {
		int asciiCode = random.nextInt(GlobalSettings.AsciiMax);
		if(asciiCode < GlobalSettings.AsciiMin) {
			asciiCode = asciiCode + GlobalSettings.AsciiMin;
		}
		return asciiCode;
	}
	
	public static char RandomAsciiCharacter() throws Exception {
		int asciiCode = RandomAsciiCode();
		char c = (char)asciiCode;
		
		if(asciiCode < GlobalSettings.AsciiMin || asciiCode > GlobalSettings.AsciiMax) {
			throw new Exception("Random Ascii Code: " + asciiCode + " or character: " + c + " is not valid.");
		}

		return c;
	}
	
	public static String ReadFile(File file) throws Exception {
	    BufferedReader reader = new BufferedReader( new FileReader (file));
	    String line = null;
	    StringBuilder stringBuilder = new StringBuilder();

	    while(( line = reader.readLine()) != null ) {
	        stringBuilder.append( line );
	    }
	    reader.close();
	    String data = stringBuilder.toString();
	    String decrypted = DawsonCipher.decrypt(MasterLock.getMasterLock(), MasterKey.getMasterKey(), data);
	    
	    return decrypted;
	}
	
	public static void SetLockSpinnerItemByUuid(Spinner spnr, UUID id)
	{
	    LockListSpinnerAdapter adapter = (LockListSpinnerAdapter) spnr.getAdapter();
	    for (int position = 0; position < adapter.getCount(); position++)
	    {
	    	Lock l = (Lock)adapter.getItem(position);
	        if(l.getId().toString().equals(id.toString()))
	        {
	            spnr.setSelection(position);
	            return;
	        }
	    }
	}
	
	public static void SetKeySpinnerItemByUuid(Spinner spnr, UUID id)
	{
	    KeyListSpinnerAdapter adapter = (KeyListSpinnerAdapter) spnr.getAdapter();
	    for (int position = 0; position < adapter.getCount(); position++)
	    {
	    	Key k = (Key)adapter.getItem(position);
	    	if(k.getId().toString().equals(id.toString()))
	        {
	            spnr.setSelection(position);
	            return;
	        }
	    }
	}
	
	public static String GetPath(String fileWithPath) {
		String s = fileWithPath.substring(0, fileWithPath.lastIndexOf('/'));
		return s;
	}
	
	public static String GetFileNameFromPath(String fileWithPath) {
		String s = fileWithPath.substring(fileWithPath.lastIndexOf('/') + 1);
		return s;
	}
	
	public static String GetFileNameWithoutExtension(String fileWithPath) {
		String s = fileWithPath.substring(fileWithPath.lastIndexOf('/') + 1);
		return s.substring(0, s.indexOf('.'));
	}
	
}
