package ie.transportdublin.shortestpath;

import ie.transportdublin.graphalgo.Dijkstra;
import ie.transportdublin.graphalgo.impl.WaitingTimeCostEvaluator;
import ie.transportdublin.xml.Connection;
import ie.transportdublin.xml.Connection.Type;
import ie.transportdublin.xml.Hub;
import ie.transportdublin.xml.Stop;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Expander;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.Traversal;

public class ShortestPath
{

    private static GraphDatabaseService db;
    private static Index<Node> hubLayer1;
    private static Index<Node> hubLayer2;
    private static Expander expander;
    private static SpatialService spatialService;

    public ShortestPath( GraphDatabaseService  db )
    {
        ShortestPath.db=db;       
        spatialService= new SpatialService(db);
        hubLayer1 = db.index().forNodes( "hubLayer1" );
        hubLayer2 = db.index().forNodes( "hubLayer2" );
    }
    
    public WeightedPath findShortestPath( Double lat1, Double lon1, Double lat2, Double lon2, Double time )
    {
        long startTime = System.nanoTime();
        WeightedPath path = findPaths(lat1, lon1, lat2, lon2, time );    
        System.out.println( "time: "+ (System.nanoTime() - startTime)/ 1000000);
        System.out.println( "path: "+ path);
        return path;       
    }

    private  WeightedPath findPaths( Double lat1, Double lon1, Double lat2, Double lon2, Double time )
    {
        Node start = setupStart( lat1, lon1, time );
        Node end = setupEnd( lat2, lon2 );
        RelationshipType lat2lon2 = DynamicRelationshipType.withName( lat2.toString() + lon2.toString() );
        WaitingTimeCostEvaluator eval = new WaitingTimeCostEvaluator(Connection.COST, lat2lon2);
        Dijkstra dijkstra = new Dijkstra( expander, eval, time );
        return dijkstra.findSinglePath( start, end );
    }

  
    private  Node setupStart( double lat1, double lon1, double time )
    {
        Node startNode = db.createNode();
        startNode.setProperty( Hub.LATITUDE, lat1);
        startNode.setProperty( Hub.LONGITUDE, lon1 );  
        HashSet<String> hubSet = new HashSet<String>();
        Map<Node, Double> hits = spatialService.queryWithinDistance( lat1, lon1 );
        int i = 0;
        for ( Entry<Node, Double> entry : hits.entrySet() )
        {
            String hubID = (String) entry.getKey().getProperty( Stop.TOHUB );
            if(!hubSet.contains( hubID ))
            {
                Node startHub = hubLayer1.get( Hub.HUBID, hubID ).getSingle();
                double[] transferTimes =(double[])  startHub.getProperty( Hub.DEPTTIMES );
                double distanceInKm = ( entry.getValue() );
                double distanceInMins = Math.round( 20 * distanceInKm * 1e2 ) / 1e2;
                int insertionPoint = Arrays.binarySearch( transferTimes, ( time + distanceInMins +(Double)entry.getKey().getProperty( Stop.TIMETOHUB ) ) );
                insertionPoint = ( insertionPoint < 0 ) ? ( ( insertionPoint * -1 ) - 1 ) : insertionPoint;            
                if ( insertionPoint != transferTimes.length )
                {
                    double waitingTime = transferTimes[insertionPoint] - time;
                     if (  waitingTime < Connection.MAXWAITINGTIME )
                    {               
                        Relationship walk = startNode.createRelationshipTo( startHub,Connection.Type.WALK1 );
                        walk.setProperty( Connection.COST, waitingTime + distanceInMins * 5 );
                        walk.setProperty( Connection.DISTANCE, distanceInKm );
                        walk.setProperty( Connection.TIME, transferTimes[insertionPoint] );
                        walk.setProperty( Stop.STOPID, (String)entry.getKey().getProperty( Stop.STOPID )  );
                        walk.setProperty( Stop.LATITUDE, (Double)entry.getKey().getProperty( Stop.LATITUDE )  );
                        walk.setProperty( Stop.LONGITUDE, (Double)entry.getKey().getProperty( Stop.LONGITUDE )  );
                    }
                   hubSet.add( hubID );
                   if ( ++i == 50 ) break;
                }
            }
        }
        return startNode;
    }
    

    private  Node setupEnd( Double lat2, Double lon2 )
    {
        Node endNode = db.createNode();
        endNode.setProperty( Hub.LATITUDE, lat2);
        endNode.setProperty( Hub.LONGITUDE, lon2 );  
        RelationshipType lat2lon2 = DynamicRelationshipType.withName( lat2.toString() + lon2.toString() );
        expander = Traversal.expanderForTypes( 
                Type.WALK1, Direction.OUTGOING, 
                Type.HUBBUS1, Direction.OUTGOING, 
                Type.TRANSFER, Direction.OUTGOING,
                Type.WALK2, Direction.OUTGOING,
                lat2lon2, Direction.OUTGOING );   
        
        Set<String> hubSet = new HashSet<String>();
        Map<Node, Double> hits = spatialService.queryWithinDistance( lat2, lon2 );
        int i = 0;
        for ( Entry<Node, Double> entry : hits.entrySet() )     
        {                    
            String hubID = (String) entry.getKey().getProperty( Stop.FROMHUB  );
            if ( !hubSet.contains( hubID ) )
            {
                for(Node endHub : hubLayer2.query( Hub.HUBID, hubID ) )
                {          
                    Relationship walk = endHub.createRelationshipTo( endNode, lat2lon2);
                    double distanceInKm = (entry.getValue() );
                    double distanceInMins = Math.round( 20 * distanceInKm * 1e2 ) / 1e2;
                    walk.setProperty( Connection.COST, distanceInMins*5  );
                    walk.setProperty( Connection.DISTANCE, distanceInKm );
                    walk.setProperty( Stop.STOPID, (String)entry.getKey().getProperty( Stop.STOPID )  );
                    walk.setProperty( Stop.LATITUDE, (Double)entry.getKey().getProperty( Stop.LATITUDE )  );
                    walk.setProperty( Stop.LONGITUDE, (Double)entry.getKey().getProperty( Stop.LONGITUDE )  );
                    RelationshipType routeId = DynamicRelationshipType.withName((String) entry.getKey().getProperty( Stop.ROUTEID  ));
                    expander = expander.add( routeId, Direction.OUTGOING );                   
                    hubSet.add( hubID );   
                    if ( ++i == 50 ) break;
                }

            }
        }
        return endNode;
    }

}
