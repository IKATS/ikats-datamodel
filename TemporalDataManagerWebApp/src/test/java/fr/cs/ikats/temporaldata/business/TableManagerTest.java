package fr.cs.ikats.temporaldata.business;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.cs.ikats.temporaldata.business.TableInfo.DataLink;
import fr.cs.ikats.temporaldata.exception.IkatsException;
import fr.cs.ikats.temporaldata.exception.ResourceNotFoundException;
import junit.framework.TestCase;
/**
 * TableManagerTest tests the TableManager and its end-user services.
 */
public class TableManagerTest extends TestCase {

	/**
	 * verbose == true enables more logs (to sysout), for instance in DEV environment, debugging the JUnit tests.
	 * Expected for usual deployment: verbose == false for usual tests, not requiring displays.
	 */
	private static boolean verbose = false;
	
    /**
     * Do not change this sample: reused by several tests: for new purposes =>
     * create another one
     */
    private final static String JSON_CONTENT_SAMPLE_1 = "{\"table_desc\":{\"title\":\"Discretized matrix\",\"desc\":\"This is a ...\"},\"headers\":{\"col\":{\"data\":[\"funcId\",\"metric\",\"min_B1\",\"max_B1\",\"min_B2\",\"max_B2\"],\"links\":null,\"default_links\":null},\"row\":{\"data\":[null,\"Flid1_VIB2\",\"Flid1_VIB3\",\"Flid1_VIB4\",\"Flid1_VIB5\"],\"default_links\":{\"type\":\"ts_bucket\",\"context\":\"processdata\"},\"links\":[null,{\"val\":\"1\"},{\"val\":\"2\"},{\"val\":\"3\"},{\"val\":\"4\"}]}},\"content\":{\"cells\":[[\"VIB2\",-50.0,12.1,1.0,3.4],[\"VIB3\",-5.0,2.1,1.0,3.4],[\"VIB4\",0.0,2.1,12.0,3.4],[\"VIB5\",0.0,2.1,1.0,3.4]]}}";

    /**
     * Tests getColumnFromTable: case when selected column is the row-header
     * values (below top-left corner)
     */
    public void testGetFirstColumnFromTable() {
        try {
            TableManager mng = new TableManager();

            TableInfo table = mng.loadFromJson(TableManagerTest.JSON_CONTENT_SAMPLE_1);

            Table tableH = mng.initTable(table, false);
            
            tableH.checkConsistency();
            
            // Review#158227 FTL non utilisé
            String firstColName = (String) tableH.getColumnsHeader().getData().get(0);
            List<String> funcIds = tableH.getColumn("funcId");
            List<Object> refFuncIds = new ArrayList<Object>(table.headers.row.data);
            refFuncIds.remove(0);

            if ( verbose ) System.out.println( funcIds );
            if ( verbose ) System.out.println( refFuncIds );
            
            assertEquals(refFuncIds, funcIds);
            
            
        }
        // Review#158227 FTL try catch Exception inutile, à supprimer
        catch (Exception e) {
            e.printStackTrace();
            fail("Test got unexpected error");
        }

    }

    /**
     * Tests getColumnFromTable: case selecting the content values
     */
    public void testGetOtherColumnsFromTable() {
        try {
            TableManager mng = new TableManager();

            TableInfo tableJson = mng.loadFromJson(TableManagerTest.JSON_CONTENT_SAMPLE_1);
            
            if ( verbose ) System.out.println(TableManagerTest.JSON_CONTENT_SAMPLE_1);

            Table table = mng.initTable(tableJson, false);

            // Testing typed String access
            //
            List<String> metrics = table.getColumn("metric");
            List<String> refMetrics = new ArrayList<>();
            for (List<Object> row : tableJson.content.cells) {
                refMetrics.add((String) row.get(0));
            }

            assertEquals(refMetrics, metrics);

            // Testing typed Double access
            //

            List<Double> otherDecimal = table.getColumn("min_B1", Double.class);
            List<Double> refOtherDecimal = new ArrayList<>();
            for (List<Object> row : tableJson.content.cells) {
                refOtherDecimal.add((Double) row.get(1));
            }

            if ( verbose ) System.out.println( refOtherDecimal );
            if ( verbose ) System.out.println( otherDecimal );
            
            assertEquals(refOtherDecimal, otherDecimal);
            assertEquals(-50.0, otherDecimal.get(0));

            // Testing untyped case: Object
            //
            List<Object> other = table.getColumn("max_B1", Object.class);
            List<Object> refOther = new ArrayList<>();
            for (List<Object> row : tableJson.content.cells) {
                refOther.add(row.get(2));
            }

            assertEquals(refOther, other);
            assertEquals(12.1, other.get(0));
        }
        // Review#158227 FTL try catch Exception inutile, à supprimer
        catch (Exception e) {
            e.printStackTrace();
            fail("Test got unexptected error");
        }

    }

