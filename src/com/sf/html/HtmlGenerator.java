package com.sf.html;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class HtmlGenerator {

	private final String TemplateFolder = "template";
	private final String TemplateName = "single.html";
	// private final String TargetItemFolder = "test/";
	private final String TargetItemFolder = "/var/www/html/items/single/";

	public HtmlGenerator() {

	}

	public String generateHtml(String title, String content,
			String itemdescription) {

		String itemfilename = generateHtmlFileName();

		try {
			Configuration configuration = new Configuration();
			configuration.setDirectoryForTemplateLoading(new File(
					TemplateFolder));
			configuration.setObjectWrapper(new DefaultObjectWrapper());
			configuration.setDefaultEncoding("UTF-8");

			Template template = configuration.getTemplate(TemplateName);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("itemTitle", title);
			paramMap.put("description", itemdescription);
			paramMap.put("itemcontent", content);

			Writer writer = new OutputStreamWriter(new FileOutputStream(
					TargetItemFolder + itemfilename), "UTF-8");
			template.process(paramMap, writer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("finish generate [" + itemfilename + "]");
		return itemfilename;
	}

	public String generateHtml(String title, String logoimg, String content,
			String itemdescription, String originurl, String categoryname) {

		String itemfilename = generateHtmlFileName();

		try {
			Configuration configuration = new Configuration();
			configuration.setDirectoryForTemplateLoading(new File(
					TemplateFolder));
			configuration.setObjectWrapper(new DefaultObjectWrapper());
			configuration.setDefaultEncoding("UTF-8");

			Template template = configuration.getTemplate(TemplateName);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("itemTitle", title);
			paramMap.put("itemLogo", logoimg);
			paramMap.put("description", itemdescription);
			paramMap.put("itemcontent", content);
			paramMap.put("itemOrigin", originurl);
			buildCategory(paramMap,categoryname);

			Writer writer = new OutputStreamWriter(new FileOutputStream(
					TargetItemFolder + itemfilename), "UTF-8");
			template.process(paramMap, writer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("finish generate [" + itemfilename + "]");
		return itemfilename;
	}

	private void buildCategory(Map<String, Object> paramMap,
			String categoryname) {

		String link = "";
		String name = "";

		switch (categoryname) {
		case "Tugua":
			link = "tugua.html";
			name = "图卦";
			break;
		case "Zhihu":
			link = "zhihu.html";
			name = "知乎精选";
			break;
		case "MacStory":
			link = "macstory.html";
			name = "Mac Story";
			break;
		case "Quora":
			link = "quora.html";
			name = "Quora精选";
			break;
		case "IFan":
			link = "ifan.html";
			name = "极客范儿";
			break;
		case "IdeaLife":
			link = "fasion.html";
			name = "时尚设计";
			break;
		case "Smzdm":
			link = "smzdm.html";
			name = "神马值得买";
			break;
		case "Smashmagz":
			link = "fasion.html";
			name = "时尚设计";
			break;
		case "Penti":
			link = "penti.html";
			name = "喷嚏一下";
			break;
		case "Geekpark":
			link = "jike.html";
			name = "极客公园";
			break;
		case "Whoshipm":
			link = "ifan.html";
			name = "极客范儿";
			break;
		default:
			break;
		}
		paramMap.put("categoryLink", link);
		paramMap.put("categoryName", name);
	}

	private String generateHtmlFileName() {

		Date date = new Date();
		String randomNum = Double.toString(Math.random()).substring(2);
		return (Long.toString(date.getTime()) + "_" + randomNum) + ".html";
	}

}
