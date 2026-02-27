import io.gitlab.arturbosch.detekt.Detekt

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.12.3" apply false
    id("org.jetbrains.kotlin.android") version "2.3.10" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.8" apply false
    id("org.jlleitschuh.gradle.ktlint") version "14.0.1" apply false
}

subprojects {
    // ── detekt ──────────────────────────────────────────────────────────
    apply(plugin = "io.gitlab.arturbosch.detekt")

    tasks.withType<Detekt>().configureEach {
        jvmTarget = "17"
        config.setFrom(file("$rootDir/config/detekt/detekt.yml"))
        buildUponDefaultConfig = true
        reports {
            html.required.set(true)
            xml.required.set(true)
            sarif.required.set(true)
        }
    }

    // ── ktlint ──────────────────────────────────────────────────────────
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}
