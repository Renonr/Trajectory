package ru.nic.trajectory;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class LoggerConfig {
    public static void setup() {
        try {
            // логгер
            Logger logger = Logger.getLogger("ru.test.diagrams");
            
            for (Handler handler : logger.getHandlers()) {
                logger.removeHandler(handler);
            }
            
            logger.setUseParentHandlers(false);

            // FileHandler для записи логов в файл
            java.util.logging.FileHandler fileHandler = new java.util.logging.FileHandler("application.log", 10 * 1024 * 1024, 10, true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            // ConsoleHandler для вывода логов в консоль
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(consoleHandler);

            // уровень логирования
            //setLevel(Level.ALL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    static class LogFilter implements Filter {
        @Override
        public boolean isLoggable(LogRecord record) {
            // Проверяем, что сообщение приходит от логгера вашего приложения
            return record.getLoggerName().startsWith("ru.test.diagrams");
        }
    }
}
