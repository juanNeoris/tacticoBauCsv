package interfaz;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import conexion.Conexion;
import style.ConstantsStyles;
import style.Styles;
import view.vista;

/**
 * Clase que pasa de csv a
 *  excel con los Style´s requeridos
 */

public class CsvToExcel {

	
	/**
	 * Instancia de la conexion para obtener el nombre de la empresa que se va a
	 * evaluar
	 */
	public static final Conexion conection = new Conexion();
	/**
	 * Instancia de la LOGGER para mostrar mensajes
	 */
	private static final Logger LOGGER = Logger.getLogger(CsvToExcel.class.getName());
	/**
	 * Instancia de la variable workbook
	 */
	public static final HSSFWorkbook hwb = new HSSFWorkbook();
	/**
	 * Instancia de la stylesC para obtener los styles
	 */
	public static final Styles styles = new Styles(hwb);
	/**
	 * Instancia de la stylesC para obtener los styles
	 */
	public static final ConstantsStyles stylesC = new ConstantsStyles(hwb);
	/**
	 * Instancia de la variable valor
	 */
	public static final String VALOR = "null";


	/**
	  * metodo csvToExcel encargado de recorrer el archivo csv
	  * para aplicar los Styles
	  * @param nombreInterfaz recibe el nombre de la interfaz CSV
	  * @param grupo recibe el grupo para obtener el nombre de la empresa 
	  * @param date recibe la fecha que se usara para generar el nombre de la carpeta
	  * @throws Exception excepcion generada durante la ejecucion del proceso 
	  */
	public static void csvToExcel(String nombreInterfaz, String grupo, String date) throws Exception {
		ArrayList arList = extraeCSV(nombreInterfaz);
		HSSFSheet sheet = hwb.createSheet("Reporte");
		defaultColumnStyle(hwb, sheet);
		for (int k = 0; k < arList.size(); k++) {
			ArrayList ardata = (ArrayList) arList.get(k);
			HSSFRow row = sheet.createRow((short) 0 + k);
			for (int p = 0; p < ardata.size(); p++) {
				HSSFCell cell = row.createCell((short) p);
				String data = ardata.get(p).toString();
				validaStyle(data, cell, row, ardata, p);
			}
			int progreso = calcularAvance(arList.size(), k);
			vista.progressBar.setValue(progreso);
			vista.progressBar.update(vista.progressBar.getGraphics());
		}
		autoSizeStyle(sheet);
		cierraExcel(nombreInterfaz, date, grupo, hwb);
	}


   /**
	 * Metodo evaluaStyles metodo encargado de evaluar la cadena y posicion
	 * 
	 * @param data valor en la posicion actual
	 * @param cell celda a la que se aplicara el style
	 * @param row fila a la que se le aplicara el style
	 * @param ardata registro para validar el style
	 * @param p valor de la columna para validar el Style correspondiente
	 * @throws ParseException generada al parsear a double y fechas
	 */

	public static void validaStyle(String data, HSSFCell cell, HSSFRow row, ArrayList ardata, Integer p)
			throws ParseException {

		if (ardata.toString().contains("  -  ")) {
			row.setHeightInPoints(20);
			cell.setCellStyle(styles.getStyleT());
			cell.setCellValue(data);
		} else if (ardata.toString().contains("CPTYPARENT")) {
			row.setHeightInPoints(20);
			cell.setCellStyle(styles.getStyleE());
			cell.setCellValue(data);
		}else {
			validaStyleGarantia(data, cell,  ardata, p);
		}
		if (ardata.toString().contains("TOTAL GENERAL")) {
			if (p == 8 || p == 9 || p == 10) {
				double d = DecimalFormat.getNumberInstance().parse(data.trim()).doubleValue();
				cell.setCellStyle(styles.getStyleTg());
				cell.setCellValue(d);
			} else {
				row.setHeightInPoints(20);
				cell.setCellStyle(stylesC.getStyleN());
				cell.setCellValue(data);
			}
		}
		if (ardata.toString().contains("TOTALES ")) {
			if (p == 8 || p == 9 || p == 10) {
				double d = DecimalFormat.getNumberInstance().parse(data.trim()).doubleValue();
				cell.setCellStyle(stylesC.getStyleDg());
				cell.setCellValue(d);
			} else {
				data = data.replaceAll("\"", "");
				data = data.replaceAll("null", "");
				row.setHeightInPoints(15);
				cell.setCellStyle(stylesC.getStyleG());
				cell.setCellValue(data);
			}
		}
	}

