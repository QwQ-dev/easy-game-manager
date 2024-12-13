package me.qwqdev.egm.service.file;

import de.leonhard.storage.Yaml;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.qwqdev.egm.model.file.ConfigurableFile;
import me.qwqdev.egm.utils.IOUtils;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * 该类实现 {@link FileServiceInterface}，为 Yaml 管理类，单例模式。
 *
 * @author NaerQAQ / 2000000
 * @version 1.0
 * @since 2023/7/29
 */
@Service
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class YamlFileService implements FileServiceInterface<Yaml> {
    /**
     * {@inheritDoc}
     *
     * @param file                    {@inheritDoc}
     * @param inputStreamFromResource {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Yaml get(File file, boolean inputStreamFromResource) {
        return ConfigurableFile.builder()
                .setFile(file)
                .setInputStreamFromResource(inputStreamFromResource)
                .build()
                .getSimplixBuilder()
                .createYaml();
    }

    /**
     * {@inheritDoc}
     *
     * @param name                    {@inheritDoc}
     * @param path                    {@inheritDoc}
     * @param inputStreamFromResource {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Yaml get(String name, String path, boolean inputStreamFromResource) {
        return get(new File(path, IOUtils.getFinalFileName(name, ".yml")), inputStreamFromResource);
    }
}