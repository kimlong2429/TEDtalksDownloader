package com.longvu.ted.actions.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.apache.poi.hssf.usermodel.HeaderFooter;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.inject.Inject;
import com.longvu.ted.TEDtalk;
import com.longvu.ted.actions.api.IGenerateAction;
import com.longvu.ted.model.TranscriptResponse.StandardizedTranscript;
import com.longvu.ted.utils.TranscriptDocument;

public class XlsxGenerateActionImpl implements IGenerateAction {

	private ExecutorService executor;
	
	@Inject
	public XlsxGenerateActionImpl(ExecutorService executor) {
		this.executor = executor;
	}

	@Override
	public CompletableFuture<Object> generate(TEDtalk talk, TranscriptDocument script) {
		return CompletableFuture.supplyAsync(() -> {
			Workbook workbook = new XSSFWorkbook();
			generateVerticalSheet(talk, workbook, script);
			generateHorizontalSheet(talk, workbook, script);
			return workbook;
		}, executor);
	}
	
	private void generateVerticalSheet(TEDtalk talk, Workbook wb, TranscriptDocument script) {
		Sheet sheet = wb.createSheet("Vertical");
		pageSetup(talk, sheet, false);
		
		int inserted = insertIntroductions(talk, wb, sheet, false);
		
		for (int i = 0; i < script.getEnSubtitle().getParagraphs().size(); i++) {
			insertToCellVertical(wb, sheet, script.getEnSubtitle(), script.getViSubtitle(), i, inserted);
		}
		
		sheet.setColumnWidth(0, 25000);
	}
	
	private void generateHorizontalSheet(TEDtalk talk, Workbook wb, TranscriptDocument script) {
		Sheet sheet = wb.createSheet("Horizontal");
		pageSetup(talk, sheet, true);
		
		int inserted = insertIntroductions(talk, wb, sheet, true);
		
		for (int i = 0; i < script.getEnSubtitle().getParagraphs().size(); i++) {
			insertToCellHorizontal(wb, sheet, script.getEnSubtitle(), "en", i, inserted);
			insertToCellHorizontal(wb, sheet, script.getViSubtitle(), "vi", i, inserted);
		}
		
		sheet.setColumnWidth(0, 25000);
		sheet.setColumnWidth(1, 750);
		sheet.setColumnWidth(2, 750);
		sheet.setColumnWidth(3, 25000);
	}

	private int insertIntroductions(TEDtalk talk, Workbook wb, Sheet sheet, boolean isHorizontal) {
		// set title
		Row row = sheet.getRow(0);
		if (row == null) {
			row = sheet.createRow(0);
		}
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setWrapText(true);
		
		Font font = wb.createFont();
		font.setBold(true);
		font.setFontHeightInPoints((short)16);
		cellStyle.setFont(font);
		
		Cell cell = row.createCell(0, CellType.STRING);
		cell.setCellStyle(cellStyle);
		cell.setCellValue(talk.getTitle());
		
		// set description
		row = sheet.getRow(1);
		if (row == null) {
			row = sheet.createRow(1);
		}
		
		cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.JUSTIFY);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setWrapText(true);
		
		font = wb.createFont();
		font.setItalic(true);
		cellStyle.setFont(font);
		
		cell = row.createCell(0, CellType.STRING);
		cell.setCellStyle(cellStyle);
		cell.setCellValue(talk.getDescription());
		
		if (isHorizontal) {
			int toRow = (int) Math.ceil((double)talk.getDescription().length()/253);
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
			sheet.addMergedRegion(new CellRangeAddress(1, toRow, 0, 3));
			return 3 + toRow;
		}
		