    /**
     * Tests getting a column with col header name, from a table with columns
     * header and without rows header.
     */
    public void testGetColumnFromHeaderName() {
        try {
            TableManager mng = new TableManager();

            // Test first subcase: without rows header
            Table lTestedTable = mng.initTable(Arrays.asList("Id", "Target"), false);
            lTestedTable.appendRow(Arrays.asList("hello", 1));
            lTestedTable.appendRow(Arrays.asList("hello2", 10));
            lTestedTable.appendRow(Arrays.asList("hello3", 100));

            lTestedTable.checkConsistency();
            
            List<String> myIds = lTestedTable.getColumn("Id");
            List<Integer> myTargets = lTestedTable.getColumn("Target", Integer.class);
            assertEquals(Arrays.asList("hello", "hello2", "hello3"), myIds);
            assertEquals(Arrays.asList(1, 10, 100), myTargets);
            
            // Test second subcase: with rows header
            Table lTestedTableWithRowsH = mng.initTable(Arrays.asList("Id", "Target"), true);
            lTestedTableWithRowsH.appendRow("hello", Arrays.asList(1));
            lTestedTableWithRowsH.appendRow("hello2", Arrays.asList(10));
            lTestedTableWithRowsH.appendRow("hello3", Arrays.asList(100));

            lTestedTableWithRowsH.checkConsistency();
            
            List<String> myIdsBIS = lTestedTableWithRowsH.getColumn("Id");
            // testing converted String
            List<String> myTargetsBIS = lTestedTableWithRowsH.getColumn("Target");

            assertEquals(Arrays.asList("hello", "hello2", "hello3"), myIdsBIS);
            // testing converted String
            assertEquals(Arrays.asList("1", "10", "100"), myTargetsBIS);

        }
        // Review#158227 FTL try catch Exception inutile, à supprimer
        catch (Exception e) {
            e.printStackTrace();
            fail("Test got unexptected error");
        }
    }

    public void testGetRowFromTable() {
        try {
            TableManager mng = new TableManager();

            TableInfo table = mng.loadFromJson(TableManagerTest.JSON_CONTENT_SAMPLE_1);

            Table tableH = mng.initTable(table, false);

            // The simpler getter: row at index=... from TableContent:
            int contentIndex = 0;
            List<Object> selectedRowValsBis = tableH.getRow(contentIndex + 1, Object.class);

            // Another way: using the row header name
            // Reads the row header value: at position (contentIndex + 1) (after
            // top left corner)
            String secondRowName = (String) tableH.getRowsHeader().getData().get(contentIndex + 1);
            List<Object> selectedRowVals = tableH.getRow(secondRowName, Object.class);
            List<Object> ref = new ArrayList<Object>(table.content.cells.get(contentIndex));

            if ( verbose ) System.out.println(selectedRowValsBis);
            if ( verbose ) System.out.println(selectedRowVals);
            if ( verbose ) System.out.println(ref);

            assertEquals(selectedRowVals, selectedRowValsBis);
            assertEquals(selectedRowVals, Arrays.asList("VIB2", -50.0, 12.1, 1.0, 3.4));

            assertEquals(selectedRowVals, ref);

        }
        // Review#158227 FTL try catch Exception inutile, à supprimer
        catch (Exception e) {
            e.printStackTrace();
            fail("Test got unexptected error");
        }

    }

    
    /**
     * Tests initCsvLikeTable: case of the creation of a simple-csv Table: with
     * one column header and simple rows (without row header).
     */
    public void testInitTableSimple() {

        TableManager mng = new TableManager();
        TableInfo table = new TableInfo();
        Table tableH = mng.initTable(table, false);
        try {

            // Deprecated for end-user
            tableH.initColumnsHeader(true, null, new ArrayList<>(), null).addItem("One", null).addItem("Two", null).addItem("Three", null);
            tableH.initContent(false, null);

            // Simple initializer
            Table tableHBis = mng.initTable(Arrays.asList("One", "Two", "Three"), false);

            Object[] row1 = new Object[] { "One", new Double(2.0), Boolean.FALSE };

            Double[] row2 = new Double[] { 1.0, 2.2, 3.5 };

            Boolean[] row3 = new Boolean[] { Boolean.TRUE, false, Boolean.TRUE };

            tableH.appendRow(Arrays.asList(row1));
            tableH.appendRow(Arrays.asList(row2));
            tableH.appendRow(Arrays.asList(row3));
            tableHBis.appendRow(Arrays.asList(row1));
            tableHBis.appendRow(Arrays.asList(row2));
            tableHBis.appendRow(Arrays.asList(row3));

            tableH.checkConsistency();
            tableHBis.checkConsistency();
            
            if ( verbose ) System.out.println(mng.serializeToJson(table));
            if ( verbose ) System.out.println(mng.serializeToJson(tableHBis.getTableInfo()));

            assertEquals(mng.serializeToJson(table), mng.serializeToJson(tableHBis.getTableInfo()));

            // Review#158227 FTL non utilisé
            List<Object> columnn = tableH.getColumn("One");

        }
        // Review#158227 FTL try catch Exception inutile, à supprimer
        catch (Exception e) {
            e.printStackTrace();
            fail("Failed test: unexpected error");
        }

    }

