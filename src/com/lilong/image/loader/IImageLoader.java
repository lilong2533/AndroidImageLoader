package com.lilong.image.loader;

import java.util.LinkedList;

public interface IImageLoader {
	/**
	 * 定义通知策略
	 * Onece : 当所有的扫描工作完成后一次通知
	 * RealTime : 每扫描出一个文件就通知一次
	 * */
	public static enum NotifyPolicy{
		Once,
		RealTime
	};
	
	/**
	 * 数据源通过此接口将扫描到的图片通知给实现此接口的模块
	 * */
	interface Callback{
		public void notifyRealTime(String file);
		public void notifyOnce(LinkedList<String> files);
	}
}
