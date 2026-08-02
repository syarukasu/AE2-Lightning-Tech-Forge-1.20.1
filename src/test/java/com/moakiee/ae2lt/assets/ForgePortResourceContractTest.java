package com.moakiee.ae2lt.assets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
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

    @Test
    void everyMixinSourceIsConfigured() throws Exception {
        assertMixinSourcesConfigured(
                RESOURCES.resolve("ae2lt.mixins.json"),
                JAVA,
                JAVA.resolve("ae2wtlib"));
        assertMixinSourcesConfigured(
                RESOURCES.resolve("ae2lt.ae2wtlib.mixins.json"),
                JAVA.resolve("ae2wtlib"),
                null);
    }

    @Test
    void everyMixinConfigLoadsTheGeneratedRefmap() throws Exception {
        for (String fileName : List.of("ae2lt.mixins.json", "ae2lt.ae2wtlib.mixins.json")) {
            var json = JsonParser.parseString(Files.readString(RESOURCES.resolve(fileName))).getAsJsonObject();
            assertTrue(json.has("refmap"), fileName + " does not declare a refmap");
            assertTrue(
                    json.get("refmap").getAsString().equals("ae2lt.refmap.json"),
                    fileName + " declares the wrong refmap");
        }
    }

    @Test
    void attributionIdentifiesBothUpstreamsAndTheUnofficialPort() throws Exception {
        String credits = Files.readString(Path.of("CREDITS.md"));
        assertTrue(credits.contains("MOAKIEE"), "original AE2LT creator is not credited");
        assertTrue(
                credits.contains("TeamAppliedEnergistics"),
                "Applied Energistics 2 team is not credited");
        assertTrue(credits.contains("unofficial"), "fork status is not disclosed");

        String modsToml = Files.readString(RESOURCES.resolve("META-INF/mods.toml"));
        assertTrue(modsToml.contains("credits = \"${mod_credits}\""));
    }

    @Test
    void japaneseLocalizationRetainsEveryEnglishKey() throws Exception {
        Path lang = RESOURCES.resolve("assets/ae2lt/lang");
        var english = JsonParser.parseString(Files.readString(lang.resolve("en_us.json")))
                .getAsJsonObject();
        var japanese = JsonParser.parseString(Files.readString(lang.resolve("ja_jp.json")))
                .getAsJsonObject();

        assertEquals(english.keySet(), japanese.keySet(), "Japanese localization keys differ from English");
    }

    @Test
    void japaneseGuideRetainsEveryEnglishPage() throws Exception {
        Path guide = RESOURCES.resolve("assets/ae2lt/ae2guide");
        Path japaneseGuide = guide.resolve("_ja_jp");

        Set<String> englishPages;
        try (Stream<Path> paths = Files.walk(guide)) {
            englishPages = paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".md"))
                    .filter(path -> !path.startsWith(guide.resolve("_ja_jp")))
                    .filter(path -> !path.startsWith(guide.resolve("_zh_cn")))
                    .map(path -> guide.relativize(path).toString())
                    .collect(Collectors.toSet());
        }

        Set<String> japanesePages;
        try (Stream<Path> paths = Files.walk(japaneseGuide)) {
            japanesePages = paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".md"))
                    .map(path -> japaneseGuide.relativize(path).toString())
                    .collect(Collectors.toSet());
        }

        assertEquals(englishPages, japanesePages, "Japanese AE2 Guide pages differ from English");
    }

    @Test
    void distributionMetadataMatchesTheSupportedForgePort() throws Exception {
        Properties properties = new Properties();
        try (var reader = Files.newBufferedReader(Path.of("gradle.properties"))) {
            properties.load(reader);
        }

        assertEquals("1.1.4-forge-1.20.1-r8", properties.getProperty("mod_version"));
        assertEquals("LGPL-3.0-only", properties.getProperty("mod_license"));
        assertEquals("[1.20.1,1.20.2)", properties.getProperty("minecraft_version_range"));
        assertEquals("[47.4.20,48)", properties.getProperty("forge_version_range"));
        assertEquals("[15.4.10,15.5.0)", properties.getProperty("ae2_version_range"));
        assertTrue(properties.getProperty("mod_name").contains("Unofficial Forge 1.20.1 Port"));
        assertTrue(properties.getProperty("mod_authors").contains("syarukasu"));
    }

    @Test
    void distributionDocsCannotBeMistakenForUpstreamDownloads() throws Exception {
        String readme = Files.readString(Path.of("README.md"));
        assertFalse(readme.contains("img.shields.io/modrinth"));
        assertFalse(readme.contains("img.shields.io/curseforge"));
        assertTrue(readme.contains("original NeoForge project"));

        String description = Files.readString(Path.of("CURSEFORGE_DESCRIPTION.md"));
        assertTrue(description.chars().allMatch(codePoint -> codePoint <= 0x7f));
        assertTrue(description.contains("independent, unofficial Forge 1.20.1 port"));
        assertTrue(description.contains("syarukasu/AE2-Lightning-Tech-Forge-1.20.1"));
        assertFalse(description.contains("C:\\Users\\"));
    }

    @Test
    void manifestUsesModernForgeMixinMetadata() throws Exception {
        String buildScript = Files.readString(Path.of("build.gradle"));
        assertFalse(buildScript.contains("'TweakClass'"));
        assertFalse(buildScript.contains("'TweakOrder'"));
        assertTrue(buildScript.contains("'MixinConfigs'"));
        assertTrue(buildScript.contains("finalizedBy 'reobfJarJar'"));
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

    private static void assertMixinSourcesConfigured(Path config, Path packageRoot, Path excludedRoot)
            throws Exception {
        var json = JsonParser.parseString(Files.readString(config)).getAsJsonObject();
        var configured = new java.util.HashSet<String>();
        for (String section : List.of("mixins", "client")) {
            if (!json.has(section)) {
                continue;
            }
            for (var element : json.getAsJsonArray(section)) {
                configured.add(element.getAsString());
            }
        }

        try (Stream<Path> paths = Files.walk(packageRoot)) {
            for (Path source : paths.filter(Files::isRegularFile)
                    .filter(candidate -> candidate.toString().endsWith(".java"))
                    .filter(candidate -> excludedRoot == null || !candidate.startsWith(excludedRoot))
                    .filter(candidate -> {
                        try {
                            return Files.readString(candidate).matches("(?s).*@Mixin\\s*\\(.*");
                        } catch (Exception exception) {
                            throw new RuntimeException(exception);
                        }
                    })
                    .toList()) {
                String className = packageRoot.relativize(source).toString()
                        .replace('\\', '.')
                        .replace('/', '.')
                        .replaceFirst("\\.java$", "");
                assertTrue(configured.contains(className), className + " is a mixin source but is not configured");
            }
        }
    }
}
