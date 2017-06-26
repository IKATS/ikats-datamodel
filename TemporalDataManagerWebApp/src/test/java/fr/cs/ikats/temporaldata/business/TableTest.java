package fr.cs.ikats.temporaldata.business;

import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fr.cs.ikats.temporaldata.business.TableInfo.DataLink;
import junit.framework.TestCase;

/**
 * JUnit tests on Table business resource
 */
public class TableTest extends TestCase {

    public final static String JSON_CONTENT_SAMPLE_1 = "{\"table_desc\":{\"title\":\"Discretized matrix\",\"desc\":\"This is a ...\"},\"headers\":{\"col\":{\"data\":[\"funcId\",\"metric\",\"min_B1\",\"max_B1\",\"min_B2\",\"max_B2\"],\"links\":null,\"default_links\":null},\"row\":{\"data\":[null,\"Flid1_VIB2\",\"Flid1_VIB3\",\"Flid1_VIB4\",\"Flid1_VIB5\"],\"default_links\":{\"type\":\"ts_bucket\",\"context\":\"processdata\"},\"links\":[null,{\"val\":\"1\"},{\"val\":\"2\"},{\"val\":\"3\"},{\"val\":\"4\"}]}},\"content\":{\"cells\":[[\"VIB2\",-50.0,12.1,1.0,3.4],[\"VIB3\",-5.0,2.1,1.0,3.4],[\"VIB4\",0.0,2.1,12.0,3.4],[\"VIB5\",0.0,2.1,1.0,3.4]]}}";
    
    @Test
    public void testLoadJSON()
    { 
        try { 
            // V3: table first version (before 15/06/17)
            String jsonContent= TableTest.JSON_CONTENT_SAMPLE_1;
            
            // Prepare expected config of ObjectMapper.
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
            objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, false);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
            objectMapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS,  false);
            objectMapper.setSerializationInclusion(Include.NON_NULL);
            TableInfo testedTable = objectMapper.readValue( jsonContent, TableInfo.class);
            System.out.println( testedTable );
            
            
             String written1 = new ObjectMapper().writeValueAsString(testedTable);
             System.out.println( "avec null: " + written1);
             String written2 = objectMapper.writeValueAsString(testedTable);
             System.out.println( "sans null: " + written2);
             System.out.println( "ref input: " + jsonContent);
            
            // non-exhaustive test
            //
            // tests TableDesc init
            assertEquals( testedTable.table_desc.title, "Discretized matrix");
            assertEquals( testedTable.table_desc.desc, "This is a ...");
            // tests TableHeaders : initialized columns
            assertEquals( testedTable.headers.col.data.size(), 6);
            assertEquals( testedTable.headers.col.data.get(1), "metric");
            assertEquals( testedTable.headers.col.links, null);
            assertEquals( testedTable.headers.col.default_links, null);
            // tests TableHeaders : initialized rows
            assertEquals( testedTable.headers.row.data.size(), 5);
            assertEquals( testedTable.headers.row.data.get(0), null);
            assertEquals( testedTable.headers.row.data.get(1), "Flid1_VIB2");
            assertEquals( testedTable.headers.row.default_links.type, "ts_bucket");
            assertEquals( testedTable.headers.row.default_links.context, "processdata");
            assertEquals( testedTable.headers.row.links.size(), 5);
            assertEquals( testedTable.headers.row.links.get(0), null);
            assertTrue( testedTable.headers.row.links.get(1) instanceof DataLink);
            assertEquals( testedTable.headers.row.links.get(1).val, "1");
            
            // tests TableContent (... just adding a few asserts)
            assertEquals( testedTable.content.cells.size(), 4 );
            for (List rowCells : testedTable.content.cells) {
                assertEquals( rowCells.size(), 5 );               
            }
            assertEquals( testedTable.content.cells.get(1).get(0), "VIB3" );
            assertEquals( testedTable.content.cells.get(1).get(1), -5.0 ); 
            assertEquals( testedTable.content.links, null);
            assertEquals( testedTable.content.default_links, null);
        }
        catch (Exception e) {
            fail("Failed test");
            e.printStackTrace();
        }
    }
    
    @Test
    public void testWriteJSON()
    { 
        try { 
            // TODO
        }
        catch (Exception e) {
            fail("Failed test");
            System.err.println(e);
            e.printStackTrace();
        }
   
    }
}
