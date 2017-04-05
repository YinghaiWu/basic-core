package cn.yihai.basic.core.hbase;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.util.Bytes;

import cn.yihai.basic.core.mail.Email;

public class HBaseClient {

	private Configuration configuration;
	private Connection connection;
	private Admin admin;

	private static String HBASE_ZOOKEEPER_QUORUM;
	private static HBaseClient instance;
	
	private static final String CONFIG_FILE = "/hbase.properties";
	
	static{
		Properties properties = new Properties();
		InputStream in = null;
		try {
			in = Email.class.getResourceAsStream(CONFIG_FILE);
			HBASE_ZOOKEEPER_QUORUM = properties.getProperty("hbase.zookeeper.quorum");
			properties.load(in);
		} catch (IOException e) {
			throw new RuntimeException("加载hbase配置文件失败");
		} finally {
			try {
				if(in!=null){
					in.close();
				}
			} catch (IOException e) {
			}
		}
	}
	
	private HBaseClient() throws IOException{
		configuration = HBaseConfiguration.create();
		configuration.set("hbase.zookeeper.quorum", HBASE_ZOOKEEPER_QUORUM);
		connection = ConnectionFactory.createConnection(configuration);
		admin = connection.getAdmin();
	};
	
	public static HBaseClient getInstance() throws IOException{
		if(instance==null){
			synchronized (HBaseClient.class) {
				if(instance==null){
					instance = new HBaseClient();
				}
			}
		}
		return instance;
	}
	
	public void close() throws IOException{
		if(admin!=null){
			admin.close();
		}
		if(connection!=null){
			connection.close();
		}
	}

