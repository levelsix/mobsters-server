package com.lvl6.utils;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.codahale.metrics.MetricRegistry;
import com.lvl6.server.metrics.Metrics;

@Component
public class MetricsUtil {
	@Resource(name="metrics")
	public void setMetricsRegistry(MetricRegistry reg) {
		Metrics.setRegistry(reg);
	}
}
