import com.diffplug.spotless.kotlin.KtfmtStep
import org.jetbrains.kotlin.builtins.StandardNames.FqNames.target

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    kotlin("plugin.serialization") version "2.0.21" apply  false
    alias(libs.plugins.detekt.plugin) apply false
    alias(libs.plugins.spotless) apply true
}

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**/*.gradle.kts")
        ktfmt().googleStyle().configure {
          it.setBlockIndent(4)
          it.setContinuationIndent(4)
          it.setTrailingCommaManagementStrategy(KtfmtStep.TrailingCommaManagementStrategy.NONE)
        }
    }

    kotlinGradle {
        target("**/*.gradle.kts")
        targetExclude("**/build/**/*.gradle.kts")
        ktfmt().googleStyle().configure {
          it.setBlockIndent(4)
          it.setContinuationIndent(4)
          it.setTrailingCommaManagementStrategy(KtfmtStep.TrailingCommaManagementStrategy.NONE)
        }
    }

    yaml {
        target("**/*.yml")
        jackson()
    }
}

afterEvaluate {
    // apply it on every build, this will create diffs but is preferable to breaking CI builds over minor formatting
    tasks.getByName("spotlessCheck").dependsOn(tasks.getByName("spotlessApply"))
}