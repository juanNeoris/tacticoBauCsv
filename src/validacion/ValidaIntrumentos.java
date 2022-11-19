package validacion;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import conexion.Conexion;
import interfaz.AvalBonos;
import interfaz.Csv;
import util.ConstanstUtils;
import util.ConstantsUtil;
import util.ObjetosInstrumentos;
import util.ObjetosIntrumento;

/**
 * clase ValidaIntrumentos valida registro a registro a que instrumento
 * pertenece y agregado al objeto para despues ser escrito
 */
public class ValidaIntrumentos {

	/**
	 * Array que guardara la contraparte
	 */
	private List<String> info = new ArrayList<String>();
	/**
	 * instancia de instrumentos guardara todos los objetos de tipo instrumento que
	 * se generaran durante la evaluacion
	 */
	//private List<AvalBonos> instrumento = new ArrayList<AvalBonos>();
	
	Map<String,AvalBonos> instrumento = new HashMap<String,AvalBonos>();
	/**
	 * instancia de instrumento conection que consultara la contraparte
	 * 
	 */
	private Conexion conection = new Conexion();
	/**
	 * instancia de instrumento contraparte
	 */

	private ArrayList<String> contraparte = new ArrayList<String>();
	/**
	 * instancia de instrumento constans
	 */

	private ConstantsUtil constans = new ConstantsUtil();
	private ConstanstUtils constan = new ConstanstUtils();
	private Sumatoria sumatoria = new Sumatoria();

	/**
	 * instancia de instrumento que guardara la cadena de las garantias
	 * 
	 */

	private List<String> mexicoGaran = new ArrayList<String>();
	/**
	 * instancia de instrumento que guardara la sumatoria del nominal value cur
	 * 
	 */
	private List<Double> mexicoGaranValCurSum = new ArrayList<Double>();
	/**
	 * instancia de instrumento que guardara la sumatoria del cer
	 * 
	 */
	private List<Double> mexicoGaranCerSum = new ArrayList<Double>();
	/**
	 * instancia de instrumento que guardara la sumatoria del nominal value
	 * 
	 */
	private List<Double> mexicoGaranNomValSum = new ArrayList<Double>();
	/**
	 * se intancia las clase que guardan los objetos de tipo Instrumento
	 * 
	 */
	private ObjetosInstrumentos obj = new ObjetosInstrumentos();
	private ObjetosIntrumento objs = new ObjetosIntrumento();

	/**
	 * se declara contador que indicara que ya es el ultimo registro evaluado para
	 * mandarlo escribir
	 * 
	 */
	private int cont = 0;
	
	/**
	 * se declara la variable que 
	 * llevara la sumatoria del NominalValueCur
	 * 
	 */
	private double sumatoriaNomValCur;
	/**
	 * se declara la variable que 
	 * llevara la sumatoria del cer
	 * 
	 */
	private double sumatoriaCer;
	/**
	 * se declara la variable que 
	 * llevara la sumatoria del NominalValue
	 * 
	 */
	private double sumatoriaNomVal;
	/**
	 * se declara la variable que 
	 * llevara el control del pais que se esta
	 * evaluando
	 * 
	 */

	private String validapais = null;
	/**
	 * se declara variable que 
	 * gurdara la cedena de las garantias
	 * 
	 */
	private String cadenaMexicoGaran;

	/**
	 * metodo intrumentosParteUno evalua la primera parte de los instrumentos
	 * 
	 * @param systCode       el resgistro obtenido de la consulta
	 * @param instrumentos   el campo que contiene el intrumento
	 * @param fechaConsumo   usada para obtener la contraparte del registro evaluado
	 * @param deal           usado para obtener la contraparte
	 * @param pais           para evaluar si el pais ha cambiado
	 * @param nomValCur      campo para realizar la sumatoria
	 * @param cer            campo para realizar la sumatoria
	 * @param nomVal         campo para realizar la sumatoria
	 * @param nombreInterfaz nombre de la interfaz sobre la cual se va a escribir
	 * @param registros      validar si ya se evaluaron todos los registos se
	 *                       escribe la interfaz
	 * @throws Exception excepcion generar durante la ejecucion del proceso
	 */

