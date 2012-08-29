package boa.server.domain;

import java.util.*;

import org.neo4j.gis.spatial.EditableLayer;
import org.neo4j.gis.spatial.EditableLayerImpl;
import org.neo4j.gis.spatial.Layer;
import org.neo4j.gis.spatial.SpatialDatabaseRecord;
import org.neo4j.gis.spatial.SpatialDatabaseService;
import org.neo4j.gis.spatial.WKTGeometryEncoder;
import org.neo4j.gis.spatial.indexprovider.LayerNodeIndex;
import org.neo4j.gis.spatial.pipes.GeoPipeline;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

import com.vividsolutions.jts.geom.Coordinate;

import boa.server.importer.domain.StationImporter;

public class Stations {
    protected EditableLayer stationSpatialIndex;
    protected Index<Node> stationIndex;

    protected static Stations instance = null;
    
    public static Stations getStations() {
        if (instance == null) 
            instance = new Stations();
        return instance;
    }    
    
    public static void destroy() {
    	instance = null;
    }    
    
    protected Stations(){
        stationIndex = DbConnection.getDb().index().forNodes("stationsIndex");
        stationSpatialIndex = DbConnection.getSpatialDb().getOrCreatePointLayer("stationSpatialIndex", "lon", "lat");
    }
        
    public void addStationToIndex(Station s){
    	stationIndex.remove(s.getUnderlyingNode());
    	stationIndex.add(s.getUnderlyingNode(), "id", s.getId());
    }
  
    public void updateSpatialIndex(Station s){
  		stationSpatialIndex.update(
  				s.getUnderlyingNode().getId(), 
  				stationSpatialIndex.getGeometryFactory().createPoint(
  						new Coordinate(
  								s.getLongitude(),
  								s.getLatitude())));
    }
    
    public void addStationToSpatialIndex(Station s){
    	//stationSpatialIndex.removeFromIndex(s.getUnderlyingNode().getId());
        stationSpatialIndex.add(s.getUnderlyingNode());
      }
    
    public void deleteStation(Station s){

    	for(Stop stop : s.getAllStops()){
    		Stops.getStops().deleteStop(stop);
    	}
    	
//    	for(Run run : s.getAllRuns()){
//    		Runs.getRuns().deleteRun(run);
//    	}
    	
//    	for(Route route : s.getAllRoutes()){
//    		Routes.getRoutes().deleteRoute(route);    		    		
//    	}
    	
    	for(Relationship rel : s.getUnderlyingNode().getRelationships(RelTypes.ROUTEFROM, Direction.INCOMING)){
    		Route route = new Route(rel.getStartNode());
    		Routes.getRoutes().deleteRoute(route);
    	}

    	for(Relationship rel : s.getUnderlyingNode().getRelationships(RelTypes.ROUTETOWARDS, Direction.INCOMING)){
    		Route route = new Route(rel.getStartNode());
    		Routes.getRoutes().deleteRoute(route);    		
    	}
    	
    	stationSpatialIndex.removeFromIndex(s.getUnderlyingNode().getId());
    	stationIndex.remove(s.getUnderlyingNode());
    	
//		System.out.println("\n\nDELETING Station " + s.getId());
//    	for(Relationship rel : s.getUnderlyingNode().getRelationships()){
//    		System.out.println(rel + " (" + rel.getType() + ") :  " +  rel.getStartNode() + " --> " + rel.getEndNode());
//    	}
    	
    	s.deleteStopIndex();
    	s.getUnderlyingNode().delete();    	
    }
           
	public Station getStationById(Long id){
		if(id == null)
			return null;
		
        IndexHits<Node> result = stationIndex.get("id", id);
        Node n = result.getSingle();
        result.close();
        if(n == null)
            return null;
        return new Station(n);    
    }
    
