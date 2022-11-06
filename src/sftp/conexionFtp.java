package sftp;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
/*
Autor: Ing. Juan Manuel Baca Zúñiga
 */
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import conexion.SSHConnector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import sftp.conexionFtp;
import view.vista;

/*
 * Clase que establece la conexion SFTP 
 * con el servidor de GBO y tranfiere
 * los archivos RTRA 
 * */

public class conexionFtp {

	private static final Logger logger = LogManager.getLogger(conexionFtp.class);

	// Inicia una variable de tipo sftp.session para poder realizar operaciones de
	// tipo sftp
	private static Session session;
	static ConfigProperties confPro = new ConfigProperties();

	/*
	 * Metodo que valida la conexion SFTP
	 */
	public void conn() throws JSchException, IllegalAccessException {

		confPro.createVar();

		if (this.session == null || !this.session.isConnected()) {
			JSch jsch = new JSch();

			// Si el Parametro strict.HostKey del archivos properties esta true entonces se
			// utiliza una key para conectar
			// implementar la conexión cuando se utilice un key y knowhost
			if (confPro.isStrictHKC() != true && confPro.isConectKnowHost() != true) {
				this.session = jsch.getSession(confPro.getUsuario(), confPro.getHost(), confPro.getPuerto());
				this.session.setConfig("StrictHostKeyChecking", "no");
				this.session.setPassword(confPro.getPassword());
			} else {
				jsch.addIdentity(confPro.getKey());
				this.session = jsch.getSession(confPro.getUsuario(), confPro.getHost(), confPro.getPuerto());
				this.session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
				java.util.Properties config = new java.util.Properties();
				config.put("StrictHostKeyChecking", "no");
				session.setConfig(config);
				logger.info("conexion con key activa");
			}
			this.session.connect();
			logger.info("Sftp connected");
		} else {
			throw new IllegalAccessException("Sesión sftp ya establecida.");
		}
	}

	/*
	 * Metodo que cierra la conexion SFTP
	 */
	public final void closeConn() {
		this.session.disconnect();
		logger.info("Sftp disconnected");
	}

