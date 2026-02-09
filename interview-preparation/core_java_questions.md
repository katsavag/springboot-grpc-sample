What’s the difference between ArrayList and LinkedList? When would you use one over the other?
---

## ✅ Difference Between `ArrayList` and `LinkedList`

### **1. Internal Structure**

* **ArrayList** → Backed by a **dynamic array**.
* **LinkedList** → Doubly linked list (each node stores data + references to next/previous).

---

### **2. Access**

* **ArrayList** → Fast random access (`O(1)`) because it uses an index over an array.
* **LinkedList** → Sequential access (`O(n)`), you must traverse nodes one by one.

---

### **3. Insertions/Deletions**

* **ArrayList**

    * Insertion at end: amortized `O(1)` (may need resizing).
    * Insertion in middle/start: `O(n)` (requires shifting elements).
* **LinkedList**

    * Insertion/removal at start or middle (given node reference): `O(1)`.
    * But finding the position first is `O(n)`.

---

### **4. Memory Overhead**

* **ArrayList** → Stores only the actual objects + array overhead.
* **LinkedList** → Extra memory for node pointers (prev + next).

---

### **5. Cache Locality**

* **ArrayList** → Better cache performance (contiguous memory).
* **LinkedList** → Poor cache locality (nodes scattered in memory).

---

## 🚀 When to Use Which?

* **Use `ArrayList` when:**

    * You need **fast random access** (by index).
    * Insertions/deletions are mostly at the **end**.
    * Memory efficiency is important.

* **Use `LinkedList` when:**

    * Your application requires **frequent insertions/deletions in the middle/start**.
    * You don’t need much random access.

---

⚡ Example in financial systems (like Revolut):

* `ArrayList` → Great for storing a large batch of transactions where you often **iterate or access by index**.
* `LinkedList` → Might be useful in implementing a queue where frequent **insert/remove at both ends** is needed.

---

# How would you make a List read-only?

---

## ✅ How to Make a `List` Read-Only in Java

### **1. Using `Collections.unmodifiableList`**

```java
import java.util.*;

public class ReadOnlyListExample {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("A");
        list.add("B");

        List<String> readOnlyList = Collections.unmodifiableList(list);

        System.out.println(readOnlyList); // [A, B]

        // readOnlyList.add("C"); // Throws UnsupportedOperationException
    }
}
```

* The wrapper prevents modifications (`add`, `remove`, `clear`, etc.).
* But ⚠️ if the underlying list is modified, the read-only view **will reflect those changes**.

---

### **2. Using `List.of()` (Java 9+)**

```java
List<String> readOnlyList = List.of("A", "B", "C");
```

* Immutable by design — any modification attempt throws `UnsupportedOperationException`.
* Unlike the wrapper approach, the underlying list cannot change.

---

### **3. Using Streams (Java 10+)**

```java
List<String> readOnlyList = 
    List.copyOf(Arrays.asList("A", "B", "C"));
```

* Returns an **immutable copy** of the given collection.

---

## 🚀 Interview-style Answer

> You can make a `List` read-only by wrapping it with `Collections.unmodifiableList()`, which blocks modifications but still reflects changes to the original list.
> From Java 9 onwards, you can also use `List.of()` or `List.copyOf()`, which create truly immutable lists.

---

# Given a List<String>, how do you remove duplicates while preserving order?

---

Great one ⚡ — this tests both **Collections API knowledge** and problem-solving.

---

## ✅ Removing Duplicates from a `List<String>` While Preserving Order

### **1. Use `LinkedHashSet`**

```java
import java.util.*;

public class RemoveDuplicatesExample {
    public static void main(String[] args) {
        List<String> list = Arrays.asList("A", "B", "A", "C", "B", "D");

        List<String> result = new ArrayList<>(new LinkedHashSet<>(list));

        System.out.println(result); // [A, B, C, D]
    }
}
```

* `LinkedHashSet` keeps **insertion order** and removes duplicates.

---

### **2. Use Streams (Java 8+)**

```java
List<String> result = list.stream()
                          .distinct()
                          .toList(); // Java 16+, or use .collect(Collectors.toList()) in earlier versions
```

* `distinct()` uses `equals()` for comparison.
* Preserves the encounter order of the stream.

---

### **3. Manual Approach (for clarity in interviews)**

```java
List<String> result = new ArrayList<>();
Set<String> seen = new HashSet<>();

for (String s : list) {
    if (seen.add(s)) {   // add() returns false if element already exists
        result.add(s);
    }
}
System.out.println(result); // [A, B, C, D]
```

* More verbose, but shows understanding of `Set` + `List` interaction.

---

## 🚀 Interview-Style Answer

