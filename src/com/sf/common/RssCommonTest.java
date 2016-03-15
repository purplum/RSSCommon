package com.sf.common;

import java.net.URL;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.sf.beans.CommonRSSItem;
import com.sf.html.HtmlGenerator;
import com.sf.utils.DatabaseUtils;
import com.sf.utils.Dom4jXmlParser;
import com.sf.utils.PublicUtils;

public class RssCommonTest {

	private Logger logger = Logger.getLogger(getClass());

	private final static String TuguaCategory = "Tugua";
	private final static String ZhihuCategory = "Zhihu";
	private final static String MacCategory = "MacStory";
	private final static String QuoraCategory = "Quora";
	private final static String IFanCategory = "IFan";
	private final static String ILifeCategory = "IdeaLife";
	private final static String SmzdmCategory = "Smzdm";
	private final static String SmashCategory = "Smashmagz";
	private final static String PentiCategory = "Penti";

	private final static int TuguaFeed = 1;
	private final static int ZhihuFeed = 2;
	private final static int MacStoryFeed = 3;
	private final static int QuoraFeed = 4;
	private final static int IFanFeed = 5;
	private final static int ILifeFeed = 6;
	private final static int SmzdmFeed = 7;
	private final static int SmashFeed = 8;
	private final static int PentiFeed = 9;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// System.setProperty("http.proxyHost", "proxy");
		// System.setProperty("http.proxyPort", "8080");

		RssCommonTest rssTest = new RssCommonTest();

		rssTest.scanCommonRssSources(TuguaCategory, TuguaFeed);
		rssTest.scanCommonRssSources(ZhihuCategory, ZhihuFeed);
		rssTest.scanCommonRssSources(MacCategory, MacStoryFeed);
		rssTest.scanCommonRssSources(QuoraCategory, QuoraFeed);
		rssTest.scanCommonRssSources(IFanCategory, IFanFeed);
		rssTest.scanCommonRssSources(ILifeCategory, ILifeFeed);
		rssTest.scanCommonRssSources(SmzdmCategory, SmzdmFeed);
		rssTest.scanCommonRssSources(SmashCategory, SmashFeed);
		rssTest.scanCommonRssSources(PentiCategory, PentiFeed);
	}

	public void scanCommonRssSources(String categoryname, int feedid) {

		logger.info("### Start Common Rss scan... ###");

		Dom4jXmlParser parser = new Dom4jXmlParser();
		try {
			ArrayList<CommonRSSItem> itemlist = parser
					.getCommonLinkAsXmlData(new URL(PublicUtils
							.getUrl(categoryname.toLowerCase())));
			for (CommonRSSItem item : itemlist) {

				String title = item.getTitle().trim();
				String content = item.getDescription();
				insertIntoDB(title, content.trim(), item.getPubdate().trim(),
						item.getLink(), categoryname, "'" + item.getPicLink()
								+ "'", feedid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void scanTuguaSource() {

		scanCommonRssSources("Tugua", 1);
	}

	public void scanZhihuSource() {

		scanCommonRssSources("Zhihu", 2);
	}

	private String generateItemLink(String title, String itemcontent) {

		HtmlGenerator hg = new HtmlGenerator();
		String htmlname = hg.generateHtml(title, itemcontent, "");

		return "http://120.25.232.93/items/images/" + htmlname;
	}

	public void insertIntoDB(String Title, String Description, String Date,
			String Link, String category, String picLink, int feedid) {

		System.out.println("### Start Insert into db..[" + Title + "] ###");
		DatabaseUtils dbutil = new DatabaseUtils();
		String sql = dbutil.buildUpdateSql(Title, Description, Date, Link,
				category, dbutil.downloadPicture(picLink), feedid);

		System.out.println("### SQL IS: [" + sql + "] ###");
		try {
			dbutil.insertSql(sql);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		dbutil.closeCon();
	}

}
