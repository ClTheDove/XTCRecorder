package xds.fishy.recorder.widget.popupwindow.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import xds.fishy.recorder.R;
import xds.fishy.util.file.FileUtil;
import xds.fishy.util.widget.maxlengthwatcher.MaxLengthWatcher;

public class PopupWindowDialog {
    String enter_name;
    private Context context;
    private View rootView;
    private PopupWindow popupWindow;
    private RelativeLayout rl_show_delete;
    private RelativeLayout rl_show_manage;
    private RelativeLayout rl_show_rename;
    private RelativeLayout rl_show_name;
    private InputMethodManager inputManager;

    @SuppressLint("InflateParams")
    public PopupWindowDialog(Context context) {
        this.context = context;
        rootView = LayoutInflater.from(context).inflate(R.layout.popup_window_dialog,
                null);
        popupWindow = new PopupWindow(rootView, -1, -1, false);
        popupWindow.setFocusable(true);
        rl_show_delete = rootView.findViewById(R.id.rl_show_delete);
        rl_show_manage = rootView.findViewById(R.id.rl_show_manage);
        rl_show_rename = rootView.findViewById(R.id.rl_show_rename);
        rl_show_name = rootView.findViewById(R.id.rl_show_save);
        inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public void dismiss() {
        if (popupWindow != null) popupWindow.dismiss();
    }

    public void showDelete(View view, final DeleteListener deleteListener) {
        if (!popupWindow.isShowing()) {
            rl_show_manage.setVisibility(View.GONE);
            rl_show_rename.setVisibility(View.GONE);
            rl_show_name.setVisibility(View.GONE);
            rl_show_delete.setVisibility(View.VISIBLE);
            ImageView iv_cancel_delete = rootView.findViewById(R.id.iv_cancel_delete);
            ImageView iv_delete = rootView.findViewById(R.id.iv_delete);
            if (deleteListener != null) {
                iv_delete.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        deleteListener.onDelete();
                    }
                });
                iv_cancel_delete.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        deleteListener.onCancel();
                    }
                });
            }
            popupWindow.showAtLocation(view, Gravity.START, 0, 0);
        }
    }

    public void showManage(View view, final ManageListener manageListener) {
        if (!popupWindow.isShowing()) {
            rl_show_delete.setVisibility(View.GONE);
            rl_show_rename.setVisibility(View.GONE);
            rl_show_name.setVisibility(View.GONE);
            rl_show_manage.setVisibility(View.VISIBLE);
            ImageView iv_manage_rename = rootView.findViewById(R.id.iv_manage_rename);
            ImageView iv_manage_delete = rootView.findViewById(R.id.iv_manage_delete);
            Button btn_cancel_manage = rootView.findViewById(R.id.btn_cancel_manage);
            if (manageListener != null) {
                iv_manage_rename.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        manageListener.onRename();
                    }
                });
                iv_manage_delete.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        manageListener.onDelete();
                    }
                });
                btn_cancel_manage.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View view) {
                        manageListener.onCancel();
                    }
                });
            }
            popupWindow.showAtLocation(view, Gravity.START, 0, 0);
        }
    }

    public void showRename(View view, final RenameListener renameListener) {
        if (!popupWindow.isShowing()) {
            rl_show_delete.setVisibility(View.GONE);
            rl_show_manage.setVisibility(View.GONE);
            rl_show_name.setVisibility(View.GONE);
            rl_show_rename.setVisibility(View.VISIBLE);
            final EditText ed_rename_name = rootView.findViewById(R.id.ed_rename_name);
            final TextView tv_rename_content = rootView.findViewById(R.id.tv_rename_content);
            TextView tv_rename_cancel = rootView.findViewById(R.id.tv_rename_cancel);
            final TextView tv_rename_confirm = rootView.findViewById(R.id.tv_rename_confirm);
            ed_rename_name.setFilters(new InputFilter[]{new InputFilter.LengthFilter(24)});
            ed_rename_name.addTextChangedListener(new MaxLengthWatcher(context, 24));
            ed_rename_name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    enter_name = FileUtil.getValidFilename(editable.toString());
                    if (TextUtils.isEmpty(enter_name)) {
                        tv_rename_content.setText(R.string.hint_enter);
                        return;
                    }
                    tv_rename_content.setText(enter_name);
                }
            });
            ed_rename_name.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    if (i == 66) {
                        inputManager.hideSoftInputFromWindow(ed_rename_name.getWindowToken(), 0);
                        tv_rename_confirm.callOnClick();
                    }
                    return true;
                }
            });
            tv_rename_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inputManager.showSoftInput(ed_rename_name, 0);
                }
            });
            if (renameListener != null) {
                tv_rename_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        renameListener.onCancel();
                    }
                });
                tv_rename_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        enter_name = FileUtil.getValidFilename(ed_rename_name.getText().toString());
                        dismiss();
                        if (TextUtils.isEmpty(enter_name)) {
                            Toast.makeText(context, context.getResources().
                                    getString(R.string.please_enter_file_name), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        renameListener.onRename(enter_name);
                    }
                });
            }
            popupWindow.showAtLocation(view, Gravity.START, 0, 0);
            ed_rename_name.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (inputManager.showSoftInput(ed_rename_name, 0))
                        ed_rename_name.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
    }

    public void showSave(View view, final SaveListener saveListener) {
        if (!popupWindow.isShowing()) {
            rl_show_delete.setVisibility(View.GONE);
            rl_show_manage.setVisibility(View.GONE);
            rl_show_rename.setVisibility(View.GONE);
            rl_show_name.setVisibility(View.VISIBLE);
            final EditText ed_save_name = rootView.findViewById(R.id.ed_save_name);
            final TextView tv_save_content = rootView.findViewById(R.id.tv_save_content);
            TextView tv_save_cancel = rootView.findViewById(R.id.tv_save_cancel);
            TextView tv_save_confirm = rootView.findViewById(R.id.tv_save_confirm);
            ed_save_name.setFilters(new InputFilter[]{new InputFilter.LengthFilter(24)});
            ed_save_name.addTextChangedListener(new MaxLengthWatcher(context, 24));
            ed_save_name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    enter_name = FileUtil.getValidFilename(editable.toString());
                    if (TextUtils.isEmpty(enter_name)) {
                        tv_save_content.setText(R.string.hint_enter);
                        return;
                    }
                    tv_save_content.setText(enter_name);
                }
            });
            tv_save_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inputManager.showSoftInput(ed_save_name, 0);
                }
            });
            ed_save_name.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    if (i == 66)
                        inputManager.hideSoftInputFromWindow(ed_save_name.getWindowToken(), 0);
                    return true;
                }
            });
            if (saveListener != null) {
                tv_save_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveListener.onCancel();
                    }
                });
                tv_save_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        enter_name = FileUtil.getValidFilename(ed_save_name.getText().toString());
                        if (TextUtils.isEmpty(enter_name)) {
                            Toast.makeText(context, context.getResources().
                                    getString(R.string.please_enter_file_name), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        saveListener.onSave(enter_name);
                    }
                });
            }
            popupWindow.showAtLocation(view, Gravity.START, 0, 0);
            /*
            ed_save_name.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (inputManager.showSoftInput(ed_save_name, 0))
                        ed_save_name.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
            */
        }
    }

    public interface DeleteListener {
        void onDelete();

        void onCancel();
    }

    public interface ManageListener {
        void onDelete();

        void onRename();

        void onCancel();
    }

    public interface RenameListener {
        void onRename(String name);

        void onCancel();
    }

    public interface SaveListener {
        void onSave(String name);

        void onCancel();
    }
}
