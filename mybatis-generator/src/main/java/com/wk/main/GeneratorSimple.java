package com.wk.main;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.exception.InvalidConfigurationException;

import com.wk.common.CamelCaseUtil;
import com.wk.common.ConfigConsts;
import com.wk.common.ConfigConsts.MessageType;

/**
 * date 2019/01/18 概述 ： 生成文件（baseEntity、javaMapper、mapperXml）
 * 
 * @author wk
 *
 */
public class GeneratorSimple extends AbastractGenerator {

	public static void main(String[] args) {
		new GeneratorSimple().generator("wd_car_channel");
	}

	/**
	 * 生成文件
	 * 
	 * @return 消息
	 */
	public Map<MessageType, List<String>> generator(String tableName) {
		Map<MessageType, List<String>> message = new HashMap<ConfigConsts.MessageType, List<String>>();
		List<String> warnings = new ArrayList<String>();
		List<String> infos = new ArrayList<String>();
		// 设置表名
		getProperties().put(ConfigConsts.tableName, tableName);
		getProperties().put(ConfigConsts.domainObjectname, CamelCaseUtil.UnderlineToCamel(tableName));

		try {
			MyBatisGenerator myBatisGenerator = new MyBatisGenerator(getConfiguration(),
					getDefaultShellCallback(getOverwrite()), warnings);
			// 生成baseEntity、javaMapper、mapperXml
			myBatisGenerator.generate(null);
			infos.add("生成完毕!");
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		message.put(MessageType.WARNING, warnings);
		message.put(MessageType.INFO, infos);
		return message;
	}

}