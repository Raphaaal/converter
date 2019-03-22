package toPdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class toPdf
{

    public static void main( String[] args )
    {
        long startTime = System.currentTimeMillis();

        try
        {
            // 1) Load docx with POI XWPFDocument
       	 FileInputStream fis = new FileInputStream("test.docx");
    	 XWPFDocument document=new XWPFDocument(OPCPackage.open(fis));

            // 2) Convert POI XWPFDocument 2 PDF with iText
            File outFile = new File( "target/test.pdf" );
            outFile.getParentFile().mkdirs();

            OutputStream out = new FileOutputStream( outFile );
            PdfOptions options = PdfOptions.create().fontEncoding( "windows-1250" );
            PdfConverter.getInstance().convert( document, out, options );
        }
        catch ( Throwable e )
        {
            e.printStackTrace();
        }
    }
}
