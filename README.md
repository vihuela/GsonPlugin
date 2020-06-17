### 描述

使用代码插桩，优化Gson处理后台弱数据类型字段不规范的问题

## 来由

后台如果是弱数据类型，如我司：php，那么数据返回就会存在不可控，因为Android 使用Gson时为强字段类似约束，就会存在：<br>
定义的是**Int**，返回**String**<br>
定义的是**Obejct**，返回**Array**<br>
定义的是**Boolean**，返回的是**String**<br>
...

### 分析
我们拿集合解析举例：
GSON会使用**CollectionTypeAdapterFactory** 处理 集合解析

73行

```java
@Override public Collection<E> read(JsonReader in) throws IOException {
  ...
  in.beginArray();
 ...
}
```

```java
public void beginArray() throws IOException {
  ...
    throw new IllegalStateException("Expected BEGIN_ARRAY but was " + peek() + locationString());
  }
}
```

可以看到若in.beginArray();解析异常则**中断整个解析**<br>

其它解析大同小异，如

**ReflectiveTypeAdapterFactory**处理对象解析<br>**TypeAdapters**处理基本数据类型解析

### 解决

GsonBuilder提供了自定义的解析工厂**registerTypeAdapter**,如：<br>

```
val IntDeser = JsonDeserializer<Int> { json, typeOfT, context ->
    try {
        json?.asInt ?: 0
    } catch (e: NumberFormatException) {
        0
    }
}
registerTypeAdapter(Int::class.java, IntDeser)
```

但是目前无法处理对象类型，所以考虑使用代码插桩：<br>

我们可以在解析工厂类的read方法之前之前**插入代码**，若不满足要求则不进去read方法，同时跳过当前解析，则可以**解决字段解析抛出异常中断整个解析链**的问题



### 使用

根build.gradle

```css
buildscript {
   
    repositories {
       ...
        maven { url 'https://www.jitpack.io' }
    }
    dependencies {
      ...
        classpath 'com.github.vihuela:GsonPlugin:1.3'

    }
}
```

app的build.gradle

```
apply plugin: 'com.ricky.plugin.transform.gson'

dependencies {
   ...
    implementation 'com.google.code.gson:gson:2.8.6'

}
```
