import io.kotless.plugin.gradle.dsl.Webapp.Route53
import io.kotless.plugin.gradle.dsl.kotless

group = rootProject.group
version = rootProject.version

plugins {
    id("io.kotless") version "0.1.6" apply true
}

dependencies {
    implementation("commons-validator", "commons-validator", "1.6")
    implementation("com.amazonaws", "aws-java-sdk-dynamodb", "1.11.650")

    implementation("io.kotless", "ktor-lang", "0.1.6")
    implementation("io.ktor", "ktor-html-builder", "1.4.0")
}

kotless {
    config {
        bucket = "eu.ktor-short.s3.ktls.aws.intellij.net"
        prefix = "ktor-short"

        terraform {
            profile = "kotless-jetbrains"
            region = "eu-west-1"
        }
    }

    webapp {
        route53 = Route53("ktor.short", "kotless.io")
    }

    extensions {
        local {
            useAWSEmulation = true
        }

        terraform {
            files {
                add(file("src/main/tf/extensions.tf"))
            }
        }
    }
}

