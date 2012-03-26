package boa.server.json;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "checkpoint" )
public class CheckPoint {
    private long id;
    private long dt;
    private Coordinate latLon;
    private String from;
    private String towards;
    private String next;
    private String prev;
    private String url;
    

	public CheckPoint() {
	}

    private CheckPoint( long id, long dt, Coordinate latLon ){
    	super();
        this.id = id;
        this.dt = dt;
        this.latLon = latLon;
    }

    public CheckPoint( boa.server.domain.CheckPoint cp ){
   		this(cp.getId(), cp.getDt(), new Coordinate(cp.getLatitude(), cp.getLongitude()));
        
   		setFrom(cp.getFrom().getUrl());
        
   		setTowards(cp.getTowards().getUrl());
        
        if(cp.getNextCheckPoint() != null){
        	setNext(cp.getNextCheckPoint().getUrl());
        } else {
        	setNext("");
        }
        
        if(cp.getPrevCheckPoint() != null){
        	setPrev(cp.getPrevCheckPoint().getUrl());
        } else {
        	setPrev("");
        }
        
        setUrl(cp.getUrl());
    }

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getDt() {
		return dt;
	}

	public void setDt(long dt) {
		this.dt = dt;
	}

	public Coordinate getLatLon() {
		return latLon;
	}

	public void setLatLon(Coordinate latLon) {
		this.latLon = latLon;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTowards() {
		return towards;
	}

	public void setTowards(String towards) {
		this.towards = towards;
	}

	public String getNext() {
		return next;
	}

	public void setNext(String next) {
		this.next = next;
	}
	
    public String getPrev() {
		return prev;
	}

	public void setPrev(String prev) {
		this.prev = prev;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}	
}
