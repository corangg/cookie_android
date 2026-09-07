package com.nuecoo.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import org.junit.Test

// ──────────────────────────────────────────────
// core / feature 경계 강제
//
// core 는 어떤 feature 에도 의존하지 않는다 (core.dependsOnNothing()).
// 각 feature 는 core 에는 의존할 수 있지만, 서로에게는 의존할 수 없다
// (다른 feature 로의 의존은 여기서 선언하지 않으므로 기본적으로 금지된다).
// ──────────────────────────────────────────────
class ArchitectureTest {

    @Test
    fun `core is independent and feature modules do not depend on each other`() {
        Konsist.scopeFromProduction().assertArchitecture {
            val core = Layer("Core", "com.nuecoo.core..")
            val featureAuth = Layer("FeatureAuth", "com.nuecoo.feature.auth..")
            val featureMain = Layer("FeatureMain", "com.nuecoo.feature.main..")
            val featureSplash = Layer("FeatureSplash", "com.nuecoo.feature.splash..")
            val featureWidget = Layer("FeatureWidget", "com.nuecoo.feature.widget..")

            core.dependsOnNothing()
            featureAuth.dependsOn(core)
            featureMain.dependsOn(core)
            featureSplash.dependsOn(core)
            featureWidget.dependsOn(core)
        }
    }
}
