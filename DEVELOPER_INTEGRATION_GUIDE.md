# 🚀 Notification Microservice - Developer Integration Guide

## Overview
Add SMS notifications to your app in **5 minutes** with just **3 API calls**. No SMS complexity, no PhilSMS integration needed.

---

## ⚡ Quick Start

### 1. Start the Notification Service
```bash
java -jar notification-microservice.jar
# Service runs on http://localhost:8081
```

### 2. Use These 3 API Endpoints
```
POST /api/users                    # Register user for SMS
GET  /api/preferences/{email}      # Get notification settings  
POST /api/preferences/{email}      # Update notification settings
```

---

## 💻 Integration Code

### JavaScript/React (12 lines)
```javascript
const API_URL = 'http://localhost:8081/api';

// 1. Register user for SMS notifications
await fetch(`${API_URL}/users`, {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    firstName: "John", lastName: "Doe", email: "john@example.com",
    password: "password123", phoneNumber: "09123456789"
  })
});

// 2. Get user's notification preferences
const preferences = await fetch(`${API_URL}/preferences/john@example.com`)
  .then(response => response.json());

// 3. Update notification preferences
await fetch(`${API_URL}/preferences/john@example.com`, {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    alertTypes: [
      {alertId: 1, enabled: true},   // Weather Alert
      {alertId: 2, enabled: true},   // Emergency Alert
      {alertId: 3, enabled: false}   // Traffic Alert
    ]
  })
});
```

### Python (8 lines)
```python
import requests

API_URL = "http://localhost:8081/api"

# 1. Register user
requests.post(f"{API_URL}/users", json={
    "firstName": "Jane", "lastName": "Smith", "email": "jane@example.com",
    "password": "password123", "phoneNumber": "09987654321"
})

# 2. Get preferences
preferences = requests.get(f"{API_URL}/preferences/jane@example.com").json()

# 3. Update preferences
requests.post(f"{API_URL}/preferences/jane@example.com", json={
    "alertTypes": [{"alertId": 1, "enabled": True}, {"alertId": 2, "enabled": False}]
})
```

### Java/Spring Boot (15 lines)
```java
@Service
public class NotificationClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String API_URL = "http://localhost:8081/api";
    
    // 1. Register user
    public void registerUser(UserDTO user) {
        restTemplate.postForObject(API_URL + "/users", user, Void.class);
    }
    
    // 2. Get preferences
    public Map<String, Object> getPreferences(String email) {
        return restTemplate.getForObject(API_URL + "/preferences/" + email, Map.class);
    }
    
    // 3. Update preferences
    public void updatePreferences(String email, PreferencesDTO preferences) {
        restTemplate.postForObject(API_URL + "/preferences/" + email, preferences, Void.class);
    }
}
```

### Node.js (10 lines)
```javascript
const axios = require('axios');
const API_URL = 'http://localhost:8081/api';

// 1. Register user
await axios.post(`${API_URL}/users`, {
  firstName: "Bob", lastName: "Wilson", email: "bob@example.com",
  password: "password123", phoneNumber: "09555123456"
});

// 2. Get preferences
const preferences = (await axios.get(`${API_URL}/preferences/bob@example.com`)).data;

// 3. Update preferences
await axios.post(`${API_URL}/preferences/bob@example.com`, {
  alertTypes: [{alertId: 1, enabled: true}, {alertId: 2, enabled: false}]
});
```

---

## 📋 Available Alert Types

| ID | Alert Type | Description |
|----|------------|-------------|
| 1 | Weather Alert | Severe weather conditions and warnings |
| 2 | Emergency Alert | Critical emergencies and evacuations |
| 3 | Traffic Alert | Road closures and traffic updates |
| 4 | Health Alert | Public health notifications |
| 5 | School Alert | School closures and announcements |
| 6 | Community Alert | Local community events and updates |

---

## 🔧 Data Formats

### User Registration Request
```json
{
  "firstName": "John",
  "lastName": "Doe", 
  "email": "john@example.com",
  "password": "password123",
  "phoneNumber": "09123456789"
}
```

### Get Preferences Response
```json
{
  "userId": 1,
  "email": "john@example.com",
  "alertTypes": [
    {
      "alertId": 1,
      "alertName": "Weather Alert",
      "alertDescription": "Severe weather conditions and warnings",
      "enabled": true
    },
    {
      "alertId": 2,
      "alertName": "Emergency Alert", 
      "alertDescription": "Critical emergencies and evacuations",
      "enabled": false
    }
  ]
}
```

