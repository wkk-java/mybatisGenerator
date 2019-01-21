package com.wk.main;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TooManyListenersException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.internal.JDBCConnectionFactory;

import com.wk.common.ConfigConsts;
import com.wk.common.ConfigConsts.MessageType;

/**
 * 2019/01/18
 * 
 * @author wk
 *
 */
public class GeneratorUI {

	private final JFrame mainPanel = new JFrame();
	private final JLabel name = new JLabel("数据库连接地址：");
	private final JFileChooser chooser = new JFileChooser();
	private final JTextArea txtMessage = new JTextArea("", 8, 100);
	private final JScrollPane scroll = new JScrollPane();
	private final JButton b_log = new JButton("generator");
	private final JButton b_exit = new JButton("exit");
	private final JLabel lblNewLabel_1 = new JLabel("用户名：");
	private final JTextField txtjdbcUser = new JTextField();
	private final JLabel label = new JLabel("密码：");
	private final JTextField txtJdbcUrl = new JTextField();
	private final JLabel lblModelPackage = new JLabel("model package：");
	private final JTextField txtPackageModel = new JTextField();
	private final JLabel lblMapperPackage = new JLabel("mapper package：");
	private final JTextField txtPackageMapper = new JTextField();
	private final JLabel label_1 = new JLabel("项目根路径：");
	private final JTextField txtProjectPath = new JTextField();
	private final JLabel lblMappingPackage = new JLabel("mapping package：");
	private final JTextField txtPackageMapping = new JTextField();

	private final JLabel lblServicePackage = new JLabel("service package：");
	private JTextField txtPackageService;
	private JPasswordField txtPassword;

	private final JComboBox cboTables = new JComboBox();

	private static String cboDefaultValue = "                 --请选择--";
	private Runnable infoTextUpdateRunner = null;// 提示信息更新线程
	private Runnable errorTextUpdateRunner = null;// 错误信息更新线程

	private static String infoText = "";
	private static String errorText = "";
	private JTextField txtJdbcDriver;

