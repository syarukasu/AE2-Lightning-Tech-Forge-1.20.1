---
navigation:
  title: 拡張オーバーロードパターンプロバイダー
  icon: ae2lt:extended_overloaded_pattern_provider
  parent: overloaded-network/overloaded-network-index.md
item_ids:
  - ae2lt:extended_overloaded_pattern_provider
  - ae2lt:overloaded_pattern_provider_upgrade
---

# 拡張オーバーロードパターンプロバイダー

<Row>
  <BlockImage id="ae2lt:extended_overloaded_pattern_provider" scale="4" />
</Row>

**拡張オーバーロードパターンプロバイダー**は、<ItemLink id="ae2lt:overloaded_pattern_provider" />の大容量版です。**無線モード**、自動回収、分配方式、**オーバーロードパターン対応**など、オーバーロードパターンプロバイダーの機能を**すべて**継承しています。違いは、大量のパターンを扱う大規模自動化ネットワーク向けの**複数ページ式パターンスロット**です。

無線モード、回収モード、分配方式、オーバーロードパターンの挙動は通常版と同じです。詳細は<ItemLink id="ae2lt:overloaded_pattern_provider" />のページを参照してください。

## パターン容量

パターンスロットは、1ページ36スロットとして管理されます。

* 初期設定は**4ページ**、合計**144パターンスロット**
* ページ数はMOD設定で**1～64ページ**（36～2304スロット）に変更可能

通常のオーバーロードパターンプロバイダーは36スロット固定で、1ページ分に相当します。

## 入手方法

### 直接クラフト

オーバーロードパターンプロバイダーと<ItemLink id="ae2lt:ultimate_overload_core" />からクラフトします。

<RecipeFor id="ae2lt:extended_overloaded_pattern_provider" />

### 設置したままアップグレード

<ItemImage id="ae2lt:overloaded_pattern_provider_upgrade" scale="2" float="left" />

すでに設置・設定済みの**オーバーロードパターンプロバイダー**がある場合は、**オーバーロードパターンプロバイダーアップグレード**を使うと、その場で拡張版へ更新できます。パターン、無線接続、設定、ブロックの向きは**すべて維持**されるため、撤去して接続し直す必要はありません。

使い方：オーバーロードパターンプロバイダーアップグレードを持ち、設置済みのオーバーロードパターンプロバイダーを**右クリック**します。アップグレード1回につき、アイテムを1個消費します。

注意：

* アップグレードできるのは**オーバーロードパターンプロバイダー**だけです。通常のパターンプロバイダーには作用しません
* すでに拡張済みのプロバイダーは再度アップグレードできません

アップグレードアイテムは、拡張オーバーロードパターンプロバイダー1個とインゴットからクラフトします。

<RecipeFor id="ae2lt:overloaded_pattern_provider_upgrade" />
