/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xolpoc.config;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.MetricReaderPublicMetrics;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.boot.actuate.metrics.integration.SpringIntegrationMetricReader;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.monitor.IntegrationMBeanExporter;

/**
 * @author Dave Syer
 *
 */
@Configuration
public class IntegrationMetricsConfiguration {
	
	@Autowired
	private IntegrationMBeanExporter exporter;
	
	@Autowired
	private MetricsEndpoint endpoint;

	private MetricReaderPublicMetrics metrics;
	
	@PostConstruct
	public void init() {
		metrics = new MetricReaderPublicMetrics(new SpringIntegrationMetricReader(exporter));
		endpoint.registerPublicMetrics(metrics);
	}
	
	@PreDestroy
	public void destroy() {
		if (metrics!=null) {
			endpoint.unregisterPublicMetrics(metrics);
		}
	}
	
}