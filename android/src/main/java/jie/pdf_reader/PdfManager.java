package jie.pdf_reader;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/**
 * FlutterFullPdfViewerManager
 */
class PdfManager {

    boolean closed = false;
    PDFView pdfView;
    Activity mActivity;

    PdfManager(final Activity activity) {
        mActivity = activity;
        pdfView = new PDFView(mActivity, null);
    }

    void openPDF(String path) {

        File file = new File(path);
        pdfView.fromFile(file)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .load();
    }

    void resize(FrameLayout.LayoutParams params) {
        pdfView.setLayoutParams(params);
    }

    void close(MethodCall call, MethodChannel.Result result) {
        if (pdfView != null) {
            ViewGroup vg = (ViewGroup) (pdfView.getParent());
            vg.removeView(pdfView);
        }
        pdfView = null;
        if (result != null) {
            result.success(null);
        }

        closed = true;
        PdfReaderPlugin.channel.invokeMethod("onDestroy", null);
    }
    void change(){
        ViewGroup vg = (ViewGroup) (pdfView.getParent());
        vg.removeView(pdfView);
    }

    void close() {
        close(null, null);
    }
}