	/*
	 * Metodo que realiza la Tranferencia de los archivos SFTP
	 * 
	 * @param fecha
	 */
	public void tranferirArchivos(long time)
			throws SftpException, IllegalAccessException, JSchException, IOException, InterruptedException {

		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
		String date = sdf.format(time);
		SimpleDateFormat sdfRtra = new SimpleDateFormat("yyyyMMdd");
		String dateShelRTRA = sdfRtra.format(time);
		String dateShelRTRALog = DateTimeFormatter.ofPattern("MMMdd", Locale.ENGLISH).format(LocalDateTime.now());

		System.out.println("date :" + date);
		System.out.println("dateShelRTRA :" + dateShelRTRA);
		System.out.println("dateShelRTRALog :" + dateShelRTRALog);

		confPro.createVar();
		JSch jsch = new JSch();
		/*********************************************************
		 * Carga rtra victoria dolphing
		 *********************************************************************/

		/*
		 * conexion por SSH para la ejecucion del shell que realizara la carga de los
		 * archivos rtra victoria y dolphing.
		 */
		vista.textField_1.setText("Sin carga, cargando victoria y dolphing, espere...");
		vista.textField_1.update(vista.textField_1.getGraphics());

		try {
			/*
			 * establecemos las variables de conexion para tranferir los RTRA una vez que
			 * validemos que existen en la ruta
			 */

			Session session = jsch.getSession(confPro.getUsuario(), confPro.getHost(), confPro.getPuerto());
			session.setPassword(confPro.getPassword());

			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);

			session.connect(10000);

			Channel channel = session.openChannel("sftp");
			channel.connect(50000);
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			sftpChannel.put(
					"\\\\mx2ct1hnascifnfsevs1.mx.corp\\ExtraccionesMIR\\rtra\\"
							+ "rtra-cream-ges-dolphin-europa_mexico_" + dateShelRTRA + ".txt",
					"/planPGTMEX/procesos/RISK/interfaces");
			sftpChannel.put(
					"\\\\mx2ct1hnascifnfsevs1.mx.corp\\ExtraccionesMIR\\rtra\\"
							+ "rtra-cream-ges-victoria-europa_mexico_" + dateShelRTRA + ".txt",
					"/planPGTMEX/procesos/RISK/interfaces");
			sftpChannel.exit();
			sftpChannel.disconnect();

			/*
			 * establecemos las variables de conexion se valida que existan las interfaces
			 * en la ruta: /planPGTMEX/procesos/RISK/interfaces
			 */
			SSHConnector sshConnector = new SSHConnector();
			sshConnector.connect(confPro.getUsuario(), confPro.getPassword(), confPro.getHost(), confPro.getPuerto());

			String resultVic = sshConnector.executeCommand(
					"cd /planPGTMEX/procesos/RISK/interfaces; ls -ltr rtra-cream-ges-victoria-europa_mexico_"
							+ dateShelRTRA + ".txt ");
			String resultDolp = sshConnector.executeCommand(
					"cd /planPGTMEX/procesos/RISK/interfaces; ls -ltr rtra-cream-ges-dolphin-europa_mexico_"
							+ dateShelRTRA + ".txt ");

			if (resultVic.isEmpty() && resultDolp.isEmpty()) {
				vista.textField_1.setText("No existen tus ficheros RTRA's en el FileShare");
				vista.textField_1.update(vista.textField_1.getGraphics());
				Thread.sleep(3000);

			} else {

				String result = sshConnector
						.executeCommand("cd /planPGTMEX/procesos/RISK/;./cargartrabau.sh " + date + "");
			}

			/*
			 * Se validan los logs si el proceso termino de manera exitosa
			 */
			String resultLogs = sshConnector.executeCommand("cd /planPGTMEX/procesos/RISK/salidas/" + dateShelRTRALog
					+ "/cargartrabau.sh; ls -ltr cargartrabau.sh.out");
			String resultLogsVictoria = sshConnector.executeCommand("cd /planPGTMEX/procesos/RISK/salidas/"
					+ dateShelRTRALog + "/cargartrabau.sh; ls -ltr interfazVictoria.bad");

			/*
			 * Se borran las interfaces de la ruta: /planPGTMEX/procesos/RISK/salidas/ una
			 * vez que han sido cargadas de manea exitosa
			 */

			sshConnector
					.executeCommand("cd /planPGTMEX/procesos/RISK/interfaces; rm rtra-cream-ges-victoria-europa_mexico_"
							+ dateShelRTRA + ".txt ");
			sshConnector
					.executeCommand("cd /planPGTMEX/procesos/RISK/interfaces; rm rtra-cream-ges-dolphin-europa_mexico_"
							+ dateShelRTRA + ".txt ");

			sshConnector.disconnect();

			if (!resultLogsVictoria.isEmpty()) {
				vista.textField_1.setText("Carga parcial archivo con registros erroneos ");
				vista.textField_1.update(vista.textField_1.getGraphics());

			} else if (resultLogs.isEmpty()) {
				vista.textField_1.setText("Ocurrio un problema al cargar los RTRA's");
				vista.textField_1.update(vista.textField_1.getGraphics());
				Thread.sleep(4000);

			} else {
				vista.textField_1.setText("Proceso finalizado, indique grupo");
				vista.textField_1.update(vista.textField_1.getGraphics());
			}

		} catch (RuntimeException e1) {
			try {
				vista.textField_1.setText("No se pudo realizar la carga de archivos RTRA's");
				vista.textField_1.update(vista.textField_1.getGraphics());
				Thread.sleep(4000);

			} catch (RuntimeException e) {
				logger.info("Problemas en SFTP" + e);
			}

		}

	}

	/*
	 * Genera el archivo log que mostrara si se genero o no un problmea en la
	 * conexion SFTP
	 * 
	 * @param nombreFichero
	 * 
	 * @param datosAEscribir
	 * 
	 * @return fichero
	 */
	public boolean makeFichero(String nombreFichero, String datosAEscribir) throws IOException {
		String formatoFecha = "yyyy_MM_dd-hh_mm_ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatoFecha);
		String pathUsuario = System.getProperty("user.dir");
		String fechaArchivo = simpleDateFormat.format(new Date());
		File fichero = new File(pathUsuario, nombreFichero + fechaArchivo + ".txt");
		escribeArchivo(String.valueOf(fichero), datosAEscribir);

		return fichero.createNewFile();
	}

	/*
	 * Genera el archivo log que mostrara si se genero o no un problmea en la
	 * conexion SFTP
	 * 
	 * @param nombreFichero
	 * 
	 * @param datosAEscribir
	 */

	private void escribeArchivo(String rutaCompletaFichero, String datosAEscribir) throws IOException {
		FileWriter fichero = new FileWriter(rutaCompletaFichero);

		fichero.write(datosAEscribir);
		fichero.close();

	}

	/*
	 * Genera el archivo log que mostrara si se genero o no un problmea en la
	 * conexion SFTP
	 * 
	 * @param nombreFichero
	 * 
	 * @param datosAEscribir
	 */
	public String datosServidor() {
		ConfigProperties confPro = new ConfigProperties();
		confPro.createVar();
		String tipoConexion = "Password";

		if (confPro.isStrictHKC() == true) {
			tipoConexion = "ID_RSA";
		}
		return "Host: " + confPro.getHost() + "\nPuerto: " + confPro.getPuerto() + "\nConexión mediante: "
				+ tipoConexion;
	}

}
