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

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.convertapi.ConvertApi;

import email.Attachment;
import email.EmailAttachmentReceiver;
import email.EmailAttachmentSender;

public class toPdf
{

	// DOCUMENTATION : https://github.com/ConvertAPI/convertapi-java

	//Test de bout en bout (v1)

	public static void main( String[] args ) throws InterruptedException {

		// TODO : WatchDogs 
		Queue<Attachment> messagesAttachments = new PriorityQueue<>(); // TODO : passer sur une PriorityQueue qui ordonne les attachments selon leur date

		//Récupération de l'inbox (IMAP)
		String host_receive = "ssl0.ovh.net"; // TODO : faire un while(true) pour interroger toutes les secondes la boite mail
		String port_receive = "993";
		String userName_receive = "topdf@middleman.paris";
		String password_receive = "azertyuiop";
		String saveDirectory = "./filesToConvert/";

		EmailAttachmentReceiver receiver = new EmailAttachmentReceiver();
		receiver.setSaveDirectory(saveDirectory);
		receiver.setMessagesAttachmentsFIFO(messagesAttachments);

		while(true) {

			receiver.downloadEmailAttachments(host_receive, port_receive, userName_receive, password_receive); // TODO : récupérer uniquement le dernier email avec une PJ (utiliser la date la plus récente de la Queue)
			int FIFOSize = messagesAttachments.size();
			Attachment currentConversion = null; 
			String[] attachmentsArray = null;

			for (int i = 0; i < FIFOSize; i++) {

				//Conversion with convertapi.com Java client
				currentConversion = messagesAttachments.poll();
				attachmentsArray = currentConversion.getName().split(",");

				System.out.println(attachmentsArray[0]);


				ConvertApi.convertFile("./filesToConvert/" + attachmentsArray[0], "./filesConverted/" + attachmentsArray[0] + ".pdf", "3q5DgWSGJshJhRKA"); // TODO : récupérer le nom du fichier téléchargé depuis l'inbox pour indiquer son path dynamiquement
				System.out.println("Conversion done.");

			}
			
			if(FIFOSize > 0) {

				//Envoi du fichier converti
				String host_send = "SSL0.OVH.NET";
				String port_send = "465";
				String mailFrom_send = "topdf@middleman.paris";
				String password_send = "azertyuiop";
				System.out.println(currentConversion.getSender());
				String mailTo = currentConversion.getSender(); // TODO : récupérer l'email de l'utilisateur pour lui renvoyer le fichier converti dynamiquement
				String subject = "New email with attachments in PDF";
				String message = "I have some attachments for you.";
				String[] attachFiles = new String[1];
				attachFiles[0] = "./filesConverted/" + attachmentsArray[0] + ".pdf";

				try {
					EmailAttachmentSender.sendEmailWithAttachments(host_send, port_send, mailFrom_send, password_send, mailTo, subject, message, attachFiles);
					System.out.println("Email sent.");
				} catch (Exception ex) {
					System.out.println("Could not send email.");
					ex.printStackTrace();
				}
			}
			// TODO : supprimer les fichiers à la fin
		}
	}

}
