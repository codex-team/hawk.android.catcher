# Hawk Android catcher ![](https://jitpack.io/v/jitpack/maven-simple.svg?style=flat-square)
### Exception catcher

This library provides in-app errors catching and sending them to the [Hawk](https://hawk.so).  monitoring system.
You can also send errors, which you caught in **try-catch**
-----

### Connection
To connect the library, add the following code to your **build.gradle** config.
```
    allprojects {
        repositories {
            jcenter()
            maven { url "https://jitpack.io" }
        }
   }
   ...
   dependencies {
        compile 'com.github.codex-team:hawk.android:v2.0'
   }
```
### Example
For cather activation add following code to your application class (f.e. **UseSample**)

```java
public class UseSample extends Application {

    HawkExceptionCatcher exceptionCatcher;
    public void defineExceptionCather()
    {
        exceptionCatcher = new HawkExceptionCatcher(this,"your hawk token");
        try {
            exceptionCatcher.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        defineExceptionCather();
    }
}

```
**Input parameters** 

> **Context** - current application context

> **Token** - unique authorization key(f.e. 0927e8cc-f3f0-4ce4-aa27-916f0774af51). You can get token after hawk.so registration

**Output example:**
```json
{  
   "token":"your hawk token",
   "message":"java.lang.ArithmeticException: divide by zero",
   "stack":"java.lang.RuntimeException: Unable to start activity ComponentInfo{com.hawkandroidcatcher.akscorp.hawkandroidcatcher\/com.hawkandroidcatcher.akscorp.hawkandroidcatcher.SampleMainActivity}: java.lang.ArithmeticException: divide by zero",
   "brand":"Android",
   "device":"generic_x86",
   "model":"Android SDK built for x86",
   "product":"sdk_google_phone_x86",
   "SDK":"22",
   "release":"5.1.1"
   "screenSize":"1920*1080"
}
```

### Параметры вывода
> **message** - exception name

> **stack** - error's stack trace

> **brand** - vendor android device code

> **device** - device name

> **model** - device model

> **product** - common name of product

> **SDK** - SDK version

> **release** - android version

> **screen size** - device screen size

## Example  

Catching **UncheckedException**

```java
void myTask() {
	int d = 10 / 0;
}
...
myTask();
```
Caught exception will be send with **JSON** format 

Sending handled exceptions

```java
void myTask() {
    try {
        int d = 10 / 0;
    } catch(ArithmeticException e) {
        exceptionCatcher.log(e); 
        //This method sends an exception with JSON-format
    }
}
...
myTask();
```

Wherein, without using the function **log()** in the **try-catch**, the error won't be sent.

```java
void myTask() {
    try {
        int d = 10 / 0;
    } catch(ArithmeticException e) {
        e.printStackTrace();
        //The exception won't be sent
    }
}
...
myTask();
```
