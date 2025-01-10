package hr.tvz.diplomski.webshop.enumeration;

public enum UserType {

    CUSTOMER, ADMIN;

    UserType() {

    }

    public String getRole() {
        return "ROLE_" + name();
    }
}
