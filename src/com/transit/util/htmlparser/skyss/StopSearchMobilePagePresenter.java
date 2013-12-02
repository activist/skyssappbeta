package com.transit.util.htmlparser.skyss;

import com.transit.util.htmlparser.HtmlStringPresenter;

public class StopSearchMobilePagePresenter extends HtmlStringPresenter {

	public String getPresentableHTML(String html) {
		StringBuilder parsedHtml = new StringBuilder();

		if (html != null) {
			int tableIndex = html.indexOf("<table>");
			int endTableIndex = html.indexOf("</table>", tableIndex);
			endTableIndex += 8; //end of </table>
			
			parsedHtml.append("<html><body bgcolor=\"#FFFFFF\" TEXT=\"#222222\" LINK=\"#222222\" VLINK=\"#222222\" ALINK=\"#222222\">");
			parsedHtml.append("<style type=\"text/css\">table{border-color:#4E5404;border:1px solid;}</style>");
			parsedHtml.append(html.substring(tableIndex, endTableIndex));
			parsedHtml.append("</body></html>");
		}
		return parsedHtml.toString();
	}

	public boolean isAvailableForInputHTML(String html) {
		return html != null && html.contains("<title>Holdeplasser");
	}
}
