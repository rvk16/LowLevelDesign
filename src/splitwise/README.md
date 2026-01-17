# Splitwise Low-Level Design (LLD)

> A comprehensive expense-sharing application demonstrating OOP concepts, SOLID principles, and Design Patterns - Perfect for System Design Interviews!

## Table of Contents
1. [System Overview](#system-overview)
2. [Requirements](#requirements)
3. [Class Diagram](#class-diagram)
4. [Design Patterns](#design-patterns)
5. [SOLID Principles](#solid-principles)
6. [Core Components](#core-components)
7. [Sequence Diagrams](#sequence-diagrams)
8. [Data Flow](#data-flow)
9. [Key Algorithms](#key-algorithms)
10. [Interview Talking Points](#interview-talking-points)
11. [How to Run](#how-to-run)

---

## System Overview

Splitwise is an expense-sharing application that allows users to:
- Split expenses among friends (Equal, Exact, Percentage)
- Create groups for shared expenses
- Track balances between users
- Simplify debts to minimize transactions
- Support multiple currencies
- Record payments and settlements
- View activity feed

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                         CLIENT LAYER                                │
│                      (oop.abstraction.Main.java - Demo)                             │
└─────────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      MANAGER LAYER (Facade)                         │
│                    SplitwiseManager (Singleton)                     │
│         - Single entry point for all operations                     │
│         - Dependency Injection container                            │
└─────────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────────┐
│                       SERVICE LAYER                                 │
│  ┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────────────┐   │
│  │UserService│ │GroupService│ │ExpenseServ│ │BalanceService     │   │
│  └───────────┘ └───────────┘ └───────────┘ └───────────────────┘   │
│  ┌───────────┐ ┌───────────┐ ┌───────────┐                         │
│  │CurrencySvc│ │Transaction│ │ActivitySvc│                         │
│  └───────────┘ └───────────┘ └───────────┘                         │
└─────────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────────┐
│                       DOMAIN LAYER                                  │
│  ┌──────┐ ┌───────┐ ┌───────┐ ┌───────┐ ┌───────────┐ ┌────────┐  │
│  │ User │ │ Group │ │Expense│ │Balance│ │Transaction│ │Activity│  │
│  └──────┘ └───────┘ └───────┘ └───────┘ └───────────┘ └────────┘  │
│                        │                                            │
│                        ▼                                            │
│              ┌─────────────────┐                                    │
│              │  Split (Abstract)│                                   │
│              └─────────────────┘                                    │
│                ▲     ▲      ▲                                       │
│    ┌───────────┘     │      └────────────┐                         │
│    │                 │                   │                          │
│ ┌──────────┐ ┌───────────┐ ┌──────────────┐                        │
│ │EqualSplit│ │ExactSplit │ │PercentageSplit│                       │
│ └──────────┘ └───────────┘ └──────────────┘                        │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Requirements

### Functional Requirements
1. **User Management**: Create and manage users
2. **Group Management**: Create groups, add/remove members
3. **Expense Management**: Add expenses with different split types
4. **Balance Tracking**: Track who owes whom and how much
5. **Settlements**: Record payments between users
6. **Multi-Currency**: Support multiple currencies with conversion
7. **Activity Feed**: Track all activities in the system

### Non-Functional Requirements
1. **Extensibility**: Easy to add new split types
2. **Maintainability**: Clean separation of concerns
3. **Testability**: Loose coupling enables unit testing

---

## Class Diagram

### Complete Class Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                                 MODELS                                          │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌─────────────────────┐         ┌─────────────────────┐                       │
│  │        User         │         │        Group        │                       │
│  ├─────────────────────┤         ├─────────────────────┤                       │
│  │ - id: String        │         │ - id: String        │                       │
│  │ - name: String      │         │ - name: String      │                       │
│  │ - email: String     │◄───────┐│ - description: String                       │
│  │ - phone: String     │        ││ - createdBy: User   │                       │
│  │ - preferredCurrency │        ││ - members: Set<User>│                       │
│  │ - balances: Map     │        ││ - admins: Set<User> │                       │
│  ├─────────────────────┤        │├─────────────────────┤                       │
│  │ + updateBalance()   │        ││ + addMember()       │                       │
│  │ + getBalanceWith()  │        ││ + removeMember()    │                       │
│  │ + getTotalOwed()    │        ││ + isMember()        │                       │
│  └─────────────────────┘        │└─────────────────────┘                       │
│           ▲                     │                                               │
│           │                     │                                               │
│           │         ┌───────────┴──────────────┐                               │
│           │         │                          │                               │
│  ┌────────┴────────────────┐    ┌─────────────────────┐                       │
│  │       Expense           │    │      Balance        │                       │
│  ├─────────────────────────┤    ├─────────────────────┤                       │
│  │ - id: String            │    │ - fromUser: User    │                       │
│  │ - description: String   │    │ - toUser: User      │                       │
│  │ - amount: double        │    │ - amount: double    │                       │
│  │ - currency: Currency    │    │ - currency: Currency│                       │
│  │ - paidBy: User          │    ├─────────────────────┤                       │
│  │ - splits: List<Split>   │    │ + isSettled()       │                       │
│  │ - type: ExpenseType     │    │ + getDescription()  │                       │
│  │ - groupId: String       │    └─────────────────────┘                       │
│  ├─────────────────────────┤                                                   │
│  │ + involvesUser()        │                                                   │
│  │ + getShareForUser()     │                                                   │
│  └─────────────────────────┘                                                   │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────┐
│                            SPLIT HIERARCHY (Inheritance)                        │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│                        ┌─────────────────────┐                                 │
│                        │   <<abstract>>      │                                 │
│                        │      Split          │                                 │
│                        ├─────────────────────┤                                 │
│                        │ # user: User        │                                 │
│                        │ # amount: double    │                                 │
│                        ├─────────────────────┤                                 │
│                        │ + getUser()         │                                 │
│                        │ + getAmount()       │                                 │
│                        │ + setAmount()       │                                 │
│                        │ + validate(): bool  │  ◄── Abstract method           │
│                        └─────────────────────┘                                 │
│                                  △                                              │
│                                  │                                              │
│            ┌─────────────────────┼─────────────────────┐                       │
│            │                     │                     │                       │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────┐            │
│  │   EqualSplit    │  │   ExactSplit    │  │  PercentageSplit    │            │
│  ├─────────────────┤  ├─────────────────┤  ├─────────────────────┤            │
│  │                 │  │                 │  │ - percentage: double│            │
│  ├─────────────────┤  ├─────────────────┤  ├─────────────────────┤            │
│  │ + validate()    │  │ + validate()    │  │ + validate()        │            │
│  │   // always true│  │   // amount >= 0│  │   // 0 <= % <= 100  │            │
│  └─────────────────┘  └─────────────────┘  └─────────────────────┘            │
│                                                                                 │
│  Liskov Substitution: Any Split subclass can replace the base Split class      │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────┐
│                          STRATEGY PATTERN                                       │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│                      ┌───────────────────────────┐                             │
│                      │    <<interface>>          │                             │
│                      │    SplitStrategy          │                             │
│                      ├───────────────────────────┤                             │
│                      │ + validate(amount, splits)│                             │
│                      │ + calculateSplits()       │                             │
│                      │ + createSplits()          │                             │
│                      └───────────────────────────┘                             │
│                                  △                                              │
│                                  │                                              │
│         ┌────────────────────────┼────────────────────────┐                    │
│         │                        │                        │                    │
│ ┌───────────────────┐ ┌───────────────────┐ ┌───────────────────────┐         │
│ │EqualSplitStrategy │ │ExactSplitStrategy │ │PercentageSplitStrategy│         │
│ ├───────────────────┤ ├───────────────────┤ ├───────────────────────┤         │
│ │ Divides equally   │ │ Validates sum     │ │ Validates 100%        │         │
│ │ among all users   │ │ equals total      │ │ Calculates amounts    │         │
│ └───────────────────┘ └───────────────────┘ └───────────────────────┘         │
│                                                                                 │
│  Open/Closed Principle: Add new strategies without modifying existing code     │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────┐
│                           SERVICE LAYER                                         │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌──────────────────┐         ┌──────────────────────┐                         │
│  │  <<interface>>   │         │                      │                         │
│  │   UserService    │◄────────│   UserServiceImpl    │                         │
│  ├──────────────────┤         └──────────────────────┘                         │
│  │ + createUser()   │                                                          │
│  │ + getUserById()  │         ┌──────────────────────┐                         │
│  │ + getAllUsers()  │         │                      │                         │
│  └──────────────────┘         │  GroupServiceImpl    │                         │
│                               └──────────────────────┘                         │
│  ┌──────────────────┐                    ▲                                     │
│  │  <<interface>>   │                    │                                     │
│  │  ExpenseService  │◄───────────────────┤                                     │
│  ├──────────────────┤         ┌──────────────────────┐                         │
│  │ + createExpense()│         │                      │                         │
│  │ + addObserver()  │◄────────│  ExpenseServiceImpl  │                         │
│  │ + deleteExpense()│         │  - observers: List   │                         │
│  └──────────────────┘         └──────────────────────┘                         │
│                                          │                                     │
│                                          │ uses                                │
│                                          ▼                                     │
│                               ┌──────────────────────┐                         │
│                               │   BalanceService     │                         │
│                               └──────────────────────┘                         │
│                                                                                 │
│  Dependency Inversion: Services depend on interfaces, not implementations      │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## Design Patterns

### 1. Strategy Pattern (Behavioral)

**Problem**: Different expense types need different split calculation logic.

**Solution**: Define a family of algorithms (split strategies), encapsulate each one, and make them interchangeable.

```
┌─────────────────────────────────────────────────────────────────┐
│                    STRATEGY PATTERN                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│    Client (ExpenseFactory)                                      │
│           │                                                     │
│           │ uses                                                │
│           ▼                                                     │
│    ┌─────────────────┐                                         │
│    │  SplitStrategy  │◄─────── Strategy Interface              │
│    │    (interface)  │                                         │
│    └────────┬────────┘                                         │
│             │                                                   │
│    ┌────────┴────────┬─────────────────┐                       │
│    │                 │                 │                       │
│    ▼                 ▼                 ▼                       │
│ ┌──────────┐  ┌──────────┐  ┌────────────────┐                │
│ │  Equal   │  │  Exact   │  │  Percentage    │                │
│ │ Strategy │  │ Strategy │  │   Strategy     │                │
│ └──────────┘  └──────────┘  └────────────────┘                │
│                                                                 │
│  Benefits:                                                      │
│  ✓ Add new split types without changing existing code          │
│  ✓ Runtime strategy selection                                  │
│  ✓ Each algorithm is independently testable                    │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

**Code Example**:
```java
// Strategy Interface
public interface SplitStrategy {
    void validate(double totalAmount, List<Split> splits);
    void calculateSplits(double totalAmount, List<Split> splits);
    List<Split> createSplits(double totalAmount, List<User> users);
}

// Concrete Strategy
public class EqualSplitStrategy implements SplitStrategy {
    @Override
    public void calculateSplits(double totalAmount, List<Split> splits) {
        double equalShare = totalAmount / splits.size();
        for (Split split : splits) {
            split.setAmount(equalShare);
        }
    }
}

// Context (uses strategy)
public class ExpenseFactory {
    public static Expense createExpense(..., ExpenseType type, ...) {
        SplitStrategy strategy = SplitFactory.getStrategy(type);  // Get strategy
        List<Split> splits = strategy.createSplits(amount, participants);
        strategy.validate(amount, splits);  // Use strategy
        // ...
    }
}
```

---

### 2. Factory Pattern (Creational)

**Problem**: Complex object creation logic scattered across the codebase.

**Solution**: Centralize object creation in dedicated factory classes.

```
┌─────────────────────────────────────────────────────────────────┐
│                     FACTORY PATTERN                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│                    ┌─────────────────┐                         │
│                    │  SplitFactory   │                         │
│                    ├─────────────────┤                         │
│                    │ + createSplit() │                         │
│                    │ + getStrategy() │                         │
│                    └────────┬────────┘                         │
│                             │                                   │
│              creates        │                                   │
│         ┌───────────────────┼───────────────────┐              │
│         │                   │                   │              │
│         ▼                   ▼                   ▼              │
│   ┌───────────┐      ┌───────────┐      ┌──────────────┐      │
│   │EqualSplit │      │ExactSplit │      │PercentageSplit│     │
│   └───────────┘      └───────────┘      └──────────────┘      │
│                                                                 │
│                    ┌─────────────────┐                         │
│                    │ ExpenseFactory  │                         │
│                    ├─────────────────┤                         │
│                    │+createExpense() │───────► Creates         │
│                    │+createGroupExp()│        validated        │
│                    └─────────────────┘        expenses         │
│                                                                 │
│  Benefits:                                                      │
│  ✓ Single place for creation logic                             │
│  ✓ Easy to change how objects are created                      │
│  ✓ Validation during creation                                  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

**Code Example**:
```java
public class SplitFactory {

    public static Split createSplit(ExpenseType type, User user, double amount) {
        switch (type) {
            case EQUAL:      return new EqualSplit(user);
            case EXACT:      return new ExactSplit(user, amount);
            case PERCENTAGE: return new PercentageSplit(user, amount);
            default: throw new IllegalArgumentException("Unknown type");
        }
    }

    public static SplitStrategy getStrategy(ExpenseType type) {
        switch (type) {
            case EQUAL:      return new EqualSplitStrategy();
            case EXACT:      return new ExactSplitStrategy();
            case PERCENTAGE: return new PercentageSplitStrategy();
            default: throw new IllegalArgumentException("Unknown type");
        }
    }
}
```

---

### 3. Singleton Pattern (Creational)

**Problem**: Need a single point of access to manage all services.

**Solution**: Ensure a class has only one instance with global access.

```
┌─────────────────────────────────────────────────────────────────┐
│                    SINGLETON PATTERN                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│              ┌─────────────────────────────┐                   │
│              │     SplitwiseManager        │                   │
│              ├─────────────────────────────┤                   │
│              │ - instance: SplitwiseManager│ ◄── static        │
│              │ - userService               │                   │
│              │ - groupService              │                   │
│              │ - expenseService            │                   │
│              │ - balanceService            │                   │
│              │ - ...                       │                   │
│              ├─────────────────────────────┤                   │
│              │ - SplitwiseManager()        │ ◄── private       │
│              │ + getInstance()             │ ◄── static        │
│              │ + createUser()              │                   │
│              │ + addExpense()              │                   │
│              │ + ...                       │                   │
│              └─────────────────────────────┘                   │
│                                                                 │
│  Thread-Safe Double-Checked Locking:                           │
│                                                                 │
│  public static SplitwiseManager getInstance() {                │
│      if (instance == null) {                                   │
│          synchronized (SplitwiseManager.class) {               │
│              if (instance == null) {                           │
│                  instance = new SplitwiseManager();            │
│              }                                                 │
│          }                                                     │
│      }                                                         │
│      return instance;                                          │
│  }                                                             │
│                                                                 │
│  Benefits:                                                      │
│  ✓ Single entry point (Facade pattern combined)                │
│  ✓ Manages all service dependencies                            │
│  ✓ Global access without passing references                    │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

### 4. Observer Pattern (Behavioral)

**Problem**: Multiple objects need to be notified when an expense is added/deleted.

**Solution**: Define a one-to-many dependency between objects.

```
┌─────────────────────────────────────────────────────────────────┐
│                    OBSERVER PATTERN                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│           ┌───────────────────────────┐                        │
│           │   ExpenseServiceImpl      │                        │
│           │       (Subject)           │                        │
│           ├───────────────────────────┤                        │
│           │ - observers: List         │                        │
│           ├───────────────────────────┤                        │
│           │ + addObserver()           │                        │
│           │ + removeObserver()        │                        │
│           │ + notifyExpenseAdded()    │───────┐                │
│           └───────────────────────────┘       │                │
│                                               │ notifies       │
│           ┌───────────────────────────┐       │                │
│           │   <<interface>>           │       │                │
│           │   ExpenseObserver         │◄──────┘                │
│           ├───────────────────────────┤                        │
│           │ + onExpenseAdded()        │                        │
│           │ + onExpenseUpdated()      │                        │
│           │ + onExpenseDeleted()      │                        │
│           └───────────────────────────┘                        │
│                       △                                         │
│                       │ implements                              │
│                       │                                         │
│           ┌───────────────────────────┐                        │
│           │   NotificationService     │                        │
│           ├───────────────────────────┤                        │
│           │ + onExpenseAdded()        │──► Sends notifications │
│           │ + onExpenseDeleted()      │                        │
│           └───────────────────────────┘                        │
│                                                                 │
│  Flow:                                                          │
│  1. Expense created in ExpenseService                          │
│  2. ExpenseService calls notifyExpenseAdded()                  │
│  3. All registered observers are notified                      │
│  4. NotificationService sends alerts to users                  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

**Code Example**:
```java
// Observer Interface
public interface ExpenseObserver {
    void onExpenseAdded(Expense expense);
    void onExpenseDeleted(Expense expense);
}

// Subject (Observable)
public class ExpenseServiceImpl implements ExpenseService {
    private final List<ExpenseObserver> observers = new ArrayList<>();

    public void addObserver(ExpenseObserver observer) {
        observers.add(observer);
    }

    private void notifyExpenseAdded(Expense expense) {
        for (ExpenseObserver observer : observers) {
            observer.onExpenseAdded(expense);  // Notify all observers
        }
    }

    public Expense createExpense(...) {
        Expense expense = // create expense
        notifyExpenseAdded(expense);  // Trigger notifications
        return expense;
    }
}

// Concrete Observer
public class NotificationService implements ExpenseObserver {
    @Override
    public void onExpenseAdded(Expense expense) {
        // Send push notification, email, etc.
        System.out.println("New expense added: " + expense.getDescription());
    }
}
```

---

## SOLID Principles

### S - Single Responsibility Principle

> "A class should have only one reason to change."

```
┌─────────────────────────────────────────────────────────────────┐
│                SINGLE RESPONSIBILITY                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ✗ BAD: One class doing everything                             │
│  ┌─────────────────────────────┐                               │
│  │      SplitwiseApp           │                               │
│  ├─────────────────────────────┤                               │
│  │ + createUser()              │                               │
│  │ + createExpense()           │                               │
│  │ + calculateSplits()         │  ◄── Too many responsibilities│
│  │ + updateBalances()          │                               │
│  │ + sendNotifications()       │                               │
│  │ + convertCurrency()         │                               │
│  └─────────────────────────────┘                               │
│                                                                 │
│  ✓ GOOD: Separate classes for each responsibility              │
│                                                                 │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐              │
│  │ UserService │ │ExpenseService│ │BalanceService│             │
│  ├─────────────┤ ├─────────────┤ ├─────────────┤              │
│  │+ createUser │ │+createExpense│ │+updateBalance│             │
│  │+ getUser    │ │+deleteExpense│ │+getBalance   │             │
│  └─────────────┘ └─────────────┘ └─────────────┘              │
│                                                                 │
│  Each class has ONE reason to change:                          │
│  - UserService: Only if user management logic changes          │
│  - ExpenseService: Only if expense handling changes            │
│  - BalanceService: Only if balance calculation changes         │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

### O - Open/Closed Principle

> "Open for extension, closed for modification."

```
┌─────────────────────────────────────────────────────────────────┐
│                   OPEN/CLOSED PRINCIPLE                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ✗ BAD: Modifying existing code for new split types            │
│                                                                 │
│  public void calculateSplit(ExpenseType type, ...) {           │
│      if (type == EQUAL) { ... }                                │
│      else if (type == EXACT) { ... }                           │
│      else if (type == PERCENTAGE) { ... }                      │
│      // Adding new type requires modifying this method!        │
│      else if (type == SHARES) { ... }  ◄── Modification!       │
│  }                                                             │
│                                                                 │
│  ✓ GOOD: Using Strategy Pattern - extend without modification  │
│                                                                 │
│       ┌─────────────────┐                                      │
│       │  SplitStrategy  │ ◄── Interface (closed)               │
│       └────────┬────────┘                                      │
│                │                                               │
│    ┌───────────┼───────────┬───────────┐                      │
│    │           │           │           │                      │
│    ▼           ▼           ▼           ▼                      │
│ ┌──────┐  ┌──────┐  ┌──────────┐  ┌──────────┐               │
│ │Equal │  │Exact │  │Percentage│  │  Shares  │               │
│ └──────┘  └──────┘  └──────────┘  └──────────┘               │
│                                       ▲                        │
│                                       │                        │
│                         NEW! Just add a new class              │
│                         No existing code modified!             │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

### L - Liskov Substitution Principle

> "Objects of a superclass should be replaceable with objects of its subclasses."

```
┌─────────────────────────────────────────────────────────────────┐
│               LISKOV SUBSTITUTION PRINCIPLE                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  All Split subclasses can substitute the base Split class:     │
│                                                                 │
│  public void processExpense(Expense expense) {                 │
│      for (Split split : expense.getSplits()) {                 │
│          // Works with ANY Split subclass!                     │
│          User user = split.getUser();     ✓                    │
│          double amount = split.getAmount(); ✓                  │
│          boolean valid = split.validate();  ✓                  │
│      }                                                         │
│  }                                                             │
│                                                                 │
│  ┌──────────────────┐                                          │
│  │  abstract Split  │                                          │
│  ├──────────────────┤                                          │
│  │ + getUser()      │                                          │
│  │ + getAmount()    │                                          │
│  │ + validate()     │ ◄── Contract that ALL subclasses honor   │
│  └────────┬─────────┘                                          │
│           │                                                    │
│   ┌───────┼───────┬───────────────┐                           │
│   │       │       │               │                           │
│   ▼       ▼       ▼               ▼                           │
│ Equal   Exact  Percentage    [Any Future Split]               │
│                                                                 │
│  All subclasses:                                               │
│  ✓ Return valid User from getUser()                           │
│  ✓ Return valid amount from getAmount()                       │
│  ✓ Return boolean from validate()                             │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

### I - Interface Segregation Principle

> "Clients should not be forced to depend on interfaces they don't use."

```
┌─────────────────────────────────────────────────────────────────┐
│              INTERFACE SEGREGATION PRINCIPLE                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ✗ BAD: Fat interface forcing unnecessary implementations      │
│                                                                 │
│  interface SplitwiseService {                                  │
│      void createUser();                                        │
│      void createExpense();                                     │
│      void updateBalance();         ◄── Too many methods!      │
│      void sendNotification();                                  │
│      void convertCurrency();                                   │
│  }                                                             │
│                                                                 │
│  ✓ GOOD: Small, focused interfaces                             │
│                                                                 │
│  ┌─────────────────┐  ┌─────────────────┐  ┌────────────────┐ │
│  │  UserService    │  │ ExpenseObserver │  │ActivityObserver│ │
│  ├─────────────────┤  ├─────────────────┤  ├────────────────┤ │
│  │ + createUser()  │  │ + onExpenseAdd()│  │+ onActivity()  │ │
│  │ + getUser()     │  │ + onExpenseDel()│  └────────────────┘ │
│  └─────────────────┘  └─────────────────┘                      │
│                                                                 │
│  Clients depend ONLY on what they need:                        │
│                                                                 │
│  - NotificationService implements ExpenseObserver              │
│    (doesn't need UserService methods)                          │
│                                                                 │
│  - ActivityService implements ActivityObserver                 │
│    (doesn't need ExpenseObserver methods)                      │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

### D - Dependency Inversion Principle

> "Depend on abstractions, not concretions."

```
┌─────────────────────────────────────────────────────────────────┐
│              DEPENDENCY INVERSION PRINCIPLE                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ✗ BAD: High-level depends on low-level                        │
│                                                                 │
│  ┌──────────────────────┐                                      │
│  │  ExpenseServiceImpl  │                                      │
│  ├──────────────────────┤                                      │
│  │ - balance = new      │                                      │
│  │   BalanceServiceImpl │ ◄── Direct dependency on impl!       │
│  └──────────────────────┘                                      │
│                                                                 │
│  ✓ GOOD: Both depend on oop.abstraction                            │
│                                                                 │
│  ┌──────────────────────┐         ┌───────────────────┐        │
│  │  ExpenseServiceImpl  │         │  <<interface>>    │        │
│  ├──────────────────────┤         │  BalanceService   │        │
│  │ - balanceService     │────────►├───────────────────┤        │
│  │   : BalanceService   │         │ + updateBalance() │        │
│  └──────────────────────┘         └───────────────────┘        │
│                                            △                   │
│                                            │                   │
│                                   ┌────────┴────────┐          │
│                                   │BalanceServiceImpl│         │
│                                   └─────────────────┘          │
│                                                                 │
│  Constructor Injection:                                         │
│                                                                 │
│  public ExpenseServiceImpl(BalanceService balanceService) {    │
│      this.balanceService = balanceService;  // Interface!      │
│  }                                                             │
│                                                                 │
│  Benefits:                                                      │
│  ✓ Easy to mock for testing                                    │
│  ✓ Can swap implementations without changing code              │
│  ✓ Loose coupling between components                           │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## Core Components

### Balance Calculation Flow

```
┌─────────────────────────────────────────────────────────────────┐
│              BALANCE UPDATE ON EXPENSE                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Scenario: Alice pays $90 for dinner (split equally with Bob   │
│            and Charlie)                                         │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  BEFORE EXPENSE:                                        │   │
│  │  Alice.balances = {}                                    │   │
│  │  Bob.balances = {}                                      │   │
│  │  Charlie.balances = {}                                  │   │
│  └─────────────────────────────────────────────────────────┘   │
│                           │                                     │
│                           ▼                                     │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  EXPENSE CREATED:                                       │   │
│  │  - Amount: $90                                          │   │
│  │  - Paid by: Alice                                       │   │
│  │  - Splits: Alice=$30, Bob=$30, Charlie=$30              │   │
│  └─────────────────────────────────────────────────────────┘   │
│                           │                                     │
│                           ▼                                     │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  BALANCE UPDATE LOGIC:                                  │   │
│  │                                                         │   │
│  │  For each participant (except payer):                   │   │
│  │    - Payer's balance with participant: +share          │   │
│  │    - Participant's balance with payer: -share          │   │
│  │                                                         │   │
│  │  Alice paid, so:                                        │   │
│  │    Alice.balance[Bob] += 30      (Bob owes Alice)      │   │
│  │    Bob.balance[Alice] -= 30      (Bob owes Alice)      │   │
│  │    Alice.balance[Charlie] += 30  (Charlie owes Alice)  │   │
│  │    Charlie.balance[Alice] -= 30  (Charlie owes Alice)  │   │
│  └─────────────────────────────────────────────────────────┘   │
│                           │                                     │
│                           ▼                                     │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  AFTER EXPENSE:                                         │   │
│  │  Alice.balances = {Bob: +30, Charlie: +30}             │   │
│  │  Bob.balances = {Alice: -30}                           │   │
│  │  Charlie.balances = {Alice: -30}                       │   │
│  │                                                         │   │
│  │  Interpretation:                                        │   │
│  │  - Positive: Others owe me                             │   │
│  │  - Negative: I owe others                              │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## Sequence Diagrams

### Add Expense Sequence

```
┌──────┐    ┌─────────────┐    ┌──────────────┐    ┌─────────────┐    ┌────────────────┐
│Client│    │SplitwiseManager│  │ExpenseService│   │BalanceService│   │NotificationSvc │
└──┬───┘    └──────┬──────┘    └──────┬───────┘    └──────┬──────┘    └───────┬────────┘
   │               │                   │                   │                   │
   │ addExpense()  │                   │                   │                   │
   │──────────────►│                   │                   │                   │
   │               │                   │                   │                   │
   │               │ createExpense()   │                   │                   │
   │               │──────────────────►│                   │                   │
   │               │                   │                   │                   │
   │               │                   │ ┌─────────────────┴──────────────┐    │
   │               │                   │ │ 1. Get strategy from factory   │    │
   │               │                   │ │ 2. Create splits               │    │
   │               │                   │ │ 3. Validate splits             │    │
   │               │                   │ │ 4. Create Expense object       │    │
   │               │                   │ └─────────────────┬──────────────┘    │
   │               │                   │                   │                   │
   │               │                   │ updateBalances()  │                   │
   │               │                   │──────────────────►│                   │
   │               │                   │                   │                   │
   │               │                   │                   │ ┌────────────────┐│
   │               │                   │                   │ │Update balances ││
   │               │                   │                   │ │for all users   ││
   │               │                   │                   │ └────────────────┘│
   │               │                   │                   │                   │
   │               │                   │ notifyObservers() │                   │
   │               │                   │───────────────────┼──────────────────►│
   │               │                   │                   │                   │
   │               │                   │                   │                   │ ┌──────────┐
   │               │                   │                   │                   │ │Send push │
   │               │                   │                   │                   │ │notifs    │
   │               │                   │                   │                   │ └──────────┘
   │               │                   │                   │                   │
   │               │   Expense         │                   │                   │
   │               │◄──────────────────│                   │                   │
   │               │                   │                   │                   │
   │   Expense     │                   │                   │                   │
   │◄──────────────│                   │                   │                   │
   │               │                   │                   │                   │
```

### Settle Up Sequence

```
┌──────┐    ┌─────────────┐    ┌──────────────────┐    ┌─────────────┐
│Client│    │SplitwiseManager│  │TransactionService│   │BalanceService│
└──┬───┘    └──────┬──────┘    └────────┬─────────┘    └──────┬──────┘
   │               │                     │                     │
   │ settleUp()    │                     │                     │
   │──────────────►│                     │                     │
   │               │                     │                     │
   │               │ getBalanceBetween() │                     │
   │               │─────────────────────┼────────────────────►│
   │               │                     │                     │
   │               │                     │      balance        │
   │               │◄────────────────────┼─────────────────────│
   │               │                     │                     │
   │               │ recordSettlement()  │                     │
   │               │────────────────────►│                     │
   │               │                     │                     │
   │               │                     │ settleBalance()     │
   │               │                     │────────────────────►│
   │               │                     │                     │
   │               │                     │    ┌────────────────┴───────────┐
   │               │                     │    │ fromUser.balance[toUser]+= │
   │               │                     │    │ toUser.balance[fromUser]-= │
   │               │                     │    └────────────────┬───────────┘
   │               │                     │                     │
   │               │    Transaction      │                     │
   │               │◄────────────────────│                     │
   │               │                     │                     │
   │  Transaction  │                     │                     │
   │◄──────────────│                     │                     │
   │               │                     │                     │
```

---

## Key Algorithms

### Debt Simplification (Greedy Algorithm)

```
┌─────────────────────────────────────────────────────────────────┐
│              DEBT SIMPLIFICATION ALGORITHM                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Problem: Minimize number of transactions to settle all debts  │
│                                                                 │
│  Before Simplification:                                         │
│  ┌─────────────────────────────────────────┐                   │
│  │  Alice ──$30──► Bob                     │                   │
│  │  Alice ──$20──► Charlie                 │                   │
│  │  Bob ──$50──► Charlie                   │                   │
│  │                                         │                   │
│  │  Total: 3 transactions                  │                   │
│  └─────────────────────────────────────────┘                   │
│                                                                 │
│  Algorithm Steps:                                               │
│                                                                 │
│  Step 1: Calculate NET balance for each person                 │
│  ┌─────────────────────────────────────────┐                   │
│  │  Alice:  -30 - 20 = -50 (owes $50)     │                   │
│  │  Bob:    +30 - 50 = -20 (owes $20)     │                   │
│  │  Charlie: +20 + 50 = +70 (owed $70)    │                   │
│  └─────────────────────────────────────────┘                   │
│                                                                 │
│  Step 2: Separate into creditors and debtors                   │
│  ┌─────────────────────────────────────────┐                   │
│  │  Creditors (owed money):  Charlie (+70) │                   │
│  │  Debtors (owe money):     Alice (-50)   │                   │
│  │                           Bob (-20)     │                   │
│  └─────────────────────────────────────────┘                   │
│                                                                 │
│  Step 3: Match max creditor with max debtor (greedy)           │
│  ┌─────────────────────────────────────────┐                   │
│  │  Iteration 1:                           │                   │
│  │    Charlie needs: $70                   │                   │
│  │    Alice owes: $50                      │                   │
│  │    → Alice pays Charlie $50             │                   │
│  │    Charlie now needs: $20               │                   │
│  │                                         │                   │
│  │  Iteration 2:                           │                   │
│  │    Charlie needs: $20                   │                   │
│  │    Bob owes: $20                        │                   │
│  │    → Bob pays Charlie $20               │                   │
│  │    All settled!                         │                   │
│  └─────────────────────────────────────────┘                   │
│                                                                 │
│  After Simplification:                                          │
│  ┌─────────────────────────────────────────┐                   │
│  │  Alice ──$50──► Charlie                 │                   │
│  │  Bob ──$20──► Charlie                   │                   │
│  │                                         │                   │
│  │  Total: 2 transactions (reduced!)       │                   │
│  └─────────────────────────────────────────┘                   │
│                                                                 │
│  Time Complexity: O(n log n) using priority queues             │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

**Code Implementation**:
```java
public static List<Balance> simplify(List<User> users) {
    List<Balance> result = new ArrayList<>();

    // Calculate net balance for each user
    Map<User, Double> netBalances = calculateNetBalances(users);

    // Priority queues for creditors (max heap) and debtors (min heap)
    PriorityQueue<UserAmount> creditors = new PriorityQueue<>(
        (a, b) -> Double.compare(b.amount, a.amount));  // Max heap
    PriorityQueue<UserAmount> debtors = new PriorityQueue<>(
        Comparator.comparingDouble(ua -> ua.amount));   // Min heap (most negative first)

    // Separate users into creditors and debtors
    for (Map.Entry<User, Double> entry : netBalances.entrySet()) {
        if (entry.getValue() > 0.01) {
            creditors.add(new UserAmount(entry.getKey(), entry.getValue()));
        } else if (entry.getValue() < -0.01) {
            debtors.add(new UserAmount(entry.getKey(), entry.getValue()));
        }
    }

    // Greedy matching
    while (!creditors.isEmpty() && !debtors.isEmpty()) {
        UserAmount creditor = creditors.poll();
        UserAmount debtor = debtors.poll();

        double settleAmount = Math.min(creditor.amount, Math.abs(debtor.amount));
        result.add(new Balance(debtor.user, creditor.user, settleAmount));

        // Put back remaining amounts
        if (creditor.amount - settleAmount > 0.01) {
            creditors.add(new UserAmount(creditor.user, creditor.amount - settleAmount));
        }
        if (Math.abs(debtor.amount) - settleAmount > 0.01) {
            debtors.add(new UserAmount(debtor.user, debtor.amount + settleAmount));
        }
    }

    return result;
}
```

---

## Interview Talking Points

### When Asked "Design Splitwise"

**1. Start with Requirements Clarification** (2-3 min)
```
Questions to ask:
- "Should we support groups, or just direct expenses between users?"
- "What split types do we need? Equal, exact amount, percentage?"
- "Do we need multi-currency support?"
- "Should we optimize the number of settlements (debt simplification)?"
- "Do we need real-time notifications?"
```

**2. Define Core Entities** (2-3 min)
```
User     - id, name, email, balances (Map<userId, amount>)
Group    - id, name, members, expenses
Expense  - id, amount, paidBy, splits, type
Split    - user, amount (abstract class with subclasses)
Balance  - fromUser, toUser, amount
```

**3. Explain Key Design Decisions** (5-7 min)

| Decision | Pattern | Justification |
|----------|---------|---------------|
| Different split calculations | Strategy Pattern | Open/Closed - add new types easily |
| Object creation | Factory Pattern | Centralize validation logic |
| Single entry point | Singleton + Facade | Simplify API, manage dependencies |
| Notifications | Observer Pattern | Decouple notification from core logic |

**4. Walk Through a Flow** (3-4 min)
```
"Let me trace through adding an expense:
1. Client calls manager.addExpense()
2. Factory creates Expense with Strategy for split calculation
3. Strategy validates and calculates split amounts
4. BalanceService updates all user balances
5. Observer pattern notifies notification service
6. Activity is recorded for the feed"
```

**5. Discuss Trade-offs** (2-3 min)
```
- In-memory storage vs Database (chose in-memory for demo)
- Individual balances vs Simplified debts (track both)
- Push vs Pull for notifications (implemented observer for push)
```

### Common Follow-up Questions

**Q: How would you handle concurrent expense additions?**
```
A: Use synchronization in BalanceService when updating user balances.
   In production, use database transactions with row-level locking.

   synchronized void updateBalancesForExpense(Expense expense) {
       // Update balances atomically
   }
```

**Q: How would you scale this system?**
```
A:
1. Database sharding by user_id
2. Read replicas for balance queries
3. Message queue for async notifications
4. Cache frequently accessed balances in Redis
5. Separate microservices for Users, Expenses, Notifications
```

**Q: How would you implement currency conversion?**
```
A: (Already implemented!)
- Store expense in original currency
- Convert on-demand when displaying
- Use CurrencyConverter utility with exchange rates
- Can integrate with external API for real-time rates
```

**Q: How does debt simplification work?**
```
A: Greedy algorithm using net balances:
1. Calculate net balance per user (positive=creditor, negative=debtor)
2. Use priority queues to match max creditor with max debtor
3. Continue until all settled
4. Results in minimum transactions

Time: O(n log n) where n = number of users
```

---

## Project Structure

```
src/splitwise/
├── oop.abstraction.Main.java                          # Demo application
├── README.md                          # This file
├── model/
│   ├── User.java                      # User entity
│   ├── Group.java                     # Group entity
│   ├── Expense.java                   # Expense entity
│   ├── Balance.java                   # Balance value object
│   ├── Transaction.java               # Payment/settlement record
│   ├── Activity.java                  # Activity feed item
│   ├── ExpenseType.java               # Enum: EQUAL, EXACT, PERCENTAGE
│   ├── Currency.java                  # Enum: USD, EUR, INR, GBP, JPY
│   ├── ActivityType.java              # Enum for activities
│   ├── TransactionType.java           # Enum: PAYMENT, SETTLEMENT
│   └── split/
│       ├── Split.java                 # Abstract base class
│       ├── EqualSplit.java            # Equal split
│       ├── ExactSplit.java            # Exact amount split
│       └── PercentageSplit.java       # Percentage split
├── strategy/
│   ├── SplitStrategy.java             # Strategy interface
│   ├── EqualSplitStrategy.java        # Equal split algorithm
│   ├── ExactSplitStrategy.java        # Exact split validation
│   └── PercentageSplitStrategy.java   # Percentage calculation
├── factory/
│   ├── SplitFactory.java              # Creates splits and strategies
│   └── ExpenseFactory.java            # Creates validated expenses
├── service/
│   ├── UserService.java               # Interface
│   ├── UserServiceImpl.java           # Implementation
│   ├── GroupService.java              # Interface
│   ├── GroupServiceImpl.java          # Implementation
│   ├── ExpenseService.java            # Interface
│   ├── ExpenseServiceImpl.java        # Implementation + Observer
│   ├── BalanceService.java            # Interface
│   ├── BalanceServiceImpl.java        # Balance calculations
│   ├── CurrencyService.java           # Interface
│   ├── CurrencyServiceImpl.java       # Currency conversion
│   ├── TransactionService.java        # Interface
│   ├── TransactionServiceImpl.java    # Payment recording
│   ├── ActivityService.java           # Interface
│   └── ActivityServiceImpl.java       # Activity feed
├── manager/
│   └── SplitwiseManager.java          # Singleton facade
├── observer/
│   ├── ExpenseObserver.java           # Observer interface
│   ├── ActivityObserver.java          # Activity observer
│   └── NotificationService.java       # Concrete observer
├── exception/
│   ├── InvalidSplitException.java     # Split validation errors
│   ├── UserNotFoundException.java     # User not found
│   ├── GroupNotFoundException.java    # Group not found
│   └── InvalidCurrencyException.java  # Currency errors
└── util/
    ├── BalanceSimplifier.java         # Debt simplification
    └── CurrencyConverter.java         # Exchange rates
```

---

## How to Run

```bash
# Compile
javac -d out src/splitwise/**/*.java src/splitwise/*.java

# Run
java -cp out splitwise.oop.abstraction.Main
```

---

## Summary Cheat Sheet

| Concept | Implementation | File |
|---------|---------------|------|
| **Inheritance** | Split → EqualSplit, ExactSplit, PercentageSplit | model/split/*.java |
| **Abstraction** | Abstract Split class with validate() | model/split/Split.java |
| **Encapsulation** | Private fields, public getters | All model classes |
| **Polymorphism** | Split subclasses used interchangeably | ExpenseService |
| **Strategy** | SplitStrategy interface | strategy/*.java |
| **Factory** | SplitFactory, ExpenseFactory | factory/*.java |
| **Singleton** | SplitwiseManager | manager/SplitwiseManager.java |
| **Observer** | ExpenseObserver, NotificationService | observer/*.java |
| **SRP** | Separate services for each domain | service/*.java |
| **OCP** | Strategy pattern for extensibility | strategy/*.java |
| **LSP** | Split subclasses honor contract | model/split/*.java |
| **ISP** | Small focused interfaces | observer/*.java |
| **DIP** | Constructor injection | service/*Impl.java |

---

**Good luck with your interviews!** 🚀
