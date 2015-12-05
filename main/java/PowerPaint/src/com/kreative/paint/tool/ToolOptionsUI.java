package com.kreative.paint.tool;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import com.kreative.paint.ToolContext;
import com.kreative.paint.ToolContextListener;
import com.kreative.paint.tool.ToolOptions.*;
import com.kreative.paint.tool.ui.*;
import com.kreative.paint.util.SwingUtils;

public class ToolOptionsUI extends JPanel implements ToolContextListener {
	private static final long serialVersionUID = 1L;
	private static final int GRID_SPACING = 8; // Grid Spacing
	private static final int GRID_SPACING_MINI = 4; // Grid Spacing (Mini)
	private static final int SECTION_SPACING = 16; // Section Spacing
	private static final int SECTION_SPACING_MINI = 8; // Section Spacing (Mini)
	
	private JPanel drawOptionsPanel;
	private JPanel cornerRadiusPanel;
	private JPanel quickShadowPanel;
	private JPanel powerBrushPanel;
	private JPanel powerSpraypaintPanel;
	private JPanel curlPanel;
	private JPanel regPolygonPanel;
	private JPanel alphabetPanel;
	private JPanel brushesPanel;
	private JPanel calligraphyPanel;
	private JPanel charcoalPanel;
	private JPanel framePanel;
	private JPanel rubberStampPanel;
	private JPanel powerShapePanel;
	private JPanel sprinklesPanel;
	private JPanel customPanel;
	private String customTitleString;
	private JLabel customTitleLabel;
	private JLabel noOptionsLabel;
	
	public ToolOptionsUI(ToolContext tc, boolean mini) {
		List<JComponent> rows = new Vector<JComponent>();
		rows.add(drawOptionsPanel = makeSection(ToolUtilities.messages.getString("options.DrawOptions"), new DrawOptionsUI(tc, mini), mini));
		rows.add(cornerRadiusPanel = makeSection(ToolUtilities.messages.getString("options.RoundCorners"), new CornerRadiusUI(tc, mini), mini));
		rows.add(quickShadowPanel = makeSection(ToolUtilities.messages.getString("options.QuickShadow"), new QuickShadowUI(tc, mini), mini));
		rows.add(powerBrushPanel = makeSection(ToolUtilities.messages.getString("options.PowerBrushes"), new PowerBrushUI(tc, mini), mini));
		rows.add(powerSpraypaintPanel = makeSection(ToolUtilities.messages.getString("options.PowerSpraypaint"), new PowerSpraypaintUI(tc, mini), mini));
		rows.add(curlPanel = makeSection(ToolUtilities.messages.getString("options.Curl"), new CurlUI(tc, mini), mini));
		rows.add(regPolygonPanel = makeSection(ToolUtilities.messages.getString("options.RegPoly"), new RegPolygonUI(tc, mini), mini));
		rows.add(alphabetPanel = makeSection(ToolUtilities.messages.getString("options.Alphabet"), new AlphabetsUI(tc, mini), mini));
		rows.add(brushesPanel = makeSection(ToolUtilities.messages.getString("options.Brushes"), new BrushesUI(tc, mini), mini));
		rows.add(calligraphyPanel = makeSection(ToolUtilities.messages.getString("options.Calligraphy"), new CalligraphyBrushesUI(tc, mini), mini));
		rows.add(charcoalPanel = makeSection(ToolUtilities.messages.getString("options.Charcoal"), new CharcoalBrushesUI(tc, mini), mini));
		rows.add(framePanel = makeSection(ToolUtilities.messages.getString("options.Frames"), new FramesUI(tc, mini), mini));
		rows.add(rubberStampPanel = makeSection(ToolUtilities.messages.getString("options.Stamps"), new RubberStampsUI(tc, mini), mini));
		rows.add(powerShapePanel = makeSection(ToolUtilities.messages.getString("options.PowerShapes"), new PowerShapesUI(tc, mini), mini));
		rows.add(sprinklesPanel = makeSection(ToolUtilities.messages.getString("options.Sprinkles"), new SprinklesUI(tc, mini), mini));
		rows.add(customPanel = makeSection(ToolUtilities.messages.getString("options.Custom"), new CustomUI(tc, mini), mini));
		rows.add(noOptionsLabel = new JLabel(ToolUtilities.messages.getString("options.na")));
		noOptionsLabel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
		if (mini) SwingUtils.shrink(noOptionsLabel);
		toolChanged(tc, tc.getTool(), tc.getTool());
		setLayout(new GridLayout(1,1));
		add(makeSectionStack(rows, mini));
		tc.addToolContextListener(this);
	}

