package com.appathy.lumberhq

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

data class Dept(
    val id: String,
    val name: String,
    val aiRole: String,
    val mission: String,
    val tasks: List<String>,
    val kpi: String,
    val toolTitle: String
)

class MainActivity : Activity() {

    private val ink = Color.parseColor("#26221B")
    private val sub = Color.parseColor("#6E6558")
    private val bg = Color.parseColor("#F5F1E8")
    private val panel = Color.parseColor("#FDFBF6")
    private val ai = Color.parseColor("#1F3A5F")
    private val accent = Color.parseColor("#B4451F")
    private val wood = Color.parseColor("#8E6231")
    private val green = Color.parseColor("#2E6B3E")

    private lateinit var prefs: SharedPreferences
    private var currentDept: Dept? = null
    private var showingBrand = false

    private val depts = listOf(
        Dept("keiei", "経営企画室", "統括AI",
            "全部門AIの目標・優先順位を統括し、部門間衝突を裁定。最希少資源「代表の時間」を週次予算で配分し、連絡は1日1回の統合ブリーフに集約する。",
            listOf("全社KPIツリーと仕入枠の管理", "部門間裁定(法令>キャッシュ>約束>粗利)", "日次の統合ブリーフ作成", "事業フェーズの移行判定"),
            "月次粗利額 / 代表時間の予算内運用",
            "代表時間トラッカー"),
        Dept("chotatsu", "仕入部", "仕入判断AI",
            "「いくらでなら仕入れてよいか」を売価逆算で決める。EC卸・オークション監視から仕入上限額の算定・発注起案まで行い、実行は代表。",
            listOf("EC卸・オークションの巡回監視", "売価逆算による仕入上限額の算定", "仕入候補カードの起案", "相場データベースの定点更新"),
            "想定粗利と実績の乖離±10% / 起案承認率",
            "仕入上限額 計算機"),
        Dept("kenpin", "倉庫部", "品質保管AI",
            "検品〜保管〜出庫を統合管理。個体ID+QRで現物と在庫データを常に一致させ、温湿度監視と養生指示で反り・カビから商品価値を守る。",
            listOf("検品チェックリスト生成と等級判定", "個体カードによる1点管理", "温湿度センサー監視と養生指示", "滞留・劣化個体の値下げ提案"),
            "在庫差異ゼロ / 検品48時間以内",
            "個体カード台帳(簡易版)"),
        Dept("hanbai", "販売部", "販売物流AI",
            "受注獲得から配達完了まで一気通貫。「送料込みで儲かる売り方」を商品ごとに設計し、出品前に配送手段と実質粗利を確定させる。",
            listOf("個体カードからの商品ページ生成", "送料込み値付けと価格改定ルール運用", "梱包チェックリストと出荷バッチ指示", "勝ち筋分析を仕入部へ毎週還元"),
            "実質粗利 / 発送2営業日以内 / 破損率1%未満",
            "配送サイズ判定"),
        Dept("marketing", "マーケティング部", "マーケティングAI",
            "入り口は自社HP一本:商品掲載+注文フォーム。対面ゼロ・全ネット完結で、集客〜受注〜再購入までの顧客体験を統合管理。核はリピーター化。",
            listOf("個体カード→商品ページの24時間反映", "注文・問い合わせフォームの受付と回答起案", "入荷通知と購入者限定の先行公開", "リピート率分析と仕入への示唆還元"),
            "リピート率 / 一次返信24時間以内100%",
            "返信テンプレ(タップでコピー)"),
        Dept("keiri", "経理・財務部", "会計AI",
            "記帳・請求・資金繰り予測をAIが起案し、代表は朝の承認リスト1本で確認。振込・申告の実行のみ代表が行う。",
            listOf("クラウド会計の仕訳ルール設計", "13週資金繰り表の常時更新", "商品別の送料込み原価計算", "申告書ドラフトと納付期日管理"),
            "代表の経理時間 週30分以内 / 支払遅延ゼロ",
            "実質粗利 計算機"),
        Dept("jinji", "外部サービス部", "外部サービスAI",
            "配送・レンタル倉庫・SaaSなど既存サービスの選定比較と契約管理。「代表の作業を減らすサービス」を探し続ける。",
            listOf("家具配送・空調倉庫の比較表作成", "契約・サブスク台帳と更新管理", "送料マスタの整備と提供", "過剰プラン・重複契約の削減提案"),
            "固定費の対売上比 / サービス化提案 月1件",
            "サブスク台帳"),
        Dept("homu", "法務チェック部", "法務相談AI",
            "Phase 1 は質問回答に専念:古物商・クリーンウッド法・特商法・個人情報の相談に根拠と確度つきで24時間以内に回答。将来は能動的な改善提案へ進化。",
            listOf("法務相談への回答(根拠・確度つき)", "対外文書の公開前レビュー", "許認可・届出の期限台帳", "高リスク論点の専門家相談推奨"),
            "回答24時間以内100% / レビュー起因事故ゼロ",
            "開業チェックリスト"),
        Dept("it", "AI部", "システムAI",
            "Phase 1 は技術相談に専念:Termux+GitHub Actions前提で「一番手間の少ない作り方」を工数見積つきで回答。将来はプロトタイプつき改善提案へ進化。",
            listOf("個体カード台帳・連携構成の相談回答", "自作かサービス利用かの判定", "部門間データ形式の共通化", "代表スクリプトの保守性レビュー"),
            "回答24時間以内100% / 業務停止ゼロ",
            "自動化候補ランキング")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("lumberhq", Context.MODE_PRIVATE)
        showHome()
    }

