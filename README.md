# HawkCatcher

### Сборщик ошибок 
Сбор ошибок непроверяемых ошибок во время работы приложение и отправки их в на сервер
Так же возможность отправлять отловленные в **try-catch** ошибки

-----


Подключение
------------
Добавить в Ваш класс **Application** следующий код

```java
public class UseSample extends Application {

    HawkExceptionCatcher exceptionCatcher;
    public void defineExceptionCather()
    {
        exceptionCatcher = new HawkExceptionCatcher(this,"0927e8cc-f3f0-4ce4-aa27-916f0774af51");
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
Входные параметры 


> **Context** - текущий context приложения

> **Token** - уникальный ключ авторизации
Примеры вывода:

```json
{  
   "token":"0927e8cc-f3f0-4ce4-aa27-916f0774af51",
   "message":"java.lang.ArithmeticException: divide by zero",
   "stack":"java.lang.RuntimeException: Unable to start activity ComponentInfo{com.hawkandroidcatcher.akscorp.hawkandroidcatcher\/com.hawkandroidcatcher.akscorp.hawkandroidcatcher.SampleMainActivity}: java.lang.ArithmeticException: divide by zero",
   "brand":"Android",
   "device":"generic_x86",
   "model":"Android SDK built for x86",
   "product":"sdk_google_phone_x86",
   "SDK":"22",
   "release":"5.1.1",
   "incremental":"4212452"
}
```
### Параметры вывода
> **message** - название самой ошибки

> **stack** - стек ошибки

> **brand** - код поставщика android устройства

> **device** - имя устройства в рамках индустриального дизайна(?)

> **model** - общеизвестное имя android устройства

> **product** - общее наименование продукции

> **SDK** - версия SDK

> **release** - версия андроида

> **incremental** - 

## Пример работы  

Отлавливание **UncheckedException**

```java
void myTask() {
	int d = 10 / 0;
}
...

myTask();
```
Отловленная ошибка будет соотвествовать формату json выше

Отправка отловленных исключений

```java
void myTask() {
	try {
		int d = 10 / 0;
	} catch(ArithmeticException e) {
		exceptionCatcher.log(e); 
		//Данный метод форматирует исключение в JSON и отправляет его
	}
}
...

myTask();
```
При этом ошибки, отловленные в **try-catch** без использовния функции **log()** отправлены не будут

```java
void myTask() {
	try {
		int d = 10 / 0;
	} catch(ArithmeticException e) {
		e.printStackTrace();
		//ошибка отправлена не будет
	}
}
...
myTask();
```
