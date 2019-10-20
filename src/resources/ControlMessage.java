package resources;

public enum ControlMessage {
    CLOSE_CONNECTION("/end", "Закрыть соединение"),
    AUTH("/auth", ""),
    AUTH_OK("/authok", ""),
    ERROR("/error", ""),
    REG("/reg", ""),
    REG_OK("/regok", ""),
    WHISPER("/w", "Отправить личное сообщение (/w [ник] [сообщение])"),
    BLACKLIST("/bl", "Добавить пользователя в черный список (/bl [ник])"),
    BLACKLIST_REMOVE("/blremove", "Удалить пользователя из черного списка (/blremove [ник])"),
    HELP("/help", "Показать справку по командам");

    private String message;
    private String description;

    ControlMessage(String message, String description) {
        this.message = message;
        this.description = description;
    }

    public boolean check(String s) {
        return s.equalsIgnoreCase(message);
    }

    public boolean hasDescription() {
        return !description.isEmpty();
    }

    public String getFullDescription() {
        String tabSpaces = "";
        if (message.length() < 5) tabSpaces = "\t\t\t";
        else if (message.length() < 9) tabSpaces = "\t\t";
        else tabSpaces = "\t";
        return message + tabSpaces + description;
    }

    @Override
    public String toString() {
        return message;
    }
}
