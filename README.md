### 描述

使用代码插桩，优化Gson处理后台弱数据类型字段不规范的问题

## 来由

后台如果是弱数据类型，如：PHP，那么数据返回就会存在不可控，尤其后台是长周期大项目，几乎不可能为了App兼容<br>
Android 使用Gson时为强字段类型约束，就会存在：<br>
定义的是**Int**，返回**String**<br>
定义的是**Obejct**，返回**Array**<br>
定义的是**Boolean**，返回的是**String**<br>
定义的是**String**，返回的是**null**<br>
...
大部分情况是某个字段解析失败，导致整个Json解析失败，界面无法渲染<br>
虽然可以使用JsonElement接收，也可以使用String接收，然后在判空、判断类型取值，但是**存在大量重复代码**

### 分析
我们拿集合解析举例：
GSON(2.8.6)会使用**CollectionTypeAdapterFactory** 处理 集合解析

源码73行

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
拦截Json解析错误

```
GsonPluginUtil.setListener { exception, invokeStack ->
            Log.e("gson:", exception)
        }
```
