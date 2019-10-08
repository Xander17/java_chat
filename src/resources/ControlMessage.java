package resources;

public enum ControlMessage {
    CLOSE_CONNECTION("/end"),
    AUTH("/auth"),
    AUTH_OK("/authok"),
    ERROR("/error"),
    REG("/reg"),
    REG_OK("/regok"),
    WHISPER("/w");

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
