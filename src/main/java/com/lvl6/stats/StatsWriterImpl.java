package com.lvl6.stats;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.influxdb.dto.Serie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.codahale.metrics.MetricRegistry;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.ILock;
import com.lvl6.metrics.MetricsReporter;
import com.lvl6.retrieveutils.StatisticsRetrieveUtil;
import com.lvl6.ui.admin.components.ApplicationStats;
import com.lvl6.utils.ApplicationUtils;


@Component
public class StatsWriterImpl implements StatsWriter {
	
	private static Logger log = LoggerFactory.getLogger(StatsWriterImpl.class);
	
	@Autowired
	protected StatisticsRetrieveUtil statsUtil;
	
	@Autowired
	protected ApplicationUtils appUtils;
	
	@Autowired
	protected HazelcastInstance hazel;
	
	@Autowired
	protected MetricsReporter metricsReporter;


	protected IAtomicLong lastUpdate;
	
	protected String businessMetricsSeriesBase = "";
	
	@PostConstruct
	protected void setup() {
		lastUpdate = hazel.getAtomicLong("businessMetricsLastUpdateTime");
		businessMetricsSeriesBase = metricsReporter.getEnvironment()+".game.";
	}
	
	
	//distributed scheduler hack -- should probably fix at some point
	protected void saveStats() {
		log.info("Saving game stats");
		if(lastUpdate.get() < System.currentTimeMillis() + 10000l) {
			String key = "businessstatsLock";
			ILock lock = hazel.getLock(key);
			boolean gotLock = false;
			try {
				if(lock.tryLock(100, TimeUnit.MILLISECONDS)) {
					gotLock = true;
					lastUpdate.set(System.currentTimeMillis());
					stats();
				}
			} catch (Exception e) {
				log.debug("Another node is already saving stats", e);
			}finally {
				if(gotLock) lock.forceUnlock();
			}
		}

	}


	@Scheduled(cron="0/10 * * * * ?")
	public void scheduledStats() {
		saveStats();
	}


	
	@SuppressWarnings("unchecked")
	protected void stats() {
		ApplicationStats stats = appUtils.getStats();
		try {
			for(String stat : stats.values.keySet()) {
				try {
					Serie serie = new Serie.Builder(businessMetricsSeriesBase+stat)
			        .columns("value")
			        .values(stats.values.get(stat))
			        .build();			
					metricsReporter.getInfluxdbBusiness().write(metricsReporter.getInfluxdbBusinessName(), TimeUnit.SECONDS, serie);
				}catch(Throwable t) {
					log.error("Error saving stat {}", stat, t);
				}
			}
		} catch (Exception e1) {
			log.error("Error saving stats", e1);
		}
		
	}



	public void setHazel(HazelcastInstance hazel) {
		this.hazel = hazel;
	}

	public void setStatsUtil(StatisticsRetrieveUtil statsUtil) {
		this.statsUtil = statsUtil;
	}
	
	public void setAppUtils(ApplicationUtils appUtils) {
		this.appUtils = appUtils;
	}


	public MetricsReporter getMetricsReporter() {
		return metricsReporter;
	}


	public void setMetricsReporter(MetricsReporter metricsReporter) {
		this.metricsReporter = metricsReporter;
	}



}
