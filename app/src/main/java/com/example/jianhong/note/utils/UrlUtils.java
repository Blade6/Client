package com.example.jianhong.note.utils;

public class UrlUtils {
	private static String IP = "192.168.202.97";
	private static String port = "8086";
	private static String Address = "http://" + IP + ":" + port;
	private static String URLHead = Address + "/note/index.php/Home/User/";
	
	public static final String LoginURL = URLHead + "login/";
	public static final String RegisterURL = URLHead + "register/";
	public static final String GETSYNURL = URLHead + "getSynUid/";
	public static final String SynuURL = URLHead + "synu";
	public static final String SyndURL = URLHead + "synd/";
}
