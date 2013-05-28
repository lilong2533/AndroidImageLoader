package com.lilong.image.cache;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import com.lilong.image.R;
import com.lilong.image.utils.CLog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

public class Images {
	private LinkedHashMap<ImageView, WeakReference<ImageAsyncLoadTask>> lTasksMap;
	private ImageCreator lImgCreator;
	private ImageCache lImgCache;
    
	/**
	 * 这个接口用来产生需要Cache的Bitmap对象
	 * */
	public interface ImageCreator{
		public Bitmap generateBitmap(Object obj ,int img_width ,int img_height);
	}
	
	/**
	 * 这个接口用来缓存ImageCreator产生的图片
	 * */
	public interface ImageCache {
		public void initCache();
	    public Bitmap getBitmapFromCache(String imgURI);
	    public void addBitmapToCache(String imgURI ,Bitmap bitmap);
	}
	
	public Images(ImageCreator creator ,ImageCache cache){
		lTasksMap = new LinkedHashMap<ImageView, WeakReference<ImageAsyncLoadTask>>();
		lImgCreator = creator;
		if(cache != null){
			lImgCache = cache;
			lImgCache.initCache();
		}
	}
	
    /**
     * 根据ImageView获取ImageAsyncLoadTask对象的弱引用，并且由ImageAsyncLoadTask来加载图片。
     * @param img_uri : 图片资源的URI
     * @param img_view : 用来显示图片的UI控件
     * @param img_width : UI控件需要图片的宽度
     * @param img_height : UI控件需要图片的高度
     * */
    public void loadBitmap(String img_uri ,ImageView img_view ,int img_width ,int img_height){
    	WeakReference<ImageAsyncLoadTask> _reference = lTasksMap.get(img_view);
    	if(_reference != null){
    		ImageAsyncLoadTask _img_load_task = _reference.get();
    		if(_img_load_task != null){
    			String _img_uri = _img_load_task.getImgURI();
	    		if(_img_uri == null || !_img_uri.equals(img_uri)){
	    			_img_load_task.cancel(true);
	    		}else{
	    			return;
	    		}
    		}
    	}
    	
    	ImageAsyncLoadTask task = new ImageAsyncLoadTask(img_view ,img_width ,img_height);
    	lTasksMap.put(img_view, new WeakReference<ImageAsyncLoadTask>(task));
        task.execute(img_uri);
    }
    
    class ImageAsyncLoadTask extends AsyncTask<String, Void, Bitmap>{
		private WeakReference<ImageView> lImageViewReference;
		private String imgURI;
		private int width;
		private int height;
		
		public ImageAsyncLoadTask(ImageView image ,int width ,int height){
			lImageViewReference = new WeakReference<ImageView>(image);
			image.setImageResource(R.drawable.photoitem_bg);
			this.width = width;
			this.height = height;
		}
		
		public String getImgURI(){
			return imgURI;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			imgURI = params[0];
			if(lImgCache != null){
				Bitmap bitmap = lImgCache.getBitmapFromCache(imgURI);
				if(bitmap == null){
					bitmap = lImgCreator.generateBitmap(imgURI ,width ,height);
					lImgCache.addBitmapToCache(imgURI, bitmap);
				}else{
					CLog.i("cache *** " + imgURI);
				}
				
				return bitmap;
			}else{
				return lImgCreator.generateBitmap(imgURI ,width ,height);
			}
		}
		
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if(lImageViewReference != null && bitmap != null){
				ImageView _image_view = lImageViewReference.get();
				if(_image_view != null){
					_image_view.setImageBitmap(bitmap);
				}
			}
		}
	}
}
