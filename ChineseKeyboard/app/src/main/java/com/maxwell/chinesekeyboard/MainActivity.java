package com.maxwell.chinesekeyboard;

import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private EditText etContent;
    private KeyboardView keyboardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etContent = (EditText) findViewById(R.id.etContent);
        keyboardView = (KeyboardView) findViewById(R.id.keyboardView);
        Keyboard chineseKeyboard = new Keyboard(this, R.xml.chinese);
        keyboardView.setKeyboard(chineseKeyboard);
        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(true);
        keyboardView.setOnKeyboardActionListener(listener);

        //禁用系统自带的键盘
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {// api < 10
            etContent.setInputType(InputType.TYPE_NULL);
        } else {
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(etContent, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //禁用EditText的长按出现上下文菜单
        etContent.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            //在这里可以添加自己的菜单选项,再return true
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;//返回false表示屏蔽ActionMode菜单
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

        //点击EditText弹出汉字键盘
        etContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showKeyboard();
                return false;
            }
        });

    }

    public void showKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            keyboardView.setVisibility(View.VISIBLE);
        }
    }

    public void hideKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.VISIBLE) {
            keyboardView.setVisibility(View.GONE);
        }
    }

    private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void onPress(int primaryCode) {
            //删除键和完成键不显示预览
            if (primaryCode == Keyboard.KEYCODE_DELETE || primaryCode == Keyboard.KEYCODE_CANCEL) {
                keyboardView.setPreviewEnabled(false);
            } else { //其他键显示预览
                keyboardView.setPreviewEnabled(true);
            }

        }

        @Override
        public void onRelease(int primaryCode) {

        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Editable editable = etContent.getText();
            int start = etContent.getSelectionStart();//获得光标选中的开始位置
            if (primaryCode == Keyboard.KEYCODE_CANCEL) { //完成
                hideKeyboard();
            } else if (primaryCode == Keyboard.KEYCODE_DELETE) {//退格
                if (editable != null && editable.length() > 0) {
                    if (start > 0) {
                        editable.delete(start - 1, start);
                    }
                }
            } else { //汉字键
                editable.insert(start, Character.toString((char) primaryCode));
            }
        }

        @Override
        public void onText(CharSequence text) {

        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }
    };
}
