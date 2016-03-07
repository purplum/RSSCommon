package com.sf.utils;

import java.io.FileInputStream;
import java.util.Properties;

public class PublicUtils {

	private static String userdir = System.getProperty("user.dir");

	public static String getUrl() {
		String url = "";

		String resouceFilePath = userdir + "/resources/resource.properties";
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(resouceFilePath));
		} catch (Exception e) {
			System.out.println("Cannot find [" + resouceFilePath + "] file!!!");
			return null;
		}

		url = prop.getProperty("url");

		return url;
	}

	public static String getUrl(String rssname) {
		String url = "";

		String resouceFilePath = userdir + "/resources/resource.properties";
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(resouceFilePath));
		} catch (Exception e) {
			System.out.println("Cannot find [" + resouceFilePath + "] file!!!");
			return null;
		}

		url = prop.getProperty(rssname);

		return url;
	}

}
