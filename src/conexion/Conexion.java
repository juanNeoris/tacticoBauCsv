package conexion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;



import oracle.jdbc.pool.OracleDataSource;

/**
 * Clase conexión para BD vía Wallet
 * 
 * @author hmh
 */
public class Conexion {

	private Connection con;
	private PreparedStatement pstmt;
	private Statement stmt;
	private ResultSet rs;
	private StringBuilder strbSql;

	public Conexion() {
		super();
		this.con = null;
		this.pstmt = null;
		this.stmt = null;
		this.rs = null;
	}

	/**
	 * Obtener una conexión a la base de datos, obteniendo los valores de los
	 * exports hecho a la sesión
	 * 
	 * @throws Exception
	 */
	public void conecGBO() throws Exception {

		OracleDataSource ods = new OracleDataSource();

		String connString = "jdbc:oracle:thin:@180.181.37.37:1651:mexmdr";

		ods.setURL(connString);
		ods.setUser("pgt_mex");
		ods.setPassword("pgt_mex");
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
	 * Obteiene el ID de la fecha de volcker
	 * 
	 * @param fecha
	 * @return
	 * @throws Exception
	 */

	public String getRec(String grupo, String nombreInterfaz, String fechaConsumo) throws Exception {
		Statement sta = con.createStatement();

		String systCode = " SELECT 'Codigo', 'Cliente', 'PAIS', 'Cod_Grupo', 'Nom_Grupo', 'MERFIN', 'FINAN', 'FIRMA', 'COMEX', 'TOTAL' FROM DUAL  UNION ALL SELECT CptyCode, CptyName, PAIS, GroupCode, GroupName, MERFINRECNTOTMAX, FINANRECNTOTTBMAX, FIRMARECNTOTMAX, COMEXRECNTOTMAX, TotalResultado FROM( SELECT  CptyCode,   CptyName , NVL((SELECT D.lastparentfcountryname FROM  PGT_MEX.T_PGT_MEX_CONSUMOSC_D D  WHERE  D.lastparentf='"
				+ grupo
				+ "' AND D.cptyparent=CptyCode AND ROWNUM=1),' ') AS PAIS, GroupCode, GroupName, TO_CHAR(REPLACE(MERFINRECNTOTMAX,'-', '0.00')) MERFINRECNTOTMAX , TO_CHAR(REPLACE(FINANRECNTOTTBMAX,'-', '0.00')) FINANRECNTOTTBMAX, TO_CHAR(REPLACE(FIRMARECNTOTMAX ,'-', '0.00'))FIRMARECNTOTMAX  , TO_CHAR(REPLACE(COMEXRECNTOTMAX ,'-', '0.00')) COMEXRECNTOTMAX , TO_CHAR((REPLACE(REPLACE(MERFINRECNTOTMAX,'-','0.00'),',','0.00')+REPLACE( REPLACE(FINANRECNTOTTBMAX,'-','0.00'),',','0.00')+REPLACE( REPLACE(FIRMARECNTOTMAX ,'-','0.00'),',','0.00')+REPLACE( REPLACE(COMEXRECNTOTMAX ,'-','0.00'),',','0.00'))) TotalResultado FROM PGT_MEX.T_PGT_CARTERA_BAU WHERE GroupCode='"
				+ grupo + "' and FECHACARGA='" + fechaConsumo + "' ORDER BY CptyCode ASC)";
		ResultSet rs = sta.executeQuery(systCode);

		if (rs.equals(null) || rs.next() == false) {
			systCode = "No existen registros para este grupo en la interfaz Rec";
		} else {
			String directoryName = System.getProperty("user.dir");
			FileOutputStream fos = new FileOutputStream(nombreInterfaz);
			do {
				systCode = rs.getString(1) + "," + "\"" + rs.getString(2) + "\"" + "," + rs.getString(3) + ","
						+ rs.getString(4) + "," + rs.getString(5) + "," + rs.getString(6) + "," + rs.getString(7) + ","
						+ rs.getString(8) + "," + rs.getString(9) + "," + rs.getString(10) + "\n";
				fos.write(systCode.getBytes());

			} while (rs.next());
			if (systCode.isEmpty()) {
				systCode = "No existen registros para este grupo en la interfaz CONSULTA";
			}
		}
		rs.close();
		sta.close();
		return systCode;
	}

	public String getCer(String grupo, String nombreInterfaz, String fechaConsumo) throws Exception {
		Statement sta = con.createStatement();
		String systCode = "SELECT 'Codigo', 'Cliente','PAIS','Cod_Grupo','Nom_Grupo','MERFIN','FINAN','FIRMA','COMEX','TOTAL','LIMITE' FROM DUAL UNION ALL SELECT CptyCode, CptyName, PAIS, GroupCode , GroupName, MERFINCERTOT, FINANCERTOT, FIRMACERTOT, COMEXCERTOT, Total,CERLimit from(SELECT CptyCode , CptyName , NVL((SELECT D.lastparentfcountryname FROM  PGT_MEX.T_PGT_MEX_CONSUMOSC_D D  WHERE  D.lastparentf='"
				+ grupo
				+ "' AND D.cptyparent=CptyCode AND ROWNUM=1), ' ') AS PAIS, GroupCode, GroupName, TO_CHAR(REPLACE(MERFINCERTOT,'-', '0.00')) MERFINCERTOT, TO_CHAR(REPLACE(FINANCERTOT,'-', '0.00'))FINANCERTOT, TO_CHAR(REPLACE( FIRMACERTOT ,'-', '0.00')) FIRMACERTOT ,TO_CHAR(REPLACE(COMEXCERTOT ,'-', '0.00')) COMEXCERTOT , TO_CHAR((REPLACE(REPLACE(MERFINCERTOT,'-',0),',','0.00')+REPLACE( REPLACE(FINANCERTOT, '-',0),',','0.00')+REPLACE(REPLACE(FIRMACERTOT,'-',0),',','0.00')+REPLACE(REPLACE(COMEXCERTOT,'-',0 ),',','0.00'))) Total, TO_CHAR(REPLACE(CERLimit ,'-', '0.00')) CERLimit FROM PGT_MEX.T_PGT_CARTERA_BAU WHERE GroupCode='"
				+ grupo + "' and FECHACARGA='" + fechaConsumo + "' ORDER BY CptyCode ASC)";
		ResultSet rs = sta.executeQuery(systCode);

		if (rs.equals(null) || rs.next() == false) {

			systCode = "No existen registros para este grupo en la interfaz CER";

		} else {
			String directoryName = System.getProperty("user.dir");
			FileOutputStream fos = new FileOutputStream(nombreInterfaz);

			do {
				systCode = rs.getString(1) + "," + "\"" + rs.getString(2) + "\"" + "," + rs.getString(3) + ","
						+ rs.getString(4) + "," + rs.getString(5) + "," + rs.getString(6) + "," + rs.getString(7) + ","
						+ rs.getString(8) + "," + rs.getString(9) + "," + rs.getString(10) + "," + rs.getString(11)
						+ "\n";
				fos.write(systCode.getBytes());

				if (rs.getFetchSize() == 1) {
					System.out.println("Esta en la primera lectura");
				}
			} while (rs.next());
			if (systCode.isEmpty()) {
				systCode = "No existen registros para este grupo en la interfaz CONSULTA";
			}

			fos.flush();
			fos.close();
		}
		rs.close();
		sta.close();
		return systCode;
	}

	public String getConsulta(String grupo, String nombreInterfaz, String fechaConsumo) throws Exception {

		Statement sta = con.createStatement();

		// bonos Mexico
		List<String> MexicoBonos = new ArrayList<String>();
		// bonos sumatoria Spain

		// bonos Spain

		// creditos documentariado Mexico
		List<String> MexicoCredDocu = new ArrayList<String>();
		// creditos documentariado sumatoria Mexico
		// creditos documentariado Spain

		// Exportacion/Importacion Mexico
		List<String> MexicoExportImport = new ArrayList<String>();
		// Exportacion/Importacion sumatoria Mexico
		// Exportacion/Importacion Spain

		// Comex/Forfaiting Mexico
		List<String> MexicoComFor = new ArrayList<String>();
		// Comex/Forfaiting sumatoria Mexico
		// Comex/Forfaiting Spain
		// Sindicado Mexico
		List<String> MexicoSindicado = new ArrayList<String>();
		// Sindicado sumatoria Mexico
		// Sindicado Spain

		// Confirming Mexico
		List<String> MexicoConfir = new ArrayList<String>();
		// Confirming sumatoria Mexico
		// Confirming Spain

		// Descuentos Mexico
		List<String> MexicoDesc = new ArrayList<String>();
		// Descuentos sumatoria Mexico
		// Descuentos Spain

		// Factoring Mexico
		List<String> MexicoFac = new ArrayList<String>();
		// Factoring sumatoria Mexico
		// Factoring Spain

		// Tarjetas
		List<String> MexicoTar = new ArrayList<String>();
		// Tarjetas sumatoria Mexico
		// Tarjetas Spain

		// Lineas Comprometidas Mexico
		List<String> MexicoLinCom = new ArrayList<String>();
		// Lineas Comprometidas sumatoria Mexico
		// Lineas Comprometidas Spain

		// Garantias Mexico
		List<String> MexicoGaran = new ArrayList<String>();
		// Garantias sumatoria Mexico

		// Avales Mexico
		List<String> MexicoAval = new ArrayList<String>();
		// Avales sumatoria Mexico

		// Derivados Mexico
		List<String> MexicoDer = new ArrayList<String>();
		// Derivados sumatoria Mexico

		List<String> encabezado = new ArrayList<>();
		encabezado.add("cptyparent");
		encabezado.add("cptyparentrating");
		encabezado.add("cptyparentname");
		encabezado.add("dealstamp");
		encabezado.add("instrumentname");
		encabezado.add("valuedate");
		encabezado.add("maturitydate");
		encabezado.add("currency");
		encabezado.add("nominalvaluecur");
		encabezado.add("cer");
		encabezado.add("nominalvalue");
		encabezado.add("oneoff");
		encabezado.add("cptyname");
		encabezado.add("foldercountryname");
		encabezado.add("cptycountry");
		encabezado.add("cptyparentcountry");
		encabezado.add("foldercountry");
		encabezado.add("\n");

		String systCode = "SELECT cptyparent,cptyparentrating,cptyparentname,dealstamp,instrumentname,valuedate,maturitydate,currency,DECODE(nominalvaluecur,null, '0.0',nominalvaluecur) AS nominalvaluecur,DECODE(CER,null, '0.0',CER) AS CER,DECODE(nominalvalue,null, '0.0',nominalvalue) AS nominalvalue,oneoff,cptyname,foldercountryname,cptycountry,cptyparentcountry,foldercountry from PGT_MEX.T_PGT_MEX_CONSUMOSC_V WHERE LastParentF ='"
				+ grupo + "' and FECHACARGA='" + fechaConsumo
				+ "' UNION ALL SELECT cptyparent,cptyparentrating,cptyparentname,dealstamp,instrumentname,valuedate,maturitydate,currency,DECODE(nominalvaluecur,null, '0.0',nominalvaluecur) AS nominalvaluecur,DECODE(CER,null, '0.0',CER) AS CER,DECODE(nominalvalue,null, '0.0',nominalvalue) AS nominalvalue,oneoff,cptyname,foldercountryname,cptycountry,cptyparentcountry,foldercountry from PGT_MEX.T_PGT_MEX_CONSUMOSC_D WHERE LastParentF ='"
				+ grupo + "' and FECHACARGA='" + fechaConsumo + "' ORDER BY foldercountry,instrumentname";
		ResultSet rs = sta.executeQuery(systCode);

		if (rs.equals(null) || rs.next() == false) {
			systCode = "No existen registros para este grupo en la interfaz CONSULTA";
		} else {
			String directoryName = System.getProperty("user.dir");

			FileWriter writer = new FileWriter(nombreInterfaz);

			do {
				// cadena
				systCode = rs.getString(1) + "," + rs.getString(2) + "," + "\"" + rs.getString(3) + "\"" + ","
						+ rs.getString(4) + "," + rs.getString(5) + "," + rs.getString(6) + "," + rs.getString(7) + ","
						+ rs.getString(8) + "," + rs.getString(9) + "," + rs.getString(10) + "," + rs.getString(11)
						+ "," + rs.getString(12) + "," + "\"" + rs.getString(13) + "\"" + "," + rs.getString(14) + ","
						+ "\"" + rs.getString(15) + "\"" + "," + rs.getString(16) + "," + rs.getString(17) + "\n";

				if (rs.getString(5).contains("BOND")) {
					MexicoBonos.add(systCode);

				} else if (rs.getString(5).contains(" CREDITO DOCUMENTARIO")) {
					MexicoCredDocu.add(systCode);

				} else if (rs.getString(5).contains("EXPORTACION") || rs.getString(5).contains("IMPORTACION")) {
					MexicoExportImport.add(systCode);

				} else if (rs.getString(5).contains("COMEX") || rs.getString(5).contains("FORFAITING")) {
					MexicoComFor.add(systCode);

				} else if (rs.getString(5).contains("SINDICADO")) {
					MexicoSindicado.add(systCode);

				} else if (rs.getString(5).contains("CONFIRMING")) {
					MexicoConfir.add(systCode);

				} else if (rs.getString(5).contains("DESCUENTOS")) {
					MexicoDesc.add(systCode);

				} else if (rs.getString(5).contains("FACTORING")) {
					MexicoFac.add(systCode);

				} else if (rs.getString(5).contains("TARJETAS")) {
					MexicoTar.add(systCode);

				} else if (rs.getString(5).contains("LINEA MULTIDEAL RESTO")
						|| rs.getString(5).contains("CREDITOS - COMPROMETIDO")
						|| rs.getString(5).contains("CREDITO BACKUP") || rs.getString(5).contains("CREDITO OTROS")) {
					MexicoLinCom.add(systCode);

				} else if (rs.getString(5).contains("GARANTIA ACCIONES") || rs.getString(5).contains("GARANTIA AVAL")
						|| rs.getString(5).contains("GARANTIA DERECHOS")
						|| rs.getString(5).contains("GARANTIA PERSONAL")
						|| rs.getString(5).contains("OTRAS GARANTIAS EN")
						|| rs.getString(5).contains("OTRAS GARANTIAS REALES")
						|| rs.getString(5).contains("OTHER GUARANTY")) {
					MexicoGaran.add(systCode);

				} else if (rs.getString(5).contains("AVAL COMERCIAL")
						|| rs.getString(5).contains("AVAL FINANCIERO - NO COMPROMETIDO")
						|| rs.getString(5).contains("AVAL NO") || rs.getString(5).contains("AVAL TECNICO")
						|| rs.getString(5).contains("GARANTIA LINE") || rs.getString(5).contains("STANDBY")
						|| rs.getString(5).contains("LINEA DE AVALES")) {
					MexicoAval.add(systCode);

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
					MexicoDer.add(systCode);

				}

			} while (rs.next());
			if (systCode.isEmpty()) {
				systCode = "No existen registros para este grupo en la interfaz CONSULTA";
			}
			// encabezados
			String CadenaEncabeza = encabezado.stream().collect(Collectors.joining(","));

			// Bonos
			String CadenaBonosMex = MexicoBonos.stream().collect(Collectors.joining(""));

			// Creditos Documentariados
			String CadenaMexicoCredDoc = MexicoCredDocu.stream().collect(Collectors.joining(""));

			// Exportaciones / importacions
			String CadenaMexicoExportImport = MexicoExportImport.stream().collect(Collectors.joining(""));

			// Comex/Forfaiting
			String CadenaMexicoComFor = MexicoComFor.stream().collect(Collectors.joining(""));

			// Sindicado
			String CadenaMexicoSindicado = MexicoSindicado.stream().collect(Collectors.joining(""));

			// Confirming
			String CadenaMexicoConfir = MexicoConfir.stream().collect(Collectors.joining(""));

			// Descuentos
			String CadenaMexicoDesc = MexicoDesc.stream().collect(Collectors.joining(""));

			// Descuentos
			String CadenaMexicoFac = MexicoFac.stream().collect(Collectors.joining(""));
			// Tarjetas
			String CadenaMexicoTar = MexicoTar.stream().collect(Collectors.joining(""));

			// Lineas Comprometidas
			String CadenaMexicoLinCom = MexicoLinCom.stream().collect(Collectors.joining(""));

			// Garantias
			String CadenaMexicoGaran = MexicoGaran.stream().collect(Collectors.joining(""));

			// Aval
			String CadenaMexicoAval = MexicoAval.stream().collect(Collectors.joining(""));

			// Derivados
			String CadenaMexicoDer = MexicoDer.stream().collect(Collectors.joining(""));

			// Bonos Mexico
			if (!MexicoBonos.isEmpty()) {
				writer.write("Bonos\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaBonosMex);
				writer.write("\n");

				// Credito Documentariado Mexico
			}
			if (!MexicoCredDocu.isEmpty()) {
				writer.write("Creditos Documentariado\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoCredDoc);
				writer.write("\n");
				// Exportacion/Importacion Mexio
			}
			if (!MexicoExportImport.isEmpty()) {
				writer.write("Financiamiento IMP/EXP\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoExportImport);
				writer.write("\n");
				// Comex/forfaiting Mexico
			}
			if (!MexicoComFor.isEmpty()) {
				writer.write("Financiamiento Comex\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoComFor);
				writer.write("\n");

				// Sindicado Mexico
			}
			if (!MexicoSindicado.isEmpty()) {
				writer.write("Creditos Sindicados\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoSindicado);
				writer.write("\n");
				// Confirming Mexico
			}
			if (!MexicoConfir.isEmpty()) {
				writer.write("Confirming\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoConfir);
				writer.write("\n");
				// Descuentos Mexico
			}
			if (!MexicoDesc.isEmpty()) {
				writer.write("Descuentos\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoDesc);
				writer.write("\n");
				// Factoring Mexico
			}
			if (!MexicoFac.isEmpty()) {
				writer.write("Factoring\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoFac);
				writer.write("\n");
				// Tarjetas Mexico
			}
			if (!MexicoTar.isEmpty()) {
				writer.write("Tarjeta de Credito\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoTar);
				writer.write("\n");
				// Lineas Comprometidas Mexico
			}
			if (!MexicoLinCom.isEmpty()) {
				writer.write("Lineas Comprometidas\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoLinCom);
				writer.write("\n");
				// Garantias Mexico
			}
			if (!MexicoGaran.isEmpty()) {
				writer.write("Garantias\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoGaran);
				writer.write("\n");
				// Aval Mexico
			}
			if (!MexicoAval.isEmpty()) {
				writer.write("Aval\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoAval);
				writer.write("\n");
			}
			if (!MexicoDer.isEmpty()) {
				writer.write("Derivados\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoDer);
				writer.write("\n");

			}

			writer.close();

		}

		return systCode;
	}

	/*********************
	 * validar carga cartera
	 ****************************************************************/
	public String getCargaCartera() throws Exception {
		strbSql = new StringBuilder();
		String systCode = "";

		strbSql.append(
				"SELECT FECHACARGA FROM PGT_MEX.T_PGT_CARTERA_BAU  WHERE TRUNC(FECHACARGA)<=(SELECT TO_CHAR(CURRENT_DATE ,'DDMMYYYY') FROM dual)Order by FECHACARGA DESC");
		try {
			pstmt = con.prepareStatement(strbSql.toString());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				systCode = rs.getString(1);
				// SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
				// date = simpleDateFormat.format(new Date());

			} else {
				systCode = "No hay ultima carga";
			}
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}

		return systCode;
	}

	/*********************
	 * validar carga contrapartida
	 ****************************************************************/

	public String getCargaContrapartida() throws Exception {
		strbSql = new StringBuilder();
		String systCode = "";
		strbSql.append(
				"SELECT FECHACARGA FROM PGT_MEX.T_PGT_CONTRAPARTIDA_BAU  WHERE TRUNC(FECHACARGA)<=(SELECT TO_CHAR(CURRENT_DATE ,'DDMMYYYY') FROM dual)Order by FECHACARGA DESC");
		try {
			pstmt = con.prepareStatement(strbSql.toString());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				systCode = rs.getString(1);
			} else {
				systCode = "No hay ultima carga";
			}
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}

		return systCode;
	}

	/*********************
	 * validar carga Victoria
	 ****************************************************************/

	public String getCargaVictoria() throws Exception {
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
			throw e;
		} catch (Exception e) {
			throw e;
		}

		return systCode;
	}

	/*********************
	 * validar carga Dolphing
	 ****************************************************************/

	public String getCargaDolphing() throws Exception {
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
			throw e;
		} catch (Exception e) {
			throw e;
		}

		return systCode;
	}

	/*********************
	 * validar carga cartera Historico
	 ****************************************************************/
	public String getCargaCarteraHistorico(String fecha) throws Exception {
		strbSql = new StringBuilder();
		String systCode = "";
		// String date = "";

		strbSql.append("SELECT FECHACARGA FROM PGT_MEX.T_PGT_CARTERA_BAU  WHERE TRUNC(FECHACARGA)='" + fecha + "'");
		try {
			pstmt = con.prepareStatement(strbSql.toString());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				systCode = rs.getString(1);
				// SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
				// date = simpleDateFormat.format(new Date());

			} else {
				systCode = "No hay ultima carga";
			}
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}

		return systCode;
	}

	/*********************
	 * validar carga contrapartida Historico
	 ****************************************************************/

	public String getCargaContrapartidaHistorico(String fecha) throws Exception {
		strbSql = new StringBuilder();
		String systCode = "";
		strbSql.append(
				"SELECT FECHACARGA FROM PGT_MEX.T_PGT_CONTRAPARTIDA_BAU  WHERE TRUNC(FECHACARGA)='" + fecha + "'");
		try {
			pstmt = con.prepareStatement(strbSql.toString());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				systCode = rs.getString(1);
			} else {
				systCode = "No hay ultima carga";
			}
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}

		return systCode;
	}

	/*********************
	 * validar carga Victoria
	 ****************************************************************/

	public String getCargaVictoriaHistorico(String fecha) throws Exception {
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
			throw e;
		} catch (Exception e) {
			throw e;
		}

		return systCode;
	}

	/*********************
	 * validar carga Dolphing
	 ****************************************************************/

	public String getCargaDolphingHistorico(String fecha) throws Exception {
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
			throw e;
		} catch (Exception e) {
			throw e;
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