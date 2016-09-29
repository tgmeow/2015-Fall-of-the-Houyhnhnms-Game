package main;
import processing.core.PApplet;
import processing.event.MouseEvent;


//ZOOM SCROLL
//WORK IN PROGRESS
public class ScrollZoom{
	private PApplet parent;
	private float zoom = 100;
	private final float zoomMin = 10;
	private final float zoomMax = 1000;
	public ScrollZoom(PApplet p){
		parent = p;
	}

	//private boolean userZoomed = false;
	//private Point scrollLocation = new Point();
	private float zoomTransX = 0, zoomTransY = 0, zoomDelta = 0, lastZoomDelta = 0;
	
	public float zoom(){
		return zoom;
	}

	public void mouseWheel(MouseEvent event) {
		//scrollLocation = new Point(parent.mouseX, parent.mouseY);
		// userZoomed = true;  //boolean if the user scrollled or not
		float e = event.getCount();

		//float screenWHalf = parent.width/2;
		//float screenHHalf = parent.height/2;

		float stepSize = 1.5f;  //initial zoom step size
		zoomDelta = e * stepSize * (zoom/100.0f);
		if (zoom <= zoomMin && zoomDelta>0) zoomDelta = 0;    //limit the zooms
		if (zoom >= zoomMax && zoomDelta<0) zoomDelta = 0;    //limit the zooms
		zoom -= zoomDelta;                      //increment or decrement the zoom
		// zoomTransX +=  ((mouseX - screenWHalf)) * zoomDelta/100;
		// zoomTransY +=  ((mouseY - screenHHalf)) * zoomDelta/100;
		lastZoomDelta += zoomDelta/(zoom/100.0);              //zoom delta for decay
		if (zoom <= zoomMin) zoom = zoomMin;    //limit the zooms
		if (zoom >= zoomMax) zoom = zoomMax;    //limit the zooms
	}

	public void translateZoomScroll() {
		//float screenWHalf = parent.width/2;
		//float screenHHalf = parent.height/2;

		lastZoomDelta *= 0.91;    //DECAY THE DELTA
		if (Math.abs(lastZoomDelta) <= 0.008f) lastZoomDelta = 0;  //TO GET RID OF THE "TAIL"
		zoom -= lastZoomDelta;      //increment zoom by the decayed "momentum" value
		if (zoom <= zoomMin) zoom = zoomMin;  //limit zoom by bounds
		if (zoom >= zoomMax) zoom = zoomMax;
		// zoomTransX += (scrollLocation.x - screenWHalf)  * (lastZoomDelta/ 100.0);
		// zoomTransY +=  (scrollLocation.y - screenHHalf)  * (lastZoomDelta/ 100.0);

		parent.translate(zoomTransX + FallOfTheHouyhnhnms.panXY.x, zoomTransY + FallOfTheHouyhnhnms.panXY.y);  //ZOOM INTO CURSOR
		parent.scale(zoom/100.0f);    //matrix SCALE/ZOOM
	}
}