/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xolpoc.support;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.xd.dirt.module.ResourceModuleRegistry;
import org.springframework.xd.module.ModuleDefinition;
import org.springframework.xd.module.ModuleDefinitions;
import org.springframework.xd.module.ModuleType;
import org.springframework.xd.module.SimpleModuleDefinition;

/**
 * Workaround that uses getUrl() instead of getFile() due to JAR'ed resources.
 *
 * @author Mark Fisher
 */
public class TempResourceModuleRegistry extends ResourceModuleRegistry {

	public TempResourceModuleRegistry(String root) {
		super(root);
	}

	@Override
	protected void fromResource(Resource resource, List<ModuleDefinition> holder) throws IOException {
		if (!resource.exists()) {
			return;
		}
		File file = new File(resource.getURL().getFile());
		String filename = file.getCanonicalFile().getName();

		boolean isDir = file.isDirectory();
		if (!isDir && !filename.endsWith(ARCHIVE_AS_FILE_EXTENSION)) {
			return;
		}
		String name = isDir ? filename : filename.substring(0, filename.lastIndexOf(ARCHIVE_AS_FILE_EXTENSION));
		String canonicalPath = file.getCanonicalPath();

		String fileSeparator = File.separator;
		int lastSlash = canonicalPath.lastIndexOf(fileSeparator);
		String typeAsString = canonicalPath.substring(canonicalPath.lastIndexOf(fileSeparator, lastSlash - 1) + 1,
				lastSlash);
		ModuleType type = null;
		try {
			type = ModuleType.valueOf(typeAsString);
		}
		catch (IllegalArgumentException e) {
			// Not an actual type name, skip
			return;
		}
		ModuleDefinition found = ModuleDefinitions.simple(name, type,
				"file:" + canonicalPath + (isDir ? fileSeparator : ""));

		if (holder.contains(found)) {
			SimpleModuleDefinition one = (SimpleModuleDefinition) found;
			SimpleModuleDefinition two = (SimpleModuleDefinition) holder.get(holder.indexOf(found));
			throw new IllegalStateException(String.format("Duplicate module definitions for '%s:%s' found at '%s' " +
							"and" +
							" " +
							"'%s'",
					found.getType(), found.getName(), one.getLocation(), two.getLocation()));
		}
		else {
			holder.add(found);
		}
	}


	
}
