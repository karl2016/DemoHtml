package com.tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateInfo {
	List<ApkUpdateInfo> apkInfoList;
	List<HtmlUpdateInfo> htmlInfoList;

	public UpdateInfo() {
		apkInfoList = new ArrayList<ApkUpdateInfo>();
		htmlInfoList = new ArrayList<HtmlUpdateInfo>();
	}

	// ascend sort
	public void sort() {
		Collections.sort(apkInfoList);
		Collections.sort(htmlInfoList);
		Collections.reverse(apkInfoList);
		Collections.reverse(htmlInfoList);
	}

	private class ApkUpdateInfo implements Comparable<ApkUpdateInfo> {
		String version;
		String uri;

		public ApkUpdateInfo(String v, String u) {
			version = v;
			uri = u;
		}

		@Override
		public int compareTo(ApkUpdateInfo arg0) {
			return compareVersion(this.version, arg0.version);
		}
	}

	private class HtmlUpdateInfo implements Comparable<HtmlUpdateInfo> {
		String version;
		String uri;
		String requestApkVersion;

		public HtmlUpdateInfo(String v, String u, String r) {
			version = v;
			uri = u;
			requestApkVersion = r;
		}

		@Override
		public int compareTo(HtmlUpdateInfo arg0) {
			return compareVersion(this.version, arg0.version);
		}
	}

	public void addNewApkUpdateInfo(String version, String uri) {
		apkInfoList.add(new ApkUpdateInfo(version, uri));
	}

	public void addNewHtmlUpdateInfo(String version, String uri,
			String requestApkVersion) {
		htmlInfoList.add(new HtmlUpdateInfo(version, uri, requestApkVersion));
	}

	public String getApkUpdateUri() {
		if (apkInfoList.size() > 0) {
			ApkUpdateInfo info = apkInfoList.get(0);
			return info.uri;
		}
		return null;
	}

	public String getApkUpdateVersion() {
		if (apkInfoList.size() > 0) {
			ApkUpdateInfo info = apkInfoList.get(0);
			return info.version;
		}
		return null;
	}

	public String getHtmlUpdateUri() {
		if (apkInfoList.size() > 0) {
			HtmlUpdateInfo info = htmlInfoList.get(0);
			return info.uri;
		}
		return null;
	}

	public String getHtmlUpdateVersion() {
		if (apkInfoList.size() > 0) {
			HtmlUpdateInfo info = htmlInfoList.get(0);
			return info.version;
		}
		return null;
	}

	public boolean canApkUpdate(String loacalapkVersion) {
		String updateVersion = getApkUpdateVersion();
		if (compareVersion(updateVersion, loacalapkVersion) > 0) {
			return true;
		}
		return false;
	}

	public boolean canHtmlUpdate(String loacalHtmlVersion) {
		String updateVersion = getHtmlUpdateVersion();
		if (compareVersion(updateVersion, loacalHtmlVersion) > 0) {
			return true;
		}
		return false;
	}

	public static int compareVersion(String version1, String version2) {
		if (version1.equals(version2)) {
			return 0;
		}
		String pattern = "(\\d)";

		Pattern r = Pattern.compile(pattern);
		Matcher m1 = r.matcher(version1);
		Matcher m2 = r.matcher(version2);
		int diff = 0;
		while (m1.find() && m2.find() && diff == 0) {
			diff = Integer.parseInt(m1.group()) - Integer.parseInt(m2.group());
			System.out
					.println("Found value: " + m1.group() + "  " + m2.group());
		}
		if (diff == 0)
			return 0;
		else
			return diff > 0 ? 1 : -1;
	}
}
