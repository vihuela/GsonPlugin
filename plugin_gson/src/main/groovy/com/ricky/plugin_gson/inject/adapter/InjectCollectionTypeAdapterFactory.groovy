package com.ricky.plugin_gson.inject.adapter

import com.ricky.plugin_gson.pool.InjectClassPool
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod

public class InjectCollectionTypeAdapterFactory {

  public static void inject(String dirPath) {

    ClassPool classPool = InjectClassPool.getClassPool()

    File dir = new File(dirPath)
    if (dir.isDirectory()) {
      dir.eachFileRecurse { File file ->
        if ("CollectionTypeAdapterFactory.class".equals(file.name)) {
          CtClass ctClass = classPool.getCtClass("com.google.gson.internal.bind.CollectionTypeAdapterFactory\$Adapter")
          CtMethod ctMethod = ctClass.getDeclaredMethod("read")
          //处理com.google.gson.internal.bind.ReflectiveTypeAdapterFactory 132行对非基础类型直接赋予null值
          ctMethod.insertBefore("     if (!com.ricky.plugin_gson_sdk.GsonPluginUtil.checkJsonToken(\$1, com.google.gson.stream.JsonToken.BEGIN_ARRAY)) {\n" +
              "        return Collections.emptyList();\n" +
              "      }")
          ctClass.writeFile(dirPath)
          ctClass.detach()
          println("GsonPlugin: inject CollectionTypeAdapterFactory success")
        }
      }
    }
  }
}
