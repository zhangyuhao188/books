package com.book.frame;

import com.book.utils.DatabaseUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private JTextField userIdField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;

    public LoginFrame() {
        // 设置窗口属性
        setTitle("图书管理系统 - 登录");
        setSize(400, 250); // 设置窗口大小
        setResizable(false); // 禁止调整窗口大小
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 设置字体和颜色
        Font labelFont = new Font("微软雅黑", Font.BOLD, 14);
        Font buttonFont = new Font("微软雅黑", Font.PLAIN, 12);

        // 标题
        JLabel titleLabel = new JLabel("图书管理系统");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // 用户名标签和输入框
        JLabel userIdLabel = new JLabel("用户ID：");
        userIdLabel.setFont(labelFont);
        userIdField = new JTextField();
        userIdField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        userIdField.setHorizontalAlignment(SwingConstants.CENTER); // 输入框内容居中显示

        // 密码标签和输入框
        JLabel passwordLabel = new JLabel("密码：");
        passwordLabel.setFont(labelFont);
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        passwordField.setHorizontalAlignment(SwingConstants.CENTER); // 输入框内容居中显示

        // 按钮
        loginButton = new JButton("登录");
        loginButton.setFont(buttonFont);
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);

        registerButton = new JButton("注册");
        registerButton.setFont(buttonFont);
        registerButton.setBackground(new Color(100, 149, 237));
        registerButton.setForeground(Color.WHITE);

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
                                .addComponent(userIdLabel)
                                .addComponent(passwordLabel))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(userIdField, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                                .addComponent(passwordField, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createSequentialGroup()
                        .addComponent(loginButton, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                        .addGap(30)
                        .addComponent(registerButton, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE))
        );

        // 垂直布局
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(titleLabel)
                .addGap(20)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(userIdLabel)
                        .addComponent(userIdField, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(passwordLabel)
                        .addComponent(passwordField, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
                .addGap(20)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(loginButton, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                        .addComponent(registerButton, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
        );

        // 添加事件
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegisterFrame();
            }
        });

        // 设置主窗口布局
        add(formPanel, BorderLayout.CENTER);

        // 居中显示
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // 登录功能
    private void login() {
        String userId = userIdField.getText();
        String password = new String(passwordField.getPassword());

        if (userId.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请填写所有字段！");
            return;
        }

        try (Connection conn = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM users WHERE user_id = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, userId);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                JOptionPane.showMessageDialog(this, "登录成功，欢迎 " + userId + "！");
                if ("admin".equalsIgnoreCase(role)) {
                    new AdminDashboard(userId); // 登录成功后，打开管理员界面
                } else {
                    new UserDashboard(userId); // 登录成功后，打开普通用户界面（假设存在 UserDashboard）
                }
                dispose(); // 关闭当前的登录窗口
            } else {
                JOptionPane.showMessageDialog(this, "用户名或密码错误！");
            }
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "数据库连接出错！");
        }
    }

    // 打开注册页面
    // 打开注册页面
    private void openRegisterFrame() {
        new RegisterFrame(); // 假设已经有一个注册界面的类
        dispose(); // 关闭当前的登录窗口
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
