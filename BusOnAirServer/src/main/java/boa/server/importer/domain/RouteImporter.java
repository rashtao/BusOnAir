package boa.server.importer.domain;

import org.neo4j.graphdb.Node;

import boa.server.domain.DbConnection;
import boa.server.domain.Route;
import boa.server.domain.Run;
import boa.server.domain.Station;
import boa.server.domain.Stations;

public class RouteImporter extends Route {
    
    protected static int count = 0;
    protected static int runCount = 0;
    
	public RouteImporter(Node node, String line){
		this(node, node.getId(), line, null, null);
	}   
	
    public RouteImporter(Route r) {
		super(r.getUnderlyingNode());
	}

	public void addRun(Run r){
        runIndex.add(r.getUnderlyingNode(), "id", runCount++);
    }

    public void clearIndex(){
    	for(Run r : getAllRuns()){
    		runIndex.remove(r.getUnderlyingNode());
    	}    	
    }

	public RouteImporter(Node node, long id, String line, Long from, Long towards){
		super();
        underlyingNode = node;
        setLine(line);
	    setType();
	    setId(id);
	    Station fromS = null;
	    Station towS = null;
	    
	    if(from != null){
	    	fromS = Stations.getStations().getStationById(from);
	    } 
	    
	    if(towards != null){
	    	towS = Stations.getStations().getStationById(towards);
	    } 
	    
	    setFrom(fromS);
	    setTowards(towS);
        runIndex = DbConnection.getDb().index().forNodes("runIndex" + getId());		
	}
}
