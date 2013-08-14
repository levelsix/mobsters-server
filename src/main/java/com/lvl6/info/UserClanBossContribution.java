package com.lvl6.info;

import java.io.Serializable;

public class UserClanBossContribution implements Serializable {

  private static final long serialVersionUID = 9153933930548195860L;
  private int userId;
  private int clanId;
  private int bossId;
  private int totalDamageDone;
  private int totalEnergyUsed;
  private int numRunesOne;
  private int numRunesTwo;
  private int numRunesThree;
  private int numRunesFour;
  private int numRunesFive;
  
  public UserClanBossContribution(int userId, int clanId, int bossId,
      int totalDamageDone, int totalEnergyUsed, int numRunesOne,
      int numRunesTwo, int numRunesThree, int numRunesFour, int numRunesFive) {
    super();
    this.userId = userId;
    this.clanId = clanId;
    this.bossId = bossId;
    this.totalDamageDone = totalDamageDone;
    this.totalEnergyUsed = totalEnergyUsed;
    this.numRunesOne = numRunesOne;
    this.numRunesTwo = numRunesTwo;
    this.numRunesThree = numRunesThree;
    this.numRunesFour = numRunesFour;
    this.numRunesFive = numRunesFive;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public int getClanId() {
    return clanId;
  }

  public void setClanId(int clanId) {
    this.clanId = clanId;
  }

  public int getBossId() {
    return bossId;
  }

  public void setBossId(int bossId) {
    this.bossId = bossId;
  }

  public int getTotalDamageDone() {
    return totalDamageDone;
  }

  public void setTotalDamageDone(int totalDamageDone) {
    this.totalDamageDone = totalDamageDone;
  }

  public int getTotalEnergyUsed() {
    return totalEnergyUsed;
  }

  public void setTotalEnergyUsed(int totalEnergyUsed) {
    this.totalEnergyUsed = totalEnergyUsed;
  }

  public int getNumRunesOne() {
    return numRunesOne;
  }

  public void setNumRunesOne(int numRunesOne) {
    this.numRunesOne = numRunesOne;
  }

  public int getNumRunesTwo() {
    return numRunesTwo;
  }

  public void setNumRunesTwo(int numRunesTwo) {
    this.numRunesTwo = numRunesTwo;
  }

  public int getNumRunesThree() {
    return numRunesThree;
  }

  public void setNumRunesThree(int numRunesThree) {
    this.numRunesThree = numRunesThree;
  }

  public int getNumRunesFour() {
    return numRunesFour;
  }

  public void setNumRunesFour(int numRunesFour) {
    this.numRunesFour = numRunesFour;
  }

  public int getNumRunesFive() {
    return numRunesFive;
  }

  public void setNumRunesFive(int numRunesFive) {
    this.numRunesFive = numRunesFive;
  }

  @Override
  public String toString() {
    return "UserClanBossContribution [userId=" + userId + ", clanId=" + clanId
        + ", bossId=" + bossId + ", totalDamageDone=" + totalDamageDone
        + ", totalEnergyUsed=" + totalEnergyUsed + ", numRunesOne="
        + numRunesOne + ", numRunesTwo=" + numRunesTwo + ", numRunesThree="
        + numRunesThree + ", numRunesFour=" + numRunesFour + ", numRunesFive="
        + numRunesFive + "]";
  }
  
}
