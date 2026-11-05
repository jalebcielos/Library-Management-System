package interfaces;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import mainpackage.*;
import users.Member;

public class MemberGUI extends JFrame {

    private Library lb;
    private Member member;
    private Runnable onLogout;
    private boolean updatingFilters = false;

    //PANELS
    JPanel userPanel = new JPanel();
    JPanel Bookpanel = new JPanel();
    JPanel line1 = new JPanel();
    JPanel line2 = new JPanel();
    JPanel line3 = new JPanel();
    JPanel line4 = new JPanel();
    JPanel line5 = new JPanel();

    //LABELS
    JLabel welcomeLabel = new JLabel("Welcome, Member");
    JLabel subtitleLabel = new JLabel("Greenfield University Library");
    JLabel previewHeader = new JLabel("  Book Preview");
    JLabel PictureLabel = new JLabel("Book Cover", JLabel.CENTER);
    JLabel TitleLabel = new JLabel("Title:");
    JLabel AuthorLabel = new JLabel("Author:");
    JLabel CategoryLabel = new JLabel("Category:");
    JLabel YearLabel = new JLabel("Pub. Year:");
    JLabel AvailabilityLabel = new JLabel("Status:");
    JLabel logo = new JLabel();
    JLabel borrowedCountLabel = new JLabel();

    String column[] = {"ID", "Title", "Author", "Category", "Status"};

