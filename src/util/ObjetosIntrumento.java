package util;

import interfaz.AvalBonos;
import lombok.Getter;
import lombok.Setter;

/**
 * clase que se encarga de 
 * los onturmentos
 */
public class ObjetosIntrumento {
	/**
	 * instancia de instrumento tarjeta
	 */
	@Getter
	@Setter
	protected AvalBonos tarjeta = new AvalBonos("TARJETAS DE CREDITO");
	/**
	 * instancia de instrumento leasrent
	 */
	@Getter
	@Setter
	protected AvalBonos leasrent = new AvalBonos("LEASING - RENTING");

	/**
	 * instancia de instrumento nocompro
	 */
	@Getter
	@Setter
	protected AvalBonos nocompro = new AvalBonos("LINEAS NO COMPROMETIDAS");

	/**
	 * instancia de instrumento over
	 */
	@Getter
	@Setter
	protected AvalBonos over = new AvalBonos("OVERDRAFTS");
	/**
	 * instancia de instrumento derivados
	 */
	@Getter
	@Setter
	protected AvalBonos derivados = new AvalBonos("DERIVADOS");

	/**
	 * instancia de instrumento aval
	 */
	@Getter
	@Setter
	protected AvalBonos aval = new AvalBonos("AVALES");
}
