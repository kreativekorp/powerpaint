package test;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import com.kreative.paint.material.sprite.ArrayOrdering;
import com.kreative.paint.material.sprite.ColorTransform;
import com.kreative.paint.material.sprite.Sprite;
import com.kreative.paint.material.sprite.SpriteIntent;
import com.kreative.paint.material.sprite.SpriteSheet;
import com.kreative.paint.material.sprite.SpriteSheetReader;

public class SpriteSheetDemo {
	public static void main(String[] args) throws IOException {
		SpriteSheetReader.Options o = new SpriteSheetReader.Options();
		boolean processingFlags = true;
		int i = 0; while (i < args.length) {
			String arg = args[i++];
			if (processingFlags && arg.startsWith("-")) {
				if (arg.equals("--")) {
					processingFlags = false;
				} else if (arg.equals("-s") && i < args.length) {
					arg = args[i++].trim();
					if (arg.equalsIgnoreCase("whole")) {
						o.setDefaultSlicingNone();
					} else if (arg.equalsIgnoreCase("strip")) {
						o.setDefaultSlicingStrip();
					} else {
						Pattern p = Pattern.compile("([0-9]+)\\s*[Xx,]\\s*([0-9]+)");
						Matcher m = p.matcher(arg);
						if (m.matches()) {
							int w = Integer.parseInt(m.group(1));
							int h = Integer.parseInt(m.group(2));
							o.setDefaultSlicingFixed(w, h);
						} else {
							System.err.println("Ignoring invalid option parameter: -s " + arg);
						}
					}
				} else if (arg.equals("-h") && i < args.length) {
					arg = args[i++].trim();
					Pattern p = Pattern.compile(
						"([Cc][Ee][Nn][Tt][Ee][Rr]|[+-]?[0-9]+)\\s*,\\s*" +
						"([Cc][Ee][Nn][Tt][Ee][Rr]|[+-]?[0-9]+)"
					);
					Matcher m = p.matcher(arg);
					if (m.matches()) {
						int x = m.group(1).equalsIgnoreCase("center") ?
						        SpriteSheetReader.Options.HOTSPOT_CENTER :
						        Integer.parseInt(m.group(1));
						int y = m.group(2).equalsIgnoreCase("center") ?
						        SpriteSheetReader.Options.HOTSPOT_CENTER :
						        Integer.parseInt(m.group(2));
						o.setDefaultHotspot(x, y);
					} else {
						System.err.println("Ignoring invalid option parameter: -h " + arg);
					}
				} else if (arg.equals("-o")) {
					o.setDefaultSlicingOrder(ArrayOrdering.fromString(args[i++]));
				} else if (arg.equals("-t")) {
					o.setDefaultColorTransform(ColorTransform.fromString(args[i++]));
				} else if (arg.equals("-p")) {
					arg = args[i++].trim();
					if (arg.equalsIgnoreCase("auto")) {
						o.setDefaultPresentationAuto();
					} else {
						Pattern p = Pattern.compile(
							"([+-]?[0-9]+)\\s*[Xx,]\\s*" +
							"([+-]?[0-9]+)\\s*,\\s*(.+)"
						);
						Matcher m = p.matcher(arg);
						if (m.matches()) {
							int w = Integer.parseInt(m.group(1));
							int h = Integer.parseInt(m.group(2));
							ArrayOrdering order = ArrayOrdering.fromString(m.group(3));
							o.setDefaultPresentation(w, h, order);
						} else {
							System.err.println("Ignoring invalid option parameter: -p " + arg);
						}
					}
				} else if (arg.equals("-i")) {
					o.setDefaultIntent(SpriteIntent.fromString(args[i++]));
				} else if (arg.equals("-d")) {
					arg = args[i++].trim();
					if (arg.equalsIgnoreCase("flat")) {
						o.setDefaultStructureFlat();
					} else if (arg.equalsIgnoreCase("single")) {
						o.setDefaultStructureSingleParent(false);
					} else if (arg.equalsIgnoreCase("xsingle")) {
						o.setDefaultStructureSingleParent(true);
					} else if (arg.equalsIgnoreCase("multi")) {
						o.setDefaultStructureMultipleParents(false);
					} else if (arg.equalsIgnoreCase("xmulti")) {
						o.setDefaultStructureMultipleParents(true);
					} else {
						System.err.println("Ignoring invalid option parameter: -d " + arg);
					}
				} else {
					System.err.println("Ignoring unknown option: " + arg);
				}
			} else {
				try {
					processFile(new File(arg), o);
				} catch (IOException e) {
					System.err.println("Error processing " + arg + ": " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void processFile(File file, SpriteSheetReader.Options o) throws IOException {
		SpriteSheet sheet = SpriteSheetReader.readSpriteSheet(file, o);
		SpriteSheetTreeModel model = new SpriteSheetTreeModel(sheet);
		SpriteSheetCellRenderer renderer = new SpriteSheetCellRenderer();
		final JTree view = new JTree(model);
		view.setRowHeight(0);
		view.setCellRenderer(renderer);
		view.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() != MouseEvent.BUTTON1) {
					Object o = view.getLastSelectedPathComponent();
					if (o instanceof Sprite) {
						Sprite s = (Sprite)o;
						view.setCursor(s.getPreparedCursor(e.isAltDown()));
					}
				}
			}
			@Override public void mouseEntered(MouseEvent e) {}
			@Override public void mouseExited(MouseEvent e) {}
			@Override public void mousePressed(MouseEvent e) {}
			@Override public void mouseReleased(MouseEvent e) {}
		});
		JScrollPane pane = new JScrollPane(view,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		JFrame frame = new JFrame(file.getName());
		frame.setContentPane(pane);
		frame.setSize(300, 300);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	private static class SpriteSheetTreeModel implements TreeModel {
		private final SpriteSheet sheet;
		public SpriteSheetTreeModel(SpriteSheet sheet) {
			this.sheet = sheet;
		}
		@Override
		public Object getRoot() {
			return sheet;
		}
		@Override
		public int getChildCount(Object parent) {
			if (parent instanceof SpriteSheet) {
				return ((SpriteSheet)parent).getSpriteCount();
			}
			if (parent instanceof Sprite) {
				return ((Sprite)parent).getChildCount();
			}
			return 0;
		}
		@Override
		public Object getChild(Object parent, int index) {
			if (parent instanceof SpriteSheet) {
				return ((SpriteSheet)parent).getSprite(index);
			}
			if (parent instanceof Sprite) {
				return ((Sprite)parent).getChild(index);
			}
			return null;
		}
		@Override
		public int getIndexOfChild(Object parent, Object child) {
			int count = getChildCount(parent);
			for (int i = 0; i < count; i++) {
				if (getChild(parent, i) == child) {
					return i;
				}
			}
			return -1;
		}
		@Override
		public boolean isLeaf(Object o) {
			return getChildCount(o) == 0;
		}
		@Override public void addTreeModelListener(TreeModelListener listener) {}
		@Override public void removeTreeModelListener(TreeModelListener listener) {}
		@Override public void valueForPathChanged(TreePath path, Object o) {}
	}
	
	private static class SpriteSheetCellRenderer extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = 1L;
		@Override
		public Component getTreeCellRendererComponent(
			JTree tree, Object value, boolean sel, boolean expanded,
			boolean leaf, int row, boolean hasFocus
		) {
			JLabel label = (JLabel)super.getTreeCellRendererComponent(
				tree, value, sel, expanded, leaf, row, hasFocus
			);
			if (value instanceof SpriteSheet) {
				SpriteSheet sheet = (SpriteSheet)value;
				if (sheet.name != null && sheet.name.length() > 0) {
					label.setText(sheet.name);
				} else {
					label.setText("<SpriteSheet>");
				}
			}
			if (value instanceof Sprite) {
				Sprite s = (Sprite)value;
				label.setIcon(new ImageIcon(s.getPreparedImage()));
				if (s.getName() != null && s.getName().length() > 0) {
					label.setText(s.getName());
				} else {
					label.setText("<Sprite>");
				}
			}
			label.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			return label;
		}
	}
}
