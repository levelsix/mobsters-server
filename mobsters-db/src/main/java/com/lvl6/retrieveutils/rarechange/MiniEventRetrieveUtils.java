package com.lvl6.retrieveutils.rarechange;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;
import com.lvl6.info.MiniEvent;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;
import com.lvl6.utils.TimeUtils;
import com.lvl6.mobsters.db.jooq.generated.tables.daos.MiniEventConfigDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.MiniEventConfig;

@Component
public class MiniEventRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(MiniEventRetrieveUtils.class);

//	private static final String TABLE_NAME = DBConstants.TABLE_MINI_EVENT_CONFIG;

	private Map<Integer, MiniEventConfig> idToMiniEventConfig;

	@Autowired
	protected MiniEventConfigDao miniEventConfigDao;

	public Map<Integer, MiniEventConfig> getAllIdsToMiniEvents() {
		if (null == idToMiniEventConfig) {
			reload();
		}

		return idToMiniEventConfig;
	}

	public MiniEventConfig getMiniEventById(int id) {
		if (null == idToMiniEventConfig) {
			reload();
		}
		MiniEventConfig ep = idToMiniEventConfig.get(id);
		if (null == ep) {
			log.error("No MiniEventConfig for id={}", id);
		}
		return ep;
	}

	public void reload() {
		setStaticIdsToMiniEventConfigs();
	}

	private void setStaticIdsToMiniEventConfigs() {
		log.debug("setting static map of id to MiniEventConfig");

		Map<Integer, MiniEventConfig> idToMiniEventConfigTemp = new HashMap<Integer, MiniEventConfig>();
		try {
			for (MiniEventConfig mec : miniEventConfigDao.findAll()) {
				idToMiniEventConfigTemp.put(
						mec.getId(), mec);
			}
		} catch (Exception e) {
			log.error("could not setStaticIdsToMiniEventConfigs()", e);
		}
		idToMiniEventConfig = idToMiniEventConfigTemp;
	}

}
