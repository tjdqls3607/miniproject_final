package ui;

import jdbc.MemberDto;
import jdbc.Test;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PhoneStoreUI extends JFrame {
    private JComboBox<String> memberComboBox;
    private JComboBox<String> phoneComboBox;
    private JLabel originalPriceLabel;
    private JLabel discountedPriceLabel;
    private JLabel membershipInfoLabel;
    private JLabel stockInfoLabel;

    public PhoneStoreUI() {
        // Set up the JFrame
        setTitle("휴대폰 멤버십 등급에 따른 판매 관리");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(7, 2));

        // Member selection
        add(new JLabel("회원을 선택해 주세요:"));
        memberComboBox = new JComboBox<>(getMembersWithMembershipInfo());
        add(memberComboBox);

        // Phone selection
        add(new JLabel("휴대폰 기종을 선택해 주세요:"));
        phoneComboBox = new JComboBox<>(getPhones());
        phoneComboBox.addActionListener(e -> updateStockInfo()); // 휴대폰 선택 시 재고 정보 업데이트
        add(phoneComboBox);

        // Original price display
        add(new JLabel("출고가:"));
        originalPriceLabel = new JLabel();
        add(originalPriceLabel);

        // Discounted price display
        add(new JLabel("멤버십 적용가:"));
        discountedPriceLabel = new JLabel();
        add(discountedPriceLabel);

        // Membership info display
        add(new JLabel("등급 정보:"));
        membershipInfoLabel = new JLabel();
        add(membershipInfoLabel);

        // Stock info display
        add(new JLabel("재고:"));
        stockInfoLabel = new JLabel();
        add(stockInfoLabel);

        // Calculate button
        JButton calculateButton = new JButton("계산");
        calculateButton.addActionListener(e -> calculatePrice());
        add(calculateButton);

        // Display the JFrame
        setVisible(true);
    }

    private String[] getMembersWithMembershipInfo() {
        List<MemberDto> members = Test.listmember();
        String[] memberInfo = new String[members.size()];
        for (int i = 0; i < members.size(); i++) {
            MemberDto dto = members.get(i);
            String level = Test.getMembershipLevel(dto.getId());
            memberInfo[i] = dto.getName() + " (ID: " + dto.getId() + ", 멤버십 등급: " + level + ")";
        }
        return memberInfo;
    }

    private String[] getPhones() {
        // 휴대폰 목록을 가져오는 로직 (예: model 테이블에서 model_name 조회)
        return new String[]{"iPhone 13", "Galaxy S21", "Pixel 6", "iPhone 14", "Galaxy S22", "Pixel 7", "iPhone SE", "Galaxy A53", "Pixel 5a", "iPhone 12"};
    }

    private void updateStockInfo() {
        String selectedPhone = (String) phoneComboBox.getSelectedItem();
        int modelId = getModelIdByName(selectedPhone); // 휴대폰 이름으로 model_id 조회
        int stock = Test.getStock(modelId);
        if (stock == 0) {
            stockInfoLabel.setText("재고가 부족하여 구매할 수 없습니다. 죄송합니다.");
        } else {
            stockInfoLabel.setText("재고: " + stock + "개");
        }
    }

    private void calculatePrice() {
        String selectedMemberWithInfo = (String) memberComboBox.getSelectedItem();
        String selectedPhone = (String) phoneComboBox.getSelectedItem();

        // 회원 ID 추출 (예: "Alice (ID: 1, Discount: 20.0%)"에서 ID 추출)
        int memberId = Integer.parseInt(selectedMemberWithInfo.split("ID: ")[1].split(",")[0]);

        // 휴대폰 ID 추출
        int modelId = getModelIdByName(selectedPhone);

        // 재고 확인
        int stock = Test.getStock(modelId);
        if (stock == 0) {
            JOptionPane.showMessageDialog(this, "재고가 부족하여 구매할 수 없습니다. 죄송합니다.", "구매 불가", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 멤버십 할인율 조회
        double discountRate = Test.getMembershipDiscountRate(memberId);

        // 휴대폰 가격 조회
        double price = Test.getPhonePrice(modelId);

        // 할인 적용된 최종 가격 계산
        double discountedPrice = price * (1 - discountRate / 100);

        // 멤버십 정보 표시
        membershipInfoLabel.setText("할인율: " + discountRate + "%");

        // 가격 표시
        originalPriceLabel.setText(String.format("%,.0f원", price));
        discountedPriceLabel.setText(String.format("%,.0f원", discountedPrice));
    }

    private int getModelIdByName(String modelName) {
        // 휴대폰 이름으로 model_id를 조회하는 로직 (예: model 테이블에서 조회)
        // 여기서는 임시로 휴대폰 이름에 따라 model_id를 반환합니다.
        switch (modelName) {
            case "iPhone 13": return 1;
            case "Galaxy S21": return 2;
            case "Pixel 6": return 3;
            case "iPhone 14": return 4;
            case "Galaxy S22": return 5;
            case "Pixel 7": return 6;
            case "iPhone SE": return 7;
            case "Galaxy A53": return 8;
            case "Pixel 5a": return 9;
            case "iPhone 12": return 10;
            default: return -1;
        }
    }

    public static void main(String[] args) {
        new PhoneStoreUI();
    }
}