package com.resolveit.items;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;

import com.resolveit.common.WebDriverController;

public class EmailConfirmation extends WebDriverController {

	public EmailConfirmation(WebDriver driver) {
		this.driver = driver;
	}
	
	public String emailConfirmation(String expectedSubject) throws Exception {
		String actualSubject = null;
		CheckEmail mail = new CheckEmail();			
		try {
			actualSubject = mail.checkEmail(expectedSubject);	
			while (!actualSubject.equals(expectedSubject)) {
				actualSubject = mail.checkEmail(expectedSubject);	
				if (actualSubject != null && actualSubject.equals(expectedSubject)) {
					actualSubject = expectedSubject;
					break;
				}
			}
			System.out.println("Expected Subject Name to be found: " + expectedSubject);
		} catch (Exception e) {
			System.out.println("Not found");
			e.printStackTrace();
		}
		return actualSubject;
	}
	
	public String extractLink(String emailContent) {
		Document doc = Jsoup.parse(emailContent);
		Elements links = doc.select("a[href*=]");
		System.out.println(links);
		String href = links.attr("href");
		System.out.println("href = " + href);
		return href;
	 
	}

}
