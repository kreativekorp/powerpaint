package test;

import java.awt.Component;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.kreative.paint.sprite.Sprite;
import com.kreative.paint.sprite.SpriteSheet;
import com.kreative.paint.sprite.SpriteSheetReader;

public class SpriteSheetDemo {
	public static void main(String[] args) throws IOException {
		for (String arg : args) {
			File file = new File(arg);
			SpriteSheet sheet = SpriteSheetReader.readSpriteSheet(file);
			SpriteSheetTreeModel model = new SpriteSheetTreeModel(sheet);
			SpriteSheetCellRenderer renderer = new SpriteSheetCellRenderer();
			JTree view = new JTree(model); view.setCellRenderer(renderer);
			JScrollPane pane = new JScrollPane(view,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			JFrame frame = new JFrame(file.getName());
			frame.setContentPane(pane);
			frame.setSize(300, 300);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		}
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
			return label;
		}
	}
}
