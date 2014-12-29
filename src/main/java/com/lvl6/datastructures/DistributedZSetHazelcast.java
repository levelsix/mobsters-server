package com.lvl6.datastructures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;

/**
 * @author kelly
 * This is a distributed version of SortedSet with ranking (Redis ZSet)
 * Adds and removes are async / eventually consistent. Reads immediately after an add/remove may not reflect the change.
 * This is necessary to maintain consistency and read performance.
 */
public class DistributedZSetHazelcast implements DistributedZSet {

	
	private static final Logger log = LoggerFactory.getLogger(DistributedZSetHazelcast.class);
	
	protected HazelcastInstance hz;
	protected String name;
	protected IList<Long> ranks;
	protected MultiMap<Long, String> keysByScore;
	protected IMap<String, Long> scoresByKey;
	protected ILock pendingChangesLock;
	protected IList<String> pendingQueue;
	
	/**
	 * @param name Used to identify the hazelcast lists and maps
	 * @param hz The hazelcast instance to use
	 */
	public DistributedZSetHazelcast( String name, HazelcastInstance hz) {
		super();
		this.hz = hz;
		this.name = name;
		if(name == null || name.equals("")) {
			throw new RuntimeException("Must provide a name for this set");
		}
		if(hz == null ){
			throw new RuntimeException("Must provide a hazelcast instance for this set");
		}
		createHazelcastItems();
	}


	@Override
	public void add(String key, Long score) {
		if(key == null || key.equals("") || score == null ) {
			return;
		}
		pendingQueue.add("add:"+key+":"+score);
		processPending();
	}

	


	@Override
	public void replaceIfHigher(String key, Long score) {
		if(key == null || key.equals("") || score == null ) {
			return;
		}
		pendingQueue.add("replaceIfHigher:"+key+":"+score);
		processPending();
	}


	@Override
	public void increment(String key, Long increment) {
		if(key == null || key.equals("") || increment == null ) {
			return;
		}
		pendingQueue.add("increment:"+key+":"+increment);
		processPending();
	}
	

	@Override
	public Integer size() {
		return scoresByKey.size();
	}

	@Override
	public Integer count(Long minScore, Long maxScore) {
		int start = findIndex(minScore);
		int end = findIndex(maxScore);
		return end-start;
	}

	
	@Override
	public ZSetMember get(String key) {
		Long score = scoresByKey.get(key);
		if(score != null) {
			Integer rank = getRank(score);
			ZSetMember zSetMember = new ZSetMember(key, score, rank);
			log.trace("get: {}", zSetMember);
			return zSetMember;
		}
		log.trace("Score not found for key: {}", key);
		return new ZSetMember(key, -1l, -1);
	}

	
	@Override
	public List<ZSetMember> get(String... keys) {
		List<ZSetMember> m = new ArrayList<ZSetMember>();
		for(String key : keys) {
			m.add(get(key));
		}
		return null;
	}


	

	
	@Override
	public List<ZSetMember> range(Integer minRank, Integer maxRank) {
		log.trace("Getting range");
		if(minRank > maxRank) {
			minRank = minRank+maxRank;
			maxRank = minRank - maxRank;
			minRank = minRank - maxRank;
		}
		if(minRank >= ranks.size()) {
			return new ArrayList<ZSetMember>();
		}
		if(maxRank >= ranks.size()) {
			maxRank = ranks.size() - 1;
		}
		List<Long> scores = ranks.subList(minRank, maxRank);
		log.trace(" scores: {}",scores);
		List<ZSetMember> members = new ArrayList<ZSetMember>();
		log.trace(" members: ", members);
		int rank = minRank;
		for(Long score : scores) {
			if(score != null && keysByScore.containsKey(score)) {
				Collection<String> keys = keysByScore.get(score);
				for(String key : keys) {
					members.add(new ZSetMember(key, score, rank));
				}
			}
			rank++;
		}
		return members;
	}

	@Override
	public void remove(String... keys) {
		for(String key : keys) {
			pendingQueue.add("remove:"+key);
		}
		processPending();
	}
	
	
	@Override
	public void remove(String key) {
		pendingQueue.add("remove:"+key);
		processPending();
	}

	
	
