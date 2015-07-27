package com.kreative.paint.frame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.imageio.ImageIO;

public class UnmakeFrame {
	public static void main(String[] args) {
		for (String arg : args) {
			try {
				File file = new File(arg);
				File parent = file.getParentFile();
				String basename = file.getName().replaceFirst("\\.[a-zA-Z0-9]+$", "") + "-";
				Frame frame = FrameReader.readFrame(file);
				// Frame Info
				File frnfFile = new File(parent, basename + "frameinfo.frnx");
				PrintWriter finfOut = new PrintWriter(new OutputStreamWriter(new FileOutputStream(frnfFile), "UTF-8"), true);
				FrameWriter.printFRNX(finfOut, frame);
				finfOut.flush();
				finfOut.close();
				// IIOWrite
				File iiowFile = new File(parent, basename + "iiowrite.png");
				ImageIO.write(frame.image, "png", iiowFile);
				// Stripped
				File stripFile = new File(parent, basename + "stripped.png");
				DataInputStream stripIn = new DataInputStream(new FileInputStream(file));
				DataOutputStream stripOut = new DataOutputStream(new FileOutputStream(stripFile));
				FrameWriter.stripFRNF(stripIn, stripOut);
				stripOut.flush();
				stripOut.close();
				stripIn.close();
			} catch (IOException e) {
				System.err.println("Error processing " + arg + ": " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
