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