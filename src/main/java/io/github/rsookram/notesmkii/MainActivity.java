package io.github.rsookram.notesmkii;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Insets;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.TextView;
import android.widget.Toolbar;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE_OPEN = 1;
    private static final int REQUEST_CODE_CREATE = 2;

    public static final String STATE_URI = "uri";

    private MainViewModel vm;
    private TextView editor;

    private int bottomIgnoreAreaHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        vm = (MainViewModel) getLastNonConfigurationInstance();
        if (vm == null) {
            vm = new MainViewModel(new UriData(getApplicationContext()));
        }

        editor = findViewById(R.id.text);

        if (savedInstanceState != null) {
            Uri uri = savedInstanceState.getParcelable(STATE_URI);
            if (uri != null) {
                vm.onUriSelected(uri);
            }
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.toolbar);

        toolbar.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.open) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT)
                        .setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        .setType("text/plain");

                startActivityForResult(intent, REQUEST_CODE_OPEN);
                return true;
            }

            if (menuItem.getItemId() == R.id.create) {
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT).setType("text/plain");

                startActivityForResult(intent, REQUEST_CODE_CREATE);
                return true;
            }

            return false;
        });

        vm.setOnTitleChange(toolbar::setTitle);

        editor.setFilters(new InputFilter[]{new RemoveFormattingFilter()});

        editor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                vm.onTextChanged(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        vm.setOnContentLoad(content -> {
            if (!editor.getText().toString().equals(content)) {
                editor.setText(content);
            }
        });

        applySystemUiVisibility(toolbar, editor);

        findViewById(android.R.id.content).setOnApplyWindowInsetsListener((v, insets) -> {
            bottomIgnoreAreaHeight = insets.getInsets(WindowInsets.Type.systemBars()).bottom;

            return insets;
        });
    }

    private void applySystemUiVisibility(View toolbar, View content) {
        getWindow().setDecorFitsSystemWindows(false);

        toolbar.setOnApplyWindowInsetsListener((v, insets) -> {
            Insets systemInsets = insets.getInsets(WindowInsets.Type.systemBars());
            v.setPadding(systemInsets.left, systemInsets.top, systemInsets.right, 0);

            return insets;
        });

        content.setOnApplyWindowInsetsListener((v, insets) -> {
            int padding = getResources().getDimensionPixelSize(R.dimen.content_padding);
            Insets systemInsets = insets.getInsets(WindowInsets.Type.systemBars());
            v.setPadding(
                    padding + systemInsets.left,
                    padding,
                    padding + systemInsets.right,
                    padding + systemInsets.bottom
            );

            return insets;
        });
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return vm;
    }

    @Override
    protected void onPause() {
        super.onPause();
        vm.save();

        editor.clearFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        vm.setOnTitleChange(title -> {});
        vm.setOnContentLoad(content -> {});

        if (isFinishing()) {
            vm.onCleared();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null || data.getData() == null) {
            finish();
            return;
        }

        vm.onUriSelected(data.getData());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_URI, vm.getUri());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getY() > getWindow().getDecorView().getHeight() - bottomIgnoreAreaHeight) {
            return false;
        }

        return super.dispatchTouchEvent(ev);
    }
}
