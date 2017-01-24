package com.github.sah4ez.exdb;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by aleksandr on 03.01.17.
 */
public class ProcedureExecutor {
    private static DataSource dataSource; // = DataBaseHelper.getDataSource();
    private CallableStatement proc = null;
    private Connection conn = null;
    private String scheme = "";
    private String procedure = "";
    private int outParameter = Types.NULL;
    volatile private ArrayList<Object> inParameters = new ArrayList<>();
    private Object result = null;

    public ProcedureExecutor(DataSource dataSource, String scheme, String procedure) {
        this.scheme = scheme;
        this.procedure = procedure;
        this.setDataSource(dataSource);
    }

    public ProcedureExecutor(String scheme, String procedure, Connection conn) {
        this.scheme = scheme;
        this.procedure = procedure;
        this.conn = conn;
    }

    protected static DataSource getDataSource() {
        return dataSource;
    }

    public ProcedureExecutor setDataSource(DataSource ds) {
        ProcedureExecutor.dataSource = ds;
        return this;
    }

    public void outType(int type) {
        this.outParameter = type;
    }

    public void inputParameters(Object... inParameter) {
        Arrays.stream(inParameter).forEach(c -> this.inParameters.add(c));
    }

    public void executeThrow(Class<? extends Throwable> projectErrorsClass) throws Throwable {
        try {
            perform();
        } catch (SQLException e) {
            e.printStackTrace();
            throw projectErrorsClass.newInstance();
        }
    }

    public void execute() {
        try {
            perform();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void perform() throws SQLException {
        String sql = String.format("{ %s call %s.%s(%s)}"
                , getOutParam()
                , this.scheme
                , this.procedure
                , getInParam());
        createConnection();
        proc = conn.prepareCall(sql);
        setOutParameters();
        setInParameters();
        executeQuery();
        //TODO:Реализовать автоматическое закрытие(Сейчас ResultSet закрывается)
        //closeConnectionAndStatement();
    }

    private void setInParameters() {
        final int[] number = {1};
        if (outParameter != Types.NULL && outParameter != Types.OTHER)
            number[0] = 2;
        inParameters.forEach(inParameter -> {
            try {
                setInObjectParameter(number[0], inParameter);
                number[0]++;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void setInObjectParameter(int number, Object inParameter) throws SQLException {
        if (inParameter instanceof Integer) {
            proc.setInt(number, ((Integer) inParameter));
        }else if (inParameter instanceof String) {
            proc.setString(number, ((String) inParameter));
        }else if (inParameter instanceof Boolean) {
            proc.setBoolean(number, ((Boolean) inParameter));
        }else if (inParameter instanceof Float) {
            proc.setFloat(number, ((Float) inParameter));
        }else if (inParameter == null) {
            proc.setNull(number, Types.NULL);
        }else if (inParameter instanceof Timestamp) {
            proc.setTimestamp(number, ((Timestamp) inParameter));
        }else if (inParameter instanceof Array){
            proc.setArray(number, ((Array) inParameter));
        }
    }

    private void setOutParameters() {
        if (outParameter != 0 && outParameter != Types.OTHER) {
            try {
                proc.registerOutParameter(1, outParameter);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void executeQuery() throws SQLException {
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
        String out = "";
        if (outParameter != 0 && outParameter != Types.OTHER)
            out = "? =";
        return out;
    }

    public String getInParam() {
        final String[] in = {""};
        int size = inParameters.size();
        final int[] counter = {1};
        inParameters.forEach(inParameter -> {
            in[0] += "?";
            if (counter[0] != size)
                in[0] += ",";
            counter[0]++;
        });
        return in[0];
    }

    private void createConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource is Null! Please set DataSource");
        }
        conn = dataSource.getConnection();
    }

    public void closeConnectionAndStatement() {
        try {
            proc.close();
            conn.close();
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

    public Array createArray(String typeName, Object[] objects){
        Array array = null;
        try {
            Connection conn = dataSource.getConnection();
            array = conn.createArrayOf(typeName, objects);
        } catch (SQLException e) {
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
}
