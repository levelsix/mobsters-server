package com.lvl6.metrics;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import metrics_influxdb.Influxdb;
import metrics_influxdb.InfluxdbReporter;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;



//@Component
public class MetricsReporter {
	private static final Logger log = LoggerFactory.getLogger(MetricsReporter.class);
	
	
	protected MetricRegistry registry;
	protected MetricRegistry business_registry;
	
	protected String environment;


	protected String hostname;
	protected String influxdbAddress;
	protected String influxdbName;
	protected String influxdbBusinessName;
	protected String influxdbUser;
	protected String influxdbPassword;
	
	protected  InfluxDB influxdbBusiness;
	public InfluxDB getInfluxdbBusiness() {
		return influxdbBusiness;
	}
	
	@PostConstruct
	public void setup()  {
		try {
			hostname = InetAddress.getLocalHost().getHostName();
			Object[] args = {influxdbAddress, 8086, influxdbName, influxdbUser, influxdbPassword};
//			log.info("Setting up InfluxDB connection address:{} port:{} dbname: {} user: {} pass: {}", args);
		    final Influxdb influxdb = new Influxdb(influxdbAddress, 8086, influxdbName, influxdbUser, influxdbPassword); // http transport
		    final InfluxdbReporter reporter = InfluxdbReporter
		            .forRegistry(registry)
		            .prefixedWith(environment+"."+hostname)
		            .convertRatesTo(TimeUnit.SECONDS)
		            .convertDurationsTo(TimeUnit.MILLISECONDS)
		            .filter(MetricFilter.ALL)
		            .skipIdleMetrics(false) // Only report metrics that have changed.
		            .build(influxdb);
		    reporter.start(10, TimeUnit.SECONDS);
		    //return reporter;
		}catch(Throwable e) {
			log.error("Error creating InfluxDBReporter", e);
		}
		
	}
	
	
	@PostConstruct
	public void setupBusiness()  {
		try {
			hostname = InetAddress.getLocalHost().getHostName();
			Object[] args = {influxdbAddress, 8086, influxdbBusinessName, influxdbUser, influxdbPassword};
//			log.info("Setting up InfluxDB connection address:{} port:{} dbname: {} user: {} pass: {}", args);
		    final InfluxDB influxDB = InfluxDBFactory.connect("http://"+influxdbAddress+":"+8086, influxdbUser, influxdbPassword);//new Influxdb(influxdbAddress, 8086, influxdbBusinessName, influxdbUser, influxdbPassword); // http transport
		    influxdbBusiness = influxDB;
		}catch(Throwable e) {
			log.error("Error creating InfluxDBReporter", e);
		}
	}
	
	
	
	public void setEnvironment(String environment) {
		this.environment = environment;
	}
	
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	public void setInfluxdbAddress(String influxdbAddress) {
		this.influxdbAddress = influxdbAddress;
	}
	
	public void setInfluxdbName(String influxdbName) {
		this.influxdbName = influxdbName;
	}
	
	public void setInfluxdbUser(String influxdbUser) {
		this.influxdbUser = influxdbUser;
	}

	
	public void setInfluxdbPassword(String influxdbPassword) {
		this.influxdbPassword = influxdbPassword;
	}

	
	public void setRegistry(MetricRegistry registry) {
		this.registry = registry;
	}


	public void setBusiness_registry(MetricRegistry business_registry) {
		this.business_registry = business_registry;
	}


	public void setInfluxdbBusinessName(String influxdbBusinessName) {
		this.influxdbBusinessName = influxdbBusinessName;
	}

	public MetricRegistry getRegistry() {
		return registry;
	}

	public MetricRegistry getBusiness_registry() {
		return business_registry;
	}

	public String getEnvironment() {
		return environment;
	}

	public String getHostname() {
		return hostname;
	}

	public String getInfluxdbAddress() {
		return influxdbAddress;
	}

	public String getInfluxdbName() {
		return influxdbName;
	}

	public String getInfluxdbBusinessName() {
		return influxdbBusinessName;
	}

	public String getInfluxdbUser() {
		return influxdbUser;
	}

	public String getInfluxdbPassword() {
		return influxdbPassword;
	}

	public void setInfluxdbBusiness(InfluxDB influxdbBusiness) {
		this.influxdbBusiness = influxdbBusiness;
	}

}
