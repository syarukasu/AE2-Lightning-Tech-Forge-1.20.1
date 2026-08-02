---
navigation:
  title: オーバーロードデバイス
  icon: ae2lt:overload_device_workbench
  parent: index.md
  position: 50
item_ids:
  - ae2lt:overload_device_workbench
  - ae2lt:electromagnetic_railgun
  - ae2lt:celestweave_oculus
  - ae2lt:celestweave_core
  - ae2lt:celestweave_conduit
  - ae2lt:celestweave_stride
---

# オーバーロードデバイス

オーバーロードデバイスは、<ItemLink id="ae2lt:overload_device_workbench" /> で組み立てられたハイエンド機器です。これらは、ローカル FE バッファー、バインドされた MEネットワーク、およびデバイス固有のモジュールを使用します。

## 利用可能なデバイス

* [電磁レールガン](electromagnetic-railgun.md) — ビーム射撃、チャージショット、チェーンアーク、戦術モジュールを備えた遠隔武器
* [セレスティウィーヴ](celestweave.md) — 移動、防御、ユーティリティ効果のためのモジュラーアーマーセット

## アセンブリ

1. オーバーロード装置作業台 を MEネットワーク に配置します
2. オーバーロードデバイスを挿入します。ワークベンチはワークベンチをそのネットワークに自動的にバインドします
3. <ItemLink id="ae2lt:ultimate_overload_core" /> をコアスロットに取り付けます
4. 互換性のあるモジュールをモジュール入力スロットに挿入します。 1 つのモジュールは約 20 ティック後にインストールされます
5. 構成されたキー (デフォルトは G) で オーバーロード装置ハブ を開き、モジュールと設定を切り替えます

## エネルギーとネットワーク

各デバイスには独自の FE バッファがあり、FE を直接受信できます。 Applied Flux FE ストレージがバインドされた MEネットワーク で利用可能な場合、デバイスはそのネットワークから補充することもできます。

戦闘および装甲モジュールは、同じバインドされたネットワークから **高電圧ライトニング** または **超高電圧ライトニング** を消費する可能性があります。アクティブなモジュールに依存する前に、十分な ライトニングを保存しておいてください。
