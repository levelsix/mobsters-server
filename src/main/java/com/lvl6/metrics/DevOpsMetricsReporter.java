package com.lvl6.metrics;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import metrics_influxdb.Influxdb;
import metrics_influxdb.InfluxdbReporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;



@Component
public class DevOpsMetricsReporter {
	private static final Logger log = LoggerFactory.getLogger(DevOpsMetricsReporter.class);
	
	
	protected MetricRegistry registry;
	protected String environment;


	protected String hostname;
	protected String influxdbAddress;
	protected String influxdbName;
	protected String influxdbUser;
	protected String influxdbPassword;

	
	@PostConstruct
	public void setup()  {
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		    final Influxdb influxdb = new Influxdb(influxdbAddress, 8086, influxdbName, influxdbUser, influxdbPassword); // http transport
		    // = new InfluxDbUdp("127.0.0.1", 1234); // udp transport
		    //influxdb.debugJson = true; // to print json on System.err
		    //influxdb.jsonBuilder = new MyJsonBuildler(); // to use MyJsonBuilder to create json
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

}
