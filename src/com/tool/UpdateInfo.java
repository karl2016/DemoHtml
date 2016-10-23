package com.tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UpdateInfo {
	List<ApkUpdateInfo> apkInfoList;
	List<HtmlUpdateInfo> htmlInfoList;
	public UpdateInfo() {
		apkInfoList = new ArrayList<ApkUpdateInfo>();
		htmlInfoList = new ArrayList<HtmlUpdateInfo>();
	}
	
	//  ascend sort
	public void sort()
	{
		  Collections.sort(apkInfoList);
		  Collections.sort(htmlInfoList);
		  Collections.reverse(apkInfoList);
		  Collections.reverse(htmlInfoList);
	}
	private class ApkUpdateInfo implements Comparable<ApkUpdateInfo>
	{
		String version;
		String uri;
		public ApkUpdateInfo(String v, String u) {
			version = v;
			uri = u;
		}
		@Override
		public int compareTo(ApkUpdateInfo arg0) {
			return this.version.compareTo(arg0.version);
		}
	}
	private class HtmlUpdateInfo implements Comparable<HtmlUpdateInfo>
	{
		String version;
		String uri;
		String requestApkVersion;
		public HtmlUpdateInfo(String v, String u, String r) {
			version = v;
			uri = u;
			requestApkVersion = r;
		}@Override
		public int compareTo(HtmlUpdateInfo arg0) {
			return this.version.compareTo(arg0.version);
		}
	}
	public void addNewApkUpdateInfo(String version,String uri)
	{
		apkInfoList.add(new ApkUpdateInfo(version, uri));
	}
	public void addNewHtmlUpdateInfo(String version,String uri, String requestApkVersion)
	{
		htmlInfoList.add(new HtmlUpdateInfo(version, uri, requestApkVersion));
	}
	public String getApkUpdateUri()
	{
		if (apkInfoList.size() > 0){
			ApkUpdateInfo  info = apkInfoList.get(0);
			return info.uri;	
		}
		return null;
	}
	public String getApkUpdateVersion()
	{
		if (apkInfoList.size() > 0){
			ApkUpdateInfo  info = apkInfoList.get(0);
			return info.version;	
		}
		return null;
	}
	public String getHtmlUpdateUri()
	{
		if (apkInfoList.size() > 0){
			HtmlUpdateInfo  info = htmlInfoList.get(0);
			return info.uri;	
		}
		return null;
	}
	public String getHtmlUpdateVersion()
	{
		if (apkInfoList.size() > 0){
			HtmlUpdateInfo  info = htmlInfoList.get(0);
			return info.version;	
		}
		return null;
	}
}
