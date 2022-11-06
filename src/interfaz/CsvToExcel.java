package interfaz;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import conexion.Conexion;
import view.vista;

/**
 * Clase que pasa de csv a excel con los Style´s requeridos
 */

public class CsvToExcel {

	public static Conexion conection = new Conexion();
	private static final Logger LOGGER = Logger.getLogger(vista.class.getName());

	/**
	 * Metodo csvToExcel que pasa de csv a excel con los Style´s requeridos
	 * reciviendo como parametro
	 * 
	 * @param nombreInterfaz nombre de la interfaz
	 * @param grupo          el grupo
	 * @param date           la fecha
	 */
	public static void csvToExcel(String nombreInterfaz, String grupo, String date) {
		ArrayList arList = null;
		ArrayList al = null;
		String fName = nombreInterfaz;
		String csv = nombreInterfaz;
		String thisLine;

		String valor = "null";

		try {

			FileInputStream fis = new FileInputStream(fName);
			DataInputStream myInput = new DataInputStream(fis);
			int i = 0;
			arList = new ArrayList();

			while ((thisLine = myInput.readLine()) != null) {
				al = new ArrayList();
				String strar[] = thisLine.split("\\|");
				for (int j = 0; j < strar.length; j++) {
					al.add(strar[j]);
				}
				arList.add(al);
				i++;
			}
			fis.close();
			myInput.close();
		} catch (IOException e) {
			LOGGER.info(e);
		}

		try {

			HSSFWorkbook hwb = new HSSFWorkbook();
			HSSFSheet sheet = hwb.createSheet("Reporte");
			CreationHelper createHelper = hwb.getCreationHelper();

			CellStyle cellStyle = hwb.createCellStyle();
			CellStyle cellStyle1 = hwb.createCellStyle();
			CellStyle cellStyle2 = hwb.createCellStyle();
			CellStyle cellStyle3 = hwb.createCellStyle();
			CellStyle cellStyle4 = hwb.createCellStyle();
			CellStyle cellStyle5 = hwb.createCellStyle();
			CellStyle cellStyle7 = hwb.createCellStyle();
			CellStyle cellStylefecha = hwb.createCellStyle();
			cellStylefecha.setDataFormat(createHelper.createDataFormat().getFormat("dd-mmm-aa"));
			CellStyle my_style_1 = hwb.createCellStyle();
			CellStyle my_style_2 = hwb.createCellStyle();
			CellStyle my_style_3 = hwb.createCellStyle();
			CellStyle my_style_4 = hwb.createCellStyle();
			CellStyle my_style_5 = hwb.createCellStyle();
			CellStyle my_style_6 = hwb.createCellStyle();

			HSSFFont cellFont = hwb.createFont();

			HSSFFont font = hwb.createFont();
			HSSFFont fontR = hwb.createFont();
			HSSFFont fontW = hwb.createFont();

			cellStyle5.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

			sheet.setDefaultColumnStyle(0, cellStyle5);
			sheet.setDefaultColumnStyle(1, cellStyle5);
			sheet.setDefaultColumnStyle(3, cellStyle5);
			sheet.setDefaultColumnStyle(5, cellStyle5);
			sheet.setDefaultColumnStyle(6, cellStyle5);
			sheet.setDefaultColumnStyle(7, cellStyle5);
			sheet.setDefaultColumnStyle(8, cellStyle5);
			sheet.setDefaultColumnStyle(9, cellStyle5);
			sheet.setDefaultColumnStyle(10, cellStyle5);
			sheet.setDefaultColumnStyle(11, cellStyle5);
			sheet.setDefaultColumnStyle(13, cellStyle5);
			sheet.setDefaultColumnStyle(14, cellStyle5);
			sheet.setDefaultColumnStyle(15, cellStyle5);
			sheet.setDefaultColumnStyle(16, cellStyle5);
			for (int k = 0; k < arList.size(); k++) {
				ArrayList ardata = (ArrayList) arList.get(k);
				HSSFRow row = sheet.createRow((short) 0 + k);

				for (int p = 0; p < ardata.size(); p++) {
					HSSFCell cell = row.createCell((short) p);
					String data = ardata.get(p).toString();

					if (ardata.toString().contains("MEXICO - ") || ardata.toString().contains(" - TARJETA DE CREDITO")
							|| ardata.toString().contains(" - LINEAS") || ardata.toString().contains(" - GARANTIAS")
							|| ardata.toString().contains(" - FINANCIAMIENTO")
							|| ardata.toString().contains(" - FACTORING") || ardata.toString().contains(" - DESCUENTOS")
							|| ardata.toString().contains(" - DERIVADOS") || ardata.toString().contains(" - CREDITOS")
							|| ardata.toString().contains(" - CONFIRMING") || ardata.toString().contains(" - BONOS")
							|| ardata.toString().contains(" - AVAL")) {

						cellStyle7.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.LEFT);
						cellStyle7.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
						cellFont.setColor(HSSFColorPredefined.BLACK.getIndex());
						cellStyle7.setFont(cellFont);
						cellFont.setBold((true));
						cellStyle7.setFont(cellFont);
						row.setHeightInPoints(20);
						cell.setCellStyle(cellStyle7);
						cell.setCellValue(data);
					} else if (ardata.toString().contains("CPTYPARENT")) {

						cellStyle1.setFillForegroundColor(IndexedColors.RED1.getIndex());
						cellStyle1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						cellStyle1.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER_SELECTION);
						cellStyle1.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
						font.setColor(HSSFColorPredefined.WHITE.getIndex());
						font.setBold((true));
						cellStyle1.setFont(font);
						row.setHeightInPoints(20);
						cell.setCellStyle(cellStyle1);
						cell.setCellValue(data);

					} else if (data.startsWith("\"")) {
						data = data.replaceAll("\"", "");
						if (ardata.toString().contains("GARANTIA")) {

							if (p == 0 || p == 1 || p == 3 || p == 7 || p == 11 || p == 13 || p == 14 || p == 15
									|| p == 16) {

								cellStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
								cellStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);

								fontR.setColor(HSSFColorPredefined.DARK_RED.getIndex());
								cellStyle.setFont(fontR);
								cell.setCellStyle(cellStyle);
								cell.setCellValue(data);

							} else if (p == 5 || p == 6) {

								if (!data.trim().equals(valor)) {
									SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MMM-yy");
									Date date2 = formatter2.parse(data);

									my_style_1.setDataFormat(HSSFDataFormat.getBuiltinFormat("d-mmm-yy"));
									my_style_1.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
									my_style_1
											.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
									fontR.setColor(HSSFColorPredefined.DARK_RED.getIndex());
									my_style_1.setFont(fontR);
									cell.setCellStyle(my_style_1);
									cell.setCellValue(date2);

								} else {
									data = data.replaceAll("null", "-");
									cell.setCellValue(data);
								}
							} else if (p == 8 || p == 9 || p == 10) {

								double d = DecimalFormat.getNumberInstance().parse(data.trim()).doubleValue();

								my_style_4.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
								my_style_4.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.RIGHT);
								my_style_4.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
								fontR.setColor(HSSFColorPredefined.DARK_RED.getIndex());
								my_style_4.setFont(fontR);
								cell.setCellStyle(my_style_4);
								cell.setCellValue(d);

							} else if (p == 2 || p == 4 || p == 12) {
								cellStyle3.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);

								cellStyle3.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.LEFT);
								fontR.setColor(HSSFColorPredefined.DARK_RED.getIndex());
								cellStyle3.setFont(fontR);
								cell.setCellStyle(cellStyle3);
								cell.setCellValue(data);
							}
						}
						if (p == 5 || p == 6) {

							if (!data.trim().equals(valor) && !data.isEmpty()) {
								SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MMM-yy");
								Date date2 = formatter2.parse(data);
								my_style_2.setDataFormat(HSSFDataFormat.getBuiltinFormat("d-mmm-yy"));
								my_style_2.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
								my_style_2.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
								cell.setCellStyle(my_style_2);
								cell.setCellValue(date2);
							}

						} else if (p == 8 || p == 9 || p == 10) {

							double d = DecimalFormat.getNumberInstance().parse(data.trim()).doubleValue();

							my_style_3.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
							my_style_3.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.RIGHT);
							my_style_3.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
							cell.setCellStyle(my_style_3);
							cell.setCellValue(d);

						} else {
							cell.setCellValue(data);
						}

					} else {
						data = data.replaceAll("\"", "");

						if (ardata.toString().contains("GARANTIA")) {

							if (p == 0 || p == 1 || p == 3 || p == 7 || p == 11 || p == 13 || p == 14 || p == 15
									|| p == 16) {

								cellStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
								cellStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);

								fontR.setColor(HSSFColorPredefined.DARK_RED.getIndex());
								cellStyle.setFont(fontR);
								cell.setCellStyle(cellStyle);
								cell.setCellValue(data);

							} else if (p == 5 || p == 6) {

								if (!data.trim().equals(valor)) {
									SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MMM-yy");
									Date date2 = formatter2.parse(data);
									my_style_1.setDataFormat(HSSFDataFormat.getBuiltinFormat("d-mmm-yy"));
									my_style_1.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
									my_style_1
											.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
									fontR.setColor(HSSFColorPredefined.DARK_RED.getIndex());
									my_style_1.setFont(fontR);
									cell.setCellStyle(my_style_1);
									cell.setCellValue(date2);

								} else {
									data = data.replaceAll("null", "-");
									cell.setCellValue(data);
								}
							} else if (p == 8 || p == 9 || p == 10) {

								double d = DecimalFormat.getNumberInstance().parse(data.trim()).doubleValue();

								my_style_4.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
								my_style_4.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.RIGHT);
								my_style_4.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
								fontR.setColor(HSSFColorPredefined.DARK_RED.getIndex());
								my_style_4.setFont(fontR);
								cell.setCellStyle(my_style_4);
								cell.setCellValue(d);

							} else if (p == 2 || p == 4 || p == 12) {
								cellStyle3.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.LEFT);
								cellStyle3.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);

								fontR.setColor(HSSFColorPredefined.DARK_RED.getIndex());
								cellStyle3.setFont(fontR);
								cell.setCellStyle(cellStyle3);
								cell.setCellValue(data);

							}
						} else {

							if (p == 5 || p == 6) {
								if (!data.trim().equals(valor) && !data.isEmpty()) {
									SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MMM-yy");
									Date date2 = formatter2.parse(data);
									my_style_2.setDataFormat(HSSFDataFormat.getBuiltinFormat("d-mmm-yy"));
									my_style_2.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
									my_style_2
											.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
									cell.setCellStyle(my_style_2);
									cell.setCellValue(date2);
								}
							} else if (p == 8 || p == 9 || p == 10) {

								double d = DecimalFormat.getNumberInstance().parse(data.trim()).doubleValue();

								my_style_3.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
								my_style_3.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.RIGHT);
								my_style_3.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
								cell.setCellStyle(my_style_3);
								cell.setCellValue(d);

							} else {
								cell.setCellValue(data);
							}
						}
					}
					if (ardata.toString().contains("TOTAL GENERAL")) {

						if (p == 8 || p == 9 || p == 10) {

							double d = DecimalFormat.getNumberInstance().parse(data.trim()).doubleValue();

							my_style_6.setFillForegroundColor(IndexedColors.BLACK.getIndex());
							my_style_6.setFillPattern(FillPatternType.SOLID_FOREGROUND);
							my_style_6.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
							my_style_6.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
							my_style_6.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
							fontW.setColor(HSSFColorPredefined.WHITE.getIndex());
							fontW.setBold((true));
							my_style_6.setFont(fontW);
							cell.setCellStyle(my_style_6);
							cell.setCellValue(d);

						} else {
							cellStyle2.setFillForegroundColor(IndexedColors.BLACK.getIndex());
							cellStyle2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
							cellStyle2.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
							cellStyle2.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);

							font.setColor(HSSFColorPredefined.WHITE.getIndex());
							cellStyle2.setFont(font);
							row.setHeightInPoints(20);
							cell.setCellStyle(cellStyle2);
							cell.setCellValue(data);
						}

					} else if (ardata.toString().contains("TOTAL TARJETAS DE CREDITO")
							|| ardata.toString().contains("TOTAL LINEAS NO COMPROMETIDAS")
							|| ardata.toString().contains("TOTAL LINEAS COMPROMETIDAS")
							|| ardata.toString().contains("TOTAL GARANTIAS")
							|| ardata.toString().contains("TOTAL FINANCIAMIENTO IMP/EXP")
							|| ardata.toString().contains("TOTAL FINANCIAMIENTO COMEX")
							|| ardata.toString().contains("TOTAL FACTORING")
							|| ardata.toString().contains("TOTAL DESCUENTOS")
							|| ardata.toString().contains("TOTAL DERIVADOS")
							|| ardata.toString().contains("TOTAL CREDITOS SINDICADOS")
							|| ardata.toString().contains("TOTAL CREDITOS DOCUMENTARIOS")
							|| ardata.toString().contains("TOTAL CONFIRMING")
							|| ardata.toString().contains("TOTAL BONOS")
							|| ardata.toString().contains("TOTAL AVALES")) {

						if (p == 8 || p == 9 || p == 10) {

							double d = DecimalFormat.getNumberInstance().parse(data.trim()).doubleValue();

							my_style_5.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
							my_style_5.setFillPattern(FillPatternType.SOLID_FOREGROUND);
							my_style_5.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
							my_style_5.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.RIGHT);
							my_style_5.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
							fontW.setColor(HSSFColorPredefined.WHITE.getIndex());
							fontW.setBold((true));
							my_style_5.setFont(fontW);
							cell.setCellStyle(my_style_5);
							cell.setCellValue(d);

						} else {

							cellStyle4.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
							cellStyle4.setFillPattern(FillPatternType.SOLID_FOREGROUND);
							cellStyle4.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
							cellStyle4.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
							fontW.setColor(HSSFColorPredefined.WHITE.getIndex());
							fontW.setBold((true));
							cellStyle4.setFont(fontW);
							row.setHeightInPoints(15);
							cell.setCellStyle(cellStyle4);
							cell.setCellValue(data);

						}
					}
				}
				int progreso = calcularAvance(arList.size(), k);
				vista.progressBar.setValue(progreso);
				vista.progressBar.update(vista.progressBar.getGraphics());

			}

			sheet.setColumnWidth(0, 25 * 256);
			sheet.setColumnWidth(1, 25 * 256);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4);
			sheet.autoSizeColumn(5);
			sheet.autoSizeColumn(6);
			sheet.autoSizeColumn(7);
			sheet.autoSizeColumn(8);
			sheet.autoSizeColumn(9);
			sheet.autoSizeColumn(10);
			sheet.autoSizeColumn(11);
			sheet.autoSizeColumn(12);
			sheet.autoSizeColumn(13);
			sheet.autoSizeColumn(14);
			sheet.autoSizeColumn(15);
			sheet.autoSizeColumn(16);
			// 0 , 1 ,3--centrado 5-11--centrado 13-16--centrado

			String[] interfazExle = csv.split("\\-");

			String part1 = interfazExle[0];

			Date miFecha = new SimpleDateFormat("ddMMyyyy").parse(date);

			// creo un calendario
			Calendar calendario = Calendar.getInstance();
			// establezco mi fecha
			calendario.setTime(miFecha);

			// obtener el año
			int anio = calendario.get(Calendar.YEAR);
			// obtener el mes (0-11 ::: enero es 0 y diciembre es 11)
			int mes = calendario.get(Calendar.MONTH);
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.set(Calendar.MONTH, mes);
			String nameMonth = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
			String last3 = nameMonth.substring(0, 3);
			String mesnum = date.substring(2, 4);

			conection.conecGBO();
			String Empresa = conection.getNombreGrupo(grupo, date);
			String directoryName = System.getProperty("user.dir");

			File directorio = new File(
					directoryName + "\\" + grupo + "-" + Empresa + "\\" + anio + "\\" + mesnum + " ) " + last3);
			if (!directorio.exists()) {
				if (directorio.mkdirs()) {
					FileOutputStream fileOut = new FileOutputStream(directorio + "\\" + part1 + ".xls");

					hwb.write(fileOut);
					fileOut.close();
					LOGGER.info("Directorio creado");
				} else {
					LOGGER.info("Error al crear directorio");

				}
			}
			FileOutputStream fileOut = new FileOutputStream(directorio + "\\" + part1 + ".xls");

			hwb.write(fileOut);
			fileOut.close();
			LOGGER.info("Excel generado");

		} catch (Exception ex) {
			LOGGER.info(ex);
		}

	}

	/**
	 * Metodo que realiza el calculo del avance para enviarselo al progressbar
	 * 
	 * @param cantidadRegistros recibe la cantidad de registos
	 * @param posicion          en la que se encuentra
	 * @return progreso realiza el calculo y regresa el porcentaje
	 */
	public static int calcularAvance(int cantidadRegistros, int posicion) {

		Double indice = Double.valueOf(posicion);
		Double total = Double.valueOf(cantidadRegistros - 1);
		double avance = (indice / total) * 100.0;

		return (int) Math.round(avance);
	}

}
