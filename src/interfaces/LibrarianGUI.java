package interfaces;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import mainpackage.*;
import users.*;

public class LibrarianGUI extends JFrame implements ActionListener {

    private Library lb;
    private Librarian librarian;
    private Runnable onLogout;
    private String imagePath;
    private String column[] = {"ID", "Title", "Author", "Category", "Year", "Stock", "Status"};
    private String[] options = {"Confirm", "Cancel"};
    private int selectedBookID = -1;
    private boolean updatingFilters = false;

    DefaultTableModel model = new DefaultTableModel(column, 0) {

        @Override
        public boolean isCellEditable(int row, int col) {

            return false;

        }

    };

    //Image
    ImageIcon logo = new ImageIcon("src/icon/Logo.png");

    //Table
    JTable table = new JTable(model);
    JScrollPane scrollPane = new JScrollPane(table);
    JSpinner addStock = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
    DefaultTableCellRenderer center = new DefaultTableCellRenderer();

    //ComboBoxes
    JComboBox<String> cbStatus = new JComboBox<>();
    JComboBox<String> cbCategory = new JComboBox<>();
    JComboBox<String> cbYear = new JComboBox<>();

    //Panels
    JPanel addBookPanel = new JPanel();
    JPanel detailsPanel = new JPanel();
    JPanel userPanel = new JPanel();

    //Labels
    JLabel titleLabel = new JLabel("Title*         :");
    JLabel authorLabel = new JLabel("Author*     :");
    JLabel categoryLabel = new JLabel("Category*  :");
    JLabel yearLabel = new JLabel("Year*        :");
    JLabel stockLabel = new JLabel("Stock* :");
    JLabel pTitleLabel = new JLabel("Title:");
    JLabel pAuthorLabel = new JLabel("Author:");
    JLabel pCategoryLabel = new JLabel("Category:");
    JLabel pPubYearLabel = new JLabel("Pub. Year:");
    JLabel pStockLabel = new JLabel("Stock:");
    JLabel welcomeLabel = new JLabel();
    JLabel pPictureLabel = new JLabel();
    JLabel cPictureLabel = new JLabel();
    JLabel logoLabel = new JLabel();

    //TextFields
    JTextField txtTitle = new JTextField("Enter title");
    JTextField txtAuthor = new JTextField("Enter author");
    JTextField txtCategory = new JTextField("Enter category");
    JTextField txtYear = new JTextField("Enter published year");
    JTextField txtSearch = new JTextField("Search title or author");

    //Buttons
    JButton addBookBtn = new JButton("Add Book");
    JButton chooseCoverBtn = new JButton("Choose Cover");
    JButton clearBtn = new JButton("Clear");
    JButton removeBookBtn = new JButton("Remove");
    JButton editBookBtn = new JButton("Edit");
    JButton cancelEditBtn = new JButton("Cancel");

