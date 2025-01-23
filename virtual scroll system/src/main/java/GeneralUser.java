
class GeneralUser extends User {

    private String customIDKey;
    private String password;
    private String fullName;
    private String emailAddress;
    private String phone;
    public GeneralUser(String customIDKey, String password, String fullName, String emailAddress, String phone) {
        super(customIDKey, password, fullName, emailAddress, phone);
    }

    public boolean isAdmin() {
        return false; // By default, regular users are not admins
    }


}
