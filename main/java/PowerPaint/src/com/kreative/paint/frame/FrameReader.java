package com.kreative.paint.frame;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class FrameReader {
	public static final long PNG_MAGIC_NUMBER = 0x89504E470D0A1A0AL;
	public static final int PNG_CHUNK_FRNF = 0x66724E46;
	
	public static Frame readFrame(File file) throws IOException {
		String name = file.getName();
		name = name.replaceFirst("^#[0-9]+ ", "");
		name = name.replaceFirst("\\.[a-zA-Z0-9]+$", "");
		name = name.trim();
		BufferedImage image = ImageIO.read(file);
		return readFrame(name, file, image);
	}
	
	public static Frame readFrame(String name, byte[] data) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		BufferedImage image = ImageIO.read(in);
		in.close();
		return readFrame(name, data, image);
	}
	
	public static Frame readFrame(String name, InputStream in) throws IOException {
		in.mark(0x100000);
		BufferedImage image = ImageIO.read(in);
		in.reset();
		return readFrame(name, in, image);
	}
	
	public static Frame readFrame(String name, File file, BufferedImage image) throws IOException {
		InputStream in = new FileInputStream(file);
		Frame frame = readFrame(name, in, image);
		in.close();
		return frame;
	}
	
	public static Frame readFrame(String name, byte[] data, BufferedImage image) throws IOException {
		InputStream in = new ByteArrayInputStream(data);
		Frame frame = readFrame(name, in, image);
		in.close();
		return frame;
	}
	
	public static Frame readFrame(String name, InputStream in, BufferedImage image) throws IOException {
		DataInputStream data = new DataInputStream(in);
		if (data.readLong() == PNG_MAGIC_NUMBER) {
			while (data.available() > 0) {
				int cl = data.readInt();
				int ct = data.readInt();
				byte[] cd = new byte[cl];
				data.read(cd);
				data.readInt();
				if (ct == PNG_CHUNK_FRNF) {
					DataInputStream chunk = new DataInputStream(new ByteArrayInputStream(cd));
					int consx = (chunk.available() > 0) ? chunk.readShort() : 0;
					int consy = (chunk.available() > 0) ? chunk.readShort() : 0;
					int conex = (chunk.available() > 0) ? chunk.readShort() : 0;
					int coney = (chunk.available() > 0) ? chunk.readShort() : 0;
					Rectangle con = (
						(consx == 0 && consy == 0 && conex == 0 & coney == 0) ? null :
						new Rectangle(consx, consy, conex, coney)
					);
					int repsx = (chunk.available() > 0) ? chunk.readShort() : 0;
					int repsy = (chunk.available() > 0) ? chunk.readShort() : 0;
					int repex = (chunk.available() > 0) ? chunk.readShort() : 0;
					int repey = (chunk.available() > 0) ? chunk.readShort() : 0;
					Rectangle rep = (
						(repsx == 0 && repsy == 0 && repex == 0 && repey == 0) ? null :
						new Rectangle(repsx, repsy, repex, repey)
					);
					int reswm = (chunk.available() > 0) ? chunk.readShort() : 0;
					int reswb = (chunk.available() > 0) ? chunk.readShort() : 0;
					int reshm = (chunk.available() > 0) ? chunk.readShort() : 0;
					int reshb = (chunk.available() > 0) ? chunk.readShort() : 0;
					Rectangle res = (
						(reswm == 0 && reswb == 0 && reshm == 0 && reshb == 0) ? null :
						new Rectangle(reswb, reshb, reswm, reshm)
					);
					String iname = (chunk.available() > 0) ? chunk.readUTF() : null;
					if (iname == null || iname.length() == 0) iname = name;
					return new Frame(image, con, rep, res, iname);
				}
			}
		}
		return new Frame(image, name);
	}
}
