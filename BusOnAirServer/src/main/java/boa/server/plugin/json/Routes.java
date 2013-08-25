package boa.server.plugin.json;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "List")
public class Routes {
    @XmlElement(name = "routelist")
    List<String> routelist = new ArrayList<String>();

    public Routes() {
    }

    public void add(Route r) {
        routelist.add(r.getUrl());
    }

    public void add(boa.server.domain.Route r) {
        routelist.add(r.getUrl());
    }

}
