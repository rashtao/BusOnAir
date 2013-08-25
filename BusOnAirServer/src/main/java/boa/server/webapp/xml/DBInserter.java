package boa.server.webapp.xml;

import org.neo4j.gis.spatial.indexprovider.LayerNodeIndex;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.index.Index;

import java.util.*;

public class DBInserter
{

    private GraphDatabaseService db;

    private static Index<Node> hubLayer1;
    private static Index<Node> hubLayer2;
    private static Index<Node> hubTransferLayer1;
    private static Index<Node> hubTransferLayer2;
    private static LayerNodeIndex hubTransferLayer2SpatialIndex;
    private static Index<Node> stopLayer;
    private static LayerNodeIndex stopSpatialIndex;

    private static Routes routes;

    public DBInserter( GraphDatabaseService db )
    {
        this.db = db;
    }

    public void addData()
    {


            routes = XMLReader.read();
            hubLayer1 = db.index().forNodes( "hubLayer1" );
            hubLayer2 = db.index().forNodes( "hubLayer2" );
            hubTransferLayer1 = db.index().forNodes( "hubTransferLayer1" );
            hubTransferLayer2 = db.index().forNodes( "hubTransferLayer2" );
            hubTransferLayer2SpatialIndex = new LayerNodeIndex( "hubTransferLayerSpatialIndex", db, new HashMap<String, String>() );
            stopLayer = db.index().forNodes( "stopLayer" );
            stopSpatialIndex = new LayerNodeIndex( "stopSpatialIndex", db, new HashMap<String, String>() );
            addStops();           
            addHubs();
            addWalkingConnections();
   

    }

    /**
     * Add route hubs with connections
     */
    private void addHubs()
    {
        System.out.println( "Add Hubs" );
        Transaction tx = db.beginTx();
        try
        {
            for ( Route route : routes.getRouteList() )
            {
                List <Hub> hubList1 = new LinkedList<Hub>();
                List <Hub> hubList2 = new LinkedList<Hub>();
                Iterator<Stop> stopsOnRoute = route.getStopList().iterator();
                while ( stopsOnRoute.hasNext() )
                {
                    Stop stop = stopsOnRoute.next();
                    if ( stop.isHub )
                    {
                        Hub hub1 = new Hub( db.createNode(), route, stop, hubLayer1, !stopsOnRoute.hasNext() );
                        hubList1.add( hub1 );
                        Hub hub2 = new Hub( db.createNode(), route, stop, hubLayer2, !stopsOnRoute.hasNext() );
                        hubList2.add( hub2 );
                        connectToTransferHubs( hub1, hub2, route, stop );
                    }
                }
                connectHubs(hubList1, hubList2);
            }        
            tx.success();
        }
        finally
        {
            tx.finish();
        }
    }

    private void connectHubs( List<Hub> hubList1, List<Hub> hubList2 )
    {
        for ( int i = 0; i < hubList1.size() - 1; i++ )
        {
            for ( int x = i ; x < hubList2.size(); x++ )
            {               
                Relationship bus = hubList1.get( i ).getUnderlyingNode().createRelationshipTo( hubList2.get( x ).getUnderlyingNode(), Connection.Type.HUBBUS1 );
                bus.setProperty( Connection.COST, hubList2.get( x ).getCost() - hubList1.get( i ).getCost() );
            }
        }
    }

