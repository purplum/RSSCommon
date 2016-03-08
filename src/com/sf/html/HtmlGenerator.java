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
	private final String TemplateName = "tugua.html";
	private final String ItemFolder = "/var/www/html/items/tugua/";
//	private final String ItemFolder = "/var/www/html/items/";

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
					ItemFolder + itemfilename), "UTF-8");
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

	private String generateHtmlFileName() {

		Date date = new Date();
		String randomNum = Double.toString(Math.random()).substring(2);
		return (Long.toString(date.getTime()) + "_" + randomNum) + ".html";
	}

}
