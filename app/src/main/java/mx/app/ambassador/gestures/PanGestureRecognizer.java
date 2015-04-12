/*
 * Copyright 2012 Greg Billetdeaux
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mx.app.ambassador.gestures;

import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;

public class PanGestureRecognizer implements OnTouchListener {

	public static int DOUBLE_TAP_TIME = 300;
	
	public float pressY;
	public float pressX;
	public float scrollY;
	public float scrollX;
	
	private View view;
	private PanGestureListener listener;
	private VelocityTracker tracker;
	
	private float lastY;
	private float lastX;
	private long timer;
	
	
	public PanGestureRecognizer(View v, PanGestureListener listener) {
		
		this.view = v;
		this.listener = listener;
		this.view.setOnTouchListener(this);
		this.timer = System.currentTimeMillis();
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
						
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		switch(action) {
		case MotionEvent.ACTION_DOWN:
			if (timer + DOUBLE_TAP_TIME > System.currentTimeMillis()) {
				listener.onDoubleTap(v);
				action = 0;
				break;
			}
			tracker = VelocityTracker.obtain();
			tracker.addMovement(event);
			pressY = event.getRawY();
			pressX = event.getRawX();
			lastY = event.getRawY();
			lastX = event.getRawX();
			timer = System.currentTimeMillis();
			listener.onPanStart(v, lastX, lastY);
			break;
		case MotionEvent.ACTION_UP:
			tracker.computeCurrentVelocity(1000);
			listener.onLift(v, tracker.getXVelocity(), tracker.getYVelocity());
			tracker.recycle();
			lastY = event.getRawY();
			lastX = event.getRawX();
			listener.onPanStop(v, lastX, lastY);
			break;
		case MotionEvent.ACTION_MOVE:
			tracker.addMovement(event);
			lastY = event.getRawY();
			lastX = event.getRawX();
			timer = System.currentTimeMillis();
			listener.onPanMove(v, lastX, lastY);
			action = 0;
			break;
		}
		return true;
	}
	
	public void resetReference() {
		pressY = lastY;
		pressX = lastX;
	}

}