   /**
	 * Metodo evaluaStyles metodo encargado de evaluar la cadena y posicion
	 * 
	 * @param data valor en la posicion actual
	 * @param cell celda a la que se aplicara el style
	 * @param ardata registro para validar el style
	 * @param p valor de la columna para validar el Style correspondiente
	 * @throws ParseException generada al parsear fecha o datos a double
	 */
	public static void validaStyleGarantia(String data, HSSFCell cell,  ArrayList ardata, Integer p) throws ParseException {
    /**
     * Se validan si el intrumento es una garantia 
     * y el Style sera de color vino ademas de 
     * que se aplicaran formatos a las fechas 
     * y valores double
     */
		data = data.replaceAll("\"", "");
		if (ardata.toString().contains("GARANTIA")) {
			if (stylesC.getCentrados().contains(p)) {
				cell.setCellStyle(styles.getStyleGc());
				cell.setCellValue(data);
			} else if (p == 5 || p == 6) {
				if (!data.trim().equals(VALOR) && !data.isEmpty()) {
					SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MMM-yy");
					Date date2 = formatter2.parse(data);
					cell.setCellStyle(styles.getStyleFc());
					cell.setCellValue(date2);
				} else {
					data = data.replaceAll("null", "-");
					cell.setCellValue(data);
				}
			} else if (p == 8 || p == 9 || p == 10) {
				double d = DecimalFormat.getNumberInstance().parse(data.trim()).doubleValue();
				cell.setCellStyle(styles.getStyleGd());
				cell.setCellValue(d);
			} else if (p == 2 || p == 4 || p == 12) {
				cell.setCellStyle(styles.getStyleGl());
				cell.setCellValue(data);
			}
			/**
			 * de no ser una garantia se
			 * aplicaran Styles a los nuemericos 
			 * y a las fechas 
			 */
		} else {
			if (p == 5 || p == 6) {
				if (!data.trim().equals(VALOR) && !data.isEmpty()) {
					SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MMM-yy");
					Date date2 = formatter2.parse(data);
					cell.setCellStyle(styles.getStyleF());
					cell.setCellValue(date2);
				}
			} else if (p == 8 || p == 9 || p == 10) {
				double d = DecimalFormat.getNumberInstance().parse(data.trim()).doubleValue();
				cell.setCellStyle(styles.getStyleD());
				cell.setCellValue(d);
				/**
				 * de no entrar en las condiciones 
				 * anteriores el valor se pasara 
				 * a la celda sin ningun Style 
				 * y tomara uno por default definido 
				 * mas adelante
				 */
			} else {
				cell.setCellValue(data);

			}
		}

	}

   /**
	 * Metodo extraeCSV se encarga de extraer el 
	 * archivo csv y meterlo en un array
	 * para depues ser evaluado
	 * 
	 * @param nombreInterfaz recibe el nombre de la interfaz que se guardara en el
	 *                       array
	 * @return arList regresa el array ya con la informacion del archivo csv
	 */
	public static ArrayList extraeCSV(String nombreInterfaz) {
		String fName = nombreInterfaz;
		String thisLine;
		ArrayList arLis = new ArrayList();
		ArrayList al = null;
		try (FileInputStream fis = new FileInputStream(fName); DataInputStream myInput = new DataInputStream(fis)) {
			int i = 0;
			while ((thisLine = myInput.readLine()) != null) {
				al = new ArrayList();
				String strar[] = thisLine.split("\\|");
				for (int j = 0; j < strar.length; j++) {
					al.add(strar[j]);
				}
				arLis.add(al);
				i++;
			}
		} catch (IOException e) {
			LOGGER.info(e);
		} finally {
			LOGGER.info("Archivo Cerrado");
		}
		return arLis;
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
		Double total = Double.valueOf(cantidadRegistros - 1.0);
		double avance = (indice / total) * 100.0;

		return (int) Math.round(avance);
	}

