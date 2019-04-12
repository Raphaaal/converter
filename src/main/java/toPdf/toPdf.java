package toPdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.convertapi.ConvertApi;

import email.EmailAttachmentReceiver;
import email.EmailAttachmentSender;

public class toPdf
{

	// DOCUMENTATION : https://github.com/ConvertAPI/convertapi-java
	//Test de bout en bout (v1)

    public static void main( String[] args ) {
    	
    	//Récupération de l'inbox (IMAP)
        String host_receive = "ssl0.ovh.net"; // TODO : passer en IMAP pour être notifé lors de l'arrivée d'un email ou faire un while pour interroger toutes les secondes ?
        String port_receive = "993";
        String userName_receive = "topdf@middleman.paris";
        String password_receive = "azertyuiop";
        String saveDirectory = "./filesToConvert/";
        EmailAttachmentReceiver receiver = new EmailAttachmentReceiver();
        receiver.setSaveDirectory(saveDirectory);
        receiver.downloadEmailAttachments(host_receive, port_receive, userName_receive, password_receive); // TODO : récupérer uniquement le dernier email avec une PJ
        
        //Conversion
        ConvertApi.convertFile("./filesToConvert/test.rtf", "./filesConverted/test.pdf", "3q5DgWSGJshJhRKA"); // TODO : récupérer le nom du fichier téléchargé depuis l'inbox pour indiquer son path dynamiquement
        System.out.println("Convesion done.");
        
        //Envoi du fichier converti
        String host_send = "SSL0.OVH.NET";
        String port_send = "465";
        String mailFrom_send = "topdf@middleman.paris";
        String password_send = "azertyuiop";
        String mailTo = "azorin.raphael@gmail.com"; // TODO : récupérer l'email de l'utilisateur pour lui renvoyer le fichier converti dynamiquement
        String subject = "New email with attachments";
        String message = "I have some attachments for you.";
        String[] attachFiles = new String[1];
        attachFiles[0] = "./filesConverted/test.pdf";
 
        try {
        	EmailAttachmentSender.sendEmailWithAttachments(host_send, port_send, mailFrom_send, password_send, mailTo, subject, message, attachFiles);
            System.out.println("Email sent.");
        } catch (Exception ex) {
            System.out.println("Could not send email.");
            ex.printStackTrace();
        }
        // TODO : supprimer les fichiers à la fin
    }
    
}
