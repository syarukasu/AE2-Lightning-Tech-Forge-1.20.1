# AE2 Lightning Tech Forge 1.20.1 port agent entrypoint

このファイルはCodex・LLM・自動レビューが、Forge port全体を毎回読み込まずに作業範囲を決めるための入口です。

## 最小読込手順

1. 最初に本書と [`docs/CODEBASE_MAP.md`](docs/CODEBASE_MAP.md) だけを読む。
2. MapのTask routeを1つ選び、そのrouteに記載された文書・package・直近testだけを開く。
3. Javaファイルは対象symbolを検索し、必要なclass/method範囲だけを読む。
4. compile error、test failure、実依存関係が示した場合だけ隣接packageへ範囲を広げる。
5. 全assets、全recipes、全block entities、全client code、全optional integrationの再帰読込を開始条件にしない。

## 固定契約

```text
Project status             unofficial Forge port/support repository
Minecraft                  1.20.1
Loader                     Forge 47.4.20
Required mod               Applied Energistics 2
Source license             LGPL-3.0-only
Visual assets license      CC BY-NC-SA 3.0
Stable addon API           com.moakiee.ae2lt.api.* only
Default branch             main
```

このportを原作者・AE2 teamの公式releaseと表現しません。upstream attribution、source license、asset license、permanent noticesを削除・曖昧化しません。

`com.moakiee.ae2lt.api.*`だけがthird-party addon向けstable surfaceです。その他package、internal block entity、menu、network、registry実装をpublic compatibility contractとして扱いません。

Lightning storage、wireless frequency/security、AE2 grid connection、machine inventory、recipe、network packet、World/NBTはserver authorityを維持します。性能目的でclient側推測、非同期World mutation、保存形式の無断変更を入れません。

## 安全規則

- Forge 1.20.1/AE2 runtime nameとlifecycleを基準にする。
- Frequency API/binding lifecycleはserver threadとAE2 node lifecycleを維持する。
- Lightning capture eventはinsert前のcancellation/amount rewrite契約を崩さない。
- Public ID、serialized tier名、recipe/block entity IDを無断変更しない。
- optional modがない環境でclass loading errorを起こさない。
- build成功だけでmultiblock、wireless recovery、save/restart、client renderingを検証済みと書かない。

## 編集規則

- source変更では同じpackageのtest、READMEの該当contract、`PORT_NOTES.md`を先に確認する。
- public API変更はAPI package docs、README、互換性/移行説明を同じ変更で更新する。
- entrypoint、主要package、public API、重要testの位置が変わる場合は `docs/CODEBASE_MAP.md` を更新する。
- `AE2LightningTech.java`などの大型fileは対象registration/symbol周辺だけを読む。

## 検証順

```text
対象testまたはcompile task
-> ./gradlew clean build --no-daemon
-> 必要な場合だけForge実環境でworld / machine / network / save / restart確認
```

unit testやCIだけの結果をruntime verifiedとして扱いません。
