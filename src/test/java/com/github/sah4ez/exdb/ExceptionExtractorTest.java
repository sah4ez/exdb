package com.github.sah4ez.exdb;

import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by User on 1/11/2017.
 */
public class ExceptionExtractorTest {
    private ExceptionExtractor exception;

    @Before
    public void setUp() {
        exception = new ExceptionExtractor();
    }

    @Test
    public void testWriteLog() {
        exception.setWriteLog(true);

        assertEquals(true, exception.isWriteLog());

        exception.add(new SQLException("Test"));

        exception.clearExceptions();
    }

    @Test
    public void removeException() {
        exception.add(new SQLException("Test"));
        exception.removeException(0);

        assertEquals(false, exception.isExceptions());

        exception.add(new SQLException("Test1"));
        exception.removeException(exception.getLastException());

        assertEquals(false, exception.isExceptions());
    }

    @Test
    public void clearExceptions() {
        fillException(10);

        exception.clearExceptions();

        assertEquals(false, exception.isExceptions());
    }

    @Test
    public void getExceptionFromIndex() {
        List<SQLException> exceptions = fillException(10);
        int targetIndex = 0;

        assertEquals(exceptions.get(targetIndex),
                exception.getExceptionFromIndex(targetIndex));
    }

    @Test
    public void getExceptions() {
        int count = 10;

        fillException(count);
        assertEquals(count, exception.getExceptions().size());
    }

    @Test
    public void getLastException() {
        List<SQLException> exceptions = fillException(10);

        assertEquals(exceptions.get(exceptions.size()-1),
                exception.getLastException());
    }

    @Test
    public void isExceptions() {
        exception.add(new SQLException("Test"));
        assertEquals(true, exception.isExceptions());
    }

    private List<SQLException> fillException(int max) {
        List<SQLException> list = new ArrayList<>();

        for (int i = 0; i < max; i++) {
            SQLException ex = new SQLException("Test " + i);
            list.add(ex);
            exception.add(ex);
        }

        return list;
    }
}