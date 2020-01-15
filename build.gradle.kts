@file:Suppress("PropertyName")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.jfrog.bintray.gradle.BintrayExtension
import net.fabricmc.loom.task.RemapJarTask
import net.fabricmc.loom.task.RemapSourcesJarTask

val curseProjectId: String by project
val basePackage: String by project
val modJarBaseName: String by project
val modMavenGroup: String by project
val modVersion: String by project

val minecraft_version: String by project
val yarn_mappings: String by project
val loader_version: String by project
val fabric_version: String by project

plugins {
    java
    idea
    `maven-publish`
    signing
    id("com.jfrog.bintray") version "1.8.4"
    id("fabric-loom") version "0.2.6-SNAPSHOT"
    id("com.palantir.git-version") version "0.11.0"
    id("com.github.johnrengelman.shadow") version "5.2.0"
//    id("com.matthewprenger.cursegradle") version "1.2.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

base {
    archivesBaseName = modJarBaseName
}

repositories {
    maven(url = "https://maven.fabricmc.net/")
    jcenter()
}

version = modVersion
group = modMavenGroup

minecraft {
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft_version")
    mappings("net.fabricmc:yarn:$yarn_mappings")
    modImplementation("net.fabricmc:fabric-loader:$loader_version")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabric_version")

    modImplementation("me.shedaniel.cloth:config-2:2.6.6")
    modImplementation("io.github.prospector:modmenu:1.8.4+build.20")

    shadow("blue.endless:jankson:1.1.+")
    implementation("blue.endless:jankson:1.1.+")

    shadow("com.moandjiezana.toml:toml4j:0.7.+") {
        exclude(group = "com.google.code.gson", module = "gson")
    }
    implementation("com.moandjiezana.toml:toml4j:0.7.+")
}

val processResources = tasks.getByName<ProcessResources>("processResources") {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        filter { line -> line.replace("%VERSION%", "${project.version}") }
    }
}

val javaCompile = tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val jar = tasks.getByName<Jar>("jar") {
    from("LICENSE")
}

val shadowJar = tasks.getByName<ShadowJar>("shadowJar") {
    relocate("blue.endless.jankson", "$basePackage.shadowed.blue.endless.jankson")
    relocate("com.moandjiezana.toml", "$basePackage.shadowed.com.moandjiezana.toml")

    configurations = listOf(project.configurations["shadow"])
}

@Suppress("CAST_NEVER_SUCCEEDS", "UnstableApiUsage")
val remapJar = tasks.getByName<RemapJarTask>("remapJar") {
    this.dependsOn(shadowJar)
    (this.input as RegularFileProperty).set(shadowJar.archiveFile)
}

val remapSourcesJar = tasks.getByName<RemapSourcesJarTask>("remapSourcesJar")

bintray {
    user = System.getenv("BINTRAY_USER")
    key = System.getenv("BINTRAY_KEY")
    setPublications("mavenJava")
    override = true
    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = "autoconfig1u"
        name = "autoconfig1u"
        userOrg = "shedaniel"
        setLicenses("Apache-2.0")
        version.apply {
            name = modVersion
            vcsTag = modVersion
            githubRepo = "shedaniel/AutoConfig"
            websiteUrl = "https://github.com/shedaniel/AutoConfig"
            issueTrackerUrl = "https://github.com/shedaniel/AutoConfig/issues"
            vcsUrl = "https://github.com/shedaniel/AutoConfig.git"
            gpg.sign = true
        }
    })
}

publishing {
    publications {
        afterEvaluate {
            register("mavenJava", MavenPublication::class) {
                artifact(remapJar)
                artifact(sourcesJar.get()) {
                    builtBy(sourcesJar)
                }
            }
        }
    }

    repositories {
    }
}

