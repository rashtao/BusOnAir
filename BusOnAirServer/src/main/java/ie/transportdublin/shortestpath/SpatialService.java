package ie.transportdublin.shortestpath;

import ie.transportdublin.xml.Connection;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.geotools.referencing.datum.DefaultEllipsoid;
import org.neo4j.gis.spatial.indexprovider.LayerNodeIndex;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

public class SpatialService
{
    private static GraphDatabaseService db;
    private static LayerNodeIndex stopSpatialIndex;
    final double[] cityCentre = {-6.259117,53.347234};
       
    public SpatialService(GraphDatabaseService db)
    {
        this.db=db;
        stopSpatialIndex = new LayerNodeIndex( "stopSpatialIndex", db, new HashMap<String, String>() );        
    }
      
    public  Map<Node, Double> queryWithinDistance( Double lat, Double lon)
    {
        double distanceFromCity = DefaultEllipsoid.WGS84.orthodromicDistance(cityCentre[0],
                cityCentre[1], lon, lat ) / 1000;         
        Map<String, Object> params = new HashMap<String, Object>();
        params.put( LayerNodeIndex.DISTANCE_IN_KM_PARAMETER, (distanceFromCity > 
        Connection.ONEKILOMETRE) ? Connection.ONEKILOMETRE :Connection.FIVEHUNDREDMETRES);
        params.put( LayerNodeIndex.POINT_PARAMETER, new Double[] { lat, lon } );              
        Map<Node, Double> results = new HashMap<Node, Double>();
        for ( Node spatialRecord : stopSpatialIndex.query( LayerNodeIndex.WITHIN_DISTANCE_QUERY, params ) )
          results.put( db.getNodeById( (Long) spatialRecord.getProperty( "id" )), (Double) spatialRecord.getProperty( "distanceInKm" ) );               
        return sortByValue( results );
    }

    @SuppressWarnings( "unchecked" )
    static Map<Node, Double> sortByValue( Map<Node, Double> map )
    {
        List<Entry<Node, Double>> list = new LinkedList<Entry<Node, Double>>( map.entrySet() );
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
