package boa.server.webapp.xml;

import org.neo4j.graphdb.RelationshipType;

public class Connection
{

    public static final String COST = "cost";
    public static final String TIME = "time";
    public static final String DISTANCE = "distance";
    public static final double FIVEHUNDREDMETRES = 0.5;
    public static final double ONEKILOMETRE = 1.0;
    public static final double MAXWAITINGTIME = 60.0;
    public static final double MINWAITINGTIME = 5.0;
    public static final double TRANSFERPENALTY = 300.0;

    
    public enum Type implements RelationshipType
    {
        WALK1, WALK2, HUBBUS1, HUBBUS2, TRANSFER
    }
}
