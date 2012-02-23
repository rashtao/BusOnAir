package json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "stop" )
public class Stop
{
    private int stopId;
    private int time;
    private int staticTime;

	public Stop()
    {
    }

    public Stop( int stopId, int time, int staticTime )
    {
        this.stopId = stopId;
        this.time = time;
        this.staticTime = staticTime;
    }

    public Stop( domain.Stop ds )
    {
    	this(ds.getId(), ds.getTime(), ds.getStaticTime());
    }
    
    public int getStopId(){
    	return stopId;
    }
    
    public void setStopId(int stopId){
    	this.stopId = stopId;
    }
    
    public int getTime(){
    	return time;
    }
    
    public void setTime(int time){
    	this.time = time;
    }
    
    public int getStaticTime() {
		return staticTime;
	}

	public void setStaticTime(int staticTime) {
		this.staticTime = staticTime;
	}
}
