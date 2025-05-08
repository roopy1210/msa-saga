# Kafka Local Setup with Homebrew (macOS)

이 문서는 macOS 환경에서 Homebrew를 사용하여 Apache Kafka와 Zookeeper를 설치하고, Kafka 토픽을 생성하고 메시지를 테스트하는 방법을 안내합니다.

---

## ✅ 환경 준비

- macOS
- Homebrew 설치: https://brew.sh/

---

#### 1. Zookeeper 설치 및 실행

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

#### 2. Kafka 설치 및 실행

**설치**

```bash
brew install kafka
```

**백그라운드 수행**
```bash
brew services start kafka
```

#### 3. Kafka 정상 동작 확인

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

---

## ✅ Kafka 트랜잭션 프로듀서 초기화 및 커밋

- 실제 수행 로그 결과 로그 설명

---

#### 1. KafkaMetricsCollector 초기화

```bash
initializing Kafka metrics collector
```

#### 2. 트랜잭셔널 프로듀서 인스턴스 생성

```bash
Instantiated a transactional producer.
```

#### 3. Kafka 버전 정보

```bash
Kafka version: 3.8.1
```

#### 4. 트랜잭션 매니저 상태 변경

```bash
Transition from state UNINITIALIZED to INITIALIZING
```

#### 5. InitProducerId 요청 전송

```bash
InitProducerIdRequestData(transactionalId='card-tx-0', ...)
```

#### 6. FindCoordinator 요청 → 트랜잭션 coordinator 찾기

```bash
Discovered transaction coordinator localhost:9092
```

#### 7. InitProducerId 응답 → ProducerId 와 Epoch 설정

```bash
ProducerId set to 1002 with epoch 3
```

#### 8. 트랜잭션 시작

```bash
Transition from state READY to IN_TRANSACTION
```

#### 9. 트랜잭션에 파티션 추가 요청

```bash
Begin adding new partition test-topic-0 to transaction
```

#### 10. EndTxn (커밋 요청) → 트랜잭션 커밋

```bash
Enqueuing transactional request EndTxnRequestData(..., committed=true)
```

#### 11. 성공적으로 배치 전송 및 파티션에 producerId/epoch 지정

```bash
Successfully added partitions [test-topic-0] to transaction
Assigned producerId 1002 and producerEpoch 3 to batch ...
```