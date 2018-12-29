package jp.fmaru.app.livechatapp.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.meg7.widget.BaseImageView;

import jp.fmaru.app.livechatapp.R;

/**
 * Created by kenvin on 22/10/2016.
 */
public class RoundCornerImageView extends BaseImageView {
    private static final int DEFAULT_CORNER = 10;
    private int conner = DEFAULT_CORNER;
    public RoundCornerImageView(Context context) {
        super(context);
    }

    public RoundCornerImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundCornerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundCornerImageView, defStyle, 0);
        conner = a.getDimensionPixelSize(R.styleable.RoundCornerImageView_corner, DEFAULT_CORNER);
        a.recycle();
    }

    private Bitmap getBitmap(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        canvas.drawRoundRect(new RectF(0.0f, 0.0f, width, height), conner, conner, paint);

        return bitmap;
    }

    @Override
    public Bitmap getBitmap() {
        return getBitmap(getWidth(), getHeight());
    }
}
