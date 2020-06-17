package com.ricky.plugin_gson

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.ricky.plugin_gson.inject.InjectGsonJar
import com.ricky.plugin_gson.pool.InjectClassPool
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Project

class GsonJarTransform extends Transform {

    public Project mProject

    GsonJarTransform(Project project) {
        mProject = project
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    String getName() {
        return "GsonJarTransform"
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        //一、全局配置class pool
        InjectClassPool.resetClassPool(mProject, transformInvocation)

        //二、处理jar和file
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider()
        // Transform的inputs有两种类型，一种是目录，一种是jar包
        for (TransformInput input : transformInvocation.getInputs()) {
            for (JarInput jarInput : input.getJarInputs()) {
                // name must be unique，or throw exception "multiple dex files define"
                def jarInputName = jarInput.name
                if (jarInputName.endsWith('.jar')) {
                    jarInputName = jarInputName.substring(0, jarInputName.length() - 4)
                }
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())

                //【拦截jar进行处理】
                File file = InjectGsonJar.inject(jarInput, transformInvocation.context, mProject)
                if (file == null) {
                    file = jarInput.file
                }
                //dest file
                File dest = outputProvider.getContentLocation(jarInputName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                FileUtils.copyFile(file, dest)
            }

            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                File dest = outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(directoryInput.file, dest)
            }
        }
    }
}