package com.androidsamples;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Sprite {

	// direction = 0 up, 1 left, 2 down, 3 right,

	// animation = 3 back, 1 left, 0 front, 2 right

	int[] DIRECTION_TO_ANIMATION_MAP = { 3, 1, 0, 2 };

	private static final int BMP_ROWS = 4;

	private static final int BMP_COLUMNS = 3;

	private int x = 0;

	private int y = 0;

	private int xSpeed = 5;

	private ToposGameView gameView;

	private Bitmap bmp;

	private int currentFrame = 0;

	private int width;

	private int height;

	private int ySpeed;

	public Sprite(ToposGameView gameView, Bitmap bmp) {

		this.width = bmp.getWidth() / BMP_COLUMNS;

		this.height = bmp.getHeight() / BMP_ROWS;

		this.gameView = gameView;

		this.bmp = bmp;

		Random rnd = new Random(System.currentTimeMillis());

		xSpeed = rnd.nextInt(50) - 5;

		ySpeed = rnd.nextInt(50) - 5;

	}

	private void update() {

		if (x >= gameView.getWidth() - width - xSpeed || x + xSpeed <= 0) {

			xSpeed = -xSpeed;

		}

		x = x + xSpeed;

		if (y >= gameView.getHeight() - height - ySpeed || y + ySpeed <= 0) {

			ySpeed = -ySpeed;

		}

		y = y + ySpeed;

		currentFrame = ++currentFrame % BMP_COLUMNS;

	}

	public void onDraw(Canvas canvas) {

		update();

		int srcX = currentFrame * width;

		int srcY = getAnimationRow() * height;

		Rect src = new Rect(srcX, srcY, srcX + width, srcY + height);

		Rect dst = new Rect(x, y, x + width, y + height);

		canvas.drawBitmap(bmp, src, dst, null);

	}

	// direction = 0 up, 1 left, 2 down, 3 right,

	// animation = 3 back, 1 left, 0 front, 2 right

	private int getAnimationRow() {

		double dirDouble = (Math.atan2(xSpeed, ySpeed) / (Math.PI / 2) + 2);

		int direction = (int) Math.round(dirDouble) % BMP_ROWS;

		return DIRECTION_TO_ANIMATION_MAP[direction];

	}

}
