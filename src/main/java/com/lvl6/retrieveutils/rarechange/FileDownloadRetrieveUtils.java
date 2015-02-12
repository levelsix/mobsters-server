package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.FileDownload;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component @DependsOn("gameServer") public class FileDownloadRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private static Map<Integer, FileDownload> idsToFileDownloads;

	private static final String TABLE_NAME = DBConstants.TABLE_FILE_DOWNLOAD_CONFIG;

	public static Map<Integer, FileDownload> getIdsToFileDownloads() {
		log.debug("retrieving all filedownload map");
		if (null == idsToFileDownloads) {
			setStaticIdsToFileDownloads();
		}
		return idsToFileDownloads;
	}

	public static FileDownload getFileDownloadForId(int fileDownloadId) {
		log.debug(String.format(
			"retrieve skill data for skill=%s", fileDownloadId));
		if (null == idsToFileDownloads) {
			setStaticIdsToFileDownloads();
		}
		return idsToFileDownloads.get(fileDownloadId);
	}

	private static void setStaticIdsToFileDownloads() {
		log.debug("setting static map of skillIds to skills");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						Map<Integer, FileDownload> idsToFileDownloadTemp =
							new HashMap<Integer, FileDownload>();
						//loop through each row and convert it into a java object
						while(rs.next()) {  
							FileDownload fd = convertRSRowToFileDownload(rs);
							if (fd == null) {
								continue;
							}

							int fileDownloadId = fd.getId();
							idsToFileDownloadTemp.put(fileDownloadId, fd);
						}
						idsToFileDownloads = idsToFileDownloadTemp;

					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}    
			}
		} catch (Exception e) {
			log.error("skill retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public static void reload() {
		setStaticIdsToFileDownloads();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private static FileDownload convertRSRowToFileDownload(ResultSet rs) throws SQLException {
		int id = rs.getInt(DBConstants.FILE_DOWNLOAD_CONFIG__ID);
		String filename = rs.getString(DBConstants.FILE_DOWNLOAD_CONGIG__FILENAME);
		int priority = rs.getInt(DBConstants.FILE_DOWNLOAD_CONGIG__PRIORITY);
		
		FileDownload fd = new FileDownload(id, filename, priority);
		return fd;
	}
	
}
