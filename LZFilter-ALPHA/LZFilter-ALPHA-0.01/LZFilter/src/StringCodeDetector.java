
import org.mozilla.intl.chardet.HtmlCharsetDetector;
import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;


public class StringCodeDetector {
	public static String[] detectedStringCode(String str,int language) {
		boolean found = false ;
		//初始化并设置识别的语言类型
		nsDetector det = new nsDetector(language) ;

		det.Init(new nsICharsetDetectionObserver() {
			public void Notify(String charset) {
			    HtmlCharsetDetector.found = true ;
			    System.out.println("CHARSET = " + charset);
			}
    	});

		
		byte[] buf = str.getBytes() ;
		int len=str.getBytes().length;
		boolean done = false ;
		boolean isAscii = true ;
		   

		// Check if the stream is only ascii.
		if (isAscii) isAscii = det.isAscii(buf,len);

		// DoIt if non-ascii and not done yet.
		if (!isAscii && !done) done = det.DoIt(buf,len, false);
		
		det.DataEnd();

		if (isAscii) {
		   System.out.println("CHARSET = ASCII");
		   found = true ;
		}
		String prob[] ={"ASCII"};
		if (!found) {
		   prob = det.getProbableCharsets() ;
		   for(int i=0; i<prob.length; i++) {
			System.out.println("Probable Charset = " + prob[i]);
			try {
				System.out.println("Probable Value = " +new String(str.getBytes(prob[i])));
			} catch (Exception e) {
			}
		   }
		}
		return prob;
	}
}
