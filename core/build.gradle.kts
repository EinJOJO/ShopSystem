plugins {
    id("java")
    alias(libs.plugins.shadow)
    alias(libs.plugins.runpaper)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://libraries.minecraft.net/")
}

dependencies {
    project.project(":platforms").subprojects.forEach {
        implementation(project(it.path))
    }
    compileOnly(libs.jbannotations)
    compileOnly(libs.paper)
    implementation(libs.obliviateinvcore)
    implementation(libs.obliviateinvpagination)
    implementation(libs.acf)
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"

    }

    assemble {
        dependsOn("shadowJar")
    }

    processResources {
        from(sourceSets.main.get().resources.srcDirs) {
            filesMatching("plugin.yml") {
                expand(mapOf("version" to version))
            }
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }


    }

    runServer {
        minecraftVersion("1.20.4")
    }


    shadowJar {
        dependsOn(project.project(":platforms").subprojects.map {
            it.tasks.named("assemble")
        })
        archiveBaseName.set("ShopSystem")
        archiveVersion.set("")
        archiveClassifier.set("")
        relocate("mc.obliviate.inventory", "it.einjojo.shopsystem.shadow.inventory")
        relocate("co.aikar", "it.einjojo.shopsystem.shadow.aikar")
        minimize()


    }
    test {
        useJUnitPlatform()
    }

}
