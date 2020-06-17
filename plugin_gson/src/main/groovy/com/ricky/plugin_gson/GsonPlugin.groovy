package com.ricky.plugin_gson

import org.gradle.api.Plugin
import org.gradle.api.Project

class GsonPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        // add dependencies
        project.repositories.maven {
            url "https://jitpack.io"
        }
        project.dependencies.add("api",
                "com.github.vihuela:GsonPluginSdk:1.0")

        // register transform
        project.android.registerTransform(new GsonJarTransform(project))
    }
}