    /**
     * Tests initCsvLikeTable: case of the creation of a simple-csv Table: with
     * column header and rows header.
     */
    public void testInitTableWithRowsHeader() {

        TableManager mng = new TableManager();
        TableInfo table = new TableInfo();
        Table tableH = mng.initTable(table, false);
        try {

            tableH.initColumnsHeader(true, null, new ArrayList<>(), null).addItem("Above row header", null).addItem("One", null).addItem("Two", null).addItem("Three",
                    null);
            tableH.initRowsHeader(false, null, new ArrayList<>(), null);
            tableH.initContent(false, null);

            // Simplified initializer used with tableHBis
            Table tableHBis = mng.initTable(Arrays.asList(new String[] { "Above row header", "One", "Two", "Three" }), true);

            // Defining the content rows - excluding row header part-
            Object[] row1 = new Object[] { "One", new Double(2.0), Boolean.FALSE };

            Double[] row2 = new Double[] { 1.0, 2.2, 3.5 };

            Boolean[] row3 = new Boolean[] { Boolean.TRUE, false, Boolean.TRUE };

            // append content rows + defines headers "A" "B" ...
            tableH.appendRow("A", Arrays.asList(row1));
            tableH.appendRow("B", Arrays.asList(row2));
            tableH.appendRow("C", Arrays.asList(row3));
            tableHBis.appendRow("A", Arrays.asList(row1));
            tableHBis.appendRow("B", Arrays.asList(row2));
            tableHBis.appendRow("C", Arrays.asList(row3));

            tableH.checkConsistency();
            tableHBis.checkConsistency();
            
            if ( verbose )  System.out.println(mng.serializeToJson(table));
            if ( verbose )  System.out.println(mng.serializeToJson(tableHBis.getTableInfo()));

            assertEquals(mng.serializeToJson(table), mng.serializeToJson(tableHBis.getTableInfo()));

            List<Object> columnnTwo = tableH.getColumn("Two", Object.class);
            if ( verbose )  System.out.println( columnnTwo );

            assertEquals(columnnTwo, Arrays.asList(new Object[] { 2.0, 2.2, false }));

            List<Object> columnnOfRowHeaders = tableH.getColumn("Above row header");
            if ( verbose )  System.out.println( columnnOfRowHeaders );

            assertEquals(columnnOfRowHeaders, Arrays.asList("A", "B", "C"));
        }
        // Review#158227 FTL try catch Exception inutile, à supprimer
        catch (Exception e) {
            e.printStackTrace(System.err);
            fail("Failed test: unexpected error");
        }
    }

    /**
     * Tests the init of Table handling links and headers.
     * Added at the end of test: the getters on TableElements by Row or by Column.
     */
    public void testInitTableWithRowsHeaderWithLinks() {

        TableManager mng = new TableManager();
        TableInfo table = new TableInfo();
        Table tableH = mng.initTable(table, false);
        try {
            DataLink defColH = new DataLink();
            defColH.context = "conf col header link";
            DataLink defRowH = new DataLink();
            defRowH.context = "conf row header link";
            DataLink defContent = new DataLink();
            defContent.context = "conf content link";

            tableH.initColumnsHeader(true, null, new ArrayList<>(), null).addItem("Above row header", null).addItem("One", null).addItem("Two", null).addItem("Three",
                    null);
            tableH.initRowsHeader(false, null, new ArrayList<>(), null);
            tableH.initContent(false, null);
            tableH.enableLinks(true, defColH, true, defRowH, true, defContent);

            // Simplified initializer used with tableHBis
            Table tableHBis = mng.initTable(Arrays.asList(new String[] { "Above row header", "One", "Two", "Three" }), true);
            tableHBis.enableLinks(true, defColH, true, defRowH, true, defContent);

            // Defining the content rows - excluding row header part-
            Object[] row1 = new Object[] { "One", new Double(2.0), Boolean.FALSE };

            // Row2 ....
            DataLink linkOne = new DataLink();
            linkOne.context = "ctx 1";
            linkOne.val = "val 1";

            DataLink linkTwo = new DataLink();
            linkTwo.type = "typ 2";
            linkTwo.val = "val 2";

            // ... this row has defined links ...
            List<TableElement> row2AsList = TableElement.encodeElements(new TableElement("Prem", linkOne), new TableElement("Sec", linkTwo), "Tri");

            // Row3
            List<TableElement> row3 = TableElement.encodeElements(1, 2, 3);

            // Init tableH with links ...
            //
            // append content rows + defines headers "A" "B" ...

            tableH.appendRow("A", Arrays.asList(row1));

            tableH.appendRow("B", row2AsList);

            tableH.appendRow("C", row3);

            // Tests the same content with tableHBis: testing that 2
            // initializations are equivalent.
            tableHBis.appendRow("A", Arrays.asList(row1));

            // ... this row has defined links ...
            tableHBis.appendRow("B", row2AsList);

            tableHBis.appendRow("C", row3);

            tableH.checkConsistency();
            tableHBis.checkConsistency();

            if ( verbose )  System.out.println(mng.serializeToJson(table));
            if ( verbose )  System.out.println(mng.serializeToJson(tableHBis.getTableInfo()));
            assertEquals(mng.serializeToJson(table), mng.serializeToJson(tableHBis.getTableInfo()));

            List<Object> columnnTwo = tableH.getColumn("Two", Object.class);
            if ( verbose )  System.out.println( columnnTwo );

            assertEquals(columnnTwo, Arrays.asList(new Object[] { row1[1], row2AsList.get(1).data, row3.get(1).data }));
            assertEquals(columnnTwo, tableHBis.getColumn("Two", Object.class));

            // Tests link selection: linkTwo.type= "typ 2";
            // linkTwo.val= "val 2";
            // assertEquals( tableH.cells.links.get(1).get(1).type,
            // tableHBis.getColumnFromTable("Two") );

            List<Object> columnnOfRowHeaders = tableH.getColumn("Above row header");
            if ( verbose )  System.out.println( columnnOfRowHeaders );

            assertEquals(columnnOfRowHeaders, Arrays.asList(new Object[] { "A", "B", "C" }));
            
            // added test for getRow getting TableElement
            List<TableElement> elemsInB = tableHBis.getRow("B", TableElement.class);
            assertEquals( "Prem", elemsInB.get(0).data);
            assertEquals( linkOne, elemsInB.get(0).link);
            
            // added test for getRow getting TableElement from index
            List<TableElement> elemsInBfromIndex = tableHBis.getRow(2, TableElement.class);
            assertEquals( "Prem", elemsInBfromIndex.get(0).data);
            assertEquals( linkOne, elemsInBfromIndex.get(0).link);
            
            // added test for getRow getting TableElement from index
            List<TableElement> elemsContentRow1 = tableHBis.getContentRow(1, TableElement.class);
            assertEquals( "Prem", elemsContentRow1.get(0).data);
            assertEquals( linkOne, elemsContentRow1.get(0).link);
            
            
            // added test for getColumn getting TableElement
            List<TableElement> elemsInOne = tableHBis.getColumn("One", TableElement.class);
            assertEquals( "Prem", elemsInOne.get(1).data);
            assertEquals( linkOne, elemsInOne.get(1).link);
            
            // added test for getColumn getting TableElement
            List<TableElement> elemsContent0 = tableHBis.getContentColumn(0, TableElement.class);
            assertEquals( "Prem", elemsContent0.get(1).data);
            assertEquals( linkOne, elemsContent0.get(1).link);
            
            // added test for getColumn getting TableElement from index
            List<TableElement> elemsInOnefromIndex = tableHBis.getColumn(1, TableElement.class);
            assertEquals( "Prem", elemsInOnefromIndex.get(1).data);
            assertEquals( linkOne, elemsInOnefromIndex.get(1).link);
            
            
            
        }
        // Review#158227 FTL try catch Exception inutile, à supprimer
        catch (Exception e) {
            e.printStackTrace(System.err);
            fail("Failed test: unexpected error");
        }
    }

