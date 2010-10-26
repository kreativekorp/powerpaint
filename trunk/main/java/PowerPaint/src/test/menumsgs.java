package test;

import com.kreative.paint.ui.menu.MenuUtilities;

public class menumsgs {
	public static void main(String[] args) {
		System.out.println(MenuUtilities.messages.getString("File"));
		System.out.println(MenuUtilities.messages.getString("Edit"));
		System.out.println(MenuUtilities.messages.getString("View"));
		System.out.println(MenuUtilities.messages.getString("Draw"));
		System.out.println(MenuUtilities.messages.getString("Filter"));
		System.out.println(MenuUtilities.messages.getString("Help"));
	}
}
