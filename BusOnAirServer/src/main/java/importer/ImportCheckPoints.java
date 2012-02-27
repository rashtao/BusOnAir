package importer;

import domain.DbConnection;



public class ImportCheckPoints {
	private static DBInserter dbInserter;  
	public static void main(String[] args) {        
		DbConnection.createEmbeddedDbConnection();
        dbInserter = new DBInserter();
		 		
		dbInserter.linkCheckPoints();
        DbConnection.turnoff();
}
}
