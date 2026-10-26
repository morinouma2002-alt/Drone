# Java Drone Development

## プロジェクト概要

Javaを使用して、Telloドローンの自動飛行システムを開発するプロジェクト。

最終的には、Javaのオブジェクト指向プログラミングを活用し、ドローンの飛行制御、センサー情報の取得、画像認識を組み合わせた自律飛行システムの実現を目指す。

## 開発環境

- プログラミング言語：Java
- 開発環境：Eclipse
- 使用ドローン：Tello
- 通信方式：UDP
- IPアドレス：192.168.10.1
- UDPポート番号：8889

---

# 第1回 学習記録（2026年10月26日）

## 1. 今回の学習内容

今回の学習では、Javaを使ってドローンと通信するための基礎知識を学んだ。

主な学習内容は以下のとおり。

1. UDP通信の仕組み
2. DatagramSocketとDatagramPacketの違い
3. データの送信と受信
4. 受信タイムアウトの設定
5. SocketTimeoutExceptionの仕組み
6. Java標準ライブラリと自作クラスの違い
7. try-catchによる例外処理
8. throwとthrowsの違い
9. 例外クラスの継承関係

## 2. UDP通信の基本

UDPは、ネットワーク上でデータを送受信するための通信方式の一つ。

TCPとは異なり、データの到達や順序を保証しない。

今回使用するTelloドローンでは、UDP通信によって飛行命令を送信する。

```java
InetAddress address =
    InetAddress.getByName("192.168.10.1");
```

`InetAddress` はIPアドレスを扱うためのクラス。

`192.168.10.1` はTelloドローンのIPアドレスである。

## 3. DatagramSocketとDatagramPacket

### DatagramSocket

```java
DatagramSocket socket = new DatagramSocket();
```

UDP通信でデータを送受信するためのソケットを生成する。

### DatagramPacket

```java
DatagramPacket packet =
    new DatagramPacket(
        data, data.length, address, 8889
    );
```

送信するデータ、データ長、送信先IPアドレス、ポート番号をまとめたオブジェクトを生成する。

**学習した重要なポイント**

`DatagramPacket` を生成しただけでは、データは送信されない。

実際にデータを送信するには、次のメソッドを実行する。

```java
socket.send(packet);
```

## 4. データの受信

ドローンからの応答を受信するには、`receive()` メソッドを使用する。

```java
byte[] buffer = new byte[1024];

DatagramPacket responsePacket =
    new DatagramPacket(buffer, buffer.length);

socket.receive(responsePacket);
```

`receive()` は、通常、データを受信するまで待機する。

受信したデータは、指定した `DatagramPacket` に格納される。

## 5. タイムアウト処理

```java
socket.setSoTimeout(3000);
```

受信待機時間の上限を3000ミリ秒（3秒）に設定する。

指定時間内にデータを受信できない場合、`SocketTimeoutException` が発生する。

```java
try {
    socket.receive(responsePacket);
} catch (SocketTimeoutException e) {
    System.out.println("応答がありません");
}
```

ドローンの自動飛行では、通信エラーが発生した場合に次の飛行命令を停止するなど、安全性を考慮した処理が必要になる。

---

# 6. 今回発生した問題：SocketTimeoutException

当初、次のような例外クラスを自作した。

```java
package Exception;

public class SocketTimeoutException extends Exception {

    public SocketTimeoutException(String message) {
        super(message);
    }
}
```

そして、ドローンのプログラムで以下をimportした。

```java
import Exception.SocketTimeoutException;
```

しかし、Javaには標準で `SocketTimeoutException` が用意されている。

正しいimport文は次のとおり。

```java
import java.net.SocketTimeoutException;
```

### なぜ自作クラスでは問題になるのか？

自作の `Exception.SocketTimeoutException` と標準の `java.net.SocketTimeoutException` は、名前が同じでも別のクラスだからである。

また、両方が `Exception` を継承していても、互いに親子関係があるわけではない。

そのため、自作の例外クラスを指定したcatchでは、標準ライブラリが発生させるタイムアウト例外を捕まえられない。

今回のコードでは、自作のチェック例外がtryブロックから投げられないため、catch部分がコンパイルエラーになる。

