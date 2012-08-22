数式展開プログラム Java版


[ディレクトリ/ファイルの構成]

CellMLonGPU			Eclipseのプロジェクト
+-- .classpath			Eclipseが使用するファイル
+-- .project			Eclipseが使用するファイル
+-- .settings			Eclipseが使用するディレクトリ
+-- bin				Javaクラスファイル格納ディレクトリ
+-- gen_all.bat			デバッグ用実行ファイル
+-- lib				Java jarファイル格納ディレクトリ
+-- model			デバッグ用データファイル格納ディレクトリ
+-- out_common_gen		デバッグ用数式展開結果格納ディレクトリ(共通コード)
+-- out_cuda_gen		デバッグ用数式展開結果格納ディレクトリ(cudaコード)
+-- out_java_bigdecimal_gen	デバッグ用数式展開結果格納ディレクトリ(java bigdecimalコード)
+-- out_java_gen		デバッグ用数式展開結果格納ディレクトリ(javaコード)
+-- out_simple_gen		デバッグ用数式展開結果格納ディレクトリ(シンプルコード)
+-- parser.bat			コマンド版実行コマンドファイル
+-- parserGUI.bat		GUI版実行サブコマンドファイル
+-- readme.txt			このファイル
+-- src				Javaソースファイル格納ディレクトリ
+-- startGUI.bat		GUI版実行コマンドファイル


[実行方法]

●GUI版

startGUI.bat をダブルクリックして下さい。
GUIウィンドウが表示されます。
RelMLファイル等を設定し、実行ボタンを押すと数式展開します。


●コマンド版

parser.bat が実行コマンドです。
引数なしで起動すると、使用方法を出力します。

コマンドの説明
  parser [ -g name ] RelML [output-option]

  -g: 生成コード指定オプション
      指定しなければ、cuda コードを生成します。

  name:
    cuda	cuda コード
    common	共通コード
    simple	シンプルコード
    java	javaコード
    java_bigdecimal	java bigdecimalコード

  RelML: RelMLファイルを指定します。

  output-option: 出力先を指定するオプション
    下記の順に指定します。
    出力ディレクトリ [変数関係ファイル [初期化式ファイル [プログラムファイル]]]

    すべて指定する必要はありません。
    指定がない場合には下記の指定とみなします。
      出力ディレクトリ: コマンドがあるディレクトリ
      変数関係ファイル: relation.txt
      初期化式ファイル: initialize.txt
      プログラムファイル: 標準出力

    生成コード指定オプションが simple の場合、
    プログラムファイル指定は無視されます。


以上

