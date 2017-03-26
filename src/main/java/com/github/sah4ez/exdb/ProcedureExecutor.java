package com.github.sah4ez.exdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringJoiner;

/**
 * Created by aleksandr on 03.01.17.
 */
public class ProcedureExecutor {

    private static final Logger log = LoggerFactory.getLogger(ProcedureExecutor.class);

    private final DataSource dataSource;
    private final Connection conn;
    private String scheme = "";
    private String procedure = "";
    private int outParameter = Types.OTHER;
    volatile private ArrayList<Object> inParameters = new ArrayList<>();
    private Object result = null;

    public ProcedureExecutor(DataSource dataSource, String scheme, String procedure) {
        this.scheme = scheme;
        this.procedure = procedure;
        this.dataSource = dataSource;
        conn = createConnection();
    }

    public ProcedureExecutor(String scheme, String procedure, Connection conn) {
        this.scheme = scheme;
        this.procedure = procedure;
        this.conn = conn;
        dataSource = null;
    }

    public void outType(int type) {
        outParameter = type;
    }

    public void inputParameters(Object... inParameter) {
        inParameters.addAll(Arrays.asList(inParameter));
    }

    public void executeThrow(Class<? extends Throwable> projectErrorsClass) throws Throwable {
        try {
            perform();
        } catch (SQLException e) {
            log.error("Executing SQL query with exception: \n {}", e.toString());
            throw projectErrorsClass.newInstance();
        }
    }

    public void execute() {
        try {
            perform();
        } catch (SQLException e) {
            log.error(e.toString());
        }
    }

    private void perform() throws SQLException {
        String sql = String.format("{ %s call %s.%s(%s)}"
                , getOutParam()
                , this.scheme
                , this.procedure
                , getInParam());

        if (dataSource == null) {
            log.error("DataSource is NULL");
            return;
        }

        try (CallableStatement proc = conn.prepareCall(sql)) {
            setOutParameters(proc);
            setInParameters(proc);
            executeQuery(proc);
        }
    }

    private void setInObjectParameter(CallableStatement proc, int number, Object inParameter) throws SQLException {
        if (inParameter instanceof Integer) {
            proc.setInt(number, ((Integer) inParameter));
        } else if (inParameter instanceof String) {
            proc.setString(number, ((String) inParameter));
        } else if (inParameter instanceof Boolean) {
            proc.setBoolean(number, ((Boolean) inParameter));
        } else if (inParameter instanceof Float) {
            proc.setFloat(number, ((Float) inParameter));
        } else if (inParameter == null) {
            proc.setNull(number, Types.NULL);
        } else if (inParameter instanceof Timestamp) {
            proc.setTimestamp(number, ((Timestamp) inParameter));
        } else if (inParameter instanceof Array) {
            proc.setArray(number, ((Array) inParameter));
        } else if (inParameter instanceof byte[]) {
            ByteArrayInputStream bais = new ByteArrayInputStream(((byte[]) inParameter));
            proc.setBinaryStream(number, bais, ((byte[]) inParameter).length);
        } else if (inParameter instanceof BigDecimal) {
            proc.setBigDecimal(number, ((BigDecimal) inParameter));
        } else if (inParameter instanceof Long) {
            proc.setLong(number, ((Long) inParameter));
        }
    }

    private void setOutParameters(CallableStatement proc) {
        if (outParameter != 0 && outParameter != Types.OTHER) {
            try {
                proc.registerOutParameter(1, outParameter);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void executeQuery(CallableStatement proc) throws SQLException {
        switch (outParameter) {
            case (Types.NULL):
                proc.execute();
                break;
            case (Types.OTHER):
                result = proc.executeQuery();
                break;
            default:
                proc.execute();
                result = proc.getObject(1);
        }
    }

    private String getOutParam() {
        return outParameter != 0 && outParameter != Types.OTHER ? "? =" : "";
    }

    private String getInParam() {
        StringJoiner joiner = new StringJoiner(", ");
        for (int i = 0; i < inParameters.size(); i++) {
            joiner.add("?");
        }
        return joiner.toString();
    }

    private Connection createConnection() {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            log.error(Arrays.toString(e.getStackTrace()));
        }catch (NullPointerException e){
            log.debug(Arrays.toString(e.getStackTrace()));
        }
        return conn;
    }

    /**
     * Instead this to use {@code closeConnection} before instance software shutdown.
     */
    @Deprecated
    public void closeConnectionAndStatement() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection(){
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getResultSet() {
        if (result != null && outParameter == Types.OTHER)
            return (ResultSet) result;
        return null;
    }

    public Integer getIntResult() {
        if (result != null && outParameter == Types.INTEGER)
            return (Integer) result;
        return 0;
    }

    public BigDecimal getBigIntResult() {
        if (result != null && outParameter == Types.BIGINT)
            return (BigDecimal) result;
        return new BigDecimal(0);
    }

    public Long getLongResult() {
        if (result != null && outParameter == Types.BIGINT)
            return (Long) result;
        return 0L;
    }

    public String getStringResult() {
        if (result != null && outParameter == Types.VARCHAR)
            return (String) result;
        return "";
    }

    public Boolean getBooleanResult() {
        if (result != null && outParameter == Types.BOOLEAN)
            return (Boolean) result;
        return null;
    }

    public Float getFloatResult() {
        if (result != null && outParameter == Types.FLOAT)
            return (Float) result;
        return null;
    }

    public Timestamp getTimeStampResult() {
        if (result != null && outParameter == Types.TIMESTAMP)
            return (Timestamp) result;
        return null;
    }

    public Array createArray(String typeName, Object[] objects) {
        Array array = null;
        try {
            array = conn.createArrayOf(typeName, objects);
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
        return array;
    }

    public Connection getConn() {
        return conn;
    }

    public String getScheme() {
        return scheme;
    }

    public String getProcedure() {
        return procedure;
    }

    public int getOutParameter() {
        return outParameter;
    }

    public ArrayList<Object> getInParameters() {
        return inParameters;
    }

    private void setInParameters(CallableStatement proc) {
        final int[] number = {1};
        if (outParameter != Types.NULL && outParameter != Types.OTHER)
            number[0] = 2;
        inParameters.forEach(inParameter -> {
            try {
                setInObjectParameter(proc, number[0], inParameter);
                number[0]++;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void setAutoCommit(boolean b) {
        try {
            conn.setAutoCommit(b);
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void commit() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
