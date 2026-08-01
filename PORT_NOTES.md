# Forge 1.20.1 Port Provenance

This private repository preserves the upstream history of
[ae2lt/AE2-Lightning-Tech](https://github.com/ae2lt/AE2-Lightning-Tech).
It uses the upstream `port/1.20.1-forge` branch at commit `b7830ef` as
the platform baseline and ports the upstream `1.1.4` release at commit
`e4bce55f` onto it.

## Runtime target

- Minecraft `1.20.1`
- Forge `47.4.20`
- Applied Energistics 2 `15.4.10`
- Java `17`

The mod id, package names, registered content, recipes, assets, configuration,
and gameplay behavior follow upstream `1.1.4`. Platform APIs, networking,
registries, data components, resource paths, and JSON formats are adapted only
where Minecraft 1.20.1 and Forge 47.4.20 require different interfaces.

The upstream NeoForge project remains the canonical project. This repository
exists solely to maintain the Minecraft 1.20.1 Forge port.

## Version-specific compatibility

Upstream `1.1.4` optionally adds an Overload Processor recipe to ExtendedAE's
1.21-only Crystal Assembler. ExtendedAE for Minecraft 1.20.1 does not register
that machine or recipe type, so the port does not publish an invalid placeholder
recipe. Overload Processors retain both upstream core production paths: the AE2
Inscriber recipe and the 36-output Overload Processing Factory recipe. Empty
storage-cell disassembly is provided directly by AE2 15.4.10's
`BasicStorageCell` behavior rather than duplicate data-component recipes.

## Attribution and licenses

- Source code: GNU Lesser General Public License version 3 (`LICENSE`)
- Textures and visual assets: CC BY-NC-SA 3.0 (`LICENSE_ASSETS.md`)
- Original project and credits: `README.md`

The original author and contributor credits remain intact. Visual assets may
not be used commercially and derivatives must retain attribution and the same
CC BY-NC-SA 3.0 license.
