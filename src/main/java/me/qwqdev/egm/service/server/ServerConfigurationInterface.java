package me.qwqdev.egm.service.server;

import de.leonhard.storage.Yaml;

import java.util.Map;
import java.util.Set;

/**
 * Interface for server configuration management.
 *
 * @author qwq-dev
 * @since 2024-12-09 15:13
 */
public interface ServerConfigurationInterface {
    /**
     * Gets the yaml of the server configuration.
     *
     * @return the yaml
     */
    Yaml getYaml();

    /**
     * Gets the name of the server configuration.
     *
     * @return the name
     */
    String getName();

    /**
     * Gets the source file directory.
     *
     * @return the source file directory
     */
    String getSourceFileDir();

    /**
     * Gets the target file directory.
     *
     * @return the target file directory
     */
    String getTargetFileDir();

    /**
     * Gets the minimum port number.
     *
     * @return the minimum port number
     */
    int getPortMin();

    /**
     * Gets the maximum port number.
     *
     * @return the maximum port number
     */
    int getPortMax();

    /**
     * Gets the set of ports currently in use.
     *
     * @return the set of in-use ports
     */
    Set<Integer> getInUsedPorts();

    /**
     * Gets the map of currently running processes.
     *
     * @return a map of port numbers to running processes
     */
    Map<Integer, Process> getRunningProcesses();

    /**
     * Creates and configures a new server instance asynchronously.
     */
    void create();

    /**
     * Sends a command to the running process on a specified port.
     *
     * @param port    the port of the running process
     * @param command the command to send
     */
    void sendCommandToRunningProcess(int port, String command);
}
