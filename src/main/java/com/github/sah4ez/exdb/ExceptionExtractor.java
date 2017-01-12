package com.github.sah4ez.exdb;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 1/11/2017.
 */
public class ExceptionExtractor extends ArrayList<SQLException> {
    private boolean isWriteLog;

    public ExceptionExtractor() {
        isWriteLog = false;

    }

    public boolean isWriteLog() {
        return isWriteLog;
    }

    public void setWriteLog(boolean value) {
        this.isWriteLog = value;
    }

    @Override
    public boolean add(SQLException exception) {
        super.add(exception);

        if (isWriteLog()) {
            exception.printStackTrace();
        }
        return false;
    }

    public SQLException getLastException() {
        return super.get(super.size()-1);
    }

    public boolean isExceptions() {
        if (super.size() > 0) {
            return true;
        } else return false;
    }
}
