# Kafka Local Setup with Homebrew (macOS)

이 문서는 macOS 환경에서 Homebrew를 사용하여 Apache Kafka와 Zookeeper를 설치하고, Kafka 토픽을 생성하고 메시지를 테스트하는 방법을 안내합니다.

---

## ✅ 환경 준비

- macOS
- Homebrew 설치: https://brew.sh/

---

## 📦 1. Zookeeper 설치 및 실행

**설치**

```bash
brew install zookeeper
```

**설정 파일 생성 (처음 한 번만)**
```bash
brew services start zookeeper
```

**또는 백그라운드 말고 직접 실행하고 싶다면:**
```bash
zkServer start
```

**정상 실행 확인:**
```bash
zkServer status
```

## 📦 2. Kafka 설치 및 실행

**설치**

```bash
brew install kafka
```

**백그라운드 수행**
```bash
brew services start kafka
```

## 📦 3. Kafka 정상 동작 확인

**토픽 생성**

```bash
kafka-topics --create \
  --bootstrap-server localhost:9092 \
  --replication-factor 1 \
  --partitions 1 \
  --topic test-topic
```

**토픽 목록 조회**
```bash
kafka-topics --list --bootstrap-server localhost:9092
```

**메세지 보내기(Producer)**
```bash
kafka-console-producer --broker-list localhost:9092 --topic test-topic
```

**메세지 받기(Consumer)**
```bash
kafka-console-consumer --bootstrap-server localhost:9092 \
  --topic test-topic --from-beginning
```