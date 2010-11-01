/*
 * Copyright &copy; 2009-2010 Rebecca G. Bettencourt / Kreative Software
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

package com.kreative.paint.filter;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import javax.swing.JComboBox;
import com.kreative.paint.form.CustomOption;
import com.kreative.paint.form.Form;
import com.kreative.paint.form.PreviewGenerator;
import com.kreative.paint.res.MaterialsManager;
import com.kreative.paint.util.DitherAlgorithm;
import com.kreative.paint.util.ImageUtils;
import com.kreative.paint.util.PairList;
import com.kreative.paint.util.UpdateLock;

public class DitherFilter extends AbstractFilter {
	private PairList<String,int[]> palettes;
	private PairList<String,DitherAlgorithm> ditherers;
	private String colorsName;
	private String dithererName;
	private int[] colors;
	private DitherAlgorithm ditherer;
	
	public DitherFilter(MaterialsManager mm) {
		palettes = mm.getColorArrays();
		ditherers = mm.getDitherAlgorithms();
		colorsName = "Black & White";
		dithererName = "Threshold";
		colors = new int[] { 0xFF000000, 0xFFFFFFFF };
		ditherer = new DitherAlgorithm(new int[][]{new int[]{0}}, 1);
	}
	
	public Image filter(Image src) {
		return ditherer.dither(src, colors);
	}
	
	public boolean usesOptionForm() {
		return true;
	}
	
	public Form getOptionForm(final Image src) {
		Form f = new Form();
		f.add(new PreviewGenerator() {
			public String getName() { return null; }
			public void generatePreview(Graphics2D g, Rectangle r) {
				Shape clip = g.getClip();
				BufferedImage i = ditherer.dither(ImageUtils.toBufferedImage(src,200,200), colors);
				g.setClip(r);
				g.drawImage(i, null, r.x + (r.width-i.getWidth())/2, r.y + (r.height-i.getHeight())/2);
				g.setClip(clip);
			}
		});
		f.add(new CustomOption<JComboBox>() {
			private UpdateLock u = new UpdateLock();
			public String getName() { return FilterUtilities.messages.getString("dither.Palette"); }
			public JComboBox makeUI(boolean mini) {
				final JComboBox cc = new JComboBox(palettes.toFormerArray(new String[0]));
				cc.setEditable(false);
				cc.setMaximumRowCount(48);
				cc.setSelectedItem(colorsName);
				cc.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						if (u.lock()) {
							colorsName = cc.getSelectedItem().toString();
							colors = palettes.getLatter(cc.getSelectedIndex());
							if (ui != null) ui.update();
							u.unlock();
						}
					}
				});
				return cc;
			}
			public void update(JComboBox cb) {
				if (u.lock()) {
					cb.setSelectedItem(colorsName);
					if (ui != null) ui.update();
					u.unlock();
				}
			}
		});
		f.add(new CustomOption<JComboBox>() {
			private UpdateLock u = new UpdateLock();
			public String getName() { return FilterUtilities.messages.getString("dither.Algorithm"); }
			public JComboBox makeUI(boolean mini) {
				final JComboBox ac = new JComboBox(ditherers.toFormerArray(new String[0]));
				ac.setEditable(false);
				ac.setMaximumRowCount(48);
				ac.setSelectedItem(dithererName);
				ac.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						if (u.lock()) {
							dithererName = ac.getSelectedItem().toString();
							ditherer = ditherers.getLatter(ac.getSelectedIndex());
							if (ui != null) ui.update();
							u.unlock();
						}
					}
				});
				return ac;
			}
			public void update(JComboBox cb) {
				if (u.lock()) {
					cb.setSelectedItem(dithererName);
					if (ui != null) ui.update();
					u.unlock();
				}
			}
		});
		return f;
	}
}
