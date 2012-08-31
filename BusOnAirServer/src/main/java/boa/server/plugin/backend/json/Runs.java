package boa.server.plugin.backend.json;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "List" )
public class Runs{
    public List<Run> runsObjectsList;

	public Runs() {
		runsObjectsList = new LinkedList<Run>();
	};

	public Runs(Collection<Run> runsObjectsList) {
		super();
		this.runsObjectsList = (runsObjectsList != null) ? new LinkedList<Run>(runsObjectsList) : new LinkedList<Run>();
	}
}