### Update Preferences Request
```json
{
  "alertTypes": [
    {"alertId": 1, "enabled": true},
    {"alertId": 2, "enabled": false},
    {"alertId": 3, "enabled": true}
  ]
}
```

---

## ✅ What You Get

### Instant SMS Functionality
- ✅ **User registration** for SMS notifications
- ✅ **6 alert types** with descriptions
- ✅ **Preference management** (enable/disable alerts)
- ✅ **Automatic SMS delivery** via PhilSMS
- ✅ **Database persistence** of users and preferences

### Zero SMS Complexity
- ❌ No PhilSMS API integration needed
- ❌ No SMS formatting or delivery logic
- ❌ No webhook handling or error management
- ❌ No SMS provider documentation to read

---

## 🚀 Complete Example: School Alert System

```javascript
// Complete working example in JavaScript
class SchoolAlertSystem {
  constructor() {
    this.api = 'http://localhost:8081/api';
  }

  // Parent signs up for school alerts
  async registerParent(parentData) {
    await fetch(`${this.api}/users`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(parentData)
    });
  }

  // Load parent's notification settings
  async loadSettings(email) {
    const response = await fetch(`${this.api}/preferences/${email}`);
    return response.json();
  }

  // Save parent's notification preferences
  async saveSettings(email, preferences) {
    await fetch(`${this.api}/preferences/${email}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(preferences)
    });
  }
}

// Usage
const schoolSystem = new SchoolAlertSystem();

// Register parent
await schoolSystem.registerParent({
  firstName: "Maria", lastName: "Garcia", email: "maria@example.com",
  password: "securepass", phoneNumber: "09123456789"
});

// Enable emergency and school alerts only
await schoolSystem.saveSettings("maria@example.com", {
  alertTypes: [
    {alertId: 2, enabled: true},  // Emergency Alert
    {alertId: 5, enabled: true},  // School Alert  
    {alertId: 1, enabled: false}, // Weather Alert (disabled)
    {alertId: 3, enabled: false}, // Traffic Alert (disabled)
    {alertId: 4, enabled: false}, // Health Alert (disabled)
    {alertId: 6, enabled: false}  // Community Alert (disabled)
  ]
});

// Now Maria will receive SMS for school emergencies and announcements!
```

---

## 🔍 Testing Your Integration

### 1. Test User Registration
```bash
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "User", 
    "email": "test@example.com",
    "password": "password",
    "phoneNumber": "09123456789"
  }'
```

### 2. Test Get Preferences
```bash
curl http://localhost:8081/api/preferences/test@example.com
```

### 3. Test Update Preferences
```bash
curl -X POST http://localhost:8081/api/preferences/test@example.com \
  -H "Content-Type: application/json" \
  -d '{
    "alertTypes": [
      {"alertId": 1, "enabled": true},
      {"alertId": 2, "enabled": true}
    ]
  }'
```

---

## ⚙️ Environment Setup

### Required Environment Variables
```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/notification_db
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=yourpassword

# PhilSMS Configuration (for SMS sending)
PHILSMS_API_TOKEN=your_philsms_token
PHILSMS_SENDER_ID=YourSenderID
```

### Dependencies (for Java projects)
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

---

## 🎯 Integration Checklist

### Before You Start
- [ ] Notification microservice is running on port 8081
- [ ] Database is configured and accessible
- [ ] PhilSMS credentials are set up

### During Integration  
- [ ] Test user registration endpoint
- [ ] Test preferences retrieval
- [ ] Test preferences update
- [ ] Verify SMS delivery (optional)

### After Integration
- [ ] Add error handling for API calls
- [ ] Implement user authentication in your app
- [ ] Create UI for notification settings
- [ ] Monitor API usage and performance

---

## 📞 Need Help?

### Common Issues
- **Service not responding**: Check if running on port 8081
- **Database errors**: Verify MySQL connection and credentials
- **SMS not sending**: Check PhilSMS API token and sender ID

### Quick Debug
```bash
# Check if service is running
curl http://localhost:8081/api/preferences/test@example.com

# Expected response: JSON with alert types
```

---

## 🎉 That's It!

With just **8-15 lines of code**, you now have:
- ✅ Enterprise-grade SMS notifications
- ✅ User preference management
- ✅ 6 different alert types
- ✅ Automatic message delivery
- ✅ Production-ready reliability

**Focus on your app's core features** - we handle all the SMS complexity for you!
