package com.github.sah4ez.exdb;

import com.github.sah4ez.exdb.mock.ExtractorDataResultSetMockito;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
/**
 * Test for ExtractorDataResultSet. Used class ExtractorDataResultSetMockito
 * for generation mock of ResultSet with default parameters.
 *
 * @author sah4ez
 */
public class ExtractorDataResultSetTest extends Assert {
    private Extractor extractor = null;
    @Before
    public void setUp() throws Exception {
        ExtractorDataResultSetMockito mockito = new ExtractorDataResultSetMockito();
        extractor = new Extractor(mockito.resultSet());
    }
    @Test
    public void getInt() throws Exception {
        assertEquals(1, extractor.getInt("name").intValue());
        assertEquals(0, extractor.getInt("exception").intValue());
        assertEquals(0, extractor.getInt("name12").intValue());
    }
    @Test
    public void getIntNull() throws Exception {
        assertEquals(1, extractor.getIntNull("name").intValue());
        assertEquals(0, extractor.getIntNull("exception").intValue());
        assertNull(extractor.getIntNull("name12"));
    }
    @Test
    public void getString() throws Exception {
        assertEquals("string", extractor.getString("name"));
        assertEquals("", extractor.getString("exception"));
        assertEquals("", extractor.getString("name12"));
    }
    @Test
    public void getStringNull() throws Exception {
        assertEquals("string", extractor.getStringNull("name"));
        assertEquals("", extractor.getStringNull("exception"));
        assertNull(extractor.getStringNull("name12"));
    }
    @Test
    public void getLocalDateTime() throws Exception {
        LocalDateTime time = LocalDateTime.of(2016, 12, 1, 1, 1, 1, 0);
        assertEquals(time, extractor.getLocalDateTime("name"));
        assertEquals(Timestamp.valueOf(LocalDateTime.MIN).toLocalDateTime(), extractor.getLocalDateTime("exception"));
        assertEquals(Timestamp.valueOf(LocalDateTime.MIN).toLocalDateTime(), extractor.getLocalDateTime("name12"));
    }
    @Test
    public void getLocalDateTimeNull() throws Exception {
        LocalDateTime time = LocalDateTime.of(2016, 12, 1, 1, 1, 1, 0);
        assertEquals(time, extractor.getLocalDateTimeNull("name"));
        assertEquals(Timestamp.valueOf(LocalDateTime.MIN).toLocalDateTime(), extractor.getLocalDateTimeNull("exception"));
        assertNull(extractor.getLocalDateTimeNull("name12"));
    }
    @Test
    public void getValue() throws Exception {
        assertEquals("valuetext", extractor.getValue());
    }
    @Test
    public void getVariableId() throws Exception {
        assertEquals(1, extractor.getVariableId().intValue());
    }
    @Test
    public void getFloat() throws Exception {
        assertEquals(1.0f, extractor.getFloat("name"), 0.01f);
        assertEquals(0.0f, extractor.getFloat("exception"), 0.01f);
        assertEquals(0.0f, extractor.getFloat("name12"), 0.01f);
    }
    @Test
    public void getFloatNull() throws Exception {
        assertEquals(1.0f, extractor.getFloatNull("name"), 0.01f);
        assertEquals(0.0f, extractor.getFloatNull("exception"), 0.01f);
        assertNull(extractor.getFloatNull("name12"));
    }
    @Test
    public void getBoolean() throws Exception {
        assertTrue(extractor.getBoolean("name"));
        assertFalse(extractor.getBoolean("exception"));
        assertFalse(extractor.getBoolean("name12"));
    }
    @Test
    public void getBooleanNull() throws Exception {
        assertTrue(extractor.getBooleanNull("name"));
        assertFalse(extractor.getBooleanNull("exception"));
        assertNull(extractor.getBooleanNull("name12"));
    }
    @Test
    public void testNotFoundStringColumn() {
        try {
            extractor.check("fff");
        } catch (Extractor.NotFoundColumnResultSetException e) {
            assertEquals("Column with name 'fff' not exist in ResultSet.", e.getMessage());
        }
    }
    @Test
    public void testCheckColumnName() throws Extractor.NotFoundColumnResultSetException {
        extractor.check("name");
    }
    @Test(expected = Extractor.NotFoundColumnResultSetException.class)
    public void testCheckException() throws Extractor.NotFoundColumnResultSetException {
        extractor.check("name11");
    }
}
