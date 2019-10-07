package resources;

public enum LoginRegError {
    NO_CONNECTION("No connection to server"),
    NOT_ENOUGH_DATA("Please fill all fields"),
    INCORRECT_LOGIN_PASS("No such login or incorrect password"),
    LOGIN_EXISTS("Login already exists"),
    NICKNAME_EXISTS("Nickname already exists");

    private String message;

    LoginRegError(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
