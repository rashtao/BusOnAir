package ie.transportdublin.graphalgo.impl;
import ie.transportdublin.xml.Connection;
import ie.transportdublin.xml.Hub;

import java.util.Arrays;

import org.neo4j.graphalgo.CostEvaluator;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

public  class WaitingTimeCostEvaluator implements CostEvaluator<Double>
{
    private String costpropertyName;
    private RelationshipType lat2lon2 ;

    public WaitingTimeCostEvaluator( String costpropertyName, RelationshipType lat2lon2 )
    {
        super();
        this.costpropertyName = costpropertyName;
        this.lat2lon2 = lat2lon2;
    }

    public double getCost( Relationship relationship, Direction direction, double pathWeight )
    {
        if ( relationship.isType( Connection.Type.WALK1 ) || relationship.isType( Connection.Type.WALK2 ) || relationship.isType( Connection.Type.HUBBUS1 )|| relationship.isType( Connection.Type.HUBBUS2 )|| relationship.isType( Connection.Type.TRANSFER )|| relationship.isType( lat2lon2))
        {
            return (Double) relationship.getProperty( costpropertyName );
        }
        else 
        {
            double distanceInKm = ( (Double) relationship.getProperty( costpropertyName ) );
            double distanceInMins = Math.round( 20 * distanceInKm * 1e2 ) / 1e2;
            double[] transferTimes =(double[]) relationship.getEndNode().getProperty( Hub.DEPTTIMES );
            int insertionPoint = Arrays.binarySearch( transferTimes, ( pathWeight + distanceInMins + Connection.MINWAITINGTIME ) );
            insertionPoint = ( insertionPoint < 0 ) ? ( ( insertionPoint * -1 ) - 1 ) : insertionPoint; // ( -( insertion point ) - 1)
            if(insertionPoint != transferTimes.length)
            {
                double cost = ( transferTimes[insertionPoint] - pathWeight );
                if(cost < Connection.MAXWAITINGTIME)
                    return cost;             
            }
              return 10000.0;
        }
    }

    public Double getCost( Relationship relationship, Direction direction )
    {
        return (Double) relationship.getProperty( costpropertyName );
    }

}
