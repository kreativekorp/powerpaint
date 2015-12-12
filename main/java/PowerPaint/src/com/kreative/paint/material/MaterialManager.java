package com.kreative.paint.material;

public class MaterialManager {
	private final MaterialLoader     materialLoader     ;
	private final AlphabetLoader     alphabetLoader     ;
	private final ColorPaletteLoader colorPaletteLoader ;
	private final DitherLoader       ditherLoader       ;
	private final FontLoader         fontLoader         ;
	private final FrameLoader        frameLoader        ;
	private final GradientLoader     gradientLoader     ;
	private final JarLoader          jarLoader          ;
	private final PatternLoader      patternLoader      ;
	private final ShapeLoader        shapeLoader        ;
	private final SpriteLoader       spriteLoader       ;
	private final StrokeLoader       strokeLoader       ;
	private final TextureLoader      textureLoader      ;
	
	public MaterialManager(MaterialLoader loader) {
		this.materialLoader     =                        (loader);
		this.alphabetLoader     = new AlphabetLoader     (loader);
		this.colorPaletteLoader = new ColorPaletteLoader (loader);
		this.ditherLoader       = new DitherLoader       (loader);
		this.fontLoader         = new FontLoader         (loader);
		this.frameLoader        = new FrameLoader        (loader);
		this.gradientLoader     = new GradientLoader     (loader);
		this.jarLoader          = new JarLoader          (loader);
		this.patternLoader      = new PatternLoader      (loader);
		this.shapeLoader        = new ShapeLoader        (loader);
		this.spriteLoader       = new SpriteLoader       (loader);
		this.strokeLoader       = new StrokeLoader       (loader);
		this.textureLoader      = new TextureLoader      (loader);
	}
	
	public MaterialLoader     materialLoader     () { return materialLoader     ; }
	public AlphabetLoader     alphabetLoader     () { return alphabetLoader     ; }
	public ColorPaletteLoader colorPaletteLoader () { return colorPaletteLoader ; }
	public DitherLoader       ditherLoader       () { return ditherLoader       ; }
	public FontLoader         fontLoader         () { return fontLoader         ; }
	public FrameLoader        frameLoader        () { return frameLoader        ; }
	public GradientLoader     gradientLoader     () { return gradientLoader     ; }
	public JarLoader          jarLoader          () { return jarLoader          ; }
	public PatternLoader      patternLoader      () { return patternLoader      ; }
	public ShapeLoader        shapeLoader        () { return shapeLoader        ; }
	public SpriteLoader       spriteLoader       () { return spriteLoader       ; }
	public StrokeLoader       strokeLoader       () { return strokeLoader       ; }
	public TextureLoader      textureLoader      () { return textureLoader      ; }
}
