package cn.yihai.basic.core.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;

public class HBaseClientMainTest {
	
	private static HBaseClient hbaseClient;
	
	public static void main(String[] args) throws IOException {
		hbaseClient = HBaseClient.getInstance();
		HBaseClientMainTest test = new HBaseClientMainTest();
		test.testCreateNamespace();
		test.testCreateTable();
		test.testInsertData();
		test.testQuery();
		test.testQueryByRow();
		test.testQueryByFilter();
		test.testQueryByFilters();
		test.testAddColumn();
		test.testDeleteColumn();
		test.testDeleteRow();
		test.testDeleteTable();
		hbaseClient.close();
	}
	
	public void testCreateNamespace() throws IOException{
		hbaseClient.createNamespace("ns1");
	}

	public void testCreateTable() throws IOException{
		hbaseClient.createTableForced("ns1:t1", "cf");
	}
	
	public void testInsertData() throws IOException {
		List<Put> putList = new ArrayList<Put>();
		Put put = null;
		for (int i = 0; i < 10; i++) {
			put = new Put(Bytes.toBytes("row" + i));
			put.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("c1"), Bytes.toBytes("r" + i + "c1"));
			put.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("c2"), Bytes.toBytes("r" + i + "c2"));
			putList.add(put);
		}
		hbaseClient.insertData("ns1:t1", putList);
	}
	
	public void testQuery() throws IOException{
		ResultScanner scanner = hbaseClient.queryTable("ns1:t1");
		for (Result result : scanner) {
			byte[] row = result.getRow();
			System.out.println("row key is:" + new String(row));
			List<Cell> listCells = result.listCells();
			for (Cell cell : listCells) {
				byte[] familyArray = cell.getFamilyArray();
				byte[] qualifierArray = cell.getQualifierArray();
				byte[] valueArray = cell.getValueArray();
				System.out.println("row value is:" + new String(familyArray)
						+ new String(qualifierArray) + new String(valueArray));
			}
		}
		scanner.close();
	}
	
	public void testQueryByRow() throws IOException{
		Get get = new Get("row5".getBytes());
		Result result = hbaseClient.queryTableByRowKey("ns1:t1", get);
		byte[] row = result.getRow();
		System.out.println("row key is:" + new String(row));
		List<Cell> listCells = result.listCells();
		for (Cell cell : listCells) {
			byte[] familyArray = cell.getFamilyArray();
			byte[] qualifierArray = cell.getQualifierArray();
			byte[] valueArray = cell.getValueArray();
			System.out.println("row value is:" + new String(familyArray)
					+ new String(qualifierArray) + new String(valueArray));
		}
	}
	
	public void testQueryByFilter() throws IOException{
		Filter filter = new SingleColumnValueFilter(
				Bytes.toBytes("cf"), Bytes.toBytes("c1"),
				CompareOp.EQUAL, Bytes.toBytes("r3c1"));
		ResultScanner scanner = hbaseClient.queryTableByFilter("ns1:t1", filter);
		for (Result result : scanner) {
			byte[] row = result.getRow();
			System.out.println("row key is:" + new String(row));
			List<Cell> listCells = result.listCells();
			for (Cell cell : listCells) {
				byte[] familyArray = cell.getFamilyArray();
				byte[] qualifierArray = cell.getQualifierArray();
				byte[] valueArray = cell.getValueArray();
				System.out.println("row value is:" + new String(familyArray)
						+ new String(qualifierArray) + new String(valueArray));
			}
		}
		scanner.close();
	}
	
	public void testQueryByFilters() throws IOException{
		List<Filter> filters = new ArrayList<Filter>();
		Filter filter1 = new SingleColumnValueFilter(
				Bytes.toBytes("cf"), Bytes.toBytes("c1"),
				CompareOp.EQUAL, Bytes.toBytes("r5c1"));
		Filter filter2 = new SingleColumnValueFilter(
				Bytes.toBytes("cf"), Bytes.toBytes("c2"),
				CompareOp.EQUAL, Bytes.toBytes("r5c2"));
		filters.add(filter1);
		filters.add(filter2);
		ResultScanner scanner = hbaseClient.queryTableByFilters("ns1:t1", filters);
		for (Result result : scanner) {
			byte[] row = result.getRow();
			System.out.println("row key is:" + new String(row));
			List<Cell> listCells = result.listCells();
			for (Cell cell : listCells) {
				byte[] familyArray = cell.getFamilyArray();
				byte[] qualifierArray = cell.getQualifierArray();
				byte[] valueArray = cell.getValueArray();
				System.out.println("row value is:" + new String(familyArray)
						+ new String(qualifierArray, "UTF-8") + new String(valueArray, "UTF-8"));
			}
		}
		scanner.close();
	}
	
	public void testAddColumn() throws IOException{
		hbaseClient.addColumn("ns1:t1", "fam");
		testQueryByRow();
	}
	
	public void testDeleteColumn() throws IOException{
		hbaseClient.deleteColumn("ns1:t1", "fam");
		testQueryByRow();
	}
	
	public void testDeleteRow() throws IOException{
		List<Delete> list = new ArrayList<Delete>();
		Delete delete = new Delete(Bytes.toBytes("row4"));
		list.add(delete);
		hbaseClient.deleteRow("ns1:t1",list);
		testQuery();
	}
	
	public void testDeleteTable() throws IOException{
		hbaseClient.truncateTable("ns1:t1");
		testQuery();
		hbaseClient.deleteTable("ns1:t1");
		System.out.println(hbaseClient.isExistsTable("ns1:t1"));
	}
}
