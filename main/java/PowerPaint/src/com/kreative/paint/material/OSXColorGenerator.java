package com.kreative.paint.material;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import com.kreative.paint.material.colorpalette.PaletteWriter;
import com.kreative.paint.material.colorpalette.RCPXPalette;
import com.kreative.paint.material.colorpalette.RCPXParser;

public class OSXColorGenerator {
	public static void main(String[] args) {
		PaletteWriter pw = new PaletteWriter.CLRWriter(false);
		File colors = new File("colors"); colors.mkdir();
		MaterialLocator locator = new MaterialLocator("Kreative", "PowerPaint");
		MaterialLoader loader = locator.getMaterialLoader();
		for (MaterialResource r : loader.listResources()) {
			if (r.isFormat("rcpx", false)) {
				try {
					InputStream in = r.getInputStream();
					RCPXPalette rcpx = RCPXParser.parse(r.getResourceName(), in);
					in.close();
					if (pw.isCompatible(rcpx)) {
						File f = new File(colors, rcpx.name + ".clr");
						FileOutputStream out = new FileOutputStream(f);
						pw.write(rcpx, out);
						out.flush();
						out.close();
					}
				} catch (IOException e) {
					System.err.println("Warning: Failed to compile color palette " + r.getResourceName() + ".");
					e.printStackTrace();
				}
			}
		}
	}
}
