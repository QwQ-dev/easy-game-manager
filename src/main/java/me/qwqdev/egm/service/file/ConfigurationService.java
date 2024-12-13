package me.qwqdev.egm.service.file;

import de.leonhard.storage.Yaml;
import lombok.Getter;
import me.qwqdev.egm.service.server.ServerConfiguration;
import me.qwqdev.egm.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The type Config manager.
 *
 * @author qwq-dev
 * @since 2024-12-06 20:41
 */
@Service
public class ConfigurationService {
    /**
     * The constant SERVER_CONFIGS_DIR.
     */
    public static final String SERVER_CONFIGS_DIR = IOUtils.CURRENT_DIR + "/server-configs/";

    /**
     * The constant SERVER_CONFIGS.
     */
    public static final List<ServerConfiguration> SERVER_CONFIGS = new ArrayList<>();

    @Getter
    private final Yaml config;

    private final FileServiceInterface<Yaml> yamlFileService;

    /**
     * Instantiates a new Configuration service.
     *
     * @param yamlFileService the yaml file service
     */
    @Autowired
    public ConfigurationService(YamlFileService yamlFileService) {
        this.yamlFileService = yamlFileService;
        this.config = yamlFileService.get("config", IOUtils.CURRENT_DIR, true);
    }

    /**
     * Update server configs.
     *
     * <p>We need to read all files from the path {@link #SERVER_CONFIGS_DIR}
     * and add them to {@link #SERVER_CONFIGS}.
     */
    public void updateServerConfigs() {
        SERVER_CONFIGS.clear();

        IOUtils.getFiles(SERVER_CONFIGS_DIR).stream()
                .map(file -> new ServerConfiguration(yamlFileService.get(file, false)))
                .forEach(SERVER_CONFIGS::add);
    }

    /**
     * Find server configs with name optional.
     *
     * @param name the name
     * @return the optional
     */
    public Optional<ServerConfiguration> findServerConfigsWithName(String name) {
        return SERVER_CONFIGS.stream().filter(serverConfiguration -> serverConfiguration.getName().equals(name)).findFirst();
    }
}
