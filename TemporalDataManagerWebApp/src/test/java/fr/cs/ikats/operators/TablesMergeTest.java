package fr.cs.ikats.operators;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.cs.ikats.operators.TablesMerge.Request;
import fr.cs.ikats.temporaldata.business.Table;
import fr.cs.ikats.temporaldata.business.TableInfo;
import fr.cs.ikats.temporaldata.business.TableManager;
import fr.cs.ikats.temporaldata.exception.IkatsException;
import fr.cs.ikats.temporaldata.exception.IkatsJsonException;

// Review#158268 FTA : .ods file could be removed from git and added as story attachment
// Review#158268 FTL : Plus utilisable au plus proche du code (difficulté de faire suivre les évols dans la forge). Pas d'impact sur le bianire final
// Review#158268 FTL : Pour autres relecteurs il est dans src/test/resources/fr.cs.ikats.operator.TablesMerge/tables-tests-csv.ods 
                     
// Review#158268 FTA : New test proposed : tables have different row count (headers not included)
// Review#158268 FTL : OK a faire -> MBD

// Review#158268 FTA : New test proposed : No headers for 1st table, Headers for 2nd table. What happens ?
// Review#158268 FTL : déjà fait -> testDoMergeWithHeaderOnSecondTable

// Review#158268 FTA : New test proposed : JoinKey not found in Table 1
// Review#158268 FTL : -> testDoMergeWithJoinKeyNotFoundFirstTable()
// Review#158268 FTA : New test proposed : JoinKey not found in Table 2
// Review#158268 FTL : -> testDoMergeWithJoinKeyNotFoundSecondTable()
// Review#158268 FTA : New test proposed : JoinKey present in both table but no identical values
// Review#158268 FTL : OK a faire -> décision MBD
public class TablesMergeTest {

    private static final Logger logger       = Logger.getLogger(TablesMergeTest.class);

    private static final String TABLE1_CSV   = "H1-1;H1-2;H1-3;H1-4;H1-5\n"
            + "H;eight;08;8;1000\n"
            + "E;five;05;5;0101\n"
            + "D;four;04;4;0100\n"
            + "I;nine;09;9;1001\n"
            + "A;one;01;1;0001\n"
            + "G;seven;07;7;0111\n"
            + "F;six;06;6;0110\n"
            + "J;ten;10;10;1010\n"
            + "C;three;03;3;0011\n"
            + "B;two;02;2;0010";
    
    private static final String TABLE1_SMALLER_CSV   = "H1-1;H1-2;H1-3;H1-4;H1-5\n"
            + "H;eight;08;8;1000\n"
            + "E;five;05;5;0101\n"
            + "I;nine;09;9;1001\n"
            + "A;un;01;1;0001\n"
            + "G;seven;07;7;0111\n"
            + "J;ten;10;10;1010\n"
            + "C;three;03;3;0011\n"
            + "B;two;02;2;0010";

    private static final String TABLE2_CSV   = "H2-1;H2-2;H2-3;H1-1;H1-2\n"
            + "0;3,14;6,28;F;six\n"
            + "3,14;9,42;6,28;A;one\n"
            + "3,14;15,71;12,57;D;four\n"
            + "3,14;0;0;H;eight\n"
            + "6,28;9,42;9,42;G;seven\n"
            + "9,42;12,57;6,28;B;two\n"
            + "9,42;0;12,57;C;three\n"
            + "9,42;6,28;15,71;I;nine\n"
            + "15,71;15,71;6,28;J;ten\n"
            + "15,71;3,14;0;E;five\n";
    
    private static final String TABLE2_SMALLER_CSV   = "H2-1;H2-2;H2-3;H1-1;H1-2\n"
            + "3,14;9,42;6,28;A;un\n"
            + "3,14;0;0;H;eight\n"
            + "6,28;9,42;9,42;G;seven\n"
            + "9,42;12,57;6,28;B;two\n"
            + "9,42;0;12,57;C;three\n"
            + "9,42;6,28;15,71;I;nine\n"
            + "15,71;15,71;6,28;J;ten\n"
            + "15,71;3,14;0;E;five\n";

    private static final String TABLE3_CSV   = "H;eight;08;8;1000\n"
            + "E;five;05;5;0101\n"
            + "D;four;04;4;0100\n"
            + "I;nine;09;9;1001\n"
            + "A;one;01;1;0001\n"
            + "G;seven;07;7;0111\n"
            + "F;six;06;6;0110\n"
            + "J;ten;10;10;1010\n"
            + "C;three;03;3;0011\n"
            + "B;two;02;2;0010\n";

