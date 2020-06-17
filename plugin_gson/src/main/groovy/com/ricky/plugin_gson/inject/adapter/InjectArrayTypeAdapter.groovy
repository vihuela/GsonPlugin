package com.ricky.plugin_gson.inject.adapter

import com.ricky.plugin_gson.pool.InjectClassPool
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod

public class InjectArrayTypeAdapter {

  public static void inject(String dirPath) {

    ClassPool classPool = InjectClassPool.getClassPool()

    File dir = new File(dirPath)
    if (dir.isDirectory()) {
      dir.eachFileRecurse { File file ->
        if ("ArrayTypeAdapter.class".equals(file.name)) {
          CtClass ctClass = classPool.getCtClass("com.google.gson.internal.bind.ArrayTypeAdapter")
          CtMethod ctMethod = ctClass.getDeclaredMethod("read")
          ctMethod.insertBefore("     if (!com.ricky.plugin_gson_sdk.GsonPluginUtil.checkJsonToken(\$1, com.google.gson.stream.JsonToken.BEGIN_ARRAY)) {\n" +
              "        return null;\n" +
              "      }")
          ctClass.writeFile(dirPath)
          ctClass.detach()
          println("GsonPlugin: inject ArrayTypeAdapter success")
        }
      }
    }
  }
}
