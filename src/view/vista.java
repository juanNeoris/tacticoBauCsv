package view;

import java.awt.EventQueue;
import javax.swing.JFrame;

import com.jcraft.jsch.JSchException;

import com.jcraft.jsch.SftpException;

import conexion.Conexion;

import ficheros.Validaficheros;
import interfaz.CsvToExcel;
import sftp.conexionFtp;
import util.ConstantsUtil;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import com.toedter.calendar.JDateChooser;
import java.awt.Toolkit;
import java.awt.SystemColor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;

public class vista extends JFrame {

	/**
	 * 
	 */

	private static final Logger LOGGER = Logger.getLogger(vista.class.getName());

	private static final long serialVersionUID = 1L;

	private JTextField textField;

	private JDateChooser dateChooser = new JDateChooser();
	Date date = new Date();
	String username = "deupgbom";
	String host = "180.181.37.37";
	Conexion conection = new Conexion();
	String cartera = null;
	String contrapartida = null;
	String msj = "";
	public static JTextField textField_1;
	String dolphing = null;
	String victoria = null;

	String dt2 = ConstantsUtil.sdf3.format(new Date());
	String dn2 = System.getProperty("user.dir");

	private static final String ERROR_VPN = "No se pudo establecer la conexion, valide su VPN";
	public final static JProgressBar progressBar = new JProgressBar();
	public static final DecimalFormat DFORMATO = new DecimalFormat("###,###,###.##");

	conexionFtp cone = new conexionFtp();

