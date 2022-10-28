package view;

import java.awt.EventQueue;
import javax.swing.JFrame;
import com.aspose.cells.Workbook;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import conexion.Conexion;
import conexion.SSHConnector;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.sl.draw.geom.Path;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import com.toedter.calendar.JDateChooser;
import java.awt.Toolkit;
import java.awt.SystemColor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;

public class vista extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String PASSWORD = "GBO#Oct22";
	private JTextField textField;

	private JDateChooser dateChooser = new JDateChooser();
	Date date = new Date();
	String username = "deupgbom";
	String host = "180.181.37.37";
	Conexion conection = new Conexion();
	String cartera = null;
	String contrapartida = null;
	String msj = "";
	private JTextField textField_1;
	String dolphing = null;
	String victoria = null;
	SimpleDateFormat sdf3 = new SimpleDateFormat("ddMMyyyy");
	String dt2 = sdf3.format(new Date());
	String dn2 = System.getProperty("user.dir");
	SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
	private static final String ERROR_VPN = "No se pudo establecer la conexion, valide su VPN";
	private final JProgressBar progressBar = new JProgressBar();
	public static final DecimalFormat DFORMATO = new DecimalFormat("###,###,###.##");

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

				/*********************************************************
				 * Carga excel Cartera Contrapartida
				 ****************************************************************/

//				try {
//					conection.conecGBO();
//					cartera = conection.getCargaCartera();
//
//					contrapartida = conection.getCargaContrapartida();
//
//				} catch (Exception e2) {
//					try {
//						textField_1.setText(ERROR_VPN);
//						textField_1.update(textField_1.getGraphics());
//						Thread.sleep(5000);
//						System.exit(ABORT);
//					} catch (Exception e3) {
//						e3.getMessage();
//					}
//				}

