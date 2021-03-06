package net.aixin.app.team.bean;

import java.util.ArrayList;
import java.util.List;

import net.aixin.app.bean.Entity;
import net.aixin.app.bean.ListEntity;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@SuppressWarnings("serial")
@XStreamAlias("oschina")
public class TeamMyActiveList extends Entity implements ListEntity<TeamMyActive> {

    @XStreamAlias("actives")
    private List<TeamMyActive> list = new ArrayList<TeamMyActive>();

    @Override
    public List<TeamMyActive> getList() {
        return list;
    }

    public void setList(List<TeamMyActive> list) {
        this.list = list;
    }

}
