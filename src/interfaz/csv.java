package interfaz;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;

import lombok.Getter;
import lombok.Setter;
import validacion.Sumatoria;


/**
 * Clase que escribe los intrumentos validados
 */

public class Csv {

	private static final Logger LOGGER = Logger.getLogger(Csv.class.getName());
	/**
	 * variable que nos permite aplicar un formato a las cantidades nominal value
	 * cur cer nominal value
	 */
	public static final DecimalFormat DFORMATO = new DecimalFormat("###,###,###.##");
	
	/**
	 * variable que nos permite aplicar un formato a las fechas
	 */
	public static final String TOTAL = "TOTAL ";
	/**
	 * variable que nos permite validar
	 * si es una garantia
	 */
	public static final String GARANTIAS = "GARANTIAS";
	/**
	 * variable que nos permite validar
	 * si es una garantia
	 */
	public static final String GARANTIA = "- GARANTIAS";
	
	/**
	 * Instancia de la sumatoria 
	 * 
	 */
	@Getter
	@Setter
	private Sumatoria suma= new Sumatoria();
	
	/**
	 * Array que contiene el encabezado de nuestra interfaz final
	 */
	private List<String> encabezado = new ArrayList<>();
	/**
	 * Cadena con el encabezado
	 */
	private String cadenaEncabezado = "CPTYPARENT|CPTYPARENTRATING|CPTYPARENTNAME|DEALSTAMP|INSTRUMENTNAME|VALUEDATE|MATURITYDATE|CURRENCY|NOMINALVALUECUR|CER|NOMINALVALUE|ONEOFF|CPTYNAME|FOLDERCOUNTRYNAME|CPTYCOUNTRY|CPTYPARENTCOUNTRY|FOLDERCOUNTRY\n";

	/**
	 * Metodo interfazCsvPrimeraParte que genera el CSV con los instrumentos
	 * evaluados
	 * 
	 * @param instrumento               array de objetos que contienen las cadenas y
	 *                                  sumatorias de los instrumentos
	 * @param newList                   lista de las garantias
	 * @param cadenaMexicoGaran         lista de las garantias
	 * @param totalMexicoGaranValCurSum sumatoria de las garantias nominal value cur
	 * @param totalMexicoGaranCerSum    sumatoria de las garantias del cer
	 * @param totalMexicoGaranNomValSum sumatoria de las garantias nominal value
	 * @param pais                      colocar en encabezado de cada pais
	 * @param nombreInterfaz            generar la interfaz temporal
	 * @throws IOException atrapa la excepcion durante tiempo de ejecucion
	 */
	public void interfazCsvPrimeraParte(Map<String,AvalBonos>instrumento, List<String> newList, String cadenaMexicoGaran,
			String nombreInterfaz, Double totalMexicoGaranValCurSum, Double totalMexicoGaranCerSum,
			Double totalMexicoGaranNomValSum, String pais) {
		/**
		 * se genera la instancia del archivo temporal
		 */
		
		try (FileWriter writer = new FileWriter(nombreInterfaz, true)){
		
		encabezado.add(cadenaEncabezado);
		String cadenaEncabeza = encabezado.stream().collect(Collectors.joining(""));
		AvalBonos intrumentoProcesado;
		/**
		 * se genera la lista de los instrumentos para ser evaluados
		 */
		ArrayList<String> grades = new ArrayList<>(
				Arrays.asList("AVALES", "BONOS", "CONFIRMING", "CREDITOS DOCUMENTARIOS", "CREDITOS SINDICADOS",
						"DERIVADOS", "DESCUENTOS", "FACTORING", "FINANCIAMIENTO COMEX", "FINANCIAMIENTO IMP/EXP",
						"GARANTIAS", "LEASING - RENTING", "LINEAS COMPROMETIDAS", "LINEAS NO COMPROMETIDAS","TARJETAS DE CREDITO", "OVERDRAFTS"));
		/*
		 * Declaramos el Iterador que obtendra los elementos de la lista que y llamara
		 * la metodo getInstrumento que regresara el objeto con los instrumentos y
		 * sumatorias
		 */
		Iterator<String> nombreIterator = grades.iterator();
		while (nombreIterator.hasNext()) {
			String elemento = nombreIterator.next();
			if (elemento.equals(GARANTIAS) && !newList.isEmpty()) {

				writer.write(pais.toUpperCase() +"  -  "+GARANTIAS+"\n");
				writer.write(cadenaEncabeza);
				writer.write(cadenaMexicoGaran);
				writer.write("TOTALES "+pais.toUpperCase() +GARANTIA + "|" + "|" + "|" + "|" + "TOTAL GARANTIAS" + "|" + "|" + "|"
						+ "|" + DFORMATO.format(totalMexicoGaranValCurSum).toString() + "|"
						+ DFORMATO.format(totalMexicoGaranCerSum).toString() + "|"
						+ DFORMATO.format(totalMexicoGaranNomValSum).toString() + "|" + "|" + "|" + "|" + "|" + "|"
						+ "_");
				writer.write("\n");
				writer.write("\n");

			}

			intrumentoProcesado = instrumento.get(elemento);
			
			if (intrumentoProcesado != null && intrumentoProcesado.getCadenaBonosMex() != null) {
				writer.write(pais.toUpperCase() + "  -  " + elemento + "\n");
				writer.write(cadenaEncabeza);
				writer.write(intrumentoProcesado.getCadenaBonosMex().toString());
				writer.write("TOTALES "+ pais.toUpperCase() + " - " + elemento + "|" + "|" + "|" + "|" + TOTAL
						+ elemento + "|" + "|" + "|" + "|"
						+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomValCur()).toString() + "|"
						+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosCer()).toString() + "|"
						+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomVal()).toString() + "|" + "|" + "|"
						+ "|" + "|" + "|" + "_");
				writer.write("\n");
				writer.write("\n");
			}
		}	
		/* el total general de todos los intrumentos para un pais */
     	if(suma.getTotalGenBonosNomValCur() > Math.abs(0.0)) {
			writer.write(TOTAL+pais.toUpperCase() + "|" + "|" + "|" + "|" + "TOTAL GENERAL" + "|" + "|" + "|" + "|"
					+ DFORMATO.format(suma.getTotalGenBonosNomValCur()).toString() + "|"
					+ DFORMATO.format(suma.getTotalGenBonosCer()).toString() + "|"
					+ DFORMATO.format(suma.getTotalGenBonosNomVal()).toString() + "|" + "|" + "|"
					+ "|" + "|" + "|" + "_");
			writer.write("\n");
			writer.write("\n");
     	}
		writer.flush();
		instrumento.clear();
		} catch (IOException e) {
			LOGGER.info("Problemas escribir archivo" + e);
		} finally {
			LOGGER.info("archivo cerrado");
		}
	}

}
