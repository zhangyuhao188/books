package com.book.frame;

import com.book.utils.DatabaseUtils;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterFrame extends JFrame {
    private JTextField usernameField, phoneField, emailField;
    private JPasswordField passwordField;
    private JComboBox<String> genderBox;
    private JButton registerButton, backButton;

    public RegisterFrame() {
        setTitle("图书管理系统 - 注册");
        setSize(450, 350); // 设置窗口大小
        setResizable(false); // 禁止调整窗口大小
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 设置字体
        Font labelFont = new Font("微软雅黑", Font.BOLD, 14);
        Font inputFont = new Font("微软雅黑", Font.PLAIN, 14);
        Font buttonFont = new Font("微软雅黑", Font.PLAIN, 12);

        // 创建组件
        JLabel titleLabel = new JLabel("注册新账户");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel usernameLabel = new JLabel("用户名：");
        usernameLabel.setFont(labelFont);
        usernameField = new JTextField();
        usernameField.setFont(inputFont);
        usernameField.setHorizontalAlignment(SwingConstants.CENTER); // 居中显示

        JLabel passwordLabel = new JLabel("密码：");
        passwordLabel.setFont(labelFont);
        passwordField = new JPasswordField();
        passwordField.setFont(inputFont);
        passwordField.setHorizontalAlignment(SwingConstants.CENTER); // 居中显示

        JLabel genderLabel = new JLabel("性别：");
        genderLabel.setFont(labelFont);
        genderBox = new JComboBox<>(new String[]{"男", "女", "其他"});
        genderBox.setFont(inputFont);

        JLabel phoneLabel = new JLabel("手机号：");
        phoneLabel.setFont(labelFont);
        phoneField = new JTextField();
        phoneField.setFont(inputFont);
        phoneField.setHorizontalAlignment(SwingConstants.CENTER); // 居中显示

        JLabel emailLabel = new JLabel("邮箱：");
        emailLabel.setFont(labelFont);
        emailField = new JTextField();
        emailField.setFont(inputFont);
        emailField.setHorizontalAlignment(SwingConstants.CENTER); // 居中显示

        registerButton = new JButton("注册");
        registerButton.setFont(buttonFont);
        registerButton.setBackground(new Color(70, 130, 180));
        registerButton.setForeground(Color.WHITE);

        backButton = new JButton("返回");
        backButton.setFont(buttonFont);
        backButton.setBackground(new Color(100, 149, 237));
        backButton.setForeground(Color.WHITE);

        // 设置布局
        JPanel formPanel = new JPanel();
        GroupLayout layout = new GroupLayout(formPanel);
        formPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // 水平布局
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(titleLabel)
                .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(usernameLabel)
                                .addComponent(passwordLabel)
                                .addComponent(genderLabel)
                                .addComponent(phoneLabel)
                                .addComponent(emailLabel))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(usernameField, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                                .addComponent(passwordField, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                                .addComponent(genderBox, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                                .addComponent(phoneField, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                                .addComponent(emailField, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createSequentialGroup()
                        .addComponent(registerButton, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                        .addGap(30)
                        .addComponent(backButton, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE))
        );

        // 垂直布局
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(titleLabel)
                .addGap(20)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(usernameLabel)
                        .addComponent(usernameField, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(passwordLabel)
                        .addComponent(passwordField, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(genderLabel)
                        .addComponent(genderBox, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(phoneLabel)
                        .addComponent(phoneField, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(emailLabel)
                        .addComponent(emailField, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
                .addGap(20)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(registerButton, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                        .addComponent(backButton, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
        );

        // 添加事件监听
        registerButton.addActionListener(e -> register());
        backButton.addActionListener(e -> backToLogin());

        // 添加布局到窗口
        add(formPanel, BorderLayout.CENTER);

        // 居中显示
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void register() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String gender = genderBox.getSelectedItem().toString();
        String phone = phoneField.getText();
        String email = emailField.getText();

        if (username.isEmpty() || password.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请填写所有字段！");
            return;
        }

        try (Connection conn = DatabaseUtils.getConnection()) {
            String query = "INSERT INTO users (username, password, gender, phone, email, role) VALUES (?, ?, ?, ?, ?, 'admin')";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, gender);
            ps.setString(4, phone);
            ps.setString(5, email);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "注册成功！");
                backToLogin();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void backToLogin() {
        new LoginFrame();
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RegisterFrame::new);
    }
}
