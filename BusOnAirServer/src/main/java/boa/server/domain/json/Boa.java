package boa.server.domain.json;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement( name = "boaData" )
public class Boa
{
    private Stations stations;
    private Routes routes;
    private Runs runs;
    private Stops stops;
	
    public Boa(){
    }
    
    public Boa(Stations stations, Routes routes, Runs runs, Stops stops) {
		super();
		this.stations = stations;
		this.routes = routes;
		this.runs = runs;
		this.stops = stops;
	}

	public Stations getStations() {
		return stations;
	}

	public void setStations(Stations stations) {
		this.stations = stations;
	}

	public Routes getRoutes() {
		return routes;
	}

	public void setRoutes(Routes routes) {
		this.routes = routes;
	}

	public Runs getRuns() {
		return runs;
	}

	public void setRuns(Runs runs) {
		this.runs = runs;
	}

	public Stops getStops() {
		return stops;
	}

	public void setStops(Stops stops) {
		this.stops = stops;
	}

	@Override
	public String toString() {
		return "BoaData [stations=" + stations + ", routes=" + routes
				+ ", runs=" + runs + ", stops=" + stops + "]";
	}
    
}
