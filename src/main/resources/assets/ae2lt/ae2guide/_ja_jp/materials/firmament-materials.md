---
navigation:
  title: 天穹素材
  icon: ae2lt:firmament_alloy_ingot
  parent: materials/materials-index.md
item_ids:
  - ae2lt:firmament_dust
  - ae2lt:firmament_essence
  - ae2lt:firmament_mixture
  - ae2lt:firmament_alloy_ingot
  - ae2lt:firmament_superconducting_wire
  - ae2lt:inactive_firmament_spirit_core
  - ae2lt:firmament_spirit_core_oculus
  - ae2lt:firmament_spirit_core_core
  - ae2lt:firmament_spirit_core_conduit
  - ae2lt:firmament_spirit_core_stride
---

# 天穹素材

<ItemImage id="ae2lt:firmament_alloy_ingot" scale="2" float="left" />

天穹 マテリアルは、AE2 Lightning Tech のエンド層マテリアル ラインです。すべてはエンドでのみ入手できる天穹の粉から始まり、天穹変換コアとオーバーロード処理工場を経て、セレスティウィーヴのアーマーセットと電磁レールガンのコアコンポーネントへと段階的に洗練されていく。

<ItemGrid>
  <ItemIcon id="ae2lt:firmament_dust" />
  <ItemIcon id="ae2lt:firmament_essence" />
  <ItemIcon id="ae2lt:firmament_mixture" />
  <ItemIcon id="ae2lt:firmament_alloy_ingot" />
  <ItemIcon id="ae2lt:firmament_superconducting_wire" />
</ItemGrid>

## 天穹の粉

天穹の粉は素材系統全体の起点であり、**ジ・エンドでのみ生成できます**。

ジ・エンドの**建築上限**に<ItemLink id="ae2:annihilation_plane" />を**上向き**で設置し、電力の通ったMEネットワークへ接続します。条件を満たすと、消滅面はブロックを壊さずに天穹の粉を生成し、約10秒ごとに1個をネットワークストレージへ搬入します。

条件:

* ディメンションは終了でなければなりません
* 殲滅面は上向きでなければなりません
* 消滅面は建築上限へ置く必要があります（通常のジ・エンドではy=255。ワールド高度が変更されている場合は、その上限が使われます）
* 消滅面は、電力の通ったアクティブなネットワークへ接続する必要があります

> 条件を満たす各消滅面は独立して生成されるため、いくつかを並行して実行して出力をスケールアップできます。

## 天穹変換コア

ほとんどの精製ステップは **天穹変換コア** に依存します。このブロックは作成できず、**天穹宇宙船** の内側にのみ存在します。エンド島の外側に浮かぶ構造物です。中央の島を越えて移動して見つけてください。コアは破壊できず、採掘したり移動したりすることはできず、宇宙船の構造内でのみ動作するため、その場所で使用してください。

コアはエネルギーも雷も消費せず、各ジョブは短い待機後に終了します。挿入するマテリアルを手に持ってコアを右クリックし、**空の手で右クリックして**結果を収集します。一度に最大 3 つの入力を受け入れ、各レシピで定義された出力を生成します。ホッパーとパイプはどの側面からも材料を挿入し、製品を取り出すことができるため、宇宙船上に自動生産ラインを構築することができます。

## 精製されたマテリアル

天穹の粉 は 2 つの生産ラインに沿って処理されます。

| 素材 | で製造されました | 主に次の用途に使用されます。 |
|------|---------|---------|
| 天穹エッセンス | 天穹変換コア | 天穹超伝導ワイヤー 以上のレシピ |
| 天穹混合物 | [オーバーロード処理工場](../machines/overload-processing-factory.md) | を 天穹合金インゴット に精製 |
| 天穹合金インゴット | 天穹変換コア | セレスティウィーヴおよび 電磁レールガン の主な材料 |
| 天穹超伝導ワイヤー | [オーバーロード処理工場](../machines/overload-processing-factory.md) | のエネルギー導管セレスティウィーヴ |

これらのレシピでは、大空の材料に加えて、オーバーロード合金、ネザライトスクラップ、ファントム膜などの補助材料も必要です。 天穹合金インゴット は MOD の最上位構造マテリアルであり、天穹超伝導ワイヤー は セレスティウィーヴセットのエネルギー伝導コンポーネントとして機能します。

## 天穹霊核

スピリットコアは、セレスティウィーヴアーマーセットの中心です。

<ItemLink id="ae2lt:inactive_firmament_spirit_core" />は、エンドシティの宝箱から見つかることがあります。天穹変換コアで処理すると**活性化**され、4種類の天穹霊核を1個ずつ生成します。

<ItemGrid>
  <ItemIcon id="ae2lt:firmament_spirit_core_oculus" />
  <ItemIcon id="ae2lt:firmament_spirit_core_core" />
  <ItemIcon id="ae2lt:firmament_spirit_core_conduit" />
  <ItemIcon id="ae2lt:firmament_spirit_core_stride" />
</ItemGrid>

4 つのコアは、[セレスティウィーヴ](../celestweave.md) セットの オキュラス、コア、コンジット、ストライド ピースに対応しており、[ライトニング組立室](../machines/lightning-assembly-chamber.md) で各ピースを組み立てるための重要なコンポーネントです。