    /**
     * tests simple case of appendRow, with only data part, without link.
     */
    public void testAppendRowWithoutLinks() {

        try {
            TableManager mng = new TableManager();

            mng = new TableManager();

            TableInfo table = mng.loadFromJson(TableManagerTest.JSON_CONTENT_SAMPLE_1);

            Table tableH = mng.initTable(table, false);
            int initialRowCount = tableH.getRowCount(true);
            int initialColumnCount = tableH.getColumnCount(true);

            if ( verbose )  System.out.println(TableManagerTest.JSON_CONTENT_SAMPLE_1);

            List<Object> addedList = new ArrayList<>();
            for (int i = 0; i < 4; i++)
                addedList.add("item" + i);

            // Should accept different types in a row:
            // => insert a different Type: int instead of String
            addedList.add(10);

            // Tests appended row with not links
            String addedRowHeaderData = "AddedRow";
            // Review#158227 FTL non utilisé
            int index = tableH.appendRow(addedRowHeaderData, addedList);

            // Review#158227 FTL code en commentaire, à supprimer
            // System.out.println("Row header data: " +
            // table.headers.row.data.get(5));
            // Review#158227 FTL code en commentaire, à supprimer
            // System.out.println("" + table.content.cells.get(4));

            int finalRowCount = tableH.getRowCount(true);
            int finalColumnCount = tableH.getColumnCount(true);

            tableH.checkConsistency();
            
            assertTrue(initialColumnCount == finalColumnCount);
            assertTrue(initialRowCount + 1 == finalRowCount);

            assertEquals(table.headers.row.data.get(finalRowCount - 1), addedRowHeaderData);
            assertEquals(table.content.cells.get(finalRowCount - 2), addedList);
        }
        // Review#158227 FTL try catch Exception inutile, à supprimer
        catch (Exception e) {
            e.printStackTrace();
            fail("Test got unexpected error");
        }

    }