    private static final String TABLE4_CSV   = "F;six;0;3,14;6,28\n"
            + "H;eight;3,14;0;0\n"
            + "D;four;3,14;15,71;12,57\n"
            + "A;one;3,14;9,42;6,28\n"
            + "G;seven;6,28;9,42;9,42\n"
            + "I;nine;9,42;6,28;15,71\n"
            + "C;three;9,42;0;12,57\n"
            + "B;two;9,42;12,57;6,28\n"
            + "E;five;15,71;3,14;0\n"
            + "J;ten;15,71;15,71;6,28\n";

    private static Table        table1       = null;
    private static Table        table2       = null;
    private static Table        table1Smaller       = null;
    private static Table        table2Smaller       = null;
    private static Table        table3       = null;
    private static Table        table4       = null;
    private static TableManager tableManager = new TableManager();

    @BeforeClass
    public static void setUpBeforClass() throws Exception {
        table1 = buildTableFromCSVString("table1", TABLE1_CSV, true);
        table2 = buildTableFromCSVString("table2", TABLE2_CSV, true);
        table1Smaller = buildTableFromCSVString("table1Smaller", TABLE1_SMALLER_CSV, true);
        table2Smaller = buildTableFromCSVString("table2Smaller", TABLE2_SMALLER_CSV, true);
        table3 = buildTableFromCSVString("table3", TABLE3_CSV, false);
        table4 = buildTableFromCSVString("table4", TABLE4_CSV, false);
    }

    /**
     * Construction check with 2 tables -> OK
     */
    @Test
    public final void testTablesMergeConstructorNominal() {

        // Build the nominal request
        Request tableMergeRequest = new Request();
        tableMergeRequest.joinOn = "join_key";
        tableMergeRequest.outputTableName = "output_table_name";
        tableMergeRequest.tables = new TableInfo[] { table1.getTableInfo(), table2.getTableInfo() };

        try {
            // Pass it to the constructor
            new TablesMerge(tableMergeRequest);
        }
        catch (IkatsOperatorException e) {
            fail("Error initializing TablesMerge operator");
        }

        // Review#158268 FTA : Internal attributes of construction could be also checked (to prove all attributes are taken in account).
    }

    /**
     * Construction check with 1 Table
     */
    @Test(expected = IkatsOperatorException.class)
    public final void testTablesMergeConstructorException() throws IkatsOperatorException {

        Request tableMergeRequest = new Request();
        tableMergeRequest.joinOn = "join_key";
        tableMergeRequest.outputTableName = "output_table_name";
        // Will raise the exception because at least 2 tables are expected
        tableMergeRequest.tables = new TableInfo[] { table1.getTableInfo() };

        new TablesMerge(tableMergeRequest);
    }

    /**
     * Construction check with no table
     */
    @Test(expected = IkatsOperatorException.class)
    public final void testTablesMergeConstructorExceptionNoTables() throws IkatsOperatorException {

        // Review#158268 FTA : Added this test because if in HMI we don't provide inputs, the method could send no values for tables field --> Protect by try catch to raise 400
    	// Review#158268 MBD test failed : unexpected NullPointerException
    	Request tableMergeRequest = new Request();
        tableMergeRequest.joinOn = "join_key";
        tableMergeRequest.outputTableName = "output_table_name";
        // Will raise the exception because at least 2 tables are expected
        tableMergeRequest.tables = null;

        new TablesMerge(tableMergeRequest);
    }

    /**
     * First Table (T1) contains headers
     * Second Table (T2) contains headers
     * 
     * There is a match: T1-col("H1-2") matches T2-col("H1-2")
     * Note: columns index begins at index 0 in above description (or use the column name)
     */
    @Test
    public final void testDoMergeNominal() throws IOException, IkatsException, IkatsOperatorException {

        // Review#158268 FTA : begin
        // I don't think having 2 columns named "H1-1" is what PO wants in results. 
        // Check with her and add to the followup the description of the behaviour since we don't have any requirements document
        // Review#158268 FTA : end
        String expected_merge = "H1-1;H1-2;H1-3;H1-4;H1-5;H2-1;H2-2;H2-3;H1-1\n"
                + "H;eight;08;8;1000;3,14;0;0;H\n"
                + "E;five;05;5;0101;15,71;3,14;0;E\n"
                + "D;four;04;4;0100;3,14;15,71;12,57;D\n"
                + "I;nine;09;9;1001;9,42;6,28;15,71;I\n"
                + "A;one;01;1;0001;3,14;9,42;6,28;A\n"
                + "G;seven;07;7;0111;6,28;9,42;9,42;G\n"
                + "F;six;06;6;0110;0;3,14;6,28;F\n"
                + "J;ten;10;10;1010;15,71;15,71;6,28;J\n"
                + "C;three;03;3;0011;9,42;0;12,57;C\n"
                + "B;two;02;2;0010;9,42;12,57;6,28;B\n";

        testTableMerge(table1, table2, "H1-2", "expected join", expected_merge);
    }
    
