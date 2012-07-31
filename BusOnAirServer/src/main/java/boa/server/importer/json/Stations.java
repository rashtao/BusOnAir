package boa.server.importer.json;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "List" )
public class Stations{
    @XmlElement( name = "stationsObjectsList" )
    ArrayList<Station> stationsObjectsList = new ArrayList<Station>();

	public Stations(){
	}

	public Stations(ArrayList<Station> stationsObjectsList) {
		super();
		this.stationsObjectsList = stationsObjectsList;
	}

	public ArrayList<Station> getStationsObjectsList() {
		return stationsObjectsList;
	}

	public void setStationsObjectsList(ArrayList<Station> stationsObjectsList) {
		this.stationsObjectsList = stationsObjectsList;
	}

}
