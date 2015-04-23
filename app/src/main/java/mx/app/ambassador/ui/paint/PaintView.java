package mx.app.ambassador.ui.paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class PaintView extends View {
	
	public static final int TYPE_PENCIL = 0;
	public static final int TYPE_LINE   = 1;
	public static final int TYPE_ERASE  = 2;

	private Paint   mPaint;
    private Bitmap  mBitmap;
    private Canvas  mCanvas;
    private Paint   mBitmapPaint;    
    private Path	mPath;
    
    private Integer color = Color.RED;
    private Integer type  = TYPE_PENCIL;
    
    private ArrayList<PathFinger> paths;
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    
    private boolean enabled = true;

    public PaintView(Context c) {
        
    	super(c);
        
        paths 		 = new ArrayList<PathFinger>();        
        mBitmapPaint = new Paint();
        mPath		 = new Path();
        
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
        
    }
    
    public void setEnabled(boolean b) {
    	enabled = b;
    }
    
    public void setColor(int c) {
    	color = c;
    	mPaint.setColor(c);
    }
    
    public void setType(int t) {
    	type = t;
    	if (type == TYPE_ERASE) {
    		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    	} else if (type == TYPE_PENCIL || type == TYPE_LINE) {
    		mPaint.setXfermode(null);
    	}
    	
    }
    
    public int getCount() {
    	return paths.size();
    }
    
    public void clear() {
    	while(paths.size() > 0) {
    		undo();
    	}
    }
    
    public void undo() {
    	
    	if (paths.size() == 0) return;
    	
    	paths.remove(paths.size()-1);
    	mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    	
    	for (int i=0; i<paths.size(); i++) {    
    		
    		PathFinger item = paths.get(i);
    		Path path	  = item.getPath();
    		Integer type  = item.getType();
    		
    		if (type == TYPE_PENCIL || type == TYPE_LINE) {
    			mPaint.setColor(item.getColor());
    			mPaint.setXfermode(null);
    		} else if (type == TYPE_ERASE) {
    			mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    		}
    		
    		mCanvas.drawPath(path, mPaint);
    		invalidate();

    	}  
    	
    	mPath.reset();
    	invalidate();
    	
    	setType(type);
    	setColor(color);
    	
    }
    
    private PathFinger addPath() {   
    	
    	PathFinger pv;
    	if (type == TYPE_PENCIL || type == TYPE_ERASE) {
    		pv = new PathFinger();
    	} else {
    		pv = new PathLine();
    	}
    	    	
    	pv.setType(type);
    	pv.setColor(color);
    	paths.add(pv);
    	
    	return pv;
    	
    }
    
    private PathFinger getLastPath() {
    	PathFinger p = paths.get(paths.size()-1);
    	return p;
    }
    
    
    private boolean canDraw() {
    	return !(paths.size() == 0 && type == TYPE_ERASE);
    }
    
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    
    @Override
    protected void onDraw(Canvas canvas) {   
    	if (paths.size() > 0) {
    		canvas.drawColor(Color.TRANSPARENT);
    		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
    		canvas.drawPath(mPath, mPaint);
    	}
    }

    private void touch_start(float x, float y) {
    	addPath().moveTo(x, y, mPath);
        mX = x;
        mY = y;   
    }
    
    private void touch_move(float x, float y) {
    	    	
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
                
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
        	        	
        	PathFinger item = getLastPath(); 
            item.quadTo(mX, mY, (x + mX)/2, (y + mY)/2, mPath);

            if (item.getType() == TYPE_ERASE) {
           	 	mCanvas.drawPath(mPath, mPaint);
           	 	mPath.reset();	
           	 	mPath.moveTo(mX, mY);
        	}
                        
        	mX = x;
            mY = y;
            
        }
    }

    private void touch_up() {
    	getLastPath().lineTo(mX, mY, mPath);
        mCanvas.drawPath(mPath, mPaint);
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	
    	if (!enabled) return true;
    	if (canDraw() == false) return true;
    	
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }
    	
}