    /**
     * Tests the merge with H1-2 criterion, on tables with different sizes and H1-2 entries:
     * <ul><li>
     * table1Smaller  is built from table1. Lines were deleted: those with H1-2 equal to «four» or « six ». Cell was modified:  H1-2 with « one » becomes H1-2 with « un »  
     * </li><li>
     * table2Smaller is built from table2. Lines were deleted: those with H1-2 equal to «four» or « six ». Cell was modified:  H1-2 with « one » becomes H1-2 with « un »  
     * </li></ul>
     * @throws IOException
     * @throws IkatsException
     * @throws IkatsOperatorException
     */
    @Test
    public final void testDoMergeNominalDifferentSize() throws IOException, IkatsException, IkatsOperatorException {
        
    	// Review#158268 FTA : begin
    	//      MBD: review point copied from testDoMergeNominal: did not corrected the test for that point
        // I don't think having 2 columns named "H1-1" is what PO wants in results. 
        // Check with her and add to the followup the description of the behaviour since we don't have any requirements document
        // Review#158268 FTA : end
    	
    	// The expected result is the same for merge(table1smaller, table2) and merge(table1, table2smaller)
    	// It has no line whose H1-2 is "one" "un" "four" or "six"
        String expected_merge = "H1-1;H1-2;H1-3;H1-4;H1-5;H2-1;H2-2;H2-3;H1-1\n"
                + "H;eight;08;8;1000;3,14;0;0;H\n"
                + "E;five;05;5;0101;15,71;3,14;0;E\n"
                + "I;nine;09;9;1001;9,42;6,28;15,71;I\n"
                + "G;seven;07;7;0111;6,28;9,42;9,42;G\n"
                + "J;ten;10;10;1010;15,71;15,71;6,28;J\n"
                + "C;three;03;3;0011;9,42;0;12,57;C\n"
                + "B;two;02;2;0010;9,42;12,57;6,28;B\n";

        
        testTableMerge(table1Smaller, table2, "H1-2", "DoMergeNominalDifferentSize", expected_merge);
        
        testTableMerge(table1, table2Smaller, "H1-2", "DoMergeNominalDifferentSize", expected_merge);
    }

    /**
     * First Table (T1) contains headers
     * Second Table (T2) contains headers
     * No defined Key to use for join
     * There is a match: T1-col("H1-1") matches T2-col("H1-1")
     * Note: columns index begins at index 0 in above description (or use the column name)
     */
    @Test
    public final void testDoMergeWithoutJoinKey() throws IOException, IkatsException, IkatsOperatorException {

        // Review#158268 FTA : Same remark as above with "H1-2"
        String expected_merge = "H1-1;H1-2;H1-3;H1-4;H1-5;H2-1;H2-2;H2-3;H1-2\n"
                + "H;eight;08;8;1000;3,14;0;0;eight\n"
                + "E;five;05;5;0101;15,71;3,14;0;five\n"
                + "D;four;04;4;0100;3,14;15,71;12,57;four\n"
                + "I;nine;09;9;1001;9,42;6,28;15,71;nine\n"
                + "A;one;01;1;0001;3,14;9,42;6,28;one\n"
                + "G;seven;07;7;0111;6,28;9,42;9,42;seven\n"
                + "F;six;06;6;0110;0;3,14;6,28;six\n"
                + "J;ten;10;10;1010;15,71;15,71;6,28;ten\n"
                + "C;three;03;3;0011;9,42;0;12,57;three\n"
                + "B;two;02;2;0010;9,42;12,57;6,28;two\n";

        testTableMerge(table1, table2, null, "expected join_without_join_key", expected_merge);
    }

