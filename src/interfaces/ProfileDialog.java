package interfaces;

import java.awt.*;
import javax.swing.*;
import mainpackage.Library;
import users.Librarian;
import users.User;

//PROFILE WINDOW
public class ProfileDialog extends JDialog {

    private Library lb;
    private User user;

    private JTextField usernameField = new JTextField();

    public ProfileDialog(JFrame owner, Library lb, User user) {

        super(owner, "My Profile", true);

        this.lb = lb;
        this.user = user;

        setSize(450, 440);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(owner);
        getContentPane().setBackground(Color.decode("#FFF1D6"));

        //HEADER
        JPanel header = new JPanel(null);
        header.setBackground(Color.decode("#2F5A1A"));
        header.setBounds(0, 0, 450, 70);

        JLabel icon = new JLabel(ImageUtil.smoothIcon("src/icon/Profile.png", 40, 40));
        icon.setBounds(20, 15, 40, 40);
        header.add(icon);

        JLabel title = new JLabel("My Profile");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setBounds(75, 20, 300, 30);
        header.add(title);

        add(header);

        //ID
        addRow("User ID", 100);
        JTextField idField = new JTextField(user.getUserID());
        styleReadOnly(idField);
        idField.setBounds(130, 95, 170, 30);
        add(idField);

        //NAME
        addRow("Name", 150);
        JTextField nameField = new JTextField(user.getName());
        styleReadOnly(nameField);
        nameField.setBounds(130, 145, 170, 30);
        add(nameField);

        //USERNAME
        addRow("Username", 200);
        usernameField.setText(user.getUsername());
        styleReadOnly(usernameField);
        usernameField.setBounds(130, 195, 170, 30);
        add(usernameField);

//        if (user instanceof Librarian) {
//
//            JButton editUsername = smallButton("Edit");
//            editUsername.setBounds(315, 195, 90, 30);
//            editUsername.addActionListener(e -> editUsername());
//            add(editUsername);
//
//        }

        //ROLE
        addRow("Role", 250);
        JTextField roleField = new JTextField(user.getRole());
        styleReadOnly(roleField);
        roleField.setBounds(130, 245, 170, 30);
        add(roleField);

        //PASSWORD
        addRow("Password", 300);
        JPasswordField passField = new JPasswordField("********");
        styleReadOnly(passField);
        passField.setBounds(130, 295, 170, 30);
        add(passField);

        JButton changePass = smallButton("Change");
        changePass.setBounds(315, 295, 90, 30);
        changePass.addActionListener(e -> changePassword());
        add(changePass);

        JButton close = new JButton("Close");
        close.setBackground(Color.GRAY);
        close.setForeground(Color.WHITE);
        close.setFocusPainted(false);
        close.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        close.setBounds(165, 360, 120, 32);
        close.addActionListener(e -> dispose());
        add(close);

    }

    private void addRow(String text, int y) {

        JLabel label = new JLabel(text + ":");
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setBounds(30, y, 95, 20);
        add(label);

    }

    private void styleReadOnly(JTextField field) {

        field.setEditable(false);
        field.setBackground(Color.decode("#EEEEEE"));
        field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), BorderFactory.createEmptyBorder(0, 6, 0, 0)));

    }

    private JButton smallButton(String text) {

        JButton b = new JButton(text);
        b.setBackground(Color.decode("#F29F05"));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder());
        return b;

    }

    private void editUsername() {

        while (true) {

            String input = JOptionPane.showInputDialog(this, "New username (at least " + Library.MIN_USERNAME + " characters):", user.getUsername());

            if (input == null) {

                return;

            }

            input = input.trim();

            if (input.equals(user.getUsername())) {

                warn("That is already your username!");
                continue;

            }

            String problem = Library.usernameProblem(input);

            if (problem != null) {

                warn(problem);
                continue;

            }

            if (!input.equalsIgnoreCase(user.getUsername()) && lb.usernameExists(input)) {

                warn("That username is already taken!");
                continue;

            }

            user.setUsername(input);
            lb.saveUsers();
            usernameField.setText(input);
            JOptionPane.showMessageDialog(this, "Username updated!", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;

        }

    }

    private void changePassword() {

        JPasswordField current = new JPasswordField();
        JPasswordField newPass = new JPasswordField();
        JPasswordField confirm = new JPasswordField();

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 5));
        form.add(new JLabel("Current password:"));
        form.add(current);
        form.add(new JLabel("New password (at least " + Library.MIN_PASSWORD + " characters):"));
        form.add(newPass);
        form.add(new JLabel("Confirm new password:"));
        form.add(confirm);

        while (true) {

            int result = JOptionPane.showConfirmDialog(this, form, "Change Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result != JOptionPane.OK_OPTION) {

                return;

            }

            String oldText = new String(current.getPassword());
            String newText = new String(newPass.getPassword());
            String confirmText = new String(confirm.getPassword());

            if (!user.checkPassword(oldText)) {

                warn("Your current password is incorrect!");
                continue;

            }

            if (newText.equals(oldText)) {

                warn("Your new password can't be the same as your current password!");
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

            user.setPassword(newText);
            lb.saveUsers();
            JOptionPane.showMessageDialog(this, "Password updated!", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;

        }

    }

    private void warn(String message) {

        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);

    }

}
