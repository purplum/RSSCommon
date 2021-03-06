package com.sf.utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.sf.beans.CommonRSSItem;
import com.sf.beans.News;
import com.sf.beans.Outline;

public class Dom4jXmlParser {
	
	private Logger logger = Logger.getLogger(Dom4jXmlParser.class);

	public Map<String, List<Outline>> getLinkAsXmlData(URL url)
			throws Exception {
		Map<String, List<Outline>> map_Channels = new HashMap<String, List<Outline>>();

		// 加载XML到内存解析并得到Document对象
		SAXReader reader = new SAXReader();
		// reader.setEncoding("utf-16");
		Document document = reader.read(url);

		// 获取根节点
		Element root = document.getRootElement();

		// 获取根节点下的body,然后获取其下的所有一级的outline节点,并迭代
		List<Element> ele_Channels = root.element("body").elements("outline");
		Iterator<Element> iterator_Channel = ele_Channels.iterator();
		while (iterator_Channel.hasNext()) {
			// 频道名称节点
			Element ele_Channel = iterator_Channel.next();
			// 获取频道名称
			String channelName = ele_Channel.attributeValue("text");

			// 获取当前频道下的所有outline节点，并迭代
			List<Element> ele_Items = ele_Channel.elements("outline");
			Iterator<Element> iterator_Item = ele_Items.iterator();

			// 存放一个频道内的子频道的集合
			List<Outline> list_Items = new ArrayList<Outline>();
			while (iterator_Item.hasNext()) {
				// 子频道节点
				Element ele_Item = iterator_Item.next();

				Outline outline = new Outline();
				outline.setTitle(ele_Item.attributeValue("title"));
				outline.setText(ele_Item.attributeValue("text"));
				outline.setType(ele_Item.attributeValue("type"));
				outline.setXmlUrl(ele_Item.attributeValue("xmlUrl"));
				outline.setHtmlUrl(ele_Item.attributeValue("htmlUrl"));

				list_Items.add(outline);
			}

			map_Channels.put(channelName, list_Items);
		}

		return map_Channels;
	}

	public ArrayList<CommonRSSItem> getCommonLinkAsXmlData(URL url)
			throws Exception {
		ArrayList<CommonRSSItem> itemlist = new ArrayList<CommonRSSItem>();

		SAXReader reader = new SAXReader();

		Document document = null;
		document = reader.read(url);
		System.out.println("### Read out rss url contents. ###");

		int circle = 5;
		while (circle-- > 0 && document == null) {
			System.out.println("### Cannot resolve url, try again.. ###");
			document = reader.read(url);
			Thread.sleep(3000);
		}
		if (document == null) {
			System.out.println("### Cannot resolve url, abort.. ###");
			return itemlist;
		}
		System.out.println("### Start parsing server response ["+url.toString()+"] ###");
		Element root = document.getRootElement();

		List<Element> ele_items = root.element("channel").elements("item");
		Iterator<Element> iterator_items = ele_items.iterator();
		while (iterator_items.hasNext()) {
			Element ele_item = iterator_items.next();
			String itemName = ele_item.elementText("title");
			String description = ele_item.elementText("description");
			String contentencode = ele_item.elementText("encoded");
			String image = ele_item.elementText("image");
			String focusimage = ele_item.elementText("focus_pic");
			if(description==null || description.equals("")) {
				System.out.println("### Description node null, turn to content node.. ###");
				description = contentencode;
			}
			else if(contentencode!=null) {
				if(contentencode.contains(description)) {
					description = contentencode;
				}
				else {
					description = description+contentencode;
				}
			}
			String pubdate = ele_item.elementText("pubDate");
			String link = ele_item.elementText("link");

			CommonRSSItem item = new CommonRSSItem();
			item.setTitle(itemName);
			String tmp = description;
			item.setOriginContent(adjustFullContents(tmp));
			if(description==null) {
				item.setDescription("");
			}
			else {
				if(description.length()>100) {
					item.setDescription(description.substring(0, 60));
				}
				else {
					item.setDescription(description);
				}
			}
			item.setPubdate(pubdate);
			item.setLink(link);
			if(image==null) {
				if(focusimage==null) {
					item.setPicLink(buildPicURL(description==null?"":description,contentencode));
				}
				else {
					item.setPicLink(buildFocusImage(focusimage));
				}
			}
			else {
				item.setPicLink(image);
			}

			itemlist.add(item);
		}

		return itemlist;
	}
	
