package com.github.sah4ez.exdb;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Calendar;

/**
 * Created by aleksandr on 03.01.17.
 */
public class ProcedureExecutorTest extends Assert {

    CallableStatement proc = Mockito.mock(CallableStatement.class);
    Connection conn = Mockito.mock(Connection.class);

    DataSource dataSource = Mockito.mock(DataSource.class);
    DataSource dataSourceException = Mockito.mock(DataSource.class);

    ProcedureExecutor executor;

    @Before
    public void setUp() throws Exception {
        Mockito.when(conn.prepareCall(Mockito.anyString())).thenReturn(proc);
        Mockito.when(dataSourceException.getConnection()).thenThrow(SQLException.class);
        Mockito.when(dataSource.getConnection()).thenReturn(conn);
        executor = new ProcedureExecutor(dataSource, "scheme1", "procedure1");
    }

    @Test
    public void testQueryParameter() {
        assertEquals("scheme1", executor.getScheme());
        assertEquals("procedure1", executor.getProcedure());
        assertNotNull(ProcedureExecutor.getDataSource());
    }

    @Test
    public void setDataSource() throws Exception {
        executor.setDataSource(dataSource);
        assertNotNull(ProcedureExecutor.getDataSource());
    }

    @Test
    public void outType() throws Exception {
        executor.outType(Types.INTEGER);
        assertEquals(Types.INTEGER, executor.getOutParameter());
    }

    @Test
    public void inputParameters() throws Exception {
        executor.inputParameters("h", 1, 1.0f);
        assertEquals(3, executor.getInParameters().size());
        assertEquals("h", executor.getInParameters().get(0));
        assertEquals(1, executor.getInParameters().get(1));
        assertEquals(1.0f, (Float) executor.getInParameters().get(2), 0.01F);
    }

    @Test
    public void executeThrowWithoutException() throws Throwable {

        ProcedureExecutor executor =
                new ProcedureExecutor(dataSource, "scheme1", "procedure1");
        executor.outType(Types.INTEGER);
        executor.inputParameters(1);

        Mockito.when(dataSource.getConnection()).thenReturn(conn);
        executor.executeThrow(NullPointerException.class);
        Mockito.verify(proc).execute();
    }

    @Test(expected = NullPointerException.class)
    public void executeThrowWithException() throws Throwable {

        ProcedureExecutor executor =
                new ProcedureExecutor(dataSourceException, "scheme1", "procedure1");
        executor.outType(Types.INTEGER);
        executor.inputParameters(1);
        executor.executeThrow(NullPointerException.class);
        Mockito.verify(dataSourceException).getConnection();
    }

    @Test
    public void testExecute() throws Exception {
        Timestamp timestamp = Timestamp.from(Calendar.getInstance().toInstant().minusMillis(1));
        Array array = Mockito.mock(Array.class);
        byte[] bytes = "bb".getBytes();
        executor.outType(Types.INTEGER);
        executor.inputParameters(1, "h", false, 1.0f, null, timestamp, array, bytes, new BigDecimal(10));
        executor.execute();
    }

    @Test
    public void executeWithException() throws Exception {
        executor.setDataSource(dataSourceException);
        executor.outType(Types.INTEGER);
        executor.inputParameters(1);
        executor.execute();
    }

    @Test(expected = SQLException.class)
    public void testCreateConnection() throws Throwable {
        executor.setDataSource(null);
        executor.executeThrow(SQLException.class);
    }

    @Test
    public void closeConnectionAndStatement() throws Exception {
        executor.setDataSource(dataSource);
        executor.execute();
        executor.closeConnectionAndStatement();
        Mockito.verify(conn).close();
    }

    @Test
    public void getResultSetNull() throws Exception {
        assertNull(executor.getResultSet());
    }

    @Test
    public void getResultSet() throws Exception {
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        Mockito.when(proc.executeQuery()).thenReturn(resultSet);
        executor.outType(Types.OTHER);
        executor.execute();
        assertEquals(resultSet, executor.getResultSet());
    }

    @Test
    public void getIntResultNull() throws Exception {
        assertEquals(0, executor.getIntResult().intValue());
    }

