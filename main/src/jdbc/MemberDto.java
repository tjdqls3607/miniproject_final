package jdbc;

import java.util.Date;

public class MemberDto {
    private int member_id;
    private String name;
    private String phone_number;
    private Date birth_date;

    public MemberDto() {}
    public MemberDto(int id, String name, String phoneNumber, Date birthDate) {
        super();
        this.member_id = id;
        this.name = name;
        this.phone_number = phoneNumber;
        this.birth_date = birthDate;
    }
    public int getId() {
        return member_id;
    }
    public void setId(int id) {
        this.member_id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhoneNumber() {
        return phone_number;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phone_number = phoneNumber;
    }
    public Date getBirthDate() {
        return birth_date;
    }
    public void setBirthDate(Date birthDate) {
        this.birth_date = birthDate;
    }

    @Override
    public String toString() {
        return "MemberDto [id=" + member_id + ", name=" + name + ", " +
                "phoneNumber=" + phone_number + ", birthDate=" + birth_date + "]";
    }
}
