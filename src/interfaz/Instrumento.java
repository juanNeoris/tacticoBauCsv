package interfaz;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import gettersetter.BeanIntrumento;

/**
 * Clase Instrumento que se encargara de 
 * escribir el bloque del intrumento evaludo obtenido enviado del 
 * meto {@code operaciones()} que se encuentra en la clase {@link Conexion}
 * 
 * @author Z363772
 *@since 06/12/2022
 */
public class Instrumento {
    /**
     * variable que permitira mostrar los mensaje 
     * de la ejecucion del proceso
     */
	private static final Logger LOGGER = LogManager.getLogger(Instrumento.class);
	/**
	 * Cadena con el encabezado
	 */
	private static String cadenaEncabezado = "CPTYPARENT|CPTYPARENTRATING|CPTYPARENTNAME|DEALSTAMP|INSTRUMENTNAME|VALUEDATE|MATURITYDATE|CURRENCY|NOMINALVALUECUR|CER|NOMINALVALUE|ONEOFF|CPTYNAME|FOLDERCOUNTRYNAME|CPTYCOUNTRY|CPTYPARENTCOUNTRY|FOLDERCOUNTRY\n";
	

	/**
	 * metodo interfazCsv que escribira el intrumento con su total parcial del intrumento y pais evaluado
	 * @param instrumento que se esta evaluando 
	 * @param nombreInterfaz sobre la cual se escribira el bloque evaluado 
	 * @param pais que se esta evaluando 
	 * @param elemento la lista con el bloque evaludo
	 */
	public static void interfazCsv(List<BeanIntrumento> instrumento, String nombreInterfaz, String pais,
			String elemento) {
		
		try (FileWriter writer = new FileWriter(nombreInterfaz, true)) {
			
			writer.write(pais.toUpperCase() + "  -  " + elemento + "\n");
			writer.write(cadenaEncabezado);
			for (BeanIntrumento beanIntrumento : instrumento) {
				if (instrumento != null && !beanIntrumento.toString().isEmpty()) {
					writer.write(beanIntrumento.toString());
					writer.write("\n");		
				}
			}
			writer.write("\n");
			writer.flush();
		} catch (IOException e) {
			LOGGER.info("Problemas escribir archivo" + e);
		} finally {
			LOGGER.info("archivo cerrado");
		}
	}
	
	/**
	 * metodo interfazCsvTotales que escribira debajo el total general del pais evaluado 
	 * @param instrumento evaluado 
	 * @param nombreInterfaz sobre la cual se va a escribir el total general
	 * @param pais pais que se evaluo 
	 */
	public static void interfazCsvTotales(List<BeanIntrumento> instrumento, String nombreInterfaz) {
		try (FileWriter writer = new FileWriter(nombreInterfaz, true)) {
			for (BeanIntrumento beanIntrumento : instrumento) {
				if (instrumento != null && !beanIntrumento.toStringTotales().isEmpty()) {
					writer.write(beanIntrumento.toStringTotales());
					writer.write("\n");				
				}
			}
			writer.write("\n");
			writer.flush();
		} catch (IOException e) {
			LOGGER.info("Problemas escribir archivo" + e);
		} finally {
			LOGGER.info("archivo cerrado");
		}
	}
}
