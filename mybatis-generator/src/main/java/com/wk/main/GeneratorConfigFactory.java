package com.wk.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;

import com.wk.common.ConfigConsts;
import com.wk.common.YamlParser;
import com.wk.common.YamlParser.ParseCase;

public class GeneratorConfigFactory {
	/**
	 * 获取默认配置信息
	 * 
	 * @return 配置信息
	 */
	public static Properties getDefaultConfigValue() {
		try {
			Properties properties = new Properties();
			properties.load(GeneratorConfigFactory.class.getClassLoader()
					.getResourceAsStream(ConfigConsts.generatorConfigProperties));
			return properties;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取配置
	 * 
	 * @param file 配置文件
	 * @return 配置文件
	 */
	public static Properties getConfigValue(File file) {
		final Properties config = new Properties();
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (file.getName().endsWith("properties")) {
			try {
				config.load(fileInputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (file.getName().endsWith("yml") || file.getName().endsWith("yaml")) {
			Properties configYaml = YamlParser.parse2Properties(file, ParseCase.LOWER);
			return configYaml;
		}
		// 转小写,便于取值容错
		Properties pros = new Properties();
		config.keySet().forEach(key -> {
			Object value = config.get(key);
			pros.put(key.toString().toLowerCase(), value);
		});
		return pros;
	}

	/**
	 * @param config
	 * @return
	 */
	public static Configuration getConfiguration(Properties config) {
		List<String> warnings = new ArrayList<String>();
		File configXMLFile = new File(
				GeneratorConfigFactory.class.getClassLoader().getResource(ConfigConsts.generatorConfigXML).getPath());

		Properties configTemp = new Properties();
		if (config != null) {
			configTemp = config;
		} else {
			configTemp = getDefaultConfigValue();
		}
		ConfigurationParser cp = new ConfigurationParser(configTemp, warnings);
		try {
			return cp.parseConfiguration(configXMLFile);
		} catch (IOException | XMLParserException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static DefaultShellCallback getDefaultShellCallback(boolean overwrite) {
		DefaultShellCallback dsc = new DefaultShellCallback(overwrite);
		return dsc;
	}
}
