package com.moakiee.ae2lt.item.railgun;

import net.minecraft.util.StringRepresentable;

public enum RailgunModuleType implements StringRepresentable {
    CORE("core"),
    COMPUTE("compute"),
    ACCELERATION("acceleration"),
    OVERLOAD_EXECUTION("overload_execution");

    private final String name;

    RailgunModuleType(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
