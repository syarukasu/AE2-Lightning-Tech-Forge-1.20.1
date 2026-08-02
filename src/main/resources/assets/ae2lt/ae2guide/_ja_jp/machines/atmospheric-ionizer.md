---
navigation:
  title: 大気イオン化装置
  icon: ae2lt:atmospheric_ionizer
  parent: machines/machines-index.md
item_ids:
  - ae2lt:atmospheric_ionizer
  - ae2lt:clear_condensate
  - ae2lt:rain_condensate
  - ae2lt:thunderstorm_condensate
---

# 大気イオン化装置

<Row>
  <BlockImage id="ae2lt:atmospheric_ionizer" scale="4" />
</Row>

**大気イオン化装置**は気象制御装置です。 **天候凝縮液** と AE のエネルギーを消費して、世界の天気を特定の状態に強制し、ライトニングコレクター に信頼できる 自然落雷 を供給できるようにします。

## 天候凝縮液

天候凝縮液 には 3 つのタイプがあり、気象状態ごとに 1 つずつあります。

<ItemGrid>
  <ItemIcon id="ae2lt:clear_condensate" />
  <ItemIcon id="ae2lt:rain_condensate" />
  <ItemIcon id="ae2lt:thunderstorm_condensate" />
</ItemGrid>

| 凝縮水 | 対象天気 | AE コスト | 持続時間 |
|------------|----------------|---------|----------|
| 晴天凝縮液 | クリア | 500,000 AE | 12,000 ～ 180,000 ティック |
| 雨天凝縮液 | 雨 | 1,000,000 AE | 12,000 ～ 24,000 ティック |
| 雷雨凝縮液 | 雷雨 | 8,000,000 AE | 3,600 ~ 15,600 ティック |

## 動作フロー

1. 大気イオン化装置 を MEネットワーク に接続します
2. 必要な 天候凝縮液 を入力スロットに入力します
3. マシンは MEネットワーク から AE を連続的に引き出してイオン化します
4. イオン化が完了すると、世界の気象は強制的に目標の状態になります
5. 凝縮水が消費される

## 注記

* 大気イオン化装置はFEではなく、MEネットワークから**AEエネルギー**を消費します
* 雷雨凝縮液 の一回使用コストが最も高くなります (8,000,000 AE) - ネットワークに十分なエネルギーがあることを確認してください
* 天候に対応できない寸法では、機械は動作できません
* 対象の天候がすでに現在の天候である場合、マシンは凝縮水を消費しません
