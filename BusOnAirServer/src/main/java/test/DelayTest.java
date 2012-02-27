package test;


import org.neo4j.graphdb.GraphDatabaseService;

import domain.*;


public class DelayTest {

	private static GraphDatabaseService db;
    

    public static void main(String[] args) {     
		DbConnection.createEmbeddedDbConnection();
		db = DbConnection.getDb();

		
		
		Run r = Runs.getRuns().getRunById(11);
		Stop s = Stops.getStops().getStopById(4475);
		Station staz = s.getStazione();
		Stop fsStaz = staz.getFirstStopsFromTime(0);
		
		System.out.print("\nPrima:\n");
		System.out.print("\nRun:\n");
		System.out.print(r.toString());
		System.out.print("\nStation:\n");

		while(fsStaz != null){
			System.out.print("-->" + fsStaz);
			fsStaz = fsStaz.getNextInStation();
		}
		

		r.updateRun(s, 430);

		System.out.print("\nDopo:\n");
		System.out.print("\nRun:\n");
		System.out.print(r.toString());
		System.out.print("\nStation:\n");

		fsStaz = staz.getFirstStopsFromTime(0);
		while(fsStaz != null){
			System.out.print("-->" + fsStaz);
			fsStaz = fsStaz.getNextInStation();
		}
		
		
		DbConnection.turnoff();
		
		
		
	}

}
