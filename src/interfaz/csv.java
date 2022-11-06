package interfaz;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class csv {
	
	public static final DecimalFormat DFORMATO = new DecimalFormat("###,###,###.##");
	List<String> encabezado = new ArrayList<>();
	String cadenaEncabezado = "CPTYPARENT|CPTYPARENTRATING|CPTYPARENTNAME|DEALSTAMP|INSTRUMENTNAME|VALUEDATE|MATURITYDATE|CURRENCY|NOMINALVALUECUR|CER|NOMINALVALUE|ONEOFF|CPTYNAME|FOLDERCOUNTRYNAME|CPTYCOUNTRY|CPTYPARENTCOUNTRY|FOLDERCOUNTRY\n";
	ArrayList<String> instrumentos = new ArrayList<String>();
	 
	public void interfazCsvPrimeraParte(List<AvalBonos> instrumento, List<String> newList, String CadenaMexicoGaran,
			String nombreInterfaz, Double totalMexicoGaranValCurSum, Double totalMexicoGaranCerSum,
			Double totalMexicoGaranNomValSum) throws IOException {

		
		FileWriter writer = new FileWriter(nombreInterfaz, true);
		encabezado.add(cadenaEncabezado);
		String CadenaEncabeza = encabezado.stream().collect(Collectors.joining(""));
		AvalBonos intrumentoProcesado;
		
		intrumentoProcesado = getInstrumento("aval", instrumento);

		if (intrumentoProcesado != null && intrumentoProcesado.getCadenaBonosMex() != null) {
			writer.write("MEXICO - AVAL\n");
			writer.write(CadenaEncabeza);
			writer.write(intrumentoProcesado.getCadenaBonosMex().toString());
			writer.write("TOTAL MEXICO - AVALES" + "|" + "|" + "|" + "|" + "TOTAL AVALES" + "|" + "|" + "|" + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomValCur()).toString() + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosCer()).toString() + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomVal()).toString() + "|" + "|" + "|"
					+ "|" + "|" + "|" + "_");
			writer.write("\n");
			writer.write("\n");
		} // Bonos Mexico

		intrumentoProcesado = getInstrumento("bonos", instrumento);

		if (intrumentoProcesado != null && intrumentoProcesado.getCadenaBonosMex() != null) {
			writer.write("MEXICO - BONOS\n");
			writer.write(CadenaEncabeza);
			writer.write(intrumentoProcesado.getCadenaBonosMex().toString());
			writer.write("TOTAL MEXICO - BONOS" + "|" + "|" + "|" + "|" + "TOTAL BONOS" + "|" + "|" + "|" + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomValCur()).toString() + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosCer()).toString() + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomVal()).toString() + "|" + "|" + "|"
					+ "|" + "|" + "|" + "_");
			writer.write("\n");
			writer.write("\n");
		} // Confirming Mexico

		intrumentoProcesado = getInstrumento("confirming", instrumento);

		if (intrumentoProcesado != null && intrumentoProcesado.getCadenaBonosMex() != null) {
			writer.write("-MEXICO - CONFIRMING\n");
			writer.write(CadenaEncabeza);
			writer.write(intrumentoProcesado.getCadenaBonosMex().toString());
			writer.write("TOTAL MEXICO - CONFIRMING" + "|" + "|" + "|" + "|" + "TOTAL CONFIRMING" + "|" + "|" + "|"
					+ "|" + DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomValCur()).toString() + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosCer()).toString() + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomVal()).toString() + "|" + "|" + "|"
					+ "|" + "|" + "|" + "_");

			writer.write("\n");
			writer.write("\n");
		} // Creditos Documentariado Mexio

		intrumentoProcesado = getInstrumento("documentariado", instrumento);

		if (intrumentoProcesado != null && intrumentoProcesado.getCadenaBonosMex() != null) {
			writer.write("MEXICO - CREDITOS DOCUMENTARIADO\n");
			writer.write(CadenaEncabeza);
			writer.write(intrumentoProcesado.getCadenaBonosMex().toString());
			writer.write("TOTAL MEXICO - CREDITOS DOCUMENTARIOS" + "|" + "|" + "|" + "|"
					+ "TOTAL CREDITOS DOCUMENTARIOS" + "|" + "|" + "|" + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomValCur()).toString() + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosCer()).toString() + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomVal()).toString() + "|" + "|" + "|"
					+ "|" + "|" + "|" + "_");
			writer.write("\n");
			writer.write("\n");
		} // Creditos Sindicados-Mexico
		intrumentoProcesado = getInstrumento("sindicado", instrumento);
		if (intrumentoProcesado != null && intrumentoProcesado.getCadenaBonosMex() != null) {
			writer.write("MEXICO - CREDITOS SINDICADOS\n");
			writer.write(CadenaEncabeza);
			writer.write(intrumentoProcesado.getCadenaBonosMex().toString());
			writer.write("TOTAL MEXICO - CREDITOS SINDICADOS" + "|" + "|" + "|" + "|" + "TOTAL CREDITOS SINDICADOS"
					+ "|" + "|" + "|" + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomValCur()).toString() + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosCer()).toString() + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomVal()).toString() + "|" + "|" + "|"
					+ "|" + "|" + "|" + "_");
			writer.write("\n");
			writer.write("\n");
		} // Derivados-Mexico
		intrumentoProcesado = getInstrumento("derivados", instrumento);
		if (intrumentoProcesado != null && intrumentoProcesado.getCadenaBonosMex() != null) {
			writer.write("MEXICO - DERIVADOS\n");
			writer.write(CadenaEncabeza);
			writer.write(intrumentoProcesado.getCadenaBonosMex().toString());
			writer.write("TOTAL MEXICO - DERIVADOS" + "|" + "|" + "|" + "|" + "TOTAL DERIVADOS" + "|" + "|" + "|" + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomValCur()).toString() + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosCer()).toString() + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomVal()).toString() + "|" + "|" + "|"
					+ "|" + "|" + "|" + "_");
			writer.write("\n");
			writer.write("\n");
		} // Descuentos-Mexico
		intrumentoProcesado = getInstrumento("descuentos", instrumento);
		if (intrumentoProcesado != null && intrumentoProcesado.getCadenaBonosMex() != null) {
			writer.write("MEXICO - DESCUENTOS\n");
			writer.write(CadenaEncabeza);
			writer.write(intrumentoProcesado.getCadenaBonosMex().toString());
			writer.write("TOTAL MEXICO - DESCUENTOS" + "|" + "|" + "|" + "|" + "TOTAL DESCUENTOS" + "|" + "|" + "|"
					+ "|" + DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomValCur()).toString() + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosCer()).toString() + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomVal()).toString() + "|" + "|" + "|"
					+ "|" + "|" + "|" + "_");
			writer.write("\n");
			writer.write("\n");
		} // Factoring Mexico
		intrumentoProcesado = getInstrumento("factoring", instrumento);
		if (intrumentoProcesado != null && intrumentoProcesado.getCadenaBonosMex() != null) {
			writer.write("MEXICO - FACTORING\n");
			writer.write(CadenaEncabeza);
			writer.write(intrumentoProcesado.getCadenaBonosMex().toString());
			writer.write("TOTAL MEXICO - FACTORING" + "|" + "|" + "|" + "|" + "TOTAL FACTORING" + "|" + "|" + "|" + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomValCur()).toString() + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosCer()).toString() + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomVal()).toString() + "|" + "|" + "|"
					+ "|" + "|" + "|" + "_");
			writer.write("\n");
			writer.write("\n");
		} // Financiamiento Comex
		intrumentoProcesado = getInstrumento("comex", instrumento);
		if (intrumentoProcesado != null && intrumentoProcesado.getCadenaBonosMex() != null) {
			writer.write("MEXICO - FINANCIAMIENTO COMEX\n");
			writer.write(CadenaEncabeza);
			writer.write(intrumentoProcesado.getCadenaBonosMex().toString());
			writer.write("TOTAL MEXICO - FINANCIAMIENTO COMEX" + "|" + "|" + "|" + "|" + "TOTAL FINANCIAMIENTO COMEX"
					+ "|" + "|" + "|" + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomValCur()).toString() + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosCer()).toString() + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomVal()).toString() + "|" + "|" + "|"
					+ "|" + "|" + "|" + "_");
			writer.write("\n");
			writer.write("\n");
		} // Financiamiento IMP/EXP-Mexico
		intrumentoProcesado = getInstrumento("impexp", instrumento);
		if (intrumentoProcesado != null && intrumentoProcesado.getCadenaBonosMex() != null) {
			writer.write("MEXICO - FINANCIAMIENTO IMP/EXP\n");
			writer.write(CadenaEncabeza);
			writer.write(intrumentoProcesado.getCadenaBonosMex().toString());
			writer.write("TOTAL MEXICO - FINANCIAMIENTO IMP/EXP" + "|" + "|" + "|" + "|"
					+ "TOTAL FINANCIAMIENTO IMP/EXP" + "|" + "|" + "|" + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomValCur()).toString() + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosCer()).toString() + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomVal()).toString() + "|" + "|" + "|"
					+ "|" + "|" + "|" + "_");
			writer.write("\n");
			writer.write("\n");
		} // Garantias
		if (!newList.isEmpty()) {
			writer.write("MEXICO - GARANTIAS\n");
			writer.write(CadenaEncabeza);
			writer.write(CadenaMexicoGaran);
			writer.write("TOTAL MEXICO - GARANTIAS" + "|" + "|" + "|" + "|" + "TOTAL GARANTIAS" + "|" + "|" + "|" + "|"
					+ DFORMATO.format(totalMexicoGaranValCurSum).toString() + "|"
					+ DFORMATO.format(totalMexicoGaranCerSum).toString() + "|"
					+ DFORMATO.format(totalMexicoGaranNomValSum).toString() + "|" + "|" + "|" + "|" + "|" + "|" + "_");
			writer.write("\n");
			writer.write("\n");
		} // Leasing-Renting
		intrumentoProcesado = getInstrumento("leasrent", instrumento);
		if (intrumentoProcesado != null && intrumentoProcesado.getCadenaBonosMex() != null) {
			writer.write("MEXICO - LEASING - RENTING\n");
			writer.write(CadenaEncabeza);
			writer.write(intrumentoProcesado.getCadenaBonosMex().toString());
			writer.write("TOTAL MEXICO - LEASING - RENTING" + "|" + "|" + "|" + "|" + "TOTAL LEASING - RENTING" + "|"
					+ "|" + "|" + "|" + DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomValCur()).toString()
					+ "|" + DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosCer()).toString() + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomVal()).toString() + "|" + "|" + "|"
					+ "|" + "|" + "|" + "_");
			writer.write("\n");
			writer.write("\n");
		} // Lineas Comprometidas
		intrumentoProcesado = getInstrumento("comprome", instrumento);
		if (intrumentoProcesado != null && intrumentoProcesado.getCadenaBonosMex() != null) {
			writer.write("MEXICO - LINEAS COMPROMETIDAS\n");
			writer.write(CadenaEncabeza);
			writer.write(intrumentoProcesado.getCadenaBonosMex().toString());
			writer.write("TOTAL MEXICO - LINEAS COMPROMETIDAS" + "|" + "|" + "|" + "|" + "TOTAL LINEAS COMPROMETIDAS"
					+ "|" + "|" + "|" + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomValCur()).toString() + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosCer()).toString() + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomVal()).toString() + "|" + "|" + "|"
					+ "|" + "|" + "|" + "_");
			writer.write("\n");
			writer.write("\n");
		} // Lineas No Comprometidas

		intrumentoProcesado = getInstrumento("nocompro", instrumento);

		if (intrumentoProcesado != null && intrumentoProcesado.getCadenaBonosMex() != null) {
			writer.write("MEXICO - LINEAS NO COMPROMETIDAS\n");
			writer.write(CadenaEncabeza);
			writer.write(intrumentoProcesado.getCadenaBonosMex().toString());
			writer.write("TOTAL MEXICO - LINEAS NO COMPROMETIDAS" + "|" + "|" + "|" + "|"
					+ "TOTAL LINEAS NO COMPROMETIDAS" + "|" + "|" + "|" + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomValCur()).toString() + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosCer()).toString() + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomVal()).toString() + "|" + "|" + "|"
					+ "|" + "|" + "|" + "_");
			writer.write("\n");
			writer.write("\n");
		} // Tarjeta de Credito Mexico

		intrumentoProcesado = getInstrumento("tarjeta", instrumento);

		if (intrumentoProcesado != null && intrumentoProcesado.getCadenaBonosMex() != null) {
			writer.write("MEXICO - TARJETA DE CREDITO\n");
			writer.write(CadenaEncabeza);
			writer.write(intrumentoProcesado.getCadenaBonosMex().toString());
			writer.write("TOTAL MEXICO - TARJETAS DE CREDITO" + "|" + "|" + "|" + "|" + "TOTAL TARJETAS DE CREDITO"
					+ "|" + "|" + "|" + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomValCur()).toString() + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosCer()).toString() + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomVal()).toString() + "|" + "|" + "|"
					+ "|" + "|" + "|" + "_");
			writer.write("\n");
			writer.write("\n");
		} // OVERDRAFTS Mexico

		intrumentoProcesado = getInstrumento("over", instrumento);

		if (intrumentoProcesado != null && intrumentoProcesado.getCadenaBonosMex() != null) {
			writer.write("MEXICO - OVERDRAFTS\n");
			writer.write(CadenaEncabeza);
			writer.write(intrumentoProcesado.getCadenaBonosMex().toString());
			writer.write("TOTAL MEXICO - OVERDRAFTS" + "|" + "|" + "|" + "|" + "TOTAL OVERDRAFTS" + "|" + "|" + "|"
					+ "|" + DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomValCur()).toString() + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosCer()).toString() + "|"
					+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomVal()).toString() + "|" + "|" + "|"
					+ "|" + "|" + "|" + "_");
			writer.write("\n");
			writer.write("\n");
		}
		intrumentoProcesado = getInstrumento("sumatoria", instrumento);
		if (intrumentoProcesado != null && intrumentoProcesado.getCadenaBonosMex() != null) {
		writer.write("TOTAL MEXICO" + "|" + "|" + "|" + "|" + "TOTAL GENERAL" + "|" + "|" + "|" + "|"
				+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomValCur()).toString() + "|"
				+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosCer()).toString() + "|"
				+ DFORMATO.format(intrumentoProcesado.getTotalMexicoBonosNomVal()).toString() + "|" + "|" + "|" + "|" + "|" + "|" + "_");
		writer.write("\n");
		writer.write("\n");
		}
		writer.flush();
		writer.close();
	}

	private AvalBonos getInstrumento(String nombreInstrumento, List<AvalBonos> instrumento) {

		for (AvalBonos avalBonos : instrumento) {
			if (avalBonos.getInstrumento().equals(nombreInstrumento)) {
				return avalBonos;
			}
		}

		return null;
	}
}
