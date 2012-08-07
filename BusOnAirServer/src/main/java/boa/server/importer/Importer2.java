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
		
		// STATIONS IMPORT
		Map<Long, Station> stationsById = new LinkedHashMap<Long, Station>();
		readData = XMLReader.readStations();
		for (StationSql station : readData.getStationList()){
			System.out.print("\n" + station);
			Station jsStation = station.toJSON();
			stationsById.put(jsStation.getId(), jsStation);
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
			
		// routesById LinkedHashMap creation
		Map<Long, Route> routesById = new LinkedHashMap<Long, Route>();
		for (Route route : routes.values()){
			routesById.put(route.getId(), route);			
		}
		
		// runsById LinkedHashMap creation
		Map<Integer, Run> runsById = new LinkedHashMap<Integer, Run>();
		for (Run run : runs.values()){
			runsById.put(run.getId(), run);			
		}
		
//		for (Stop current : stopsById.values()){		
//			System.out.print("\njsSTOP:" 
//					+ "\n\tid:\t" + current.getId() 
//					+ "\n\trun:\t" + current.getRun() 
//					+ "\n\tstation:\t" + current.getStation() 
//					+ "\n\tpir:\t" + current.getPrevInRun());
//		}
		
		// SET RUN.FIRSTSTOP 
		for (Stop current : stopsById.values()){		
//			System.out.print("\njsSTOP:" 
//					+ "\n\tid:\t" + current.getId() 
//					+ "\n\trun:\t" + current.getRun() 
//					+ "\n\tstation:\t" + current.getStation() 
//					+ "\n\tpir:\t" + current.getPrevInRun());
			
			if(current.getPrevInRun() == 0){
				Run run = runsById.get(current.getRun());
				run.setFirstStop(current.getId());
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
		
		
		//route.from e route.tw
		
//		Stations ss = new Stations((List<Station>) stationsById.values());
//		Transaction tx = DbConnection.getDb().beginTx();
//		try{
//			boa.server.domain.Stations.getStations().createOrUpdateStations(ss);
//			tx.success();
//		}finally{
//			tx.finish();			
//		}   
		
        ImportTest.importTest();
        DbConnection.turnoff();
		

	}

}
