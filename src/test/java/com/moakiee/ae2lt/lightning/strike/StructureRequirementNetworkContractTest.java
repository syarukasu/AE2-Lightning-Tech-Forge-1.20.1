package com.moakiee.ae2lt.lightning.strike;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

final class StructureRequirementNetworkContractTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/moakiee/ae2lt/lightning/strike/StructureRequirement.java");

    @Test
    void readerAndWriterUseOffsetBlockConsumeOrder() throws Exception {
        String source = Files.readString(SOURCE);
        String reader = source.substring(
                source.indexOf("public static StructureRequirement fromNetwork"),
                source.indexOf("public void toNetwork"));
        String writer = source.substring(source.indexOf("public void toNetwork"));

        // ログイン時のレシピ同期を壊さないよう、双方のフィールド順を同じ契約へ固定する。
        assertOrdered(reader, "buffer.readBlockPos()", "buffer.readResourceLocation()", "buffer.readBoolean()");
        assertOrdered(writer, "buffer.writeBlockPos(offset)", "buffer.writeResourceLocation", "buffer.writeBoolean(consume)");
    }

    private static void assertOrdered(String source, String first, String second, String third) {
        int firstIndex = source.indexOf(first);
        int secondIndex = source.indexOf(second);
        int thirdIndex = source.indexOf(third);

        assertTrue(firstIndex >= 0, "通信フィールドが見つかりません: " + first);
        assertTrue(secondIndex > firstIndex, "通信フィールドの順序が不正です: " + second);
        assertTrue(thirdIndex > secondIndex, "通信フィールドの順序が不正です: " + third);
    }
}
