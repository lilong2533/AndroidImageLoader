package com.lilong.image.loader;

import java.io.File;
import java.util.LinkedList;
import android.webkit.MimeTypeMap;

public class DiscImageLoader implements IImageLoader ,Runnable{
	
	private enum WorkStatus{
		STATUS_INIT,
		STATUS_WORKING,
		STATUS_STOPED,
		STATUS_ERROR
	};
	
	private WorkStatus lWorkStatus; 
	private NotifyPolicy lNotifyPolicy;
	private String lImagesDir;
	private LinkedList<String> lImages;
	private Callback lCallback;
	
	public DiscImageLoader(String images_dir ,Callback callback){
		lWorkStatus = WorkStatus.STATUS_INIT;
		lImagesDir = images_dir;
		lCallback = callback;
		lNotifyPolicy = NotifyPolicy.Once;
		lImages = new LinkedList<String>();
	}
	
	public boolean changeNotifyPolicy(IImageLoader.NotifyPolicy policy){
		if(WorkStatus.STATUS_WORKING == lWorkStatus){
			return false;
		}else{
			lNotifyPolicy = policy;
			return true;
		}
	}

	@Override
	public void run() {
		if(NotifyPolicy.Once == lNotifyPolicy && lCallback == null){
			lWorkStatus = WorkStatus.STATUS_ERROR;
			return;
		}
		
		lWorkStatus = WorkStatus.STATUS_WORKING;
		
		File _root_dir = new File(lImagesDir);
		if(_root_dir.exists() && _root_dir.isDirectory()){
			lImages.clear();
			
			traverseDir(_root_dir);
			
			if(IImageLoader.NotifyPolicy.Once == lNotifyPolicy){
				//一次回调通知
				lCallback.notifyOnce(lImages);
			}
		}
		
		lWorkStatus = WorkStatus.STATUS_STOPED;
	}
	
	/**
	 * 遍历当前Images目录
	 * */
	private void traverseDir(File path){
		if(path == null || path.isFile()) return;
		
		File[] _child_files = path.listFiles();
		if(_child_files != null && _child_files.length > 0){
			for(int index = 0 ; index < _child_files.length ; index++){
				File _tmp = _child_files[index];
				if(_tmp.isFile()){
					handleImageFile(_tmp);
				}else{
					traverseDir(_tmp);
				}
			}
		}
	}
	
	private void handleImageFile(File file){
		if(isImage(file.getName())){
			if(IImageLoader.NotifyPolicy.RealTime == lNotifyPolicy){
				//一次回调通知
				lCallback.notifyRealTime(file.getAbsolutePath());
			}else{
				lImages.add(file.getAbsolutePath());
			}
		}
	}
	
	private boolean isImage(String name) {
		if(null == name || 0 == name.trim().length()){
		}else{
			String _lower_case = name.toLowerCase();

			String _extension = null;
			int _dot = _lower_case.lastIndexOf(".");
			if (_dot >= 0){
				_extension = _lower_case.substring(_dot + 1);
				String _mime_type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(_extension);
				if(_mime_type.startsWith("image")){
					return true;
				}
			}
		}
		return false;
	}
}
