package application;

public enum BackgroundIMG {
    MAIN("main"),
    EXERCISE("exercise"),
    REST("rest"),
    PAUSE("pause");
    
    private final String cssCls;
    
    BackgroundIMG(String cssCls) {
	this.cssCls = cssCls;
    }
    
    public String getCssClass() {
	return cssCls;
    }

}
