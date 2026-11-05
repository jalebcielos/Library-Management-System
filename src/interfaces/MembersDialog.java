package interfaces;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import mainpackage.Library;
import users.*;

public class MembersDialog extends JDialog {

    private Library lb;
    private Librarian admin;

    private String column[] = {"Member ID", "Name", "Username", "Books Borrowed"};

    private DefaultTableModel model = new DefaultTableModel(column, 0) {

        @Override
        public boolean isCellEditable(int row, int col) {

            return false;

        }

    };

    private JTable table = new JTable(model);

    public MembersDialog(JFrame owner, Library lb, Librarian admin) {

        super(owner, "Members", true);

        this.lb = lb;
        this.admin = admin;

        setSize(700, 470);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(owner);
        getContentPane().setBackground(Color.decode("#4F7D32"));

        JPanel header = new JPanel(null);
        header.setBackground(Color.decode("#2F5A1A"));
        header.setBounds(0, 0, 700, 70);

        JLabel icon = new JLabel(ImageUtil.smoothIcon("src/icon/AddMember.png", 36, 36));
        icon.setBounds(20, 17, 36, 36);
        header.add(icon);

        JLabel title = new JLabel("Manage Members");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setBounds(70, 20, 300, 30);
        header.add(title);

        add(header);

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

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(20, 90, 645, 270);
        add(scroll);

        JButton addBtn = makeButton("Add Member", "#266210", 20);
        addBtn.addActionListener(e -> addMember());

        JButton passBtn = makeButton("Change Password", "#CC6F00", 175);
        passBtn.addActionListener(e -> changePassword());

        JButton deleteBtn = makeButton("Delete Member", "#B93636", 330);
        deleteBtn.addActionListener(e -> deleteMember());

        JButton closeBtn = makeButton("Close", "#57595B", 545);
        closeBtn.addActionListener(e -> dispose());

        refresh();

    }

    private JButton makeButton(String text, String color, int x) {

        JButton b = new JButton(text);
        b.setBackground(Color.decode(color));
        b.setForeground(Color.decode("#FCF9EA"));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        b.setBounds(x, 385, x == 545 ? 120 : 145, 34);
        add(b);
        return b;

    }

    private void refresh() {

        model.setRowCount(0);

        for (Member m : lb.getMembers()) {

            model.addRow(new Object[]{m.getUserID(), m.getName(), m.getUsername(), m.getBooks().size()});

        }

    }

    private Member getSelectedMember() {

        int row = table.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(this, "Please select a member first!", "Warning", JOptionPane.WARNING_MESSAGE);
            return null;

        }

        return lb.findMember((String) table.getValueAt(row, 0));

    }

    private void addMember() {

        JTextField nameField = new JTextField();
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 5));
        form.add(new JLabel("Full name:"));
        form.add(nameField);
        form.add(new JLabel("Student ID:"));
        form.add(userField);
        form.add(new JLabel("Password (at least " + Library.MIN_PASSWORD + " characters):"));
        form.add(passField);

        while (true) {

            int result = JOptionPane.showConfirmDialog(this, form, "Add Member", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result != JOptionPane.OK_OPTION) {

                return;

            }

            String name = nameField.getText().trim();
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword());

            if (name.isEmpty() || user.isEmpty() || pass.isEmpty()) {

                warn("Please fill out all information!");
                continue;

            }

            String problem = Library.usernameProblem(user);

            if (problem == null) {

                problem = Library.passwordProblem(pass);

            }

            if (problem != null) {

                warn(problem);
                continue;

            }

            if (lb.usernameExists(user)) {

                warn("That username is already taken!");
                continue;

            }

            Member member = new Member(user, name, user, pass);
            lb.addMember(member);
            refresh();

            JOptionPane.showMessageDialog(this, "Member added!\nMember ID: " + member.getUserID(), "Information", JOptionPane.INFORMATION_MESSAGE);
            return;

        }

    }

    private void changePassword() {

        Member member = getSelectedMember();

        if (member == null) {

            return;

        }

        JPasswordField newPass = new JPasswordField();
        JPasswordField confirm = new JPasswordField();

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 5));
        form.add(new JLabel("New password for " + member.getName() + " (at least " + Library.MIN_PASSWORD + " characters):"));
        form.add(newPass);
        form.add(new JLabel("Confirm new password:"));
        form.add(confirm);

        while (true) {

            int result = JOptionPane.showConfirmDialog(this, form, "Change Member Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result != JOptionPane.OK_OPTION) {

                return;

            }

            String newText = new String(newPass.getPassword());
            String confirmText = new String(confirm.getPassword());

            if (member.checkPassword(newText)) {

                warn("The new password can't be the same as their current password!");
                continue;

            }

            String problem = Library.passwordProblem(newText);

            if (problem != null) {

                warn(problem);
                continue;

            }

            if (!newText.equals(confirmText)) {

                warn("The new passwords don't match!");
                continue;

            }

            member.setPassword(newText);
            lb.saveUsers();
            JOptionPane.showMessageDialog(this, "Password updated for " + member.getName() + "!", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;

        }

    }

    private void deleteMember() {

        Member member = getSelectedMember();

        if (member == null) {

            return;

        }

        if (!member.getBooks().isEmpty()) {

            warn(member.getName() + " still has " + member.getBooks().size() + " borrowed book/s.\nThe account can only be deleted after they are returned.");
            return;

        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this member?\n\n" + member.getName() + " (" + member.getUserID() + ")\n\nThis cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {

            return;

        }

        JPasswordField passField = new JPasswordField();
        JPanel form = new JPanel(new GridLayout(0, 1, 0, 5));
        form.add(new JLabel("Enter your admin password to delete this member:"));
        form.add(passField);

        int result = JOptionPane.showConfirmDialog(this, form, "Admin Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {

            return;

        }

        if (!admin.checkPassword(new String(passField.getPassword()))) {

            warn("Incorrect password. The member was not deleted.");
            return;

        }

        lb.removeMember(member);
        refresh();
        JOptionPane.showMessageDialog(this, "Member has been deleted.", "Information", JOptionPane.INFORMATION_MESSAGE);

    }

    private void warn(String message) {

        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);

    }

}
