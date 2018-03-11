# 14 多线程
## 14.1 什么是线程
skipped
## 14.2 中断线程
todo
## 14.3 线程状态
todo
## 14.4 线程属性
todo
## 14.5 同步
### 14.5.1 竞争条件的一个例子

### 14.5.2 竞争条件详解
[unsynch.Bank](./unsynch/Bank.java)无法正确工作,假设两个线程同时执行指令`accounts[to] += amount`;问题在于这不是原子操作，　该指令可能被处理如下：
1. 将`accounts[to]`加载到寄存器
2. 增加amount
3. 将结果写回`accounts[to]`

### 14.5.3 锁对象
JavaSE5.0引入了ReentrantLock类，用ReentrantLock保护代码块的基本结构如下
```java
myLock.lock()
try {
    // critical section
} finally {
    myLock.unlock(); // make sure the lock is unlocked even if an exception is thrown
}
```
(完整示例查看[synch.Bank](./synch/Bank.java))\
注意每一个Bank对象都有自己的ReentrantLock对象。如果两个线程试图访问同一个Bank对象，那么锁以串行的方式提供服务。但是，如果两个线程访问不同的Bank对象，每一个线程得到不同的锁对象，两个线程都不会发生阻塞。

### 14.5.4 条件对象
通常，线程进入临界区，却发现在某一个条件满足之后它才能执行。要使用一个条件对象来管理那些已经获得了一个锁但是却不能做有用工作的线程。\
在示例代码中，如果transfer方法发现余额不足，它调用`condition.await()`, 此时当前线程被阻塞，并放弃了锁。希望这样可以使得另一个线程可以进行增加余额的操作。\
当另一线程转账时，它应该调用`condition.signalAll()`，这一调用重新激活这一条件而等待的所有线程。同时，它们将试图重新进入该对象，一旦锁成为可用的，它们中的某个将从await调用返回，获得该锁并从阻塞的地方继续执行。\
此时，线程应该再次测试该条件。由于无法确保该条件被满足, signalAll方法仅仅是通知正在等待的线程：此时有可能已经满足条件，值得再次去检测该条件。通常，对await的调用应该在如下形式的循环体中:
```java
while(!(ok to proceed))
    condition.await();
```
至关重要的是最终需要某个线程调用signalAll方法。当一个线程调用await时，它没有办法重新激活自身。它寄希望于其它线程。如果没有其它线程来重新激活等待的线程，它就永远不再执行了。这将导致令人不愉快的死锁（deadlock）现象。如果所有其它线程被阻塞，最后一个活动线程在解除其它线程的阻塞状态之前就调用了await方法，那么它也被阻塞。没有任何线程可以解除其它线程的阻塞，那么该程序就挂起了。

### 14.5.5 synchronized 关键字
有关锁和条件的关键之处:
- 锁用来保护代码片段，任何时刻只能有一个线程执行被保护的代码
- 锁可以管理试图进入被保护代码段的线程
- 锁可以拥有一个或多个相关的条件对象
- 每个条件对象管理那些已经进入被保护的代码段但是还不能运行的线程
java中的每一个对象都有一个内部锁，如果一个方法用synchronized关键字声明，那么对象的锁保护整个方法。也就是说，要调用该方法，线程必须获得内部的对象锁。换句话说，
```java
public synchronized void method(){
    // method body
}
```
等价于
```java
public void method(){
    this.intrinsicLock.lock();
    try {
        // method body
    } finally {
        this.intrinsicLock.unlock();
    }
}
```
内部对象锁只有一个相关条件。wait方法添加一个线程到等待集中，notifyAll/notify方法解除等待线程的阻塞状态。换句话说，调用instance.wait()或instance.notifyAll()等价于
```java
intrinsicCondition.await();
intrinsicCondition.signalAll();
```
将静态方法声明为synchronized也是合法的，如果调用这种方法，该方法获得相关的类对象的内部锁。\
Conclusion:
- 优先选择java.util.concurrent包中的解决方案，e.g.阻塞队列
- 其次尽量选择synchronized关键字 
- 如果需要Lock/Condition结果提供的独有特性时，才使用Lock/Condition

### 14.5.6 同步阻塞
skipped
### 14.5.7 监视器概念
skipped
### 14.5.8 Volatile field
Volatile关键字为实例域提供了一种免锁机制，如果声明一个域为volatile,那么编译器和虚拟机就知道该域是可能被另一个线程并发更新的。(volatile确保虚拟机不去使用缓冲区的值)
### 14.5.9 final variable
skipped
### 14.5.10 Atomicity
java.util.concurrent.atomic包中有很多类使用了很高效的机器级指令（而不是使用锁）来保证其它操作的原子性
### 14.5.11 DeadLock
todo
### 14.5.12 ThreadLocal
todo
### 14.5.13 锁测试与超时
todo
### 14.5.14 读/写锁
todo
### 14.5.14 为什么弃用stop和suspend方法
todo
## 14.6 阻塞队列
todo
## 14.7 线程安全的集合
todo
## 14.8 Callable与Future
todo
## 14.9 执行器
todo
## 14.10 同步器
todo
## 14.11 线程与swing
skipped