//				if (contrapartida.equals("No hay ultima carga") && cartera.equals("No hay ultima carga")) {
//					validaFicheros();
//					cargarCarteraContrapartida();
//				} else if (!date.trim().equals(cartera.trim()) && !date.trim().equals(contrapartida.trim())) {
//					validaFicheros();
//					cargarCarteraContrapartida();
//				} else {
//					try {
//						// Parsea la fecha que viene de la consulta sql y la muestra en el textField_1
//						Date carteraCr = sdf.parse(cartera);
//						String Carterafrt = sdf2.format(carteraCr);
//
//						Date contrapartidaCr = sdf.parse(contrapartida);
//						String contrapartidafrt = sdf2.format(contrapartidaCr);
//
//						try {
//							textField_1.setText("Ultima carga cartera: " + Carterafrt + " contrapartida: " + contrapartidafrt + "");
//							textField_1.update(textField_1.getGraphics());
//							Thread.sleep(5000);
//						} catch (InterruptedException e1) {
//							e1.printStackTrace();
//						}
//					} catch (ParseException e3) {
//						e3.printStackTrace();
//					}
//				}
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

					validaFicherosDolphinVictoria();
					cargaVictoriaDolphing();

				} else if (!date.trim().equals(victoria.trim()) || !date.trim().equals(dolphing.trim())) {

					validaFicherosDolphinVictoria();
					cargaVictoriaDolphing();

				} else {
					try {
						// Parsea la fecha que viene de la consulta sql y la muestra en el textField_1
						Date victoriaCr = sdf.parse(victoria);
						String victoriafrt = sdf2.format(victoriaCr);

						Date dolphingCr = sdf.parse(dolphing);
						String dolphingfrt = sdf2.format(dolphingCr);

						try {
							textField_1.setText(
									"Ultima carga victoria: " + victoriafrt + " dolphing: " + dolphingfrt + "");
							textField_1.update(textField_1.getGraphics());
							Thread.sleep(5000);

							textField_1.setText("Insumos cargados, indique grupo.");
							textField_1.update(textField_1.getGraphics());
						} catch (InterruptedException e1) {
							e1.printStackTrace();
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
				// #################################### Creacion de la interfaz REC
				// ####################################
//				try {
//					// Valida si el grupo esta vacio.
//					if (!textField.getText().isEmpty()) {
//						textField_1.setText("Generando interfaz REC");
//						textField_1.update(textField_1.getGraphics());
//
//						String nombreInterfaz = "consulta_REC_" + date + "_" + textField.getText() + ".csv";
//						String grupo = textField.getText();
//						conection.conecGBO();
//						String res = conection.getRec(grupo, nombreInterfaz, date);
//						
//						if (res.equals("No existen registros para este grupo en la interfaz Rec")) {
//							textField_1.setText("No se encontro el grupo");
//							textField_1.update(textField_1.getGraphics());
//							Thread.sleep(3000);
//							textField_1.setText("No se puede crear interfaz vacia");
//							textField_1.update(textField_1.getGraphics());
//							Thread.sleep(3000);
//						}
//					}
//				} catch (Exception e1) {
//					e1.printStackTrace();
//
//					textField_1.setText("No se pudo generar la interfaz REC");
//					textField_1.update(textField_1.getGraphics());
//				}
				// #################################### creacion de la interfaz CER
				// ####################################
//				try {
//					// Valida si el grupo esta vacio.
//					if (!textField.getText().isEmpty()) {
//						textField_1.setText("Generando interfaz CER");
//						textField_1.update(textField_1.getGraphics());
//						String nombreInterfaz = "consulta_CER_" + date + "_" + textField.getText() + ".csv";
//						String grupo = textField.getText();
//						conection.conecGBO();
//						String res = conection.getCer(grupo, nombreInterfaz, date);
//						
//						if (res.equals("No existen registros para este grupo en la interfaz CER")) {
//							textField_1.setText("No se encontro el grupo");
//							textField_1.update(textField_1.getGraphics());
//							Thread.sleep(3000);
//							textField_1.setText("No se puede crear interfaz vacia");
//							textField_1.update(textField_1.getGraphics());
//							Thread.sleep(3000);
//						}	
//					}
//				} catch (Exception e1) {
//					e1.printStackTrace();
//					textField_1.setText("No se pudo generar la interfaz CER");
//					textField_1.update(textField_1.getGraphics());
//				}
				// #################################### creacion de la interfaz CONSULTA
				// ####################################
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
								csvToExcel(nombreInterfaz, grupo, date);
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
						e2.printStackTrace();
					}
					e1.printStackTrace();
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
					/*********************************************************
					 * Carga excel Cartera Contrapartida
					 ****************************************************************/
//					try {
//						conection.conecGBO();
//						cartera = conection.getCargaCarteraHistorico(date);
//						contrapartida = conection.getCargaContrapartidaHistorico(date);
//					} catch (Exception e2) {
//						try {
//							textField_1.setText(ERROR_VPN);
//							textField_1.update(textField_1.getGraphics());
//							Thread.sleep(5000);
//							System.exit(ABORT);
//						} catch (Exception e3) {
//							e3.getMessage();
//						}
//						
//					}

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
//						//Si se procesa solo si la fecha es igual o menor a la actual
//						textField_1.setText("Procesando insumos locales de retrabajo");
//						textField_1.update(textField_1.getGraphics());
//						if (contrapartida.equals("No hay ultima carga") && cartera.equals("No hay ultima carga")) {
//							//validaFicheros();
//							//cargarCarteraContrapartidaHistorico();
//						} else if (!date.trim().equals(cartera.trim()) && !date.trim().equals(contrapartida.trim())) {
//							//validaFicheros();
//						//	cargarCarteraContrapartidaHistorico();
//						} else {
//							try {
//								Date carteraCr = sdf.parse(cartera);
//								String Carterafrt = sdf2.format(carteraCr);
//								Date contrapartidaCr = sdf.parse(contrapartida);
//								String contrapartidafrt = sdf2.format(contrapartidaCr);
//								try {
//									textField_1.setText("Ultima carga cartera: " + Carterafrt + "  Contrapartida: "
//											+ contrapartidafrt + "");
//									textField_1.update(textField_1.getGraphics());
//									Thread.sleep(5000);
//								} catch (InterruptedException e1) {
//									e1.printStackTrace();
//								}
//							} catch (ParseException e3) {
//								e3.printStackTrace();
//							}
//						}
//						
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
							validaFicherosDolphinVictoria();

							cargaVictoriaDolphingHistorico();
						} else if (!date.trim().equals(victoria.trim()) || !date.trim().equals(dolphing.trim())) {
							validaFicherosDolphinVictoria();

							cargaVictoriaDolphingHistorico();
						} else {
							try {
								// Parsea la fecha que viene de la consulta sql y la muestra en el textField_1
								Date victoriaCr = sdf.parse(victoria);
								String victoriafrt = sdf2.format(victoriaCr);

								Date dolphingCr = sdf.parse(dolphing);
								String dolphingfrt = sdf2.format(dolphingCr);

								try {
									textField_1.setText(
											"Ultima carga Victoria: " + victoriafrt + " Dolphing: " + dolphingfrt + "");
									textField_1.update(textField_1.getGraphics());
									Thread.sleep(5000);

									textField_1.setText("Insumos reprocesados, indique grupo.");
									textField_1.update(textField_1.getGraphics());
								} catch (InterruptedException e1) {
									e1.printStackTrace();
								}
							} catch (ParseException e3) {
								e3.printStackTrace();
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

	public int calcularAvance(int cantidadRegistros, int posicion) {
		Double indice = Double.valueOf(posicion);
		Double total = Double.valueOf(cantidadRegistros - 1);

		double avance = (indice / total) * 100.0;
		int progreso = (int) Math.round(avance);

		return progreso;
	}

	public void csvToExcel(String nombreInterfaz, String grupo, String date) {
		ArrayList arList = null;
		ArrayList al = null;
		String fName = nombreInterfaz;
		String csv = nombreInterfaz;
		String thisLine;
		int count = 0;
		String valor = "null";

		try {

			FileInputStream fis = new FileInputStream(fName);
			DataInputStream myInput = new DataInputStream(fis);
			int i = 0;
			arList = new ArrayList();

			while ((thisLine = myInput.readLine()) != null) {
				al = new ArrayList();
				String strar[] = thisLine.split("\\|");
				for (int j = 0; j < strar.length; j++) {
					al.add(strar[j]);
				}
				arList.add(al);
				System.out.println();
				i++;
			}
			fis.close();
			myInput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {

			HSSFWorkbook hwb = new HSSFWorkbook();
			HSSFSheet sheet = hwb.createSheet("Reporte");
			CreationHelper createHelper = hwb.getCreationHelper();

			CellStyle cellStyle = hwb.createCellStyle();
			CellStyle cellStyle1 = hwb.createCellStyle();
			CellStyle cellStyle2 = hwb.createCellStyle();
			CellStyle cellStyle3 = hwb.createCellStyle();
			CellStyle cellStyle4 = hwb.createCellStyle();
			CellStyle cellStyle5 = hwb.createCellStyle();
			CellStyle cellStyle7 = hwb.createCellStyle();
			CellStyle cellStylefecha = hwb.createCellStyle();
			cellStylefecha.setDataFormat(createHelper.createDataFormat().getFormat("dd-mmm-aa"));
			CellStyle my_style_1 = hwb.createCellStyle();
			CellStyle my_style_2 = hwb.createCellStyle();
			CellStyle my_style_3 = hwb.createCellStyle();
			CellStyle my_style_4 = hwb.createCellStyle();
			CellStyle my_style_5 = hwb.createCellStyle();
			CellStyle my_style_6 = hwb.createCellStyle();

			HSSFFont cellFont = hwb.createFont();

			HSSFFont font = hwb.createFont();
			HSSFFont fontR = hwb.createFont();
			HSSFFont fontW = hwb.createFont();
			DataFormat fmt = hwb.createDataFormat();

			cellStyle5.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

			sheet.setDefaultColumnStyle(0, cellStyle5);
			sheet.setDefaultColumnStyle(1, cellStyle5);
			sheet.setDefaultColumnStyle(3, cellStyle5);
			sheet.setDefaultColumnStyle(5, cellStyle5);
			sheet.setDefaultColumnStyle(6, cellStyle5);
			sheet.setDefaultColumnStyle(7, cellStyle5);
			sheet.setDefaultColumnStyle(8, cellStyle5);
			sheet.setDefaultColumnStyle(9, cellStyle5);
			sheet.setDefaultColumnStyle(10, cellStyle5);
			sheet.setDefaultColumnStyle(11, cellStyle5);
			sheet.setDefaultColumnStyle(13, cellStyle5);
			sheet.setDefaultColumnStyle(14, cellStyle5);
			sheet.setDefaultColumnStyle(15, cellStyle5);
			sheet.setDefaultColumnStyle(16, cellStyle5);
			for (int k = 0; k < arList.size(); k++) {
				ArrayList ardata = (ArrayList) arList.get(k);
				HSSFRow row = sheet.createRow((short) 0 + k);

				for (int p = 0; p < ardata.size(); p++) {
					HSSFCell cell = row.createCell((short) p);
					String data = ardata.get(p).toString();

					if (ardata.toString().contains("MEXICO - ") || ardata.toString().contains(" - TARJETA DE CREDITO")
							|| ardata.toString().contains(" - LINEAS") || ardata.toString().contains(" - GARANTIAS")
							|| ardata.toString().contains(" - FINANCIAMIENTO")
							|| ardata.toString().contains(" - FACTORING") || ardata.toString().contains(" - DESCUENTOS")
							|| ardata.toString().contains(" - DERIVADOS") || ardata.toString().contains(" - CREDITOS")
							|| ardata.toString().contains(" - CONFIRMING") || ardata.toString().contains(" - BONOS")
							|| ardata.toString().contains(" - AVAL") || ardata.toString().contains(" - OVERDRAFTS")
							|| ardata.toString().contains(" - LEASING")) {

						cellStyle7.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.LEFT);
						cellStyle7.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
						cellFont.setColor(HSSFColorPredefined.BLACK.getIndex());
						cellStyle7.setFont(cellFont);
						cellFont.setBold((true));
						cellStyle7.setFont(cellFont);
						row.setHeightInPoints(20);
						cell.setCellStyle(cellStyle7);
						cell.setCellValue(data);
					} else if (data.equals("CPTYPARENT") || data.equals("CPTYPARENTRATING")
							|| data.equals("CPTYPARENTNAME") || data.equals("DEALSTAMP")
							|| data.equals("INSTRUMENTNAME") || data.equals("VALUEDATE") || data.equals("MATURITYDATE")
							|| data.equals("CURRENCY") || data.equals("NOMINALVALUECUR") || data.equals("CER")
							|| data.equals("NOMINALVALUE") || data.equals("ONEOFF") || data.equals("CPTYNAME")
							|| data.equals("FOLDERCOUNTRYNAME") || data.equals("CPTYCOUNTRY")
							|| data.equals("CPTYPARENTCOUNTRY") || data.equals("FOLDERCOUNTRY")) {

						cellStyle1.setFillForegroundColor(IndexedColors.RED1.getIndex());
						cellStyle1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						cellStyle1.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER_SELECTION);
						cellStyle1.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
						font.setColor(HSSFColorPredefined.WHITE.getIndex());
						font.setBold((true));
						cellStyle1.setFont(font);
						row.setHeightInPoints(20);
						cell.setCellStyle(cellStyle1);
						cell.setCellValue(data);

					} else if (data.startsWith("\"")) {
						data = data.replaceAll("\"", "");
						if (ardata.toString().contains("GARANTIA")) {

							if (p == 0 || p == 1 || p == 3 || p == 7 || p == 11 || p == 13 || p == 14 || p == 15
									|| p == 16) {

								cellStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
								cellStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);

								fontR.setColor(HSSFColorPredefined.DARK_RED.getIndex());
								cellStyle.setFont(fontR);
								cell.setCellStyle(cellStyle);
								cell.setCellValue(data);

							} else if (p == 5 || p == 6) {

								if (!data.trim().equals(valor)) {
									SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MMM-yy");
									Date date2 = formatter2.parse(data);
									System.out.println(date2);
									my_style_1.setDataFormat(HSSFDataFormat.getBuiltinFormat("d-mmm-yy"));
									my_style_1.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
									my_style_1
											.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
									fontR.setColor(HSSFColorPredefined.DARK_RED.getIndex());
									my_style_1.setFont(fontR);
									cell.setCellStyle(my_style_1);
									cell.setCellValue(date2);

								} else {
									data = data.replaceAll("null", "-");
									cell.setCellValue(data);
								}
							} else if (p == 8 || p == 9 || p == 10) {

								double d = DecimalFormat.getNumberInstance().parse(data.trim()).doubleValue();
								System.out.println(d);
								my_style_4.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
								my_style_4.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.RIGHT);
								my_style_4.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
								fontR.setColor(HSSFColorPredefined.DARK_RED.getIndex());
								my_style_4.setFont(fontR);
								cell.setCellStyle(my_style_4);
								cell.setCellValue(d);

							} else if (p == 2 || p == 4 || p == 12) {
								cellStyle3.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);

								cellStyle3.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.LEFT);
								fontR.setColor(HSSFColorPredefined.DARK_RED.getIndex());
								cellStyle3.setFont(fontR);
								cell.setCellStyle(cellStyle3);
								cell.setCellValue(data);
							}
						}
						if (p == 5 || p == 6) {

							if (!data.trim().equals(valor) && !data.isEmpty()) {
								SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MMM-yy");
								Date date2 = formatter2.parse(data);
								my_style_2.setDataFormat(HSSFDataFormat.getBuiltinFormat("d-mmm-yy"));
								my_style_2.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
								my_style_2.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
								cell.setCellStyle(my_style_2);
								cell.setCellValue(date2);
							}

						} else if (p == 8 || p == 9 || p == 10) {

							double d = DecimalFormat.getNumberInstance().parse(data.trim()).doubleValue();
							System.out.println(d);
							my_style_3.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
							my_style_3.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.RIGHT);
							my_style_3.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
							cell.setCellStyle(my_style_3);
							cell.setCellValue(d);

						} else {
							cell.setCellValue(data);
						}

					} else {
						data = data.replaceAll("\"", "");

						if (ardata.toString().contains("GARANTIA")) {

							if (p == 0 || p == 1 || p == 3 || p == 7 || p == 11 || p == 13 || p == 14 || p == 15
									|| p == 16) {

								cellStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
								cellStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);

								fontR.setColor(HSSFColorPredefined.DARK_RED.getIndex());
								cellStyle.setFont(fontR);
								cell.setCellStyle(cellStyle);
								cell.setCellValue(data);

							} else if (p == 5 || p == 6) {

								if (!data.trim().equals(valor)) {
									SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MMM-yy");
									Date date2 = formatter2.parse(data);
									my_style_1.setDataFormat(HSSFDataFormat.getBuiltinFormat("d-mmm-yy"));
									my_style_1.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
									my_style_1
											.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
									fontR.setColor(HSSFColorPredefined.DARK_RED.getIndex());
									my_style_1.setFont(fontR);
									cell.setCellStyle(my_style_1);
									cell.setCellValue(date2);

								} else {
									data = data.replaceAll("null", "-");
									cell.setCellValue(data);
								}
							} else if (p == 8 || p == 9 || p == 10) {

								double d = DecimalFormat.getNumberInstance().parse(data.trim()).doubleValue();
								System.out.println(d);
								my_style_4.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
								my_style_4.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.RIGHT);
								my_style_4.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
								fontR.setColor(HSSFColorPredefined.DARK_RED.getIndex());
								my_style_4.setFont(fontR);
								cell.setCellStyle(my_style_4);
								cell.setCellValue(d);

							} else if (p == 2 || p == 4 || p == 12) {
								cellStyle3.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.LEFT);
								cellStyle3.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);

								fontR.setColor(HSSFColorPredefined.DARK_RED.getIndex());
								cellStyle3.setFont(fontR);
								cell.setCellStyle(cellStyle3);
								cell.setCellValue(data);

							}
						} else {

							if (p == 5 || p == 6) {
								if (!data.trim().equals(valor) && !data.isEmpty()) {
									SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MMM-yy");
									Date date2 = formatter2.parse(data);
									my_style_2.setDataFormat(HSSFDataFormat.getBuiltinFormat("d-mmm-yy"));
									my_style_2.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
									my_style_2
											.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
									cell.setCellStyle(my_style_2);
									cell.setCellValue(date2);
								}
							} else if (p == 8 || p == 9 || p == 10) {

								double d = DecimalFormat.getNumberInstance().parse(data.trim()).doubleValue();
								System.out.println(d);
								my_style_3.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
								my_style_3.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.RIGHT);
								my_style_3.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
								cell.setCellStyle(my_style_3);
								cell.setCellValue(d);

							} else {
								cell.setCellValue(data);
							}
						}
					}
					if (ardata.toString().contains("TOTAL GENERAL")) {

						if (p == 8 || p == 9 || p == 10) {

							double d = DecimalFormat.getNumberInstance().parse(data.trim()).doubleValue();
							System.out.println(d);
							my_style_6.setFillForegroundColor(IndexedColors.BLACK.getIndex());
							my_style_6.setFillPattern(FillPatternType.SOLID_FOREGROUND);
							my_style_6.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
							my_style_6.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
							my_style_6.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
							fontW.setColor(HSSFColorPredefined.WHITE.getIndex());
							fontW.setBold((true));
							my_style_6.setFont(fontW);
							cell.setCellStyle(my_style_6);
							cell.setCellValue(d);

						} else {
							cellStyle2.setFillForegroundColor(IndexedColors.BLACK.getIndex());
							cellStyle2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
							cellStyle2.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
							cellStyle2.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);

							font.setColor(HSSFColorPredefined.WHITE.getIndex());
							cellStyle2.setFont(font);
							row.setHeightInPoints(20);
							cell.setCellStyle(cellStyle2);
							cell.setCellValue(data);
						}

					} else if (ardata.toString().contains("TOTAL TARJETAS DE CREDITO")
							|| ardata.toString().contains("TOTAL LINEAS NO COMPROMETIDAS")
							|| ardata.toString().contains("TOTAL LINEAS COMPROMETIDAS")
							|| ardata.toString().contains("TOTAL GARANTIAS")
							|| ardata.toString().contains("TOTAL FINANCIAMIENTO IMP/EXP")
							|| ardata.toString().contains("TOTAL FINANCIAMIENTO COMEX")
							|| ardata.toString().contains("TOTAL FACTORING")
							|| ardata.toString().contains("TOTAL DESCUENTOS")
							|| ardata.toString().contains("TOTAL DERIVADOS")
							|| ardata.toString().contains("TOTAL CREDITOS SINDICADOS")
							|| ardata.toString().contains("TOTAL CREDITOS DOCUMENTARIOS")
							|| ardata.toString().contains("TOTAL CONFIRMING")
							|| ardata.toString().contains("TOTAL BONOS") || ardata.toString().contains("TOTAL AVALES")
							|| ardata.toString().contains("TOTAL LEASING")
							|| ardata.toString().contains("TOTAL OVERDRAFTS")) {

						if (p == 8 || p == 9 || p == 10) {

							double d = DecimalFormat.getNumberInstance().parse(data.trim()).doubleValue();
							System.out.println(d);
							my_style_5.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
							my_style_5.setFillPattern(FillPatternType.SOLID_FOREGROUND);
							my_style_5.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
							my_style_5.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.RIGHT);
							my_style_5.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
							fontW.setColor(HSSFColorPredefined.WHITE.getIndex());
							fontW.setBold((true));
							my_style_5.setFont(fontW);
							cell.setCellStyle(my_style_5);
							cell.setCellValue(d);

						} else {

							cellStyle4.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
							cellStyle4.setFillPattern(FillPatternType.SOLID_FOREGROUND);
							cellStyle4.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
							cellStyle4.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
							fontW.setColor(HSSFColorPredefined.WHITE.getIndex());
							fontW.setBold((true));
							cellStyle4.setFont(fontW);
							row.setHeightInPoints(15);
							cell.setCellStyle(cellStyle4);
							cell.setCellValue(data);

						}
					}
				}
				int progreso = this.calcularAvance(arList.size(), k);
				progressBar.setValue(progreso);
				progressBar.update(progressBar.getGraphics());

			}

			sheet.setColumnWidth(0, 25 * 256);
			sheet.setColumnWidth(1, 25 * 256);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4);
			sheet.autoSizeColumn(5);
			sheet.autoSizeColumn(6);
			sheet.autoSizeColumn(7);
			sheet.autoSizeColumn(8);
			sheet.autoSizeColumn(9);
			sheet.autoSizeColumn(10);
			sheet.autoSizeColumn(11);
			sheet.autoSizeColumn(12);
			sheet.autoSizeColumn(13);
			sheet.autoSizeColumn(14);
			sheet.autoSizeColumn(15);
			sheet.autoSizeColumn(16);
			// 0 , 1 ,3--centrado 5-11--centrado 13-16--centrado

			String[] interfazExle = csv.split("\\-");

			String part1 = interfazExle[0];

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

			File directorio = new File(
					directoryName + "\\" + grupo + "-" + Empresa + "\\" + anio + "\\" + mesnum + " ) " + last3);
			if (!directorio.exists()) {
				if (directorio.mkdirs()) {
					FileOutputStream fileOut = new FileOutputStream(directorio + "\\" + part1 + ".xls");

					hwb.write(fileOut);
					fileOut.close();
					System.out.println("Directorio creado");
				} else {
					System.out.println("Error al crear directorio");
				}
			}
			FileOutputStream fileOut = new FileOutputStream(directorio + "\\" + part1 + ".xls");

			hwb.write(fileOut);
			fileOut.close();

			System.out.println("Your excel file has been generated");

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
//	public void cargarCarteraContrapartida() {
//		textField_1.setText("Sin carga, cargando cartera y contrapartida, espere...");
//		textField_1.update(textField_1.getGraphics());
//
//		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
//		String date = sdf.format(dateChooser.getDate().getTime());
//
//		Workbook workbook;
//		String directoryName = System.getProperty("user.dir");
//		JSch jsch = new JSch();
//
//		try {
//
//			workbook = new Workbook(directoryName + "\\" + date + " Rating de Contrapartida.xlsx");
//			workbook.save(directoryName + "\\" + date + "_Rating_de_Contrapartida.csv");
//			workbook = new Workbook(directoryName + "\\" + date + " Inf Cartera.xlsx");
//			workbook.save(directoryName + "\\" + date + "_Inf_Cartera.csv");
//
//			Session session = jsch.getSession(username, host, 22);
//			session.setPassword("GBO#Oct22");
//
//			Properties config = new Properties();
//			config.put("StrictHostKeyChecking", "no");
//			session.setConfig(config);
//
//			session.connect(10000);
//
//			Channel channel = session.openChannel("sftp");
//			channel.connect(50000);
//
//			// se realiza put al servidor de GBO
//			ChannelSftp sftpChannel = (ChannelSftp) channel;
//			sftpChannel.put(date + "_Inf_Cartera.csv", "/planPGTMEX/procesos/RISK/interfaces");
//			sftpChannel.put(date + "_Rating_de_Contrapartida.csv", "/planPGTMEX/procesos/RISK/interfaces");
//			sftpChannel.exit();
//			sftpChannel.disconnect();
//
//			SSHConnector sshConnector = new SSHConnector();
//			sshConnector.connect(username, PASSWORD, host, 22);
//			
//			SimpleDateFormat sdfRtraLogExcel = new SimpleDateFormat("MMMdd", Locale.ENGLISH);
//			String dateShelRTRALogExcel =  sdfRtraLogExcel.format(dateChooser.getDate().getTime());
//			 // se ejecuta el shell que realiza la carga de las interfaces a la BBDD de GBO
//			String result = sshConnector.executeCommand("cd /planPGTMEX/procesos/RISK/;./carga_csv_motor.sh " + date + "");
//			String resultLogsexcels = sshConnector.executeCommand("cd /planPGTMEX/procesos/RISK/salidas/"+dateShelRTRALogExcel+"/carga_csv_motor.sh; ls -ltr carga_csv_motor.sh.out");
//			
//			/*Se borran las interfaces de la ruta: /planPGTMEX/procesos/RISK/salidas/
//			 *  una vez que han sido cargadas de manea exitosa
//			 * */
//			
//			sshConnector.executeCommand("cd /planPGTMEX/procesos/RISK/interfaces; rm "+date + "_Rating_de_Contrapartida.csv");
//			sshConnector.executeCommand("cd /planPGTMEX/procesos/RISK/interfaces; rm "+date + "_Inf_Cartera.csv");
//		
//			
//			sshConnector.disconnect();
//
//			if (resultLogsexcels.isEmpty()) {
//				textField_1.setText("Ocurrio un problema al cargar cartera");
//				textField_1.update(textField_1.getGraphics());
//				Thread.sleep(3000);
//				textField_1.setText("Ocurrio un problema al cargar contrapartida");
//				textField_1.update(textField_1.getGraphics());
//				Thread.sleep(3000);
//				System.exit(ABORT);
//			}
//			textField_1.setText("Carga completada de cartera y contrapartida.");
//			textField_1.update(textField_1.getGraphics());
//			Thread.sleep(3000);
//		} catch (Exception e1) {
//			try {
//				textField_1.setText("Problemas al convertir archivos de cartera y contrapartida.");
//				textField_1.update(textField_1.getGraphics());
//				Thread.sleep(4000);
//				System.exit(ERROR);
//			} catch (InterruptedException e2) {
//				e1.printStackTrace();
//			}
//		}
//	}

