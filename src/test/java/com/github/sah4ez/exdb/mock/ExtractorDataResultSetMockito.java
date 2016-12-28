package com.github.sah4ez.exdb.mock;
import org.mockito.Mockito;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
/**
 * Created by pc999 on 09.12.16.
 */
public class ExtractorDataResultSetMockito {
    public ResultSet resultSet(){
        ResultSet rs = null;
        LocalDateTime time =LocalDateTime.of(2016, 12, 1, 1,1,1,0);
        try{
            rs = Mockito.mock(ResultSet.class);
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
            Mockito.when(rs.getInt("exception")).thenThrow(SQLException.class);
            Mockito.when(rs.getTimestamp("exception")).thenThrow(SQLException.class);
            Mockito.when(rs.getString("exception")).thenThrow(SQLException.class);
            Mockito.when(rs.getBoolean("exception")).thenThrow(SQLException.class);
            Mockito.when(rs.getFloat("exception")).thenThrow(SQLException.class);
            Mockito.when(rs.findColumn("name11")).thenThrow(SQLException.class);
            ResultSetMetaData rsmd = metaData();
            Mockito.when(rs.getMetaData()).thenReturn(rsmd);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }
    private ResultSetMetaData metaData(){
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
