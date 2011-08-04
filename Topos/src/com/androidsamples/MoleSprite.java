package com.androidsamples;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.opengl.GLUtils;
import android.util.Log;
import android.view.View;

import com.bunkerdev.savemycarrots.R;

public class MoleSprite extends View{

	public static final int HOLE = 4;
	public static final int DIGUP1 = 3;
	public static final int DIGUP2 = 2;
	public static final int DIGUP3 = 1;
	public static final int DIGUPFULL = 0;
	public static final int HIT1 = 3;
	public static final int HITFULL = 2;
	public static final int ANIMATIONBIG = 2;
	public static final int ANIMATIONHIT = 1;
	public static final int ANIMATIONWEASEL = 4;
	public static final int ANIMATIONWEASELHIT = 5;
	public static final int ANIMATIONBIGHIT = 3;
	private static final long ANIMATION_HIT_TIME = 300;
	private static final long ANIMATION_DIGGING_TIME = 300;
	private static final int MAX_DIGGING_TICKS = 4; //total number frames of animation -1
	private static final int MAX_HIT_TICKS = 2;
	private static final int BMP_ROWS = 5;
	private static final int BMP_COLUMNS = 6;

	private int posx;
	private int posy;
	private int x;
	private int y;
	private boolean shouldLoadTexture;
	private float side = 500f;
	private int textureId;

	private ToposGameView view;
	private static Bitmap bmp;
	private int width;
	private int height;	
	private int status; //Each row, each frame of the animation
	private int animation = 0; //Each column, for example entering hole or being hit
	private boolean isHit;
	private boolean isDigging;
	private long animationHitStartTime;
	private long animationDigStartTime;
	private int diggingDirection = 0; //Up-> -1   Down -> 1
	private int diggingTick = 0; //How many frames have we already shown
	private int bigClicks;
	
	private float vertices[] = {
		      -0.5f,  0.5f, 0.0f,  // 0, Top Left
		      -0.5f, -0.5f, 0.0f,  // 1, Bottom Left
		       0.5f, -0.5f, 0.0f,  // 2, Bottom Right
		       0.5f,  0.5f, 0.0f,  // 3, Top Right
		};

	private short[] indices = { 0, 1, 2, 0, 2, 3 };
	private float[] textureCoords = new float[8];
	private FloatBuffer vertexBuffer;
	private ShortBuffer indexBuffer;
	private FloatBuffer textureBuffer;

	public MoleSprite(ToposGameView view, int posx, int posy) {
		super(view.getContext());
		this.view = view;
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.sprites);
		this.width = 100;		
		this.height = 100;

		this.posx=posx;
		this.posy=posy;

		status = HOLE;
		isDigging = false;
		isHit = false;
		bigClicks = 0;
		
