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
import java.awt.image.BufferedImage;
import java.text.*;
import javax.swing.*;
import com.kreative.paint.filter.Filter;
import com.kreative.paint.format.Format;
import com.kreative.paint.tool.Tool;

public class PluginListCellRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = 1L;
	
	private static Image DocIcon16 = null;
	private static Image KKFixed4x7 = null;
	private static Image PluginIcon = null;
	static {
		Class<?> cl = PluginListCellRenderer.class;
		Toolkit tk = Toolkit.getDefaultToolkit();
		DocIcon16 = tk.createImage(cl.getResource("DocumentIcon16.png"));
		KKFixed4x7 = tk.createImage(cl.getResource("KKFixed4x7.png"));
		PluginIcon = tk.createImage(cl.getResource("GenericPluginIcon.png"));
		tk.prepareImage(DocIcon16, -1, -1, null);
		tk.prepareImage(KKFixed4x7, -1, -1, null);
		tk.prepareImage(PluginIcon, -1, -1, null);
	}
	
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean sel, boolean focus) {
		JLabel l = (JLabel)super.getListCellRendererComponent(list, value, index, sel, focus);
		if (value instanceof Format) {
			BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = img.createGraphics();
			g.drawImage(DocIcon16, 0, 0, null);
			String ext = ((Format)value).getExtension().toUpperCase();
			g.setPaint(new Color(0x80FFFFFF,true));
			g.fillRect(8-ext.length()*2, 8, ext.length()*4+1, 7);
			CharacterIterator si = new StringCharacterIterator(ext); int sx = 9-ext.length()*2;
			for (char ch = si.first(); ch != CharacterIterator.DONE; ch = si.next(), sx += 4) {
				g.drawImage(KKFixed4x7, sx, 9, sx+4, 16, 4*((ch & 0x7F)-32), 7*((ch >>> 7) & 1), 4*((ch & 0x7F)-32)+4, 7*((ch >>> 7) & 1)+7, null);
			}
			g.dispose();
			l.setIcon(new ImageIcon(img));
			String name = ((Format)value).getName();
			if (!((Format)value).getExpandedName().equalsIgnoreCase(name)) name += " ("+((Format)value).getExpandedName()+")";
			if (((Format)value).supportsRead() && ((Format)value).supportsWrite()) {
				//name = "<html>"+name+" <small><font color=\"#808080\">(read and write)</font></small></html>";
			} else if (((Format)value).supportsRead()) {
				name = "<html>"+name+" <small><font color=\"#808080\">(read only)</font></small></html>";
			} else if (((Format)value).supportsWrite()) {
				name = "<html>"+name+" <small><font color=\"#808080\">(write only)</font></small></html>";
			} else {
				name = "<html>"+name+" <small><font color=\"#808080\">(not supported)</font></small></html>";
			}
			l.setText(name);
		}
		else if (value instanceof Filter) {
			l.setIcon(new ImageIcon(PluginIcon));
			l.setText(((Filter)value).getCategory()+" > "+((Filter)value).getName());
		}
		else if (value instanceof Tool) {
			l.setIcon(new ImageIcon(((Tool)value).getIcon()));
			l.setText(((Tool)value).getName());
		}
		else {
			l.setFont(l.getFont().deriveFont(Font.BOLD));
		}
		l.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
		return l;
	}
}
