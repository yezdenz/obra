package com.obra.studio.data.local

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Owns creation and versioning of OBRA's local SQLite schema.
 * Application repositories and feature-specific database operations belong elsewhere.
 */
class ObraDatabaseHelper(
    context: Context,
    databaseName: String = DATABASE_NAME
) : SQLiteOpenHelper(context.applicationContext, databaseName, null, DATABASE_VERSION) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        schemaStatements.forEach(db::execSQL)
        seedCommissionTypes(db)
    }

    /**
     * There are no older OBRA database versions yet. When the schema version is raised,
     * add one explicit migration for each step here. Unknown paths fail without deleting data.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        var version = oldVersion
        while (version < newVersion) {
            version = when (version) {
                // Add migrate1To2(db) here when DATABASE_VERSION becomes 2.
                else -> throw SQLException(
                    "No non-destructive migration is defined from database version $version."
                )
            }
        }
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        throw SQLException(
            "Database downgrade from version $oldVersion to $newVersion is not supported."
        )
    }

    private fun seedCommissionTypes(db: SQLiteDatabase) {
        commissionTypeNames.forEach { name ->
            val values = ContentValues().apply {
                put(COLUMN_NAME, name)
                put(COLUMN_DEFAULT_PRICE_MINOR, null as Long?)
                put(COLUMN_IS_ACTIVE, 1)
            }
            db.insertWithOnConflict(
                TABLE_COMMISSION_TYPES,
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE
            )
        }
    }

    companion object {
        private const val COLUMN_NAME = "name"
        private const val COLUMN_DEFAULT_PRICE_MINOR = "default_price_minor"
        private const val COLUMN_IS_ACTIVE = "is_active"

        private val commissionTypeNames = listOf(
            "Full body",
            "Half body",
            "Chibi",
            "Portrait",
            "Full illustration",
            "Character sheet",
            "Icon"
        )

        private val schemaStatements = listOf(
            """
            CREATE TABLE clients (
                id INTEGER PRIMARY KEY,
                platform TEXT NOT NULL CHECK (platform IN ('X', 'DISCORD')),
                username TEXT NOT NULL CHECK (length(trim(username)) > 0),
                display_name TEXT,
                notes TEXT
            )
            """.trimIndent(),
            """
            CREATE TABLE commission_types (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL UNIQUE CHECK (length(trim(name)) > 0),
                default_price_minor INTEGER CHECK (
                    default_price_minor IS NULL OR
                    (typeof(default_price_minor) = 'integer' AND default_price_minor >= 0)
                ),
                is_active INTEGER NOT NULL DEFAULT 1 CHECK (is_active IN (0, 1))
            )
            """.trimIndent(),
            """
            CREATE TABLE genres (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL UNIQUE CHECK (length(trim(name)) > 0),
                is_active INTEGER NOT NULL DEFAULT 1 CHECK (is_active IN (0, 1))
            )
            """.trimIndent(),
            """
            CREATE TABLE commissions (
                id INTEGER PRIMARY KEY,
                creation_token TEXT UNIQUE,
                client_id INTEGER NOT NULL
                    REFERENCES clients(id) ON DELETE RESTRICT,
                type_id INTEGER NOT NULL
                    REFERENCES commission_types(id) ON DELETE RESTRICT,
                primary_genre_id INTEGER
                    REFERENCES genres(id) ON DELETE RESTRICT,
                client_display_name TEXT,
                title TEXT NOT NULL CHECK (length(trim(title)) > 0),
                brief TEXT,
                pose_notes TEXT,
                notes TEXT,
                agreed_total_minor INTEGER NOT NULL CHECK (
                    typeof(agreed_total_minor) = 'integer' AND agreed_total_minor > 0
                ),
                status TEXT NOT NULL DEFAULT 'QUEUED'
                    CHECK (status IN ('QUEUED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
                stage TEXT CHECK (
                    stage IS NULL OR stage IN ('SKETCH', 'LINEART', 'COLORS', 'FINISHED')
                ),
                commission_on TEXT NOT NULL CHECK (
                    commission_on GLOB '[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]'
                ),
                planned_start_on TEXT CHECK (
                    planned_start_on IS NULL OR planned_start_on GLOB
                    '[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]'
                ),
                started_on TEXT CHECK (
                    started_on IS NULL OR started_on GLOB
                    '[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]'
                ),
                due_on TEXT NOT NULL CHECK (
                    due_on GLOB '[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]'
                    AND due_on >= commission_on
                ),
                finished_on TEXT CHECK (
                    finished_on IS NULL OR finished_on GLOB
                    '[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]'
                ),
                cancelled_on TEXT CHECK (
                    cancelled_on IS NULL OR cancelled_on GLOB
                    '[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]'
                ),
                cancellation_note TEXT,
                created_at TEXT NOT NULL DEFAULT
                    (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
                updated_at TEXT NOT NULL DEFAULT
                    (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
                CHECK (
                    planned_start_on IS NULL OR
                    planned_start_on BETWEEN commission_on AND due_on
                ),
                CHECK (started_on IS NULL OR started_on >= commission_on),
                CHECK (
                    finished_on IS NULL OR
                    (started_on IS NOT NULL AND finished_on >= started_on)
                )
            )
            """.trimIndent(),
            """
            CREATE TABLE commission_tags (
                commission_id INTEGER NOT NULL
                    REFERENCES commissions(id) ON DELETE RESTRICT,
                label TEXT NOT NULL CHECK (length(trim(label)) > 0),
                PRIMARY KEY (commission_id, label)
            )
            """.trimIndent(),
            """
            CREATE TABLE attachments (
                id INTEGER PRIMARY KEY,
                commission_id INTEGER NOT NULL
                    REFERENCES commissions(id) ON DELETE RESTRICT,
                kind TEXT NOT NULL CHECK (kind IN ('REFERENCE', 'FINAL')),
                storage_kind TEXT NOT NULL
                    CHECK (storage_kind IN ('CONTENT_URI', 'APP_FILE')),
                location TEXT NOT NULL CHECK (length(trim(location)) > 0),
                display_name TEXT,
                mime_type TEXT NOT NULL CHECK (mime_type LIKE 'image/%'),
                caption TEXT,
                sort_order INTEGER NOT NULL DEFAULT 0 CHECK (sort_order >= 0),
                UNIQUE (commission_id, kind, sort_order)
            )
            """.trimIndent(),
            """
            CREATE TABLE payments (
                id INTEGER PRIMARY KEY,
                commission_id INTEGER NOT NULL
                    REFERENCES commissions(id) ON DELETE RESTRICT,
                submission_token TEXT UNIQUE,
                amount_minor INTEGER NOT NULL CHECK (
                    typeof(amount_minor) = 'integer' AND amount_minor > 0
                ),
                received_on TEXT NOT NULL CHECK (
                    received_on GLOB '[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]'
                ),
                method TEXT NOT NULL CHECK (
                    method IN ('GCASH', 'BANK_TRANSFER', 'PAYPAL', 'CASH', 'OTHER')
                ),
                purpose TEXT,
                note TEXT,
                created_at TEXT NOT NULL DEFAULT
                    (strftime('%Y-%m-%dT%H:%M:%fZ', 'now'))
            )
            """.trimIndent(),
            """
            CREATE TABLE checklist_items (
                id INTEGER PRIMARY KEY,
                commission_id INTEGER NOT NULL
                    REFERENCES commissions(id) ON DELETE RESTRICT,
                label TEXT NOT NULL CHECK (length(trim(label)) > 0),
                is_done INTEGER NOT NULL DEFAULT 0 CHECK (is_done IN (0, 1)),
                sort_order INTEGER NOT NULL DEFAULT 0 CHECK (sort_order >= 0)
            )
            """.trimIndent(),
            "CREATE INDEX idx_clients_identity ON clients(platform, username)",
            "CREATE INDEX idx_commissions_status_due ON commissions(status, due_on, id)",
            "CREATE INDEX idx_commissions_queue ON commissions(status, planned_start_on, id)",
            "CREATE INDEX idx_commissions_client ON commissions(client_id)",
            "CREATE INDEX idx_commissions_reconnect ON commissions(type_id, primary_genre_id, status, finished_on, id)",
            "CREATE INDEX idx_payments_month ON payments(received_on, id)",
            "CREATE INDEX idx_payments_commission ON payments(commission_id, received_on, id)",
            "CREATE INDEX idx_checklist_commission_state ON checklist_items(commission_id, is_done, sort_order)",
            "CREATE INDEX idx_attachments_commission_kind ON attachments(commission_id, kind, sort_order)"
        )

        const val DATABASE_NAME = "obra.db"
        const val DATABASE_VERSION = 1
        const val TABLE_CLIENTS = "clients"
        const val TABLE_COMMISSION_TYPES = "commission_types"
        const val TABLE_GENRES = "genres"
        const val TABLE_COMMISSIONS = "commissions"
        const val TABLE_COMMISSION_TAGS = "commission_tags"
        const val TABLE_ATTACHMENTS = "attachments"
        const val TABLE_PAYMENTS = "payments"
        const val TABLE_CHECKLIST_ITEMS = "checklist_items"
    }
}
