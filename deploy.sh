#!/data/data/com.termux/files/usr/bin/bash
# Appathy標準 deploy.sh (LumberHQApp)
# 冪等設計:リポジトリ作成(422は続行) → git init(済みならスキップ) → remote再設定 → add/commit/push
set -e

# ホームディレクトリでのgit init事故防止:必ず自フォルダへ移動
cd "$(dirname "$0")"

REPO="LumberHQApp"
GHUSER="Sekiguchi-Takashi"
MSG="${1:-update}"

TOKEN="$(git config --global github.token)"
if [ -z "$TOKEN" ]; then
    printf '%s\n' "ERROR: git config --global github.token が未設定です" >&2
    exit 1
fi

# リポジトリ作成(既存なら422が返るが続行)
curl -s -o /dev/null -X POST \
    -H "Authorization: token ${TOKEN}" \
    -H "Accept: application/vnd.github+json" \
    https://api.github.com/user/repos \
    -d "{\"name\":\"${REPO}\",\"private\":false}" || true

# git init(初期化済みならスキップ)
if [ ! -d .git ]; then
    git init -b main
fi

# remote再設定
git remote remove origin 2>/dev/null || true
git remote add origin "https://${TOKEN}@github.com/${GHUSER}/${REPO}.git"

git add -A
git commit -m "$MSG" || true
git push -u origin main

printf '%s\n' "DONE: https://github.com/${GHUSER}/${REPO}"
