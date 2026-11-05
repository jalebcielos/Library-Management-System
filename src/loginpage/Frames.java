package loginpage;

import javax.swing.*;
import java.awt.*;

public class Frames extends JFrame {
    
    protected JTextField username;
    protected JPasswordField password;
    protected JPanel whitepanel;        
    protected JPanel greenpanel;  
    protected JButton loginbtn;
    protected JButton confirmbtn;
    protected JLabel userlbl;
    protected JLabel passlbl;
    protected JLabel confirmlbl;
    protected JLabel Lib;

    public Frames() {
        
        setTitle("Library Management System");
        setSize(1000, 650);
        setLayout(new GridLayout(1, 2));

        //GREEN PANEL
        greenpanel = new JPanel();
        greenpanel.setLayout(null);
        greenpanel.setBackground(Color.decode("#346739"));

        ImageIcon logoIcon = new ImageIcon("src/icon/Logo.png");
        Image scaledLogo = logoIcon.getImage().getScaledInstance(650, 650, Image.SCALE_SMOOTH);
        JLabel logo = new JLabel(new ImageIcon(scaledLogo));
        logo.setBounds(35, -20, 650, 650);
        greenpanel.add(logo);

        //WHITEPANEL
        whitepanel = new JPanel();
        whitepanel.setLayout(null);
        whitepanel.setBackground(Color.WHITE);

        JLabel heading = new JLabel("Login");
        heading.setFont(new Font("Arial", Font.BOLD, 32));
        heading.setForeground(new Color(34, 139, 34));
        heading.setBounds(100, 150, 300, 40);
        whitepanel.add(heading);

        username = new JTextField(15);
        password = new JPasswordField(15);
        userlbl = new JLabel("Username");
        passlbl = new JLabel("Password");
        loginbtn = new JButton("Login");

        Font labelFont = new Font("Bahnschrift", Font.BOLD, 14);
        Font fieldFont = new Font("Bahnschrift", Font.PLAIN, 15);

        userlbl.setFont(labelFont);
        passlbl.setFont(labelFont);
        username.setFont(fieldFont);
        password.setFont(fieldFont);

        userlbl.setBounds(100, 220, 300, 20);
        username.setBounds(100, 245, 300, 35);
        passlbl.setBounds(100, 295, 300, 20);
        password.setBounds(100, 320, 300, 35);
        loginbtn.setBounds(100, 385, 300, 40);

        loginbtn.setBackground(new Color(34, 139, 34));
        loginbtn.setForeground(Color.WHITE);
        loginbtn.setFont(new Font("Arial", Font.BOLD, 16));
        loginbtn.setFocusPainted(false);
        loginbtn.setBorderPainted(false);
        loginbtn.setOpaque(true);   

        whitepanel.add(userlbl);
        whitepanel.add(username);
        whitepanel.add(passlbl);
        whitepanel.add(password);
        whitepanel.add(loginbtn);

        add(greenpanel);
        add(whitepanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        setLocationRelativeTo(null);
        
    }
}