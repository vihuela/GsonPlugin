package com.ricky.plugin_gson.inject.adapter

import com.ricky.plugin_gson.pool.InjectClassPool
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod

public class InjectTypeAdapters {

  public static void inject(String dirPath) {

    ClassPool classPool = InjectClassPool.getClassPool()

    File dir = new File(dirPath)
    if (dir.isDirectory()) {
      dir.eachFileRecurse { File file ->
        if (file.name.startsWith("TypeAdapters\$")) {
          //TypeAdapters$1.class 取$1
          String innerClassName = file.name.substring(13, file.name.length() - 6)
          CtClass ctClass = classPool.getCtClass("com.google.gson.internal.bind.TypeAdapters\$" + innerClassName)
          //only deal type Number Boolean String
          CtMethod[] methods = ctClass.declaredMethods
          boolean isModified = false
          String logReturnTypeName = ""
          for (CtMethod ctMethod : methods) {
            if ("read".equals(ctMethod.name)) {
              String returnTypeName = ctMethod.getReturnType().name
              if ("java.lang.Number".equals(returnTypeName)
                  || "java.lang.Boolean".equals(returnTypeName)
                  || "java.lang.String".equals(returnTypeName)) {
                logReturnTypeName = returnTypeName
                CtClass etype = classPool.get("java.lang.Exception")
                ctMethod.addCatch("{com.ricky.plugin_gson_sdk.GsonPluginUtil.onJsonTokenParseException(\$1, \$e); return null;}", etype)
                isModified = true
              }
            }
          }
          if (isModified) {
            ctClass.writeFile(dirPath)
            println("GsonPlugin: inject TypeAdapters success " + logReturnTypeName)
          }
          ctClass.detach()
        }
      }
    }
  }
}
