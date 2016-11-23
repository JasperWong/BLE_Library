

/**
 * Created by project_x on 2015/4/25.
 */

package com.jasperwong.ble.activity;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

public class WaverView extends View{
    private int XPoint = 60;
    private int YPoint = 260;
    private float XScale = 8;  //刻度长度
    private float YScale = 40;
    private int XLength = 380;
    private int YLength = 240;
    int width = 0;
    int height = 0;
    int centerY = 0;

    private int MaxDataSize = 1024;

    private List<Integer> data = new ArrayList<Integer>();

    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        // before measure, get the center of view
        width = (int) getWidth() ;
        height = (int) getHeight();

    }


    private String[] YLabel = new String[(int)(YLength / YScale)];

    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            if(msg.what == 0x1234){
                WaverView.this.invalidate();
            }
        };
    };


    public WaverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        for(int i=0; i<YLabel.length; i++){
            YLabel[i] = (i + 1) + "M/s";
        }

//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                while(true){
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    if(data.size() >= MaxDataSize){
//                        data.remove(0);
//                    }
//                    data.add(new Random().nextInt(4) + 1);
//                    handler.sendEmptyMessage(0x1234);
//                }
//            }
//        }).start();
    }

    public void addData(int value)
    {
        if(data.size() >= MaxDataSize){
            data.remove(0);
        }
        data.add(value);
        handler.sendEmptyMessage(0x1234);

    }

    int target = 0;
    public void setTarget(int value)
    {
        target = value;
    }




    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true); //去锯齿
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2);

        Paint redPaint = new Paint();
        redPaint.setStyle(Paint.Style.STROKE);
        redPaint.setAntiAlias(true); //去锯齿
        redPaint.setColor(Color.RED);
        redPaint.setStrokeWidth(5);

        int width = canvas.getWidth();
        int height = canvas.getHeight();
        XPoint = (int)(0);
        YPoint = (int)(height);
        YLength = height;
        XLength = width;
        //间隔比较少！
        XScale = 1.0f;
        YScale = height / 180.0f;
        centerY = height / 2;
        MaxDataSize = (int)(XLength / XScale);


        //画Y轴
        canvas.drawLine(0, 0, 0, YLength, paint);

        //Y轴箭头
        canvas.drawLine(XPoint, YPoint - YLength, XPoint - 3, YPoint-YLength + 6, paint);  //箭头
        canvas.drawLine(XPoint, YPoint - YLength, XPoint + 3, YPoint-YLength + 6 ,paint);

        //添加刻度和文字
        for(int i=0; i < 5; i++) {
            canvas.drawLine(0, YPoint - i * height/4, width, YPoint - i * height/4, paint);  //刻度

//            canvas.drawText(YLabel[i], 0, YPoint - i * YScale, paint);//文字
        }

        //画X轴
        canvas.drawLine(XPoint, centerY, XPoint + XLength, centerY, paint);
        canvas.drawLine(XPoint, centerY - target * YScale, XPoint + XLength, centerY - target * YScale,redPaint );


        //画数据
        if(data.size() > 1){
            for(int i=1; i<data.size(); i++){
                canvas.drawLine(XPoint + (i-1) * XScale, centerY - data.get(i-1) * YScale,XPoint + i * XScale, centerY - data.get(i) * YScale, paint);
            }
        }
    }
}