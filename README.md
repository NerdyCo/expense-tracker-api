# 💰 Personal Finance Tracker

A personal finance tracker to log income and expenses by category.

> **Note:** Authentication & Authorization are **not implemented** yet.

---

## ✨ Features

- Track income and expenses
- View monthly totals
- Analyze category breakdowns

---

## 🗃️ Database Schema

### 🔹 User

| Field    | Type    | Notes         |
|----------|---------|---------------|
| id       | Long    | Primary Key   |
| username | String  | Unique        |
| email    | String  | Unique        |
| password | String  | Hashed        |

**Relationships:**

- One `User` → Many `Categories`
- One `User` → Many `Transactions`

---

### 🔹 Category

| Field    | Type   | Notes               |
|----------|--------|---------------------|
| id       | Long   | Primary Key         |
| user_id  | Long   | Foreign Key → User  |
| name     | String | e.g., Food, Transport |

**Relationships:**

- One `User` → Many `Categories`
- One `Category` → Many `Transactions`

---

### 🔹 Transaction

| Field       | Type        | Notes                                |
|-------------|-------------|--------------------------------------|
| id          | Long        | Primary Key                          |
| user_id     | Long        | Foreign Key → User                   |
| category_id | Long        | Foreign Key → Category               |
| amount      | BigDecimal  |                                      |
| type        | String      | INCOME or EXPENSE                    |
| description | String      | Optional                             |
| date        | LocalDate   | When the transaction occurred        |

**Relationships:**

- One `User` → Many `Transactions`
- One `Category` → Many `Transactions`

---

## 🛢️ SQL Schema

```sql
CREATE TABLE User (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE Category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE
);

CREATE TABLE Transaction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    type VARCHAR(10) CHECK (type IN ('INCOME', 'EXPENSE')) NOT NULL,
    description TEXT,
    date DATE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES Category(id) ON DELETE CASCADE
);
```

## 🔌 API Endpoints
### 👤 User
_POST /api/register

**Request**

```json
{
  "username": "kautsar",
  "email": "kautsar@mail.com",
  "password": "supersecurepassword"
}
```
**Response**

```json
{
  "id": 1,
  "username": "kautsar",
  "email": "kautsar@mail.com"
}
```

### 📁 Category
_POST /api/categories_

**Request**

```json
{
  "name": "Food"
}
```

**Response**

```json
{
  "id": 1,
  "name": "Food"
}
```

_GET /api/categories_

**Response**
```json
[
  {
    "id": 1,
    "name": "Food"
  },
  {
    "id": 2,
    "name": "Transport"
  }
]
```

### 💸 Transaction

_POST /api/transactions_

**Request**

```json
{
  "amount": 55000,
  "type": "EXPENSE",
  "description": "Lunch at warung",
  "date": "2025-05-12",
  "category_id": 1
}
```

**Response**
```json
{
  "id": 1,
  "amount": 55000,
  "type": "EXPENSE",
  "description": "Lunch at warung",
  "date": "2025-05-12",
  "category": {
    "id": 1,
    "name": "Food"
  }
}
```

_GET /api/transactions_

**Response**

```json
[
  {
    "id": 1,
    "amount": 55000,
    "type": "EXPENSE",
    "description": "Lunch at warung",
    "date": "2025-05-12",
    "category": {
      "id": 1,
      "name": "Food"
    }
  },
  {
    "id": 2,
    "amount": 300000,
    "type": "INCOME",
    "description": "Freelance project",
    "date": "2025-05-10",
    "category": {
      "id": 3,
      "name": "Work"
    }
  }
]
```

### 📊 Summary (Optional)

_GET /api/summary?month=2025-05_

**Response**

```json
{
  "total_income": 300000,
  "total_expense": 55000,
  "balance": 245000
}
```

### 🧾 License
> This project is open-source and free to use.
