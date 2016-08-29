package com.randomsilo.hailcaesar.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.util.Log;

import com.randomsilo.hailcaesar.GlobalSettings;
import com.randomsilo.hailcaesar.Utility;
import com.randomsilo.hailcaesar.model.Key;
import com.randomsilo.hailcaesar.model.Lock;

@SuppressLint("UseSparseArrays") 
public class DawsonCipher {
	private static Random random = new Random();

	public static Lock generateLock(String name) throws Exception {
		Lock l = new Lock(name);
		
		HashMap<Integer,Integer> keyedOffset = new HashMap<Integer,Integer>();
		for(int i=0; i<=GlobalSettings.LockMaxIndex; i++) {
			int randomNum = random.nextInt(GlobalSettings.AsciiCharacterCount);
			keyedOffset.put(i, randomNum);
		}
		
		l.setKeyedOffset(keyedOffset);
		
		return l;
	}
	
	public static Key generateKey(String name) {
		Key k = new Key(name);
		
		HashMap<Integer,Integer> wards = new HashMap<Integer,Integer>();
		for(int i=0; i<=GlobalSettings.KeyMaxIndex; i++) {
			int randomNum = random.nextInt(GlobalSettings.LockMaxIndex);
			wards.put(i, randomNum);
		}
		
		k.setWards(wards);
		
		return k;
	}
	
	public static String encrypt(Lock lock, Key key, String text) throws Exception {
		StringBuilder content = new StringBuilder();
		
		text = text.replaceAll("\n", GlobalSettings.NewlineReplacement);
		text = text.replaceAll("\t", GlobalSettings.TabReplacement);
		
		for(int i=0; i<32; i++) {
			text = text.replaceAll(""+(char)i, " ");
		}
		
		content.append(text);
		if(Utility.ContainsOnlyPrintableValues(content.toString())) {
			
			// 1. Fill String to Block Length
			Fill(lock, content);
			
			// 2. PolyAlphabetic Encode 
			PolyAlphabeticEncode(lock, key, content);
			
			// 3. Reverse String
			Reverse(content);
			
			// 4. Block Swap
			BlockSwap(content);
			
			// 5. Char Twist
			CharTwist(content);
			
		} else {
			throw new Exception("Content contains non-printable characters.");
		}
		
		return content.toString();
	}
	
	public static String decrypt(Lock lock, Key key, String text) throws Exception {
		StringBuilder content = new StringBuilder();
		content.append(text);
		
		if(Utility.ContainsOnlyPrintableValues(content.toString())) {
			
			// 5. Char UnTwist
			CharUnTwist(content);
			
			// 4. Block Unswap
			BlockUnswap(content);
			
			// 3. Reverse String
			Reverse(content);
			
			// 2. PolyAlphabetic Encode 
			PolyAlphabeticDecode(lock, key, content);
			
			// 1. Remove Block Filler
			UnFill(content);
			
		} else {
			throw new Exception("Content contains non-printable characters.");
		}
		
		String decryptedText = content.toString();
		decryptedText = decryptedText.replaceAll(GlobalSettings.NewlineReplacement, "\n");
		decryptedText = decryptedText.replaceAll(GlobalSettings.TabReplacement, "\t");
		
		return decryptedText;
	}
	
	// encrypt stages
	public static void Fill(Lock lock, StringBuilder content) throws Exception {
		String s = content.toString();
		s.replace(GlobalSettings.EndMarker, GlobalSettings.EndMarkerReplacement);
		content.setLength(0);
		content.append(s);
		content.append(GlobalSettings.EndMarker);
		
		int contentlength = content.length();
		int fillLength = lock.getBlockFillLength();
		
		// end marker crosses boundry, fill more
		while(contentlength + GlobalSettings.EndMarker.length() >= fillLength){
			fillLength += lock.getBlockFillLength(); 
		}
		fillLength -= contentlength;
		
		for(int i=0; i<fillLength; i++) {
			content.append(Utility.RandomAsciiCharacter());
		}

		s = content.toString();
	}
	
	public static void PolyAlphabeticEncode(Lock lock, Key key, StringBuilder content) throws Exception {
		StringBuilder working = new StringBuilder();
		working.append(content.toString());
		content.setLength(0);
		
		int keyIndex = 0;
		for(int position=0; position<working.length();) {
			char character = working.charAt(position);
			int wardId = key.getWard(keyIndex);
			int offset = lock.getOffset(wardId);
			
			int charCode = (int)character;
			int asciiCode = charCode + offset;
			if(asciiCode > GlobalSettings.AsciiMax) {
				asciiCode = asciiCode - GlobalSettings.AsciiMax + GlobalSettings.AsciiMin;
			}
			
			if(!Utility.AsciiValidPrintChar(asciiCode)) {
				asciiCode = GlobalSettings.AsciiSpace;
				Log.w("PolyAlphabeticEncode", "Encoding Character outside printable scope: " + asciiCode);
			}
			
			content.append((char)asciiCode);
			
			position++;
			keyIndex++;
			if(keyIndex>=GlobalSettings.KeyMaxIndex) {
				keyIndex = 0;	
			}
		}
	}
	
