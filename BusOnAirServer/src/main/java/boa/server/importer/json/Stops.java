package boa.server.importer.json;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "List" )
public class Stops{
    public List<Stop> stopsObjectsList;

	public Stops() {};

	public Stops(List<Stop> stopsObjectsList) {
		super();
		this.stopsObjectsList = (stopsObjectsList != null) ? new LinkedList<Stop>(stopsObjectsList) : null;
	}
}
