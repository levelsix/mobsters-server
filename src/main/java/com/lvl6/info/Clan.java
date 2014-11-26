package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class Clan implements Serializable{
    
    private static final long   serialVersionUID    = -6551373040373362240L;

    private String id;
//  private String ownerId;
    private String name;
    private Date createTime;
    private String description;
    private String tag;
    private boolean requestToJoinRequired;
    private int clanIconId;
    
    public Clan() {
        super();
    }

    public Clan(String id, String name, Date createTime, String description,
            String tag, boolean requestToJoinRequired, int clanIconId) {
        super();
        this.id = id;
        this.name = name;
        this.createTime = createTime;
        this.description = description;
        this.tag = tag;
        this.requestToJoinRequired = requestToJoinRequired;
        this.clanIconId = clanIconId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isRequestToJoinRequired() {
        return requestToJoinRequired;
    }

    public void setRequestToJoinRequired(boolean requestToJoinRequired) {
        this.requestToJoinRequired = requestToJoinRequired;
    }

    public int getClanIconId() {
        return clanIconId;
    }

    public void setClanIconId(int clanIconId) {
        this.clanIconId = clanIconId;
    }

    @Override
    public String toString() {
        return "Clan [id=" + id + ", name=" + name + ", createTime=" + createTime
                + ", description=" + description + ", tag=" + tag
                + ", requestToJoinRequired=" + requestToJoinRequired + ", clanIconId="
                + clanIconId + "]";
    }
    
}
