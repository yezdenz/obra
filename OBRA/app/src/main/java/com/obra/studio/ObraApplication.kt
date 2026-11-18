package com.obra.studio

import android.app.Application
import android.util.Log
import com.obra.studio.data.local.ObraDatabaseHelper

/** Opens the schema once during process startup without coupling it to a screen. */
class ObraApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val databaseFile = getDatabasePath(ObraDatabaseHelper.DATABASE_NAME)
        Log.i(
            TAG,
            "SQLite initialization attempted: file=${ObraDatabaseHelper.DATABASE_NAME}, " +
                "version=${ObraDatabaseHelper.DATABASE_VERSION}, path=${databaseFile.absolutePath}"
        )

        val helper = ObraDatabaseHelper(this)
        try {
            val (tables, foreignKeysEnabled) = helper.writableDatabase.use { database ->
                val tableNames = database.rawQuery(
                    """SELECT name FROM sqlite_master
                        WHERE type = 'table'
                          AND name NOT LIKE 'sqlite_%'
                          AND name != 'android_metadata'
                        ORDER BY name""".trimIndent(),
                    null
                ).use { cursor ->
                    buildList {
                        while (cursor.moveToNext()) add(cursor.getString(0))
                    }.joinToString()
                }
                val foreignKeys = database.rawQuery("PRAGMA foreign_keys", null).use { cursor ->
                    cursor.moveToFirst() && cursor.getInt(0) == 1
                }
                tableNames to foreignKeys
            }
            helper.close()
            Log.i(
                TAG,
                "SQLite initialization succeeded: file=${ObraDatabaseHelper.DATABASE_NAME}, " +
                    "version=${ObraDatabaseHelper.DATABASE_VERSION}, path=${databaseFile.absolutePath}, " +
                    "size=${databaseFile.length()} bytes, foreignKeysEnabled=$foreignKeysEnabled, " +
                    "tables=[$tables]"
            )
        } catch (error: Exception) {
            helper.close()
            Log.e(
                TAG,
                "SQLite initialization failed: file=${ObraDatabaseHelper.DATABASE_NAME}, " +
                    "version=${ObraDatabaseHelper.DATABASE_VERSION}, path=${databaseFile.absolutePath}",
                error
            )
            throw error
        }
    }

    companion object {
        private const val TAG = "ObraDatabase"
    }
}
