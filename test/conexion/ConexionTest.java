package conexion;

import static org.junit.Assert.*;

import org.junit.Test;

public class ConexionTest {

	@Test
	public void conecGBO() {
	Conexion con = new Conexion();
	
	try {
		con.conecGBO();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}

}
