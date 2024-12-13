package me.qwqdev.egm.service.logger;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * The type Logger service.
 *
 * @author qwq-dev
 * @since 2024-12-06 21:14
 */
@Getter
@Service
public class LoggerService implements LoggerServiceInterface {
    private final Logger normalLogger;
    private final Logger simpleLogger;

    /**
     * Instantiates a new Logger service.
     */
    public LoggerService() {
        this.normalLogger = LoggerFactory.getLogger("NORMAL");
        this.simpleLogger = LoggerFactory.getLogger("SIMPLE");
    }

    /**
     * {@inheritDoc}
     */
    public void sendEmptyInfo() {
        simpleLogger.info(" ");
    }
}