	public void intrumentosParteUno(String systCode, String instrumentos, String fechaConsumo, String deal, String pais,
			String nomValCur, String cer, String nomVal, String nombreInterfaz, Integer registros) throws Exception {
		conection.conecGBO();
		/**
		 * se valida si es un bono
		 */
				
		if (validapais == null || pais.equals(validapais)) {
			validapais = pais;
			
			if (constans.getBonos().contains(instrumentos)) {
				obj.getBono().bonos(fechaConsumo, systCode, deal, pais, nomValCur, cer, nomVal);
				sumatoria.suma(nomValCur, cer, nomVal);
				info = conection.getContraparte(fechaConsumo, deal, pais);
				contraparte.addAll(info);
				instrumento.put("BONOS",obj.getBono());
			}
			/**
			 * se valida si es un credito documentariado
			 */
			if (constans.getDocument().contains(instrumentos)) {
				obj.getDocumentariado().bonos(fechaConsumo, systCode, deal, pais, nomValCur, cer, nomVal);
				sumatoria.suma(nomValCur, cer, nomVal);
				info = conection.getContraparte(fechaConsumo, deal, pais);
				contraparte.addAll(info);
				instrumento.put("CREDITOS DOCUMENTARIOS",obj.getDocumentariado());
			}
			/**
			 * se valida si es exportacion/importacion
			 */
			if (constans.getImpExp().contains(instrumentos)) {
				obj.getImpexp().bonos(fechaConsumo, systCode, deal, pais, nomValCur, cer, nomVal);
				sumatoria.suma(nomValCur, cer, nomVal);
				info = conection.getContraparte(fechaConsumo, deal, pais);
				contraparte.addAll(info);
				instrumento.put("FINANCIAMIENTO IMP/EXP",obj.getImpexp());
			}
			/**
			 * se valida si es comex
			 */
			if (constans.getComex().contains(instrumentos)) {
				obj.getComex().bonos(fechaConsumo, systCode, deal, pais, nomValCur, cer, nomVal);
				sumatoria.suma(nomValCur, cer, nomVal);
				info = conection.getContraparte(fechaConsumo, deal, pais);
				contraparte.addAll(info);
				instrumento.put("FINANCIAMIENTO COMEX",obj.getComex());
			}
			/**
			 * se valida si es credito sindicado
			 */
			if (constans.getSindi().contains(instrumentos)) {
				obj.getSindicado().bonos(fechaConsumo, systCode, deal, pais, nomValCur, cer, nomVal);
				sumatoria.suma(nomValCur, cer, nomVal);
				info = conection.getContraparte(fechaConsumo, deal, pais);
				contraparte.addAll(info);
				instrumento.put("CREDITOS SINDICADOS",obj.getSindicado());
			}
			/**
			 * se valida si es confirming
			 */
			if (constans.getConfirm().contains(instrumentos)) {
				obj.getConfirming().bonos(fechaConsumo, systCode, deal, pais, nomValCur, cer, nomVal);
				sumatoria.suma(nomValCur, cer, nomVal);
				info = conection.getContraparte(fechaConsumo, deal, pais);
				contraparte.addAll(info);
				instrumento.put("CONFIRMING",obj.getConfirming());
			}
			/**
			 * se valida si es descuentos
			 */
			if (constans.getDescuen().contains(instrumentos)) {
				obj.getDescuentos().bonos(fechaConsumo, systCode, deal, pais, nomValCur, cer, nomVal);
				sumatoria.suma(nomValCur, cer, nomVal);
				info = conection.getContraparte(fechaConsumo, deal, pais);
				contraparte.addAll(info);
				instrumento.put("DESCUENTOS",obj.getDescuentos());
			}
			/**
			 * se valida si es factoring
			 */
			if (constans.getFactor().contains(instrumentos)) {
				obj.getFactoring().bonos(fechaConsumo, systCode, deal, pais, nomValCur, cer, nomVal);
				sumatoria.suma(nomValCur, cer, nomVal);
				info = conection.getContraparte(fechaConsumo, deal, pais);
				contraparte.addAll(info);
				instrumento.put("FACTORING",obj.getFactoring());
			}
			/**
			 * se valida si son tarjetas de credito
			 */
			if (constans.getTarjeta().contains(instrumentos)) {
				objs.getTarjeta().bonos(fechaConsumo, systCode, deal, pais, nomValCur, cer, nomVal);
				sumatoria.suma(nomValCur, cer, nomVal);
				info = conection.getContraparte(fechaConsumo, deal, pais);
				contraparte.addAll(info);
				instrumento.put("TARJETAS DE CREDITO",objs.getTarjeta());
			}
			/**
			 * se valida si son lineas comprometidas
			 */
			if (constan.getComprome().contains(instrumentos)) {
				obj.getComprome().bonos(fechaConsumo, systCode, deal, pais, nomValCur, cer, nomVal);
				sumatoria.suma(nomValCur, cer, nomVal);
				info = conection.getContraparte(fechaConsumo, deal, pais);
				contraparte.addAll(info);
				instrumento.put("LINEAS COMPROMETIDAS",obj.getComprome());
			}
			this.intrumentosParteDos(systCode, instrumentos, fechaConsumo, deal, pais, nomValCur, cer, nomVal,
					nombreInterfaz, registros);
		} else {
			this.intrumentosParteDos(systCode, instrumentos, fechaConsumo, deal, pais, nomValCur, cer, nomVal,
					nombreInterfaz, registros);
		}

	}

