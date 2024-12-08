package com.book.frame;

import com.book.utils.DatabaseUtils;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminDashboard extends JFrame {

    private String currentUserId;

    public AdminDashboard(String userId) {
        this.currentUserId = userId;

        // 设置窗口属性
        setTitle("图书管理系统 - 管理员界面");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 标题
        JLabel titleLabel = new JLabel("图书管理系统 - 管理员操作面板");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel welcomeLabel = new JLabel("欢迎，管理员 " + userId);
        welcomeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER); // 设置居中对齐

        // 创建按钮
        JButton addUpdateBookButton = createButton("添加书籍");
        JButton updateBookButton = createButton("更新书籍");
        JButton deleteBookButton = createButton("删除书籍");
        JButton queryBooksButton = createButton("查询全部书籍");
        JButton viewBorrowRecordsButton = createButton("查看借阅记录");
        JButton viewUserBorrowReturnButton = createButton("查询借书和还书记录");
        JButton updateUserInfoButton = createButton("修改用户信息");
        JButton logoutButton = createButton("退出登录");

        // 按钮展示
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(7, 1, 10, 10)); // 7行1列，按钮之间的间距为10
        buttonPanel.add(addUpdateBookButton);
        buttonPanel.add(updateBookButton);
        buttonPanel.add(deleteBookButton);
        buttonPanel.add(queryBooksButton);
        buttonPanel.add(viewBorrowRecordsButton);
        buttonPanel.add(viewUserBorrowReturnButton);
        buttonPanel.add(updateUserInfoButton);
        buttonPanel.add(logoutButton);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // 内边距
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(welcomeLabel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 添加事件监听
        addUpdateBookButton.addActionListener(e -> addNewBook());
        updateBookButton.addActionListener(e -> updateBookInfo());
        deleteBookButton.addActionListener(e -> deleteBook());
        queryBooksButton.addActionListener(e -> queryAllBooks());
        viewBorrowRecordsButton.addActionListener(e -> {
            try {
                viewBorrowRecords();
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        viewBorrowRecordsButton.addActionListener(e -> {
            try {
                viewBorrowRecords();
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
        viewUserBorrowReturnButton.addActionListener(e -> viewUserBorrowReturnRecords());
        updateUserInfoButton.addActionListener(e -> updateUserInfo());
        logoutButton.addActionListener(e -> logout());

        // 设置主窗口内容和居中显示
        add(mainPanel);
        setLocationRelativeTo(null); // 窗口居中
        setVisible(true);
    }

    // 查询借书和还书记录
    private void viewUserBorrowReturnRecords() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField userIdField = new JTextField();
        JTextField bookIdField = new JTextField();

        panel.add(new JLabel("用户ID："));
        panel.add(userIdField);
        panel.add(new JLabel("图书ID或名称关键字："));
        panel.add(bookIdField);

        int result = JOptionPane.showConfirmDialog(this, panel, "查询借书和还书记录", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String userId = userIdField.getText();
            String bookIdOrKeyword = bookIdField.getText();

            if (!userId.isEmpty()) {
                // 根据用户 ID 查询借阅和还书记录
                try (Connection conn = DatabaseUtils.getConnection()) {
                    String query = "SELECT * FROM borrow_records WHERE user_id = ?";
                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setString(1, userId);
                    ResultSet rs = ps.executeQuery();

                    StringBuilder results = new StringBuilder("借阅和还书记录：\n");
                    while (rs.next()) {
                        results.append("借阅ID: ").append(rs.getInt("borrow_id"))
                                .append(", 用户ID: ").append(rs.getString("user_id"))
                                .append(", 图书ID: ").append(rs.getInt("book_id"))
                                .append(", 借阅日期: ").append(rs.getDate("borrow_date"))
                                .append(", 还书日期: ").append(rs.getDate("return_date"))
                                .append("\n");
                    }
                    JOptionPane.showMessageDialog(this, results.toString());
                } catch (SQLException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            }

            if (!bookIdOrKeyword.isEmpty()) {
                // 根据图书 ID 或名称关键字查询借阅情况
                try (Connection conn = DatabaseUtils.getConnection()) {
                    String query = "SELECT * FROM books WHERE book_id = ? OR title LIKE ?";
                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setString(1, bookIdOrKeyword);
                    ps.setString(2, "%" + bookIdOrKeyword + "%");
                    ResultSet rs = ps.executeQuery();

                    StringBuilder results = new StringBuilder("图书借阅情况：\n");
                    while (rs.next()) {
                        results.append("图书ID: ").append(rs.getInt("book_id"))
                                .append(", 名称: ").append(rs.getString("title"))
                                .append(", 数量: ").append(rs.getInt("quantity"))
                                .append(", 位置: ").append(rs.getString("location"))
                                .append("\n");
                    }
                    JOptionPane.showMessageDialog(this, results.toString());
                } catch (SQLException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        }
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

    private void addNewBook() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField pubDateField = new JTextField();
        JTextField isbnField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField locationField = new JTextField();

        panel.add(new JLabel("书名："));
        panel.add(titleField);
        panel.add(new JLabel("作者名："));
        panel.add(authorField);
        panel.add(new JLabel("出版日期 (yyyy-MM-dd)："));
        panel.add(pubDateField);
        panel.add(new JLabel("ISBN："));
        panel.add(isbnField);
        panel.add(new JLabel("书籍数量："));
        panel.add(quantityField);
        panel.add(new JLabel("位置："));
        panel.add(locationField);

        int result = JOptionPane.showConfirmDialog(this, panel, "添加书籍", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText();
            String author = authorField.getText();
            String pubDate = pubDateField.getText();
            String isbn = isbnField.getText();
            String quantityStr = quantityField.getText();
            String location = locationField.getText();

            try (Connection conn = DatabaseUtils.getConnection()) {
                String query = "INSERT INTO books (title, author, publication_date, isbn, quantity, location) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, title);
                ps.setString(2, author);
                ps.setDate(3, Date.valueOf(pubDate));
                ps.setString(4, isbn);
                ps.setInt(5, Integer.parseInt(quantityStr));
                ps.setString(6, location);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "新书添加成功！");
                }
            } catch (SQLException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void updateBookInfo() {
        String bookId = JOptionPane.showInputDialog(this, "请输入书籍ID：");
        if (bookId == null || bookId.isEmpty()) {
            return;
        }

        try (Connection conn = DatabaseUtils.getConnection()) {
            String selectQuery = "SELECT * FROM books WHERE book_id = ?";
            PreparedStatement selectPs = conn.prepareStatement(selectQuery);
            selectPs.setInt(1, Integer.parseInt(bookId));
            ResultSet rs = selectPs.executeQuery();

            if (rs.next()) {
                String currentTitle = rs.getString("title");
                String currentAuthor = rs.getString("author");
                String currentPubDate = rs.getString("publication_date");
                String currentIsbn = rs.getString("isbn");
                int currentQuantity = rs.getInt("quantity");
                String currentLocation = rs.getString("location");

                JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
                JTextField titleField = new JTextField(currentTitle);
                JTextField authorField = new JTextField(currentAuthor);
                JTextField pubDateField = new JTextField(currentPubDate);
                JTextField isbnField = new JTextField(currentIsbn);
                JTextField quantityField = new JTextField(String.valueOf(currentQuantity));
                JTextField locationField = new JTextField(currentLocation);

                panel.add(new JLabel("书名："));
                panel.add(titleField);
                panel.add(new JLabel("作者名："));
                panel.add(authorField);
                panel.add(new JLabel("出版日期 (yyyy-MM-dd)："));
                panel.add(pubDateField);
                panel.add(new JLabel("ISBN："));
                panel.add(isbnField);
                panel.add(new JLabel("书籍数量："));
                panel.add(quantityField);
                panel.add(new JLabel("位置："));
                panel.add(locationField);

                int result = JOptionPane.showConfirmDialog(this, panel, "更新书籍信息", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    String newTitle = titleField.getText();
                    String newAuthor = authorField.getText();
                    String newPubDate = pubDateField.getText();
                    String newIsbn = isbnField.getText();
                    String newQuantityStr = quantityField.getText();
                    String newLocation = locationField.getText();

                    String updateQuery = "UPDATE books SET title = ?, author = ?, publication_date = ?, isbn = ?, quantity = ?, location = ? WHERE book_id = ?";
                    PreparedStatement updatePs = conn.prepareStatement(updateQuery);
                    updatePs.setString(1, newTitle);
                    updatePs.setString(2, newAuthor);
                    updatePs.setDate(3, Date.valueOf(newPubDate));
                    updatePs.setString(4, newIsbn);
                    updatePs.setInt(5, Integer.parseInt(newQuantityStr));
                    updatePs.setString(6, newLocation);
                    updatePs.setInt(7, Integer.parseInt(bookId));
                    int rows = updatePs.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "书籍信息更新成功！");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "未找到对应的书籍信息！");
            }
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteBook() {
        String bookId = JOptionPane.showInputDialog(this, "请输入书籍ID：");
        try (Connection conn = DatabaseUtils.getConnection()) {
            String query = "DELETE FROM books WHERE book_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, Integer.parseInt(bookId));
            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "书籍删除成功！");
            }
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private void queryAllBooks() {
        try (Connection conn = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM books";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            StringBuilder results = new StringBuilder("查询结果：\n");
            while (rs.next()) {
                results.append("ID: ").append(rs.getInt("book_id"))
                        .append(", 名称: ").append(rs.getString("title"))
                        .append(", 作者: ").append(rs.getString("author"))
                        .append(", 出版日期: ").append(rs.getDate("publication_date"))
                        .append(", ISBN: ").append(rs.getString("isbn"))
                        .append(", 数量: ").append(rs.getInt("quantity"))
                        .append(", 位置: ").append(rs.getString("location"))
                        .append("\n");
            }
            JOptionPane.showMessageDialog(this, results.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void viewBorrowRecords() throws ClassNotFoundException {
        try (Connection conn = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM borrow_records";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            StringBuilder results = new StringBuilder("借阅记录：\n");
            while (rs.next()) {
                results.append("用户ID: ").append(rs.getString("user_id"))
                        .append(", 图书ID: ").append(rs.getInt("book_id"))
                        .append(", 借阅日期: ").append(rs.getDate("borrow_date"))
                        .append("\n");
            }
            JOptionPane.showMessageDialog(this, results.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateUserInfo() {
        try (Connection conn = DatabaseUtils.getConnection()) {
            String selectQuery = "SELECT * FROM users WHERE user_id = ?";
            PreparedStatement selectPs = conn.prepareStatement(selectQuery);
            selectPs.setString(1, currentUserId);
            ResultSet rs = selectPs.executeQuery();

            if (rs.next()) {
                String currentPassword = rs.getString("password");
                String currentGender = rs.getString("gender");
                String currentPhone = rs.getString("phone");
                String currentEmail = rs.getString("email");

                JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
                JTextField passwordField = new JTextField(currentPassword);
                JTextField genderField = new JTextField(currentGender);
                JTextField phoneField = new JTextField(currentPhone);
                JTextField emailField = new JTextField(currentEmail);

                panel.add(new JLabel("密码："));
                panel.add(passwordField);
                panel.add(new JLabel("性别："));
                panel.add(genderField);
                panel.add(new JLabel("电话号码："));
                panel.add(phoneField);
                panel.add(new JLabel("邮箱："));
                panel.add(emailField);

                int result = JOptionPane.showConfirmDialog(this, panel, "修改用户信息", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    String newPassword = passwordField.getText();
                    String newGender = genderField.getText();
                    String newPhone = phoneField.getText();
                    String newEmail = emailField.getText();

                    String updateQuery = "UPDATE users SET password = ?, gender = ?, phone = ?, email = ? WHERE user_id = ?";
                    PreparedStatement updatePs = conn.prepareStatement(updateQuery);
                    updatePs.setString(1, newPassword);
                    updatePs.setString(2, newGender);
                    updatePs.setString(3, newPhone);
                    updatePs.setString(4, newEmail);
                    updatePs.setString(5, currentUserId);
                    int rows = updatePs.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "用户信息更新成功！");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "未找到对应的用户信息！");
            }
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
