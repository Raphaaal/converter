package toPdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.convertapi.ConvertApi;

import email.Attachment;
import email.EmailAttachmentReceiver;
import email.EmailAttachmentSender;

import com.sun.mail.imap.protocol.FLAGS;  

public class toPdf {

	public static void main( String[] args ) throws InterruptedException, MessagingException {

		Queue<Attachment> messagesAttachments = new PriorityQueue<Attachment>(); 
		// Get inbox (IMAP)
		String host_receive = "ssl0.ovh.net"; 
		String port_receive = "993";
		String userName_receive = "topdf@middleman.paris";
		String password_receive = "@Azerty123";
		String saveDirectory = "./filesToConvert/";

		EmailAttachmentReceiver receiver = new EmailAttachmentReceiver();
		receiver.setSaveDirectory(saveDirectory);
		receiver.setMessagesAttachmentsFIFO(messagesAttachments);

		while(true) {

			receiver.downloadEmailAttachments(host_receive, port_receive, userName_receive, password_receive);
			int FIFOSize = messagesAttachments.size();
			Attachment currentConversion = null; 
			String[] attachmentsArray = null;

			System.out.println("Number of emails: " + FIFOSize);

			for (int k = 0; k < FIFOSize; k++) {
				currentConversion = messagesAttachments.poll();				
				attachmentsArray = currentConversion.getName().split(","); // Separate each file name

				for (int i = 0; i < attachmentsArray.length; i++) {
					System.out.println("Attachment n°: " + i + " - " + attachmentsArray[i]);
					try {
						ConvertApi.convertFile("./filesToConvert/" + attachmentsArray[i], "./filesConverted/" + attachmentsArray[i] + ".pdf", "tm0Pujim5xPRYRYW");
						System.out.println("Conversion done.");
					}
					catch (Exception ex) {
						System.out.println("Could not convert file.");
						ex.printStackTrace();
					}
				}

				// Send converted file
				String host_send = "SSL0.OVH.NET";
				String port_send = "465";
				String mailFrom_send = "topdf@middleman.paris";
				String password_send = "@Azerty123";
				System.out.println(currentConversion.getSender());
				String mailTo = currentConversion.getSender();
				String subject = "New email with attachments in PDF";
				String message = "I have some attachments for you.";
				String[] attachFiles = new String[attachmentsArray.length];
				for (int i = 0; i < attachmentsArray.length; ++i) {
					System.out.println("attachmentArray " + i + ": " + attachmentsArray[i]);
					attachFiles[i] = "./filesConverted/" + attachmentsArray[i] + ".pdf";
				}

				try {
					EmailAttachmentSender.sendEmailWithAttachments(host_send, port_send, mailFrom_send, password_send, mailTo, subject, message, attachFiles);
					System.out.println("Email sent.");
					System.out.println("-------------------------");

				} catch (Exception ex) {
					System.out.println("Could not send email.");
					ex.printStackTrace();
					System.out.println("-------------------------");
				}

			}

			// Delete files to convert and files converted
			if (attachmentsArray != null) {
				for (int i = 0; i < attachmentsArray.length; i++) {
					File fileToConvert = new File("./filesToConvert/" + attachmentsArray[i]);
					File fileConverted = new File("./filesConverted/" + attachmentsArray[i] + ".pdf");
					if(fileToConvert.exists())
						fileToConvert.delete();
					if(fileConverted.exists())
						fileConverted.delete();
				}
			}

			Thread.currentThread().sleep(5000); // Pause btw. each inbox check
		}
	}
}
