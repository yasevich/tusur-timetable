/**
 * Copyrights (C) 2012 Vyacheslav Yasevich.
 * All rights reserved.
 */

package com.synergy.sql;

/**
 * Provides SQL columns information. To return SQL statement use
 * {@link #toSqlStatement()}.
 * 
 * @author Vyacheslav Yasevich
 */
public class SqlColumnInfo {
    private static final String MODIFIER_PRIMARY_KEY = "PRIMARY KEY";
    private static final String MODIFIER_NOT_NULL = "NOT NULL";
    private static final String MODIFIER_DEFAULT_VALUE = "DEFAULT";
    
    private String mName;
    private SqlDataType mType;
    private boolean mPrimaryKey = false;
    private boolean mNotNull = false;
    private String mDefaultValue;
    
    /**
     * Creates new instance of this class with specified parameters.
     * 
     * @param name the name of a column
     * @param type the type of a column
     */
    public SqlColumnInfo(String name, SqlDataType type) {
        if (name == null) {
            throw new NullPointerException("The name of a column should not be null.");
        }
        if (type == null) {
            throw new NullPointerException("The type of a column should not be null.");
        }
        mName = name;
        mType = type;
    }
    
    /**
     * Creates new instance of this class with specified parameters.
     * 
     * @param name    the name of a column
     * @param type    the type of a column
     * @param notNull <code>true</code> if a value of a column should be
     *                <code>NOT NULL</code>
     */
    public SqlColumnInfo(String name, SqlDataType type, boolean notNull) {
        this(name, type);
        mNotNull = notNull;
    }
    
    /**
     * Creates new instance of this class with specified parameters.
     * 
     * @param name         the name of a column
     * @param type         the type of a column
     * @param notNull      <code>true</code> if a value of a column should be
     *                     <code>NOT NULL</code>
     * @param defaultValue the default value
     */
    public SqlColumnInfo(String name, SqlDataType type, boolean notNull, String defaultValue) {
        this(name, type, notNull);
        mDefaultValue = defaultValue;
    }
    
    /**
     * Creates new instance of this class with specified parameters.
     * 
     * @param name       the name of a column
     * @param type       the type of a column
     * @param notNull    <code>true</code> if values of a column should be
     *                   <code>NOT NULL</code>
     * @param primaryKey <code>true</code> if values of a column are part of a
     *                   <code>PRIMARY KEY</code>
     */
    public SqlColumnInfo(String name, SqlDataType type, boolean notNull, boolean primaryKey) {
        this(name, type, notNull);
        mPrimaryKey = primaryKey;
    }
    
    /**
     * Sets whether the values of a column are part of a <code>PRIMARY
     * KEY</code>. 
     * 
     * @param primaryKey <code>true</code> if values of a column are part of a
     *                   <code>PRIMARY KEY</code>
     */
    public void setPrimaryKey(boolean primaryKey) {
        mPrimaryKey = primaryKey;
    }
    
    /**
     * Sets whether values of a column should be <code>NOT NULL</code>
     * 
     * @param notNull <code>true</code> if values of a column should be
     *                <code>NOT NULL</code>
     */
    public void setNotNull(boolean notNull) {
        mNotNull = notNull;
    }
    
    /**
     * Sets default value for the column.
     * 
     * @param defaultValue the default value
     */
    public void setDefaultValue(String defaultValue) {
        mDefaultValue = defaultValue;
    }
    
    /**
     * Returns SQL statement of a column's description.
     * 
     * @return the SQL statement
     */
    public String toSqlStatement() {
        StringBuilder sb = new StringBuilder().append(mName).append(' ').append(mType.name());
        if (mPrimaryKey) {
            sb.append(' ').append(MODIFIER_PRIMARY_KEY);
        } else if (mNotNull) {
            sb.append(' ').append(MODIFIER_NOT_NULL);
        }
        if (mDefaultValue != null) {
            sb.append(' ').append(MODIFIER_DEFAULT_VALUE).append(' ').append(mDefaultValue);
        }
        return sb.toString();
    }
}
