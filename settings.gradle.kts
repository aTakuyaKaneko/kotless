rootProject.name = "kotless"

pluginManagement {
    repositories {
        gradlePluginPortal()
        jcenter()
        maven(url = "https://releases.jfrog.io/artifactory/oss-releases/")
        maven(url = "https://groovy.jfrog.io/artifactory/libs-release/")
    }
}

include(":schema")
include(":model")
include(":engine")

include(":dsl:common:lang-common")
include(":dsl:common:lang-parser-common")

include(":dsl:kotless:kotless-lang")
include(":dsl:kotless:kotless-lang-local")
include(":dsl:kotless:kotless-lang-parser")

include(":dsl:spring:spring-boot-lang")
include(":dsl:spring:spring-boot-lang-local")
include(":dsl:spring:spring-lang-parser")

include(":dsl:ktor:ktor-lang")
include(":dsl:ktor:ktor-lang-local")
include(":dsl:ktor:ktor-lang-parser")

include(":plugins:gradle")