    public ArrayList<Station> getAll(){        
        
        ArrayList<Station> output = new ArrayList<Station>();
        IndexHits<Node> result = stationIndex.query("id", "*");
        for(Node n : result){
            output.add(new Station(n));           
        }        
        result.close();
        return output;
    }

    
    public Collection<Station> getNearestStations( double lat1, double lon1){
		return getNearestStations(lat1, lon1, 100000);
    }
    	
    public Collection<Station> getNearestStations( double lat1, double lon1, int range){    
    	//range in meters
    	
    	double distance = range / 1000.0; 
    	
    	Collection<Station> result = new ArrayList<Station>();
    	
		List<SpatialDatabaseRecord> results = GeoPipeline.startNearestNeighborLatLonSearch(stationSpatialIndex, new Coordinate(lon1, lat1), distance).sort("OrthodromicDistance").toSpatialDatabaseRecordList();		
		for(SpatialDatabaseRecord ris : results){
			Node n = DbConnection.getDb().getNodeById(ris.getNodeId());
			result.add(new Station(n));
		}
    	
		return result;
    }
    
    public Station getNearestStation( double lat1, double lon1){
		List<SpatialDatabaseRecord> results = GeoPipeline.startNearestNeighborLatLonSearch(stationSpatialIndex, new Coordinate(lon1, lat1), 1).toSpatialDatabaseRecordList();
		
		Node node = DbConnection.getDb().getNodeById(results.iterator().next().getNodeId());
		        
        if(node != null){
            Station out = new Station(node);
            return out;
        } else {
            return null;
        }
    }    

    public Station createOrUpdateStation(boa.server.importer.json.Station js){
		// creates a new station having the specified id
    	// if the id already exists then updates the corresponding db record

    	Station s = getStationById(js.getId());
	  	if(s != null){	// update (latLon cannot be updated)
			s.setName(s.getName());
//			s.setLatitude(js.getLatLon().getLat());
//			s.setLongitude(js.getLatLon().getLon());    	
//	  		updateSpatialIndex(s);
	  		
	  		// update CheckPoints associati
//	  		for(Stop stop : s.getAllStops()){
//		  		for(Relationship rel : stop.getUnderlyingNode().getRelationships(RelTypes.CHECKPOINTFROM, Direction.INCOMING)){
//		  			CheckPoint cp = new CheckPoint(rel.getStartNode());
//		  			if(cp.getTowards().equals(stop)){	// checkpoint in the station position
//		  				cp.setLatitude(s.getLatitude());
//		  				cp.setLongitude(s.getLongitude());
//		  				stop.getRun().updateCpSpatialIndex(cp);		  				
//		  			}
//		  		}
//	  		}
	  	} else {		// create
	  		s = new StationImporter(
		  			  DbConnection.getDb().createNode(), 
		  			  js.getId(),
		  			  js.getName(),
					  js.getLatLon().getLat(),
					  js.getLatLon().getLon());
	  		addStationToIndex(s);
	  		addStationToSpatialIndex(s);
	  	}

	  	return s;
	}	

    public void createOrUpdateStations(boa.server.importer.json.Stations stations){
		// creates new stations having the specified ids
    	// if an id already exists then updates the corresponding db record

    	for(boa.server.importer.json.Station s : stations.stationsObjectsList){
    		Transaction tx = DbConnection.getDb().beginTx();
    		try{
    			createOrUpdateStation(s);
    			tx.success();
    		}finally{
    			tx.finish();			
    		}  
    	}
	}	
    
    public void deleteAllStations(){
    	for(Station s : getAll()){
			deleteStation(s);	    			
    	}
    }
    
    public ArrayList<Station> getAllStationsInSpatialIndex() {
        ArrayList<Station> output = new ArrayList<Station>();
        List<SpatialDatabaseRecord> results = GeoPipeline.start(stationSpatialIndex).toSpatialDatabaseRecordList();
		for(SpatialDatabaseRecord ris : results){
			//System.out.print("\n" + ris.getGeometry());
			Node node = DbConnection.getDb().getNodeById(ris.getNodeId());
			Station s = new Station(node);
			output.add(s);
		}	

		return output;
    }        
}