    /**
     * tests the getColumn services
     */
    public void testGetColumn() {
        try {
            TableManager mng = new TableManager();
            Table myT = mng.initTable(Arrays.asList("One", "Two", "Three"), false);
            for (int i = 0; i < 10; i++) {
                myT.appendRow(Arrays.asList(i < 5, "" + i, null));
            }

            if ( verbose ) System.out.println(myT.getColumn("One"));
            if ( verbose ) System.out.println(myT.getColumn("Two"));
            if ( verbose ) System.out.println(myT.getColumn("Three"));

            List<Boolean> strOneList = myT.getColumn("One", Boolean.class);
            List<String> strOneListAString = myT.getColumn("One");
            // Review#158227 FTL code mort à supprimer : wrongTypeTested non utilisé
            try {
                List<BigDecimal> wrongTypeTested = myT.getColumn("One", BigDecimal.class);
                fail("Incorrect: class cast exception not detected !");
            }
            catch (IkatsException e) {
                assertTrue(true);
            }
            catch (Exception e) {
                fail("Unexpected exception");
            }

            assert (strOneList.get(0) instanceof Boolean);
            assert (strOneListAString.get(0) instanceof String);
        }
        // Review#158227 FTL try catch Exception inutile, à supprimer
        catch (Exception e) {
            e.printStackTrace();
            fail("Test got unexptected error");
        }

    }
    /**
     * Tests the getRow services
     */
    public void testGetRow() {
        try {
            TableManager mng = new TableManager();
            Table myT = mng.initEmptyTable(true, true);
            myT.getColumnsHeader().addItem( "One").addItem( "Two").addItem("Three");
            for (int i = 0; i < 10; i++) {
                myT.appendRow("row"+i, Arrays.asList(i, i +2) );
            }

            if ( verbose ) System.out.println(myT.getRow("row1"));
            if ( verbose ) System.out.println(myT.getRow("row2"));
            if ( verbose ) System.out.println(myT.getRow("row9"));

            List<Integer> strOneList = myT.getRow("row1", Integer.class);
            List<String> strOneListAString = myT.getRow("row1");
            // Review#158227 FTL code mort à supprimer : wrongTypeTested non utilisé
            try {
                List<BigDecimal> wrongTypeTested = myT.getRow("row2", BigDecimal.class);
                fail("Incorrect: class cast exception not detected !");
            }
            catch (IkatsException e) {
                if ( verbose ) System.out.println( "testGetRow: Got expected exception" );
            }
            catch (Exception e) {
                fail("Unexpected exception");
            }

            assert (strOneList.get(0) instanceof Integer);
            assert (strOneList.get(0).equals( 0 ) );
            assert (strOneList.get(1).equals( 2 ) );
            assert (strOneListAString.get(0) instanceof String);
            assert (strOneListAString.get(0).equals("0" ));
            assert (strOneListAString.get(1).equals("2" ));
           
        }
        // Review#158227 FTL try catch Exception inutile, à supprimer
        catch (Exception e) {
            e.printStackTrace();
            fail("Test got unexptected error");
        }

    }
    
    /**
     * Tests the getRow services
     */
    public void testGetRowsHeaderItems() {
        try {
            TableManager mng = new TableManager();
            Table myT = mng.initEmptyTable(false, true);
            
          
            myT.getRowsHeader().addItems("0", "1", "2", "3", "4");
            for (int i = 0; i < 10; i++) {
                myT.appendColumn( Arrays.asList("a"+i, "b"+i, "c"+i,  "d"+i, "e"+i));
            }

            System.out.println(myT.getRowsHeader().getItems());
            assertEquals( "0", myT.getRowsHeader().getItems().get(0) );
            assertEquals( "4", myT.getRowsHeader().getItems().get(4) );
            
            System.out.println(myT.getRow("0"));
            assertEquals( "a0", myT.getRow("0").get(0) );
            assertEquals( "a5", myT.getRow("0").get(5) );
          
            // Note: we could manage also  myT.getRowsHeader().addItems(0, 1, 2, 3, 4);
            // ...
            // in that case: we also retrieve the row using myT.getRow("0") instead of myT.getRow(0) 
            // => this will work
        }
        // Review#158227 FTL try catch Exception inutile, à supprimer
        catch (Exception e) {
            e.printStackTrace();
            fail("Test got unexptected error");
        }

    }
    
    
    /**
     * Tests the getColumn services:
     * 
     */
    public void testGetColumnsHeaderItems() {
        try {
            TableManager mng = new TableManager();
            Table myT = mng.initEmptyTable(true, false);
            
            // unusual case: handling Integer -instead of String- in header data ... why not ...
            myT.getColumnsHeader().addItems(0, 1, 2, 3, 8);
            for (int i = 0; i < 10; i++) {
                myT.appendRow( Arrays.asList("a"+i, "b"+i, "c"+i,  "d"+i, "e"+i));
            }
            
            System.out.println(myT.getColumnsHeader().getItems());
            // getting effective header data
            assertEquals( new Integer(0), myT.getColumnsHeader().getItems(Integer.class).get(0));
            assertEquals( new Integer(8), myT.getColumnsHeader().getItems(Integer.class).get(4));
            // getting header data as string
            assertEquals( "8", myT.getColumnsHeader().getItems().get(4));
            
            // a bit weird but the key for header 0 is toString() representation "0"
            // => 
            System.out.println(myT.getColumn("0"));
            
            
            // usual case: handling Strings in header data
            Table myUsualT = mng.initEmptyTable(true, false);
            myUsualT.getColumnsHeader().addItems("10", "1", "2", "3", "8");
            for (int i = 0; i < 10; i++) {
                myUsualT.appendRow( Arrays.asList("a"+i, "b"+i, "c"+i,  "d"+i, "e"+i));
            }
            
            System.out.println(myUsualT.getColumnsHeader().getItems());
            assertEquals( "10", myUsualT.getColumnsHeader().getItems().get(0));
            assertEquals( "8", myUsualT.getColumnsHeader().getItems().get(4));
            System.out.println(myUsualT.getColumn("10"));
            
        }
        // Review#158227 FTL try catch Exception inutile, à supprimer
        catch (Exception e) {
            e.printStackTrace();
            fail("Test got unexpected error");
        }
    }
    
