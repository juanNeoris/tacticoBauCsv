package conexion;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import interfaz.AvalBonos;
import interfaz.Csv;
import oracle.jdbc.pool.OracleDataSource;

/**
 * Clase conexión para BD vía Wallet
 * 
 * @author hmh
 */
public class Conexion {
	
	private static final Logger LOGGER = LogManager.getLogger(Conexion.class);
	private Connection con;
	private PreparedStatement pstmt;
	private Statement stmt;
	private ResultSet rs;
	private StringBuilder strbSql;
	private static final DecimalFormat DFORMATO = new DecimalFormat("###,###,###.##");
	
	private Properties getPro = cargaProperties();
	

	public Conexion() {

		super();
		this.con = null;
		this.pstmt = null;
		this.stmt = null;
		this.rs = null;
	}

	private Properties cargaProperties() {
		Properties properties = new Properties();
		try (InputStream inStream = new FileInputStream(
				System.getProperty("user.dir") + "\\" + "hostProperties.properties")) {
			properties.load(inStream);
		} catch (IOException e) {

			LOGGER.info("Error al cargar el archivo properties" + e);
		}
		return properties;
	}

	/**
	 * Obtener una conexión a la base de datos, obteniendo los valores de los
	 * exports hecho a la sesión
	 * 
	 * @throws Exception
	 */
	public void conecGBO() throws Exception {

		OracleDataSource ods = new OracleDataSource();

		String connString = getPro.getProperty("bbdd.jdbc") + getPro.getProperty("bbdd.host") + ":"
				+ getPro.getProperty("bbdd.puerto") + ":" + getPro.getProperty("bbdd.sid");

		ods.setURL(connString);
		ods.setUser(getPro.getProperty("bbdd.usuario"));
		ods.setPassword(getPro.getProperty("bbdd.contrasena"));
		this.con = ods.getConnection();
		this.con.setAutoCommit(false);
		;

	}

	/**
	 * Cierra la conexión a la base de datos
	 * 
	 * @throws SQLException
	 */
	public void closeResources() throws SQLException {
		if (rs != null) {
			rs.close();
			rs = null;
		}

		if (pstmt != null) {
			pstmt.close();
			pstmt = null;
		}

		if (stmt != null) {
			stmt.close();
			stmt = null;
		}
	}

	/**
	 * Cierra la conexión a la base de datos
	 * 
	 * @throws SQLException
	 */
	public void disconect() throws SQLException {

		closeResources();

		if (con != null && !con.isClosed()) {
			con.close();
			con = null;
		}
	}

	/**
	 * Metodo que valida la carga Dolphing
	 * 
	 * @param fecha recive como parametro para la consulta que valida la carga
	 * @throws SQLException atrapa la excepcion generada por el query
	 * @return systCode regresa lo que obtiene de ejecutar el query
	 */

	public String getCargaDolphingHistorico(String fecha) throws SQLException {
		strbSql = new StringBuilder();
		String systCode = "";
		strbSql.append("SELECT FECHACARGA FROM PGT_MEX.T_PGT_MEX_CONSUMOSC_D  WHERE TRUNC(FECHACARGA)='" + fecha + "'");
		try {
			pstmt = con.prepareStatement(strbSql.toString());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				systCode = rs.getString(1);
			} else {
				systCode = "No hay ultima carga";
			}
		} catch (SQLException e) {

			LOGGER.info(e);
		}

