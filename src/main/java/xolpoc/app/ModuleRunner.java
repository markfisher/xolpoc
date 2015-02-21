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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.xd.dirt.integration.bus.MessageBus;
import org.springframework.xd.dirt.module.ModuleDeployer;
import org.springframework.xd.dirt.module.ModuleRegistry;
import org.springframework.xd.module.ModuleDefinition;
import org.springframework.xd.module.ModuleDeploymentProperties;
import org.springframework.xd.module.ModuleDescriptor;
import org.springframework.xd.module.ModuleType;
import org.springframework.xd.module.core.Module;
import org.springframework.xd.module.core.Plugin;

import xolpoc.config.DeployerConfiguration;
import xolpoc.config.EmptyConfiguration;
import xolpoc.plugins.StreamPlugin;

/**
 * Main method for running a single Module as a self-contained application.
 *
 * @author Mark Fisher
 */
@SpringBootApplication
@ImportResource({"classpath*:/META-INF/spring-xd/bus/*.xml"})
public class ModuleRunner {

	@Autowired
	private MessageBus messageBus;

	public static void main(String[] args) throws InterruptedException {
		System.setProperty("xd.config.home", "META-INF");
		ConfigurableApplicationContext context = new SpringApplicationBuilder()
				.sources(EmptyConfiguration.class) // this hierarchical depth is expected
				.child(EmptyConfiguration.class) // so these 2 levels satisfy an assertion (temporary)
				.child(ModuleRunner.class)
				.child(DeployerConfiguration.class)
				.run(args);

		String streamName = System.getProperty("stream.name", "test");
		String moduleName = System.getProperty("module.name", "time");
		ModuleType moduleType = ModuleType.valueOf(System.getProperty("module.type", "source"));

		ModuleRegistry registry = context.getBean(ModuleRegistry.class);
		ModuleDefinition definition = registry.findDefinition(moduleName, moduleType);
		ModuleDescriptor descriptor = new ModuleDescriptor.Builder()
				.setModuleDefinition(definition)
				.setModuleName(moduleName)
				.setType(moduleType)
				.setGroup(streamName)
				.setIndex((moduleType == ModuleType.source) ? 0 : 1)
				.build();
		ModuleDeployer deployer = context.getBean(ModuleDeployer.class);
		ModuleDeploymentProperties deploymentProperties = new ModuleDeploymentProperties();
		Module module = deployer.createModule(descriptor, deploymentProperties);
		deployer.deploy(module, descriptor);
	}

	@Bean
	public Plugin streamPlugin() {
		return new StreamPlugin(messageBus);
	}

}
