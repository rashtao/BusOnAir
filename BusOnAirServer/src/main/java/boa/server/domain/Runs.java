package boa.server.domain;

import java.util.ArrayList;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

import boa.server.importer.domain.RouteImporter;
import boa.server.importer.domain.RunImporter;

public class Runs {
    protected Index<Node> runsIndex;
    protected Index<Node> runningBuses;

    protected static Runs instance = null;
    
    public static synchronized Runs getRuns() {
        if (instance == null) 
            instance = new Runs();
        return instance;
    }    
    
    protected Runs(){
        runsIndex = DbConnection.getDb().index().forNodes("runsIndex");
        runningBuses = DbConnection.getDb().index().forNodes("runningBuses");
    }
    
    public void addRun(Run r){
        runsIndex.add(r.getUnderlyingNode(), "id", r.getId());
    }
    
	public void deleteAllRuns() {
		for(Run r : getAll()){
			deleteRun(r);
		}
	}
	
	public void deleteRun(Run run) {
		for(CheckPoint cp : run.getAllCheckPoints()){
			run.deleteCheckPoint(cp);
		}	
		
		run.deleteCpSpatialIndex();		
		run.deleteCpIndex();
		
		run.setFirstStop(null);
		
		for(Stop s : run.getAllStops()){
			Stops.getStops().deleteStop(s);
		}	

		runsIndex.remove(run.getUnderlyingNode());
		runningBuses.remove(run.getUnderlyingNode());
		run.getRoute().removeRun(run);

		run.setRoute(null);	
		run.getUnderlyingNode().delete();		
	}
    
    public void addRunningBus(Run r){
    	runningBuses.add(r.getUnderlyingNode(), "id", r.getId());    		
    }
    
    public Run getRunById(Integer id){
        IndexHits<Node> result = runsIndex.get("id", id);
        Node n = result.getSingle();
        result.close();
        if(n == null){
            return null;
        } else {
            return new Run(n);                
        }
    }

    public Run getRunningBusById(Integer id){
        IndexHits<Node> result = runningBuses.get("id", id);
        Node n = result.getSingle();
        result.close();
        if(n == null){
            return null;
        } else {
            return new Run(n);                
        }
    }

    public ArrayList<Run> getAll() {
        ArrayList<Run> output = new ArrayList<Run>();
        IndexHits<Node> result = runsIndex.query("id", "*");
        for(Node n : result){
            output.add(new Run(n));           
        }     
        result.close();
        return output;
    }
    
    public ArrayList<Run> getAllRunningBuses() {
        ArrayList<Run> output = new ArrayList<Run>();
        IndexHits<Node> result = runningBuses.query("id", "*");
        for(Node n : result){
            output.add(new Run(n));           
        }     
        result.close();
        return output;
    }

    public void removeRunningBus(Run r){
    	runningBuses.remove(r.getUnderlyingNode());
    }
    
    public void updateIndex(Run r){
    	runsIndex.remove(r.getUnderlyingNode());
    	addRun(r);
    }
    
    public Run createOrUpdateRun(boa.server.importer.json.Run jr){
		// creates a new Run having the specified id
    	// if the id already exists then updates the corresponding db record

    	Run r = Runs.getRuns().getRunById(jr.getId());
	  	if(r != null){	// update
	  		Route oldRoute = r.getRoute();
	  		oldRoute.removeRun(r);
	  	} else {		// create
	    	r = new RunImporter(
	    			  DbConnection.getDb().createNode(), 
		  			  jr.getId());
	    	addRun(r);
	  	}
	  		  	
  		r.setRoute(Routes.getRoutes().getRouteById(jr.getRoute()));
  		r.setFirstStop(Stops.getStops().getStopById(jr.getFirstStop()));
  		r.setFirstCheckPoint(r.getCheckPointById(jr.getFirstCheckPoint()));
	  	return r;
	}	    
        
}
