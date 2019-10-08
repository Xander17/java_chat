package resources;

public enum LoginRegError {
    NO_CONNECTION("No connection to server"),
    NOT_ENOUGH_DATA("Please fill all fields"),
    INCORRECT_LOGIN_PASS("No such login or incorrect password"),
    LOGGED_ALREADY("User is already online"),
    LOGIN_EXISTS("Login already exists"),
    NICKNAME_EXISTS("Nickname already exists"),
    REG_ERROR("Registration fail"),
    DB_ERROR("DB error"),
    RESPONSE_ERROR("Server response error");

    private String message;

    LoginRegError(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }

}
