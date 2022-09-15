
package tt.reducto.pdf.fitz;

// This class handles the loading of the TTPDF shared library, together
// with the ThreadLocal magic to get the required context.
//
// The only publicly accessible method here is Context.setStoreSize, which
// sets the store size to use. This must be called before any other TTPDF
// function.
public class Context
{
	// Make sure to initialize inited before calling
	// init() from the static block below.
	private static boolean inited = false;

	static {
		init();
	}

	private static native int initNative();

	public static void init() {
		if (!inited) {
			inited = true;
			try {
				System.loadLibrary("ttpdf");
			} catch (UnsatisfiedLinkError e) {
				try {
					System.loadLibrary("ttpdf64");
				} catch (UnsatisfiedLinkError ee) {
					System.loadLibrary("ttpdf32");
				}
			}
			if (initNative() < 0)
				throw new RuntimeException("cannot initialize ttpdf library");
		}
	}

	// FIXME: We should support the store size being changed dynamically.
	// This requires changes within the TTPDF core.
	//public native static void setStoreSize(long newSize);

	//  empty the store
	public native static void emptyStore();

	public native static void enableICC();
	public native static void disableICC();
	public native static void setAntiAliasLevel(int level);

	public native static Version getVersion();

	public class Version {
		public String version;
		public int major;
		public int minor;
		public int patch;
	}
}