    DefaultTableModel model = new DefaultTableModel(column, 0) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };

    JTable table = new JTable(model);
    JScrollPane scrollPane = new JScrollPane(table);
    DefaultTableCellRenderer center = new DefaultTableCellRenderer();

    //TEXTFIELD
    JTextField txtSearch = new JTextField("Search title or author");

    //COMBOBOXES
    JComboBox<String> Category = new JComboBox<>();
    JComboBox<String> statusBox = new JComboBox<>();

    //BUTTONS
    JButton borrowBtn = new JButton("Borrow a Book");
    JButton returnBtn = new JButton("Return a Book");

    public MemberGUI(Library library, Member member, Runnable onLogout) {

        this.lb = library;
        this.member = member;
        this.onLogout = onLogout;

        this.setTitle("Greenfield University LMS - Member");
        this.setIconImage(new ImageIcon("src/icon/Logo.png").getImage());
        this.setSize(1100, 680);
        this.setLocationRelativeTo(null);
        this.setLayout(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(Color.decode("#4F7D32"));
        this.getContentPane().setFocusable(true);

        //TOPBAR
        this.add(userPanel);
        userPanel.setBackground(Color.decode("#2F5A1A"));
        userPanel.setBounds(0, 0, 1100, 70);
        userPanel.setLayout(null);

        logo.setIcon(ImageUtil.smoothIcon("src/icon/Logo.png", 50, 50));
        logo.setBounds(20, 10, 50, 50);
        userPanel.add(logo);

        userPanel.add(welcomeLabel);
        welcomeLabel.setText("Welcome, " + member.getName());
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBounds(80, 10, 400, 30);

        userPanel.add(subtitleLabel);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.decode("#CFE3BF"));
        subtitleLabel.setBounds(80, 40, 400, 20);

        userPanel.add(new IconButton("src/icon/History.png", "My Records", this::showRecords).place(796, 7));
        userPanel.add(new IconButton("src/icon/Profile.png", "Profile", this::showProfile).place(883, 7));
        userPanel.add(new IconButton("src/icon/Logout.png", "Logout", this::logout).place(970, 7));

        //SEARCH
        this.add(txtSearch);
        txtSearch.setBounds(20, 100, 300, 30);
        txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), BorderFactory.createEmptyBorder(0, 5, 0, 0)));
        setHintPlaceholder(txtSearch, "Search title or author");
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilters();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilters();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilters();
            }
        });

        this.add(Category);
        Category.setBounds(335, 100, 170, 30);
        Category.addActionListener(e -> applyFilters());

        this.add(statusBox);
        statusBox.setBounds(520, 100, 150, 30);
        statusBox.addItem("All Books");
        statusBox.addItem("Available");
        statusBox.addItem("Unavailable");
        statusBox.addActionListener(e -> applyFilters());

        //PREVIEW
        this.add(Bookpanel);
        Bookpanel.setLayout(null);
        Bookpanel.setBackground(Color.decode("#FFF1D6"));
        Bookpanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        Bookpanel.setBounds(710, 80, 360, 550);

        Bookpanel.add(previewHeader);
        previewHeader.setOpaque(true);
        previewHeader.setBackground(Color.decode("#F29F05"));
        previewHeader.setForeground(Color.WHITE);
        previewHeader.setFont(new Font("SansSerif", Font.BOLD, 15));
        previewHeader.setBounds(0, 0, 360, 44);

        Bookpanel.add(PictureLabel);
        PictureLabel.setOpaque(true);
        PictureLabel.setBackground(Color.decode("#FFE3A8"));
        PictureLabel.setForeground(Color.decode("#9A7A3A"));
        PictureLabel.setHorizontalAlignment(JLabel.CENTER);
        PictureLabel.setBorder(BorderFactory.createLineBorder(Color.decode("#E0B25A")));
        PictureLabel.setBounds(78, 64, 200, 260);

        JLabel[] previewLabels = {TitleLabel, AuthorLabel, CategoryLabel, YearLabel, AvailabilityLabel};
        JPanel[] previewLines = {line1, line2, line3, line4, line5};

        for (int i = 0; i < previewLabels.length; i++) {

            previewLabels[i].setFont(new Font("SansSerif", Font.PLAIN, 13));
            previewLabels[i].setForeground(Color.BLACK);
            previewLabels[i].setBounds(30, 340 + (i * 40), 300, 18);
            Bookpanel.add(previewLabels[i]);

            previewLines[i].setBackground(Color.decode("#F0CF93"));
            previewLines[i].setBounds(30, 364 + (i * 40), 296, 1);
            Bookpanel.add(previewLines[i]);

        }
        
        this.add(scrollPane);
        membertableFormat();

        //BORROW/RETURN
        this.add(borrowedCountLabel);
        borrowedCountLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        borrowedCountLabel.setForeground(Color.WHITE);
        borrowedCountLabel.setBounds(20, 585, 380, 30);

        this.add(borrowBtn);
        borrowBtn.setBackground(Color.decode("#266210"));
        borrowBtn.setForeground(Color.decode("#FCF9EA"));
        borrowBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        borrowBtn.setFocusPainted(false);
        borrowBtn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        borrowBtn.setBounds(400, 585, 130, 32);
        borrowBtn.addActionListener(e -> openBorrowReturn(true));

        this.add(returnBtn);
        returnBtn.setBackground(Color.decode("#CC6F00"));
        returnBtn.setForeground(Color.decode("#FCF9EA"));
        returnBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        returnBtn.setFocusPainted(false);
        returnBtn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        returnBtn.setBounds(540, 585, 130, 32);
        returnBtn.addActionListener(e -> openBorrowReturn(false));

        refreshTable();
        clearDetails();

    }

    public void membertableFormat() {

        scrollPane.setBackground(Color.GRAY);
        scrollPane.setBounds(20, 150, 650, 420);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.decode("#D6D6D6")));

        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(Color.GRAY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.setBackground(Color.WHITE);
        table.setForeground(Color.BLACK);
        table.setRowHeight(25);

        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);

        center.setHorizontalAlignment(JLabel.CENTER);
        center.setVerticalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setResizable(false);
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setForeground(Color.WHITE);
                setBackground(value.equals("Available") ? Color.decode("#76C457") : Color.decode("#DA6556"));

                return this;
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Book b = getSelectedBook();
                if (b == null) {
                    clearDetails();
                } else {
                    showDetails(b);
                }
            }
        });
    }

    public void refreshTable() {

        refreshCategories();
        applyFilters();
        borrowedCountLabel.setText("Books you have borrowed: " + member.getBooks().size());

    }

    public void refreshCategories() {

        updatingFilters = true;

        Object selected = Category.getSelectedItem();

        Category.removeAllItems();
        Category.addItem("All Categories");

        ArrayList<String> cats = new ArrayList<>();

        for (Book b : lb.getBooks()) {
            if (!cats.contains(b.getCategory())) {
                cats.add(b.getCategory());
            }
        }

        java.util.Collections.sort(cats);

        for (String c : cats) {
            Category.addItem(c);
        }

        if (selected != null) {
            Category.setSelectedItem(selected);
        }

        updatingFilters = false;

    }

    public void applyFilters() {

        if (updatingFilters || Category.getSelectedItem() == null) {
            return;
        }

        String search = getFieldText(txtSearch, "Search title or author").toLowerCase();
        String cate = Category.getSelectedItem().toString();
        int stat = statusBox.getSelectedIndex();

        model.setRowCount(0);

        for (Book b : lb.getBooks()) {

            if (!search.isEmpty() && !b.getTitle().toLowerCase().contains(search) && !b.getAuthor().toLowerCase().contains(search)) {
                continue;
            }
            if (!cate.equals("All Categories") && !b.getCategory().equals(cate)) {
                continue;
            }
            if (stat == 1 && !b.isAvailable()) {
                continue;
            }
            if (stat == 2 && b.isAvailable()) {
                continue;
            }

            model.addRow(new Object[]{b.getItemID(), b.getTitle(), b.getAuthor(), b.getCategory(), b.isAvailable() ? "Available" : "Unavailable"});
        }
    }

    private Book getSelectedBook() {

        int row = table.getSelectedRow();

        if (row == -1) {
            return null;
        }

        return lb.findBook((int) table.getValueAt(row, 0));

    }

    private void showDetails(Book b) {

        TitleLabel.setText("Title: " + b.getTitle());
        TitleLabel.setToolTipText(b.getTitle());
        AuthorLabel.setText("Author: " + b.getAuthor());
        CategoryLabel.setText("Category: " + b.getCategory());
        YearLabel.setText("Pub. Year: " + b.getYear());
        AvailabilityLabel.setText("Status: " + (b.isAvailable() ? "Available" : "Unavailable"));

        ImageIcon icon = ImageUtil.scale(b.getCover(), 198, 258);
        PictureLabel.setIcon(icon);
        PictureLabel.setText(icon == null ? "No Cover" : "");

    }

    private void clearDetails() {

        TitleLabel.setText("Title:");
        TitleLabel.setToolTipText(null);
        AuthorLabel.setText("Author:");
        CategoryLabel.setText("Category:");
        YearLabel.setText("Pub. Year:");
        AvailabilityLabel.setText("Availability:");
        PictureLabel.setIcon(null);
        PictureLabel.setText("Book Cover");

    }

    private void openBorrowReturn(boolean borrowing) {

        Book selected = getSelectedBook();
        int selectedID = (selected == null) ? -1 : selected.getItemID();

        new BorrowReturnDialog(this, lb, member, borrowing).setVisible(true);

        // the window is closed: refresh the list and keep the same book selected
        refreshTable();

        for (int i = 0; i < table.getRowCount(); i++) {
            if ((int) table.getValueAt(i, 0) == selectedID) {
                table.setRowSelectionInterval(i, i);
                break;
            }
        }

    }

    private void showRecords() {

        new TransactionDialog(this, "My Records", lb.getTransactionsOf(member.getUserID()), false).setVisible(true);

    }

    private void showProfile() {

        new ProfileDialog(this, lb, member).setVisible(true);

    }

    private void logout() {

        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Logout", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            onLogout.run();
            dispose();
        }

    }

    public void setHintPlaceholder(JTextField textField, String hint) {

        textField.setForeground(Color.GRAY);
        textField.setText(hint);

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                if (textField.getText().equals(hint)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent evt) {
                if (textField.getText().trim().isEmpty()) {
                    textField.setText(hint);
                    textField.setForeground(Color.GRAY);
                }
            }
        });

    }

    public String getFieldText(JTextField textField, String hint) {

        String text = textField.getText().trim();

        if (text.equals(hint)) {
            return "";
        }

        return text;

    }

}
