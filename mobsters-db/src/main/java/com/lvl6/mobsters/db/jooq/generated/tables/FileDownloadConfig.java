/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.FileDownloadConfigRecord;

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
public class FileDownloadConfig extends TableImpl<FileDownloadConfigRecord> {

	private static final long serialVersionUID = 79653017;

	/**
	 * The reference instance of <code>mobsters.file_download_config</code>
	 */
	public static final FileDownloadConfig FILE_DOWNLOAD_CONFIG = new FileDownloadConfig();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<FileDownloadConfigRecord> getRecordType() {
		return FileDownloadConfigRecord.class;
	}

	/**
	 * The column <code>mobsters.file_download_config.id</code>.
	 */
	public final TableField<FileDownloadConfigRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mobsters.file_download_config.filename</code>. name of images
	 */
	public final TableField<FileDownloadConfigRecord, String> FILENAME = createField("filename", org.jooq.impl.SQLDataType.VARCHAR.length(100).nullable(false), this, "name of images");

	/**
	 * The column <code>mobsters.file_download_config.priority</code>. priority of which to download first
	 */
	public final TableField<FileDownloadConfigRecord, Integer> PRIORITY = createField("priority", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "priority of which to download first");

	/**
	 * The column <code>mobsters.file_download_config.download_only_over_wifi</code>. only download if connect to wifi
	 */
	public final TableField<FileDownloadConfigRecord, Boolean> DOWNLOAD_ONLY_OVER_WIFI = createField("download_only_over_wifi", org.jooq.impl.SQLDataType.BIT.nullable(false), this, "only download if connect to wifi");

	/**
	 * The column <code>mobsters.file_download_config.use_iphone6_prefix</code>. certain case for iPhone 6
	 */
	public final TableField<FileDownloadConfigRecord, Boolean> USE_IPHONE6_PREFIX = createField("use_iphone6_prefix", org.jooq.impl.SQLDataType.BIT.nullable(false), this, "certain case for iPhone 6");

	/**
	 * The column <code>mobsters.file_download_config.use_ipad_suffix</code>.
	 */
	public final TableField<FileDownloadConfigRecord, Boolean> USE_IPAD_SUFFIX = createField("use_ipad_suffix", org.jooq.impl.SQLDataType.BIT.nullable(false), this, "");

	/**
	 * Create a <code>mobsters.file_download_config</code> table reference
	 */
	public FileDownloadConfig() {
		this("file_download_config", null);
	}

	/**
	 * Create an aliased <code>mobsters.file_download_config</code> table reference
	 */
	public FileDownloadConfig(String alias) {
		this(alias, FILE_DOWNLOAD_CONFIG);
	}

	private FileDownloadConfig(String alias, Table<FileDownloadConfigRecord> aliased) {
		this(alias, aliased, null);
	}

	private FileDownloadConfig(String alias, Table<FileDownloadConfigRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<FileDownloadConfigRecord, Integer> getIdentity() {
		return Keys.IDENTITY_FILE_DOWNLOAD_CONFIG;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<FileDownloadConfigRecord> getPrimaryKey() {
		return Keys.KEY_FILE_DOWNLOAD_CONFIG_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<FileDownloadConfigRecord>> getKeys() {
		return Arrays.<UniqueKey<FileDownloadConfigRecord>>asList(Keys.KEY_FILE_DOWNLOAD_CONFIG_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FileDownloadConfig as(String alias) {
		return new FileDownloadConfig(alias, this);
	}

	/**
	 * Rename this table
	 */
	public FileDownloadConfig rename(String name) {
		return new FileDownloadConfig(name, null);
	}
}
