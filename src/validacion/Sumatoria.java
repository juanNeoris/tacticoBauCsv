package validacion;

import java.text.DecimalFormat;
import java.text.ParseException;

import lombok.Getter;
import lombok.Setter;

/**
 * Clase que se encarga de 
 * llevar el conteo de las sumatorias 
 * generales de cada Pais
 */
public class Sumatoria {
	
	/**
	 * totalGenBonosNomValCur
	 * Total para nominal value cur
	 */
	@Setter
	@Getter
	public static double totalGenBonosNomValCur;
	/**
	 * totalGenBonosCer
	 * total para cer
	 */
	@Setter
	@Getter
	public static double totalGenBonosCer;
	/**
	 * totalGenBonosNomVal
	 * total para nominal value
	 */
	@Setter
	@Getter
	public static double totalGenBonosNomVal;
	
   /**
	 * metodo sumatoria se encarga de sumar para todos los 
	 * intrumentos
	 * @param rsGetString9 nominalvaluecur guarda los datos numerico 
	 * @param rsGetString10 cer guarda los datos numerico 
	 * @param rsGetString11 nominalvalue guarda los datos numerico 
	 * @throws ParseException excepcion generada por el parseo de informacion
	 */
	public void suma(String rsGetString9, String rsGetString10, String rsGetString11) throws ParseException {	
		double sumatoriaNomValCur = DecimalFormat.getNumberInstance().parse(rsGetString9.trim()).doubleValue();
		double sumatoriaCer = DecimalFormat.getNumberInstance().parse(rsGetString10.trim()).doubleValue();
		double sumatoriaNomVal = DecimalFormat.getNumberInstance().parse(rsGetString11.trim()).doubleValue(); 
		/**
		 * se crea la sumatoria para el intrumento evaluado
		 * y es lo que se usa para generar la interfaz 
		 */			
		totalGenBonosNomValCur = totalGenBonosNomValCur+sumatoriaNomValCur;
		totalGenBonosCer = totalGenBonosCer+sumatoriaCer;	
		totalGenBonosNomVal = totalGenBonosNomVal+sumatoriaNomVal;
		
	}
	
	/**
	  * Metdo que limpiara los array para cuando se opere otros paises
	  * metodo limpiar
	  */
	public void limpiar() {
		totalGenBonosNomValCur=0;
		totalGenBonosCer=0;
		totalGenBonosNomVal=0;
	}


	
		
	
}
