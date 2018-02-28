package com.kreative.paint.material.colorpalette;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PaletteConverter {
	public static void main(String[] args) {
		if (args.length == 0) {
			printHelp();
		} else {
			boolean processingOptions = true;
			Options o = new Options();
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (processingOptions && arg.startsWith("-")) {
					if (arg.equals("--")) {
						processingOptions = false;
					} else if (arg.equals("-pn")) {
						o.usePaletteName = true;
					} else if (arg.equals("-fn")) {
						o.usePaletteName = false;
					} else if (arg.equals("-o") && argi < args.length) {
						o.dest = new File(args[argi++]);
					} else if (arg.equals("-f") && argi < args.length) {
						String fmt = args[argi++];
						if (fmt.equalsIgnoreCase("auto")) o.outFormat = null;
						else try { o.outFormat = Format.valueOf(fmt.toUpperCase()); }
						catch (IllegalArgumentException e) {
							System.err.println("Unknown format: " + fmt.toLowerCase());
							return;
						}
					} else if (arg.equals("-if") && argi < args.length) {
						String fmt = args[argi++];
						if (fmt.equalsIgnoreCase("auto")) o.inFormat = null;
						else try { o.inFormat = Format.valueOf(fmt.toUpperCase()); }
						catch (IllegalArgumentException e) {
							System.err.println("Unknown format: " + fmt.toLowerCase());
							return;
						}
					} else if (arg.equals("--help")) {
						printHelp();
						return;
					} else {
						System.err.println("Unknown option: " + arg);
						return;
					}
				} else {
					try {
						System.out.print(arg + "...");
						process(o, new File(arg));
						System.out.println(" DONE");
					} catch (IOException e) {
						System.out.println(" FAILED: " + e.getClass().getSimpleName() + ": " + e.getMessage());
					}
				}
			}
		}
	}
	
	private static void printHelp() {
		System.out.println();
		System.out.println("Usage:");
		System.out.println("  java com.kreative.paint.material.colorpalette.PaletteConverter <options> <files>");
		System.out.println();
		System.out.println("Options:");
		System.out.println("  -pn           Use palette name as output file name.");
		System.out.println("  -fn           Use input file name as output file name.");
		System.out.println("  -o <path>     Write output to the specified file or directory.");
		System.out.println("  -f <format>   Set the output format.");
		System.out.println("  -if <format>  Set the input format.");
		System.out.println("  --            Process remaining arguments as file names.");
		System.out.println();
		System.out.println("Formats:");
		for (Format fmt : Format.values()) System.out.println("  " + fmt.description);
		System.out.println();
	}
	
	private static class Options {
		public Format inFormat = null;
		public Format outFormat = null;
		public File dest = null;
		public boolean usePaletteName = false;
	}
	
	private static void process(Options o, File in) throws IOException {
		String name = in.getName();
		int dot = name.lastIndexOf('.');
		String ext = (dot > 0) ? name.substring(dot + 1) : "";
		if (dot > 0) name = name.substring(0, dot);
		Format inFmt = o.inFormat;
		if (inFmt == null) {
			try {
				inFmt = Format.valueOf(ext.toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new IOException("Unknown format: " + ext.toLowerCase());
			}
		}
		FileInputStream fis = new FileInputStream(in);
		RCPXPalette pal = inFmt.reader.read(name, fis);
		fis.close();
		
		if (o.usePaletteName) {
			if (pal.name != null && pal.name.length() > 0) {
				name = pal.name;
			}
		}
		
		File out = o.dest;
		Format outFmt = o.outFormat;
		if (out == null) {
			if (outFmt == null) outFmt = inFmt;
			name = name + "." + outFmt.name().toLowerCase();
			out = new File(in.getParentFile(), name);
			if (out.equals(in)) {
				name = in.getName() + "." + outFmt.name().toLowerCase();
				out = new File(in.getParentFile(), name);
			}
		} else if (out.isDirectory()) {
			if (outFmt == null) outFmt = inFmt;
			name = name + "." + outFmt.name().toLowerCase();
			out = new File(out, name);
		} else {
			if (outFmt == null) {
				name = out.getName();
				dot = name.lastIndexOf('.');
				ext = (dot > 0) ? name.substring(dot + 1) : "";
				try {
					outFmt = Format.valueOf(ext.toUpperCase());
				} catch (IllegalArgumentException e) {
					throw new IOException("Unknown format: " + ext.toLowerCase());
				}
			}
			o.dest = null;
		}
		if (outFmt.writer.isCompatible(pal)) {
			FileOutputStream fos = new FileOutputStream(out);
			outFmt.writer.write(pal, fos);
			fos.close();
		} else {
			throw new IOException("Incompatible format: " + outFmt.name().toLowerCase());
		}
	}
	
	private static enum Format {
		RCPX("RCPX (PowerPaint)", new PaletteReader.RCPXReader(), new PaletteWriter.RCPXWriter()),
		ACT("ACT (Photoshop)", new PaletteReader.ACTReader(), new PaletteWriter.ACTWriter()),
		ACO("ACO (Photoshop)", new PaletteReader.ACOReader(), new PaletteWriter.ACOWriter()),
		ASE("ASE (Illustrator)", new PaletteReader.ASEReader(), new PaletteWriter.ASEWriter()),
		ACB("ACB (Adobe Color Book)", new PaletteReader.ACBReader(), new PaletteWriter.ACBWriter()),
		GPL("GPL (GIMP)", new PaletteReader.GPLReader(), new PaletteWriter.GPLWriter()),
		PAL("PAL (PaintShop Pro)", new PaletteReader.PALReader(), new PaletteWriter.PALWriter()),
		CLR("CLR (Mac OS X)", new PaletteReader.CLRReader(), new PaletteWriter.CLRWriter()),
		CLUT("CLUT (Mac OS Classic)", new PaletteReader.CLUTReader(), new PaletteWriter.CLUTWriter()),
		PLTT("PLTT (Mac OS Classic)", new PaletteReader.PLTTReader(), new PaletteWriter.PLTTWriter());
		
		public final String description;
		public final PaletteReader reader;
		public final PaletteWriter writer;
		
		private Format(String description, PaletteReader reader, PaletteWriter writer) {
			this.description = description;
			this.reader = reader;
			this.writer = writer;
		}
	}
}
