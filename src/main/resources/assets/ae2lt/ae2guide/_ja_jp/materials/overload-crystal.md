---
navigation:
  title: オーバーロードクリスタル
  icon: ae2lt:overload_crystal
  parent: materials/materials-index.md
item_ids:
  - ae2lt:overload_crystal
  - ae2lt:overload_crystal_dust
  - ae2lt:overload_crystal_block
  - ae2lt:flawless_budding_overload_crystal
  - ae2lt:flawed_budding_overload_crystal
  - ae2lt:cracked_budding_overload_crystal
  - ae2lt:damaged_budding_overload_crystal
  - ae2lt:small_overload_crystal_bud
  - ae2lt:medium_overload_crystal_bud
  - ae2lt:large_overload_crystal_bud
  - ae2lt:overload_crystal_cluster
---

# オーバーロードクリスタル

<ItemImage id="ae2lt:overload_crystal" scale="2" float="left" />

**オーバーロードクリスタル** は、AE2 Lightning Tech の最も基本的かつ重要なマテリアルです。ほぼすべてのゲーム中期および後期のレシピには、それまたはその派生のいずれかが必要です。

## 入手方法

### 栽培 芽生えたオーバーロードクリスタル

オーバーロードクリスタル の主な発生源は、**芽生えたオーバーロードクリスタル** ブロックの表面に自然に成長するクラスターです。

芽生えたオーバーロードクリスタル は、マルチブロック構造を組み立て、それをキャップする 避雷針 上で **落雷** をトリガーすることによって取得されます。詳細については、以下の「芽生えたオーバーロードクリスタル の入手」セクションを参照してください。

### 新進気鋭の階層

芽生えたオーバーロードクリスタル には 4 つの階層があります。

| ティア | 名前 | 衰退 |
|------|------|-------|
| 完璧 | 完璧な芽生えたオーバーロードクリスタル | 成長中に減衰しない |
| 欠陥あり | 傷ついた芽生えたオーバーロードクリスタル | 低い減衰確率 |
| ひび割れ | ひび割れた芽生えたオーバーロードクリスタル | 中程度の減衰確率 |
| 破損しています | 壊れかけの芽生えたオーバーロードクリスタル | 高い減衰確率 |

不完全な出芽ブロック上で芽が成長するたびに、出芽ブロックが 1 層減衰する可能性があります。ダメージを受けた出芽ブロックがさらに減衰すると、通常のオーバーロードクリスタルブロックになります。

> 採掘すると、**シルクタッチ**は、欠陥、ひび割れ、損傷した出芽ブロックを保存します。 **完璧な芽生えたオーバーロードクリスタル** は採掘されると欠陥としてドロップします。

### 芽の成長段階

オーバーロードクリスタル 芽は 4 つの段階を経て成長します。

1. **小さい オーバーロードクリスタル つぼみ** → 壊れると オーバーロードクリスタルの粉 がドロップします
2. **中 オーバーロードクリスタル つぼみ** → 壊れると オーバーロードクリスタルの粉 をドロップ
3. **大 オーバーロードクリスタル つぼみ** → 壊れると オーバーロードクリスタルの粉 をドロップ
4. **オーバーロードクリスタルの塊**（完全成長）→ 壊すと**オーバーロードクリスタル**をドロップします（幸運が有効）

### 加速する成長

<ItemLink id="ae2:growth_accelerator" /> は オーバーロードクリスタル の芽に作用します。出芽ブロックの周りに加速器を配置すると、芽の成長が劇的にスピードアップします。

## 取得 芽生えたオーバーロードクリスタル

芽生えたオーバーロードクリスタル は、3×3 マルチブロックを構築し、その中心の上にある 避雷針 で 落雷 をトリガーすることによって生成されます。 **リッチ**バリアントと**シンプル**バリアントの 2 つの構造が利用可能です。

### リッチ構造 (自然 ライトニング、同一階層変換)

<GameScene zoom="4" background="transparent">
  <ImportStructure src="../assets/assemblies/flawless_budding_overload.snbt" />
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

構造要件:

* AE2の芽生えたケルタスクォーツ の一致する層を中央に配置します
* <ItemLink id="ae2:fluix_block" /> を 4 つの基本的な側面 (E / W / N / S) のそれぞれに同じ高さに配置します。
* 四隅に <ItemLink id="ae2lt:overload_crystal_block" /> を配置します
* 中央の真上に 避雷針 を配置します

構築したら、避雷針 で **自然落雷** を待ちます。出力層は入力と一致します。

| 入力 (中央) | 出力 |
|----------------|--------|
| <ItemLink id="ae2:damaged_budding_quartz" /> | <ItemLink id="ae2lt:damaged_budding_overload_crystal" /> |
| <ItemLink id="ae2:chipped_budding_quartz" /> | <ItemLink id="ae2lt:cracked_budding_overload_crystal" /> |
| <ItemLink id="ae2:flawed_budding_quartz" /> | <ItemLink id="ae2lt:flawed_budding_overload_crystal" /> |
| <ItemLink id="ae2:flawless_budding_quartz" /> | <ItemLink id="ae2lt:flawless_budding_overload_crystal" /> |

> リッチ構造は **自然発生の雷** のみを受け入れます。 オーバーロードクリスタルを運ぶことで召喚された人工雷は誘発しません。

### 単純な構造 (任意の ライトニング、出力は 1 層ドロップされます)

オーバーロードクリスタルブロック が制限されている場合でも、単純な構造でも完璧ではない 3 つの層を生成できます。

* AE2の芽生えたケルタスクォーツ の一致する層を中央に配置します
* <ItemLink id="ae2:quartz_block" /> (ケルタスクォーツブロック) を四隅に配置します
* <ItemLink id="ae2:fluix_block" /> を 4 つの基本的な側面 (E / W / N / S) のそれぞれに同じ高さに配置します。
* 中央の真上に 避雷針 を配置します

**避雷針へ落ちたあらゆる雷**で変換が起動し、出力は入力よりAE2の芽生えた結晶ティアが1段階下がります。

| 入力 (中央) | 出力 |
|----------------|--------|
| <ItemLink id="ae2:chipped_budding_quartz" /> | <ItemLink id="ae2lt:damaged_budding_overload_crystal" /> |
| <ItemLink id="ae2:flawed_budding_quartz" /> | <ItemLink id="ae2lt:cracked_budding_overload_crystal" /> |
| <ItemLink id="ae2:flawless_budding_quartz" /> | <ItemLink id="ae2lt:flawed_budding_overload_crystal" /> |

> 完璧 層は単純な構造では作成できません。完璧な芽生えたオーバーロードクリスタル には 自然発生の雷 を備えた豊富な構造が必要です。

ストライクが着地すると、周囲の外側の 8 つのブロックが消費され、中央のブロックが一致する 芽生えたオーバーロードクリスタル になります。

## 誘導体

| アイテム | 使用 |
|------|-----|
| オーバーロードクリスタルの粉 | HV モードの テスラコイル によって消費されます。いくつかのレシピでも使用されています |
| オーバーロードクリスタルブロック | 建築/装飾ブロック、および 完璧な芽生えたオーバーロードクリスタル マルチブロックの建築にも使用されます |
