package interfaces;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.*;
import mainpackage.Transaction;

public class TransactionDialog extends JDialog {

    public TransactionDialog(JFrame owner, String heading, ArrayList<Transaction> list, boolean showMember) {

        super(owner, heading, true);

        String[] columns = showMember
                ? new String[]{"Date & Time", "Member ID", "Member", "Book ID", "Title", "Action", "Due Date", "Fine"}
                : new String[]{"Date & Time", "Book ID", "Title", "Action", "Due Date", "Fine"};

        DefaultTableModel model = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int col) {

                return false;

            }

        };

        for (int i = list.size() - 1; i >= 0; i--) {

            Transaction t = list.get(i);

            if (showMember) {

                model.addRow(new Object[]{t.getTimeText(), t.getMemberID(), t.getMemberName(), t.getBookID(), t.getBookTitle(), t.getAction(), t.getDueText(), t.getFineText()});

            } else {

                model.addRow(new Object[]{t.getTimeText(), t.getBookID(), t.getBookTitle(), t.getAction(), t.getDueText(), t.getFineText()});

            }

        }

        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(Color.GRAY);
        table.getTableHeader().setForeground(Color.WHITE);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {

            table.getColumnModel().getColumn(i).setCellRenderer(center);

        }

        table.getColumnModel().getColumn(0).setPreferredWidth(190);

        if (showMember) {

            table.getColumnModel().getColumn(1).setPreferredWidth(80);
            table.getColumnModel().getColumn(3).setPreferredWidth(80);
            table.getColumnModel().getColumn(4).setPreferredWidth(170);

        } else {

            table.getColumnModel().getColumn(1).setPreferredWidth(80);
            table.getColumnModel().getColumn(2).setPreferredWidth(200);

        }

        int actionCol = showMember ? 5 : 3;

        table.getColumnModel().getColumn(actionCol).setCellRenderer(new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setForeground(Color.WHITE);
                setBackground(value.equals("Borrowed") ? Color.decode("#F29F05") : Color.decode("#76C457"));
                return this;

            }

        });

        JLabel totalLabel = new JLabel("  Total records: " + list.size());
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        totalLabel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(totalLabel, BorderLayout.SOUTH);
        setSize(showMember ? 1050 : 850, 400);
        setLocationRelativeTo(owner);

    }

}
