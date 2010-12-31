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

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import com.kreative.paint.filter.FilterManager;
import com.kreative.paint.format.FormatManager;
import com.kreative.paint.tool.ToolManager;
import com.kreative.paint.ui.UIUtilities;
import com.kreative.paint.util.OSUtils;

public class AboutBox extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private static Image AboutImage;
	private static Image AboutImageAlt;
	private static String AboutString;
	private static String AboutStringAlt;
	static {
		Class<?> cl = AboutBox.class;
		Toolkit tk = Toolkit.getDefaultToolkit();
		AboutImage = tk.createImage(cl.getResource("AboutImage.png"));
		AboutImageAlt = tk.createImage(cl.getResource("AboutImageAlt.png"));
		tk.prepareImage(AboutImage, -1, -1, null);
		tk.prepareImage(AboutImageAlt, -1, -1, null);
		String osString = "";
		String jreString = "";
		String vmString = "";
		try {
			osString = prep(OSUtils.getOSName()) + "<br>" + OSUtils.getOSVersion();
			jreString = prep(System.getProperty("java.runtime.name")) + "<br>" + System.getProperty("java.runtime.version");
			vmString = prep(System.getProperty("java.vm.name")) + "<br>" + System.getProperty("java.vm.version");
		} catch (Exception e) {}
		long hs = Runtime.getRuntime().freeMemory();
		String hsString = UIUtilities.messages.getString("about.freemem").replace("$", ((hs >= 1048576) ? ((hs / 1048576) + " MiB") : (hs >= 1024) ? ((hs / 1024) + " KiB") : (hs + " bytes")));
		AboutString =
			"<html><center>" +
			UIUtilities.messages.getString("program.copyright1") +
			"<br>" +
			UIUtilities.messages.getString("program.copyright2") +
			"<br><br>" +
			UIUtilities.messages.getString("program.longname") +
			"<br>" +
			UIUtilities.messages.getString("program.longversion") +
			"<br>" +
			osString +
			"<br>" +
			jreString +
			"<br>" +
			vmString +
			"<br>" +
			hsString +
			"</center></html>";
		AboutStringAlt =
			"<html><center>" +
			UIUtilities.messages.getString("program.copyright1") +
			"<br>" +
			UIUtilities.messages.getString("program.copyright2") +
			"<br>" +
			UIUtilities.messages.getString("program.copyright3") +
			"<br><br>" +
			UIUtilities.messages.getString("program.longname") +
			"<br>" +
			UIUtilities.messages.getString("program.longversion") +
			"<br>" +
			osString +
			"<br>" +
			jreString +
			"<br>" +
			vmString +
			"<br>" +
			hsString +
			"</center></html>";
	}
	
	private JLabel splashPanel;
	private CreditsRoller creditsRoller;
	private JLabel infoPanel;
	private PluginList pluginList;
	private JScrollPane pluginScroll;
	private CardLayout cl;
	private JPanel cpp;
	private JButton credits, plugins, ok;
	private JPanel buttonPanel, lp, rp, main;
	private StringBuffer sb;
	
	public AboutBox(FormatManager fmt, FilterManager ftr, ToolManager tm) {
		super();
		makeGUI(fmt, ftr, tm);
	}
	
	public AboutBox(Dialog owner, FormatManager fmt, FilterManager ftr, ToolManager tm) {
		super(owner);
		makeGUI(fmt, ftr, tm);
	}
	
	public AboutBox(Dialog owner, boolean modal, FormatManager fmt, FilterManager ftr, ToolManager tm) {
		super(owner, modal);
		makeGUI(fmt, ftr, tm);
	}
	
	public AboutBox(Frame owner, FormatManager fmt, FilterManager ftr, ToolManager tm) {
		super(owner);
		makeGUI(fmt, ftr, tm);
	}
	
	public AboutBox(Frame owner, boolean modal, FormatManager fmt, FilterManager ftr, ToolManager tm) {
		super(owner, modal);
		makeGUI(fmt, ftr, tm);
	}
	
	private void makeGUI(FormatManager fmt, FilterManager ftr, ToolManager tm) {
		setTitle(UIUtilities.messages.getString("about.title"));
		splashPanel = new JLabel(new ImageIcon(AboutImage));
		creditsRoller = new CreditsRoller();
		creditsRoller.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black), BorderFactory.createLineBorder(Color.white)));
		infoPanel = new JLabel(AboutString);
		infoPanel.setFont(infoPanel.getFont().deriveFont(9.0f));
		infoPanel.setHorizontalAlignment(JLabel.CENTER);
		infoPanel.setHorizontalTextPosition(JLabel.CENTER);
		infoPanel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		pluginList = new PluginList(fmt, ftr, tm);
		pluginList.setVisibleRowCount(5);
		pluginScroll = new JScrollPane(pluginList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pluginScroll.setMinimumSize(new Dimension(1,1));
		pluginScroll.setPreferredSize(new Dimension(1,1));
		
		cl = new CardLayout();
		cpp = new JPanel(cl);
		cpp.add(creditsRoller, "credits");
		cpp.add(pluginScroll, "plugins");
		
		credits = new JButton(UIUtilities.messages.getString("about.creditsbtn"));
		credits.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cl.show(cpp, "credits");
				creditsRoller.start();
			}
		});
		plugins = new JButton(UIUtilities.messages.getString("about.pluginsbtn"));
		plugins.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				creditsRoller.stop();
				cl.show(cpp, "plugins");
			}
		});
		ok = new JButton(UIUtilities.messages.getString("about.ok"));
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(credits);
		buttonPanel.add(plugins);
		buttonPanel.add(ok);
		
		lp = new JPanel(new BorderLayout(8,8));
		lp.add(splashPanel, BorderLayout.CENTER);
		rp = new JPanel(new BorderLayout(8,8));
		rp.add(cpp, BorderLayout.NORTH);
		rp.add(infoPanel, BorderLayout.CENTER);
		rp.add(buttonPanel, BorderLayout.SOUTH);
		main = new JPanel(new GridLayout(1,2,16,16));
		main.add(lp);
		main.add(rp);
		main.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
		setContentPane(main);
		getRootPane().setDefaultButton(ok);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		
		sb = new StringBuffer();
		KeyListener kl = new KeyListener() {
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {
				char ch = e.getKeyChar();
				if ((ch >= 0x20 && ch < 0x7F) || (ch >= 0xA0 && ch < 0xFFFD)) {
					sb.append(ch);
					if (sb.toString().equalsIgnoreCase("secret about box")) {
						splashPanel.setIcon(new ImageIcon(AboutImageAlt));
						infoPanel.setText(AboutStringAlt);
						validate();
						pack();
						setLocationRelativeTo(null);
					}
				}
			}
		};
		credits.addKeyListener(kl);
		plugins.addKeyListener(kl);
		ok.addKeyListener(kl);
	}
	
	public void setVisible(boolean visible) {
		if (visible) {
			sb = new StringBuffer();
			cl.show(cpp, "credits");
			creditsRoller.start();
		} else {
			creditsRoller.stop();
		}
		super.setVisible(visible);
	}
	
	@Deprecated
	public void show() {
		sb = new StringBuffer();
		cl.show(cpp, "credits");
		creditsRoller.start();
		super.show();
	}
	
	@Deprecated
	public void hide() {
		creditsRoller.stop();
		super.hide();
	}
	
	public void dispose() {
		creditsRoller.stop();
		super.dispose();
	}
	
	private static String prep(String s) {
		s = s.replace("(TM)", "\u2122");
		s = s.replace("(C)", "\u00A9");
		s = s.replace("(R)", "\u00AE");
		return s;
	}
}
