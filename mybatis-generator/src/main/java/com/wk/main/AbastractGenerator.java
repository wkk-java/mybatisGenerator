package com.wk.main;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.internal.DefaultShellCallback;

import com.wk.common.ConfigConsts;
import com.wk.common.ConfigConsts.MessageType;

public abstract class AbastractGenerator {

	private Properties properties;

	// 默认true
	private Boolean overwrite = true;

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public Boolean getOverwrite() {
		return overwrite;
	}

	public void setOverwrite(Boolean overwrite) {
		this.overwrite = overwrite;
	}

	public AbastractGenerator() {
		this.setProperties(GeneratorConfigFactory.getDefaultConfigValue());
	}

	public abstract Map<MessageType, List<String>> generator(String tableName);

	public AbastractGenerator overwriteProperties(Properties properties) {
		this.properties.putAll(properties);
		return this;
	}

	public AbastractGenerator overwriteProperties(String name, String value) {
		this.properties.put(name, value);
		return this;
	}

	public Configuration getConfiguration() {
		String targetProjectPath = getProperties().getProperty(ConfigConsts.projectPath);
		String projectBasePackageJava = getProperties().getProperty(ConfigConsts.projectBasePackageJava);
		String projectBasePackageResource = getProperties().getProperty(ConfigConsts.projectBasePackageResource);

		// 回设根路径
		getProperties().put(ConfigConsts.projectBasePackageJava,
				targetProjectPath + File.separatorChar + projectBasePackageJava);
		getProperties().put(ConfigConsts.projectBasePackageResource,
				targetProjectPath + File.separatorChar + projectBasePackageResource);

		File basePackageJava = new File(targetProjectPath + File.separatorChar + projectBasePackageJava);
		if (!basePackageJava.exists()) {
			basePackageJava.mkdirs();
		}
		File basePackageResource = new File(targetProjectPath + File.separatorChar + projectBasePackageResource);
		if (!basePackageResource.exists()) {
			basePackageResource.mkdirs();
		}

		Configuration cfgDefault = GeneratorConfigFactory.getConfiguration(getProperties());
		return cfgDefault;
	}

	public DefaultShellCallback getDefaultShellCallback(boolean overwrite) {
		DefaultShellCallback callback = GeneratorConfigFactory.getDefaultShellCallback(overwrite);
		return callback;
	}
}