---

# 7. Javaの例外処理と継承関係

Java標準の例外クラスには、次のような継承関係がある。

```text
Exception
   └── IOException
         └── SocketException
               └── SocketTimeoutException
```

### 親クラスのcatchは子クラスの例外を捕まえられる

```java
try {
    throw new SocketTimeoutException();
} catch (IOException e) {
    System.out.println("A");
}
```

出力結果：

```text
A
```

`SocketTimeoutException` は `IOException` の子孫クラスなので、`IOException` で捕まえられる。

### 子クラスのcatchでは親クラスの例外を捕まえられない

```java
try {
    throw new IOException();
} catch (SocketTimeoutException e) {
    System.out.println("A");
}
```

このコードはコンパイルエラーになる。

理由は、`IOException` がチェック例外であり、適切にcatchされず、throws宣言もないためである。

**重要なルール**

catchで捕まえられるのは、指定した例外クラス自身と、その子孫クラスの例外である。

---

# 8. throwとthrowsの違い

今回、特に理解が深まった内容。

### throw

例外を実際に発生させる。

```java
throw new IOException();
```

### throws

メソッドから例外が呼び出し元へ伝播する可能性を宣言する。

```java
public static void main(String[] args)
        throws IOException {

    throw new IOException();
}
```

### catchできなくてもthrowsで伝播できる

```java
public static void main(String[] args)
        throws IOException {

    try {
        throw new IOException();
    } catch (SocketTimeoutException e) {
        System.out.println("A");
    }
}
```

このコードはコンパイルできる。

処理の流れ：

1. `IOException` が発生する。
2. `SocketTimeoutException` では捕まえられない。
3. `throws IOException` により、例外が呼び出し元へ伝播する。
4. `main()` から例外が伝播し、プログラムは異常終了する。

**今回理解した重要事項**

- `throw`：例外を発生させる。
- `catch`：例外を捕まえて処理する。
- `throws`：例外が呼び出し元へ伝播することを宣言する。
- catchで捕まえられないことが、必ずしもコンパイルエラーになるとは限らない。

---

# 9. 今回のプログラムの進捗

### 実装・学習した内容

- [x] TelloのIPアドレスを指定
- [x] UDPソケットを生成
- [x] 送信用DatagramPacketを生成
- [x] send()による命令送信処理を記述
- [x] receive()による応答受信処理を記述
- [x] 受信タイムアウトを設定
- [x] 例外処理の問題点を特定
- [x] 正しいSocketTimeoutExceptionのimport方法を理解

### 未確認・未実装の内容

- [ ] 修正後のプログラムのコンパイル確認
- [ ] Telloとの実機通信
- [ ] `command` 送信後の `ok` 受信確認
- [ ] 通信エラー時の処理改善
- [ ] 飛行制御メソッドの実装
- [ ] 自動飛行の実装

---

# 10. 今後の開発計画

## 第2段階：通信処理の完成

- TelloとJavaのUDP通信を実機で確認
- ドローンからの応答を取得
- 通信エラーを適切に処理
- `sendCommand()` メソッドを実装

## 第3段階：オブジェクト指向による設計

- Droneクラスの作成
- takeoff()メソッド
- land()メソッド
- moveForward()メソッド
- moveUp()メソッド
- カプセル化と例外処理の実装

## 第4段階：自動飛行

- 離陸から着陸までの命令を自動実行
- 飛行ルートの設定
- 命令の実行結果を確認
- 通信異常時の安全対策

## 第5段階：自律飛行への発展

- OpenCVを使った画像認識
- カメラ映像の取得
- 目標物の認識
- センサー情報による飛行制御
- 自律的な移動判断

---

## 今回の振り返り

今回の学習では、Javaのネットワーク通信と例外処理について理解を深めた。

特に、DatagramSocketとDatagramPacketの役割の違い、例外クラスの継承関係、catchとthrowsの違いを理解できた。

Java Silverの学習で得た知識が、実際のドローン開発でも活用されていることを確認できた。

次回は、実機とのUDP通信を確認し、通信処理をメソッド化することを目標とする。
