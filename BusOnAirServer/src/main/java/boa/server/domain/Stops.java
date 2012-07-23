package boa.server.domain;

import java.util.ArrayList;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
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
  		Run run = Runs.getRuns().getRunById(js.getRun());
  		
  		if(staz == null || run == null)
  			return null;

    	if(s != null){	// update
	  		s.setStaticTime(js.getStaticTime());
	  		s.updateStopPosition();
	  		s.setRun(Runs.getRuns().getRunById(js.getRun()));	  		
	  	} else {		// create	  		
	  		s = new StopImporter(
		  			  DbConnection.getDb().createNode(), 
		  			  js.getId(),
		  			  js.getStaticTime(),
		  			  staz,
		  			  run);
	  		addStop(s);
	  	}

    	s.setNextInStation(staz.getFirstStopFromTime(0));	//insert in 1st position
    	s.updateStopPosition();		// place the stop in the correct position
    	staz.updateStopIndex(s);
    	
    	
    	//TODO:
    	//gestire next/prev in RUN e next/prev in STATION 
    	
    	
	  	return s;
	}	
    
    public void deleteStop(Stop s){
    	Run run = s.getRun();

    	Stop fsrun = run.getFirstStop();
    	
    	if(s.equals(fsrun))
    		run.setFirstStop(s.getNextInRun());
		
    	Stop pis = s.getPrevInStation();
		Stop nis = s.getNextInStation();
		
		Stop pir = s.getPrevInRun();
		Stop nir = s.getNextInRun();
		
		Station staz = s.getStation();
		
		if(pis != null){
			pis.setNextInStation(nis);
		} 

		if(pir != null){
			pir.setNextInRun(nir);
		} 

        Iterable<Relationship> rels = s.getUnderlyingNode().getRelationships(RelTypes.CHECKPOINTFROM, Direction.INCOMING);
        for(Relationship r : rels){
            run.deleteCheckPoint(new CheckPoint(r.getStartNode()));
        }
		
        rels = s.getUnderlyingNode().getRelationships(RelTypes.CHECKPOINTTOWARDS, Direction.INCOMING);
        for(Relationship r : rels){
            run.deleteCheckPoint(new CheckPoint(r.getStartNode()));
        }
				
		staz.removeStop(s);
		s.setRun(null);
		s.setStation(null);
		s.setNextInRun(null);
		s.setNextInStation(null);
		removeStop(s);
		s.getUnderlyingNode().delete();			
    }
    
}
