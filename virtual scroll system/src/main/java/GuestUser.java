class GuestUser extends User {

    public GuestUser() {
        super("", "", "", "", "");
    }

    public boolean isAdmin() {
        return false; // By default, regular users are not admins
    }

}
