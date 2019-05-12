package com.longvu.ted.export.pdf;

import java.io.IOException;
import java.net.MalformedURLException;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.longvu.ted.TEDtalk;

public class HeaderFooterPageEvent extends PdfPageEventHelper {

    private PdfTemplate t;
    private Image total;
    private TEDtalk talk;
    
    public HeaderFooterPageEvent(TEDtalk talk) {
    	this.talk = talk;
    }

    public void onOpenDocument(PdfWriter writer, Document document) {
        t = writer.getDirectContent().createTemplate(30, 16);
        try {
            total = Image.getInstance(t);
            total.setRole(PdfName.ARTIFACT);
        } catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        addHeader(writer);
        addFooter(writer);
    }

    private void addHeader(PdfWriter writer){
        PdfPTable header = new PdfPTable(2);
        try {
            // set defaults
            header.setWidths(new int[]{15, 85});
            header.setTotalWidth(PageSize.A4.getWidth() - 36*2);
            header.setLockedWidth(true);

            // add image
            Image logo = Image.getInstance(HeaderFooterPageEvent.class.getResource("/images/TED_three_letter_logo.png"));
            PdfPCell logoCell = new PdfPCell(logo, true);
            logoCell.setBorder(Rectangle.BOTTOM);
            logoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            logoCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            logoCell.setBorderColor(BaseColor.LIGHT_GRAY);
            logoCell.setPaddingBottom(7);
            header.addCell(logoCell);

            // add text
            Font font = FontFactory.getFont("RobotoSlab-Bold", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 13);
            PdfPCell text = new PdfPCell(new Phrase(talk.getTalkId(), font));
            text.setVerticalAlignment(Element.ALIGN_BOTTOM);
            text.setHorizontalAlignment(Element.ALIGN_RIGHT);
            text.setBorder(Rectangle.BOTTOM);
            text.setBorderColor(BaseColor.LIGHT_GRAY);
            text.setPaddingBottom(7);
            header.addCell(text);

            // write content
            header.writeSelectedRows(0, -1, 34, 810, writer.getDirectContent());
        } catch(DocumentException de) {
            throw new ExceptionConverter(de);
        } catch (MalformedURLException e) {
            throw new ExceptionConverter(e);
        } catch (IOException e) {
            throw new ExceptionConverter(e);
        }
    }

    private void addFooter(PdfWriter writer){
        PdfPTable footer = new PdfPTable(3);
        try {
            // set defaults
            footer.setWidths(new int[]{80, 15, 5});
            footer.setTotalWidth(527);
            footer.setLockedWidth(true);
            footer.getDefaultCell().setFixedHeight(40);
            footer.getDefaultCell().setBorder(Rectangle.TOP);
            footer.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);

            Font linkFont = FontFactory.getFont("RobotoSlab-Regular", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 8, Font.ITALIC);
            PdfPCell link = new PdfPCell(new Phrase(talk.getTalkUrl(), linkFont));
            link.setBorder(Rectangle.TOP);
            link.setBorderColor(BaseColor.LIGHT_GRAY);
            footer.addCell(link);
            
            // add current page count
            Font pageFont = FontFactory.getFont("RobotoSlab-Regular", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 8);
            footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            footer.addCell(new Phrase(String.format("Page %d of", writer.getPageNumber()), pageFont));

            // add placeholder for total page count
            PdfPCell totalPageCount = new PdfPCell(total);
            totalPageCount.setBorder(Rectangle.TOP);
            totalPageCount.setBorderColor(BaseColor.LIGHT_GRAY);
            footer.addCell(totalPageCount);
            
            // write page
            PdfContentByte canvas = writer.getDirectContent();
            canvas.beginMarkedContentSequence(PdfName.ARTIFACT);
            footer.writeSelectedRows(0, -1, 34, 50, canvas);
            canvas.endMarkedContentSequence();
        } catch(DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }

    public void onCloseDocument(PdfWriter writer, Document document) {
        int totalLength = String.valueOf(writer.getPageNumber()).length();
        int totalWidth = totalLength * 5;
        Font font = FontFactory.getFont("RobotoSlab-Regular", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 8);
        ColumnText.showTextAligned(t, Element.ALIGN_RIGHT,
                new Phrase(String.valueOf(writer.getPageNumber()), font),
                totalWidth, 6, 0);
    }
}