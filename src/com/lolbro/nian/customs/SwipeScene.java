package com.lolbro.nian.customs;

import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.lolbro.nian.R;

public class SwipeScene extends Scene implements IOnSceneTouchListener {
	
	
	private int mMinSwipeDistance;
	private SwipeListener listener;
	
	public interface SwipeListener{
		public static final int DIRECTION_UP = 1;
		public static final int DIRECTION_DOWN = 2;
		public static final int DIRECTION_LEFT = 3;
		public static final int DIRECTION_RIGHT = 4;
		
		public void onSwipe(int direction);
	}
	
	public SwipeScene(Context context){
		mMinSwipeDistance = (int) context.getResources().getDimension(R.dimen.min_swipe_distance);
		Log.d("nian", "min distance " + mMinSwipeDistance);
	}
	
	public void registerForSwipes(Context context, SwipeListener listener) {
		this.listener = listener;
		setOnSceneTouchListener(this);
	}

	private float touchDownX;
	private float touchDownY;
	private boolean motionDetected;
	
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		MotionEvent event = pSceneTouchEvent.getMotionEvent();
		
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			touchDownX = event.getX();
			touchDownY = event.getY();
			motionDetected = false;
			break;
		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_UP:
			if(motionDetected){
				return true;
			}
			float dX = Math.abs(event.getX() - touchDownX);
			float dY = Math.abs(event.getY() - touchDownY);
			
			if(dX > dY){
				if(Math.abs(dX) > mMinSwipeDistance){
					motionDetected = true;
					if(event.getX() < touchDownX){
						listener.onSwipe(SwipeListener.DIRECTION_LEFT);
					} else {
						listener.onSwipe(SwipeListener.DIRECTION_RIGHT);						
					}
				}
			} else {
				if(Math.abs(dY) > mMinSwipeDistance){
					motionDetected = true;
					if(event.getY() < touchDownY){
						listener.onSwipe(SwipeListener.DIRECTION_UP);
					} else {
						listener.onSwipe(SwipeListener.DIRECTION_DOWN);						
					}
				}				
			}
			break;
		}
		
		return true;
	}
	
}
