package com.obra.studio.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class ObraDatabaseHelperTest {
    private lateinit var context: Context
    private lateinit var databaseName: String

    @Before
    fun createIsolatedDatabaseName() {
        context = ApplicationProvider.getApplicationContext()
        databaseName = "obra-instrumentation-${UUID.randomUUID()}.db"
        context.deleteDatabase(databaseName)
    }

    @After
    fun removeIsolatedDatabase() {
        context.deleteDatabase(databaseName)
    }

    @Test
    fun initializesSchemaAndPersistsCommissionAfterReopen() {
        val helper = ObraDatabaseHelper(context, databaseName)
        val commissionId: Long

        try {
            helper.writableDatabase.use { db ->
                assertEquals(ObraDatabaseHelper.DATABASE_VERSION, db.version)
                assertEquals(1, db.rawQuery("PRAGMA foreign_keys", null).use { cursor ->
                    assertTrue(cursor.moveToFirst())
                    cursor.getInt(0)
                })
                assertEquals(7, db.rawQuery(
                    "SELECT COUNT(*) FROM commission_types",
                    null
                ).use { cursor ->
                    assertTrue(cursor.moveToFirst())
                    cursor.getInt(0)
                })

                val expectedTables = setOf(
                    "clients",
                    "commission_types",
                    "genres",
                    "commissions",
                    "commission_tags",
                    "attachments",
                    "payments",
                    "checklist_items",
                    "artwork_updates"
                )
                val actualTables = db.rawQuery(
                    """
                    SELECT name FROM sqlite_master
                    WHERE type = 'table'
                      AND name NOT LIKE 'sqlite_%'
                      AND name != 'android_metadata'
                    """.trimIndent(),
                    null
                ).use { cursor ->
                    buildSet {
                        while (cursor.moveToNext()) add(cursor.getString(0))
                    }
                }
                assertEquals(expectedTables, actualTables)

                val clientValues = ContentValues().apply {
                    put("platform", "X")
                    put("username", "instrumentation.test")
                }
                val clientId = db.insertOrThrow("clients", null, clientValues)

                val typeId = db.rawQuery(
                    "SELECT id FROM commission_types WHERE name = ?",
                    arrayOf("Portrait")
                ).use { cursor ->
                    assertTrue(cursor.moveToFirst())
                    cursor.getLong(0)
                }
                assertTrue(typeId > 0)

                val commissionValues = ContentValues().apply {
                    put("creation_token", "instrumentation-${UUID.randomUUID()}")
                    put("client_id", clientId)
                    put("type_id", typeId)
                    put("title", "Database persistence check")
                    put("agreed_total_minor", 125_000L)
                    put("status", "QUEUED")
                    put("commission_on", "2026-11-18")
                    put("due_on", "2026-12-02")
                }
                commissionId = db.insertOrThrow("commissions", null, commissionValues)
                assertTrue(commissionId > 0)
            }
        } finally {
            helper.close()
        }

        val reopenedHelper = ObraDatabaseHelper(context, databaseName)
        try {
            reopenedHelper.readableDatabase.use { db ->
                db.query(
                    "commissions",
                    arrayOf("title", "agreed_total_minor"),
                    "id = ?",
                    arrayOf(commissionId.toString()),
                    null,
                    null,
                    null
                ).use { cursor ->
                    assertTrue(cursor.moveToFirst())
                    assertEquals("Database persistence check", cursor.getString(0))
                    assertEquals(125_000L, cursor.getLong(1))
                }
                assertEquals(7, db.rawQuery(
                    "SELECT COUNT(*) FROM commission_types",
                    null
                ).use { cursor ->
                    assertTrue(cursor.moveToFirst())
                    cursor.getInt(0)
                })
            }
        } finally {
            reopenedHelper.close()
        }
    }

    @Test
    fun isolatedDatabaseSupportsCrudAndRejectsInvalidData() {
        val helper = ObraDatabaseHelper(context, databaseName)
        var clientId = -1L

        try {
            helper.writableDatabase.use { db ->
                val values = ContentValues().apply {
                    put("platform", "DISCORD")
                    put("username", "temporary-verification-user")
                }
                clientId = db.insertOrThrow("clients", null, values)

                db.query(
                    "clients",
                    arrayOf("username"),
                    "id = ?",
                    arrayOf(clientId.toString()),
                    null,
                    null,
                    null
                ).use { cursor ->
                    assertTrue(cursor.moveToFirst())
                    assertEquals("temporary-verification-user", cursor.getString(0))
                }

                val update = ContentValues().apply { put("display_name", "Updated verification user") }
                assertEquals(1, db.update("clients", update, "id = ?", arrayOf(clientId.toString())))
                db.query(
                    "clients",
                    arrayOf("display_name"),
                    "id = ?",
                    arrayOf(clientId.toString()),
                    null,
                    null,
                    null
                ).use { cursor ->
                    assertTrue(cursor.moveToFirst())
                    assertEquals("Updated verification user", cursor.getString(0))
                }

                val invalidForeignKeyValues = ContentValues().apply {
                    put("client_id", Long.MAX_VALUE)
                    put("type_id", 1)
                    put("title", "Must be rejected")
                    put("agreed_total_minor", 100L)
                    put("commission_on", "2026-11-18")
                    put("due_on", "2026-11-19")
                }
                assertConstraintRejected { db.insertOrThrow("commissions", null, invalidForeignKeyValues) }

                val invalidConstraintValues = ContentValues().apply {
                    put("platform", "NOT_A_PLATFORM")
                    put("username", "invalid-platform-user")
                }
                assertConstraintRejected { db.insertOrThrow("clients", null, invalidConstraintValues) }

                assertEquals(1, db.delete("clients", "id = ?", arrayOf(clientId.toString())))
                db.query("clients", arrayOf("id"), "id = ?", arrayOf(clientId.toString()), null, null, null)
                    .use { cursor -> assertFalse(cursor.moveToFirst()) }
            }
        } finally {
            helper.close()
        }
    }

    @Test
    fun productionDatabaseIsCreatedAndReopensWithoutTestRecords() {
        val productionFile = context.getDatabasePath(ObraDatabaseHelper.DATABASE_NAME)
        assertTrue("App startup should create the production database", productionFile.isFile)
        assertTrue("Production database should not be empty", productionFile.length() > 0)

        val firstHelper = ObraDatabaseHelper(context)
        try {
            firstHelper.readableDatabase.use { db ->
                assertEquals(ObraDatabaseHelper.DATABASE_VERSION, db.version)
                assertEquals(1, db.rawQuery("PRAGMA foreign_keys", null).use { cursor ->
                    assertTrue(cursor.moveToFirst())
                    cursor.getInt(0)
                })
                assertNotNull(db.rawQuery(
                    "SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'commissions'",
                    null
                ).use { cursor -> if (cursor.moveToFirst()) cursor.getString(0) else null })
            }
        } finally {
            firstHelper.close()
        }

        val secondHelper = ObraDatabaseHelper(context)
        try {
            secondHelper.readableDatabase.use { db ->
                assertEquals(ObraDatabaseHelper.DATABASE_VERSION, db.version)
                assertTrue(db.rawQuery(
                    "SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = 'commissions'",
                    null
                ).use { it.moveToFirst() })
            }
        } finally {
            secondHelper.close()
        }
        assertTrue(productionFile.isFile && productionFile.length() > 0)
    }

    private fun assertConstraintRejected(operation: () -> Long) {
        try {
            operation()
        } catch (_: SQLiteConstraintException) {
            return
        }
        throw AssertionError("Expected SQLite constraint rejection")
    }
}
