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
		if (args.length == 0) {
			printHelp();
		} else {
			File outDir = null;
			boolean makeIIOW = false;
			boolean makeInj = false;
			boolean processingFlags = true;
			int i = 0; while (i < args.length) {
				String arg = args[i++];
				if (processingFlags && arg.startsWith("-")) {
					if (arg.equals("--")) {
						processingFlags = false;
					} else if (arg.equals("-D") && i < args.length) {
						outDir = new File(args[i++]);
					} else if (arg.equals("-I")) {
						makeIIOW = true;
					} else if (arg.equals("-P")) {
						makeInj = true;
					} else if (arg.equals("--help")) {
						printHelp();
					} else {
						System.err.println("Ignoring unknown option: " + arg);
					}
				} else {
					try {
						processFile(new File(arg), outDir, makeIIOW, makeInj);
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
		System.out.println("MakeFrame - Inject frame info into PNG.");
		System.out.println();
		System.out.println("Options:");
		System.out.println("  -D <path>  Set output directory.");
		System.out.println("  -I         Output PNG file rewritten by ImageIO.");
		System.out.println("  -P         Output PNG file with frNF chunk.");
		System.out.println("  --         Treat remaining arguments as file names.");
		System.out.println();
		System.out.println("If neither -I nor -P is specified, both are implied.");
		System.out.println();
	}
	
	private static void processFile(
		File inFile,
		File outDir,
		boolean makeIIOW,
		boolean makeInj
	) throws IOException {
		if (!(makeIIOW || makeInj)) {
			makeIIOW = makeInj = true;
		}
		File parent = inFile.getParentFile();
		String basename = inFile.getName();
		// Find PNG and FRNX files.
		File pngFile, frnxFile;
		if (basename.toLowerCase().endsWith("-frameinfo.frnx")) {
			basename = basename.substring(0, basename.length() - 15);
			frnxFile = inFile;
			pngFile = new File(parent, basename + "-stripped.png");
			if (!pngFile.exists()) pngFile = new File(parent, basename + "-iiowrite.png");
			if (!pngFile.exists()) throw new IOException("Cannot find PNG counterpart.");
		} else if (basename.toLowerCase().endsWith("-stripped.png")
		        || basename.toLowerCase().endsWith("-iiowrite.png")) {
			basename = basename.substring(0, basename.length() - 13);
			pngFile = inFile;
			frnxFile = new File(parent, basename + "-frameinfo.frnx");
			if (!frnxFile.exists()) throw new IOException("Cannot find FRNX counterpart.");
		} else if (basename.toLowerCase().endsWith(".frnx")) {
			basename = basename.substring(0, basename.length() - 5);
			frnxFile = inFile;
			pngFile = new File(parent, basename + ".png");
			if (!pngFile.exists()) throw new IOException("Cannot find PNG counterpart.");
		} else if (basename.toLowerCase().endsWith(".png")) {
			basename = basename.substring(0, basename.length() - 4);
			pngFile = inFile;
			frnxFile = new File(parent, basename + ".frnx");
			if (!frnxFile.exists()) throw new IOException("Cannot find FRNX counterpart.");
		} else {
			throw new IOException("Unknown file type.");
		}
		// Determine output files.
		File iiowFile, injFile;
		if (outDir == null) {
			iiowFile = new File(parent, basename + "-iioinject.png");
			injFile = new File(parent, basename + "-inject.png");
		} else if (makeIIOW && makeInj) {
			iiowFile = new File(outDir, basename + "-iioinject.png");
			injFile = new File(outDir, basename + "-inject.png");
		} else {
			iiowFile = new File(outDir, basename + ".png");
			injFile = new File(outDir, basename + ".png");
		}
		// Read PNG and FRNX files.
		String frnxName = basename.replaceFirst("^#[0-9]+ ", "").trim();
		BufferedImage image = ImageIO.read(pngFile);
		InputStream frnxIn = new FileInputStream(frnxFile);
		Frame frame = FRNXParser.parse(frnxName, frnxIn, image);
		frnxIn.close();
		// Write output files.
		if (makeInj) {
			DataInputStream injIn = new DataInputStream(new FileInputStream(pngFile));
			DataOutputStream injOut = new DataOutputStream(new FileOutputStream(injFile));
			FrameWriter.injectFRNF(injIn, injOut, frame);
			injOut.flush();
			injOut.close();
			injIn.close();
		}
		if (makeIIOW) {
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
		}
	}
}
