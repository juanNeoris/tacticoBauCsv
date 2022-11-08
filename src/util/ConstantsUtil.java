package util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import lombok.Getter;

/**
 * Clase de constantes se utilizan durante la ejecucion del programa y para la
 * configuracion del mismo
 */
public final class ConstantsUtil {

	/** Constante el formato de la fecha */

	/**
	 * Contante de formato
	 */
	public static final SimpleDateFormat sdf3 = new SimpleDateFormat("ddMMyyyy");

	/**
	 * Contante de formato
	 */
	public static final SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");

	/**
	 * Contante de formato
	 */
	public static final DecimalFormat DFORMATO = new DecimalFormat("####.########");
	/**
	 * array que almacena instrumentos Avales
	 */
	@Getter
	private static final ArrayList<String> avales = new ArrayList<>(Arrays.asList("AVAL COMERCIAL - NO COMPROMETIDO",
			"AVAL FINANCIERO - NO COMPROMETIDO", "AVAL NO DIRECTAMENTE DINERARIO - NO COMPROMETIDO",
			"AVAL TECNICO/PERFORMANCE - NO COMPROMETIDO", "GARANTIA LINE DE AVALES DISPONIBLES - NO USAR (3Q 2016)",
			"GARANTIA STANDBY LETTER OF CREDIT - NO USAR (3Q 2016)",
			"LINEA DE AVALES DISPONIBLES - NO COMPROMETIDO - NO USAR (3Q 2017)", "STANDBY LETTER OF CREDIT"));
	/**
	 * array que almacena instrumentos bonos
	 */
	@Getter
	private ArrayList<String> bonos = new ArrayList<>(
			Arrays.asList("BOND FORWARDS", "BOND FUTURES MMOO", "BOND OPTION MMOO", "BOND OPTIONS", "BOND REPOS",
					"BOND REPOS", "BOND REPOS UNDERLYING", "BOND SPOT", "COLLATERAL BOND", "COLLATERAL BOND UNDERLYING",
					"CORPORATE BONDS", "GOVERNMENT BOND LENDING FOP", "INFLATION SWAP PAYER BOND TYPE",
					"INFLATION SWAP RECEIVER BOND TYPE", "INITIAL MARGIN BOND", "INITIAL MARGIN BOND 3TH PARTY",
					"INITIAL MARGIN BOND UNDERLYING", "INITIAL MARGIN BOND UNDERLYING 3TH PARTY"));
	/**
	 * array que almacena instrumentos creditos
	 * documentarios
	 */
	@Getter
	private ArrayList<String> document = new ArrayList<>(Arrays.asList("APERTURA CREDITO DOCUMENTARIO",
			"CONFIRMACION CREDITO DOCUMENTARIO", "FINANCIACION CREDITO DOCUMENTARIO"));
	/**
	 * array que almacena instrumentos Importaciones/exportaciones
	 */
	@Getter
	private ArrayList<String> impExp = new ArrayList<>(Arrays.asList("FINANCIACION DE EXPORTACIONES - NO COMPROMETIDO",
			"FINANCIACION DE IMPORTACIONES - NO COMPROMETIDO"));
	/**
	 * array que almacena instrumentos Comex
	 */
	@Getter
	private ArrayList<String> comex = new ArrayList<>(
			Arrays.asList("OTRAS FINANCIACIONES COMEX - NO COMPROMETIDO - NO USAR (3Q 2016)",
					"OTRAS GARANTIAS COMEX  - NO USAR (3Q 2016)", "TRADE FINANCE - FORFAITING - NO COMPROMETIDO",
					"WAREHOUSING COMEX - NO COMPROMETIDO"));
	/**
	 * array que almacena instrumentos de
	 * creditos sindicados
	 */
	@Getter
	private ArrayList<String> sindi = new ArrayList<>(
			Arrays.asList("LINEA MULTIDEAL SINDICADO - COMPROMETIDO", "LINEA MULTIDEAL SINDICADO - NO COMPROMETIDO",
					"CREDITO SINDICADO - NO COMPROMETIDO", "CREDITO SINDICADO - COMPROMETIDO"));

	/**
	 * array que almacena instrumentos de confirming
	 */
	@Getter
	private ArrayList<String> confirm = new ArrayList<>(Arrays.asList("CONFIRMING - NO COMPROMETIDO"));

	/**
	 * array que almacena instrumentos descuentos
	 */
	@Getter
	private ArrayList<String> descuen = new ArrayList<>(
			Arrays.asList("DESCUENTOS - COMPROMETIDO", "DESCUENTOS - NO COMPROMETIDO"));
	
	/**
	 * array que almacena instrumentos factoring
	 */
	@Getter
	private ArrayList<String> factor = new ArrayList<>(Arrays.asList("FACTORING - NO COMPROMETIDO"));
	/**
	 * array que almacena instrumentos tarjetas de credito
	 */
	@Getter
	private ArrayList<String> tarjeta = new ArrayList<>(Arrays.asList("TARJETAS CREDITO - COMPROMETIDO"));
	