    /**
     * Tests sort algo based upon column values from table content, - not from the row header -
     * Ascending order is tested here.
     */
    public void testSortRowswithoutHeaders()
    {
        try {
            TableManager mng = new TableManager();
            Table myTWithoutRowHeader = mng.initEmptyTable(false, false);
            
            myTWithoutRowHeader.appendRow(Arrays.asList("bla3", "BLAH3", 3.5));
            myTWithoutRowHeader.appendRow(Arrays.asList("bla2", "BLAH2", 2.0));
            myTWithoutRowHeader.appendRow(Arrays.asList("bla4", "BLAH4", 3.7));
            myTWithoutRowHeader.appendRow(Arrays.asList("bla6", "BLAH6", 6.0));
            myTWithoutRowHeader.appendRow(Arrays.asList("-bla6", "-BLAH6", -6.0));

            // Review#158227 FTL non utilisé
            boolean display = true;
            
            displayTestedTable(myTWithoutRowHeader);

            myTWithoutRowHeader.sortRowsByColumnValues(2, false);
            
            displayTestedTable(myTWithoutRowHeader);
            
            assertEquals(Arrays.asList( -6.0, 2.0, 3.5, 3.7, 6.0), myTWithoutRowHeader.getColumn(2, Double.class));
            
            assertEquals(Arrays.asList("-bla6", "-BLAH6", -6.0), myTWithoutRowHeader.getRow(0, Object.class) );
            assertEquals(Arrays.asList("bla2", "BLAH2", 2.0), myTWithoutRowHeader.getRow(1, Object.class) );
            assertEquals(Arrays.asList("bla3", "BLAH3", 3.5), myTWithoutRowHeader.getRow(2, Object.class) );
            assertEquals(Arrays.asList("bla4", "BLAH4", 3.7), myTWithoutRowHeader.getRow(3, Object.class) );
            assertEquals(Arrays.asList("bla6", "BLAH6", 6.0), myTWithoutRowHeader.getRow(4, Object.class) );
            
            myTWithoutRowHeader.checkConsistency();
            
        }
        // Review#158227 FTL try catch Exception inutile, à supprimer
        catch (Exception e) {
            e.printStackTrace();
            fail("Test got unexptected error");
        } 
    }
    
    /**
     * Tests sort algo based upon column values from table content, - not from the row header -
     * Ascending order is tested here.
     */
    public void testSortRowsWithColHeader()
    {
        try {
            TableManager mng = new TableManager();
            Table myTWithoutRowHeader = mng.initEmptyTable(true, false);
            
            // unusual case: handling Integer -instead of String- in header data ... why not ...
            myTWithoutRowHeader.getColumnsHeader().addItems("First", "Blabla", "Order");
          
            myTWithoutRowHeader.appendRow(Arrays.asList("bla3", "BLAH3", 3));
            myTWithoutRowHeader.appendRow(Arrays.asList("bla2", "BLAH2", 2));
            myTWithoutRowHeader.appendRow(Arrays.asList("bla4", "BLAH4", 4));
            myTWithoutRowHeader.appendRow(Arrays.asList("bla6", "BLAH6", 6));
            myTWithoutRowHeader.appendRow(Arrays.asList("-bla6", "-BLAH6", -6));

            // Review#158227 FTL non utilisé
            boolean display = true;
            
            displayTestedTable(myTWithoutRowHeader);

            myTWithoutRowHeader.sortRowsByColumnValues("Order", false);
            
            displayTestedTable(myTWithoutRowHeader);
            
            assertEquals(Arrays.asList( -6, 2, 3, 4, 6), myTWithoutRowHeader.getColumn("Order", Integer.class));
            
            assertEquals(Arrays.asList("-bla6", "-BLAH6", -6), myTWithoutRowHeader.getRow(1, Object.class) );
            assertEquals(Arrays.asList("bla2", "BLAH2", 2), myTWithoutRowHeader.getRow(2, Object.class) );
            assertEquals(Arrays.asList("bla3", "BLAH3", 3), myTWithoutRowHeader.getRow(3, Object.class) );
            assertEquals(Arrays.asList("bla4", "BLAH4", 4), myTWithoutRowHeader.getRow(4, Object.class) );
            assertEquals(Arrays.asList("bla6", "BLAH6", 6), myTWithoutRowHeader.getRow(5, Object.class) );
            
            myTWithoutRowHeader.checkConsistency();
        }
        // Review#158227 FTL try catch Exception inutile, à supprimer
        catch (Exception e) {
            e.printStackTrace();
            fail("Test got unexptected error");
        } 
    }
    
