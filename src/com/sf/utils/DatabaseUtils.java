package com.sf.utils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

public class DatabaseUtils {

	private Logger logger = Logger.getLogger(getClass());

	private final static String DBIP = "120.25.232.93";
	private final static String DBUSER = "auto";
	private final static String DBPASSWORD = "123";
	private String DBSID = "autotest";
	private String contentTable = "";
	private final static String Encode = "utf-8";
	
	private final String ItemFolder = "/var/www/html/items/images/";

	private Connection conn;

	private Statement statement;

	public DatabaseUtils() {

		initDB();
	}

	private void initDB() {

		readDbProperty();
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://" + DBIP + ":3306/" + DBSID;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, DBUSER, DBPASSWORD);

			if (!conn.isClosed()) {
				logger.info("###Succeeded connecting to the Database! ###");
			}

			statement = conn.createStatement();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void readDbProperty() {

		DBSID = PublicUtils.getUrl("dbsid");
		contentTable = PublicUtils.getUrl("dbcontenttable");
	}

	private void executeQuery(String sql) {

		ResultSet rs;
		try {
			rs = statement.executeQuery(sql);

			logger.info("-----------------");
			logger.info("-- |Title|\t |contentID|\t |Link| --");
			logger.info("-----------------");

			String name = null;

			try {
				while (rs.next()) {

					name = rs.getString("Title");
					String contentid = rs.getString("ContentID");
					String link = rs.getString("Link");

					logger.info(name + "\t" + contentid + "\t" + link);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			logger.info("-----------------");
			rs.close();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public void insertSql(String sql) {

		int rs;
		try {
			rs = statement.executeUpdate(sql);
			logger.info("### Finish Insert into db.. ###");
		}  catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e1) {
			logger.info("##########################");
			logger.info("### Duplicate entry... ###");
			logger.info("##########################");
		}
	}

	private String buildQuerySql() {

		String sql = "select * from rsscontent ORDER BY Title DESC LIMIT 9";

		return sql;
	}

	public String buildUpdateSql(String Title, String Description, String Date,
			String Link, String category, String picLink, int feedid) {

		if (Title.contains("'")) {
			Title = Title.replaceAll("'", "''");
		}
		if (Description.contains("'")) {
			Description = Description.replaceAll("'", "''");
		}
		String sql = "insert into "
				+ contentTable
				+ " (Category, Title, Description, Date, Link, FeedID, picLink) "
				+ "VALUES ('" + category + "','" + Title + "','" + Description
				+ "', " + "'" + convertDate(Date) + "', '" + Link + "','"
				+ feedid + "'," + picLink + ")";

		try {
			sql = new String(sql.getBytes("utf8"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sql;
	}

	public void testCon() {

		// String insertSql = buildUpdateSql();
		// insertSql(insertSql);

		String querysql = buildQuerySql();
		executeQuery(querysql);
	}

	public void closeCon() {

		if (conn != null) {
			try {
				conn.close();
				logger.info("### DB Closed.. ###");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public String convertDate(String dateStr) {

		int firstindex = dateStr.indexOf(" ");
		int lastindex = dateStr.lastIndexOf(" ");

		if (firstindex >= 0 && lastindex > 0) {
			dateStr = dateStr.substring(firstindex + 1, lastindex);

			SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
			java.util.Date now;
			try {
				now = df.parse(dateStr);
				System.out.println(now.getTime());
				return String.valueOf(now.getTime());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return "0";
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		DatabaseUtils dbutil = new DatabaseUtils();
		// dbutil.convertDate("");
		// dbutil.insertSql("delete * from rsscontent");
		// dbutil.testCon();
		//
		// dbutil.closeCon();
	}
	
	private String getImageName(String urls) {
		
		int index = urls.lastIndexOf("/");
		return urls.substring(index+1);
	}

	public String downloadPicture(String urlString) {

		urlString = urlString.replaceAll("'", "");
		try {
			URL url = new URL(URLDecoder.decode(urlString, "utf-8"));
			
			DataInputStream dataInputStream = new DataInputStream(
					url.openStream());
			String newImage = getImageName(urlString);
			String imageName = ItemFolder + newImage;
			FileOutputStream fileOutputStream = new FileOutputStream(new File(
					imageName));

			byte[] buffer = new byte[1024];
			int length;

			while ((length = dataInputStream.read(buffer)) > 0) {
				fileOutputStream.write(buffer, 0, length);
			}

			dataInputStream.close();
			fileOutputStream.close();
			System.out.println("### Finish download image caches.. ###");
			
			return "'http://120.25.232.93/items/images/" + newImage+"'";
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ".jpg";
	}
}
