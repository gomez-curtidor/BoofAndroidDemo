package org.boofcv.android;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import boofcv.android.ConvertBitmap;
import boofcv.struct.image.ImageDataType;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.MultiSpectral;

/**
 * Visualizes a process where the output is simply a rendered Bitmap image.
 *
 * @author Peter Abeles
 */
public abstract class BoofMsImageProcessing extends BoofRenderProcessing<MultiSpectral<ImageUInt8>> {

	// output image which is modified by processing thread
	Bitmap output;
	// output image which is displayed by the GUI
	Bitmap outputGUI;
	// storage used during image convert
	byte[] storage;

	protected BoofMsImageProcessing() {
		super(ImageDataType.ms(ImageUInt8.class));
	}

	@Override
	protected void declareImages( int width , int height ) {
		super.declareImages(width,height);
		output = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888 );
		outputGUI = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888 );
		storage = ConvertBitmap.declareStorage(output,storage);
	}

	@Override
	protected void process(MultiSpectral<ImageUInt8> color) {
		process(gray,output,storage);
		synchronized ( lockGui ) {
			Bitmap tmp = output;
			output = outputGUI;
			outputGUI = tmp;
		}
	}

	@Override
	protected void render(Canvas canvas, double imageToOutput) {
		canvas.drawBitmap(outputGUI,0,0,null);
	}

	protected abstract void process( MultiSpectral<ImageUInt8> color , Bitmap output , byte[] storage );
}