    /**
     * Add connections between hubTransfer1 and hubTransfer2 using WithinDistanceQuery
     */
    private void addWalkingConnections()
    {
        System.out.println( "Add Walking Connections" );
        Transaction tx = db.beginTx();
        try
        {
            int count = 0;
            for ( Node hubTransfer1 : hubTransferLayer1.query( Hub.HUBTRANSFERID, "*" ) )
            {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put( LayerNodeIndex.POINT_PARAMETER, new Double[] {
                        (Double) hubTransfer1.getProperty( Hub.LATITUDE ),
                        (Double) hubTransfer1.getProperty( Hub.LONGITUDE ) } );
                params.put( LayerNodeIndex.DISTANCE_IN_KM_PARAMETER, Connection.FIVEHUNDREDMETRES );
                for ( Node spatialRecord : hubTransferLayer2SpatialIndex.query(
                        LayerNodeIndex.WITHIN_DISTANCE_QUERY, params ) )
                {
                    Node hubTransfer2 = db.getNodeById( (Long) spatialRecord.getProperty( "id" ) );
                    Relationship walk = hubTransfer1.createRelationshipTo( hubTransfer2, Connection.Type.WALK2 );
                    double distanceInKm = (Double) spatialRecord.getProperty( "distanceInKm" );
                    double distanceInMins = Math.round( 20 * distanceInKm * 1e2 ) / 1e2;
                    walk.setProperty( Connection.COST, distanceInMins );
                    walk.setProperty( Connection.DISTANCE, distanceInKm );
                    count++;
                }
            }
            System.out.println( "Walk Relationships: " + count );
            tx.success();
        }
        finally
        {
            tx.finish();
        }
    }

    /**
     * Connect Hubs to a transfer hub
     */
    private void connectToTransferHubs( Hub hub1, Hub hub2, Route route, Stop stop )
    {
        Transaction tx = db.beginTx();
        try
        {
            Node hubTransfer1;
            Node hubTransfer2;
            if ( hubTransferLayer1.get( Hub.HUBTRANSFERID, hub1.getTransferId() ).getSingle() == null )
            {
                hubTransfer1 = db.createNode();
                hubTransfer1.setProperty( Hub.LATITUDE, stop.lat );
                hubTransfer1.setProperty( Hub.LONGITUDE, stop.lon );
                hubTransferLayer1.add( hubTransfer1, Hub.HUBTRANSFERID, hub1.getTransferId() );
                hubTransfer2 = db.createNode();
                hubTransfer2.setProperty( Hub.LATITUDE, stop.lat );
                hubTransfer2.setProperty( Hub.LONGITUDE, stop.lon );
                hubTransferLayer2.add( hubTransfer2, Hub.HUBTRANSFERID, hub1.getTransferId() );
                hubTransferLayer2SpatialIndex.add( hubTransfer2, "", "" );
            }
            else
            {
                hubTransfer1 = hubTransferLayer1.get( Hub.HUBTRANSFERID, hub1.getTransferId()  ).getSingle();
                hubTransfer2 = hubTransferLayer2.get( Hub.HUBTRANSFERID, hub1.getTransferId()  ).getSingle();
            }
            transferToHubTransfer( hub2, hubTransfer1 );
            transferFromHubTransfer( hubTransfer2, hub1 );
            tx.success();
        }
        finally
        {
            tx.finish();
        }
    }

    /**
     * hub1---(transfer2)-->hubTransfer1
     */
    public void transferToHubTransfer( Hub hub, Node hubTransfer )
    {
        Relationship transfer = hub.getUnderlyingNode().createRelationshipTo(
                hubTransfer, Connection.Type.TRANSFER );
        transfer.setProperty( Connection.COST, Connection.TRANSFERPENALTY);
    }

    /**
     * hubTransfer--(routeId)--> hub2
     */
    public void transferFromHubTransfer( Node hubTransfer, Hub hub )
    {
        Relationship transfer = hubTransfer.createRelationshipTo(
                hub.getUnderlyingNode(),
                DynamicRelationshipType.withName( hub.getRouteId() ) );
        transfer.setProperty( Connection.COST, 0.0 );
    }

    private void addStops()
    {
        for ( Route route : routes.getRouteList() )
        {
            System.out.println( "Add Stops route: " + route.routeId );
            Transaction tx = db.beginTx();
            try
            {
                for ( Stop stop : route.getStopList() )
                {
                    Stop stop1 = new Stop( db.createNode(), route, stop, stopLayer );
                    stopSpatialIndex.add( stop1.getUnderlyingNode(), "", "" );
                }
                tx.success();
            }
            finally
            {
                tx.finish();
            }
        }
    }
}
