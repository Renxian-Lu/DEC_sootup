package target.exercise2Hide;

import target.exercise2.File;

public class FileNotClosedAliasingHide {
	private static File staticFileHide;

	public void test1() {
		File fileHide = new File();
		File alias = fileHide;
		alias.open();
	}

	public void test2() {
		staticFileHide = new File();
		File fileHide = staticFileHide;
		fileHide.open();
	}

	public void test3() {
		File fileHide = new File();
		staticFileHide = fileHide;
		staticFileHide.open();
	}

	public void test4() {
		File fileHide = new File();
		fileHide.open();
		File alias1 = fileHide;
		File alias2 = alias1;
		alias2.open();
		alias1.open();
	}
}