	private String adjustFullContents(String origincontent) {
		
//		return reShapePicURL(origincontent);
		return origincontent;
	}
	
	private String buildFocusImage(String originImage) {
		
		System.out.println("### build focus image :"+originImage+" ###");
		int firstindex = originImage.indexOf("http");
		int jpgindex = originImage.indexOf(".jpg");
		int pngindex = originImage.indexOf(".png");
		if(firstindex<0) {
			return ".jpg";
		}
		if(jpgindex<0) {
			if(pngindex<0) {
				return ".jpg";
			}
			else {
				return originImage.substring(firstindex, pngindex+4);
			}
		}
		else {
			return originImage.substring(firstindex, jpgindex+4);
		}
		
	}

	private String buildPicURL(String description,String contentencode) {
		
		ImageUtils util = new ImageUtils();
		String topimage = util.getTopImageSrc(description);
		if(topimage==null||topimage.equals("")) {
			topimage = util.getTopImageSrc(contentencode);
		}
		return topimage;
	}
	
//	private String reShapePicURL(String description) {
//		
//		ImageUtils util = new ImageUtils();
//		String topimage = util.reShapeImageSrc(description);
//		return topimage;
//	}

	public static void main(String[] args) {

		String description = "<table width=><img src=\"http://statisches.auslieferung.commindo-media-ressourcen.de/advertisement.gif\" /"
				+ "><br /><a href=\"http://auslieferung.commindo-media-ressourcen.de/random.php?mode=target&collection=smashing-rss&position"
				+ "=><img src=\"http://auslieferung.commindo-media-ressourcen.de/random.php?mode=image&collection=smashing-rss&position=1"
				+ "http://auslieferung.commindo-media-ressourcen.de/random.php?mode=target&collection=smashing-rss&position="
				+ "2\"><img src=\"http://auslieferung.commindo-media-ressourcen.de/random.php?mode=image&collection=smashing-rss&position=2\""
				+ "http://auslieferung.commindo-media-ressourcen.de/random.php?mode=target&collection=smashing-rss&position=3";

		Dom4jXmlParser parser = new Dom4jXmlParser();
		String finalstr = parser.buildFocusImage(description);
		System.out.println(finalstr);
	}

	// 根据URL解析单个子频道内的XML新闻数据
	public List<News> getNewsAsXmlData(URL url) throws Exception {
		// 加载XML到内存解析并得到Document对象
		SAXReader reader = new SAXReader();
		Document document = reader.read(url);

		// 获取根节点
		Element root = document.getRootElement();

		// 获取所有新闻条目的集合，并迭代
		List<Element> ele_Items = root.element("channel").elements("item");
		Iterator<Element> iterator_Item = ele_Items.iterator();

		// 存放所有新闻项的集合
		List<News> list_News = new ArrayList<News>();
		while (iterator_Item.hasNext()) {
			// 新闻项节点
			Element ele_Item = iterator_Item.next();

			News news = new News();
			news.setTitle(ele_Item.elementText("title"));
			news.setLink(ele_Item.elementText("link"));
			news.setAuthor(ele_Item.elementText("author"));
			news.setGuid(ele_Item.elementText("guid"));
			news.setCategory(ele_Item.elementText("category"));
			news.setPubDate(ele_Item.elementText("pubDate"));
			news.setComments(ele_Item.elementText("comments"));
			news.setDescription(ele_Item.elementText("description"));

			list_News.add(news);
		}

		return list_News;
	}
}
