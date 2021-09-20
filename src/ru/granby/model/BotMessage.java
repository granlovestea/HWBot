package ru.granby.model;

public class BotMessage {
    public static final String ABOUT_BOT = "Бот умеет делать то-то";
    public static final String UNKNOWN_COMMAND =
            "Неизвестная команда\n" +
            "/start - посмотреть возможности бота";
    public static final String CHOOSE_HOMEWORK_WEBSITE = "Выбери сайт где задали домашку";
    public static final String UNKNOWN_WEBSITE = "Неизвестный сайт с ДЗ, выбери из меню";
    public static final String NEED_AUTH = "Необходима авторизация в аккаунте %s для работы бота";
    public static final String NEED_LOGIN = "Введите логин от аккаунта";
    public static final String NEED_PASSWORD = "Введите пароль от аккуанта";
    public static final String AUTH_IN_PROGRESS = "Вхожу в аккаунт...";
    public static final String INCORRECT_CREDENTIALS = "Неверный логин или пароль, повторите попытку";
    public static final String INTERNAL_ERROR = "Произошла внутренняя ошибка, повторите позже";
    public static final String AUTH_SUCCESS = "Успешная авторизация";
    public static final String TRY_AUTH_EXISTING_ACCOUNT = "Пытаюсь войти в аккаунт по раннее введенным данным...";
    public static final String AUTH_LAST_CREDENTIALS_SUCCESS = "Успешная авторизация по раннее введенным входным данным";
    public static final String NEED_SKYSMART_HOMEWORK_URL = "Отправьте ссылку на домашнее задание\n" +
            "(ссылка вида https://edu.skysmart.ru/student/abcdefghijk\n" +
            " или https://edu.skysmart.ru/lesson/homework/qrstuvwxyz/1)";
    public static final String NOT_YET_SUPPORTED = "Данная функция пока не поддерживается";
    public static final String SHOW_INCORRECT_HOMEWORK_URL = "Некорректная ссылка на домашнее задание, попробуйте снова";
    public static final String TOKEN_NOT_VALID = "Произошла внутренняя ошибка авторизации, возможна необходима повторная авторизация";
    public static final String FINISHED_SKYSMART_ROOM = "Все ответы на задания в текущей работе отправлены. " +
            "\nЕсли необходима обратная связь, нажмите кнопку \"Поддержка\"." +
            "\nЕсли вопрос связан с конкретным заданием, при обращении в поддержку укажите идентификатор задания и комнаты.";
    public static final String SOLVED_SKYSMART_STEP_SCREENSHOT = "Skysmart ответ на задание №%s" +
            "\nИдентификатор комнаты: %s" +
            "\nИдентификатор задания: %s";
}