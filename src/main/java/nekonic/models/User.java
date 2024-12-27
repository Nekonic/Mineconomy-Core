package nekonic.models;

public class User {
    private String nameId;
    private int balance;
    private String uuid;
    private String language;

    public User(String nameId, int balance, String uuid, String language) {
        this.nameId = nameId;
        this.balance = balance;
        this.uuid = uuid;
        this.language = language;
    }

    public String getNameId() {
        return nameId;
    }

    public double getBalance() {
        return balance;
    }

    public String getUuid() {
        return uuid;
    }

    public String getLanguage() {
        return language;
    }

    public void updateBalance(double amount) {
        this.balance += amount;
    }
}
