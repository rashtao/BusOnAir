package boa.server.webapp.xml;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;

public class Hub
{
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lon";
    public static final String HUBID = "hubId";
    public static final String HUB = "hub";
    public static final String HUBTRANSFERID = "hubTransfer";
    public static final String ROUTEID = "routeId";
    public static final String HUBNUM = "hubNum";
    public static final String LASTHUB = "lastHub";
    public static final String DEPTTIMES = "deptTimes";
    public static final String COST = "cost";

    private Node underlyingNode;

    public Hub( Node node )
    {
        this.underlyingNode = node;
    }

    public Hub( Node node, Route route, Stop stop, Index<Node> hubLayer, Boolean isLast )
    {
        this.underlyingNode = node;
        this.underlyingNode.setProperty( Hub.LATITUDE, stop.lat );
        this.underlyingNode.setProperty( Hub.LONGITUDE, stop.lon );
        this.underlyingNode.setProperty( Stop.STOPNAME, stop.stopName );
        this.underlyingNode.setProperty( Stop.ROUTENAME, route.routeName );
        this.underlyingNode.setProperty( Stop.STOPNUM , Integer.parseInt( stop.stopId.trim() ) );
        this.underlyingNode.setProperty( Hub.ROUTEID,  route.direction + route.routeId);
        this.underlyingNode.setProperty( Hub.HUBTRANSFERID, stop.transferHub);
        this.underlyingNode.setProperty( Hub.LASTHUB, isLast );
        String hubId = route.direction + route.routeId + stop.stopId;
        this.underlyingNode.setProperty( Hub.HUBID, hubId );
        this.underlyingNode.setProperty( Hub.COST, stop.cost );

        Double[] startTimes = route.deptTimes;
        double[] hubtimes = new double[startTimes.length];
        for ( int x = 0; x < startTimes.length; x++ )
            hubtimes[x] = startTimes[x] + stop.cost;
        this.underlyingNode.setProperty( Hub.DEPTTIMES, hubtimes );
        hubLayer.add( this.underlyingNode, Hub.HUBID, hubId );
    }

    public Node getUnderlyingNode()
    {
        return this.underlyingNode;
    }
   
    public double[] getDeptTimes()
    {
        return (double[]) this.underlyingNode.getProperty( Hub.DEPTTIMES);
    }

    public Double[] getLatLon()
    {
        return new Double[] { (Double) this.underlyingNode.getProperty( LATITUDE ), (Double) this.underlyingNode.getProperty( LONGITUDE ) };
    }

    public String getRouteId()
    {
        return (String) this.underlyingNode.getProperty( ROUTEID );
    }
    
    public String getHubId()
    {
        return (String) this.underlyingNode.getProperty( HUBID );
    }

    public Boolean isFirstHub()
    {
        return ((Integer) this.underlyingNode.getProperty( HUBNUM ) ==1);
    }
    
    public Boolean isLastHub()
    {
        return (Boolean) this.underlyingNode.getProperty( LASTHUB );
    }
    
    public Double getCost()
    {
        return (Double) this.underlyingNode.getProperty( Hub.COST );       
    }
    
    public String getTransferId()
    {
        return (String) this.underlyingNode.getProperty( Hub.HUBTRANSFERID );        
    }

}
