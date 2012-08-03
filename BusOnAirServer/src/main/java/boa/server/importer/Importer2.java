package boa.server.importer;

import java.io.File;
import java.io.IOException;

import org.codehaus.groovy.tools.shell.commands.ClearCommand;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
		
		
		// STATIONS IMPORT
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

		
		// STOPS, RUNS, ROUTES IMPORT
		readData = XMLReader.readStops();
		
		//ROUTES CREATION
		Map<String, Route> routes = new HashMap<String, Route>();
		for (StopSql current : readData.getStopList()){
			Route route = new Route();
			route.setline(current.line + current.note);
			routes.put(current.line + current.note, route);			
		}	
		
		//Routes id
		int i = 0;
		for (Route route : routes.values()){
			route.setId(i);
			i++;
		}	
		
		//RUNS CREATION
		Map<String, Run> runs = new HashMap<String, Run>();
		for (StopSql current : readData.getStopList()){
			Run run = new Run();
			run.setRoute(routes.get(current.line + current.note).getId());
			runs.put(current.runcode, run);
		}			
		
		//Runs id
		int j = 0;
		for (Run run : runs.values()){
			run.setId(j);
			j++;
		}	
		

		//IMPORT STOPS
		Map<Integer, Stop> stops = new LinkedHashMap<Integer, Stop>();
		for (StopSql current : readData.getStopList()){
			Stop stop = new Stop();
			stop.setId(Integer.parseInt(current.Id_Stop));
			stop.setRun(runs.get(current.runcode).getId());
			stop.setStaticTime(current.getMinutesFromMidn());
			stop.setStation(current.src);
			stops.put(stop.getId(), stop);
		}
		
		//LINK STOPS prevInRun and nextInRun
		Stop prev = null;
		int ctrl = 0;
		for (Stop current : stops.values()){		
			if(prev != null){
				if(prev.getRun() == current.getRun()){
					prev.setNextInRun(current.getId());
					current.setPrevInRun(prev.getId());
				} else {
					ctrl++;	// conta una run
				}
			}			
			prev = current;
		}			
		
		System.out.print("\nCTRL: " + ctrl);		
		System.out.print("\t-\t" + runs.size());		
		
		
//// TEST LETTURA FILEs XML
//		readData = XMLReader.readRoutes();
//		for (RouteSql route : readData.getRouteList()){
//			System.out.print("\n" + route);			
//		}	
//	
//	
//
//		readData = XMLReader.readStops();
//		for (StopSql stop : readData.getStopList()){
//			System.out.print("\n" + stop);			
//		}	

        ImportTest.importTest();
        DbConnection.turnoff();
		

	}
    
    


}
