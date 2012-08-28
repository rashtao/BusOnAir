package boa.server.importer.json;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "checkpoint" )
public class CheckPoint {
    private Long id;
    private Long dt;
    private Coordinate latLon;
    private Integer from;
    private Integer towards;
    private Long next;
    private Long prev;

	public CheckPoint() {
	}

	public CheckPoint(Long id, Long dt, Coordinate latLon, Integer from,
			Integer towards, Long next, Long prev) {
		super();
		this.id = id;
		this.dt = dt;
		this.latLon = latLon;
		this.from = from;
		this.towards = towards;
		this.next = next;
		this.prev = prev;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDt() {
		return dt;
	}

	public void setDt(Long dt) {
		this.dt = dt;
	}

	public Coordinate getLatLon() {
		return latLon;
	}

	public void setLatLon(Coordinate latLon) {
		this.latLon = latLon;
	}

	public Integer getFrom() {
		return from;
	}

	public void setFrom(Integer from) {
		this.from = from;
	}

	public Integer getTowards() {
		return towards;
	}

	public void setTowards(Integer towards) {
		this.towards = towards;
	}

	public Long getNext() {
		return next;
	}

	public void setNext(Long next) {
		this.next = next;
	}

	public Long getPrev() {
		return prev;
	}

	public void setPrev(Long prev) {
		this.prev = prev;
	}

    @Override
    public boolean equals(Object other){
        if (this == other)
        	return true;
        
        if (!(other instanceof CheckPoint)) 
        	return false;
        
        CheckPoint otherCheckPoint = (CheckPoint) other;
        
        if(getId() != otherCheckPoint.getId())
        	return false;
        
        return true;
    }
	
}
