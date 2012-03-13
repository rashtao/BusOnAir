/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package boa.server.domain;

import java.util.*;
import java.util.Map.Entry;

import org.neo4j.gis.spatial.indexprovider.LayerNodeIndex;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

/**
 *
 * @author rashta
 */
public class Stations {
    private LayerNodeIndex stationSpatialIndex;
    private Index<Node> stationIndex;

    private static Stations instance = null;
    
    public static Stations getStations() {
        if (instance == null) 
            instance = new Stations();
        return instance;
    }    
    
    private Stations(){        
        stationSpatialIndex = new LayerNodeIndex( "stationSpatialIndex", DbConnection.getDb(), new HashMap<String, String>() );
        stationIndex = DbConnection.getDb().index().forNodes("stationsIndex");    
    }
    
    public void addStation(Station s){
//        stationSpatialIndex.add(s.getUnderlyingNode(), "", "" );
        stationIndex.add(s.getUnderlyingNode(), "id", s.getId());
    }
        
    public Station getStationById(int id){
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

    
    public Collection<Station> nearestStations( double lat1, double lon1){
		return nearestStations(lat1, lon1, 100000);
    }
    	
    public Collection<Station> nearestStations( double lat1, double lon1, int range){    
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
    
    public Station nearestStation( double lat1, double lon1){
        Map<Node, Double> hits = queryWithinDistance( lat1, lon1 );
        Map.Entry<Node, Double> entry = hits.entrySet().iterator().next();
        
        if(entry != null && entry.getKey() != null){
            Station out = new Station(entry.getKey());
//            System.out.print("\n\n-----Dist: " + entry.getValue() + "\n" + out);          
            return out;
        } else {
            return null;
        }
    }    

    public Map<Node, Double> queryWithinDistance( Double lat, Double lon){
    	return queryWithinDistance(lat, lon, 1000.0);
    }
    
public Map<Node, Double> queryWithinDistance( Double lat, Double lon, Double distance)
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
    static Map<Node, Double> sortByValue( Map<Node, Double> map )
    {
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
    
}
