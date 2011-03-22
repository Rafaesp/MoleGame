package com.androidsamples;

import android.content.Context;
<<<<<<< HEAD
import android.view.View;
=======
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
>>>>>>> fd629bc0c7d6c47a3b27f5529593c5415ed3ffeb

public class ToposGameView extends SurfaceView{

	private Bitmap bmp;
	private SurfaceHolder holder;

	public ToposGameView(Context context) {
		super(context);
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
		holder= getHolder();
		holder.addCallback(new Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder arg0) {

			}

			@Override
			public void surfaceCreated(SurfaceHolder arg0) {
				// TODO Bloquear Canvas, usar, y desbloquear, rendimiento
				Canvas c= holder.lockCanvas();
				onDraw(c);
				holder.unlockCanvasAndPost(c);

			}

			@Override
			public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}
		});

	}

	protected void onDraw(Canvas canvas) {

		canvas.drawColor(Color.BLACK);
		canvas.drawBitmap(bmp, 10, 10, null);

	}


}
