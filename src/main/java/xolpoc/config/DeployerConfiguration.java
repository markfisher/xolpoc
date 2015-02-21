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

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.xd.dirt.module.ModuleDeployer;
import org.springframework.xd.dirt.module.ResourceModuleRegistry;
import org.springframework.xd.dirt.plugins.job.JobPluginMetadataResolver;
import org.springframework.xd.dirt.plugins.stream.ModuleTypeConversionPluginMetadataResolver;
import org.springframework.xd.module.core.ModuleFactory;
import org.springframework.xd.module.options.DefaultModuleOptionsMetadataResolver;
import org.springframework.xd.module.options.DelegatingModuleOptionsMetadataResolver;
import org.springframework.xd.module.options.EnvironmentAwareModuleOptionsMetadataResolver;
import org.springframework.xd.module.options.ModuleOptionsMetadataResolver;

import xolpoc.support.TempModuleFactory;
import xolpoc.support.TempResourceModuleRegistry;

/**
 * Instantiates the components required for loading and deploying Modules.
 *
 * @author Mark Fisher
 */
@Configuration
@EnableAutoConfiguration
public class DeployerConfiguration {

	private static final String MODULE_HOME = "classpath*:/META-INF/modules";

	@Bean
	public ModuleDeployer moduleDeployer() {
		return new ModuleDeployer(moduleFactory());
	}

	@Bean
	public ResourceModuleRegistry moduleRegistry() {
		return new TempResourceModuleRegistry(MODULE_HOME);
	}

	@Bean
	public ModuleFactory moduleFactory() {
		return new TempModuleFactory(moduleOptionsMetadataResolver());
	}

	@Bean
	public EnvironmentAwareModuleOptionsMetadataResolver moduleOptionsMetadataResolver() {
		List<ModuleOptionsMetadataResolver> delegates = new ArrayList<ModuleOptionsMetadataResolver>();
		delegates.add(defaultModuleOptionsMetadataResolver());
		delegates.add(new ModuleTypeConversionPluginMetadataResolver());
		delegates.add(new JobPluginMetadataResolver());
		DelegatingModuleOptionsMetadataResolver delegatingResolver = new DelegatingModuleOptionsMetadataResolver();
		delegatingResolver.setDelegates(delegates);
		EnvironmentAwareModuleOptionsMetadataResolver resolver = new EnvironmentAwareModuleOptionsMetadataResolver();
		resolver.setDelegate(delegatingResolver);
		return resolver;
	}

	@Bean
	public DefaultModuleOptionsMetadataResolver defaultModuleOptionsMetadataResolver() {
		DefaultModuleOptionsMetadataResolver resolver = new DefaultModuleOptionsMetadataResolver();
		//resolver.setCompositeResolver(moduleOptionsMetadataResolver());
		return resolver;
	}

}
