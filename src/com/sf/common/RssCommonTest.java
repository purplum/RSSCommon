package com.sf.common;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.sf.beans.News;
import com.sf.beans.Outline;
import com.sf.utils.Dom4jXmlParser;
import com.sf.utils.PublicUtils;

public class RssCommonTest {

	private Logger logger = Logger.getLogger(getClass());

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.setProperty("http.proxyHost", "proxy");
		System.setProperty("http.proxyPort", "8080");

		RssCommonTest rssTest = new RssCommonTest();
		rssTest.scanRssSources();
	}
	
	public void scanRssSources() {
		
		try {
			// 获取各个频道名称及子频道集合，并迭代
			Map<String, List<Outline>> map_Channels = Dom4jXmlParser
					.getLinkAsXmlData(new URL(PublicUtils.getUrl()));
			Iterator<Entry<String, List<Outline>>> iterator_Channels = map_Channels
					.entrySet().iterator();
			while (iterator_Channels.hasNext()) {
				// 一个频道
				Entry<String, List<Outline>> entry = iterator_Channels.next();
				String channelName = entry.getKey(); // 频道名称
				List<Outline> outlines = entry.getValue(); // 频道内子频道项目

				logger.info("### Module name：[" + channelName+"] ###");
				if (!"体育频道-新浪RSS".equals(channelName)) {
					continue;
				}

				// 迭代子频道
				Iterator<Outline> iterator_Item = outlines.iterator();
				while (iterator_Item.hasNext()) {
					// 一个子频道
					int newsIndex = 0;
					Outline outline = iterator_Item.next();
					String outlinename = outline.getText();
					if(!outlinename.equals("国际足坛")) {
						continue;
					}
					logger.info("#### Outline"+outline.getText() + "："
							+ outline.getXmlUrl() + "Contents : ####\n");
					URL url = new URL(outline.getXmlUrl());
					// 根据子频道的XMLURL获取新闻集合，并迭代
					List<News> list_News = Dom4jXmlParser.getNewsAsXmlData(url);
					Iterator<News> iterator_News = list_News.iterator();
					while (iterator_News.hasNext()) {
						// News
						News news = iterator_News.next();
						logger.info("##### News["+(newsIndex++)+"]:");
						logger.info("# Title: "+news.getTitle().trim()+" #");
						logger.info("# Description: "+news.getDescription().trim()+" #");
						logger.info("# Date: "+news.getPubDate().trim()+" #");
						logger.info("# Link: "+news.getLink().trim()+" #");
						logger.info("# Category: "+news.getCategory().trim()+" #");
						logger.info("# Comments: "+news.getComments().trim()+" #");
						logger.info("##### News End ####\n");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
