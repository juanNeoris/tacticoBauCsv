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

import org.apache.commons.io.FileUtils;

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
		String str = "GARANTIA";
		double sumatoriaNomValCur;
		double sumatoriaCer;
		double sumatoriaNomVal;

		// bonos Mexico
		List<String> MexicoBonos = new ArrayList<String>();
		// bonos sumatoria Spain
		List<Double> MexicoBonosNomValCurSum = new ArrayList<Double>();
		List<Double> MexicoBonosCerSum = new ArrayList<Double>();
		List<Double> MexicoBonosNomValSum = new ArrayList<Double>();
		
		
		// bonos Spain
		List<String> SpainBonos = new ArrayList<String>();
		// bonos sumatoria Spain
		List<Double> SpainBonosNomValCurSum = new ArrayList<Double>();
		List<Double> SpainBonosCerSum = new ArrayList<Double>();
		List<Double> SpainBonosNomValSum = new ArrayList<Double>();

		
		// creditos documentariado Mexico
		List<String> MexicoCredDocu = new ArrayList<String>();
		// creditos documentariado sumatoria Mexico
		List<Double> MexicoCredDocuNomValCurSum = new ArrayList<Double>();
		List<Double> MexicoCredDocuCerSum = new ArrayList<Double>();
		List<Double> MexicoCredDocuNomValSum = new ArrayList<Double>();
		
		
		// creditos documentariado Spain
		List<String> SpainCredDocu = new ArrayList<String>();
		// creditos documentariado sumatoria Spain
		List<Double> SpainCredDocuNomValCurSum = new ArrayList<Double>();
		List<Double> SpainCredDocuCerSum = new ArrayList<Double>();
		List<Double> SpainCredDocuNomValSum = new ArrayList<Double>();

		
		// Exportacion/Importacion Mexico
		List<String> MexicoExportImport = new ArrayList<String>();
		// Exportacion/Importacion sumatoria Mexico
		List<Double> MexicoExportImportNomValCurSum = new ArrayList<Double>();
		List<Double> MexicoExportImportCerSum = new ArrayList<Double>();
		List<Double> MexicoExportImportNomValSum = new ArrayList<Double>();
		
		
		// Exportacion/Importacion Spain
		List<String> SpainExportImport = new ArrayList<String>();
		// Exportacion/Importacion sumatoria Spain
		List<Double> SpainExportImportNomValCurSum = new ArrayList<Double>();
		List<Double> SpainExportImportCerSum = new ArrayList<Double>();
		List<Double> SpainExportImportNomValSum = new ArrayList<Double>();
		
		
		
		// Comex/Forfaiting Mexico
		List<String> MexicoComFor = new ArrayList<String>();
		// Comex/Forfaiting sumatoria Mexico
		List<Double> MexicoComForNomValCurSum = new ArrayList<Double>();
		List<Double> MexicoComForCerSum = new ArrayList<Double>();
		List<Double> MexicoComForNomValSum = new ArrayList<Double>();
		// Comex/Forfaiting Spain
		List<String> SpainComFor = new ArrayList<String>();
		// Comex/Forfaiting sumatoria Spain
		List<Double> SpainComForNomValCurSum = new ArrayList<Double>();
		List<Double> SpainComForCerSum = new ArrayList<Double>();
		List<Double> SpainComForNomValSum = new ArrayList<Double>();
		
		
		
		// Sindicado Mexico
		List<String> MexicoSindicado = new ArrayList<String>();
		// Sindicado sumatoria Mexico
		List<Double> MexicoSindicadoNomValCurSum = new ArrayList<Double>();
		List<Double> MexicoSindicadoCerSum = new ArrayList<Double>();
		List<Double> MexicoSindicadoNomValSum = new ArrayList<Double>();
		// Sindicado  Spain
		List<String> SpainSindicado = new ArrayList<String>();
		// Sindicado sumatoria Spain
		List<Double> SpainSindicadoNomValCurSum = new ArrayList<Double>();
		List<Double> SpainSindicadoCerSum = new ArrayList<Double>();
		List<Double> SpainSindicadoNomValSum = new ArrayList<Double>();
		
		
		// Confirming Mexico
		List<String> MexicoConfir = new ArrayList<String>();
		// Confirming sumatoria Mexico
		List<Double> MexicoConfirNomValCurSum = new ArrayList<Double>();
		List<Double> MexicoConfirCerSum = new ArrayList<Double>();
		List<Double> MexicoConfirNomValSum = new ArrayList<Double>();
		// Confirming Spain
		List<String> SpainConfir = new ArrayList<String>();
		// Confirming sumatoria Spain
		List<Double> SpainConfirNomValCurSum = new ArrayList<Double>();
		List<Double> SpainConfirCerSum = new ArrayList<Double>();
		List<Double> SpainConfirNomValSum = new ArrayList<Double>();
		
		
		
		// Descuentos Mexico
		List<String> MexicoDesc = new ArrayList<String>();
		// Descuentos sumatoria Mexico
		List<Double> MexicoDescValCurSum = new ArrayList<Double>();
		List<Double> MexicoDescCerSum = new ArrayList<Double>();
		List<Double> MexicoDescNomValSum = new ArrayList<Double>();
		// Descuentos Spain
		List<String> SpainDesc = new ArrayList<String>();
		// Descuentos sumatoria Spain
		List<Double> SpainDescNomValCurSum = new ArrayList<Double>();
		List<Double> SpainDescCerSum = new ArrayList<Double>();
		List<Double> SpainDescNomValSum = new ArrayList<Double>();
		
		
		
		// Factoring Mexico
		List<String> MexicoFac = new ArrayList<String>();
		// Factoring sumatoria Mexico
		List<Double> MexicoFacValCurSum = new ArrayList<Double>();
		List<Double> MexicoFacCerSum = new ArrayList<Double>();
		List<Double> MexicoFacNomValSum = new ArrayList<Double>();
		// Factoring Spain
		List<String> SpainFac = new ArrayList<String>();
		// Factoring sumatoria Spain
		List<Double> SpainFacNomValCurSum = new ArrayList<Double>();
		List<Double> SpainFacCerSum = new ArrayList<Double>();
		List<Double> SpainFacNomValSum = new ArrayList<Double>();
		
		
		// Tarjetas
		List<String> MexicoTar = new ArrayList<String>();
		// Tarjetas sumatoria Mexico
		List<Double> MexicoTarValCurSum = new ArrayList<Double>();
		List<Double> MexicoTarCerSum = new ArrayList<Double>();
		List<Double> MexicoTarNomValSum = new ArrayList<Double>();
		// Tarjetas Spain
		List<String> SpainTar = new ArrayList<String>();
		// Tarjetas sumatoria Spain
		List<Double> SpainTarNomValCurSum = new ArrayList<Double>();
		List<Double> SpainTarCerSum = new ArrayList<Double>();
		List<Double> SpainTarNomValSum = new ArrayList<Double>();
		
		// Lineas Comprometidas Mexico
		List<String> MexicoLinCom = new ArrayList<String>();
		// Lineas Comprometidas sumatoria Mexico
		List<Double> MexicoLinComValCurSum = new ArrayList<Double>();
		List<Double> MexicoLinComCerSum = new ArrayList<Double>();
		List<Double> MexicoLinComNomValSum = new ArrayList<Double>();
		// Lineas Comprometidas Spain
		List<String> SpainLinCom = new ArrayList<String>();
		// Lineas Comprometidas sumatorias Spain
		List<Double> SpainLinComNomValCurSum = new ArrayList<Double>();
		List<Double> SpainLinComCerSum = new ArrayList<Double>();
		List<Double> SpainLinComNomValSum = new ArrayList<Double>();
		
		
		// Garantias Mexico 
		List<String> MexicoGaran = new ArrayList<String>();
		// Garantias sumatoria Mexico
		List<Double> MexicoGaranValCurSum = new ArrayList<Double>();
		List<Double> MexicoGaranCerSum = new ArrayList<Double>();
		List<Double> MexicoGaranNomValSum = new ArrayList<Double>();
		// Garantias Spain
		List<String> SpainGaran = new ArrayList<String>();
		// Garantias sumatoria Mexico
		List<Double> SpainGaranNomValCurSum = new ArrayList<Double>();
		List<Double> SpainGaranCerSum = new ArrayList<Double>();
		List<Double> SpainGaranNomValSum = new ArrayList<Double>();
		
		
		// Avales Mexico
		List<String> MexicoAval = new ArrayList<String>();
		// Avales sumatoria Mexico
		List<Double> MexicoAvalValCurSum = new ArrayList<Double>();
		List<Double> MexicoAvalCerSum = new ArrayList<Double>();
		List<Double> MexicoAvalNomValSum = new ArrayList<Double>();
		
		// Avales Spain
		List<String> SpainAval = new ArrayList<String>();
		// Avales sumatoria Spain
		List<Double> SpainAvalNomValCurSum = new ArrayList<Double>();
		List<Double> SpainAvalCerSum = new ArrayList<Double>();
		List<Double> SpainAvalNomValSum = new ArrayList<Double>();
		
		// Derivados Mexico
		List<String> MexicoDer = new ArrayList<String>();
		// Derivados sumatoria Mexico
		List<Double> MexicoDerValCurSum = new ArrayList<Double>();
		List<Double> MexicoDerCerSum = new ArrayList<Double>();
		List<Double> MexicoDerNomValSum = new ArrayList<Double>();
		
		// Derivados Spain
		List<String> SpainDer = new ArrayList<String>();
		// Derivados sumatoria Spain
		List<Double> SpainDerNomValCurSum = new ArrayList<Double>();
		List<Double> SpainDerCerSum = new ArrayList<Double>();
		List<Double> SpainDerNomValSum = new ArrayList<Double>();
		
		//Total general Mexico
		List<Double> MexicoTotValCurSum = new ArrayList<Double>();
		List<Double> MexicoTotCerSum = new ArrayList<Double>();
		List<Double> MexicoTotNomValSum = new ArrayList<Double>();
		//Total General Spain
		List<Double> SpainTotNomValCurSum = new ArrayList<Double>();
		List<Double> SpainTotCerSum = new ArrayList<Double>();
		List<Double> SpainTotNomValSum = new ArrayList<Double>();
		
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
				+ grupo + "' and FECHACARGA='" + fechaConsumo + "'";
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
						+ "," + rs.getString(12) + "," + rs.getString(13) + "," + rs.getString(14) + "," + "\""
						+ rs.getString(15) + "\"" + "," + rs.getString(16) + "," + rs.getString(17) + "\n";

				// cadena obtener la sumatoria
				sumatoriaNomValCur = rs.getDouble(9);
				sumatoriaCer = rs.getDouble(10);
				sumatoriaNomVal = rs.getDouble(11);

				if (rs.getString(14).contains("Mexico")) {

					if (rs.getString(5).contains("BOND")) {
						MexicoBonos.add(systCode);
						MexicoBonosNomValCurSum.add(Double.valueOf(sumatoriaNomValCur));
						MexicoBonosCerSum.add(Double.valueOf(sumatoriaCer));
						MexicoBonosNomValSum.add(Double.valueOf(sumatoriaNomVal));
						MexicoTotValCurSum.add(sumatoriaNomValCur);
						MexicoTotCerSum.add(sumatoriaCer);
						MexicoTotNomValSum.add(sumatoriaNomVal);
						
					} else if (rs.getString(5).contains(" CREDITO DOCUMENTARIO")) {
						MexicoCredDocu.add(systCode);
						MexicoCredDocuNomValCurSum.add(sumatoriaNomValCur);
						MexicoCredDocuCerSum.add(sumatoriaCer);
						MexicoCredDocuNomValSum.add(sumatoriaNomVal);
						MexicoTotValCurSum.add(sumatoriaNomValCur);
						MexicoTotCerSum.add(sumatoriaCer);
						MexicoTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("EXPORTACION") || rs.getString(5).contains("IMPORTACION")) {
						MexicoExportImport.add(systCode);
						MexicoExportImportNomValCurSum.add(sumatoriaNomValCur);
						MexicoExportImportCerSum.add(sumatoriaCer);
						MexicoExportImportNomValSum.add(sumatoriaNomVal);
						MexicoTotValCurSum.add(sumatoriaNomValCur);
						MexicoTotCerSum.add(sumatoriaCer);
						MexicoTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("COMEX") || rs.getString(5).contains("FORFAITING")) {
						MexicoComFor.add(systCode);
						MexicoComForNomValCurSum.add(sumatoriaNomValCur);
						MexicoComForCerSum.add(sumatoriaCer);
						MexicoComForNomValSum.add(sumatoriaNomVal);
						MexicoTotValCurSum.add(sumatoriaNomValCur);
						MexicoTotCerSum.add(sumatoriaCer);
						MexicoTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("SINDICADO")) {
						MexicoSindicado.add(systCode);
						MexicoSindicadoNomValCurSum.add(sumatoriaNomValCur);
						MexicoSindicadoCerSum.add(sumatoriaCer);
						MexicoSindicadoNomValSum.add(sumatoriaNomVal);
						MexicoTotValCurSum.add(sumatoriaNomValCur);
						MexicoTotCerSum.add(sumatoriaCer);
						MexicoTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("CONFIRMING")) {
						MexicoConfir.add(systCode);
						MexicoConfirNomValCurSum.add(sumatoriaNomValCur);
						MexicoConfirCerSum.add(sumatoriaCer);
						MexicoConfirNomValSum.add(sumatoriaNomVal);
						MexicoTotValCurSum.add(sumatoriaNomValCur);
						MexicoTotCerSum.add(sumatoriaCer);
						MexicoTotNomValSum.add(sumatoriaNomVal);
						
					} else if (rs.getString(5).contains("DESCUENTOS")) {
						MexicoDesc.add(systCode);
						MexicoDescValCurSum.add(sumatoriaNomValCur);
						MexicoDescCerSum.add(sumatoriaCer);
						MexicoDescNomValSum.add(sumatoriaNomVal);
						MexicoTotValCurSum.add(sumatoriaNomValCur);
						MexicoTotCerSum.add(sumatoriaCer);
						MexicoTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("FACTORING")) {
						MexicoFac.add(systCode);
						MexicoFacValCurSum.add(sumatoriaNomValCur);
						MexicoFacCerSum.add(sumatoriaCer);
						MexicoFacNomValSum.add(sumatoriaNomVal);
						MexicoTotValCurSum.add(sumatoriaNomValCur);
						MexicoTotCerSum.add(sumatoriaCer);
						MexicoTotNomValSum.add(sumatoriaNomVal);
						
					} else if (rs.getString(5).contains("TARJETAS")) {
						MexicoTar.add(systCode);
						MexicoTarValCurSum.add(sumatoriaNomValCur);
						MexicoTarCerSum.add(sumatoriaCer);
						MexicoTarNomValSum.add(sumatoriaNomVal);
						MexicoTotValCurSum.add(sumatoriaNomValCur);
						MexicoTotCerSum.add(sumatoriaCer);
						MexicoTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("LINEA MULTIDEAL RESTO")
							|| rs.getString(5).contains("CREDITOS - COMPROMETIDO")
							|| rs.getString(5).contains("CREDITO BACKUP")
							|| rs.getString(5).contains("CREDITO OTROS")) {
						MexicoLinCom.add(systCode);
						MexicoLinComValCurSum.add(sumatoriaNomValCur);
						MexicoLinComCerSum.add(sumatoriaCer);
						MexicoLinComNomValSum.add(sumatoriaNomVal);
						MexicoTotValCurSum.add(sumatoriaNomValCur);
						MexicoTotCerSum.add(sumatoriaCer);
						MexicoTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("GARANTIA ACCIONES")
							|| rs.getString(5).contains("GARANTIA AVAL")
							|| rs.getString(5).contains("GARANTIA DERECHOS")
							|| rs.getString(5).contains("GARANTIA PERSONAL")
							|| rs.getString(5).contains("OTRAS GARANTIAS EN")
							|| rs.getString(5).contains("OTRAS GARANTIAS REALES")
							|| rs.getString(5).contains("OTHER GUARANTY")) {
						MexicoGaran.add(systCode);
						MexicoGaranValCurSum.add(sumatoriaNomValCur);
						MexicoGaranCerSum.add(sumatoriaCer);
						MexicoGaranNomValSum.add(sumatoriaNomVal);
						MexicoTotValCurSum.add(sumatoriaNomValCur);
						MexicoTotCerSum.add(sumatoriaCer);
						MexicoTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("AVAL COMERCIAL")
							|| rs.getString(5).contains("AVAL FINANCIERO - NO COMPROMETIDO")
							|| rs.getString(5).contains("AVAL NO") || rs.getString(5).contains("AVAL TECNICO")
							|| rs.getString(5).contains("GARANTIA LINE") || rs.getString(5).contains("STANDBY")
							|| rs.getString(5).contains("LINEA DE AVALES")) {
						MexicoAval.add(systCode);
						MexicoAvalValCurSum.add(sumatoriaNomValCur);
						MexicoAvalCerSum.add(sumatoriaCer);
						MexicoAvalNomValSum.add(sumatoriaNomVal);
						MexicoTotValCurSum.add(sumatoriaNomValCur);
						MexicoTotCerSum.add(sumatoriaCer);
						MexicoTotNomValSum.add(sumatoriaNomVal);
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
						MexicoDerValCurSum.add(sumatoriaNomValCur);
						MexicoDerCerSum.add(sumatoriaCer);
						MexicoDerNomValSum.add(sumatoriaNomVal);
						MexicoTotValCurSum.add(sumatoriaNomValCur);
						MexicoTotCerSum.add(sumatoriaCer);
						MexicoTotNomValSum.add(sumatoriaNomVal);
					}

				} else if (rs.getString(14).contains("Spain")) {

					if (rs.getString(5).contains("BOND")) {
						SpainBonos.add(systCode);
						SpainBonosNomValCurSum.add(Double.valueOf(sumatoriaNomValCur));
						SpainBonosCerSum.add(Double.valueOf(sumatoriaCer));
						SpainBonosNomValSum.add(Double.valueOf(sumatoriaNomVal));
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);

					} else if (rs.getString(5).contains(" CREDITO DOCUMENTARIO")) {
						SpainCredDocu.add(systCode);
						SpainCredDocuNomValCurSum.add(sumatoriaNomValCur);
						SpainCredDocuCerSum.add(sumatoriaCer);
						SpainCredDocuNomValSum.add(sumatoriaNomVal);
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("EXPORTACION") || rs.getString(5).contains("IMPORTACION")) {
						SpainExportImport.add(systCode);
						SpainExportImportNomValCurSum.add(sumatoriaNomValCur);
						SpainExportImportCerSum.add(sumatoriaCer);
						SpainExportImportNomValSum.add(sumatoriaNomVal);
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("COMEX") || rs.getString(5).contains("FORFAITING")) {
						SpainComFor.add(systCode);
						SpainComForNomValCurSum.add(sumatoriaNomValCur);
						SpainComForCerSum.add(sumatoriaCer);
						SpainComForNomValSum.add(sumatoriaNomVal);
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("SINDICADO")) {
						SpainSindicado.add(systCode);
						SpainSindicadoNomValCurSum.add(sumatoriaNomValCur);
						SpainSindicadoCerSum.add(sumatoriaCer);
						SpainSindicadoNomValSum.add(sumatoriaNomVal);
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);
						
					} else if (rs.getString(5).contains("CONFIRMING")) {
						SpainConfir.add(systCode);
						SpainConfirNomValCurSum.add(sumatoriaNomValCur);
						SpainConfirCerSum.add(sumatoriaCer);
						SpainConfirNomValSum.add(sumatoriaNomVal);
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("DESCUENTOS")) {
						SpainDesc.add(systCode);
						SpainDescNomValCurSum.add(sumatoriaNomValCur);
						SpainDescCerSum.add(sumatoriaCer);
						SpainDescNomValSum.add(sumatoriaNomVal);
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("FACTORING")) {
						SpainFac.add(systCode);
						SpainFacNomValCurSum.add(sumatoriaNomValCur);
						SpainFacCerSum.add(sumatoriaCer);
						SpainFacNomValSum.add(sumatoriaNomVal);
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);

					} else if (rs.getString(5).contains("TARJETAS")) {
						SpainTar.add(systCode);
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
						SpainDerNomValCurSum.add(sumatoriaNomValCur);
						SpainDerCerSum.add(sumatoriaCer);
						SpainDerNomValSum.add(sumatoriaNomVal);
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);
					}

				}

			} while (rs.next());
			if (systCode.isEmpty()) {
				systCode = "No existen registros para este grupo en la interfaz CONSULTA";
			}
			// encabezados
			String CadenaEncabeza = encabezado.stream().collect(Collectors.joining(","));

			// Bonos
			String CadenaBonosMex = MexicoBonos.stream().collect(Collectors.joining(""));
			double totalMexicoBonosNomValCur = MexicoBonosNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoBonosCer = MexicoBonosCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoBonosNomVal = MexicoBonosNomValSum.stream().mapToDouble(Double::doubleValue).sum();
			
			String CadenaBonosSpain = SpainBonos.stream().collect(Collectors.joining(""));
			double totalSpainBonosNomValCur = SpainBonosNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalSpainBonosCer = SpainBonosCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalSpainBonosNomVal = SpainBonosNomValSum.stream().mapToDouble(Double::doubleValue).sum();

			
			// Creditos Documentariados
			String CadenaMexicoCredDoc = MexicoCredDocu.stream().collect(Collectors.joining(""));
			double totalMexicoCredDocuNomValCurSum = MexicoCredDocuNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoCredDocuCerSum = MexicoCredDocuCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoCredDocuNomValSum = MexicoCredDocuNomValSum.stream().mapToDouble(Double::doubleValue).sum();
			
			String CadenaSpainCredDoc = SpainCredDocu.stream().collect(Collectors.joining(""));
			double totalSpainCredDocuNomValCurSum = SpainCredDocuNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalSpainCredDocuCerSum = SpainCredDocuCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalSpainCredDocuNomValSum = SpainCredDocuNomValSum.stream().mapToDouble(Double::doubleValue).sum();
			
			// Exportaciones / importacions
			String CadenaMexicoExportImport = MexicoExportImport.stream().collect(Collectors.joining(""));
			double totalMexicoExportImportNomValCurSum = MexicoExportImportNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoExportImportCerSum = MexicoExportImportCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoExportImportNomValSum = MexicoExportImportNomValSum.stream().mapToDouble(Double::doubleValue).sum();
			
			String CadenaSpainExportImport = SpainExportImport.stream().collect(Collectors.joining(""));
			double totalSpainExportImportNomValCurSum = SpainExportImportNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalSpainExportImportCerSum = SpainExportImportCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalSpainExportImportNomValSum = SpainExportImportNomValSum.stream().mapToDouble(Double::doubleValue).sum();
			
			
			
			// Comex/Forfaiting
			String CadenaMexicoComFor = MexicoComFor.stream().collect(Collectors.joining(""));
			double totalMexicoComForNomValCurSum = MexicoComForNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoComForCerSum = MexicoComForCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoComForNomValSum = MexicoComForNomValSum.stream().mapToDouble(Double::doubleValue).sum();
		
			String CadenaSpainComFor = SpainComFor.stream().collect(Collectors.joining(""));
			double totalSpainComForNomValCurSum = SpainComForNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalSpainComForCerSum = SpainComForCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalSpainComForNomValSum = SpainComForNomValSum.stream().mapToDouble(Double::doubleValue).sum();
			
			
			
			
			// Sindicado
			String CadenaMexicoSindicado = MexicoSindicado.stream().collect(Collectors.joining(""));
			double totalMexicoSindicadoNomValCurSum = MexicoSindicadoNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoSindicadoCerSum = MexicoSindicadoCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoSindicadoNomValSum = MexicoSindicadoNomValSum.stream().mapToDouble(Double::doubleValue).sum();
		
			String CadenaSpainSindicado = SpainSindicado.stream().collect(Collectors.joining(""));
			double totalSpainSindicadoNomValCurSum = SpainSindicadoNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalSpainSindicadoCerSum = SpainSindicadoCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalSpainSindicadoNomValSum = SpainSindicadoNomValSum.stream().mapToDouble(Double::doubleValue).sum();
		
			
			
			
			// Confirming
			String CadenaMexicoConfir = MexicoConfir.stream().collect(Collectors.joining(""));
			double totalMexicoConfirNomValCurSum = MexicoConfirNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoConfirCerSum = MexicoConfirCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoConfirNomValSum = MexicoConfirNomValSum.stream().mapToDouble(Double::doubleValue).sum();
		
			String CadenaSpainConfir = SpainConfir.stream().collect(Collectors.joining(""));
			double totalSpainConfirNomValCurSum = SpainConfirNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalSpainConfirCerSum = SpainConfirCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalSpainConfirNomValSum = SpainConfirNomValSum.stream().mapToDouble(Double::doubleValue).sum();
		
			
			// Descuentos
			String CadenaMexicoDesc = MexicoDesc.stream().collect(Collectors.joining(""));
			double totalMexicoDescValCurSum = MexicoDescValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoDescCerSum = MexicoDescCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoDescNomValSum = MexicoDescNomValSum.stream().mapToDouble(Double::doubleValue).sum();
		
			String CadenaSpainDesc = SpainDesc.stream().collect(Collectors.joining(""));
			double totalSpainDescNomValCurSum = SpainDescNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalSpainDescCerSum = SpainDescCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalSpainDescNomValSum = SpainDescNomValSum.stream().mapToDouble(Double::doubleValue).sum();
		
			
			
			// Descuentos
			String CadenaMexicoFac = MexicoFac.stream().collect(Collectors.joining(""));
			double totalMexicoFacValCurSum = MexicoFacValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoFacCerSum = MexicoFacCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoFacNomValSum = MexicoFacNomValSum.stream().mapToDouble(Double::doubleValue).sum();
		
			String CadenaSpainFac = SpainFac.stream().collect(Collectors.joining(""));
			double totalSpainFacNomValCurSum = SpainFacNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalSpainFacCerSum = SpainFacCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalSpainFacNomValSum = SpainFacNomValSum.stream().mapToDouble(Double::doubleValue).sum();
		
			
			// Tarjetas
			String CadenaMexicoTar = MexicoTar.stream().collect(Collectors.joining(""));
			double totalMexicoTarValCurSum = MexicoTarValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoTarCerSum = MexicoTarCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoTarNomValSum = MexicoTarNomValSum.stream().mapToDouble(Double::doubleValue).sum();
		
			String CadenaSpainTar = SpainTar.stream().collect(Collectors.joining(""));
			double totalSpainTarNomValCurSum = SpainTarNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalSpainTarCerSum = SpainTarCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalSpainTarNomValSum = SpainTarNomValSum.stream().mapToDouble(Double::doubleValue).sum();
		
			
			
			// Lineas Comprometidas
			String CadenaMexicoLinCom = MexicoLinCom.stream().collect(Collectors.joining(""));
			double totalMexicoLinComValCurSum = MexicoLinComValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoLinComCerSum = MexicoLinComCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoLinComNomValSum = MexicoLinComNomValSum.stream().mapToDouble(Double::doubleValue).sum();
		
			String CadenaSpainLinCom = SpainLinCom.stream().collect(Collectors.joining(""));
			double totalSpainLinComNomValCurSum = SpainLinComNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalSpainLinComCerSum = SpainLinComCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalSpainLinComNomValSum = SpainLinComNomValSum.stream().mapToDouble(Double::doubleValue).sum();
		
			
			
			// Garantias
			String CadenaMexicoGaran = MexicoGaran.stream().collect(Collectors.joining(""));
			double totalMexicoGaranValCurSum = MexicoGaranValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoGaranCerSum = MexicoGaranCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoGaranNomValSum = MexicoGaranNomValSum.stream().mapToDouble(Double::doubleValue).sum();
		
			String CadenaSpainGaran = SpainGaran.stream().collect(Collectors.joining(""));
			double totalSpainGaranNomValCurSum = SpainGaranNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalSpainGaranCerSum = SpainGaranCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalSpainGaranNomValSum = SpainGaranNomValSum.stream().mapToDouble(Double::doubleValue).sum();
		
			
			// Aval
			String CadenaMexicoAval = MexicoAval.stream().collect(Collectors.joining(""));
			double totalMexicoAvalValCurSum = MexicoAvalValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoAvalCerSum = MexicoAvalCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoAvalNomValSum = MexicoAvalNomValSum.stream().mapToDouble(Double::doubleValue).sum();
		
			String CadenaSpainAval = SpainAval.stream().collect(Collectors.joining(""));
			double totalSpainAvalNomValCurSum = SpainAvalNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalSpainAvalCerSum = SpainAvalCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalSpainAvalNomValSum = SpainAvalNomValSum.stream().mapToDouble(Double::doubleValue).sum();
		
			
			// Derivados
			String CadenaMexicoDer = MexicoDer.stream().collect(Collectors.joining(""));
			double totalMexicoDerValCurSum = MexicoDerValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoDerCerSum = MexicoDerCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoDerNomValSum = MexicoDerNomValSum.stream().mapToDouble(Double::doubleValue).sum();
		
			String CadenaSpainDer = SpainDer.stream().collect(Collectors.joining(""));
			double totalSpainDerNomValCurSum = SpainDerNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalSpainDerCerSum = SpainDerCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalSpainDerNomValSum = SpainDerNomValSum.stream().mapToDouble(Double::doubleValue).sum();
			
			//totales
			double totalMexicoTotValCurSum = MexicoTotValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoTotCerSum = MexicoTotCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoTotNomValSum = MexicoTotNomValSum.stream().mapToDouble(Double::doubleValue).sum();
			
			double totalSpainTotNomValCurSum = SpainTotNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalSpainTotCerSum = SpainTotCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalSpainTotNomValSum = SpainTotNomValSum.stream().mapToDouble(Double::doubleValue).sum();
			
			// Bonos Mexico
			if (!MexicoBonos.isEmpty()) {
				writer.write("Bonos-Mexico\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaBonosMex);
				writer.write("," + "," + "," + "," + "Total Bonos"+"," + "," + "," + "," +"'"+ Double.toString(totalMexicoBonosNomValCur)
				+ "," +"'"+ Double.toString(totalMexicoBonosCer) + "," +"'"+ Double.toString(totalMexicoBonosNomVal));
				writer.write("\n");

				// Credito Documentariado Mexico
			}
			if (!MexicoCredDocu.isEmpty()) {
				writer.write("Creditos Documentariado-Mexico\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoCredDoc);
				writer.write("," + "," + "," + "," + "Total Creditos Documentariado"+"," + "," + "," + "," +"'"+ Double.toString(totalMexicoCredDocuNomValCurSum)
				+ "," + "'"+ Double.toString(totalMexicoCredDocuCerSum) + "," +"'"+ Double.toString(totalMexicoCredDocuNomValSum));
				writer.write("\n");
				// Exportacion/Importacion Mexio
			}
			if (!MexicoExportImport.isEmpty()) {
				writer.write("Financiamiento IMP/EXP-Mexico\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoExportImport);
				writer.write("," + "," + "," + "," + "Total Financiamiento IMP/EXP"+"," + "," + "," + "," +"'"+ Double.toString(totalMexicoExportImportNomValCurSum)
				+ "," + "'"+ Double.toString(totalMexicoExportImportCerSum) + "," + "'"+ Double.toString(totalMexicoExportImportNomValSum));
				writer.write("\n");
				// Comex/forfaiting Mexico
			}
			if (!MexicoComFor.isEmpty()) {
				writer.write("Financiamiento Comex-Mexico\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoComFor);
				writer.write("," + "," + "," + "," + "Total Financiamiento Comex"+"," + "," + "," + "," + "'"+ Double.toString(totalMexicoComForNomValCurSum)
				+ "," + "'"+ Double.toString(totalMexicoComForCerSum) + "," + "'"+ Double.toString(totalMexicoComForNomValSum));
				writer.write("\n");
				
				// Sindicado Mexico
			}
			if (!MexicoSindicado.isEmpty()) {
				writer.write("Creditos Sindicados-Mexico\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoSindicado);
				writer.write("," + "," + "," + "," + "Total Creditos Sindicados"+"," + "," + "," + "," + "'"+ Double.toString(totalMexicoSindicadoNomValCurSum)
				+ "," + "'"+ Double.toString(totalMexicoSindicadoCerSum) + "," + "'"+ Double.toString(totalMexicoSindicadoNomValSum));
				writer.write("\n");
				// Confirming Mexico
			}
			if (!MexicoConfir.isEmpty()) {
				writer.write("Confirming-Mexico\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoConfir);
				writer.write("," + "," + "," + "," +"Total Confirming"+ "," + "," + "," + "," + "'"+ Double.toString(totalMexicoConfirNomValCurSum)
				+ "," + "'"+ Double.toString(totalMexicoConfirCerSum) + "," + "'"+ Double.toString(totalMexicoConfirNomValSum));
				
				writer.write("\n");
				// Descuentos Mexico
			}
			if (!MexicoDesc.isEmpty()) {
				writer.write("Descuentos-Mexico\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoDesc);
				writer.write("," + "," + "," + "," + "Total Descuentos"+"," + "," + "," + "," + "'"+ Double.toString(totalMexicoDescValCurSum)
				+ "," + "'"+ Double.toString(totalMexicoDescCerSum) + "," + "'"+ Double.toString(totalMexicoDescNomValSum));
				writer.write("\n");
				// Factoring Mexico
			}
			if (!MexicoFac.isEmpty()) {
				writer.write("Factoring-Mexico\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoFac);
				writer.write("," + "," + "," + "," + "Total Factoring"+"," + "," + "," + "," + "'"+ Double.toString(totalMexicoFacValCurSum)
				+ "," + "'"+ Double.toString(totalMexicoFacCerSum) + "," +"'"+ Double.toString(totalMexicoFacNomValSum));
				writer.write("\n");
				// Tarjetas Mexico
			}
			if (!MexicoTar.isEmpty()) {
				writer.write("Tarjeta de Credito-Mexico\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoTar);
				writer.write("," + "," + "," + "," + "Tarjeta de Credito"+"," + "," + "," + "," + "'"+ Double.toString(totalMexicoTarValCurSum)
				+ "," + "'"+ Double.toString(totalMexicoTarCerSum) + "," + "'"+ Double.toString(totalMexicoTarNomValSum));
				writer.write("\n");
				// Lineas Comprometidas Mexico
			}
			if (!MexicoLinCom.isEmpty()) {
				writer.write("Lineas Comprometidas-Mexico\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoLinCom);
				writer.write("," + "," + "," + "," + "Lineas Comprometidas"+"," + "," + "," + "," +"'"+ Double.toString(totalMexicoLinComValCurSum)
				+ "," + "'"+ Double.toString(totalMexicoLinComCerSum) + "," + "'"+ Double.toString(totalMexicoLinComNomValSum));
				writer.write("\n");
				// Garantias Mexico
			}
			if (!MexicoGaran.isEmpty()) {
				writer.write("Garantias-Mexico\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoGaran);
				writer.write("," + "," + "," + "," + "Total Garantias" +"," + "," + "," + "," + "'"+ Double.toString(totalMexicoGaranValCurSum)
				+ "," + "'"+ Double.toString(totalMexicoGaranCerSum) + "," + "'"+ Double.toString(totalMexicoGaranNomValSum));
				writer.write("\n");
				// Aval Mexico
			}
			if (!MexicoAval.isEmpty()) {
				writer.write("Aval-Mexico\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoAval);
				writer.write("," + "," + "," + "," + "Total Aval" +"," + "," + "," + "," + "'"+ Double.toString(totalMexicoAvalValCurSum)
				+ "," + "'"+ Double.toString(totalMexicoAvalCerSum) + "," + "'"+ Double.toString(totalMexicoAvalNomValSum));
				writer.write("\n");
			}
			if (!MexicoDer.isEmpty()) {
				writer.write("Derivados-Mexico\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoDer);
				writer.write("," + "," + "," + "," + "Total Derivados"+"," + "," + "," + "," + "'"+ Double.toString(totalMexicoDerValCurSum)
				+ "," + "'"+ Double.toString(totalMexicoDerCerSum) + "," + "'"+ Double.toString(totalMexicoDerNomValSum));
				writer.write("\n");

				
			}
			
			writer.write("TOTAL Mexico"+"," + "," + "," + "," + "Total general"+"," + "," + "," + "," +"'"+ Double.toString(totalMexicoTotValCurSum)
			+ "," +"'"+ Double.toString(totalMexicoTotCerSum) + "," +"'"+ Double.toString(totalMexicoTotNomValSum));
			writer.write("\n");
			
			// Bonos de Spain
			if (!SpainBonos.isEmpty()) {
				writer.write("Bonos-Spain\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaBonosSpain);
				writer.write("," + "," + "," + "," + "Total Bonos"+"," + "," + "," + "," + "'"+ Double.toString(totalSpainBonosNomValCur)
						+ "," + "'"+ Double.toString(totalSpainBonosCer) + "," + "'"+ Double.toString(totalSpainBonosNomVal));
				writer.write("\n");
				// Credito Documentariado Spain
			}
			if (!SpainCredDocu.isEmpty()) {
				writer.write("Creditos Documentariado-Spain\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainCredDoc);
				writer.write("," + "," + "," + "," + " Total Creditos Documentariado"+"," + "," + "," + "," + "'"+ Double.toString(totalSpainCredDocuNomValCurSum)
				+ "," + "'"+ Double.toString(totalSpainCredDocuCerSum) + "," + "'"+ Double.toString(totalSpainCredDocuNomValSum));
				writer.write("\n");
				// Exportacion/Importacion Spain
			}
			if (!SpainExportImport.isEmpty()) {
				writer.write("Financiamiento IMP/EXP-Spain\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainExportImport);
				writer.write("," + "," + "," + "," + "Total Financiamiento IMP/EXP"+"," + "," + "," + "," + "'"+ Double.toString(totalSpainExportImportNomValCurSum)
				+ "," + "'"+ Double.toString(totalSpainExportImportCerSum) + "," + "'"+ Double.toString(totalSpainExportImportNomValSum));
				writer.write("\n");
				// Comex/Forfaiting Spain
			}
			if (!SpainComFor.isEmpty()) {
				writer.write("Financiamiento Comex-Spain\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainComFor);
				writer.write("," + "," + "," + "," + "Total Financiamiento Comex"+"," + "," + "," + "," + "'"+ Double.toString(totalSpainComForNomValCurSum)
				+ "," + "'"+ Double.toString(totalSpainComForCerSum) + "," + "'"+ Double.toString(totalSpainComForNomValSum));
				writer.write("\n");
				// Sindicado Spain
			}
			if (!SpainSindicado.isEmpty()) {
				writer.write("Creditos Sindicados-Spain\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainSindicado);
				writer.write("," + "," + "," + "," + "Total Creditos Sindicados"+"," + "," + "," + "," + "'"+ Double.toString(totalSpainSindicadoNomValCurSum)
				+ "," + "'"+ Double.toString(totalSpainSindicadoCerSum) + "," + "'"+ Double.toString(totalSpainSindicadoNomValSum));
				writer.write("\n");
				// Confirming Spain
			}
			if (!SpainConfir.isEmpty()) {
				writer.write("Confirming-Spain\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainConfir);
				writer.write("," + "," + "," + "," + "Total Confirming"+"," + "," + "," + "," + "'"+ Double.toString(totalSpainConfirNomValCurSum)
				+ "," + "'"+ Double.toString(totalSpainConfirCerSum) + "," + "'"+ Double.toString(totalSpainConfirNomValSum));
				writer.write("\n");
				// Descuentos
			}
			if (!SpainDesc.isEmpty()) {
				writer.write("Descuentos-Spain\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainDesc);
				writer.write("," + "," + "," + "," + "Toatal Descueltos"+"," + "," + "," + "," + "'"+ Double.toString(totalSpainDescNomValCurSum)
				+ "," + "'"+ Double.toString(totalSpainDescCerSum) + "," + "'"+ Double.toString(totalSpainDescNomValSum));
				writer.write("\n");
				// Factoring
			}
			if (!SpainFac.isEmpty()) {
				writer.write("Factoring-Spain\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainFac);
				writer.write("," + "," + "," + "," + "Total Factoring"+"," + "," + "," + "," + "'"+ Double.toString(totalSpainFacNomValCurSum)
				+ "," + "'"+ Double.toString(totalSpainFacCerSum) + "," + "'"+ Double.toString(totalSpainFacNomValSum));
				writer.write("\n");
				// Tarjetas
			}
			if (!SpainTar.isEmpty()) {
				writer.write("Tarjeta de Credito-Spain\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainTar);
				writer.write("," + "," + "," + "," +"Total Tarjeta de Credito"+ ","  + "," + "," + "," + "'"+ Double.toString(totalSpainTarNomValCurSum)
				+ "," + "'"+ Double.toString(totalSpainTarCerSum) + "," + "'"+ Double.toString(totalSpainTarNomValSum));
				writer.write("\n");
				// Lineas Comprometidas
			}
			if (!SpainLinCom.isEmpty()) {
				writer.write("Lineas Comprometidas-Spain\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainLinCom);
				writer.write("," + "," + "," + "," + "Total Lineas Comprometidas"+ ","  + "," + "," + "," + "'"+ Double.toString(totalSpainLinComNomValCurSum)
				+ "," + "'"+ Double.toString(totalSpainLinComCerSum) + "," + "'"+ Double.toString(totalSpainLinComNomValSum));
				writer.write("\n");
			}
			if (!SpainGaran.isEmpty()) {
				writer.write("Garantias-Spain\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainGaran);
				writer.write("," + "," + "," + "," + "Total Garantias"+ ","  + "," + "," + "," + "'"+ Double.toString(totalSpainGaranNomValCurSum)
				+ "," + "'"+ Double.toString(totalSpainGaranCerSum) + "," + "'"+ Double.toString(totalSpainGaranNomValSum));
				writer.write("\n");
			}
			if (!SpainAval.isEmpty()) {
				writer.write("Aval-Spain\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainAval);
				writer.write("," + "," + "," + "," + "Total Aval" + "," + "," + "," + "," + "'"+ Double.toString(totalSpainAvalNomValCurSum)
				+ "," + "'"+ Double.toString(totalSpainAvalCerSum) + "," + "'"+ Double.toString(totalSpainAvalNomValSum));
				writer.write("\n");
			}
			if (!SpainDer.isEmpty()) {
				writer.write("Derivados-Spain\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainDer);
				writer.write("," + "," + "," + "," + "Total Derivados" + "," + "," + "," + "," + "'"+ Double.toString(totalSpainDerNomValCurSum)
				+ "," + "'"+ Double.toString(totalSpainDerCerSum) + "," + "'"+ Double.toString(totalSpainDerNomValSum));
				writer.write("\n");
			}
			
			writer.write("TOTAL SPAIN"+"," + "," + "," + "," + "Total general"+"," + "," + "," + "," +"'"+ Double.toString(totalSpainTotNomValCurSum)
			+ "," +"'"+ Double.toString(totalSpainTotCerSum) + "," +"'"+ Double.toString(totalSpainTotNomValSum));
			writer.write("\n");

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