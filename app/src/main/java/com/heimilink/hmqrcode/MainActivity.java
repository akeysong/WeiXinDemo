package com.heimilink.hmqrcode;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {
    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;
    private WebView wechatView;
    private ProgressBar mBar;
    private Button qrBtn;
    private ImageView qrImg;
    private boolean catchHomeKey = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);//关键代码
        setContentView(R.layout.activity_main);
        wechatView = (WebView) findViewById(R.id.login_view);
        mBar = (ProgressBar) findViewById(R.id.web_progress_bar);
        qrBtn = (Button) findViewById(R.id.qr_btn);
        qrImg = (ImageView) findViewById(R.id.qr_img);
        WebSettings settings = wechatView.getSettings();
        settings.setJavaScriptEnabled(true);
        wechatView.loadUrl("http://www.baidu.com");
        wechatView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("bopai", "request_url=" + url);

                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        wechatView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mBar.setVisibility(View.GONE);
                } else {
                    if (View.GONE == mBar.getVisibility()) {
                        mBar.setVisibility(View.VISIBLE);
                    }
                    mBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        qrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = generateBitmap("http://www.baidu.com", 300, 300);
                qrImg.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    protected void onUserLeaveHint() {
        Log.d("bopai", "onUserLeaveHint");
        super.onUserLeaveHint();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("bopai", "KeyEvent=" + event.getAction());
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Toast.makeText(MainActivity.this, "不可返回", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            Toast.makeText(MainActivity.this, "点击home键", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 创建二维码
     *
     * @param content
     * @param width
     * @param height
     * @return
     */
    private Bitmap generateBitmap(String content, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            BitMatrix encode = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (encode.get(j, i)) {
                        pixels[i * width + j] = 0x00000000;
                    } else {
                        pixels[i * width + j] = 0xffffffff;
                    }
                }
            }
            return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);
        } catch (WriterException e) {
            Log.e("bopai", "generate_qrcode_error=" + e.getMessage());
        }
        return null;
    }
}
