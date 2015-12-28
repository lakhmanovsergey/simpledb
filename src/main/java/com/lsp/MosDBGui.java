package com.lsp;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * Created by lsp on 27.09.15.
 */
public class MosDBGui implements ActionListener, ListSelectionListener, MouseListener {
    private static JFrame frame;

    private JPanel totalGUI,testPane;
    private JMenuBar menuBar;
    private JMenu actionMenu,settingMenu;
    private JMenuItem compItem,userItem,pathItem;
    private JRadioButtonMenuItem compRadioButton,userRadioButton;
    private JTable computerTable,userTable;

    private String dirPath;
    private MosDBase mosDBase;
    private int selectedRow;

    public static void createAndShowGUI(MosDBGui mosDBGui) {

        JFrame.setDefaultLookAndFeelDecorated(true);
        frame = new JFrame("База данных");

        //Создаем и устанавливаем панель меню.
        frame.setJMenuBar(mosDBGui.createMenuBar());
        frame.pack();


        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 390);
        frame.setVisible(true);

        frame.getContentPane().add(mosDBGui.createComputerTable());
    }

    public MosDBGui(MosDBase mosDBase) {
        this.mosDBase = mosDBase;
    }

    private JMenuBar createMenuBar() {
        // делаем меню
        menuBar=new JMenuBar(); //создается менюбар
        actionMenu=new JMenu("действие");
        menuBar.add(actionMenu);

        compRadioButton=new JRadioButtonMenuItem("computers");
        userRadioButton=new JRadioButtonMenuItem("users");
        ButtonGroup buttonGroup=new ButtonGroup();
        buttonGroup.add(compRadioButton);
        buttonGroup.add(userRadioButton);
        actionMenu.add(compRadioButton);
        actionMenu.add(userRadioButton);
        /*
        compItem = new JMenuItem("показать компьютеры");
        compItem.addActionListener(this);
        actionMenu.add(compItem);

        userItem = new JMenuItem("показать пользователей");
        userItem.addActionListener(this);
        actionMenu.add(userItem);
        */
        settingMenu=new JMenu("настройка");
        menuBar.add(settingMenu);
        pathItem=new JMenuItem("каталог отчетов ("+mosDBase.path+")");
        pathItem.addActionListener(this);
        settingMenu.add(pathItem);

        return menuBar;
    }
    private JScrollPane createComputerTable(){
        final ArrayList<Computer> computers= mosDBase.getComputers();
        computerTable=new JTable(new AbstractTableModel() {
            public int getRowCount() {// обязательно – количество строк в таблице
                return computers.size();
            }
            public int getColumnCount() {// количество колонок в таблице
                return 4;
            }
            // и последний жестко обязаталельный метод – возвращающий значение элемента,
            // который должен находится в указанных координатах
            public Object getValueAt(int rowIndex, int colIndex) {
                switch (colIndex) {
                    case 0:
                        return computers.get(rowIndex).getId();
                    case 1:
                        return computers.get(rowIndex).getName();
                    case 2:
                        return computers.get(rowIndex).getCriptoPro();
                    case 3: {
                        User temp = computers.get(rowIndex).getUser();
                        return temp==null?"no user":temp.getLogin();
                    }
                }
                return "";
            }
            public String getColumnName(int colIndex){
                switch (colIndex){
                    case 0:
                        return "ID";
                    case 1:
                        return "Comp Name";
                    case 2:
                        return "CriptoPro License";
                }
                return "";
            }
            public Class<?> getColumnClass(int colIndex){
                switch (colIndex){
                    case 0:
                        return Integer.class;
                    case 3:
                        return User.class;
                }
                return String.class;
            }
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
            /*
            @Override
            public void setValueAt(Object o, int rowIndex, int colIndex) {
                super.setValueAt(o, rowIndex, colIndex);
                computers.get(rowIndex).setIsUpdated(true);
                switch (colIndex){
                    case 2:
                        computers.get(rowIndex).setCriptoPro(o.toString());
                        break;
                    case 3:
                        computers.get(rowIndex).setUser((User) o);
                        //System.out.println(o.toString());
                        break;
                }
            }
            */
        });
        DefaultTableCellRenderer renderer=new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent
                    (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                Color temp=null;
                temp=(isSelected)?cell.getBackground():Color.white;
                if (computers.get(row).isDeleted()) cell.setBackground(Color.red);
                else cell.setBackground(computers.get(row).isUpdated() ? Color.yellow : temp);
                return cell;
            }
        };
        for (int i = 0; i < computerTable.getColumnCount(); i++) {
            computerTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        computerTable.getSelectionModel().addListSelectionListener(this);
        computerTable.addMouseListener(this);
        /*
        JComboBox comboBox=new JComboBox(mosDBase.getUsers().toArray());
        {
            User temp=null;
            @Override
            public void setRenderer(ListCellRenderer listCellRenderer) {
                super.setRenderer(new ListCellRenderer() {
                    public Component getListCellRendererComponent(JList jList, Object o, int i, boolean b, boolean b1) {
                        DefaultListCellRenderer defaultListCellRenderer=new DefaultListCellRenderer();
                        JLabel renderer=(JLabel)defaultListCellRenderer.getListCellRendererComponent(jList,o,i,b,b1);
                        if(o instanceof User) renderer.setText(((User)o).getLogin());
                        return renderer;
                    }
                });
            }

            @Override
            public void addActionListener(ActionListener l) {
                super.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        temp=users.get(((JComboBox)actionEvent.getSource()).getSelectedIndex());
                    }
                });
            }

            @Override
            public Object getSelectedItem() {
                User retUser=null;
                if(temp instanceof User)  retUser=temp;
                temp=null;
                return retUser;
                //return super.getSelectedItem();
            }
        };

        computerTable.setDefaultEditor(User.class, new TableCellEditor() {
            JComboBox comboBox=new JComboBox(mosDBase.getUsers().toArray());
            public Component getTableCellEditorComponent(JTable jTable, Object o, boolean b, int i, int i1) {
                if(mosDBase.getUsers().contains(o)) comboBox.setSelectedItem(o);
                return comboBox;
            }

            public Object getCellEditorValue() {
                return comboBox.getSelectedItem();
            }

            public boolean isCellEditable(EventObject eventObject) {
                return true;
            }

            public boolean shouldSelectCell(EventObject eventObject) {
                return true;
            }

            public boolean stopCellEditing() {
                return false;
            }

            public void cancelCellEditing() {

            }

            public void addCellEditorListener(CellEditorListener cellEditorListener) {

            }

            public void removeCellEditorListener(CellEditorListener cellEditorListener) {

            }
        });
        */
        //computerTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(comboBox));
        JScrollPane scrollPane = new JScrollPane(computerTable);
        return scrollPane;
    }
    public void computerRecordsEdit(){
        final Computer computer= mosDBase.getComputers().get(selectedRow);
        int rows=computer.getFields().length+1;
        JDialog dialog=new JDialog(MosDBGui.frame,true);
        JLabel label[]= new JLabel[rows];
        final JTextField textField[]=new JTextField[rows];
        JPanel fieldPane[]=new JPanel[rows];
        JPanel editPane=new JPanel();
        JButton endButton=new JButton("закончить редактирование");
        final JComboBox comboBox=new JComboBox(mosDBase.getUsers().toArray()){
            //User temp=null;
            @Override
            public void setRenderer(ListCellRenderer listCellRenderer) {
                super.setRenderer(new ListCellRenderer() {
                    public Component getListCellRendererComponent(JList jList, Object o, int i, boolean b, boolean b1) {
                        DefaultListCellRenderer defaultListCellRenderer=new DefaultListCellRenderer();
                        JLabel renderer=(JLabel)defaultListCellRenderer.getListCellRendererComponent(jList,o,i,b,b1);
                        if(o instanceof User) renderer.setText(((User)o).getLogin());
                        return renderer;
                    }
                });
            }
        };
        for (int i = 0; i < rows-1; i++) {
            textField[i]=new JTextField(computer.getFields()[i]);
            if (i==0) textField[i].setEditable(false);
            label[i]=new JLabel(Computer.getNames()[i]);
            fieldPane[i]=new JPanel();
            fieldPane[i].add(label[i]);fieldPane[i].add(textField[i]);
            editPane.add(fieldPane[i]);
        }
        label[rows-1]=new JLabel("user");
        fieldPane[rows-1]=new JPanel();
        fieldPane[rows-1].add(label[rows-1]);fieldPane[rows-1].add(comboBox);
        editPane.add(fieldPane[rows-1]);
        endButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                computer.setCriptoPro(textField[1].getText());
                computer.setUser((User) comboBox.getSelectedItem());
                computer.setIsUpdated(true);
                computerTable.updateUI();
            }
        });
        editPane.add(endButton);
        dialog.add(editPane);
        dialog.setSize(450,390);
        dialog.setVisible(true);
        //TODO
    }
    /*
    frame.addWindowListener(new WindowListener() {
        public void windowOpened(WindowEvent windowEvent) {
        }

    public void windowClosing(WindowEvent windowEvent) {
        Object[] options = { "Да", "Нет!" };
        int n = JOptionPane
                .showOptionDialog(windowEvent.getWindow(), "Сохранить изменения?",
                        "Подтверждение", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options,
                        options[0]);
        if (n == 0) {
            windowEvent.getWindow().setVisible(false);
            try {
                mosDBase.saveUsersDB();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void actionPerformed(ActionEvent actionEvent) {
        //label.setText(actionEvent.toString());
    }
*/
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        if (listSelectionEvent.getValueIsAdjusting()) selectedRow=computerTable.getSelectedRow();
    }

    public void actionPerformed(ActionEvent actionEvent) {
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount()>=2)computerRecordsEdit();
    }

    public void mousePressed(MouseEvent mouseEvent) {

    }

    public void mouseReleased(MouseEvent mouseEvent) {

    }

    public void mouseEntered(MouseEvent mouseEvent) {

    }

    public void mouseExited(MouseEvent mouseEvent) {

    }

    /*
    public MosDBGui(String title, final MosDBase mosDBase, final DBManager dbManager) {
        super(title);
        setBounds(100, 100, 1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // делаем меню
        JMenuBar menuBar=new JMenuBar(); //создается менюбар
        setJMenuBar(menuBar); //сначала добавляем менюбар в форму, затем пункты
        JMenu actionMenu=new JMenu("действие");
        menuBar.add(actionMenu);

        JMenuItem item;
        item = new JMenuItem("показать компьютеры");
        actionMenu.add(item);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    mosDBase.showComputerTable();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        item=new JMenuItem("показать пользователей");
        actionMenu.add(item);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    mosDBase.showUserTable();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        JMenu settingMenu=new JMenu("настройка");
        menuBar.add(settingMenu);
        JMenuItem dirItem=new JMenuItem("каталог отчетов ("+mosDBase.path+")");
        settingMenu.add(dirItem);
    }

    public void compTableToPanel(final MosDBase mosDBase, final ArrayList<Computer> computers, final ArrayList<User> users) throws SQLException {
        for (User user : users) {
        }
        final JFrame frame=new JFrame();
        frame.setBounds(100, 100, 1000, 800);
        // add table to panel
        final Map<Integer,Computer> map=new HashMap<Integer, Computer>();
        final JTable table=new JTable(new AbstractTableModel() {// в состав модели входят многожество методов но только три нижеследующий обязательны
            public int getRowCount() {// обязательно – количество строк в таблице
                return computers.size();
            }
            public int getColumnCount() {// количество колонок в таблице
                return 4;
            }
            // и последний жестко обязаталельный метод – возвращающий значение элемента,
            // который должен находится в указанных координатах
            public Object getValueAt(int rowIndex, int colIndex) {
                switch (colIndex) {
                    case 0:
                        return computers.get(rowIndex).getId();
                    case 1:
                        return computers.get(rowIndex).getName();
                    case 2:
                        return computers.get(rowIndex).getCriptoPro();
                    case 3: {
                        User temp = computers.get(rowIndex).getUser();
                        return temp==null?"no user":temp.getLogin();
                    }
                }
                return "";
            }
            public String getColumnName(int colIndex){
               switch (colIndex){
                   case 0:
                       return "ID";
                   case 1:
                       return "Comp Name";
                   case 2:
                       return "CriptoPro License";
               }
                return "";
            }
            public Class<?> getColumnClass(int colIndex){
                switch (colIndex){
                    case 0:
                        return Integer.class;
                    case 3:
                        return User.class;
                }
                return String.class;
            }
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex){
                switch (colIndex){
                    case 0:
                        return false;
                    case 1:
                        return false;
                }
                return true;
            }

            @Override
            public void setValueAt(Object o, int rowIndex, int colIndex) {
                super.setValueAt(o, rowIndex, colIndex);
                computers.get(rowIndex).setIsUpdated(true);
                switch (colIndex){
                    case 2:
                        computers.get(rowIndex).setCriptoPro(o.toString());
                        break;
                    case 3:
                        computers.get(rowIndex).setUser((User) o);
                        //System.out.println(o.toString());
                        break;
                }
            }

        });
        frame.addWindowListener(new WindowListener() {
            public void windowOpened(WindowEvent windowEvent) {
            }

            public void windowClosing(WindowEvent windowEvent) {
                Object[] options = { "Да", "Нет!" };
                int n = JOptionPane
                        .showOptionDialog(windowEvent.getWindow(), "Сохранить изменения?",
                                "Подтверждение", JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE, null, options,
                                options[0]);
                if (n == 0) {
                    windowEvent.getWindow().setVisible(false);
                    try {
                        mosDBase.saveComputersDB();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

            public void windowClosed(WindowEvent windowEvent) {

            }

            public void windowIconified(WindowEvent windowEvent) {

            }

            public void windowDeiconified(WindowEvent windowEvent) {

            }

            public void windowActivated(WindowEvent windowEvent) {

            }

            public void windowDeactivated(WindowEvent windowEvent) {

            }
        });
        DefaultTableCellRenderer renderer=new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent
                    (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                Color temp=null;
                temp=(isSelected)?cell.getBackground():Color.white;
                if (computers.get(row).isDeleted()) cell.setBackground(Color.red);
                else cell.setBackground(computers.get(row).isUpdated() ? Color.yellow : temp);
                return cell;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        JButton delKey=new JButton("удалить выделенные строки");
        delKey.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                int[] rows = table.getSelectedRows();
                for (int i : rows) {
                    computers.get(i).setIsDeleted(true);
                    table.clearSelection();
                    table.updateUI();
                }
            }
        });
        JButton saveKey=new JButton("сохранить изменения");
        saveKey.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    mosDBase.saveComputersDB();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                table.updateUI();
            }
        });
        JComboBox comboBox=new JComboBox(users.toArray()){
            User temp=null;
            @Override
            public void setRenderer(ListCellRenderer listCellRenderer) {
                super.setRenderer(new ListCellRenderer() {
                    public Component getListCellRendererComponent(JList jList, Object o, int i, boolean b, boolean b1) {
                        DefaultListCellRenderer defaultListCellRenderer=new DefaultListCellRenderer();
                        JLabel renderer=(JLabel)defaultListCellRenderer.getListCellRendererComponent(jList,o,i,b,b1);
                        if(o instanceof User) renderer.setText(((User)o).getLogin());
                        return renderer;
                    }
                });
            }

            @Override
            public void addActionListener(ActionListener l) {
                super.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        temp=users.get(((JComboBox)actionEvent.getSource()).getSelectedIndex());
                    }
                });
            }

            @Override
            public Object getSelectedItem() {
                User retUser=null;
                if(temp instanceof User)  retUser=temp;
                temp=null;
                return retUser;
                //return super.getSelectedItem();
            }
        };
        table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(comboBox));
        /*
        table.setDefaultEditor(User.class, new TableCellEditor() {
            public Component getTableCellEditorComponent(JTable jTable, Object o, boolean b, int i, int i1) {
                ArrayList<String> temp=new ArrayList<String>();
                for (User user : users) {
                    temp.add(user.getLogin());
                }
                JComboBox comboBox=new JComboBox(new DefaultComboBoxModel(temp.toArray()));
                comboBox.setSelectedItem(computers.get(i).getUser());
                return comboBox;
            }

            public Object getCellEditorValue() {
                return null;
            }

            public boolean isCellEditable(EventObject eventObject) {
                return true;
            }

            public boolean shouldSelectCell(EventObject eventObject) {
                return false;
            }

            public boolean stopCellEditing() {
                return false;
            }

            public void cancelCellEditing() {

            }

            public void addCellEditorListener(CellEditorListener cellEditorListener) {

            }

            public void removeCellEditorListener(CellEditorListener cellEditorListener) {

            }
        });

        JPanel buttonsPanel = new JPanel(new FlowLayout());
        //Расставляем компоненты по местам
        buttonsPanel.add(delKey);
        buttonsPanel.add(saveKey);
        frame.add(buttonsPanel, BorderLayout.NORTH);
        frame.getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);// table add with scroll line
        frame.pack();
        frame.setVisible(true);

    }
    public void userTableToPanel(final MosDBase mosDBase, final ArrayList<User> users, ArrayList<Computer> computers) throws SQLException {
        JFrame frame=new JFrame();
        frame.setBounds(100, 100, 1000, 800);
        // add table to panel
        final JTable table=new JTable(new AbstractTableModel() {// в состав модели входят многожество методов но только три нижеследующий обязательны
            public int getRowCount() {// обязательно – количество строк в таблице
                return users.size();
            }
            public int getColumnCount() {// количество колонок в таблице
                return 7;
            }
            // и последний жестко обязаталельный метод – возвращающий значение элемента,
            // который должен находится в указанных координатах
            public Object getValueAt(int rowIndex, int colIndex) {
                switch (colIndex) {
                    case 0:
                        return users.get(rowIndex).getId();
                    case 1:
                        return users.get(rowIndex).getLogin();
                    case 2:
                        return users.get(rowIndex).getFullName();
                    case 3:
                        return users.get(rowIndex).getOU();
                    case 4:
                        return users.get(rowIndex).getPosition();
                    case 5:
                        return users.get(rowIndex).getKabinet();
                    case 6:
                        return users.get(rowIndex).getPhone();
                }
                return "";
            }
            public String getColumnName(int colIndex){
                switch (colIndex){
                    case 0:
                        return "ID";
                    case 1:
                        return "Login";
                    case 2:
                        return "ФИО";
                    case 3:
                        return "Отдел/Сектор";
                    case 4:
                        return "Должность";
                    case 5:
                        return "Кабинет";
                    case 6:
                        return "Телефон";
                }
                return "";
            }
            public Class<?> getColumnClass(int colIndex){
                switch (colIndex){
                    case 0:
                        return Integer.class;
                }
                return String.class;
            }
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex){
                switch (colIndex){
                    case 0:
                        return false;
                    case 1:
                        return false;
                }
                return true;
            }
            @Override
            public void setValueAt(Object o, int rowIndex, int colIndex) {
                super.setValueAt(o, rowIndex, colIndex);
                users.get(rowIndex).setIsUpdated(true);
                switch (colIndex){
                    case 2:
                        users.get(rowIndex).setFullName(o.toString());
                        break;
                    case 3:
                        users.get(rowIndex).setoU(o.toString());
                        break;
                    case 4:
                        users.get(rowIndex).setPosition(o.toString());
                        break;
                    case 5:
                        users.get(rowIndex).setKabinet(o.toString());
                        break;
                    case 6:
                        users.get(rowIndex).setPhone(o.toString());
                        break;
                }
            }
        });
        frame.addWindowListener(new WindowListener() {
            public void windowOpened(WindowEvent windowEvent) {
            }

            public void windowClosing(WindowEvent windowEvent) {
                Object[] options = { "Да", "Нет!" };
                int n = JOptionPane
                        .showOptionDialog(windowEvent.getWindow(), "Сохранить изменения?",
                                "Подтверждение", JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE, null, options,
                                options[0]);
                if (n == 0) {
                    windowEvent.getWindow().setVisible(false);
                    try {
                        mosDBase.saveUsersDB();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

            public void windowClosed(WindowEvent windowEvent) {

            }

            public void windowIconified(WindowEvent windowEvent) {

            }

            public void windowDeiconified(WindowEvent windowEvent) {

            }

            public void windowActivated(WindowEvent windowEvent) {

            }

            public void windowDeactivated(WindowEvent windowEvent) {

            }
        });
        DefaultTableCellRenderer renderer=new DefaultTableCellRenderer(){
            Color temp;
            public Component getTableCellRendererComponent
                    (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                Color temp=null;
                temp=(isSelected)?cell.getBackground():Color.white;
                if (users.get(row).isDeleted()) cell.setBackground(Color.red);
                else cell.setBackground(users.get(row).isUpdated() ? Color.yellow : temp);
                return cell;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        JButton delKey=new JButton("удалить выделенные строки");
        delKey.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                int[] rows=table.getSelectedRows();
                for (int i : rows){
                    users.get(i).setIsUpdated(true);
                    table.clearSelection();
                    table.updateUI();
                }
            }
        });
        JButton saveKey=new JButton("сохранить изменения");
        saveKey.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    mosDBase.saveUsersDB();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        JPanel buttonsPanel = new JPanel(new FlowLayout());
        //Расставляем компоненты по местам
        buttonsPanel.add(delKey);
        buttonsPanel.add(saveKey);
        frame.add(buttonsPanel, BorderLayout.NORTH);
        frame.getContentPane().add (new JScrollPane(table),BorderLayout.CENTER);// table add with scroll line
        frame.pack();
        frame.setVisible(true);
    }

    class UserEditor extends AbstractCellEditor implements TableCellEditor,ActionListener{

        private User user;
        private List<User> userList;
        private int index;

        public UserEditor(List<User> userList) {
            this.userList = userList;
        }

        public void actionPerformed(ActionEvent actionEvent) {

        }

        public Component getTableCellEditorComponent(JTable jTable, Object o, boolean b, int i, int i1) {
            //if (o instanceof User) {
            //    this.user = (User) o;
            //}

            JComboBox comboUser = new JComboBox(new DefaultComboBoxModel(userList.toArray()){
                @Override
                public Object getSelectedItem() {
                    return (User)super.getSelectedItem();
                }

                @Override
                public Object getElementAt(int i) {
                    return (User)super.getElementAt(i);
                }

            });

            index=comboUser.getSelectedIndex();
            comboUser.addActionListener(this);

            return comboUser;
        }

        public Object getCellEditorValue() {
            return userList.get(index);
        }
    }
    */
}