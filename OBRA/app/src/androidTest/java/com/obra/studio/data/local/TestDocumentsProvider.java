package com.obra.studio.data.local;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/** Runs in the test APK's separate process, using only framework and Java classes. */
public class TestDocumentsProvider extends ContentProvider {
    @Override public boolean onCreate() { return true; }

    private File file(String id) {
        if (id == null || !id.matches("[a-zA-Z0-9-]+\\.png")) {
            throw new IllegalArgumentException("Invalid fixture name");
        }
        return new File(getContext().getCacheDir(), id);
    }

    @Override public Bundle call(String method, String arg, Bundle extras) {
        if ("createFixture".equals(method)) {
            int size = extras == null ? 32 : extras.getInt("size", 32);
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            try (FileOutputStream output = new FileOutputStream(file(arg))) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            } catch (IOException error) {
                throw new IllegalStateException("Could not create test reference", error);
            } finally { bitmap.recycle(); }
            return new Bundle();
        }
        if ("removeFixture".equals(method)) { file(arg).delete(); return new Bundle(); }
        if ("grantFixture".equals(method)) {
            Uri uri = Uri.parse(extras.getString("uri"));
            getContext().grantUriPermission(arg, uri,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION | android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            return new Bundle();
        }
        return super.call(method, arg, extras);
    }

    @Override public Cursor query(Uri uri, String[] projection, String selection, String[] args, String order) {
        String[] columns = projection == null ? new String[] { OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE } : projection;
        File reference = file(uri.getLastPathSegment());
        MatrixCursor cursor = new MatrixCursor(columns);
        MatrixCursor.RowBuilder row = cursor.newRow();
        for (String column : columns) {
            row.add(OpenableColumns.SIZE.equals(column) ? reference.length() : reference.getName());
        }
        return cursor;
    }
    @Override public String getType(Uri uri) { return "image/png"; }
    @Override public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        return ParcelFileDescriptor.open(file(uri.getLastPathSegment()), ParcelFileDescriptor.MODE_READ_ONLY);
    }
    @Override public Uri insert(Uri uri, ContentValues values) { throw new UnsupportedOperationException(); }
    @Override public int update(Uri uri, ContentValues values, String selection, String[] args) { return 0; }
    @Override public int delete(Uri uri, String selection, String[] args) { return 0; }
}
