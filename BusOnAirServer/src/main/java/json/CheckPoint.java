package json;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "checkpoint" )
public class CheckPoint {
    private int checkPointId;
    private int dt;
    private Coordinate latLon;
    private String from;
    private String towards;
    private String next;
    
 
    public CheckPoint() {
	}

    public CheckPoint( int checkPointId, int dt, Coordinate latLon ){
    	super();
        this.checkPointId = checkPointId;
        this.dt = dt;
        this.latLon = latLon;
    }

    public CheckPoint( domain.CheckPoint cp ){
   		this(cp.getId(), cp.getDt(), new Coordinate(cp.getLatitude(), cp.getLongitude()));
    }

	public int getCheckPointId() {
		return checkPointId;
	}

	public void setCheckPointId(int checkPointId) {
		this.checkPointId = checkPointId;
	}

	public int getDt() {
		return dt;
	}

	public void setDt(int dt) {
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
	
	
}
