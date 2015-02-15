/**
 * Copyrights (C) 2012 Vyacheslav Yasevich.
 * All rights reserved.
 */

package com.synergy.sql;

import java.util.Iterator;
import java.util.List;

/**
 * The static methods of this class help to build SQL queries.
 * 
 * @author Vyacheslav Yasevich
 */
public final class SqlQueryHelper {
    /**
     * Creates an SQL table with a given name, set of columns and <code>NOT
     * NULL</code> modifier as needed.
     * 
     * @param name    the name of a table
     * @param columns the columns set of a table
     * @return        SQL query for table creation
     */
    public static String createTable(String name, List<SqlColumnInfo> columns) {
        if (name == null) {
            throw new NullPointerException("The name of a database should not be null.");
        }
        if (columns == null) {
            throw new NullPointerException("The list of columns should not be null.");
        }
        if (columns.size() == 0) {
            throw new IllegalArgumentException("There should be at least one column in a table.");
        }
        
        final String CREATE_TABLE = "CREATE TABLE [name] ([columns])";
        String query = CREATE_TABLE.replace("[name]", name);
        StringBuilder columnsBuilder = new StringBuilder();
        Iterator<SqlColumnInfo> iterator = columns.iterator();
        columnsBuilder.append(iterator.next().toSqlStatement());
        while (iterator.hasNext()) {
            columnsBuilder.append(", ").append(iterator.next().toSqlStatement());
        }
        return query.replace("[columns]", columnsBuilder.toString());
    }
    
    /**
     * Creates an SQL query to drop a table if it exists.
     * 
     * @param name the name of a table to drop
     * @return     SQL query to drop table
     */
    public static String dropTableIfExists(String name) {
        if (name == null) {
            throw new NullPointerException("The name of a database should not be null.");
        }
        final String DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS [name]";
        return DROP_TABLE_IF_EXISTS.replace("[name]", name);
    }
    
    /**
     * Creates an SQL query for ALTER TABLE name ADD COLUMN column_def
     * 
     * @param name   the name of the table
     * @param column the definition of column
     * @return       the query
     */
    public static String alterTableAddColumn(String name, SqlColumnInfo column) {
        if (name == null) {
            throw new NullPointerException("The name of a table should not be null.");
        }
        if (column == null) {
            throw new NullPointerException("The column should not be null.");
        }
        final String ALTER_TABLE_ADD_COLUMN = "ALTER TABLE [name] ADD COLUMN [column]";
        return ALTER_TABLE_ADD_COLUMN
                .replace("[name]", name)
                .replace("[column]", column.toSqlStatement());
    }
    
    /**
     * Creates an SQL query for ALTER TABLE oldName ADD COLUMN newName
     * 
     * @param oldName the old name
     * @param newName the new name
     * @return        the query
     */
    public static String alterTableRename(String oldName, String newName) {
        if (oldName == null) {
            throw new NullPointerException("The old name of a table should not be null.");
        }
        if (newName == null) {
            throw new NullPointerException("The new name of a table should not be null.");
        }
        final String ALTER_TABLE_RENAME_TO = "ALTER TABLE %1$s RENAME TO %2$s";
        return String.format(ALTER_TABLE_RENAME_TO, oldName, newName);
    }
    
    /**
     * Creates an SQL query to select all from a table.
     * 
     * @param name the name of a source table
     * @return     SQL query to select data from a table
     */
    public static String selectAll(String name) {
        if (name == null) {
            throw new NullPointerException("The name of a table should not be null.");
        }
        final String SELECT_ALL = "SELECT * FROM [name]";
        return SELECT_ALL.replace("[name]", name);
    }
    
    /**
     * Creates an SQL query to insert or replace values in a table
     * 
     * @param tableName the name of the table
     * @param columns   the columns' names
     * @param values    the values
     * @return          SQL query
     */
    public static String insertOrReplace(String tableName, List<String> columns,
            List<String> values) {
        if (tableName == null) {
            throw new NullPointerException("The name of a table shoud not be null");
        }
        if (columns == null) {
            throw new NullPointerException("There are no columns specified.");
        }
        if (values == null) {
            throw new NullPointerException("There are no values specified.");
        }
        if (columns.size() == 0 || values.size() == 0 || columns.size() != values.size()) {
            throw new IllegalArgumentException("Columns or values mismatch.");
        }
        final String insertOrReplace = "INSERT OR REPLACE INTO %1$s ([columns]) VALUES ([values])";
        String query = String.format(insertOrReplace, tableName);
        return query.replace("[columns]", getCommaSeparatedArray(columns))
                .replace("[values]", getCommaSeparatedArray(values));
    }
    
    private static String getCommaSeparatedArray(List<String> values) { 
        StringBuilder result = new StringBuilder();
        Iterator<String> iterator = values.iterator();
        result.append(iterator.next());
        while (iterator.hasNext()) {
            result.append(", ").append(iterator.next());
        }
        return result.toString();
    }
}
