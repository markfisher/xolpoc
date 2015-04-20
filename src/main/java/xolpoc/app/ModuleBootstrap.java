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

package xolpoc.app;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.xd.dirt.integration.bus.MessageBus;
import org.springframework.xd.dirt.module.ModuleDeployer;
import org.springframework.xd.dirt.module.ModuleRegistry;
import org.springframework.xd.module.core.Plugin;

import xolpoc.config.DeployerConfiguration;
import xolpoc.config.EmptyConfiguration;
import xolpoc.core.ModuleRunner;
import xolpoc.plugins.StreamPlugin;

/**
 * Main method for running a single Module as a self-contained application.
 *
 * @author Mark Fisher
 */
@SpringBootApplication
//@ImportResource({"classpath*:/META-INF/spring-xd/bus/*.xml"})
@ImportResource({"classpath*:/META-INF/spring-xd/bus/redis-bus.xml",
	"classpath*:/META-INF/spring-xd/bus/codec.xml"})
public class ModuleBootstrap {

	private static final String OPTION_PROPERTY_PREFIX = "option.";

	@Autowired
	private MessageBus messageBus;

	public static void main(String[] args) throws InterruptedException {
		System.setProperty("xd.config.home", "META-INF");
		ConfigurableApplicationContext context = new SpringApplicationBuilder()
				.sources(EmptyConfiguration.class) // this hierarchical depth is expected
				.child(EmptyConfiguration.class) // so these 2 levels satisfy an assertion (temporary)
				.child(ModuleBootstrap.class)
				.child(DeployerConfiguration.class)
				.run(args);
		ModuleRunner runner = new ModuleRunner(context.getBean(ModuleRegistry.class), context.getBean(ModuleDeployer.class));
		String moduleProperty = System.getProperty("module", "ticktock.source.time.0");
		Properties options = new Properties();
		for (String propertyName : System.getProperties().stringPropertyNames()) {
			if (propertyName.startsWith(OPTION_PROPERTY_PREFIX)) {
				String key = propertyName.substring(OPTION_PROPERTY_PREFIX.length());
				options.setProperty(key, System.getProperty(OPTION_PROPERTY_PREFIX + key));
			}
		}
		runner.run(moduleProperty, options);
	}

	@Bean
	public Plugin streamPlugin() {
		return new StreamPlugin(messageBus);
	}

}