	/**
	 * array que almacena instrumentos lineas compormetidas
	 */
	@Getter
	private ArrayList<String> comprome = new ArrayList<>(Arrays.asList("LINEA MULTIDEAL RESTO -  COMPROMETIDO",
			"CREDITOS - COMPROMETIDO", "CREDITO BACKUP - COMPROMETIDO - NO USAR (3Q 2016)",
			"CREDITO OTROS - COMPROMETIDO - NO USAR (3Q 2016)"));
	/**
	 * array que almacena instrumentos garantias
	 */
	@Getter
	private ArrayList<String> garantia = new ArrayList<>(Arrays.asList("GARANTIA ACCIONES COTIZADAS",
			"GARANTIA AVAL FINANCIERO - NO USAR (3Q 2016)", "GARANTIA DERECHOS DE COBRO",
			"GARANTIA PERSONAL MANCOMUNADA", "GARANTIA PERSONAL SOLIDARIA", "GARANTIA PERSONAL SOLIDARIA FINAN",
			"OTRAS GARANTIAS EN EFECTIVO", "OTRAS GARANTIAS REALES NO LIQUIDAS", "OTHER GUARANTY CASH"));
	/**
	 * array que almacena instrumentos derivados
	 */
	@Getter
	private ArrayList<String> deriva = new ArrayList<>(Arrays.asList("ASSET SWAP", "CALL MONEY SWAP FLOATING FLOATING",
			"CALL MONEY SWAP FLOATING LONG", "CALL MONEY SWAP PAYER", "CALL MONEY SWAP RECEIVER",
			"CERTIFICATES OF DEPOSIT", "COLLAR KIKO", "COLLATERAL EQUITY", "COMMODITY FUTURES MMOO",
			"COMMODITY OPTIONS MMOO", "COMMODITY SWAP", "CREDIT DEFAULT SWAP - BOUGHT", "CREDIT DEFAULT SWAP - SOLD",
			"CREDIT DEFAULT SWAP UNDERLYING", "CURRENCY SWAPS", "DESCUBIERTOS CUENTAS VOSTRO", "EQUITY FORWARD",
			"EQUITY FUTURES MMOO", "EQUITY OPTION INDEX", "EQUITY OPTIONS", "EQUITY OPTIONS MMOO", "EQUITY SPOT",
			"EQUITY STOCK LENDING FOP", "EQUITY STOCK LENDING GBP", "EQUITY SWAPS", "FIXED INCOME LENDING UNDERLYING",
			"FIXED INFLATION SWAP PAYER", "FIXED INFLATION SWAP RECEIVER", "FLOATING RATE NOTES", "FORWARD ACOTADO",
			"FX - FORWARD", "FX - FORWARD AMERICANO", "FX  FUTURES MMOO", "FX - SPOT", "FX - SWAPS",
			"FX OPTIONS - MMOO", "FX OPTIONS - OTC", "FXTARKO", "GOVERNMENT SECURITIES", "INDEXED LINKED SWAP PAYER",
			"INDEXED LINKED SWAPRECEIVER", "INFLATION INTEREST RATE CAP", "INFLATION INTEREST RATE FLOOR",
			"INFLATION SWAP GENERIC DEALS", "INFLATION SWAP PAYER REVENUE", "INFLATION SWAP PAYER YEAR ON YEAR",
			"INFLATION SWAP PAYER ZERO COUPON", "INFLATION SWAP RECEIVER REVENUE",
			"INFLATION SWAP RECEIVER YEAR ON YEAR", "INFLATION SWAP RECEIVER ZERO COUPON", "INITIAL MARGIN CASH",
			"INITIAL MARGIN CASH 3TH PARTY", "INITIAL MARGIN EQUITY", "INTEREST RATE CAP", "INTEREST RATE FLOOR",
			"INTEREST RATE FORWARDS - FRA", "INTEREST RATE FUTURE - MMOO", "INTEREST RATE SWAP FLOATING FLOATING",
			"INTEREST RATE SWAP FLOATING LONG", "INTEREST RATE SWAP FLOATING SHORT", "INTEREST RATE SWAP PAYER",
			"INTEREST RATE SWAP RECEIVER", "INTEREST RATE SWAPS", "INTEREST RATE SWAPTIONS", "MONEY MARKET DEPOSITS",
			"MONEY MARKET PLACEMENTS", "MORTGAGE BACKED SECURITIES", "NON DELIVERABLE FORWARD", "OPTIONS RECEIVER",
			"OPTIONSPAYER", "SALDO EN CUENTAS NOSTRO", "SECURED FIXED INCOME LENDING",
			"SECURED FIXED INCOME LENDING FOP", "SECURED STOCK LENDING FOP", "SERIAL ZERO COUPON PAYER",
			"SERIAL ZERO COUPON RECEIVER", "SWAP BONIFICADO SAME FREQ", "SWAP FORWARD INFLACION",
			"TITULIZACION DE HIPOTECAS", "UNSECURED FIXED INCOME LENDING", "UNSECURED STOCK LENDING",
			"VARIATION MARGIN CASH", "VARIATION MARGIN CASH 3TH PARTY", "ZERO COUPON SWAP PAYER",
			"ZERO COUPON SWAPRECEIVER", "CURRENCY SWAP"

	));

	/**
	 * array que almacena instrumentos 
	 * creditos no comprometidos
	 */
	@Getter
	private ArrayList<String> nocomprome = new ArrayList<>(Arrays.asList("CREDITOS - NO COMPROMETIDO"));
	/**
	 * array que almacena instrumentos 
	 * overdrafts
	 */
	@Getter
	private ArrayList<String> overdraf = new ArrayList<>(Arrays.asList("OVERDRAFTS"));
	/**
	 * array que almacena instrumentos 
	 * leasing renting
	 */
	@Getter
	private ArrayList<String> learent = new ArrayList<>(Arrays.asList("LEASING", "RENTING"));
	/**
	 * contructor de la clase constantsUtils
	 */
	public ConstantsUtil() {
		// Constructor vacio para prevenir acceso ilegal
	}
}