    /**
     * Tests sort algo based upon column values from table content, - not from the row header -
     * Ascending order is tested here.
     */
    public void testSortRowsWithAllHeaders()
    {
        try {
            TableManager mng = new TableManager();
            Table myTWithoutRowHeader = mng.initEmptyTable(true, true);
            
            // unusual case: handling Integer -instead of String- in header data ... why not ...
            myTWithoutRowHeader.getColumnsHeader().addItems("TopLeft", "First", "Blabla", "Order");
            // needs to define the top left corner as undefined !
            myTWithoutRowHeader.getRowsHeader().addItem(null);
            
            myTWithoutRowHeader.appendRow("B1.1", Arrays.asList("bla3", "BLAH3", 3));
            myTWithoutRowHeader.appendRow("B1", Arrays.asList("bla2", "BLAH2", 2));
            myTWithoutRowHeader.appendRow("A100", Arrays.asList("bla4", "BLAH4", 4));
            myTWithoutRowHeader.appendRow("A10", Arrays.asList("bla6", "BLAH6", 6));
            myTWithoutRowHeader.appendRow("A2", Arrays.asList("-bla6", "-BLAH6", -6));

            // Review#158227 FTL non utilisé
            boolean display = true;
            
            displayTestedTable( myTWithoutRowHeader);

            myTWithoutRowHeader.sortRowsByColumnValues("Order", false);
            
            displayTestedTable( myTWithoutRowHeader);
            
            assertEquals(Arrays.asList( -6, 2, 3, 4, 6), myTWithoutRowHeader.getColumn("Order", Integer.class));
            
            assertEquals(Arrays.asList("-bla6", "-BLAH6", -6), myTWithoutRowHeader.getRow(1, Object.class) );
            assertEquals(Arrays.asList("bla2", "BLAH2", 2), myTWithoutRowHeader.getRow(2, Object.class) );
            assertEquals(Arrays.asList("bla3", "BLAH3", 3), myTWithoutRowHeader.getRow(3, Object.class) );
            assertEquals(Arrays.asList("bla4", "BLAH4", 4), myTWithoutRowHeader.getRow(4, Object.class) );
            assertEquals(Arrays.asList("bla6", "BLAH6", 6), myTWithoutRowHeader.getRow(5, Object.class) );
            
            myTWithoutRowHeader.sortRowsByColumnValues("TopLeft", false);
            
            displayTestedTable(myTWithoutRowHeader);
            
            assertEquals(Arrays.asList( "A2", "A10", "A100", "B1", "B1.1"), myTWithoutRowHeader.getColumn("TopLeft", String.class));
            
            myTWithoutRowHeader.checkConsistency();
        }
        // Review#158227 FTL try catch Exception inutile, à supprimer
        catch (Exception e) {
            e.printStackTrace();
            fail("Test got unexptected error");
        } 
    }

    /**
     * Test insertColumn with all headers activated
     */
    public void testInsertColumnWithAllHeaders()
    {
        try {
            TableManager mng = new TableManager();
            
            // both headers are managed
            Table myTable = mng.initEmptyTable(true, true);
            
            // unusual case: handling Integer -instead of String- in header data ... why not ...
            myTable.getColumnsHeader().addItems("TopLeft", "First", "Blabla", "Order");
            // needs to define the top left corner as undefined !
            myTable.getRowsHeader().addItem(null);
            
            myTable.appendRow("B1.1", Arrays.asList("bla3", "BLAH3", 3));
            myTable.appendRow("B1", Arrays.asList("bla2", "BLAH2", 2));
            myTable.appendRow("A100", Arrays.asList("bla4", "BLAH4", 4));
            myTable.appendRow("A10", Arrays.asList("bla6", "BLAH6", 6));
            myTable.appendRow("A2", Arrays.asList("-bla6", "-BLAH6", -6));
 
            displayTestedTable(myTable);

            myTable.insertColumn( "Blabla", "Bazar", Arrays.asList( Boolean.TRUE, Boolean.FALSE, "text", null, 3.14) );
            
            displayTestedTable(myTable);
            
            myTable.checkConsistency();
        
            assertEquals(Arrays.asList( "TopLeft", "First", "Bazar", "Blabla", "Order"), myTable.getColumnsHeader().getItems());
            assertEquals(Arrays.asList( true, false, "text", null, 3.14 ), myTable.getColumn("Bazar", Object.class) );
            
        }
        // Review#158227 FTL try catch Exception inutile, à supprimer
        catch (Exception e) {
            e.printStackTrace();
            fail("Test got unexptected error");
        } 
    }
    
    /**
     * Test insertColumn() with only the column header
     */
    public void testInsertColumnWithColHeader()
    {
        try {
            TableManager mng = new TableManager();
            Table myTWithColHeader = mng.initEmptyTable(true, false);
            
            // unusual case: handling Integer -instead of String- in header data ... why not ...
            myTWithColHeader.getColumnsHeader().addItems("First", "Blabla", "Order");
            
            myTWithColHeader.appendRow(Arrays.asList("bla3", "BLAH3", 3));
            myTWithColHeader.appendRow(Arrays.asList("bla2", "BLAH2", 2));
            myTWithColHeader.appendRow(Arrays.asList("bla4", "BLAH4", 4));
            myTWithColHeader.appendRow(Arrays.asList("bla6", "BLAH6", 6));
            myTWithColHeader.appendRow(Arrays.asList("-bla6", "-BLAH6", -6));

            // Review#158227 FTL non utilisé
            boolean display = true;
            
            displayTestedTable( myTWithColHeader);

            myTWithColHeader.insertColumn( "Blabla", "Bazar", Arrays.asList( Boolean.TRUE, Boolean.FALSE, "text", null, 3.14) );
            
            displayTestedTable( myTWithColHeader);
            
            myTWithColHeader.checkConsistency();
        
            assertEquals(Arrays.asList( "First", "Bazar", "Blabla", "Order"), myTWithColHeader.getColumnsHeader().getItems());
            assertEquals(Arrays.asList( true, false, "text", null, 3.14 ), myTWithColHeader.getColumn("Bazar", Object.class) );
            
        }
        // Review#158227 FTL try catch Exception inutile, à supprimer
        catch (Exception e) {
            e.printStackTrace();
            fail("Test got unexpected error");
        } 
    }
    
