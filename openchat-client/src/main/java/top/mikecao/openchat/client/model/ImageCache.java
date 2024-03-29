package top.mikecao.openchat.client.model;

import javafx.scene.image.Image;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author mike
 */
public class ImageCache {
	private final ConcurrentHashMap<String, WeakReference<Image>> map;
	private static final ImageCache INSTANCE = new ImageCache();
	public static ImageCache getInstance() {
		return INSTANCE;
	}
	private ImageCache() {
		map = new ConcurrentHashMap<>();
	}

	public Image getImage(String path) {
		WeakReference<Image> ref = map.get(path);
		if(ref == null || ref.get() == null) {
			ref = new WeakReference<>(new Image(path, true));
			map.put(path, ref);
		}
		return ref.get();
	}
}
