package com.lvl6.datastructures;

import java.util.List;
import java.util.Set;

/**
 * @author kelly
 *
 */

public interface DistributedZSet {

	/**
	 * @param key
	 * @param score Adds a member to the set or replaces score if it already
	 *            exists
	 */
	public void add(String key, Long score);

	/*
	 * @param key
	 * @param score
	 * Replaces a member in the set if the score is higher than current score
	 */
	public void replaceIfHigher(String key, Long score);

	/**
	 * @param key
	 * @param increment Increments the score for the given key
	 */
	public void increment(String key, Long increment);

	/**
	 * @return Get the number of members in the set
	 */
	public Integer size();

	/**
	 * @param minScore
	 * @param maxScore Count the members in a sorted set with scores within the
	 *            given values
	 */
	public Integer count(Long minScore, Long maxScore);

	/**
	 * @param minIndex
	 * @param maxIndex
	 * @return Returns range of members in sorted set, by index
	 */
	public List<ZSetMember> range(Integer minIndex, Integer maxIndex);

	/**
	 * @param minScore
	 * @param maxScore
	 * @return Returns range of members in sorted set, by score
	 */
	public List<ZSetMember> rangeByScore(Long minScore, Long maxScore);

	/**
	 * @param key
	 * @return Determine the index of a member in a sorted set, with scores
	 *         ordered from high to low
	 */
	public ZSetMember get(String key);

	public List<ZSetMember> get(String... keys);

	public Set<String> getAllIds();

	/**
	 * @param keys Removes members from sorted set
	 */
	public void remove(String... keys);

	/**
	 * @param key Removes member from sorted set
	 */
	public void remove(String key);

	/**
	 * Removes all data from this set
	 */
	public void clear();

	/**
	 * Removes this set and all data inside it from memory
	 */
	public void destroy();

	/**
	 * @return Set is async / eventually consistent. This is useful for unit
	 *         tests.
	 */
	public boolean hasPendingUpdates();

}
