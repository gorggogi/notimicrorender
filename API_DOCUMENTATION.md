# Notification Microservice API Documentation

## 🚀 Overview

The Notification Microservice provides REST APIs for managing users, alert types, user preferences, and sending notifications. All API endpoints are **open access** - no authentication required for integration.

**Base URL:** `http://localhost:8081`

---

## 📋 API Endpoints

### 1. Alert Types API

#### Get All Alert Types
Retrieve all available alert types in the system.

**Endpoint:** `GET /api/v1/alert-types`

**Response:**
```json
[
  {
    "alertId": 1,
    "alertName": "Weather Alert",
    "alertDescription": "Receive notifications about severe weather conditions, storms, and weather warnings in your area."
  },
  {
    "alertId": 2,
    "alertName": "Emergency Alert", 
    "alertDescription": "Critical emergency notifications including natural disasters, public safety warnings, and evacuation notices."
  },
  {
    "alertId": 3,
    "alertName": "Traffic Alert",
    "alertDescription": "Traffic updates, road closures, accidents, and transportation disruptions in your area."
  }
]
```

**Example Usage:**
```bash
curl -X GET http://localhost:8081/api/v1/alert-types
```

---

### 2. User Management API

#### Register User
Register a new user in the notification system.

**Endpoint:** `POST /api/users`

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe", 
  "email": "john.doe@example.com",
  "userPhoneNumber": "+1234567890"
}
```

**Response:** `200 OK` (No response body)

**Example Usage:**
```bash
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com", 
    "userPhoneNumber": "+1234567890"
  }'
```

---

### 3. User Preferences API

#### Get User Preferences
Retrieve all alert type preferences for a specific user.

**Endpoint:** `GET /api/preferences/{email}`

**Path Parameters:**
- `email` - User's email address

**Response:**
```json
{
  "userId": 1,
  "email": "john.doe@example.com",
  "alertTypes": [
    {
      "alertId": 1,
      "alertName": "Weather Alert",
      "alertDescription": "Weather notifications...",
      "enabled": true
    },
    {
      "alertId": 2, 
      "alertName": "Emergency Alert",
      "alertDescription": "Emergency notifications...",
      "enabled": false
    }
  ]
}
```

**Example Usage:**
```bash
curl -X GET http://localhost:8081/api/preferences/john.doe@example.com
```

#### Update User Preferences
Update alert type preferences for a specific user.

**Endpoint:** `POST /api/preferences/{email}`

**Path Parameters:**
- `email` - User's email address

**Request Body:**
```json
{
  "alertTypes": [
    {
      "alertId": 1,
      "enabled": true
    },
    {
      "alertId": 2,
      "enabled": false
    },
    {
      "alertId": 3,
      "enabled": true
    }
  ]
}
```

**Response:**
```json
{
  "message": "Preferences updated successfully"
}
```

**Example Usage:**
```bash
curl -X POST http://localhost:8081/api/preferences/john.doe@example.com \
  -H "Content-Type: application/json" \
  -d '{
    "alertTypes": [
      {"alertId": 1, "enabled": true},
      {"alertId": 2, "enabled": false}
    ]
  }'
```

---

### 4. Notification Management API

#### Get All User Preferences (by User ID)
Retrieve all alert type preferences for a user by their ID.

**Endpoint:** `GET /api/v1/users/{userId}/preferences`

**Path Parameters:**
- `userId` - User's numeric ID

**Response:**
```json
[
  {
    "alertId": 1,
    "alertName": "Weather Alert",
    "enabled": true
  },
  {
    "alertId": 2,
    "alertName": "Emergency Alert", 
    "enabled": false
  }
]
```

#### Get Single User Preference
Check if a user is subscribed to a specific alert type.

**Endpoint:** `GET /api/v1/users/{userId}/preferences/{alertId}`

**Path Parameters:**
- `userId` - User's numeric ID
- `alertId` - Alert type ID

**Response:**
```json
{
  "isEnabled": true
}
```

#### Update Single User Preference
Update a user's subscription to a specific alert type.

**Endpoint:** `POST /api/v1/users/{userId}/preferences`

**Path Parameters:**
- `userId` - User's numeric ID

**Request Body:**
```json
{
  "alertId": 1,
  "isEnabled": true
}
```

**Response:** `"User preferences updated."`

#### Send Notification (Admin Only)
Send a notification to all users subscribed to a specific alert type.

**Endpoint:** `POST /api/v1/notifications`

**Request Body:**
```json
{
  "alertId": 1,
  "message": "Severe weather warning in your area. Take shelter immediately."
}
```

**Response:** `"Notification sent successfully."`

**Note:** This endpoint requires admin authentication.

#### SMS Status Webhook
Webhook endpoint for SMS delivery status updates (used by SMS providers).

**Endpoint:** `POST /api/v1/notifications/status`

**Request Parameters:**
- `MessageSid` - SMS message ID
- `MessageStatus` - Delivery status

---

## 🔧 Integration Examples

### JavaScript/Node.js
```javascript
// Get alert types
const response = await fetch('http://localhost:8081/api/v1/alert-types');
const alertTypes = await response.json();

// Register user
await fetch('http://localhost:8081/api/users', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    firstName: 'Jane',
    lastName: 'Smith',
    email: 'jane@example.com',
    userPhoneNumber: '+1987654321'
  })
});
```

### Python
```python
import requests

# Get alert types
response = requests.get('http://localhost:8081/api/v1/alert-types')
alert_types = response.json()

# Register user
user_data = {
    'firstName': 'Jane',
    'lastName': 'Smith', 
    'email': 'jane@example.com',
    'userPhoneNumber': '+1987654321'
}
requests.post('http://localhost:8081/api/users', json=user_data)
```

### Java/Spring Boot
```java
RestTemplate restTemplate = new RestTemplate();

// Get alert types
AlertType[] alertTypes = restTemplate.getForObject(
    "http://localhost:8081/api/v1/alert-types", 
    AlertType[].class
);

// Register user
UserDTO user = new UserDTO("Jane", "Smith", "jane@example.com", "+1987654321");
restTemplate.postForObject("http://localhost:8081/api/users", user, Void.class);
```

---

## 🛡️ Security

- **API Endpoints (`/api/**`)**: Open access, no authentication required
- **Admin Web Interface (`/admin/**`)**: Requires authentication (admin/password)
- **CSRF Protection**: Disabled for API endpoints
- **Session Management**: Stateless for API endpoints

---

## 📊 Error Responses

### 404 Not Found
```json
{
  "error": "User not found"
}
```

### 400 Bad Request
```json
{
  "error": "Failed to update preferences: Invalid alert type ID"
}
```

### 500 Internal Server Error
```json
{
  "error": "Internal server error occurred"
}
```

---

## 🚀 Getting Started

1. **Start the notification service**: `mvn spring-boot:run`
2. **Service runs on**: `http://localhost:8081`
3. **Create alert types**: Access admin interface at `http://localhost:8081/admin` (admin/password)
4. **Add your alert types**: Use the admin interface to create alert types (Weather, Emergency, etc.)
5. **Test the API**: `curl http://localhost:8081/api/v1/alert-types`

**Note**: The service starts with an empty alert types database. Admins must create alert types through the web interface before the API will return data.

---

## 📞 Support

For questions or issues:
- Check the application logs for detailed error information
- Ensure the service is running on port 8081
- Verify JSON request format matches the examples above

---

## 🔄 API Versioning

Current API version: `v1`
- All endpoints are prefixed with `/api/v1/` or `/api/`
- Future versions will maintain backward compatibility
