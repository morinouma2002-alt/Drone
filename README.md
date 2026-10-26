# Java Drone Development

## プロジェクト概要

Javaを使用して、Telloドローンを自動飛行させるシステムの開発を目指す。

最終的には、Javaによる飛行制御だけでなく、画像認識やセンサー情報を活用した自律飛行システムの実装を目標とする。

## 開発環境

- 言語：Java
- IDE：Eclipse
- ドローン：Tello
- 通信方式：UDP
- ドローンのIPアドレス：192.168.10.1
- 通信ポート：8889

---

## 第1回学習記録（2026年10月26日）

### 1. 今回の学習目標

Javaを使ってドローンと通信する仕組みを理解する。

特に、以下の内容を学習した。

- IPアドレスとポート番号
- UDP通信の基本
- DatagramSocketクラス
- DatagramPacketクラス
- データの送信と受信
- タイムアウトと例外処理

### 2. DatagramSocketとは

`DatagramSocket` は、UDP通信でデータを送受信するためのクラス。

```java
DatagramSocket socket = new DatagramSocket();
```

このコードでは、UDP通信に使用するソケットオブジェクトを生成している。

ただし、この時点ではデータの送受信は行われていない。

### 3. DatagramPacketとは

`DatagramPacket` は、UDP通信で送受信するデータを扱うクラス。

```java
DatagramPacket packet =
    new DatagramPacket(data, data.length, address, 8889);
```

各引数の意味：

| 引数 | 意味 |
|---|---|
| data | 送信するバイト配列 |
| data.length | 送信するバイト数 |
| address | 送信先のIPアドレス |
| 8889 | 送信先のポート番号 |

**重要な理解**

`new DatagramPacket(...)` はオブジェクトを生成しているだけであり、データの送信は行われない。

### 4. データの送信

```java
socket.send(packet);
```

`DatagramSocket` の `send()` メソッドを使って、パケットのデータを送信する。

- socket：通信を行うオブジェクト
- send()：送信メソッド
- packet：送信する情報を保持するオブジェクトへの参照

### 5. データの受信

```java
byte[] buffer = new byte[1024];

DatagramPacket responsePacket =
    new DatagramPacket(buffer, buffer.length);

socket.receive(responsePacket);
```

`receive()` は、UDPデータを受信するためのメソッド。

通常はデータが届くまで待機し、受信すると指定したパケットにデータが格納される。

### 6. タイムアウト処理

```java
socket.setSoTimeout(3000);
```

受信待機時間の上限を3秒に設定する。

指定時間内にデータを受信できなかった場合、`SocketTimeoutException` が発生する。

```java
try {
    socket.receive(responsePacket);
} catch (SocketTimeoutException e) {
    System.out.println("応答がありません");
}
```

通信が失敗した場合の処理は、自動飛行システムの安全性を高めるために重要である。

---

## 今回理解したこと

1. `DatagramSocket` はUDP通信の送受信を行う。
2. `DatagramPacket` は送受信するデータや宛先などの情報を保持する。
3. パケットを生成しただけではデータは送信されない。
4. `send()` を実行することで送信処理が行われる。
5. `receive()` を実行することでデータを受信する。
6. `setSoTimeout()` を使うことで受信待機時間を制限できる。
7. Javaの通信プログラムには、基本情報技術者試験で学ぶネットワークの知識が活用されている。

## 現在の開発状況

**学習済み**
- UDP通信の基本概念
- DatagramSocketとDatagramPacketの役割
- 送信・受信メソッドの仕組み
- タイムアウト処理の基本

**実装状況**
- UDPソケット生成コードを作成
- 送信用パケット生成コードを作成
- 送受信処理を追加したサンプルコードを学習

※ 実機への命令送信・応答受信は、まだ動作確認していない。

## 今後の開発予定

- [ ] TelloとJavaプログラムの通信を実機で確認
- [ ] `command` を送信して `ok` を受信
- [ ] 送受信処理をメソッド化
- [ ] Droneクラスを設計
- [ ] 自動離陸・着陸機能を実装
- [ ] 前進・後退・上昇・下降の制御
- [ ] 複数の飛行命令を順番に実行
- [ ] センサー情報の取得
- [ ] OpenCVによる画像認識
- [ ] 画像認識を利用した自律飛行

## 最終目標

Javaのオブジェクト指向プログラミングを活用し、ドローンの通信・飛行制御・画像認識を統合した自律飛行システムを開発する。
