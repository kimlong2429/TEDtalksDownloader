package com.longvu.ted.export.pdf;

import java.io.File;
import java.io.FileOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.longvu.ted.TEDtalk;
import com.longvu.ted.actions.api.IExportAction;
import com.longvu.ted.model.DuoDocument;
import com.longvu.ted.model.DuoDocument.Line;
import com.longvu.ted.model.DuoDocument.Section;
import com.longvu.ted.utils.TEDutils;

public class PdfExportAction implements IExportAction<Document>{
	private static Logger logger = LoggerFactory.getLogger(PdfExportAction.class);

	@Inject
	private TEDtalk talk;
	
	@Override
	public Document export(DuoDocument document) {
		String fileName = TEDutils.generateFileName(talk);
		String filePath = System.getProperty("user.dir")+ File.separator + fileName + ".pdf";
		
		// create new pdf document
		Document pdf = null;
		PdfWriter writer = null;
		
		try {
			pdf = new Document(PageSize.A4, 36, 36, 100, 50);
			writer = PdfWriter.getInstance(pdf, new FileOutputStream(filePath));
			writer.setPageEvent(new HeaderFooterPageEvent(talk));
			pdf.open();
			
			pdf.addAuthor(talk.getAuthor());
			pdf.addSubject(talk.getTitle());
			pdf.addTitle(talk.getTitle());
			pdf.addCreationDate();
			
			// write data
			writeTEDtalkInformation(pdf);
			writeTableContent(pdf, document);
			
			talk.setFileName(fileName + ".pdf");
			logger.debug("Saved to file: {}", talk.getFileName());
		} catch (Exception e) {
			logger.error("Failed to export to pdf ", e);
		} finally {
			if (pdf != null) {
				pdf.close();
			}
			if (writer != null) {
				writer.close();
			}
		}
		
		return pdf;
	}
	
	private void writeTEDtalkInformation(Document pdf) {
		try {
			Font titleFont = FontFactory.getFont("Lobster-Regular", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 25);
			Font authorFont = FontFactory.getFont("ZillaSlab-Regular", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 13);
			
			// write title and author 
			Paragraph p = new Paragraph();
			Chunk title = new Chunk(talk.getTitle() + "\n", titleFont);
			Chunk author = new Chunk(talk.getAuthor(), authorFont);
			p.add(title);
			p.add(author);
			
			float maxWidth = title.getWidthPoint() > author.getWidthPoint() ? title.getWidthPoint() : author.getWidthPoint();
			
			PdfPTable info = new PdfPTable(1);
			info.setTotalWidth(maxWidth);
			info.setLockedWidth(true);
			
			PdfPCell cell = new PdfPCell(p);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPadding(0);
			cell.setLeading(20, 0);
			cell.setNoWrap(true);
			cell.setBorder(Rectangle.NO_BORDER);
			
			info.addCell(cell);
			info.setHorizontalAlignment(Element.ALIGN_CENTER);
			
			pdf.add(info);
			
			// write description
			Font enFont = FontFactory.getFont("Quicksand-Medium", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12, Font.ITALIC);
			p = new Paragraph(talk.getDescription(), enFont);
			p.setAlignment(Rectangle.ALIGN_JUSTIFIED);
			p.setFirstLineIndent(30);
			p.setSpacingBefore(40);
			
			pdf.add(p);
		} catch (DocumentException e) {
			logger.error("Failed to write talk's informations ", e);
		}
	}

	private void writeTableContent(Document pdf, DuoDocument document) {
		// TODO write table content
		logger.debug("Registered fonts: {}", FontFactory.getRegisteredFonts());
		
		try {
			PdfPTable table = new PdfPTable(2);
			table.setWidthPercentage(100);
			table.setWidths(new int[]{50, 50});
			table.setSpacingBefore(40);
			
			Font enFont = FontFactory.getFont("Quicksand-Medium", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 10);
			Font viFont = FontFactory.getFont("OpenSans-Regular", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 10, Font.ITALIC);

			for(int i = 0; i < document.getSections().size(); i++) {
				Section section = document.getSections().get(i);
				Paragraph pSource = new Paragraph();
				Paragraph pTranslated = new Paragraph();
				
				for(int j = 0; j < section.getLines().size(); j++) {
					Line line = section.getLines().get(j);
					
					// source line
					Phrase prSource = new Phrase(line.getSource() != null ? line.getSource() : "", enFont);
					
					// translated line
					Phrase prTranslated = new Phrase(line.getTranslate() != null ? line.getTranslate() : "", viFont);
					
					if (j > 0) {
						pSource.add(" ");
						pTranslated.add(" ");
					}

					pSource.add(prSource);
					pTranslated.add(prTranslated);
				}
				
				if (section.getLines().size() > 1) {
					pSource.setFirstLineIndent(20);
					pTranslated.setFirstLineIndent(20);
				}
				
				PdfPCell cSource = new PdfPCell(pSource);
				cSource.setBorder(Rectangle.RIGHT);
				cSource.setBorderColor(BaseColor.LIGHT_GRAY);
				cSource.setVerticalAlignment(Element.ALIGN_TOP);
				cSource.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
				cSource.setPaddingRight(10);
				
				PdfPCell cTranslated = new PdfPCell(pTranslated);
				cTranslated.setBorder(Rectangle.NO_BORDER);
				cTranslated.setVerticalAlignment(Element.ALIGN_TOP);
				cTranslated.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
				cTranslated.setPaddingLeft(10);
				
				if (i > 0) {
					cSource.setPaddingTop(20);
					cTranslated.setPaddingTop(20);
				}
				
				table.addCell(cSource);
				table.addCell(cTranslated);
			}
			
			pdf.add(table);
		} catch (Exception e) {
			logger.error("Failed to write talk's content ", e);
		}
	}
}
