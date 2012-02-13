package json;

import ie.transportdublin.xml.Stop;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.index.lucene.QueryContext;

@XmlRootElement
public class DirectionsRoute
{

    private String deptTime;
    private String arrTime;
    private String routeId;
    private String deptStation;
    private String arrStation;
    private Integer numOfStops;
    private ArrayList<Coordinate> latLon;
    
    public DirectionsRoute( )
    {
        
    }

    public DirectionsRoute(domain.Stop start, domain.Stop end){
        System.out.print("DirectionsRoute from: " + start + " to " + end);
        
        deptTime = convertTime(start.getTime());
        arrTime = convertTime(end.getTime());
        routeId = start.getRun().getRoute().getLine();
        deptStation = start.getStazione().getName();
        arrStation = end.getStazione().getName();
        latLon = new ArrayList<Coordinate>();
       
        int i = 0;
        domain.Stop s = end;
        Stack<Coordinate> coords = new Stack<Coordinate>();
        while(s != null && !s.equals(start)){
            coords.push(new Coordinate(s.getStazione().getLatitude(), s.getStazione().getLongitude()));       
            i++;
            s = s.prevSP;
        }
        
        coords.push(new Coordinate(s.getStazione().getLatitude(), s.getStazione().getLongitude()));  
        
        while(!coords.isEmpty()){
            latLon.add(coords.pop());
        }
        
        numOfStops = i;        
    }   
    
    public DirectionsRoute(  double deptTime, double cost, List<Node> nodeList)
    {
        this.deptTime = convertTime( deptTime );
        this.arrTime = convertTime( deptTime +cost );
        this.routeId = (String) nodeList.get( 0 ).getProperty( Stop.ROUTENAME );
        this.deptStation = ( (String) nodeList.get( 0 ).getProperty( Stop.STOPNAME ) ).split( "," )[0].trim(); 
        this.arrStation = ( (String) nodeList.get( 1 ).getProperty( Stop.STOPNAME ) ).split( "," )[0].trim(); 
        
        Integer startNum = (Integer) nodeList.get( 0 ).getProperty( Stop.STOPNUM );
        Integer endNum = (Integer) nodeList.get( 1 ).getProperty( Stop.STOPNUM ); 
        
        Query query1 = new TermQuery( new Term( Stop.ROUTEID, (String) nodeList.get( 0 ).getProperty(Stop.ROUTEID ) )) ;
        Query query2 = NumericRangeQuery.newIntRange( Stop.STOPNUM, startNum, endNum, true, true );
        BooleanQuery booleanQuery = new BooleanQuery();
        booleanQuery.add(query1, BooleanClause.Occur.MUST);
        booleanQuery.add(query2, BooleanClause.Occur.MUST);
        QueryContext query = new QueryContext( booleanQuery ).sort( new Sort( new SortField( Stop.STOPNUM, SortField.INT ))) ;
        
        IndexHits<Node> stops = nodeList.get( 0 ).getGraphDatabase().index().forNodes( "stopLayer" ).query( query );
        this.numOfStops = stops.size();
        this.latLon = new ArrayList<Coordinate>();
        for(Node node : stops)
            latLon.add( new Coordinate ((Double)node.getProperty( Stop.LATITUDE ) , (Double)node.getProperty( Stop.LONGITUDE )));
    }

    
    @XmlElement
    public String getDeptTime()
    {
        return deptTime;
    }

    public void setDeptTime( String deptTime )
    {
        this.deptTime = deptTime;
    }

    @XmlElement
    public String getArrTime()
    {
        return arrTime;
    }

    public void setArrTime( String arrTime )
    {
        this.arrTime = arrTime;
    }

    @XmlElement
    public String getRouteId()
    {
        return routeId;
    }

    public void setRouteId( String routeId )
    {
        this.routeId = routeId;
    }

    @XmlElement
    public String getDeptStation()
    {
        return deptStation;
    }

    public void setDeptStation( String deptStation )
    {
        this.deptStation = deptStation;
    }

    @XmlElement
    public String getArrStation()
    {
        return arrStation;
    }

    public void setArrStation( String arrStation )
    {
        this.arrStation = arrStation;
    }
    @XmlElement
    public ArrayList<Coordinate> getLatLon()
    {
        return latLon;
    }

    public void setLatLon( ArrayList<Coordinate> latLon )
    {
        this.latLon = latLon;
    }

    @XmlElement
    public Integer getNumOfStops()
    {
        return numOfStops;
    }


    public void setNumOfStops( Integer numOfStops )
    {
        this.numOfStops = numOfStops;
    }
    
    private String convertTime(double time)
    {
        DateTimeFormatter formatter = DateTimeFormat.forPattern( "HH:mm" );
        DateTime dt = new DateTime(2010, 1, 1, 0, 0, 0, 0);
        DateTime date = dt.plusMinutes( (int) time );
        return date.toString( formatter );        
    }


}
