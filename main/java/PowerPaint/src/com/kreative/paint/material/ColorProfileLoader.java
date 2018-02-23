package com.kreative.paint.material;

import java.awt.color.ICC_Profile;
import java.io.IOException;
import java.io.InputStream;
import com.kreative.paint.util.CIEColorModel;
import com.kreative.paint.util.ColorModel;
import com.kreative.paint.util.ICCColorModel;

public class ColorProfileLoader {
	private final MaterialLoader loader;
	private final MaterialList<ICC_Profile> profiles;
	private final MaterialList<ColorModel> models;
	
	public ColorProfileLoader(MaterialLoader loader) {
		this.loader = loader;
		this.profiles = new MaterialList<ICC_Profile>();
		this.models = new MaterialList<ColorModel>();
	}
	
	public MaterialList<ICC_Profile> getColorProfiles() {
		if (profiles.isEmpty() && models.isEmpty()) loadResources();
		return profiles;
	}
	
	public MaterialList<ColorModel> getColorModels() {
		if (profiles.isEmpty() && models.isEmpty()) loadResources();
		return models;
	}
	
	private void loadResources() {
		models.add(ColorModel.GRAY_4.getName(), ColorModel.GRAY_4);
		models.add(ColorModel.GRAY_8.getName(), ColorModel.GRAY_8);
		models.add(ColorModel.GRAY_16.getName(), ColorModel.GRAY_16);
		models.add(ColorModel.GRAY_100.getName(), ColorModel.GRAY_100);
		models.add(ColorModel.RGB_4.getName(), ColorModel.RGB_4);
		models.add(ColorModel.RGB_8.getName(), ColorModel.RGB_8);
		models.add(ColorModel.RGB_16.getName(), ColorModel.RGB_16);
		models.add(ColorModel.RGB_100.getName(), ColorModel.RGB_100);
		models.add(ColorModel.HSV_360_100.getName(), ColorModel.HSV_360_100);
		models.add(ColorModel.HSL_360_100.getName(), ColorModel.HSL_360_100);
		models.add(ColorModel.HWB_360_100.getName(), ColorModel.HWB_360_100);
		models.add(ColorModel.NAIVE_CMYK_100.getName(), ColorModel.NAIVE_CMYK_100);
		
		for (MaterialResource r : loader.listResources()) {
			if (r.isFormat("icc", false)) {
				try {
					InputStream in = r.getInputStream();
					ICC_Profile profile = ICC_Profile.getInstance(in);
					in.close();
					String name = r.getResourceName();
					profiles.add(name, profile);
					models.add(name, new ICCColorModel(name, profile));
				} catch (IOException e) {
					System.err.println("Warning: Failed to load color profile " + r.getResourceName() + ".");
					e.printStackTrace();
				}
			}
		}
		
		models.add(ColorModel.YUV_SDTV.getName(), ColorModel.YUV_SDTV);
		models.add(ColorModel.YUV_HDTV.getName(), ColorModel.YUV_HDTV);
		models.add(ColorModel.Y_CB_CR_SDTV.getName(), ColorModel.Y_CB_CR_SDTV);
		models.add(ColorModel.Y_CB_CR_HDTV.getName(), ColorModel.Y_CB_CR_HDTV);
		models.add(ColorModel.Y_DB_DR.getName(), ColorModel.Y_DB_DR);
		models.add(ColorModel.YIQ.getName(), ColorModel.YIQ);
		models.add(ColorModel.Y_CG_CO.getName(), ColorModel.Y_CG_CO);
		models.add(CIEColorModel.CIE_XYZ_100.getName(), CIEColorModel.CIE_XYZ_100);
		models.add(CIEColorModel.CIE_xyY_100.getName(), CIEColorModel.CIE_xyY_100);
		models.add(CIEColorModel.CIE_RGB_100.getName(), CIEColorModel.CIE_RGB_100);
		models.add(CIEColorModel.CIE_Lab_D65.getName(), CIEColorModel.CIE_Lab_D65);
		models.add(CIEColorModel.CIE_LCh_D65.getName(), CIEColorModel.CIE_LCh_D65);
		models.add(CIEColorModel.Hunter_Lab_D65.getName(), CIEColorModel.Hunter_Lab_D65);
		models.add(CIEColorModel.Hunter_LCh_D65.getName(), CIEColorModel.Hunter_LCh_D65);
	}
}
