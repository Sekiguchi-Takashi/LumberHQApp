# HANDOFF.md — LumberHQApp(AI材木商HQ)v1.0

## プロジェクト概要
「一人 + AI」木材事業(テーブル・板材の仕入→保管→販売)の**本部アプリ**。
全9部門の役割定義を閲覧でき、各部門に実務ツールを1つずつ搭載する。
役割定義書の正本は `docs/` の md ファイル。アプリ内の記述は要約版。

## ビルド規約(Appathy標準・変更禁止)
- AGP **8.5.2** / Kotlin **1.9.24** / Gradle **8.9**(Actionsで手動導入、**ラッパーなし**)
- 外部依存**ゼロ**(androidx不使用、`android.app.Activity` 直継承)
- **プログラマティックUIのみ**(XMLレイアウトなし)
- 署名:コミット済み `app/debug.keystore`(alias: androiddebugkey / pass: android)
- APK取得:GitHub Actions の Artifacts(`LumberHQApp-v1.0-debug`)

## ファイル構成
```
LumberHQApp/
├── deploy.sh                 # Appathy標準デプロイ(冪等・自フォルダcd)
├── HANDOFF.md                # 本ファイル
├── settings.gradle / build.gradle
├── .github/workflows/build.yml
├── docs/                     # ★役割定義書の正本(9部門)
│   ├── DEPT_STRATEGY_v1.0.md      経営企画室(統括AI)
│   ├── DEPT_PROCUREMENT_v1.0.md   調達部(仕入判断AI)
│   ├── DEPT_QW_WAREHOUSE_v1.0.md  品質・倉庫部(品質保管AI)
│   ├── DEPT_SALES_LOGI_v1.1.md    販売・物流部(販売物流AI)
│   ├── DEPT_CUSTOMER_v1.0.md      顧客接点部(顧客接点AI)
│   ├── DEPT_FINANCE_v1.0.md       経理・財務部(会計AI)
│   ├── DEPT_SERVICE_v2.0.md       サービス調達部(旧・外注管理部)
│   ├── DEPT_LEGAL_v1.0.md         法務・コンプライアンス部(質問回答型)
│   └── DEPT_IT_v1.0.md            IT・自動化推進部(質問回答型)
└── app/
    ├── build.gradle
    ├── debug.keystore
    └── src/main/
        ├── AndroidManifest.xml
        └── java/com/appathy/lumberhq/MainActivity.kt  # 全画面・全ツール
```

## アプリ構成(v1.0)
- ホーム:9部門カード → タップで部門画面
- 部門画面:役割・主要業務・KPI +「部門ツール」
- データ保存:SharedPreferences(`lumberhq`)。台帳系は改行区切り文字列、行の長押しで削除。

| 部門 | ツール | prefsキー |
|---|---|---|
| 経営企画室 | 代表時間トラッカー(週次予算 vs 実績) | time_entries / time_budget |
| 調達部 | 仕入上限額 計算機(売価逆算) | — |
| 品質・倉庫部 | 個体カード台帳 簡易版(K-0001自動採番) | kotai / kotai_seq |
| 販売・物流部 | 配送サイズ判定(三辺合計+重量) | — |
| 顧客接点部 | 返信テンプレ5種(タップでコピー) | — |
| 経理・財務部 | 実質粗利 計算機 | — |
| サービス調達部 | サブスク台帳(月額合計・年換算) | subs |
| 法務 | 開業チェックリスト(7項目・保存) | check_* |
| IT | 自動化候補ランキング(年間工数を自動計算) | auto_items |

## 会社設計の要点(全docsに共通)
- 一人+AI企業。AIは判断・起案、代表が実行(支払・契約・物理作業)。
- 外注=既存サービスの利用のみ(配送・レンタル倉庫・SaaS)。人の代替外注はしない。
- 顧客接点は自社HP+注文フォームのみ。対面ゼロ。リピーター重視(核は入荷通知)。
- 法務・ITはPhase 1では質問回答型。フェーズ移行(統括AI判定)で試行・提案型へ進化。
- 個体カードが在庫・出品・会計の唯一の情報源(個別法評価)。

## 既知の注意点
- `git init` は必ずプロジェクトフォルダ内で(deploy.sh が冒頭で自フォルダに cd するので、必ず deploy.sh 経由でpushすること)。ホームで init するとトークン露出(GH013)リスク。
- 旧ドキュメント `DEPT_OUTSOURCE_v1.0.md`(外注管理部)は方針変更により廃止済み。`DEPT_SERVICE_v2.0.md` が後継。リポジトリには含めない。
- 配送サイズ判定の区分はあくまで一般的目安。実料金は送料マスタ(サービス調達部の成果物)を正とする。

## 次の候補(未実装)
- 個体カードのQR生成・ステータス(在庫/出品中/受注済/出荷済)管理
- docs/ の md をアプリ内で直接閲覧(assetsに同梱)
- 統合ブリーフ画面(各ツールのデータを1画面に集約)
- 通知アプリ基盤(LINE Messaging API)との温湿度アラート連携
