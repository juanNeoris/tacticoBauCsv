package gettersetter;

import lombok.Getter;
import lombok.Setter;

/**
 * GetterSetterBd se generan los getter and setter de la clase para obtener las
 * variables de conexion de un archivo properties
 */
public class GetterSetterBd {

	/**
	 * obtener host del archivo properties
	 */
	@Getter
	private String host;
	/**
	 * obtener usuario del archivo properties
	 */
	@Getter
	private String usuario;
	/**
	 * obtener contrasena del archivo properties
	 */
	@Getter
	private String contrasena;
	/**
	 * obtener jdbc del archivo properties
	 */
	@Getter
	private String jdbc;
	/**
	 * obtener puerto del archivo properties
	 */
	@Getter
	private Integer puerto;
	/**
	 * obtener sid del archivo properties
	 */
	@Getter
	private String sid;
	/**
	 * obtener url del archivo properties
	 */
	@Setter
	@Getter
	private String url;
}
