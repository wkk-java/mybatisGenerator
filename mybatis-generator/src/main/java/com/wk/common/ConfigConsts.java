package com.wk.common;

/**
 * 配置常量
 * 
 * @author wk
 *
 */
public final class ConfigConsts {

	/**
	 * 编码
	 */
	public static final String encoding = "UTF-8";
	/**
	 * 生成配置文件
	 */
	public static final String generatorConfigXML = "generatorConfig.xml";
	/**
	 * 具体配置
	 */
	public static final String generatorConfigProperties = "generator.properties";

	/**
	 * mysql模块id
	 */
	public static final String mysqlContextId = "mysqlContext";

	public static final String tableNameKey = "tableName";
	public static final String tableNameDomainKey = "domainObjectName";

	public static String jdbcOracleUrlTemplate = "jdbc:{0}:thin:@127.0.0.1:1521:dbname";
	public static String jdbcMysqlUrlTemplate = "jdbc:{0}://101.37.78.253:3306/bk_product?characterEncoding=UTF-8";

	public static String jdbcDriverClassMysqlDefaultValue = "com.mysql.cj.jdbc.Driver";
	public static String jdbcDriverClassOracleDefaultValue = "oracle.jdbc.driver.OracleDriver";

	/********* 以下为pro配置key ********/
	public static final String jdbcDriverClass = "jdbc.driverclass";

	public static final String jdbcUrl = "jdbc.url";

	public static final String jdbcUser = "jdbc.user";

	public static final String jdbcPassword = "jdbc.password";

	public static final String tableName = "table.name";

	public static final String domainObjectname = "domain.objectname";

	public static final String projectPath = "project.path";

	public static final String projectBasePackageJava = "project.package.base.java";

	public static final String projectBasePackageResource = "project.package.base.resource";

	public static final String packageModel = "project.package.model";

	public static final String packageMapping = "project.package.mapping";

	public static final String packageMapper = "project.package.mapper";

	public static final String packageService = "project.package.service";

	/**
	 * 消息类型
	 */
	public enum MessageType {
		ERROR, WARNING, INFO
	}

	public static void main(String[] args) {
		String str = "jdbc:mysql://101.37.78.253:3306/bk_product?characterEncoding=UTF-8";
		int lastIndex = str.indexOf("?") > 0 ? str.indexOf("?") : str.length();
		System.out.println(str.substring(str.lastIndexOf("/") + 1, lastIndex));
	}

}