	@Override
	public List<ZSetMember> rangeByScore(Long minScore, Long maxScore) {
		if(minScore > maxScore) {
			minScore = minScore+maxScore;
			maxScore = minScore - maxScore;
			minScore = minScore - maxScore;
		}
		int start = findIndex(minScore);
		int end = findIndex(maxScore);
		return range(start, end);
	}

	
	@Override
	public boolean hasPendingUpdates() {
		return !pendingQueue.isEmpty();
	}

	
	protected void processPending() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(hasPendingUpdates()) {
					boolean gotLock = false;
					try {
						if(pendingChangesLock.tryLock()) {
							gotLock = true;
							while(hasPendingUpdates()) {
								processPendingItems();
							}
						}
					}catch(Throwable e) {
						
					}finally {
						if(gotLock) pendingChangesLock.forceUnlock();
					}
				}
			}

		}).start();
		
	}


	protected void processPendingItems() {
		//log.trace("Processing pending");
		String item = pendingQueue.get(0);
		if(item != null) {
			String[] args = item.split(":");
			if(args[0].equals("add")) {
				doAdd(args[1], Long.valueOf(args[2]));
			}
			else if(args[0].equals("remove")) {
				doRemove(args[1]);
			}
			else if(args[0].equals("replaceIfHigher")) {
				doReplaceIfHigher(args[1], Long.valueOf(args[2]));;
			}
			else if(args[0].equals("increment")) {
				doIncrement(args[1], Long.valueOf(args[2]));;
			}
		}
		pendingQueue.remove(0);
	}	
	

	protected void doAdd(String key, Long score) {
		log.trace("Adding key: {} score: {}", key, score);
		if(!ranks.contains(score)) {
			ranks.add(findIndex(score), score);
		}
		keysByScore.put(score, key);
		scoresByKey.put(key, score);
		log.trace("scoresByKey.size: {}",scoresByKey.size());
	}

	
	protected void doReplaceIfHigher(String key, Long score) {
		log.trace("Replacing if higher: {} score: {}", key, score);
		Long scoreByKey = scoresByKey.get(key);
		if(scoreByKey == null || scoreByKey < score) {
			log.trace("  new score higher... replacing");
			doRemove(key);
			doAdd(key, score);
		}
	}
	
	
	protected void doIncrement(String key, Long score) {
		log.trace("Incrementing {} amount: {}", key, score);
		ZSetMember m = get(key);
		if(m.getRank().equals(-1)) {
			doAdd(key, score);
		}else {
			doRemove(key);
			doAdd(key, m.getScore()+score);
		}
	}
	
	
	
	protected void doRemove(String key) {
		log.trace("Removing key: {}", key);
		Long score = scoresByKey.remove(key);
		if(score != null) {
			Collection<String> keysForScore = keysByScore.get(score);
			if(keysForScore.size() < 2) {
				ranks.remove(score);
			}
			keysByScore.remove(score, key);
		}
	}

	
	protected Integer getRank(Long score) {
		int indexOf = ranks.indexOf(score);
		log.trace("Rank for score {} : {}", score, indexOf);
		return indexOf;
	}

	
	protected Integer findIndex(Long score) {
		int index = Collections.binarySearch(ranks, score, Collections.reverseOrder());
		if(index < 0) {
			index = ++index * -1;
		}
		return index;
	}
	

	
	@Override
	public void destroy() {
		ranks.destroy();
		keysByScore.destroy();
		scoresByKey.destroy();
		pendingChangesLock.destroy();
		pendingQueue.destroy();
	}
	
	
	protected void createHazelcastItems() {
		ranks = hz.getList(name+"RanksList");
		keysByScore = hz.getMultiMap(name+"keysByScoreMultiMap");
		scoresByKey = hz.getMap(name+"ScoresByKeyMap");
		pendingChangesLock = hz.getLock(name+"PendingLock");
		pendingQueue = hz.getList(name+"PendingQueue");
	}


	@Override
	public void clear() {
		ranks.clear();
		keysByScore.clear();
		scoresByKey.clear();
		pendingQueue.clear();
		//pendingChangesLock.forceUnlock();
		//while(!scoresByKey.isEmpty()) {};
		
	}

	
	public void traceScoresByKey() {
		for(String key : scoresByKey.keySet()) {
			log.trace(key+"="+scoresByKey.get(key));
		}
	}


	@Override
	public Set<String> getAllIds() {
		return scoresByKey.keySet();
	}




}
