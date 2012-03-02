package json;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "checkpoint" )
public class CheckPoint {
    private int checkPointId;
    private int time;
    private Coordinate latLon;
    private String from;
    private String towards;
    private String next;
    
 
    public CheckPoint() {
	}

    public CheckPoint( int checkPointId, int time, Coordinate latLon ){
    	super();
        this.checkPointId = checkPointId;
        this.time = time;
        this.latLon = latLon;
    }

    public CheckPoint( domain.CheckPoint cp ){
   		this(cp.getId(), cp.getTime(), new Coordinate(cp.getLatitude(), cp.getLongitude()));
        setFrom("/stops/" + cp.getFrom().getId());
        setTowards("/stops/" + cp.getTowards().getId());
        if(cp.getNextCheckPoint() != null){
        	setNext("/runs/" + cp.getFrom().getRun().getId() + "/checkpoints/" + cp.getNextCheckPoint().getId());
        } else {
        	setNext("");
        }
    }

	public int getCheckPointId() {
		return checkPointId;
	}

	public void setCheckPointId(int checkPointId) {
		this.checkPointId = checkPointId;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
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
