package me.qwqdev.egm.controller;

import me.qwqdev.egm.service.file.ConfigurationService;
import me.qwqdev.egm.service.logger.LoggerServiceInterface;
import me.qwqdev.egm.service.server.ServerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * The type Server controller.
 *
 * @author qwq-dev
 * @since 2024-12-09 10:01
 */
@RestController
public class ServerController implements ServerControllerInterface {
    private final ConfigurationService configurationService;
    private final LoggerServiceInterface loggerService;

    /**
     * Instantiates a new Server controller.
     *
     * @param configurationService the configuration service
     * @param loggerService        the logger service
     */
    @Autowired
    public ServerController(ConfigurationService configurationService, LoggerServiceInterface loggerService) {
        this.configurationService = configurationService;
        this.loggerService = loggerService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PostMapping("/updateServerConfigs")
    public void updateServerConfigs() {
        configurationService.updateServerConfigs();
    }

    /**
     * {@inheritDoc}
     *
     * @param name {@inheritDoc}
     */
    @Override
    @Async("threadPoolTaskExecutor")
    @PostMapping("/create")
    public void create(@RequestParam String name) {
        Optional<ServerConfiguration> serverConfigsWithNameOpt = configurationService.findServerConfigsWithName(name);

        serverConfigsWithNameOpt.ifPresentOrElse(
                ServerConfiguration::create,
                () -> sendNotFoundServerConfigurationError(name)
        );
    }

    /**
     * {@inheritDoc}
     *
     * @param name    {@inheritDoc}
     * @param port    {@inheritDoc}
     * @param command {@inheritDoc}
     */
    @Override
    @Async("threadPoolTaskExecutor")
    @PostMapping("/sendCommandToRunningProcess")
    public void sendCommandToRunningProcess(@RequestParam String name, @RequestParam int port, @RequestParam String command) {
        Optional<ServerConfiguration> serverConfigsWithNameOpt = configurationService.findServerConfigsWithName(name);

        serverConfigsWithNameOpt.ifPresentOrElse(
                value -> value.sendCommandToRunningProcess(port, command),
                () -> sendNotFoundServerConfigurationError(name)
        );
    }

    /**
     * {@inheritDoc}
     *
     * @param name {@inheritDoc}
     * @param port {@inheritDoc}
     */
    @Override
    @PostMapping("/interruptedRunningProcess")
    public void interruptedRunningProcess(@RequestParam String name, @RequestParam int port) {
        Optional<ServerConfiguration> serverConfigsWithNameOpt = configurationService.findServerConfigsWithName(name);

        serverConfigsWithNameOpt.ifPresentOrElse(
                value -> Optional.ofNullable(value.getRunningProcesses().get(port)).ifPresentOrElse(
                        Process::destroyForcibly,
                        () -> sendNotFoundRunningProcessesError(name)
                ),
                () -> sendNotFoundServerConfigurationError(name)
        );
    }

    /**
     * {@inheritDoc}
     *
     * @param name {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    @GetMapping("/getUsedPorts")
    public Set<Integer> getUsedPorts(@RequestParam String name) {
        Optional<ServerConfiguration> serverConfigsWithNameOpt = configurationService.findServerConfigsWithName(name);

        if (serverConfigsWithNameOpt.isEmpty()) {
            sendNotFoundServerConfigurationError(name);
            return Collections.emptySet();
        }

        return serverConfigsWithNameOpt.get().getInUsedPorts();
    }

    private void sendNotFoundServerConfigurationError(String name) {
        loggerService.getNormalLogger().error("[{}] Unable to find the corresponding ServerConfiguration, need to update it?", name);
    }

    private void sendNotFoundRunningProcessesError(String name) {
        loggerService.getNormalLogger().error("[{}] No process is running.", name);
    }
}