	public void toolSettingsChanged(ToolContext src, long delta) {}
	
	public void modeChanged(ToolContext src, boolean drawMode) {}
	
	public void toolChanged(ToolContext src, Tool previousTool, Tool nextTool) {
		drawOptionsPanel.setVisible(nextTool instanceof DrawOptions);
		cornerRadiusPanel.setVisible(nextTool instanceof CornerRadius);
		quickShadowPanel.setVisible(nextTool instanceof QuickShadow);
		powerBrushPanel.setVisible(nextTool instanceof PowerBrush);
		powerSpraypaintPanel.setVisible(nextTool instanceof PowerSpraypaint);
		curlPanel.setVisible(nextTool instanceof Curl);
		regPolygonPanel.setVisible(nextTool instanceof RegPolygon);
		alphabetPanel.setVisible(nextTool instanceof Alphabets);
		brushesPanel.setVisible(nextTool instanceof Brushes);
		calligraphyPanel.setVisible(nextTool instanceof CalligraphyBrushes);
		charcoalPanel.setVisible(nextTool instanceof CharcoalBrushes);
		framePanel.setVisible(nextTool instanceof Frames);
		rubberStampPanel.setVisible(nextTool instanceof RubberStamps);
		powerShapePanel.setVisible(nextTool instanceof PowerShapes);
		sprinklesPanel.setVisible(nextTool instanceof Sprinkles);
		customPanel.setVisible(nextTool instanceof Custom);
		customTitleLabel.setText(customTitleString.replace("$", nextTool.getName()));
		noOptionsLabel.setVisible(!(nextTool instanceof ToolOptions));
		invalidate();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				pack();
			}
		});
	}
	
	public void toolDoubleClicked(ToolContext src, Tool tool) {}
	
	public void pack() {
		Component c = this;
		c.invalidate();
		while (c != null) {
			if (c instanceof Window) { ((Window)c).pack(); break; }
			else if (c instanceof Frame) { ((Frame)c).pack(); break; }
			else if (c instanceof Dialog) { ((Dialog)c).pack(); break; }
			else c = c.getParent();
		}
	}
	
	private JPanel makeSection(String label, JComponent content, boolean mini) {
		JPanel p = new JPanel(new BorderLayout(mini ? GRID_SPACING_MINI : GRID_SPACING, mini ? GRID_SPACING_MINI : GRID_SPACING));
		JLabel l = new JLabel(label);
		if (mini) SwingUtils.shrink(l);
		if (content instanceof CustomUI) {
			customTitleString = label;
			customTitleLabel = l;
		}
		Border inner = BorderFactory.createEmptyBorder(0, 0, mini ? 2 : 4, 0);
		Border outer = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black);
		l.setBorder(BorderFactory.createCompoundBorder(outer, inner));
		l.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		l.setHorizontalAlignment(JLabel.LEFT);
		l.setHorizontalTextPosition(JLabel.LEFT);
		p.add(l, BorderLayout.NORTH);
		p.add(content, BorderLayout.CENTER);
		return p;
	}
	
	private JPanel makeSectionStack(Collection<JComponent> rows, boolean mini) {
		int s = mini ? SECTION_SPACING_MINI : SECTION_SPACING;
		JPanel ip = new JPanel(new BorderLayout());
		for (JComponent p : rows) {
			p.setBorder(BorderFactory.createEmptyBorder(0, 0, s, 0));
			ip.add(p, BorderLayout.CENTER);
			JPanel tp = new JPanel(new BorderLayout());
			tp.add(ip, BorderLayout.NORTH);
			ip = tp;
		}
		ip.setBorder(BorderFactory.createEmptyBorder(s, s, 0, s));
		return ip;
	}
}
