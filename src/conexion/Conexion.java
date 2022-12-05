package conexion;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import gettersetter.BeanIntrumento;
import interfaz.Instrumento;
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
			pstmt.close();
			rs.close();
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
		LOGGER.info("empezando Mexico");
		String pais = "Mexico";
		String syscode = "";
		List<BeanIntrumento> operaciones = new ArrayList<BeanIntrumento>();
		ArrayList<String> instrumentos = this.consultaInstrumentos();

		Iterator<String> nombreIterator = instrumentos.iterator();
		while (nombreIterator.hasNext()) {
			String elemento = nombreIterator.next();
			System.out.println("elemento  :" + elemento);
			CallableStatement cs = con.prepareCall("{? = call pgt_mex.PKG_CONSUMOS_RTRA.F_CONSUMO_GENERAL_S(?,?,?,?)}");
			cs.registerOutParameter(1, -10);
			cs.setString(2, fechaConsumo);
			cs.setString(3, grupo);
			cs.setString(4, elemento);
			cs.setString(5, pais);
			cs.executeQuery();
			ResultSet rs = (ResultSet) cs.getObject(1);

			if (rs.equals(null) || rs.next() == false) {
				syscode = "No existen registros para este grupo en la interfaz CONSULTA";
			} else {
				do {
					try {
						BeanIntrumento bean = new BeanIntrumento();
						bean.setCptyparent(rs.getString(3));
						bean.setCptyparentrating(rs.getString(4));
						bean.setCptyparentname(rs.getString(5));
						bean.setDealstamp(rs.getString(6));
						bean.setInstrumentname(rs.getString(7));
						bean.setValuedate(rs.getString(8));
						bean.setMaturitydate(rs.getString(9));
						bean.setCurrency(rs.getString(10));
						bean.setNominalvaluecur(rs.getString(11));
						bean.setCer(rs.getString(12));
						bean.setNominalvalue(rs.getString(13));
						bean.setOneoff(rs.getString(14));
						bean.setCptyname(rs.getString(15));
						bean.setFoldercountryname(rs.getString(16));
						bean.setCptycountry(rs.getString(17));
						bean.setCptyparentcountry(rs.getString(18));
						bean.setFoldercountry(rs.getString(19));
						operaciones.add(bean);
					} catch (Exception e) {
						LOGGER.error(e);
						LOGGER.error(e.getMessage(), e);
						LOGGER.error(e.getStackTrace());
					}
					/**
					 * cierra el while que obtiene el bloque de datos para el intrumento
					 */
				} while (rs.next());
				/**
				 * cierra el while que recorre los intrumentos
				 */
				Instrumento.interfazCsv(operaciones, nombreInterfaz, pais, elemento);
				operaciones.clear();
			}

		}
		this.consulTotalGral(fechaConsumo,nombreInterfaz, grupo, pais);
		syscode = "terminado Mexico";
		LOGGER.info("terminado Mexico");
		return syscode;
	}

	/**
	 * metodo consultaInstrumentos que se encarga de obtener los instrumentos en
	 * orden alfabetico para ser consultados uno a uno
	 * 
	 * @return instrumentos array que guarda los instrumentos parametrizados
	 * @throws SQLException atrapa las excepciones generadas por la ejecucion del
	 *                      paquete
	 */
	public ArrayList<String> consultaInstrumentos() throws SQLException {

		ArrayList<String> instrumentos = new ArrayList();

		CallableStatement cs = con.prepareCall("{? = call pgt_mex.PKG_CONSUMOS_RTRA.F_CONSUMO_INSTR_S()}");
		cs.registerOutParameter(1, -10);
		cs.executeQuery();
		ResultSet rs = (ResultSet) cs.getObject(1);

		if (rs.equals(null) || rs.next() == false) {
			String syscode = "No existen registros para este grupo en la interfaz CONSULTA";
		} else {
			do {
				try {
					if (!rs.getString(1).equals("No existen registros para este grupo en la interfaz CONSULTA")) {
						instrumentos.add(rs.getString(1));
						LOGGER.info(rs.getString(1));
					}
				} catch (Exception e) {
					LOGGER.error(e);
					LOGGER.error(e.getMessage(), e);
					LOGGER.error(e.getStackTrace());
				}

			} while (rs.next());
		}
		return instrumentos;

	}