	// 私有构造
	private GeneratorUI() {
		txtProjectPath.setBounds(162, 250, 479, 21);
		txtProjectPath.setColumns(10);
		txtPackageMapper.setHorizontalAlignment(SwingConstants.LEFT);
		txtPackageMapper.setBounds(531, 189, 207, 21);
		txtPackageMapper.setColumns(10);
		txtPackageModel.setHorizontalAlignment(SwingConstants.LEFT);
		txtPackageModel.setBounds(163, 189, 192, 21);
		txtPackageModel.setColumns(10);
		txtJdbcUrl.setHorizontalAlignment(SwingConstants.LEFT);
		txtJdbcUrl.setBounds(163, 126, 479, 21);
		txtJdbcUrl.setColumns(10);
		txtjdbcUser.setHorizontalAlignment(SwingConstants.LEFT);
		txtjdbcUser.setBounds(162, 93, 193, 21);
		txtjdbcUser.setColumns(10);
		infoTextUpdateRunner = new Runnable() {// 实例化更新组件的线程
			public void run() {
				if (infoText != null && infoText.trim().length() != 0) {
					txtMessage.append(infoText + "\n");
					infoText = "";
					// 滑至底部
					txtMessage.setCaretPosition(txtMessage.getText().length());
				}
			}
		};

		errorTextUpdateRunner = new Runnable() {// 实例化更新组件的线程
			public void run() {
				if (errorText != null && errorText.trim().length() != 0) {
					txtMessage.append(errorText + "\n");
					errorText = "";
					// 滑至底部
					txtMessage.setCaretPosition(txtMessage.getText().length());
				}
			}
		};

		mainPanel.getContentPane().setFont(new Font("新宋体", Font.PLAIN, 12));
		DropTarget dropTarget = new DropTarget(mainPanel, DnDConstants.ACTION_MOVE, null, true);
		try {
			dropTarget.addDropTargetListener(new DropTargetListener() {
				@Override
				public void dropActionChanged(DropTargetDragEvent dtde) {
					// TODO Auto-generated method stub

				}

				@Override
				public void drop(DropTargetDropEvent dtde) {
					boolean isAccept = false;
					if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
						// 接收拖拽目标数据
						dtde.acceptDrop(DnDConstants.ACTION_MOVE);
						isAccept = true;
						// 以文本的形式获取数据
						String text = "";
						try {
							text = dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor).toString();
						} catch (UnsupportedFlavorException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

						String filePath = text.substring(1, text.length() - 1);
						// 输出到文本区域
						txtMessage.append("\n加载配置: " + filePath);
						File configFile = new File(filePath);
						Properties config = GeneratorConfigFactory.getConfigValue(configFile);
						initConfig(config);
						// 输出到文本区域
						txtMessage.append("\n加载完毕！");
						// 如果此次拖拽的数据是被接受的, 则必须设置拖拽完成（否则可能会看到拖拽目标返回原位置, 造成视觉上以为是不支持拖拽的错误效果）
						if (isAccept) {
							dtde.dropComplete(true);
						}
					}

				}

				@Override
				public void dragOver(DropTargetDragEvent dtde) {
					// TODO Auto-generated method stub

				}

				@Override
				public void dragEnter(DropTargetDragEvent dtde) {
					// TODO Auto-generated method stub

				}

				@Override
				public void dragExit(DropTargetEvent dte) {
					// TODO Auto-generated method stub

				}

			});
		} catch (TooManyListenersException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		mainPanel.setTitle("生成工具");
		mainPanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainPanel.setExtendedState(JFrame.MAXIMIZED_BOTH);
		mainPanel.setVisible(true);
		mainPanel.getContentPane().setLayout(null);

		// 设置窗口的大小和位置
		mainPanel.setPreferredSize(new Dimension(820, 540));
		mainPanel.pack();

		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		scroll.setBounds(69, 280, 673, 166);
		// 横向滚动
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		// 纵向滚动
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		mainPanel.getContentPane().add(scroll);
		scroll.setViewportView(txtMessage);

		txtMessage.setEditable(false);
		txtMessage.setLineWrap(true); // 激活自动换行功能
		txtMessage.setWrapStyleWord(true); // 激活断行不断字功能
		txtMessage.setAutoscrolls(true);

		txtMessage.setEditable(false);
		txtMessage.setLineWrap(true); // 激活自动换行功能
		txtMessage.setWrapStyleWord(true); // 激活断行不断字功能
		txtMessage.setAutoscrolls(true);
		b_log.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 1.先测试连接，2.执行生成
				ProcessThread pt = new ProcessThread();
				pt.start();
			}
		});

		b_log.setFont(new Font("新宋体", Font.BOLD, 15));
		b_log.setBounds(230, 469, 125, 23);
		mainPanel.getContentPane().add(b_log);
		b_exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainPanel.dispose();
				System.exit(0);
			}
		});

		b_exit.setFont(new Font("新宋体", Font.BOLD, 15));
		b_exit.setBounds(440, 469, 125, 23);
		mainPanel.getContentPane().add(b_exit);

		JLabel lblNewLabel = new JLabel("mybatis code generator");
		lblNewLabel.setToolTipText("author：kkw");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("新宋体", Font.BOLD, 20));
		lblNewLabel.setBounds(272, 20, 269, 26);
		mainPanel.getContentPane().add(lblNewLabel);

		JLabel lblDatabase = new JLabel("databse：");
		lblDatabase.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDatabase.setBounds(87, 68, 74, 15);
		mainPanel.getContentPane().add(lblDatabase);

		JComboBox cboDatabase = new JComboBox();
		cboDatabase.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				String selected = cboDatabase.getSelectedItem().toString();
				if ("mysql".equals(selected)) {
					txtJdbcDriver.setText(ConfigConsts.jdbcDriverClassMysqlDefaultValue);
				} else if ("oracle".equals(selected)) {
					txtJdbcDriver.setText(ConfigConsts.jdbcDriverClassOracleDefaultValue);
				}
			}
		});
		cboDatabase.setModel(new DefaultComboBoxModel(new String[] { "mysql", "oracle" }));
		cboDatabase.setSelectedIndex(0);
		cboDatabase.setBounds(163, 65, 192, 21);
		mainPanel.getContentPane().add(cboDatabase);
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_1.setBounds(107, 96, 54, 15);

		mainPanel.getContentPane().add(lblNewLabel_1);

		mainPanel.getContentPane().add(txtjdbcUser);
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setBounds(473, 96, 54, 15);

		mainPanel.getContentPane().add(label);

		mainPanel.getContentPane().add(txtJdbcUrl);
		lblModelPackage.setHorizontalAlignment(SwingConstants.RIGHT);
		lblModelPackage.setBounds(53, 192, 108, 15);

		mainPanel.getContentPane().add(lblModelPackage);

		mainPanel.getContentPane().add(txtPackageModel);
		lblMapperPackage.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMapperPackage.setBounds(413, 192, 114, 15);

		mainPanel.getContentPane().add(lblMapperPackage);

		mainPanel.getContentPane().add(txtPackageMapper);
		label_1.setHorizontalAlignment(SwingConstants.RIGHT);
		label_1.setBounds(59, 252, 101, 15);

		mainPanel.getContentPane().add(label_1);

		mainPanel.getContentPane().add(txtProjectPath);

		JButton btnChooser = new JButton("请选择...");
		// 仅能选择目录
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		btnChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 按钮点击事件
				int returnVal = chooser.showOpenDialog(btnChooser); // 是否打开文件选择框
				if (returnVal == JFileChooser.APPROVE_OPTION) { // 如果符合文件类型
					String filepathStr = chooser.getSelectedFile().getAbsolutePath(); // 获取绝对路径
					txtProjectPath.setText(filepathStr);
				}
			}
		});
		btnChooser.setBounds(651, 248, 87, 23);
		mainPanel.getContentPane().add(btnChooser);
		lblMappingPackage.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMappingPackage.setBounds(47, 220, 114, 15);

		mainPanel.getContentPane().add(lblMappingPackage);
		txtPackageMapping.setHorizontalAlignment(SwingConstants.LEFT);
		txtPackageMapping.setColumns(10);
		txtPackageMapping.setBounds(163, 217, 192, 21);

		mainPanel.getContentPane().add(txtPackageMapping);

		JLabel label_2 = new JLabel("数据库连接符：");
		label_2.setHorizontalAlignment(SwingConstants.RIGHT);
		label_2.setBounds(53, 129, 108, 15);
		mainPanel.getContentPane().add(label_2);
		lblServicePackage.setHorizontalAlignment(SwingConstants.RIGHT);
		lblServicePackage.setBounds(419, 220, 108, 15);

		mainPanel.getContentPane().add(lblServicePackage);

		txtPackageService = new JTextField();
		txtPackageService.setHorizontalAlignment(SwingConstants.LEFT);
		txtPackageService.setColumns(10);
		txtPackageService.setBounds(531, 220, 207, 21);
		mainPanel.getContentPane().add(txtPackageService);

		txtPassword = new JPasswordField();
		txtPassword.setEchoChar('*');
		txtPassword.setBounds(531, 96, 207, 21);
		mainPanel.getContentPane().add(txtPassword);

		JButton btnTestConnection = new JButton("测试");
		btnTestConnection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 测试连接
				testConnection();
			}
		});
		btnTestConnection.setToolTipText("测试连接");
		btnTestConnection.setBounds(652, 125, 86, 23);
		mainPanel.getContentPane().add(btnTestConnection);

		JLabel lblDriver = new JLabel("jdbc driver：");
		lblDriver.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDriver.setBounds(401, 66, 126, 18);
		mainPanel.getContentPane().add(lblDriver);

		txtJdbcDriver = new JTextField();
		txtJdbcDriver.setBounds(531, 63, 207, 24);
		mainPanel.getContentPane().add(txtJdbcDriver);
		txtJdbcDriver.setColumns(10);

		JLabel lblTable = new JLabel("table：");
		lblTable.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTable.setBounds(89, 161, 72, 18);
		mainPanel.getContentPane().add(lblTable);

		cboTables.setModel(new DefaultComboBoxModel(new String[] { cboDefaultValue }));
		cboTables.setBounds(163, 160, 479, 24);
		mainPanel.getContentPane().add(cboTables);

		JRadioButton raoService = new JRadioButton("生成service(impl)");
		raoService.setSelected(true);
		raoService.setFont(new Font("新宋体", Font.PLAIN, 12));
		raoService.setBounds(651, 161, 141, 27);
		mainPanel.getContentPane().add(raoService);
		mainPanel.setResizable(false); // 禁止拉边框拉长拉断
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		int x = (int) (toolkit.getScreenSize().getWidth() - mainPanel.getWidth()) / 2;
		int y = (int) (toolkit.getScreenSize().getHeight() - mainPanel.getHeight()) / 2;
		mainPanel.setLocation(x, y);

		mainPanel.pack();
		mainPanel.getContentPane().repaint();

	}

	/**
	 * 主方法
	 * 
	 * @param args 参数
	 */
	public static void main(String[] args) {
		new GeneratorUI().initConfig(null);
	}

	/**
	 * 初始配置
	 */
	private void initConfig(Properties defaultConfigValue) {
		if (defaultConfigValue == null) {
			defaultConfigValue = GeneratorConfigFactory.getDefaultConfigValue();
		}
		String jdbcUrl = defaultConfigValue.getProperty(ConfigConsts.jdbcUrl);
		String jdbcUser = defaultConfigValue.getProperty(ConfigConsts.jdbcUser);
		String jdbcDriverClass = defaultConfigValue.getProperty(ConfigConsts.jdbcDriverClass);
		String jdbcPassword = defaultConfigValue.getProperty(ConfigConsts.jdbcPassword);
		String projectPath = defaultConfigValue.getProperty(ConfigConsts.projectPath);
		String packageModel = defaultConfigValue.getProperty(ConfigConsts.packageModel);
		String packageMapper = defaultConfigValue.getProperty(ConfigConsts.packageMapper);
		String packageMapping = defaultConfigValue.getProperty(ConfigConsts.packageMapping);
		String packageService = defaultConfigValue.getProperty(ConfigConsts.packageService);
		txtJdbcDriver.setText(jdbcDriverClass);
		txtJdbcUrl.setText(jdbcUrl);
		txtjdbcUser.setText(jdbcUser);
		txtPassword.setText(jdbcPassword);
		txtProjectPath.setText(projectPath);
		txtPackageModel.setText(packageModel);
		txtPackageMapping.setText(packageMapping);
		txtPackageMapper.setText(packageMapper);
		txtPackageService.setText(packageService);
	}

	/**
	 * 获取用户测试配置
	 * 
	 * @return 配置
	 */
	private Properties getUserProperties() {
		Properties defaultConfigValue = GeneratorConfigFactory.getDefaultConfigValue();
		defaultConfigValue.setProperty(ConfigConsts.jdbcUrl, txtJdbcUrl.getText());
		defaultConfigValue.setProperty(ConfigConsts.jdbcUser, txtjdbcUser.getText());
		defaultConfigValue.setProperty(ConfigConsts.jdbcDriverClass, txtJdbcDriver.getText());
		defaultConfigValue.setProperty(ConfigConsts.jdbcPassword, new String(txtPassword.getPassword()));
		defaultConfigValue.setProperty(ConfigConsts.projectPath, txtProjectPath.getText());
		defaultConfigValue.setProperty(ConfigConsts.packageModel, txtPackageModel.getText());
		defaultConfigValue.setProperty(ConfigConsts.packageMapper, txtPackageMapper.getText());
		defaultConfigValue.setProperty(ConfigConsts.packageMapping, txtPackageMapping.getText());
		defaultConfigValue.setProperty(ConfigConsts.packageService, txtPackageService.getText());
		return defaultConfigValue;
	}

	/**
	 * 测试连接
	 */
	private void testConnection() {
		JDBCConnectionConfiguration config = new JDBCConnectionConfiguration();
		config.setConnectionURL(txtJdbcUrl.getText());
		config.setDriverClass(txtJdbcDriver.getText());
		config.setUserId(txtjdbcUser.getText());
		config.setPassword(new String(txtPassword.getPassword()));
		JDBCConnectionFactory jdbcConnFac = new JDBCConnectionFactory(config);
		try {
			Connection connection = jdbcConnFac.getConnection();
			SQLWarning warnings = connection.getWarnings();
			appendInfoText(getFullExceptionMessage(warnings));
			String msg = connection != null ? "TEST SUCCESS!" : "TEST FAILED!";
			int messageType = connection == null ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE;
			JOptionPane.showMessageDialog(mainPanel, msg, "提示", messageType);
			// 连接成功
			if (connection != null) {
				String jdbcUrlValue = txtJdbcUrl.getText();
				int lastIndex = jdbcUrlValue.indexOf("?") > 0 ? jdbcUrlValue.indexOf("?") : jdbcUrlValue.length();
				String schema = jdbcUrlValue.substring(jdbcUrlValue.lastIndexOf("/") + 1, lastIndex);
				List<String> tables = getAllTables(connection, schema);
				tables.add(0, this.cboDefaultValue);
				cboTables.removeAllItems();
				tables.forEach(value -> {
					cboTables.addItem(value);
				});
			}
		} catch (Exception e1) {
			appendErrorText(getFullExceptionMessage(e1));
		}
	}

	/**
	 * 获取所有表
	 * 
	 * @param connection 连接
	 * @param schema     schema
	 * @return 所有表名称
	 */
	private List<String> getAllTables(Connection connection, String schema) {
		List<String> tables = new ArrayList<String>();
		Statement createStatement = null;
		try {
			String sql = String.format("select TABLE_NAME from information_schema.TABLES where TABLE_SCHEMA='%s'",
					schema);
			createStatement = connection.createStatement();
			ResultSet rs = createStatement.executeQuery(sql);
			while (rs.next()) {
				tables.add(rs.getString(1));
			}
			return tables;
		} catch (Exception e) {
			appendErrorText(getFullExceptionMessage(e));
		} finally {
			try {
				if (createStatement != null && createStatement.isClosed()) {
					createStatement.close();
				}
				if (connection != null && connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 检查输入值
	 */
	private boolean checkUIValid() {
		if (cboTables.getSelectedIndex() == 0) {
			cboTables.showPopup();
			return false;
		}
		return true;
	}

	private boolean checkFileExist() {

		return false;
	}

	/**
	 * 线程处理方法.
	 */
	private void processMethod() {
		if (!checkUIValid()) {
			return;
		}
		if (checkFileExist()) {

		}
		GeneratorSimple generatorSimple = new GeneratorSimple();
		generatorSimple.overwriteProperties(getUserProperties());
		String tableName = cboTables.getSelectedItem().toString();
		Map<MessageType, List<String>> generator = generatorSimple.generator(tableName);
		List<String> infoList = generator.get(MessageType.INFO);
		if (infoList != null && !infoList.isEmpty()) {
			appendInfoText("\n" + Arrays.toString(infoList.toArray()));
		}
		List<String> errorList = generator.get(MessageType.ERROR);
		if (errorList != null && !errorList.isEmpty()) {
			appendErrorText("\n" + Arrays.toString(errorList.toArray()));
		}
	}

	/**
	 * 显示提示消息
	 * 
	 * @param infoText 消息
	 */
	private void appendInfoText(String infoText) {
		if (infoText != null) {
			this.infoText = infoText;
			try {
				// 等上一条写完再写
				Thread.sleep(19);
			} catch (InterruptedException e) {
				SwingUtilities.invokeLater(infoTextUpdateRunner);
			}
			// 将对象排到事件派发线程的队列中
			SwingUtilities.invokeLater(infoTextUpdateRunner);
		}
	}

	/**
	 * 显示错误消息
	 * 
	 * @param errorText 消息
	 */
	private void appendErrorText(String errorText) {
		if (errorText != null) {
			this.errorText = errorText;
			try {
				// 等上一条写完再写
				Thread.sleep(17);
			} catch (InterruptedException e) {
				SwingUtilities.invokeLater(errorTextUpdateRunner);
			}
			// 将对象排到事件派发线程的队列中
			SwingUtilities.invokeLater(errorTextUpdateRunner);
		}
	}

	/**
	 * 设置界面是否可用
	 *
	 * @param enable 是否可用
	 */
	private void setUIEnable(boolean enable) {

		if (enable) {
			// 开启窗口关闭
			mainPanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		} else {
			// 禁用窗口关闭
			mainPanel.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		}
	}

	/**
	 * 显示提示信息并设置UI是否可用
	 *
	 * @param uiEnable    UI是否可用
	 * @param queryType   查询类型(身份证核实查询 / 征信查询)
	 * @param messageType 消息类型，是否（错误 / 提示）信息
	 * @param msg         提示信息
	 */
	private void showMessageAndSetUIEnable(boolean uiEnable, MessageType messageType, String msg) {
		if (MessageType.INFO.equals(messageType)) {
			appendInfoText("\n" + msg);
		} else {
			appendErrorText("\n" + msg);
		}
		setUIEnable(uiEnable);
		JOptionPane.showMessageDialog(null, msg, "提示",
				MessageType.ERROR.equals(messageType) ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * 获取完整的错误信息.
	 * 
	 * @param ex 异常
	 * @return 完整异常信息
	 */
	private static String getFullExceptionMessage(Exception ex) {
		if (ex == null)
			return null;
		try {
			final Writer writer = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(writer);
			ex.fillInStackTrace().printStackTrace(printWriter);
			writer.flush();
			writer.close();
			return writer.toString();
		} catch (Exception e2) {
			return e2.getMessage();
		}
	}

	/**
	 * 具体执行的子线程
	 */
	class ProcessThread extends Thread {
		public void run() {
			processMethod();
		}
	}
}
