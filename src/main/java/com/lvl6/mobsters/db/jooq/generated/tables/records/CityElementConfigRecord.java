/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.CityElementConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ICityElementConfig;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record11;
import org.jooq.Record2;
import org.jooq.Row;
import org.jooq.Row11;
import org.jooq.impl.UpdatableRecordImpl;


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
@Table(name = "city_element_config", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"city_id", "asset_id"})
})
public class CityElementConfigRecord extends UpdatableRecordImpl<CityElementConfigRecord> implements Record11<Integer, Integer, String, Double, Double, Double, Double, String, String, Double, Double>, ICityElementConfig {

	private static final long serialVersionUID = 1934346068;

	/**
	 * Setter for <code>mobsters.city_element_config.city_id</code>.
	 */
	@Override
	public CityElementConfigRecord setCityId(Integer value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.city_element_config.city_id</code>.
	 */
	@Column(name = "city_id", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getCityId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.city_element_config.asset_id</code>.
	 */
	@Override
	public CityElementConfigRecord setAssetId(Integer value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.city_element_config.asset_id</code>.
	 */
	@Column(name = "asset_id", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getAssetId() {
		return (Integer) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.city_element_config.elem_type</code>.
	 */
	@Override
	public CityElementConfigRecord setElemType(String value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.city_element_config.elem_type</code>.
	 */
	@Column(name = "elem_type", nullable = false, length = 45)
	@NotNull
	@Size(max = 45)
	@Override
	public String getElemType() {
		return (String) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.city_element_config.x_coordinate</code>.
	 */
	@Override
	public CityElementConfigRecord setXCoordinate(Double value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.city_element_config.x_coordinate</code>.
	 */
	@Column(name = "x_coordinate", nullable = false, precision = 12)
	@NotNull
	@Override
	public Double getXCoordinate() {
		return (Double) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.city_element_config.y_coordinate</code>.
	 */
	@Override
	public CityElementConfigRecord setYCoordinate(Double value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.city_element_config.y_coordinate</code>.
	 */
	@Column(name = "y_coordinate", nullable = false, precision = 12)
	@NotNull
	@Override
	public Double getYCoordinate() {
		return (Double) getValue(4);
	}

	/**
	 * Setter for <code>mobsters.city_element_config.x_length</code>.
	 */
	@Override
	public CityElementConfigRecord setXLength(Double value) {
		setValue(5, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.city_element_config.x_length</code>.
	 */
	@Column(name = "x_length", precision = 12)
	@Override
	public Double getXLength() {
		return (Double) getValue(5);
	}

	/**
	 * Setter for <code>mobsters.city_element_config.y_length</code>.
	 */
	@Override
	public CityElementConfigRecord setYLength(Double value) {
		setValue(6, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.city_element_config.y_length</code>.
	 */
	@Column(name = "y_length", precision = 12)
	@Override
	public Double getYLength() {
		return (Double) getValue(6);
	}

	/**
	 * Setter for <code>mobsters.city_element_config.img_id_good</code>.
	 */
	@Override
	public CityElementConfigRecord setImgIdGood(String value) {
		setValue(7, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.city_element_config.img_id_good</code>.
	 */
	@Column(name = "img_id_good", nullable = false, length = 45)
	@NotNull
	@Size(max = 45)
	@Override
	public String getImgIdGood() {
		return (String) getValue(7);
	}

	/**
	 * Setter for <code>mobsters.city_element_config.orientation</code>.
	 */
	@Override
	public CityElementConfigRecord setOrientation(String value) {
		setValue(8, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.city_element_config.orientation</code>.
	 */
	@Column(name = "orientation", length = 45)
	@Size(max = 45)
	@Override
	public String getOrientation() {
		return (String) getValue(8);
	}

	/**
	 * Setter for <code>mobsters.city_element_config.sprite_landing_coordinate_x</code>.
	 */
	@Override
	public CityElementConfigRecord setSpriteLandingCoordinateX(Double value) {
		setValue(9, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.city_element_config.sprite_landing_coordinate_x</code>.
	 */
	@Column(name = "sprite_landing_coordinate_x", precision = 12)
	@Override
	public Double getSpriteLandingCoordinateX() {
		return (Double) getValue(9);
	}

	/**
	 * Setter for <code>mobsters.city_element_config.sprite_landing_coordinate_y</code>.
	 */
	@Override
	public CityElementConfigRecord setSpriteLandingCoordinateY(Double value) {
		setValue(10, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.city_element_config.sprite_landing_coordinate_y</code>.
	 */
	@Column(name = "sprite_landing_coordinate_y", precision = 12)
	@Override
	public Double getSpriteLandingCoordinateY() {
		return (Double) getValue(10);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record2<Integer, Integer> key() {
		return (Record2) super.key();
	}

	// -------------------------------------------------------------------------
	// Record11 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row11<Integer, Integer, String, Double, Double, Double, Double, String, String, Double, Double> fieldsRow() {
		return (Row11) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row11<Integer, Integer, String, Double, Double, Double, Double, String, String, Double, Double> valuesRow() {
		return (Row11) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return CityElementConfig.CITY_ELEMENT_CONFIG.CITY_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field2() {
		return CityElementConfig.CITY_ELEMENT_CONFIG.ASSET_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field3() {
		return CityElementConfig.CITY_ELEMENT_CONFIG.ELEM_TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Double> field4() {
		return CityElementConfig.CITY_ELEMENT_CONFIG.X_COORDINATE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Double> field5() {
		return CityElementConfig.CITY_ELEMENT_CONFIG.Y_COORDINATE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Double> field6() {
		return CityElementConfig.CITY_ELEMENT_CONFIG.X_LENGTH;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Double> field7() {
		return CityElementConfig.CITY_ELEMENT_CONFIG.Y_LENGTH;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field8() {
		return CityElementConfig.CITY_ELEMENT_CONFIG.IMG_ID_GOOD;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field9() {
		return CityElementConfig.CITY_ELEMENT_CONFIG.ORIENTATION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Double> field10() {
		return CityElementConfig.CITY_ELEMENT_CONFIG.SPRITE_LANDING_COORDINATE_X;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Double> field11() {
		return CityElementConfig.CITY_ELEMENT_CONFIG.SPRITE_LANDING_COORDINATE_Y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value1() {
		return getCityId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value2() {
		return getAssetId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value3() {
		return getElemType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double value4() {
		return getXCoordinate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double value5() {
		return getYCoordinate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double value6() {
		return getXLength();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double value7() {
		return getYLength();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value8() {
		return getImgIdGood();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value9() {
		return getOrientation();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double value10() {
		return getSpriteLandingCoordinateX();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double value11() {
		return getSpriteLandingCoordinateY();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CityElementConfigRecord value1(Integer value) {
		setCityId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CityElementConfigRecord value2(Integer value) {
		setAssetId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CityElementConfigRecord value3(String value) {
		setElemType(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CityElementConfigRecord value4(Double value) {
		setXCoordinate(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CityElementConfigRecord value5(Double value) {
		setYCoordinate(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CityElementConfigRecord value6(Double value) {
		setXLength(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CityElementConfigRecord value7(Double value) {
		setYLength(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CityElementConfigRecord value8(String value) {
		setImgIdGood(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CityElementConfigRecord value9(String value) {
		setOrientation(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CityElementConfigRecord value10(Double value) {
		setSpriteLandingCoordinateX(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CityElementConfigRecord value11(Double value) {
		setSpriteLandingCoordinateY(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CityElementConfigRecord values(Integer value1, Integer value2, String value3, Double value4, Double value5, Double value6, Double value7, String value8, String value9, Double value10, Double value11) {
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
		value11(value11);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(ICityElementConfig from) {
		setCityId(from.getCityId());
		setAssetId(from.getAssetId());
		setElemType(from.getElemType());
		setXCoordinate(from.getXCoordinate());
		setYCoordinate(from.getYCoordinate());
		setXLength(from.getXLength());
		setYLength(from.getYLength());
		setImgIdGood(from.getImgIdGood());
		setOrientation(from.getOrientation());
		setSpriteLandingCoordinateX(from.getSpriteLandingCoordinateX());
		setSpriteLandingCoordinateY(from.getSpriteLandingCoordinateY());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends ICityElementConfig> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached CityElementConfigRecord
	 */
	public CityElementConfigRecord() {
		super(CityElementConfig.CITY_ELEMENT_CONFIG);
	}

	/**
	 * Create a detached, initialised CityElementConfigRecord
	 */
	public CityElementConfigRecord(Integer cityId, Integer assetId, String elemType, Double xCoordinate, Double yCoordinate, Double xLength, Double yLength, String imgIdGood, String orientation, Double spriteLandingCoordinateX, Double spriteLandingCoordinateY) {
		super(CityElementConfig.CITY_ELEMENT_CONFIG);

		setValue(0, cityId);
		setValue(1, assetId);
		setValue(2, elemType);
		setValue(3, xCoordinate);
		setValue(4, yCoordinate);
		setValue(5, xLength);
		setValue(6, yLength);
		setValue(7, imgIdGood);
		setValue(8, orientation);
		setValue(9, spriteLandingCoordinateX);
		setValue(10, spriteLandingCoordinateY);
	}
}
