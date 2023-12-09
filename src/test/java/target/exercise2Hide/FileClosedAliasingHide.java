package target.exercise2Hide;

import target.exercise2.File;

public class FileClosedAliasingHide {
	private static File staticFileHide;
	
	public void test1() {
		File fileHide = new File();
		File alias = fileHide;
		fileHide.open();
		alias.close();
	}
	
	public void test2()
	{
		staticFileHide=new File();
		File fileHide=staticFileHide;
		staticFileHide.open();
		fileHide.close();
	}
}
