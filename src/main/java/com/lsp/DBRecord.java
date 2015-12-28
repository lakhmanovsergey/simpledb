package com.lsp;

/**
 * Created by lsp on 27.10.15.
 */
public abstract class DBRecord {
    protected String[] names;
    protected String table;

    protected int id;
    protected String[] fields;
    protected boolean isDeleted=false;
    protected boolean isUpdated=false;

    protected static String getSqlSelect(String[] names, String table) {
        StringBuffer sql=new StringBuffer();
        sql.append("SELECT ");
        sql.append("id,");
        for (int i = 0; i < names.length; i++) {
            sql.append(names[i]);
            if(i!=(names.length-1)) sql.append(",");
        }
        sql.append(" FROM "+table+";");
        return sql.toString();
    }
    public DBRecord(String[] names, String[] fields) {
        this.names=names;
        this.id=0;
        this.fields=fields;
    }

    public DBRecord(String[] names, String field0) {
        this.names=names;
        this.id =0;
        this.fields=new String[names.length];
        this.fields[0] = field0;
    }

    public DBRecord(String[] names ) {
        this.names=names;
        this.id = 0;
        this.fields=new String[names.length];
    }
    public DBRecord(String[] names, int id, String field0) {
        this.names=names;
        this.id = id;
        this.fields=new String[names.length];
        this.fields[0] = field0;
    }

    public DBRecord(String[] names, int id, String[] fields) {
        this.names=names;
        this.id = id;
        this.fields = fields;
    }

    public DBRecord(String[] names, int id) {
        this.names=names;
        this.id = id;
        this.fields=new String[names.length];
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setIsUpdated(boolean isUpdated) {
        this.isUpdated = isUpdated;
    }

    protected String getSqlDelete() {
        return "DELETE FROM "+table+" WHERE id="+id+";";
    }

    protected String getSqlUpdate() {
        StringBuffer sql=new StringBuffer();
        sql.append("UPDATE "+table+" SET ");
        for (int i = 0; i < names.length; i++) {
            sql.append(names[i]+"=\""+fields[i]+"\"");
            if(i!=(names.length-1)) sql.append(",");
        }
        sql.append(" WHERE id="+id+";");
        return sql.toString();
    }

    protected String getSqlInsert() {
        StringBuffer sql=new StringBuffer();
        sql.append("INSERT INTO "+table+" SET ");
        for (int i = 0; i < names.length; i++) {
            sql.append(names[i]+"=\""+fields[i]+"\"");
            if(i!=(names.length-1)) sql.append(",");
        }
        sql.append(";");
        return sql.toString();
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append("Id=");stringBuffer.append(this.getId());stringBuffer.append(";");
        for (int i = 0; i <names.length ; i++) {
            stringBuffer.append(names[i]+"=");stringBuffer.append(fields[i]+";");
        }
        return stringBuffer.toString();
    }
}
