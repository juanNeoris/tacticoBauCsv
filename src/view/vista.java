package view;

import java.awt.EventQueue;
import javax.swing.JFrame;
import conexion.Conexion;
import validacion.Validaficheros;
import interfaz.CsvToExcel;
import sftp.ConexionFtp;
import util.ConstantsUtil;
import util.ResourceLoader;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import org.apache.log4j.Logger;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import com.toedter.calendar.JDateChooser;
import java.awt.SystemColor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * Interfaz grafica con la que el usaurio interactua
 *
 */
public class vista extends JFrame {

	/**
	 * Serializable
	 */
	private static final long serialVersionUID = 8250188286505260470L;
	/**
	 * Declaracion de las variables que se usaran en la generacion del reporte las
	 * cuales almacenaran mensajes o result de conexiones o consultas a la BBDD
	 */

	public static final JProgressBar progressBar = new JProgressBar();
	public static final JTextField textField1 = new JTextField();
	/**
	 * Formato de la fecha para validar cargas o realizar extracciones
	 */
	public static final String FORMFEC = "ddMMyyyy";
	private static final Logger LOGGER = Logger.getLogger(vista.class.getName());

	private static final String ERROR_VPN = "No se pudo establecer la conexion, valide su VPN";
	private JTextField textField;
	private JDateChooser dateChooser = new JDateChooser();
	Date date = new Date();
	private Conexion conection = new Conexion();
	/**
	 * 
	 * variables que almacenan result´s de consultas a la BBDD
	 */
	String dolphing = null;
	String victoria = null;

	/**
	 * se instancia la conexion SFTP que realizara la tranferencia y carga de los
	 * archivos RTRA
	 */
	private ConexionFtp cone = new ConexionFtp();

