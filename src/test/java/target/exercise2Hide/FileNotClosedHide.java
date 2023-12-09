package target.exercise2Hide;

import target.exercise2.File;

public class FileNotClosedHide {
	
	public void test1() {
		File fileHide = new File();
		fileHide.open();
	}

	public void test2() {
		File fileHide = new File();
		fileHide.open();
		fileHide = new File();
		fileHide.close();
	}

	public void test3() {
		File fileHide = new File();
		fileHide.open();
		fileHide.close();
		fileHide.open();
	}
	
}
