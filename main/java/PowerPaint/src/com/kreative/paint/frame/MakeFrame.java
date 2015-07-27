package com.kreative.paint.frame;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class MakeFrame {
	public static void main(String[] args) {
		for (String arg : args) {
			try {
				File file = new File(arg);
				File parent = file.getParentFile();
				String basename = file.getName();
				File pngFile, frnxFile;
				if (basename.toLowerCase().endsWith("-frameinfo.frnx")) {
					basename = basename.substring(0, basename.length() - 15);
					frnxFile = file;
					pngFile = new File(parent, basename + "-stripped.png");
					if (!pngFile.exists()) pngFile = new File(parent, basename + "-iiowrite.png");
					if (!pngFile.exists()) throw new IOException("Cannot find PNG counterpart.");
				} else if (basename.toLowerCase().endsWith("-stripped.png")
				        || basename.toLowerCase().endsWith("-iiowrite.png")) {
					basename = basename.substring(0, basename.length() - 13);
					pngFile = file;
					frnxFile = new File(parent, basename + "-frameinfo.frnx");
					if (!frnxFile.exists()) throw new IOException("Cannot find FRNX counterpart.");
				} else if (basename.toLowerCase().endsWith(".frnx")) {
					basename = basename.substring(0, basename.length() - 5);
					frnxFile = file;
					pngFile = new File(parent, basename + ".png");
					if (!pngFile.exists()) throw new IOException("Cannot find PNG counterpart.");
				} else if (basename.toLowerCase().endsWith(".png")) {
					basename = basename.substring(0, basename.length() - 4);
					pngFile = file;
					frnxFile = new File(parent, basename + ".frnx");
					if (!frnxFile.exists()) throw new IOException("Cannot find FRNX counterpart.");
				} else {
					throw new IOException("Unknown file type.");
				}
				basename = basename.replaceFirst("^#[0-9]+ ", "").trim();
				BufferedImage image = ImageIO.read(pngFile);
				InputStream frnxIn = new FileInputStream(frnxFile);
				Frame frame = FRNXParser.parse(basename, frnxIn, image);
				frnxIn.close();
				// Injected
				File injFile = new File(parent, basename + "-inject.png");
				DataInputStream injIn = new DataInputStream(new FileInputStream(pngFile));
				DataOutputStream injOut = new DataOutputStream(new FileOutputStream(injFile));
				FrameWriter.injectFRNF(injIn, injOut, frame);
				injOut.flush();
				injOut.close();
				injIn.close();
				// IIOWrite
				File iiowFile = new File(parent, basename + "-iioinject.png");
				ByteArrayOutputStream iioOut = new ByteArrayOutputStream();
				ImageIO.write(frame.image, "png", iioOut);
				iioOut.flush();
				iioOut.close();
				DataInputStream iiowIn = new DataInputStream(new ByteArrayInputStream(iioOut.toByteArray()));
				DataOutputStream iiowOut = new DataOutputStream(new FileOutputStream(iiowFile));
				FrameWriter.injectFRNF(iiowIn, iiowOut, frame);
				iiowOut.flush();
				iiowOut.close();
				iiowIn.close();
			} catch (IOException e) {
				System.err.println("Error processing " + arg + ": " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
