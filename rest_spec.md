#  Command

- Contents
  - [Information](#Information)
  - [Description](#Description)
  - [Flow List](#Flow-List)
  - [Command List](#Command-List)
  - [Configuration List](#Configuration-List)
  - [Message List](#Message-List)


## Information
REST-API関連のコマンドを提供します。

- Name : `rest`
- Custom Package : `com.epion_t3.rest`

## Description
REST-APIのテストをするために必要な機能を提供します。

## Flow List

## Command List

|Name|Summary|Assert|Evidence|
|:---|:---|:---|:---|
|[StoreJsonElement](#StoreJsonElement)|レスポンスのボディを解析して値を抽出し、Variableへ登録するための機能です。REST-APIを連続して実行するようなシナリオにおいて、レスポンスの値を引き継ぐ必要がある場合に利用します。  |||
|[ExecuteRestApi](#ExecuteRestApi)|REST-APIを呼び出して結果を取得するためのコマンド。  ||X|
|[AssertHttpStatus](#AssertHttpStatus)|REST-APIの実行結果のHTTPステータスコードを確認します。  |X||
|[AssertHttpHeader](#AssertHttpHeader)|REST-APIの実行結果のHTTPヘッダを確認します。  |X||
|[AssertResponseBodyJson](#AssertResponseBodyJson)|REST-APIの実行結果のBodyを確認します。  |X||

------

### StoreJsonElement
レスポンスのボディを解析して値を抽出し、Variableへ登録するための機能です。REST-APIを連続して実行するようなシナリオにおいて、レスポンスの値を引き継ぐ必要がある場合に利用します。
#### Command Type
- Assert : No
- Evidence : No

#### Functions
- REST-APIの実行結果のレスポンスボディ（JSONに限る）から値を[JSONPath](https://github.com/json-path/JsonPath)で抽出して変数に登録する。

#### Structure
```yaml
commands : 
  id : コマンドのID
  command : 「StoreJsonElement」固定
  summary : コマンドの概要（任意）
  description : コマンドの詳細（任意）
  target : ExecuteRestApiを実行したFlowID # (1)
  value : 登録する変数名 # (2)
  jsonPath : 抽出するJSONPath # (3)

```

1. ExecuteRestApiを実行したFlowIDを指定します。
1. 登録する変数名は、スコープ.名称で指定します。（例：scenario.hoge）
1. [JSONPath](https://github.com/json-path/JsonPath)にて指定を行います。
------

### ExecuteRestApi
REST-APIを呼び出して結果を取得するためのコマンド。
#### Command Type
- Assert : No
- Evidence : __Yes__

#### Functions
- REST-APIを呼び出すことが可能です。
- レスポンスをエビデンスとして保持します。

#### Structure
```yaml
commands : 
  id : コマンドのID
  command : 「ExecuteRestApi」固定
  summary : コマンドの概要（任意）
  description : コマンドの詳細（任意）
  connectTimeout : 接続タイムアウトをミリ秒で指定（任意） # (1)
  readTimeout : 読込タイムアウトをミリ秒で指定（任意） # (2)
  bodyEncoding : リクエストボディのエンコーディング指定（任意） # (3)
  request : リクエスト詳細指定
    method : HTTPのメソッドを指定（get、post、put、deleteのいずれかに対応）
    headers : HTTPヘッダを表す連想配列にて必要なものを指定
    queryParameters : HTTPのクエリーパラメータを指定する
    targetUrl : リクエスト対象URLを指定
    body : リクエストBodyを指定

```

1. REST通信の接続タイムアウトを発生させる時間をミリ秒で指定します。デフォルト値は3000ms（3s）となっています。
1. REST通信の読込タイムアウトを発生させる時間をミリ秒で指定します。デフォルト値は3000ms（3s）となっています。
1. リクエストボディのエンコーディング指定を行えます。デフォルト値は存在しないため、Javaの起動時のエンコーディングが採用されます。（通常UTF-8）また、このオプションはPOST、PUT、PATCHのみに適用されます。
------

### AssertHttpStatus
REST-APIの実行結果のHTTPステータスコードを確認します。
#### Command Type
- Assert : __Yes__
- Evidence : No

#### Functions
- REST-APIの実行結果のHTTPステータスコードを確認します。

#### Structure
```yaml
commands : 
  id : コマンドのID
  command : 「AssertHttpStatus」固定
  summary : コマンドの概要（任意）
  description : コマンドの詳細（任意）
  target : ExecuteRestApiを実行したFlowID # (1)
  value : 期待値 # (2)

```

1. ExecuteRestApiを実行したFlowIDを指定します。
1. 期待値としてのHTTPステータスコードを数値で設定します。
------

### AssertHttpHeader
REST-APIの実行結果のHTTPヘッダを確認します。
#### Command Type
- Assert : __Yes__
- Evidence : No

#### Functions
- REST-APIの実行結果のHTTPヘッダを確認します。

#### Structure
```yaml
commands : 
  id : コマンドのID
  command : 「AssertHttpHeader」固定
  summary : コマンドの概要（任意）
  description : コマンドの詳細（任意）
  target : ExecuteRestApiを実行したFlowID # (1)
  value : 期待値 # (2)
  header : HTTPヘッダ名 # (3)

```

1. ExecuteRestApiを実行したFlowIDを指定します。
1. 期待値としてのHTTPヘッダの値を設定します。設定値が複数項目の場合、セミコロン区切りで指定してください。
1. 確認対象のHTTPヘッダ名を指定します。
------

### AssertResponseBodyJson
REST-APIの実行結果のBodyを確認します。
#### Command Type
- Assert : __Yes__
- Evidence : No

#### Functions
- REST-APIの実行結果のBodyを確認します。
- 期待値のJSONファイルとの比較を行います。

#### Structure
```yaml
commands : 
  id : コマンドのID
  command : 「AssertResponseBodyJson」固定
  summary : コマンドの概要（任意）
  description : コマンドの詳細（任意）
  target : ExecuteRestApiを実行したFlowID # (1)
  value : 期待値 # (2)
  ignores : 確認不要リスト

```

1. ExecuteRestApiを実行したFlowIDを指定します。
1. 期待値としてのHTTPステータスコードを数値で設定します。
1. 確認を行わなくともよい要素、もしくはおこなことができない要素について指定します。

## Configuration List

## Message List

 Command output messages.

|MessageID|MessageContents|
|:---|:---|
|com.epion_t3.rest.info.1001|HTTPステータスコードは一致します.|
|com.epion_t3.rest.info.1002|HTTPヘッダは一致します.|
|com.epion_t3.rest.err.9009|JSONパスが不正です.JSONPath：{0}|
|com.epion_t3.rest.err.9008|エビデンスのREST-APIの実行結果のレスポンスにはBodyが存在しません.FlowID：{0}|
|com.epion_t3.rest.err.9019|期待値のJSONファイルが見つかりません.パス：{0}|
|com.epion_t3.rest.err.9007|エビデンスはREST-APIの実行結果のレスポンスではありません.FlowID：{0}|
|com.epion_t3.rest.err.9018|指定されたエンコーディングはサポート対象外です.エンコーディング：{0}|
|com.epion_t3.rest.err.9006|エビデンスが参照できません.FlowID：{0}|
|com.epion_t3.rest.err.9017|HTTPヘッダは一致しません.|
|com.epion_t3.rest.err.9005|値（value）は必須です.|
|com.epion_t3.rest.err.9016|HTTPヘッダ名は必須です.|
|com.epion_t3.rest.err.9004|対象（target）は必須です.|
|com.epion_t3.rest.err.9015|読込タイムアウトは数値(ms)で指定してください.読込タイムアウト：{0}|
|com.epion_t3.rest.err.9003|HTTPステータスコードは一致しません.|
|com.epion_t3.rest.err.9014|読込タイムアウトは必須です.|
|com.epion_t3.rest.err.9002|HTTPステータスコードは数値で指定してください.ステータスコード:{0}|
|com.epion_t3.rest.err.9013|接続タイムアウトは数値(ms)で指定してください.接続タイムアウト：{0}|
|com.epion_t3.rest.err.9001|不明なメソッドです.メソッド:{0}|
|com.epion_t3.rest.err.9012|接続タイムアウトは必須です.|
|com.epion_t3.rest.err.9011|ストアは単一プロパティのみ可能です.ArrayやObjectは不可能です.JSONPath：{0}|
|com.epion_t3.rest.err.9010|JSONパスは必須です.|