		return systCode;
	}

	/**
	 * Metodo que consulta los instrumentos para los paises Mexico
	 * 
	 * @param grupo          usado para la consulta de instrumentos
	 * @param nombreInterfaz donde se escribiran los instrumentos
	 * @param fechaConsumo   quenera la consulta del dia deseado
	 * @throws Exception atrapa la excepcion generada durante la ejecucion
	 * @return systCode regresa lo que obtiene de ejecutar el query
	 */
	public String getConsultaMexico(String grupo, String nombreInterfaz, String fechaConsumo) throws Exception {
		Statement sta = con.createStatement();
		Csv interfazCsv = new Csv();
		AvalBonos aval = new AvalBonos("AVALES");
		AvalBonos bono = new AvalBonos("BONOSS");
		AvalBonos confirming = new AvalBonos("CONFIRMING");
		AvalBonos documentariado = new AvalBonos("CREDITOS DOCUMENTARIOS");
		AvalBonos sindicado = new AvalBonos("CREDITOS SINDICADOS");
		AvalBonos derivados = new AvalBonos("DERIVADOS");
		AvalBonos descuentos = new AvalBonos("DESCUENTOS");
		AvalBonos factoring = new AvalBonos("FACTORING");
		AvalBonos comex = new AvalBonos("FINANCIAMIENTO COMEX");
		AvalBonos impexp = new AvalBonos("FINANCIAMIENTO IMPEXP");
		AvalBonos sumatoria = new AvalBonos("sumatoria");
		AvalBonos leasrent = new AvalBonos("LEASING - RENTING");
		AvalBonos comprome = new AvalBonos("LINEAS COMPROMETIDAS");
		AvalBonos nocompro = new AvalBonos("LINEAS NO COMPROMETIDAS");
		AvalBonos tarjeta = new AvalBonos("TARJETAS DE CREDITO");
		AvalBonos over = new AvalBonos("OVERDRAFTS");
		double sumatoriaNomValCur;
		double sumatoriaCer;
		double sumatoriaNomVal;

		List<AvalBonos> instrumento = new ArrayList<AvalBonos>();
		String pais = "";
		// Garantias Mexico
		List<String> MexicoGaran = new ArrayList<String>();
		// Garantias sumatoria Mexico
		List<Double> MexicoGaranValCurSum = new ArrayList<Double>();
		List<Double> MexicoGaranCerSum = new ArrayList<Double>();
		List<Double> MexicoGaranNomValSum = new ArrayList<Double>();
		List<String> info = new ArrayList<String>();
		ArrayList<String> contraparte = new ArrayList<String>();

		String systCode = "SELECT cptyparent, NVL(cptyparentrating, 'SIN RATING'),cptyparentname,dealstamp,instrumentname,TO_CHAR(TO_DATE(valuedate,  'YYYY-MM-DD'), 'DD-mon-YY'),TO_CHAR(TO_DATE(maturitydate,  'YYYY-MM-DD'), 'DD-mon-YY'),currency,to_char(DECODE(nominalvaluecur,null, '0.0',nominalvaluecur), '999,999,999,999.99')  AS nominalvaluecur,to_char(DECODE(CER,null, '0.0',CER), '999,999,999,999.99')  AS CER,to_char(DECODE(nominalvalue,null, '0.0',nominalvalue), '999,999,999,999.99')  AS nominalvalue,oneoff,cptyname,foldercountryname,cptycountry,cptyparentcountry,foldercountry from PGT_MEX.T_PGT_MEX_CONSUMOSC_V WHERE LastParentF ='"+ grupo + "' and FECHACARGA='" + fechaConsumo+ "' AND foldercountryname='Mexico' UNION ALL SELECT cptyparent, NVL(cptyparentrating, 'SIN RATING'),cptyparentname,dealstamp,instrumentname,TO_CHAR(TO_DATE(valuedate,  'YYYY-MM-DD'), 'DD-mon-YY'),TO_CHAR(TO_DATE(maturitydate,  'YYYY-MM-DD'), 'DD-mon-YY'),currency,to_char(DECODE(nominalvaluecur,null, '0.0',nominalvaluecur), '999,999,999,999.99')  AS nominalvaluecur,to_char(DECODE(CER,null, '0.0',CER), '999,999,999,999.99')  AS CER,to_char(DECODE(nominalvalue,null, '0.0',nominalvalue), '999,999,999,999.99')  AS nominalvalue,oneoff,cptyname,foldercountryname,cptycountry,cptyparentcountry,foldercountry from PGT_MEX.T_PGT_MEX_CONSUMOSC_D WHERE LastParentF ='"+ grupo + "' and FECHACARGA='" + fechaConsumo+ "' AND foldercountryname='Mexico' ORDER BY foldercountryname,instrumentname ";
		ResultSet rs = sta.executeQuery(systCode);
		 int total = getQueryRowCount(systCode);
		 System.out.println(total);
		
		if (rs.equals(null) || rs.next() == false) {
			systCode = "No existen registros para este grupo en la interfaz CONSULTA";
		} else {
			do {
				// cadena
				systCode = rs.getString(1) + "|" + rs.getString(2) + "|" + "\"" + rs.getString(3) + "\"" + "|"+ rs.getString(4) + "|" + rs.getString(5) + 							"|" + rs.getString(6) + "|" + rs.getString(7) + "|"+ rs.getString(8) + "|" + rs.getString(9) + "|" + rs.getString(10) + "|" + 							rs.getString(11)+ "|" + rs.getString(12) + "|" + "\"" + rs.getString(13) + "\"" + "|" + rs.getString(14) + "|"+ "\"" + 							rs.getString(15) + "\"" + "|" + rs.getString(16) + "|" + rs.getString(17) + "\n";

				// cadena obtener la sumatoria
				sumatoriaNomValCur = DecimalFormat.getNumberInstance().parse(rs.getString(9).trim()).doubleValue();
				sumatoriaCer = DecimalFormat.getNumberInstance().parse(rs.getString(10).trim()).doubleValue();
				sumatoriaNomVal = DecimalFormat.getNumberInstance().parse(rs.getString(11).trim()).doubleValue();
				if (rs.getString(5).contains("BOND")) {
					bono.bonos(fechaConsumo, systCode, rs.getString(4), rs.getString(14), rs.getString(9),rs.getString(10), rs.getString(11));
					sumatoria.bonos(fechaConsumo, systCode, rs.getString(4), rs.getString(14), rs.getString(9),rs.getString(10), rs.getString(11));
					info = this.getContraparte( fechaConsumo, rs.getString(4), rs.getString(14));
					contraparte.addAll(info);
					instrumento.add(bono);
					instrumento.add(sumatoria);
				} else if (rs.getString(5).contains(" CREDITO DOCUMENTARIO")) {
					documentariado.bonos(fechaConsumo,systCode, rs.getString(4), rs.getString(14),rs.getString(9),rs.getString(10),rs.getString(11));
					sumatoria.bonos(fechaConsumo, systCode, rs.getString(4), rs.getString(14), rs.getString(9),rs.getString(10), rs.getString(11));
					info = this.getContraparte( fechaConsumo, rs.getString(4), rs.getString(14));
					contraparte.addAll(info);
					instrumento.add(documentariado);
					instrumento.add(sumatoria);
				} else if (rs.getString(5).contains("EXPORTACION") || rs.getString(5).contains("IMPORTACION")) {
					impexp.bonos(fechaConsumo, systCode, rs.getString(4), rs.getString(14),  rs.getString(9), rs.getString(10), rs.getString(11));
					sumatoria.bonos(fechaConsumo, systCode, rs.getString(4), rs.getString(14), rs.getString(9), rs.getString(10), rs.getString(11));
					info = this.getContraparte( fechaConsumo, rs.getString(4), rs.getString(14));
					contraparte.addAll(info);
					instrumento.add(impexp);
					instrumento.add(sumatoria);
				} else if (rs.getString(5).contains("COMEX") || rs.getString(5).contains("FORFAITING")) {
					comex.bonos(fechaConsumo, systCode, rs.getString(4), rs.getString(14),  rs.getString(9), rs.getString(10), rs.getString(11));
					sumatoria.bonos(fechaConsumo, systCode, rs.getString(4), rs.getString(14), rs.getString(9), rs.getString(10), rs.getString(11));
					info = this.getContraparte( fechaConsumo, rs.getString(4), rs.getString(14));
					contraparte.addAll(info);
					instrumento.add(comex);
					instrumento.add(sumatoria);
				} else if (rs.getString(5).contains("SINDICADO")) {
					sindicado.bonos(fechaConsumo, systCode, rs.getString(4), rs.getString(14),rs.getString(9), rs.getString(10), rs.getString(11));
					sumatoria.bonos(fechaConsumo, systCode, rs.getString(4), rs.getString(14),rs.getString(9), rs.getString(10), rs.getString(11));
					info = this.getContraparte( fechaConsumo, rs.getString(4), rs.getString(14));
					contraparte.addAll(info);
					instrumento.add(sindicado);
					instrumento.add(sumatoria);
				} else if (rs.getString(5).contains("CONFIRMING")) {
					confirming.bonos(fechaConsumo, systCode, rs.getString(4), rs.getString(14),rs.getString(9), rs.getString(10), rs.getString(11));
					sumatoria.bonos(fechaConsumo, systCode, rs.getString(4), rs.getString(14), rs.getString(9), rs.getString(10), rs.getString(11));
					info = this.getContraparte( fechaConsumo, rs.getString(4), rs.getString(14));
					contraparte.addAll(info);
					instrumento.add(confirming);
					instrumento.add(sumatoria);
				} else if (rs.getString(5).contains("DESCUENTOS")) {
					descuentos.bonos(fechaConsumo, systCode, rs.getString(4), rs.getString(14),rs.getString(9), rs.getString(10), rs.getString(11));
					sumatoria.bonos(fechaConsumo, systCode, rs.getString(4), rs.getString(14),rs.getString(9), rs.getString(10), rs.getString(11));
					info = this.getContraparte( fechaConsumo, rs.getString(4), rs.getString(14));
					contraparte.addAll(info);
					instrumento.add(descuentos);
					instrumento.add(sumatoria);
				} else if (rs.getString(5).contains("FACTORING")) {
					factoring.bonos(fechaConsumo, systCode, rs.getString(4), rs.getString(14),rs.getString(9), rs.getString(10), rs.getString(11));
					sumatoria.bonos(fechaConsumo, systCode, rs.getString(4), rs.getString(14),rs.getString(9), rs.getString(10), rs.getString(11));
					info = this.getContraparte( fechaConsumo, rs.getString(4), rs.getString(14));
					contraparte.addAll(info);
					instrumento.add(factoring);
					instrumento.add(sumatoria);
				} else if (rs.getString(5).contains("TARJETAS")) {
					tarjeta.bonos(fechaConsumo, systCode, rs.getString(4), rs.getString(14),rs.getString(9), rs.getString(10), rs.getString(11));
					sumatoria.bonos(fechaConsumo, systCode, rs.getString(4), rs.getString(14),rs.getString(9), rs.getString(10), rs.getString(11));
					info = this.getContraparte( fechaConsumo, rs.getString(4), rs.getString(14));
					contraparte.addAll(info);
					instrumento.add(tarjeta);
					instrumento.add(sumatoria);
				} else if (rs.getString(5).contains("LINEA MULTIDEAL RESTO")
						|| rs.getString(5).contains("CREDITOS - COMPROMETIDO")
						|| rs.getString(5).contains("CREDITO BACKUP") || rs.getString(5).contains("CREDITO OTROS")) {
					comprome.bonos(fechaConsumo, systCode, rs.getString(4), rs.getString(14),rs.getString(9), rs.getString(10), rs.getString(11));
					sumatoria.bonos(fechaConsumo, systCode, rs.getString(4), rs.getString(14),rs.getString(9), rs.getString(10), rs.getString(11));
					info = this.getContraparte( fechaConsumo, rs.getString(4), rs.getString(14));
					contraparte.addAll(info);
					instrumento.add(comprome);
					instrumento.add(sumatoria);
				} else if (rs.getString(5).contains("GARANTIA ACCIONES") || rs.getString(5).contains("GARANTIA AVAL")
						|| rs.getString(5).contains("GARANTIA DERECHOS")
						|| rs.getString(5).contains("GARANTIA PERSONAL")
						|| rs.getString(5).contains("OTRAS GARANTIAS EN")
						|| rs.getString(5).contains("OTRAS GARANTIAS REALES")
						|| rs.getString(5).contains("OTHER GUARANTY")) {
					MexicoGaran.add(systCode);
					sumatoria.bonos(fechaConsumo, systCode, rs.getString(4), rs.getString(14),rs.getString(9), rs.getString(10), rs.getString(11));
					MexicoGaranValCurSum.add(sumatoriaNomValCur);
					MexicoGaranCerSum.add(sumatoriaCer);
					MexicoGaranNomValSum.add(sumatoriaNomVal);
					instrumento.add(sumatoria);
				} else if (rs.getString(5).contains("AVAL COMERCIAL")
						|| rs.getString(5).contains("AVAL FINANCIERO - NO COMPROMETIDO")
						|| rs.getString(5).contains("AVAL NO") || rs.getString(5).contains("AVAL TECNICO")
						|| rs.getString(5).contains("GARANTIA LINE") || rs.getString(5).contains("STANDBY")
						|| rs.getString(5).contains("LINEA DE AVALES")) {
					aval.bonos(fechaConsumo, systCode, rs.getString(4), rs.getString(14),rs.getString(9), rs.getString(10), rs.getString(11));
					sumatoria.bonos(fechaConsumo, systCode, rs.getString(4), rs.getString(14),rs.getString(9), rs.getString(10), rs.getString(11));
					info = this.getContraparte( fechaConsumo, rs.getString(4), rs.getString(14));
					contraparte.addAll(info);
					instrumento.add(aval);
					instrumento.add(sumatoria);
				} else if (rs.getString(5).contains("ASSET") || rs.getString(5).contains("CALL")
						|| rs.getString(5).contains("CERTIFICATES") || rs.getString(5).contains("COLLAR")
						|| rs.getString(5).contains("EQUITY") || rs.getString(5).contains("COMMODITY")
						|| rs.getString(5).contains("CREDIT DEFAULT") || rs.getString(5).contains("CURRENCY")
						|| rs.getString(5).contains("FIXED") || rs.getString(5).contains("FLOATING")
						|| rs.getString(5).contains("FX") || rs.getString(5).contains("INDEXED")
						|| rs.getString(5).contains("INTEREST") || rs.getString(5).contains("ZERO")
						|| rs.getString(5).contains("INITIAL MARGIN CASH") || rs.getString(5).contains("VARIATION")
						|| rs.getString(5).contains("NOSTRO") || rs.getString(5).contains("VOSTRO")
						|| rs.getString(5).contains("ACOTADO") || rs.getString(5).contains("SECURITIES")
						|| rs.getString(5).contains("MONEY MARKET") || rs.getString(5).contains("SECURED")
						|| rs.getString(5).contains("RECEIVER REVENUE") || rs.getString(5).contains("YEAR ON YEAR")
						|| rs.getString(5).contains("GENERIC DEALS") || rs.getString(5).contains("OPTIONSPAYER")
						|| rs.getString(5).contains("OPTIONS RECEIVER") || rs.getString(5).contains("BONIFICADO")
						|| rs.getString(5).contains("SWAP FORWARD") || rs.getString(5).contains("TITULIZACION")
						|| rs.getString(5).contains("DELIVERABLE") || rs.getString(5).contains("GENERIC DEALS")
						|| rs.getString(5).contains("PAYER REVENUE")) {
					derivados.bonos(fechaConsumo, systCode, rs.getString(4), rs.getString(14),rs.getString(9), rs.getString(10), rs.getString(11));
					sumatoria.bonos(fechaConsumo, systCode, rs.getString(4), rs.getString(14),rs.getString(9), rs.getString(10), rs.getString(11));
					info = this.getContraparte( fechaConsumo, rs.getString(4), rs.getString(14));
					contraparte.addAll(info);
					instrumento.add(derivados);
					instrumento.add(sumatoria);
				} else if (rs.getString(5).contains("CREDITOS - NO COMPROMETIDO")) {
					nocompro.bonos(fechaConsumo, systCode, rs.getString(4), rs.getString(14),rs.getString(9), rs.getString(10), rs.getString(11));
					sumatoria.bonos(fechaConsumo, systCode, rs.getString(4), rs.getString(14),rs.getString(9), rs.getString(10), rs.getString(11));
					info = this.getContraparte( fechaConsumo, rs.getString(4), rs.getString(14));
					contraparte.addAll(info);
					instrumento.add(nocompro);
					instrumento.add(sumatoria);
				} else if (rs.getString(5).contains("LEASING") || rs.getString(5).contains("RENTING")) {
					leasrent.bonos(fechaConsumo, systCode, rs.getString(4), rs.getString(14),rs.getString(9), rs.getString(10), rs.getString(11));
					sumatoria.bonos(fechaConsumo, systCode, rs.getString(4), rs.getString(14),rs.getString(9), rs.getString(10), rs.getString(11));
					info = this.getContraparte( fechaConsumo, rs.getString(4), rs.getString(14));
					contraparte.addAll(info);
					instrumento.add(leasrent);
					instrumento.add(sumatoria);
				} else if (rs.getString(5).contains("OVERDRAFTS")) {
					over.bonos(fechaConsumo, systCode, rs.getString(4), rs.getString(14),rs.getString(9), rs.getString(10), rs.getString(11));
					sumatoria.bonos(fechaConsumo, systCode, rs.getString(4), rs.getString(14),rs.getString(9), rs.getString(10), rs.getString(11));
					info = this.getContraparte( fechaConsumo, rs.getString(4), rs.getString(14));
					contraparte.addAll(info);
					instrumento.add(over);
					instrumento.add(sumatoria);
				}
				pais = rs.getString(14);
				
			} while (rs.next());
			if (systCode.isEmpty()) {
				systCode = "No existen registros para este grupo en la interfaz CONSULTA";
			}
			ArrayList<String> newList = new ArrayList<String>();
			for (String element : MexicoGaran) {
				if (!contraparte.contains(element)) {
					newList.add(element);
				}
			}
			
			// Garantias
			String CadenaMexicoGaran = newList.stream().collect(Collectors.joining(""));
			double totalMexicoGaranValCurSum = MexicoGaranValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoGaranCerSum = MexicoGaranCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoGaranNomValSum = MexicoGaranNomValSum.stream().mapToDouble(Double::doubleValue).sum();
			interfazCsv.interfazCsvPrimeraParte(instrumento, newList, CadenaMexicoGaran, nombreInterfaz,
					totalMexicoGaranValCurSum, totalMexicoGaranCerSum, totalMexicoGaranNomValSum, pais);
		}

		return systCode;
	}
	
	
	int getQueryRowCount(String query) throws SQLException {
		
	    try (Statement sta = con.createStatement();
	        ResultSet standardRS = sta.executeQuery(query)) {
	        int size = 0;
	        while (standardRS.next()) {
	            size++;
	        }
	        return size;
	    }
	}

	/**
	 * Metodo getConsultaOtrosPaises que consulta los instrumentos para los paises
	 * restantes
	 * 
	 * @param grupo          usado para la consulta de instrumentos
	 * @param nombreInterfaz donde se escribiran los instrumentos
	 * @param fechaConsumo   quenera la consulta del dia deseado
	 * @throws SQLException   atrapa la excepcion generada durante la ejecucion del
	 *                        query
	 * @throws ParseException atrapa la excepcion generada por al parsear de string
	 *                        a double
	 * @return systCode regresa lo que obtiene de ejecutar el query
	 */
	public String getConsultaOtrosPaises(String grupo, String nombreInterfaz, String fechaConsumo) throws Exception {

		Statement sta = con.createStatement();

		double sumatoriaNomValCur;
		double sumatoriaCer;
		double sumatoriaNomVal;
		List<String> info = new ArrayList<String>();
		// bonos Spain
		List<String> SpainBonos = new ArrayList<String>();
		// bonos sumatoria Spain
		List<Double> SpainBonosNomValCurSum = new ArrayList<Double>();
		List<Double> SpainBonosCerSum = new ArrayList<Double>();
		List<Double> SpainBonosNomValSum = new ArrayList<Double>();

		// creditos documentariado Spain
		List<String> SpainCredDocu = new ArrayList<String>();
		// creditos documentariado sumatoria Spain
		List<Double> SpainCredDocuNomValCurSum = new ArrayList<Double>();
		List<Double> SpainCredDocuCerSum = new ArrayList<Double>();
		List<Double> SpainCredDocuNomValSum = new ArrayList<Double>();

		// Exportacion/Importacion Spain
		List<String> SpainExportImport = new ArrayList<String>();
		// Exportacion/Importacion sumatoria Spain
		List<Double> SpainExportImportNomValCurSum = new ArrayList<Double>();
		List<Double> SpainExportImportCerSum = new ArrayList<Double>();
		List<Double> SpainExportImportNomValSum = new ArrayList<Double>();

		// Comex/Forfaiting Spain
		List<String> SpainComFor = new ArrayList<String>();
		// Comex/Forfaiting sumatoria Spain
		List<Double> SpainComForNomValCurSum = new ArrayList<Double>();
		List<Double> SpainComForCerSum = new ArrayList<Double>();
		List<Double> SpainComForNomValSum = new ArrayList<Double>();

		// Sindicado Spain
		List<String> SpainSindicado = new ArrayList<String>();
		// Sindicado sumatoria Spain
		List<Double> SpainSindicadoNomValCurSum = new ArrayList<Double>();
		List<Double> SpainSindicadoCerSum = new ArrayList<Double>();
		List<Double> SpainSindicadoNomValSum = new ArrayList<Double>();

		// Confirming Spain
		List<String> SpainConfir = new ArrayList<String>();
		// Confirming sumatoria Spain
		List<Double> SpainConfirNomValCurSum = new ArrayList<Double>();
		List<Double> SpainConfirCerSum = new ArrayList<Double>();
		List<Double> SpainConfirNomValSum = new ArrayList<Double>();

		// Descuentos Spain
		List<String> SpainDesc = new ArrayList<String>();
		// Descuentos sumatoria Spain
		List<Double> SpainDescNomValCurSum = new ArrayList<Double>();
		List<Double> SpainDescCerSum = new ArrayList<Double>();
		List<Double> SpainDescNomValSum = new ArrayList<Double>();

		// Factoring Spain
		List<String> SpainFac = new ArrayList<String>();
		// Factoring sumatoria Spain
		List<Double> SpainFacNomValCurSum = new ArrayList<Double>();
		List<Double> SpainFacCerSum = new ArrayList<Double>();
		List<Double> SpainFacNomValSum = new ArrayList<Double>();

		// Tarjetas Spain
		List<String> SpainTar = new ArrayList<String>();
		// Tarjetas sumatoria Spain
		List<Double> SpainTarNomValCurSum = new ArrayList<Double>();
		List<Double> SpainTarCerSum = new ArrayList<Double>();
		List<Double> SpainTarNomValSum = new ArrayList<Double>();

		// Lineas Comprometidas Spain
		List<String> SpainLinCom = new ArrayList<String>();
		// Lineas Comprometidas sumatorias Spain
		List<Double> SpainLinComNomValCurSum = new ArrayList<Double>();
		List<Double> SpainLinComCerSum = new ArrayList<Double>();
		List<Double> SpainLinComNomValSum = new ArrayList<Double>();

		// Lineas Comprometidas Spain
		List<String> SpainLinNoCom = new ArrayList<String>();
		// Lineas Comprometidas sumatorias Spain
		List<Double> SpainLinNoComNomValCurSum = new ArrayList<Double>();
		List<Double> SpainLinNoComCerSum = new ArrayList<Double>();
		List<Double> SpainLinNoComNomValSum = new ArrayList<Double>();

		// Garantias Spain
		List<String> SpainGaran = new ArrayList<String>();
		// Garantias sumatoria Mexico
		List<Double> SpainGaranNomValCurSum = new ArrayList<Double>();
		List<Double> SpainGaranCerSum = new ArrayList<Double>();
		List<Double> SpainGaranNomValSum = new ArrayList<Double>();

		// Avales Spain
		List<String> SpainAval = new ArrayList<String>();
		// Avales sumatoria Spain
		List<Double> SpainAvalNomValCurSum = new ArrayList<Double>();
		List<Double> SpainAvalCerSum = new ArrayList<Double>();
		List<Double> SpainAvalNomValSum = new ArrayList<Double>();

		// Derivados Spain
		List<String> SpainDer = new ArrayList<String>();
		// Derivados sumatoria Spain
		List<Double> SpainDerNomValCurSum = new ArrayList<Double>();
		List<Double> SpainDerCerSum = new ArrayList<Double>();
		List<Double> SpainDerNomValSum = new ArrayList<Double>();

		// Total General Spain
		List<Double> SpainTotNomValCurSum = new ArrayList<Double>();
		List<Double> SpainTotCerSum = new ArrayList<Double>();
		List<Double> SpainTotNomValSum = new ArrayList<Double>();

		// LeasingRenting Spain
		List<String> SpainLeasingRenting = new ArrayList<String>();
		// Avales sumatoria Mexico
		List<Double> SpainLeasingRentingValCurSum = new ArrayList<Double>();
		List<Double> SpainLeasingRentingCerSum = new ArrayList<Double>();
		List<Double> SpainLeasingRentingNomValSum = new ArrayList<Double>();

		// Overdrafts Spain
		List<String> SpainOverdrafts = new ArrayList<String>(); // registro completo 17
		// Avales sumatoria Mexico
		List<Double> SpainOverdraftsValCurSum = new ArrayList<Double>();// nom
		List<Double> SpainOverdraftsCerSum = new ArrayList<Double>();// cer
		List<Double> SpainOverdraftsNomValSum = new ArrayList<Double>();// nomvalcur

		ArrayList<String> contraparte = new ArrayList<String>();

		List<String> encabezado = new ArrayList<>();
		encabezado.add("CPTYPARENT|");
		encabezado.add("CPTYPARENTRATING|");
		encabezado.add("CPTYPARENTNAME|");
		encabezado.add("DEALSTAMP|");
		encabezado.add("INSTRUMENTNAME|");
		encabezado.add("VALUEDATE|");
		encabezado.add("MATURITYDATE|");
		encabezado.add("CURRENCY|");
		encabezado.add("NOMINALVALUECUR|");
		encabezado.add("CER|");
		encabezado.add("NOMINALVALUE|");
		encabezado.add("ONEOFF|");
		encabezado.add("CPTYNAME|");
		encabezado.add("FOLDERCOUNTRYNAME|");
		encabezado.add("CPTYCOUNTRY|");
		encabezado.add("CPTYPARENTCOUNTRY|");
		encabezado.add("FOLDERCOUNTRY");
		encabezado.add("\n");

		String systCode = "SELECT cptyparent, NVL(cptyparentrating, 'SIN RATING'),cptyparentname,dealstamp,instrumentname,TO_CHAR(TO_DATE(valuedate,  'YYYY-MM-DD'), 'DD-mon-YY'),TO_CHAR(TO_DATE(maturitydate,  'YYYY-MM-DD'), 'DD-mon-YY'),currency,to_char(DECODE(nominalvaluecur,null, '0.0',nominalvaluecur), '999,999,999,999.99')  AS nominalvaluecur,to_char(DECODE(CER,null, '0.0',CER), '999,999,999,999.99')  AS CER,to_char(DECODE(nominalvalue,null, '0.0',nominalvalue), '999,999,999,999.99')  AS nominalvalue,oneoff,cptyname,foldercountryname,cptycountry,cptyparentcountry,foldercountry from PGT_MEX.T_PGT_MEX_CONSUMOSC_V WHERE LastParentF ='"
				+ grupo + "' and FECHACARGA='" + fechaConsumo
				+ "' AND foldercountryname<>'Mexico' UNION ALL SELECT cptyparent, NVL(cptyparentrating, 'SIN RATING'),cptyparentname,dealstamp,instrumentname,TO_CHAR(TO_DATE(valuedate,  'YYYY-MM-DD'), 'DD-mon-YY'),TO_CHAR(TO_DATE(maturitydate,  'YYYY-MM-DD'), 'DD-mon-YY'),currency,to_char(DECODE(nominalvaluecur,null, '0.0',nominalvaluecur), '999,999,999,999.99')  AS nominalvaluecur,to_char(DECODE(CER,null, '0.0',CER), '999,999,999,999.99')  AS CER,to_char(DECODE(nominalvalue,null, '0.0',nominalvalue), '999,999,999,999.99')  AS nominalvalue,oneoff,cptyname,foldercountryname,cptycountry,cptyparentcountry,foldercountry from PGT_MEX.T_PGT_MEX_CONSUMOSC_D WHERE LastParentF ='"
				+ grupo + "' and FECHACARGA='" + fechaConsumo
				+ "' AND foldercountryname<>'Mexico' ORDER BY foldercountryname ,instrumentname ";
		ResultSet rs = sta.executeQuery(systCode);

		if (rs.equals(null) || rs.next() == false) {
			systCode = "No existen registros para este grupo en la interfaz CONSULTA";
		} else {

			String pais = null;
			do {
				// cadena
				systCode = rs.getString(1) + "|" + rs.getString(2) + "|" + "\"" + rs.getString(3) + "\"" + "|"
						+ rs.getString(4) + "|" + rs.getString(5) + "|" + rs.getString(6) + "|" + rs.getString(7) + "|"
						+ rs.getString(8) + "|" + rs.getString(9) + "|" + rs.getString(10) + "|" + rs.getString(11)
						+ "|" + rs.getString(12) + "|" + "\"" + rs.getString(13) + "\"" + "|" + rs.getString(14) + "|"
						+ "\"" + rs.getString(15) + "\"" + "|" + rs.getString(16) + "|" + rs.getString(17) + "\n";
				sumatoriaNomValCur = DecimalFormat.getNumberInstance().parse(rs.getString(9).trim()).doubleValue();
				sumatoriaCer = DecimalFormat.getNumberInstance().parse(rs.getString(10).trim()).doubleValue();
				sumatoriaNomVal = DecimalFormat.getNumberInstance().parse(rs.getString(11).trim()).doubleValue();
				if (pais == null || pais.equals(rs.getNString(14))) {
					pais = rs.getNString(14);
					if (rs.getString(5).contains("BOND")) {
						SpainBonos.add(systCode);
						info = this.getContraparte( fechaConsumo, rs.getString(4), rs.getString(14));
						contraparte.addAll(info);
						SpainBonos.addAll(info);
						SpainBonosNomValCurSum.add(Double.valueOf(sumatoriaNomValCur));
						SpainBonosCerSum.add(Double.valueOf(sumatoriaCer));
						SpainBonosNomValSum.add(Double.valueOf(sumatoriaNomVal));
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains(" CREDITO DOCUMENTARIO")) {
						SpainCredDocu.add(systCode);
						info = this.getContraparte( fechaConsumo, rs.getString(4), rs.getString(14));
						contraparte.addAll(info);
						SpainCredDocu.addAll(info);
						SpainCredDocuNomValCurSum.add(sumatoriaNomValCur);
						SpainCredDocuCerSum.add(sumatoriaCer);
						SpainCredDocuNomValSum.add(sumatoriaNomVal);
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("EXPORTACION") || rs.getString(5).contains("IMPORTACION")) {
						SpainExportImport.add(systCode);
						info = this.getContraparte( fechaConsumo, rs.getString(4), rs.getString(14));
						contraparte.addAll(info);
						SpainExportImport.addAll(info);
						SpainExportImportNomValCurSum.add(sumatoriaNomValCur);
						SpainExportImportCerSum.add(sumatoriaCer);
						SpainExportImportNomValSum.add(sumatoriaNomVal);
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("COMEX") || rs.getString(5).contains("FORFAITING")) {
						SpainComFor.add(systCode);
						info = this.getContraparte( fechaConsumo, rs.getString(4), rs.getString(14));
						contraparte.addAll(info);
						SpainComFor.addAll(info);
						SpainComForNomValCurSum.add(sumatoriaNomValCur);
						SpainComForCerSum.add(sumatoriaCer);
						SpainComForNomValSum.add(sumatoriaNomVal);
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("SINDICADO")) {
						SpainSindicado.add(systCode);
						info = this.getContraparte( fechaConsumo, rs.getString(4), rs.getString(14));
						contraparte.addAll(info);
						SpainSindicado.addAll(info);
						SpainSindicadoNomValCurSum.add(sumatoriaNomValCur);
						SpainSindicadoCerSum.add(sumatoriaCer);
						SpainSindicadoNomValSum.add(sumatoriaNomVal);
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("CONFIRMING")) {
						SpainConfir.add(systCode);
						info = this.getContraparte( fechaConsumo, rs.getString(4), rs.getString(14));
						contraparte.addAll(info);
						SpainConfir.addAll(info);
						SpainConfirNomValCurSum.add(sumatoriaNomValCur);
						SpainConfirCerSum.add(sumatoriaCer);
						SpainConfirNomValSum.add(sumatoriaNomVal);
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("DESCUENTOS")) {
						SpainDesc.add(systCode);
						info = this.getContraparte( fechaConsumo, rs.getString(4), rs.getString(14));
						contraparte.addAll(info);
						SpainDesc.addAll(info);
						SpainDescNomValCurSum.add(sumatoriaNomValCur);
						SpainDescCerSum.add(sumatoriaCer);
						SpainDescNomValSum.add(sumatoriaNomVal);
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("FACTORING")) {
						SpainFac.add(systCode);
						info = this.getContraparte( fechaConsumo, rs.getString(4), rs.getString(14));
						contraparte.addAll(info);
						SpainFac.addAll(info);
						SpainFacNomValCurSum.add(sumatoriaNomValCur);
						SpainFacCerSum.add(sumatoriaCer);
						SpainFacNomValSum.add(sumatoriaNomVal);
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("TARJETAS")) {
						SpainTar.add(systCode);
						info = this.getContraparte( fechaConsumo, rs.getString(4), rs.getString(14));
						contraparte.addAll(info);
						SpainTar.addAll(info);
						SpainTarNomValCurSum.add(sumatoriaNomValCur);
						SpainTarCerSum.add(sumatoriaCer);
						SpainTarNomValSum.add(sumatoriaNomVal);
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("LINEA MULTIDEAL RESTO")
							|| rs.getString(5).contains("CREDITOS - COMPROMETIDO")
							|| rs.getString(5).contains("CREDITO BACKUP")
							|| rs.getString(5).contains("CREDITO OTROS")) {
						SpainLinCom.add(systCode);
						info = this.getContraparte( fechaConsumo, rs.getString(4), rs.getString(14));
						contraparte.addAll(info);
						SpainLinCom.addAll(info);
						SpainLinComNomValCurSum.add(sumatoriaNomValCur);
						SpainLinComCerSum.add(sumatoriaCer);
						SpainLinComNomValSum.add(sumatoriaNomVal);
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("GARANTIA ACCIONES")
							|| rs.getString(5).contains("GARANTIA AVAL")
							|| rs.getString(5).contains("GARANTIA DERECHOS")
							|| rs.getString(5).contains("GARANTIA PERSONAL")
							|| rs.getString(5).contains("OTRAS GARANTIAS EN")
							|| rs.getString(5).contains("OTRAS GARANTIAS REALES")
							|| rs.getString(5).contains("OTHER GUARANTY")) {
						SpainGaran.add(systCode);
						SpainGaranNomValCurSum.add(sumatoriaNomValCur);
						SpainGaranCerSum.add(sumatoriaCer);
						SpainGaranNomValSum.add(sumatoriaNomVal);
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("AVAL COMERCIAL")
							|| rs.getString(5).contains("AVAL FINANCIERO - NO COMPROMETIDO")
							|| rs.getString(5).contains("AVAL NO") || rs.getString(5).contains("AVAL TECNICO")
							|| rs.getString(5).contains("GARANTIA LINE") || rs.getString(5).contains("STANDBY")
							|| rs.getString(5).contains("LINEA DE AVALES")) {
						SpainAval.add(systCode);
						info = this.getContraparte( fechaConsumo, rs.getString(4), rs.getString(14));
						contraparte.addAll(info);
						SpainAval.addAll(info);
						SpainAvalNomValCurSum.add(sumatoriaNomValCur);
						SpainAvalCerSum.add(sumatoriaCer);
						SpainAvalNomValSum.add(sumatoriaNomVal);
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("ASSET") || rs.getString(5).contains("CALL")
							|| rs.getString(5).contains("CERTIFICATES") || rs.getString(5).contains("COLLAR")
							|| rs.getString(5).contains("EQUITY") || rs.getString(5).contains("COMMODITY")
							|| rs.getString(5).contains("CREDIT DEFAULT") || rs.getString(5).contains("CURRENCY")
							|| rs.getString(5).contains("FIXED") || rs.getString(5).contains("FLOATING")
							|| rs.getString(5).contains("FX") || rs.getString(5).contains("INDEXED")
							|| rs.getString(5).contains("INTEREST") || rs.getString(5).contains("ZERO")
							|| rs.getString(5).contains("INITIAL MARGIN CASH") || rs.getString(5).contains("VARIATION")
							|| rs.getString(5).contains("NOSTRO") || rs.getString(5).contains("VOSTRO")
							|| rs.getString(5).contains("ACOTADO") || rs.getString(5).contains("SECURITIES")
							|| rs.getString(5).contains("MONEY MARKET") || rs.getString(5).contains("SECURED")
							|| rs.getString(5).contains("RECEIVER REVENUE") || rs.getString(5).contains("YEAR ON YEAR")
							|| rs.getString(5).contains("GENERIC DEALS") || rs.getString(5).contains("OPTIONSPAYER")
							|| rs.getString(5).contains("OPTIONS RECEIVER") || rs.getString(5).contains("BONIFICADO")
							|| rs.getString(5).contains("SWAP FORWARD") || rs.getString(5).contains("TITULIZACION")
							|| rs.getString(5).contains("DELIVERABLE") || rs.getString(5).contains("GENERIC DEALS")
							|| rs.getString(5).contains("PAYER REVENUE")) {
						SpainDer.add(systCode);
						info = this.getContraparte( fechaConsumo, rs.getString(4), rs.getString(14));
						contraparte.addAll(info);
						SpainDer.addAll(info);
						SpainDerNomValCurSum.add(sumatoriaNomValCur);
						SpainDerCerSum.add(sumatoriaCer);
						SpainDerNomValSum.add(sumatoriaNomVal);
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("CREDITOS - NO COMPROMETIDO")) {
						SpainLinNoCom.add(systCode);
						info = this.getContraparte( fechaConsumo, rs.getString(4), rs.getString(14));
						contraparte.addAll(info);
						SpainLinNoCom.addAll(info);
						SpainLinNoComNomValCurSum.add(sumatoriaNomValCur);
						SpainLinNoComCerSum.add(sumatoriaCer);
						SpainLinNoComNomValSum.add(sumatoriaNomVal);
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("LEASING") || rs.getString(5).contains("RENTING")) {
						SpainLeasingRenting.add(systCode);
						info = this.getContraparte(fechaConsumo, rs.getString(4), rs.getString(14));
						contraparte.addAll(info);
						SpainLeasingRenting.addAll(info);
						SpainLeasingRentingValCurSum.add(sumatoriaNomValCur);
						SpainLeasingRentingCerSum.add(sumatoriaCer);
						SpainLeasingRentingNomValSum.add(sumatoriaNomVal);
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("OVERDRAFTS")) {
						SpainOverdrafts.add(systCode);
						info = this.getContraparte(fechaConsumo, rs.getString(4), rs.getString(14));
						contraparte.addAll(info);
						SpainOverdrafts.addAll(info);
						SpainOverdraftsValCurSum.add(sumatoriaNomValCur);
						SpainOverdraftsCerSum.add(sumatoriaCer);
						SpainOverdraftsNomValSum.add(sumatoriaNomVal);
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);
					}

				} else {

					this.creaInterfaz(encabezado, SpainBonos, SpainBonosNomValCurSum, SpainBonosCerSum,
							SpainBonosNomValSum, SpainCredDocu, SpainCredDocuNomValCurSum, SpainCredDocuCerSum,
							SpainCredDocuNomValSum, SpainExportImport, SpainExportImportNomValCurSum,
							SpainExportImportCerSum, SpainExportImportNomValSum, SpainComFor, SpainComForNomValCurSum,
							SpainComForCerSum, SpainComForNomValSum, SpainSindicado, SpainSindicadoNomValCurSum,
							SpainSindicadoCerSum, SpainSindicadoNomValSum, SpainConfir, SpainConfirNomValCurSum,
							SpainConfirCerSum, SpainConfirNomValSum, SpainDesc, SpainDescNomValCurSum, SpainDescCerSum,
							SpainDescNomValSum, SpainFac, SpainFacNomValCurSum, SpainFacCerSum, SpainFacNomValSum,
							SpainTar, SpainTarNomValCurSum, SpainTarCerSum, SpainTarNomValSum, SpainLinCom,
							SpainLinComNomValCurSum, SpainLinComCerSum, SpainLinComNomValSum, SpainGaran,
							SpainGaranNomValCurSum, SpainGaranCerSum, SpainGaranNomValSum, SpainAval,
							SpainAvalNomValCurSum, SpainAvalCerSum, SpainAvalNomValSum, SpainDer, SpainDerNomValCurSum,
							SpainDerCerSum, SpainDerNomValSum, SpainTotNomValCurSum, SpainTotCerSum, SpainTotNomValSum,
							nombreInterfaz, pais, contraparte, SpainLinNoCom, SpainLinNoComNomValCurSum,
							SpainLinNoComCerSum, SpainLinNoComNomValSum, SpainLeasingRenting,
							SpainLeasingRentingValCurSum, SpainLeasingRentingCerSum, SpainLeasingRentingNomValSum,
							SpainOverdrafts, SpainOverdraftsValCurSum, SpainOverdraftsCerSum, SpainOverdraftsNomValSum);

					pais = rs.getNString(14);
					contraparte.clear();
					SpainBonos.clear();
					SpainBonosNomValCurSum.clear();
					SpainBonosCerSum.clear();
					SpainBonosNomValSum.clear();
					SpainCredDocu.clear();
					SpainCredDocuNomValCurSum.clear();
					SpainCredDocuCerSum.clear();
					SpainCredDocuNomValSum.clear();
					SpainExportImport.clear();
					SpainExportImportNomValCurSum.clear();
					SpainExportImportCerSum.clear();
					SpainExportImportNomValSum.clear();
					SpainComFor.clear();
					SpainComForNomValCurSum.clear();
					SpainComForCerSum.clear();
					SpainComForNomValSum.clear();
					SpainSindicado.clear();
					SpainSindicadoNomValCurSum.clear();
					SpainSindicadoCerSum.clear();
					SpainSindicadoNomValSum.clear();
					SpainConfir.clear();
					SpainConfirNomValCurSum.clear();
					SpainConfirCerSum.clear();
					SpainConfirNomValSum.clear();
					SpainDesc.clear();
					SpainDescNomValCurSum.clear();
					SpainDescCerSum.clear();
					SpainDescNomValSum.clear();
					SpainFac.clear();
					SpainFacNomValCurSum.clear();
					SpainFacCerSum.clear();
					SpainFacNomValSum.clear();
					SpainTar.clear();
					SpainTarNomValCurSum.clear();
					SpainTarCerSum.clear();
					SpainTarNomValSum.clear();
					SpainLinCom.clear();
					SpainLinComNomValCurSum.clear();
					SpainLinComCerSum.clear();
					SpainLinComNomValSum.clear();
					SpainGaran.clear();
					SpainGaranNomValCurSum.clear();
					SpainGaranCerSum.clear();
					SpainGaranNomValSum.clear();
					SpainAval.clear();
					SpainAvalNomValCurSum.clear();
					SpainAvalCerSum.clear();
					SpainAvalNomValSum.clear();
					SpainDer.clear();
					SpainDerNomValCurSum.clear();
					SpainDerCerSum.clear();
					SpainDerNomValSum.clear();
					SpainTotNomValCurSum.clear();
					SpainTotCerSum.clear();
					SpainTotNomValSum.clear();
					SpainLinNoCom.clear();
					SpainLinNoComNomValCurSum.clear();
					SpainLinNoComCerSum.clear();
					SpainLinNoComNomValSum.clear();
					SpainLeasingRenting.clear();
					SpainLeasingRentingValCurSum.clear();
					SpainLeasingRentingCerSum.clear();
					SpainLeasingRentingNomValSum.clear();
					SpainOverdrafts.clear();
					SpainOverdraftsValCurSum.clear();
					SpainOverdraftsCerSum.clear();
					SpainOverdraftsNomValSum.clear();

				}

			} while (rs.next());
			if (systCode.isEmpty()) {
				systCode = "No existen registros para este grupo en la interfaz CONSULTA";
			}

			this.creaInterfaz(encabezado, SpainBonos, SpainBonosNomValCurSum, SpainBonosCerSum, SpainBonosNomValSum,
					SpainCredDocu, SpainCredDocuNomValCurSum, SpainCredDocuCerSum, SpainCredDocuNomValSum,
					SpainExportImport, SpainExportImportNomValCurSum, SpainExportImportCerSum,
					SpainExportImportNomValSum, SpainComFor, SpainComForNomValCurSum, SpainComForCerSum,
					SpainComForNomValSum, SpainSindicado, SpainSindicadoNomValCurSum, SpainSindicadoCerSum,
					SpainSindicadoNomValSum, SpainConfir, SpainConfirNomValCurSum, SpainConfirCerSum,
					SpainConfirNomValSum, SpainDesc, SpainDescNomValCurSum, SpainDescCerSum, SpainDescNomValSum,
					SpainFac, SpainFacNomValCurSum, SpainFacCerSum, SpainFacNomValSum, SpainTar, SpainTarNomValCurSum,
					SpainTarCerSum, SpainTarNomValSum, SpainLinCom, SpainLinComNomValCurSum, SpainLinComCerSum,
					SpainLinComNomValSum, SpainGaran, SpainGaranNomValCurSum, SpainGaranCerSum, SpainGaranNomValSum,
					SpainAval, SpainAvalNomValCurSum, SpainAvalCerSum, SpainAvalNomValSum, SpainDer,
					SpainDerNomValCurSum, SpainDerCerSum, SpainDerNomValSum, SpainTotNomValCurSum, SpainTotCerSum,
					SpainTotNomValSum, nombreInterfaz, pais, contraparte, SpainLinNoCom, SpainLinNoComNomValCurSum,
					SpainLinNoComCerSum, SpainLinNoComNomValSum, SpainLeasingRenting, SpainLeasingRentingValCurSum,
					SpainLeasingRentingCerSum, SpainLeasingRentingNomValSum, SpainOverdrafts, SpainOverdraftsValCurSum,
					SpainOverdraftsCerSum, SpainOverdraftsNomValSum);

		}

		return systCode;
	}

	/**
	  * Metodo getContraparte validar si tiene una garantia 
	  * la operacion que se esta evaluando 
	  * @param fechaConsumo se valida con la fecha que se esta evaluando 
	  * @param deal usa para validar la garantia 
	  * @param pais otra condicion es que sea del pais 
	  * @return registrosInterfaz regresa el array con las contrapartes
	  */
	public List<String> getContraparte(String fechaConsumo, String deal, String pais) {
		strbSql = new StringBuilder();

		List<String> registrosInterfaz;
		registrosInterfaz = new ArrayList<String>();
		String systCode = "";
		strbSql.append(
				"SELECT cptyparent, NVL(cptyparentrating, 'SIN RATING'),cptyparentname,dealstamp,instrumentname,TO_CHAR(TO_DATE(valuedate,  'YYYY-MM-DD'), 'DD-mon-YY'),TO_CHAR(TO_DATE(maturitydate,  'YYYY-MM-DD'), 'DD-mon-YY'),currency,to_char(DECODE(nominalvaluecur,null, '0.0',nominalvaluecur), '999,999,999,999.99')  AS nominalvaluecur,to_char(DECODE(CER,null, '0.0',CER), '999,999,999,999.99')  AS CER,to_char(DECODE(nominalvalue,null, '0.0',nominalvalue), '999,999,999,999.99')  AS nominalvalue,oneoff,cptyname,foldercountryname,cptycountry,cptyparentcountry,foldercountry from PGT_MEX.T_PGT_MEX_CONSUMOSC_V WHERE FECHACARGA='"
						+ fechaConsumo + "' AND foldercountryname='" + pais + "' AND DEALSTAMP='" + deal
						+ "' AND INSTRUMENTNAME IN('GARANTIA ACCIONES COTIZADAS','GARANTIA AVAL FINANCIERO - NO USAR (3Q 2016)','GARANTIA DERECHOS DE COBRO','GARANTIA PERSONAL MANCOMUNADA','GARANTIA PERSONAL SOLIDARIA','GARANTIA PERSONAL SOLIDARIA FINAN','OTRAS GARANTIAS EN EFECTIVO','OTRAS GARANTIAS REALES NO LIQUIDAS','OTHER GUARANTY CASH','GARANTIA BONOS LIQUIDOS AAA/A-') UNION ALL  SELECT cptyparent, NVL(cptyparentrating, 'SIN RATING'),cptyparentname,dealstamp,instrumentname,TO_CHAR(TO_DATE(valuedate,  'YYYY-MM-DD'), 'DD-mon-YY'),TO_CHAR(TO_DATE(maturitydate,  'YYYY-MM-DD'), 'DD-mon-YY'),currency,to_char(DECODE(nominalvaluecur,null, '0.0',nominalvaluecur), '999,999,999,999.99')  AS nominalvaluecur,to_char(DECODE(CER,null, '0.0',CER), '999,999,999,999.99')  AS CER,to_char(DECODE(nominalvalue,null, '0.0',nominalvalue), '999,999,999,999.99')  AS nominalvalue,oneoff,cptyname,foldercountryname,cptycountry,cptyparentcountry,foldercountry from PGT_MEX.T_PGT_MEX_CONSUMOSC_D WHERE FECHACARGA='"
						+ fechaConsumo + "' AND foldercountryname='" + pais + "' AND DEALSTAMP='" + deal
						+ "' AND INSTRUMENTNAME IN('GARANTIA ACCIONES COTIZADAS','GARANTIA AVAL FINANCIERO - NO USAR (3Q 2016)','GARANTIA DERECHOS DE COBRO','GARANTIA PERSONAL MANCOMUNADA','GARANTIA PERSONAL SOLIDARIA','GARANTIA PERSONAL SOLIDARIA FINAN','OTRAS GARANTIAS EN EFECTIVO','OTRAS GARANTIAS REALES NO LIQUIDAS','OTHER GUARANTY CASH','GARANTIA BONOS LIQUIDOS AAA/A-') ORDER BY foldercountryname,instrumentname,cptyparentname");

		try {
			pstmt = con.prepareStatement(strbSql.toString());
			rs = pstmt.executeQuery();

			while (rs.next()) {
				systCode = rs.getString(1) + "|" + rs.getString(2) + "|" + "\"" + rs.getString(3) + "\"" + "|"
						+ rs.getString(4) + "|" + rs.getString(5) + "|" + rs.getString(6) + "|" + rs.getString(7) + "|"
						+ rs.getString(8) + "|" + rs.getString(9) + "|" + rs.getString(10) + "|" + rs.getString(11)
						+ "|" + rs.getString(12) + "|" + "\"" + rs.getString(13) + "\"" + "|" + rs.getString(14) + "|"
						+ "\"" + rs.getString(15) + "\"" + "|" + rs.getString(16) + "|" + rs.getString(17) + "\n";

				registrosInterfaz.add(systCode);
			}
			pstmt.close();
			rs.close();
		} catch (SQLException e) {
			LOGGER.info("error en query " + e);
		}

		return registrosInterfaz;
	}

	public void creaInterfaz(List<String> encabezado, List<String> spainBonos, List<Double> spainBonosNomValCurSum,
			List<Double> spainBonosCerSum, List<Double> spainBonosNomValSum, List<String> spainCredDocu,
			List<Double> spainCredDocuNomValCurSum, List<Double> spainCredDocuCerSum,
			List<Double> spainCredDocuNomValSum, List<String> spainExportImport,
			List<Double> spainExportImportNomValCurSum, List<Double> spainExportImportCerSum,
			List<Double> spainExportImportNomValSum, List<String> spainComFor, List<Double> spainComForNomValCurSum,
			List<Double> spainComForCerSum, List<Double> spainComForNomValSum, List<String> spainSindicado,
			List<Double> spainSindicadoNomValCurSum, List<Double> spainSindicadoCerSum,
			List<Double> spainSindicadoNomValSum, List<String> spainConfir, List<Double> spainConfirNomValCurSum,
			List<Double> spainConfirCerSum, List<Double> spainConfirNomValSum, List<String> spainDesc,
			List<Double> spainDescNomValCurSum, List<Double> spainDescCerSum, List<Double> spainDescNomValSum,
			List<String> spainFac, List<Double> spainFacNomValCurSum, List<Double> spainFacCerSum,
			List<Double> spainFacNomValSum, List<String> spainTar, List<Double> spainTarNomValCurSum,
			List<Double> spainTarCerSum, List<Double> spainTarNomValSum, List<String> spainLinCom,
			List<Double> spainLinComNomValCurSum, List<Double> spainLinComCerSum, List<Double> spainLinComNomValSum,
			List<String> spainGaran, List<Double> spainGaranNomValCurSum, List<Double> spainGaranCerSum,
			List<Double> spainGaranNomValSum, List<String> spainAval, List<Double> spainAvalNomValCurSum,
			List<Double> spainAvalCerSum, List<Double> spainAvalNomValSum, List<String> spainDer,
			List<Double> spainDerNomValCurSum, List<Double> spainDerCerSum, List<Double> spainDerNomValSum,
			List<Double> spainTotNomValCurSum, List<Double> spainTotCerSum, List<Double> spainTotNomValSum,
			String nombreInterfaz, String pais, List<String> contraparte, List<String> SpainLinNoCom,
			List<Double> SpainLinNoComNomValCurSum, List<Double> SpainLinNoComCerSum,
			List<Double> SpainLinNoComNomValSum, List<String> SpainLeasingRenting,
			List<Double> SpainLeasingRentingValCurSum, List<Double> SpainLeasingRentingCerSum,
			List<Double> SpainLeasingRentingNomValSum, List<String> SpainOverdrafts,
			List<Double> SpainOverdraftsValCurSum, List<Double> SpainOverdraftsCerSum,
			List<Double> SpainOverdraftsNomValSum) {

		ArrayList<String> newList = new ArrayList<String>();
		for (String element : spainGaran) {
			if (!contraparte.contains(element)) {
				newList.add(element);
			}
		}

		String CadenaBonosSpain = spainBonos.stream().collect(Collectors.joining(""));
		double totalSpainBonosNomValCur = spainBonosNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainBonosCer = spainBonosCerSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainBonosNomVal = spainBonosNomValSum.stream().mapToDouble(Double::doubleValue).sum();
		String CadenaEncabeza = encabezado.stream().collect(Collectors.joining(""));

		String CadenaSpainCredDoc = spainCredDocu.stream().collect(Collectors.joining(""));
		double totalSpainCredDocuNomValCurSum = spainCredDocuNomValCurSum.stream().mapToDouble(Double::doubleValue)
				.sum();
		double totalSpainCredDocuCerSum = spainCredDocuCerSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainCredDocuNomValSum = spainCredDocuNomValSum.stream().mapToDouble(Double::doubleValue).sum();

		String CadenaSpainExportImport = spainExportImport.stream().collect(Collectors.joining(""));
		double totalSpainExportImportNomValCurSum = spainExportImportNomValCurSum.stream()
				.mapToDouble(Double::doubleValue).sum();
		double totalSpainExportImportCerSum = spainExportImportCerSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainExportImportNomValSum = spainExportImportNomValSum.stream().mapToDouble(Double::doubleValue)
				.sum();

		String CadenaSpainComFor = spainComFor.stream().collect(Collectors.joining(""));
		double totalSpainComForNomValCurSum = spainComForNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainComForCerSum = spainComForCerSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainComForNomValSum = spainComForNomValSum.stream().mapToDouble(Double::doubleValue).sum();

		String CadenaSpainSindicado = spainSindicado.stream().collect(Collectors.joining(""));
		double totalSpainSindicadoNomValCurSum = spainSindicadoNomValCurSum.stream().mapToDouble(Double::doubleValue)
				.sum();
		double totalSpainSindicadoCerSum = spainSindicadoCerSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainSindicadoNomValSum = spainSindicadoNomValSum.stream().mapToDouble(Double::doubleValue).sum();

		String CadenaSpainConfir = spainConfir.stream().collect(Collectors.joining(""));
		double totalSpainConfirNomValCurSum = spainConfirNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainConfirCerSum = spainConfirCerSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainConfirNomValSum = spainConfirNomValSum.stream().mapToDouble(Double::doubleValue).sum();

		String CadenaSpainDesc = spainDesc.stream().collect(Collectors.joining(""));
		double totalSpainDescNomValCurSum = spainDescNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainDescCerSum = spainDescCerSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainDescNomValSum = spainDescNomValSum.stream().mapToDouble(Double::doubleValue).sum();

		String CadenaSpainFac = spainFac.stream().collect(Collectors.joining(""));
		double totalSpainFacNomValCurSum = spainFacNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainFacCerSum = spainFacCerSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainFacNomValSum = spainFacNomValSum.stream().mapToDouble(Double::doubleValue).sum();

		String CadenaSpainTar = spainTar.stream().collect(Collectors.joining(""));
		double totalSpainTarNomValCurSum = spainTarNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainTarCerSum = spainTarCerSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainTarNomValSum = spainTarNomValSum.stream().mapToDouble(Double::doubleValue).sum();

		String CadenaSpainLinCom = spainLinCom.stream().collect(Collectors.joining(""));
		double totalSpainLinComNomValCurSum = spainLinComNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainLinComCerSum = spainLinComCerSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainLinComNomValSum = spainLinComNomValSum.stream().mapToDouble(Double::doubleValue).sum();

		String CadenaSpainNoLinCom = SpainLinNoCom.stream().collect(Collectors.joining(""));
		double totalSpainLinNoComNomValCurSum = SpainLinNoComNomValCurSum.stream().mapToDouble(Double::doubleValue)
				.sum();
		double totalSpainLinNoComCerSum = SpainLinNoComCerSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainLinNoComNomValSum = SpainLinNoComNomValSum.stream().mapToDouble(Double::doubleValue).sum();

		String CadenaSpainGaran = newList.stream().collect(Collectors.joining(""));
		double totalSpainGaranNomValCurSum = spainGaranNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainGaranCerSum = spainGaranCerSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainGaranNomValSum = spainGaranNomValSum.stream().mapToDouble(Double::doubleValue).sum();

		String CadenaSpainAval = spainAval.stream().collect(Collectors.joining(""));
		double totalSpainAvalNomValCurSum = spainAvalNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainAvalCerSum = spainAvalCerSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainAvalNomValSum = spainAvalNomValSum.stream().mapToDouble(Double::doubleValue).sum();

		String CadenaSpainDer = spainDer.stream().collect(Collectors.joining(""));
		double totalSpainDerNomValCurSum = spainDerNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainDerCerSum = spainDerCerSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainDerNomValSum = spainDerNomValSum.stream().mapToDouble(Double::doubleValue).sum();

		String CadenaSpainLeasingRenting = SpainLeasingRenting.stream().collect(Collectors.joining(""));
		double totalSpainLeasingRentingValCurSum = SpainLeasingRentingValCurSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainLeasingRentingCerSum = SpainLeasingRentingCerSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainLeasingRentingNomValSum = SpainLeasingRentingNomValSum.stream().mapToDouble(Double::doubleValue).sum();

		String CadenaSpainOverdrafts = SpainOverdrafts.stream().collect(Collectors.joining(""));
		double totalSpainOverdraftsValCurSum = SpainOverdraftsValCurSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainOverdraftsCerSum = SpainOverdraftsCerSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainOverdraftsNomValSum = SpainOverdraftsNomValSum.stream().mapToDouble(Double::doubleValue).sum();

		double totalSpainTotNomValCurSum = spainTotNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainTotCerSum = spainTotCerSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainTotNomValSum = spainTotNomValSum.stream().mapToDouble(Double::doubleValue).sum();

		try {
			FileWriter writer = new FileWriter(nombreInterfaz, true);

			// Aval
			if (!spainAval.isEmpty()) {
				writer.write(pais.toUpperCase() + " - AVAL" + "\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainAval);
				writer.write("TOTAL " + pais.toUpperCase() + " - AVALES" + "|" + "|" + "|" + "|" + "TOTAL AVALES" + "|"
						+ "|" + "|" + "|" + DFORMATO.format(totalSpainAvalNomValCurSum).toString() + "|"
						+ DFORMATO.format(totalSpainAvalCerSum).toString() + "|"
						+ DFORMATO.format(totalSpainAvalNomValSum).toString() + "|" + "|" + "|" + "|" + "|" + "|"
						+ "_");
				writer.write("\n");
				writer.write("\n");
			} // Bonos
			if (!spainBonos.isEmpty()) {
				writer.write(pais.toUpperCase() + " - BONOS" + "\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaBonosSpain);
				writer.write("TOTAL " + pais.toUpperCase() + " - BONOS" + "|" + "|" + "|" + "|" + "TOTAL BONOS" + "|"
						+ "|" + "|" + "|" + DFORMATO.format(totalSpainBonosNomValCur).toString() + "|"
						+ DFORMATO.format(totalSpainBonosCer).toString() + "|"
						+ DFORMATO.format(totalSpainBonosNomVal).toString() + "|" + "|" + "|" + "|" + "|" + "|" + "_");
				writer.write("\n");
				writer.write("\n");
			} // Confirming Spain
			if (!spainConfir.isEmpty()) {
				writer.write(pais.toUpperCase() + " - CONFIRMING" + "\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainConfir);
				writer.write(
						"TOTAL " + pais.toUpperCase() + " - CONFIRMING" + "|" + "|" + "|" + "|" + "TOTAL CONFIRMING"
								+ "|" + "|" + "|" + "|" + DFORMATO.format(totalSpainConfirNomValCurSum).toString() + "|"
								+ DFORMATO.format(totalSpainConfirCerSum).toString() + "|"
								+ DFORMATO.format(totalSpainConfirNomValSum).toString() + "|" + "|" + "|" + "|" + "|"
								+ "|" + "_");
				writer.write("\n");
				writer.write("\n");
			} // Creditos Documentariado
			if (!spainCredDocu.isEmpty()) {
				writer.write(pais.toUpperCase() + " - CREDITOS DOCUMENTARIADO" + "\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainCredDoc);
				writer.write("TOTAL " + pais.toUpperCase() + " - CREDITOS DOCUMENTARIOS" + "|" + "|" + "|" + "|"
						+ "TOTAL CREDITOS DOCUMENTARIOS" + "|" + "|" + "|" + "|"
						+ DFORMATO.format(totalSpainCredDocuNomValCurSum).toString() + "|"
						+ DFORMATO.format(totalSpainCredDocuCerSum).toString() + "|"
						+ DFORMATO.format(totalSpainCredDocuNomValSum).toString() + "|" + "|" + "|" + "|" + "|" + "|"
						+ "_");
				writer.write("\n");
				writer.write("\n");
			} // Creditos Sindicados Spain
			if (!spainSindicado.isEmpty()) {
				writer.write(pais.toUpperCase() + " - CREDITOS SINDICADOS" + "\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainSindicado);
				writer.write("TOTAL " + pais.toUpperCase() + " - CREDITOS SINDICADOS" + "|" + "|" + "|" + "|"
						+ "TOTAL CREDITOS SINDICADOS" + "|" + "|" + "|" + "|"
						+ DFORMATO.format(totalSpainSindicadoNomValCurSum).toString() + "|"
						+ DFORMATO.format(totalSpainSindicadoCerSum).toString() + "|"
						+ DFORMATO.format(totalSpainSindicadoNomValSum).toString() + "|" + "|" + "|" + "|" + "|" + "|"
						+ "_");
				writer.write("\n");
				writer.write("\n");
			} // Derivados Spain
			if (!spainDer.isEmpty()) {
				writer.write(pais.toUpperCase() + " - DERIVADOS" + "\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainDer);
				writer.write("TOTAL " + pais.toUpperCase() + " - DERIVADOS" + "|" + "|" + "|" + "|" + "TOTAL DERIVADOS"
						+ "|" + "|" + "|" + "|" + DFORMATO.format(totalSpainDerNomValCurSum).toString() + "|"
						+ DFORMATO.format(totalSpainDerCerSum).toString() + "|"
						+ DFORMATO.format(totalSpainDerNomValSum).toString() + "|" + "|" + "|" + "|" + "|" + "|" + "_");
				writer.write("\n");
				writer.write("\n");
			} // Descuentos
			if (!spainDesc.isEmpty()) {
				writer.write(pais.toUpperCase() + " - DESCUENTOS" + "\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainDesc);
				writer.write(
						"TOTAL " + pais.toUpperCase() + " - DESCUENTOS" + "|" + "|" + "|" + "|" + "TOTAL DESCUENTOS"
								+ "|" + "|" + "|" + "|" + DFORMATO.format(totalSpainDescNomValCurSum).toString() + "|"
								+ DFORMATO.format(totalSpainDescCerSum).toString() + "|"
								+ DFORMATO.format(totalSpainDescNomValSum).toString() + "|" + "|" + "|" + "|" + "|"
								+ "|" + "_");
				writer.write("\n");
				writer.write("\n");
			} // Factoring
			if (!spainFac.isEmpty()) {
				writer.write(pais.toUpperCase() + " - FACTORING" + "\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainFac);
				writer.write("TOTAL " + pais.toUpperCase() + " - FACTORING" + "|" + "|" + "|" + "|" + "TOTAL FACTORING"
						+ "|" + "|" + "|" + "|" + DFORMATO.format(totalSpainFacNomValCurSum).toString() + "|"
						+ DFORMATO.format(totalSpainFacCerSum).toString() + "|"
						+ DFORMATO.format(totalSpainFacNomValSum).toString() + "|" + "|" + "|" + "|" + "|" + "|" + "_");
				writer.write("\n");
				writer.write("\n");
			} // Financiamiento Comex
			if (!spainComFor.isEmpty()) {
				writer.write(pais.toUpperCase() + " - FINANCIAMIENTO COMEX" + "\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainComFor);
				writer.write("TOTAL " + pais.toUpperCase() + " - FINANCIAMIENTO COMEX" + "|" + "|" + "|" + "|"
						+ "TOTAL FINANCIAMIENTO COMEX" + "|" + "|" + "|" + "|"
						+ DFORMATO.format(totalSpainComForNomValCurSum).toString() + "|"
						+ DFORMATO.format(totalSpainComForCerSum).toString() + "|"
						+ DFORMATO.format(totalSpainComForNomValSum).toString() + "|" + "|" + "|" + "|" + "|" + "|"
						+ "_");
				writer.write("\n");
				writer.write("\n");
			} // Financiamiento IMP/EXP Spain
			if (!spainExportImport.isEmpty()) {
				writer.write(pais.toUpperCase() + " - FINANCIAMIENTO IMP/EXP" + "\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainExportImport);
				writer.write("TOTAL " + pais.toUpperCase() + " - FINANCIAMIENTO IMP/EXP" + "|" + "|" + "|" + "|"
						+ "TOTAL FINANCIAMIENTO IMP/EXP" + "|" + "|" + "|" + "|"
						+ DFORMATO.format(totalSpainExportImportNomValCurSum).toString() + "|"
						+ DFORMATO.format(totalSpainExportImportCerSum).toString() + "|"
						+ DFORMATO.format(totalSpainExportImportNomValSum).toString() + "|" + "|" + "|" + "|" + "|"
						+ "|" + "_");
				writer.write("\n");
				writer.write("\n");
			} // Garantias Spain
			if (!newList.isEmpty()) {
				writer.write(pais.toUpperCase() + " - GARANTIAS" + "\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainGaran);
				writer.write("TOTAL " + pais.toUpperCase() + " - GARANTIAS" + "|" + "|" + "|" + "|" + "TOTAL GARANTIAS"
						+ "|" + "|" + "|" + "|" + DFORMATO.format(totalSpainGaranNomValCurSum).toString() + "|"
						+ DFORMATO.format(totalSpainGaranCerSum).toString() + "|"
						+ DFORMATO.format(totalSpainGaranNomValSum).toString() + "|" + "|" + "|" + "|" + "|" + "|"
						+ "_");
				writer.write("\n");
				writer.write("\n");
			} // Leasing Renting
			if (!SpainLeasingRenting.isEmpty()) {
				writer.write(pais.toUpperCase() + " - LEASING - RENTING" + "\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainLeasingRenting);
				writer.write("TOTAL " + pais.toUpperCase() + " - LEASING - RENTING" + "|" + "|" + "|" + "|"
						+ "TOTAL LEASING - RENTING" + "|" + "|" + "|" + "|"
						+ DFORMATO.format(totalSpainLeasingRentingValCurSum).toString() + "|"
						+ DFORMATO.format(totalSpainLeasingRentingCerSum).toString() + "|"
						+ DFORMATO.format(totalSpainLeasingRentingNomValSum).toString() + "|" + "|" + "|" + "|" + "|"
						+ "|" + "_");
				writer.write("\n");
				writer.write("\n");
			} // Lineas Comprometidas
			if (!spainLinCom.isEmpty()) {
				writer.write(pais.toUpperCase() + " - LINEAS COMPORMETIDAS" + "\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainLinCom);
				writer.write("TOTAL " + pais.toUpperCase() + " - LINEAS COMPROMETIDAS" + "|" + "|" + "|" + "|"
						+ "TOTAL LINEAS COMPROMETIDAS" + "|" + "|" + "|" + "|"
						+ DFORMATO.format(totalSpainLinComNomValCurSum).toString() + "|"
						+ DFORMATO.format(totalSpainLinComCerSum).toString() + "|"
						+ DFORMATO.format(totalSpainLinComNomValSum).toString() + "|" + "|" + "|" + "|" + "|" + "|"
						+ "_");
				writer.write("\n");
				writer.write("\n");
			} // Lineas No Comprometidas
			if (!SpainLinNoCom.isEmpty()) {
				writer.write(pais.toUpperCase() + " - LINEAS NO COMPORMETIDAS" + "\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainNoLinCom);
				writer.write("TOTAL " + pais.toUpperCase() + " - LINEAS NO COMPROMETIDAS" + "|" + "|" + "|" + "|"
						+ "TOTAL LINEAS NO COMPROMETIDAS" + "|" + "|" + "|" + "|"
						+ DFORMATO.format(totalSpainLinNoComNomValCurSum).toString() + "|"
						+ DFORMATO.format(totalSpainLinNoComCerSum).toString() + "|"
						+ DFORMATO.format(totalSpainLinNoComNomValSum).toString() + "|" + "|" + "|" + "|" + "|" + "|"
						+ "_");
				writer.write("\n");
				writer.write("\n");
			} // Tarjeta de Credito
			if (!spainTar.isEmpty()) {
				writer.write(pais.toUpperCase() + " - TARJETA DE CREDITO" + "\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainTar);
				writer.write("TOTAL " + pais.toUpperCase() + " - TARJETAS DE CREDITO" + "|" + "|" + "|" + "|"
						+ "TOTAL TARJETAS DE CREDITO" + "|" + "|" + "|" + "|"
						+ DFORMATO.format(totalSpainTarNomValCurSum).toString() + "|"
						+ Double.toString(totalSpainTarCerSum).toString() + "|"
						+ DFORMATO.format(totalSpainTarNomValSum).toString() + "|" + "|" + "|" + "|" + "|" + "|" + "_");
				writer.write("\n");
				writer.write("\n");
			} // Overdrafts
			if (!SpainOverdrafts.isEmpty()) {
				writer.write(pais.toUpperCase() + " - OVERDRAFTS" + "\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainOverdrafts);
				writer.write(
						"TOTAL " + pais.toUpperCase() + " - OVERDRAFTS" + "|" + "|" + "|" + "|" + "TOTAL OVERDRAFTS"
								+ "|" + "|" + "|" + "|" + DFORMATO.format(totalSpainOverdraftsValCurSum).toString()
								+ "|" + Double.toString(totalSpainOverdraftsCerSum).toString() + "|"
								+ DFORMATO.format(totalSpainOverdraftsNomValSum).toString() + "|" + "|" + "|" + "|"
								+ "|" + "|" + "_");
				writer.write("\n");
				writer.write("\n");
			}
			if (totalSpainTotNomValCurSum != 0) {
				writer.write("TOTAL " + pais.toUpperCase() + "|" + "|" + "|" + "|" + "TOTAL GENERAL" + "|" + "|" + "|"
						+ "|" + DFORMATO.format(totalSpainTotNomValCurSum).toString() + "|"
						+ DFORMATO.format(totalSpainTotCerSum).toString() + "|"
						+ DFORMATO.format(totalSpainTotNomValSum).toString() + "|" + "|" + "|" + "|" + "|" + "|" + "_");
				writer.write("\n");
				writer.write("\n");
			}

			writer.flush();
			writer.close();
		} catch (IOException e) {
			LOGGER.info(e);
		}

	}

	/**
	 * Metodo getNombreGrupo obtiene el nombre de la empresa a la que le coresponde
	 * por medio del grupo
	 * 
	 * @param grupo para la consulta
	 * @param date  para la consulta del dia
	 * @return systCode regresa el nombre de la empresa
	 * @throws SQLException atrapa la excepcion generada en la ejecuion del query
	 */

	public String getNombreGrupo(String grupo, String date) throws SQLException {
		strbSql = new StringBuilder();
		String systCode = "";
		strbSql.append("SELECT lastparentfname  AS nominalvalue\r\n"
				+ "from PGT_MEX.T_PGT_MEX_CONSUMOSC_D WHERE LastParentF ='" + grupo + "' and FECHACARGA='" + date
				+ "' AND foldercountryname='Mexico'");
		try {
			pstmt = con.prepareStatement(strbSql.toString());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				systCode = rs.getString(1);
			} else {
				systCode = "No hay ultima carga";
			}
		} catch (SQLException e) {
			LOGGER.info(e);
			
		} catch (Exception e) {
			LOGGER.info(e);
		}

		return systCode;
	}

	/**
	 * validar carga Victoria
	 * 
	 * @return systCode regresa la fecha de la consulta
	 * @throws SQLException
	 */

	public String getCargaVictoria() throws SQLException {
		strbSql = new StringBuilder();
		String systCode = "";
		strbSql.append(
				"SELECT FECHACARGA FROM PGT_MEX.T_PGT_MEX_CONSUMOSC_V  WHERE TRUNC(FECHACARGA)<=(SELECT TO_CHAR(CURRENT_DATE ,'DDMMYYYY') FROM dual)Order by FECHACARGA DESC");
		try {
			pstmt = con.prepareStatement(strbSql.toString());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				systCode = rs.getString(1);
			} else {
				systCode = "No hay ultima carga";
			}
		} catch (SQLException e) {
			LOGGER.info(e);
		}

		return systCode;
	}

	/**
	 * validar carga Dolphing
	 * 
	 * @return systCode regresa la fecha de la consulta
	 * @throws SQLException atrapa la excepcion generada por la ejecucion del query
	 */

	public String getCargaDolphing() throws SQLException {
		strbSql = new StringBuilder();
		String systCode = "";
		strbSql.append(
				"SELECT FECHACARGA FROM PGT_MEX.T_PGT_MEX_CONSUMOSC_D  WHERE TRUNC(FECHACARGA)<=(SELECT TO_CHAR(CURRENT_DATE ,'DDMMYYYY') FROM dual)Order by FECHACARGA DESC");
		try {
			pstmt = con.prepareStatement(strbSql.toString());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				systCode = rs.getString(1);
			} else {
				systCode = "No hay ultima carga";
			}
		} catch (SQLException e) {
			LOGGER.info(e);
		}
		return systCode;
	}

	/**
	 * validar carga Victoria
	 * 
	 * @param fecha para obtener la ultima carga de los RTRA
	 * @throws SQLException generada durante la ejecucion del query
	 * @return systCode regresa la fecha de la consulta
	 */

	public String getCargaVictoriaHistorico(String fecha) throws SQLException {
		strbSql = new StringBuilder();
		String systCode = "";
		strbSql.append("SELECT FECHACARGA FROM PGT_MEX.T_PGT_MEX_CONSUMOSC_V  WHERE TRUNC(FECHACARGA)='" + fecha + "'");
		try {
			pstmt = con.prepareStatement(strbSql.toString());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				systCode = rs.getString(1);
			} else {
				systCode = "No hay ultima carga";
			}
		} catch (SQLException e) {
			LOGGER.info(e);
		}

		return systCode;
	}

	/**
	 * Commit
	 * 
	 * @throws SQLException
	 */
	public void commit() throws SQLException {
		con.commit();
	}

	/**
	 * Rollback
	 * 
	 * @throws SQLException
	 */
	public void rollback() throws SQLException {
		con.rollback();
	}
}