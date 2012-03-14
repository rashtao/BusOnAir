package boa.server.test;


import org.neo4j.graphdb.GraphDatabaseService;

import boa.server.domain.*;



public class DelayTest {

	private static GraphDatabaseService db;
    

    public static void main(String[] args) {     
		DbConnection.createEmbeddedDbConnection();
		db = DbConnection.getDb();

		Run r = Runs.getRuns().getRunById(11);
		r.restore();
		
		CheckPoint cp = r.getFirstCheckPoint().getNextCheckPoint();
		Stop s = cp.getTowards();
		Station staz = s.getStation();
		Stop fsStaz = staz.getFirstStopFromTime(0);

		System.out.print("CheckPoint:\n" + cp + "\n\n\n");
		System.out.print("Stop:\n" + s + "\n\n\n");
		
		
		System.out.print("\nPrima:\n");
		System.out.print("\nRun:\n");
		System.out.print(r.toString());
		System.out.print("\nStation:\n");

		while(fsStaz != null){
			System.out.print("-->" + fsStaz);
			fsStaz = fsStaz.getNextInStation();
		}

		r.checkPointPass(cp, 445);

		System.out.print("\nDopo:\n");
		System.out.print("\nRun:\n");
		System.out.print(r.toString());
		System.out.print("\nStation:\n");

		fsStaz = staz.getFirstStopFromTime(0);
		while(fsStaz != null){
			System.out.print("-->" + fsStaz);
			fsStaz = fsStaz.getNextInStation();
		}
		
		
		DbConnection.turnoff();
	}

}
