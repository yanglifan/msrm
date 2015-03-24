# Domain Model
## The type of time: Date or Java 8 DateTime

```java
try {
    new Object().wait();
    Thread.sleep(1000);
    Object element = blockingQueue.take();
    // Other blocked methods.
} catch(InterruptedException e) {
    // This means the current thread has been interrupted.
    // Sometimes we do not need to handle it. 
}
```