    /**
     * First Table (T1) doesn't contain headers
     * Second Table (T2) doesn't contain  headers
     * No defined Key to use for join
     * There is a match: T1-col(0) matches T2-col(0)
     * Note: columns index begins at index 0 in above description (or use the column name)
     */
    @Test
    public final void testDoMergeWithoutColumnsHeaderAndNoJoinKey() throws IkatsJsonException, IOException, IkatsException, IkatsOperatorException {

        String expected_merge = "H;eight;08;8;1000;eight;3,14;0;0\n"
                + "E;five;05;5;0101;five;15,71;3,14;0\n"
                + "D;four;04;4;0100;four;3,14;15,71;12,57\n"
                + "I;nine;09;9;1001;nine;9,42;6,28;15,71\n"
                + "A;one;01;1;0001;one;3,14;9,42;6,28\n"
                + "G;seven;07;7;0111;seven;6,28;9,42;9,42\n"
                + "F;six;06;6;0110;six;0;3,14;6,28\n"
                + "J;ten;10;10;1010;ten;15,71;15,71;6,28\n"
                + "C;three;03;3;0011;three;9,42;0;12,57\n"
                + "B;two;02;2;0010;two;9,42;12,57;6,28\n";

        testTableMerge(table3, table4, null, "MergeWithoutColumnsHeaderAndNoJoinKey", expected_merge);
    }

    @Test
    @Ignore("To do when join could be realized with the numeric index of the column, when no header")
    public final void testDoMergeWithoutColumnsHeader() throws IkatsJsonException, IOException, IkatsException, IkatsOperatorException {

        // Review#158268 FTA : About @Ignore, when is it planned ? Test method not reviewed until I get answer

        String expected_merge = "H;eight;08;8;1000;H;3,14;0;0\n"
                + "E;five;05;5;0101;E;15,71;3,14;0\n"
                + "D;four;04;4;0100;D;3,14;15,71;12,57\n"
                + "I;nine;09;9;1001;I;9,42;6,28;15,71\n"
                + "A;one;01;1;0001;A;3,14;9,42;6,28\n"
                + "G;seven;07;7;0111;G;6,28;9,42;9,42\n"
                + "F;six;06;6;0110;F;0;3,14;6,28\n"
                + "J;ten;10;10;1010;J;15,71;15,71;6,28\n"
                + "C;three;03;3;0011;C;9,42;0;12,57\n"
                + "B;two;02;2;0010;B;9,42;12,57;6,28\n";

        testTableMerge(table3, table4, "2", "MergeWithoutColumnsHeader", expected_merge);
    }

    /**
     * First Table (T1) doesn't contain headers
     * Second Table (T2) contains headers
     * No defined Key to use for join
     * There is a match: T1-col(0) matches T2-col("H1-1")
     * Note: columns index begins at index 0 in above description (or use the column name)
     */
    @Test
    public final void testDoMergeWithHeaderOnSecondTable() throws IkatsJsonException, IOException, IkatsException, IkatsOperatorException {

        String expected_merge = ";;;;;H1-2;H1-3;H1-4;H1-5\n"
                + "F;six;0;3,14;6,28;six;06;6;0110\n"
                + "H;eight;3,14;0;0;eight;08;8;1000\n"
                + "D;four;3,14;15,71;12,57;four;04;4;0100\n"
                + "A;one;3,14;9,42;6,28;one;01;1;0001\n"
                + "G;seven;6,28;9,42;9,42;seven;07;7;0111\n"
                + "I;nine;9,42;6,28;15,71;nine;09;9;1001\n"
                + "C;three;9,42;0;12,57;three;03;3;0011\n"
                + "B;two;9,42;12,57;6,28;two;02;2;0010\n"
                + "E;five;15,71;3,14;0;five;05;5;0101\n"
                + "J;ten;15,71;15,71;6,28;ten;10;10;1010\n";

        testTableMerge(table4, table1, null, "MergeWithHeaderOnSecondTable", expected_merge);
    }


    
    /**
     * Test the operator exception where JoinKey is not found in Table 1
     * 
     * @throws IkatsJsonException
     * @throws IOException
     * @throws IkatsException
     * @throws IkatsOperatorException
     */
    @Test(expected = IkatsOperatorException.class)
    public final void testDoMergeWithJoinKeyNotFoundFirstTable() throws IkatsJsonException, IOException, IkatsException, IkatsOperatorException {
        
        String expected_merge = ";";
        testTableMerge(table1, table2, "H2-2", "MergeWithJoinKeyNotFound1", expected_merge);
    }
    
    /**
     * Test the operator exception where JoinKey is not found in Table 1
     * 
     * @throws IkatsJsonException
     * @throws IOException
     * @throws IkatsException
     * @throws IkatsOperatorException
     */
    @Test(expected = IkatsOperatorException.class)
    public final void testDoMergeWithJoinKeyNotFoundSecondTable() throws IkatsJsonException, IOException, IkatsException, IkatsOperatorException {
        
        String expected_merge = ";";
        testTableMerge(table1, table2, "H1-3", "MergeWithJoinKeyNotFound2", expected_merge);
    }
    
