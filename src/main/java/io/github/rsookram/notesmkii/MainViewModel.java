package io.github.rsookram.notesmkii;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class MainViewModel {

    private final UriData uriData;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final List<Future<?>> cancellables = new ArrayList<>();

    private String editedContent;

    private Consumer<String> onTitleChange = title -> {};
    private Consumer<String> onContentLoad = content -> {};

    private String title;
    private String content;

    private Uri uri;

    public MainViewModel(UriData uriData) {
        this.uriData = uriData;
    }

    public void onUriSelected(Uri uri) {
        this.uri = uri;

        cancelPendingWork();

        Future<?> future = uriData.getName(uri)
                .thenAccept(title -> handler.post(() -> {
                    MainViewModel.this.title = title;
                    onTitleChange.accept(title);
                }));
        cancellables.add(future);

        future = uriData.readContent(uri)
                .thenAccept(content -> handler.post(() -> {
                    MainViewModel.this.content = content;
                    onContentLoad.accept(content);
                }));
        cancellables.add(future);
    }

    public void onTextChanged(String text) {
        editedContent = text;
    }

    public void save() {
        if (uri == null || editedContent == null) {
            return;
        }

        uriData.writeContent(uri, editedContent);
    }

    public void onCleared() {
        cancelPendingWork();
    }

    private void cancelPendingWork() {
        for (Future<?> cancellable : cancellables) {
            cancellable.cancel(false);
        }
        cancellables.clear();

        handler.removeCallbacksAndMessages(null);
    }

    public Uri getUri() {
        return uri;
    }

    public void setOnTitleChange(Consumer<String> onTitleChange) {
        if (title != null) {
            onTitleChange.accept(title);
        }
        this.onTitleChange = onTitleChange;
    }

    public void setOnContentLoad(Consumer<String> onContentLoad) {
        if (content != null) {
            onContentLoad.accept(content);
        }
        this.onContentLoad = onContentLoad;
    }
}
