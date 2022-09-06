package aiol.report;

import static aiol.report.constants.CommonConstants.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.Units;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import aiol.report.dto.ResultDto;;

public class App {

	public static void main(String[] args) throws Exception {
		readInputFiles();
	}

	private static void readInputFiles() throws Exception {
		File[] files = new File(".").listFiles();
		for (File file : files) {
			if (file.isFile() && file.getName().toLowerCase().endsWith(XLS_EXTENSION)
					&& file.getName().toLowerCase().startsWith(FILE_NAME_PREFIX)) {
				Map<String, List<ResultDto>> resultsMap = extractedData(file);
				createWordReport(resultsMap);
			}
		}
	}

	private static Map<String, List<ResultDto>> extractedData(File file) throws Exception {
		FileInputStream fis = new FileInputStream(file);
		HSSFWorkbook workbook = new HSSFWorkbook(fis);
		HSSFSheet sheet = workbook.getSheet(MEAN_SHEET_NAME);
		FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
		Map<String, List<ResultDto>> resultsMap = new HashMap<>();
		for (int rowNo = 1; rowNo < sheet.getLastRowNum() + 1; rowNo++) {
			Row row = sheet.getRow(rowNo);
			ResultDto resultDto = new ResultDto();
			resultDto.setDate(row.getCell(DATE_INDEX).getStringCellValue());
			resultDto.setSampleId(row.getCell(SAMPLE_ID_INDEX).getStringCellValue());
			resultDto.setAssay(row.getCell(ASSAY_INDEX).getStringCellValue());
			resultDto.setConcentration(row.getCell(CONCENTRATION_INDEX).getStringCellValue());
			resultDto.setUnit(row.getCell(UNIT_INDEX).getStringCellValue());
			if (!resultsMap.containsKey(resultDto.getSampleId())) {
				resultsMap.put(resultDto.getSampleId(), new ArrayList<>());
			}
			resultsMap.get(resultDto.getSampleId()).add(resultDto);
		}
		return resultsMap;
	}

	private static void createWordReport(Map<String, List<ResultDto>> resultsMap) throws Exception {
		resultsMap.entrySet().forEach(entry -> {
			List<ResultDto> resultDtos = entry.getValue();
			try (XWPFDocument document = new XWPFDocument()) {
				createResultTable(document, resultDtos);
				createHeaderFooter(document);
				writeToDocument(document);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private static void createResultTable(XWPFDocument document, List<ResultDto> resultDtos) {
		XWPFTable table = document.createTable();
		table.setWidth(9550);
        XWPFTableRow row1 = table.getRow(0);
        row1.getCell(0).setText(PARAMETERS);
        row1.addNewTableCell().setText(VALUES);
        row1.addNewTableCell().setText(UNIT);
        row1.addNewTableCell().setText(REFERENCE_VALUES);
        
        resultDtos.forEach(resultDto -> {
        	XWPFTableRow row = table.createRow();
            row.getCell(0).setText(resultDto.getAssay());
            row.getCell(1).setText(resultDto.getConcentration());
            row.getCell(2).setText(resultDto.getUnit());
            row.getCell(3).setText("Reference Value");
        });
	}

	private static void writeToDocument(XWPFDocument document) throws IOException, FileNotFoundException {
		try (OutputStream outputStream = new FileOutputStream(new File("header.docx"))) {
			document.write(outputStream);
		}
		document.close();
	}

	private static void createHeaderFooter(XWPFDocument document)
			throws InvalidFormatException, IOException, FileNotFoundException {
		XWPFHeader header = document.createHeader(HeaderFooterType.DEFAULT);
		header.createParagraph().createRun().addPicture(new FileInputStream(REPORT_HEADER), Document.PICTURE_TYPE_JPEG,
				REPORT_HEADER, Units.toEMU(480), Units.toEMU(100));
		XWPFFooter footer = document.createFooter(HeaderFooterType.DEFAULT);
		footer.createParagraph().createRun().addPicture(new FileInputStream(REPORT_FOOTER), Document.PICTURE_TYPE_JPEG,
				REPORT_FOOTER, Units.toEMU(480), Units.toEMU(50));
	}

}