	public static void BlockSwap(StringBuilder content) {
		StringBuilder working = new StringBuilder();
		List<String> evenBlocks = new ArrayList<String>();
		List<String> oddBlocks = new ArrayList<String>();
		
		working.append(content.toString());
		content.setLength(0);
		
		int blockCount = working.length() / GlobalSettings.BlockSize;
		for(int b=0; b<blockCount; b++) {
			int startIndex = GlobalSettings.BlockSize * b;
			int endIndex = startIndex + GlobalSettings.BlockSize;
			String blockPart = working.substring(startIndex, endIndex);
			
			if(b % 2 == 0) {
				evenBlocks.add(blockPart);
			} else {
				oddBlocks.add(blockPart);
			}
		}
		
		for(int o=0; o<oddBlocks.size(); o++) {
			content.append(oddBlocks.get(o));
		}
		
		for(int e=0; e<evenBlocks.size(); e++) {
			content.append(evenBlocks.get(e));
		}
	}
	
	public static void CharTwist(StringBuilder content) {
		StringBuilder working = new StringBuilder();
		
		working.append(content.toString());
		content.setLength(0);
		
		for(int i=0; i<working.length()-1; i++) {
			content.append(working.charAt(i+1));	
			content.append(working.charAt(i));
			i++;
		}
	}
	
	// decrypt stages
	public static void UnFill(StringBuilder content) throws Exception {
		StringBuilder working = new StringBuilder();
		
		working.append(content.toString());
		content.setLength(0);
		
		int endMarkerIndex = working.indexOf(GlobalSettings.EndMarker);
		if(endMarkerIndex == -1 || !(endMarkerIndex < working.length())) {
			throw new Exception("Cannot unfill a text message without an End Marker");
		}
		content.append(working.substring(0, working.indexOf(GlobalSettings.EndMarker)));
	}
	
	public static void PolyAlphabeticDecode(Lock lock, Key key, StringBuilder content) throws Exception {
		StringBuilder working = new StringBuilder();
		working.append(content.toString());
		content.setLength(0);
		
		int keyIndex = 0;
		for(int position=0; position<working.length();) {
			char character = working.charAt(position);
			int wardId = key.getWard(keyIndex);
			int offset = lock.getOffset(wardId);
			
			int charCode = (int)character;
			int asciiCode = charCode - offset;
			if(asciiCode < GlobalSettings.AsciiMin) {
				asciiCode = asciiCode + GlobalSettings.AsciiMax - GlobalSettings.AsciiMin;
			}
			
			if(!Utility.AsciiValidPrintChar(asciiCode)) {
				throw new Exception("Decoding Character outside printable scope.");
			}
			
			content.append((char)asciiCode);
			
			position++;
			keyIndex++;
			if(keyIndex>=GlobalSettings.KeyMaxIndex) {
				keyIndex = 0;	
			}
		}
	}
	
	public static void BlockUnswap(StringBuilder content) throws Exception {
		StringBuilder working = new StringBuilder();
		List<String> evenBlocks = new ArrayList<String>();
		List<String> oddBlocks = new ArrayList<String>();
		
		working.append(content.toString());
		content.setLength(0);
		
		int blockCount = working.length() / GlobalSettings.BlockSize;
		int halfBlockCount = blockCount / 2;
		for(int b=0; b<blockCount; b++) {
			int startIndex = GlobalSettings.BlockSize * b;
			int endIndex = startIndex + GlobalSettings.BlockSize;
			String blockPart = working.substring(startIndex, endIndex);
			
			if(b >= halfBlockCount) {
				evenBlocks.add(blockPart);
			} else {
				oddBlocks.add(blockPart);
			}
		}
		
		// Equal number of blocks required
		if(oddBlocks.size() != evenBlocks.size()) {
			throw new Exception("Block counts don't match");
		}
		
		for(int b=0; b<oddBlocks.size(); b++) {
			content.append(evenBlocks.get(b));
			content.append(oddBlocks.get(b));
		}

	}
	
	public static void CharUnTwist(StringBuilder content) {
		StringBuilder working = new StringBuilder();
		
		working.append(content.toString());
		content.setLength(0);
		
		for(int i=0; i<working.length()-1; i++) {
			content.append(working.charAt(i+1));	
			content.append(working.charAt(i));
			i++;
		}
	}
	
	// common to both encryption and decryption
	public static void Reverse(StringBuilder content) {
		content.reverse();
	}
	
	
	
}
