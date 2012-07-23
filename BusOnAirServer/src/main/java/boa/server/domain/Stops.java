package boa.server.domain;

import java.util.ArrayList;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

import boa.server.importer.domain.StationImporter;
import boa.server.importer.domain.StopImporter;

public class Stops {
    protected Index<Node> stopsIndex;

    protected static Stops instance = null;
    
    public static synchronized Stops getStops() {
        if (instance == null) 
            instance = new Stops();
        return instance;
    }    
    
    protected Stops(){
        stopsIndex = DbConnection.getDb().index().forNodes("stopsIndex");
    }
    
    public void addStop(Stop s){
        stopsIndex.add(s.getUnderlyingNode(), "id", s.getId());
    }
    
    public void removeStop(Stop s){
    	stopsIndex.remove(s.getUnderlyingNode());
    }
    
    public Stop getStopById(int id){
        IndexHits<Node> result = stopsIndex.get("id", id);
        Node n = result.getSingle();
        result.close();
        if(n == null){
            return null;
        } else {
            return new Stop(n);                
        }
    }

    public ArrayList<Stop> getAll() {
        ArrayList<Stop> output = new ArrayList<Stop>();
        IndexHits<Node> result = stopsIndex.query("id", "*");
        for(Node n : result){
            output.add(new Stop(n));           
        }     
        result.close();
        return output;
    }
    
    public Stop createOrUpdateStop(boa.server.importer.json.Stop js){
		// creates a new stop having the specified id
    	// if the id already exists then updates the corresponding db record

    	Stop s = getStopById(js.getId());
  		Station staz = Stations.getStations().getStationById(js.getStation());
  		Run r = Runs.getRuns().getRunById(js.getRun());
  		
  		if(staz == null || r == null)
  			return null;

    	if(s != null){
	  		s.setStaticTime(js.getStaticTime());
	  		s.updateStopPosition();
	  		s.setRun(Runs.getRuns().getRunById(js.getRun()));	  		
	  	} else {	  		
	  		s = new StopImporter(
		  			  DbConnection.getDb().createNode(), 
		  			  js.getId(),
		  			  js.getStaticTime(),
		  			  staz,
		  			  r);
	  	}

    	s.updateStopPosition();
    	staz.updateStop(s);
    	
    	//TODO:
    	//gestire next/prev in RUN e next/prev in STATION 
    	
    	
	  	return s;
	}	
    
    
}
