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
		String[] version1Array = version1.split("\\.");
		String[] version2Array = version2.split("\\.");
		int index = 0;
		int minLen = Math.min(version1Array.length, version2Array.length);
		int diff = 0;
		while (index < minLen && diff == 0) {
			String v1 = version1Array[index];
			if (v1.indexOf("(") > 0)
				v1 = v1.substring(0, v1.indexOf("("));

			String v2 = version2Array[index];
			if (v2.indexOf("(") > 0)
				v2 = v2.substring(0, v2.indexOf("("));

			diff = Integer.parseInt(v1) - Integer.parseInt(v2);
			index++;
		}
		if (diff == 0) {
			for (int i = index; i < version1Array.length; i++) {
				if (Integer.parseInt(version1Array[i]) > 0) {
					return 1;
				}
			}
			for (int i = index; i < version2Array.length; i++) {
				if (Integer.parseInt(version2Array[i]) > 0) {
					return -1;
				}
			}
			return 0;
		} else {
			return diff > 0 ? 1 : -1;
		}
	}
}
