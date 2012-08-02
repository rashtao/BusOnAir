package boa.server.importer.json;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "List" )
public class Runs{
//    @XmlElement( name = "runsObjectsList" )
    public List<Run> runsObjectsList;

	public Runs() {};

	public Runs(List<Run> runsObjectsList) {
		super();
		this.runsObjectsList = (runsObjectsList != null) ? new LinkedList<Run>(runsObjectsList) : null;
	}
}
