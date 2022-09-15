
package tt.reducto.pdf.fitz.android;

import android.graphics.Bitmap;

import tt.reducto.pdf.fitz.Context;
import tt.reducto.pdf.fitz.Image;


public final class AndroidImage extends Image
{
	static {
		Context.init();
	}

	private native long newAndroidImageFromBitmap(Bitmap bitmap, long mask);

	public AndroidImage(Bitmap bitmap, AndroidImage mask)
	{
		super(0);
		pointer = newAndroidImageFromBitmap(bitmap, mask.pointer);
	}
}