    /**
     * Tests insertColumn() with index, and no headers at all
     */
    public void testInsertColumnWithoutHeader()
    {
        try {
            TableManager mng = new TableManager();
            
            // no headers
            Table myTable = mng.initEmptyTable(false, false);
            
            myTable.appendRow(Arrays.asList("bla3", "BLAH3", 3));
            myTable.appendRow(Arrays.asList("bla2", "BLAH2", 2));
            myTable.appendRow(Arrays.asList("bla4", "BLAH4", 4));
            myTable.appendRow(Arrays.asList("bla6", "BLAH6", 6));
            myTable.appendRow(Arrays.asList("-bla6", "-BLAH6", -6));

            // Review#158227 FTL non utilisé
            boolean display = true;
            
            displayTestedTable( myTable);

            myTable.insertColumn( 1, Arrays.asList( Boolean.TRUE, Boolean.FALSE, "text", null, 3.14) );
            
            displayTestedTable( myTable);
            
            myTable.checkConsistency();
        
            assertEquals(Arrays.asList( true, false, "text", null, 3.14 ), myTable.getColumn(1, Object.class) );
            
        }
        // Review#158227 FTL try catch Exception inutile, à supprimer
        catch (Exception e) {
            e.printStackTrace();
            fail("Test got unexptected error");
        }
    }
    
    /**
     * Tests the insertRow() without header
     */
    public void testInsertRowWithoutHeader()
    {
        try {
            TableManager mng = new TableManager();
            
            // no headers
            Table myTable = mng.initEmptyTable(false, false);
            
            myTable.appendRow(Arrays.asList("bla3", "BLAH3", 3));
            myTable.appendRow(Arrays.asList("bla2", "BLAH2", 2));
            myTable.appendRow(Arrays.asList("bla4", "BLAH4", 4));
            myTable.appendRow(Arrays.asList("bla6", "BLAH6", 6));
            myTable.appendRow(Arrays.asList("-bla6", "-BLAH6", -6));

            // Review#158227 FTL non utilisé
            boolean display = true;
            
            displayTestedTable( myTable);

            myTable.insertRow( 1, Arrays.asList( "avantBla2", Boolean.FALSE, "text") );
            
            displayTestedTable(myTable);
            
            myTable.checkConsistency();
        
            assertEquals(Arrays.asList("avantBla2", Boolean.FALSE, "text" ), myTable.getRow(1, Object.class) );
            assertEquals(Arrays.asList("bla2", "BLAH2", 2 ), myTable.getRow(2, Object.class) );
            
        }
        // Review#158227 FTL try catch Exception inutile, à supprimer
        catch (Exception e) {
            e.printStackTrace();
            fail("Test got unexptected error");
        }
    }
    
    /**
     * Test insertRow with all headers activated
     */
    public void testInsertRowWithAllHeaders()
    {
        try {
            TableManager mng = new TableManager();
            Table myTable = mng.initEmptyTable(true, true);
            
            // unusual case: handling Integer -instead of String- in header data ... why not ...
            myTable.getColumnsHeader().addItems("TopLeft", "First", "Blabla", "Order");
            // needs to define the top left corner as undefined !
            myTable.getRowsHeader().addItem(null);
            
            myTable.appendRow("B1.1", Arrays.asList("bla3", "BLAH3", 3));
            myTable.appendRow("B1", Arrays.asList("bla2", "BLAH2", 2));
            myTable.appendRow("A100", Arrays.asList("bla4", "BLAH4", 4));
            myTable.appendRow("A10", Arrays.asList("bla6", "BLAH6", 6));
            myTable.appendRow("A2", Arrays.asList("-bla6", "-BLAH6", -6));

            // Review#158227 FTL non utilisé
            boolean display = true;
            
            displayTestedTable( myTable);

            myTable.insertRow( "A100", "Bazar", Arrays.asList( Boolean.TRUE, Boolean.FALSE, 3.14) );
            
            displayTestedTable(myTable);
            
            myTable.checkConsistency();
        
            assertEquals(Arrays.asList( null, "B1.1", "B1", "Bazar", "A100", "A10", "A2"), 
                         myTable.getRowsHeader().getItems());
            assertEquals(Arrays.asList( true, false, 3.14), myTable.getRow("Bazar", Object.class) );
            assertEquals(Arrays.asList( true, false, 3.14), myTable.getRow(3, Object.class) );
            
        }
        // Review#158227 FTL try catch Exception inutile, à supprimer
        catch (Exception e) {
            e.printStackTrace();
            fail("Test got unexptected error");
        } 
    }
    
    /**
     * Makes sysout display of Table activated, once verbose is True
     * @param table
     * @throws IkatsException
     * @throws ResourceNotFoundException
     */
    private void displayTestedTable(Table table) throws IkatsException, ResourceNotFoundException {
        if ( verbose ) 
        {
            if ( table.isHandlingColumnsHeader() )
                System.out.println( table.getColumnsHeader().getItems());
            List<Object> rowsHeaderItems = table.isHandlingRowsHeader() ? table.getRowsHeader().getItems(Object.class): null; 
            for (int i = 0; i < table.getRowCount(true); i++) {
                  String start = "";
                  if ( rowsHeaderItems != null)
                      {
                          start = "" + rowsHeaderItems.get(i ) + ": ";
                      }
                System.out.println(start +  table.getRow(i, Object.class));
            }
            System.out.println( " ");
        }
    }
}
