package boa.server.plugin.json;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "List")
public class RunsObjects {
    @XmlElement(name = "runsObjectsList")
    List<Run> runlist = new ArrayList<Run>();

    public RunsObjects() {
    }

    public void add(Run r) {
        runlist.add(r);
    }

    public void add(boa.server.domain.Run r) {
        runlist.add(new Run(r));
    }

}
