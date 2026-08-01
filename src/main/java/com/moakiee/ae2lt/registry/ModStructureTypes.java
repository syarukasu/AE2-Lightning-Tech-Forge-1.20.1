package com.moakiee.ae2lt.registry;

import com.moakiee.ae2lt.AE2LightningTech;
import com.moakiee.ae2lt.worldgen.FirmamentStarshipPiece;
import com.moakiee.ae2lt.worldgen.FirmamentStarshipStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModStructureTypes {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, AE2LightningTech.MODID);
    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECES =
            DeferredRegister.create(Registries.STRUCTURE_PIECE, AE2LightningTech.MODID);

    public static final RegistryObject<StructureType<FirmamentStarshipStructure>>
            FIRMAMENT_STARSHIP =
                    STRUCTURE_TYPES.register("firmament_starship", () -> () -> FirmamentStarshipStructure.CODEC);

    public static final RegistryObject<StructurePieceType> FIRMAMENT_STARSHIP_PIECE =
            STRUCTURE_PIECES.register(
                    "firmament_starship_piece",
                    () -> (context, tag) -> new FirmamentStarshipPiece(context.structureTemplateManager(), tag));

    private ModStructureTypes() {
    }
}
