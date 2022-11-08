package validacion;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * clase que realiza validacion de los ficheros
 */

public final class Validaficheros {

	/**
	 * Contructor vacio
	 */
	private Validaficheros() {
		// Constructor vacio para prevenir acceso ilegal
	}

	/**
	 * metodo que valida ficheros
	 * 
	 * @param l recive la fecha para realizar el calculo del nombre de la fecha
	 * @return fechaCal regrese el mensaje en dado caso que el archivo no exista en
	 *         la ruta definida
	 *
	 */
	public static String validaFicherosDolphinVictoria(long l) {
		SimpleDateFormat sdfx = new SimpleDateFormat("yyyyMMdd");
		String fechaCal = sdfx.format(l);

		File filDolphin = new File("\\\\mx2ct1hnascifnfsevs1.mx.corp\\ExtraccionesMIR\\rtra\\"
				+ "rtra-cream-ges-dolphin-europa_mexico_" + fechaCal + ".txt");
		File filVictoria = new File("\\\\mx2ct1hnascifnfsevs1.mx.corp\\ExtraccionesMIR\\rtra\\"
				+ "rtra-cream-ges-victoria-europa_mexico_" + fechaCal + ".txt");

		if (!filDolphin.exists() || !filVictoria.exists()) {
			fechaCal = "Sin rtra Victoria o Dolphing, coloca tus ficheros.";
		} else {
			fechaCal = "Los insumos existen, cargando...";
		}
		return fechaCal;
	}
}
