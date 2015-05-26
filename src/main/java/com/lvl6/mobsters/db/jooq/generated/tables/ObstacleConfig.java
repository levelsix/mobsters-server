/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ObstacleConfigRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Identity;
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
public class ObstacleConfig extends TableImpl<ObstacleConfigRecord> {

	private static final long serialVersionUID = -1460972449;

	/**
	 * The reference instance of <code>mobsters.obstacle_config</code>
	 */
	public static final ObstacleConfig OBSTACLE_CONFIG = new ObstacleConfig();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<ObstacleConfigRecord> getRecordType() {
		return ObstacleConfigRecord.class;
	}

	/**
	 * The column <code>mobsters.obstacle_config.id</code>.
	 */
	public final TableField<ObstacleConfigRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mobsters.obstacle_config.name</code>.
	 */
	public final TableField<ObstacleConfigRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR.length(45), this, "");

	/**
	 * The column <code>mobsters.obstacle_config.removal_cost_type</code>.
	 */
	public final TableField<ObstacleConfigRecord, String> REMOVAL_COST_TYPE = createField("removal_cost_type", org.jooq.impl.SQLDataType.VARCHAR.length(45), this, "");

	/**
	 * The column <code>mobsters.obstacle_config.cost</code>.
	 */
	public final TableField<ObstacleConfigRecord, Integer> COST = createField("cost", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.obstacle_config.seconds_to_remove</code>.
	 */
	public final TableField<ObstacleConfigRecord, Integer> SECONDS_TO_REMOVE = createField("seconds_to_remove", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.obstacle_config.width</code>.
	 */
	public final TableField<ObstacleConfigRecord, Byte> WIDTH = createField("width", org.jooq.impl.SQLDataType.TINYINT, this, "");

	/**
	 * The column <code>mobsters.obstacle_config.height</code>.
	 */
	public final TableField<ObstacleConfigRecord, Byte> HEIGHT = createField("height", org.jooq.impl.SQLDataType.TINYINT, this, "");

	/**
	 * The column <code>mobsters.obstacle_config.img_name</code>.
	 */
	public final TableField<ObstacleConfigRecord, String> IMG_NAME = createField("img_name", org.jooq.impl.SQLDataType.VARCHAR.length(45), this, "");

	/**
	 * The column <code>mobsters.obstacle_config.img_vertical_pixel_offset</code>.
	 */
	public final TableField<ObstacleConfigRecord, Double> IMG_VERTICAL_PIXEL_OFFSET = createField("img_vertical_pixel_offset", org.jooq.impl.SQLDataType.FLOAT, this, "");

	/**
	 * The column <code>mobsters.obstacle_config.description</code>.
	 */
	public final TableField<ObstacleConfigRecord, String> DESCRIPTION = createField("description", org.jooq.impl.SQLDataType.VARCHAR.length(45), this, "");

	/**
	 * The column <code>mobsters.obstacle_config.chance_to_appear</code>.
	 */
	public final TableField<ObstacleConfigRecord, Double> CHANCE_TO_APPEAR = createField("chance_to_appear", org.jooq.impl.SQLDataType.FLOAT, this, "");

	/**
	 * The column <code>mobsters.obstacle_config.shadow_img_name</code>.
	 */
	public final TableField<ObstacleConfigRecord, String> SHADOW_IMG_NAME = createField("shadow_img_name", org.jooq.impl.SQLDataType.VARCHAR.length(45), this, "");

	/**
	 * The column <code>mobsters.obstacle_config.shadow_vertical_offset</code>.
	 */
	public final TableField<ObstacleConfigRecord, Double> SHADOW_VERTICAL_OFFSET = createField("shadow_vertical_offset", org.jooq.impl.SQLDataType.FLOAT, this, "");

	/**
	 * The column <code>mobsters.obstacle_config.shadow_horizontal_offset</code>.
	 */
	public final TableField<ObstacleConfigRecord, Double> SHADOW_HORIZONTAL_OFFSET = createField("shadow_horizontal_offset", org.jooq.impl.SQLDataType.FLOAT, this, "");

	/**
	 * Create a <code>mobsters.obstacle_config</code> table reference
	 */
	public ObstacleConfig() {
		this("obstacle_config", null);
	}

	/**
	 * Create an aliased <code>mobsters.obstacle_config</code> table reference
	 */
	public ObstacleConfig(String alias) {
		this(alias, OBSTACLE_CONFIG);
	}

	private ObstacleConfig(String alias, Table<ObstacleConfigRecord> aliased) {
		this(alias, aliased, null);
	}

	private ObstacleConfig(String alias, Table<ObstacleConfigRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<ObstacleConfigRecord, Integer> getIdentity() {
		return Keys.IDENTITY_OBSTACLE_CONFIG;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<ObstacleConfigRecord> getPrimaryKey() {
		return Keys.KEY_OBSTACLE_CONFIG_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<ObstacleConfigRecord>> getKeys() {
		return Arrays.<UniqueKey<ObstacleConfigRecord>>asList(Keys.KEY_OBSTACLE_CONFIG_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ObstacleConfig as(String alias) {
		return new ObstacleConfig(alias, this);
	}

	/**
	 * Rename this table
	 */
	public ObstacleConfig rename(String name) {
		return new ObstacleConfig(name, null);
	}
}
