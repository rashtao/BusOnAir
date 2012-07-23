package boa.server.importer.json;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "checkpoint" )
public class CheckPoint {
    private long id;
    private long dt;
    private Coordinate latLon;
    private int from;
    private int towards;
    private long next;
    private long prev;

	public CheckPoint() {
	}

	public CheckPoint(long id, long dt, Coordinate latLon, int from,
			int towards, long next, long prev) {
		super();
		this.id = id;
		this.dt = dt;
		this.latLon = latLon;
		this.from = from;
		this.towards = towards;
		this.next = next;
		this.prev = prev;
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

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getTowards() {
		return towards;
	}

	public void setTowards(int towards) {
		this.towards = towards;
	}

	public long getNext() {
		return next;
	}

	public void setNext(long next) {
		this.next = next;
	}

	public long getPrev() {
		return prev;
	}

	public void setPrev(long prev) {
		this.prev = prev;
	}

}
