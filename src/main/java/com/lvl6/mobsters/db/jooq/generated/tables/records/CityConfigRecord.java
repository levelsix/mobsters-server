/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.CityConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ICityConfig;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Row;
import org.jooq.Row10;
import org.jooq.impl.UpdatableRecordImpl;
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
@Table(name = "city_config", schema = "mobsters")
public class CityConfigRecord extends UpdatableRecordImpl<CityConfigRecord> implements Record10<UInteger, String, String, Double, Double, String, String, Double, Double, String>, ICityConfig {

	private static final long serialVersionUID = 103961137;

	/**
	 * Setter for <code>mobsters.city_config.id</code>.
	 */
	@Override
	public CityConfigRecord setId(UInteger value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.city_config.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public UInteger getId() {
		return (UInteger) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.city_config.name</code>.
	 */
	@Override
	public CityConfigRecord setName(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.city_config.name</code>.
	 */
	@Column(name = "name", nullable = false, length = 45)
	@NotNull
	@Size(max = 45)
	@Override
	public String getName() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.city_config.map_img_name</code>.
	 */
	@Override
	public CityConfigRecord setMapImgName(String value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.city_config.map_img_name</code>.
	 */
	@Column(name = "map_img_name", nullable = false, length = 45)
	@NotNull
	@Size(max = 45)
	@Override
	public String getMapImgName() {
		return (String) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.city_config.center_xcoord</code>.
	 */
	@Override
	public CityConfigRecord setCenterXcoord(Double value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.city_config.center_xcoord</code>.
	 */
	@Column(name = "center_xcoord", nullable = false, precision = 12)
	@NotNull
	@Override
	public Double getCenterXcoord() {
		return (Double) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.city_config.center_ycoord</code>.
	 */
	@Override
	public CityConfigRecord setCenterYcoord(Double value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.city_config.center_ycoord</code>.
	 */
	@Column(name = "center_ycoord", nullable = false, precision = 12)
	@NotNull
	@Override
	public Double getCenterYcoord() {
		return (Double) getValue(4);
	}

	/**
	 * Setter for <code>mobsters.city_config.road_img_name</code>.
	 */
	@Override
	public CityConfigRecord setRoadImgName(String value) {
		setValue(5, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.city_config.road_img_name</code>.
	 */
	@Column(name = "road_img_name", length = 45)
	@Size(max = 45)
	@Override
	public String getRoadImgName() {
		return (String) getValue(5);
	}

	/**
	 * Setter for <code>mobsters.city_config.map_tmx_name</code>.
	 */
	@Override
	public CityConfigRecord setMapTmxName(String value) {
		setValue(6, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.city_config.map_tmx_name</code>.
	 */
	@Column(name = "map_tmx_name", length = 45)
	@Size(max = 45)
	@Override
	public String getMapTmxName() {
		return (String) getValue(6);
	}

	/**
	 * Setter for <code>mobsters.city_config.road_img_pos_x</code>.
	 */
	@Override
	public CityConfigRecord setRoadImgPosX(Double value) {
		setValue(7, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.city_config.road_img_pos_x</code>.
	 */
	@Column(name = "road_img_pos_x", precision = 12)
	@Override
	public Double getRoadImgPosX() {
		return (Double) getValue(7);
	}

	/**
	 * Setter for <code>mobsters.city_config.road_img_pos_y</code>.
	 */
	@Override
	public CityConfigRecord setRoadImgPosY(Double value) {
		setValue(8, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.city_config.road_img_pos_y</code>.
	 */
	@Column(name = "road_img_pos_y", precision = 12)
	@Override
	public Double getRoadImgPosY() {
		return (Double) getValue(8);
	}

	/**
	 * Setter for <code>mobsters.city_config.attack_map_label_img_name</code>.
	 */
	@Override
	public CityConfigRecord setAttackMapLabelImgName(String value) {
		setValue(9, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.city_config.attack_map_label_img_name</code>.
	 */
	@Column(name = "attack_map_label_img_name", length = 45)
	@Size(max = 45)
	@Override
	public String getAttackMapLabelImgName() {
		return (String) getValue(9);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record1<UInteger> key() {
		return (Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record10 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row10<UInteger, String, String, Double, Double, String, String, Double, Double, String> fieldsRow() {
		return (Row10) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row10<UInteger, String, String, Double, Double, String, String, Double, Double, String> valuesRow() {
		return (Row10) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<UInteger> field1() {
		return CityConfig.CITY_CONFIG.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return CityConfig.CITY_CONFIG.NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field3() {
		return CityConfig.CITY_CONFIG.MAP_IMG_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Double> field4() {
		return CityConfig.CITY_CONFIG.CENTER_XCOORD;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Double> field5() {
		return CityConfig.CITY_CONFIG.CENTER_YCOORD;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field6() {
		return CityConfig.CITY_CONFIG.ROAD_IMG_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field7() {
		return CityConfig.CITY_CONFIG.MAP_TMX_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Double> field8() {
		return CityConfig.CITY_CONFIG.ROAD_IMG_POS_X;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Double> field9() {
		return CityConfig.CITY_CONFIG.ROAD_IMG_POS_Y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field10() {
		return CityConfig.CITY_CONFIG.ATTACK_MAP_LABEL_IMG_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UInteger value1() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value2() {
		return getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value3() {
		return getMapImgName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double value4() {
		return getCenterXcoord();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double value5() {
		return getCenterYcoord();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value6() {
		return getRoadImgName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value7() {
		return getMapTmxName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double value8() {
		return getRoadImgPosX();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double value9() {
		return getRoadImgPosY();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value10() {
		return getAttackMapLabelImgName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CityConfigRecord value1(UInteger value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CityConfigRecord value2(String value) {
		setName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CityConfigRecord value3(String value) {
		setMapImgName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CityConfigRecord value4(Double value) {
		setCenterXcoord(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CityConfigRecord value5(Double value) {
		setCenterYcoord(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CityConfigRecord value6(String value) {
		setRoadImgName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CityConfigRecord value7(String value) {
		setMapTmxName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CityConfigRecord value8(Double value) {
		setRoadImgPosX(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CityConfigRecord value9(Double value) {
		setRoadImgPosY(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CityConfigRecord value10(String value) {
		setAttackMapLabelImgName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CityConfigRecord values(UInteger value1, String value2, String value3, Double value4, Double value5, String value6, String value7, Double value8, Double value9, String value10) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		value6(value6);
		value7(value7);
		value8(value8);
		value9(value9);
		value10(value10);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(ICityConfig from) {
		setId(from.getId());
		setName(from.getName());
		setMapImgName(from.getMapImgName());
		setCenterXcoord(from.getCenterXcoord());
		setCenterYcoord(from.getCenterYcoord());
		setRoadImgName(from.getRoadImgName());
		setMapTmxName(from.getMapTmxName());
		setRoadImgPosX(from.getRoadImgPosX());
		setRoadImgPosY(from.getRoadImgPosY());
		setAttackMapLabelImgName(from.getAttackMapLabelImgName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends ICityConfig> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached CityConfigRecord
	 */
	public CityConfigRecord() {
		super(CityConfig.CITY_CONFIG);
	}

	/**
	 * Create a detached, initialised CityConfigRecord
	 */
	public CityConfigRecord(UInteger id, String name, String mapImgName, Double centerXcoord, Double centerYcoord, String roadImgName, String mapTmxName, Double roadImgPosX, Double roadImgPosY, String attackMapLabelImgName) {
		super(CityConfig.CITY_CONFIG);

		setValue(0, id);
		setValue(1, name);
		setValue(2, mapImgName);
		setValue(3, centerXcoord);
		setValue(4, centerYcoord);
		setValue(5, roadImgName);
		setValue(6, mapTmxName);
		setValue(7, roadImgPosX);
		setValue(8, roadImgPosY);
		setValue(9, attackMapLabelImgName);
	}
}
