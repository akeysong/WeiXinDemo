package com.heimilink.hmqrcode;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity implements TextWatcher {
    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;
    //一个为二维码信息  一个为请求后台结果地址
    private EditText qrcodeText, bgUrlText;
    private Button qrBtn;
    private ImageView qrImg;
    private boolean catchHomeKey = false;
    //接收edittext的值
    private String content, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);//关键代码
        setContentView(R.layout.activity_main);
        qrBtn = (Button) findViewById(R.id.qr_btn);
        qrImg = (ImageView) findViewById(R.id.qr_img);
        qrcodeText = (EditText) findViewById(R.id.qrcode_content);
        bgUrlText = (EditText) findViewById(R.id.bg_connect_url);
        qrcodeText.addTextChangedListener(this);
        bgUrlText.addTextChangedListener(this);

        qrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (content != null && !content.isEmpty()) {
                    Bitmap bitmap = generateBitmap(content, 356, 356);
                    qrImg.setImageBitmap(bitmap);
                } else {
                    //系统默认禁止消息，连同Toast也不能弹出
                    Toast.makeText(MainActivity.this, "二维码信息为空", Toast.LENGTH_SHORT).show();
                }
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        content = qrcodeText.getText().toString();
        url = bgUrlText.getText().toString();
    }
}
