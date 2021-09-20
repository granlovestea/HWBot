package ru.granby.bot.handler.website.skysmart;

public class IncorrectHomeworkUrlException extends RuntimeException {
    public IncorrectHomeworkUrlException() {
        super();
    }

    public IncorrectHomeworkUrlException(String message) {
        super(message);
    }

    public IncorrectHomeworkUrlException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectHomeworkUrlException(Throwable cause) {
        super(cause);
    }

    protected IncorrectHomeworkUrlException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
