package sicc.bparser;

public class SpecialTokens
{
	public String getBegin() { return "{"; }
	public String getEnd() { return "}"; }
	public String getBlockPrefix() { return "@"; }
	public String getBlockString(int i)
	{
		return getBegin() + getBlockPrefix() + Integer.toHexString(i) + getEnd();
	}

	public int getBlockIndex(String image) {
		String num = image.substring(getBegin().length() + getBlockPrefix().length(), image.length() - getEnd().length());
		return Integer.parseInt(num, 0x10); 
	}

	public String getRawStringString(String rawstring, int i) {
		char q = rawstring.charAt(0);
		return q + Integer.toHexString(i); //TODO + q: the last quote should be contained in the raw string...
	}
};
