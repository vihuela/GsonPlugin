package com.ricky.plugin_gson.inject

import com.android.build.api.transform.Context
import com.android.build.api.transform.JarInput
import com.ricky.plugin_gson.inject.adapter.InjectArrayTypeAdapter
import com.ricky.plugin_gson.inject.adapter.InjectCollectionTypeAdapterFactory
import com.ricky.plugin_gson.inject.adapter.InjectGsonTypeAdapter
import com.ricky.plugin_gson.inject.adapter.InjectMapTypeAdapterFactory
import com.ricky.plugin_gson.inject.adapter.InjectReflectiveTypeAdapterFactory
import com.ricky.plugin_gson.inject.adapter.InjectTypeAdapters
import com.ricky.plugin_gson.utils.Compressor
import com.ricky.plugin_gson.utils.Decompression
import com.ricky.plugin_gson.utils.StrongFileUtil
import javassist.NotFoundException
import org.gradle.api.Project

class InjectGsonJar {

  public static File inject(JarInput jarInput, Context context, Project project) throws NotFoundException {
    def jarInputName = jarInput.name
    File jarFile = jarInput.file
    if (!jarFile.name.startsWith("gson") && !jarInputName.startsWith("com.google.code.gson:gson")) {
      return null
    }
    println("--------------GsonPlugin: inject gson jar start--------------")
    //原始jar path
    String srcPath = jarFile.getAbsolutePath()

    //原始jar解压后的tmpDir
    String tmpDirName = jarFile.name.substring(0, jarFile.name.length() - 4)
    String tmpDirPath = context.temporaryDir.getAbsolutePath() + File.separator + tmpDirName

    //目标jar path
    String targetPath = context.temporaryDir.getAbsolutePath() + File.separator + jarFile.name

    //解压
    Decompression.uncompress(srcPath, tmpDirPath)

    //修改
    InjectReflectiveTypeAdapterFactory.inject(tmpDirPath)
    InjectMapTypeAdapterFactory.inject(tmpDirPath)
    InjectArrayTypeAdapter.inject(tmpDirPath)
    InjectCollectionTypeAdapterFactory.inject(tmpDirPath)
    InjectTypeAdapters.inject(tmpDirPath)
    InjectGsonTypeAdapter.inject(tmpDirPath)

    //重新压缩
    Compressor.compress(tmpDirPath, targetPath)

    //删除临时目录
    StrongFileUtil.deleteDirPath(tmpDirPath)

    println("--------------GsonPlugin: inject gson jar end--------------")

    //返回目标jar
    File targetFile = new File(targetPath)
    if (targetFile.exists()) {
      return targetFile
    }
    return null
  }
}
