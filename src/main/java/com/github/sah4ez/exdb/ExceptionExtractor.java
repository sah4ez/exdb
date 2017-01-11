package com.github.sah4ez.exdb;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 1/11/2017.
 */
public class ExceptionExtractor {
    private List<SQLException> exceptions;
    private boolean isWriteLog;

    public ExceptionExtractor() {
        exceptions = new ArrayList<>();
        isWriteLog = false;
    }

    public boolean isWriteLog() {
        return isWriteLog;
    }

    public void setWriteLog(boolean value) {
        this.isWriteLog = value;
    }

    public void add(SQLException exception) {
        exceptions.add(exception);

        if (isWriteLog()) {
            exception.printStackTrace();
        }
    }

    public void removeException(int index) {
        exceptions.remove(index);
    }

    public void removeException(SQLException exception) {
        exceptions.remove(exception);
    }

    public void clearExceptions() {
        exceptions.clear();
    }

    public SQLException getExceptionFromIndex(int index) {
        return exceptions.get(index);
    }

    public List<SQLException> getExceptions() {
        return exceptions;
    }

    public SQLException getLastException() {
        return exceptions.get(exceptions.size()-1);
    }

    public boolean isExceptions() {
        if (exceptions.size() > 0) {
            return true;
        } else return false;
    }
}
