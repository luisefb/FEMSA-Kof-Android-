package mx.app.ambassador.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

public class Font {
	
	static private HashMap<String,Typeface> fonts;
	
	static public Typeface get(Context c, String font) {
		
		if (fonts == null) {
			fonts = new HashMap<String,Typeface>();
			//fonts.put("dragon-bold", 	Typeface.createFromAsset(c.getAssets(), "fonts/dragon-serial-bold.ttf"));
			//fonts.put("dragon-regular", Typeface.createFromAsset(c.getAssets(), "fonts/DragonSerial-Regular.otf"));
			fonts.put("dragon-bold", 	Typeface.createFromAsset(c.getAssets(), "fonts/Pieces of Eight.ttf"));
			fonts.put("dragon-regular", Typeface.createFromAsset(c.getAssets(), "fonts/Pieces of Eight.ttf"));
			fonts.put("pieces-eight",  	Typeface.createFromAsset(c.getAssets(), "fonts/Pieces of Eight.ttf"));
		}
		return fonts.get(font);
	}	
	
}
