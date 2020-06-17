package com.ricky.plugin_gson

import org.gradle.api.Plugin
import org.gradle.api.Project

class GsonPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        // add dependencies
        project.dependencies.add("api",
                "com.xiaoe.base:xe-plugin-gson:1.0.0")

        // register transform
        project.android.registerTransform(new GsonJarTransform(project))
    }
}