	/**
	 * Launch the application.
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
	 * Create the frame.
	 */
	public vista() {
		JFrame f = new JFrame("Táctico BAU ");

		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {

				SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
				String date = sdf.format(dateChooser.getDate().getTime());

				try {
					conection.conecGBO();
					textField_1.setText("Validando su conexón espere...");
					textField_1.update(textField_1.getGraphics());
					Thread.sleep(5000);

					victoria = conection.getCargaVictoria();
					dolphing = conection.getCargaDolphing();

				} catch (Exception e2) {
					try {
						textField_1.setText(ERROR_VPN);
						textField_1.update(textField_1.getGraphics());
						Thread.sleep(5000);
						System.exit(ABORT);
					} catch (Exception e3) {
						e3.getMessage();
					}
				}

				if (victoria.equals("No hay ultima carga") || dolphing.equals("No hay ultima carga")) {

					try {
						String res = Validaficheros.validaFicherosDolphinVictoria(dateChooser.getDate().getTime());
						textField_1.setText(res);
						textField_1.update(textField_1.getGraphics());
						Thread.sleep(5000);
						cone.tranferirArchivos(dateChooser.getDate().getTime());

					} catch (InterruptedException | SftpException | IllegalAccessException | RuntimeException
							| JSchException | IOException e1) {
						LOGGER.info(e1);
					}

				} else if (!date.trim().equals(victoria.trim()) || !date.trim().equals(dolphing.trim())) {

					try {
						String res = Validaficheros.validaFicherosDolphinVictoria(dateChooser.getDate().getTime());
						textField_1.setText(res);
						textField_1.update(textField_1.getGraphics());
						Thread.sleep(5000);
						cone.tranferirArchivos(dateChooser.getDate().getTime());
					} catch (InterruptedException | SftpException | IllegalAccessException | RuntimeException
							| JSchException | IOException e1) {
						LOGGER.info(e1);
					}

				} else {
					try {
						// Parsea la fecha que viene de la consulta sql y la muestra en el textField_1
						Date victoriaCr = sdf.parse(victoria);
						String victoriafrt = ConstantsUtil.sdf2.format(victoriaCr);

						Date dolphingCr = sdf.parse(dolphing);
						String dolphingfrt = ConstantsUtil.sdf2.format(dolphingCr);

						try {
							textField_1.setText(
									"Ultima carga victoria: " + victoriafrt + " dolphing: " + dolphingfrt + "");
							textField_1.update(textField_1.getGraphics());
							Thread.sleep(5000);

							textField_1.setText("Insumos cargados, indique grupo.");
							textField_1.update(textField_1.getGraphics());
						} catch (InterruptedException e1) {
							LOGGER.info(e1);
						}
					} catch (ParseException e3) {
						e3.printStackTrace();
					}
				}
			}
		});
		f.setIconImage(Toolkit.getDefaultToolkit()
				.getImage("C:\\Users\\z363772\\Downloads\\bau\\tacticoBAU\\sources\\santecLogo.png"));
		f.setSize(357, 338);
		f.getContentPane().setLayout(null);

		JButton btnNewButton_1 = new JButton("GENERAR RTRA");
		btnNewButton_1.setBackground(Color.LIGHT_GRAY);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
				String date = sdf.format(dateChooser.getDate().getTime());
				progressBar.setValue(0);
				progressBar.update(progressBar.getGraphics());
				/*********************************************************
				 * Genera interfaces REC, CER y CONSULTA
				 *************************************************************/

				try {
					// Valida si el grupo esta vacio.
					if (!textField.getText().isEmpty()) {
						textField_1.setText("Generando RTRA");
						textField_1.update(textField_1.getGraphics());
						String nombreInterfaz = date + "_" + "rtra" + "_" + textField.getText() + "-" + "TEMP" + ".csv";
						String grupo = textField.getText();
						conection.conecGBO();
						String res = conection.getConsultaMexico(grupo, nombreInterfaz, date);
						String res2 = conection.getConsultaOtrosPaises(grupo, nombreInterfaz, date);

						if (res.equals("No existen registros para este grupo en la interfaz CONSULTA")) {
							textField_1.setText("No se encontro el grupo");
							textField_1.update(textField_1.getGraphics());
							Thread.sleep(3000);
							textField_1.setText("No se puede crear interfaz vacia");
							textField_1.update(textField_1.getGraphics());
							Thread.sleep(3000);
							textField_1.setText("Proceso finalizado, indique grupo");
							textField_1.update(textField_1.getGraphics());
						} else {

							Date miFecha = new SimpleDateFormat("ddMMyyyy").parse(date);

							// creo un calendario
							Calendar calendario = Calendar.getInstance();
							// establezco mi fecha
							calendario.setTime(miFecha);

							// obtener el año
							int anio = calendario.get(Calendar.YEAR);
							// obtener el mes (0-11 ::: enero es 0 y diciembre es 11)
							int mes = calendario.get(Calendar.MONTH);
							Calendar cal = Calendar.getInstance();
							cal.setTime(new Date());
							cal.set(Calendar.MONTH, mes);
							String nameMonth = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
							String last3 = nameMonth.substring(0, 3);
							String mesnum = date.substring(2, 4);

							String Empresa = conection.getNombreGrupo(grupo, date);
							String directoryName = System.getProperty("user.dir");

							String[] interfazExle = nombreInterfaz.split("\\-");
							String part1 = interfazExle[0];

							File directorio = new File(directoryName + "\\" + grupo + "-" + Empresa + "\\" + anio + "\\"
									+ mesnum + " ) " + last3 + "\\" + part1 + ".xls");

							System.out.println("file:" + directorio);

							if (!directorio.exists()) {
								progressBar.setValue(0);
								CsvToExcel.csvToExcel(nombreInterfaz, grupo, date);
								String directoryNames = System.getProperty("user.dir");
								File fichero = new File(directoryNames + "\\" + nombreInterfaz);
								fichero.delete();
								textField_1.setText("RTRA Generado con éxito.");
								textField_1.update(textField_1.getGraphics());

							} else if (directorio.exists()) {
								textField_1.setText("RTRA ya existe.");
								textField_1.update(textField_1.getGraphics());
								String directoryNames = System.getProperty("user.dir");
								File fichero = new File(directoryNames + "\\" + nombreInterfaz);
								fichero.delete();

							}

						}
					} else {
						textField_1.setText("No puedes dejar el campo de grupo vacio");
						textField_1.update(textField_1.getGraphics());
					}
				} catch (Exception e1) {

					try {
						Thread.sleep(2000);
						textField_1.setText("No se pudo generar la interfaz CONSULTA");
						textField_1.update(textField_1.getGraphics());
					} catch (InterruptedException e2) {
						LOGGER.info(e2);
						System.out.println("error :" + e2);
						System.out.println("error :" + e2.getStackTrace());
					}
					System.out.println("error :" + e1);
					LOGGER.info(e1);
					System.out.println("error :" + e1.getLocalizedMessage() + e1.getMessage() );
				}

			}
		});
		btnNewButton_1.setBounds(10, 234, 147, 23);
		f.getContentPane().add(btnNewButton_1);

		textField = new JTextField();
		textField.setBounds(132, 98, 76, 34);
		f.getContentPane().add(textField);
		EventQueue.invokeLater(() -> textField.requestFocusInWindow());
		textField.setColumns(10);

		JLabel lblNewLabel = new JLabel("Grupo");
		lblNewLabel.setBounds(73, 98, 49, 34);
		f.getContentPane().add(lblNewLabel);
		JLabel lblNewLabel_1 = new JLabel("Fecha proceso");
		lblNewLabel_1.setBounds(34, 44, 96, 34);
		f.getContentPane().add(lblNewLabel_1);

		JButton btnSalir = new JButton("Salir");
		btnSalir.setBackground(Color.LIGHT_GRAY);
		btnSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					System.exit(ABORT);
				} catch (Exception err) {
					textField_1.setText("Ocurrio un problema con la aplicación");
					textField_1.update(textField_1.getGraphics());
				} finally {
					System.exit(ABORT);
				}
			}
		});
		btnSalir.setBounds(214, 234, 89, 23);
		f.getContentPane().add(btnSalir);
		dateChooser = new JDateChooser();
		dateChooser.getCalendarButton().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// Al dar doble click sobre el datechooser se dispara el siguiente evento
				if (e.getClickCount() == 2 && !e.isConsumed()) {
					SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
					String date = sdf.format(dateChooser.getDate().getTime());

					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyy");
					LocalDateTime fechaSnForm = LocalDateTime.now();
					Date fechaVal = null;
					Date fechaAct = null;
					try {
						fechaVal = sdf.parse(sdf.format(dateChooser.getDate().getTime()));
						fechaAct = sdf.parse((dtf.format(fechaSnForm)));
					} catch (ParseException e3) {
						e3.printStackTrace();
					}

					if (fechaVal.compareTo(fechaAct) > 0) {
						// Se valida que no se procese una fecha mayor a la actual
						textField_1.setText("No puedes procesar fechas mayores a la actual");
						textField_1.update(textField_1.getGraphics());
					} else {

						/******************************************************
						 * Realiza reproceso de Dolphing y victoria
						 ******************************************************/
						try {
							conection.conecGBO();
							victoria = conection.getCargaVictoriaHistorico(date);
							dolphing = conection.getCargaDolphingHistorico(date);
						} catch (Exception e2) {
							try {
								textField_1.setText(ERROR_VPN);
								textField_1.update(textField_1.getGraphics());
								Thread.sleep(5000);
								System.exit(ABORT);
							} catch (Exception e3) {
								e3.getMessage();
							}
						}

						if (victoria.equals("No hay ultima carga") || dolphing.equals("No hay ultima carga")) {

							try {
								String res = Validaficheros.validaFicherosDolphinVictoria(dateChooser.getDate().getTime());
								textField_1.setText(res);
								textField_1.update(textField_1.getGraphics());
								Thread.sleep(5000);
								cone.tranferirArchivos(dateChooser.getDate().getTime());
							} catch (InterruptedException | SftpException | IllegalAccessException | RuntimeException
									| JSchException | IOException e1) {
								LOGGER.info(e1);
							}

						} else if (!date.trim().equals(victoria.trim()) || !date.trim().equals(dolphing.trim())) {
							try {
								String res = Validaficheros
										.validaFicherosDolphinVictoria(dateChooser.getDate().getTime());
								textField_1.setText(res);
								textField_1.update(textField_1.getGraphics());
								Thread.sleep(5000);
								cone.tranferirArchivos(dateChooser.getDate().getTime());
							} catch (InterruptedException | SftpException | IllegalAccessException | RuntimeException
									| JSchException | IOException e1) {
								LOGGER.info(e1);
							}

						} else {

							// Parsea la fecha que viene de la consulta sql y la muestra en el textField_1

							try {
								Date victoriaCr = sdf.parse(victoria);
								String victoriafrt = ConstantsUtil.sdf2.format(victoriaCr);

								Date dolphingCr = sdf.parse(dolphing);
								String dolphingfrt = ConstantsUtil.sdf2.format(dolphingCr);
								textField_1.setText(
										"Ultima carga Victoria: " + victoriafrt + " Dolphing: " + dolphingfrt + "");
								textField_1.update(textField_1.getGraphics());
								Thread.sleep(5000);

								textField_1.setText("Insumos reprocesados, indique grupo.");
								textField_1.update(textField_1.getGraphics());
							} catch (InterruptedException | ParseException e1) {

								LOGGER.info(e1);
							}

						}
					}
				}
			}
		});
		dateChooser.setDate(date);
		dateChooser.setDateFormatString("dd/MM/yyyy");
		dateChooser.setBounds(132, 44, 96, 34);
		f.getContentPane().add(dateChooser);

		textField_1 = new JTextField();
		textField_1.setForeground(SystemColor.desktop);
		textField_1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		textField_1.setEditable(false);
		textField_1.setBackground(SystemColor.menu);
		textField_1.setBounds(10, 165, 321, 34);
		f.getContentPane().add(textField_1);
		textField_1.setColumns(10);
		progressBar.setForeground(new Color(204, 0, 0));
		progressBar.setBounds(10, 143, 321, 14);
		f.getContentPane().add(progressBar);
		f.setVisible(true);
	}

}