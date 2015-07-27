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
		if (args.length == 0) {
			printHelp();
		} else {
			File outDir = null;
			boolean makeFRNX = false;
			boolean makeIIOW = false;
			boolean makeStrip = false;
			boolean processingFlags = true;
			int i = 0; while (i < args.length) {
				String arg = args[i++];
				if (processingFlags && arg.startsWith("-")) {
					if (arg.equals("--")) {
						processingFlags = false;
					} else if (arg.equals("-D") && i < args.length) {
						outDir = new File(args[i++]);
					} else if (arg.equals("-F")) {
						makeFRNX = true;
					} else if (arg.equals("-I")) {
						makeIIOW = true;
					} else if (arg.equals("-P")) {
						makeStrip = true;
					} else if (arg.equals("--help")) {
						printHelp();
					} else {
						System.err.println("Ignoring unknown option: " + arg);
					}
				} else {
					try {
						processFile(new File(arg), outDir, makeFRNX, makeIIOW, makeStrip);
					} catch (IOException e) {
						System.err.println("Error processing " + arg + ": " + e.getMessage());
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private static void printHelp() {
		System.out.println();
		System.out.println("UnmakeFrame - Extract frame info from PNG.");
		System.out.println();
		System.out.println("Options:");
		System.out.println("  -D <path>  Set output directory.");
		System.out.println("  -F         Output FRNX file.");
		System.out.println("  -I         Output PNG file rewritten by ImageIO.");
		System.out.println("  -P         Output PNG file without frNF chunk.");
		System.out.println("  --         Treat remaining arguments as file names.");
		System.out.println();
		System.out.println("If none of -F, -I, -P are specified, all are implied.");
		System.out.println();
	}
	
	private static void processFile(
		File inFile,
		File outDir,
		boolean makeFRNX,
		boolean makeIIOW,
		boolean makeStrip
	) throws IOException {
		if (!(makeFRNX || makeIIOW || makeStrip)) {
			makeFRNX = makeIIOW = makeStrip = true;
		}
		File frnfFile, iiowFile, stripFile;
		if (outDir == null) {
			File parent = inFile.getParentFile();
			String basename = inFile.getName().replaceFirst("\\.[a-zA-Z0-9]+$", "");
			frnfFile = new File(parent, basename + "-frameinfo.frnx");
			iiowFile = new File(parent, basename + "-iiowrite.png");
			stripFile = new File(parent, basename + "-stripped.png");
		} else if (makeIIOW && makeStrip) {
			String basename = inFile.getName().replaceFirst("\\.[a-zA-Z0-9]+$", "");
			frnfFile = new File(outDir, basename + "-frameinfo.frnx");
			iiowFile = new File(outDir, basename + "-iiowrite.png");
			stripFile = new File(outDir, basename + "-stripped.png");
		} else {
			String basename = inFile.getName().replaceFirst("\\.[a-zA-Z0-9]+$", "");
			frnfFile = new File(outDir, basename + ".frnx");
			iiowFile = new File(outDir, basename + ".png");
			stripFile = new File(outDir, basename + ".png");
		}
		Frame frame = FrameReader.readFrame(inFile);
		if (makeFRNX) {
			PrintWriter finfOut = new PrintWriter(new OutputStreamWriter(new FileOutputStream(frnfFile), "UTF-8"), true);
			FrameWriter.printFRNX(finfOut, frame);
			finfOut.flush();
			finfOut.close();
		}
		if (makeIIOW) {
			ImageIO.write(frame.image, "png", iiowFile);
		}
		if (makeStrip) {
			DataInputStream stripIn = new DataInputStream(new FileInputStream(inFile));
			DataOutputStream stripOut = new DataOutputStream(new FileOutputStream(stripFile));
			FrameWriter.stripFRNF(stripIn, stripOut);
			stripOut.flush();
			stripOut.close();
			stripIn.close();
		}
	}
}
