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
import util.ConstantsUtil;

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
	private String validaVacio = "";
	private String validaNoVacio = "";

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
		ArrayList<String> instrumentos = this.consultaInstrumentos();

		Iterator<String> nombreIterator = instrumentos.iterator();
		while (nombreIterator.hasNext()) {
			String elemento = nombreIterator.next();
			LOGGER.info("evaluando elemento" +elemento);
			try (CallableStatement cs = con
					.prepareCall("{? = call pgt_mex.PKG_CONSUMOS_RTRA.F_CONSUMO_GENERAL_S(?,?,?,?)}")) {
				cs.registerOutParameter(1, -10);
				cs.setString(2, fechaConsumo);
				cs.setString(3, grupo);
				cs.setString(4, elemento);
				cs.setString(5, pais);
				cs.executeQuery();
				ResultSet rs = (ResultSet) cs.getObject(1);
				syscode = this.operaciones(rs, nombreInterfaz, pais, elemento);

			} catch (SQLException e) {
				LOGGER.error(e);
				LOGGER.error(e.getMessage(), e);
				LOGGER.error(e.getStackTrace());
			} finally {
				//LOGGER.info(ConstantsUtil.CIERRA);
			}
		}
		this.consulTotalGral(fechaConsumo, nombreInterfaz, grupo, pais);
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
	public ArrayList<String> consultaInstrumentos() {

		ArrayList<String> instrumentos = new ArrayList();

		try (CallableStatement cs = con.prepareCall("{? = call pgt_mex.PKG_CONSUMOS_RTRA.F_CONSUMO_INSTR_S()}")) {
			cs.registerOutParameter(1, -10);
			cs.executeQuery();
			ResultSet rs = (ResultSet) cs.getObject(1);

			if (rs == null || rs.next() == false) {
				LOGGER.info(ConstantsUtil.NOREGISTROS);
			} else {
				do {
					instrumentos.add(rs.getString(1));
				} while (rs.next());
			}
		} catch (SQLException e) {
			LOGGER.error(e);
			LOGGER.error(e.getMessage(), e);
			LOGGER.error(e.getStackTrace());
		} finally {
			LOGGER.info(ConstantsUtil.CIERRA);
		}
		return instrumentos;

	}

	/**
	 * metodo consulTotalGral que se encarga de obtener el total general del pais
	 * evaluado
	 * 
	 * @param fechaConsumo   enviada como parametro para la ejecucion del paquete
	 *                       que obtiene el total
	 * @param nombreInterfaz sobre la cual se va a escribir el total del pais
	 *                       evaluado
	 * @param grupo          enviada como parametro para la ejecucion del paquete
	 *                       que obtiene el total
	 * @param pais           enviada como parametro para la ejecucion del paquete
	 *                       que obtiene el total
	 * @return operaciones regresa en un Array el registro con el total
	 * @throws SQLException atrapa y muestra la excepcion generada por la ejecucion
	 *                      del paquete
	 */
	public List<BeanIntrumento> consulTotalGral(String fechaConsumo, String nombreInterfaz, String grupo, String pais)
			throws SQLException {
		List<BeanIntrumento> operaciones = new ArrayList<BeanIntrumento>();
		try (CallableStatement cs = con
				.prepareCall("{? = call pgt_mex.PKG_CONSUMOS_RTRA.F_CONSUMO_FINALMOUNT_S(?,?,?)}")) {
			cs.registerOutParameter(1, -10);
			cs.setString(2, fechaConsumo);
			cs.setString(3, grupo);
			cs.setString(4, pais);
			cs.executeQuery();
			ResultSet rs = (ResultSet) cs.getObject(1);
			if (rs == null || rs.next() == false) {
				LOGGER.info(ConstantsUtil.NOREGISTROS);
			} else {
				do {

					BeanIntrumento bean = new BeanIntrumento();
					bean.setCptyparent("TOTAL " + pais.toUpperCase());
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

				} while (rs.next());
				rs.close();
				cs.close();
				Instrumento.interfazCsvTotales(operaciones, nombreInterfaz);
				operaciones.clear();
			}
		} catch (SQLException e) {
			LOGGER.error(e);
			LOGGER.error(e.getMessage(), e);
			LOGGER.error(e.getStackTrace());
		} finally {
			LOGGER.info(ConstantsUtil.CIERRA);
		}
		return operaciones;

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
	public void getConsultaOtrosPaises(String grupo, String nombreInterfaz, String fechaConsumo) throws SQLException {
		LOGGER.info("Empezando Otros Paises");

		ArrayList<String> paises = this.getPaisesDisponibles(fechaConsumo, grupo);
		Iterator<String> nombrePais = paises.iterator();
		while (nombrePais.hasNext()) {
			String paisDisponible = nombrePais.next();

			ArrayList<String> instrumentos = this.consultaInstrumentos();
			Iterator<String> nombreIntrumento = instrumentos.iterator();
			while (nombreIntrumento.hasNext()) {
				String elemento = nombreIntrumento.next();
				try (CallableStatement cs = con
						.prepareCall("{? = call pgt_mex.PKG_CONSUMOS_RTRA.F_CONSUMO_GENERAL_S(?,?,?,?)}")) {
					cs.registerOutParameter(1, -10);
					cs.setString(2, fechaConsumo);
					cs.setString(3, grupo);
					cs.setString(4, elemento);
					cs.setString(5, paisDisponible);
					cs.executeQuery();
					ResultSet rs = (ResultSet) cs.getObject(1);
					this.operaciones(rs, nombreInterfaz, paisDisponible, elemento);
				} catch (SQLException e) {
					LOGGER.error(e);
					LOGGER.error(e.getMessage(), e);
					LOGGER.error(e.getStackTrace());
				} finally {
					LOGGER.info(ConstantsUtil.CIERRA);
				}
			}
			this.consulTotalGral(fechaConsumo, nombreInterfaz, grupo, paisDisponible);
		}
		LOGGER.info("Terminado Otros Paises");

	}

	/**
	 * Metodo operaciones que se encargara de almacenar en un objeto el result
	 * obyenido de la ejecucion del paquete
	 * 
	 * @param rs             result enviado de la ejecucion del paquete
	 * @param nombreInterfaz con la que se generara la interfaz
	 * @param paisDisponible enviado de la ejecucion del paquete que obtiene los
	 *                       paises disponibles
	 * @param elemento       Intrumento evaluado
	 * @return syscode regresa un string que permitira validar si existe o no
	 *         informacion para generar no generar la interfaz vacia
	 * @throws SQLException atrapada y mostrada durante la ejecucion del paquete
	 */

	public String operaciones(ResultSet rs, String nombreInterfaz, String paisDisponible, String elemento)
			throws SQLException {

		List<BeanIntrumento> operaciones = new ArrayList<BeanIntrumento>();
		if (rs == null || rs.next() == false) {
			validaVacio = ConstantsUtil.NOREGISTROS;
		} else {

			do {
				//cpty	lastparent	lastparentname	lastparentcountry	lastparentrating	foldername	guaranteepercent_cpty	 addoncur 	guaranteedparentrating	 recequivalente 	 recbruto 	lastparentf	lastparentfname	lastparentfcountryname	lastparentfrating	 dispuesto 	 cer2 	collateralagreement

				validaNoVacio = "no vacio";
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

				/**
				 * cierra el while que obtiene el bloque de datos para el intrumento
				 */
			} while (rs.next());
			/**
			 * cierra el while que recorre los intrumentos
			 */
			rs.close();
			LOGGER.info("elemento escrito " + elemento);
			LOGGER.info("pais escrito "+paisDisponible);
			Instrumento.interfazCsv(operaciones, nombreInterfaz, paisDisponible, elemento);
			operaciones.clear();

		}
		if (!validaNoVacio.isEmpty()) {
			return validaNoVacio;
		} else {
			return validaVacio;
		}
	}

	/**
	 * Metodo getContraparte validar si tiene una garantia la operacion que se esta
	 * evaluando
	 * 
	 * @param fechaConsumo se valida con la fecha que se esta evaluando
	 * @param deal         usa para validar la garantia
	 * @param pais         otra condicion es que sea del pais
	 * @param grupo        se envia como parametro para la ejecucion del paquete
	 * @return registrosInterfaz regresa el array con las contrapartes
	 * @throws SQLException atrapa y muestra las excepciones generadas por la
	 *                      ejecucion del paquete
	 */
	public ArrayList<String> getPaisesDisponibles(String fechaConsumo, String grupo) throws SQLException {
		ArrayList<String> paisesDisponibles = new ArrayList<String>();

		try (CallableStatement cs = con.prepareCall("{? = call pgt_mex.PKG_CONSUMOS_RTRA.F_CONSUMO_COUNTRY_S(?,?)}")) {
			cs.registerOutParameter(1, -10);
			cs.setString(2, fechaConsumo);
			cs.setString(3, grupo);
			cs.executeQuery();
			ResultSet rs = (ResultSet) cs.getObject(1);
			if (rs == null || rs.next() == false) {
				LOGGER.info(ConstantsUtil.NOREGISTROS);
			} else {
				do {
					paisesDisponibles.add(rs.getString(1));
				} while (rs.next());
			}
		} catch (SQLException e) {
			LOGGER.error(e);
			LOGGER.error(e.getMessage(), e);
			LOGGER.error(e.getStackTrace());
		} finally {
			LOGGER.info(ConstantsUtil.CIERRA);
		}
		return paisesDisponibles;
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