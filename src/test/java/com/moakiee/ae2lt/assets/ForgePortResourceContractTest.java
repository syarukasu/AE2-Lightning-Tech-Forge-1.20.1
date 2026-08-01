package com.moakiee.ae2lt.assets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

final class ForgePortResourceContractTest {

    private static final Path RESOURCES = Path.of("src/main/resources");
    private static final Path JAVA = Path.of("src/main/java/com/moakiee/ae2lt/mixin");

    @Test
    void allJsonResourcesParse() throws Exception {
        try (Stream<Path> paths = Files.walk(RESOURCES)) {
            for (Path path : paths.filter(Files::isRegularFile)
                    .filter(candidate -> candidate.toString().endsWith(".json"))
                    .toList()) {
                try (var reader = Files.newBufferedReader(path)) {
                    JsonParser.parseReader(reader);
                }
            }
        }
    }

    @Test
    void forgeResourcesDoNotContainNeoForgeRecipeIds() throws Exception {
        try (Stream<Path> paths = Files.walk(RESOURCES.resolve("data"))) {
            for (Path path : paths.filter(Files::isRegularFile)
                    .filter(candidate -> candidate.toString().endsWith(".json"))
                    .toList()) {
                assertFalse(
                        Files.readString(path).contains("neoforge:"),
                        path + " still contains a NeoForge-only identifier");
            }
        }
    }

    @Test
    void dataPackUsesMinecraft120PluralDirectories() throws Exception {
        List<String> invalidDirectories = List.of("recipe", "loot_table", "advancement", "structure");
        try (Stream<Path> namespaces = Files.list(RESOURCES.resolve("data"))) {
            for (Path namespace : namespaces.filter(Files::isDirectory).toList()) {
                for (String directory : invalidDirectories) {
                    Path candidate = namespace.resolve(directory);
                    if (!Files.isDirectory(candidate)) {
                        continue;
                    }
                    try (Stream<Path> files = Files.walk(candidate)) {
                        assertFalse(
                                files.anyMatch(Files::isRegularFile),
                                candidate + " contains 1.21-style singular data-pack paths");
                    }
                }
            }
        }
    }

    @Test
    void everyConfiguredMixinHasSource() throws Exception {
        assertMixinSourcesExist(RESOURCES.resolve("ae2lt.mixins.json"), JAVA);
        assertMixinSourcesExist(
                RESOURCES.resolve("ae2lt.ae2wtlib.mixins.json"),
                JAVA.resolve("ae2wtlib"));
    }

    private static void assertMixinSourcesExist(Path config, Path packageRoot) throws Exception {
        var json = JsonParser.parseString(Files.readString(config)).getAsJsonObject();
        for (String section : List.of("mixins", "client")) {
            if (!json.has(section)) {
                continue;
            }
            for (var element : json.getAsJsonArray(section)) {
                String className = element.getAsString();
                Path source = packageRoot.resolve(className.replace('.', '/') + ".java");
                assertTrue(Files.isRegularFile(source), className + " is configured but has no source file");
            }
        }
    }
}
