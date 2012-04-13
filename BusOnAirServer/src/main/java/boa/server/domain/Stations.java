package boa.server.domain;

import java.util.*;

import org.neo4j.gis.spatial.indexprovider.LayerNodeIndex;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

import boa.server.importer.domain.StationImporter;

public class Stations {
    protected LayerNodeIndex stationSpatialIndex;
    protected Index<Node> stationIndex;

    protected static Stations instance = null;
    
    public static Stations getStations() {
        if (instance == null) 
            instance = new Stations();
        return instance;
    }    
    
    protected Stations(){        
        stationSpatialIndex = new LayerNodeIndex( "stationSpatialIndex", DbConnection.getDb(), new HashMap<String, String>() );
        stationIndex = DbConnection.getDb().index().forNodes("stationsIndex");    
    }
    
    public void addStation(Station s){
//      stationSpatialIndex.add(s.getUnderlyingNode(), "", "" );
      stationIndex.add(s.getUnderlyingNode(), "id", s.getId());
  }
  
    public void addStationToSpatialIndex(Station s){
      stationSpatialIndex.add(s.getUnderlyingNode(), "", "" );
  }
  
    public void deleteStation(Station staz){
    	ArrayList<Route> routes = staz.getAllRoutes();

    	for(Route route : routes){
    		Routes.getRoutes().deleteRoute(route);    		    		
    	}
    	
    	for(Relationship rel : staz.getUnderlyingNode().getRelationships(RelTypes.ROUTEFROM, Direction.INCOMING)){
    		Route route = new Route(rel.getStartNode());
    		Routes.getRoutes().deleteRoute(route);
    	}

    	for(Relationship rel : staz.getUnderlyingNode().getRelationships(RelTypes.ROUTETOWARDS, Direction.INCOMING)){
    		Route route = new Route(rel.getStartNode());
    		Routes.getRoutes().deleteRoute(route);    		
    	}

    	ArrayList<Run> runs = staz.getAllRuns();
    	for(Run run : runs){
    		Runs.getRuns().deleteRun(run);
    	}
    	
    	
    	stationIndex.remove(staz.getUnderlyingNode());
    	for(Relationship rel : staz.getUnderlyingNode().getRelationships()){
    		System.out.println(rel + " (" + rel.getType() + ") :  " +  rel.getStartNode() + " --> " + rel.getEndNode());
    	}
    	
    	staz.getUnderlyingNode().delete();    	
    	
    	updateSpatialIndex();
    }
        
    public void updateSpatialIndex() {
    	stationSpatialIndex = new LayerNodeIndex( "stationSpatialIndex", DbConnection.getDb(), new HashMap<String, String>() );
    	stationSpatialIndex.delete();
    	
    	stationSpatialIndex = new LayerNodeIndex( "stationSpatialIndex", DbConnection.getDb(), new HashMap<String, String>() );
    	for(Station s : getAll()){
    		addStationToSpatialIndex(s);
    	}
	}

	public Station getStationById(long id){
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
    	
    	double kmrange = (double) range / 1000.0;
    	    	
    	Collection<Station> result = new ArrayList<Station>(); 
        Map<Node, Double> hits = queryWithinDistance( lat1, lon1, (double) kmrange);

        Iterator it = hits.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Node, Double> entry = (Map.Entry<Node, Double>)it.next();
            
            if(entry != null && entry.getKey() != null && entry.getValue() < (double) range){
            	result.add(new Station(entry.getKey()));            	
            }
        }

		return result;
    }
    
    public Station getNearestStation( double lat1, double lon1){
        Map<Node, Double> hits = queryWithinDistance( lat1, lon1 );
        Map.Entry<Node, Double> entry = hits.entrySet().iterator().next();
        
        if(entry != null && entry.getKey() != null){
            Station out = new Station(entry.getKey());
            return out;
        } else {
            return null;
        }
    }    

    protected Map<Node, Double> queryWithinDistance( Double lat, Double lon){
    	return queryWithinDistance(lat, lon, 1000.0);
    }
    
    protected Map<Node, Double> queryWithinDistance( Double lat, Double lon, Double distance)
    {        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put( LayerNodeIndex.DISTANCE_IN_KM_PARAMETER, distance);
        params.put( LayerNodeIndex.POINT_PARAMETER, new Double[] { lat, lon } );              
        Map<Node, Double> results = new HashMap<Node, Double>();
        for ( Node spatialRecord : stationSpatialIndex.query( LayerNodeIndex.WITHIN_DISTANCE_QUERY, params ) )
          results.put( DbConnection.getDb().getNodeById( (Long) spatialRecord.getProperty( "id" )), (Double) spatialRecord.getProperty( "distanceInKm" ) );               
        return sortByValue( results );
    }

    @SuppressWarnings( "unchecked" )
    protected static Map<Node, Double> sortByValue( Map<Node, Double> map ){
        List<Map.Entry<Node, Double>> list = new LinkedList<Map.Entry<Node, Double>>( map.entrySet() );
        Collections.sort( list, new Comparator()
        {
            public int compare( Object o1, Object o2 )
            {
                return ( (Comparable) ( (Map.Entry) ( o1 ) ).getValue() ).compareTo( ( (Map.Entry) ( o2 ) ).getValue() );
            }
        } );
        Map<Node, Double> result = new LinkedHashMap();
        for ( Iterator it = list.iterator(); it.hasNext(); )
        {
            Map.Entry entry = (Map.Entry) it.next();
            result.put( (Node) entry.getKey(), (Double) entry.getValue() );
        }
        return result;
    }

//	public void updateSpatialIndex(Station station) {
//    	stationSpatialIndex = new LayerNodeIndex( "stationSpatialIndex", DbConnection.getDb(), new HashMap<String, String>() );    	
//    	stationSpatialIndex.remove(station.getUnderlyingNode(), "", "" );
//    	addStationToSpatialIndex(station);
//	}
    

    public Station createStation(boa.server.domain.importer.Station js){
		// creates a new station
    	// js id is ignored
    	
    	Node node = DbConnection.getDb().createNode();
	  	  StationImporter s = new StationImporter(
	  			  node, 
	  			  node.getId(),
	  			  js.getName(),
				  js.getLatLon().getLat(),
				  js.getLatLon().getLon());
		  addStation(s);
		  addStationToSpatialIndex(s);
		  
		  return s;
	}	

    public Station createOrUpdateStation(boa.server.domain.importer.Station js){
		// creates a new station having the specified id
    	// if the id already exists then updates the corresponding db record

    	Station staz = getStationById(js.getId());
	  	if(staz != null){
	  		staz.updateName(js.getName());
	  		staz.updatePosition(js.getLatLon().getLat(), 
	  				js.getLatLon().getLon());
	  	} else {
		  	  staz = new StationImporter(
		  			  DbConnection.getDb().createNode(), 
		  			  js.getId(),
		  			  js.getName(),
					  js.getLatLon().getLat(),
					  js.getLatLon().getLon());
			  addStation(staz);
			  addStationToSpatialIndex(staz);
	  	}

	  	return staz;
	}	


}
