package json;

import ie.transportdublin.server.plugin.json.Coordinate;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "run" )
public class Run
{
    private int runId;
    private Coordinate latLon;
    private int lastUpdateTime;

    public Run()
    {
    }

    public Run(int runId, Coordinate latLon, int lastUpdateTime){
        this.runId = runId;
        this.latLon = latLon;
        this.lastUpdateTime = lastUpdateTime;
    }

    public Run( domain.Run r )
    {
    	this(
    			r.getId(), 
    			new Coordinate(r.getLatitude(), r.getLongitude()), 
    			r.getLastUpdateTime());
    }
    
    public int getRunId(){
    	return runId;
    }
    
    public void setRunId(int runId){
    	this.runId = runId;
    }

	public Coordinate getLatLon() {
		return latLon;
	}

	public void setLatLon(Coordinate latLon) {
		this.latLon = latLon;
	}

	public int getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(int lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
    
    
}
