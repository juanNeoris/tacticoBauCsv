package conexion;

import java.io.FileOutputStream;
import java.security.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

	public String getConsulta(String grupo, String excelFilePath, String fechaConsumo) throws Exception {

		Statement sta = con.createStatement();
		String str = "GARANTIA";
		String systCode2 = "";
		
		List<String> operacion = new ArrayList<String>();
		
		String systCode = "SELECT to_char(INSTR(instrumentname,'GARANTIA')) GARANTIA,dealstamp, cpty,cptyname,cptycountry,cptyparent,cptyparentname,cptyparentcountry,cptyparentrating,lastparent,lastparentname,lastparentcountry,lastparentrating,instrumentname,foldername,foldercountryname,valuedate,maturitydate,guaranteepercent_cpty,currency,nominalvaluecur,nominalvalue,oneoff,guaranteedparentrating,cer,recequivalente,recbruto,lastparentfname,lastparentfcountryname,lastparentfrating,dispuesto,cer2,collateralagreement from PGT_MEX.T_PGT_MEX_CONSUMOSC_V WHERE LastParentF ='"
				+ grupo + "' and FECHACARGA='" + fechaConsumo
				+ "' UNION ALL SELECT to_char(INSTR(instrumentname,'GARANTIA')) GARANTIA,dealstamp,cpty,cptyname,cptycountry,cptyparent,cptyparentname,cptyparentcountry,cptyparentrating,lastparent, lastparentname,lastparentcountry,lastparentrating,instrumentname,foldername,foldercountryname,valuedate,maturitydate,guaranteepercent_cpty,currency,nominalvaluecur,nominalvalue,oneoff,guaranteedparentrating,cer,recequivalente,recbruto,lastparentfname,lastparentfcountryname,lastparentfrating,dispuesto,cer2,collateralagreement from PGT_MEX.T_PGT_MEX_CONSUMOSC_D WHERE LastParentF ='"
				+ grupo + "' and FECHACARGA='" + fechaConsumo + "'";
		ResultSet rs = sta.executeQuery(systCode);

		Workbook workbook = new XSSFWorkbook();

		Sheet sheet = workbook.createSheet("rtras");
		
		
		try {
			writeHeaderLine(sheet);
			writeDataLines(rs, workbook, sheet);
			FileOutputStream outputStream = new FileOutputStream(excelFilePath);
			workbook.write(outputStream);
			workbook.close();
			
		} catch (Exception e) {
			System.out.println("File IO error:");
			e.printStackTrace();
		}

		return systCode;
	}

	private void writeHeaderLine(Sheet sheet) {

		Row headerRow = sheet.createRow(0);

		Cell headerCell = headerRow.createCell(0);
		headerCell.setCellValue("Garantia");

		headerCell = headerRow.createCell(1);
		headerCell.setCellValue("dealstamp");

		headerCell = headerRow.createCell(2);
		headerCell.setCellValue("cpty");

		headerCell = headerRow.createCell(3);
		headerCell.setCellValue("cptyname");

		headerCell = headerRow.createCell(4);
		headerCell.setCellValue("cptycountry");

        headerCell = headerRow.createCell(5);
		headerCell.setCellValue("cptyparent");

		headerCell = headerRow.createCell(6);
		headerCell.setCellValue("cptyparentname");

        headerCell = headerRow.createCell(7);
		headerCell.setCellValue("cptyparentcountry");

		headerCell = headerRow.createCell(8);
		headerCell.setCellValue("cptyparentrating");

        headerCell = headerRow.createCell(9);
		headerCell.setCellValue("lastparent");

		headerCell = headerRow.createCell(10);
		headerCell.setCellValue("lastparentname");

        headerCell = headerRow.createCell(11);
		headerCell.setCellValue("lastparentcountry");

		headerCell = headerRow.createCell(12);
		headerCell.setCellValue("lastparentrating");

        headerCell = headerRow.createCell(13);
		headerCell.setCellValue("instrumentname");

		headerCell = headerRow.createCell(14);
		headerCell.setCellValue("foldername");

        headerCell = headerRow.createCell(15);
		headerCell.setCellValue("foldercountryname");

		headerCell = headerRow.createCell(16);
		headerCell.setCellValue("valuedate");

        headerCell = headerRow.createCell(17);
		headerCell.setCellValue("maturitydate");

		headerCell = headerRow.createCell(18);
		headerCell.setCellValue("guaranteepercent_cpty");

        headerCell = headerRow.createCell(19);
		headerCell.setCellValue("currency");
        
        headerCell = headerRow.createCell(20);
		headerCell.setCellValue("nominalvaluecur");

        headerCell = headerRow.createCell(21);
		headerCell.setCellValue("nominalvalue");

        headerCell = headerRow.createCell(22);
		headerCell.setCellValue("oneoff");

        headerCell = headerRow.createCell(23);
		headerCell.setCellValue("guaranteedparentrating");

        headerCell = headerRow.createCell(24);
		headerCell.setCellValue("cer");

        headerCell = headerRow.createCell(25);
		headerCell.setCellValue("recequivalente");

        headerCell = headerRow.createCell(26);
		headerCell.setCellValue("recbruto");

        headerCell = headerRow.createCell(27);
		headerCell.setCellValue("lastparentfname");

        headerCell = headerRow.createCell(28);
		headerCell.setCellValue("lastparentfcountryname");

        headerCell = headerRow.createCell(29);
		headerCell.setCellValue("lastparentfrating");

        headerCell = headerRow.createCell(30);
		headerCell.setCellValue("dispuesto");

        headerCell = headerRow.createCell(31);
		headerCell.setCellValue("cer2");

        headerCell = headerRow.createCell(32);
		headerCell.setCellValue("collateralagreement");
	}

	private void writeDataLines(ResultSet rs, Workbook workbook, Sheet sheet) throws SQLException {
		int rowCount = 1;

		while (rs.next()) {
			String Garantia = rs.getString("Garantia");
			String dealstamp =rs.getString("DEALSTAMP");
			String cpty = rs.getString("cpty");
			String cptyname = rs.getString("cptyname");
			String cptycountry = rs.getString("cptycountry");
			String cptyparent = rs.getString("cptyparent");
			String cptyparentname = rs.getString("cptyparentname");
			String cptyparentcountry = rs.getString("cptyparentcountry");
			String cptyparentrating = rs.getString("cptyparentrating");
			String lastparent = rs.getString("lastparent");
			String lastparentname = rs.getString("lastparentname");
			String lastparentcountry = rs.getString("lastparentcountry");
			String lastparentrating = rs.getString("lastparentrating");
			String instrumentname = rs.getString("instrumentname");
			String foldername = rs.getString("foldername");
			String foldercountryname = rs.getString("foldercountryname");
			String valuedate = rs.getString("valuedate");
			String maturitydate = rs.getString("maturitydate");
			String guaranteepercent_cpty = rs.getString("guaranteepercent_cpty");
			String currency = rs.getString("currency");
			String nominalvaluecur = rs.getString("nominalvaluecur");
			String nominalvalue = rs.getString("nominalvalue");
			String oneoff = rs.getString("oneoff");
			String guaranteedparentrating = rs.getString("guaranteedparentrating");
			String cer = rs.getString("cer");
			String recequivalente = rs.getString("recequivalente");
			String recbruto = rs.getString("recbruto");
			String lastparentfname = rs.getString("lastparentfname");
			String lastparentfcountryname = rs.getString("lastparentfcountryname");
			String lastparentfrating = rs.getString("lastparentfrating");
			String dispuesto = rs.getString("dispuesto");
			String cer2 = rs.getString("cer2");
			String collateralagreement = rs.getString("collateralagreement");
			
			Row row = sheet.createRow(rowCount++);

			int columnCount = 0;
			
			if(Garantia.equals("1")) {
				
				Cell cell = row.createCell(columnCount++);
				CellStyle cellStyle = workbook.createCellStyle();
	            CreationHelper creationHelper = workbook.getCreationHelper();
	            cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);    
				cell.setCellValue(Garantia);
				
				
				cell = row.createCell(columnCount++);
	            cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);
				cell.setCellValue(dealstamp);

				
				cell = row.createCell(columnCount++);
				cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);
				cell.setCellValue(cpty);

				cell = row.createCell(columnCount++);
				cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);
				cell.setCellValue(cptyname);

				cell = row.createCell(columnCount++);
				cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);
				cell.setCellValue(cptycountry);

	            
	            cell = row.createCell(columnCount++);
	            cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);
				cell.setCellValue(cptyparent);

				cell = row.createCell(columnCount++);
				cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);
				cell.setCellValue(cptyparentname);

				cell = row.createCell(columnCount++);
				cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);
				cell.setCellValue(cptyparentcountry);

				cell = row.createCell(columnCount++);
				cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);
				cell.setCellValue(cptyparentrating);

				cell = row.createCell(columnCount++);
				cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);
				cell.setCellValue(lastparent);

	            cell = row.createCell(columnCount++);
	            cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);
				cell.setCellValue(lastparentname);

				cell = row.createCell(columnCount++);
				cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);
				cell.setCellValue(lastparentcountry);

				cell = row.createCell(columnCount++);
				cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);
				cell.setCellValue(lastparentrating);

				cell = row.createCell(columnCount++);
				cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);
				cell.setCellValue(instrumentname);

				cell = row.createCell(columnCount++);
				cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);
				cell.setCellValue(foldername);

	            cell = row.createCell(columnCount++);
	            cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);
				cell.setCellValue(foldercountryname);

				cell = row.createCell(columnCount++);
				cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);
				cell.setCellValue(valuedate);

				cell = row.createCell(columnCount++);
				cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);
				cell.setCellValue(maturitydate);

				cell = row.createCell(columnCount++);
				cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);
				cell.setCellValue(guaranteepercent_cpty);

				cell = row.createCell(columnCount++);
				cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);
				cell.setCellValue(currency);

	            cell = row.createCell(columnCount++);
	            cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);
				cell.setCellValue(nominalvaluecur);

				cell = row.createCell(columnCount++);
				cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);
				cell.setCellValue(nominalvalue);

				cell = row.createCell(columnCount++);
				cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);
				cell.setCellValue(oneoff);

				cell = row.createCell(columnCount++);
				cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);
				cell.setCellValue(guaranteedparentrating);

				cell = row.createCell(columnCount++);
				cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);
				cell.setCellValue(cer);

	             cell = row.createCell(columnCount++);
	             cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
		            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
		            cell.setCellStyle(cellStyle);
				cell.setCellValue(recequivalente);

				cell = row.createCell(columnCount++);
				cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);
				cell.setCellValue(recbruto);

				cell = row.createCell(columnCount++);
				cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);
				cell.setCellValue(lastparentfname);

				cell = row.createCell(columnCount++);
				cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);
				cell.setCellValue(lastparentfcountryname);

				cell = row.createCell(columnCount++);
				cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);
				cell.setCellValue(lastparentfrating);

	             cell = row.createCell(columnCount++);
	             cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
		            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
		            cell.setCellStyle(cellStyle);
				cell.setCellValue(dispuesto);

				cell = row.createCell(columnCount++);
				cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);
				cell.setCellValue(cer2);

				cell = row.createCell(columnCount++);
				cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	            cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
	            cell.setCellStyle(cellStyle);
				cell.setCellValue(collateralagreement);
				
				
				
			}else {
			Cell cell = row.createCell(columnCount++);
			cell.setCellValue(Garantia);
		

			cell = row.createCell(columnCount++);
			cell.setCellValue(dealstamp);

			cell = row.createCell(columnCount++);
			cell.setCellValue(cpty);

			cell = row.createCell(columnCount++);
			cell.setCellValue(cptyname);

			cell = row.createCell(columnCount++);
			cell.setCellValue(cptycountry);

            
            cell = row.createCell(columnCount++);
			cell.setCellValue(cptyparent);

			cell = row.createCell(columnCount++);
			cell.setCellValue(cptyparentname);

			cell = row.createCell(columnCount++);
			cell.setCellValue(cptyparentcountry);

			cell = row.createCell(columnCount++);
			cell.setCellValue(cptyparentrating);

			cell = row.createCell(columnCount++);
			cell.setCellValue(lastparent);

            cell = row.createCell(columnCount++);
			cell.setCellValue(lastparentname);

			cell = row.createCell(columnCount++);
			cell.setCellValue(lastparentcountry);

			cell = row.createCell(columnCount++);
			cell.setCellValue(lastparentrating);

			cell = row.createCell(columnCount++);
			cell.setCellValue(instrumentname);

			cell = row.createCell(columnCount++);
			cell.setCellValue(foldername);

            cell = row.createCell(columnCount++);
			cell.setCellValue(foldercountryname);

			cell = row.createCell(columnCount++);
			cell.setCellValue(valuedate);

			cell = row.createCell(columnCount++);
			cell.setCellValue(maturitydate);

			cell = row.createCell(columnCount++);
			cell.setCellValue(guaranteepercent_cpty);

			cell = row.createCell(columnCount++);
			cell.setCellValue(currency);

            cell = row.createCell(columnCount++);
			cell.setCellValue(nominalvaluecur);

			cell = row.createCell(columnCount++);
			cell.setCellValue(nominalvalue);

			cell = row.createCell(columnCount++);
			cell.setCellValue(oneoff);

			cell = row.createCell(columnCount++);
			cell.setCellValue(guaranteedparentrating);

			cell = row.createCell(columnCount++);
			cell.setCellValue(cer);

             cell = row.createCell(columnCount++);
			cell.setCellValue(recequivalente);

			cell = row.createCell(columnCount++);
			cell.setCellValue(recbruto);

			cell = row.createCell(columnCount++);
			cell.setCellValue(lastparentfname);

			cell = row.createCell(columnCount++);
			cell.setCellValue(lastparentfcountryname);

			cell = row.createCell(columnCount++);
			cell.setCellValue(lastparentfrating);

             cell = row.createCell(columnCount++);
			cell.setCellValue(dispuesto);

			cell = row.createCell(columnCount++);
			cell.setCellValue(cer2);

			cell = row.createCell(columnCount++);
			cell.setCellValue(collateralagreement);
			}
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