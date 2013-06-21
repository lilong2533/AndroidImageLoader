package com.lilong.image;
import java.util.LinkedList;
import com.lilong.image.cache.Images;
import com.lilong.image.cache.MemoryCache;
import com.lilong.image.loader.DiscImageLoader;
import com.lilong.image.loader.IImageLoader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class HomeActivity extends Activity {
	private ShowAdapter lAdapter;
	private GridView vRoot;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_activity);
		
		lAdapter = new ShowAdapter();
//		new Thread(new DiscImageLoader("/mnt/sdcard/lilong", lAdapter)).start();
		new Thread(new DiscImageLoader("/mnt/sdcard2/壁纸", lAdapter)).start();
		
		vRoot = (GridView) findViewById(R.id.photo_list_root);
		vRoot.setAdapter(lAdapter);
	}
	
	class LocalImageCreator implements Images.ImageCreator{
		
		@Override
		public Bitmap generateBitmap(Object obj ,int img_width ,int img_height) {
			if(obj != null){
				String _img_uri = (String) obj;
				BitmapFactory.Options _options = new BitmapFactory.Options();
				_options.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(_img_uri, _options);
				
				int inSampleSize = 1;
				/* 如果原始图片的尺寸大于缩率图，则对原始图片进行压缩处理 */
			    if (_options.outHeight > img_height || _options.outWidth > img_width) {
			        final int heightRatio = Math.round((float) _options.outHeight / (float) img_height);
			        final int widthRatio = Math.round((float) _options.outWidth / (float) img_width);

			        inSampleSize = widthRatio < heightRatio ? widthRatio : heightRatio;
			    }
			    
			    _options.inJustDecodeBounds = false;
			    _options.inSampleSize = inSampleSize;
			    return BitmapFactory.decodeFile(_img_uri, _options);
			}
			return null;
		}
	}
	
	class ShowAdapter extends BaseAdapter implements IImageLoader.Callback{
		private LinkedList<String> lShowImage;
		private LayoutInflater lInflater;
		private Images images;
		
		public ShowAdapter(){
			lInflater = (LayoutInflater) HomeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			lShowImage = new LinkedList<String>();
			images = new Images(new LocalImageCreator() ,new MemoryCache());
		}
		
		@Override
		public int getCount() {
			synchronized (lShowImage) {
				return lShowImage.size();
			}
		}

		@Override
		public Object getItem(int position) {
			synchronized (lShowImage) {
				return lShowImage.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = lInflater.inflate(R.layout.photo_item_layout, null);
			}
			
			String _img_file = null;
			synchronized (lShowImage) {
				_img_file = lShowImage.get(position);
			}
			ImageView _imgv = (ImageView) convertView.findViewById(R.id.photo_item);
			
			images.loadBitmap(_img_file, _imgv, 200, 200);
			return convertView;
		}

		@Override
		public void notifyRealTime(String file) {
			synchronized (lShowImage) {
				lShowImage.add(file);
			}
			//通过handler刷新UI
		}

		@Override
		public void notifyOnce(LinkedList<String> files) {
			synchronized (lShowImage) {
				lShowImage.addAll(files);
			}
			//通过handler刷新UI
		}
	}
}
