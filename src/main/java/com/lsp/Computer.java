package com.lsp;

/**
 * Created by lsp on 28.10.15.
 */
public class Computer extends DBRecord{
    private static String[] names=new String[]{"name", "criptoPro"};
    private static String table=new String("Computers");

    private User user;

    public Computer(String login) {
        super(names, login);
        super.table=table;
    }
    public Computer(String login, User user) {
        super(names, login);
        super.table=table;
        this.user=user;
    }

    public Computer(String[] names, String[] fields) {
        super(names, fields);
        super.table=table;
    }

    public Computer() {
        super(names);
        super.table=table;
    }

    public Computer(int id, String login) {
        super(names, id, login);
        super.table=table;
    }

    public Computer(int id, String[] fields, User user) {
        super(names, id, fields);
        super.table=table;
        this.user=user;
    }

    public Computer(int id) {
        super(names, id);
        super.table=table;
    }


    public String getName() {
        return fields[0];
    }

    public String getCriptoPro() {
        return fields[1];
    }

    public void setCriptoPro(String criptoPro) {
        this.fields[1] = criptoPro;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
