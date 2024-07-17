plugins {
    id("java-library")
}


repositories {
    mavenCentral()
    maven("https://repo.akani.dev/releases")
}

dependencies {
    api(project(":platforms:common"))
    compileOnly(libs.akanicore)
    compileOnly(libs.jbannotations)
}

tasks.test {
    useJUnitPlatform()
}