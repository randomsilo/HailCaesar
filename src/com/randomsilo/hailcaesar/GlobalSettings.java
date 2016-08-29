package com.randomsilo.hailcaesar;

public final class GlobalSettings {
	
	public static int BlockLengthMessage = 140;
	public static int BlockLengthAttachment = 1000;
	public static int BlockSize = 5; // Msg must be divisible by 5
	
	public static int AsciiMin = 32;
	public static int AsciiMax = 126;
	public static int AsciiCharacterCount = AsciiMax - AsciiMin;
	
	public static int AsciiSpace = 32;
	public static int AsciiNewline = 13;
	public static int AsciiTab = 11;
	
	public static String NewlineReplacement = "<hcn>";
	public static String TabReplacement = "<hct>";
	
	public static int KeyMaxIndex = 9;
	public static int LockMaxIndex = 99;

	public static String EndMarker = "{hce}";
	public static String EndMarkerReplacement = "[hce}";
	
	public static String FolderDownload = "Download";
	
	public static String FolderMessages = "Messages";
	public static String ExtensionMessage = ".hcm";
	
	public static String FolderLocks = "Locks";
	public static String ExtensionLock = ".hcl";
	
	public static String FolderKeys = "Keys";
	public static String ExtensionKey = ".hck";
	
	public static String FileNameIdentity = "Identity";
	public static String FolderIdentity = "Identity";
	public static String ExtensionIdentity = ".hci";
	
}
