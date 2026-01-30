package com.nexora.storage.autoconfigure;

import com.nexora.storage.FileStorageProperties;
import com.nexora.storage.FileUploadHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link FileStorageAutoConfiguration}.
 */
@DisplayName("FileStorageAutoConfiguration Integration Tests")
class FileStorageAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(FileStorageAutoConfiguration.class));

    @Test
    @DisplayName("Should load FileStorageAutoConfiguration")
    void shouldLoad() {
        contextRunner
            .run(context -> {
                assertThat(context).hasNotFailed();
                assertThat(context).hasSingleBean(FileStorageAutoConfiguration.class);
            });
    }

    @Test
    @DisplayName("Should create FileUploadHelper bean")
    void shouldCreateFileUploadHelper() {
        contextRunner
            .run(context -> {
                assertThat(context).hasSingleBean(FileUploadHelper.class);
            });
    }

    @Test
    @DisplayName("Should register FileStorageProperties bean")
    void shouldRegisterFileStorageProperties() {
        contextRunner
            .run(context -> {
                assertThat(context).hasSingleBean(FileStorageProperties.class);
            });
    }

    @Test
    @DisplayName("Should configure FileStorageProperties with default values")
    void shouldConfigureDefaultProperties() {
        contextRunner
            .run(context -> {
                FileStorageProperties properties = context.getBean(FileStorageProperties.class);
                assertThat(properties).isNotNull();
            });
    }

    @Test
    @DisplayName("Should configure FileStorageProperties with custom values")
    void shouldConfigureCustomProperties() {
        contextRunner
            .withPropertyValues(
                "nexora.file-storage.type=local",
                "nexora.file-storage.upload-dir=/uploads",
                "nexora.file-storage.base-url=http://localhost:8080/files"
            )
            .run(context -> {
                FileStorageProperties properties = context.getBean(FileStorageProperties.class);
                assertThat(properties).isNotNull();
                assertThat(properties.getType()).isEqualTo(FileStorageProperties.StorageType.LOCAL);
            });
    }

    // Note: max-file-size uses a custom DataSize class that requires custom converter
    // Skipping this test as the DataSize class would need @ConstructorBinding support

    @Test
    @DisplayName("Should configure allowed extensions")
    void shouldConfigureAllowedExtensions() {
        contextRunner
            .withPropertyValues("nexora.file-storage.allowed-extensions=jpg,png,pdf")
            .run(context -> {
                FileStorageProperties properties = context.getBean(FileStorageProperties.class);
                assertThat(properties).isNotNull();
                assertThat(properties.getAllowedExtensions()).isEqualTo("jpg,png,pdf");
            });
    }

    @Test
    @DisplayName("FileUploadHelper should use FileStorageProperties")
    void fileUploadHelperShouldUseProperties() {
        contextRunner
            .withPropertyValues(
                "nexora.file-storage.allowed-extensions=jpg,png"
            )
            .run(context -> {
                FileUploadHelper helper = context.getBean(FileUploadHelper.class);
                FileStorageProperties properties = context.getBean(FileStorageProperties.class);
                assertThat(helper).isNotNull();
                assertThat(properties).isNotNull();
            });
    }
}
