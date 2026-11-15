plugins { id("com.android.application") version "9.5.1" apply false }

// Keep generated files out of cloud-synced folders on Windows. Each checkout
// receives a separate directory so another copy cannot overwrite its outputs.
val localAppData = providers.environmentVariable("LOCALAPPDATA").orNull
if (System.getProperty("os.name").startsWith("Windows") && localAppData != null) {
    val checkoutKey = java.security.MessageDigest.getInstance("SHA-256")
        .digest(rootDir.canonicalPath.toByteArray(Charsets.UTF_8))
        .take(8).joinToString("") { "%02x".format(it) }
    val outputRoot = file("$localAppData/OBRA/build-output/$checkoutKey")
    allprojects {
        val moduleDirectory = if (path == ":") "root" else path.trim(':').replace(':', '/')
        layout.buildDirectory.set(outputRoot.resolve(moduleDirectory))
    }
}