   /**
	 * Metodo cierraExcel que cierrra el archivo una vez se generaron los Styles
	 * 
	 * @param csv   recibe el nombre del archivo
	 * @param date  la fecha para crear las carpetas corrspondientes al mes y año
	 * @param grupo obtener el nombre de la empresa a la que corresponde el grupo
	 *              evaluado
	 * @param hwb   el archivo en si con todos los Style finales
	 * @throws Exception generada durante el cierre del archivo
	 */
	public static void cierraExcel(String csv, String date, String grupo, HSSFWorkbook hwb) throws Exception {

	   /**
		 * se obtiene el nombre de la interfza y se obtienen los caracteres hasta donde
		 * se encuentra las diagonales
		 */
		String[] interfazExle = csv.split("\\-");
		String part1 = interfazExle[0];
		/**
		 * se realiza el calculo de la fecha para construir la ruta donde sera
		 * depositado el fichero
		 */
		Date miFecha = new SimpleDateFormat("ddMMyyyy").parse(date);
		Calendar calendario = Calendar.getInstance();
		calendario.setTime(miFecha);
		int anio = calendario.get(Calendar.YEAR);
		int mes = calendario.get(Calendar.MONTH);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.MONTH, mes);
		String nameMonth = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
		String last3 = nameMonth.substring(0, 3);
		String mesnum = date.substring(2, 4);
		/**
		 * se establece la conexion para validar el nombre de la empresa al que
		 * pertenece el grupo evaluado
		 */
		conection.conecGBO();
		String empresa = conection.getNombreGrupo(grupo, date);
		String directoryName = System.getProperty("user.dir");
		conection.disconect();
		/**
		 * crea el directorio sobre el que se va a depositar el archivo
		 */
		File directorio = new File(directoryName + ConstantsStyles.BARRA + grupo + "-" + empresa + ConstantsStyles.BARRA
				+ anio + ConstantsStyles.BARRA + mesnum + " ) " + last3);
		/**
		 * valida si el directorio ya existe al igual que 
		 * el archivo de ser asi ya no lo
		 * vuelve a generer y muestra mensaje en pantalla
		 */
		if (!directorio.exists()) {
			if (directorio.mkdirs()) {
				FileOutputStream fileOut = new FileOutputStream(directorio + ConstantsStyles.BARRA + part1 + ".xls");
				hwb.write(fileOut);
				fileOut.close();
				LOGGER.info("Directorio creado");
			} else {
				LOGGER.info("Error al crear directorio");
			}
		}
		/**
		 * se cierra el archivo con todos los Styles
		 */
		FileOutputStream fileOut = new FileOutputStream(directorio + ConstantsStyles.BARRA + part1 + ".xls");
		hwb.write(fileOut);
		fileOut.close();
		LOGGER.info("Excel generado");
	}

   /**
	 * Metodo defaultColumnStyle aplica por default 
	 * un Style a las columnas a las
	 * que no se les aplico ningun Style
	 * 
	 * @param sheet la hoja que almacena la informacion
	 * @param hwb   el archivo en si con todos los Style finales
	 * 
	 */
	public static void defaultColumnStyle(HSSFWorkbook hwb, HSSFSheet sheet) {
		CellStyle cellStyle5 = hwb.createCellStyle();
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
	}

   /**
	 * Metodo autoSizeStyle aplica el tamaño automatico para las columna se les
	 * aplico ningun Style
	 * 
	 * @param sheet la hoja que almacena la informacion y guardara toda la
	 *              informacion del csv
	 * 
	 */
	public static void autoSizeStyle(HSSFSheet sheet) {
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
	}
}