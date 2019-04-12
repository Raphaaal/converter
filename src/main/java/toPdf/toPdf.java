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

public class toPdf
{

    public static void main( String[] args ) {
        ConvertApi.convertFile("test.rtf", "result_rtf.pdf", "3q5DgWSGJshJhRKA");
        System.out.println("Convesion done.");
    }
    
}
