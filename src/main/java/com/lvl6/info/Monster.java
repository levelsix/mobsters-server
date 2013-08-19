package com.lvl6.info;

import java.io.Serializable;
import java.util.Random;

public class Monster implements Serializable {

	private static final long serialVersionUID = -1267848696592503097L;
	private int id;
	private String name;
	private int maxHp;
	private String imageName;
	private boolean isBoss;
	private int weaponId;
	private int weaponLvl;
	private int armorId;
	private int armorLvl;
	private int amuletId;
	private int amuletLvl;
	private int expDrop;
	private int minSilverDrop;
	private int maxSilverDrop;
	
	private Random rand;

	public Monster(int id, String name, int maxHp, String imageName,
			boolean isBoss, int weaponId, int weaponLvl, int armorId,
			int armorLvl, int amuletId, int amuletLvl, int expDrop,
			int minSilverDrop, int maxSilverDrop) {
		super();
		this.id = id;
		this.name = name;
		this.maxHp = maxHp;
		this.imageName = imageName;
		this.isBoss = isBoss;
		this.weaponId = weaponId;
		this.weaponLvl = weaponLvl;
		this.armorId = armorId;
		this.armorLvl = armorLvl;
		this.amuletId = amuletId;
		this.amuletLvl = amuletLvl;
		this.expDrop = expDrop;
		this.minSilverDrop = minSilverDrop;
		this.maxSilverDrop = maxSilverDrop;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMaxHp() {
		return maxHp;
	}

	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public boolean isBoss() {
		return isBoss;
	}

	public void setBoss(boolean isBoss) {
		this.isBoss = isBoss;
	}

	public int getWeaponId() {
		return weaponId;
	}

	public void setWeaponId(int weaponId) {
		this.weaponId = weaponId;
	}

	public int getWeaponLvl() {
		return weaponLvl;
	}

	public void setWeaponLvl(int weaponLvl) {
		this.weaponLvl = weaponLvl;
	}

	public int getArmorId() {
		return armorId;
	}

	public void setArmorId(int armorId) {
		this.armorId = armorId;
	}

	public int getArmorLvl() {
		return armorLvl;
	}

	public void setArmorLvl(int armorLvl) {
		this.armorLvl = armorLvl;
	}

	public int getAmuletId() {
		return amuletId;
	}

	public void setAmuletId(int amuletId) {
		this.amuletId = amuletId;
	}

	public int getAmuletLvl() {
		return amuletLvl;
	}

	public void setAmuletLvl(int amuletLvl) {
		this.amuletLvl = amuletLvl;
	}

	public int getExpDrop() {
		return expDrop;
	}

	public void setExpDrop(int expDrop) {
		this.expDrop = expDrop;
	}

	public int getMinSilverDrop() {
		return minSilverDrop;
	}

	public void setMinSilverDrop(int minSilverDrop) {
		this.minSilverDrop = minSilverDrop;
	}

	public int getMaxSilverDrop() {
		return maxSilverDrop;
	}

	public void setMaxSilverDrop(int maxSilverDrop) {
		this.maxSilverDrop = maxSilverDrop;
	}

	public Random getRand() {
		return rand;
	}

	public void setRand(Random rand) {
		this.rand = rand;
	}
	
	public int getSilverDrop() {
		//example goal: [5, 10]
		//generate 6 random numbers [0, 5]
		int range = getMaxSilverDrop() - getMinSilverDrop() + 1;
		
		//5 + [0, 5] = [5, 10]
		int randSilver = rand.nextInt(range) + range; 
		
		return randSilver;
	}

	@Override
	public String toString() {
		return "Monster [id=" + id + ", name=" + name + ", maxHp=" + maxHp
				+ ", imageName=" + imageName + ", isBoss=" + isBoss
				+ ", weaponId=" + weaponId + ", weaponLvl=" + weaponLvl
				+ ", armorId=" + armorId + ", armorLvl=" + armorLvl
				+ ", amuletId=" + amuletId + ", amuletLvl=" + amuletLvl
				+ ", expDrop=" + expDrop + ", minSilverDrop=" + minSilverDrop
				+ ", maxSilverDrop=" + maxSilverDrop + ", rand=" + rand + "]";
	}

}
