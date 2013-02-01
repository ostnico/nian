package com.lolbro.nian.customs;

import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;

import android.content.Context;
import android.os.SystemClock;
import android.view.MotionEvent;

public class SwipeScene extends Scene implements IOnSceneTouchListener {

	public static final int MIN_SWIPE_DISTANCE = 10;
	public static final int SWIPE_SENS = 200;
	
	private SwipeListener listener;
	
	public interface SwipeListener{
		public static final int DIRECTION_UP = 1;
		public static final int DIRECTION_DOWN = 2;
		public static final int DIRECTION_LEFT = 3;
		public static final int DIRECTION_RIGHT = 4;
		
		public void onSwipe(int direction);
	}
	
	public void registerForSwipes(Context context, SwipeListener listener) {
		this.listener = listener;
		setOnSceneTouchListener(this);
	}

	private float lastX;
	private float lastLastX;
	private float lastY;
	private float lastLastY;
	private long lastTime;
	private long lastLastTime;
	private int touchFrames;
	private boolean motionDetected;
	
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		MotionEvent event = pSceneTouchEvent.getMotionEvent();
		
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:			
			touchFrames = 1;
			lastX = event.getX();
			lastY = event.getY();
			lastTime = SystemClock.uptimeMillis();
			motionDetected = false;
			break;
		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_UP:
			if(motionDetected){
				return true;
			}
			if(touchFrames > 2){
				float dTime = (SystemClock.uptimeMillis() - lastLastTime) / 1000f;
				float dX = Math.abs(event.getX() - lastLastX);
				float dY = Math.abs(event.getY() - lastLastY);
				
				if(dY > dX){
					float velocity = dY / dTime;
					if(Math.abs(velocity) > SWIPE_SENS && Math.abs(dY) > MIN_SWIPE_DISTANCE){
						motionDetected = true;
						if(event.getY() < lastLastY){
							listener.onSwipe(SwipeListener.DIRECTION_UP);
						} else {
							listener.onSwipe(SwipeListener.DIRECTION_DOWN);						
						}
					}
				} else {					
					float velocity = dX / dTime;
					if(Math.abs(velocity) > SWIPE_SENS && Math.abs(dX) > MIN_SWIPE_DISTANCE){
						motionDetected = true;
						if(event.getX() < lastLastX){
							listener.onSwipe(SwipeListener.DIRECTION_LEFT);
						} else {
							listener.onSwipe(SwipeListener.DIRECTION_RIGHT);						
						}
					}
				}
				
			}
			
			touchFrames++;
			lastLastX = lastX;
			lastLastY = lastY;
			lastX = event.getX();
			lastY = event.getY();
			lastLastTime = lastTime;
			lastTime = SystemClock.uptimeMillis();
			break;
		}
		
		return true;
	}
	
}