//fechaConsumo,nombreInterfaz, grupo, pais
	public List<BeanIntrumento> consulTotalGral(String fechaConsumo, String nombreInterfaz,String grupo, String pais) throws SQLException {

		List<BeanIntrumento> operaciones = new ArrayList<BeanIntrumento>();
		
		
		CallableStatement cs = con.prepareCall("{? = call pgt_mex.PKG_CONSUMOS_RTRA.F_CONSUMO_FINALMOUNT_S(?,?,?)}");
		cs.registerOutParameter(1, -10);
		cs.setString(2, fechaConsumo);
		cs.setString(3, grupo);
		cs.setString(4, pais);
		cs.executeQuery();
		
		ResultSet rs = (ResultSet) cs.getObject(1);

		if (rs.equals(null) || rs.next() == false) {
			String syscode = "No existen registros para este grupo en la interfaz CONSULTA";
		} else {
			do {
				try {
					BeanIntrumento bean = new BeanIntrumento();
					bean.setCptyparent("TOTAL "+pais.toUpperCase());
					bean.setCptyparentrating("");
					bean.setCptyparentname("");
					bean.setDealstamp("");
					bean.setInstrumentname("TOTAL GENERAL");
					bean.setValuedate("");
					bean.setMaturitydate("");
					bean.setCurrency("");
					bean.setNominalvaluecur(rs.getString(11));
					bean.setCer(rs.getString(12));
					bean.setNominalvalue(rs.getString(13));
					bean.setOneoff("");
					bean.setCptyname("");
					bean.setFoldercountryname("");
					bean.setCptycountry("");
					bean.setCptyparentcountry("");
					bean.setFoldercountry("_");
					operaciones.add(bean);
				} catch (Exception e) {
					LOGGER.error(e);
					LOGGER.error(e.getMessage(), e);
					LOGGER.error(e.getStackTrace());
				}

			} while (rs.next());
			Instrumento.interfazCsvTotales(operaciones, nombreInterfaz, pais);
			operaciones.clear();
		}
		return operaciones;

	}

	/**
	 * metodo getQueryRowCount que obtiene el numero de registros
	 * 
	 * @param query recibe el query para obtener el count
	 * @return size regresa el numero de registros
	 * @throws SQLException atrapa la excepcion genera por la ejecucion del query
	 */
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
		LOGGER.info("empezando Otros Paises");
		String systCode = "SELECT cptyparent, NVL(cptyparentrating, 'SIN RATING'),cptyparentname,dealstamp,instrumentname,TO_CHAR(TO_DATE(valuedate,  'YYYY-MM-DD'), 'DD-mon-YY'),TO_CHAR(TO_DATE(maturitydate,  'YYYY-MM-DD'), 'DD-mon-YY'),currency,to_char(DECODE(nominalvaluecur,null, '0.0',nominalvaluecur), '999,999,999,999.99')  AS nominalvaluecur,to_char(DECODE(CER,null, '0.0',CER), '999,999,999,999.99')  AS CER,to_char(DECODE(nominalvalue,null, '0.0',nominalvalue), '999,999,999,999.99')  AS nominalvalue,oneoff,cptyname,foldercountryname,cptycountry,cptyparentcountry,foldercountry from PGT_MEX.T_PGT_MEX_CONSUMOSC_V WHERE LastParentF ='"
				+ grupo + "' and FECHACARGA='" + fechaConsumo
				+ "' AND foldercountryname<>'Mexico' UNION ALL SELECT cptyparent, NVL(cptyparentrating, 'SIN RATING'),cptyparentname,dealstamp,instrumentname,TO_CHAR(TO_DATE(valuedate,  'YYYY-MM-DD'), 'DD-mon-YY'),TO_CHAR(TO_DATE(maturitydate,  'YYYY-MM-DD'), 'DD-mon-YY'),currency,to_char(DECODE(nominalvaluecur,null, '0.0',nominalvaluecur), '999,999,999,999.99')  AS nominalvaluecur,to_char(DECODE(CER,null, '0.0',CER), '999,999,999,999.99')  AS CER,to_char(DECODE(nominalvalue,null, '0.0',nominalvalue), '999,999,999,999.99')  AS nominalvalue,oneoff,cptyname,foldercountryname,cptycountry,cptyparentcountry,foldercountry from PGT_MEX.T_PGT_MEX_CONSUMOSC_D WHERE LastParentF ='"
				+ grupo + "' and FECHACARGA='" + fechaConsumo
				+ "' AND foldercountryname<>'Mexico' ORDER BY foldercountryname ,instrumentname";
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
							rs.getString(14), rs.getString(9), rs.getString(10), rs.getString(11), nombreInterfaz,
							total);
				} catch (Exception e) {
					LOGGER.error(e);
					LOGGER.error(e.getMessage(), e);
					LOGGER.error(e.getStackTrace());
				}

			} while (rs.next());
			if (systCode.isEmpty()) {
				systCode = "No existen registros para este grupo en la interfaz CONSULTA";
			}

		}
		LOGGER.info("terminado otros Paises");
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
		strbSql.append("SELECT lastparentfname  AS nominalvalue from PGT_MEX.T_PGT_MEX_CONSUMOSC_D WHERE LastParentF ='"
				+ grupo + "' and FECHACARGA='" + date + "' AND foldercountryname='Mexico'");
		try {
			pstmt = con.prepareStatement(strbSql.toString());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				systCode = rs.getString(1);
			} else {
				systCode = "No hay ultima carga";
			}
			pstmt.close();
			rs.close();
		} catch (SQLException e) {
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
			pstmt.close();
			rs.close();
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
			pstmt.close();
			rs.close();
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
			pstmt.close();
			rs.close();
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