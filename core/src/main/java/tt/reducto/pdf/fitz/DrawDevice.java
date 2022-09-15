
package tt.reducto.pdf.fitz;

public final class DrawDevice extends NativeDevice
{
	static {
		Context.init();
	}

	private static native long newNative(Pixmap pixmap);

	public DrawDevice(Pixmap pixmap) {
		super(newNative(pixmap));
	}
}