    override fun onBackPressed() {
        if (showingBrand) {
            showingBrand = false
            showHome()
        } else if (currentDept != null) {
            currentDept = null
            showHome()
        } else {
            super.onBackPressed()
        }
    }

    // ---------- 共通UI部品 ----------

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    private fun rootColumn(): LinearLayout {
        val col = LinearLayout(this)
        col.orientation = LinearLayout.VERTICAL
        col.setBackgroundColor(bg)
        col.setPadding(dp(16), dp(16), dp(16), dp(32))
        return col
    }

    private fun wrapScroll(v: View): ScrollView {
        val sc = ScrollView(this)
        sc.setBackgroundColor(bg)
        sc.isFillViewport = true
        sc.addView(v)
        return sc
    }

    private fun text(s: String, size: Float, color: Int, bold: Boolean = false): TextView {
        val t = TextView(this)
        t.text = s
        t.textSize = size
        t.setTextColor(color)
        if (bold) t.setTypeface(null, Typeface.BOLD)
        return t
    }

    private fun card(): LinearLayout {
        val c = LinearLayout(this)
        c.orientation = LinearLayout.VERTICAL
        c.setBackgroundColor(panel)
        c.setPadding(dp(14), dp(12), dp(14), dp(12))
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.topMargin = dp(10)
        c.layoutParams = lp
        return c
    }

    private fun numInput(hint: String): EditText {
        val e = EditText(this)
        e.hint = hint
        e.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        e.textSize = 14f
        e.setTextColor(ink)
        return e
    }

    private fun textInput(hint: String): EditText {
        val e = EditText(this)
        e.hint = hint
        e.textSize = 14f
        e.setTextColor(ink)
        return e
    }

    private fun button(label: String, onClick: () -> Unit): Button {
        val b = Button(this)
        b.text = label
        b.textSize = 14f
        b.setBackgroundColor(ai)
        b.setTextColor(Color.WHITE)
        b.setOnClickListener { onClick() }
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.topMargin = dp(8)
        b.layoutParams = lp
        return b
    }

    private fun num(e: EditText): Double = e.text.toString().toDoubleOrNull() ?: 0.0

