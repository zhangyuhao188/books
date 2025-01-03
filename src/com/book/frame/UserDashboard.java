package com.book.frame;

import com.book.utils.DatabaseUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class UserDashboard extends JFrame {
    private String userId;

    public UserDashboard(String userId) {
        this.userId = userId;

        // 设置窗口属性
        setTitle("图书管理系统 - 用户界面");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 设置标题
        JLabel titleLabel = new JLabel("欢迎使用图书管理系统");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel welcomeLabel = new JLabel("您好，用户ID：" + userId);
        welcomeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // 设置按钮
        JButton changePasswordButton = createButton("修改密码");
        JButton queryBooksButton = createButton("查询图书");
        JButton borrowRecordsButton = createButton("借阅记录");
        JButton returnRecordsButton = createButton("归还记录");
        JButton logoutButton = createButton("退出登录");

        // 按钮容器
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 1, 10, 10)); // 5行1列，按钮之间的间距为10
        buttonPanel.add(changePasswordButton);
        buttonPanel.add(queryBooksButton);
        buttonPanel.add(borrowRecordsButton);
        buttonPanel.add(returnRecordsButton);
        buttonPanel.add(logoutButton);

        // 主面板
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // 内边距
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(welcomeLabel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 添加事件监听
        changePasswordButton.addActionListener(e -> changePassword());
        queryBooksButton.addActionListener(e -> queryBooks());
        borrowRecordsButton.addActionListener(e -> viewBorrowRecords());
        returnRecordsButton.addActionListener(e -> viewReturnRecords());
        logoutButton.addActionListener(e -> logout());

        // 设置主窗口内容和居中显示
        add(mainPanel);
        setLocationRelativeTo(null); // 窗口居中
        setVisible(true);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return button;
    }

    private void changePassword() {
        String newPassword = JOptionPane.showInputDialog(this, "请输入新密码：");
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            try (Connection conn = DatabaseUtils.getConnection()) {
                String query = "UPDATE users SET password = ? WHERE user_id = ?";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, newPassword);
                ps.setString(2, userId);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "密码修改成功！");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void queryBooks() {
        String keyword = JOptionPane.showInputDialog(this, "请输入图书名称或作者关键字：");
        if (keyword != null && !keyword.trim().isEmpty()) {
            try (Connection conn = DatabaseUtils.getConnection()) {
                // 1. 定义 SQL 查询语句
                // 使用 LIKE 关键字进行模糊查询，以查找与用户输入的关键字匹配的图书信息
                // 这里使用了两个占位符?，分别用于接收用户输入的关键字
                // 查询语句将在 books 表中查找满足以下条件的记录：
                // 要么 title 列包含用户输入的关键字，要么 author 列包含用户输入的关键字
                String query = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";
                // 2. 准备 SQL 语句
                // 使用 conn.prepareStatement(query) 方法将 SQL 语句预编译为 PreparedStatement 对象
                // 预编译的好处是可以提高性能，同时防止 SQL 注入攻击
                PreparedStatement ps = conn.prepareStatement(query);
                // 3. 设置占位符的值
                // 使用 ps.setString 方法将用户输入的关键字添加到 SQL 语句的占位符中
                // 这里使用了 LIKE 关键字，所以需要在关键字前后添加 % 符号
                // % 表示匹配零个或多个字符，例如 %keyword% 可以匹配包含 keyword 的任何字符串
                // 第一个占位符设置为 "%keyword%"
                ps.setString(1, "%" + keyword + "%");
                ps.setString(2, "%" + keyword + "%");
                // 4. 执行查询操作
                // 使用 ps.executeQuery() 方法执行查询操作，并将结果存储在 ResultSet 对象中
                // ResultSet 对象包含了满足查询条件的所有记录的信息
                ResultSet rs = ps.executeQuery();

                StringBuilder results = new StringBuilder("查询结果：\n");
                while (rs.next()) {
                    results.append("ID: ").append(rs.getInt("book_id"))
                            .append(", 名称: ").append(rs.getString("title"))
                            .append(", 作者: ").append(rs.getString("author"))
                            .append(", 可借数量: ").append(rs.getInt("quantity"))
                            .append("\n");
                }
                JOptionPane.showMessageDialog(this, results.toString());
            } catch (SQLException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void viewBorrowRecords() {
        try (Connection conn = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM borrow_records WHERE user_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            StringBuilder results = new StringBuilder("借阅记录：\n");
            while (rs.next()) {
                results.append("图书ID: ").append(rs.getInt("book_id"))
                        .append(", 借阅日期: ").append(rs.getDate("borrow_date"))
                        .append("\n");
            }
            JOptionPane.showMessageDialog(this, results.toString());
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private void viewReturnRecords() {
        try (Connection conn = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM return_records WHERE user_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            StringBuilder results = new StringBuilder("归还记录：\n");
            while (rs.next()) {
                results.append("图书ID: ").append(rs.getInt("book_id"))
                        .append(", 归还日期: ").append(rs.getDate("return_date"))
                        .append("\n");
            }
            JOptionPane.showMessageDialog(this, results.toString());
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private void logout() {
        JOptionPane.showMessageDialog(this, "已成功退出登录！");
        new LoginFrame();
        dispose();
    }
}
