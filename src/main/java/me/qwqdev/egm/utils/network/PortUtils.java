package me.qwqdev.egm.utils.network;

import lombok.experimental.UtilityClass;

import java.net.ServerSocket;

/**
 * The type Port utils.
 *
 * @author qwq-dev
 * @since 2024-12-07 19:53
 */
@UtilityClass
public class PortUtils {
    /**
     * Is port in use boolean.
     *
     * @param port the port
     * @return the boolean
     */
    public static boolean isPortInUse(int port) {
        if (port < 1 || port > 65535) {
            return false;
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            return false;
        } catch (Exception exception) {
            return true;
        }
    }
}
