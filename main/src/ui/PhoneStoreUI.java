package ui;

import jdbc.DBManager;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PhoneStoreUI extends JFrame {
    private JComboBox<String> memberComboBox;
    private JComboBox<String> phoneComboBox;
    private JLabel originalPriceLabel;
    private JLabel discountedPriceLabel;

    private Map<String, Double> membershipDiscounts;
    private Map<String, Double> phonePrices;

    public PhoneStoreUI() {
        // Initialize data
        initializeData();

        // Set up the JFrame
        setTitle("Phone Store");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 2));

        // Member selection
        add(new JLabel("Select Member:"));
        memberComboBox = new JComboBox<>(getMembers());
        add(memberComboBox);

        // Phone selection
        add(new JLabel("Select Phone:"));
        phoneComboBox = new JComboBox<>(getPhones());
        add(phoneComboBox);

        // Original price display
        add(new JLabel("Original Price:"));
        originalPriceLabel = new JLabel();
        add(originalPriceLabel);

        // Discounted price display
        add(new JLabel("Discounted Price:"));
        discountedPriceLabel = new JLabel();
        add(discountedPriceLabel);

        // Calculate button
        JButton calculateButton = new JButton("Calculate Price");
        calculateButton.addActionListener(e -> calculatePrice());
        add(calculateButton);

        // Display the JFrame
        setVisible(true);
    }

    private void initializeData() {
        // Initialize membership discounts
        membershipDiscounts = new HashMap<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DBManager.getConnection();
            String sql = "SELECT m.name, ms.discount_rate FROM member m " +
                    "JOIN membership ms ON m.member_id = ms.member_id";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                membershipDiscounts.put(rs.getString("name"), rs.getDouble("discount_rate"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(rs, pstmt, con);
        }

        // Initialize phone prices
        phonePrices = new HashMap<>();
        try {
            con = DBManager.getConnection();
            String sql = "SELECT model_name, price FROM model";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                phonePrices.put(rs.getString("model_name"), rs.getDouble("price"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(rs, pstmt, con);
        }
    }

    private String[] getMembers() {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        java.util.List<String> members = new java.util.ArrayList<>();
        try {
            con = DBManager.getConnection();
            String sql = "SELECT name FROM member";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                members.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(rs, pstmt, con);
        }
        return members.toArray(new String[0]);
    }

    private String[] getPhones() {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        java.util.List<String> phones = new java.util.ArrayList<>();
        try {
            con = DBManager.getConnection();
            String sql = "SELECT model_name FROM model";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                phones.add(rs.getString("model_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.releaseConnection(rs, pstmt, con);
        }
        return phones.toArray(new String[0]);
    }

    private void calculatePrice() {
        String selectedMember = (String) memberComboBox.getSelectedItem();
        String selectedPhone = (String) phoneComboBox.getSelectedItem();

        double discountRate = membershipDiscounts.get(selectedMember);
        double originalPrice = phonePrices.get(selectedPhone);

        double discountedPrice = originalPrice * (1 - discountRate / 100);

        originalPriceLabel.setText(String.format("%,.2f원", originalPrice));
        discountedPriceLabel.setText(String.format("%,.2f원", discountedPrice));
    }

    public static void main(String[] args) {
        new PhoneStoreUI();
    }
}
