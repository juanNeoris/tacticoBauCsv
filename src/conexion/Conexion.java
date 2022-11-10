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

import oracle.jdbc.pool.OracleDataSource;
import validacion.ValidaIntrumentos;

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
	 * @throws SQLException generada durante la ejecucion del query
	 */
	public String getConsultaMexico(String grupo, String nombreInterfaz, String fechaConsumo) throws SQLException {
		Statement sta = con.createStatement();
		ValidaIntrumentos valida = new ValidaIntrumentos();

		String systCode = "SELECT cptyparent, NVL(cptyparentrating, 'SIN RATING'),cptyparentname,dealstamp,instrumentname,TO_CHAR(TO_DATE(valuedate,  'YYYY-MM-DD'), 'DD-mon-YY'),TO_CHAR(TO_DATE(maturitydate,  'YYYY-MM-DD'), 'DD-mon-YY'),currency,to_char(DECODE(nominalvaluecur,null, '0.0',nominalvaluecur), '999,999,999,999.99')  AS nominalvaluecur,to_char(DECODE(CER,null, '0.0',CER), '999,999,999,999.99')  AS CER,to_char(DECODE(nominalvalue,null, '0.0',nominalvalue), '999,999,999,999.99')  AS nominalvalue,oneoff,cptyname,foldercountryname,cptycountry,cptyparentcountry,foldercountry from PGT_MEX.T_PGT_MEX_CONSUMOSC_V WHERE LastParentF ='"
				+ grupo + "' and FECHACARGA='" + fechaConsumo
				+ "' AND foldercountryname='Mexico' UNION ALL SELECT cptyparent, NVL(cptyparentrating, 'SIN RATING'),cptyparentname,dealstamp,instrumentname,TO_CHAR(TO_DATE(valuedate,  'YYYY-MM-DD'), 'DD-mon-YY'),TO_CHAR(TO_DATE(maturitydate,  'YYYY-MM-DD'), 'DD-mon-YY'),currency,to_char(DECODE(nominalvaluecur,null, '0.0',nominalvaluecur), '999,999,999,999.99')  AS nominalvaluecur,to_char(DECODE(CER,null, '0.0',CER), '999,999,999,999.99')  AS CER,to_char(DECODE(nominalvalue,null, '0.0',nominalvalue), '999,999,999,999.99')  AS nominalvalue,oneoff,cptyname,foldercountryname,cptycountry,cptyparentcountry,foldercountry from PGT_MEX.T_PGT_MEX_CONSUMOSC_D WHERE LastParentF ='"
				+ grupo + "' and FECHACARGA='" + fechaConsumo
				+ "' AND foldercountryname='Mexico' ORDER BY foldercountryname,instrumentname ";
		ResultSet rs = sta.executeQuery(systCode);
		int total = getQueryRowCount(systCode);

		if (rs.equals(null) || rs.next() == false) {
			systCode = "No existen registros para este grupo en la interfaz CONSULTA";
		} else {
			do {
				// cadena
				systCode = rs.getString(1) + "|" + rs.getString(2) + "|" + "\"" + rs.getString(3) + "\"" + "|"
						+ rs.getString(4) + "|" + rs.getString(5) + "|" + rs.getString(6) + "|" + rs.getString(7) + "|"
						+ rs.getString(8) + "|" + rs.getString(9) + "|" + rs.getString(10) + "|" + rs.getString(11)
						+ "|" + rs.getString(12) + "|" + "\"" + rs.getString(13) + "\"" + "|" + rs.getString(14) + "|"
						+ "\"" + rs.getString(15) + "\"" + "|" + rs.getString(16) + "|" + rs.getString(17) + "\n";

				// cadena obtener la sumatoria
				try {
					valida.intrumentosParteUno(systCode, rs.getString(5), fechaConsumo, rs.getString(4),
							rs.getString(14), rs.getString(9), rs.getString(10), rs.getString(11), nombreInterfaz,
							total);
				} catch (Exception e) {
					LOGGER.info(e);
				}

			} while (rs.next());
			if (systCode.isEmpty()) {
				systCode = "No existen registros para este grupo en la interfaz CONSULTA";
			}

		}

		return systCode;
	}

	int getQueryRowCount(String query) throws SQLException {

		try (Statement sta = con.createStatement(); ResultSet standardRS = sta.executeQuery(query)) {
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

		String systCode = "SELECT cptyparent, NVL(cptyparentrating, 'SIN RATING'),cptyparentname,dealstamp,instrumentname,TO_CHAR(TO_DATE(valuedate,  'YYYY-MM-DD'), 'DD-mon-YY'),TO_CHAR(TO_DATE(maturitydate,  'YYYY-MM-DD'), 'DD-mon-YY'),currency,to_char(DECODE(nominalvaluecur,null, '0.0',nominalvaluecur), '999,999,999,999.99')  AS nominalvaluecur,to_char(DECODE(CER,null, '0.0',CER), '999,999,999,999.99')  AS CER,to_char(DECODE(nominalvalue,null, '0.0',nominalvalue), '999,999,999,999.99')  AS nominalvalue,oneoff,cptyname,foldercountryname,cptycountry,cptyparentcountry,foldercountry from PGT_MEX.T_PGT_MEX_CONSUMOSC_V WHERE LastParentF ='"
				+ grupo + "' and FECHACARGA='" + fechaConsumo
				+ "' AND foldercountryname<>'Mexico' UNION ALL SELECT cptyparent, NVL(cptyparentrating, 'SIN RATING'),cptyparentname,dealstamp,instrumentname,TO_CHAR(TO_DATE(valuedate,  'YYYY-MM-DD'), 'DD-mon-YY'),TO_CHAR(TO_DATE(maturitydate,  'YYYY-MM-DD'), 'DD-mon-YY'),currency,to_char(DECODE(nominalvaluecur,null, '0.0',nominalvaluecur), '999,999,999,999.99')  AS nominalvaluecur,to_char(DECODE(CER,null, '0.0',CER), '999,999,999,999.99')  AS CER,to_char(DECODE(nominalvalue,null, '0.0',nominalvalue), '999,999,999,999.99')  AS nominalvalue,oneoff,cptyname,foldercountryname,cptycountry,cptyparentcountry,foldercountry from PGT_MEX.T_PGT_MEX_CONSUMOSC_D WHERE LastParentF ='"
				+ grupo + "' and FECHACARGA='" + fechaConsumo
				+ "' AND foldercountryname<>'Mexico' ORDER BY foldercountryname ,instrumentname ";
		ResultSet rs = sta.executeQuery(systCode);
		ValidaIntrumentos valida = new ValidaIntrumentos();
		int total = getQueryRowCount(systCode);
		if (rs.equals(null) || rs.next() == false) {
			systCode = "No existen registros para este grupo en la interfaz CONSULTA";
		} else {

			do {
				// cadena
				systCode = rs.getString(1) + "|" + rs.getString(2) + "|" + "\"" + rs.getString(3) + "\"" + "|"
						+ rs.getString(4) + "|" + rs.getString(5) + "|" + rs.getString(6) + "|" + rs.getString(7) + "|"
						+ rs.getString(8) + "|" + rs.getString(9) + "|" + rs.getString(10) + "|" + rs.getString(11)
						+ "|" + rs.getString(12) + "|" + "\"" + rs.getString(13) + "\"" + "|" + rs.getString(14) + "|"
						+ "\"" + rs.getString(15) + "\"" + "|" + rs.getString(16) + "|" + rs.getString(17) + "\n";

				try {
					valida.intrumentosParteUno(systCode, rs.getString(5), fechaConsumo, rs.getString(4),
							rs.getString(14), rs.getString(9), rs.getString(10), rs.getString(11), nombreInterfaz,total);
				} catch (Exception e) {
					LOGGER.info(e);
				}

			} while (rs.next());
			if (systCode.isEmpty()) {
				systCode = "No existen registros para este grupo en la interfaz CONSULTA";
			}

		}

		return systCode;
	}

	/**
	 * Metodo getContraparte validar si tiene una garantia la operacion que se esta
	 * evaluando
	 * 
	 * @param fechaConsumo se valida con la fecha que se esta evaluando
	 * @param deal         usa para validar la garantia
	 * @param pais         otra condicion es que sea del pais
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
		double totalSpainLeasingRentingValCurSum = SpainLeasingRentingValCurSum.stream()
				.mapToDouble(Double::doubleValue).sum();
		double totalSpainLeasingRentingCerSum = SpainLeasingRentingCerSum.stream().mapToDouble(Double::doubleValue)
				.sum();
		double totalSpainLeasingRentingNomValSum = SpainLeasingRentingNomValSum.stream()
				.mapToDouble(Double::doubleValue).sum();

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