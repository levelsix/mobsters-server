package com.lvl6.retrieveutils.rarechange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.db.jooq.generated.tables.daos.CustomMenuConfigDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.CustomMenuConfigPojo;

@Component
@DependsOn("gameServer")
public class CustomMenuRetrieveUtils {

	private static final Logger log = LoggerFactory
			.getLogger(CustomMenuRetrieveUtils.class);

	@Autowired
	protected CustomMenuConfigDao customMenuDao;

	private static Map<Integer, List<CustomMenuConfigPojo>> idsToCustomMenuConfigs;

//	private static final String TABLE_NAME = DBConstants.TABLE_CUSTOM_MENU_CONFIG;

	public Map<Integer, List<CustomMenuConfigPojo>> getIdsToCustomMenuConfigs() {
		log.debug("retrieving all CustomMenu map");
		if (null == idsToCustomMenuConfigs) {
			setStaticIdsToCustomMenuConfigs();
		}
		return idsToCustomMenuConfigs;
	}

	public List<CustomMenuConfigPojo> getCustomMenuConfigForId(int customMenuId) {
		log.debug("retrieve CustomMenus for id={}", customMenuId);
		if (null == idsToCustomMenuConfigs) {
			setStaticIdsToCustomMenuConfigs();
		}
		return idsToCustomMenuConfigs.get(customMenuId);
	}

	private void setStaticIdsToCustomMenuConfigs() {
		log.debug("setting static map of ids to CustomMenus");

		Map<Integer, List<CustomMenuConfigPojo>> idsToCustomMenuConfigTemp = new HashMap<Integer, List<CustomMenuConfigPojo>>();
		for (CustomMenuConfigPojo cmc : customMenuDao.findAll()) {
			int id = cmc.getCustomMenuId();

			if (!idsToCustomMenuConfigTemp.containsKey(id)) {
				idsToCustomMenuConfigTemp.put(id, new ArrayList<CustomMenuConfigPojo>());
			}

			List<CustomMenuConfigPojo> cms = idsToCustomMenuConfigTemp.get(id);
			cms.add(cmc);
		}
		idsToCustomMenuConfigs = idsToCustomMenuConfigTemp;
	}

	public void reload() {
		setStaticIdsToCustomMenuConfigs();
	}

}