	/**
	 * realiza la ejecucion del java cargara la interfaz con sus elementos
	 * 
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					@SuppressWarnings("unused")
					vista frame = new vista();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create la interfaz grafica con la que va a interactuar el usuario.
	 * 
	 * @throws IOException
	 * 
	 */
	public vista() throws IOException {
		JFrame f = new JFrame("Táctico BAU ");

		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {

				DOMConfigurator.configure(System.getenv("DIR_PROG") + ConstantsUtil.LOG_NAME);

				SimpleDateFormat sdf = new SimpleDateFormat(FORMFEC);
				String date = sdf.format(dateChooser.getDate().getTime());
				/**
				 * Se valida la conexion a la BBDD se puede generar errores por no estar
				 * coenectado a la VPN
				 */
				try {
					textField1.setText("Validando su conexón espere...");
					textField1.update(textField1.getGraphics());
					esperar();
					conection.conecGBO();
					victoria = conection.getCargaVictoria();
					dolphing = conection.getCargaDolphing();
					conection.disconect();
					/**
					 * Erro atrapado en caso de conexion no establecida con VPN
					 */
				} catch (Exception e2) {
					textField1.setText(ERROR_VPN);
					textField1.update(textField1.getGraphics());
					esperar();
				}
				/**
				 * valida las cargas en cuanto se ejecuta el jar de no tener informacion
				 * realizara las validacion de los fichero y despues su carga
				 */
				if (victoria.equals(ConstantsUtil.NOCARGA) || dolphing.equals(ConstantsUtil.NOCARGA)
						|| !date.trim().equals(victoria.trim()) || !date.trim().equals(dolphing.trim())) {
					String res = Validaficheros.validaFicherosDolphinVictoria(dateChooser.getDate().getTime());
					textField1.setText(res);
					textField1.update(textField1.getGraphics());
					esperar();
					cone.tranferirArchivos(dateChooser.getDate().getTime());
				} else {
					try {
						/**
						 * valida la carga de los RTRA en cuanto se ejecuta el jar y lo mostrara en el
						 * textfiel
						 */
						Date victoriaCr = sdf.parse(victoria);
						String victoriafrt = ConstantsUtil.sdf2.format(victoriaCr);

						Date dolphingCr = sdf.parse(dolphing);
						String dolphingfrt = ConstantsUtil.sdf2.format(dolphingCr);

						textField1.setText("Ultima carga victoria: " + victoriafrt + " dolphing: " + dolphingfrt + "");
						textField1.update(textField1.getGraphics());

						esperar();
						textField1.setText("Insumos cargados, indique grupo.");
						textField1.update(textField1.getGraphics());

					} catch (ParseException e3) {
						e3.printStackTrace();
					}
				}
			}
		});

		
		Image img = ImageIO.read(ResourceLoader.load("santecLogo.png"));
		f.setIconImage(img);
		f.setSize(357, 338);
		f.getContentPane().setLayout(null);
		/**
		 * Boton GENERAR RTRA detona el proceso
		 */
		JButton btnNewButton_1 = new JButton("GENERAR RTRA");
		btnNewButton_1.setBackground(Color.LIGHT_GRAY);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SimpleDateFormat sdf = new SimpleDateFormat(FORMFEC);
				String date = sdf.format(dateChooser.getDate().getTime());
				progressBar.setValue(0);
				progressBar.update(progressBar.getGraphics());
				/**
				 * proceso que genera la interfaz mediante la ejecucion del query y validacion
				 * de los intrumentos
				 * 
				 */

				try {
					/**
					 * 
					 * Valida si el grupo esta vacio. de lo contrario no generar la interfaz
					 */
					if (!textField.getText().isEmpty()) {
						textField1.setText("Generando RTRA");
						textField1.update(textField1.getGraphics());
						String nombreInterfaz = date + "_" + "rtra" + "_" + textField.getText() + "-" + "TEMP" + ".csv";
						String grupo = textField.getText();
						conection.conecGBO();
						String res = conection.getConsultaMexico(grupo, nombreInterfaz, date);
						conection.getConsultaOtrosPaises(grupo, nombreInterfaz, date);
						conection.disconect();
						/**
						 * se valida si el grupo que se esta consultando tiene informacion en la BBDD de
						 * no ser asi la interfaz no se puede generar vacia
						 */

						if (res.equals("No existen registros para este grupo en la interfaz CONSULTA")) {
							textField1.setText("No se encontro el grupo");
							textField1.update(textField1.getGraphics());
							esperar();
							textField1.setText("No se puede crear interfaz vacia");
							textField1.update(textField1.getGraphics());
							esperar();
							textField1.setText("Proceso finalizado, indique grupo");
							textField1.update(textField1.getGraphics());
						} else {

							/**
							 * parte que implementara los Estilos a la primera interfaz generada csv se
							 * enviara el archivo csv y la fecha
							 */
							Date miFecha = new SimpleDateFormat(FORMFEC).parse(date);

							/**
							 * se aplican formatos a la fecha para generar el nombre de nuestra interfaz
							 */
							Calendar calendario = Calendar.getInstance();
							calendario.setTime(miFecha);
							int anio = calendario.get(Calendar.YEAR);
							int mes = calendario.get(Calendar.MONTH);
							Calendar cal = Calendar.getInstance();
							cal.setTime(new Date());
							cal.set(Calendar.MONTH, mes);
							String nameMonth = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
							String last3 = nameMonth.substring(0, 3);
							String mesnum = date.substring(2, 4);
							conection.conecGBO();
							String Empresa = conection.getNombreGrupo(grupo, date);
							conection.disconect();
							String directoryName = System.getProperty("user.dir");

							String[] interfazExle = nombreInterfaz.split("\\-");
							String part1 = interfazExle[0];
							/**
							 * se genera el nombre de la interfaz final que contendra la informacion ya con
							 * los estilos requeridos por el Usuario
							 */
							File directorio = new File(directoryName + "\\" + grupo + "-" + Empresa + "\\" + anio + "\\"
									+ mesnum + " ) " + last3 + "\\" + part1 + ".xls");
							/**
							 * Se valida si el directorio existe para depositar el fichero de no ser asi lo
							 * genera y de posita en la ruta armada
							 */
							if (!directorio.exists()) {
								progressBar.setValue(0);
								CsvToExcel.csvToExcel(nombreInterfaz, grupo, date);
								String directoryNames = System.getProperty("user.dir");
								File fichero = new File(directoryNames + "\\" + nombreInterfaz);
								fichero.delete();
								textField1.setText("RTRA Generado con éxito.");
								textField1.update(textField1.getGraphics());
								/**
								 * se valida si el rtra ya existe y se vuelva a dar clic en generar RTRA por
								 * algun error
								 */
							} else if (directorio.exists()) {
								textField1.setText("RTRA ya existe.");
								textField1.update(textField1.getGraphics());
								String directoryNames = System.getProperty("user.dir");
								File fichero = new File(directoryNames + "\\" + nombreInterfaz);
								fichero.delete();

							}

						}
						/**
						 * validacion cuando se quiere generar un reporte y el text area de la interfaz
						 * esta vacio
						 */
					} else {
						textField1.setText("No puedes dejar el campo de grupo vacio");
						textField1.update(textField1.getGraphics());
					}
					/**
					 * Excepcion generada cuando ocurrio un problema al generar la interfaz
					 */
				} catch (Exception e1) {
					esperar();
					textField1.setText("No se pudo generar la interfaz CONSULTA");
					textField1.update(textField1.getGraphics());
					LOGGER.info(e1);
					LOGGER.error("error conexion");
					LOGGER.error(e1.getMessage(), e1);
					LOGGER.error(e1.getStackTrace());
				}

			}
		});
		btnNewButton_1.setBounds(10, 234, 147, 23);
		f.getContentPane().add(btnNewButton_1);
		/**
		 * texfiel declarado con todas sus propiedades en cuestion a las dimenciones y
		 * visibilidad
		 */
		textField = new JTextField();
		textField.setBounds(132, 98, 76, 34);
		f.getContentPane().add(textField);
		EventQueue.invokeLater(() -> textField.requestFocusInWindow());
		textField.setColumns(10);
		/**
		 * lblNewLabel declarado con todas sus propiedades en cuestion a las dimenciones
		 * y visibilidad
		 */
		JLabel lblNewLabel = new JLabel("Grupo");
		lblNewLabel.setBounds(73, 98, 49, 34);
		f.getContentPane().add(lblNewLabel);
		JLabel lblNewLabel_1 = new JLabel("Fecha proceso");
		lblNewLabel_1.setBounds(34, 44, 96, 34);
		f.getContentPane().add(lblNewLabel_1);
		/**
		 * boton Salir termina con el proceso inmediatamente en cuanto lo presionan
		 */
		JButton btnSalir = new JButton("Salir");
		btnSalir.setBackground(Color.LIGHT_GRAY);
		btnSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					System.exit(ABORT);
				} catch (Exception err) {
					textField1.setText("Ocurrio un problema con la aplicación");
					textField1.update(textField1.getGraphics());
				} finally {
					System.exit(ABORT);
				}
			}
		});
		btnSalir.setBounds(214, 234, 89, 23);
		f.getContentPane().add(btnSalir);
		dateChooser = new JDateChooser();
		/**
		 * boton en el calendario que se encargara de realizar un reporceso realizando
		 * todas las validaciones como cuando se ejecuta por primera vez
		 */
		dateChooser.getCalendarButton().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				/**
				 * Evento de doble click que validara cargas de los fichero en la BBDD en dado
				 * caso de ser un reproceso
				 */
				if (e.getClickCount() == 2 && !e.isConsumed()) {
					SimpleDateFormat sdf = new SimpleDateFormat(FORMFEC);
					String date = sdf.format(dateChooser.getDate().getTime());

					DateTimeFormatter dtf = DateTimeFormatter.ofPattern(FORMFEC);
					LocalDateTime fechaSnForm = LocalDateTime.now();
					Date fechaVal = null;
					Date fechaAct = null;
					try {
						fechaVal = sdf.parse(sdf.format(dateChooser.getDate().getTime()));
						fechaAct = sdf.parse((dtf.format(fechaSnForm)));

						/**
						 * se valida que el usuario no haga un proceso de un dia siguiente al actual de
						 * ser asi mostrara el mensaje en pantalla que no es permitido
						 */
						if (fechaVal.compareTo(fechaAct) > 0) {

							textField1.setText("No puedes procesar fechas mayores a la actual");
							textField1.update(textField1.getGraphics());
						}

						/**
						 * se instancia la conexion que va a obtener la ultimas carga de los insumos
						 * Victoria y Dolphing
						 */

						conection.conecGBO();
						victoria = conection.getCargaVictoriaHistorico(date);
						dolphing = conection.getCargaDolphingHistorico(date);
						conection.disconect();

						/**
						 * se valida si tiene la ultima carga de no ser asi realiza la ejecucion de la
						 * clase SFTP que tranfiere y ejecuta las cargas de los RTRA
						 */
						if (victoria.equals(ConstantsUtil.NOCARGA) || dolphing.equals(ConstantsUtil.NOCARGA)
								|| !date.trim().equals(victoria.trim()) || !date.trim().equals(dolphing.trim())) {
							String res = Validaficheros.validaFicherosDolphinVictoria(dateChooser.getDate().getTime());
							textField1.setText(res);
							textField1.update(textField1.getGraphics());
							esperar();
							cone.tranferirArchivos(dateChooser.getDate().getTime());
						} else {

							Date victoriaCr = sdf.parse(victoria);
							String victoriafrt = ConstantsUtil.sdf2.format(victoriaCr);
							Date dolphingCr = sdf.parse(dolphing);
							String dolphingfrt = ConstantsUtil.sdf2.format(dolphingCr);
							/**
							 * se relaiza el parseo de la fecha obtenida del data chose calendar para
							 * validar su carga en la BBDD y lo muestra en el texfield
							 */
							textField1.setText(
									"Ultima carga Victoria: " + victoriafrt + " Dolphing: " + dolphingfrt + "");
							textField1.update(textField1.getGraphics());

							esperar();
							textField1.setText("Insumos reprocesados, indique grupo.");
							textField1.update(textField1.getGraphics());
						}

					} catch (Exception e2) {
						textField1.setText(ERROR_VPN);
						textField1.update(textField1.getGraphics());
						esperar();
					}
				}
			}
		});
		/**
		 * dateChooser declarado con todas sus propiedades en cuestion a las dimenciones
		 * y visibilidad
		 */
		dateChooser.setDate(date);
		dateChooser.setDateFormatString("dd/MM/yyyy");
		dateChooser.setBounds(132, 44, 96, 34);
		f.getContentPane().add(dateChooser);
		/**
		 * componentes de la interaz grafica teexfiel progressbar Estilos
		 */
		textField1.setForeground(SystemColor.desktop);
		textField1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		textField1.setEditable(false);
		textField1.setBackground(SystemColor.menu);
		textField1.setBounds(10, 165, 321, 34);
		f.getContentPane().add(textField1);
		textField1.setColumns(10);
		progressBar.setForeground(new Color(204, 0, 0));
		progressBar.setBounds(10, 143, 321, 14);
		f.getContentPane().add(progressBar);
		f.setVisible(true);
	}

	/**
	 * Pausa la ejecución durante X segundos.
	 * 
	 * @param segundos El número de segundos que se quiere esperar.
	 * @throws InterruptedException excepcion generada durante la ejecucion del
	 *                              metodo
	 */
	public static void esperar() {
		try {

			Thread.sleep(5000);
		} catch (InterruptedException e) {
			LOGGER.info(e);
		}
	}

}