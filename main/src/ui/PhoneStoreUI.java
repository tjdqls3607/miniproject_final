package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

public class PhoneStoreUI {
    private JComboBox<String> memberComboBox;
    private JComboBox<String> phoneComboBox;
    private JLabel originalPriceLabel;
    private JLabel discountedPriceLabel;
    private JLabel membershipInfoLabel;
    private JLabel stockInfoLabel;

    private Map<String, Double> membershipDiscounts;
    private Map<String, Double> membershipLevels;
    private Map<String, Double> phonePrices;
    private Map<String, Double> phoneStocks;

    public PhoneStoreUI() {
        initializeData();

        setTitle("Phone Store");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(7, 2));

        add(new JLabel("회원을 선택해 주세요: "));
        memberComboBox = new JComboBox<>(getMembersWithMembershipInfo());
        add(memberComboBox);

        add(new JLabel("휴대폰 기종을 선택해 주세요: "));
        phoneComboBox = new JComboBox<>(getPhones());
        phoneComboBox.addActionListener(e -> updateStockInfo());

        add(phoneComboBox);

        add(new JLabel("출시가: "));
        originalPriceLabel = new JLabel();
        add(originalPriceLabel);

        add(new JLabel("할인가: "));
        discountedPriceLabel = new JLabel();
        add(discountedPriceLabel);

        add(new JLabel("멤버십 등급별 할인 안내: "));
        membershipInfoLabel = new JLabel();
        add(membershipInfoLabel);

        add(new JLabel("재고 안내: "));
        stockInfoLabel = new JLabel();
        add(stockInfoLabel);

        JButton calculateButton = new JButton("계산");
        calculateButton.addActionListener(e -> calculatePrice());
        add(calculateButton);

        setVisible(true);
    }

    private void initializeData() {
        membershipDiscounts = new HashMap<>();
        membershipLevels = new HashMap<>();
        Connection con = null;
        PreparedStatement pstmt = null;
    }
}