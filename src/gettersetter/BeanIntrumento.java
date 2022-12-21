package gettersetter;

import lombok.Getter;
import lombok.Setter;

/**
 * Clase bean que guardara y asigara a las variables declaradas el resul
 * obtenido de la ejecucion del paquete en la clase {@link Conexion}
 * 
 * @author Z363772
 * @since 01/12/2022
 */
public class BeanIntrumento {
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String cptyparent;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String cptyparentrating;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String cptyparentname;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String dealstamp;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String instrumentname;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String valuedate;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String maturitydate;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String currency;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String nominalvaluecur;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String cer;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String nominalvalue;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String oneoff;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String cptyname;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String foldercountryname;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String cptycountry;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String cptyparentcountry;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String foldercountry;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String cpty;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String lastparent;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String lastparentname;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String lastparentcountry;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String lastparentrating;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String foldername;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String guaranteepercent_cpty;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String addoncur;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String guaranteedparentrating;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String recequivalente;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String recbruto;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String lastparentf;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String lastparentfname;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String lastparentfcountryname;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String lastparentfrating;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String dispuesto;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String cer2;
	/**
	 * bean con getter and setter
	 */
	@Getter
	@Setter
	private String collateralagreement;

	@Override
	public String toString() {
		return this.cptyparent + "|" + this.cptyparentrating + "|" + this.cptyparentname + "|" + this.dealstamp + "|"
				+ this.instrumentname + "|" + this.valuedate + "|" + this.maturitydate + "|" + this.currency + "|"
				+ this.nominalvaluecur + "|" + this.cer + "|" + this.nominalvalue + "|" + this.oneoff + "|"
				+ this.cptyname + "|" + this.foldercountryname + "|" + this.cptycountry + "|" + this.cptyparentcountry
				+ "|" + this.foldercountry + "|" + this.cpty + "|" + this.lastparent + "|" + this.lastparentname + "|"
				+ this.lastparentcountry + "|" + this.lastparentrating + "|" + this.foldername + "|"
				+ this.guaranteepercent_cpty + "|" + this.addoncur + "|" + this.guaranteedparentrating + "|"
				+ this.recequivalente + "|" + this.recbruto + "|" + this.lastparentf + "|" + this.lastparentfname + "|"
				+ this.lastparentfcountryname + "|" + this.lastparentfrating + "|" + this.dispuesto + "|" + this.cer2
				+ "|" + this.collateralagreement;
	}

	/**
	 * metodo toStringTotales que armara la cadena para los totales generales de
	 * cada pais
	 * 
	 * @return cadena armada
	 */
	public String toStringTotales() {

		return this.cptyparent + "|" + this.cptyparentrating + "|" + this.cptyparentname + "|" + this.dealstamp + "|"
				+ this.instrumentname + "|" + this.valuedate + "|" + this.maturitydate + "|" + this.currency + "|"
				+ this.nominalvaluecur + "|" + this.cer + "|" + this.nominalvalue + "|" + this.oneoff + "|"
				+ this.cptyname + "|" + this.foldercountryname + "|" + this.cptycountry + "|" + this.cptyparentcountry
				+ "|" + this.foldercountry + "|" + this.cpty + "|" + this.lastparent + "|" + this.lastparentname + "|"
				+ this.lastparentcountry + "|" + this.lastparentrating + "|" + this.foldername + "|"
				+ this.guaranteepercent_cpty + "|" + this.addoncur + "|" + this.guaranteedparentrating + "|"
				+ this.recequivalente + "|" + this.recbruto + "|" + this.lastparentf + "|" + this.lastparentfname + "|"
				+ this.lastparentfcountryname + "|" + this.lastparentfrating + "|" + this.dispuesto + "|" + this.cer2
				+ "|" + this.collateralagreement;

	}

}
