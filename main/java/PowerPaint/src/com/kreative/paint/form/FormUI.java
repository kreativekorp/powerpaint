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

package com.kreative.paint.form;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import com.kreative.paint.form.ui.*;
import com.kreative.paint.util.SwingUtils;

public class FormUI extends JPanel implements FormMetrics {
	private static final long serialVersionUID = 1L;
	
	public FormUI(Form f, boolean mini) {
		super(new BorderLayout(mini ? GRID_SPACING_MINI : GRID_SPACING, mini ? GRID_SPACING_MINI : GRID_SPACING));
		
		Map<FormOption,JLabel> labels = new HashMap<FormOption,JLabel>();
		int lw = 0;
		for (FormOption fo : f) {
			if (!(fo instanceof PreviewGenerator || fo.getName() == null)) {
				JLabel l = new JLabel(fo.getName());
				if (mini) SwingUtils.shrink(l);
				l.setHorizontalAlignment(JLabel.TRAILING);
				labels.put(fo, l);
				lw = Math.max(lw, l.getPreferredSize().width);
			}
		}
		
		JPanel ip = new JPanel(new BorderLayout(mini ? GRID_SPACING_MINI : GRID_SPACING, mini ? GRID_SPACING_MINI : GRID_SPACING));
		for (FormOption fo : f) {
			if (fo instanceof PreviewGenerator) {
				JComponent c = makeFormOptionUI(fo, mini);
				add(addFormOptionUI(c), BorderLayout.EAST);
			} else if (fo.getName() == null) {
				JPanel p1 = new JPanel(new BorderLayout(mini ? GRID_SPACING_MINI : GRID_SPACING, mini ? GRID_SPACING_MINI : GRID_SPACING));
				JComponent c = makeFormOptionUI(fo, mini);
				p1.add(addFormOptionUI(c), BorderLayout.LINE_START);
				ip.add(p1, BorderLayout.CENTER);
				JPanel tp = new JPanel(new BorderLayout(mini ? GRID_SPACING_MINI : GRID_SPACING, mini ? GRID_SPACING_MINI : GRID_SPACING));
				tp.add(ip, BorderLayout.NORTH);
				ip = tp;
			} else {
				JLabel l = labels.get(fo);
				l.setMinimumSize(new Dimension(lw, l.getMinimumSize().height));
				l.setPreferredSize(new Dimension(lw, l.getPreferredSize().height));
				l.setMaximumSize(new Dimension(lw, l.getMaximumSize().height));
				JPanel p1 = new JPanel(new BorderLayout(mini ? GRID_SPACING_MINI : GRID_SPACING, mini ? GRID_SPACING_MINI : GRID_SPACING));
				JComponent c = makeFormOptionUI(fo, mini);
				p1.add(addFormOptionUI(c), BorderLayout.LINE_START);
				JPanel p2 = new JPanel(new BorderLayout(mini ? GRID_SPACING_MINI : GRID_SPACING, mini ? GRID_SPACING_MINI : GRID_SPACING));
				p2.add(p1, BorderLayout.CENTER);
				p2.add(l, BorderLayout.LINE_START);
				ip.add(p2, BorderLayout.CENTER);
				JPanel tp = new JPanel(new BorderLayout(mini ? GRID_SPACING_MINI : GRID_SPACING, mini ? GRID_SPACING_MINI : GRID_SPACING));
				tp.add(ip, BorderLayout.NORTH);
				ip = tp;
			}
		}
		
		JPanel mp = new JPanel(new FlowLayout());
		mp.add(ip);
		add(mp, BorderLayout.CENTER);
	}
	
	private Set<FormOptionUI> u = new HashSet<FormOptionUI>();
	
	private Component addFormOptionUI(Component c) {
		if (c instanceof FormOptionUI) u.add((FormOptionUI)c);
		if (c instanceof Container) {
			for (Component c2 : ((Container)c).getComponents()) {
				addFormOptionUI(c2);
			}
		}
		return c;
	}
	
	public void update() {
		for (FormOptionUI ui : u) ui.update();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private JComponent makeFormOptionUI(FormOption f, boolean mini) {
		if (f instanceof PreviewGenerator) return new PreviewGeneratorUI((PreviewGenerator)f, mini);
		else if (f instanceof BooleanOption) return new BooleanOptionUI((BooleanOption)f, mini);
		else if (f instanceof IntegerOption) return new IntegerOptionUI((IntegerOption)f, mini);
		else if (f instanceof DoubleOption) return new DoubleOptionUI((DoubleOption)f, mini);
		else if (f instanceof CharacterOption) return new CharacterOptionUI((CharacterOption)f, mini);
		else if (f instanceof StringOption) return new StringOptionUI((StringOption)f, mini);
		else if (f instanceof IntegerEnumOption) return new IntegerEnumOptionUI((IntegerEnumOption)f, mini);
		else if (f instanceof EnumOption) return new EnumOptionUI((EnumOption)f, mini);
		else if (f instanceof CustomOption) return new CustomOptionUI((CustomOption)f, mini);
		else {
			System.err.println("Error: No FormUI for " + f.getClass().getSimpleName() + ".");
			return null;
		}
	}
}
