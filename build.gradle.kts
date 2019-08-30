@file:Suppress("PropertyName")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import com.matthewprenger.cursegradle.Options
import com.palantir.gradle.gitversion.VersionDetails
import net.fabricmc.loom.task.RemapJarTask
import net.fabricmc.loom.task.RemapSourcesJarTask

val curseProjectId: String by project
val basePackage: String by project
val modJarBaseName: String by project
val modMavenGroup: String by project

val minecraft_version: String by project
val yarn_mappings: String by project
val loader_version: String by project
val fabric_version: String by project

plugins {
    java
    idea
    `maven-publish`
    id("fabric-loom") version "0.2.5-SNAPSHOT"
    id("com.palantir.git-version") version "0.11.0"
    id("com.github.johnrengelman.shadow") version "5.0.0"
    id("com.matthewprenger.cursegradle") version "1.2.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

base {
    archivesBaseName = modJarBaseName
}

repositories {
    maven(url = "http://maven.fabricmc.net/")
    maven(url = "https://minecraft.curseforge.com/api/maven")
    jcenter()
}

val gitVersion: groovy.lang.Closure<Any> by extra
val versionDetails: groovy.lang.Closure<VersionDetails> by extra

version = "${gitVersion()}+mc$minecraft_version"
group = modMavenGroup

minecraft {
}

configurations {
    listOf(shadow, implementation, mappings, modCompile, include).forEach {
        it {
            resolutionStrategy.activateDependencyLocking()
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft_version")
    mappings("net.fabricmc:yarn:$yarn_mappings")
    modCompile("net.fabricmc:fabric-loader:$loader_version")
    modCompile("net.fabricmc.fabric-api:fabric-api:$fabric_version")

    modCompile("cloth-config:ClothConfig:0.2.4.17")
    modCompile("io.github.prospector:modmenu:1.7+")

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

val shadowJar = tasks.getByName<ShadowJar>("shadowJar").apply {
    relocate("blue.endless.jankson", "$basePackage.shadowed.blue.endless.jankson")
    relocate("com.moandjiezana.toml", "$basePackage.shadowed.com.moandjiezana.toml")

    configurations = listOf(project.configurations["shadow"])
    archiveClassifier.set("shadow")
}

val jar = tasks.getByName<Jar>("jar") {
    from("LICENSE")
}

val remapJar = tasks.getByName<RemapJarTask>("remapJar") {
    /*dependsOn("shadowJar")
    afterEvaluate {
        setInput(shadowJar.archiveFile.get().asFile)
    }*/
}

val remapSourcesJar = tasks.getByName<RemapSourcesJarTask>("remapSourcesJar")

if (versionDetails().isCleanTag) {

    publishing {

        publications {
            afterEvaluate {
                register("mavenJava", MavenPublication::class) {
                    artifact(remapJar) {
                        builtBy(remapJar)
                    }
                    artifact(sourcesJar.get()) {
                        builtBy(sourcesJar)
                    }
                }
            }
        }

        repositories {
            if (project.hasProperty("publish_maven_s3_url")) {
                maven {
                    setUrl(project.property("publish_maven_s3_url")!!)
                    credentials(AwsCredentials::class) {
                        accessKey = project.property("publish_maven_s3_access_key") as String
                        secretKey = project.property("publish_maven_s3_secret_key") as String
                    }
                }
            } else {
                mavenLocal()
            }
        }
    }

    curseforge {
        if (project.hasProperty("curseforge_api_key")) {
            apiKey = project.property("curseforge_api_key")!!
        }

        project(closureOf<CurseProject> {
            id = curseProjectId
            changelog = file("changelog.txt")
            releaseType = "release"
            addGameVersion(minecraft_version)
            relations(closureOf<CurseRelation> {
                requiredDependency("fabric-api")
                requiredDependency("cloth-config")
            })
            mainArtifact(file("${project.buildDir}/libs/${base.archivesBaseName}-$version.jar"))
            afterEvaluate {
                uploadTask.dependsOn(remapJar)
            }

        })

        options(closureOf<Options> {
            forgeGradleIntegration = false
        })
    }

}
