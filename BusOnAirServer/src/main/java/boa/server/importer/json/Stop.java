package boa.server.importer.json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "stop" )
public class Stop
{
    private int id;
    private int staticTime;
    private int nextInRun;
    private int prevInRun;
    private long station;
    private int run;
    
	public Stop()
    {
    }

	public Stop(int id, int staticTime, int nextInRun, int prevInRun,
			long station, int run) {
		super();
		this.id = id;
		this.staticTime = staticTime;
		this.nextInRun = nextInRun;
		this.prevInRun = prevInRun;
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

	public int getNextInRun() {
		return nextInRun;
	}

	public void setNextInRun(int nextInRun) {
		this.nextInRun = nextInRun;
	}

	public int getPrevInRun() {
		return prevInRun;
	}

	public void setPrevInRun(int prevInRun) {
		this.prevInRun = prevInRun;
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
