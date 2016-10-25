package com.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.util.Log;

public abstract class ZipTool {
	static String TAG = "ZipTool";
	String mZipFileName;
	String mOutputDirectory;
	String mUnzippingFileName = "";
	String mException = "";
	boolean mbClearOutputDir = false;
	abstract public void onUnzipingFile(String filename);

	abstract public void onUnzipException(String exception);

	abstract public void onUnzipFinish(String zipFileName);

	public String getUnzippingFile() {
		return mUnzippingFileName;
	}

	public String getException() {
		return mException;
	}

	public String getZipFileName() {
		return mZipFileName;
	}

	public void unzip(String zipFileName, String outputDirectory) {
		mZipFileName = zipFileName;
		mOutputDirectory = outputDirectory;
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (mbClearOutputDir == true){
					File file = new File(mOutputDirectory);
					DeleteFile(file);	
				}
				
				
				ZipInputStream in = null;
				try {
					in = new ZipInputStream(new FileInputStream(mZipFileName));

					ZipEntry z;
					String name = "";
					String extractedFile = "";
					int counter = 0;

					while ((z = in.getNextEntry()) != null) {
						name = z.getName();
						Log.d(TAG, "unzipping file: " + name);
						mUnzippingFileName = name;
						onUnzipingFile(mUnzippingFileName);
						if (z.isDirectory()) {
							Log.d(TAG, name + "is a folder");
							// get the folder name of the widget
							name = name.substring(0, name.length() - 1);
							File folder = new File(mOutputDirectory
									+ File.separator + name);
							folder.mkdirs();
							if (counter == 0) {
								extractedFile = folder.toString();
							}
							counter++;
							Log.d(TAG, "mkdir " + mOutputDirectory
									+ File.separator + name);
						} else {
							Log.d(TAG, name + "is a normal file");
							File file = new File(mOutputDirectory
									+ File.separator + name);
							file.createNewFile();
							// get the output stream of the file
							FileOutputStream out = new FileOutputStream(file);
							int ch;
							byte[] buffer = new byte[1024];
							// read (ch) bytes into buffer
							while ((ch = in.read(buffer)) != -1) {
								// write (ch) byte from buffer at the position 0
								out.write(buffer, 0, ch);
								out.flush();
							}
							out.close();
						}
					}

					in.close();
					onUnzipFinish(mZipFileName);
				} catch (Exception e) {
					e.printStackTrace();
					mException = e.getMessage();
					onUnzipException(e.getMessage());
				}
			}

		}).start();
	}

	public void setClearOutputDirBeforeUnzip(boolean clear) {
		mbClearOutputDir = clear;

	}

	private void DeleteFile(File file) {
		if (file.exists() == false) {
			return;
		} else {
			if (file.isFile()) {
				file.delete();
				return;
			}
			if (file.isDirectory()) {
				File[] childFile = file.listFiles();
				if (childFile == null || childFile.length == 0) {
					file.delete();
					return;
				}
				for (File f : childFile) {
					DeleteFile(f);
				}
				file.delete();
			}
		}
	}
}
