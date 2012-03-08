package test;

import org.neo4j.graphdb.GraphDatabaseService;

import test.utils.Chronometer;

import domain.*;

public class TestPesanteUpdate {


	private static GraphDatabaseService db;
    

    public static void main(String[] args) {     
		DbConnection.createEmbeddedDbConnection();
		db = DbConnection.getDb();

		int i = 0;

        Chronometer ch = new Chronometer();
        
        ch.start();
        
		for(Run r : Runs.getRuns().getAll()){
			Stop fs = r.getFirstStop();
			
			//System.out.print("\n-------------------\n" + r);
			
			r.checkPointPass(r.getFirstCheckPoint(), fs.getTime() + 30);
			//System.out.print("\n" + r);

			//r.restoreRun();

			i++;
		}
			
        ch.stop();
        System.out.println("\n" + ch.getSeconds());
        
		System.out.print("\n" + i);
			
		
		
		DbConnection.turnoff();
			
			
	}

}
