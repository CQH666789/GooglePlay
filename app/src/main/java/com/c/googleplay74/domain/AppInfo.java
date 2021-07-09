package com.c.googleplay74.domain;

import java.util.ArrayList;


public class AppInfo {

	public String des;
	public String downloadUrl;
	public String iconUrl;
	public long size;
	public float stars;
	public String id;
	public String name;
	public String packageName;
	
	//补充字段, 供应用详情页使用
	public String author;
	public String date;
	public String downloadNum;
	public String version;
	public ArrayList<SafeInfo> safe;
	public ArrayList<String> screen;

	//当一个内部类是public static的时候, 和外部类没有区别
	public static class SafeInfo {
		public String safeDes;
		public String safeDesUrl;
		public String safeUrl;
	}
}
