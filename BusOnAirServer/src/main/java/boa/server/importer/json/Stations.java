package boa.server.importer.json;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "List" )
public class Stations{
//    @XmlElement( name = "stationsObjectsList" )
    public List<Station> stationsObjectsList;

	public Stations() {};

	public Stations(List<Station> stationsObjectsList) {
		super();
		this.stationsObjectsList = (stationsObjectsList != null) ? new LinkedList<Station>(stationsObjectsList) : null;
	}
}
