package com.kreative.paint.material;

import java.io.IOException;
import java.io.InputStream;
import com.kreative.paint.material.shape.ArcType;
import com.kreative.paint.material.shape.ParameterizedShape;
import com.kreative.paint.material.shape.ParameterizedValue;
import com.kreative.paint.material.shape.PowerShape;
import com.kreative.paint.material.shape.PowerShapeList;
import com.kreative.paint.material.shape.PowerShapeParser;
import com.kreative.paint.material.shape.WindingRule;

public class ShapeLoader {
	private final MaterialLoader loader;
	private final MaterialList<PowerShapeList> shapes;
	
	public ShapeLoader(MaterialLoader loader) {
		this.loader = loader;
		this.shapes = new MaterialList<PowerShapeList>();
	}
	
	public MaterialList<PowerShapeList> getShapes() {
		if (shapes.isEmpty()) loadResources();
		if (shapes.isEmpty()) createShapes();
		return shapes;
	}
	
	private void loadResources() {
		for (MaterialResource r : loader.listResources()) {
			if (r.isFormat("shpx", false)) {
				try {
					InputStream in = r.getInputStream();
					PowerShapeList list = PowerShapeParser.parse(r.getResourceName(), in);
					in.close();
					String name = (list.name != null) ? list.name : r.getResourceName();
					shapes.add(name, list);
				} catch (IOException e) {
					System.err.println("Warning: Failed to compile shape set " + r.getResourceName() + ".");
					e.printStackTrace();
				}
			}
		}
	}
	
	private void createShapes() {
		System.err.println("Notice: No shapes found. Generating generic shapes.");
		PowerShapeList list = new PowerShapeList("Basic");
		PowerShape line = new PowerShape(WindingRule.NON_ZERO, "Line");
		line.addShape(new ParameterizedShape.Line(
			new ParameterizedValue(0.0), new ParameterizedValue(0.0),
			new ParameterizedValue(1.0), new ParameterizedValue(1.0)
		));
		list.add(line);
		PowerShape rect = new PowerShape(WindingRule.NON_ZERO, "Rectangle");
		rect.addShape(new ParameterizedShape.Rect(
			new ParameterizedValue(0.0), new ParameterizedValue(0.0),
			new ParameterizedValue(1.0), new ParameterizedValue(1.0),
			new ParameterizedValue(0.0), new ParameterizedValue(0.0)
		));
		list.add(rect);
		PowerShape rrect = new PowerShape(WindingRule.NON_ZERO, "Round Rectangle");
		rrect.addShape(new ParameterizedShape.Rect(
			new ParameterizedValue(0.0), new ParameterizedValue(0.0),
			new ParameterizedValue(1.0), new ParameterizedValue(1.0),
			new ParameterizedValue(0.25), new ParameterizedValue(0.25)
		));
		list.add(rrect);
		PowerShape ellipse = new PowerShape(WindingRule.NON_ZERO, "Ellipse");
		ellipse.addShape(new ParameterizedShape.Ellipse(
			new ParameterizedValue(0.5), new ParameterizedValue(0.5),
			new ParameterizedValue(0.5), new ParameterizedValue(0.5)
		));
		list.add(ellipse);
		PowerShape arc = new PowerShape(WindingRule.NON_ZERO, "Arc");
		arc.addShape(new ParameterizedShape.Arc(
			new ParameterizedValue(0.0), new ParameterizedValue(1.0),
			new ParameterizedValue(1.0), new ParameterizedValue(1.0),
			new ParameterizedValue(0.0), new ParameterizedValue(90.0),
			ArcType.OPEN
		));
		list.add(arc);
		shapes.add("Basic", list);
	}
}
