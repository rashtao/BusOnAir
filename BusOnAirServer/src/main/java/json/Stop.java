package json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "stop" )
public class Stop
{
    private int stopId;
    private int time;

    public Stop()
    {
    }

    public Stop( int stopId, int time )
    {
        this.stopId = stopId;
        this.time = time;
    }

    public Stop( domain.Stop ds )
    {
    	this(ds.getId(), ds.getTime());
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
}
