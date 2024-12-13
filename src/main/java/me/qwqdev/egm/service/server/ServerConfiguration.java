package me.qwqdev.egm.service.server;

import de.leonhard.storage.Yaml;
import lombok.Getter;
import me.qwqdev.egm.service.logger.LoggerServiceInterface;
import me.qwqdev.egm.service.server.exception.PropertiesNotFoundException;
import me.qwqdev.egm.utils.IOUtils;
import me.qwqdev.egm.utils.MathUtils;
import me.qwqdev.egm.utils.SpringContextHolder;
import me.qwqdev.egm.utils.network.PortUtils;
import me.qwqdev.egm.utils.text.StringUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The type Server configuration.
 *
 * @author qwq-dev
 * @since 2024-12-07 10:57
 */
public class ServerConfiguration implements ServerConfigurationInterface {
    private final LoggerServiceInterface loggerService;

    @Getter
    private final Yaml yaml;

    @Getter
    private final String name;
    @Getter
    private final String sourceFileDir;
    @Getter
    private final String targetFileDir;

    @Getter
    private final int portMin;
    @Getter
    private final int portMax;

    @Getter
    private final Set<Integer> inUsedPorts;

    private final Map<Integer, Lock> portLocks;
    private final Map<String, ProcessBuilder> processBuilders;

    @Getter
    private final Map<Integer, Process> runningProcesses;

    /**
     * Instantiates a new Server configuration.
     *
     * @param yaml the yaml
     */
    public ServerConfiguration(Yaml yaml) {
        this.yaml = yaml;
        this.loggerService = SpringContextHolder.getBean(LoggerServiceInterface.class);

        this.name = yaml.getString("name");
        this.sourceFileDir = yaml.getString("source-file-dir");
        this.targetFileDir = yaml.getString("target-file-dir");

        this.portMin = yaml.getInt("port-min");
        this.portMax = yaml.getInt("port-max");

        this.inUsedPorts = ConcurrentHashMap.newKeySet();
        this.portLocks = new ConcurrentHashMap<>();
        this.processBuilders = Collections.synchronizedMap(new LinkedHashMap<>(16, 0.75f, true));
        this.runningProcesses = new ConcurrentHashMap<>();

        String runNodeString = "run-node";
        yaml.singleLayerKeySet(runNodeString).forEach(node -> processBuilders.put(node, new ProcessBuilder(yaml.getStringList(runNodeString + "." + node + ".commands"))));
    }

    private String getTargetFilePortDir(int port) {
        return targetFileDir + "-" + port;
    }

    /**
     * {@inheritDoc}
     */
    public void create() {
        Logger normalLogger = loggerService.getNormalLogger();
        OptionalInt availablePortOpt = MathUtils.findClosestAvailable(portMin, portMax, inUsedPorts);

        // if port cant be found
        if (availablePortOpt.isEmpty()) {
            normalLogger.info("[{}] No available ports found.", name);
            return;
        }

        // fine-grained lock
        int port = availablePortOpt.getAsInt();
        Lock portLock = portLocks.computeIfAbsent(port, _ -> new ReentrantLock());

        // try to acquire the lock with a timeout
        boolean lockAcquired = false;
        try {
            // set an appropriate timeout
            lockAcquired = portLock.tryLock(100, TimeUnit.MILLISECONDS);

            if (!lockAcquired) {
                normalLogger.info("[{} {}] Port lock acquisition timed out, port is likely in use.", name, port);
                return;
            }

            // if port already in use
            if (PortUtils.isPortInUse(port) || inUsedPorts.contains(port)) {
                normalLogger.info("[{} {}] Port already in use.", name, port);
                return;
            }

            String targetFilePortDir = getTargetFilePortDir(port);
            boolean isTargetFilePortDirExist = IOUtils.isDirectoryExist(targetFilePortDir);

            // if target file port dir exists
            if (isTargetFilePortDirExist) {
                normalLogger.error("[{} {}] Dir: {} exists, deleted error??", name, port, targetFilePortDir);
                return;
            }

            // set to using
            inUsedPorts.add(port);
            normalLogger.info("[{} {}] Port set to using.", name, port);

            // copy file to target file port dir
            IOUtils.copyFiles(sourceFileDir, targetFilePortDir);
            normalLogger.info("[{} {}] Target file port dir: {}", name, port, targetFilePortDir);

            // set server.properties port
            try {
                modifyServerPortInProperties(targetFilePortDir, port, normalLogger);
            } catch (PropertiesNotFoundException exception) {
                normalLogger.error("[{} {}] server.properties not found in directory: {}", name, port, targetFilePortDir);
                return;
            } catch (IOException exception) {
                normalLogger.error("[{} {}] Failed to modify server.properties file: ", name, port, exception);
                return;
            }

            // run node
            try {
                processBuilders.forEach((key, processBuilder) -> {
                    try {
                        normalLogger.info("[{} {} {}] Starting...", name, port, key);

                        processBuilder.command().replaceAll(command -> StringUtils.formatting(command, "{target-path}", targetFilePortDir));
                        Process startedProcess = processBuilder.start();

                        runningProcesses.put(port, startedProcess);
                        startedProcess.waitFor();
                    } catch (IOException exception) {
                        normalLogger.error("[{} {} {}] Failed to start process builder: ", name, port, key, exception);
                    } catch (InterruptedException exception) {
                        normalLogger.error("[{} {} {}] Interrupted: ", name, port, key, exception);
                    }
                });
            } finally {
                try {
                    runningProcesses.remove(port);
                    IOUtils.deleteAllFilesInDirectory(targetFilePortDir);
                } catch (IOException exception) {
                    normalLogger.error("[{} {}] Failed to delete target file port dir: ", name, port, exception);
                }
            }
        } catch (IOException exception) {
            normalLogger.error("[{} {}] IO exception: ", name, port, exception);
        } catch (InterruptedException exception) {
            normalLogger.error("[{} {}] Interrupted while waiting for lock: ", name, port, exception);
        } finally {
            // remove port
            inUsedPorts.remove(port);

            // make sure to unlock only if the lock was acquired
            if (lockAcquired) {
                portLock.unlock();
            }
        }
    }

    private void modifyServerPortInProperties(String targetFilePortDir, int port, Logger normalLogger) throws IOException, PropertiesNotFoundException {
        File serverPropertiesFile = new File(targetFilePortDir, "server.properties");

        if (!serverPropertiesFile.exists()) {
            throw new PropertiesNotFoundException(targetFilePortDir);
        }

        Properties properties = new Properties();

        try (InputStream input = new FileInputStream(serverPropertiesFile)) {
            properties.load(input);
            properties.setProperty("server-port", String.valueOf(port));

            try (OutputStream output = new FileOutputStream(serverPropertiesFile)) {
                properties.store(output, "Updated server-port to " + port);
                normalLogger.info("[{} {}] server.properties updated with server-port: {}", name, port, port);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param port    {@inheritDoc}
     * @param command {@inheritDoc}
     */
    public void sendCommandToRunningProcess(int port, String command) {
        Logger normalLogger = loggerService.getNormalLogger();
        Process runningProcess = runningProcesses.get(port);

        if (runningProcess == null) {
            normalLogger.error("[{}] No process is running.", name);
            return;
        }

        try (OutputStream outputStream = runningProcess.getOutputStream()) {
            org.apache.commons.io.IOUtils.write(command + "\n", outputStream, "UTF-8");
            outputStream.flush();
        } catch (IOException exception) {
            normalLogger.error("[{}] Failed to send command to process: ", name, exception);
        }
    }
}

