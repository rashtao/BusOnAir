package boa.server.plugin.json;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "List")
public class StationsObjects {
    @XmlElement(name = "stationsObjectsList")
    List<Station> stationslist = new ArrayList<Station>();

    public StationsObjects() {
    }

    public void add(Station s) {
        stationslist.add(s);
    }

    public void add(boa.server.domain.Station s) {
        stationslist.add(new Station(s));
    }
}