//	public void cargarCarteraContrapartidaHistorico() {
//		textField_1.setText("Sin carga, cargando cartera y contrapartida, espere...");
//		textField_1.update(textField_1.getGraphics());
//
//		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
//		String date = sdf.format(dateChooser.getDate().getTime());
//
//		Workbook workbook;
//		String directoryName = System.getProperty("user.dir");
//		JSch jsch = new JSch();
//
//		try {
//			workbook = new Workbook(directoryName + "\\" + date + " Rating de Contrapartida.xlsx");
//			workbook.save(directoryName + "\\" + date + "_Rating_de_Contrapartida.csv");
//			workbook = new Workbook(directoryName + "\\" + date + " Inf Cartera.xlsx");
//			workbook.save(directoryName + "\\" + date + "_Inf_Cartera.csv");
//
//			Session session = jsch.getSession(username, host, 22);
//			session.setPassword("GBO#Oct22");
//
//			Properties config = new Properties();
//			config.put("StrictHostKeyChecking", "no");
//			session.setConfig(config);
//
//			session.connect(10000);
//
//			Channel channel = session.openChannel("sftp");
//			channel.connect(50000);
//
//			// se realiza put al servidor de GBO
//			ChannelSftp sftpChannel = (ChannelSftp) channel;
//			sftpChannel.put(date + "_Inf_Cartera.csv", "/planPGTMEX/procesos/RISK/interfaces");
//			sftpChannel.put(date + "_Rating_de_Contrapartida.csv", "/planPGTMEX/procesos/RISK/interfaces");
//			sftpChannel.exit();
//			sftpChannel.disconnect();
//
//			SSHConnector sshConnector = new SSHConnector();
//			sshConnector.connect(username, PASSWORD, host, 22);
//			
//			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMMdd", Locale.ENGLISH);
//			String dateShelRTRALogExcel =  dtf.format(LocalDateTime.now());
//
//			 // se ejecuta el shell que realiza la carga de las interfaces a la BBDD de GBO
//			String result = sshConnector.executeCommand("cd /planPGTMEX/procesos/RISK/;./carga_csv_motor.sh " + date + "");
//			String resultLogsexcels = sshConnector.executeCommand("cd /planPGTMEX/procesos/RISK/salidas/"+dateShelRTRALogExcel+"/carga_csv_motor.sh; ls -ltr carga_csv_motor.sh.out");
//			
//			/*Se borran las interfaces de la ruta: /planPGTMEX/procesos/RISK/salidas/
//			 *  una vez que han sido cargadas de manea exitosa
//			 * */
//			
//			sshConnector.executeCommand("cd /planPGTMEX/procesos/RISK/interfaces; rm "+date + "_Rating_de_Contrapartida.csv");
//			sshConnector.executeCommand("cd /planPGTMEX/procesos/RISK/interfaces; rm "+date + "_Inf_Cartera.csv");
//		
//			
//			sshConnector.disconnect();
//           	
//           
//           if (resultLogsexcels.isEmpty()) {
//				textField_1.setText("Ocurrio un problema al cargar cartera");
//				textField_1.update(textField_1.getGraphics());
//				Thread.sleep(3000);
//				textField_1.setText("Ocurrio un problema al cargar contrapartida");
//				textField_1.update(textField_1.getGraphics());
//				Thread.sleep(3000);
//				System.exit(ABORT);
//			}
//			textField_1.setText("Carga completada de cartera y contrapartida.");
//			textField_1.update(textField_1.getGraphics());
//			Thread.sleep(3000);
//		} catch (Exception e1) {
//			try {
//				textField_1.setText("Problemas al convertir archivos en contrapartida y cartera");
//				textField_1.update(textField_1.getGraphics());
//				Thread.sleep(4000);
//				System.exit(ABORT);
//			} catch (InterruptedException e2) {
//				e2.printStackTrace();
//			}
//		}
//	}

