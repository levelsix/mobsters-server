/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.StructureResidenceConfigRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;


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
public class StructureResidenceConfig extends TableImpl<StructureResidenceConfigRecord> {

	private static final long serialVersionUID = 398052702;

	/**
	 * The reference instance of <code>mobsters.structure_residence_config</code>
	 */
	public static final StructureResidenceConfig STRUCTURE_RESIDENCE_CONFIG = new StructureResidenceConfig();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<StructureResidenceConfigRecord> getRecordType() {
		return StructureResidenceConfigRecord.class;
	}

	/**
	 * The column <code>mobsters.structure_residence_config.struct_id</code>.
	 */
	public final TableField<StructureResidenceConfigRecord, Integer> STRUCT_ID = createField("struct_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mobsters.structure_residence_config.num_monster_slots</code>.
	 */
	public final TableField<StructureResidenceConfigRecord, Integer> NUM_MONSTER_SLOTS = createField("num_monster_slots", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>mobsters.structure_residence_config.num_bonus_monster_slots</code>.
	 */
	public final TableField<StructureResidenceConfigRecord, Integer> NUM_BONUS_MONSTER_SLOTS = createField("num_bonus_monster_slots", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>mobsters.structure_residence_config.num_gems_required</code>.
	 */
	public final TableField<StructureResidenceConfigRecord, Integer> NUM_GEMS_REQUIRED = createField("num_gems_required", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>mobsters.structure_residence_config.num_accepeted_fb_invites</code>.
	 */
	public final TableField<StructureResidenceConfigRecord, Integer> NUM_ACCEPETED_FB_INVITES = createField("num_accepeted_fb_invites", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>mobsters.structure_residence_config.occupation_name</code>. This is flavor text, for which position the user invites his friends to do, in the journey for more slots.
	 */
	public final TableField<StructureResidenceConfigRecord, String> OCCUPATION_NAME = createField("occupation_name", org.jooq.impl.SQLDataType.VARCHAR.length(45), this, "This is flavor text, for which position the user invites his friends to do, in the journey for more slots.");

	/**
	 * The column <code>mobsters.structure_residence_config.img_suffix</code>.
	 */
	public final TableField<StructureResidenceConfigRecord, String> IMG_SUFFIX = createField("img_suffix", org.jooq.impl.SQLDataType.VARCHAR.length(45), this, "");

	/**
	 * Create a <code>mobsters.structure_residence_config</code> table reference
	 */
	public StructureResidenceConfig() {
		this("structure_residence_config", null);
	}

	/**
	 * Create an aliased <code>mobsters.structure_residence_config</code> table reference
	 */
	public StructureResidenceConfig(String alias) {
		this(alias, STRUCTURE_RESIDENCE_CONFIG);
	}

	private StructureResidenceConfig(String alias, Table<StructureResidenceConfigRecord> aliased) {
		this(alias, aliased, null);
	}

	private StructureResidenceConfig(String alias, Table<StructureResidenceConfigRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<StructureResidenceConfigRecord> getPrimaryKey() {
		return Keys.KEY_STRUCTURE_RESIDENCE_CONFIG_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<StructureResidenceConfigRecord>> getKeys() {
		return Arrays.<UniqueKey<StructureResidenceConfigRecord>>asList(Keys.KEY_STRUCTURE_RESIDENCE_CONFIG_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureResidenceConfig as(String alias) {
		return new StructureResidenceConfig(alias, this);
	}

	/**
	 * Rename this table
	 */
	public StructureResidenceConfig rename(String name) {
		return new StructureResidenceConfig(name, null);
	}
}