    // ---------- ブランド表記 ----------
    // JO = 木肌 / v = 緑(継手) / AI = 藍
    private fun wordmark(size: Float): TextView {
        val s = SpannableString("JOvAI")
        s.setSpan(ForegroundColorSpan(wood), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        s.setSpan(ForegroundColorSpan(green), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        s.setSpan(RelativeSizeSpan(0.68f), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        s.setSpan(ForegroundColorSpan(ai), 3, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        s.setSpan(StyleSpan(Typeface.BOLD), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val t = TextView(this)
        t.text = s
        t.textSize = size
        t.letterSpacing = 0.04f
        return t
    }

    // ---------- ホーム画面 ----------

    private fun showHome() {
        val col = rootColumn()
        col.addView(wordmark(30f))
        col.addView(text("ジョバイ / 上のAI・木星のAI", 12f, sub))
        val bizline = text("木材事業本部 — 一人+AI企業 / 仕入・保管・販売 / 全9部門", 12f, sub)
        bizline.setPadding(0, dp(6), 0, dp(2))
        col.addView(bizline)

        val brandLink = text("社名について →", 12f, green, true)
        brandLink.setPadding(0, dp(2), 0, 0)
        brandLink.setOnClickListener {
            showingBrand = true
            showBrand()
        }
        col.addView(brandLink)

        for (d in depts) {
            val c = card()
            c.addView(text(d.name, 17f, ink, true))
            c.addView(text("管理職:" + d.aiRole, 12f, ai, true))
            val m = text(d.mission, 12f, sub)
            m.setPadding(0, dp(4), 0, 0)
            c.addView(m)
            c.setOnClickListener {
                currentDept = d
                showDept(d)
            }
            col.addView(c)
        }

        val foot = text("役割定義書の全文は docs/ フォルダ(リポジトリ)を参照", 11f, sub)
        foot.setPadding(0, dp(16), 0, 0)
        col.addView(foot)
        setContentView(wrapScroll(col))
    }

    // ---------- 社名画面 ----------

    private fun showBrand() {
        val col = rootColumn()

        val back = text("← 部門一覧へ", 13f, ai, true)
        back.setPadding(0, 0, 0, dp(8))
        back.setOnClickListener {
            showingBrand = false
            showHome()
        }
        col.addView(back)

        col.addView(wordmark(38f))
        col.addView(text("ジョバイ", 13f, sub))

        val c1 = card()
        c1.addView(text("二つの意味", 14f, ink, true))
        c1.addView(text("① 上(じょう)のAI", 13f, wood, true))
        c1.addView(text("「上」は上小節(じょうこぶし)など、木材の等級で最上級を指す言葉。最も良い材を選び抜くAI、という宣言。", 13f, sub))
        val jp = text("② 木星(Jove)のAI", 13f, ai, true)
        jp.setPadding(0, dp(8), 0, 0)
        c1.addView(jp)
        c1.addView(text("Jove は木星、そして木曜日(dies Jovis = 木星の日)の語源。樫は木星の神ユピテルの聖樹であり、木星は拡大と成長を司る星とされる。木・時・成長がひとつに繋がる。", 13f, sub))
        col.addView(c1)

        val c2 = card()
        c2.addView(text("小文字の v = 継手(つぎて)", 14f, green, true))
        c2.addView(text("JO(木)と AI を、真ん中の小さな v が継いでいる。木と木を噛み合わせて繋ぐ日本の木工技法になぞらえた、木とAIを継ぐという意味。vだけを小さく書くのはこのため。", 13f, sub))
        col.addView(c2)

        val c3 = card()
        c3.addView(text("表記ルール", 14f, ink, true))
        c3.addView(text("・正式表記は JOvAI(JO大文字 / v小文字 / AI大文字)", 13f, sub))
        c3.addView(text("・読みは「ジョバイ」", 13f, sub))
        c3.addView(text("・JOVAI / Jovai / jovai などの表記は使わない", 13f, sub))
        c3.addView(text("・色は JO=木肌(#8E6231) / v=緑(#2E6B3E) / AI=藍(#1F3A5F)", 13f, sub))
        c3.addView(text("・AI は日本語の「藍」にも掛かる。藍色を使うのはこのため。", 13f, sub))
        col.addView(c3)

        val c4 = card()
        c4.addView(text("事業", 14f, ink, true))
        c4.addView(text("木製テーブル・板材の仕入 → 保管 → 販売。管理職はすべてAIで、人は代表ひとり。外注は既存サービスの利用のみ。顧客接点は自社サイトで完結し、リピーターを軸に育てる。", 13f, sub))
        col.addView(c4)

        col.addView(text("※ 商標(J-PlatPat)・ドメイン・既存社名の確認は法務部の初回タスク", 11f, accent))
        setContentView(wrapScroll(col))
    }

    // ---------- 部門画面 ----------

    private fun showDept(d: Dept) {
        showingBrand = false
        val col = rootColumn()

        val back = text("← 部門一覧へ", 13f, ai, true)
        back.setPadding(0, 0, 0, dp(8))
        back.setOnClickListener {
            currentDept = null
            showHome()
        }
        col.addView(back)

        col.addView(text(d.name, 22f, ink, true))
        col.addView(text("管理職:" + d.aiRole, 13f, ai, true))

        val roleCard = card()
        roleCard.addView(text("役割", 13f, ink, true))
        roleCard.addView(text(d.mission, 13f, sub))
        val tHead = text("主要業務", 13f, ink, true)
        tHead.setPadding(0, dp(8), 0, 0)
        roleCard.addView(tHead)
        for (t in d.tasks) roleCard.addView(text("・" + t, 13f, sub))
        val kHead = text("KPI:" + d.kpi, 12f, accent, true)
        kHead.setPadding(0, dp(8), 0, 0)
        roleCard.addView(kHead)
        col.addView(roleCard)

        val toolCard = card()
        toolCard.addView(text("部門ツール:" + d.toolTitle, 14f, ink, true))
        when (d.id) {
            "keiei" -> toolTimeTracker(toolCard)
            "chotatsu" -> toolBuyLimit(toolCard)
            "kenpin" -> toolKotai(toolCard)
            "hanbai" -> toolShipSize(toolCard)
            "marketing" -> toolTemplates(toolCard)
            "keiri" -> toolGrossProfit(toolCard)
            "jinji" -> toolSubs(toolCard)
            "homu" -> toolChecklist(toolCard)
            "it" -> toolAutoRank(toolCard)
        }
        col.addView(toolCard)
        setContentView(wrapScroll(col))
    }

    // ---------- 台帳ヘルパー(SharedPreferencesに行区切りで保存) ----------

    private fun loadLines(key: String): MutableList<String> {
        val raw = prefs.getString(key, "") ?: ""
        return if (raw.isEmpty()) mutableListOf() else raw.split("\n").toMutableList()
    }

    private fun saveLines(key: String, lines: List<String>) {
        prefs.edit().putString(key, lines.joinToString("\n")).apply()
    }

    // ---------- 1. 経営企画:代表時間トラッカー ----------

    private fun toolTimeTracker(root: LinearLayout) {
        root.addView(text("今週使った「代表時間」を記録し、週次予算の残りを見る。", 12f, sub))
        val budget = numInput("週次予算(分) 例: 600")
        budget.setText(prefs.getInt("time_budget", 600).toString())
        root.addView(budget)
        val label = textInput("作業内容 例: 入荷検品バッチ")
        root.addView(label)
        val mins = numInput("所要時間(分)")
        root.addView(mins)
        val listArea = LinearLayout(this)
        listArea.orientation = LinearLayout.VERTICAL
        val total = text("", 14f, accent, true)

        fun refresh() {
            listArea.removeAllViews()
            val lines = loadLines("time_entries")
            var sum = 0
            for ((i, line) in lines.withIndex()) {
                val p = line.split("|")
                if (p.size < 2) continue
                sum += p[1].toIntOrNull() ?: 0
                val row = text("・" + p[0] + "(" + p[1] + "分)", 13f, ink)
                row.setOnLongClickListener {
                    lines.removeAt(i)
                    saveLines("time_entries", lines)
                    refresh()
                    true
                }
                listArea.addView(row)
            }
            val b = budget.text.toString().toIntOrNull() ?: 600
            total.text = "合計 " + sum + " 分 / 予算 " + b + " 分(残り " + (b - sum) + " 分)"
        }

        root.addView(button("記録する") {
            if (label.text.isNotEmpty() && mins.text.isNotEmpty()) {
                val lines = loadLines("time_entries")
                lines.add(label.text.toString() + "|" + mins.text.toString())
                saveLines("time_entries", lines)
                prefs.edit().putInt("time_budget", budget.text.toString().toIntOrNull() ?: 600).apply()
                label.setText("")
                mins.setText("")
                refresh()
            }
        })
        root.addView(button("今週分をリセット") {
            saveLines("time_entries", listOf())
            refresh()
        })
        root.addView(total)
        root.addView(listArea)
        root.addView(text("行を長押しで削除", 11f, sub))
        refresh()
    }

    // ---------- 2. 調達:仕入上限額 ----------

    private fun toolBuyLimit(root: LinearLayout) {
        root.addView(text("仕入上限 = 想定売価×(1−粗利率) − 送料 − 手数料 − 保管按分", 12f, sub))
        val price = numInput("想定売価(円)")
        val margin = numInput("目標粗利率(%) 例: 35")
        val shipIn = numInput("入庫送料(円)")
        val shipOut = numInput("出荷送料見込(円)")
        val feePct = numInput("販売手数料(%) 例: 5")
        val storage = numInput("保管按分(円) 例: 300")
        for (v in listOf(price, margin, shipIn, shipOut, feePct, storage)) root.addView(v)
        val result = text("", 16f, accent, true)
        root.addView(button("上限額を計算") {
            val p = num(price)
            val limit = p * (1 - num(margin) / 100.0) - num(shipIn) - num(shipOut) - p * num(feePct) / 100.0 - num(storage)
            result.text = if (limit > 0)
                "仕入上限:" + limit.toInt() + " 円"
            else
                "この条件では仕入不可(上限がマイナス)"
        })
        root.addView(result)
    }

    // ---------- 3. 品質・倉庫:個体カード台帳 ----------

    private fun toolKotai(root: LinearLayout) {
        root.addView(text("個体IDを自動採番して1点管理。正式台帳ができるまでの簡易版。", 12f, sub))
        val species = textInput("樹種 例: 杉")
        val size = textInput("寸法 例: 1800×300×30")
        val memo = textInput("メモ(等級・欠点・置き場所)")
        for (v in listOf(species, size, memo)) root.addView(v)
        val listArea = LinearLayout(this)
        listArea.orientation = LinearLayout.VERTICAL

        fun refresh() {
            listArea.removeAllViews()
            val lines = loadLines("kotai")
            for ((i, line) in lines.withIndex()) {
                val row = text(line, 13f, ink)
                row.setPadding(0, dp(4), 0, 0)
                row.setOnLongClickListener {
                    lines.removeAt(i)
                    saveLines("kotai", lines)
                    refresh()
                    true
                }
                listArea.addView(row)
            }
        }

        root.addView(button("個体を登録(ID自動採番)") {
            if (species.text.isNotEmpty()) {
                val seq = prefs.getInt("kotai_seq", 0) + 1
                prefs.edit().putInt("kotai_seq", seq).apply()
                val id = "K-" + seq.toString().padStart(4, '0')
                val lines = loadLines("kotai")
                lines.add(0, id + " | " + species.text + " | " + size.text + " | " + memo.text)
                saveLines("kotai", lines)
                species.setText("")
                size.setText("")
                memo.setText("")
                refresh()
            }
        })
        root.addView(listArea)
        root.addView(text("行を長押しで削除", 11f, sub))
        refresh()
    }

    // ---------- 4. 販売・物流:配送サイズ判定 ----------

    private fun toolShipSize(root: LinearLayout) {
        root.addView(text("三辺合計からサイズ区分の目安を判定。出品前に配送可否を確認する。", 12f, sub))
        val l = numInput("長さ(cm)")
        val w = numInput("幅(cm)")
        val h = numInput("高さ(cm)")
        val kg = numInput("重量(kg)")
        for (v in listOf(l, w, h, kg)) root.addView(v)
        val result = text("", 15f, accent, true)
        root.addView(button("判定する") {
            val sum = num(l) + num(w) + num(h)
            val weight = num(kg)
            val s = StringBuilder()
            s.append("三辺合計:").append(sum.toInt()).append(" cm\n")
            when {
                sum <= 0.0 -> s.append("寸法を入力してください")
                sum <= 60 && weight <= 2 -> s.append("目安:60サイズ")
                sum <= 80 && weight <= 5 -> s.append("目安:80サイズ")
                sum <= 100 && weight <= 10 -> s.append("目安:100サイズ")
                sum <= 120 && weight <= 15 -> s.append("目安:120サイズ")
                sum <= 140 && weight <= 20 -> s.append("目安:140サイズ")
                sum <= 160 && weight <= 25 -> s.append("目安:160サイズ")
                sum <= 180 && weight <= 30 -> s.append("目安:180サイズ(大型)")
                sum <= 200 && weight <= 30 -> s.append("目安:200サイズ(大型)")
                else -> s.append("宅配便の上限超え。\n家具配送便・ヤマト便系サービスを検討")
            }
            if (num(l) > 170) s.append("\n※長さ170cm超は長尺扱いの制限に注意")
            result.text = s.toString()
        })
        root.addView(result)
        root.addView(text("※区分は一般的な目安。実料金は送料マスタ(外部サービス部)を正とする", 11f, sub))
    }

    // ---------- 5. 顧客接点:返信テンプレ ----------

    private fun toolTemplates(root: LinearLayout) {
        root.addView(text("タップでコピーして、メール・フォーム返信に貼り付け。", 12f, sub))
        val templates = listOf(
            "受付" to "ご注文ありがとうございます。内容を確認のうえ、1営業日以内にお支払いのご案内をお送りします。",
            "発送通知" to "本日発送いたしました。追跡番号:___。木材は環境変化で軽微な動きが出ることがあります。到着後は直射日光を避けて保管してください。",
            "到着フォロー" to "先日は購入ありがとうございました。商品の状態はいかがでしょうか。使い方やお手入れのご質問があればいつでもご連絡ください。",
            "反り・割れの説明" to "無垢材は湿度により伸縮する天然素材です。商品ページに実測状態を記載しております。気になる点があれば追加写真をお送りしますのでお申し付けください。",
            "入荷通知" to "ご希望条件に合う材が入荷しました。一般公開前のご案内です。商品ページ:___。お取り置きは3日間可能です。"
        )
        for ((name, body) in templates) {
            val c = card()
            c.addView(text("【" + name + "】", 13f, ai, true))
            c.addView(text(body, 12f, sub))
            c.setOnClickListener {
                val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(ClipData.newPlainText("template", body))
                Toast.makeText(this, "「" + name + "」をコピーしました", Toast.LENGTH_SHORT).show()
            }
            root.addView(c)
        }
    }

    // ---------- 6. 経理:実質粗利計算 ----------

    private fun toolGrossProfit(root: LinearLayout) {
        root.addView(text("実質粗利 = 売価 −(原価+入庫送料+出荷送料+手数料)", 12f, sub))
        val price = numInput("売価(円)")
        val cost = numInput("仕入原価(円)")
        val shipIn = numInput("入庫送料(円)")
        val shipOut = numInput("出荷送料(円)")
        val feePct = numInput("販売・決済手数料(%)")
        for (v in listOf(price, cost, shipIn, shipOut, feePct)) root.addView(v)
        val result = text("", 16f, accent, true)
        root.addView(button("粗利を計算") {
            val p = num(price)
            val gp = p - num(cost) - num(shipIn) - num(shipOut) - p * num(feePct) / 100.0
            val rate = if (p > 0) gp / p * 100.0 else 0.0
            result.text = "実質粗利:" + gp.toInt() + " 円(粗利率 " + String.format("%.1f", rate) + "%)"
        })
        root.addView(result)
    }

    // ---------- 7. サービス調達:サブスク台帳 ----------

    private fun toolSubs(root: LinearLayout) {
        root.addView(text("契約中サービスの月額を台帳化し、固定費の合計を常に見える化。", 12f, sub))
        val name = textInput("サービス名 例: レンタル倉庫")
        val fee = numInput("月額(円)")
        root.addView(name)
        root.addView(fee)
        val listArea = LinearLayout(this)
        listArea.orientation = LinearLayout.VERTICAL
        val total = text("", 14f, accent, true)

        fun refresh() {
            listArea.removeAllViews()
            val lines = loadLines("subs")
            var sum = 0
            for ((i, line) in lines.withIndex()) {
                val p = line.split("|")
                if (p.size < 2) continue
                sum += p[1].toIntOrNull() ?: 0
                val row = text("・" + p[0] + ":" + p[1] + " 円/月", 13f, ink)
                row.setOnLongClickListener {
                    lines.removeAt(i)
                    saveLines("subs", lines)
                    refresh()
                    true
                }
                listArea.addView(row)
            }
            total.text = "固定費合計:" + sum + " 円/月(年間 " + sum * 12 + " 円)"
        }

        root.addView(button("台帳に追加") {
            if (name.text.isNotEmpty() && fee.text.isNotEmpty()) {
                val lines = loadLines("subs")
                lines.add(name.text.toString() + "|" + fee.text.toString())
                saveLines("subs", lines)
                name.setText("")
                fee.setText("")
                refresh()
            }
        })
        root.addView(total)
        root.addView(listArea)
        root.addView(text("行を長押しで削除", 11f, sub))
        refresh()
    }

    // ---------- 8. 法務:開業チェックリスト ----------

    private fun toolChecklist(root: LinearLayout) {
        root.addView(text("開業前に片付ける法務項目。チェック状態は保存されます。", 12f, sub))
        val items = listOf(
            "check_kobutsu" to "古物商許可の要否判定(中古品を扱うなら申請)",
            "check_cw" to "クリーンウッド法:合法伐採の確認・記録水準の整理",
            "check_tokusho" to "特定商取引法の表記ページ作成・レビュー",
            "check_privacy" to "個人情報の取扱規程(購入者台帳・入荷通知)",
            "check_kaigyou" to "開業届 or 法人設立手続の完了",
            "check_invoice" to "インボイス登録の要否判断・登録",
            "check_menseki" to "反り・割れに関する免責表現の適法性レビュー"
        )
        for ((key, label) in items) {
            val cb = CheckBox(this)
            cb.text = label
            cb.textSize = 13f
            cb.setTextColor(ink)
            cb.isChecked = prefs.getBoolean(key, false)
            cb.setOnCheckedChangeListener { _, checked ->
                prefs.edit().putBoolean(key, checked).apply()
            }
            root.addView(cb)
        }
        root.addView(text("※各項目の詳細は docs/DEPT_LEGAL_v1.0.md を参照", 11f, sub))
    }

    // ---------- 9. IT:自動化候補ランキング ----------

    private fun toolAutoRank(root: LinearLayout) {
        root.addView(text("繰り返し作業を記録すると年間工数を計算し、多い順に並べる。上位から自動化する。", 12f, sub))
        val name = textInput("作業名 例: 出品ページ作成")
        val perWeek = numInput("回数(回/週)")
        val minutes = numInput("所要(分/回)")
        for (v in listOf(name, perWeek, minutes)) root.addView(v)
        val listArea = LinearLayout(this)
        listArea.orientation = LinearLayout.VERTICAL

        fun refresh() {
            listArea.removeAllViews()
            val lines = loadLines("auto_items")
            val parsed = lines.mapNotNull { line ->
                val p = line.split("|")
                if (p.size < 3) null else {
                    val hours = (p[1].toDoubleOrNull() ?: 0.0) * (p[2].toDoubleOrNull() ?: 0.0) * 52.0 / 60.0
                    Pair(line, hours)
                }
            }.sortedByDescending { it.second }
            for ((line, hours) in parsed) {
                val p = line.split("|")
                val row = text("・" + p[0] + ":年間 " + String.format("%.1f", hours) + " 時間(" + p[1] + "回/週×" + p[2] + "分)", 13f, ink)
                row.setPadding(0, dp(4), 0, 0)
                row.setOnLongClickListener {
                    val all = loadLines("auto_items")
                    all.remove(line)
                    saveLines("auto_items", all)
                    refresh()
                    true
                }
                listArea.addView(row)
            }
        }

        root.addView(button("候補に追加") {
            if (name.text.isNotEmpty()) {
                val lines = loadLines("auto_items")
                lines.add(name.text.toString() + "|" + perWeek.text + "|" + minutes.text)
                saveLines("auto_items", lines)
                name.setText("")
                perWeek.setText("")
                minutes.setText("")
                refresh()
            }
        })
        root.addView(listArea)
        root.addView(text("行を長押しで削除", 11f, sub))
        refresh()
    }
}
