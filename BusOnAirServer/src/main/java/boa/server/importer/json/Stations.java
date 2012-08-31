package boa.server.importer.json;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "List" )
public class Stations{

    public List<Station> stationsObjectsList;

	public Stations() {
		stationsObjectsList = new LinkedList<Station>();
	};

	public Stations(Collection<Station> stationsObjectsList) {
		super();
		this.stationsObjectsList = (stationsObjectsList != null) ? new LinkedList<Station>(stationsObjectsList) : new LinkedList<Station>();
	}
}