//	public void validaFicheros() {
//		SimpleDateFormat sdfx = new SimpleDateFormat("ddMMyyyy");
//		String fechaCal = sdfx.format(dateChooser.getDate().getTime());
//		
//		File filContrapartida = new File(fechaCal + " Rating de Contrapartida.xlsx");
//		File filCartera = new File(fechaCal + " Inf Cartera.xlsx");
//		
//		if (!filContrapartida.exists() || !filCartera.exists()) {
//			try {
//				textField_1.setText("Sin excel de cartera o contrapartida, coloca tus ficheros.");
//				textField_1.update(textField_1.getGraphics());
//				Thread.sleep(5000);
//				System.exit(ABORT);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		} else {
//			try {
//				textField_1.setText("los insumos existen, cargando...");
//				textField_1.update(textField_1.getGraphics());
//				Thread.sleep(5000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//	}

	public void validaFicherosDolphinVictoria() {
		SimpleDateFormat sdfx = new SimpleDateFormat("yyyyMMdd");
		String fechaCal = sdfx.format(dateChooser.getDate().getTime());

		File filDolphin = new File("\\\\mx2ct1hnascifnfsevs1.mx.corp\\ExtraccionesMIR\\rtra\\"
				+ "rtra-cream-ges-dolphin-europa_mexico_" + fechaCal + ".txt");
		File filVictoria = new File("\\\\mx2ct1hnascifnfsevs1.mx.corp\\ExtraccionesMIR\\rtra\\"
				+ "rtra-cream-ges-victoria-europa_mexico_" + fechaCal + ".txt");

		if (!filDolphin.exists() || !filVictoria.exists()) {
			try {
				textField_1.setText("Sin rtra Victoria o Dolphing, coloca tus ficheros.");
				textField_1.update(textField_1.getGraphics());
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			try {
				textField_1.setText("los insumos existen, cargando...");
				textField_1.update(textField_1.getGraphics());
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void cargaVictoriaDolphing() {

		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
		String date = sdf.format(dateChooser.getDate().getTime());
		SimpleDateFormat sdfRtra = new SimpleDateFormat("yyyyMMdd");
		String dateShelRTRA = sdfRtra.format(dateChooser.getDate().getTime());
		SimpleDateFormat sdfRtraLog = new SimpleDateFormat("MMMdd", Locale.ENGLISH);
		String dateShelRTRALog = sdfRtraLog.format(dateChooser.getDate().getTime());
		JSch jsch = new JSch();
		/*********************************************************
		 * Carga rtra victoria dolphing
		 *********************************************************************/

		/*
		 * conexion por SSH para la ejecucion del shell que realizara la carga de los
		 * archivos rtra victoria y dolphing.
		 */
		textField_1.setText("Sin carga, cargando victoria y dolphing, espere...");
		textField_1.update(textField_1.getGraphics());

		try {
			/*
			 * establecemos las variables de conexion para tranferir los RTRA una vez que
			 * validemos que existen en la ruta
			 */

			Session session = jsch.getSession(username, host, 22);
			session.setPassword("GBO#Oct22");

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
			sshConnector.connect(username, PASSWORD, host, 22);

			String resultVic = sshConnector.executeCommand(
					"cd /planPGTMEX/procesos/RISK/interfaces; ls -ltr rtra-cream-ges-victoria-europa_mexico_"
							+ dateShelRTRA + ".txt ");
			String resultDolp = sshConnector.executeCommand(
					"cd /planPGTMEX/procesos/RISK/interfaces; ls -ltr rtra-cream-ges-dolphin-europa_mexico_"
							+ dateShelRTRA + ".txt ");

			if (resultVic.isEmpty() && resultDolp.isEmpty()) {
				textField_1.setText("No existen tus ficheros RTRA's en el FileShare");
				textField_1.update(textField_1.getGraphics());
				Thread.sleep(3000);

			} else {
				@SuppressWarnings("unused")
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
				textField_1.setText("Carga parcial archivo con registros erroneos ");
				textField_1.update(textField_1.getGraphics());

			} else if (resultLogs.isEmpty()) {
				textField_1.setText("Ocurrio un problema al cargar los RTRA's");
				textField_1.update(textField_1.getGraphics());
				Thread.sleep(4000);

			} else {
				textField_1.setText("Proceso finalizado, indique grupo");
				textField_1.update(textField_1.getGraphics());
			}

		} catch (Exception e1) {
			try {
				textField_1.setText("No se pudo realizar la carga de archivos RTRA's");
				textField_1.update(textField_1.getGraphics());
				Thread.sleep(4000);

			} catch (Exception e) {
				e1.printStackTrace();
			}

		}
	}

	public void cargaVictoriaDolphingHistorico() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMMdd", Locale.ENGLISH);
		String dateShelRTRALog = dtf.format(LocalDateTime.now());

		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
		String date = sdf.format(dateChooser.getDate().getTime());
		SimpleDateFormat sdfRtra = new SimpleDateFormat("yyyyMMdd");
		String dateShelRTRA = sdfRtra.format(dateChooser.getDate().getTime());
		JSch jsch = new JSch();

		/*********************************************************
		 * Carga rtra victoria dolphing
		 *********************************************************************/

		/*
		 * conexion por SSH para la ejecucion del shell que realizara la carga de los
		 * archivos rtra victoria y dolphing.
		 */
		textField_1.setText("Sin carga, cargando victoria y dolphing, espere...");
		textField_1.update(textField_1.getGraphics());

		try {

			/*
			 * establecemos las variables de conexion para tranferir los RTRA una vez que
			 * validemos que existen en la ruta
			 */

			Session session = jsch.getSession(username, host, 22);
			session.setPassword("GBO#Oct22");

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
			 * Se validan los logs si el proceso termino de manera exitosa
			 */
			SSHConnector sshConnector = new SSHConnector();
			sshConnector.connect(username, PASSWORD, host, 22);
			String resultVic = sshConnector.executeCommand(
					"cd /planPGTMEX/procesos/RISK/interfaces; ls -ltr rtra-cream-ges-victoria-europa_mexico_"
							+ dateShelRTRA + ".txt ");
			String resultDolp = sshConnector.executeCommand(
					"cd /planPGTMEX/procesos/RISK/interfaces; ls -ltr rtra-cream-ges-dolphin-europa_mexico_"
							+ dateShelRTRA + ".txt ");

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

			if (resultVic.isEmpty() && resultDolp.isEmpty()) {
				textField_1.setText("No existen tus ficheros RTRA's en el FileShare");
				textField_1.update(textField_1.getGraphics());
				Thread.sleep(3000);
			} else {
				@SuppressWarnings("unused")
				String result = sshConnector
						.executeCommand("cd /planPGTMEX/procesos/RISK/;./cargartrabau.sh " + date + "");
			}
			String resultLogs = sshConnector.executeCommand("cd /planPGTMEX/procesos/RISK/salidas/" + dateShelRTRALog
					+ "/cargartrabau.sh; ls -ltr cargartrabau.sh.out");
			sshConnector.disconnect();

			if (resultLogs.isEmpty()) {
				textField_1.setText("Ocurrio un problema al cargar los RTRA's");
				textField_1.update(textField_1.getGraphics());
				Thread.sleep(4000);
			} else {
				textField_1.setText("Proceso finalizado, indique grupo");
				textField_1.update(textField_1.getGraphics());
			}
		} catch (Exception e1) {
			try {
				textField_1.setText("No se pudo realizar la carga de archivos RTRA's");
				textField_1.update(textField_1.getGraphics());
				Thread.sleep(4000);
			} catch (Exception e) {
				e1.printStackTrace();
			}
		}
	}
}