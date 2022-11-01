// Generated code from Butter Knife. Do not modify!
package client.halouhuandian.app15.view.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import client.halouhuandian.app15.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class A_Index_ViewBinding implements Unbinder {
  private A_Index target;

  @UiThread
  public A_Index_ViewBinding(A_Index target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public A_Index_ViewBinding(A_Index target, View source) {
    this.target = target;

    target.version = Utils.findRequiredViewAsType(source, R.id.version, "field 'version'", TextView.class);
    target.user = Utils.findRequiredViewAsType(source, R.id.user, "field 'user'", TextView.class);
    target.title = Utils.findRequiredViewAsType(source, R.id.title, "field 'title'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    A_Index target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.version = null;
    target.user = null;
    target.title = null;
  }
}
