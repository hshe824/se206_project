//package bounce;
//
//import java.awt.Image;
//import java.util.ArrayList;
//
//import com.se251.model.PhotoCollection;
//
///*
// * This class produces a rectangle shape that contains
// * a static image file which changes its image everytime it
// * bounces on one of the walls.
// * 
// * Author: Harry She
// * 
// */
//public class ImageShape extends Shape {
//
//	private int _state;
//	private PhotoCollection _favouritePhotos;
//
//	ArrayList<Image> _images = new ArrayList<Image>();
//
//	public ImageShape(int x, int y, int deltaX, int deltaY, int width,
//			int height, PhotoCollection photoCollection) {
//		super(x, y, deltaX, deltaY, width, height);
//		_state = (int) (Math.random() * ((photoCollection.size() - 1) + 1));
//		_favouritePhotos = photoCollection;
//
//		if (_favouritePhotos.size() == 0) {
//			AnimationViewer.isEmpty();
//		} else {
//			AnimationViewer.isNotEmpty();
//		}
//
//		for (int i = 0; i < _favouritePhotos.size(); i++) {
//			if (_favouritePhotos.getPhoto(i).getFullSizeImage() != null) {
//				_images.add(_favouritePhotos.getPhoto(i).getFullSizeImage());
//			} else {
//				_images.add(_favouritePhotos.getPhoto(i).getThumbnailImage());
//			}
//
//		}
//
//	};
//
//	/**
//	 * Paints this RectangleShape object using the supplied Painter object.
//	 */
//
//	protected void paints(Painter painter) {
//
//		// Draw current state of image every bounce
//		if (AnimationViewer.getEmptyStatus() == false) {
//			painter.drawRect(_x, _y, _width, _height);
//			painter.drawImage(_images.get(_state), _x, _y, _width, _height,
//					null);
//		}
//
//	}
//
//	public void move(int _width, int _height) {
//		// Change state every bounce
//		super.move(_width, _height);
//		if (_y == 0 || _y == _height - this._height || _x == 0
//				|| _x == _width - this._width) {
//			if (_state != _images.size() - 1) {
//				_state++;
//			} else {
//				_state = 0;
//			}
//		}
//	}
//}
