package boa.server.importer.json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "run" )
public class Run
{
    private int id;
    private long route;
    private int firstStop;
    private long firstCheckPoint;    
    
    public Run()
    {
    }

	public Run(int id, long route, int firstStop, long firstCheckPoint) {
		super();
		this.id = id;
		this.route = route;
		this.firstStop = firstStop;
		this.firstCheckPoint = firstCheckPoint;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getRoute() {
		return route;
	}

	public void setRoute(long route) {
		this.route = route;
	}

	public int getFirstStop() {
		return firstStop;
	}

	public void setFirstStop(int firstStop) {
		this.firstStop = firstStop;
	}

	public long getFirstCheckPoint() {
		return firstCheckPoint;
	}

	public void setFirstCheckPoint(long firstCheckPoint) {
		this.firstCheckPoint = firstCheckPoint;
	}

}
