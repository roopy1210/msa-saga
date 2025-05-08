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

---

## ✅ Kafka의 `epoch`과 `transaction` 관련 설명

---

#### 1. `epoch`이 증가하는 조건

| 조건                        | 설명                                                         |
|-----------------------------|--------------------------------------------------------------|
| **서버 재기동**               | 애플리케이션 서버가 재시작되면 새로운 Kafka 프로듀서 인스턴스가 생성되며 `epoch` 값이 증가합니다. |
| **트랜잭션 세션 종료 후 새로 시작** | 기존 트랜잭션 세션이 종료되면 새로운 트랜잭션 세션이 시작되며 `epoch` 값이 증가합니다. |

#### 2. `epoch`과 `transaction` 예시 흐름

| 단계                            | 설명                                                         |
|---------------------------------|--------------------------------------------------------------|
| **서버 구동 시**                  | Kafka 프로듀서가 초기화되고, 첫 번째 `epoch` 값이 할당됩니다. (`epoch=0`) |
| **첫 번째 트랜잭션 수행**         | 트랜잭션이 진행되며, `epoch` 값은 변경되지 않고 계속 유지됩니다. |
| **서버 재기동**                  | 서버가 재기동되면 새로운 Kafka 프로듀서 인스턴스가 생성되며 `epoch` 값이 증가합니다. (`epoch=1`) |
| **두 번째 트랜잭션 수행**         | 새로운 트랜잭션이 시작되면, `epoch` 값은 다시 유지됩니다. |
| **서버 재기동**                  | 다시 서버가 재기동되면 `epoch` 값이 다시 증가합니다. (`epoch=2`) |

#### 3. `epoch`과 `transaction` Spring Boot 기준

- **`epoch`**: 서버가 재기동되거나 새로운 트랜잭션 세션이 시작될 때 증가합니다. 하나의 애플리케이션 구동 동안 동일한 `epoch` 값이 유지되며, 재기동 시 `epoch`이 증가하여 새로운 트랜잭션 세션이 시작된다는 것을 의미합니다.
  
- **`transaction`**: `transaction`은 해당 애플리케이션 구동 중에 시작되는 트랜잭션 세션을 나타냅니다. 서버가 재기동될 때마다 새로운 트랜잭션이 시작되고, 그에 맞춰 `epoch`도 증가합니다.

따라서, Spring Boot 애플리케이션에서 **서버가 재기동되면 `epoch` 값이 증가**하며, 이는 새로운 트랜잭션 세션이 시작되었음을 의미합니다.