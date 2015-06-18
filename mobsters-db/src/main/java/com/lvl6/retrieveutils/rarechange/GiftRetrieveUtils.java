package com.lvl6.retrieveutils.rarechange;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.db.jooq.generated.tables.daos.GiftConfigDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftConfigPojo;

@Component
@DependsOn("gameServer")
public class GiftRetrieveUtils {

	private static final Logger log = LoggerFactory
			.getLogger(GiftRetrieveUtils.class);

	@Autowired
	protected GiftConfigDao giftConfigDao;

//	private static final String TABLE_NAME = DBConstants.TABLE_GIFT_CONFIG;
	private static Map<Integer, GiftConfigPojo> idsToGifts;
	private static Map<String, Map<Integer, GiftConfigPojo>> typeToIdsToGifts;

	public GiftConfigPojo getGift(String type, int giftId) {
		Map<Integer, GiftConfigPojo> similarGifts = getGiftType(type);

		if (!similarGifts.containsKey(giftId)) {
			log.warn("no gifts of type={} id={}, gifts={}",
					new Object[] { type, giftId, similarGifts });
			return null;
		}
		return similarGifts.get(giftId);
	}

	public Map<Integer, GiftConfigPojo> getGiftType(String type) {
		if (null == typeToIdsToGifts) {
			setStaticIdsToGifts();
		}

		if (null == typeToIdsToGifts || !typeToIdsToGifts.containsKey(type))
		{
			log.warn("no gifts for type={}.", type);
			return new HashMap<Integer, GiftConfigPojo>();
		}
		return typeToIdsToGifts.get(type);
	}

	public GiftConfigPojo getGift(int giftId) {
		if (null == idsToGifts) {
			setStaticIdsToGifts();
		}

		if (null == idsToGifts || !idsToGifts.containsKey(giftId)) {
			log.warn("no gift for id={}", giftId);
			return null;
		}

		return idsToGifts.get(giftId);
	}

	public Map<Integer, GiftConfigPojo> getIdsToGifts() {
		log.debug("retrieving all Gifts");
		if (null == idsToGifts) {
			setStaticIdsToGifts();
		}
		return idsToGifts;
	}

	private void setStaticIdsToGifts() {
		log.debug("setting static map of giftIds to Gifts");

		Map<Integer, GiftConfigPojo> idsToGiftsTemp = new HashMap<Integer, GiftConfigPojo>();
		Map<String, Map<Integer, GiftConfigPojo>> typeToIdsToGiftsTemp =
				new HashMap<String, Map<Integer, GiftConfigPojo>>();
		try {
			for (GiftConfigPojo gc : giftConfigDao.findAll()) {
				int id = gc.getId();
				idsToGiftsTemp.put(id, gc);

				String type = gc.getGiftType();

				if (!typeToIdsToGiftsTemp.containsKey(type)) {
					typeToIdsToGiftsTemp.put(type, new HashMap<Integer, GiftConfigPojo>());
				}

				Map<Integer, GiftConfigPojo> similarGifts = typeToIdsToGiftsTemp
						.get(type);
				similarGifts.put(id, gc);
			}

		} catch (Exception e) {
			log.error("retrieve all Gifts error.", e);
		}

		idsToGifts = idsToGiftsTemp;
		typeToIdsToGifts = typeToIdsToGiftsTemp;
	}

	public void reload() {
		setStaticIdsToGifts();
	}

}
