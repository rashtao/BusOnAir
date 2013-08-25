package boa.server.plugin.json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "stop" )
public class Stop
{
    private int id;
    private int staticTime;
    private String nextInRun;
    private String prevInRun;
    private String station;
    private String run;
    private String url;
    
	public Stop()
    {
    }

    private Stop( int id, int staticTime )
    {
        this.id = id;
        this.staticTime = staticTime;
    }

    public Stop( boa.server.domain.Stop s )
    {
    	this(s.getId(), s.getStaticTime());
    	setNextInRun(s.getNextInRun() != null ? s.getNextInRun().getUrl() : "");
    	setPrevInRun(s.getPrevInRun() != null ? s.getPrevInRun().getUrl() : "");
    	setStation(s.getStation().getUrl());
    	setRun(s.getRun().getUrl());
    	setUrl(s.getUrl());
    }
    
    public int getId(){
    	return id;
    }
    
    public void setId(int id){
    	this.id = id;
    }
    
    public int getStaticTime() {
		return staticTime;
	}

	public void setStaticTime(int staticTime) {
		this.staticTime = staticTime;
	}

	public String getNextInRun() {
		return nextInRun;
	}

	public void setNextInRun(String nextInRun) {
		this.nextInRun = nextInRun;
	}

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	public String getRun() {
		return run;
	}

	public void setRun(String run) {
		this.run = run;
	}

	public String getPrevInRun() {
		return prevInRun;
	}

	public void setPrevInRun(String prevInRun) {
		this.prevInRun = prevInRun;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
