package conexion;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
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
	public static final DecimalFormat DFORMATO = new DecimalFormat("####.########");

	
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

	public String getConsultaMexico(String grupo, String nombreInterfaz, String fechaConsumo) throws Exception {

		Statement sta = con.createStatement();

		double sumatoriaNomValCur;
		double sumatoriaCer;
		double sumatoriaNomVal;
		
		 

		// bonos Mexico
		List<String> MexicoBonos = new ArrayList<String>();
		// bonos sumatoria Mexico
		List<Double> MexicoBonosNomValCurSum = new ArrayList<Double>();
		List<Double> MexicoBonosCerSum = new ArrayList<Double>();
		List<Double> MexicoBonosNomValSum = new ArrayList<Double>();

		// creditos documentariado Mexico
		List<String> MexicoCredDocu = new ArrayList<String>();
		// creditos documentariado sumatoria Mexico
		List<Double> MexicoCredDocuNomValCurSum = new ArrayList<Double>();
		List<Double> MexicoCredDocuCerSum = new ArrayList<Double>();
		List<Double> MexicoCredDocuNomValSum = new ArrayList<Double>();

		// Exportacion/Importacion Mexico
		List<String> MexicoExportImport = new ArrayList<String>();
		// Exportacion/Importacion sumatoria Mexico
		List<Double> MexicoExportImportNomValCurSum = new ArrayList<Double>();
		List<Double> MexicoExportImportCerSum = new ArrayList<Double>();
		List<Double> MexicoExportImportNomValSum = new ArrayList<Double>();

		// Comex/Forfaiting Mexico
		List<String> MexicoComFor = new ArrayList<String>();
		// Comex/Forfaiting sumatoria Mexico
		List<Double> MexicoComForNomValCurSum = new ArrayList<Double>();
		List<Double> MexicoComForCerSum = new ArrayList<Double>();
		List<Double> MexicoComForNomValSum = new ArrayList<Double>();

		// Sindicado Mexico
		List<String> MexicoSindicado = new ArrayList<String>();
		// Sindicado sumatoria Mexico
		List<Double> MexicoSindicadoNomValCurSum = new ArrayList<Double>();
		List<Double> MexicoSindicadoCerSum = new ArrayList<Double>();
		List<Double> MexicoSindicadoNomValSum = new ArrayList<Double>();

		// Confirming Mexico
		List<String> MexicoConfir = new ArrayList<String>();
		// Confirming sumatoria Mexico
		List<Double> MexicoConfirNomValCurSum = new ArrayList<Double>();
		List<Double> MexicoConfirCerSum = new ArrayList<Double>();
		List<Double> MexicoConfirNomValSum = new ArrayList<Double>();

		// Descuentos Mexico
		List<String> MexicoDesc = new ArrayList<String>();
		// Descuentos sumatoria Mexico
		List<Double> MexicoDescValCurSum = new ArrayList<Double>();
		List<Double> MexicoDescCerSum = new ArrayList<Double>();
		List<Double> MexicoDescNomValSum = new ArrayList<Double>();

		// Factoring Mexico
		List<String> MexicoFac = new ArrayList<String>();
		// Factoring sumatoria Mexico
		List<Double> MexicoFacValCurSum = new ArrayList<Double>();
		List<Double> MexicoFacCerSum = new ArrayList<Double>();
		List<Double> MexicoFacNomValSum = new ArrayList<Double>();

		// Tarjetas
		List<String> MexicoTar = new ArrayList<String>();
		// Tarjetas sumatoria Mexico
		List<Double> MexicoTarValCurSum = new ArrayList<Double>();
		List<Double> MexicoTarCerSum = new ArrayList<Double>();
		List<Double> MexicoTarNomValSum = new ArrayList<Double>();

		// Lineas Comprometidas Mexico
		List<String> MexicoLinCom = new ArrayList<String>();
		// Lineas Comprometidas sumatoria Mexico
		List<Double> MexicoLinComValCurSum = new ArrayList<Double>();
		List<Double> MexicoLinComCerSum = new ArrayList<Double>();
		List<Double> MexicoLinComNomValSum = new ArrayList<Double>();

		// Garantias Mexico
		List<String> MexicoGaran = new ArrayList<String>();
		// Garantias sumatoria Mexico
		List<Double> MexicoGaranValCurSum = new ArrayList<Double>();
		List<Double> MexicoGaranCerSum = new ArrayList<Double>();
		List<Double> MexicoGaranNomValSum = new ArrayList<Double>();

		// Avales Mexico
		List<String> MexicoAval = new ArrayList<String>();
		// Avales sumatoria Mexico
		List<Double> MexicoAvalValCurSum = new ArrayList<Double>();
		List<Double> MexicoAvalCerSum = new ArrayList<Double>();
		List<Double> MexicoAvalNomValSum = new ArrayList<Double>();

		// Derivados Mexico
		List<String> MexicoDer = new ArrayList<String>();
		// Derivados sumatoria Mexico
		List<Double> MexicoDerValCurSum = new ArrayList<Double>();
		List<Double> MexicoDerCerSum = new ArrayList<Double>();
		List<Double> MexicoDerNomValSum = new ArrayList<Double>();

		// Total general Mexico
		List<Double> MexicoTotValCurSum = new ArrayList<Double>();
		List<Double> MexicoTotCerSum = new ArrayList<Double>();
		List<Double> MexicoTotNomValSum = new ArrayList<Double>();
		
		List<String> info =  new ArrayList<String>();

		List<String> encabezado = new ArrayList<>();
		
		encabezado.add("CPTYPARENT");
		encabezado.add("CPTYPARENTRATING");
		encabezado.add("CPTYPARENTNAME");
		encabezado.add("DEALSTAMP");
		encabezado.add("INSTRUMENTNAME");
		encabezado.add("VALUEDATE");
		encabezado.add("MATURITYDATE");
		encabezado.add("CURRENCY");
		encabezado.add("NOMINALVALUECUR");
		encabezado.add("CER");
		encabezado.add("NOMINALVALUE");
		encabezado.add("ONEOFF");
		encabezado.add("CPTYNAME");
		encabezado.add("FOLDERCOUNTRYNAME");
		encabezado.add("CPTYCOUNTRY");
		encabezado.add("CPTYPARENTCOUNTRY");
		encabezado.add("FOLDERCOUNTRY");
		encabezado.add("\n");
		
		
		String systCode = "SELECT cptyparent,cptyparentrating,cptyparentname,dealstamp,instrumentname,TO_CHAR(TO_DATE(valuedate,  'YYYY-MM-DD'), 'DD/MON/YYYY'),TO_CHAR(TO_DATE(maturitydate,  'YYYY-MM-DD'), 'DD/MON/YYYY'),currency,DECODE(nominalvaluecur,null, '0.0',nominalvaluecur) AS nominalvaluecur,DECODE(CER,null, '0.0',CER) AS CER,DECODE(nominalvalue,null, '0.0',nominalvalue) AS nominalvalue,oneoff,cptyname,foldercountryname,cptycountry,cptyparentcountry,foldercountry from PGT_MEX.T_PGT_MEX_CONSUMOSC_V WHERE LastParentF ='"
				+ grupo + "' and FECHACARGA='" + fechaConsumo
				+ "' AND foldercountryname='Mexico' UNION ALL SELECT cptyparent,cptyparentrating,cptyparentname,dealstamp,instrumentname,TO_CHAR(TO_DATE(valuedate,  'YYYY-MM-DD'), 'DD/MON/YYYY'),TO_CHAR(TO_DATE(maturitydate,  'YYYY-MM-DD'), 'DD/MON/YYYY'),currency,DECODE(nominalvaluecur,null, '0.0',nominalvaluecur) AS nominalvaluecur,DECODE(CER,null, '0.0',CER) AS CER,DECODE(nominalvalue,null, '0.0',nominalvalue) AS nominalvalue,oneoff,cptyname,foldercountryname,cptycountry,cptyparentcountry,foldercountry from PGT_MEX.T_PGT_MEX_CONSUMOSC_D WHERE LastParentF ='"
				+ grupo + "' and FECHACARGA='" + fechaConsumo
				+ "' AND foldercountryname='Mexico' ORDER BY foldercountry,instrumentname ";
		ResultSet rs = sta.executeQuery(systCode);

		if (rs.equals(null) || rs.next() == false) {
			systCode = "No existen registros para este grupo en la interfaz CONSULTA";
		} else {
			String directoryName = System.getProperty("user.dir");

			FileWriter writer = new FileWriter(nombreInterfaz, true);
			do {
				// cadena
				systCode = rs.getString(1) + "," + rs.getString(2) + "," + "\"" + rs.getString(3) + "\"" + ","
						+ rs.getString(4) + "," + rs.getString(5) + "," + rs.getString(6) + "," + rs.getString(7) + ","
						+ rs.getString(8) + "," + rs.getString(9) + "," + rs.getString(10) + "," + rs.getString(11)
						+ "," + rs.getString(12) + "," + "\"" + rs.getString(13) + "\"" + "," + rs.getString(14) + ","
						+ "\"" + rs.getString(15) + "\"" + "," + rs.getString(16) + "," + rs.getString(17) + "\n";

				// cadena obtener la sumatoria
				sumatoriaNomValCur = rs.getDouble(9);
				sumatoriaCer = rs.getDouble(10);
				sumatoriaNomVal = rs.getDouble(11);

				if (rs.getString(5).contains("BOND")) {
					MexicoBonos.add(systCode);
					info = this.getContraparte( grupo,  fechaConsumo,  rs.getString(4), rs.getString(14),rs.getString(6),rs.getString(7));
					MexicoBonos.addAll(info);
					MexicoBonosNomValCurSum.add(Double.valueOf(sumatoriaNomValCur));
					MexicoBonosCerSum.add(Double.valueOf(sumatoriaCer));
					MexicoBonosNomValSum.add(Double.valueOf(sumatoriaNomVal));
					MexicoTotValCurSum.add(sumatoriaNomValCur);
					MexicoTotCerSum.add(sumatoriaCer);
					MexicoTotNomValSum.add(sumatoriaNomVal);

				} else if (rs.getString(5).contains(" CREDITO DOCUMENTARIO")) {
					MexicoCredDocu.add(systCode);
					info = this.getContraparte( grupo,  fechaConsumo,  rs.getString(4), rs.getString(14),rs.getString(6),rs.getString(7));
					MexicoCredDocu.addAll(info);
					MexicoCredDocuNomValCurSum.add(sumatoriaNomValCur);
					MexicoCredDocuCerSum.add(sumatoriaCer);
					MexicoCredDocuNomValSum.add(sumatoriaNomVal);
					MexicoTotValCurSum.add(sumatoriaNomValCur);
					MexicoTotCerSum.add(sumatoriaCer);
					MexicoTotNomValSum.add(sumatoriaNomVal);
				} else if (rs.getString(5).contains("EXPORTACION") || rs.getString(5).contains("IMPORTACION")) {
					MexicoExportImport.add(systCode);
					info = this.getContraparte( grupo,  fechaConsumo,  rs.getString(4), rs.getString(14),rs.getString(6),rs.getString(7));
					MexicoExportImport.addAll(info);
					MexicoExportImportNomValCurSum.add(sumatoriaNomValCur);
					MexicoExportImportCerSum.add(sumatoriaCer);
					MexicoExportImportNomValSum.add(sumatoriaNomVal);
					MexicoTotValCurSum.add(sumatoriaNomValCur);
					MexicoTotCerSum.add(sumatoriaCer);
					MexicoTotNomValSum.add(sumatoriaNomVal);
				} else if (rs.getString(5).contains("COMEX") || rs.getString(5).contains("FORFAITING")) {
					MexicoComFor.add(systCode);
					info = this.getContraparte( grupo,  fechaConsumo,  rs.getString(4), rs.getString(14),rs.getString(6),rs.getString(7));
					MexicoComFor.addAll(info);
					MexicoComForNomValCurSum.add(sumatoriaNomValCur);
					MexicoComForCerSum.add(sumatoriaCer);
					MexicoComForNomValSum.add(sumatoriaNomVal);
					MexicoTotValCurSum.add(sumatoriaNomValCur);
					MexicoTotCerSum.add(sumatoriaCer);
					MexicoTotNomValSum.add(sumatoriaNomVal);
				} else if (rs.getString(5).contains("SINDICADO")) {
					MexicoSindicado.add(systCode);
					info = this.getContraparte( grupo,  fechaConsumo,  rs.getString(4), rs.getString(14),rs.getString(6),rs.getString(7));
					MexicoSindicado.addAll(info);
					MexicoSindicadoNomValCurSum.add(sumatoriaNomValCur);
					MexicoSindicadoCerSum.add(sumatoriaCer);
					MexicoSindicadoNomValSum.add(sumatoriaNomVal);
					MexicoTotValCurSum.add(sumatoriaNomValCur);
					MexicoTotCerSum.add(sumatoriaCer);
					MexicoTotNomValSum.add(sumatoriaNomVal);
				} else if (rs.getString(5).contains("CONFIRMING")) {
					MexicoConfir.add(systCode);
					info = this.getContraparte( grupo,  fechaConsumo,  rs.getString(4), rs.getString(14),rs.getString(6),rs.getString(7));
					MexicoConfir.addAll(info);
					MexicoConfirNomValCurSum.add(sumatoriaNomValCur);
					MexicoConfirCerSum.add(sumatoriaCer);
					MexicoConfirNomValSum.add(sumatoriaNomVal);
					MexicoTotValCurSum.add(sumatoriaNomValCur);
					MexicoTotCerSum.add(sumatoriaCer);
					MexicoTotNomValSum.add(sumatoriaNomVal);

				} else if (rs.getString(5).contains("DESCUENTOS")) {
					MexicoDesc.add(systCode);
					info = this.getContraparte( grupo,  fechaConsumo,  rs.getString(4), rs.getString(14),rs.getString(6),rs.getString(7));
					MexicoDesc.addAll(info);
					MexicoDescValCurSum.add(sumatoriaNomValCur);
					MexicoDescCerSum.add(sumatoriaCer);
					MexicoDescNomValSum.add(sumatoriaNomVal);
					MexicoTotValCurSum.add(sumatoriaNomValCur);
					MexicoTotCerSum.add(sumatoriaCer);
					MexicoTotNomValSum.add(sumatoriaNomVal);
				} else if (rs.getString(5).contains("FACTORING")) {
					MexicoFac.add(systCode);
					info = this.getContraparte( grupo,  fechaConsumo,  rs.getString(4), rs.getString(14),rs.getString(6),rs.getString(7));
					MexicoFac.addAll(info);
					MexicoFacValCurSum.add(sumatoriaNomValCur);
					MexicoFacCerSum.add(sumatoriaCer);
					MexicoFacNomValSum.add(sumatoriaNomVal);
					MexicoTotValCurSum.add(sumatoriaNomValCur);
					MexicoTotCerSum.add(sumatoriaCer);
					MexicoTotNomValSum.add(sumatoriaNomVal);

				} else if (rs.getString(5).contains("TARJETAS")) {
					MexicoTar.add(systCode);
					info = this.getContraparte( grupo,  fechaConsumo,  rs.getString(4), rs.getString(14),rs.getString(6),rs.getString(7));
					MexicoTar.addAll(info);
					MexicoTarValCurSum.add(sumatoriaNomValCur);
					MexicoTarCerSum.add(sumatoriaCer);
					MexicoTarNomValSum.add(sumatoriaNomVal);
					MexicoTotValCurSum.add(sumatoriaNomValCur);
					MexicoTotCerSum.add(sumatoriaCer);
					MexicoTotNomValSum.add(sumatoriaNomVal);
				} else if (rs.getString(5).contains("LINEA MULTIDEAL RESTO")
						|| rs.getString(5).contains("CREDITOS - COMPROMETIDO")
						|| rs.getString(5).contains("CREDITO BACKUP") || rs.getString(5).contains("CREDITO OTROS")) {
					MexicoLinCom.add(systCode);
					info = this.getContraparte( grupo,  fechaConsumo,  rs.getString(4), rs.getString(14),rs.getString(6),rs.getString(7));
					MexicoLinCom.addAll(info);
					MexicoLinComValCurSum.add(sumatoriaNomValCur);
					MexicoLinComCerSum.add(sumatoriaCer);
					MexicoLinComNomValSum.add(sumatoriaNomVal);
					MexicoTotValCurSum.add(sumatoriaNomValCur);
					MexicoTotCerSum.add(sumatoriaCer);
					MexicoTotNomValSum.add(sumatoriaNomVal);
				} else if (rs.getString(5).contains("GARANTIA ACCIONES") || rs.getString(5).contains("GARANTIA AVAL")
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
					info = this.getContraparte( grupo,  fechaConsumo,  rs.getString(4), rs.getString(14),rs.getString(6),rs.getString(7));
					MexicoAval.addAll(info);
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
					info = this.getContraparte( grupo,  fechaConsumo,  rs.getString(4), rs.getString(14),rs.getString(6),rs.getString(7));
					MexicoDer.addAll(info);
					MexicoDerValCurSum.add(sumatoriaNomValCur);
					MexicoDerCerSum.add(sumatoriaCer);
					MexicoDerNomValSum.add(sumatoriaNomVal);
					MexicoTotValCurSum.add(sumatoriaNomValCur);
					MexicoTotCerSum.add(sumatoriaCer);
					MexicoTotNomValSum.add(sumatoriaNomVal);
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
			double  totalMexicoBonosNomVal = MexicoBonosNomValSum.stream().mapToDouble(Double::doubleValue).sum();

			
			// Creditos Documentariados
			String CadenaMexicoCredDoc = MexicoCredDocu.stream().collect(Collectors.joining(""));
			double totalMexicoCredDocuNomValCurSum = MexicoCredDocuNomValCurSum.stream()
					.mapToDouble(Double::doubleValue).sum();
			double totalMexicoCredDocuCerSum = MexicoCredDocuCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoCredDocuNomValSum = MexicoCredDocuNomValSum.stream().mapToDouble(Double::doubleValue)
					.sum();

			// Exportaciones / importacions
			String CadenaMexicoExportImport = MexicoExportImport.stream().collect(Collectors.joining(""));
			double totalMexicoExportImportNomValCurSum = MexicoExportImportNomValCurSum.stream()
					.mapToDouble(Double::doubleValue).sum();
			double totalMexicoExportImportCerSum = MexicoExportImportCerSum.stream().mapToDouble(Double::doubleValue)
					.sum();
			double totalMexicoExportImportNomValSum = MexicoExportImportNomValSum.stream()
					.mapToDouble(Double::doubleValue).sum();

			// Comex/Forfaiting
			String CadenaMexicoComFor = MexicoComFor.stream().collect(Collectors.joining(""));
			double totalMexicoComForNomValCurSum = MexicoComForNomValCurSum.stream().mapToDouble(Double::doubleValue)
					.sum();
			double totalMexicoComForCerSum = MexicoComForCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoComForNomValSum = MexicoComForNomValSum.stream().mapToDouble(Double::doubleValue).sum();

			// Sindicado
			String CadenaMexicoSindicado = MexicoSindicado.stream().collect(Collectors.joining(""));
			double totalMexicoSindicadoNomValCurSum = MexicoSindicadoNomValCurSum.stream()
					.mapToDouble(Double::doubleValue).sum();
			double totalMexicoSindicadoCerSum = MexicoSindicadoCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoSindicadoNomValSum = MexicoSindicadoNomValSum.stream().mapToDouble(Double::doubleValue)
					.sum();

			// Confirming
			String CadenaMexicoConfir = MexicoConfir.stream().collect(Collectors.joining(""));
			double totalMexicoConfirNomValCurSum = MexicoConfirNomValCurSum.stream().mapToDouble(Double::doubleValue)
					.sum();
			double totalMexicoConfirCerSum = MexicoConfirCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoConfirNomValSum = MexicoConfirNomValSum.stream().mapToDouble(Double::doubleValue).sum();

			// Descuentos
			String CadenaMexicoDesc = MexicoDesc.stream().collect(Collectors.joining(""));
			double totalMexicoDescValCurSum = MexicoDescValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoDescCerSum = MexicoDescCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoDescNomValSum = MexicoDescNomValSum.stream().mapToDouble(Double::doubleValue).sum();

			// Descuentos
			String CadenaMexicoFac = MexicoFac.stream().collect(Collectors.joining(""));
			double totalMexicoFacValCurSum = MexicoFacValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoFacCerSum = MexicoFacCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoFacNomValSum = MexicoFacNomValSum.stream().mapToDouble(Double::doubleValue).sum();

			// Tarjetas
			String CadenaMexicoTar = MexicoTar.stream().collect(Collectors.joining(""));
			double totalMexicoTarValCurSum = MexicoTarValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoTarCerSum = MexicoTarCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoTarNomValSum = MexicoTarNomValSum.stream().mapToDouble(Double::doubleValue).sum();

			// Lineas Comprometidas
			String CadenaMexicoLinCom = MexicoLinCom.stream().collect(Collectors.joining(""));
			double totalMexicoLinComValCurSum = MexicoLinComValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoLinComCerSum = MexicoLinComCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoLinComNomValSum = MexicoLinComNomValSum.stream().mapToDouble(Double::doubleValue).sum();

			// Garantias
			String CadenaMexicoGaran = MexicoGaran.stream().collect(Collectors.joining(""));
			double totalMexicoGaranValCurSum = MexicoGaranValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoGaranCerSum = MexicoGaranCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoGaranNomValSum = MexicoGaranNomValSum.stream().mapToDouble(Double::doubleValue).sum();

			// Aval
			String CadenaMexicoAval = MexicoAval.stream().collect(Collectors.joining(""));
			double totalMexicoAvalValCurSum = MexicoAvalValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoAvalCerSum = MexicoAvalCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoAvalNomValSum = MexicoAvalNomValSum.stream().mapToDouble(Double::doubleValue).sum();

			// Derivados
			String CadenaMexicoDer = MexicoDer.stream().collect(Collectors.joining(""));
			double totalMexicoDerValCurSum = MexicoDerValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoDerCerSum = MexicoDerCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoDerNomValSum = MexicoDerNomValSum.stream().mapToDouble(Double::doubleValue).sum();

			// totales
			double totalMexicoTotValCurSum = MexicoTotValCurSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoTotCerSum = MexicoTotCerSum.stream().mapToDouble(Double::doubleValue).sum();
			double totalMexicoTotNomValSum = MexicoTotNomValSum.stream().mapToDouble(Double::doubleValue).sum();

			// Bonos Mexico
			if (!MexicoBonos.isEmpty()) {
				writer.write("Bonos-Mexico\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaBonosMex);
				writer.write("," + "," + "," + "," + "Total Bonos" + "," + "," + "," + ","
						+ DFORMATO.format(totalMexicoBonosNomValCur).toString() + "," +DFORMATO.format(totalMexicoBonosCer).toString() + ","
						+ DFORMATO.format(totalMexicoBonosNomVal).toString());
				writer.write("\n");

				// Credito Documentariado Mexico
			}
			if (!MexicoCredDocu.isEmpty()) {
				writer.write("Creditos Documentariado-Mexico\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoCredDoc);
				writer.write("," + "," + "," + "," + "Total Creditos Documentariado" + "," + "," + "," + ","
						+ DFORMATO.format(totalMexicoCredDocuNomValCurSum).toString() + ","
						+ DFORMATO.format(totalMexicoCredDocuCerSum).toString() + ","
						+ DFORMATO.format(totalMexicoCredDocuNomValSum).toString());
				writer.write("\n");
				// Exportacion/Importacion Mexio
			}
			if (!MexicoExportImport.isEmpty()) {
				writer.write("Financiamiento IMP/EXP-Mexico\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoExportImport);
				writer.write("," + "," + "," + "," + "Total Financiamiento IMP/EXP" + "," + "," + "," + ","
						+ DFORMATO.format(totalMexicoExportImportNomValCurSum).toString() + ","
						+DFORMATO.format(totalMexicoExportImportCerSum).toString() + ","
						+ DFORMATO.format(totalMexicoExportImportNomValSum).toString());
				writer.write("\n");
				// Comex/forfaiting Mexico
			}
			if (!MexicoComFor.isEmpty()) {
				writer.write("Financiamiento Comex-Mexico\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoComFor);
				writer.write("," + "," + "," + "," + "Total Financiamiento Comex" + "," + "," + "," + ","
						+ DFORMATO.format(totalMexicoComForNomValCurSum).toString() + ","
						+ DFORMATO.format(totalMexicoComForCerSum).toString() + "," + DFORMATO.format(totalMexicoComForNomValSum).toString());
				writer.write("\n");

				// Sindicado Mexico
			}
			if (!MexicoSindicado.isEmpty()) {
				writer.write("Creditos Sindicados-Mexico\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoSindicado);
				writer.write("," + "," + "," + "," + "Total Creditos Sindicados" + "," + "," + "," + ","
						+ DFORMATO.format(totalMexicoSindicadoNomValCurSum).toString() + ","
						+ DFORMATO.format(totalMexicoSindicadoCerSum).toString() + ","
						+ DFORMATO.format(totalMexicoSindicadoNomValSum).toString());
				writer.write("\n");
				// Confirming Mexico
			}
			if (!MexicoConfir.isEmpty()) {
				writer.write("Confirming-Mexico\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoConfir);
				writer.write("," + "," + "," + "," + "Total Confirming" + "," + "," + "," + ","
						+ DFORMATO.format(totalMexicoConfirNomValCurSum).toString() + ","
						+ DFORMATO.format(totalMexicoConfirCerSum).toString() + "," + DFORMATO.format(totalMexicoConfirNomValSum).toString());

				writer.write("\n");
				// Descuentos Mexico
			}
			if (!MexicoDesc.isEmpty()) {
				writer.write("Descuentos-Mexico\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoDesc);
				writer.write("," + "," + "," + "," + "Total Descuentos" + "," + "," + "," + ","
						+ DFORMATO.format(totalMexicoDescValCurSum).toString() + "," + DFORMATO.format(totalMexicoDescCerSum).toString() + ","
						+ DFORMATO.format(totalMexicoDescNomValSum).toString());
				writer.write("\n");
				// Factoring Mexico
			}
			if (!MexicoFac.isEmpty()) {
				writer.write("Factoring-Mexico\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoFac);
				writer.write("," + "," + "," + "," + "Total Factoring" + "," + "," + "," + ","
						+ DFORMATO.format(totalMexicoFacValCurSum).toString() + "," + DFORMATO.format(totalMexicoFacCerSum).toString() + ","
						+ DFORMATO.format(totalMexicoFacNomValSum).toString());
				writer.write("\n");
				// Tarjetas Mexico
			}
			if (!MexicoTar.isEmpty()) {
				writer.write("Tarjeta de Credito-Mexico\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoTar);
				writer.write("," + "," + "," + "," + "Tarjeta de Credito" + "," + "," + "," + ","
						+ DFORMATO.format(totalMexicoTarValCurSum).toString() + "," + DFORMATO.format(totalMexicoTarCerSum).toString() + ","
						+ DFORMATO.format(totalMexicoTarNomValSum).toString());
				writer.write("\n");
				// Lineas Comprometidas Mexico
			}
			if (!MexicoLinCom.isEmpty()) {
				writer.write("Lineas Comprometidas-Mexico\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoLinCom);
				writer.write("," + "," + "," + "," + "Lineas Comprometidas" + "," + "," + "," + ","
						+ DFORMATO.format(totalMexicoLinComValCurSum).toString() + "," + DFORMATO.format(totalMexicoLinComCerSum).toString()
						+ "," + DFORMATO.format(totalMexicoLinComNomValSum).toString());
				writer.write("\n");
				// Garantias Mexico
			}
			if (!MexicoGaran.isEmpty()) {
				writer.write("Garantias-Mexico\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoGaran);
				writer.write("," + "," + "," + "," + "Total Garantias" + "," + "," + "," + ","
						+ DFORMATO.format(totalMexicoGaranValCurSum).toString() + "," + DFORMATO.format(totalMexicoGaranCerSum).toString()
						+ "," + DFORMATO.format(totalMexicoGaranNomValSum).toString());
				writer.write("\n");
				// Aval Mexico
			}
			if (!MexicoAval.isEmpty()) {
				writer.write("Aval-Mexico\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoAval);
				writer.write("," + "," + "," + "," + "Total Aval" + "," + "," + "," + ","
						+ DFORMATO.format(totalMexicoAvalValCurSum).toString() + "," + DFORMATO.format(totalMexicoAvalCerSum).toString() + ","
						+ DFORMATO.format(totalMexicoAvalNomValSum).toString());
				writer.write("\n");
			}
			if (!MexicoDer.isEmpty()) {
				writer.write("Derivados-Mexico\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaMexicoDer);
				writer.write("," + "," + "," + "," + "Total Derivados" + "," + "," + "," + ","
						+ DFORMATO.format(totalMexicoDerValCurSum).toString() + "," + DFORMATO.format(totalMexicoDerCerSum).toString() + ","
						+ DFORMATO.format(totalMexicoDerNomValSum).toString());
				writer.write("\n");

			}

			writer.write("TOTAL Mexico" + "," + "," + "," + "," + "Total general" + "," + "," + "," + ","
					+ DFORMATO.format(totalMexicoTotValCurSum).toString() + "," + DFORMATO.format(totalMexicoTotCerSum).toString() + ","
					+ DFORMATO.format(totalMexicoTotNomValSum).toString());
			writer.write("\n");

			writer.close();
		}

		return systCode;
	}

	public String getConsultaOtrosPaises(String grupo, String nombreInterfaz, String fechaConsumo) throws Exception {

		Statement sta = con.createStatement();

		double sumatoriaNomValCur;
		double sumatoriaCer;
		double sumatoriaNomVal;
		List<String> info =  new ArrayList<String>();
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

		List<String> encabezado = new ArrayList<>();
		encabezado.add("CPTYPARENT");
		encabezado.add("CPTYPARENTRATING");
		encabezado.add("CPTYPARENTNAME");
		encabezado.add("DEALSTAMP");
		encabezado.add("INSTRUMENTNAME");
		encabezado.add("VALUEDATE");
		encabezado.add("MATURITYDATE");
		encabezado.add("CURRENCY");
		encabezado.add("NOMINALVALUECUR");
		encabezado.add("CER");
		encabezado.add("NOMINALVALUE");
		encabezado.add("ONEOFF");
		encabezado.add("CPTYNAME");
		encabezado.add("FOLDERCOUNTRYNAME");
		encabezado.add("CPTYCOUNTRY");
		encabezado.add("CPTYPARENTCOUNTRY");
		encabezado.add("FOLDERCOUNTRY");
		encabezado.add("\n");

		String systCode = "SELECT cptyparent,cptyparentrating,cptyparentname,dealstamp,instrumentname,TO_CHAR(TO_DATE(valuedate,  'YYYY-MM-DD'), 'DD/MON/YYYY'),TO_CHAR(TO_DATE(maturitydate,  'YYYY-MM-DD'), 'DD/MON/YYYY'),currency,DECODE(nominalvaluecur,null, '0.0',nominalvaluecur) AS nominalvaluecur,DECODE(CER,null, '0.0',CER) AS CER,DECODE(nominalvalue,null, '0.0',nominalvalue) AS nominalvalue,oneoff,cptyname,foldercountryname,cptycountry,cptyparentcountry,foldercountry from PGT_MEX.T_PGT_MEX_CONSUMOSC_V WHERE LastParentF ='"
				+ grupo + "' and FECHACARGA='" + fechaConsumo
				+ "' AND foldercountryname<>'Mexico' UNION ALL SELECT cptyparent,cptyparentrating,cptyparentname,dealstamp,instrumentname,TO_CHAR(TO_DATE(valuedate,  'YYYY-MM-DD'), 'DD/MON/YYYY'),TO_CHAR(TO_DATE(maturitydate,  'YYYY-MM-DD'), 'DD/MON/YYYY'),currency,DECODE(nominalvaluecur,null, '0.0',nominalvaluecur) AS nominalvaluecur,DECODE(CER,null, '0.0',CER) AS CER,DECODE(nominalvalue,null, '0.0',nominalvalue) AS nominalvalue,oneoff,cptyname,foldercountryname,cptycountry,cptyparentcountry,foldercountry from PGT_MEX.T_PGT_MEX_CONSUMOSC_D WHERE LastParentF ='"
				+ grupo + "' and FECHACARGA='" + fechaConsumo
				+ "' AND foldercountryname<>'Mexico' ORDER BY foldercountry,instrumentname ";
		ResultSet rs = sta.executeQuery(systCode);

		if (rs.equals(null) || rs.next() == false) {
			systCode = "No existen registros para este grupo en la interfaz CONSULTA";
		} else {
			String directoryName = System.getProperty("user.dir");

			FileWriter writer = new FileWriter(nombreInterfaz, true);
			String pais = null;
			do {
				// cadena
				systCode = rs.getString(1) + "," + rs.getString(2) + "," + "\"" + rs.getString(3) + "\"" + ","
						+ rs.getString(4) + "," + rs.getString(5) + "," + rs.getString(6) + "," + rs.getString(7) + ","
						+ rs.getString(8) + "," + rs.getString(9) + "," + rs.getString(10) + "," + rs.getString(11)
						+ "," + rs.getString(12) + "," + "\"" + rs.getString(13) + "\"" + "," + rs.getString(14) + ","
						+ "\"" + rs.getString(15) + "\"" + "," + rs.getString(16) + "," + rs.getString(17) + "\n";

				// cadena obtener la sumatoria
				sumatoriaNomValCur = rs.getDouble(9);
				sumatoriaCer = rs.getDouble(10);
				sumatoriaNomVal = rs.getDouble(11);

				if (pais == null || pais.equals(rs.getNString(14))) {
					pais = rs.getNString(14);

					if (rs.getString(5).contains("BOND")) {
						SpainBonos.add(systCode);
						
						info = this.getContraparte( grupo,  fechaConsumo,  rs.getString(4), rs.getString(14),rs.getString(6),rs.getString(7));
						SpainBonos.addAll(info);
						
						
						SpainBonosNomValCurSum.add(Double.valueOf(sumatoriaNomValCur));
						SpainBonosCerSum.add(Double.valueOf(sumatoriaCer));
						SpainBonosNomValSum.add(Double.valueOf(sumatoriaNomVal));
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);

					} else if (rs.getString(5).contains(" CREDITO DOCUMENTARIO")) {
						SpainCredDocu.add(systCode);
						
						info = this.getContraparte( grupo,  fechaConsumo,  rs.getString(4), rs.getString(14),rs.getString(6),rs.getString(7));
						SpainCredDocu.addAll(info);
						
						
						SpainCredDocuNomValCurSum.add(sumatoriaNomValCur);
						SpainCredDocuCerSum.add(sumatoriaCer);
						SpainCredDocuNomValSum.add(sumatoriaNomVal);
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("EXPORTACION") || rs.getString(5).contains("IMPORTACION")) {
						SpainExportImport.add(systCode);
						
						info = this.getContraparte( grupo,  fechaConsumo,  rs.getString(4), rs.getString(14),rs.getString(6),rs.getString(7));
						SpainExportImport.addAll(info);
						
						
						
						SpainExportImportNomValCurSum.add(sumatoriaNomValCur);
						SpainExportImportCerSum.add(sumatoriaCer);
						SpainExportImportNomValSum.add(sumatoriaNomVal);
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("COMEX") || rs.getString(5).contains("FORFAITING")) {
						SpainComFor.add(systCode);
						
						info = this.getContraparte( grupo,  fechaConsumo,  rs.getString(4), rs.getString(14),rs.getString(6),rs.getString(7));
						SpainComFor.addAll(info);
						
						
						SpainComForNomValCurSum.add(sumatoriaNomValCur);
						SpainComForCerSum.add(sumatoriaCer);
						SpainComForNomValSum.add(sumatoriaNomVal);
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("SINDICADO")) {
						SpainSindicado.add(systCode);
						
						info = this.getContraparte( grupo,  fechaConsumo,  rs.getString(4), rs.getString(14),rs.getString(6),rs.getString(7));
						SpainSindicado.addAll(info);
						
						
						SpainSindicadoNomValCurSum.add(sumatoriaNomValCur);
						SpainSindicadoCerSum.add(sumatoriaCer);
						SpainSindicadoNomValSum.add(sumatoriaNomVal);
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);

					} else if (rs.getString(5).contains("CONFIRMING")) {
						SpainConfir.add(systCode);
						
						info = this.getContraparte( grupo,  fechaConsumo,  rs.getString(4), rs.getString(14),rs.getString(6),rs.getString(7));
						SpainConfir.addAll(info);
						
						
						SpainConfirNomValCurSum.add(sumatoriaNomValCur);
						SpainConfirCerSum.add(sumatoriaCer);
						SpainConfirNomValSum.add(sumatoriaNomVal);
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("DESCUENTOS")) {
						SpainDesc.add(systCode);
						
						info = this.getContraparte( grupo,  fechaConsumo,  rs.getString(4), rs.getString(14),rs.getString(6),rs.getString(7));
						SpainDesc.addAll(info);
						
						
						SpainDescNomValCurSum.add(sumatoriaNomValCur);
						SpainDescCerSum.add(sumatoriaCer);
						SpainDescNomValSum.add(sumatoriaNomVal);
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);
					} else if (rs.getString(5).contains("FACTORING")) {
						SpainFac.add(systCode);
						
						info = this.getContraparte( grupo,  fechaConsumo,  rs.getString(4), rs.getString(14),rs.getString(6),rs.getString(7));
						SpainFac.addAll(info);
						// validamos su contraparte
						// llamamos el metodo si tiene contraparte
						// regresa y se la a�adimos debajo del su
						// operacion return String
						// if() {

						// MexicoAval.add(contraparte );

						// }

						SpainFacNomValCurSum.add(sumatoriaNomValCur);
						SpainFacCerSum.add(sumatoriaCer);
						SpainFacNomValSum.add(sumatoriaNomVal);
						SpainTotNomValCurSum.add(sumatoriaNomValCur);
						SpainTotCerSum.add(sumatoriaCer);
						SpainTotNomValSum.add(sumatoriaNomVal);

					} else if (rs.getString(5).contains("TARJETAS")) {
						SpainTar.add(systCode);
						
						info = this.getContraparte( grupo,  fechaConsumo,  rs.getString(4), rs.getString(14),rs.getString(6),rs.getString(7));
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
						
						info = this.getContraparte( grupo,  fechaConsumo,  rs.getString(4), rs.getString(14),rs.getString(6),rs.getString(7));
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
						
						info = this.getContraparte( grupo,  fechaConsumo,  rs.getString(4), rs.getString(14),rs.getString(6),rs.getString(7));
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
						info = this.getContraparte( grupo,  fechaConsumo,  rs.getString(4), rs.getString(14),rs.getString(6),rs.getString(7));
						SpainDer.addAll(info);
						
						SpainDerNomValCurSum.add(sumatoriaNomValCur);
						SpainDerCerSum.add(sumatoriaCer);
						SpainDerNomValSum.add(sumatoriaNomVal);
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
							nombreInterfaz, pais);

					pais = rs.getNString(14);

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
					SpainTotNomValSum, nombreInterfaz, pais);

		}

		return systCode;
	}

	public List<String> getContraparte(String grupo, String fechaConsumo, String deal, String pais,String valuedate,String maturitydate) {
		strbSql = new StringBuilder();

		List<String> registrosInterfaz;
		registrosInterfaz = new ArrayList<String>();
		String systCode = "";
		strbSql.append(
				"SELECT cptyparent,cptyparentrating,cptyparentname,dealstamp,instrumentname,TO_CHAR(TO_DATE(valuedate,  'YYYY-MM-DD'), 'DD/MON/YYYY'),TO_CHAR(TO_DATE(maturitydate,  'YYYY-MM-DD'), 'DD/MON/YYYY'),currency,DECODE(nominalvaluecur,null, '0.0',nominalvaluecur) AS nominalvaluecur,DECODE(CER,null, '0.0',CER) AS CER,DECODE(nominalvalue,null, '0.0',nominalvalue) AS nominalvalue,oneoff,cptyname,foldercountryname,cptycountry,cptyparentcountry,foldercountry from PGT_MEX.T_PGT_MEX_CONSUMOSC_V WHERE LastParentF ='"+ grupo + "' and FECHACARGA='"+ fechaConsumo + "' AND foldercountryname='"+ pais + "' AND DEALSTAMP='"+ deal + "' AND valuedate=TO_CHAR(TO_DATE('"+ valuedate +"',  'DD/MON/YYYY'), 'YYYY-MM-DD')AND maturitydate=TO_CHAR(TO_DATE('"+ maturitydate +"',  'DD/MON/YYYY'), 'YYYY-MM-DD')AND INSTRUMENTNAME IN('GARANTIA ACCIONES COTIZADAS','GARANTIA AVAL FINANCIERO - NO USAR (3Q 2016)','GARANTIA DERECHOS DE COBRO','GARANTIA PERSONAL MANCOMUNADA','GARANTIA PERSONAL SOLIDARIA','GARANTIA PERSONAL SOLIDARIA FINAN','OTRAS GARANTIAS EN EFECTIVO','OTRAS GARANTIAS REALES NO LIQUIDAS','OTHER GUARANTY CASH') UNION ALL  SELECT cptyparent,cptyparentrating,cptyparentname,dealstamp,instrumentname,TO_CHAR(TO_DATE(valuedate,  'YYYY-MM-DD'), 'DD/MON/YYYY'),TO_CHAR(TO_DATE(maturitydate,  'YYYY-MM-DD'), 'DD/MON/YYYY'),currency,DECODE(nominalvaluecur,null, '0.0',nominalvaluecur) AS nominalvaluecur,DECODE(CER,null, '0.0',CER) AS CER,DECODE(nominalvalue,null, '0.0',nominalvalue) AS nominalvalue,oneoff,cptyname,foldercountryname,cptycountry,cptyparentcountry,foldercountry from PGT_MEX.T_PGT_MEX_CONSUMOSC_D WHERE LastParentF ='"+ grupo + "' and FECHACARGA='"+ fechaConsumo + "' AND foldercountryname='"+ pais + "' AND DEALSTAMP='"+ deal + "' AND valuedate=TO_CHAR(TO_DATE('"+ valuedate +"',  'DD/MON/YYYY'), 'YYYY-MM-DD') AND maturitydate=TO_CHAR(TO_DATE('"+ maturitydate +"',  'DD/MON/YYYY'), 'YYYY-MM-DD')AND INSTRUMENTNAME IN('GARANTIA ACCIONES COTIZADAS','GARANTIA AVAL FINANCIERO - NO USAR (3Q 2016)','GARANTIA DERECHOS DE COBRO','GARANTIA PERSONAL MANCOMUNADA','GARANTIA PERSONAL SOLIDARIA','GARANTIA PERSONAL SOLIDARIA FINAN','OTRAS GARANTIAS EN EFECTIVO','OTRAS GARANTIAS REALES NO LIQUIDAS','OTHER GUARANTY CASH') ORDER BY foldercountry,instrumentname");
		
		try {
			pstmt = con.prepareStatement(strbSql.toString());
			rs = pstmt.executeQuery();
			


			
			while (rs.next()) {
				systCode = rs.getString(1) + "," + rs.getString(2) + "," + "\"" + rs.getString(3) + "\"" + ","
						+ rs.getString(4) + "," + rs.getString(5) + "," + rs.getString(6) + "," + rs.getString(7)
						+ "," + rs.getString(8) + "," + rs.getString(9) + "," + rs.getString(10) + ","
						+ rs.getString(11) + "," + rs.getString(12) + "," + "\"" + rs.getString(13) + "\"" + ","
						+ rs.getString(14) + "," + "\"" + rs.getString(15) + "\"" + "," + rs.getString(16) + ","
						+ rs.getString(17) + "\n";
				
				//System.out.println("registros"+systCode);
				registrosInterfaz.add(systCode);
			} 

		} catch (SQLException e) {

			System.out.println("error en query " + e);
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
			String nombreInterfaz, String pais) {

		String CadenaBonosSpain = spainBonos.stream().collect(Collectors.joining(""));
		double totalSpainBonosNomValCur = spainBonosNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainBonosCer = spainBonosCerSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainBonosNomVal = spainBonosNomValSum.stream().mapToDouble(Double::doubleValue).sum();
		String CadenaEncabeza = encabezado.stream().collect(Collectors.joining(","));

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

		String CadenaSpainGaran = spainGaran.stream().collect(Collectors.joining(""));
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

		double totalSpainTotNomValCurSum = spainTotNomValCurSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainTotCerSum = spainTotCerSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalSpainTotNomValSum = spainTotNomValSum.stream().mapToDouble(Double::doubleValue).sum();

		try {
			FileWriter writer = new FileWriter(nombreInterfaz, true);

			if (!spainBonos.isEmpty()) {
				writer.write("Bonos-" + pais + "\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaBonosSpain);
				writer.write("," + "," + "," + "," + "Total Bonos" + "," + "," + "," + ","
						+ DFORMATO.format(totalSpainBonosNomValCur).toString() + "," + DFORMATO.format(totalSpainBonosCer).toString() + ","
						+ DFORMATO.format(totalSpainBonosNomVal).toString());
				writer.write("\n");
				// Credito Documentariado Spain

			}
			if (!spainBonos.isEmpty()) {
				writer.write("Bonos-" + pais + "\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaBonosSpain);
				writer.write("," + "," + "," + "," + "Total Bonos" + "," + "," + "," + ","
						+ DFORMATO.format(totalSpainBonosNomValCur).toString()+ "," + DFORMATO.format(totalSpainBonosCer).toString() + ","
						+ DFORMATO.format(totalSpainBonosNomVal).toString());
				writer.write("\n");

				// Credito Documentariado Spain
			}
			if (!spainCredDocu.isEmpty()) {
				writer.write("Creditos Documentariado-" + pais + "\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainCredDoc);
				writer.write("," + "," + "," + "," + " Total Creditos Documentariado" + "," + "," + "," + ","
						+ DFORMATO.format(totalSpainCredDocuNomValCurSum).toString() + ","
						+ DFORMATO.format(totalSpainCredDocuCerSum).toString() + ","
						+ DFORMATO.format(totalSpainCredDocuNomValSum).toString());
				writer.write("\n");
				// Exportacion/Importacion Spain
			}
			if (!spainExportImport.isEmpty()) {

				writer.write("Financiamiento IMP/EXP-" + pais + "\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainExportImport);
				writer.write("," + "," + "," + "," + "Total Financiamiento IMP/EXP" + "," + "," + "," + ","
						+ DFORMATO.format(totalSpainExportImportNomValCurSum).toString() + ","
						+ DFORMATO.format(totalSpainExportImportCerSum).toString() + ","
						+DFORMATO.format(totalSpainExportImportNomValSum).toString());
				writer.write("\n");

				// Comex/Forfaiting Spain
			}
			if (!spainComFor.isEmpty()) {
				writer.write("Financiamiento Comex-" + pais + "\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainComFor);
				writer.write("," + "," + "," + "," + "Total Financiamiento Comex" + "," + "," + "," + ","
						+ DFORMATO.format(totalSpainComForNomValCurSum).toString() + "," + DFORMATO.format(totalSpainComForCerSum).toString()
						+ "," + DFORMATO.format(totalSpainComForNomValSum).toString());
				writer.write("\n");
				// Sindicado Spain
			}
			if (!spainSindicado.isEmpty()) {
				writer.write("Creditos Sindicados-" + pais + "\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainSindicado);
				writer.write("," + "," + "," + "," + "Total Creditos Sindicados" + "," + "," + "," + ","
						+ DFORMATO.format(totalSpainSindicadoNomValCurSum).toString() + ","
						+ DFORMATO.format(totalSpainSindicadoCerSum).toString() + ","
						+ DFORMATO.format(totalSpainSindicadoNomValSum).toString());
				writer.write("\n");
				// Confirming Spain
			}
			if (!spainConfir.isEmpty()) {
				writer.write("Confirming-" + pais + "\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainConfir);
				writer.write("," + "," + "," + "," + "Total Confirming" + "," + "," + "," + ","
						+ DFORMATO.format(totalSpainConfirNomValCurSum).toString() + "," + DFORMATO.format(totalSpainConfirCerSum).toString()
						+ "," + DFORMATO.format(totalSpainConfirNomValSum).toString());
				writer.write("\n");
				// Descuentos
			}
			if (!spainDesc.isEmpty()) {
				writer.write("Descuentos-" + pais + "\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainDesc);
				writer.write("," + "," + "," + "," + "Toatal Descueltos" + "," + "," + "," + ","
						+ DFORMATO.format(totalSpainDescNomValCurSum).toString() + "," + DFORMATO.format(totalSpainDescCerSum).toString()
						+ "," + DFORMATO.format(totalSpainDescNomValSum).toString());
				writer.write("\n");
				// Factoring
			}
			if (!spainFac.isEmpty()) {
				writer.write("Factoring-" + pais + "\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainFac);
				writer.write("," + "," + "," + "," + "Total Factoring" + "," + "," + "," + ","
						+ DFORMATO.format(totalSpainFacNomValCurSum).toString() + "," + DFORMATO.format(totalSpainFacCerSum).toString() + ","
						+ DFORMATO.format(totalSpainFacNomValSum).toString());
				writer.write("\n");
				// Tarjetas
			}
			if (!spainTar.isEmpty()) {
				writer.write("Tarjeta de Credito-" + pais + "\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainTar);
				writer.write("," + "," + "," + "," + "Total Tarjeta de Credito" + "," + "," + "," + ","
						+ DFORMATO.format(totalSpainTarNomValCurSum).toString() + "," + Double.toString(totalSpainTarCerSum).toString() + ","
						+ DFORMATO.format(totalSpainTarNomValSum).toString());
				writer.write("\n");
				// Lineas Comprometidas
			}
			if (!spainLinCom.isEmpty()) {
				writer.write("Lineas Comprometidas-" + pais + "\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainLinCom);
				writer.write("," + "," + "," + "," + "Total Lineas Comprometidas" + "," + "," + "," + ","
						+ DFORMATO.format(totalSpainLinComNomValCurSum).toString() + "," + DFORMATO.format(totalSpainLinComCerSum).toString()
						+ "," + DFORMATO.format(totalSpainLinComNomValSum).toString());
				writer.write("\n");
			}
			if (!spainGaran.isEmpty()) {
				writer.write("Garantias-" + pais + "\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainGaran);
				writer.write("," + "," + "," + "," + "Total Garantias" + "," + "," + "," + ","
						+ DFORMATO.format(totalSpainGaranNomValCurSum).toString() + "," + DFORMATO.format(totalSpainGaranCerSum).toString()
						+ "," + DFORMATO.format(totalSpainGaranNomValSum).toString());
				writer.write("\n");
			}
			if (!spainAval.isEmpty()) {
				writer.write("Aval-" + pais + "\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainAval);
				writer.write("," + "," + "," + "," + "Total Aval" + "," + "," + "," + ","
						+ DFORMATO.format(totalSpainAvalNomValCurSum).toString() + "," + DFORMATO.format(totalSpainAvalCerSum).toString()
						+ "," + DFORMATO.format(totalSpainAvalNomValSum).toString());
				writer.write("\n");
			}
			if (!spainDer.isEmpty()) {
				writer.write("Derivados-" + pais + "\n");
				writer.write(CadenaEncabeza);
				writer.write(CadenaSpainDer);
				writer.write("," + "," + "," + "," + "Total Derivados" + "," + "," + "," + ","
						+ DFORMATO.format(totalSpainDerNomValCurSum).toString() + "," + DFORMATO.format(totalSpainDerCerSum).toString() + ","
						+ DFORMATO.format(totalSpainDerNomValSum).toString());
				writer.write("\n");
			}

			writer.write("TOTAL " + pais + "," + "," + "," + "," + "Total general" + "," + "," + "," + ","
					+ DFORMATO.format(totalSpainTotNomValCurSum).toString() + "," + DFORMATO.format(totalSpainTotCerSum).toString() + ","
					+ DFORMATO.format(totalSpainTotNomValSum).toString());
			writer.write("\n");

			writer.close();

		} catch (IOException e) {

			e.printStackTrace();
		}

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