    @Test
    public void getIntResult() throws Exception {
        Mockito.when(proc.getObject(1)).thenReturn(1);
        executor.outType(Types.INTEGER);
        executor.execute();
        assertEquals(1, executor.getIntResult().intValue());
    }

    @Test
    public void getStringResultNull() throws Exception {
        assertEquals("", executor.getStringResult());
    }

    @Test
    public void getStringResult() throws Exception {
        Mockito.when(proc.getObject(1)).thenReturn("hello");
        executor.outType(Types.VARCHAR);
        executor.execute();
        assertEquals("hello", executor.getStringResult());
    }

    @Test
    public void getBooleanResultNull() throws Exception {
        assertNull(executor.getBooleanResult());
    }

    @Test
    public void getBooleanResult() throws Exception {
        Mockito.when(proc.getObject(1)).thenReturn(true);
        executor.outType(Types.BOOLEAN);
        executor.execute();
        assertTrue(executor.getBooleanResult());
    }

    @Test
    public void getFloatResultNull() throws Exception {
        assertNull(executor.getFloatResult());
    }

    @Test
    public void getFloatResult() throws Exception {
        Mockito.when(proc.getObject(1)).thenReturn(1.0f);
        executor.outType(Types.FLOAT);
        executor.execute();
        assertEquals(1.0f, executor.getFloatResult(), 0.01f);
    }

    @Test
    public void getTimeStampResultNull() throws Exception {
        assertNull(executor.getTimeStampResult());
    }

    @Test
    public void getTimeStampResult() throws Exception {
        Timestamp timestamp = Timestamp.from(Calendar.getInstance().toInstant());
        Mockito.when(proc.getObject(1)).thenReturn(timestamp);
        executor.outType(Types.TIMESTAMP);
        executor.execute();
        assertEquals(timestamp, executor.getTimeStampResult());
    }

    @Test
    public void getConn() throws Exception {
        ProcedureExecutor executor =
                new ProcedureExecutor("shceme1", "procedure1", conn);
        assertEquals(conn, executor.getConn());
    }

    @Test
    public void testSetOutputParameterException() throws SQLException {
        Mockito.doThrow(SQLException.class)
                .when(proc)
                .registerOutParameter(1, 4);
        executor.outType(Types.INTEGER);
        executor.execute();
        Mockito.verify(proc).registerOutParameter(1,4);
    }

    @Test
    public void testSetInParameterException() throws Exception {
        Mockito.doThrow(SQLException.class)
                .when(proc)
                .setInt(2, 1);
        executor.outType(Types.INTEGER);
        executor.inputParameters(1);
        executor.execute();
        Mockito.verify(proc).setInt(2,1);
    }

    @Test
    public void testCloseConn() throws Exception {
        Mockito.doThrow(SQLException.class)
                .when(proc)
                .close();
        executor.outType(Types.INTEGER);
        executor.inputParameters(1);
        executor.execute();
        executor.closeConnectionAndStatement();
        Mockito.verify(proc).close();
    }

    @Test
    public void testCreateArray() throws Exception{
        DataSource dataSource = Mockito.mock(DataSource.class);
        Connection connection = Mockito.mock(Connection.class);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        String[] array = new String[] {"1", "2"};
        ProcedureExecutor executor = new ProcedureExecutor(dataSource, "", "");
        executor.createArray("varchar", array);
        Mockito.verify(connection).createArrayOf(Mockito.anyString(), Mockito.any(String[].class));
    }

    @Test
    public void testCreateArrayException() throws Exception{
        DataSource dataSource = Mockito.mock(DataSource.class);
        Mockito.when(dataSource.getConnection()).thenThrow(SQLException.class);
        ProcedureExecutor executor = new ProcedureExecutor(dataSource, "", "");
        executor.createArray("varchar", new Object[]{});
    }

    @Test
    public void testNullTypes() throws SQLException {
        executor.outType(Types.NULL);
        executor.execute();
        executor.closeConnectionAndStatement();
        Mockito.verify(proc).execute();
    }

    @Test
    public void setAutoCommit(){
        executor.setAutoCommit(true);
    }

    @Test
    public void testCommit() throws SQLException {
        Mockito.when(conn.isClosed()).thenReturn(false);
        executor.execute();
        executor.commit();
        Mockito.verify(conn).commit();
    }
}