> To remove duplicates while preserving order, the easiest way is to put the list into a `LinkedHashSet` and then back into a list. From Java 8 onwards, you can also use `stream().distinct()`. Both approaches rely on `equals()` for duplicate detection.

---

# Checked vs Unchecked Exceptions in Java

## Key Differences

| Feature | Checked Exceptions | Unchecked Exceptions |
|---------|-------------------|---------------------|
| Compile-time verification | Yes | No |
| Exception hierarchy | Subclasses of `Exception` (excluding `RuntimeException`) | Subclasses of `RuntimeException` or `Error` |
| Must be declared | Yes, with `throws` clause | No |
| Must be handled | Yes, with try-catch or propagated | Optional |
| Use case | Expected recoverable conditions | Programming errors/bugs |

## Checked Exceptions

Checked exceptions represent conditions that a reasonable application might want to catch and handle. They:

- Must be either caught using a `try-catch` block or declared in the method signature using the `throws` clause
- Are checked by the compiler at compile-time
- Extend `Exception` but not `RuntimeException`
- Force explicit error handling

**Examples**: `IOException`, `SQLException`, `ClassNotFoundException`

```java
// Example of handling checked exception
public void readFile(String path) {
    try {
        FileReader reader = new FileReader(path);
        // Read operations...
        reader.close();
    } catch (IOException e) {
        // Handle the exception
        System.err.println("Error reading file: " + e.getMessage());
    }
}

// Example of declaring checked exception
public void readFile(String path) throws IOException {
    FileReader reader = new FileReader(path);
    // Read operations...
    reader.close();
}
```


## Unchecked Exceptions

Unchecked exceptions represent conditions that typically reflect errors in program logic. They:

- Do not need to be caught or declared
- Are not checked at compile-time
- Extend `RuntimeException` or `Error`
- Usually indicate programming bugs or catastrophic conditions

**Examples**: `NullPointerException`, `ArrayIndexOutOfBoundsException`, `IllegalArgumentException`

```java
// Example of an unchecked exception
public int divide(int a, int b) {
    // No need to handle or declare ArithmeticException
    return a / b;  // Throws ArithmeticException if b is 0
}
```


## Best Practices

1. **Use checked exceptions** for recoverable conditions that the caller might reasonably be expected to handle (e.g., file not found, network timeout)

2. **Use unchecked exceptions** for programming errors that should not occur (e.g., null pointer, illegal state)

3. **Don't overuse checked exceptions** as they can lead to cluttered code with many try-catch blocks

4. **Create custom exceptions** when standard exceptions don't accurately describe your error condition

5. **Document all exceptions** that your methods might throw, even unchecked ones

## Interview Tips

- Be ready to discuss a scenario where you would choose one over the other
- Understand the performance implications (try-catch blocks have minimal impact when not triggered)
- Be aware that different languages handle exceptions differently (some only have unchecked exceptions)
- Mention that excessive use of checked exceptions can lead to "exception swallowing" where developers catch and ignore exceptions they don't want to handle properly

# Java Packages and Default Package

## What is a Package in Java?

A package in Java is a namespace mechanism used to:

1. **Organize classes**: Group related classes and interfaces together
2. **Prevent naming conflicts**: Classes with the same name can exist in different packages
3. **Control access**: Provide access control at package level
4. **Enable modular programming**: Help with code organization and reuse

Packages are declared using the `package` statement at the top of source files:

```java
package com.example.myapp;

public class MyClass {
    // Class implementation
}
```


## The Default Package

The default package is the unnamed package that classes belong to when no package declaration is provided in the source file.

```java
// No package declaration = default package
public class MyClass {
    // Class implementation
}
```


## Characteristics of the Default Package

1. **No Name**: It has no name and cannot be imported using an import statement

2. **Limited Scope**: Classes in the default package:
  - Can only be accessed by other classes in the default package
  - Cannot be imported by classes in named packages

3. **Limited Protection**: Package-private members are accessible to all classes in the default package

4. **Not Accessible via Reflection**: Some reflection operations have limitations with default package classes

## Why You Should Avoid the Default Package

The default package should be avoided in real applications for these reasons:

1. **No Isolation**: All classes in the default package share the same namespace, leading to potential naming conflicts

2. **No Imports**: Classes in named packages cannot import or use classes from the default package

3. **Maintainability Issues**: As projects grow, organization becomes difficult without proper package structure

4. **Limited Access Control**: Package-private access modifiers become less meaningful

5. **Not Supported in Modules**: Java's module system (introduced in Java 9) doesn't support the default package

## Best Practices

1. **Always Use Named Packages**: Even for small applications
```java
package com.mycompany.project;
```


2. **Follow Naming Conventions**: Use reversed domain name pattern
```java
package org.example.project.module;
```


