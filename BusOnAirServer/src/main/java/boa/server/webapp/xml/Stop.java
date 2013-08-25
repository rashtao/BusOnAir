package boa.server.webapp.xml;


import boa.server.webapp.webappjson.Coordinate;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.index.lucene.ValueContext;


public class Stop {

    public static final String STOPID = "stopId";
    public static final String STOPNUM = "stopNum";
    public static final String ROUTEID = "routeId";
    public static final String ROUTENAME = "routeName";
    public static final String STOPNAME = "stopName";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lon";
    public static final String FROMHUB = "fromHub";
    public static final String TOHUB = "toHub";
    public static final String TIMEFROMHUB = "timeFromHub";
    public static final String TIMETOHUB = "timeToHub";

    private Node underlyingNode;

    protected String stopId;
    protected String stopName;
    protected String transferHub;
    protected Boolean isHub;
    protected Double lat;
    protected Double lon;
    protected Double cost;
    protected String fromHub;
    protected String toHub;
    protected Double timeFromHub;
    protected Double timeToHub;


    public Stop(Node node) {
        this.underlyingNode = node;
    }

    public Stop(Node node, Route route, Stop stop, Index<Node> stopLayer) {
        this.underlyingNode = node;
        setStopNodePropertys(stop, route, stopLayer);
    }

    public void setStopNodePropertys(Stop stop, Route route, Index<Node> stopLayer) {
        this.underlyingNode.setProperty(Stop.LATITUDE, stop.lat);
        this.underlyingNode.setProperty(Stop.LONGITUDE, stop.lon);
        this.underlyingNode.setProperty(Stop.STOPNAME, stop.stopName);
        this.underlyingNode.setProperty(Stop.STOPID, route.direction + route.routeId + stop.stopId);
        this.underlyingNode.setProperty(Stop.STOPNUM, Integer.parseInt(stop.stopId.trim()));
        this.underlyingNode.setProperty(Stop.ROUTEID, route.direction + route.routeId);
        this.underlyingNode.setProperty(Stop.ROUTENAME, route.routeName);
        this.underlyingNode.setProperty(Stop.FROMHUB, stop.fromHub);
        this.underlyingNode.setProperty(Stop.TOHUB, stop.toHub);
        this.underlyingNode.setProperty(Stop.TIMEFROMHUB, stop.timeFromHub);
        this.underlyingNode.setProperty(Stop.TIMETOHUB, stop.timeToHub);

        stopLayer.add(underlyingNode, Stop.STOPNUM, new ValueContext(Integer.parseInt(stop.stopId.trim())).indexNumeric());
        stopLayer.add(underlyingNode, STOPID, route.direction + route.routeId + stop.stopId);
        stopLayer.add(underlyingNode, ROUTEID, route.direction + route.routeId);

    }

    public Node getUnderlyingNode() {
        return this.underlyingNode;
    }

    public String toHub() {
        return (String) this.underlyingNode.getProperty(Stop.TOHUB);
    }

    public String fromHub() {
        return (String) this.underlyingNode.getProperty(Stop.FROMHUB);
    }

    public Double timeToHub() {
        return (Double) this.underlyingNode.getProperty(Stop.TIMETOHUB);
    }

    public Double timeFromHub() {
        return (Double) this.underlyingNode.getProperty(Stop.TIMEFROMHUB);
    }

    public Integer getStopNum() {
        return (Integer) this.underlyingNode.getProperty(STOPNUM);
    }

    public String getStopId() {
        return (String) this.underlyingNode.getProperty(STOPID);
    }

    public String getStopName() {
        return (String) this.underlyingNode.getProperty(STOPNAME);
    }

    public Coordinate getCoordinate() {
        return new Coordinate(this.getLat(), this.getLon());
    }

    public double getLat() {
        return (Double) this.underlyingNode.getProperty(LATITUDE);
    }

    public double getLon() {
        return (Double) this.underlyingNode.getProperty(LONGITUDE);
    }


}
