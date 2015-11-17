package com.kreative.paint.sprite;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class SpriteSheetWriter {
	public static final long PNG_MAGIC_NUMBER = 0x89504E470D0A1A0AL;
	public static final int PNG_CHUNK_SPNF = 0x73704E46;
	public static final int PNG_CHUNK_IEND = 0x49454E44;
	
	public static void stripSPNF(DataInputStream in, DataOutputStream out) throws IOException {
		long magic = in.readLong();
		if (magic != PNG_MAGIC_NUMBER) throw new IOException("Not a PNG file.");
		out.writeLong(magic);
		while (in.available() > 0) {
			int cl = in.readInt();
			int ct = in.readInt();
			byte[] cd = new byte[cl];
			in.read(cd);
			int cc = in.readInt();
			if (ct != PNG_CHUNK_SPNF) {
				out.writeInt(cl);
				out.writeInt(ct);
				out.write(cd);
				out.writeInt(cc);
			}
		}
	}
	
	public static void injectSPNF(DataInputStream in, DataOutputStream out, SpriteSheet sheet) throws IOException {
		long magic = in.readLong();
		if (magic != PNG_MAGIC_NUMBER) throw new IOException("Not a PNG file.");
		out.writeLong(magic);
		while (in.available() > 0) {
			int cl = in.readInt();
			int ct = in.readInt();
			byte[] cd = new byte[cl];
			in.read(cd);
			int cc = in.readInt();
			if (ct == PNG_CHUNK_IEND) {
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				DataOutputStream d = new DataOutputStream(b);
				d.writeInt(PNG_CHUNK_SPNF);
				writeSpriteSheet(d, sheet);
				d.close();
				b.close();
				byte[] spnf = b.toByteArray();
				out.writeInt(spnf.length - 4);
				out.write(spnf);
				out.writeInt(new CRCCalculator().calculateCRC(spnf));
			}
			if (ct != PNG_CHUNK_SPNF) {
				out.writeInt(cl);
				out.writeInt(ct);
				out.write(cd);
				out.writeInt(cc);
			}
		}
	}
	
	private static void writeSpriteSheet(DataOutputStream d, SpriteSheet sheet) throws IOException {
		d.writeUTF((sheet.name == null) ? "" : sheet.name);
		d.writeShort(sheet.intent);
		d.writeShort(sheet.columns);
		d.writeShort(sheet.rows);
		d.writeShort(sheet.order.intValue);
		d.writeShort(sheet.slices.size());
		for (SpriteSheetSlice slice : sheet.slices) writeSlice(d, slice);
		d.writeShort(sheet.root.children.size());
		for (SpriteTreeNode node : sheet.root.children) writeTreeNode(d, node);
	}
	
	private static void writeSlice(DataOutputStream d, SpriteSheetSlice slice) throws IOException {
		d.writeShort(slice.startX);
		d.writeShort(slice.startY);
		d.writeShort(slice.cellWidth);
		d.writeShort(slice.cellHeight);
		d.writeShort(slice.hotspotX);
		d.writeShort(slice.hotspotY);
		d.writeShort(slice.deltaX);
		d.writeShort(slice.deltaY);
		d.writeShort(slice.columns);
		d.writeShort(slice.rows);
		d.writeShort(slice.order.intValue);
		d.writeShort(slice.transform.intValue);
	}
	
	private static void writeTreeNode(DataOutputStream d, SpriteTreeNode node) throws IOException {
		d.writeUTF((node.name == null) ? "" : node.name);
		d.writeShort(node.index);
		d.writeShort(node.duration);
		if (node instanceof SpriteTreeNode.Leaf) {
			SpriteTreeNode.Leaf l = (SpriteTreeNode.Leaf)node;
			d.writeShort(l.count);
		} else if (node instanceof SpriteTreeNode.Branch) {
			SpriteTreeNode.Branch b = (SpriteTreeNode.Branch)node;
			d.writeShort(0x8000 + b.children.size());
			for (SpriteTreeNode c : b.children) writeTreeNode(d, c);
		} else {
			d.writeShort(0);
		}
	}
	
	public static void printSPNX(PrintStream out, SpriteSheet sheet) {
		out.println(sheetToString(sheet));
	}
	
	public static void printSPNX(PrintWriter out, SpriteSheet sheet) {
		out.println(sheetToString(sheet));
	}
	
	private static String sheetToString(SpriteSheet sheet) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<!DOCTYPE sprite-sheet PUBLIC \"-//Kreative//DTD SpriteInfo 1.0//EN\" \"spnx.dtd\">\n");
		
		List<String> props = new ArrayList<String>();
		if (sheet.name != null && sheet.name.length() > 0) {
			props.add("name=\"" + xmls(sheet.name) + "\"");
		}
		if (sheet.intent != 0) {
			props.add("intent=\"" + xmls(SpriteIntent.toString(sheet.intent)) + "\"");
		}
		if (sheet.columns > 0 && sheet.rows > 0) {
			props.add("cols=\"" + sheet.columns + "\" rows=\"" + sheet.rows + "\"");
		} else if (sheet.columns > 0) {
			props.add("cols=\"" + sheet.columns + "\"");
		} else if (sheet.rows > 0) {
			props.add("rows=\"" + sheet.rows + "\"");
		}
		if (sheet.columns > 0 || sheet.rows > 0 || sheet.order != ArrayOrdering.LTR_TTB) {
			props.add("order=\"" + orderToString(sheet.order) + "\"");
		}
		if (props.isEmpty()) {
			sb.append("<sprite-sheet>");
		} else {
			for (int i = 0, j = props.size() - 1; j >= 0; i++, j--) {
				sb.append((i == 0) ? "<sprite-sheet " : "              ");
				sb.append(props.get(i));
				sb.append((j == 0) ? ">" : "\n");
			}
		}
		
		sb.append("\n");
		for (SpriteSheetSlice slice : sheet.slices) {
			sb.append("\n\t");
			sb.append(sliceToString(slice, "\t"));
		}
		
		sb.append("\n");
		for (SpriteTreeNode node : sheet.root.children) {
			sb.append("\n\t");
			sb.append(treeNodeToString(node, "\t"));
		}
		
		sb.append("\n\n");
		sb.append("</sprite-sheet>");
		return sb.toString();
	}
	
	private static String sliceToString(SpriteSheetSlice slice, String prefix) {
		return "<slice sx=\""+slice.startX+"\" sy=\""+slice.startY+"\"\n"+
		prefix+"       cw=\""+slice.cellWidth+"\" ch=\""+slice.cellHeight+"\"\n"+
		prefix+"       chx=\""+slice.hotspotX+"\" chy=\""+slice.hotspotY+"\"\n"+
		prefix+"       cdx=\""+slice.deltaX+"\" cdy=\""+slice.deltaY+"\"\n"+
		prefix+"       cols=\""+slice.columns+"\" rows=\""+slice.rows+"\"\n"+
		prefix+"       order=\""+orderToString(slice.order)+"\"\n"+
		prefix+"       color-transform=\""+slice.transform.toString().replaceAll("\\s+","\n"+
		prefix+"                        ")+"\"/>";
	}
	
	private static String treeNodeToString(SpriteTreeNode node, String prefix) {
		StringBuffer sb = new StringBuffer();
		sb.append("<sprite");
		if (node instanceof SpriteTreeNode.Branch) {
			sb.append("-set");
		}
		if (node.name != null && node.name.length() > 0) {
			sb.append(" name=\"");
			sb.append(xmls(node.name));
			sb.append("\"");
		}
		sb.append(" index=\"");
		sb.append(node.index);
		sb.append("\"");
		if (node.duration != 0) {
			sb.append(" duration=\"");
			sb.append(node.duration);
			sb.append("\"");
		}
		if (node instanceof SpriteTreeNode.Leaf) {
			int count = ((SpriteTreeNode.Leaf)node).count;
			sb.append(" count=\"");
			sb.append(count);
			sb.append("\"");
		}
		if (node instanceof SpriteTreeNode.Branch) {
			sb.append(">");
			List<SpriteTreeNode> children = ((SpriteTreeNode.Branch)node).children;
			if (!children.isEmpty()) {
				if (children.size() == 1) {
					sb.append(treeNodeToString(children.get(0), prefix));
				} else {
					String newPrefix = prefix + "\t";
					for (SpriteTreeNode child : children) {
						sb.append("\n");
						sb.append(newPrefix);
						sb.append(treeNodeToString(child, newPrefix));
					}
					sb.append("\n");
					sb.append(prefix);
				}
			}
			sb.append("</sprite-set>");
		} else {
			sb.append("/>");
		}
		return sb.toString();
	}
	
	private static String orderToString(ArrayOrdering order) {
		return order.name().toLowerCase().replace('_', '-');
	}
	
	private static String xmls(String s) {
		return s.replaceAll("&", "&amp;")
		        .replaceAll("<", "&lt;")
		        .replaceAll(">", "&gt;")
		        .replaceAll("\"", "&quot;");
	}
}
