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
	private final static int TuguaFeed = 1;
	private final static int ZhihuFeed = 2;
	private final static int MacStoryFeed = 3;
	private final static int QuoraFeed = 4;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// System.setProperty("http.proxyHost", "proxy");
		// System.setProperty("http.proxyPort", "8080");

		RssCommonTest rssTest = new RssCommonTest();

		rssTest.scanCommonRssSources(TuguaCategory,TuguaFeed);
		rssTest.scanCommonRssSources(ZhihuCategory,ZhihuFeed);
		rssTest.scanCommonRssSources(MacCategory,MacStoryFeed);
		rssTest.scanCommonRssSources(QuoraCategory,QuoraFeed);
	}

	public void scanCommonRssSources(String categoryname,int feedid) {

		logger.info("### Start Common Rss scan... ###");
		try {
			ArrayList<CommonRSSItem> itemlist = Dom4jXmlParser
					.getCommonLinkAsXmlData(new URL(PublicUtils.getUrl(categoryname.toLowerCase())));
			for (CommonRSSItem item : itemlist) {

				String title = item.getTitle().trim();
				String content = item.getDescription();
				insertIntoDB(title, content.trim(), item.getPubdate().trim(),
						item.getLink(), categoryname, "'" + item.getPicLink() + "'",
						feedid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void scanTuguaSource() {
		
		scanCommonRssSources("Tugua",1);
	}
	
	public void scanZhihuSource() {
		
		scanCommonRssSources("Zhihu",2);
	}

	// public void scanZhihuRssSources() {
	//
	// try {
	// ArrayList<CommonRSSItem> itemlist = Dom4jXmlParser
	// .getTuguaLinkAsXmlData(new URL(PublicUtils.getUrl("zhihu")));
	// for (CommonRSSItem item : itemlist) {
	//
	// String title = item.getTitle().trim();
	// String content = item.getDescription();
	// insertIntoDB(title, content.trim(), item.getPubdate().trim(),
	// item.getLink(), "Zhihu", "'" + item.getPicLink() + "'",
	// 2);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	private String generateItemLink(String title, String itemcontent) {

		HtmlGenerator hg = new HtmlGenerator();
		String htmlname = hg.generateHtml(title, itemcontent, "");

		return "http://www.ayin.linkpc.net/items/tugua/" + htmlname;
	}

	public void insertIntoDB(String Title, String Description, String Date,
			String Link, String category, String picLink, int feedid) {

		System.out.println("### Start Insert into db..["+Title+"] ###");
		DatabaseUtils dbutil = new DatabaseUtils();
		String sql = dbutil.buildUpdateSql(Title, Description, Date,
				Link, category, picLink, feedid);
		
		System.out.println("### SQL IS: ["+sql+"] ###");
		try {
			dbutil.insertSql(sql);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		dbutil.closeCon();
	}

}
