package interfaz;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import conexion.Conexion;
import lombok.Getter;

/**
 * class EvaluacionInstrumentos se van guardando en diferentes array dependiendo
 * del intrumento
 */

public class AvalBonos {

	/**
	 * cadenaBonosMex
	 */
	@Getter
	private String cadenaBonosMex;
	/**
	 * totalMexicoBonosNomValCur
	 */
	@Getter
	private double totalMexicoBonosNomValCur;
	/**
	 * totalMexicoBonosCer
	 */
	@Getter
	private double totalMexicoBonosCer;
	/**
	 * totalMexicoBonosNomVal
	 */
	@Getter
	private double totalMexicoBonosNomVal;
	
	/**
	 * mexicoBonos
	 */
	private List<String> mexicoBonos = new ArrayList<String>();
	
	/**
	 * info
	 */
	private List<String> info = new ArrayList<String>();
	@Getter
	private String instrumento;
	
	/**
	  * contructor AvalBonos
	  * @param instrumento cuando genere un objeto recibira como parametro 
	  * el instrumento al que pertenece
	  */
	public AvalBonos(String instrumento) {
		super();
		this.instrumento = instrumento;
	}
	
	/**
	 * Metodo Bonos
	 * 
	 * @param grupo         para obtener la contraparte
	 * @param fechaConsumo  obtener la contraparte
	 * @param systCode      el registro para un Bono
	 * @param rsGetString4  deal para contraparte
	 * @param rsGetString14 Pais de la contraparte
	 * @param rsGetString6  fecha de la contraparte
	 * @param rsGetString7  fecha de la contraparte
	 * @param rsGetString9  nomninal value sumatorias
	 * @param rsGetString10 cer sumatorias
	 * @param rsGetString11 nominal value cur sumatorias
	 * @throws Exception error en execucion
	 */

	public void bonos(String fechaConsumo, String systCode, String rsGetString4, String rsGetString14, String rsGetString9, String rsGetString10, String rsGetString11)
			throws Exception {
		Conexion conexion = new Conexion();

		double sumatoriaNomValCur = DecimalFormat.getNumberInstance().parse(rsGetString9.trim()).doubleValue();
		double sumatoriaCer = DecimalFormat.getNumberInstance().parse(rsGetString10.trim()).doubleValue();
		double sumatoriaNomVal = DecimalFormat.getNumberInstance().parse(rsGetString11.trim()).doubleValue(); 
		/**
		 * se agrega la dacena al array
		 */
		mexicoBonos.add(systCode);
		/**
		 * se consulta la contraparte y se coloca debajo de su operacion
		 */
		conexion.conecGBO();
		info = conexion.getContraparte(fechaConsumo, rsGetString4, rsGetString14);
		mexicoBonos.addAll(info);
		info.clear(); 
		/**
		 * se crea la sumatoria para el intrumento evaluado
		 * y es lo que se usa para generar la interfaz 
		 */
		cadenaBonosMex = mexicoBonos.stream().collect(Collectors.joining(""));
		totalMexicoBonosNomValCur = totalMexicoBonosNomValCur+sumatoriaNomValCur;
		totalMexicoBonosCer = totalMexicoBonosCer+sumatoriaCer;
		totalMexicoBonosNomVal = totalMexicoBonosNomVal+sumatoriaNomVal;

	}
	
	
	
	/**
	  * Metdo que limpiara los array para cuando se opere otros paises
	  * metodo limpiar
	  */
	public void limpiar() {
		mexicoBonos.clear();
		totalMexicoBonosNomValCur=0;
		totalMexicoBonosCer=0;
		totalMexicoBonosNomVal=0;
		
	}
		
	
	



}
