package io.github.rsookram.notesmkii;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class UriData {

    private final ContentResolver contentResolver;
    private final Executor bgExecutor = Executors.newSingleThreadExecutor();

    public UriData(Context context) {
        this.contentResolver = context.getContentResolver();
    }

    public CompletableFuture<String> getName(Uri uri) {
        return execute(() -> {
            try (Cursor cursor = contentResolver.query(
                    uri,
                    new String[]{DocumentsContract.Document.COLUMN_DISPLAY_NAME},
                    null,
                    null,
                    null
            )) {
                if (cursor.moveToFirst() && !cursor.isNull(0)) {
                    return cursor.getString(0);
                } else {
                    return null;
                }
            }
        });
    }

    public CompletableFuture<String> readContent(Uri uri) {
        return execute(() -> {
            InputStream stream;
            try {
                stream = contentResolver.openInputStream(uri);
                if (stream == null) {
                    return "";
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                return reader.lines().collect(Collectors.joining("\n"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<Void> writeContent(Uri uri, String content) {
        return execute(() -> {
            OutputStream stream;
            try {
                stream = contentResolver.openOutputStream(uri, "rwt");
                if (stream == null) {
                    return null;
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            try (OutputStreamWriter writer = new OutputStreamWriter(stream)) {
                writer.write(content);
                return null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private <T> CompletableFuture<T> execute(Supplier<T> f) {
        return CompletableFuture.supplyAsync(f, bgExecutor);
    }
}