		shouldLoadTexture = true;
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);

		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		indexBuffer = ibb.asShortBuffer();
		indexBuffer.put(indices);
		indexBuffer.position(0);
		
	}

	public int getX() {
		int gap_width = view.getWidth()/10;
		return gap_width/2+(posx)*(view.getWidth()/3);
	}
	public int getY() {
		int gap_height = view.getHeight()/10;
		return gap_height/2+(posy)*view.getHeight()/4;
	}
	public int getMoleWidth() {
		int gap_width = view.getWidth()/10;
		return view.getWidth()/3-gap_width;
	}
	public int getMoleHeight() {
		int gap_height = view.getHeight()/10;
		return view.getHeight()/4-gap_height;
	}
	public int getStatus() {
		return status;
	}
	public void changeStatus(int status){
		this.status = status;
	}

	public boolean isBig() {
		return animation == ANIMATIONBIG;
	}

	public void setBig() {
		this.animation = ANIMATIONBIG;
	}
	
	public boolean isWeasel() {
		return animation == ANIMATIONWEASEL;
	}

	public void setWeasel() {
		this.animation = ANIMATIONWEASEL;
	}

	public void resetBigClicks(){
		bigClicks = 0;
	}

	public void addBigClick(){
		bigClicks++;
	}

	public Integer getBigClicks(){
		return bigClicks;
	}
	public void draw(GL10 gl) {
		dig();
		hit();
        gl.glFrontFace(GL10.GL_CCW);
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glCullFace(GL10.GL_BACK);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

        if (shouldLoadTexture) {
                loadGLTexture(gl);
                shouldLoadTexture = false;
        }
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        
        if(view.getStatusMap().containsKey(status+""+animation+getX()+getY())){
        	textureBuffer = view.getStatusMap().get(status+""+animation+getX()+getY());
        }else{
        	float zeroW = (width*animation)/(width*BMP_COLUMNS);
        	float oneW =  (width*animation+width)/(width*BMP_COLUMNS);
        	float zeroH = (height*status)/(height*BMP_ROWS);
        	float oneH = (height*status+status)/(height*BMP_ROWS);
        	Log.i("TAG", "zW "+zeroW+" oW "+oneW+" zH " +zeroH+" oH "+oneH);
        	textureCoords[0] = zeroW;
        	textureCoords[1] = zeroH;
        	textureCoords[2] = zeroW;
        	textureCoords[3] = oneH;
        	textureCoords[4] = oneW;
        	textureCoords[5] = oneH;
        	textureCoords[6] = oneW;
        	textureCoords[7] = zeroH;
        	
        	ByteBuffer byteBuf = ByteBuffer.allocateDirect(
                    textureCoords.length * 4);
			byteBuf.order(ByteOrder.nativeOrder());
			textureBuffer = byteBuf.asFloatBuffer();
			textureBuffer.put(textureCoords);
			textureBuffer.position(0);
			view.getStatusMap().put(status+""+animation+getX()+getY(), textureBuffer);
        }
        
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);

		gl.glPushMatrix();
		gl.glTranslatef(getX(), getY(), 0f);
		gl.glScalef(side, side, 0f);
        gl.glDrawElements(GL10.GL_TRIANGLES, indices.length,
                GL10.GL_UNSIGNED_SHORT, indexBuffer);
		gl.glPopMatrix();
		
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisable(GL10.GL_CULL_FACE);
	}
	
	private void loadGLTexture(GL10 gl) { // New function
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        textureId = textures[0];
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                        GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                        GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                        GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                        GL10.GL_REPEAT);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);
	}
	
	public boolean isClicked(float eventx, float eventy){
		boolean coordx = getX() <= eventx && getX()+getMoleWidth() >= eventx;
		boolean coordy = getY() <=eventy && getY()+getMoleHeight() >= eventy;

		if(coordx && coordy){
			return true;
		}

		return false;
	}

	public boolean isHit(){
		return isHit;
	}

	public String toString(){
		return "Mole x: "+x+", y: "+y+", width: "+width+", \nheight: "+height+" ,dstWidth: "+view.getWidth()/3+", dstHeight: "+view.getHeight()/4;
	}

	private void hit(){
		if(isHit){
			isDigging = false;
			diggingTick = 0;
			
			if(animation == ANIMATIONBIGHIT || animation == ANIMATIONWEASELHIT
					|| animation == ANIMATIONHIT){
				//Do nothing
			}
			else if(isBig())
				animation = ANIMATIONBIGHIT;
			else if(isWeasel())
				animation = ANIMATIONWEASELHIT;
			else
				animation = ANIMATIONHIT;
			
			Long timeElapsed = System.currentTimeMillis() - animationHitStartTime;
			status = HITFULL;
			if(timeElapsed >= ANIMATION_HIT_TIME/MAX_HIT_TICKS){
				status = HIT1;
			}
			if(timeElapsed >= ANIMATION_HIT_TIME){
				status = HOLE;
				isHit = false;
				animation = 0;
			}


		}

	}

	public boolean isDigging(){
		return isDigging;
	}

	public boolean isDiggingDown(){
		return isDigging && diggingDirection == 1;
	}

	private void dig(){
		if(isDigging){
			Long timeElapsed = System.currentTimeMillis() - animationDigStartTime;
			if(diggingTick != MAX_DIGGING_TICKS){
				if(timeElapsed >= diggingTick*ANIMATION_DIGGING_TIME/MAX_DIGGING_TICKS){
					status = status+diggingDirection;
					diggingTick++;
				}
			}else{
				diggingTick = 0;
				if(isDiggingDown()){
					animation = 0;
				}
				isDigging = false;
			}

		}
	}

	public void doHit(){
		animationHitStartTime = System.currentTimeMillis();
		isHit = true;
	}
	public void digUp(){
		diggingDirection = -1;
		animationDigStartTime = System.currentTimeMillis();
		isDigging = true;
	}

	public void digDown(){
		
		diggingDirection = 1;
		animationDigStartTime = System.currentTimeMillis();
		isDigging = true;
	}

	public boolean equals(Object o){
		boolean resX=false;
		boolean resY=false;
		if(o instanceof MoleSprite){
			resX=this.posx==((MoleSprite) o).posx;
			resY=this.posy==((MoleSprite) o).posy;
		}
		return resX && resY;
	}

	public long getDigStartTime() {
		return animationDigStartTime+ANIMATION_DIGGING_TIME;
	}

	public void reset(){
		animation = 0;
		changeStatus(HOLE);
		diggingTick=0;
		isDigging = false;
		isHit = false;
	}

}
