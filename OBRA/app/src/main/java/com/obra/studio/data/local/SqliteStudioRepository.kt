package com.obra.studio.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.net.Uri
import com.obra.studio.data.*
import java.security.MessageDigest
import java.time.Instant
import java.time.LocalDate
import java.io.File

/** Owns each feature transaction. Call from the application's serial database worker. */
class SqliteStudioRepository(
    private val helper: ObraDatabaseHelper, private val context: Context
) : StudioWriter {
    fun snapshot(): StudioSnapshot = transaction { db ->
        val tasks = mutableMapOf<Long, MutableList<Task>>()
        db.rawQuery("SELECT * FROM checklist_items ORDER BY sort_order, id", null).use { rows ->
            while (rows.moveToNext()) tasks.getOrPut(rows.long("commission_id")) { mutableListOf() }
                .add(Task(rows.long("id"), rows.text("label"), rows.long("is_done") == 1L))
        }
        val tags = mutableMapOf<Long, MutableList<String>>()
        db.rawQuery("SELECT * FROM commission_tags ORDER BY label", null).use { rows ->
            while (rows.moveToNext()) tags.getOrPut(rows.long("commission_id")) { mutableListOf() }
                .add(rows.text("label"))
        }
        val images = mutableMapOf<Long, MutableList<String>>()
        db.rawQuery("SELECT * FROM attachments WHERE kind = 'REFERENCE' ORDER BY sort_order, id", null).use { rows ->
            while (rows.moveToNext()) images.getOrPut(rows.long("commission_id")) { mutableListOf() }
                .add(if (rows.text("storage_kind") == "APP_FILE")
                    runCatching { Uri.fromFile(appFile(rows.text("location"))).toString() }
                        .getOrDefault("unavailable:reference") else rows.text("location"))
        }
        val work = mutableListOf<Commission>()
        val updates = mutableMapOf<Long, MutableList<ArtworkUpdate>>()
        db.rawQuery("SELECT * FROM artwork_updates ORDER BY id DESC", null).use { rows ->
            while (rows.moveToNext()) updates.getOrPut(rows.long("commission_id")) { mutableListOf() }
                .add(ArtworkUpdate(rows.long("id"), displayStage(rows.text("stage")),
                    imageLocation(rows.text("storage_kind"), rows.text("location")),
                    rows.text("created_at"), rows.text("submission_token")))
        }
        val finals = mutableMapOf<Long, MutableList<String>>()
        db.rawQuery("SELECT * FROM attachments WHERE kind = 'FINAL' ORDER BY sort_order, id", null).use { rows ->
            while (rows.moveToNext()) finals.getOrPut(rows.long("commission_id")) { mutableListOf() }
                .add(imageLocation(rows.text("storage_kind"), rows.text("location")))
        }
        db.rawQuery("""SELECT c.*, cl.username, cl.platform, t.name AS type_name,
            g.name AS genre_name FROM commissions c
            JOIN clients cl ON cl.id = c.client_id
            JOIN commission_types t ON t.id = c.type_id
            LEFT JOIN genres g ON g.id = c.primary_genre_id
            ORDER BY c.due_on, c.id""", null).use { rows ->
            while (rows.moveToNext()) {
                val id = rows.long("id")
                work.add(Commission(
                    id, rows.text("title"), rows.text("username"), displayPlatform(rows.text("platform")),
                    rows.text("type_name"), rows.text("genre_name"), displayStatus(rows.text("status")),
                    displayStage(rows.text("stage")), rows.long("agreed_total_minor"),
                    rows.text("commission_on"), rows.text("due_on"), rows.text("planned_start_on"),
                    rows.text("started_on"), rows.text("finished_on"), rows.text("brief"),
                    tasks[id].orEmpty(), rows.text("client_display_name"), tags[id].orEmpty().joinToString(", "),
                    images[id].orEmpty(), rows.long("background_included") == 1L,
                    rows.long("client_id"), rows.long("type_id"), rows.nullableLong("primary_genre_id"),
                    rows.text("updated_at"), rows.text("creation_token"), updates[id].orEmpty(), finals[id].orEmpty()
                ))
            }
        }
        val payments = mutableListOf<Payment>()
        db.rawQuery("SELECT * FROM payments ORDER BY received_on DESC, id DESC", null).use { rows ->
            while (rows.moveToNext()) payments.add(Payment(
                rows.long("id"), rows.long("commission_id"), rows.long("amount_minor"),
                rows.text("received_on"), displayMethod(rows.text("method")), rows.text("note"), rows.text("submission_token")
            ))
        }
        StudioSnapshot(work, payments, activeLabels(db, "commission_types"), activeLabels(db, "genres"))
    }

    override fun saveCommission(draft: CommissionDraft, submissionToken: String): Long {
        require(submissionToken.isNotBlank()) { "The save token is missing. Reopen the form." }
        val fingerprint = draftFingerprint(draft)
        // Resolve a successful retry before checking whether old image grants are still available.
        existingCommission(submissionToken, fingerprint)?.let { return it }
        val attachments = prepareImages(draft.images)
        return transaction { db ->
            existingCommission(db, submissionToken, fingerprint)?.let { return@transaction it }
            val total = validateDraft(draft, null)
            val values = requestValues(db, draft, total)
            values.put("creation_token", submissionToken)
            values.put("creation_fingerprint", fingerprint)
            values.put("status", "QUEUED")
            values.put("updated_at", Instant.now().toString())
            val id = db.insertOrThrow("commissions", null, values)
            replaceRequestChildren(db, id, draft, attachments)
            listOf("Finish sketch", "Finish lineart").forEachIndexed { index, label ->
                db.insertOrThrow("checklist_items", null, ContentValues().apply {
                    put("commission_id", id); put("label", label); put("sort_order", index)
                })
            }
            if (draft.initialEnabled) {
                insertPayment(db, id, requireNotNull(Validation.minor(draft.initialAmount)),
                    draft.initialDate, draft.initialMethod, draft.initialNote,
                    "initial:$submissionToken", "INITIAL")
            }
            id
        }
    }

    override fun updateCommission(id: Long, draft: CommissionDraft) {
        val attachments = prepareImages(draft.images)
        transaction { db ->
            val original = commission(db, id)
            require(draft.expectedRevision == original.revision) {
                "This commission changed while you were editing. Reopen Edit to load its latest details."
            }
            val total = validateDraft(draft, original)
            require(total >= received(db, id)) { "Price cannot be below payments already received." }
            require(!exists(db, "SELECT 1 FROM payments WHERE commission_id = ? AND received_on < ?",
                id.toString(), draft.commissionOn)) { "Commission date cannot be after an existing receipt." }
            val values = requestValues(db, draft, total, original)
            // Revisions prevent an older Edit draft from replacing a later request change.
            values.put("updated_at", Instant.now().toString())
            check(db.update("commissions", values, "id = ?", arrayOf(id.toString())) == 1)
            replaceRequestChildren(db, id, draft, attachments)
        }
    }

    override fun recordPayment(commissionId: Long, amountMinor: Long, receivedOn: String,
        method: String, note: String, submissionToken: String) {
        transaction { db -> insertPayment(db, commissionId, amountMinor, receivedOn, method,
            note, submissionToken, null) }
    }

    override fun addArtwork(commissionId: Long, stage: String, image: String, submissionToken: String): Long {
        require(submissionToken.isNotBlank()) { "Reopen Add artwork and try again." }
        val stageKey = when(stage) {
            "Sketch" -> "SKETCH"; "Lineart" -> "LINEART"; "Colors" -> "COLORS"; "Finished" -> "FINISHED"
            else -> throw IllegalArgumentException("Choose an artwork stage.")
        }
        val fingerprint = fingerprint(listOf(commissionId.toString(), stageKey, image))
        fun existing(db: SQLiteDatabase): Long? = db.rawQuery(
            "SELECT id, submission_fingerprint FROM artwork_updates WHERE submission_token = ?",
            arrayOf(submissionToken)).use { rows ->
            if (!rows.moveToFirst()) null else {
                require(rows.getString(1) == fingerprint) { "This artwork save belongs to different input." }
                rows.getLong(0)
            }
        }
        existing(helper.readableDatabase)?.let { return it }
        val prepared = prepareImages(listOf(image)).single()
        return transaction { db ->
            existing(db)?.let { return@transaction it }
            require(commission(db, commissionId).status != "Cancelled") { "Cannot add artwork to cancelled work." }
            db.insertOrThrow("artwork_updates", null, ContentValues().apply {
                put("commission_id", commissionId); put("stage", stageKey)
                put("storage_kind", prepared.storageKind); put("location", prepared.location)
                put("mime_type", prepared.mime); put("submission_token", submissionToken)
                put("submission_fingerprint", fingerprint); put("created_at", Instant.now().toString())
            })
        }
    }

    private fun imageLocation(kind: String, location: String): String =
        if(kind == "APP_FILE") runCatching { Uri.fromFile(appFile(location)).toString() }
            .getOrDefault("unavailable:artwork") else location

    override fun startCommission(id: Long, startedOn: String) {
        transaction { db ->
            val current = commission(db, id)
            require(current.status == "Queued") { "Only queued work can be started." }
            val start = actualDate(startedOn)
            require(start >= LocalDate.parse(current.commissionOn)) { "Start cannot precede the commission date." }
            updateWork(db, id, ContentValues().apply {
                put("status", "IN_PROGRESS"); put("stage", "SKETCH"); put("started_on", startedOn)
            })
        }
    }

    override fun updateProgress(id: Long, stage: String, finishedOn: String?) {
        transaction { db ->
            val current = commission(db, id)
            require(current.status == "In progress") { "Only current work can have its progress updated." }
            val key = when (stage) {
                "Sketch" -> "SKETCH"; "Lineart" -> "LINEART"; "Colors" -> "COLORS"; "Finished" -> "FINISHED"
                else -> throw IllegalArgumentException("Choose a supported creative stage.")
            }
            val values = ContentValues().apply { put("stage", key) }
            if (key == "FINISHED") {
                val date = actualDate(requireNotNull(finishedOn) { "Choose an actual finish date." })
                require(date >= LocalDate.parse(current.startedOn)) { "Finish cannot precede actual start." }
                values.put("status", "COMPLETED"); values.put("finished_on", finishedOn)
            } else require(finishedOn == null) { "A finish date requires the Finished stage." }
            updateWork(db, id, values)
        }
    }

    override fun setTaskDone(commissionId: Long, taskId: Long, done: Boolean) {
        transaction { db ->
            commission(db, commissionId)
            require(db.update("checklist_items", ContentValues().apply { put("is_done", if (done) 1 else 0) },
                "id = ? AND commission_id = ?", arrayOf(taskId.toString(), commissionId.toString())) == 1) {
                "This checklist item is unavailable. Refresh the commission."
            }
        }
    }

    private fun validateDraft(draft: CommissionDraft, original: Commission?): Long {
        require(draft.username.isNotBlank()) { "Enter the client's username." }
        require(draft.title.isNotBlank()) { "Enter the artwork title." }
        Validation.schedule(draft, original)?.let { throw IllegalArgumentException(it) }
        return requireNotNull(Validation.minor(draft.total)) { "Enter a valid positive price with at most two decimal places." }
    }

    private fun requestValues(db: SQLiteDatabase, draft: CommissionDraft, total: Long,
        original: Commission? = null): ContentValues {
        val platform = platformKey(draft.platform)
        val username = draft.username.trim()
        val clientId = db.rawQuery("SELECT id FROM clients WHERE platform = ? AND username = ? ORDER BY id LIMIT 1",
            arrayOf(platform, username)).use { rows ->
            if (rows.moveToFirst()) rows.getLong(0) else db.insertOrThrow("clients", null,
                ContentValues().apply { put("platform", platform); put("username", username) })
        }
        val typeId = lookup(db, "commission_types", draft.type.trim(), false, original?.typeId)
        val genreId = draft.genre.trim().takeIf { it.isNotEmpty() }
            ?.let { lookup(db, "genres", it, true, original?.genreId) }
        return ContentValues().apply {
            put("client_id", clientId); put("type_id", typeId); put("primary_genre_id", genreId)
            put("client_display_name", draft.displayName.trim().ifEmpty { null })
            put("title", draft.title.trim()); put("brief", draft.brief.trim())
            put("agreed_total_minor", total); put("commission_on", draft.commissionOn)
            put("due_on", draft.dueOn); put("planned_start_on", draft.plannedStart.ifBlank { null })
            put("background_included", if (draft.background) 1 else 0)
        }
    }

    private fun activeLabels(db: SQLiteDatabase, table: String): List<String> =
        db.rawQuery("SELECT name FROM $table WHERE is_active = 1 ORDER BY id", null).use { rows ->
            buildList { while (rows.moveToNext()) add(rows.getString(0)) }
        }

    private fun lookup(db: SQLiteDatabase, table: String, name: String, create: Boolean,
        retainedId: Long? = null): Long {
        db.rawQuery("SELECT id, is_active FROM $table WHERE name = ?", arrayOf(name)).use { rows ->
            if (rows.moveToFirst()) {
                val id = rows.getLong(0)
                require(rows.getInt(1) == 1 || id == retainedId) {
                    "This category has been retired. Choose an active category for new work."
                }
                return id
            }
        }
        require(create) { "Choose an existing commission type." }
        return db.insertOrThrow(table, null, ContentValues().apply { put("name", name) })
    }

    private data class PreparedImage(val location: String, val mime: String, val storageKind: String)

    private fun replaceRequestChildren(db: SQLiteDatabase, id: Long, draft: CommissionDraft, images: List<PreparedImage>) {
        db.delete("commission_tags", "commission_id = ?", arrayOf(id.toString()))
        draft.tags.split(',').map { it.trim() }.filter { it.isNotEmpty() }.distinct().forEach { label ->
            db.insertOrThrow("commission_tags", null, ContentValues().apply {
                put("commission_id", id); put("label", label)
            })
        }
        val metadata = mutableMapOf<String, Pair<String?, String?>>()
        db.rawQuery("SELECT * FROM attachments WHERE commission_id = ? AND kind = 'REFERENCE'", arrayOf(id.toString())).use { rows ->
            while (rows.moveToNext()) metadata[attachmentKey(rows.text("storage_kind"), rows.text("location"))] =
                rows.getString(rows.getColumnIndexOrThrow("display_name")) to rows.getString(rows.getColumnIndexOrThrow("caption"))
        }
        db.delete("attachments", "commission_id = ? AND kind = 'REFERENCE'", arrayOf(id.toString()))
        images.forEachIndexed { index, image ->
            db.insertOrThrow("attachments", null, ContentValues().apply {
                put("commission_id", id); put("kind", "REFERENCE"); put("storage_kind", image.storageKind)
                put("location", image.location); put("mime_type", image.mime); put("sort_order", index)
                metadata[attachmentKey(image.storageKind, image.location)]?.let { (name, caption) ->
                    put("display_name", name); put("caption", caption)
                }
            })
        }
    }

    private fun appFile(location: String): File {
        val root = context.filesDir.canonicalFile
        val supplied = File(location)
        val file = (if (supplied.isAbsolute) supplied else File(root, location)).canonicalFile
        require(file.path.startsWith(root.path + File.separator)) { "The reference location is outside app storage." }
        return file
    }

    private fun attachmentKey(kind: String, location: String): String =
        kind + ":" + if (kind == "APP_FILE") runCatching { appFile(location).path }.getOrDefault(location) else location

    private fun prepareImages(images: List<String>): List<PreparedImage> {
        require(images.size <= 6) { "Attach at most six reference images." }
        return images.distinct().map { location ->
            val uri = Uri.parse(location)
            val file = if (uri.scheme == "file") appFile(requireNotNull(uri.path)) else null
            if (file == null) {
                require(uri.scheme == "content") { "Choose references using Add image." }
                require(context.contentResolver.persistedUriPermissions.any { it.uri == uri && it.isReadPermission }) {
                    "A reference no longer has durable access. Remove it and select it again."
                }
            }
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri).use { input ->
                requireNotNull(input) { "A reference image is unavailable." }
                BitmapFactory.decodeStream(input, null, options)
            }
            require(options.outWidth > 0 && options.outHeight > 0) { "A selected reference is not a supported image." }
            PreparedImage(file?.relativeTo(context.filesDir.canonicalFile)?.path ?: location,
                requireNotNull(options.outMimeType) { "Cannot identify the reference image format." },
                if (file == null) "CONTENT_URI" else "APP_FILE")
        }
    }

    private fun insertPayment(db: SQLiteDatabase, id: Long, amount: Long, date: String,
        method: String, note: String, token: String, purpose: String?) {
        require(token.isNotBlank()) { "The payment save token is missing." }
        val methodKey = methodKey(method)
        val fingerprint = fingerprint(listOf(id.toString(), amount.toString(), date, methodKey, note.trim()))
        db.rawQuery("SELECT submission_fingerprint FROM payments WHERE submission_token = ?", arrayOf(token)).use { rows ->
            if (rows.moveToFirst()) {
                require(rows.getString(0) == fingerprint) { "This payment save token belongs to different input." }
                return
            }
        }
        val work = commission(db, id)
        require(work.status != "Cancelled") { "Payments on cancelled work require an agreed correction policy." }
        require(amount in 1..100000000000L) { "Enter a valid positive payment amount." }
        require(actualDate(date) >= LocalDate.parse(work.commissionOn)) { "Payment cannot precede the commission date." }
        val balance = work.totalMinor - received(db, id)
        require(amount <= balance) { "Payment exceeds the remaining balance. Refresh and check the amount." }
        db.insertOrThrow("payments", null, ContentValues().apply {
            put("commission_id", id); put("submission_token", token); put("submission_fingerprint", fingerprint)
            put("amount_minor", amount); put("received_on", date); put("method", methodKey)
            put("purpose", purpose ?: if (amount == balance) "BALANCE" else "PARTIAL")
            put("note", note.trim())
        })
    }

    private fun existingCommission(token: String, fingerprint: String): Long? =
        existingCommission(helper.readableDatabase, token, fingerprint)

    private fun existingCommission(db: SQLiteDatabase, token: String, fingerprint: String): Long? =
        db.rawQuery("SELECT id, creation_fingerprint FROM commissions WHERE creation_token = ?", arrayOf(token)).use { rows ->
            if (!rows.moveToFirst()) null else {
                require(rows.getString(1) == fingerprint) { "This commission save token belongs to different input." }
                rows.getLong(0)
            }
        }

    private fun commission(db: SQLiteDatabase, id: Long): Commission =
        db.rawQuery("SELECT * FROM commissions WHERE id = ?", arrayOf(id.toString())).use { rows ->
            require(rows.moveToFirst()) { "This commission is unavailable. Refresh the list." }
            Commission(id, rows.text("title"), "", "", "", "", displayStatus(rows.text("status")),
                displayStage(rows.text("stage")), rows.long("agreed_total_minor"), rows.text("commission_on"),
                rows.text("due_on"), startedOn = rows.text("started_on"), revision = rows.text("updated_at"),
                typeId = rows.long("type_id"), genreId = rows.nullableLong("primary_genre_id"))
        }

    private fun received(db: SQLiteDatabase, id: Long): Long =
        db.rawQuery("SELECT COALESCE(SUM(amount_minor), 0) FROM payments WHERE commission_id = ?", arrayOf(id.toString()))
            .use { it.moveToFirst(); it.getLong(0) }

    private fun updateWork(db: SQLiteDatabase, id: Long, values: ContentValues) {
        values.put("updated_at", Instant.now().toString())
        check(db.update("commissions", values, "id = ?", arrayOf(id.toString())) == 1)
    }

    private fun actualDate(value: String): LocalDate {
        val date = requireNotNull(Validation.date(value)) { "Choose a valid calendar date." }
        require(date <= LocalDate.now()) { "Actual dates cannot be in the future." }
        return date
    }

    private fun exists(db: SQLiteDatabase, sql: String, vararg args: String) =
        db.rawQuery(sql, args).use { it.moveToFirst() }

    private fun draftFingerprint(d: CommissionDraft) = fingerprint(listOf(
        d.platform, d.username.trim(), d.displayName.trim(), d.title.trim(), d.type, d.genre.trim(),
        d.tags, d.brief.trim(), d.total, d.commissionOn, d.dueOn, d.plannedStart,
        d.background.toString(), d.images.joinToString("\n"), d.initialEnabled.toString(),
        if (d.initialEnabled) d.initialAmount else "", if (d.initialEnabled) d.initialDate else "",
        if (d.initialEnabled) d.initialMethod else "", if (d.initialEnabled) d.initialNote else ""
    ))

    private fun fingerprint(parts: List<String>): String {
        val encoded = parts.joinToString("") { "${it.length}:$it" }
        return MessageDigest.getInstance("SHA-256").digest(encoded.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
    }

    private fun platformKey(value: String) = when (value) {
        "X" -> "X"; "Discord", "DISCORD" -> "DISCORD"
        else -> throw IllegalArgumentException("Choose X or Discord.")
    }
    private fun methodKey(value: String) = when (value) {
        "GCash", "GCASH" -> "GCASH"; "Bank transfer", "BANK_TRANSFER" -> "BANK_TRANSFER"
        "PayPal", "PAYPAL" -> "PAYPAL"; "Cash", "CASH" -> "CASH"; "Other", "OTHER" -> "OTHER"
        else -> throw IllegalArgumentException("Choose a supported payment method.")
    }
    private fun displayMethod(key: String) = when (key) {
        "GCASH" -> "GCash"; "BANK_TRANSFER" -> "Bank transfer"; "PAYPAL" -> "PayPal"
        "CASH" -> "Cash"; else -> "Other"
    }
    private fun displayPlatform(key: String) = if (key == "DISCORD") "Discord" else "X"
    private fun displayStatus(key: String) = when (key) {
        "QUEUED" -> "Queued"; "IN_PROGRESS" -> "In progress"; "COMPLETED" -> "Completed"; else -> "Cancelled"
    }
    private fun displayStage(key: String) = when (key) {
        "SKETCH" -> "Sketch"; "LINEART" -> "Lineart"; "COLORS" -> "Colors"; "FINISHED" -> "Finished"; else -> ""
    }

    private fun <T> transaction(operation: (SQLiteDatabase) -> T): T {
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            val result = operation(db)
            db.setTransactionSuccessful()
            return result
        } finally { db.endTransaction() }
    }
}

private fun Cursor.text(name: String): String = getString(getColumnIndexOrThrow(name)).orEmpty()
private fun Cursor.long(name: String): Long = getLong(getColumnIndexOrThrow(name))
private fun Cursor.nullableLong(name: String): Long? {
    val column = getColumnIndexOrThrow(name)
    return if (isNull(column)) null else getLong(column)
}
