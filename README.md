# Calculator Mod for Minecraft 1.21.1 (Fabric)

ゲーム内で電卓が使えるFabric Modです。

## 機能

- **キーバインド**: デフォルトは `C` キーで電卓を開く（設定から変更可能）
- **四則演算**: 足し算・引き算・掛け算・割り算
- **追加機能**: パーセント計算（%）、符号反転（±）
- **キーボード入力**: 数字キー・演算子キー・Enterで操作可能
- **ゲームを邪魔しない**: 電卓を開いてもゲームは一時停止しない

## 操作方法

| ボタン | 機能 |
|--------|------|
| `C` | 全クリア |
| `⌫` | 一文字削除 (Backspaceキーでも可) |
| `±` | 符号反転 |
| `%` | パーセント変換 |
| `÷ × − +` | 四則演算 |
| `=` | 計算実行 (Enterキーでも可) |

### キーボードショートカット
- 数字: `0`〜`9`
- 演算子: `+` `-` `*` `/`
- 計算実行: `Enter`
- 削除: `Backspace`
- 全クリア: `Home`
- 閉じる: `ESC`

## インストール方法

1. [Fabric Loader](https://fabricmc.net/use/) 0.16.0以上をインストール
2. [Fabric API](https://modrinth.com/mod/fabric-api) をmodsフォルダに入れる
3. `calculator-mod-1.0.0.jar` をmodsフォルダに入れる

## ビルド方法

```bash
./gradlew build
```

生成されたJARは `build/libs/calculator-mod-1.0.0.jar` にあります。

## 必要環境

- Minecraft 1.21.1
- Fabric Loader 0.16.0+
- Fabric API 0.102.0+
- Java 21+
