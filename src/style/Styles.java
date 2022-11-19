package style;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

import lombok.Getter;

/**
 * clase que almacena los Styles que se aplicaran al archivo cuando se convierta
 * de csv a xls
 */
public class Styles {

	/**
	 * Constante para formtos que son de tipo double
	 */
	public static final String DOUBLE = "#,##0.00";
	/**
	 * Constante para formatos que son de tipo fecha
	 */
	public static final String FECHA = "d-mmm-yy";
	/**
	 * Getter para obtener el 
	 * Style de requerido
	 * 
	 */
	@Getter
	private CellStyle styleT;
	/**
	 * Getter para obtener el 
	 * Style de requerido
	 * 
	 */
	@Getter
	private CellStyle styleE;
	/**
	 * Getter para obtener el 
	 * Style de requerido
	 * 
	 */
	@Getter
	private CellStyle styleGc;
	/**
	 * Getter para obtener el 
	 * Style de requerido
	 * 
	 */
	@Getter
	private CellStyle styleFc;
	/**
	 * Getter para obtener el 
	 * Style de requerido
	 * 
	 */
	@Getter
	private CellStyle styleGd;
	/**
	 * Getter para obtener el 
	 * Style de requerido
	 * 
	 */
	@Getter
	private CellStyle styleGl;
	/**
	 * Getter para obtener el 
	 * Style de requerido
	 * 
	 */
	@Getter
	private CellStyle styleF;
	/**
	 * Getter para obtener el 
	 * Style de requerido
	 * 
	 */
	@Getter
	private CellStyle styleD;
	/**
	 * Getter para obtener el 
	 * Style de requerido
	 * 
	 */
	@Getter
	private CellStyle styleTg;
	
	
	/**
	 * Contructor que recibe el 
	 * workbook sobre el cual se 
	 * aplicaran los Styles
	 * @param hwb libro sobre el cual va a escribir
	 */
	public Styles(HSSFWorkbook hwb) {
		  super();
		 styleT = createTituto(hwb);	
		 styleE = createEncabezado(hwb);
		 styleGc = createGarantiaCenter(hwb);
		 styleFc = createGarantiaFecha(hwb);
		 styleGd = createGarantiaDouble(hwb);
		 styleGl = createGarantiaLeft(hwb);		
		 styleF = createFechas(hwb);
		 styleD = createDoubles(hwb);
		 styleTg = createTotales(hwb);
		 
	}
	

	
	/**
	 * Metodo createTituto formato para los titulos dependiedo del pais e
	 * instrumento
	 * 
	 * @param workbook recive el excel sobre el cual se aplicara el style
	 * @return tituloIns regresa el style ya definido
	 */
	public CellStyle createTituto(HSSFWorkbook workbook) {		
		CellStyle tituloIns = workbook.createCellStyle();
		HSSFFont fontR = workbook.createFont();
		tituloIns.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.LEFT);
		tituloIns.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
		fontR.setColor(HSSFColorPredefined.BLACK.getIndex());
		tituloIns.setFont(fontR);
		fontR.setBold((true));
		tituloIns.setFont(fontR);
		return tituloIns;
	}

	/**
	 * Metodo createEncabezado formato para los encabezados
	 * 
	 * @param workbook recive el excel sobre el cual se aplicara el style
	 * @return encabezado regresa el style ya definido
	 */
	public CellStyle createEncabezado(HSSFWorkbook workbook) {

		CellStyle encabezado = workbook.createCellStyle();
		HSSFFont fontW = workbook.createFont();
		encabezado.setFillForegroundColor(IndexedColors.RED1.getIndex());
		encabezado.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		encabezado.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER_SELECTION);
		encabezado.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
		fontW.setColor(HSSFColorPredefined.WHITE.getIndex());
		fontW.setBold((true));
		encabezado.setFont(fontW);
		return encabezado;
	}

	/**
	 * Metodo createGarantiaCenter formato para las garantias centradas
	 * 
	 * @param workbook recive el excel sobre el cual se aplicara el style
	 * @return garantiaCenter regresa el style ya definido
	 */
	public CellStyle createGarantiaCenter(HSSFWorkbook workbook) {
		CellStyle garantiaCenter = workbook.createCellStyle();
		HSSFFont fontRg = workbook.createFont();
		garantiaCenter.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
		garantiaCenter.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
		fontRg.setColor(HSSFColorPredefined.DARK_RED.getIndex());
		garantiaCenter.setFont(fontRg);
		return garantiaCenter;
	}

	/**
	 * Metodo createGarantiaFecha formato para las garantias fechas
	 * 
	 * @param workbook recive el excel sobre el cual se aplicara el style
	 * @return garantiaFecha regresa el style ya definido
	 */
	public CellStyle createGarantiaFecha(HSSFWorkbook workbook) {
		CellStyle garantiaFecha = workbook.createCellStyle();
		HSSFFont fonntF = workbook.createFont();
		garantiaFecha.setDataFormat(HSSFDataFormat.getBuiltinFormat(FECHA));
		garantiaFecha.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
		garantiaFecha.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
		fonntF.setColor(HSSFColorPredefined.DARK_RED.getIndex());
		garantiaFecha.setFont(fonntF);
		return garantiaFecha;
	}

	/**
	 * Metodo createGarantiaDouble formato para las garantias numericos
	 * 
	 * @param workbook recive el excel sobre el cual se aplicara el style
	 * @return garantiaFecha regresa el style ya definido
	 */
	public CellStyle createGarantiaDouble(HSSFWorkbook workbook) {
		CellStyle garantiaDouble = workbook.createCellStyle();
		HSSFFont fonntD = workbook.createFont();
		garantiaDouble.setDataFormat(HSSFDataFormat.getBuiltinFormat(DOUBLE));
		garantiaDouble.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.RIGHT);
		garantiaDouble.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
		fonntD.setColor(HSSFColorPredefined.DARK_RED.getIndex());
		garantiaDouble.setFont(fonntD);
		return garantiaDouble;
	}

	/**
	 * Metodo createGarantiaLeft formato para las garantias izquierda
	 * 
	 * @param workbook recive el excel sobre el cual se aplicara el style
	 * @return garantiaLeft regresa el style ya definido
	 */
	public CellStyle createGarantiaLeft(HSSFWorkbook workbook) {
		CellStyle garantiaLeft = workbook.createCellStyle();
		HSSFFont fonntL = workbook.createFont();
		garantiaLeft.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
		garantiaLeft.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.LEFT);
		fonntL.setColor(HSSFColorPredefined.DARK_RED.getIndex());
		garantiaLeft.setFont(fonntL);
		return garantiaLeft;
	}

	/**
	 * Metodo createFechas formato para las fechas
	 * 
	 * @param workbook recive el excel sobre el cual se aplicara el style
	 * @return garantiaLeft regresa el style ya definido
	 */
	public CellStyle createFechas(HSSFWorkbook workbook) {
		CellStyle fechas = workbook.createCellStyle();
		fechas.setDataFormat(HSSFDataFormat.getBuiltinFormat(FECHA));
		fechas.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
		fechas.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
		return fechas;
	}

	/**
	 * Metodo createDoubles formato para los docubles
	 * 
	 * @param workbook recive el excel sobre el cual se aplicara el style
	 * @return garantiaLeft regresa el style ya definido
	 */
	public CellStyle createDoubles(HSSFWorkbook workbook) {
		CellStyle doubles = workbook.createCellStyle();
		doubles.setDataFormat(HSSFDataFormat.getBuiltinFormat(DOUBLE));
		doubles.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.RIGHT);
		doubles.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
		return doubles;
	}

	/**
	 * Metodo createTotales formato para los doubles totales generales del pais
	 * evaluado
	 * 
	 * @param workbook recive el excel sobre el cual se aplicara el style
	 * @return totales regresa el style ya definido
	 */
	public CellStyle createTotales(HSSFWorkbook workbook) {
		CellStyle totales = workbook.createCellStyle();
		HSSFFont fonntT = workbook.createFont();
		totales.setFillForegroundColor(IndexedColors.BLACK.getIndex());
		totales.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		totales.setDataFormat(HSSFDataFormat.getBuiltinFormat(DOUBLE));
		totales.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
		totales.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
		fonntT.setColor(HSSFColorPredefined.WHITE.getIndex());
		fonntT.setBold((true));
		totales.setFont(fonntT);
		return totales;
	}

	
}
