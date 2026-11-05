package interfaces;

import java.awt.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import mainpackage.*;
import users.Member;

public class BorrowReturnDialog extends JDialog {

    private Library lb;
    private Member member;
    private boolean borrowing;

    private String borrowColumns[] = {"ID", "Title", "Author", "Category", "Copies Left"};
    private String returnColumns[] = {"ID", "Title", "Author", "Category", "Due Date", "Current Fine"};

    private DefaultTableModel model;
    private JTable table;
    private JTextField txtSearch = new JTextField("Search title or author");
    private JLabel infoLabel = new JLabel();
    private JButton actionBtn;

    public BorrowReturnDialog(JFrame owner, Library lb, Member member, boolean borrowing) {

        super(owner, borrowing ? "Borrow a Book" : "Return a Book", true);

        this.lb = lb;
        this.member = member;
        this.borrowing = borrowing;

        setSize(720, 530);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(owner);
        getContentPane().setBackground(Color.decode("#4F7D32"));

        //HEADER
        JPanel header = new JPanel(null);
        header.setBackground(Color.decode("#2F5A1A"));
        header.setBounds(0, 0, 720, 70);

        JLabel title = new JLabel(borrowing ? "Borrow a Book" : "Return a Book");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setBounds(25, 10, 400, 30);
        header.add(title);

        JLabel sub = new JLabel(borrowing ? "Select a book from the list, then press Borrow." : "Select the book you want to give back, then press Return.");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(Color.decode("#CFE3BF"));
        sub.setBounds(25, 40, 600, 20);
        header.add(sub);

        add(header);

        //SEARCH
        txtSearch.setBounds(20, 90, 450, 30);
        txtSearch.setForeground(Color.GRAY);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), BorderFactory.createEmptyBorder(0, 5, 0, 0)));
        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent e) {

                if (txtSearch.getText().equals("Search title or author")) {

                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);

                }

            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {

                if (txtSearch.getText().trim().isEmpty()) {

                    txtSearch.setText("Search title or author");
                    txtSearch.setForeground(Color.GRAY);

                }

            }

        });
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {

                refreshList();

            }

            @Override
            public void removeUpdate(DocumentEvent e) {

                refreshList();

            }

            @Override
            public void changedUpdate(DocumentEvent e) {

                refreshList();

            }

        });
        add(txtSearch);

        //TABLE
        model = new DefaultTableModel(borrowing ? borrowColumns : returnColumns, 0) {

            @Override
            public boolean isCellEditable(int row, int col) {

                return false;

            }

        };

        table = new JTable(model);
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(Color.GRAY);
        table.getTableHeader().setForeground(Color.WHITE);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {

            table.getColumnModel().getColumn(i).setCellRenderer(center);

        }

        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);

        if (!borrowing) {

            table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {

                    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    setHorizontalAlignment(JLabel.CENTER);
                    setForeground(Color.WHITE);
                    setBackground(value.equals("None") ? Color.decode("#76C457") : Color.decode("#DA6556"));
                    return this;

                }

            });

        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(20, 135, 665, 270);
        add(scroll);

        infoLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setBounds(20, 412, 665, 25);
        add(infoLabel);

        //BUTTONS
        actionBtn = new JButton(borrowing ? "Borrow" : "Return");
        actionBtn.setBackground(Color.decode(borrowing ? "#266210" : "#CC6F00"));
        actionBtn.setForeground(Color.decode("#FCF9EA"));
        actionBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        actionBtn.setFocusPainted(false);
        actionBtn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        actionBtn.setBounds(430, 445, 120, 36);
        actionBtn.addActionListener(e -> doAction());
        add(actionBtn);

        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(Color.decode("#57595B"));
        closeBtn.setForeground(Color.decode("#FCF9EA"));
        closeBtn.setFocusPainted(false);
        closeBtn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        closeBtn.setBounds(565, 445, 120, 36);
        closeBtn.addActionListener(e -> dispose());
        add(closeBtn);

        refreshList();

    }

    private void refreshList() {

        String search = txtSearch.getText().trim();

        if (search.equals("Search title or author")) {

            search = "";

        }

        search = search.toLowerCase();

        model.setRowCount(0);

        java.util.ArrayList<Book> source = borrowing ? lb.getBooks() : member.getBooks();

        for (Book b : source) {

            if (borrowing && (!b.isAvailable() || member.getBooks().contains(b))) {

                continue;

            }

            if (!search.isEmpty() && !b.getTitle().toLowerCase().contains(search) && !b.getAuthor().toLowerCase().contains(search)) {

                continue;

            }

            if (borrowing) {

                model.addRow(new Object[]{b.getItemID(), b.getTitle(), b.getAuthor(), b.getCategory(), b.getAvailableStock() + "/" + b.getStock()});

            } else {

                double fine = Library.fineFor(member.getDueDate(b));

                model.addRow(new Object[]{b.getItemID(), b.getTitle(), b.getAuthor(), b.getCategory(), Library.dateText(member.getDueDate(b)), fine > 0 ? Library.moneyText(fine) : "None"});

            }

        }

        if (model.getRowCount() == 0) {

            infoLabel.setText(borrowing ? "No books are available to borrow right now." : "You have no books to return.");

        } else {

            infoLabel.setText("You currently have " + member.getBooks().size() + " book/s borrowed.");

        }

    }

    private void doAction() {

        int row = table.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(this, "Please select a book first!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;

        }

        Book b = lb.findBook((int) table.getValueAt(row, 0));

        if (b == null) {

            return;

        }

        String verb = borrowing ? "Borrow" : "Return";
        String extra = "";
        double fine = 0;

        if (borrowing) {

            extra = "\n\nYou must return it within " + Library.LOAN_DAYS + " days (due " + Library.dateText(LocalDate.now().plusDays(Library.LOAN_DAYS)) + ")."
                    + "\nA fine of " + Library.moneyText(Library.FINE_PER_DAY) + " per day is charged for late returns.";

        } else {

            fine = Library.fineFor(member.getDueDate(b));

            if (fine > 0) {

                extra = "\n\nThis book is " + Library.daysLate(member.getDueDate(b)) + " day/s overdue."
                        + "\nFine to pay: " + Library.moneyText(fine);

            }

        }

        int choice = JOptionPane.showConfirmDialog(this, verb + " \"" + b.getTitle() + "\"?" + extra, "Confirm " + verb, JOptionPane.YES_NO_OPTION);

        if (choice != JOptionPane.YES_OPTION) {

            return;

        }

        boolean done = borrowing ? lb.borrowBook(member, b) : lb.returnBook(member, b);

        if (done) {

            String message = "You have " + (borrowing ? "borrowed" : "returned") + " \"" + b.getTitle() + "\".";

            if (borrowing) {

                message += "\nPlease return it on or before " + Library.dateText(member.getDueDate(b)) + ".";

            } else if (fine > 0) {

                message += "\nIt was returned late. Please pay the fine of " + Library.moneyText(fine) + " at the library desk.";

            }

            JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);

        } else {

            JOptionPane.showMessageDialog(this, "Sorry, that couldn't be done.", "Warning", JOptionPane.WARNING_MESSAGE);

        }

        refreshList();

    }

}