3. **Organize Logically**: Group related classes into packages
```
com.example.project.model
   com.example.project.controller
   com.example.project.util
```


4. **Package by Feature**: Consider organizing by feature rather than by layer for large applications

The default package is primarily useful for quick experiments, small test programs, or learning exercises, but should not be used in production code.

# Polymorphism in Java
Polymorphism is one of the core principles of object-oriented programming in Java. The word "polymorphism" comes from Greek, meaning "many forms." In programming, it refers to the ability of an object to take on many forms.
## Key Concepts of Polymorphism
### 1. Method Overriding
This is when a subclass provides a specific implementation of a method that is already defined in its parent class.
``` java
class Animal {
    public void makeSound() {
        System.out.println("Some generic sound");
    }
}

class Dog extends Animal {
    @Override
    public void makeSound() {
        System.out.println("Woof!");
    }
}

class Cat extends Animal {
    @Override
    public void makeSound() {
        System.out.println("Meow!");
    }
}
```
### 2. Method Overloading
This occurs when multiple methods in the same class have the same name but different parameters.
``` java
class Calculator {
    // Method with two int parameters
    public int add(int a, int b) {
        return a + b;
    }
    
    // Method with three int parameters
    public int add(int a, int b, int c) {
        return a + b + c;
    }
    
    // Method with two double parameters
    public double add(double a, double b) {
        return a + b;
    }
}
```
## Types of Polymorphism in Java
### 1. Compile-time Polymorphism (Static Binding)
Method overloading is a type of compile-time polymorphism. The compiler determines which method to call based on the method signature.
### 2. Runtime Polymorphism (Dynamic Binding)
Method overriding is an example of runtime polymorphism. The JVM determines which method to call at runtime based on the actual object type.
``` java
public class Main {
    public static void main(String[] args) {
        // Runtime polymorphism
        Animal myPet = new Dog(); // Dog object referred by Animal reference
        myPet.makeSound();        // Outputs "Woof!" not "Some generic sound"
        
        myPet = new Cat();        // Now myPet refers to a Cat object
        myPet.makeSound();        // Outputs "Meow!"
    }
}
```
## Benefits of Polymorphism
1. **Code Reusability**: Use the same method name across different classes.
2. **Flexibility**: Write code that can work with objects of multiple types.
3. **Extensibility**: Easily extend functionality without modifying existing code.
4. **Maintainability**: Changes to one implementation don't affect others.

## Real-world Example
Polymorphism is used extensively in frameworks like Spring, Hibernate, or Android SDK, where you often override methods from parent classes or interfaces to provide your custom behavior.
``` java
// In Android development
public class MyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Your custom implementation
    }
}
```
Polymorphism is a powerful concept that makes your code more flexible and maintainable by allowing you to work with objects at a more abstract level.

# Interface vs Abstract Class in Java

## Key Differences

### 1. Implementation and Methods
- **Interface**: Can contain abstract, default, static, and private methods (since Java 8/9)
- **Abstract Class**: Can contain both abstract and concrete methods with any access modifier

### 2. Inheritance
- **Interface**: Classes can implement multiple interfaces
- **Abstract Class**: Classes can extend only one abstract class (single inheritance)

### 3. Variables
- **Interface**: Only constants (public static final)
- **Abstract Class**: Can have instance variables with any access modifier

### 4. Constructors
- **Interface**: Cannot have constructors
- **Abstract Class**: Can have constructors that are called during subclass instantiation

### 5. Purpose
- **Interface**: Defines a contract ("can do" relationship)
- **Abstract Class**: Provides a common base for related classes ("is-a" relationship)

## When to Use

**Use an Interface when:**
- You need multiple inheritance
- You want unrelated classes to implement common behavior
- You want to define what classes can do, not how they do it

**Use an Abstract Class when:**
- You need to share code among closely related classes
- You need instance variables and non-public methods
- You need constructors
- You want to provide a partial implementation as a template

Despite the narrowing gap between them (with default methods in Java 8+), they still serve different design purposes in modern Java.

# Explain the difference between Comparable and Comparator interfaces in Java, and when you would choose one over the other.
## Answer
- **Comparable** (`java.lang.Comparable`)
  - Provides natural ordering for a class
  - Implemented by the class itself via `compareTo(T o)` method
  - Allows only one sorting sequence per class
  - Used when there's a clear default way to sort objects

- **Comparator** (`java.util.Comparator`)
  - Defines custom ordering strategies
  - Implemented in separate classes via `compare(T o1, T o2)` method
  - Allows multiple sorting sequences for the same class
  - Used when sorting needs to be independent of the class or when multiple sort criteria are needed

Choose Comparable for defining a class's inherent ordering, and Comparator when you need flexibility or can't modify the original class.
