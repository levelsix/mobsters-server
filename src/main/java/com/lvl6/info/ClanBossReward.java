package com.lvl6.info;

import java.io.Serializable;

public class ClanBossReward implements Serializable {

  private static final long serialVersionUID = 4581148128205247466L;
  
  private int id;
  private int clanBossId;
  private int equipId;
  
  public ClanBossReward(int id, int clanBossId, int equipId) {
    super();
    this.id = id;
    this.clanBossId = clanBossId;
    this.equipId = equipId;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getClanBossId() {
    return clanBossId;
  }

  public void setClanBossId(int clanBossId) {
    this.clanBossId = clanBossId;
  }

  public int getEquipId() {
    return equipId;
  }

  public void setEquipId(int equipId) {
    this.equipId = equipId;
  }

  @Override
  public String toString() {
    return "ClanBossReward [id=" + id + ", clanBossId=" + clanBossId
        + ", equipId=" + equipId + "]";
  }
  
}
