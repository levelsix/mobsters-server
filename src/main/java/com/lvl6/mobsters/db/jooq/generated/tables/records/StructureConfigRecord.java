/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.StructureConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IStructureConfig;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record22;
import org.jooq.Row;
import org.jooq.Row22;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.UByte;
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
@Table(name = "structure_config", schema = "mobsters")
public class StructureConfigRecord extends UpdatableRecordImpl<StructureConfigRecord> implements Record22<UInteger, String, UByte, String, String, UInteger, UInteger, UInteger, UInteger, UInteger, UInteger, String, Double, Double, String, String, String, Double, Double, Double, Integer, Integer>, IStructureConfig {

	private static final long serialVersionUID = -1867259646;

	/**
	 * Setter for <code>mobsters.structure_config.id</code>.
	 */
	@Override
	public StructureConfigRecord setId(UInteger value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_config.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public UInteger getId() {
		return (UInteger) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.structure_config.name</code>.
	 */
	@Override
	public StructureConfigRecord setName(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_config.name</code>.
	 */
	@Column(name = "name", nullable = false, length = 45)
	@NotNull
	@Size(max = 45)
	@Override
	public String getName() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.structure_config.level</code>.
	 */
	@Override
	public StructureConfigRecord setLevel(UByte value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_config.level</code>.
	 */
	@Column(name = "level", nullable = false, precision = 3)
	@NotNull
	@Override
	public UByte getLevel() {
		return (UByte) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.structure_config.struct_type</code>. resource generator/storage, hospital, town hall,...
	 */
	@Override
	public StructureConfigRecord setStructType(String value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_config.struct_type</code>. resource generator/storage, hospital, town hall,...
	 */
	@Column(name = "struct_type", nullable = false, length = 45)
	@NotNull
	@Size(max = 45)
	@Override
	public String getStructType() {
		return (String) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.structure_config.build_resource_type</code>. cash, other soft currency (like CoC's gold, elixir, dark elixir)
	 */
	@Override
	public StructureConfigRecord setBuildResourceType(String value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_config.build_resource_type</code>. cash, other soft currency (like CoC's gold, elixir, dark elixir)
	 */
	@Column(name = "build_resource_type", nullable = false, length = 45)
	@NotNull
	@Size(max = 45)
	@Override
	public String getBuildResourceType() {
		return (String) getValue(4);
	}

	/**
	 * Setter for <code>mobsters.structure_config.build_cost</code>.
	 */
	@Override
	public StructureConfigRecord setBuildCost(UInteger value) {
		setValue(5, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_config.build_cost</code>.
	 */
	@Column(name = "build_cost", nullable = false, precision = 10)
	@NotNull
	@Override
	public UInteger getBuildCost() {
		return (UInteger) getValue(5);
	}

	/**
	 * Setter for <code>mobsters.structure_config.minutes_to_build</code>.
	 */
	@Override
	public StructureConfigRecord setMinutesToBuild(UInteger value) {
		setValue(6, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_config.minutes_to_build</code>.
	 */
	@Column(name = "minutes_to_build", nullable = false, precision = 10)
	@NotNull
	@Override
	public UInteger getMinutesToBuild() {
		return (UInteger) getValue(6);
	}

	/**
	 * Setter for <code>mobsters.structure_config.width</code>.
	 */
	@Override
	public StructureConfigRecord setWidth(UInteger value) {
		setValue(7, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_config.width</code>.
	 */
	@Column(name = "width", nullable = false, precision = 10)
	@NotNull
	@Override
	public UInteger getWidth() {
		return (UInteger) getValue(7);
	}

	/**
	 * Setter for <code>mobsters.structure_config.height</code>.
	 */
	@Override
	public StructureConfigRecord setHeight(UInteger value) {
		setValue(8, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_config.height</code>.
	 */
	@Column(name = "height", nullable = false, precision = 10)
	@NotNull
	@Override
	public UInteger getHeight() {
		return (UInteger) getValue(8);
	}

	/**
	 * Setter for <code>mobsters.structure_config.predecessor_struct_id</code>. id in in this table
	 */
	@Override
	public StructureConfigRecord setPredecessorStructId(UInteger value) {
		setValue(9, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_config.predecessor_struct_id</code>. id in in this table
	 */
	@Column(name = "predecessor_struct_id", precision = 10)
	@Override
	public UInteger getPredecessorStructId() {
		return (UInteger) getValue(9);
	}

	/**
	 * Setter for <code>mobsters.structure_config.successor_struct_id</code>. id in in this table
	 */
	@Override
	public StructureConfigRecord setSuccessorStructId(UInteger value) {
		setValue(10, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_config.successor_struct_id</code>. id in in this table
	 */
	@Column(name = "successor_struct_id", precision = 10)
	@Override
	public UInteger getSuccessorStructId() {
		return (UInteger) getValue(10);
	}

	/**
	 * Setter for <code>mobsters.structure_config.img_name</code>.
	 */
	@Override
	public StructureConfigRecord setImgName(String value) {
		setValue(11, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_config.img_name</code>.
	 */
	@Column(name = "img_name", length = 45)
	@Size(max = 45)
	@Override
	public String getImgName() {
		return (String) getValue(11);
	}

	/**
	 * Setter for <code>mobsters.structure_config.img_vertical_pixel_offset</code>.
	 */
	@Override
	public StructureConfigRecord setImgVerticalPixelOffset(Double value) {
		setValue(12, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_config.img_vertical_pixel_offset</code>.
	 */
	@Column(name = "img_vertical_pixel_offset", precision = 12)
	@Override
	public Double getImgVerticalPixelOffset() {
		return (Double) getValue(12);
	}

	/**
	 * Setter for <code>mobsters.structure_config.img_horizontal_pixel_offset</code>.
	 */
	@Override
	public StructureConfigRecord setImgHorizontalPixelOffset(Double value) {
		setValue(13, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_config.img_horizontal_pixel_offset</code>.
	 */
	@Column(name = "img_horizontal_pixel_offset", precision = 12)
	@Override
	public Double getImgHorizontalPixelOffset() {
		return (Double) getValue(13);
	}

	/**
	 * Setter for <code>mobsters.structure_config.description</code>.
	 */
	@Override
	public StructureConfigRecord setDescription(String value) {
		setValue(14, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_config.description</code>.
	 */
	@Column(name = "description", length = 65535)
	@Size(max = 65535)
	@Override
	public String getDescription() {
		return (String) getValue(14);
	}

	/**
	 * Setter for <code>mobsters.structure_config.short_description</code>.
	 */
	@Override
	public StructureConfigRecord setShortDescription(String value) {
		setValue(15, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_config.short_description</code>.
	 */
	@Column(name = "short_description", length = 150)
	@Size(max = 150)
	@Override
	public String getShortDescription() {
		return (String) getValue(15);
	}

	/**
	 * Setter for <code>mobsters.structure_config.shadow_img_name</code>.
	 */
	@Override
	public StructureConfigRecord setShadowImgName(String value) {
		setValue(16, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_config.shadow_img_name</code>.
	 */
	@Column(name = "shadow_img_name", length = 45)
	@Size(max = 45)
	@Override
	public String getShadowImgName() {
		return (String) getValue(16);
	}

	/**
	 * Setter for <code>mobsters.structure_config.shadow_vertical_offset</code>.
	 */
	@Override
	public StructureConfigRecord setShadowVerticalOffset(Double value) {
		setValue(17, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_config.shadow_vertical_offset</code>.
	 */
	@Column(name = "shadow_vertical_offset", precision = 12)
	@Override
	public Double getShadowVerticalOffset() {
		return (Double) getValue(17);
	}

	/**
	 * Setter for <code>mobsters.structure_config.shadow_horizontal_offset</code>.
	 */
	@Override
	public StructureConfigRecord setShadowHorizontalOffset(Double value) {
		setValue(18, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_config.shadow_horizontal_offset</code>.
	 */
	@Column(name = "shadow_horizontal_offset", precision = 12)
	@Override
	public Double getShadowHorizontalOffset() {
		return (Double) getValue(18);
	}

	/**
	 * Setter for <code>mobsters.structure_config.shadow_scale</code>.
	 */
	@Override
	public StructureConfigRecord setShadowScale(Double value) {
		setValue(19, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_config.shadow_scale</code>.
	 */
	@Column(name = "shadow_scale", precision = 12)
	@Override
	public Double getShadowScale() {
		return (Double) getValue(19);
	}

	/**
	 * Setter for <code>mobsters.structure_config.exp_reward</code>.
	 */
	@Override
	public StructureConfigRecord setExpReward(Integer value) {
		setValue(20, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_config.exp_reward</code>.
	 */
	@Column(name = "exp_reward", precision = 10)
	@Override
	public Integer getExpReward() {
		return (Integer) getValue(20);
	}

	/**
	 * Setter for <code>mobsters.structure_config.strength</code>.
	 */
	@Override
	public StructureConfigRecord setStrength(Integer value) {
		setValue(21, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_config.strength</code>.
	 */
	@Column(name = "strength", precision = 10)
	@Override
	public Integer getStrength() {
		return (Integer) getValue(21);
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
	// Record22 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row22<UInteger, String, UByte, String, String, UInteger, UInteger, UInteger, UInteger, UInteger, UInteger, String, Double, Double, String, String, String, Double, Double, Double, Integer, Integer> fieldsRow() {
		return (Row22) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row22<UInteger, String, UByte, String, String, UInteger, UInteger, UInteger, UInteger, UInteger, UInteger, String, Double, Double, String, String, String, Double, Double, Double, Integer, Integer> valuesRow() {
		return (Row22) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<UInteger> field1() {
		return StructureConfig.STRUCTURE_CONFIG.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return StructureConfig.STRUCTURE_CONFIG.NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<UByte> field3() {
		return StructureConfig.STRUCTURE_CONFIG.LEVEL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field4() {
		return StructureConfig.STRUCTURE_CONFIG.STRUCT_TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field5() {
		return StructureConfig.STRUCTURE_CONFIG.BUILD_RESOURCE_TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<UInteger> field6() {
		return StructureConfig.STRUCTURE_CONFIG.BUILD_COST;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<UInteger> field7() {
		return StructureConfig.STRUCTURE_CONFIG.MINUTES_TO_BUILD;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<UInteger> field8() {
		return StructureConfig.STRUCTURE_CONFIG.WIDTH;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<UInteger> field9() {
		return StructureConfig.STRUCTURE_CONFIG.HEIGHT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<UInteger> field10() {
		return StructureConfig.STRUCTURE_CONFIG.PREDECESSOR_STRUCT_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<UInteger> field11() {
		return StructureConfig.STRUCTURE_CONFIG.SUCCESSOR_STRUCT_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field12() {
		return StructureConfig.STRUCTURE_CONFIG.IMG_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Double> field13() {
		return StructureConfig.STRUCTURE_CONFIG.IMG_VERTICAL_PIXEL_OFFSET;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Double> field14() {
		return StructureConfig.STRUCTURE_CONFIG.IMG_HORIZONTAL_PIXEL_OFFSET;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field15() {
		return StructureConfig.STRUCTURE_CONFIG.DESCRIPTION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field16() {
		return StructureConfig.STRUCTURE_CONFIG.SHORT_DESCRIPTION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field17() {
		return StructureConfig.STRUCTURE_CONFIG.SHADOW_IMG_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Double> field18() {
		return StructureConfig.STRUCTURE_CONFIG.SHADOW_VERTICAL_OFFSET;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Double> field19() {
		return StructureConfig.STRUCTURE_CONFIG.SHADOW_HORIZONTAL_OFFSET;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Double> field20() {
		return StructureConfig.STRUCTURE_CONFIG.SHADOW_SCALE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field21() {
		return StructureConfig.STRUCTURE_CONFIG.EXP_REWARD;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field22() {
		return StructureConfig.STRUCTURE_CONFIG.STRENGTH;
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
	public UByte value3() {
		return getLevel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value4() {
		return getStructType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value5() {
		return getBuildResourceType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UInteger value6() {
		return getBuildCost();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UInteger value7() {
		return getMinutesToBuild();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UInteger value8() {
		return getWidth();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UInteger value9() {
		return getHeight();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UInteger value10() {
		return getPredecessorStructId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UInteger value11() {
		return getSuccessorStructId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value12() {
		return getImgName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double value13() {
		return getImgVerticalPixelOffset();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double value14() {
		return getImgHorizontalPixelOffset();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value15() {
		return getDescription();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value16() {
		return getShortDescription();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value17() {
		return getShadowImgName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double value18() {
		return getShadowVerticalOffset();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double value19() {
		return getShadowHorizontalOffset();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double value20() {
		return getShadowScale();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value21() {
		return getExpReward();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value22() {
		return getStrength();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureConfigRecord value1(UInteger value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureConfigRecord value2(String value) {
		setName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureConfigRecord value3(UByte value) {
		setLevel(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureConfigRecord value4(String value) {
		setStructType(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureConfigRecord value5(String value) {
		setBuildResourceType(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureConfigRecord value6(UInteger value) {
		setBuildCost(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureConfigRecord value7(UInteger value) {
		setMinutesToBuild(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureConfigRecord value8(UInteger value) {
		setWidth(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureConfigRecord value9(UInteger value) {
		setHeight(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureConfigRecord value10(UInteger value) {
		setPredecessorStructId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureConfigRecord value11(UInteger value) {
		setSuccessorStructId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureConfigRecord value12(String value) {
		setImgName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureConfigRecord value13(Double value) {
		setImgVerticalPixelOffset(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureConfigRecord value14(Double value) {
		setImgHorizontalPixelOffset(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureConfigRecord value15(String value) {
		setDescription(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureConfigRecord value16(String value) {
		setShortDescription(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureConfigRecord value17(String value) {
		setShadowImgName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureConfigRecord value18(Double value) {
		setShadowVerticalOffset(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureConfigRecord value19(Double value) {
		setShadowHorizontalOffset(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureConfigRecord value20(Double value) {
		setShadowScale(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureConfigRecord value21(Integer value) {
		setExpReward(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureConfigRecord value22(Integer value) {
		setStrength(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureConfigRecord values(UInteger value1, String value2, UByte value3, String value4, String value5, UInteger value6, UInteger value7, UInteger value8, UInteger value9, UInteger value10, UInteger value11, String value12, Double value13, Double value14, String value15, String value16, String value17, Double value18, Double value19, Double value20, Integer value21, Integer value22) {
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
		value12(value12);
		value13(value13);
		value14(value14);
		value15(value15);
		value16(value16);
		value17(value17);
		value18(value18);
		value19(value19);
		value20(value20);
		value21(value21);
		value22(value22);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IStructureConfig from) {
		setId(from.getId());
		setName(from.getName());
		setLevel(from.getLevel());
		setStructType(from.getStructType());
		setBuildResourceType(from.getBuildResourceType());
		setBuildCost(from.getBuildCost());
		setMinutesToBuild(from.getMinutesToBuild());
		setWidth(from.getWidth());
		setHeight(from.getHeight());
		setPredecessorStructId(from.getPredecessorStructId());
		setSuccessorStructId(from.getSuccessorStructId());
		setImgName(from.getImgName());
		setImgVerticalPixelOffset(from.getImgVerticalPixelOffset());
		setImgHorizontalPixelOffset(from.getImgHorizontalPixelOffset());
		setDescription(from.getDescription());
		setShortDescription(from.getShortDescription());
		setShadowImgName(from.getShadowImgName());
		setShadowVerticalOffset(from.getShadowVerticalOffset());
		setShadowHorizontalOffset(from.getShadowHorizontalOffset());
		setShadowScale(from.getShadowScale());
		setExpReward(from.getExpReward());
		setStrength(from.getStrength());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IStructureConfig> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached StructureConfigRecord
	 */
	public StructureConfigRecord() {
		super(StructureConfig.STRUCTURE_CONFIG);
	}

	/**
	 * Create a detached, initialised StructureConfigRecord
	 */
	public StructureConfigRecord(UInteger id, String name, UByte level, String structType, String buildResourceType, UInteger buildCost, UInteger minutesToBuild, UInteger width, UInteger height, UInteger predecessorStructId, UInteger successorStructId, String imgName, Double imgVerticalPixelOffset, Double imgHorizontalPixelOffset, String description, String shortDescription, String shadowImgName, Double shadowVerticalOffset, Double shadowHorizontalOffset, Double shadowScale, Integer expReward, Integer strength) {
		super(StructureConfig.STRUCTURE_CONFIG);

		setValue(0, id);
		setValue(1, name);
		setValue(2, level);
		setValue(3, structType);
		setValue(4, buildResourceType);
		setValue(5, buildCost);
		setValue(6, minutesToBuild);
		setValue(7, width);
		setValue(8, height);
		setValue(9, predecessorStructId);
		setValue(10, successorStructId);
		setValue(11, imgName);
		setValue(12, imgVerticalPixelOffset);
		setValue(13, imgHorizontalPixelOffset);
		setValue(14, description);
		setValue(15, shortDescription);
		setValue(16, shadowImgName);
		setValue(17, shadowVerticalOffset);
		setValue(18, shadowHorizontalOffset);
		setValue(19, shadowScale);
		setValue(20, expReward);
		setValue(21, strength);
	}
}
