package sicc.diag;

import sicc.bparser.FilePos;

public class DiagException extends RuntimeException {
	private static final long serialVersionUID = 2354530790803537805L;

	public final static String KEY_INTERNAL = "Internal";
	private final static String MSG_INTERNAL = "Unexpected exception";

	private final FilePos mFPos;
	private final String mKey;
	private final String mMessage;
	private Severity mSeverity = Severity.UFATAL;

	public DiagException(FilePos fpos, String key, String message, Throwable cause) {
		super(message, cause);
		nevernull(key, "Key");
		nevernull(message, "Message");
		mFPos = (fpos != null)? new FilePos(fpos): new FilePos();
		mKey = key;
		mMessage = message;
	}
	public DiagException(FilePos fpos, String key, String message) {
		this(fpos, key, message, null);
	}
	public DiagException(String key, String message) {
		this(null, key, message, null);
	}
	public DiagException(FilePos fpos, Throwable cause) {
		this(fpos, KEY_INTERNAL, MSG_INTERNAL, cause);
	}
	public DiagException(Throwable cause) {
		this((FilePos)null, KEY_INTERNAL, MSG_INTERNAL, cause);
	}

	public Severity getDynamicSeverity() {
		return mSeverity;
	}
	public void setDynamicSeverity(Severity sev) {
		nevernull(sev, "Severity");
		mSeverity = sev;
	}
	
	public FilePos getFPos() {
		return mFPos;
	}

	public String getKey() {
		return mKey;
	}

	@Override
	public String getMessage() {
		String msg = getDynamicSeverity().toString() + " " + mKey + ": " + mFPos + ": " + mMessage;
		if (getCause() != null) {
			msg += ": " + getCause();
		}
		return msg;
	}

	private static void nevernull(Object obj, String id) {
		if (obj == null) {
			throw new IllegalArgumentException(id + " must never be null");
		}
	}
}
