/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.FileDownloadConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.records.FileDownloadConfigRecord;

import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


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
public class FileDownloadConfigDao extends DAOImpl<FileDownloadConfigRecord, com.lvl6.mobsters.db.jooq.generated.tables.pojos.FileDownloadConfig, Integer> {

	/**
	 * Create a new FileDownloadConfigDao without any configuration
	 */
	public FileDownloadConfigDao() {
		super(FileDownloadConfig.FILE_DOWNLOAD_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.FileDownloadConfig.class);
	}

	/**
	 * Create a new FileDownloadConfigDao with an attached configuration
	 */
	public FileDownloadConfigDao(Configuration configuration) {
		super(FileDownloadConfig.FILE_DOWNLOAD_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.FileDownloadConfig.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(com.lvl6.mobsters.db.jooq.generated.tables.pojos.FileDownloadConfig object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.FileDownloadConfig> fetchById(Integer... values) {
		return fetch(FileDownloadConfig.FILE_DOWNLOAD_CONFIG.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public com.lvl6.mobsters.db.jooq.generated.tables.pojos.FileDownloadConfig fetchOneById(Integer value) {
		return fetchOne(FileDownloadConfig.FILE_DOWNLOAD_CONFIG.ID, value);
	}

	/**
	 * Fetch records that have <code>filename IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.FileDownloadConfig> fetchByFilename(String... values) {
		return fetch(FileDownloadConfig.FILE_DOWNLOAD_CONFIG.FILENAME, values);
	}

	/**
	 * Fetch records that have <code>priority IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.FileDownloadConfig> fetchByPriority(Integer... values) {
		return fetch(FileDownloadConfig.FILE_DOWNLOAD_CONFIG.PRIORITY, values);
	}

	/**
	 * Fetch records that have <code>download_only_over_wifi IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.FileDownloadConfig> fetchByDownloadOnlyOverWifi(Boolean... values) {
		return fetch(FileDownloadConfig.FILE_DOWNLOAD_CONFIG.DOWNLOAD_ONLY_OVER_WIFI, values);
	}

	/**
	 * Fetch records that have <code>use_iphone6_prefix IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.FileDownloadConfig> fetchByUseIphone6Prefix(Boolean... values) {
		return fetch(FileDownloadConfig.FILE_DOWNLOAD_CONFIG.USE_IPHONE6_PREFIX, values);
	}
}