    @Test
    public final void testDoMergeWithoutJoinKeyAndNoMatch() throws IkatsJsonException, IOException, IkatsException, IkatsOperatorException {

        // Review#158268 FTA : Test is KO
        // Review#158268 FTL : it is ok on my dev branch (see origin/
    	// Review#158268 MBD : Test is KO
        // Review#158268 FTL : j'ai déplacé le commit de la branche le la story pour mettre en conformité ce test
        testTableMerge(table3, table2, null, "MergeWithoutJoinKeyAndNoMatch", ";");
    }

    /**
     * Build a {@link Table} from a CSV string
     * 
     * @param name Name of the Table to build
     * @param content Data to write into table, CSV formatted
     * @param withColumnsHeader Flag indicating if the table contains headers (true) or not (false)
     * @return the created Table
     * @throws IOException
     * @throws IkatsException
     */
    private static Table buildTableFromCSVString(String name, String content, boolean withColumnsHeader)
            throws IOException, IkatsException {

        // Convert the CSV table to expected Table format
        BufferedReader bufReader = new BufferedReader(new StringReader(content));

        String line = null;
        Table table = null;

        if (withColumnsHeader) {
            // Assuming first line contains headers
            line = bufReader.readLine();
            List<String> headersTitle = Arrays.asList(line.split(";"));
            // Replace empty strings with null (that's what do the operator when adding empty headers)
            headersTitle.replaceAll(ht -> ht.isEmpty() ? null : ht);
            table = tableManager.initTable(headersTitle, false);
        }
        else {
            table = tableManager.initEmptyTable(false, false);
        }

        // Other lines contain data
        while ((line = bufReader.readLine()) != null) {
            List<String> items = Arrays.asList(line.split(";"));
            table.appendRow(items);
        }

        table.setName(name);
        table.setDescription("Table '" + name + "' description created for tests");
        table.setTitle("Table '" + name + "' title");

        logger.trace("Table " + name + " ready");

        return table;
    }

    /**
     * Test the merge results against the expected CSV format
     * 
     * @param firstTable first Table object
     * @param secondTable second Table object
     * @param joinOn join criteria (null for no criteria)
     * @param outputTableName Name of the output
     * @param expected_merge CSV corresponding to the expected result
     * @throws IOException
     * @throws IkatsException
     * @throws IkatsJsonException
     * @throws IkatsOperatorException
     */
    private void testTableMerge(Table firstTable, Table secondTable, String joinOn, String outputTableName,
                                String expected_merge)
            throws IOException, IkatsException, IkatsJsonException, IkatsOperatorException {

        boolean resultTableWithHeader = firstTable.getColumnsHeader() != null || secondTable.getColumnsHeader() != null;

        // Prepare the expected result
        Table expectedResult = buildTableFromCSVString(outputTableName, expected_merge, resultTableWithHeader);
        expectedResult.enableLinks(true, new TableInfo.DataLink(), false, null, true, new TableInfo.DataLink());
        expectedResult.setTitle(null);
        expectedResult.setDescription(null);

        // Prepare the parameters of the merge
        Request tableMergeRequest = new Request();
        tableMergeRequest.joinOn = joinOn;
        tableMergeRequest.outputTableName = outputTableName;
        tableMergeRequest.tables = new TableInfo[] { firstTable.getTableInfo(), secondTable.getTableInfo() };

        // Instantiate the operator and do the job
        TablesMerge tablesMerge = new TablesMerge(tableMergeRequest);
        Table resultTable = tablesMerge.doMerge();

        // Test the expected number of columns
        assertEquals("Bad column count", expectedResult.getColumnCount(true), resultTable.getColumnCount(true));
        // Test the expected number of rows
        assertEquals("Bad row count", expectedResult.getRowCount(true), resultTable.getRowCount(true));

        // Test the JSON rendering -> test all the content
        String expectedTableJSON = tableManager.serializeToJson(expectedResult.getTableInfo());
        String resultTableJSON = tableManager.serializeToJson(resultTable.getTableInfo());
        expectedTableJSON = prettify(expectedTableJSON);
        resultTableJSON = prettify(resultTableJSON);
        assertEquals(expectedTableJSON, resultTableJSON);

    }

    /**
     * Format the JSON for pretty print
     * 
     * @param tableInfoJSON
     * @return the prettified JSON String, or the same string if parse error
     *         occurs
     */
    private String prettify(String tableInfoJSON) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object readValue = mapper.readValue(tableInfoJSON, Object.class);
            tableInfoJSON = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(readValue);
        }
        catch (IOException e) {
            logger.error("JSON Parsing", e);
        }

        return tableInfoJSON;
    }

}
