package com.github.sah4ez.exdb.mock;

import org.mockito.Mockito;

import javax.sql.rowset.serial.SerialBlob;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;

/**
 * Created by pc999 on 09.12.16.
 */
public class ExtractorDataResultSetMockito {
    public ResultSet resultSet() {
        ResultSet rs = null;
        Array array = Mockito.mock(Array.class);
        String[] data = new String[]{"1", "2", "3", "4"};
        LocalDateTime time = LocalDateTime.of(2016, 12, 1, 1, 1, 1, 0);
        byte[] test = new byte[]{ };
        try {
            String hello = "hello world";
            rs = Mockito.mock(ResultSet.class);
            Mockito.when(array.getArray()).thenReturn(data);
            Mockito.when(rs.getInt("name")).thenReturn(1);
            Mockito.when(rs.getBoolean("name")).thenReturn(true);
            Mockito.when(rs.getString("name")).thenReturn("string");
            Mockito.when(rs.getFloat("name")).thenReturn(1.0f);
            Mockito.when(rs.getTimestamp("name")).thenReturn(Timestamp.valueOf(time));
            Mockito.when(rs.getString("valuetext")).thenReturn("valuetext");
            Mockito.when(rs.getInt("variableid")).thenReturn(1);
            Mockito.when(rs.wasNull()).thenReturn(false).thenReturn(true);
            Mockito.when(rs.findColumn("name")).thenReturn(1);
            Mockito.when(rs.findColumn("valuetext")).thenReturn(1);
            Mockito.when(rs.findColumn("variableid")).thenReturn(1);
            Mockito.when(rs.getBytes("name")).thenReturn(hello.getBytes());
            Mockito.when(rs.getBlob("name")).thenReturn(new SerialBlob(hello.getBytes()));
            Mockito.when(rs.getInt("exception")).thenThrow(SQLException.class);
            Mockito.when(rs.getTimestamp("exception")).thenThrow(SQLException.class);
            Mockito.when(rs.getString("exception")).thenThrow(SQLException.class);
            Mockito.when(rs.getBoolean("exception")).thenThrow(SQLException.class);
            Mockito.when(rs.getFloat("exception")).thenThrow(SQLException.class);
            Mockito.when(rs.findColumn("name11")).thenThrow(SQLException.class);
            Mockito.when(rs.getArray("name")).thenReturn(array);
            Mockito.when(rs.getArray("exception")).thenThrow(SQLException.class);
            Mockito.when(rs.getBytes("exception")).thenThrow(SQLException.class);
            Mockito.when(rs.getBlob("exception")).thenThrow(SQLException.class);
            ResultSetMetaData rsmd = metaData();
            Mockito.when(rs.getMetaData()).thenReturn(rsmd);

            Mockito.when(rs.getBigDecimal("name")).thenReturn(new BigDecimal(1));
            Mockito.when(rs.getBigDecimal("exception")).thenThrow(SQLException.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    private ResultSetMetaData metaData() {
        ResultSetMetaData rsmd = null;
        try {
            rsmd = Mockito.mock(ResultSetMetaData.class);
            Mockito.when(rsmd.getColumnCount()).thenReturn(4);
            Mockito.when(rsmd.getColumnName(1)).thenReturn("name");
            Mockito.when(rsmd.getColumnName(2)).thenReturn("name");
            Mockito.when(rsmd.getColumnName(3)).thenReturn("valuetext");
            Mockito.when(rsmd.getColumnName(4)).thenReturn("variableid");
        } catch (SQLException e) {
        }
        return rsmd;
    }
}
