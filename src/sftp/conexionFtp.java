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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import view.vista;

import org.apache.log4j.Logger;

/**
 * Clase que establece la conexion SFTP con el servidor de GBO y tranfiere los
 * archivos RTRA
 */

public class ConexionFtp {

	private static final Logger LOGGER = Logger.getLogger(ConexionFtp.class.getName());

	/**
	 * variable que gurada la variable
	 *  que valida si es estricto el uso de una key
	 */
	public static final String STRICTHOSTKEYCHECKING  = "StrictHostKeyChecking";
	
	/**
	 * variable que gurada la variable
	 *  que valida si es estricto el uso de una key
	 */
	public static final String TXT = ".txt";
	
	/**
	 *  Inicia una variable de tipo sftp.session 
	 *  para poder realizar operaciones de tipo sftp
	 */
	
	private Session session;
	/**
	 * Se crea la instancia que carga los archivos Properties
	 */
	private ConfigProperties confPro = new ConfigProperties();
	

	/**
	 * Metodo que valida la conexion SFTP
	 * 
	 * @throws JSchException          excepcion genera al conectarce por SSH
	 * @throws IllegalAccessException generada si la conexion ya fue establecida
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
				this.session.setConfig(STRICTHOSTKEYCHECKING, "no");
				this.session.setPassword(confPro.getPassword());
			} else {
				jsch.addIdentity(confPro.getKey());
				this.session = jsch.getSession(confPro.getUsuario(), confPro.getHost(), confPro.getPuerto());
				this.session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
				java.util.Properties config = new java.util.Properties();
				config.put(STRICTHOSTKEYCHECKING, "no");
				session.setConfig(config);
				LOGGER.info("conexion con key activa");
			}
			/**
			 * conexion exitosa
			 */
			this.session.connect();
			LOGGER.info("Sftp connected");
		} else {
			throw new IllegalAccessException("Sesión sftp ya establecida.");
		}
	}

	/**
	 * Metodo que cierra la conexion SFTP
	 */
	public final void closeConn() {
		this.session.disconnect();
		LOGGER.info("Sftp disconnected");
	}

	/**
	 * Metodo que realiza la Tranferencia de los archivos SFTP
	 * 
	 * @param time se aplicaran distrintos formatos para realizar validaciones
	 *             ejecucion y tranferencias
	 * @throws SftpException          generada al realizar conexion sftp
	 * @throws JSchException          excepcion genera al conectarce por SSH
	 * @throws IOException            generada durante la ejecucion del metodo
	 * @throws InterruptedException   generada durante la tranferencia o ejecucion
	 * @throws IllegalAccessException genera por errores en las credenciales de
	 *                                conexion
	 * 
	 */
	public void tranferirArchivos(long time) {
		/**
		 * se aplican los formatos a la fecha obtenida desde la interfaz para transferir
		 * los arhivos al servidor de GBO para realizar la ejecucion del shell que carga
		 * los RTRA validar si la carga ha sido exitosa
		 * 
		 */
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
		String date = sdf.format(time);
		SimpleDateFormat sdfRtra = new SimpleDateFormat("yyyyMMdd");
		String dateShelRTRA = sdfRtra.format(time);
		String dateShelRTRALog = DateTimeFormatter.ofPattern("MMMdd", Locale.ENGLISH).format(LocalDateTime.now());

		confPro.createVar();
		JSch jsch = new JSch();
		/*********************************************************
		 * Carga rtra victoria dolphing
		 *********************************************************************/

		/**
		 * conexion por SSH para la ejecucion del shell que realizara la carga de los
		 * archivos rtra victoria y dolphing.
		 */
		vista.textField1.setText("Sin carga, cargando victoria y dolphing, espere...");
		vista.textField1.update(vista.textField1.getGraphics());

		try {
			/**
			 * establecemos las variables de conexion para tranferir los RTRA una vez que
			 * validemos que existen en la ruta
			 */

			Session sesion = jsch.getSession(confPro.getUsuario(), confPro.getHost(), confPro.getPuerto());
			sesion.setPassword(confPro.getPassword());

			Properties config = new Properties();
			config.put(STRICTHOSTKEYCHECKING, "no");
			sesion.setConfig(config);

			sesion.connect(10000);
			/**
			 * se establece la conexion al servidor de GBO se realiza el put desde un
			 * fileshare a la ruta /planPGTMEX/procesos/RISK/interfaces
			 */
			Channel channel = sesion.openChannel("sftp");
			channel.connect(50000);
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			sftpChannel.put(
					"\\\\mx2ct1hnascifnfsevs1.mx.corp\\ExtraccionesMIR\\rtra\\"
							+ "rtra-cream-ges-dolphin-europa_mexico_" + dateShelRTRA + TXT,
					"/planPGTMEX/procesos/RISK/interfaces");
			sftpChannel.put(
					"\\\\mx2ct1hnascifnfsevs1.mx.corp\\ExtraccionesMIR\\rtra\\"
							+ "rtra-cream-ges-victoria-europa_mexico_" + dateShelRTRA + TXT,
					"/planPGTMEX/procesos/RISK/interfaces");
				
			sftpChannel.exit();
			sftpChannel.disconnect();
			
			/**
			 * establecemos las variables de conexion se valida que existan las interfaces
			 * en la ruta: /planPGTMEX/procesos/RISK/interfaces
			 */
			SSHConnector sshConnector = new SSHConnector();
			sshConnector.connect(confPro.getUsuario(), confPro.getPassword(), confPro.getHost(), confPro.getPuerto());
			String resultVic = sshConnector.executeCommand(
					"cd /planPGTMEX/procesos/RISK/interfaces; ls -ltr rtra-cream-ges-victoria-europa_mexico_"
							+ dateShelRTRA + TXT);
			String resultDolp = sshConnector.executeCommand(
					"cd /planPGTMEX/procesos/RISK/interfaces; ls -ltr rtra-cream-ges-dolphin-europa_mexico_"
							+ dateShelRTRA + TXT);
			
			if (resultVic.isEmpty() && resultDolp.isEmpty()) {
				vista.textField1.setText("No existen tus ficheros RTRA's en el FileShare");
				vista.textField1.update(vista.textField1.getGraphics());
				Thread.sleep(3000);

			} else {
				/**
				 * se realiza la ejecucion del shell que carga los RTRA a la BBDD de GBO
				 */
				sshConnector.executeCommand("cd /planPGTMEX/procesos/RISK/;./cargartrabau.sh " + date + "");
			}

			/**
			 * Se validan los logs si el proceso termino de manera exitosa , se cargo de
			 * manera incompleta o no se cargo
			 */
			String resultLogs = sshConnector.executeCommand("cd /planPGTMEX/procesos/RISK/salidas/" + dateShelRTRALog
					+ "/cargartrabau.sh; ls -ltr cargartrabau.sh.out");
			String resultLogsVictoria = sshConnector.executeCommand("cd /planPGTMEX/procesos/RISK/salidas/"
					+ dateShelRTRALog + "/cargartrabau.sh; ls -ltr interfazVictoria.bad");

			/**
			 * Se borran las interfaces de la ruta: /planPGTMEX/procesos/RISK/salidas/ una
			 * vez que han sido cargadas de manera exitosa
			 */

			sshConnector
					.executeCommand("cd /planPGTMEX/procesos/RISK/interfaces; rm rtra-cream-ges-victoria-europa_mexico_"
							+ dateShelRTRA + TXT);
			sshConnector
					.executeCommand("cd /planPGTMEX/procesos/RISK/interfaces; rm rtra-cream-ges-dolphin-europa_mexico_"
							+ dateShelRTRA + TXT);

			sshConnector.disconnect();
			/**
			 * se imprimen los mensajes de las validaciones anteriormente echas
			 */
			if (!resultLogsVictoria.isEmpty()) {
				vista.textField1.setText("Carga parcial archivo con registros erroneos ");
				vista.textField1.update(vista.textField1.getGraphics());

			} else if (resultLogs.isEmpty()) {
				vista.textField1.setText("Ocurrio un problema al cargar los RTRA's");
				vista.textField1.update(vista.textField1.getGraphics());
				Thread.sleep(4000);

			} else {
				vista.textField1.setText("Proceso finalizado, indique grupo");
				vista.textField1.update(vista.textField1.getGraphics());
			}
		} catch (RuntimeException | JSchException | IllegalAccessException | IOException |SftpException
				| InterruptedException e1) {
			try {
				vista.textField1.setText("No se pudo realizar la carga de archivos RTRA's");
				vista.textField1.update(vista.textField1.getGraphics());
				Thread.sleep(4000);
				LOGGER.info("Problemas en SFTP" + e1);
			} catch (RuntimeException | InterruptedException e) {
				LOGGER.info("Problemas en SFTP" + e);
			}

		}

	}

	/**
	 * Genera el archivo log que mostrara si se genero o no un problmea en la
	 * conexion SFTP
	 * 
	 * @param nombreFichero  que servira como log del la transferencia
	 * @param datosAEscribir el mensaje en si generado
	 * @return fichero archivo final generado
	 * @throws IOException generada durante la ejecucion del metodo
	 */
	public boolean makeFichero(String nombreFichero, String datosAEscribir) throws IOException {
		String formatoFecha = "yyyy_MM_dd-hh_mm_ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatoFecha);
		String pathUsuario = System.getProperty("user.dir");
		String fechaArchivo = simpleDateFormat.format(new Date());
		File fichero = new File(pathUsuario, nombreFichero + fechaArchivo + TXT);
		escribeArchivo(String.valueOf(fichero), datosAEscribir);

		return fichero.createNewFile();
	}

	/**
	 * Genera el archivo log que mostrara si se genero o no un problmea en la
	 * conexion SFTP
	 * 
	 * @param nombreFichero  que servira como log del la transferencia
	 * @param datosAEscribir el mensaje en si generado
	 * @throws IOException generada durante la ejecucion del metodo
	 */

	private void escribeArchivo(String rutaCompletaFichero, String datosAEscribir) throws IOException {

		try (FileWriter fichero = new FileWriter(rutaCompletaFichero)) {
			fichero.write(datosAEscribir);
			
		} catch (IOException e) {
			LOGGER.info("Problemas escribir archivo" + e);
		}

	}

	/**
	 * Genera el archivo log que mostrara si se genero o no un problmea en la
	 * conexion SFTP
	 * 
	 * @return ruta con los datos del servidor
	 */
	public String datosServidor() {

		confPro.createVar();
		String tipoConexion = "Password";

		if (confPro.isStrictHKC() == true) {
			tipoConexion = "ID_RSA";
		}
		return "Host: " + confPro.getHost() + "\nPuerto: " + confPro.getPuerto() + "\nConexión mediante: "
				+ tipoConexion;
	}

}
