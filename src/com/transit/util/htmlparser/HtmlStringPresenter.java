package com.transit.util.htmlparser;

public abstract class HtmlStringPresenter {

	public abstract String getPresentableHTML(String htmlInput);
	
	public abstract boolean isAvailableForInputHTML(String html);
}