	/**
	 * metodo intrumentosParteUno evalua la primera parte de los instrumentos
	 * 
	 * @param systCode       el resgistro obtenido de la consulta
	 * @param instrumentos   el campo que contiene el intrumento
	 * @param fechaConsumo   usada para obtener la contraparte del registro evaluado
	 * @param deal           usado para obtener la contraparte
	 * @param pais           para evaluar si el pais ha cambiado
	 * @param nomValCur      campo para realizar la sumatoria
	 * @param cer            campo para realizar la sumatoria
	 * @param nomVal         campo para realizar la sumatoria
	 * @param nombreInterfaz nombre de la interfaz sobre la cual se va a escribir
	 * @param registros      validar si ya se evaluaron todos los registos se
	 *                       escribe la interfaz
	 * @throws Exception generada durante la ejecucion del proceso
	 */
	public void intrumentosParteDos(String systCode, String instrumentos, String fechaConsumo, String deal, String pais,
			String nomValCur, String cer, String nomVal, String nombreInterfaz, Integer registros) throws Exception {

		sumatoriaNomValCur = DecimalFormat.getNumberInstance().parse(nomValCur.trim()).doubleValue();
		sumatoriaCer = DecimalFormat.getNumberInstance().parse(cer.trim()).doubleValue();
		sumatoriaNomVal = DecimalFormat.getNumberInstance().parse(nomVal.trim()).doubleValue();
		ArrayList<String> newList = new ArrayList<String>();

		double totalMexicoGaranValCurSum = mexicoGaranValCurSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalMexicoGaranCerSum = mexicoGaranCerSum.stream().mapToDouble(Double::doubleValue).sum();
		double totalMexicoGaranNomValSum = mexicoGaranNomValSum.stream().mapToDouble(Double::doubleValue).sum();

		conection.conecGBO();
		if (pais.equals(validapais)) {
			/**
			 * se valida si son garantias
			 */
			if (constan.getGarantia().contains(instrumentos)) {
				mexicoGaran.add(systCode);
				sumatoria.suma(nomValCur, cer, nomVal);
				mexicoGaranValCurSum.add(sumatoriaNomValCur);
				mexicoGaranCerSum.add(sumatoriaCer);
				mexicoGaranNomValSum.add(sumatoriaNomVal);
			}
			/**
			 * se valida si son avales
			 */
			if (constans.getAvales().contains(instrumentos)) {

				objs.getAval().bonos(fechaConsumo, systCode, deal, pais, nomValCur, cer, nomVal);
				sumatoria.suma(nomValCur, cer, nomVal);
				info = conection.getContraparte(fechaConsumo, deal, pais);
				contraparte.addAll(info);
				instrumento.put("AVALES",objs.getAval());
			}
			/**
			 * se valida si son derivados
			 */
			if (constan.getDeriva().contains(instrumentos)) {
				objs.getDerivados().bonos(fechaConsumo, systCode, deal, pais, nomValCur, cer, nomVal);
				sumatoria.suma(nomValCur, cer, nomVal);
				info = conection.getContraparte(fechaConsumo, deal, pais);
				contraparte.addAll(info);
				instrumento.put("DERIVADOS",objs.getDerivados());
			}
			/**
			 * se valida son lineas no comprometidas
			 */
			if (constan.getNocomprome().contains(instrumentos)) {
				objs.getNocompro().bonos(fechaConsumo, systCode, deal, pais, nomValCur, cer, nomVal);
				sumatoria.suma(nomValCur, cer, nomVal);
				info = conection.getContraparte(fechaConsumo, deal, pais);
				contraparte.addAll(info);
				instrumento.put("LINEAS NO COMPROMETIDAS",objs.getNocompro());
			}
			/**
			 * se valida si es leassing - reating
			 */
			if (constan.getLearent().contains(instrumentos)) {
				objs.getLeasrent().bonos(fechaConsumo, systCode, deal, pais, nomValCur, cer, nomVal);
				sumatoria.suma(nomValCur, cer, nomVal);
				info = conection.getContraparte(fechaConsumo, deal, pais);
				contraparte.addAll(info);
				instrumento.put("LEASING - RENTING",objs.getLeasrent());
			}
			/**
			 * se valida son overdrafs
			 */
			if (constan.getOverdraf().contains(instrumentos)) {
				objs.getOver().bonos(fechaConsumo, systCode, deal, pais, nomValCur, cer, nomVal);
				sumatoria.suma(nomValCur, cer, nomVal);
				info = conection.getContraparte(fechaConsumo, deal, pais);
				contraparte.addAll(info);
				instrumento.put("OVERDRAFTS",objs.getOver());
			}

			/**
			 * se valida que las garantias que se encuentren en una contraparte ya no se
			 * repitan asi que se eliminan del array final antes de ser enviadas a escribir
			 */

			for (String element : mexicoGaran) {
				if (!contraparte.contains(element)) {
					newList.add(element);
				}
			}

			/**
			 * se obtienen los array que guardan las cantidades de nominal value cur, cer y
			 * nominal value para ser sumados y enviados al metodo que realizara la
			 * escritura de la unterfaz
			 */
			 cadenaMexicoGaran = newList.stream().collect(Collectors.joining(""));

			cont++;
			if (registros == cont) {
				Csv interfazCsv = new Csv();
				interfazCsv.setSuma(sumatoria);
				interfazCsv.interfazCsvPrimeraParte(instrumento, newList, cadenaMexicoGaran, nombreInterfaz,
				totalMexicoGaranValCurSum, totalMexicoGaranCerSum, totalMexicoGaranNomValSum, pais);
			}
		} else {
			/**
			 * en dado caso que el pais
			 * cambie se escribira el pais evaluado 
			 * con todos los instrumentos
			 */		
			Csv interfazCsv = new Csv();
			interfazCsv.setSuma(sumatoria);
			interfazCsv.interfazCsvPrimeraParte(instrumento, newList, cadenaMexicoGaran, nombreInterfaz,
					totalMexicoGaranValCurSum, totalMexicoGaranCerSum, totalMexicoGaranNomValSum, validapais);
			cont++;
			validapais = pais;
			obj.getSumatoria().limpiar();
			instrumento.clear();
			objs.getDerivados().limpiar();
			objs.getAval().limpiar();
			objs.getNocompro().limpiar();
			objs.getLeasrent().limpiar();
			objs.getOver().limpiar();
			obj.getBono().limpiar();
			obj.getComex().limpiar();
			obj.getComprome().limpiar();
			obj.getConfirming().limpiar();
			obj.getDescuentos().limpiar();
			obj.getDocumentariado().limpiar();
			obj.getFactoring().limpiar();
			obj.getImpexp().limpiar();
			obj.getSindicado().limpiar();	
			sumatoria.limpiar();
			System.gc();
		}
	}

}
