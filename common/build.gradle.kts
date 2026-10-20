plugins {
    id("multiloader-common")
    id("net.neoforged.moddev")
}

neoForge {
    enable {
        neoFormVersion = "${sc.current.version}-${sc.properties["dependencies.neoform_timestamp"] as String}"
        enabledSourceSets = sourceSets // All source sets use Minecraft code
    }

    val at = sc.branch.project.file("src/main/resources/META-INF/accesstransformer.cfg")
    accessTransformers.from(sc.process(at, "build/dev.at").absolutePath)
    validateAccessTransformers = true
}

dependencies {
    compileOnly("net.fabricmc:sponge-mixin:${sc.properties["dependencies.fabric_mixin"] as String}")
    annotationProcessor(compileOnly("io.github.llamalad7:mixinextras-common:${sc.properties["dependencies.mixinextras"] as String}")!!)
}

configurations {
    val commonJava = consumable("commonJava") {
        outgoing.artifacts(sourceSets.main.map { it.java.sourceDirectories.files })
    }
    consumable("commonResources") {
        outgoing.artifacts(sourceSets.main.map { it.resources.sourceDirectories.files })
    }
    consumable("commonDataJava") {
        extendsFrom(commonJava)
        outgoing.artifacts(sourceSets.named("datagen").map { it.java.sourceDirectories.files })
    }
}
