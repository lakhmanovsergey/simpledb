package com.lsp;

/**
 * Created by lsp on 28.10.15.
 */
public class User extends DBRecord{

    private static String[] names= new String[]{"login", "fullName",
            "OU", "position", "kabinet", "phone"};
    private static String table=new String("Users");
    public User(String field0) {
        super(names, field0);
        super.table=table;
    }

    public User(String[] fields) {
        super(names,fields);
        super.table=table;
    }

    public User() {
        super(names);
        super.table=table;
    }

    public User(int id, String field0) {
        super(names,id, field0);
        super.table=table;
    }

    public User(int id, String[] fields) {
        super(names,id, fields);
        super.table=table;
    }

    public User(int id) {
        super(names,id);
        super.table=table;
    }

    public String getLogin() {
        return fields[0];
    }
    public String getFullName() {
        return fields[1];
    }
    public String getOU() {
        return fields[2];
    }
    public String getPosition() {
        return fields[3];
    }
    public String getKabinet() {
        return fields[4];
    }
    public String getPhone() {
        return fields[5];
    }

    public void setFullName(String fullName) {
        this.fields[1] = fullName;
    }
    public void setOU(String fullName) {
        this.fields[2] = fullName;
    }
    public void setPosition(String fullName) {
        this.fields[3] = fullName;
    }
    public void setKabinet(String fullName) {
        this.fields[4] = fullName;
    }
    public void setPhone(String fullName) {
        this.fields[5] = fullName;
    }

    @Override
    public String getSqlDelete() {
        return super.getSqlDelete();
    }

    @Override
    public String getSqlUpdate() {
        return super.getSqlUpdate();
    }

    @Override
    public String getSqlInsert() {
        return super.getSqlInsert();
    }

    public static String getSqlSelect(){
        return DBRecord.getSqlSelect(names,table);
    }

    public static String[] getNames() {
        return names;
    }

    public static String getTable() {
        return table;
    }
}
