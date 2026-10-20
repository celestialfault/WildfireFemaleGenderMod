import com.wildfire.ATtoCTConverter
import com.wildfire.OptimizePng
import com.wildfire.ValidateJson
import dev.kikugie.stonecutter.build.config.ReplacementContainer
import dev.kikugie.stonecutter.data.tree.ProjectNode

plugins {
    id("dev.kikugie.stonecutter")
    id("net.neoforged.moddev") version "2.0.147" apply false
    id("net.fabricmc.fabric-loom") version "1.19-SNAPSHOT" apply false
    id("idea")
}

idea {
    module {
        // Tell IDEA to always download sources/javadoc artifacts from Maven.
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

stonecutter active "26.4"

tasks.named<Wrapper>("wrapper") {
    gradleVersion = "9.8.2"
    // https://gradle.org/release-checksums/
    // Note that we use the -all distribution to make it easier to modify build scripts by being able
    // to view Javadocs
    distributionSha256Sum = "a1bb7e9651ef09e4cd9a62d471564e0ee23b5757e5f21fd7358d7cfc21018fdf"
    distributionType = Wrapper.DistributionType.ALL
}

tasks.register("generatePackageInfos") {
    description = "Generates package-info files for any packages that are missing them"
    dependsOn(stonecutter.tasks.named("generatePackageInfos") { branch.id.isNotEmpty() })
}

tasks.register("runData") {
    description = "Run data generation for all loaders and versions"
}

tasks.register<OptimizePng>("optimizePng") {
    inputFiles.from(fileTree(layout.projectDirectory) {
        include("*/src/main/resources/**/*.png")
    })
}

tasks.register<ValidateJson>("validateJson") {
    val modId: String = stonecutter.properties["mod_id"]
    inputs.property("modId", modId)
    criticalFiles.from(
        "fabric/src/main/resources/fabric.mod.json",
        "common/src/main/resources/${modId}.mixins.json",
        "fabric/src/main/resources/${modId}.fabric.mixins.json",
        "neoforge/src/main/resources/${modId}.neo.mixins.json",
    )
    rootTranslation.set(layout.projectDirectory.file(
        "fabric/versions/${stonecutter.current?.project}/src/main/generated/assets/${modId}/lang/en_us.json",
    ))
    translationFiles.from(fileTree(layout.projectDirectory) {
        include("*/versions/*/src/main/generated/assets/${modId}/lang/*.json")
        include("common/src/main/resources/assets/${modId}/lang/*.json")
    })
    nonExhaustiveLocales.set(setOf(
        //Only generates the relevant overrides as missing lang entries fall back to en_us
        "en_au",
        "en_ca",
        "en_gb",
        //Exact matches such as "N" does not generate the duplicate file
        "en_ud",
    ))
}

tasks.register<ATtoCTConverter>("convertATtoCT") {
    atPath = layout.projectDirectory.file("common/src/main/resources/META-INF/accesstransformer.cfg")
    ctPath = layout.projectDirectory.file("fabric/src/main/resources/${stonecutter.properties["mod_id"] as String}.classtweaker")
}

val loaderOnly: ProjectNode.() -> Boolean = {
    branch.id == "fabric" || branch.id == "neoforge"
}

tasks.register("publishMods") {
    description = "Publish mod to both platforms, for both loaders, and all versions"
    group = "publishing"
    dependsOn(stonecutter.tasks.named("publishMods", loaderOnly))
}

stonecutter tasks {
    order("publishModrinth", filter = loaderOnly)
    order("publishCurseforge", filter = loaderOnly)
}

stonecutter parameters {
    for((name, value) in project.replacements) {
        if(value.strings.isNotEmpty()) {
            val config = Action<ReplacementContainer.StringReplacementSpec> {
                direction = eval(current.version, value.`when`)
                for((old, new) in value.strings) {
                    replace(old, new)
                }
            }

            when(value.alwaysActive) {
                true -> replacements.string(action = config)
                false -> replacements.string(name, action = config)
            }
        }

        if(value.regex.isNotEmpty()) {
            val config = Action<ReplacementContainer.RegexReplacementSpec> {
                direction = eval(current.version, value.`when`)
                for((forward, backward) in value.regex) {
                    replace(forward.pattern, forward.replacement, backward.pattern, backward.replacement)
                }
            }

            when(value.alwaysActive) {
                true -> replacements.regex(action = config)
                false -> replacements.regex(name, action = config)
            }
        }
    }
}
