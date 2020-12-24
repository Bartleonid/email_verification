package com.resolveit.items;

import static org.testng.Assert.assertEquals;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.SubjectTerm;
import com.sun.mail.util.MailSSLSocketFactory;

public class CheckEmail {

	public String checkEmail(String expectedSubject) throws Exception {
		Thread.sleep(5000);
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");

		MailSSLSocketFactory sf = new MailSSLSocketFactory();
		sf.setTrustAllHosts(true);
		props.put("mail.imap.ssl.trust", "*");
		props.put("mail.imap.ssl.socketFactory", sf);

		Session session = Session.getInstance(props, null);
		Store store = session.getStore("imaps");
		store.connect("imap.gmail.com", "xxxxxx@gmail.com", "xxxxxx");

		Folder folder = store.getFolder("INBOX");
		folder.open(Folder.READ_WRITE);

		System.out.println("Total Message:" + folder.getMessageCount());
		System.out.println("Unread Message:" + folder.getUnreadMessageCount());

		Message[] messages = null;
		boolean isMailFound = false;
		Message expectedEmail = null;

		messages = folder.getMessages();
		System.out.println("messages.length---" + messages.length);
		Message message = null;
		String messageContent = null;
		String actualSubject = null;
		try {
				for (int i = messages.length - 1; i > messages.length - 5; i--) {
					System.out.println("inside checkEmail while loop");
					message = messages[i];
					actualSubject = message.getSubject().toString();
					System.out.println("actualSubject1 = " + actualSubject);
					if (actualSubject != null && actualSubject.equals(expectedSubject)) {
						System.out.println("inside if ");
						actualSubject = message.getSubject().toString();
						System.out.println("actualSubject2 = " + actualSubject);
//	    		  messageContent = getTextFromMessage(message);
						message.setFlag(Flags.Flag.DELETED, true);
						break;
					} else if (!actualSubject.equals(expectedSubject)) {
						System.out.println("from second if");
						continue;
					}
				}
				return actualSubject;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return actualSubject;
	}

	private String getTextFromMessage(Message message) throws MessagingException, IOException {
		String result = "";
		if (message.isMimeType("text/plain")) {
			result = message.getContent().toString();
		} else if (message.isMimeType("multipart/*")) {
			MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
			result = getTextFromMimeMultipart(mimeMultipart);
		}
		return result;
	}

	private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
		String result = "";
		int count = mimeMultipart.getCount();
		boolean multipartAlt = new ContentType(mimeMultipart.getContentType()).match("multipart/alternative");
		if (multipartAlt)
			return getTextFromBodyPart(mimeMultipart.getBodyPart(count - 1));
		for (int i = 0; i < count; i++) {
			BodyPart bodyPart = mimeMultipart.getBodyPart(i);
			if (bodyPart.isMimeType("text/plain")) {
				result = result + "\n" + bodyPart.getContent();
				break;
			} else if (bodyPart.isMimeType("text/html")) {
				String html = (String) bodyPart.getContent();
				result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
			} else if (bodyPart.getContent() instanceof MimeMultipart) {
				result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
			}
		}
		return result;
	}

	private String getTextFromBodyPart(BodyPart bodyPart) throws IOException, MessagingException {

		String result = "";
		if (bodyPart.isMimeType("text/plain")) {
			result = (String) bodyPart.getContent();
		} else if (bodyPart.isMimeType("text/html")) {
			String html = (String) bodyPart.getContent();
			result = org.jsoup.Jsoup.parse(html).text();
		} else if (bodyPart.getContent() instanceof MimeMultipart) {
			result = getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
		}
		return result;
	}
}