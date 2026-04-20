# Spark Todo App - Laravel API Specification

## Base URL
```
http://your-domain.com/api
```

## Authentication
All authenticated endpoints require a Bearer token in the Authorization header:
```
Authorization: Bearer {token}
```

---

## Authentication Endpoints

### 1. Register
**POST** `/auth/register`

**Request Body:**
```json
{
  "name": "Maya",
  "email": "maya@example.com",
  "password": "password123",
  "password_confirmation": "password123"
}
```

**Response (201):**
```json
{
  "success": true,
  "data": {
    "user": {
      "id": 1,
      "name": "Maya",
      "email": "maya@example.com",
      "created_at": "2026-04-18T10:00:00.000000Z"
    },
    "token": "1|xyz123..."
  },
  "message": "User registered successfully"
}
```

---

### 2. Login
**POST** `/auth/login`

**Request Body:**
```json
{
  "email": "maya@example.com",
  "password": "password123"
}
```

**Response (200):**
```json
{
  "success": true,
  "data": {
    "user": {
      "id": 1,
      "name": "Maya",
      "email": "maya@example.com"
    },
    "token": "1|xyz123..."
  },
  "message": "Login successful"
}
```

**Error Response (401):**
```json
{
  "success": false,
  "message": "Invalid credentials"
}
```

---

### 3. Logout
**POST** `/auth/logout`

**Headers:** `Authorization: Bearer {token}`

**Response (200):**
```json
{
  "success": true,
  "message": "Logged out successfully"
}
```

---

### 4. Get Current User
**GET** `/auth/user`

**Headers:** `Authorization: Bearer {token}`

**Response (200):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "Maya",
    "email": "maya@example.com",
    "created_at": "2026-04-18T10:00:00.000000Z"
  }
}
```

---

## Task Endpoints

### 5. Get All Tasks
**GET** `/tasks`

**Headers:** `Authorization: Bearer {token}`

**Query Parameters (Optional):**
- `filter`: `all` | `completed` | `pending` (default: `all`)
- `tag`: Filter by tag name
- `date`: Filter by date (YYYY-MM-DD)

**Response (200):**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "title": "Review Q2 roadmap draft",
      "tag": "Work",
      "time": "10:00",
      "done": false,
      "priority": "high",
      "user_id": 1,
      "created_at": "2026-04-18T08:00:00.000000Z",
      "updated_at": "2026-04-18T08:00:00.000000Z"
    },
    {
      "id": 2,
      "title": "Buy oat milk & sourdough",
      "tag": "Errands",
      "time": "12:30",
      "done": false,
      "priority": "low",
      "user_id": 1,
      "created_at": "2026-04-18T08:00:00.000000Z",
      "updated_at": "2026-04-18T08:00:00.000000Z"
    }
  ]
}
```

---

### 6. Get Single Task
**GET** `/tasks/{id}`

**Headers:** `Authorization: Bearer {token}`

**Response (200):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "title": "Review Q2 roadmap draft",
    "tag": "Work",
    "time": "10:00",
    "done": false,
    "priority": "high",
    "user_id": 1,
    "created_at": "2026-04-18T08:00:00.000000Z",
    "updated_at": "2026-04-18T08:00:00.000000Z"
  }
}
```

**Error (404):**
```json
{
  "success": false,
  "message": "Task not found"
}
```

---

### 7. Create Task
**POST** `/tasks`

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "title": "Review Q2 roadmap draft",
  "tag": "Work",
  "time": "10:00",
  "priority": "high"
}
```

**Validation Rules:**
- `title`: required, string, max:255
- `tag`: required, string, max:50
- `time`: required, string (HH:MM format)
- `priority`: required, enum: `low`, `medium`, `high`

**Response (201):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "title": "Review Q2 roadmap draft",
    "tag": "Work",
    "time": "10:00",
    "done": false,
    "priority": "high",
    "user_id": 1,
    "created_at": "2026-04-18T08:00:00.000000Z",
    "updated_at": "2026-04-18T08:00:00.000000Z"
  },
  "message": "Task created successfully"
}
```

---

### 8. Update Task
**PUT** `/tasks/{id}`

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "title": "Review Q2 roadmap draft - Updated",
  "tag": "Work",
  "time": "11:00",
  "done": true,
  "priority": "high"
}
```

