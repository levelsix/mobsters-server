/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IBoosterPackConfig;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.types.UInteger;


/**
 * This class is generated by jOOQ.
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.1"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
@Entity
@Table(name = "booster_pack_config", schema = "mobsters")
public class BoosterPackConfig implements IBoosterPackConfig {

	private static final long serialVersionUID = -140252697;

	private Integer  id;
	private String   name;
	private UInteger gemPrice;
	private Integer  gachaCreditsPrice;
	private String   listBackgroundImgName;
	private String   listDescription;
	private String   navBarImgName;
	private String   navTitleImgName;
	private String   machineImgName;
	private Integer  expPerItem;
	private Boolean  displayToUser;
	private Integer  riggedId;
	private String   type;

	public BoosterPackConfig() {}

	public BoosterPackConfig(BoosterPackConfig value) {
		this.id = value.id;
		this.name = value.name;
		this.gemPrice = value.gemPrice;
		this.gachaCreditsPrice = value.gachaCreditsPrice;
		this.listBackgroundImgName = value.listBackgroundImgName;
		this.listDescription = value.listDescription;
		this.navBarImgName = value.navBarImgName;
		this.navTitleImgName = value.navTitleImgName;
		this.machineImgName = value.machineImgName;
		this.expPerItem = value.expPerItem;
		this.displayToUser = value.displayToUser;
		this.riggedId = value.riggedId;
		this.type = value.type;
	}

	public BoosterPackConfig(
		Integer  id,
		String   name,
		UInteger gemPrice,
		Integer  gachaCreditsPrice,
		String   listBackgroundImgName,
		String   listDescription,
		String   navBarImgName,
		String   navTitleImgName,
		String   machineImgName,
		Integer  expPerItem,
		Boolean  displayToUser,
		Integer  riggedId,
		String   type
	) {
		this.id = id;
		this.name = name;
		this.gemPrice = gemPrice;
		this.gachaCreditsPrice = gachaCreditsPrice;
		this.listBackgroundImgName = listBackgroundImgName;
		this.listDescription = listDescription;
		this.navBarImgName = navBarImgName;
		this.navTitleImgName = navTitleImgName;
		this.machineImgName = machineImgName;
		this.expPerItem = expPerItem;
		this.displayToUser = displayToUser;
		this.riggedId = riggedId;
		this.type = type;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public BoosterPackConfig setId(Integer id) {
		this.id = id;
		return this;
	}

	@Column(name = "name", length = 45)
	@Size(max = 45)
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public BoosterPackConfig setName(String name) {
		this.name = name;
		return this;
	}

	@Column(name = "gem_price", precision = 7)
	@Override
	public UInteger getGemPrice() {
		return this.gemPrice;
	}

	@Override
	public BoosterPackConfig setGemPrice(UInteger gemPrice) {
		this.gemPrice = gemPrice;
		return this;
	}

	@Column(name = "gacha_credits_price", precision = 10)
	@Override
	public Integer getGachaCreditsPrice() {
		return this.gachaCreditsPrice;
	}

	@Override
	public BoosterPackConfig setGachaCreditsPrice(Integer gachaCreditsPrice) {
		this.gachaCreditsPrice = gachaCreditsPrice;
		return this;
	}

	@Column(name = "list_background_img_name", length = 45)
	@Size(max = 45)
	@Override
	public String getListBackgroundImgName() {
		return this.listBackgroundImgName;
	}

	@Override
	public BoosterPackConfig setListBackgroundImgName(String listBackgroundImgName) {
		this.listBackgroundImgName = listBackgroundImgName;
		return this;
	}

	@Column(name = "list_description", length = 65535)
	@Size(max = 65535)
	@Override
	public String getListDescription() {
		return this.listDescription;
	}

	@Override
	public BoosterPackConfig setListDescription(String listDescription) {
		this.listDescription = listDescription;
		return this;
	}

	@Column(name = "nav_bar_img_name", length = 45)
	@Size(max = 45)
	@Override
	public String getNavBarImgName() {
		return this.navBarImgName;
	}

	@Override
	public BoosterPackConfig setNavBarImgName(String navBarImgName) {
		this.navBarImgName = navBarImgName;
		return this;
	}

	@Column(name = "nav_title_img_name", length = 45)
	@Size(max = 45)
	@Override
	public String getNavTitleImgName() {
		return this.navTitleImgName;
	}

	@Override
	public BoosterPackConfig setNavTitleImgName(String navTitleImgName) {
		this.navTitleImgName = navTitleImgName;
		return this;
	}

	@Column(name = "machine_img_name", length = 45)
	@Size(max = 45)
	@Override
	public String getMachineImgName() {
		return this.machineImgName;
	}

	@Override
	public BoosterPackConfig setMachineImgName(String machineImgName) {
		this.machineImgName = machineImgName;
		return this;
	}

	@Column(name = "exp_per_item", precision = 10)
	@Override
	public Integer getExpPerItem() {
		return this.expPerItem;
	}

	@Override
	public BoosterPackConfig setExpPerItem(Integer expPerItem) {
		this.expPerItem = expPerItem;
		return this;
	}

	@Column(name = "display_to_user", precision = 1)
	@Override
	public Boolean getDisplayToUser() {
		return this.displayToUser;
	}

	@Override
	public BoosterPackConfig setDisplayToUser(Boolean displayToUser) {
		this.displayToUser = displayToUser;
		return this;
	}

	@Column(name = "rigged_id", precision = 10)
	@Override
	public Integer getRiggedId() {
		return this.riggedId;
	}

	@Override
	public BoosterPackConfig setRiggedId(Integer riggedId) {
		this.riggedId = riggedId;
		return this;
	}

	@Column(name = "type", length = 45)
	@Size(max = 45)
	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public BoosterPackConfig setType(String type) {
		this.type = type;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IBoosterPackConfig from) {
		setId(from.getId());
		setName(from.getName());
		setGemPrice(from.getGemPrice());
		setGachaCreditsPrice(from.getGachaCreditsPrice());
		setListBackgroundImgName(from.getListBackgroundImgName());
		setListDescription(from.getListDescription());
		setNavBarImgName(from.getNavBarImgName());
		setNavTitleImgName(from.getNavTitleImgName());
		setMachineImgName(from.getMachineImgName());
		setExpPerItem(from.getExpPerItem());
		setDisplayToUser(from.getDisplayToUser());
		setRiggedId(from.getRiggedId());
		setType(from.getType());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IBoosterPackConfig> E into(E into) {
		into.from(this);
		return into;
	}
}
