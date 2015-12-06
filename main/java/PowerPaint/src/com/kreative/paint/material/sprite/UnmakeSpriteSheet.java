package com.kreative.paint.material.sprite;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

public class UnmakeSpriteSheet {
	public static void main(String[] args) {
		if (args.length == 0) {
			printHelp();
		} else {
			File outDir = null;
			SpriteSheetReader.Options o = new SpriteSheetReader.Options();
			boolean makeSPNX = false;
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
					} else if (arg.equals("-S")) {
						makeSPNX = true;
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
						processFile(new File(arg), outDir, o, makeSPNX, makeIIOW, makeStrip);
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
		System.out.println("UnmakeSpriteSheet - Extract sprite info from PNG.");
		System.out.println();
		System.out.println("Options:");
		System.out.println("  -D <path>  Set output directory.");
		System.out.println("  -S         Output SPNX file.");
		System.out.println("  -I         Output PNG file rewritten by ImageIO.");
		System.out.println("  -P         Output PNG file without spNF chunk.");
		System.out.println("  --         Treat remaining arguments as file names.");
		System.out.println();
		System.out.println("If none of -S, -I, -P are specified, all are implied.");
		System.out.println();
	}
	
	private static void processFile(
		File inFile,
		File outDir,
		SpriteSheetReader.Options o,
		boolean makeSPNX,
		boolean makeIIOW,
		boolean makeStrip
	) throws IOException {
		if (!(makeSPNX || makeIIOW || makeStrip)) {
			makeSPNX = makeIIOW = makeStrip = true;
		}
		File frnfFile, iiowFile, stripFile;
		if (outDir == null) {
			File parent = inFile.getParentFile();
			String basename = inFile.getName().replaceFirst("\\.[a-zA-Z0-9]+$", "");
			frnfFile = new File(parent, basename + "-spriteinfo.spnx");
			iiowFile = new File(parent, basename + "-iiowrite.png");
			stripFile = new File(parent, basename + "-stripped.png");
		} else if (makeIIOW && makeStrip) {
			String basename = inFile.getName().replaceFirst("\\.[a-zA-Z0-9]+$", "");
			frnfFile = new File(outDir, basename + "-spriteinfo.spnx");
			iiowFile = new File(outDir, basename + "-iiowrite.png");
			stripFile = new File(outDir, basename + "-stripped.png");
		} else {
			String basename = inFile.getName().replaceFirst("\\.[a-zA-Z0-9]+$", "");
			frnfFile = new File(outDir, basename + ".spnx");
			iiowFile = new File(outDir, basename + ".png");
			stripFile = new File(outDir, basename + ".png");
		}
		SpriteSheet sheet = SpriteSheetReader.readSpriteSheet(inFile, o);
		if (makeSPNX) {
			PrintWriter finfOut = new PrintWriter(new OutputStreamWriter(new FileOutputStream(frnfFile), "UTF-8"), true);
			SpriteSheetWriter.printSPNX(finfOut, sheet);
			finfOut.flush();
			finfOut.close();
		}
		if (makeIIOW) {
			ImageIO.write(sheet.image, "png", iiowFile);
		}
		if (makeStrip) {
			DataInputStream stripIn = new DataInputStream(new FileInputStream(inFile));
			DataOutputStream stripOut = new DataOutputStream(new FileOutputStream(stripFile));
			SpriteSheetWriter.stripSPNF(stripIn, stripOut);
			stripOut.flush();
			stripOut.close();
			stripIn.close();
		}
	}
}
