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
		tools.add(new MagicMarkerTool());
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
