# AE2 Lightning Tech Forge 1.20.1 port codebase map

> **Navigation only.** このMapはCodex・LLM・reviewerの探索量を減らすためのindexです。仕様判断はREADME、PORT_NOTES、public API docs、現行Issueを使用します。

## Repository identity

```text
Canonical:  syarukasu/AE2-Lightning-Tech-Forge-1.20.1
Retired:    syarukasu/AE2-Lightning-Tech
```

旧forkはredirect/upstream履歴参照専用です。明示的な履歴比較taskを除き、旧forkのsource、Issue、PR、Mapを横断検索しません。このMapと同じ名前のファイルが旧forkに存在しても正本として扱いません。

## 使い方

1. [`../AGENTS.md`](../AGENTS.md)を読む。
2. 下のTask routeを1つ選ぶ。
3. `Read first`と`Source scope`だけを開き、symbol検索から始める。
4. compile/test結果が別package依存を示した場合だけscopeを広げる。

初期読込の対象外:

```text
build/**
.gradle/**
生成JAR / run directory / logs
全assets / 全recipes / 全lang
全block entity / 全client code
対象外optional integration
syarukasu/AE2-Lightning-Tech（retired fork）
```

## 固定座標

```text
Minecraft          1.20.1
Forge              47.4.20
Required           Applied Energistics 2
Status             unofficial upstream port/support tracker
Stable API         com.moakiee.ae2lt.api.*
Source license     LGPL-3.0-only
Assets license     CC BY-NC-SA 3.0
Canonical repo     syarukasu/AE2-Lightning-Tech-Forge-1.20.1
```

## Task router

| Route | Task | Read first | Source scope | Verification scope |
| --- | --- | --- | --- | --- |
| `C0` | Port方針、upstream attribution、license、version | `../README.md`, `../PORT_NOTES.md`, `../CREDITS.md`, license files | docs/build metadata中心 | noticesとJAR contents、`build` |
| `A1` | Public addon API、capability、event、frozen IDs | READMEのPublic API、`api` package docs | `api`と実装側の直接bridgeだけ | API compile/contract tests、addon smoke |
| `L1` | Lightning energy、tier、collector、grid storage、weather | READMEのLightning sections | 対象`grid`/`blockentity`/`event`/`config`だけ | unit test、実lightning capture |
| `M1` | Assembly Chamber、Simulation Room、Factory、Catalyzer、Tesla Coil | READMEのMachinery | 対象machine/block entity/menu/recipe package | machine test、multiblock実動 |
| `W1` | Wireless controller/receiver、frequency、security、binding | READMEのWireless/Public API | `api/frequency`とinternal frequency/device/networkの対象だけ | bind/reconnect/save/restart test |
| `N1` | Overloaded ME Controller、cable、interface、pattern provider/encoder | READMEのOverloaded ME Network | 対象`device`/`grid`/`blockentity`/`menu`/network | AE2 network integration test |
| `C1` | Client screen、renderer、toolbar、optional UI integration | READMEのoptional integrations | `client`と対象optional integrationだけ | client load、visual/manual smoke |
| `R1` | Registry、recipes、loot、models、lang、data | READMEのfeature/ID contract | registration classと対象resource namespaceだけ | datagen/resource validation、game load |
| `V1` | Forge port build、CI、packaging、CurseForge docs | `../PORT_NOTES.md`, `../build.gradle`, workflow、submission docs | build files、`src/test`、release metadata | `clean build` |

## Package clusters

| Cluster | Typical paths | Responsibility |
| --- | --- | --- |
| Entrypoint/registration | `AE2LightningTech.java`, registry/config packages | Forge lifecycleと全feature registration |
| Stable API | `api`, `api/client`, `api/event`, `api/frequency` | third-party addon contract |
| Lightning runtime | `grid`, `event`, lightning関連block entities | energy storage/capture/tier/grid ownership |
| Machines | machine関連block/blockentity/menu/recipe | multiblock、processing、inventory/power |
| Overloaded AE network | `device`, `grid`, network/menu関連 | controller/cable/provider/interface/encoder |
| Wireless | frequency/security/binding/network関連 | controller/receiver membershipとrecovery |
| Client | `client`とrenderer/screen integration | visual/UIのみ |
| Resources | `src/main/resources` | Forge metadata、assets、recipes、tags/loot |

route選択後に対象package直下だけを列挙する。repository全体のrecursive treeを初手にしない。

## 主要entrypointとhot areas

| Purpose | Path |
| --- | --- |
| Mod entrypoint/registration | `src/main/java/com/moakiee/ae2lt/AE2LightningTech.java` |
| Stable API root | `src/main/java/com/moakiee/ae2lt/api` |
| Lightning capability | `src/main/java/com/moakiee/ae2lt/api/AE2LTCapabilities.java` |
| Collection event | `src/main/java/com/moakiee/ae2lt/api/event/LightningCollectedEvent.java` |
| Frequency facade | `src/main/java/com/moakiee/ae2lt/api/frequency/FrequencyApi.java` |
| Frequency binding access | `src/main/java/com/moakiee/ae2lt/api/frequency/FrequencyBindingAccess.java` |
| Forge metadata | `src/main/resources/META-INF/mods.toml` |
| Build/version coordinates | `build.gradle`, `gradle.properties` |
| Distribution text | `CURSEFORGE_DESCRIPTION.md`, `CURSEFORGE_SUBMISSION_CHECKLIST.md` |

`AE2LightningTech.java`は大型registration fileなので、対象DeferredRegister/event/listener symbolを検索して必要範囲だけ読む。

## 文書の読み分け

| Need | Document |
| --- | --- |
| feature、dependencies、public API | `../README.md` |
| Forge 1.20.1 port範囲 | `../PORT_NOTES.md` |
| attribution | `../CREDITS.md` |
| source/assets license boundary | `../LICENSE`, `../LICENSE_ASSETS.md` |
| CurseForge公開情報 | `../CURSEFORGE_DESCRIPTION.md`, checklist/changelog |
| 中国語user docs | `../README_zh_CN.md`（翻訳変更時のみ） |
| issue intake | `.github/ISSUE_TEMPLATE`の対象template |

## 最小検証コマンド

```text
./gradlew clean build --no-daemon
```

API変更ではconsumer compile smoke、wireless/machine変更では実Forge world、save/restart、client/server両側確認を追加する。実行していない受入を完了扱いしない。

## 省トークン用prompt

```text
AGENTS.mdとdocs/CODEBASE_MAP.mdの<Route ID>だけを基準に作業する。
Task: <作業内容>
最初はroute記載の文書、package、直近test以外を読まない。
別scopeへ広げる場合はcompile dependencyまたはtest failureを根拠として示す。
旧fork syarukasu/AE2-Lightning-Techは、明示的な履歴比較以外では読まない。
Unofficial port表記、public API、license境界を維持する。
```
