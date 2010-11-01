/*
 * Copyright &copy; 2010 Rebecca G. Bettencourt / Kreative Software
 * <p>
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <a href="http://www.mozilla.org/MPL/">http://www.mozilla.org/MPL/</a>
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Alternatively, the contents of this file may be used under the terms
 * of the GNU Lesser General Public License (the "LGPL License"), in which
 * case the provisions of LGPL License are applicable instead of those
 * above. If you wish to allow use of your version of this file only
 * under the terms of the LGPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the LGPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the LGPL License.
 * @since PowerPaint 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.paint;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Vector;
import com.kreative.paint.geom.ParameterizedPath;
import com.kreative.paint.powerbrush.BrushSettings;
import com.kreative.paint.powerbrush.BrushShape;
import com.kreative.paint.res.MaterialsManager;
import com.kreative.paint.tool.Tool;
import com.kreative.paint.util.Bitmap;
import com.kreative.paint.util.CursorUtils;
import com.kreative.paint.util.Frame;
import com.kreative.paint.util.ImageUtils;
import com.kreative.paint.util.PairList;

public class ToolContext implements ToolContextConstants {
	// tool
	private boolean drawMode;
	private Tool tool;
	// general draw options
	private boolean drawPerpendicular;
	private boolean drawSquare;
	private boolean drawFromCenter;
	private boolean drawFilled;
	private boolean drawMultiple;
	// corner radius
	private float cornerRadiusX;
	private float cornerRadiusY;
	// QuickShadow
	private int shadowType;
	private int shadowOpacity;
	private int shadowXOffset;
	private int shadowYOffset;
	// PowerBrush
	private BrushSettings powerBrushSettings;
	private BrushShape powerBrushShape;
	private float powerBrushOuterWidth;
	private float powerBrushOuterHeight;
	private float powerBrushInnerWidth;
	private float powerBrushInnerHeight;
	private int powerBrushFlowRate;
	// curl
	private double curlRadius;
	private double curlSpacing;
	// polygon
	private int polygonSides;
	private int polygonStellation;
	// alphabets
	private PairList<String,char[]> alphabets;
	private int alphabetIndex;
	private char[] alphabet;
	private int letterIndex;
	private char letter;
	private Font letterFont;
	private Font letterPUAFont;
	private BufferedImage letterImage;
	private Cursor letterCursor;
	// brushes
	private PairList<String,Vector<Bitmap>> brushSets;
	private int brushSetIndex;
	private Vector<Bitmap> brushes;
	private int brushIndex;
	private Bitmap brush;
	private int[] brushRGB;
	private Cursor brushCursor;
	// calligraphy brushes
	private PairList<String,Vector<Bitmap>> calligraphyBrushes;
	private int calligraphyBrushIndex;
	private Vector<Bitmap> calligraphyBrush;
	private boolean calligraphyContinuous;
	// charcoal brushes
	private PairList<String,Vector<Bitmap>> charcoalBrushes;
	private int charcoalBrushIndex;
	private Vector<Bitmap> charcoalBrush;
	// frames
	private PairList<String,Frame> frames;
	private int frameIndex;
	private Frame frame;
	// rubber stamps
	private PairList<String,Vector<Image>> rubberStampSets;
	private int rubberStampSetIndex;
	private Vector<Image> rubberStamps;
	private int rubberStampIndex;
	private Image rubberStamp;
	private Cursor rubberStampCursor;
	// PowerShapes
	private PairList<String,PairList<String,ParameterizedPath>> powerShapeSets;
	private int powerShapeSetIndex;
	private PairList<String,ParameterizedPath> powerShapes;
	private int powerShapeIndex;
	private ParameterizedPath powerShape;
	// sprinkles
	private PairList<String,Vector<Bitmap>> sprinkleSets;
	private int sprinkleSetIndex;
	private Vector<Bitmap> sprinkles;
	private int sprinkleIndex;
	private Bitmap sprinkle;
	private boolean sprinkleBrushMode;
	// custom
	private HashMap<String,Object> custom;
	// listeners
	private Random random;
	private HashSet<ToolContextListener> listeners;
	
	public ToolContext(MaterialsManager mm) {
		// tool
		this.drawMode = false;
		this.tool = null;
		// general draw options
		this.drawPerpendicular = false;
		this.drawSquare = false;
		this.drawFromCenter = false;
		this.drawFilled = false;
		this.drawMultiple = false;
		// corner radius
		this.cornerRadiusX = 16f;
		this.cornerRadiusY = 16f;
		// QuickShadow
		this.shadowType = 0;
		this.shadowOpacity = 255;
		this.shadowXOffset = 5;
		this.shadowYOffset = 5;
		// PowerBrush
		this.powerBrushSettings = new BrushSettings();
		this.powerBrushShape = this.powerBrushSettings.getBrushShape();
		this.powerBrushOuterWidth = this.powerBrushSettings.getOuterWidth();
		this.powerBrushOuterHeight = this.powerBrushSettings.getOuterHeight();
		this.powerBrushInnerWidth = this.powerBrushSettings.getInnerWidth();
		this.powerBrushInnerHeight = this.powerBrushSettings.getInnerHeight();
		this.powerBrushFlowRate = this.powerBrushSettings.getFlowRate();
		// curl
		this.curlRadius = 10.0;
		this.curlSpacing = 20.0;
		// polygon
		this.polygonSides = 6;
		this.polygonStellation = 1;
		// alphabets
		this.alphabets = mm.getAlphabets();
		this.alphabetIndex = 0;
		this.alphabet = this.alphabets.getLatter(0);
		this.letterIndex = 0;
		this.letter = this.alphabet[0];
		this.letterFont = new Font("Helvetica", Font.BOLD, 36);
		this.letterPUAFont = new Font("Constructium", Font.PLAIN, 36);
		setLetterImageAndCursor();
		// brushes
		this.brushSets = mm.getBrushes();
		this.brushSetIndex = 0;
		this.brushes = this.brushSets.getLatter(0);
		this.brushIndex = 0;
		this.brush = this.brushes.get(0);
		this.brushRGB = this.brush.getRGB();
		this.brushCursor = this.brush.getCursor();
		// calligraphy brushes
		this.calligraphyBrushes = mm.getCalligraphyBrushes();
		this.calligraphyBrushIndex = 0;
		this.calligraphyBrush = this.calligraphyBrushes.getLatter(0);
		this.calligraphyContinuous = false;
		// charcoal brushes
		this.charcoalBrushes = mm.getCharcoalBrushes();
		this.charcoalBrushIndex = 0;
		this.charcoalBrush = this.charcoalBrushes.getLatter(0);
		// frames
		this.frames = mm.getFrames();
		this.frameIndex = 0;
		this.frame = frames.getLatter(0);
		// rubber stamps
		this.rubberStampSets = mm.getRubberStamps();
		this.rubberStampSetIndex = 0;
		this.rubberStamps = this.rubberStampSets.getLatter(0);
		this.rubberStampIndex = 0;
		this.rubberStamp = this.rubberStamps.get(0);
		BufferedImage rsi = ImageUtils.toBufferedImage(this.rubberStamp, false);
		this.rubberStampCursor = CursorUtils.makeCursor(rsi, rsi.getWidth()/2, rsi.getHeight()/2, "Stamp");
		// PowerShapes
		this.powerShapeSets = mm.getShapes();
		this.powerShapeSetIndex = 0;
		this.powerShapes = this.powerShapeSets.getLatter(0);
		this.powerShapeIndex = 0;
		this.powerShape = this.powerShapes.getLatter(0);
		// sprinkles
		this.sprinkleSets = mm.getSprinkles();
		this.sprinkleSetIndex = 0;
		this.sprinkles = this.sprinkleSets.getLatter(0);
		this.sprinkleIndex = 0;
		this.sprinkle = this.sprinkles.get(0);
		this.sprinkleBrushMode = false;
		// custom
		this.custom = new HashMap<String,Object>();
		// listeners
		this.random = new Random();
		this.listeners = new HashSet<ToolContextListener>();
	}
	
	// tool

	public boolean isInPaintMode() {
		return !drawMode;
	}
	
	public boolean isInDrawMode() {
		return drawMode;
	}
	
	public Tool getTool() {
		return tool;
	}

	public void setPaintMode(boolean paintMode) {
		this.drawMode = !paintMode;
		for (ToolContextListener l : listeners) {
			l.modeChanged(this, !paintMode);
		}
	}
	
	public void setDrawMode(boolean drawMode) {
		this.drawMode = drawMode;
		for (ToolContextListener l : listeners) {
			l.modeChanged(this, drawMode);
		}
	}
	
	public void setTool(Tool tool) {
		Tool prevtool = this.tool;
		this.tool = tool;
		for (ToolContextListener l : listeners) {
			l.toolChanged(this, prevtool, tool);
		}
	}
	
	public void doubleClickTool(Tool tool) {
		for (ToolContextListener l : listeners) {
			l.toolDoubleClicked(this, tool);
		}
	}
	
	// general draw options
	
	public boolean drawPerpendicular() {
		return drawPerpendicular;
	}

	public boolean drawSquare() {
		return drawSquare;
	}

	public boolean drawFromCenter() {
		return drawFromCenter;
	}

	public boolean drawFilled() {
		return drawFilled;
	}

	public boolean drawMultiple() {
		return drawMultiple;
	}
	
	public void setDrawPerpendicular(boolean drawPerpendicular) {
		this.drawPerpendicular = drawPerpendicular;
		notifyToolContextListeners(CHANGED_DRAW_PERPENDICULAR);
	}

	public void setDrawSquare(boolean drawSquare) {
		this.drawSquare = drawSquare;
		notifyToolContextListeners(CHANGED_DRAW_SQUARE);
	}

	public void setDrawFromCenter(boolean drawFromCenter) {
		this.drawFromCenter = drawFromCenter;
		notifyToolContextListeners(CHANGED_DRAW_CENTER);
	}

	public void setDrawFilled(boolean drawFilled) {
		this.drawFilled = drawFilled;
		notifyToolContextListeners(CHANGED_DRAW_FILLED);
	}

	public void setDrawMultiple(boolean drawMultiple) {
		this.drawMultiple = drawMultiple;
		notifyToolContextListeners(CHANGED_DRAW_MULTIPLE);
	}
	
	// corner radius

	public float getCornerRadiusX() {
		return cornerRadiusX;
	}

	public float getCornerRadiusY() {
		return cornerRadiusY;
	}

	public void setCornerRadiusX(float cornerRadiusX) {
		this.cornerRadiusX = cornerRadiusX;
		notifyToolContextListeners(CHANGED_CORNER_RADIUS_X);
	}

	public void setCornerRadiusY(float cornerRadiusY) {
		this.cornerRadiusY = cornerRadiusY;
		notifyToolContextListeners(CHANGED_CORNER_RADIUS_Y);
	}
	
	// QuickShadow
	
	public boolean useShadow() {
		return (shadowType != 0 && shadowOpacity != 0 && (shadowXOffset != 0 || shadowYOffset != 0));
	}

	public int getShadowType() {
		return shadowType;
	}

	public int getShadowOpacity() {
		return shadowOpacity;
	}

	public int getShadowXOffset() {
		return shadowXOffset;
	}

	public int getShadowYOffset() {
		return shadowYOffset;
	}

	public void setShadowType(int shadowType) {
		this.shadowType = shadowType;
		notifyToolContextListeners(CHANGED_QUICKSHADOW_TYPE);
	}

	public void setShadowOpacity(int shadowOpacity) {
		this.shadowOpacity = shadowOpacity;
		notifyToolContextListeners(CHANGED_QUICKSHADOW_OPACITY);
	}

	public void setShadowXOffset(int shadowXOffset) {
		this.shadowXOffset = shadowXOffset;
		notifyToolContextListeners(CHANGED_QUICKSHADOW_X);
	}

	public void setShadowYOffset(int shadowYOffset) {
		this.shadowYOffset = shadowYOffset;
		notifyToolContextListeners(CHANGED_QUICKSHADOW_Y);
	}
	
	// PowerBrush
	
	public BrushSettings getPowerBrushSettings() {
		return powerBrushSettings;
	}

	public BrushShape getPowerBrushShape() {
		return powerBrushShape;
	}

	public float getPowerBrushOuterWidth() {
		return powerBrushOuterWidth;
	}

	public float getPowerBrushOuterHeight() {
		return powerBrushOuterHeight;
	}

	public float getPowerBrushInnerWidth() {
		return powerBrushInnerWidth;
	}

	public float getPowerBrushInnerHeight() {
		return powerBrushInnerHeight;
	}
	
	public int getPowerBrushFlowRate() {
		return powerBrushFlowRate;
	}
	
	public void setPowerBrushSettings(BrushSettings powerBrushSettings) {
		this.powerBrushSettings = powerBrushSettings;
		this.powerBrushShape = powerBrushSettings.getBrushShape();
		this.powerBrushOuterWidth = powerBrushSettings.getOuterWidth();
		this.powerBrushOuterHeight = powerBrushSettings.getOuterHeight();
		this.powerBrushInnerWidth = powerBrushSettings.getInnerWidth();
		this.powerBrushInnerHeight = powerBrushSettings.getInnerHeight();
		this.powerBrushFlowRate = powerBrushSettings.getFlowRate();
		notifyToolContextListeners(CHANGED_POWERBRUSH_SHAPE | CHANGED_POWERBRUSH_OUTER_SIZE | CHANGED_POWERBRUSH_INNER_SIZE | CHANGED_POWERBRUSH_FLOW_RATE);
	}

	public void setPowerBrushShape(BrushShape powerBrushShape) {
		this.powerBrushSettings = new BrushSettings(powerBrushShape, powerBrushOuterWidth, powerBrushOuterHeight, powerBrushInnerWidth, powerBrushInnerHeight, powerBrushFlowRate);
		this.powerBrushShape = powerBrushShape;
		notifyToolContextListeners(CHANGED_POWERBRUSH_SHAPE);
	}

	public void setPowerBrushOuterWidth(float powerBrushOuterWidth) {
		this.powerBrushSettings = new BrushSettings(powerBrushShape, powerBrushOuterWidth, powerBrushOuterHeight, powerBrushInnerWidth, powerBrushInnerHeight, powerBrushFlowRate);
		this.powerBrushOuterWidth = powerBrushOuterWidth;
		notifyToolContextListeners(CHANGED_POWERBRUSH_OUTER_SIZE);
	}

	public void setPowerBrushOuterHeight(float powerBrushOuterHeight) {
		this.powerBrushSettings = new BrushSettings(powerBrushShape, powerBrushOuterWidth, powerBrushOuterHeight, powerBrushInnerWidth, powerBrushInnerHeight, powerBrushFlowRate);
		this.powerBrushOuterHeight = powerBrushOuterHeight;
		notifyToolContextListeners(CHANGED_POWERBRUSH_OUTER_SIZE);
	}

	public void setPowerBrushInnerWidth(float powerBrushInnerWidth) {
		this.powerBrushSettings = new BrushSettings(powerBrushShape, powerBrushOuterWidth, powerBrushOuterHeight, powerBrushInnerWidth, powerBrushInnerHeight, powerBrushFlowRate);
		this.powerBrushInnerWidth = powerBrushInnerWidth;
		notifyToolContextListeners(CHANGED_POWERBRUSH_INNER_SIZE);
	}

	public void setPowerBrushInnerHeight(float powerBrushInnerHeight) {
		this.powerBrushSettings = new BrushSettings(powerBrushShape, powerBrushOuterWidth, powerBrushOuterHeight, powerBrushInnerWidth, powerBrushInnerHeight, powerBrushFlowRate);
		this.powerBrushInnerHeight = powerBrushInnerHeight;
		notifyToolContextListeners(CHANGED_POWERBRUSH_INNER_SIZE);
	}
	
	public void setPowerBrushFlowRate(int powerBrushFlowRate) {
		this.powerBrushSettings = new BrushSettings(powerBrushShape, powerBrushOuterWidth, powerBrushOuterHeight, powerBrushInnerWidth, powerBrushInnerHeight, powerBrushFlowRate);
		this.powerBrushFlowRate = powerBrushFlowRate;
		notifyToolContextListeners(CHANGED_POWERBRUSH_FLOW_RATE);
	}
	
	public void decrementPowerBrush() {
		if (powerBrushOuterWidth >= 2.0 && powerBrushOuterHeight >= 2.0 && powerBrushInnerWidth >= 1.0 && powerBrushInnerHeight >= 1.0) {
			powerBrushOuterWidth--;
			powerBrushOuterHeight--;
			powerBrushInnerWidth--;
			powerBrushInnerHeight--;
			powerBrushSettings = new BrushSettings(powerBrushShape, powerBrushOuterWidth, powerBrushOuterHeight, powerBrushInnerWidth, powerBrushInnerHeight, powerBrushFlowRate);
			notifyToolContextListeners(CHANGED_POWERBRUSH_OUTER_SIZE | CHANGED_POWERBRUSH_INNER_SIZE);
		}
	}
	
	public void incrementPowerBrush() {
		powerBrushOuterWidth++;
		powerBrushOuterHeight++;
		powerBrushInnerWidth++;
		powerBrushInnerHeight++;
		powerBrushSettings = new BrushSettings(powerBrushShape, powerBrushOuterWidth, powerBrushOuterHeight, powerBrushInnerWidth, powerBrushInnerHeight, powerBrushFlowRate);
		notifyToolContextListeners(CHANGED_POWERBRUSH_OUTER_SIZE | CHANGED_POWERBRUSH_INNER_SIZE);
	}
	
	public void decrementPowerBrushOuterOnly() {
		if (powerBrushOuterWidth >= 2.0 && powerBrushOuterHeight >= 2.0) {
			powerBrushOuterWidth--;
			powerBrushOuterHeight--;
			powerBrushInnerWidth = powerBrushOuterWidth;
			powerBrushInnerHeight = powerBrushOuterHeight;
			powerBrushSettings = new BrushSettings(powerBrushShape, powerBrushOuterWidth, powerBrushOuterHeight, powerBrushInnerWidth, powerBrushInnerHeight, powerBrushFlowRate);
			notifyToolContextListeners(CHANGED_POWERBRUSH_OUTER_SIZE | CHANGED_POWERBRUSH_INNER_SIZE);
		}
	}
	
	public void incrementPowerBrushOuterOnly() {
		powerBrushOuterWidth++;
		powerBrushOuterHeight++;
		powerBrushInnerWidth = powerBrushOuterWidth;
		powerBrushInnerHeight = powerBrushOuterHeight;
		powerBrushSettings = new BrushSettings(powerBrushShape, powerBrushOuterWidth, powerBrushOuterHeight, powerBrushInnerWidth, powerBrushInnerHeight, powerBrushFlowRate);
		notifyToolContextListeners(CHANGED_POWERBRUSH_OUTER_SIZE | CHANGED_POWERBRUSH_INNER_SIZE);
	}
	
	// curl

	public double getCurlRadius() {
		return Math.abs(curlRadius);
	}

	public double getCurlSpacing() {
		return Math.abs(curlSpacing);
	}
	
	public boolean getCurlCCW() {
		return (curlRadius < 0) != (curlSpacing < 0);
	}
	
	public double getRawCurlRadius() {
		return curlRadius;
	}
	
	public double getRawCurlSpacing() {
		return curlSpacing;
	}
	
	public void setCurlRadius(double curlRadius) {
		this.curlRadius = (this.curlRadius < 0) ? -Math.abs(curlRadius) : Math.abs(curlRadius);
		notifyToolContextListeners(CHANGED_CURL_RADIUS);
	}
	
	public void decrementCurlRadius() {
		if (curlRadius <= -2) curlRadius++;
		else if (curlRadius >= 2) curlRadius--;
		notifyToolContextListeners(CHANGED_CURL_RADIUS);
	}
	
	public void incrementCurlRadius() {
		if (curlRadius < 0) curlRadius--;
		else if (curlRadius > 0) curlRadius++;
		notifyToolContextListeners(CHANGED_CURL_RADIUS);
	}
	
	public void setCurlSpacing(double curlSpacing) {
		this.curlSpacing = (this.curlSpacing < 0) ? -Math.abs(curlSpacing) : Math.abs(curlSpacing);
		notifyToolContextListeners(CHANGED_CURL_SPACING);
	}
	
	public void decrementCurlSpacing() {
		if (curlSpacing <= -2) curlSpacing++;
		else if (curlSpacing >= 2) curlSpacing--;
		notifyToolContextListeners(CHANGED_CURL_SPACING);
	}
	
	public void incrementCurlSpacing() {
		if (curlSpacing < 0) curlSpacing--;
		else if (curlSpacing > 0) curlSpacing++;
		notifyToolContextListeners(CHANGED_CURL_SPACING);
	}
	
	public void setCurlCCW(boolean curlCCW) {
		this.curlRadius = Math.abs(this.curlRadius);
		this.curlSpacing = curlCCW ? -Math.abs(this.curlSpacing) : Math.abs(this.curlSpacing);
		notifyToolContextListeners(CHANGED_CURL_RADIUS | CHANGED_CURL_SPACING);
	}
	
	public void toggleCurlCCW() {
		curlSpacing =- curlSpacing;
		notifyToolContextListeners(CHANGED_CURL_RADIUS | CHANGED_CURL_SPACING);
	}
	
	// polygon
	
	public int getPolygonSides() {
		return polygonSides;
	}
	
	public int getPolygonStellation() {
		return polygonStellation;
	}
	
	public void setPolygonSides(int polygonSides) {
		this.polygonSides = (polygonSides < 3) ? 3 : polygonSides;
		this.polygonStellation = (polygonStellation < 1) ? 1 : (polygonStellation > ((polygonSides-1)/2)) ? ((polygonSides-1)/2) : polygonStellation;
		notifyToolContextListeners(CHANGED_POLYGON_SIDES | CHANGED_POLYGON_STELLATION);
	}
	
	public void decrementPolygonSides() {
		if (polygonSides > 3) {
			polygonSides--;
			if (polygonStellation > (polygonSides-1)/2) {
				polygonStellation = (polygonSides-1)/2;
			}
			notifyToolContextListeners(CHANGED_POLYGON_SIDES | CHANGED_POLYGON_STELLATION);
		}
	}
	
	public void incrementPolygonSides() {
		polygonSides++;
		notifyToolContextListeners(CHANGED_POLYGON_SIDES);
	}
	
	public void setPolygonStellation(int polygonStellation) {
		this.polygonStellation = (polygonStellation < 1) ? 1 : (polygonStellation > ((polygonSides-1)/2)) ? ((polygonSides-1)/2) : polygonStellation;
		notifyToolContextListeners(CHANGED_POLYGON_STELLATION);
	}
	
	public void decrementPolygonStellation() {
		if (polygonStellation > 1) {
			polygonStellation--;
			notifyToolContextListeners(CHANGED_POLYGON_STELLATION);
		}
	}
	
	public void incrementPolygonStellation() {
		if (polygonStellation < (polygonSides-1)/2) {
			polygonStellation++;
			notifyToolContextListeners(CHANGED_POLYGON_STELLATION);
		}
	}
	
	// alphabets

	public PairList<String, char[]> getAlphabets() {
		return alphabets;
	}
	
	public String getAlphabetName() {
		return alphabets.getFormer(alphabetIndex);
	}

	public int getAlphabetIndex() {
		return alphabetIndex;
	}

	public char[] getAlphabet() {
		return alphabet;
	}

	public int getLetterIndex() {
		return letterIndex;
	}

	public char getLetter() {
		return letter;
	}
	
	public Font getLetterFont() {
		return letterFont;
	}
	
	public Font getLetterPUAFont() {
		return letterPUAFont;
	}
	
	public Font getFontForLetter() {
		return (Character.getType(letter) == Character.PRIVATE_USE) ? letterPUAFont : letterFont;
	}
	
	public Image getLetterImage() {
		return letterImage;
	}
	
	public Cursor getLetterCursor() {
		return letterCursor;
	}
	
	public void setAlphabetName(String alphabetName) {
		if (alphabets.containsFormer(alphabetName)) {
			this.alphabetIndex = alphabets.indexOfFormer(alphabetName);
			this.alphabet = alphabets.getLatter(alphabetIndex);
			this.letterIndex = 0;
			this.letter = this.alphabet[this.letterIndex];
			setLetterImageAndCursor();
			notifyToolContextListeners(CHANGED_ALPHABET_SET | CHANGED_ALPHABET_LETTER);
		}
	}

	public void setAlphabetIndex(int alphabetIndex) {
		while (alphabetIndex < 0) alphabetIndex += alphabets.size();
		while (alphabetIndex >= alphabets.size()) alphabetIndex -= alphabets.size();
		this.alphabetIndex = alphabetIndex;
		this.alphabet = alphabets.getLatter(alphabetIndex);
		this.letterIndex = 0;
		this.letter = this.alphabet[this.letterIndex];
		setLetterImageAndCursor();
		notifyToolContextListeners(CHANGED_ALPHABET_SET | CHANGED_ALPHABET_LETTER);
	}
	
	public void prevAlphabet() {
		setAlphabetIndex(alphabetIndex-1);
	}
	
	public void nextAlphabet() {
		setAlphabetIndex(alphabetIndex+1);
	}

	public void setAlphabet(char[] alphabet) {
		if (alphabets.containsLatter(alphabet)) {
			this.alphabetIndex = alphabets.indexOfLatter(alphabet);
		}
		this.alphabet = alphabet;
		this.letterIndex = 0;
		this.letter = this.alphabet[this.letterIndex];
		setLetterImageAndCursor();
		notifyToolContextListeners(CHANGED_ALPHABET_SET | CHANGED_ALPHABET_LETTER);
	}

	public void setLetterIndex(int letterIndex) {
		while (letterIndex < 0) letterIndex += alphabet.length;
		while (letterIndex >= alphabet.length) letterIndex -= alphabet.length;
		this.letterIndex = letterIndex;
		this.letter = this.alphabet[this.letterIndex];
		setLetterImageAndCursor();
		notifyToolContextListeners(CHANGED_ALPHABET_LETTER);
	}
	
	public void prevLetter() {
		setLetterIndex(letterIndex-1);
	}
	
	public void nextLetter() {
		setLetterIndex(letterIndex+1);
	}

	public void setLetter(char letter) {
		for (int i = 0; i < alphabet.length; i++) {
			if (alphabet[i] == letter) {
				this.letterIndex = i;
				break;
			}
		}
		this.letter = letter;
		setLetterImageAndCursor();
		notifyToolContextListeners(CHANGED_ALPHABET_LETTER);
	}
	
	public void setLetterFont(Font letterFont) {
		this.letterFont = letterFont;
		setLetterImageAndCursor();
		notifyToolContextListeners(CHANGED_ALPHABET_FONT);
	}
	
	public void setLetterPUAFont(Font letterPUAFont) {
		this.letterPUAFont = letterPUAFont;
		setLetterImageAndCursor();
		notifyToolContextListeners(CHANGED_ALPHABET_FONT);
	}
	
	// brushes

	public PairList<String, Vector<Bitmap>> getBrushSets() {
		return brushSets;
	}
	
	public String getBrushSetName() {
		return brushSets.getFormer(brushSetIndex);
	}

	public int getBrushSetIndex() {
		return brushSetIndex;
	}

	public Vector<Bitmap> getBrushes() {
		return brushes;
	}

	public int getBrushIndex() {
		return brushIndex;
	}

	public Bitmap getBrush() {
		return brush;
	}
	
	public int[] getBrushRGB() {
		return brushRGB;
	}
	
	public Cursor getBrushCursor() {
		return brushCursor;
	}
	
	public void setBrushSetName(String brushSetName) {
		if (brushSets.containsFormer(brushSetName)) {
			this.brushSetIndex = brushSets.indexOfFormer(brushSetName);
			this.brushes = brushSets.getLatter(brushSetIndex);
			this.brushIndex = 0;
			this.brush = this.brushes.get(0);
			this.brushRGB = this.brush.getRGB();
			this.brushCursor = this.brush.getCursor();
			notifyToolContextListeners(CHANGED_BRUSH_SET | CHANGED_BRUSH);
		}
	}

	public void setBrushSetIndex(int brushSetIndex) {
		while (brushSetIndex < 0) brushSetIndex += brushSets.size();
		while (brushSetIndex >= brushSets.size()) brushSetIndex -= brushSets.size();
		this.brushSetIndex = brushSetIndex;
		this.brushes = brushSets.getLatter(brushSetIndex);
		this.brushIndex = 0;
		this.brush = this.brushes.get(0);
		this.brushRGB = this.brush.getRGB();
		this.brushCursor = this.brush.getCursor();
		notifyToolContextListeners(CHANGED_BRUSH_SET | CHANGED_BRUSH);
	}
	
	public void prevBrushSet() {
		setBrushSetIndex(brushSetIndex-1);
	}
	
	public void nextBrushSet() {
		setBrushSetIndex(brushSetIndex+1);
	}

	public void setBrushes(Vector<Bitmap> brushes) {
		if (brushSets.containsLatter(brushes)) {
			this.brushSetIndex = brushSets.indexOfLatter(brushes);
		}
		this.brushes = brushes;
		this.brushIndex = 0;
		this.brush = this.brushes.get(0);
		this.brushRGB = this.brush.getRGB();
		this.brushCursor = this.brush.getCursor();
		notifyToolContextListeners(CHANGED_BRUSH_SET | CHANGED_BRUSH);
	}

	public void setBrushIndex(int brushIndex) {
		while (brushIndex < 0) brushIndex += brushes.size();
		while (brushIndex >= brushes.size()) brushIndex -= brushes.size();
		this.brushIndex = brushIndex;
		this.brush = this.brushes.get(brushIndex);
		this.brushRGB = this.brush.getRGB();
		this.brushCursor = this.brush.getCursor();
		notifyToolContextListeners(CHANGED_BRUSH);
	}
	
	public void prevBrush() {
		setBrushIndex(brushIndex-1);
	}
	
	public void nextBrush() {
		setBrushIndex(brushIndex+1);
	}

	public void setBrush(Bitmap brush) {
		if (brushes.contains(brush)) {
			this.brushIndex = brushes.indexOf(brush);
		}
		this.brush = brush;
		this.brushRGB = this.brush.getRGB();
		this.brushCursor = this.brush.getCursor();
		notifyToolContextListeners(CHANGED_BRUSH);
	}
	
	// calligraphy brushes

	public PairList<String, Vector<Bitmap>> getCalligraphyBrushes() {
		return calligraphyBrushes;
	}
	
	public String getCalligraphyBrushName() {
		return calligraphyBrushes.getFormer(calligraphyBrushIndex);
	}

	public int getCalligraphyBrushIndex() {
		return calligraphyBrushIndex;
	}

	public Vector<Bitmap> getCalligraphyBrush() {
		return calligraphyBrush;
	}
	
	public Bitmap getCalligraphyBrushBitmap(int i) {
		return calligraphyBrush.get(i);
	}
	
	public Cursor getCalligraphyBrushCursor(int i) {
		return calligraphyBrush.get(i).getCursor();
	}
	
	public boolean calligraphyContinuous() {
		return calligraphyContinuous;
	}
	
	public void setCalligraphyBrushName(String calligraphyBrushName) {
		if (calligraphyBrushes.containsFormer(calligraphyBrushName)) {
			this.calligraphyBrushIndex = calligraphyBrushes.indexOfFormer(calligraphyBrushName);
			this.calligraphyBrush = calligraphyBrushes.getLatter(calligraphyBrushIndex);
			notifyToolContextListeners(CHANGED_CALLIGRAPHY_BRUSH);
		}
	}

	public void setCalligraphyBrushIndex(int calligraphyBrushIndex) {
		while (calligraphyBrushIndex < 0) calligraphyBrushIndex += calligraphyBrushes.size();
		while (calligraphyBrushIndex >= calligraphyBrushes.size()) calligraphyBrushIndex -= calligraphyBrushes.size();
		this.calligraphyBrushIndex = calligraphyBrushIndex;
		this.calligraphyBrush = calligraphyBrushes.getLatter(calligraphyBrushIndex);
		notifyToolContextListeners(CHANGED_CALLIGRAPHY_BRUSH);
	}
	
	public void prevCalligraphyBrush() {
		setCalligraphyBrushIndex(calligraphyBrushIndex-1);
	}
	
	public void nextCalligraphyBrush() {
		setCalligraphyBrushIndex(calligraphyBrushIndex+1);
	}

	public void setCalligraphyBrush(Vector<Bitmap> calligraphyBrush) {
		if (this.calligraphyBrushes.containsLatter(calligraphyBrush)) {
			this.calligraphyBrushIndex = this.calligraphyBrushes.indexOfLatter(calligraphyBrush);
		}
		this.calligraphyBrush = calligraphyBrush;
		notifyToolContextListeners(CHANGED_CALLIGRAPHY_BRUSH);
	}
	
	public void setCalligraphyContinuous(boolean calligraphyContinuous) {
		this.calligraphyContinuous = calligraphyContinuous;
		notifyToolContextListeners(CHANGED_CALLIGRAPHY_CONTINUOUS);
	}
	
	public void toggleCalligraphyContinuous() {
		calligraphyContinuous = !calligraphyContinuous;
		notifyToolContextListeners(CHANGED_CALLIGRAPHY_CONTINUOUS);
	}
	
	// charcoal brushes

	public PairList<String, Vector<Bitmap>> getCharcoalBrushes() {
		return charcoalBrushes;
	}
	
	public String getCharcoalBrushName() {
		return charcoalBrushes.getFormer(charcoalBrushIndex);
	}

	public int getCharcoalBrushIndex() {
		return charcoalBrushIndex;
	}

	public Vector<Bitmap> getCharcoalBrush() {
		return charcoalBrush;
	}
	
	public Bitmap getCharcoalBrushBitmap(int i) {
		return charcoalBrush.get(i);
	}
	
	public Cursor getCharcoalBrushCursor() {
		return charcoalBrush.get(0).getCursor();
	}
	
	public void setCharcoalBrushName(String charcoalBrushName) {
		if (charcoalBrushes.containsFormer(charcoalBrushName)) {
			this.charcoalBrushIndex = charcoalBrushes.indexOfFormer(charcoalBrushName);
			this.charcoalBrush = charcoalBrushes.getLatter(charcoalBrushIndex);
			notifyToolContextListeners(CHANGED_CHARCOAL_BRUSH);
		}
	}

	public void setCharcoalBrushIndex(int charcoalBrushIndex) {
		while (charcoalBrushIndex < 0) charcoalBrushIndex += charcoalBrushes.size();
		while (charcoalBrushIndex >= charcoalBrushes.size()) charcoalBrushIndex -= charcoalBrushes.size();
		this.charcoalBrushIndex = charcoalBrushIndex;
		this.charcoalBrush = charcoalBrushes.getLatter(charcoalBrushIndex);
		notifyToolContextListeners(CHANGED_CHARCOAL_BRUSH);
	}
	
	public void prevCharcoalBrush() {
		setCharcoalBrushIndex(charcoalBrushIndex-1);
	}
	
	public void nextCharcoalBrush() {
		setCharcoalBrushIndex(charcoalBrushIndex+1);
	}

	public void setCharcoalBrush(Vector<Bitmap> charcoalBrush) {
		if (charcoalBrushes.containsLatter(charcoalBrush)) {
			this.charcoalBrushIndex = charcoalBrushes.indexOfLatter(charcoalBrush);
		}
		this.charcoalBrush = charcoalBrush;
		notifyToolContextListeners(CHANGED_CHARCOAL_BRUSH);
	}
	
	// frames

	public PairList<String, Frame> getFrames() {
		return frames;
	}
	
	public String getFrameName() {
		return frames.getFormer(frameIndex);
	}

	public int getFrameIndex() {
		return frameIndex;
	}

	public Frame getFrame() {
		return frame;
	}
	
	public void setFrameName(String frameName) {
		if (frames.containsFormer(frameName)) {
			this.frameIndex = frames.indexOfFormer(frameName);
			this.frame = frames.getLatter(frameIndex);
			notifyToolContextListeners(CHANGED_FRAME);
		}
	}

	public void setFrameIndex(int frameIndex) {
		while (frameIndex < 0) frameIndex += frames.size();
		while (frameIndex >= frames.size()) frameIndex -= frames.size();
		this.frameIndex = frameIndex;
		this.frame = frames.getLatter(frameIndex);
		notifyToolContextListeners(CHANGED_FRAME);
	}
	
	public void prevFrame() {
		setFrameIndex(frameIndex-1);
	}
	
	public void nextFrame() {
		setFrameIndex(frameIndex+1);
	}

	public void setFrame(Frame frame) {
		if (frames.containsLatter(frame)) {
			this.frameIndex = frames.indexOfLatter(frame);
		}
		this.frame = frame;
		notifyToolContextListeners(CHANGED_FRAME);
	}
	
	// rubber stamps

	public PairList<String, Vector<Image>> getRubberStampSets() {
		return rubberStampSets;
	}
	
	public String getRubberStampSetName() {
		return rubberStampSets.getFormer(rubberStampSetIndex);
	}

	public int getRubberStampSetIndex() {
		return rubberStampSetIndex;
	}

	public Vector<Image> getRubberStamps() {
		return rubberStamps;
	}

	public int getRubberStampIndex() {
		return rubberStampIndex;
	}

	public Image getRubberStamp() {
		return rubberStamp;
	}
	
	public Cursor getRubberStampCursor() {
		return rubberStampCursor;
	}
	
	public void setRubberStampSetName(String rubberStampSetName) {
		if (rubberStampSets.containsFormer(rubberStampSetName)) {
			this.rubberStampSetIndex = rubberStampSets.indexOfFormer(rubberStampSetName);
			this.rubberStamps = rubberStampSets.getLatter(rubberStampSetIndex);
			this.rubberStampIndex = 0;
			this.rubberStamp = this.rubberStamps.get(0);
			BufferedImage rsi = ImageUtils.toBufferedImage(this.rubberStamp, false);
			this.rubberStampCursor = CursorUtils.makeCursor(rsi, rsi.getWidth()/2, rsi.getHeight()/2, "Stamp");
			notifyToolContextListeners(CHANGED_STAMP_SET | CHANGED_STAMP);
		}
	}

	public void setRubberStampSetIndex(int rubberStampSetIndex) {
		while (rubberStampSetIndex < 0) rubberStampSetIndex += rubberStampSets.size();
		while (rubberStampSetIndex >= rubberStampSets.size()) rubberStampSetIndex -= rubberStampSets.size();
		this.rubberStampSetIndex = rubberStampSetIndex;
		this.rubberStamps = rubberStampSets.getLatter(rubberStampSetIndex);
		this.rubberStampIndex = 0;
		this.rubberStamp = this.rubberStamps.get(0);
		BufferedImage rsi = ImageUtils.toBufferedImage(this.rubberStamp, false);
		this.rubberStampCursor = CursorUtils.makeCursor(rsi, rsi.getWidth()/2, rsi.getHeight()/2, "Stamp");
		notifyToolContextListeners(CHANGED_STAMP_SET | CHANGED_STAMP);
	}
	
	public void prevRubberStampSet() {
		setRubberStampSetIndex(rubberStampSetIndex-1);
	}
	
	public void nextRubberStampSet() {
		setRubberStampSetIndex(rubberStampSetIndex+1);
	}

	public void setRubberStamps(Vector<Image> rubberStamps) {
		if (rubberStampSets.containsLatter(rubberStamps)) {
			this.rubberStampSetIndex = rubberStampSets.indexOfLatter(rubberStamps);
		}
		this.rubberStamps = rubberStamps;
		this.rubberStampIndex = 0;
		this.rubberStamp = this.rubberStamps.get(0);
		BufferedImage rsi = ImageUtils.toBufferedImage(this.rubberStamp, false);
		this.rubberStampCursor = CursorUtils.makeCursor(rsi, rsi.getWidth()/2, rsi.getHeight()/2, "Stamp");
		notifyToolContextListeners(CHANGED_STAMP_SET | CHANGED_STAMP);
	}

	public void setRubberStampIndex(int rubberStampIndex) {
		while (rubberStampIndex < 0) rubberStampIndex += rubberStamps.size();
		while (rubberStampIndex >= rubberStamps.size()) rubberStampIndex -= rubberStamps.size();
		this.rubberStampIndex = rubberStampIndex;
		this.rubberStamp = this.rubberStamps.get(rubberStampIndex);
		BufferedImage rsi = ImageUtils.toBufferedImage(this.rubberStamp, false);
		this.rubberStampCursor = CursorUtils.makeCursor(rsi, rsi.getWidth()/2, rsi.getHeight()/2, "Stamp");
		notifyToolContextListeners(CHANGED_STAMP);
	}
	
	public void prevRubberStamp() {
		setRubberStampIndex(rubberStampIndex-1);
	}
	
	public void nextRubberStamp() {
		setRubberStampIndex(rubberStampIndex+1);
	}

	public void setRubberStamp(Image rubberStamp) {
		if (rubberStamps.contains(rubberStamp)) {
			this.rubberStampIndex = rubberStamps.indexOf(rubberStamp);
		}
		this.rubberStamp = rubberStamp;
		BufferedImage rsi = ImageUtils.toBufferedImage(this.rubberStamp, false);
		this.rubberStampCursor = CursorUtils.makeCursor(rsi, rsi.getWidth()/2, rsi.getHeight()/2, "Stamp");
		notifyToolContextListeners(CHANGED_STAMP);
	}
	
	// PowerShapes

	public PairList<String, PairList<String, ParameterizedPath>> getPowerShapeSets() {
		return powerShapeSets;
	}
	
	public String getPowerShapeSetName() {
		return powerShapeSets.getFormer(powerShapeSetIndex);
	}

	public int getPowerShapeSetIndex() {
		return powerShapeSetIndex;
	}

	public PairList<String, ParameterizedPath> getPowerShapes() {
		return powerShapes;
	}
	
	public String getPowerShapeName() {
		return powerShapes.getFormer(powerShapeIndex);
	}

	public int getPowerShapeIndex() {
		return powerShapeIndex;
	}

	public ParameterizedPath getPowerShape() {
		return powerShape;
	}
	
	public void setPowerShapeSetName(String powerShapeSetName) {
		if (powerShapeSets.containsFormer(powerShapeSetName)) {
			this.powerShapeSetIndex = powerShapeSets.indexOfFormer(powerShapeSetName);
			this.powerShapes = powerShapeSets.getLatter(powerShapeSetIndex);
			this.powerShapeIndex = 0;
			this.powerShape = this.powerShapes.getLatter(0);
			notifyToolContextListeners(CHANGED_SHAPE_SET | CHANGED_SHAPE);
		}
	}

	public void setPowerShapeSetIndex(int powerShapeSetIndex) {
		while (powerShapeSetIndex < 0) powerShapeSetIndex += powerShapeSets.size();
		while (powerShapeSetIndex >= powerShapeSets.size()) powerShapeSetIndex -= powerShapeSets.size();
		this.powerShapeSetIndex = powerShapeSetIndex;
		this.powerShapes = powerShapeSets.getLatter(powerShapeSetIndex);
		this.powerShapeIndex = 0;
		this.powerShape = this.powerShapes.getLatter(0);
		notifyToolContextListeners(CHANGED_SHAPE_SET | CHANGED_SHAPE);
	}
	
	public void prevPowerShapeSet() {
		setPowerShapeSetIndex(powerShapeSetIndex-1);
	}
	
	public void nextPowerShapeSet() {
		setPowerShapeSetIndex(powerShapeSetIndex+1);
	}

	public void setPowerShapes(PairList<String, ParameterizedPath> powerShapes) {
		if (powerShapeSets.containsLatter(powerShapes)) {
			this.powerShapeSetIndex = powerShapeSets.indexOfLatter(powerShapes);
		}
		this.powerShapes = powerShapes;
		this.powerShapeIndex = 0;
		this.powerShape = this.powerShapes.getLatter(0);
		notifyToolContextListeners(CHANGED_SHAPE_SET | CHANGED_SHAPE);
	}
	
	public void setPowerShapeName(String powerShapeName) {
		if (powerShapes.containsFormer(powerShapeName)) {
			this.powerShapeIndex = powerShapes.indexOfFormer(powerShapeName);
			this.powerShape = this.powerShapes.getLatter(powerShapeIndex);
			notifyToolContextListeners(CHANGED_SHAPE);
		}
	}

	public void setPowerShapeIndex(int powerShapeIndex) {
		while (powerShapeIndex < 0) powerShapeIndex += powerShapes.size();
		while (powerShapeIndex >= powerShapes.size()) powerShapeIndex -= powerShapes.size();
		this.powerShapeIndex = powerShapeIndex;
		this.powerShape = this.powerShapes.getLatter(powerShapeIndex);
		notifyToolContextListeners(CHANGED_SHAPE);
	}
	
	public void prevPowerShape() {
		setPowerShapeIndex(powerShapeIndex-1);
	}
	
	public void nextPowerShape() {
		setPowerShapeIndex(powerShapeIndex+1);
	}

	public void setPowerShape(ParameterizedPath powerShape) {
		if (powerShapes.containsLatter(powerShape)) {
			this.powerShapeIndex = powerShapes.indexOfLatter(powerShape);
		}
		this.powerShape = powerShape;
		notifyToolContextListeners(CHANGED_SHAPE);
	}
	
	// sprinkles

	public PairList<String,Vector<Bitmap>> getSprinkleSets() {
		return sprinkleSets;
	}
	
	public String getSprinkleSetName() {
		return sprinkleSets.getFormer(sprinkleSetIndex);
	}

	public int getSprinkleSetIndex() {
		return sprinkleSetIndex;
	}

	public Vector<Bitmap> getSprinkles() {
		return sprinkles;
	}

	public int getSprinkleIndex() {
		return sprinkleIndex;
	}

	public Bitmap getSprinkle() {
		return sprinkle;
	}
	
	public Cursor getSprinkleCursor() {
		return sprinkle.getCursor();
	}
	
	public boolean sprinkleBrushMode() {
		return sprinkleBrushMode;
	}
	
	public void setSprinkleSetName(String sprinkleSetName) {
		if (sprinkleSets.containsFormer(sprinkleSetName)) {
			this.sprinkleSetIndex = sprinkleSets.indexOfFormer(sprinkleSetName);
			this.sprinkles = sprinkleSets.getLatter(sprinkleSetIndex);
			this.sprinkleIndex = random.nextInt(this.sprinkles.size());
			this.sprinkle = this.sprinkles.get(this.sprinkleIndex);
			notifyToolContextListeners(CHANGED_SPRINKLE_SET | CHANGED_SPRINKLE);
		}
	}

	public void setSprinkleSetIndex(int sprinkleSetIndex) {
		while (sprinkleSetIndex < 0) sprinkleSetIndex += sprinkleSets.size();
		while (sprinkleSetIndex >= sprinkleSets.size()) sprinkleSetIndex -= sprinkleSets.size();
		this.sprinkleSetIndex = sprinkleSetIndex;
		this.sprinkles = sprinkleSets.getLatter(sprinkleSetIndex);
		this.sprinkleIndex = random.nextInt(this.sprinkles.size());
		this.sprinkle = this.sprinkles.get(this.sprinkleIndex);
		notifyToolContextListeners(CHANGED_SPRINKLE_SET | CHANGED_SPRINKLE);
	}
	
	public void prevSprinkleSet() {
		setSprinkleSetIndex(sprinkleSetIndex-1);
	}
	
	public void nextSprinkleSet() {
		setSprinkleSetIndex(sprinkleSetIndex+1);
	}

	public void setSprinkles(Vector<Bitmap> sprinkles) {
		if (sprinkleSets.containsLatter(sprinkles)) {
			this.sprinkleSetIndex = sprinkleSets.indexOfLatter(sprinkles);
		}
		this.sprinkles = sprinkles;
		this.sprinkleIndex = random.nextInt(this.sprinkles.size());
		this.sprinkle = this.sprinkles.get(this.sprinkleIndex);
		notifyToolContextListeners(CHANGED_SPRINKLE_SET | CHANGED_SPRINKLE);
	}

	public void setSprinkleIndex(int sprinkleIndex) {
		while (sprinkleIndex < 0) sprinkleIndex += sprinkles.size();
		while (sprinkleIndex >= sprinkles.size()) sprinkleIndex -= sprinkles.size();
		this.sprinkleIndex = sprinkleIndex;
		this.sprinkle = this.sprinkles.get(this.sprinkleIndex);
		notifyToolContextListeners(CHANGED_SPRINKLE);
	}
	
	public void prevSprinkle() {
		setSprinkleIndex(sprinkleIndex-1);
	}
	
	public void nextSprinkle() {
		setSprinkleIndex(sprinkleIndex+1);
	}
	
	public void randomSprinkle() {
		this.sprinkleIndex = random.nextInt(this.sprinkles.size());
		this.sprinkle = this.sprinkles.get(this.sprinkleIndex);
		notifyToolContextListeners(CHANGED_SPRINKLE);
	}

	public void setSprinkle(Bitmap sprinkle) {
		if (sprinkles.contains(sprinkle)) {
			this.sprinkleIndex = sprinkles.indexOf(sprinkle);
		}
		this.sprinkle = sprinkle;
		notifyToolContextListeners(CHANGED_SPRINKLE);
	}
	
	public void setSprinkleBrushMode(boolean sprinkleBrushMode) {
		this.sprinkleBrushMode = sprinkleBrushMode;
		notifyToolContextListeners(CHANGED_SPRINKLE_BRUSH_MODE);
	}
	
	public void toggleSprinkleBrushMode() {
		sprinkleBrushMode = !sprinkleBrushMode;
		notifyToolContextListeners(CHANGED_SPRINKLE_BRUSH_MODE);
	}
	
	// custom
	
	public <T> T getCustom(Class<? extends Tool> t, String k, Class<T> type, T def) {
		k = t.getSimpleName() + "." + k;
		if (custom.containsKey(k)) {
			Object v = custom.get(k);
			if (type.isAssignableFrom(v.getClass())) {
				return type.cast(v);
			} else {
				custom.put(k, def);
				return def;
			}
		} else {
			custom.put(k, def);
			return def;
		}
	}

	public void setCustom(Class<? extends Tool> t, String k, Object v) {
		custom.put(t.getSimpleName() + "." + k, v);
		notifyToolContextListeners(CHANGED_CUSTOM);
	}
	
	public <T extends Number> void decrementCustom(Class<? extends Tool> t, String k, Class<T> type, T def) {
		Number n = getCustom(t, k, type, def);
		if (n instanceof Double) n = n.doubleValue()-1.0;
		else if (n instanceof Float) n = n.floatValue()-1f;
		else if (n instanceof Long) n = n.longValue()-1L;
		else if (n instanceof Integer) n = n.intValue()-1;
		else if (n instanceof Short) n = n.shortValue()-(short)1;
		else if (n instanceof Byte) n = n.byteValue()-(byte)1;
		else if (n instanceof BigDecimal) n = ((BigDecimal)n).subtract(BigDecimal.ONE);
		else if (n instanceof BigInteger) n = ((BigInteger)n).subtract(BigInteger.ONE);
		else throw new RuntimeException("Unknown Number subclass in ToolContext.decrementCustom.");
		setCustom(t, k, n);
	}
	
	public <T extends Number> void decrementCustom(Class<? extends Tool> t, String k, Class<T> type, T def, T min) {
		Number n = getCustom(t, k, type, def);
		if (n instanceof Double) n = Math.max(min.doubleValue(), n.doubleValue()-1.0);
		else if (n instanceof Float) n = Math.max(min.floatValue(), n.floatValue()-1f);
		else if (n instanceof Long) n = Math.max(min.longValue(), n.longValue()-1L);
		else if (n instanceof Integer) n = Math.max(min.intValue(), n.intValue()-1);
		else if (n instanceof Short) n = Math.max(min.shortValue(), n.shortValue()-(short)1);
		else if (n instanceof Byte) n = Math.max(min.byteValue(), n.byteValue()-(byte)1);
		else if (n instanceof BigDecimal) n = max((BigDecimal)min, ((BigDecimal)n).subtract(BigDecimal.ONE));
		else if (n instanceof BigInteger) n = max((BigInteger)min, ((BigInteger)n).subtract(BigInteger.ONE));
		else throw new RuntimeException("Unknown Number subclass in ToolContext.decrementCustom.");
		setCustom(t, k, n);
	}
	
	public <T extends Number> void incrementCustom(Class<? extends Tool> t, String k, Class<T> type, T def) {
		Number n = getCustom(t, k, type, def);
		if (n instanceof Double) n = n.doubleValue()+1.0;
		else if (n instanceof Float) n = n.floatValue()+1f;
		else if (n instanceof Long) n = n.longValue()+1L;
		else if (n instanceof Integer) n = n.intValue()+1;
		else if (n instanceof Short) n = n.shortValue()+(short)1;
		else if (n instanceof Byte) n = n.byteValue()+(byte)1;
		else if (n instanceof BigDecimal) n = ((BigDecimal)n).add(BigDecimal.ONE);
		else if (n instanceof BigInteger) n = ((BigInteger)n).add(BigInteger.ONE);
		else throw new RuntimeException("Unknown Number subclass in ToolContext.incrementCustom.");
		setCustom(t, k, n);
	}
	
	public <T extends Number> void incrementCustom(Class<? extends Tool> t, String k, Class<T> type, T def, T max) {
		Number n = getCustom(t, k, type, def);
		if (n instanceof Double) n = Math.min(max.doubleValue(), n.doubleValue()+1.0);
		else if (n instanceof Float) n = Math.min(max.floatValue(), n.floatValue()+1f);
		else if (n instanceof Long) n = Math.min(max.longValue(), n.longValue()+1L);
		else if (n instanceof Integer) n = Math.min(max.intValue(), n.intValue()+1);
		else if (n instanceof Short) n = Math.min(max.shortValue(), n.shortValue()+(short)1);
		else if (n instanceof Byte) n = Math.min(max.byteValue(), n.byteValue()+(byte)1);
		else if (n instanceof BigDecimal) n = min((BigDecimal)max, ((BigDecimal)n).add(BigDecimal.ONE));
		else if (n instanceof BigInteger) n = min((BigInteger)max, ((BigInteger)n).add(BigInteger.ONE));
		else throw new RuntimeException("Unknown Number subclass in ToolContext.incrementCustom.");
		setCustom(t, k, n);
	}
	
	public void toggleCustom(Class<? extends Tool> t, String k, boolean def) {
		setCustom(t, k, !getCustom(t, k, Boolean.class, def));
	}
	
	// listeners
	
	public void addToolContextListener(ToolContextListener l) {
		listeners.add(l);
	}
	
	public void removeToolContextListener(ToolContextListener l) {
		listeners.remove(l);
	}
	
	public ToolContextListener[] getToolContextListeners() {
		return listeners.toArray(new ToolContextListener[0]);
	}
	
	protected void notifyToolContextListeners(long delta) {
		for (ToolContextListener l : listeners) {
			l.toolSettingsChanged(this, delta);
		}
	}
	
	// private interface
	
	private BigDecimal max(BigDecimal a, BigDecimal b) {
		return (a.compareTo(b) > 0) ? a : b;
	}
	
	private BigInteger max(BigInteger a, BigInteger b) {
		return (a.compareTo(b) > 0) ? a : b;
	}
	
	private BigDecimal min(BigDecimal a, BigDecimal b) {
		return (a.compareTo(b) < 0) ? a : b;
	}
	
	private BigInteger min(BigInteger a, BigInteger b) {
		return (a.compareTo(b) < 0) ? a : b;
	}
	
	private void setLetterImageAndCursor() {
		// calculate font metrics
		this.letterImage = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = this.letterImage.createGraphics();
		Font f = (Character.getType(this.letter) == Character.PRIVATE_USE) ? this.letterPUAFont : this.letterFont;
		FontMetrics fm = g.getFontMetrics(f);
		int w = fm.stringWidth(Character.toString(this.letter));
		int h = fm.getHeight();
		int a = fm.getAscent();
		g.dispose();
		// create temporary image that is larger than necessary
		this.letterImage = new BufferedImage(w+w, h+h, BufferedImage.TYPE_INT_ARGB);
		g = this.letterImage.createGraphics();
		g.setFont(f);
		g.setColor(Color.black);
		g.drawString(Character.toString(this.letter), w/2, h/2+a);
		g.dispose();
		// calculate bounding box and final hot spot using the temporary image
		int hsx = w;
		int hsy = h/2+a;
		int xmin = 0, ymin = 0;
		int xmax = this.letterImage.getWidth();
		int ymax = this.letterImage.getHeight();
		// find topmost row containing pixels or hotspot
		while (ymin < ymax && hsy > 0) {
			int[] rgb = new int[w+w];
			this.letterImage.getRGB(0, ymin, w+w, 1, rgb, 0, w+w);
			boolean containsPixels = false;
			for (int i : rgb) {
				if (i < 0 || i >= 0x01000000) {
					containsPixels = true;
					break;
				}
			}
			if (containsPixels) break;
			else { ymin++; hsy--; }
		}
		// find bottommost row containing pixels or hotspot
		while (ymax > ymin && hsy < ymax-ymin-1) {
			int[] rgb = new int[w+w];
			this.letterImage.getRGB(0, ymax-1, w+w, 1, rgb, 0, w+w);
			boolean containsPixels = false;
			for (int i : rgb) {
				if (i < 0 || i >= 0x01000000) {
					containsPixels = true;
					break;
				}
			}
			if (containsPixels) break;
			else ymax--;
		}
		// find leftmost column containing pixels or hotspot
		while (xmin < xmax && hsx > 0) {
			int[] rgb = new int[h+h];
			this.letterImage.getRGB(xmin, 0, 1, h+h, rgb, 0, 1);
			boolean containsPixels = false;
			for (int i : rgb) {
				if (i < 0 || i >= 0x01000000) {
					containsPixels = true;
					break;
				}
			}
			if (containsPixels) break;
			else { xmin++; hsx--; }
		}
		// find rightmost column containing pixels or hotspot
		while (xmax > xmin && hsx < xmax-xmin-1) {
			int[] rgb = new int[h+h];
			this.letterImage.getRGB(xmax-1, 0, 1, h+h, rgb, 0, 1);
			boolean containsPixels = false;
			for (int i : rgb) {
				if (i < 0 || i >= 0x01000000) {
					containsPixels = true;
					break;
				}
			}
			if (containsPixels) break;
			else xmax--;
		}
		// create final image trimmed from temporary image
		int[] rgb = new int[(xmax-xmin) * (ymax-ymin)];
		this.letterImage.getRGB(xmin, ymin, xmax-xmin, ymax-ymin, rgb, 0, xmax-xmin);
		this.letterImage = new BufferedImage(xmax-xmin, ymax-ymin, BufferedImage.TYPE_INT_ARGB);
		this.letterImage.setRGB(0, 0, xmax-xmin, ymax-ymin, rgb, 0, xmax-xmin);
		// set cursor
		this.letterCursor = CursorUtils.makeCursor(this.letterImage, hsx, hsy, "AStamp");
	}
}
