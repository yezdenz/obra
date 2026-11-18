package com.obra.studio

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.obra.studio.data.Studio
import com.obra.studio.data.StudioSnapshot
import com.obra.studio.data.local.ObraDatabaseHelper
import com.obra.studio.data.local.SqliteStudioRepository
import java.util.concurrent.Executors

/** One helper and serial worker for the process. Snapshots are published on main. */
open class ObraApplication : Application() {
    private lateinit var helper: ObraDatabaseHelper
    private lateinit var repository: SqliteStudioRepository
    private val worker = Executors.newSingleThreadExecutor()
    private val main = Handler(Looper.getMainLooper())

    override fun onCreate() {
        super.onCreate()
        helper = ObraDatabaseHelper(this)
        repository = SqliteStudioRepository(helper, this)
        worker.execute {
            try {
                helper.writableDatabase
                Log.i("ObraDatabase", "SQLite ready at schema version ${ObraDatabaseHelper.DATABASE_VERSION}")
            } catch (error: Exception) {
                // The first screen exposes initialization failure with Retry.
                Log.e("ObraDatabase", "SQLite initialization failed", error)
            }
        }
    }

    fun <T> execute(operation: (SqliteStudioRepository) -> T, completed: (Result<T>) -> Unit) {
        worker.execute {
            val result = runCatching { operation(repository) }
            main.post { completed(result) }
        }
    }

    fun refresh(completed: (Result<StudioSnapshot>) -> Unit) {
        execute({ it.snapshot() }) { result ->
            result.onSuccess { Studio.repository = it }
            completed(result)
        }
    }

    override fun onTerminate() {
        worker.shutdown()
        helper.close()
        super.onTerminate()
    }
}
