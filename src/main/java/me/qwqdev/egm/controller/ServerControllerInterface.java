package me.qwqdev.egm.controller;

import java.util.Set;

/**
 * The interface Server controller.
 *
 * @author qwq-dev
 * @since 2024-12-09 10:01
 */
public interface ServerControllerInterface {
    /**
     * Update server configs.
     */
    void updateServerConfigs();

    /**
     * Create a new server configuration.
     *
     * @param name the name
     */
    void create(String name);

    /**
     * Send command to a running process.
     *
     * @param name    the name
     * @param port    the port
     * @param command the command
     */
    void sendCommandToRunningProcess(String name, int port, String command);

    /**
     * Interrupt a running process.
     *
     * @param name the name
     * @param port the port
     */
    void interruptedRunningProcess(String name, int port);

    /**
     * Get the used ports for a server.
     *
     * @param name the name
     * @return the used ports
     */
    Set<Integer> getUsedPorts(String name);
}
