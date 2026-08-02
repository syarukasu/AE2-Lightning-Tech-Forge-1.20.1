---
navigation:
  title: オーバーロードプロセッサ
  icon: ae2lt:overload_processor
  parent: materials/materials-index.md
item_ids:
  - ae2lt:overload_processor
  - ae2lt:overload_circuit_board
  - ae2lt:unoverloaded_circuit_board
  - ae2lt:overload_inscriber_press
---

# オーバーロードプロセッサ

<ItemGrid>
  <ItemIcon id="ae2lt:overload_processor" />
  <ItemIcon id="ae2lt:overload_circuit_board" />
  <ItemIcon id="ae2lt:unoverloaded_circuit_board" />
  <ItemIcon id="ae2lt:overload_inscriber_press" />
</ItemGrid>

**オーバーロードプロセッサ** は、ほとんどのオーバーロードマシンや上位デバイスの作成に使用される高度なプロセッサです。その製造は、AE2刻印機 を介した複数のステップの連鎖に従います。

## クラフトチェーン

1. **オーバーロード刻印プレス**を取得します(ライトニング変換、ライトニングシミュレーション室、またはオーバーロード処理工場経由)
2. AE2刻印機 と 未加工オーバーロード合金 でプレスを使用して、**未オーバーロード回路基板** を生成します
3. 刻印機 の 未オーバーロード回路基板 を再度処理して、**オーバーロード回路基板** を生成します
4. オーバーロード回路基板 を AE2分子アセンブラーの他のマテリアルと組み合わせて、**オーバーロードプロセッサ** を作成します

## アイテム

| アイテム | 役割 |
|------|------|
| オーバーロード刻印プレス | AE2刻印機 で使用されるプレス プレート。 ライトニング変換 または機械処理によって取得 |
| 未オーバーロード回路基板 | 中間生成物; 未加工オーバーロード合金 を押しながら オーバーロード刻印プレス を押します |
| オーバーロード回路基板 | 完成した回路基板。 刻印機 の 未オーバーロード回路基板 から処理されました |
| オーバーロードプロセッサ | マシンおよびデバイスのレシピで使用される最終プロセッサー |
