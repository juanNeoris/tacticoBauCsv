package conexion;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

	public String getConsulta(String grupo,String nombreInterfaz, String fechaConsumo) throws Exception {

		Statement sta = con.createStatement();
		String str = "GARANTIA";
		String systCode2 = "";
		String systCode = "SELECT  'Garantia','dealstamp','cpty','cptyname','cptycountry','cptyparent','cptyparentname','cptyparentcountry','cptyparentrating','lastparent','lastparentname','lastparentcountry','lastparentrating','instrumentname','foldername','foldercountryname','valuedate','maturitydate','guaranteepercent_cpty','currency','nominalvaluecur','nominalvalue','oneoff','guaranteedparentrating','cer','recequivalente','recbruto','lastparentfname','lastparentfcountryname','lastparentfrating','dispuesto','cer2','collateralagreement' from DUAL UNION ALL SELECT to_char(INSTR(instrumentname,'GARANTIA')) GARANTIA,dealstamp, cpty,cptyname,cptycountry,cptyparent,cptyparentname,cptyparentcountry,cptyparentrating,lastparent,lastparentname,lastparentcountry,lastparentrating,instrumentname,foldername,foldercountryname,valuedate,maturitydate,guaranteepercent_cpty,currency,nominalvaluecur,nominalvalue,oneoff,guaranteedparentrating,cer,recequivalente,recbruto,lastparentfname,lastparentfcountryname,lastparentfrating,dispuesto,cer2,collateralagreement from PGT_MEX.T_PGT_MEX_CONSUMOSC_V WHERE LastParentF ='"+grupo+"' and FECHACARGA='"+ fechaConsumo+ "' UNION ALL SELECT to_char(INSTR(instrumentname,'GARANTIA')) GARANTIA,dealstamp,cpty,cptyname,cptycountry,cptyparent,cptyparentname,cptyparentcountry,cptyparentrating,lastparent, lastparentname,lastparentcountry,lastparentrating,instrumentname,foldername,foldercountryname,valuedate,maturitydate,guaranteepercent_cpty,currency,nominalvaluecur,nominalvalue,oneoff,guaranteedparentrating,cer,recequivalente,recbruto,lastparentfname,lastparentfcountryname,lastparentfrating,dispuesto,cer2,collateralagreement from PGT_MEX.T_PGT_MEX_CONSUMOSC_D WHERE LastParentF ='"+grupo+"' and FECHACARGA='"+ fechaConsumo + "'";
		ResultSet rs = sta.executeQuery(systCode);

		if (rs.equals(null) || rs.next() == false) {
			systCode = "No existen registros para este grupo en la interfaz CONSULTA";
		} else {
			String directoryName = System.getProperty("user.dir");
			FileOutputStream fos = new FileOutputStream(nombreInterfaz);

			do {
				// cadena no empata con numerico 
				systCode = rs.getString(1) + "," 
				         + rs.getString(2) + "," 
						 + rs.getString(3) + ","
						 + "\"" + rs.getString(4)+ "\"" + ","
						 + rs.getString(5) + "," 
   						 + rs.getString(6) + ","
   						 + "\"" + rs.getString(7) + "\"" + "," 
						 + rs.getString(8) + "," 
						 + rs.getString(9) + "," 
						 + rs.getString(10)+ "," 
						 + "\""+ rs.getString(11) + "\"" + ","
						 + rs.getString(12) + ","
						 + rs.getString(13) + "," // esta el filtro para las garantias aqui se debe de incluir el filtro
						 + rs.getString(14)+ "," 
						 + "\"" +rs.getString(15) + "\"" + "," 
						 + rs.getString(16) + ","
						 + rs.getString(17) + "," 
						 + rs.getString(18) + "," 
						 + rs.getString(19) + "," 
						 + rs.getString(20)+ "," 
						 + rs.getString(21) + "," 
						 + rs.getString(22) + "," 
						 + rs.getString(23) + ","
						 + rs.getString(24) + "," 
						 + rs.getString(25) + "," 
						 + rs.getString(26) + "," 
						 + rs.getString(27) + "," 
						 + "\"" + rs.getString(28) + "\"" + "," 
						 + "\""+rs.getString(29) + "\"" + ","
						 + rs.getString(30) + "," 
						 + rs.getString(31) + ","
						 + rs.getString(32) + ","
						 + rs.getString(33) + "\n";
				
				// cadena empata DEALSTAMP con numerico 
				systCode2 = rs.getString(1) + "," 
				         + "'"+rs.getString(2) + "," 
						 + rs.getString(3) + ","
						 + "\"" + rs.getString(4)+ "\"" + ","
						 + rs.getString(5) + "," 
   						 + rs.getString(6) + ","
   						 + "\"" + rs.getString(7) + "\"" + "," 
						 + rs.getString(8) + "," 
						 + rs.getString(9) + "," 
						 + rs.getString(10)+ "," 
						 + "\""+ rs.getString(11) + "\"" + ","
						 + rs.getString(12) + ","
						 + rs.getString(13) + "," // esta el filtro para las garantias aqui se debe de incluir el filtro
						 + rs.getString(14)+ "," 
						 + "\"" +rs.getString(15) + "\"" + "," 
						 + rs.getString(16) + ","
						 + rs.getString(17) + "," 
						 + rs.getString(18) + "," 
						 + rs.getString(19) + "," 
						 + rs.getString(20)+ "," 
						 + rs.getString(21) + "," 
						 + rs.getString(22) + "," 
						 + rs.getString(23) + ","
						 + rs.getString(24) + "," 
						 + rs.getString(25) + "," 
						 + rs.getString(26) + "," 
						 + rs.getString(27) + "," 
						 + "\"" + rs.getString(28) + "\"" + "," 
						 + "\""+rs.getString(29) + "\"" + ","
						 + rs.getString(30) + "," 
						 + rs.getString(31) + ","
						 + rs.getString(32) + ","
						 + rs.getString(33) + "\n";
					
				
					if (rs.getString(2).matches("[0-9]+")) {
						//cadena que va a escribir si es una garantia y el dealstamp es numerico 
						   fos.write(systCode2.getBytes());
						}else {

							fos.write(systCode.getBytes());
					
						}
		
			} while (rs.next());
			if (systCode.isEmpty()) {
				systCode = "No existen registros para este grupo en la interfaz CONSULTA";
			}
			fos.flush();
			fos.close();
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