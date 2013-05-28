package com.lilong.image.cache;
import android.graphics.Bitmap;
import android.util.LruCache;

public class MemoryCache implements Images.ImageCache{
	public static final int MEMORY_PERCENT = 8;
	private LruCache<String, Bitmap> lMemoryCache;
	    
    @Override
	public void initCache() {
    	final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / MEMORY_PERCENT;
        
    	lMemoryCache = new LruCache<String, Bitmap>(cacheSize){

			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getByteCount() / 1024;
			}
    	};
	}

	@Override
	public Bitmap getBitmapFromCache(String imgURI) {
		return lMemoryCache.get(imgURI);
	}

	@Override
	public void addBitmapToCache(String imgURI, Bitmap bitmap) {
		lMemoryCache.put(imgURI, bitmap);
	}
}
