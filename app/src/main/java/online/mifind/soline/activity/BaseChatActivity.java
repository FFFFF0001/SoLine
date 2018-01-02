package online.mifind.soline.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.vise.common_base.activity.BaseActivity;
import com.vise.common_base.manager.AppManager;

import online.mifind.soline.utils.StatusUtil;

public abstract class BaseChatActivity extends BaseActivity implements AppCompatCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        AppManager.getAppManager().addActivity(this);
    }

    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        StatusUtil.setMeizuStatusBarDarkIcon(this, true);
        StatusUtil.setMiuiStatusBarDarkMode(this, true);
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        AppCompatDelegate.create(this, this).setSupportActionBar(toolbar);
    }

    @Override
    public void onSupportActionModeStarted(ActionMode mode) {
    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {
    }

    @Nullable
    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initWidget();
        initData();
        initEvent();
    }

    protected abstract void initWidget();

    protected abstract void initData();

    protected abstract void initEvent();
}
