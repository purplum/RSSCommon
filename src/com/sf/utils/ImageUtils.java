package com.sf.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class ImageUtils {

	private Logger logger = Logger.getLogger(ImageUtils.class);

	private static final String ECODING = "UTF-8";
	private static final String IMGURL_REG = "<img.*src=(.*?)[^>]*?>";
	private static final String IMGSRC_REG = "http\"?(.*?)(\"|>|\\s+)";

	public static void main(String[] args) throws Exception {
		ImageUtils cm = new ImageUtils();
		// String HTML = cm.getHTML(URL);
		String HTML = "<table width=><img src=\"http://statisches.auslieferung.commindo-media-ressourcen.de/advertisement.gif\" /"
				+ "><br /><a href=\"http://auslieferung.commindo-media-ressourcen.de/random.php?mode=target&collection=smashing-rss&position"
				+ "=><img src=\"http://auslieferung.commindo-media-ressourcen.de/random.php?mode=image&collection=smashing-rss&position=1"
				+ "http://auslieferung.commindo-media-ressourcen.de/random.php?mode=target&collection=smashing-rss&position="
				+ "2\"><img src=\"http://auslieferung.commindo-media-ressourcen.de/random.php?mode=image&collection=smashing-rss&position=2\""
				+ "http://auslieferung.commindo-media-ressourcen.de/random.php?mode=target&collection=smashing-rss&position=3";
//		List<String> imgUrl = cm.getImageUrl(HTML);
//		List<String> imgSrc = cm.getImageSrc(imgUrl);
		System.out.println(cm.reShapeImageSrc(HTML));
//		System.out.println("##uRL: " + imgUrl);
//		System.out.println("##SRC: " + imgSrc);
	}

	public List<String> getImageSRCList(String content) {

		List<String> imgUrl = getImageUrl(content);
		List<String> imgSrc = getImageSrc(imgUrl);
		return imgSrc;
	}

	public String getTopImageSrc(String content) {

		List<String> srcList = getImageSRCList(content);
		if (srcList != null && srcList.size() > 0) {
			String topsrc = srcList.get(0);
			System.out.println("## Filter out image src: [" + topsrc + "] ##");
			if(topsrc.endsWith(".jpg") || topsrc.endsWith(".png") ||topsrc.endsWith(".gif")) {
				return topsrc;
			}
			else {
				String end = "";
				if(topsrc.contains(".jpg")) {
					end = ".jpg";
				}
				else if(topsrc.contains(".png")) {
					end = ".png";
				}
				else if(topsrc.contains(".gif")) {
					end = ".gif";
				}
				int endIndex = topsrc.indexOf(end);
				if(endIndex>=0) {
					String finalStr = topsrc.substring(0, endIndex+4);
					System.out.println("### Final Image url: "+finalStr);
					return finalStr;
				}
			}
		}
		return "";
	}
	
	public String reShapeImageSrc(String content) {
		
		List<String> srcList = getImageSRCList(content);
		String firsttag = "://";
		String endtag = "/";
		for(String img: srcList) {
			if(img==null) {
				continue;
			}
			if(img.contains(".gif")||img.contains(".jpg")||img.contains(".png")) {
				String tmp = "";
				
				tmp = img.replaceAll("https", "http");
				String middle = tmp;
				int firstIndex = tmp.indexOf(firsttag);
				
				if(middle.length()<=firstIndex+3) {
					continue;
				}
				middle = middle.substring(firstIndex+3);
				int lastIndex = middle.indexOf(endtag);
				middle = middle.substring(0, lastIndex);
				
				tmp = tmp.replaceAll(middle, "120.25.232.93/items/images/");
				content = content.replace(img, tmp);
			}
		}
		return content;
	}

	/***
	 * 获取HTML内容
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	private String getHTML(String url) throws Exception {
		URL uri = new URL(url);
		URLConnection connection = uri.openConnection();
		InputStream in = connection.getInputStream();
		byte[] buf = new byte[1024];
		int length = 0;
		StringBuffer sb = new StringBuffer();
		while ((length = in.read(buf, 0, buf.length)) > 0) {
			sb.append(new String(buf, ECODING));
		}
		in.close();
		return sb.toString();
	}

	/***
	 * 获取ImageUrl地址
	 * 
	 * @param HTML
	 * @return
	 */
	private List<String> getImageUrl(String HTML) {
		List<String> listImgUrl = new ArrayList<String>();
		if(HTML==null) {
			return listImgUrl;
		}
		Matcher matcher = Pattern.compile(IMGURL_REG).matcher(HTML);
		while (matcher.find()) {
			listImgUrl.add(matcher.group());
		}
		return listImgUrl;
	}

	/***
	 * 获取ImageSrc地址
	 * 
	 * @param listImageUrl
	 * @return
	 */
	private List<String> getImageSrc(List<String> listImageUrl) {
		List<String> listImgSrc = new ArrayList<String>();
		for (String image : listImageUrl) {
			Matcher matcher = Pattern.compile(IMGSRC_REG).matcher(image);
			while (matcher.find()) {
				listImgSrc.add(matcher.group().substring(0,
						matcher.group().length() - 1));
			}
		}
		return listImgSrc;
	}

	/***
	 * 下载图片
	 * 
	 * @param listImgSrc
	 */
	private void Download(List<String> listImgSrc) {
		try {
			for (String url : listImgSrc) {
				String imageName = url.substring(url.lastIndexOf("/") + 1,
						url.length());
				URL uri = new URL(url);
				InputStream in = uri.openStream();
				FileOutputStream fo = new FileOutputStream(new File(imageName));
				byte[] buf = new byte[1024];
				int length = 0;
				System.out.println("开始下载:" + url);
				while ((length = in.read(buf, 0, buf.length)) != -1) {
					fo.write(buf, 0, length);
				}
				in.close();
				fo.close();
				System.out.println(imageName + "下载完成");
			}
		} catch (Exception e) {
			System.out.println("下载失败");
		}
	}
}
