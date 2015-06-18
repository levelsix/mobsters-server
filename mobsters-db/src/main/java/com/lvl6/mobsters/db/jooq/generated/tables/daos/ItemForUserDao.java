/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.ItemForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.ItemForUserPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ItemForUserRecord;

import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.Record2;
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
public class ItemForUserDao extends DAOImpl<ItemForUserRecord, ItemForUserPojo, Record2<String, Integer>> {

	/**
	 * Create a new ItemForUserDao without any configuration
	 */
	public ItemForUserDao() {
		super(ItemForUser.ITEM_FOR_USER, ItemForUserPojo.class);
	}

	/**
	 * Create a new ItemForUserDao with an attached configuration
	 */
	public ItemForUserDao(Configuration configuration) {
		super(ItemForUser.ITEM_FOR_USER, ItemForUserPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Record2<String, Integer> getId(ItemForUserPojo object) {
		return compositeKeyRecord(object.getUserId(), object.getItemId());
	}

	/**
	 * Fetch records that have <code>user_id IN (values)</code>
	 */
	public List<ItemForUserPojo> fetchByUserId(String... values) {
		return fetch(ItemForUser.ITEM_FOR_USER.USER_ID, values);
	}

	/**
	 * Fetch records that have <code>item_id IN (values)</code>
	 */
	public List<ItemForUserPojo> fetchByItemId(Integer... values) {
		return fetch(ItemForUser.ITEM_FOR_USER.ITEM_ID, values);
	}

	/**
	 * Fetch records that have <code>quantity IN (values)</code>
	 */
	public List<ItemForUserPojo> fetchByQuantity(Integer... values) {
		return fetch(ItemForUser.ITEM_FOR_USER.QUANTITY, values);
	}
}
