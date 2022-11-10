package util;

import interfaz.AvalBonos;
import lombok.Getter;
import lombok.Setter;
/**
 * clase que se encarga de 
 * los onturmentos
 */
public class ObjetosInstrumentos {

	/**
	 * instancia de instrumento sumatoria
	 */
	@Getter
	@Setter
	protected AvalBonos sumatoria = new AvalBonos("sumatoria");

	/**
	 * instancia de instrumento bono
	 */
	@Getter
	@Setter
	protected AvalBonos bono = new AvalBonos("BONOS");
	/**
	 * instancia de instrumento confirming
	 */
	@Getter
	@Setter
	protected AvalBonos confirming = new AvalBonos("CONFIRMING");
	/**
	 * instancia de instrumento documentariado
	 */
	@Getter
	@Setter
	protected AvalBonos documentariado = new AvalBonos("CREDITOS DOCUMENTARIOS");
	/**
	 * instancia de instrumento sindicado
	 */
	@Getter
	@Setter
	protected AvalBonos sindicado = new AvalBonos("CREDITOS SINDICADOS");
	/**
	 * instancia de instrumento descuentos
	 */
	@Getter
	@Setter
	protected AvalBonos descuentos = new AvalBonos("DESCUENTOS");
	/**
	 * instancia de instrumento factoring
	 */
	@Getter
	@Setter
	protected AvalBonos factoring = new AvalBonos("FACTORING");
	/**
	 * instancia de instrumento comex
	 */
	@Getter
	@Setter
	protected AvalBonos comex = new AvalBonos("FINANCIAMIENTO COMEX");
	/**
	 * instancia de instrumento impexp
	 */
	@Getter
	@Setter
	protected AvalBonos impexp = new AvalBonos("FINANCIAMIENTO IMP/EXP");
												

	/**
	 * instancia de instrumento comprome
	 */
	@Getter
	@Setter
	protected AvalBonos comprome = new AvalBonos("LINEAS COMPROMETIDAS");
	

	
}