		return 4;
	}

	private void insertToCellHorizontal(Workbook wbHorizontal, Sheet sheet, StandardizedTranscript script, String language, int i, int inserted) {
		Row row = sheet.getRow(inserted + i*2);
		if (row == null) {
			row = sheet.createRow(inserted + i*2);
			
			// empty row
			Row empty = sheet.createRow(inserted + i*2 + 1);
			CellStyle cellStyle = wbHorizontal.createCellStyle();
			cellStyle.setBorderRight(BorderStyle.THIN);
			Cell cell = empty.createCell(1, CellType.STRING);
			cell.setCellStyle(cellStyle);
		}
		
		CellStyle cellStyle = wbHorizontal.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.JUSTIFY);
		cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
		cellStyle.setWrapText(true);
		
		Font font = wbHorizontal.createFont();
		
		Cell cell = null;
		if (language.contentEquals("en")) {
			cell = row.createCell(0, CellType.STRING);
			font.setBold(true);
			
			// empty cell
			CellStyle style = wbHorizontal.createCellStyle();
			style.setBorderRight(BorderStyle.THIN);
			Cell emptyCell = row.createCell(1, CellType.STRING);
			emptyCell.setCellStyle(style);
		} else {
			cell = row.createCell(3, CellType.STRING);
			font.setItalic(true);
		}
		
		cellStyle.setFont(font);
        cell.setCellStyle(cellStyle);
		cell.setCellValue(script.getParagraphs().get(i).getParagraph());
	}
	
	private void insertToCellVertical(Workbook wbVertical, Sheet sheet, StandardizedTranscript en, StandardizedTranscript vi, int i, int inserted) {
		Row rowEn = sheet.getRow(inserted + i*3);
		if (rowEn == null) {
			rowEn = sheet.createRow(inserted + i*3);
		}
		
		Row rowVi = sheet.getRow(inserted + i*3 + 1);
		if (rowVi == null) {
			rowVi = sheet.createRow(inserted + i*3 + 1);
			sheet.createRow(inserted + i*3 + 2);
		}
		
		CellStyle cellStyleEn = wbVertical.createCellStyle();
		cellStyleEn.setAlignment(HorizontalAlignment.JUSTIFY);
		cellStyleEn.setVerticalAlignment(VerticalAlignment.TOP);
		cellStyleEn.setWrapText(true);
		
		Font font = wbVertical.createFont();
		font.setBold(true);
		cellStyleEn.setFont(font);
		
		// cell for en script
		Cell cellEn = rowEn.createCell(0, CellType.STRING);;
        cellEn.setCellStyle(cellStyleEn);
		cellEn.setCellValue(en.getParagraphs().get(i).getParagraph());
		
		CellStyle cellStyleVi = wbVertical.createCellStyle();
		cellStyleVi.setAlignment(HorizontalAlignment.JUSTIFY);
		cellStyleVi.setVerticalAlignment(VerticalAlignment.TOP);
		cellStyleVi.setWrapText(true);
		
		font = wbVertical.createFont();
		font.setItalic(true);
		cellStyleVi.setFont(font);
		
		// cell for vi script
		Cell cellVi = rowVi.createCell(0, CellType.STRING);;
		cellVi.setCellStyle(cellStyleVi);
		cellVi.setCellValue(vi.getParagraphs().get(i).getParagraph());
	}

	private void pageSetup(TEDtalk talk, Sheet sheet, boolean isHorizontal) {
		sheet.setMargin(Sheet.TopMargin, 0.55);
		sheet.setMargin(Sheet.BottomMargin, 0.25);
		sheet.setMargin(Sheet.LeftMargin, 0.15);
		sheet.setMargin(Sheet.RightMargin, 0.15);
		sheet.setMargin(Sheet.HeaderMargin, 0.15);
		sheet.setMargin(Sheet.FooterMargin, 0);
		sheet.setHorizontallyCenter(true);
		sheet.setFitToPage(true);
		
		Header header = sheet.getHeader();
		header.setCenter(talk.getTalkId() + " | " + talk.getTalkUrl());
		
		Footer footer = sheet.getFooter();
		footer.setCenter("Page " + HeaderFooter.page());
		
		PrintSetup setup = sheet.getPrintSetup();
		setup.setFitWidth((short) 1);
		setup.setFitHeight((short) 0);
		setup.setPaperSize(PrintSetup.A4_PAPERSIZE);
		
		if (isHorizontal) {
			setup.setLandscape(true);
		} else {
			setup.setLandscape(false);
		}
	}

}
