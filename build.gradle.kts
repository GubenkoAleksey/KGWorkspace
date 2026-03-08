// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.devtools.ksp) apply false
    alias(libs.plugins.google.services) apply false
}

tasks.register("gitInitAndCommit") {
    doLast {
        val dir = rootProject.projectDir
        
        fun runCommand(vararg args: String) {
            val process = ProcessBuilder(*args)
                .directory(dir)
                .redirectErrorStream(true)
                .start()
            val output = process.inputStream.bufferedReader().readText()
            process.waitFor()
            println(output)
        }
        
        runCommand("git", "init")
        runCommand("git", "config", "user.name", "AI")
        runCommand("git", "config", "user.email", "ai@example.com")
        runCommand("git", "add", ".")
        runCommand("git", "commit", "-m", "Початковий коміт: Додаток для звітування про статус працівників")
    }
}
