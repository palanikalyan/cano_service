# ActiveMQ Queue Integration Guide

## 🚀 Setup ActiveMQ

### Option 1: Using Docker Compose (Recommended)
```bash
docker-compose up -d
```

### Option 2: Install ActiveMQ Locally
Download from: https://activemq.apache.org/components/classic/download/

### Option 3: Use Cloud ActiveMQ
Update `application.yml` with cloud credentials

## 📊 Access ActiveMQ Web Console

URL: http://localhost:8161
- Username: `admin`
- Password: `admin`

Navigate to "Queues" to see: `canonical.trades.queue`

## 🔄 How It Works

### Workflow:
```
File (JSON/XML/CSV)
      ↓
FileLoaderService
      ↓
MapperService
      ↓
ValidatorService
      ↓
Save to PostgreSQL (canonical_trades)
      ↓
Create Outbox Event (outbox_events)
      ↓
Publish to ActiveMQ Queue ✨
      ↓
QueueListener receives message
```

## 📝 Key Components

1. **ActiveMQConfig** - Queue configuration and connection factory
2. **QueuePublisherService** - Publishes canonical trades to ActiveMQ queue
3. **QueueListenerService** - Listens and processes messages from queue
4. **Queue Name**: `canonical.trades.queue`

## 🧪 Testing

1. Start ActiveMQ and PostgreSQL:
   ```bash
   docker-compose up -d
   ```

2. Start the application:
   ```bash
   mvn spring-boot:run
   ```

3. Process a file:
   ```bash
   curl -X POST http://localhost:8085/canonical/process/json
   ```

4. Check ActiveMQ Web Console:
   - Go to http://localhost:8161
   - Login: admin/admin
   - Click "Queues" tab
   - See `canonical.trades.queue`
   - Check "Number Of Pending Messages" and "Messages Enqueued"

5. Check application logs:
   ```
   Published trade to ActiveMQ queue: ORD123
   Received message from ActiveMQ queue: Order ID = ORD123, Fund Code = HDFC001, Amount = 5000
   ```

## 🔧 Configuration

In `application.yml`:
```yaml
spring:
  activemq:
    broker-url: tcp://localhost:61616
    user: admin
    password: admin
    packages:
      trust-all: true
  jms:
    pub-sub-domain: false
```

## 📦 Dependencies Added

- `spring-boot-starter-activemq` - ActiveMQ integration
- `activemq-broker` - Embedded ActiveMQ broker support

## 🎯 ActiveMQ Ports

- **61616**: JMS Port (application connection)
- **8161**: Web Console (management UI)

## 💡 Next Steps

The listener (`QueueListenerService`) currently logs the message. You can extend it to:
- Send to external systems
- Further processing
- Notifications
- Error handling and retry logic
- Dead Letter Queue (DLQ) handling
