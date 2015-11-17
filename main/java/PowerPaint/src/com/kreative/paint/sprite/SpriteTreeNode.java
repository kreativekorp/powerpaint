package com.kreative.paint.sprite;

import java.util.ArrayList;
import java.util.List;

public abstract class SpriteTreeNode {
	public final String name;
	public final int index;
	public final int duration;
	
	private SpriteTreeNode(String name, int index, int duration) {
		this.name = name;
		this.index = index;
		this.duration = duration;
	}
	
	public static class Leaf extends SpriteTreeNode {
		public final int count;
		
		public Leaf(String name, int index, int duration, int count) {
			super(name, index, duration);
			this.count = count;
		}
	}
	
	public static class Branch extends SpriteTreeNode {
		public final List<SpriteTreeNode> children;
		
		public Branch(String name, int index, int duration) {
			super(name, index, duration);
			this.children = new ArrayList<SpriteTreeNode>();
		}
		
		public int getChildCount() {
			int count = 0;
			for (SpriteTreeNode child : children) {
				if (child instanceof Leaf) {
					count += ((Leaf)child).count;
				} else {
					count++;
				}
			}
			return count;
		}
		
		public SpriteTreeNode getChild(int index) {
			if (index < 0) return null;
			for (SpriteTreeNode child : children) {
				if (child instanceof Leaf) {
					int count = ((Leaf)child).count;
					if (index < count) {
						return new Leaf(
							child.name,
							child.index + index,
							child.duration,
							1
						);
					} else {
						index -= count;
					}
				} else {
					if (index == 0) {
						return child;
					} else {
						index--;
					}
				}
			}
			return null;
		}
		
		public SpriteTreeNode getChildByPath(int... path) {
			Branch parent = this;
			for (int index : path) {
				SpriteTreeNode child = parent.getChild(index);
				if (child instanceof Branch) {
					parent = (Branch)child;
				} else {
					return child;
				}
			}
			return parent;
		}
	}
}
