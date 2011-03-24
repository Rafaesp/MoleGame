package com.androidsamples;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

 

public class Sprite {
		private static final int BMP_COLUMNS=3;
		private static final int BMP_ROWS=4;
		private int x = 0; 
		private int y = 0; 
		private int currentFrame;

       private int xSpeed = 5;

       private ToposGameView gameView;

       private Bitmap bmp;

       private int width;
       private int height;
       
      

       public Sprite(ToposGameView gameView, Bitmap bmp) {

             this.gameView=gameView;

             this.bmp=bmp;
             this.width=bmp.getWidth()/BMP_COLUMNS;
             this.height=bmp.getHeight()/BMP_ROWS;

       }

 

       private void update() {

             if (x > gameView.getWidth() - bmp.getWidth()/BMP_COLUMNS - xSpeed) {

                    xSpeed = -5;

             }

             if (x + xSpeed< 0) {

                    xSpeed = 5;

             }

             x = x + xSpeed;
             currentFrame = ++currentFrame%BMP_COLUMNS;

       }

      

       public void onDraw(Canvas canvas) {

             update();

            
			int srcX = currentFrame * width;

             int srcY = 1 * height;

             Rect src = new Rect(srcX, srcY, srcX + width, srcY + height);

             Rect dst = new Rect(x, y, x + width, y + height);
             
             canvas.drawBitmap(bmp, src , dst , null);

       }

}   