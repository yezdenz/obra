package com.obra.studio.data.local

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.obra.studio.ObraApplication
import java.io.File

/** Activity tests use a separate database so a developer's emulator records remain intact. */
class BackendTestRunner : AndroidJUnitRunner() {
    override fun newApplication(loader: ClassLoader, name: String, context: Context): Application =
        super.newApplication(loader, BackendTestApplication::class.java.name, context)
}

class BackendTestApplication : ObraApplication() {
    override fun getDatabasePath(name: String): File =
        super.getDatabasePath(if (name == ObraDatabaseHelper.DATABASE_NAME) TEST_DATABASE else name)

    override fun deleteDatabase(name: String): Boolean =
        super.deleteDatabase(if (name == ObraDatabaseHelper.DATABASE_NAME) TEST_DATABASE else name)

    override fun onCreate() {
        deleteDatabase(ObraDatabaseHelper.DATABASE_NAME)
        super.onCreate()
    }

    companion object { private const val TEST_DATABASE = "obra-instrumentation-ui.db" }
}
