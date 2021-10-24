# Hawk Android catcher ![](https://jitpack.io/v/jitpack/maven-simple.svg?style=flat-square)
### Exception catcher

This library provides in-app errors catching and sending them to the [Hawk](https://hawk.so).  monitoring system.
You can also send errors, which you caught in **try-catch**.

**Minimum required Android SDK 16**

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
        compile 'com.github.codex-team:hawk.android.catcher:v3.0'
   }
```
### Example
For cather activation add following code to your manifest (f.e. **UseSample**)

```xml
<manifest>
    <application>
        <meta-data android:name="hawk_catcher_token" android:value="TOKEN"/>
    </application>

</manifest>
```

**Parameters**

> **TOKEN** - unique authorization key. You can get token after garage.hawk.so registration

and to you application class

```java
public class UseSampleApp extends Application {

    public HawkExceptionCatcher exceptionCatcher;
    public void defineExceptionCather()
    {
        exceptionCatcher = new HawkExceptionCatcher(this);
        exceptionCatcher.start();
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
        UseSampleApp.exceptionCatcher.caught(e);
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
