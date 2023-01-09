package util;

import java.io.InputStream;

/**
 * clase {@link ResourceLoader} carga las imagenes guardadas en el resource 
 * @author Z363772
 *
 */
public class ResourceLoader {
/**
 * metodo que carga el logo de santander
 * @param path nombre de la imagen que va a cargar
 * @return input regresa la img 
 */
	public static InputStream load(String path) {
		
		InputStream input = new ResourceLoader().getClass().getResourceAsStream(path);
		if(input==null) {
			input =ResourceLoader.class.getResourceAsStream(".//sources//"+path);
		}
		return input;
	}
}
