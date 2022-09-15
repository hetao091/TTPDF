
package tt.reducto.pdf.fitz;

public final class DisplayListDevice extends NativeDevice
{
	static {
		Context.init();
	}

	private static native long newNative(DisplayList list);

	public DisplayListDevice(DisplayList list) {
		super(newNative(list));
	}
}
