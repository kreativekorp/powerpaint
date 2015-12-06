package com.kreative.paint.material.frame;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class FrameWriter {
	public static final long PNG_MAGIC_NUMBER = 0x89504E470D0A1A0AL;
	public static final int PNG_CHUNK_FRNF = 0x66724E46;
	public static final int PNG_CHUNK_IEND = 0x49454E44;
	
	public static void stripFRNF(DataInputStream in, DataOutputStream out) throws IOException {
		long magic = in.readLong();
		if (magic != PNG_MAGIC_NUMBER) throw new IOException("Not a PNG file.");
		out.writeLong(magic);
		while (in.available() > 0) {
			int cl = in.readInt();
			int ct = in.readInt();
			byte[] cd = new byte[cl];
			in.read(cd);
			int cc = in.readInt();
			if (ct != PNG_CHUNK_FRNF) {
				out.writeInt(cl);
				out.writeInt(ct);
				out.write(cd);
				out.writeInt(cc);
			}
		}
	}
	
	public static void injectFRNF(DataInputStream in, DataOutputStream out, Frame frame) throws IOException {
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
				d.writeInt(PNG_CHUNK_FRNF);
				d.writeShort(frame.contentStartX);
				d.writeShort(frame.contentStartY);
				d.writeShort(frame.contentExtentX);
				d.writeShort(frame.contentExtentY);
				d.writeShort(frame.repeatStartX);
				d.writeShort(frame.repeatStartY);
				d.writeShort(frame.repeatExtentX);
				d.writeShort(frame.repeatExtentY);
				d.writeShort(frame.widthMultiplier);
				d.writeShort(frame.widthBase);
				d.writeShort(frame.heightMultiplier);
				d.writeShort(frame.heightBase);
				if (frame.name != null) d.writeUTF(frame.name);
				d.close();
				b.close();
				byte[] frnf = b.toByteArray();
				out.writeInt(frnf.length - 4);
				out.write(frnf);
				out.writeInt(new CRCCalculator().calculateCRC(frnf));
			}
			if (ct != PNG_CHUNK_FRNF) {
				out.writeInt(cl);
				out.writeInt(ct);
				out.write(cd);
				out.writeInt(cc);
			}
		}
	}
	
	public static void printFRNX(PrintStream out, Frame frame) {
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.println("<!DOCTYPE frame PUBLIC \"-//Kreative//DTD FrameInfo 1.0//EN\" \"frnx.dtd\">");
		out.println("<frame");
		if (frame.name != null) {
			out.println("\tname=\"" + xmls(frame.name) + "\"");
		}
		out.println("\tcontent-start-x=\"" + frame.contentStartX + "\"");
		out.println("\tcontent-start-y=\"" + frame.contentStartY + "\"");
		out.println("\tcontent-extent-x=\"" + frame.contentExtentX + "\"");
		out.println("\tcontent-extent-y=\"" + frame.contentExtentY + "\"");
		out.println("\trepeat-start-x=\"" + frame.repeatStartX + "\"");
		out.println("\trepeat-start-y=\"" + frame.repeatStartY + "\"");
		out.println("\trepeat-extent-x=\"" + frame.repeatExtentX + "\"");
		out.println("\trepeat-extent-y=\"" + frame.repeatExtentY + "\"");
		if (frame.widthMultiplier > 0) {
			out.println("\trestrict-content-width-multiplier=\"" + frame.widthMultiplier + "\"");
			out.println("\trestrict-content-width-base=\"" + frame.widthBase + "\"");
		}
		if (frame.heightMultiplier > 0) {
			out.println("\trestrict-content-height-multiplier=\"" + frame.heightMultiplier + "\"");
			out.println("\trestrict-content-height-base=\"" + frame.heightBase + "\"");
		}
		out.println("/>");
	}
	
	public static void printFRNX(PrintWriter out, Frame frame) {
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.println("<!DOCTYPE frame PUBLIC \"-//Kreative//DTD FrameInfo 1.0//EN\" \"frnx.dtd\">");
		out.println("<frame");
		if (frame.name != null) {
			out.println("\tname=\"" + xmls(frame.name) + "\"");
		}
		out.println("\tcontent-start-x=\"" + frame.contentStartX + "\"");
		out.println("\tcontent-start-y=\"" + frame.contentStartY + "\"");
		out.println("\tcontent-extent-x=\"" + frame.contentExtentX + "\"");
		out.println("\tcontent-extent-y=\"" + frame.contentExtentY + "\"");
		out.println("\trepeat-start-x=\"" + frame.repeatStartX + "\"");
		out.println("\trepeat-start-y=\"" + frame.repeatStartY + "\"");
		out.println("\trepeat-extent-x=\"" + frame.repeatExtentX + "\"");
		out.println("\trepeat-extent-y=\"" + frame.repeatExtentY + "\"");
		if (frame.widthMultiplier > 0) {
			out.println("\trestrict-content-width-multiplier=\"" + frame.widthMultiplier + "\"");
			out.println("\trestrict-content-width-base=\"" + frame.widthBase + "\"");
		}
		if (frame.heightMultiplier > 0) {
			out.println("\trestrict-content-height-multiplier=\"" + frame.heightMultiplier + "\"");
			out.println("\trestrict-content-height-base=\"" + frame.heightBase + "\"");
		}
		out.println("/>");
	}
	
	private static String xmls(String s) {
		return s.replaceAll("&", "&amp;")
		        .replaceAll("<", "&lt;")
		        .replaceAll(">", "&gt;")
		        .replaceAll("\"", "&quot;");
	}
}