	/**
	 * 判断命名空间是否存在
	 * @param strNamespace 命名空间
	 * @return true-存在,false-不存在
	 * @throws IOException
	 */
	public boolean isExistsNamespace(String strNamespace) throws IOException{
		NamespaceDescriptor[] namespaces = admin.listNamespaceDescriptors();
		for(int i=0; i<namespaces.length; i++){
			if(strNamespace.equals(namespaces[i].getName())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 创建命名空间
	 * @param strNamespace 命名空间
	 * @return true-创建成功,false-存在该namespace
	 * @throws IOException
	 */
	public boolean createNamespace(String strNamespace) throws IOException{
		if(isExistsNamespace(strNamespace)){
			return false;
		}else{
			admin.createNamespace(NamespaceDescriptor.create(strNamespace).build());
			return true;
		}
	}
	
	/**
	 * 判断表是否存在
	 * @param strTableName 表名
	 * @return true-存在,false-不存在
	 * @throws IOException
	 */
	public boolean isExistsTable(String strTableName) throws IOException{
		TableName tableName = TableName.valueOf(strTableName);
		return admin.tableExists(tableName);
	}
	
	/**
	 * 创建表，存在同名表时不删除同名表也不新建表
	 * @param strTableName 表名
	 * @param strFamily 列簇名
	 * @return true-成功创建，false-已存在同名表，未新建表
	 * @throws IOException
	 */
	public boolean createTable(String strTableName, String strFamily) throws IOException {
		TableName tableName = TableName.valueOf(strTableName);
		if (admin.tableExists(tableName)) {
			return false;
		}
		HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName);
		HColumnDescriptor family = new HColumnDescriptor(strFamily);
		hTableDescriptor.addFamily(family);
		admin.createTable(hTableDescriptor);
		return true;
	}
	
	/**
	 * 创建表，存在同名表时删除同名表然后新建表
	 * @param strTableName 表名
	 * @param strFamily 列簇名
	 * @return 表创建完成后返回true
	 * @throws IOException
	 */
	public boolean createTableForced(String strTableName, String strFamily) throws IOException {
		TableName tableName = TableName.valueOf(strTableName);
		if (admin.tableExists(tableName)) {
			deleteTable(strTableName);
		}
		HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName);
		HColumnDescriptor family = new HColumnDescriptor(strFamily);
		hTableDescriptor.addFamily(family);
		admin.createTable(hTableDescriptor);
		return true;
	}
	
	/**
	 * 插入数据
	 * @param strTableName 表名
	 * @param put 插入数据对象
	 * @throws IOException
	 */
	public void insertData(String strTableName, Put put) throws IOException {
		Table table = connection.getTable(TableName.valueOf(strTableName));
		table.put(put);
	}
	
	/**
	 * 插入数据
	 * @param strTableName 表名
	 * @param putList 插入数据对象集合
	 * @throws IOException
	 */
	public void insertData(String strTableName, List<Put> putList) throws IOException {
		Table table = connection.getTable(TableName.valueOf(strTableName));
		table.put(putList);
	}

	/**
	 * 扫描表
	 * @param strTableName 表名
	 * @return ResultScanner 扫描结果对象
	 * @throws IOException
	 */
	public ResultScanner queryTable(String strTableName) throws IOException {
		Table table = connection.getTable(TableName.valueOf(strTableName));
		ResultScanner scanner = table.getScanner(new Scan());
		return scanner;
	}

	/**
	 * 获取指定行的数据
	 * @param strTableName 表名
	 * @param get 查询条件对象
	 * @return 查询结果对象
	 * @throws IOException
	 */
	public Result queryTableByRowKey(String strTableName, Get get) throws IOException {
		Table table = connection.getTable(TableName.valueOf(strTableName));
		Result result = table.get(get);
		return result;
	}

	/**
	 * 根据Filter条件对象扫描表
	 * @param strTableName 表名
	 * @param filter Filter条件对象
	 * @return 扫描结果对象
	 * @throws IOException
	 */
	public ResultScanner queryTableByFilter(String strTableName, Filter filter) throws IOException {
		Table table = connection.getTable(TableName.valueOf(strTableName));
		Scan scan = new Scan();
		scan.setFilter(filter);
		ResultScanner scanner = table.getScanner(scan);
		return scanner;
	}
	
	/**
	 * 根据Filter条件对象列表扫描表
	 * @param strTableName 表名
	 * @param filters Filter条件对象集合
	 * @return 扫描结果集合
	 * @throws IOException
	 */
	public ResultScanner queryTableByFilters(String strTableName, List<Filter> filters) throws IOException {
		Table table = connection.getTable(TableName.valueOf(strTableName));
		FilterList filterList = new FilterList(filters);
		Scan scan = new Scan();
		scan.setFilter(filterList);
		ResultScanner scanner = table.getScanner(scan);
		return scanner;
	}
	
	/**
	 * 添加列
	 * @param strTableName 表名
	 * @param strColumn 列簇名
	 * @throws IOException
	 */
	public void addColumn(String strTableName, String strColumn) throws IOException {
		TableName tableName = TableName.valueOf(strTableName);
		HColumnDescriptor columnDescriptor = new HColumnDescriptor(strColumn);
		admin.addColumn(tableName, columnDescriptor);
	}

	/**
	 * 删除列
	 * @param strTableName 表名
	 * @param strColumn 列簇名
	 * @throws IOException
	 */
	public void deleteColumn(String strTableName, String strColumn) throws IOException {
		TableName tableName = TableName.valueOf(strTableName);
		admin.deleteColumn(tableName, strColumn.getBytes());
	}
	
	/**
	 * 根据rowkey删除行
	 * @param strTableName 表名
	 * @param rowkey 行名
	 * @throws IOException
	 */
	public void deleteByRowKey(String strTableName, String rowkey) throws IOException {
		Table table = connection.getTable(TableName.valueOf(strTableName));
		Delete delete = new Delete(Bytes.toBytes(rowkey));
		table.delete(delete);
	}
	
	/**
	 * 删除行
	 * @param strTableName 表名
	 * @param list 删除数据集合
	 * @throws IOException
	 */
	public void deleteRow(String strTableName, List<Delete> list) throws IOException {
		Table table = connection.getTable(TableName.valueOf(strTableName));
		table.delete(list);
	}

	/**
	 * 根据Filter条件对象删除行
	 * @param strTableName 表名
	 * @param filter Filter条件对象
	 * @throws IOException
	 */
	public void deleteByFilter(String strTableName, Filter filter) throws IOException {
		ResultScanner scanner = queryTableByFilter(strTableName, filter);
		List<Delete> list = new ArrayList<Delete>();
		for (Result result : scanner) {
			Delete delete = new Delete(result.getRow());
			list.add(delete);
		}
		deleteRow(strTableName, list);
		scanner.close();
	}

	/**
	 * 截断表
	 * @param strTableName 表名
	 * @throws IOException
	 */
	public void truncateTable(String strTableName) throws IOException {
		TableName tableName = TableName.valueOf(strTableName);
		admin.disableTable(tableName);
		admin.truncateTable(tableName, true);
	}

	/**
	 * 删除表
	 * @param strTableName 表名
	 * @throws IOException
	 */
	public void deleteTable(String strTableName) throws IOException {
		TableName tableName = TableName.valueOf(strTableName);
		admin.disableTable(tableName);
		admin.deleteTable(tableName);
	}

}
