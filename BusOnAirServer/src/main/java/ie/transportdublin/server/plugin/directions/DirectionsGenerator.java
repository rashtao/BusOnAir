package ie.transportdublin.server.plugin.directions;

import ie.transportdublin.server.plugin.json.Directions;
import ie.transportdublin.server.plugin.json.DirectionsList;
import ie.transportdublin.server.plugin.json.DirectionsRoute;
import ie.transportdublin.server.plugin.json.DirectionsWalk;
import ie.transportdublin.xml.Connection;
import ie.transportdublin.xml.Hub;
import ie.transportdublin.xml.Stop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;

public class DirectionsGenerator
{

    private DirectionsList directionsList;
    private List<DirectionsRoute> routes;
    private List<DirectionsWalk> walks;
    private Index<Node> stopLayer;
    
    public DirectionsGenerator( WeightedPath path )
    {
        stopLayer = path.endNode().getGraphDatabase().index().forNodes( "stopLayer" );
        directionsList = new DirectionsList();
        routes = new ArrayList<DirectionsRoute>();
        walks = new ArrayList<DirectionsWalk>();
    }
       
    //example path: (start)--[WALK1,1190]-->(hub)--[HUBBUS1,49581]-->(hub)--[53.36179430400081-6.299620132568407,1224]-->(end)
    public DirectionsList convertOneBusPath( WeightedPath path, double time )
    {
        int count = 0;
        double cost = 0;
        double deptTime = 0;

        List<Node> busList = new ArrayList<Node>();

        for ( Relationship relationship : path.relationships() )
        {
            if ( count == 0 )// lat1lon1
            {
                double distanceInKm = (Double) relationship.getProperty( Connection.DISTANCE );
                double distanceInMins = Math.round( 20 * distanceInKm * 1e2 ) / 1e2;
                walks.add( new DirectionsWalk( distanceInMins, relationship.getStartNode(), relationship ) );               
                Node stop = stopLayer.get( Stop.STOPID, (String) relationship.getProperty( Stop.STOPID ) ).getSingle();
                busList.add( stop );
                deptTime = (Double) relationship.getProperty( Connection.TIME );
                cost += (Double) stop.getProperty( Stop.TIMETOHUB );
            }
            if ( count == 1 )// hubbus1
            {             
                cost += (Double) relationship.getProperty( Connection.COST );
            }
            if ( count == 2 )// lat2lon2
            {
                Node stop = stopLayer.get( Stop.STOPID, (String) relationship.getProperty( Stop.STOPID ) ).getSingle();
                cost += (Double) stop.getProperty( Stop.TIMEFROMHUB );
                busList.add( stop );
                routes.add( new DirectionsRoute( deptTime, cost, busList ) );              
                double distanceInKm = (Double) relationship.getProperty( Connection.DISTANCE );
                double distanceInMins = Math.round( 20 * distanceInKm * 1e2 ) / 1e2;
                walks.add( new DirectionsWalk( distanceInMins, relationship, relationship.getEndNode() ) );
            }
            count++;
        }

        directionsList.add( new Directions( routes, walks ) );//Add Total cost
        return directionsList;
    }
    
    
    //example path: (start)--[WALK1,25037]-->(hub)--[HUBBUS1,44652]-->(hub)--[TRANSFER,44624]-->(hubtransfer)--[WALK2,61370]-->(hubtransfer)--[O0033,48991]-->(hub)--[HUBBUS1,49028]-->(hub)--[53.373665-6.251902,25283]-->(end) 
    public DirectionsList convertTwoBusPath( WeightedPath path, double time )
    {
        int count = 0;
        double cost = 0;
        double deptTime = 0;

        List<Node> busList1 = new ArrayList<Node>();
        List<Node> busList2 = new ArrayList<Node>();

        for ( Relationship relationship : path.relationships() )
        {
            if ( count == 0 ) // lat1lon1
            {
                double distanceInKm = (Double) relationship.getProperty( Connection.DISTANCE );
                double distanceInMins = Math.round( 20 * distanceInKm * 1e2 ) / 1e2;
                walks.add( new DirectionsWalk( distanceInMins, relationship.getStartNode(), relationship ) );
                
                Node stop = stopLayer.get( Stop.STOPID, (String) relationship.getProperty( Stop.STOPID ) ).getSingle();
                busList1.add( stop );
                deptTime = (Double) relationship.getProperty( Connection.TIME );
                cost += (Double) stop.getProperty( Stop.TIMETOHUB );
            }
            if ( count == 1 ) // hubbus1
            {
                cost += (Double) relationship.getProperty( Connection.COST );
                busList1.add( relationship.getEndNode() );
                routes.add( new DirectionsRoute( deptTime, cost, busList1 ) );
            }

            if ( count == 3 ) // walk2
            {
                double distanceInMins = (Double) relationship.getProperty( Connection.COST );
                cost +=distanceInMins;
                walks.add( new DirectionsWalk( distanceInMins, relationship.getStartNode(), relationship.getEndNode() ) );
            }
            if ( count == 5 ) // hubbus1
            {
                busList2.add( relationship.getStartNode() );       
                double[] transferTimes =(double[]) relationship.getStartNode().getProperty( Hub.DEPTTIMES );
                int insertionPoint = Arrays.binarySearch( transferTimes, ( deptTime + cost  ) );
                insertionPoint = ( insertionPoint < 0 ) ? ( ( insertionPoint * -1 ) - 1 ) : insertionPoint; // ( -( insertion point ) - 1)
                if(insertionPoint != transferTimes.length)
                    deptTime = transferTimes[insertionPoint];
                cost = 0.0;
                cost += (Double) relationship.getProperty( Connection.COST );
            }
            if ( count == 6 ) // lat2lon2
            {
                Node stop = stopLayer.get( Stop.STOPID, (String) relationship.getProperty( Stop.STOPID ) ).getSingle();
                cost += (Double) stop.getProperty( Stop.TIMEFROMHUB );
                busList2.add( stop );
                routes.add( new DirectionsRoute( deptTime, cost, busList2 ) );
                double distanceInKm = (Double) relationship.getProperty( Connection.DISTANCE );
                double distanceInMins = Math.round( 20 * distanceInKm * 1e2 ) / 1e2;
                walks.add( new DirectionsWalk( distanceInMins, relationship, relationship.getEndNode() ) );
            }
            count++;
        }
        directionsList.add( new Directions( routes, walks ) );
        return directionsList;
    }

}
