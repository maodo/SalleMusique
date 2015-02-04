package connecteur;

import java.sql.Connection;
import java.sql.DriverManager;

public class Connexion {
	static Connection laConnexion;
	
	public static Connection getInstance() throws Exception{
		if(laConnexion == null){
			try{
				Class.forName("org.hsqldb.jdbcDriver").newInstance();
				laConnexion = DriverManager.getConnection("jdbc:hsqldb:file:database/BaseSalleMusique;shutdown=true", "sa",  "");
				System.out.println("Connexion réussis à HSQLDB");
			}catch(Exception e){
				System.out.println("Connexion échouée à HSQLDB");
			}
		}
		return laConnexion;
	}
}
