package target.exercise2Hide;

import target.exercise2.File;

/**
 * 
 * This is the target class for TypeStateAnalysis.java
 *
 */
public class FileClosedHide {

	public void test1() {
		File fileHide = new File();
		fileHide.open();
		fileHide.close();
	}

	public void test2() {
		File fileHide = new File();
		fileHide.open();
		if (fileHide.size() > 10) {
			fileHide.close();
		} else {
			fileHide.close();
		}
	}
}
