package resources;

public enum ControlMessage {
    CLOSE_CONNECTION("/end"),
    AUTH("/auth"),
    AUTH_OK("/authok"),
    AUTH_FAIL("/authfail"),
    REG_OK("/regok"),
    REG_LOGIN_EXISTS("/reglogin"),
    REG_NICKNAME_EXISTS("/regnick");

    private String message;

    ControlMessage(String message) {
        this.message = message;
    }

    public boolean check(String s) {
        return s.equalsIgnoreCase(message);
    }

    @Override
    public String toString() {
        return message;
    }
}
