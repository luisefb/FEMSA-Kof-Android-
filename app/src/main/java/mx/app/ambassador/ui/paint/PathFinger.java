package mx.app.ambassador.ui.paint;

import android.graphics.Path;

public class PathFinger {
	
	protected Path mPath;
	protected Integer type;
	protected Integer color;

	public PathFinger() {
		mPath = new Path();
	}
	
	public Path getPath() {
		return mPath;
	}
	
	public void moveTo(float x, float y, Path path) {
		path.reset();
		path.moveTo(x, y);
		mPath.moveTo(x, y);
	}
	
	public void lineTo(float x, float y, Path path) {
		mPath.lineTo(x, y);
		path.lineTo(x, y);
	}
	
	public void quadTo(float x1, float y1, float x2, float y2, Path path) {
		path.quadTo(x1, y1, x2, y2);
		mPath.quadTo(x1, y1, x2, y2);
	}
	
	public void reset() {
		mPath.reset();
	}
	
	public void setType(Integer t) {
		type = t;
	}
	
	public Integer getType() {
		return type;
	}
	
	public void setColor(Integer c) {
		color = c;
	}
	
	public Integer getColor() {
		return color;
	}
	
	public String toString() {
		return getClass().getName() + " [type:"+type+", color:"+color+", path:"+mPath.toString()+"]";
	}
	
}
