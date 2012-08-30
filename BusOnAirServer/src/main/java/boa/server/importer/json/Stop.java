package boa.server.importer.json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "stop" )
public class Stop
{
    private Integer id;
    private Integer staticTime;
    private Integer nextInRun;
    private Integer prevInRun;
    private Integer station;
    private Integer run;
    
	public Stop()
    {
    }

	public Stop(Integer id, Integer staticTime, Integer nextInRun, Integer prevInRun,
			Integer station, Integer run) {
		super();
		this.id = id;
		this.staticTime = staticTime;
		this.nextInRun = nextInRun;
		this.prevInRun = prevInRun;
		this.station = station;
		this.run = run;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getStaticTime() {
		return staticTime;
	}

	public void setStaticTime(Integer staticTime) {
		this.staticTime = staticTime;
	}

	public Integer getNextInRun() {
		return nextInRun;
	}

	public void setNextInRun(Integer nextInRun) {
		this.nextInRun = nextInRun;
	}

	public Integer getPrevInRun() {
		return prevInRun;
	}

	public void setPrevInRun(Integer prevInRun) {
		this.prevInRun = prevInRun;
	}

	public Integer getStation() {
		return station;
	}

	public void setStation(Integer station) {
		this.station = station;
	}

	public Integer getRun() {
		return run;
	}

	public void setRun(Integer run) {
		this.run = run;
	}

    @Override
    public boolean equals(Object other){
        if (this == other)
        	return true;
        
        if (!(other instanceof Stop)) 
        	return false;
        
        Stop otherStop = (Stop) other;
        
        if(getId() != otherStop.getId())
        	return false;
        
        return true;
    }
    
	@Override
	public String toString() {
		return "Stop [id=" + id + ", staticTime=" + staticTime + ", nextInRun="
				+ nextInRun + ", prevInRun=" + prevInRun + ", station="
				+ station + ", run=" + run + "]";
	}
}
