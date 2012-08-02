package boa.server.importer;

import java.io.File;
import java.io.IOException;

import org.codehaus.groovy.tools.shell.commands.ClearCommand;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.impl.util.*;

import boa.server.domain.DbConnection;
import boa.server.test.ImportTest;
import boa.server.importer.json.*;

public class Importer2 {

    private static BusonairSql readData;
    private static DBInserter dbInserter;  

    public static void main(String[] args) {     
    	DbConnection.clear();
		DbConnection.createEmbeddedDbConnection();
		
		List<Station> list = new LinkedList<Station>();
		readData = XMLReader.readStations();
		for (StationSql station : readData.getStationList()){
			System.out.print("\n" + station);			
			list.add(station.toJSON());
		}

		Stations ss = new Stations(list);
		Transaction tx = DbConnection.getDb().beginTx();
		try{
			boa.server.domain.Stations.getStations().createOrUpdateStations(ss);
			tx.success();
		}finally{
			tx.finish();			
		}    
		
// TEST LETTURA FILEs XML
		readData = XMLReader.readRoutes();
		for (RouteSql route : readData.getRouteList()){
			System.out.print("\n" + route);			
		}	
	
	

		readData = XMLReader.readStops();
		for (StopSql stop : readData.getStopList()){
			System.out.print("\n" + stop);			
		}	

        ImportTest.importTest();
        DbConnection.turnoff();
		

	}
    
    


}
