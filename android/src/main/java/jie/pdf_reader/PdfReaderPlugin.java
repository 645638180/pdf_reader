package jie.pdf_reader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.widget.FrameLayout;

import java.io.File;
import java.util.Map;

import androidx.annotation.NonNull;
import io.flutter.app.FlutterActivity;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * PdfReaderPlugin
 */
public class PdfReaderPlugin implements FlutterPlugin, MethodCallHandler, PluginRegistry.ActivityResultListener, ActivityAware {
    private static final String TAG = "PdfReaderPlugin.java";
    static MethodChannel channel;
    Activity activity;
    private PdfManager pdfManager;
    private static PluginRegistry.Registrar registrar;


    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "pdf_reader");
        channel.setMethodCallHandler(this);
    }

    public void registerWith(Registrar registrar) {
        PdfReaderPlugin.registrar = registrar;
        activity = registrar.activity();
        channel = new MethodChannel(registrar.messenger(), "pdf_reader");
        channel.setMethodCallHandler(new PdfReaderPlugin());
    }


    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "launch":
                openPDF(call, result);
                break;
            case "resize":
                resize(call, result);
                break;
            case "close":
                close(call, result);
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    private void openPDF(MethodCall call, MethodChannel.Result result) {
        String path = call.argument("path");
        Log.e("openPDF: ", "path:" + path);
        if (pdfManager != null) {
            pdfManager.close();
        }
        try {
        File file = new File(path);
        if (file.exists()) {
            if (pdfManager == null || pdfManager.closed) {

                pdfManager = new PdfManager(activity);
            }
            FrameLayout.LayoutParams params = buildLayoutParams(call);
            activity.addContentView(pdfManager.pdfView, params);
            pdfManager.openPDF(path);
            result.success(true);
        } else {
            result.error("文件不存在","","");
        }
        } catch (Exception e) {
            result.error(e.toString(),"","");
        }
    }

    private void resize(MethodCall call, final MethodChannel.Result result) {
        if (pdfManager != null) {
            FrameLayout.LayoutParams params = buildLayoutParams(call);
            pdfManager.resize(params);
        }
        result.success(null);
    }

    private void close(MethodCall call, MethodChannel.Result result) {
        if (pdfManager != null) {
            pdfManager.close(call, result);
            pdfManager = null;
        }
    }

    private FrameLayout.LayoutParams buildLayoutParams(MethodCall call) {
        Map<String, Number> rc = call.argument("rect");
        FrameLayout.LayoutParams params;
        if (rc != null) {
            params = new FrameLayout.LayoutParams(dp2px(activity, rc.get("width").intValue()), dp2px(activity, rc.get("height").intValue()));
            params.setMargins(dp2px(activity, rc.get("left").intValue()), dp2px(activity, rc.get("top").intValue()), 0, 0);
        } else {
            Display display = activity.getWindowManager().getDefaultDisplay();

            Point size = new Point();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                display.getSize(size);
            }
            int width = size.x;
            int height = size.y;
            params = new FrameLayout.LayoutParams(width, height);
        }
        return params;
    }

    private int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPlugin.FlutterPluginBinding binding) {
    }

    @Override
    public boolean onActivityResult(int i, int i1, Intent intent) {
        return pdfManager != null;
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding activityPluginBinding) {
        activity = activityPluginBinding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding activityPluginBinding) {

    }

    @Override
    public void onDetachedFromActivity() {

    }
}
