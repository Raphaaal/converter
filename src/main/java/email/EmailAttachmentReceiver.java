//https://www.codejava.net/java-ee/javamail/download-attachments-in-e-mail-messages-using-javamail
package email;

import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.FolderClosedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.MessageChangedEvent;
import javax.mail.event.MessageChangedListener;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeUtility;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

/**
 * This program demonstrates how to download e-mail messages and save
 * attachments into files on disk.
 *
 */
public class EmailAttachmentReceiver {

	private String saveDirectory;
	private Queue<Attachment> messagesAttachments;

	/**
	 * Sets the directory where attached files will be stored.
	 * @param dir absolute path of the directory
	 */
	public void setSaveDirectory(String dir) {
		this.saveDirectory = dir;
	}

	public void setMessagesAttachmentsFIFO(Queue<Attachment> messagesAttachments) {
		this.messagesAttachments = messagesAttachments;
	}

	/**
	 * Downloads new messages and saves attachments to disk if any.
	 * @param host
	 * @param port
	 * @param userName
	 * @param password
	 */
	private Date lastDate = new GregorianCalendar(2019, 01, 01).getTime();
	public void downloadEmailAttachments(String host, String port, String userName, String password) {
		Map<String, String> attachFilesNamesAndDate = new HashMap<String, String>();
		Properties properties = new Properties();

		// server setting
		properties.put("mail.imap.host", host);
		properties.put("mail.imap.port", port);

		// SSL setting
		properties.setProperty("mail.imap.socketFactory.class","javax.net.ssl.SSLSocketFactory");
		properties.setProperty("mail.imap.socketFactory.fallback", "false");
		properties.setProperty("mail.imap.socketFactory.port", String.valueOf(port));

		Session session = Session.getDefaultInstance(properties);

		try {
			// connects to the message store
			Store store = session.getStore("imap");
			store.connect(userName, password);

			// opens the inbox folder
			Folder folderInbox = store.getFolder("INBOX");
			folderInbox.open(Folder.READ_ONLY);

			// fetches new messages from server
			Message[] arrayMessages = folderInbox.getMessages();

			for (int i = 0; i < arrayMessages.length; i++) {
				Message message = arrayMessages[i];
				Address[] fromAddress = message.getFrom();
				String from = fromAddress[0].toString();
				String subject = message.getSubject();
				Date sentDate = message.getSentDate();

				String contentType = message.getContentType();
				String messageContent = "";

				// store attachment file name, separated by comma
				String attachFiles = "";

				if (contentType.contains("multipart")) {
					// content may contain attachments
					Multipart multiPart = (Multipart) message.getContent();
					int numberOfParts = multiPart.getCount();
					
					for (int partCount = 0; partCount < numberOfParts; partCount++) {
						
						MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
						if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
							// this part is attachment
							String encodedAttachmentName = MimeUtility.decodeText(part.getFileName()); 
							String decodedAttachmentname=  Normalizer.normalize(encodedAttachmentName , Normalizer.Form.NFC);
							String fileName = decodedAttachmentname;
							System.out.println("fileName: "+ fileName);
							// TODO : Pb lorsque le mail contient plusieurs PJ
							attachFiles += fileName + ",";
							File fileToSave = new File(fileName);
							// OLD - part.saveFile(saveDirectory + File.separator + fileName);
							part.saveFile(saveDirectory + File.separator + fileToSave);
						} else {
							// this part may be the message content
							messageContent = part.getContent().toString();
						}
						
					}
					if (!attachFiles.equals("") && sentDate.after(lastDate)) {
						String sender = from.substring(from.indexOf("<") + 1, from.indexOf(">"));
						messagesAttachments.offer(new Attachment(attachFiles, sender, sentDate));
						System.out.println("messagesAttachments: " + messagesAttachments);
						lastDate = sentDate;
					}

					if (attachFiles.length() > 1) {
						attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
					}
				} else if (contentType.contains("text/plain")
						|| contentType.contains("text/html")) {
					Object content = message.getContent();
					if (content != null) {
						messageContent = content.toString();
					}
				}
				
				
				// print out details of each message
				System.out.println("Message #" + (i + 1) + ":");
				System.out.println("\t From: " + from);
				System.out.println("\t Subject: " + subject);
				System.out.println("\t Sent Date: " + sentDate);
				System.out.println("\t Message: " + messageContent);
				System.out.println("\t Attachments: " + attachFiles);
				System.out.println("-------------------------------");
				
			}

			// disconnect
			folderInbox.close(false);
			store.close();
		} catch (NoSuchProviderException ex) {
			System.out.println("No provider for pop3.");
			ex.printStackTrace();
		} catch (MessagingException ex) {
			System.out.println("Could not connect to the message store");
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Checks for new received emails
	 * 
	 * @param host
	 * @param storeType
	 * @param user
	 * @param password
	 */
	public static void check(String host, String port, 
			//String storeType, 
			String user,
			String password) {
		try {

			//create properties field
			Properties properties = new Properties();

			properties.put("mail.imap.host", host);
			properties.put("mail.imap.port", port);
			properties.put("mail.imap.starttls.enable", "true");
			Session emailSession = Session.getDefaultInstance(properties);


			IMAPStore imapStore = (IMAPStore) emailSession.getStore("imaps");
			// imapStore.connect();

			imapStore.connect(host, user, password);

			final IMAPFolder folder = (IMAPFolder) imapStore.getFolder("Inbox");
			folder.open(IMAPFolder.READ_WRITE);

			folder.addMessageCountListener(new MessageCountListener() {

				public void messagesAdded(MessageCountEvent e) {
					System.out.println("Message Count Event Fired");
				}

				public void messagesRemoved(MessageCountEvent e) {
					System.out.println("Message Removed Event fired");
				}

			});

			folder.addMessageChangedListener(new MessageChangedListener() {

				public void messageChanged(MessageChangedEvent e) {
					System.out.println("Message Changed Event fired");
				}

			});


			// Check mail once in "freq" MILLIseconds
			int freq = 2000;
			boolean supportsIdle = false;
			try {
				if (folder instanceof IMAPFolder) {
					IMAPFolder f = (IMAPFolder) folder;
					f.idle();
					supportsIdle = true;
				}
			} catch (FolderClosedException fex) {
				throw fex;
			} catch (MessagingException mex) {
				supportsIdle = false;
			}
			for (; ; ) {
				if (supportsIdle && folder instanceof IMAPFolder) {
					IMAPFolder f = (IMAPFolder) folder;
					f.idle();
					System.out.println("IDLE done");
				} else {
					Thread.sleep(freq); // sleep for freq milliseconds

					// This is to force the IMAP server to send us
					// EXISTS notifications.
					folder.getMessageCount();
				}
			}



			/*
// retrieve the messages from the folder in an array and print it
Message[] messages = emailFolder.getMessages();
System.out.println("messages.length---" + messages.length);

for (int i = 0, n = messages.length; i < n; i++) {
Message message = messages[i];
System.out.println("---------------------------------");
System.out.println("Email Number " + (i + 1));
System.out.println("Subject: " + message.getSubject());
System.out.println("From: " + message.getFrom()[0]);
System.out.println("Text: " + message.getContent().toString());

}

			 */


			//close the store and folder objects
			//   emailFolder.close(false);
			//   store.close();

		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Runs this program with Gmail POP3 server
	 */
	public static void main(String[] args) {
		//IMAP
		String host = "ssl0.ovh.net";
		String port = "993";
		// POP : 
		//String port = "995";
		String userName = "topdf@middleman.paris";
		String password = "azertyuiop";

		String saveDirectory = "./filesToConvert/";

		EmailAttachmentReceiver receiver = new EmailAttachmentReceiver();
		receiver.setSaveDirectory(saveDirectory);
		receiver.downloadEmailAttachments(host, port, userName, password);

		check(host, port, userName, password);


	}
}
