package me.qwqdev.egm.io.thread.config;

import de.leonhard.storage.Yaml;
import me.qwqdev.egm.service.file.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * The type Thread pool config.
 *
 * @author qwq-dev
 * @since 2024-12-06 22:38
 */
@Configuration
public class ThreadPoolConfig {
    private final ConfigurationService configurationService;

    /**
     * Instantiates a new Thread pool config.
     *
     * @param configurationService the config service
     */
    @Autowired
    public ThreadPoolConfig(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * Task executor thread pool task executor.
     *
     * @return the thread pool task executor
     */
    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        Yaml config = this.configurationService.getConfig();
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();

        threadPoolTaskExecutor.setCorePoolSize(config.getInt("thread-pool.core-pool-size"));
        threadPoolTaskExecutor.setMaxPoolSize(config.getInt("thread-pool.max-pool-size"));
        threadPoolTaskExecutor.setQueueCapacity(config.getInt("thread-pool.queue-capacity"));
        threadPoolTaskExecutor.setKeepAliveSeconds(config.getInt("thread-pool.keep-alive-seconds"));
        threadPoolTaskExecutor.setThreadNamePrefix(config.getString("thread-pool.thread-name-prefix"));
        threadPoolTaskExecutor.initialize();

        return threadPoolTaskExecutor;
    }
}
