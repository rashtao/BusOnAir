package boa.server.importer.json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "stop" )
public class Stop
{
    private int id;
    private int staticTime;
    private long nextInRun;
    private long station;
    private int run;
    
	public Stop()
    {
    }

	public Stop(int id, int staticTime, long nextInRun, long station, int run) {
		super();
		this.id = id;
		this.staticTime = staticTime;
		this.nextInRun = nextInRun;
		this.station = station;
		this.run = run;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStaticTime() {
		return staticTime;
	}

	public void setStaticTime(int staticTime) {
		this.staticTime = staticTime;
	}

	public long getNextInRun() {
		return nextInRun;
	}

	public void setNextInRun(long nextInRun) {
		this.nextInRun = nextInRun;
	}

	public long getStation() {
		return station;
	}

	public void setStation(long station) {
		this.station = station;
	}

	public int getRun() {
		return run;
	}

	public void setRun(int run) {
		this.run = run;
	}
	
	
}
