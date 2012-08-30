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
    	DbConnection.deleteDbFiles();
		DbConnection.createEmbeddedDbConnection();
		
		// read Stations from XML
		readData = XMLReader.readStations();
		
		// --- STATIONS CREATION ---
		Map<Integer, Station> stationsById = new LinkedHashMap<Integer, Station>();
		for (StationSql station : readData.getStationList()){
			//System.out.print("\n" + station);
			Station jsStation = station.toJSON();
			stationsById.put(jsStation.getId(), jsStation);
		}
		// --- END STATIONS CREATION ---
		
		// read Stops from XML
		readData = XMLReader.readStops();
		
		// --- ROUTES CREATION ---
		Map<String, Route> routes = new HashMap<String, Route>();
		for (StopSql current : readData.getStopList()){
			Route route = new Route();
			route.setLine(current.line + current.note);
			routes.put(current.line + current.note, route);			
		}	
		
		// routesById LinkedHashMap creation
		int i = 0;
		Map<Integer, Route> routesById = new LinkedHashMap<Integer, Route>();
		for (Route route : routes.values()){
			route.setId(i);
			routesById.put(i, route);
			i++;
		}
		// --- END ROUTES CREATION ---
		
		// --- RUNS CREATION ---
		Map<String, Run> runs = new HashMap<String, Run>();
		for (StopSql current : readData.getStopList()){
			Run run = new Run();
			run.setRoute(routes.get(current.line + current.note).getId());
			runs.put(current.runcode, run);
		}			
			
		// runsById LinkedHashMap creation
		int j = 0;
		Map<Integer, Run> runsById = new LinkedHashMap<Integer, Run>();
		for (Run run : runs.values()){
			run.setId(j);			
			runsById.put(run.getId(), run);
			j++;
		}		
		// --- END RUNS CREATION ---
		
		// --- STOPS CREATION ---
		Map<Integer, Stop> stopsById = new LinkedHashMap<Integer, Stop>();
		for (StopSql current : readData.getStopList()){
			Stop stop = new Stop();
			stop.setId(Integer.parseInt(current.Id_Stop));
			stop.setRun(runs.get(current.runcode).getId());
			stop.setStaticTime(current.getMinutesFromMidn());
			stop.setStation(current.src);
			stopsById.put(stop.getId(), stop);
		}
		
		//LINK STOPS prevInRun and nextInRun
		Stop prev = null;
		for (Stop current : stopsById.values()){		
			if(prev != null){
				if(prev.getRun() == current.getRun()){
					prev.setNextInRun(current.getId());
					current.setPrevInRun(prev.getId());
				}
			}			
			prev = current;
		}			
		// --- STOPS CREATION ---			
		
		// SET RUN.FIRSTSTOP 
		for (Stop current : stopsById.values()){				 
			if(current.getPrevInRun() == null){
				//System.out.print("\njsSTOP: " + current);
				Run run = runsById.get(current.getRun());
				run.setFirstStop(current.getId());
			}	
		}
		

		// SET ROUTE.FROM e ROUTE.TOWARDS
		for (Run run : runsById.values()){
			Route route = routesById.get(run.getRoute());
			Stop firstStop = stopsById.get(run.getFirstStop());
			Stop lastStop = firstStop;
			while(lastStop.getNextInRun() != null){
				lastStop = stopsById.get(lastStop.getNextInRun());
			}
			
			route.setFrom(firstStop.getStation());
			route.setTowards(lastStop.getStation());
			
		}
		
		
		// IMPORT DATA INTO BOA SERVER DB
		Stations stationsDb = new Stations(stationsById.values());
		Routes routesDb = new Routes(routesById.values());
		Runs runsDb = new Runs(runsById.values());
		Stops stopsDb = new Stops(stopsById.values());

		boa.server.domain.Stations.getStations().createOrUpdateStations(stationsDb);
		boa.server.domain.Routes.getRoutes().createOrUpdateRoutes(routesDb);
		System.out.print("\n ----- 0");
		
		boa.server.domain.Runs.getRuns().createOrUpdateRuns(runsDb);
		System.out.print("\n ----- 1");
		
		System.out.print("\n \t\t\t...importing " + stopsDb.stopsObjectsList.size() + " stops...");
		boa.server.domain.Stops.getStops().createOrUpdateStops(stopsDb);
		System.out.print("\n ----- 2");
		
		boa.server.domain.Stops.getStops().createOrUpdateStops(stopsDb);
		System.out.print("\n ----- 3");
	
		boa.server.domain.Runs.getRuns().createOrUpdateRuns(runsDb);
		System.out.print("\n ----- 4");
		
		// CHECKPOINTS CREATION
		for(boa.server.domain.Run r : boa.server.domain.Runs.getRuns().getAll()){
    		Transaction tx = DbConnection.getDb().beginTx();
    		try{
    			r.createAllCheckPoints();
    			tx.success();
    		}finally{
    			tx.finish();			
    		}       		
		}
		
		
		
//		// check corrispondenza run --> route (verifico se tutte le run di una route hanno le stesse fermate)
//		// in caso trovo run aventi la sequenza di stazioni diverse associate alla stessa route allora devo creare una nuova route associata!		
//		Map<String, List<Station>> routesStationsList = new LinkedHashMap<String, List<Station>>();
//		for(Route route : routesById.values()){
//			List<Station> stationsList = new LinkedList<Station>(); 
//			for(Run run : runs.values()){
//				if(run.getRoute() != route.getId())
//					continue;
//				
//				Stop stop = stopsById.get(run.getFirstStop());
//				Station station = stationsById.get(stop.getStation());
//				
//				
//			}
//		}
		
		

        ImportTest.importTest();
        DbConnection.turnoff();
		

	}

}
