package mx.app.ambassador.ui.paint;

import android.graphics.Path;

public class PathLine extends PathFinger {
	
	float posX;
	float posY;
	
	float lastPosX;
	float lastPosY;

	public PathLine() {
		super();
	}
	
	public void moveTo(float x, float y, Path path) {
		super.moveTo(x, y, path);
		posX = x;
		posY = y;
	}

	public void lineTo(float x, float y, Path path) {		
		update(path);
		path.lineTo(lastPosX, lastPosY);
		mPath.lineTo(lastPosX, lastPosY);
	}

	public void quadTo(float x1, float y1, float x2, float y2, Path path) {
		
		update(path);
		
		lastPosX = x1;
		lastPosY = y1;
		
		path.quadTo(x1, y1, x2, y2);
		mPath.quadTo(x1, y1, x2, y2);
		
	}
	
	private void update(Path path) {
		path.reset();
		mPath.reset();
		super.moveTo(posX, posY, path);
	}
	
}
