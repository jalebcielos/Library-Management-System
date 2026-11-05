package loginpage;

import interfaces.LibrarianGUI;
import interfaces.MemberGUI;
import javax.swing.*;
import java.awt.event.*;
import mainpackage.Library;
import users.*;

public class LoginPage extends Frames implements ActionListener {

    private Library library;

    public LoginPage(Library library) {

        this.library = library;

        loginbtn.addActionListener(this);
        username.addActionListener(this);
        password.addActionListener(this);

        this.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        login();

    }

    public void login() {

        String usernameField = username.getText().trim();
        String passwordField = new String(password.getPassword());

        if (usernameField.isEmpty() || passwordField.isEmpty()) {

            JOptionPane.showMessageDialog(this, "Please Fill in all Fields!");
            return;

        }

        User user = library.login(usernameField, passwordField);

        if (user == null) {

            JOptionPane.showMessageDialog(this, "Invalid username or password!");
            return;

        }

        JOptionPane.showMessageDialog(this, "Login Successfully!");

        if (user instanceof Librarian) {

            new LibrarianGUI(library, (Librarian) user, this::showLogin).setVisible(true);

        } else if (user instanceof Member) {

            new MemberGUI(library, (Member) user, this::showLogin).setVisible(true);

        }

        this.setVisible(false);

    }

    public void showLogin() {

        username.setText("");
        password.setText("");
        this.setVisible(true);

    }

}