    public LibrarianGUI(Library library, Librarian librarian, Runnable onLogout) {

        this.lb = library;
        this.librarian = librarian;
        this.onLogout = onLogout;

        //Methods
        refreshTable();
        addBookFormat();
        tableFormat();
        detailsPanelFormat();
        searchActionPerformed();
        removeBtnActionPerformed();
        editBtnActionPerformed();
        userPanelFormat();

        //Adding to Frame
        this.setTitle("Greenfield University LMS");
        this.setIconImage(logo.getImage());
        this.setSize(1100, 680);
        this.setLocationRelativeTo(null);
        this.setLayout(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(Color.decode("#4F7D32"));
        this.getContentPane().setFocusable(true);
        this.add(detailsPanel);
        this.add(addBookPanel);
        this.add(scrollPane);
        this.add(txtSearch);
        this.add(cbStatus);
        this.add(cbCategory);
        this.add(removeBookBtn);
        this.add(editBookBtn);
        this.add(cancelEditBtn);
        this.add(userPanel);

    }

    public void userPanelFormat() {

        userPanel.setBackground(Color.decode("#2F5A1A"));
        userPanel.setBounds(0, 0, 1100, 70);
        userPanel.setLayout(null);

        logoLabel.setIcon(ImageUtil.smoothIcon("src/icon/Logo.png", 50, 50));
        logoLabel.setBounds(20, 10, 50, 50);

        welcomeLabel.setText("Welcome, " + librarian.getName() + " (Librarian)");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBounds(80, 20, 450, 30);

        userPanel.add(logoLabel);
        userPanel.add(welcomeLabel);

        // Each one is a small panel with an icon and a caption
        userPanel.add(new IconButton("src/icon/History.png", "Transactions", this::showTransactions).place(709, 7));
        userPanel.add(new IconButton("src/icon/AddMember.png", "Members", this::showMembers).place(796, 7));
        userPanel.add(new IconButton("src/icon/Profile.png", "Profile", this::showProfile).place(883, 7));
        userPanel.add(new IconButton("src/icon/Logout.png", "Logout", this::logout).place(970, 7));

    }

    public void refreshTable() {

        refreshCategories();
        applyFilters();

    }

    public void refreshCategories() {

        updatingFilters = true;

        Object selected = cbCategory.getSelectedItem();

        cbCategory.removeAllItems();
        cbCategory.addItem("All Categories");

        ArrayList<String> cats = new ArrayList<>();

        for (Book b : lb.getBooks()) {

            if (!cats.contains(b.getCategory())) {

                cats.add(b.getCategory());

            }

        }

        Collections.sort(cats);

        for (String c : cats) {

            cbCategory.addItem(c);

        }

        if (selected != null) {

            cbCategory.setSelectedItem(selected);

        }

        updatingFilters = false;

    }

    private Object[] rowOf(Book b) {

        return new Object[]{b.getItemID(), b.getTitle(), b.getAuthor(), b.getCategory(), b.getYear(), b.getAvailableStock() + "/" + b.getStock(), b.isAvailable() ? "Available" : "Unavailable"};

    }

    public void tableFormat() {

        scrollPane.setBackground(Color.GRAY);
        scrollPane.setBounds(20, 320, 720, 250);

        table.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {

                onSelectionChanged();

            }

        });
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.getTableHeader().setBackground(Color.GRAY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(125);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(60);
        table.getColumnModel().getColumn(6).setPreferredWidth(80);
        table.setRowHeight(25);

        center.setHorizontalAlignment(JLabel.CENTER);
        center.setVerticalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {

            table.getColumnModel().getColumn(i).setResizable(false);
            table.getColumnModel().getColumn(i).setCellRenderer(center);

        }

        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (value.equals("Available")) {

                    setHorizontalAlignment(JLabel.CENTER);
                    setVerticalAlignment(JLabel.CENTER);
                    setForeground(Color.WHITE);
                    setBackground(Color.decode("#76C457"));

                } else if (value.equals("Unavailable")) {

                    setHorizontalAlignment(JLabel.CENTER);
                    setVerticalAlignment(JLabel.CENTER);
                    setForeground(Color.WHITE);
                    setBackground(Color.decode("#DA6556"));

                }

                return this;

            }

        });

    }

    public void addBookFormat() {

        addBookPanel.add(titleLabel);
        titleLabel.setBounds(20, 40, 70, 20);
        titleLabel.setFont(new Font("Britannic Bold", Font.PLAIN, 13));

        addBookPanel.add(txtTitle);
        txtTitle.setBounds(100, 40, 180, 23);
        txtTitle.setFont(new Font("Sans Serif", Font.PLAIN, 12));
        txtTitle.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), BorderFactory.createEmptyBorder(0, 5, 0, 0)));
        setHintPlaceholder(txtTitle, "Enter title");

        addBookPanel.add(authorLabel);
        authorLabel.setBounds(20, 75, 70, 20);
        authorLabel.setFont(new Font("Britannic Bold", Font.PLAIN, 13));

        addBookPanel.add(txtAuthor);
        txtAuthor.setBounds(100, 75, 180, 23);
        txtAuthor.setFont(new Font("Sans Serif", Font.PLAIN, 12));
        txtAuthor.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), BorderFactory.createEmptyBorder(0, 5, 0, 0)));
        setHintPlaceholder(txtAuthor, "Enter author");

        addBookPanel.add(categoryLabel);
        categoryLabel.setBounds(20, 110, 70, 20);
        categoryLabel.setFont(new Font("Britannic Bold", Font.PLAIN, 13));

        addBookPanel.add(txtCategory);
        txtCategory.setBounds(100, 110, 180, 23);
        txtCategory.setFont(new Font("Sans Serif", Font.PLAIN, 12));
        txtCategory.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), BorderFactory.createEmptyBorder(0, 5, 0, 0)));
        setHintPlaceholder(txtCategory, "Enter category");

        addBookPanel.add(yearLabel);
        yearLabel.setBounds(20, 145, 70, 30);
        yearLabel.setFont(new Font("Britannic Bold", Font.PLAIN, 13));

        addBookPanel.add(txtYear);
        txtYear.setBounds(100, 145, 180, 23);
        txtYear.setFont(new Font("Sans Serif", Font.PLAIN, 12));
        txtYear.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), BorderFactory.createEmptyBorder(0, 5, 0, 0)));
        setHintPlaceholder(txtYear, "Enter year");

        addBookPanel.add(cPictureLabel);
        cPictureLabel.setBounds(420, 25, 140, 145);
        cPictureLabel.setOpaque(true);
        cPictureLabel.setHorizontalAlignment(JLabel.CENTER);
        cPictureLabel.setBackground(Color.decode("#EEEEEE"));
        cPictureLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        addBookPanel.add(addBookBtn);
        addBookBtn.setBackground(Color.decode("#266210"));
        addBookBtn.setForeground(Color.decode("#FCF9EA"));
        addBookBtn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        addBookBtn.setBounds(570, 145, 120, 25);
        addBookBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                addBtnActionPerformed();

            }

        });

        addBookPanel.add(clearBtn);
        clearBtn.setBackground(Color.decode("#57595B"));
        clearBtn.setForeground(Color.decode("#FCF9EA"));
        clearBtn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        clearBtn.setBounds(570, 85, 120, 25);
        clearBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                clearBtnActionPerformed();

            }

        });

        addBookPanel.add(chooseCoverBtn);
        chooseCoverBtn.setBackground(Color.decode("#CC6F00"));
        chooseCoverBtn.setForeground(Color.decode("#FCF9EA"));
        chooseCoverBtn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        chooseCoverBtn.setBounds(570, 115, 120, 25);
        chooseCoverBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                chooseCoverActionPerformed();

            }

        });

        addBookPanel.add(stockLabel);
        stockLabel.setBounds(300, 40, 50, 20);
        stockLabel.setFont(new Font("Britannic Bold", Font.PLAIN, 13));

        addBookPanel.add(addStock);
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) addStock.getEditor();
        editor.getTextField().setEditable(false);
        addStock.setBounds(350, 40, 50, 25);
        addStock.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        addBookPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.decode("#452829"), 2), "Book Information"));
        addBookPanel.setBackground(Color.decode("#FFD16A"));
        addBookPanel.setLayout(null);
        addBookPanel.setBounds(20, 85, 720, 190);

    }

    public void detailsPanelFormat() {

        Font detailFont = new Font("SansSerif", Font.PLAIN, 13);

        detailsPanel.add(pTitleLabel);
        pTitleLabel.setBounds(30, 330, 270, 25);
        pTitleLabel.setFont(detailFont);

        detailsPanel.add(pAuthorLabel);
        pAuthorLabel.setBounds(30, 360, 270, 25);
        pAuthorLabel.setFont(detailFont);

        detailsPanel.add(pCategoryLabel);
        pCategoryLabel.setBounds(30, 390, 270, 25);
        pCategoryLabel.setFont(detailFont);

        detailsPanel.add(pPubYearLabel);
        pPubYearLabel.setBounds(30, 420, 270, 25);
        pPubYearLabel.setFont(detailFont);

        detailsPanel.add(pStockLabel);
        pStockLabel.setBounds(30, 450, 270, 25);
        pStockLabel.setFont(detailFont);

        detailsPanel.add(pPictureLabel);
        pPictureLabel.setOpaque(true);
        pPictureLabel.setBackground(Color.decode("#EEEEEE"));
        pPictureLabel.setForeground(Color.GRAY);
        pPictureLabel.setHorizontalAlignment(JLabel.CENTER);
        pPictureLabel.setText("No book selected");
        pPictureLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        pPictureLabel.setBounds(20, 30, 280, 280);

        detailsPanel.setBackground(Color.decode("#FFD16A"));
        detailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.decode("#452829"), 2), "Preview"));
        detailsPanel.setLayout(null);
        detailsPanel.setBounds(755, 85, 320, 520);

    }

    public void searchActionPerformed() {

        cbStatus.setBounds(640, 285, 100, 25);
        cbStatus.addActionListener(e -> applyFilters());
        cbStatus.addItem("All");
        cbStatus.addItem("Available");
        cbStatus.addItem("Unavailable");

        cbCategory.setBounds(505, 285, 120, 25);
        cbCategory.addActionListener(e -> applyFilters());

        setHintPlaceholder(txtSearch, "Search title or author");
        txtSearch.setBounds(20, 285, 450, 25);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), BorderFactory.createEmptyBorder(0, 5, 0, 0)));
        txtSearch.setFont(new Font("Sans Serif", Font.PLAIN, 12));
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

    public void applyFilters() {

        if (updatingFilters || cbCategory.getSelectedItem() == null) {

            return;

        }

        String search = getFieldText(txtSearch, "Search title or author").toLowerCase();
        String cate = cbCategory.getSelectedItem().toString();
        int stat = cbStatus.getSelectedIndex();

        model.setRowCount(0);

        for (Book b : lb.getBooks()) {

            if (!search.isEmpty() && !b.getTitle().toLowerCase().contains(search) && !b.getAuthor().toLowerCase().contains(search)) {

                continue;

            } else if (!cate.equals("All Categories") && !b.getCategory().equals(cate)) {

                continue;

            } else if (stat == 1 && !b.isAvailable()) {

                continue;

            } else if (stat == 2 && b.isAvailable()) {

                continue;

            }

            model.addRow(rowOf(b));

        }

    }

    public void chooseCoverActionPerformed() {

        JFileChooser file = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("All icon", "png", "jpg", "jpeg");
        file.setFileFilter(filter);
        file.setCurrentDirectory(new File("C:\\Users\\jaleb\\OneDrive\\Pictures"));

        int result = file.showDialog(file, "Upload");

        if (result == 0) {

            String path = file.getSelectedFile().getAbsolutePath();
            ImageIcon icon = ImageUtil.scale(path, cPictureLabel.getWidth() - 2, cPictureLabel.getHeight() - 2);

            if (icon == null) {

                JOptionPane.showMessageDialog(this, "That image could not be loaded!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;

            }

            imagePath = path;
            cPictureLabel.setIcon(icon);

        }

    }

    public void clearBtnActionPerformed() {

        if (selectedBookID != -1) {

            finishEditing();
            return;

        }

        String title = getFieldText(txtTitle, "Enter title");
        String author = getFieldText(txtAuthor, "Enter author");
        String category = getFieldText(txtCategory, "Enter category");
        String year = getFieldText(txtYear, "Enter year");

        if (title.isEmpty() && author.isEmpty() && category.isEmpty() && year.isEmpty() && imagePath == null) {

            JOptionPane.showMessageDialog(this, "Nothing to clear.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;

        }

        txtTitle.setText("Enter title");
        txtAuthor.setText("Enter author");
        txtCategory.setText("Enter category");
        txtYear.setText("Enter year");
        addStock.setValue(1);

        txtTitle.setForeground(Color.gray);
        txtAuthor.setForeground(Color.gray);
        txtCategory.setForeground(Color.gray);
        txtYear.setForeground(Color.gray);

        cPictureLabel.setIcon(null);
        imagePath = null;

    }

    public void addBtnActionPerformed() {

        String title = getFieldText(txtTitle, "Enter title");
        String author = getFieldText(txtAuthor, "Enter author");
        String category = getFieldText(txtCategory, "Enter category");
        String yearText = getFieldText(txtYear, "Enter year");
        int stock = (int) addStock.getValue();

        if (title.isEmpty() || author.isEmpty() || category.isEmpty() || yearText.isEmpty()) {

            JOptionPane.showMessageDialog(this, "Please fill out all Information!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;

        }

        int year;

        try {

            year = Integer.parseInt(yearText);

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(this, "Published year must contain numbers only!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;

        }

        if (year < 1000 || year > 2026) {

            JOptionPane.showMessageDialog(this, "Please enter a year from 1000 - Present only!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;

        }

        if (lb.bookExists(title, author, category, year, -1)) {

            JOptionPane.showMessageDialog(this, "This book already exists in the library!\nTo add more copies, edit the existing book's stock instead.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;

        }

        Book newBook = new Book(lb.nextBookID(), title, author, category, year, stock);
        newBook.setCover(imagePath);
        lb.addBook(newBook);

        JOptionPane.showMessageDialog(this, "Book has been successfully added!", "Information", JOptionPane.INFORMATION_MESSAGE);
        refreshTable();
        applyFilters();
        clearBtnActionPerformed();

    }

    public void editBtnActionPerformed() {

        editBookBtn.setEnabled(false);
        editBookBtn.setBackground(Color.decode("#ECB65F"));
        editBookBtn.setForeground(Color.decode("#FCF9EA"));
        editBookBtn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        editBookBtn.setBounds(120, 580, 90, 30);
        editBookBtn.addActionListener(e -> {

            if (selectedBookID == -1) {
                int row = table.getSelectedRow();

                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Please select a book first!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                loadSelectedBook();

                editBookBtn.setText("Save Changes");
                cancelEditBtn.setVisible(true);
                addBookBtn.setEnabled(false);

                return;
            }

            String title = getFieldText(txtTitle, "Enter title");
            String author = getFieldText(txtAuthor, "Enter author");
            String category = getFieldText(txtCategory, "Enter category");
            String yearText = getFieldText(txtYear, "Enter year");
            int stock = (int) addStock.getValue();

            if (title.isEmpty() || author.isEmpty()
                    || category.isEmpty() || yearText.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this, "Please fill out all information!",
                        "Warning", JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            int year;

            try {
                year = Integer.parseInt(yearText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this, "Published year must contain numbers only!",
                        "Warning", JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (year < 1000 || year > 2026) {
                JOptionPane.showMessageDialog(
                        this, "Please enter a year from 1000 to 2026!",
                        "Warning", JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            Book selectedBook = null;

            for (Book b : lb.getBooks()) {
                if (b.getItemID() == selectedBookID) {
                    selectedBook = b;
                    break;
                }
            }

            if (selectedBook == null) {
                return;
            }

            if (title.equals(selectedBook.getTitle())
                    && author.equals(selectedBook.getAuthor())
                    && category.equals(selectedBook.getCategory())
                    && year == selectedBook.getYear()
                    && stock == selectedBook.getStock()
                    && sameCover(imagePath, selectedBook.getCover())) {

                JOptionPane.showMessageDialog(
                        this, "No changes have been made!",
                        "Information", JOptionPane.INFORMATION_MESSAGE
                );
                if (lb.bookExists(title, author, category, year, selectedBook.getItemID())) {

                    JOptionPane.showMessageDialog(this, "Another book with the same title, author, category and year already exists!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;

                }
                return;
            }

            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Do you want to save changes?",
                    "Confirmation",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    options, // {"Confirm", "Cancel"}
                    options[0]
            );

            if (choice != 0) {
                return;
            }

            if (!selectedBook.setStock(stock)) {

                JOptionPane.showMessageDialog(
                        this, selectedBook.getBorrowedCount() + " copy/copies are currently borrowed,\nso the stock can't be lower than that!",
                        "Warning", JOptionPane.WARNING_MESSAGE
                );
                return;

            }

            selectedBook.setTitle(title);
            selectedBook.setAuthor(author);
            selectedBook.setCategory(category);
            selectedBook.setYear(year);
            selectedBook.setCover(imagePath);
            lb.saveBooks();

            JOptionPane.showMessageDialog(
                    this, "Book has been successfully edited!",
                    "Information", JOptionPane.INFORMATION_MESSAGE
            );

            finishEditing();

        });

        cancelEditBtn.setBounds(220, 580, 90, 30);
        cancelEditBtn.setBackground(Color.GRAY);
        cancelEditBtn.setForeground(Color.WHITE);
        cancelEditBtn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        cancelEditBtn.setVisible(false);
        cancelEditBtn.addActionListener(e -> {
            finishEditing();
        });

    }

    public void removeBtnActionPerformed() {

        removeBookBtn.setEnabled(false);
        removeBookBtn.setBackground(Color.decode("#B93636"));
        removeBookBtn.setForeground(Color.decode("#FCF9EA"));
        removeBookBtn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        removeBookBtn.setBounds(20, 580, 90, 30);
        removeBookBtn.addActionListener(e -> {

            int row[] = table.getSelectedRows();

            if (row.length == 0) {

                JOptionPane.showMessageDialog(this, "Please select a book first!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;

            }

            ArrayList<Integer> selectedID = new ArrayList<>();
            ArrayList<Book> toRemove = new ArrayList<>();

            for (int i = 0; i < row.length; i++) {

                selectedID.add((int) table.getValueAt(row[i], 0));

            }

            for (Book b : lb.getBooks()) {

                if (selectedID.contains(b.getItemID())) {

                    if (b.getBorrowedCount() > 0) {

                        JOptionPane.showMessageDialog(this, "\"" + b.getTitle() + "\" still has " + b.getBorrowedCount() + " copy/copies borrowed.\nIt can only be removed after all copies are returned.", "Cannot Remove", JOptionPane.WARNING_MESSAGE);
                        return;

                    }

                    toRemove.add(b);

                }

            }

            int choice = JOptionPane.showOptionDialog(
                    this, "Are you sure you want to delete " + (row.length == 1 ? "this" : "these " + row.length) + (row.length == 1 ? " book?" : " books?"), "Confirmation", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == 0) {

                for (Book b : toRemove) {

                    lb.removeBook(b);

                }

                JOptionPane.showMessageDialog(this, "Book has been successfully deleted!", "Information", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
                removeBookBtn.setEnabled(false);

            }

        });

    }

    public void loadSelectedBook() {
        int row = table.getSelectedRow();

        if (row == -1) {
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);

        selectedBookID = (int) model.getValueAt(modelRow, 0);

        txtTitle.setText(model.getValueAt(modelRow, 1).toString());
        txtAuthor.setText(model.getValueAt(modelRow, 2).toString());
        txtCategory.setText(model.getValueAt(modelRow, 3).toString());
        txtYear.setText(model.getValueAt(modelRow, 4).toString());

        txtTitle.setForeground(Color.BLACK);
        txtAuthor.setForeground(Color.BLACK);
        txtCategory.setForeground(Color.BLACK);
        txtYear.setForeground(Color.BLACK);

        Book b = lb.findBook(selectedBookID);
        addStock.setValue(b.getStock());
        imagePath = (b == null) ? null : b.getCover();
        cPictureLabel.setIcon(ImageUtil.scale(imagePath, cPictureLabel.getWidth() - 2, cPictureLabel.getHeight() - 2));

        addBookBtn.setEnabled(false);
        editBookBtn.setText("Save Changes");
    }

    public void finishEditing() {

        selectedBookID = -1;

        txtTitle.setText("Enter title");
        txtAuthor.setText("Enter author");
        txtCategory.setText("Enter category");
        txtYear.setText("Enter year");

        txtTitle.setForeground(Color.GRAY);
        txtAuthor.setForeground(Color.GRAY);
        txtCategory.setForeground(Color.GRAY);
        txtYear.setForeground(Color.GRAY);

        addStock.setValue(1);

        cPictureLabel.setIcon(null);
        imagePath = null;

        addBookBtn.setEnabled(true);
        editBookBtn.setText("Edit");
        editBookBtn.setEnabled(false);
        cancelEditBtn.setVisible(false);

        refreshTable();
        applyFilters();
    }

    private boolean sameCover(String a, String b) {

        if (a == null) {

            return b == null;

        }

        return a.equals(b);

    }

    private void onSelectionChanged() {

        int count = table.getSelectedRowCount();

        removeBookBtn.setEnabled(count > 0);

        if (selectedBookID == -1) {

            editBookBtn.setEnabled(count == 1);

        }

        if (count == 1) {

            showDetails(lb.findBook((int) table.getValueAt(table.getSelectedRow(), 0)));

        } else {

            clearDetails();

        }

    }

    private void showDetails(Book b) {

        if (b == null) {

            clearDetails();
            return;

        }

        pTitleLabel.setText("Title: " + b.getTitle());
        pTitleLabel.setToolTipText(b.getTitle());
        pAuthorLabel.setText("Author: " + b.getAuthor());
        pCategoryLabel.setText("Category: " + b.getCategory());
        pPubYearLabel.setText("Pub. Year: " + b.getYear());
        pStockLabel.setText("Stock: " + b.getAvailableStock() + "/" + b.getStock());

        ImageIcon icon = ImageUtil.scale(b.getCover(), pPictureLabel.getWidth() - 2, pPictureLabel.getHeight() - 2);
        pPictureLabel.setIcon(icon);
        pPictureLabel.setText(icon == null ? "No Cover" : "");

    }

    private void clearDetails() {

        pTitleLabel.setText("Title:");
        pTitleLabel.setToolTipText(null);
        pAuthorLabel.setText("Author:");
        pCategoryLabel.setText("Category:");
        pPubYearLabel.setText("Pub. Year:");
        pStockLabel.setText("Stock:");
        pPictureLabel.setIcon(null);
        pPictureLabel.setText("No book selected");

    }

    private void showTransactions() {

        new TransactionDialog(this, "All Transactions", lb.getTransactions(), true).setVisible(true);

    }

    private void showMembers() {

        new MembersDialog(this, lb, librarian).setVisible(true);

    }

    private void showProfile() {

        new ProfileDialog(this, lb, librarian).setVisible(true);

    }

    private void logout() {

        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Logout", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {

            onLogout.run();
            dispose();

        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

}
