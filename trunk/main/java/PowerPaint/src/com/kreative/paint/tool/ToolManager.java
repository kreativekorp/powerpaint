/*
 * Copyright &copy; 2009-2010 Rebecca G. Bettencourt / Kreative Software
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

package com.kreative.paint.tool;

import java.util.List;
import java.util.Vector;
import com.kreative.paint.res.MaterialsManager;

public class ToolManager {
	private ArrowTool arrowTool;
	private MarqueeTool marqueeTool;
	private List<Tool> tools;
	
	public ToolManager(MaterialsManager mm) {
		tools = new Vector<Tool>();
		// SELECTION TOOLS
		tools.add(arrowTool = new ArrowTool());
		tools.add(new TransformTool());
		tools.add(new PaintDrawMarqueeTool());
		tools.add(marqueeTool = new RectangleMarqueeTool());
		tools.add(new RoundRectMarqueeTool());
		tools.add(new EllipseMarqueeTool());
		tools.add(new PolygonMarqueeTool());
		tools.add(new FreeformMarqueeTool());
		tools.add(new LassoTool());
		tools.add(new MagicWandTool());
		tools.add(new RowColumnMarqueeTool());
		//tools.add(new RowMarqueeTool());
		//tools.add(new ColumnMarqueeTool());
		tools.add(new LayerMoveTool());
		// VIEW TOOLS
		tools.add(new HotspotTool());
		tools.add(new RulerTool());
		tools.add(new GrabberTool());
		tools.add(new MagnifierTool());
		// PAINT TOOLS
		tools.add(new PencilTool());
		tools.add(new BrushTool());
		tools.add(new PowerBrushTool());
		tools.add(new FillTool());
		tools.add(new EyedropperTool());
		tools.add(new EraserTool());
		tools.add(new PowerEraserTool());
		tools.add(new TransparencyTool());
		// SHAPE TOOLS
		tools.add(new TextTool());
		tools.add(new LineTool());
		tools.add(new RectangleTool());
		tools.add(new RoundRectTool());
		tools.add(new EllipseTool());
		tools.add(new ArcTool());
		tools.add(new PolygonTool());
		tools.add(new RegularPolygonTool());
		tools.add(new FreeformShapeTool());
		tools.add(new BezierTool());
		tools.add(new PowerShapeTool());
		// MISCELLANEOUS TOOLS
		tools.add(new ThreeDBoxTool());
		tools.add(new AlphabetStampTool());
		tools.add(new BubblesTool());
		tools.add(new CalligraphyBrushTool());
		tools.add(new CellularAutomatonTool());
		tools.add(new CharcoalTool());
		tools.add(new CropMarksTool());
		tools.add(new CurlBrushTool());
		tools.add(new CurlPencilTool());
		tools.add(new CurlShapeTool());
		tools.add(new CycloidTool());
		tools.add(new DotsaTool());
		tools.add(new DryBrushTool());
		tools.add(new FlowerTool());
		tools.add(new FramerTool());
		tools.add(new GridTool());
		tools.add(new MirrorBrushTool());
		tools.add(new MovingVanTool());
		tools.add(new PerspectiveGridTool());
		tools.add(new RubberStampTool());
		tools.add(new SmudgeTool());
		tools.add(new SpinTool());
		tools.add(new SpiralTool());
		tools.add(new SpraypaintTool());
		tools.add(new PowerSpraypaintTool());
		tools.add(new SprinklerTool());
		tools.add(new TwisterTool());
		// PLUGIN TOOLS
		tools.addAll(mm.getPluginTools());
		// COPLAND PERSPECTIVE TOOLS
		// HAIKU PERSPECTIVE TOOLS
	}
	
	public List<Tool> getTools() {
		return tools;
	}
	
	public ArrowTool getArrowTool() {
		return arrowTool;
	}
	
	public MarqueeTool getMarqueeTool() {
		return marqueeTool;
	}
}
