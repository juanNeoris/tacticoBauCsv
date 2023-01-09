package style;

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import lombok.Getter;

/**
 * Clase ConstantsStyles que contiene el resto de los Style y de las variables
 * que son usadas en CsvToExcel
 */
public class ConstantsStyles {

	/**
	 * Constante para directorio donde se depositara la interfaz con los Styles
	 * Requeridos
	 */
	public static final String BARRA = "\\";

	/**
	 * Constante para formtos que son de tipo double
	 */
	public static final String DOUBLE = "#,##0.00";
	/**
	 * Getter para obtener el 
	 * Style de requerido
	 * 
	 */
	@Getter
	private CellStyle styleN;
	/**
	 * Getter para obtener el 
	 * Style de requerido
	 * 
	 */
	@Getter
	private CellStyle styleG;
	/**
	 * Getter para obtener el 
	 * Style de requerido
	 * 
	 */
	@Getter
    private CellStyle styleDg;

	/**
	 * constante que guarda las posiciones que se deben de centrar
	 * 
	 */

	@Getter
	private ArrayList<Integer> centrados = new ArrayList<>(Arrays.asList(0, 1, 3, 7, 11, 13, 14, 15, 16,17,18,19,20,21,22,24,25,28,29,30,31,34));

	/**
	 * constante que guarda las posiciones que son numericos
	 * 
	 */

	@Getter
	private ArrayList<Integer> numericos = new ArrayList<>(Arrays.asList( 8,9,10,23,24,26, 27,32,33));

	/**
	 * Contructor que recibe el 
	 * workbook sobre el cual se 
	 * aplicaran los Styles
	 * @param hwb libro sobre el cual va a escribir
	 */
	public ConstantsStyles(HSSFWorkbook hwb) {
		super();
		styleN = createTotalesN(hwb);
		styleG = createTotalesG(hwb);
		styleDg = createWarningColor(hwb);
	}

	/**
	 * Metodo createTotales formato para los doubles totales generales del pais
	 * evaluado
	 * 
	 * @param workbook recive el excel sobre el cual se aplicara el style
	 * @return totalesN regresa el style ya definido
	 */
	public CellStyle createTotalesN(HSSFWorkbook workbook) {
		CellStyle totalesN = workbook.createCellStyle();
		HSSFFont fonntN = workbook.createFont();
		totalesN.setFillForegroundColor(IndexedColors.BLACK.getIndex());
		totalesN.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		totalesN.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
		totalesN.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
		fonntN.setColor(HSSFColorPredefined.WHITE.getIndex());
		totalesN.setFont(fonntN);
		return totalesN;
	}

	/**
	 * Metodo createTotalesG formato para los doubles totales generales del pais
	 * evaluado
	 * 
	 * @param workbook recive el excel sobre el cual se aplicara el style
	 * @return totalesG regresa el style ya definido
	 */
	public CellStyle createTotalesG(HSSFWorkbook workbook) {
		CellStyle totalesG = workbook.createCellStyle();
		HSSFFont fonntG = workbook.createFont();
		totalesG.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
		totalesG.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		totalesG.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
		totalesG.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
		fonntG.setColor(HSSFColorPredefined.WHITE.getIndex());
		fonntG.setBold((true));
		totalesG.setFont(fonntG);
		return totalesG;
	}
	
	/**
	 * Metodo createWarningColor define style de los totales para cada instrumento
	 * 
	 * @param workbook recive el excel sobre el cual se aplicara el style
	 * @return doubleGrey regresa el style ya definido
	 */
	public CellStyle createWarningColor(HSSFWorkbook workbook) {

		CellStyle doubleGrey = workbook.createCellStyle();
		HSSFFont fontW = workbook.createFont();
		doubleGrey.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
		doubleGrey.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		doubleGrey.setDataFormat(HSSFDataFormat.getBuiltinFormat(DOUBLE));
		doubleGrey.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.RIGHT);
		doubleGrey.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
		fontW.setColor(HSSFColorPredefined.WHITE.getIndex());
		fontW.setBold((true));
		doubleGrey.setFont(fontW);
		return doubleGrey;
	}

}