**Response (200):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "title": "Review Q2 roadmap draft - Updated",
    "tag": "Work",
    "time": "11:00",
    "done": true,
    "priority": "high",
    "user_id": 1,
    "created_at": "2026-04-18T08:00:00.000000Z",
    "updated_at": "2026-04-18T10:30:00.000000Z"
  },
  "message": "Task updated successfully"
}
```

---

### 9. Toggle Task Status
**PATCH** `/tasks/{id}/toggle`

**Headers:** `Authorization: Bearer {token}`

**Response (200):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "done": true
  },
  "message": "Task status toggled successfully"
}
```

---

### 10. Delete Task
**DELETE** `/tasks/{id}`

**Headers:** `Authorization: Bearer {token}`

**Response (200):**
```json
{
  "success": true,
  "message": "Task deleted successfully"
}
```

---

## Analytics Endpoints

### 11. Get Analytics
**GET** `/analytics`

**Headers:** `Authorization: Bearer {token}`

**Query Parameters (Optional):**
- `period`: `today` | `week` | `month` (default: `today`)

**Response (200):**
```json
{
  "success": true,
  "data": {
    "period": "today",
    "total_tasks": 6,
    "completed_tasks": 2,
    "pending_tasks": 4,
    "completion_rate": 33.33,
    "tasks_by_tag": {
      "Work": 2,
      "Errands": 1,
      "Study": 1,
      "Health": 1,
      "Personal": 1
    },
    "tasks_by_priority": {
      "high": 1,
      "medium": 2,
      "low": 3
    },
    "productivity_streak": 5
  }
}
```

---

### 12. Get Task History
**GET** `/analytics/history`

**Headers:** `Authorization: Bearer {token}`

**Query Parameters:**
- `days`: Number of days (default: 7, max: 90)

**Response (200):**
```json
{
  "success": true,
  "data": [
    {
      "date": "2026-04-18",
      "total": 6,
      "completed": 2,
      "completion_rate": 33.33
    },
    {
      "date": "2026-04-17",
      "total": 8,
      "completed": 6,
      "completion_rate": 75.0
    }
  ]
}
```

---

## Profile Endpoints

### 13. Update Profile
**PUT** `/profile`

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "name": "Maya Updated",
  "email": "maya.updated@example.com"
}
```

**Response (200):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "Maya Updated",
    "email": "maya.updated@example.com",
    "updated_at": "2026-04-18T12:00:00.000000Z"
  },
  "message": "Profile updated successfully"
}
```

---

### 14. Change Password
**POST** `/profile/password`

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "current_password": "oldpassword123",
  "new_password": "newpassword123",
  "new_password_confirmation": "newpassword123"
}
```

**Response (200):**
```json
{
  "success": true,
  "message": "Password changed successfully"
}
```

---

## Database Schema

### Users Table
```php
Schema::create('users', function (Blueprint $table) {
    $table->id();
    $table->string('name');
    $table->string('email')->unique();
    $table->timestamp('email_verified_at')->nullable();
    $table->string('password');
    $table->rememberToken();
    $table->timestamps();
});
```

### Tasks Table
```php
Schema::create('tasks', function (Blueprint $table) {
    $table->id();
    $table->foreignId('user_id')->constrained()->onDelete('cascade');
    $table->string('title');
    $table->string('tag');
    $table->string('time'); // Format: HH:MM
    $table->boolean('done')->default(false);
    $table->enum('priority', ['low', 'medium', 'high']);
    $table->timestamps();
});
```

---

## Error Response Format

All errors follow this format:

```json
{
  "success": false,
  "message": "Error message here",
  "errors": {
    "field_name": [
      "Validation error message"
    ]
  }
}
```

**Common HTTP Status Codes:**
- `200` - Success
- `201` - Created
- `400` - Bad Request
- `401` - Unauthorized
- `403` - Forbidden
- `404` - Not Found
- `422` - Validation Error
- `500` - Server Error

---

## Implementation Notes

1. Use Laravel Sanctum for API authentication
2. Add CORS middleware for Android app access
3. Use API Resource classes for consistent response formatting
4. Implement request validation using Form Requests
5. Add rate limiting to prevent abuse
6. Use soft deletes for tasks if needed
7. Add indexes on `user_id`, `done`, and `created_at` columns for better query performance

---

## Android Integration

Add these dependencies to the Android app:

```kotlin
// Already added in build.gradle.kts
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-gson:2.11.0")
```

API Base URL configuration should be stored in `local.properties` or BuildConfig for different environments (dev/staging/production).
