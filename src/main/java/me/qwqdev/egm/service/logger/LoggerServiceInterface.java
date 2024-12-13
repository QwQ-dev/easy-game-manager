package me.qwqdev.egm.service.logger;


import org.slf4j.Logger;

/**
 * The interface Logger service interface.
 *
 * @author qwq-dev
 * @since 2024-12-08 11:17
 */
public interface LoggerServiceInterface {
    /**
     * Gets normal logger.
     *
     * @return the normal logger
     */
    Logger getNormalLogger();

    /**
     * Gets simple logger.
     *
     * @return the simple logger
     */
    Logger getSimpleLogger();

    /**
     * Send empty info.
     */
    void sendEmptyInfo();
}
