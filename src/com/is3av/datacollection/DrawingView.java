package com.is3av.datacollection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {
	private Paint       mPaint;
	public int width;
	public  int height;
	private Bitmap  mBitmap;
	private Canvas  mCanvas;
	private Path    mPath;
	private Paint   mBitmapPaint;
	Context context;
	private Paint circlePaint;
	private Path circlePath;
	private int counter=0;
	private int digit=0;
	ArrayList<Float> pressureList = new ArrayList<Float>();
	ArrayList<Float> sizeList = new ArrayList<Float>();
	ArrayList<Float> xCo = new ArrayList<Float>();
	ArrayList<Float> yCo = new ArrayList<Float>();
	ArrayList<Long> timeStamp = new ArrayList<Long>();
	String root = android.os.Environment.getExternalStorageDirectory().toString();
	File mydir = new File(root+"/digit");

	public DrawingView(Context c) {
		super(c);
		context = c; 
		init();
	}
	public DrawingView(Context context,AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public DrawingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	public void init() {

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(Color.GREEN);  //use a variable to set the color
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(36);  

		mPath = new Path();
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);  
		circlePaint = new Paint();
		circlePath = new Path();
		circlePaint.setAntiAlias(true);
		circlePaint.setColor(Color.GRAY);
		circlePaint.setStyle(Paint.Style.STROKE);
		circlePaint.setStrokeJoin(Paint.Join.MITER);
		circlePaint.setStrokeWidth(4f); 

	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		mCanvas.drawColor(Color.BLACK);

	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);

		canvas.drawPath( mPath,  mPaint);

		canvas.drawPath( circlePath,  circlePaint);
	}

	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 4;

	private void touch_start(float x, float y) {
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
	}
	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
			mX = x;
			mY = y;

			circlePath.reset();
			circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
		}
	}
	private void touch_up() {
		mPath.lineTo(mX, mY);
		circlePath.reset();
		// commit the path to our offscreen
		mCanvas.drawPath(mPath,  mPaint);
		// kill this so we don't double draw
		mPath.reset();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		float pressure;
		float size;
		float time;

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			pressure = event.getPressure();
			size = event.getSize();
			touch_start(x, y);
			time = SystemClock.uptimeMillis();
			long t = (long) time;
			xCo.add(x);
			yCo.add(y);
			pressureList.add(pressure);
			sizeList.add(size);
			timeStamp.add(t);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			pressure = event.getPressure();
			size = event.getSize();
			touch_move(x, y);
			//time = event.getEventTime()-event.getDownTime();
			time = SystemClock.uptimeMillis();
			 t = (long) time;
			timeStamp.add(t);
			xCo.add(x);
			yCo.add(y);
			pressureList.add(pressure);
			sizeList.add(size);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			pressure = event.getPressure()-event.getDownTime();
			size = event.getSize();
			time = SystemClock.uptimeMillis();
			 t = (long) (time);
			touch_up();
			timeStamp.add(t);
			xCo.add(x);
			yCo.add(y);
			pressureList.add(pressure);
			sizeList.add(size);
			invalidate();
			break;
		}
		return true;
	}

	public void clearCanvas() {		
		mCanvas.drawColor(Color.BLACK);
		invalidate();
	}  
	public void saveCanvas(String name) throws FileNotFoundException {
		
		String filename = name +"-"+ counter + "-" + digit+".txt"; 
		File mydir1 = new File(mydir+File.separator+name);
		mydir1.mkdir();
		Log.d("location",mydir1.toString());
		File file = new File(mydir1+File.separator+filename); 
		Log.d("file path",file.toString());
		try {
			counter++;
			FileWriter writer = new FileWriter(file);
			writer.append("X-Coordinates"+"\n");
			writer.append(xCo.toString());
			writer.flush();
			writer.append("\n"+"Y-Coordinates"+"\n");
			writer.append(yCo.toString());
			writer.flush();
			writer.append("\n"+"Pressure"+"\n");
			writer.append(pressureList.toString());
			writer.flush();
			writer.append("\n"+"size"+"\n");
			writer.append(sizeList.toString());
			writer.flush();
			writer.append("\n"+"time"+"\n");
			writer.append(timeStamp.toString());
			writer.flush();
			writer.close();
			if(counter%10==0) {
				digit++;
				counter=0;
			}
		} catch (IOException e) {
			System.out.println("File not created");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   
	clearCanvas();
	}
	
	public void authenticate() {
		//Authentication code goes here
		//Call async task
	}
}
