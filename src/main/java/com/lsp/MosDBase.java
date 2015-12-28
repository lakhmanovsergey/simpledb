package com.lsp;

import javax.naming.NamingException;
import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by
 */
public class MosDBase {
    public static void main(String[] args) throws SQLException, FileNotFoundException, NamingException {
        final MosDBase mosDBase = new MosDBase();
        //Заносим в график задание для потока управления событиями:
        //создание и отображение графического интерфейса пользователя этого приложения.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MosDBGui gui=new MosDBGui(mosDBase);
                MosDBGui.createAndShowGUI(gui);
            }
        });

        //mosDBase.setPath("/media/temp/netdata");
        mosDBase.setPath("/Users/lsp/Documents/netdata");
        //LDAPManager ldapManager=new LDAPManager();
        //dbManager.addMapArrayIntoDB(ldapManager.getLoginFullNameFromAD(),"Users","login","FullName","OU");
        //dbManager.putMapIntoDB(criptoProMap,"Computers","name","criptoPro");
        //mosDBase.gui.compTableToPanel(dbManager.getListComputerFromDB());
        //mosDBase.gui.userTableToPanel(dbManager.getListUserFromDB());
        //dbManager.testGetDB();
        mosDBase.getComputerTable();
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setSqlRequest(String sqlRequest) {
        this.sqlRequest = sqlRequest;
    }

    protected String path="/media/temp/netdata";
    private String regexCriptoPRO="\\s*ProductID\\s*REG_SZ\\s*";
    private String sqlRequest;
    private Statement stm;
    private Set<String> computersSetFromDir;
    private MosDBGui gui;
    private DBManager dbManager;
    private ArrayList<Computer> computers;
    private ArrayList<User> users;

    public ArrayList<Computer> getComputers() {
        return computers;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public String getPath() {
        return path;
    }

    public MosDBase() {
        dbManager = new DBManager();
        computers=new ArrayList<Computer>();
        users=new ArrayList<User>();
        //gui = new MosDBGui(this);
        //gui.setVisible(true);
    }

    public void getComputersFromDir(String path, HashSet<String> set) {
        File file = new File(path);
        // интерфейс из класса FilenameFilter, вставляется автоматом
        FilenameFilter licFilter=new FilenameFilter() {
            public boolean accept(File dir, String s) {
                if (s.endsWith(".lic")) return true;
                else return false;
            }
        };
        for (String s : file.list(licFilter)) {
           set.add(s.replaceAll("\\..*lic", ""));
        }
    }

    public Map<String,String> getCriptoProFromDir(Set<String> set) throws FileNotFoundException {
        File dir=new File(path);
        FilenameFilter licFilter=new FilenameFilter() {
            public boolean accept(File dir, String s) {
                if (s.endsWith(".lic")) return true;
                else return false;
            }
        };
        File[] files=dir.listFiles(licFilter);
        //сортируем файлы по последней модификации, потом просматриваем
        //массив для каждого компа, выдергиваем самую новую лицензию
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File file, File t1) {
                if(file.lastModified()>t1.lastModified()) return -1;
                else if(t1.lastModified()>file.lastModified()) return +1;
                else return 0;
            }
        });
        Map<String,String> map=new HashMap<String, String>();
        for (String s : set) {
            String license = null;
            for (int i = 0; i < files.length; i++) {
                if(files[i].getName().startsWith(s)){
                    if(files[i].length()!=0) {
                        Scanner in = new Scanner(files[i]);
                        while (in.hasNext()) {
                            Matcher matcher= Pattern.compile(regexCriptoPRO).matcher(in.nextLine());
                            if(matcher.find()){
                                license=matcher.replaceAll("");
                                break;
                            }
                            else license="not installed";
                        }
                        in.close();
                    }
                    else license="not installed";
                    break;
                }
                else license="not found";
            }
            map.put(s, license);
        }
        return map;
    }

    public void getComputerTable() throws SQLException {
        computers=dbManager.getListComputerFromDB();
        users=dbManager.getListUserFromDB();
    }
/*
    public void getUserTable() throws SQLException {
        users=dbManager.getListUserFromDB();
        computers=dbManager.getListComputerFromDB();
        //gui.userTableToPanel(this,users,computers);
    }
*/
    public void saveComputersDB() throws SQLException {
        dbManager.saveComputerDB(computers);
    }
    public void saveUsersDB() throws SQLException {
        dbManager.saveUserDB(users);
    }
}
