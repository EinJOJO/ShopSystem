plugins {
    id("java")
    alias(libs.plugins.shadow)
    alias(libs.plugins.runpaper)
}

group = "it.einjojo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://repo.akani.dev/releases")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://libraries.minecraft.net/")
}

dependencies {
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

        filesMatching("plugin.yml") {
            expand(mapOf("version" to project.version))
        }


    }

    shadowJar {
        minimize()
        archiveBaseName.set("ShopSystem")
        archiveVersion.set("")
        archiveClassifier.set("")
        relocate("mc.obliviate.inventory", "it.einjojo.shopsystem.shadow.inventory")
        relocate("co.aikar", "it.einjojo.shopsystem.shadow.aikar")


    }

    runServer {
        minecraftVersion("1.20.4")
    }


    test {
        useJUnitPlatform()
    }
}