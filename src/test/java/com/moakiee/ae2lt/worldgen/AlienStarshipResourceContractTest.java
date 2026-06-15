package com.moakiee.ae2lt.worldgen;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

final class AlienStarshipResourceContractTest {

    @Test
    void alienStarshipUsesOverworldHighAltitudeWorldgenResources() throws Exception {
        String structure = Files.readString(Path.of(
                "src/main/resources/data/ae2lt/worldgen/structure/alien_starship.json"));
        String structureSet = Files.readString(Path.of(
                "src/main/resources/data/ae2lt/worldgen/structure_set/alien_starship.json"));
        String biomeTag = Files.readString(Path.of(
                "src/main/resources/data/ae2lt/tags/worldgen/biome/has_alien_starship.json"));
        String templatePool = Files.readString(Path.of(
                "src/main/resources/data/ae2lt/worldgen/template_pool/alien_starship/start.json"));

        assertTrue(structure.contains("\"type\": \"minecraft:jigsaw\""));
        assertTrue(structure.contains("\"biomes\": \"#ae2lt:has_alien_starship\""));
        assertTrue(structure.contains("\"start_pool\": \"ae2lt:alien_starship/start\""));
        assertTrue(structure.contains("\"absolute\": 180"), "alien starship should generate high in the overworld sky");
        assertFalse(structure.contains("project_start_to_heightmap"), "sky structure should not be projected to terrain");

        assertTrue(structureSet.contains("\"structure\": \"ae2lt:alien_starship\""));
        assertTrue(structureSet.contains("\"spacing\": 72"));
        assertTrue(structureSet.contains("\"separation\": 24"));

        assertTrue(biomeTag.contains("\"#minecraft:is_overworld\""));

        assertTrue(templatePool.contains("\"location\": \"ae2lt:alien_starship/alien_starship\""));
        assertTrue(templatePool.contains("\"processors\": \"minecraft:empty\""));
    }

    @Test
    void alienStarshipTemplateIsCleanAndSplitsChestLootByLayer() throws Exception {
        Path nbt = Path.of("src/main/resources/data/ae2lt/structure/alien_starship/alien_starship.nbt");
        String snbt = Files.readString(Path.of(
                "src/main/resources/data/ae2lt/structure/alien_starship/alien_starship.snbt"));
        String lowerLoot = Files.readString(Path.of(
                "src/main/resources/data/ae2lt/loot_table/chests/alien_starship_lower.json"));
        String upperLoot = Files.readString(Path.of(
                "src/main/resources/data/ae2lt/loot_table/chests/alien_starship_upper.json"));

        assertTrue(Files.isRegularFile(nbt), "optimized binary structure should exist");
        assertFalse(snbt.contains("Name:\"minecraft:air\""), "optimized template should not place air");
        assertTrue(snbt.contains("LootTable:\"ae2lt:chests/alien_starship_lower\""));
        assertTrue(snbt.contains("LootTable:\"ae2lt:chests/alien_starship_upper\""));
        assertFalse(snbt.contains("minecraft:barrel") && snbt.contains("minecraft:barrel\",nbt:{LootTable"),
                "barrels should remain empty and not become loot containers");

        assertTrue(lowerLoot.contains("\"type\": \"minecraft:chest\""));
        assertTrue(lowerLoot.contains("\"name\": \"ae2:certus_quartz_crystal\""));
        assertTrue(upperLoot.contains("\"type\": \"minecraft:chest\""));
        assertTrue(upperLoot.contains("\"name\": \"ae2lt:floating_matter\""));
    }

    @Test
    void alienStarshipHasDisplayNames() throws Exception {
        String english = Files.readString(Path.of("src/main/resources/assets/ae2lt/lang/en_us.json"));
        String chinese = Files.readString(Path.of("src/main/resources/assets/ae2lt/lang/zh_cn.json"));

        assertTrue(english.contains("\"structure.ae2lt.alien_starship\": \"Alien Starship\""));
        assertTrue(chinese.contains("\"structure.ae2lt.alien_starship\": \"外星飞船\""));
    }
}
