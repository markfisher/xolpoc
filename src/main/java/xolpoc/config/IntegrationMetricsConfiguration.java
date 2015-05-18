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