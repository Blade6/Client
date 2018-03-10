package com.example.jianhong.note.utils;

public class UrlUtils {
	public static String IP = "192.168.42.154";
	public static String port = "8086";
	public static String Address = "http://" + IP + ":" + port;
	public static String URLHead = Address + "/note/index.php/Home/User/";
	
	public static final String LoginURL = URLHead + "login/";
	public static final String RegisterURL = URLHead + "register/";
}
