package com.wk.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.yaml.snakeyaml.Yaml;

/**
 * yaml转换器
 * 
 * @author wk
 *
 */
public class YamlParser {

	/**
	 * 转换类型
	 * 
	 * @author wk
	 *
	 */
	public enum ParseCase {
		LOWER, UPPER, NOCASE
	}

	public static void main(String[] args) throws FileNotFoundException {
		Map<String, String> allMap = parse2Map(new File("d://config.yaml"), ParseCase.LOWER);
		System.out.println(allMap);
	}

	/**
	 * 转换为map配置
	 * 
	 * @param inputStream 输入流
	 * @param parseCase   转换类型
	 * @return 配置map
	 */
	public static Map<String, String> parse2Map(InputStream inputStream, ParseCase parseCase) {
		Map<String, String> allMap = new HashMap<String, String>();
		Yaml yaml = new Yaml();
		Map<String, Object> yamlMap = yaml.loadAs(inputStream, Map.class);
		Iterator<String> iterator = yamlMap.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			Map map = (Map) yamlMap.get(key);
			iteratorYml(allMap, map, key, parseCase);
		}
		return allMap;
	}

	/**
	 * 转换成map
	 * 
	 * @param file      yaml文件
	 * @param parseCase 转换大小写类型
	 * @return 配置map
	 */
	public static Map<String, String> parse2Map(File file, ParseCase parseCase) {
		try {
			InputStream fileInputStream = new FileInputStream(file);
			return parse2Map(fileInputStream, parseCase);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 转换成配置对象
	 * 
	 * @param inputStream 文件
	 * @param parseCase   转换类型
	 * @return 配置对象
	 */
	public static Properties parse2Properties(InputStream inputStream, ParseCase parseCase) {
		Map<String, String> configMap = parse2Map(inputStream, parseCase);
		Properties properties = new Properties();
		properties.putAll(configMap);
		return properties;
	}

	/**
	 * 转换成配置对象
	 * 
	 * @param file      文件
	 * @param parseCase 转换类型
	 * @return 配置对象
	 */
	public static Properties parse2Properties(File file, ParseCase parseCase) {
		Map<String, String> configMap = parse2Map(file, parseCase);
		Properties properties = new Properties();
		properties.putAll(configMap);
		return properties;
	}

	/**
	 * 获取转换字符串
	 * 
	 * @param value     字符串
	 * @param parseCase 转换大小写类型
	 * @return 获取转换字符串
	 */
	private static String getValue(String value, ParseCase parseCase) {
		if (parseCase == null || parseCase.equals(ParseCase.NOCASE)) {
			return value;
		} else if (parseCase.equals(ParseCase.UPPER)) {
			return value.toUpperCase();
		} else if (parseCase.equals(ParseCase.LOWER)) {
			return value.toLowerCase();
		}
		return value;
	}

	/**
	 * 递归转换map设置到allMap
	 * 
	 * @param allMap    返回数据集
	 * @param map       map
	 * @param key       key
	 * @param parseCase 转换类型
	 */
	private static void iteratorYml(Map<String, String> allMap, Map<String, Object> map, String key,
			ParseCase parseCase) {
		Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
		// key转换
		key = getValue(key, parseCase);
		while (iterator.hasNext()) {
			Map.Entry<String, Object> entry = iterator.next();
			// key转换
			String key2 = getValue(entry.getKey(), parseCase);
			Object value = entry.getValue();
			if (value instanceof Map) {
				if (key == null) {
					iteratorYml(allMap, (Map<String, Object>) value, key2.toString(), parseCase);
				} else {
					iteratorYml(allMap, (Map<String, Object>) value, key + "." + key2.toString(), parseCase);
				}
			}
			if (value instanceof String) {
				if (key == null) {
					allMap.put(key2.toString(), value.toString());
				}
				if (key != null) {
					allMap.put(key + "." + key2.toString(), value.toString());
				}
			}
		}

	}

}
