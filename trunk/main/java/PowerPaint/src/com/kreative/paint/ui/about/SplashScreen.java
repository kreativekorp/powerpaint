/*
 * Copyright &copy; 2009-2011 Rebecca G. Bettencourt / Kreative Software
 * <p>
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <a href="http://www.mozilla.org/MPL/">http://www.mozilla.org/MPL/</a>
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Alternatively, the contents of this file may be used under the terms
 * of the GNU Lesser General Public License (the "LGPL License"), in which
 * case the provisions of LGPL License are applicable instead of those
 * above. If you wish to allow use of your version of this file only
 * under the terms of the LGPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the LGPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the LGPL License.
 * @since PowerPaint 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.paint.ui.about;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import com.kreative.paint.ui.UIUtilities;
import com.kreative.paint.util.OSUtils;

public class SplashScreen extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static Image AboutImage = tk.createImage(SplashScreen.class.getResource("AboutImage.png"));
	static { tk.prepareImage(AboutImage, -1, -1, null); }
	
	private JLabel loadingLabel;
	
	public SplashScreen() {
		JLabel splashPanel = new JLabel(new ImageIcon(AboutImage));
		JPanel infoPanel = new JPanel(new BorderLayout(4,4));
		loadingLabel = new JLabel(UIUtilities.messages.getString("about.loading"));
		loadingLabel.setFont(loadingLabel.getFont().deriveFont(9.0f));
		loadingLabel.setHorizontalAlignment(JLabel.CENTER);
		loadingLabel.setHorizontalTextPosition(JLabel.CENTER);
		loadingLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		infoPanel.add(loadingLabel, BorderLayout.PAGE_START);
		JLabel copyrightLabel = new JLabel("<html><center>"+UIUtilities.messages.getString("program.copyright1")+"</center></html>");
		copyrightLabel.setFont(copyrightLabel.getFont().deriveFont(9.0f));
		copyrightLabel.setHorizontalAlignment(JLabel.CENTER);
		copyrightLabel.setHorizontalTextPosition(JLabel.CENTER);
		copyrightLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		infoPanel.add(copyrightLabel, BorderLayout.CENTER);
		JPanel main = new JPanel(new BorderLayout());
		main.add(splashPanel, BorderLayout.CENTER);
		main.add(infoPanel, BorderLayout.PAGE_END);
		if (OSUtils.isMacOS()) {
			main.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
		} else {
			Border inner = BorderFactory.createEmptyBorder(16, 16, 16, 16);
			Border outer = BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.lightGray, Color.white, Color.darkGray, Color.gray);
			main.setBorder(BorderFactory.createCompoundBorder(outer, inner));
		}
		setContentPane(main);
		setUndecorated(true);
		setAlwaysOnTop(true);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void setLoadingMessage(String s) {
		loadingLabel.setText(s);
	}
}
