package com.book.frame;

import com.book.utils.DatabaseUtils;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminDashboard extends JFrame {

    private String currentUserId;//声明了一个私有字符串变量 currentUserId，用于存储当前用户的 ID。

    public AdminDashboard(String userId) {//定义了一个构造函数，接收一个 userId 作为参数。
        this.currentUserId = userId;//将传入的 userId 赋值给 currentUserId 变量。


        // 设置窗口属性
        setTitle("图书管理系统 - 管理员界面");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//设置当用户关闭窗口时，程序终止运行。

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
            String bookIdOrKeyword = bookIdField.getText();//获取用户输入的 userId 和 bookIdOrKeyword。

            if (!userId.isEmpty()) {
                // 根据用户 ID 查询借阅和还书记录
                try (Connection conn = DatabaseUtils.getConnection()) {   //使用 DatabaseUtils.getConnection() 获取数据库连接。
                    String query = "SELECT * FROM borrow_records WHERE user_id = ?";
                    //定义一个 SQL 查询语句字符串 query。
                    //SELECT * FROM borrow_records：表示从名为 borrow_records 的数据库表中选择所有列的数据。
                    //WHERE user_id =?：这是一个条件子句，? 是一个占位符，用于后续设置具体的 user_id 值
                    PreparedStatement ps = conn.prepareStatement(query);
                    //使用 conn.prepareStatement(query) 方法将 SQL 查询语句 query 转换为 PreparedStatement 对象 ps。
                    //PreparedStatement 是预编译的 SQL 语句对象
                    ps.setString(1, userId);
                    //使用 setString 方法将第一个占位符（在 SQL 中是 ?）的值设置为 userId。
                    //1 表示占位符的索引（从 1 开始），这里将 userId 作为参数传递给第一个占位符。
                    ResultSet rs = ps.executeQuery();
                    //执行查询操作，将结果存储在 ResultSet 中。

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
            //这是一个异常处理块，用于捕获可能发生的 SQLException 和 ClassNotFoundException。
            //SQLException：当执行 SQL 操作（如连接数据库、执行查询、更新等）时，如果发生错误，可能会抛出 SQLException。
            //ClassNotFoundException：在加载数据库驱动或使用一些依赖类时，如果找不到相应的类，会抛出 ClassNotFoundException。
            //ex.printStackTrace();：打印异常的堆栈跟踪信息，这有助于在调试时查找错误发生的位置和原因。

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
        // 显示一个确认对话框，其中包含创建的面板，对话框标题为 "添加书籍"，并提供 OK 和 CANCEL 选项
        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText();
            String author = authorField.getText();
            String pubDate = pubDateField.getText();
            String isbn = isbnField.getText();
            String quantityStr = quantityField.getText();
            String location = locationField.getText();

            try (Connection conn = DatabaseUtils.getConnection()) {// SQL 插入语句，使用占位符（?）防止 SQL 注入
                String query = "INSERT INTO books (title, author, publication_date, isbn, quantity, location) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, title);
                // 1. ps.setString(1, title);
                // 将变量 title 的值设置为 PreparedStatement 的第一个参数（占位符）的值。
                // 这里的 1 表示第一个参数的索引（从 1 开始计数），title 是一个字符串，会被插入或更新到 SQL 语句中相应位置。
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
        if (bookId == null || bookId.isEmpty()) {// 若用户取消输入或输入为空，则直接返回，不执行更新操作
            return;
        }

        try (Connection conn = DatabaseUtils.getConnection()) {// SQL 查询语句，根据输入的书籍 ID 查找书籍信息
            String selectQuery = "SELECT * FROM books WHERE book_id = ?";
            PreparedStatement selectPs = conn.prepareStatement(selectQuery);
            selectPs.setInt(1, Integer.parseInt(bookId));
            ResultSet rs = selectPs.executeQuery();

            if (rs.next()) {// 获取书籍的当前信息
                String currentTitle = rs.getString("title");
                String currentAuthor = rs.getString("author");
                String currentPubDate = rs.getString("publication_date");
                String currentIsbn = rs.getString("isbn");
                int currentQuantity = rs.getInt("quantity");
                String currentLocation = rs.getString("location");

                // 创建一个面板用于更新书籍信息
                JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
                JTextField titleField = new JTextField(currentTitle);
                JTextField authorField = new JTextField(currentAuthor);
                JTextField pubDateField = new JTextField(currentPubDate);
                JTextField isbnField = new JTextField(currentIsbn);
                JTextField quantityField = new JTextField(String.valueOf(currentQuantity));
                JTextField locationField = new JTextField(currentLocation);

                // 将标签和相应的文本输入框添加到面板中，用于更新书籍信息
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
                    // 获取用户更新后的信息
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

            StringBuilder results = new StringBuilder("查询结果：\n");// 使用 StringBuilder 构建查询结果的字符串
            while (rs.next()) {
                // 遍历结果集，将每条记录的信息添加到 StringBuilder 中
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

            if (rs.next()) {// 获取用户的当前信息
                String currentPassword = rs.getString("password");
                String currentGender = rs.getString("gender");
                String currentPhone = rs.getString("phone");
                String currentEmail = rs.getString("email");

                // 创建一个面板用于更新用户信息
                JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
                JTextField passwordField = new JTextField(currentPassword);
                JTextField genderField = new JTextField(currentGender);
                JTextField phoneField = new JTextField(currentPhone);
                JTextField emailField = new JTextField(currentEmail);

                // 将标签和相应的文本输入框添加到面板中，用于更新用户信息
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
                    // 获取用户更新后的信息
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
        new LoginFrame();// 创建一个新的 LoginFrame 对象，可能用于重新登录
        dispose();
    }
}
