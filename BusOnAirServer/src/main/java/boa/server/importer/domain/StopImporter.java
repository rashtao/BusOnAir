package boa.server.importer.domain;

import org.neo4j.graphdb.Node;

import boa.server.domain.DbConnection;
import boa.server.domain.Route;
import boa.server.domain.Routes;
import boa.server.domain.Run;
import boa.server.domain.Runs;
import boa.server.domain.Station;
import boa.server.domain.Stations;
import boa.server.domain.Stop;
import boa.server.domain.Stops;

public class StopImporter extends Stop {

	

    public StopImporter(Node node, int id) {
    	super(node);
    	setId(id);
    }  
    
    public StopImporter(Node node, int id, int time, int idStation, int idRun, String line){
        this(node, id);
        setTime(time);	    
        setType();
        Station s = Stations.getStations().getStationById(idStation);
        setStation(s);        
        setRun(idRun, line);          
        Stops.getStops().addStop(this);
        s.addStop(this);
    }   	
	
    public StopImporter(Node node, int id, int time, Station s, Run r){
        this(node, id);
        setTime(time);	    
        setType();
        setStation(s);        
        setRun(r);          
        Stops.getStops().addStop(this);
        s.addStop(this);
    }   	
    
    protected void setRun(int idRun, String line) {
    	Route r = Routes.getRoutes().getRouteByLine(line);
    	RouteImporter route;
        if(r == null){
            route = new RouteImporter(DbConnection.getDb().createNode(), line);
        } else {
        	route = new RouteImporter(r);        
        }
        Routes.getRoutes().addRoute(route);
        
        Run rr = Runs.getRuns().getRunById(idRun);

        RunImporter run;
        
        
        if(rr == null){
            run = new RunImporter(DbConnection.getDb().createNode(), idRun);
        } else {
        	 run = new RunImporter(rr);
        }

        run.setRoute(route);
        Runs.getRuns().addRun(run);
        route.addRun(run);        
        setRun(run);
    }

}
