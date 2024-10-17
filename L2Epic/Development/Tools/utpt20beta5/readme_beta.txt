Unreal Tournament Package Tool 2.0 beta 5
------------------------------------------

This beta most important change is its new interface, along with the support for
new games. However, due to the slow development it may have more visible bugs
than expected. For instance, the analysis window sometimes misbehaves.

-------------------------------------------
Beta History:
v2.0
   beta 1
   * Added partial support for UT2003
   *   TextBuffer: fixed
   *   Sound: fixed
   *   new opcodes: EX_Length
   *   new class flags: EditInlineNew, CollapseCategories, ExportStructs, Placeable
   *   Struct_Zone: fixed
   *   Struct_LightMap: fixed
   beta 2
   * Fixed bug when UTPTFormats.xml file does not exist.
   * Support for extracting classes inheriting from Texture.
   * Fixed reading CubeMap.
   * Fixed reading Array properties.
   * Fixed reading textures of Army Operations.
   * Added filters for exports list.
   * Added support for saving in DDS format (must be activated in options window).
   * Fixed file extension when saving in TGA format.
   * Added autosize & stretch option in texture visualization (context menu in image).
   * 3D export options are kept between exports (not yet between executions).
   * Added support for selecting extract filename or generating it with rules.
   * Removed "Extractable" column from export table & tree.
   * Some column name and position changes in export and import tables and trees.
   * Added format to textures view info.
   * Added view properties menu option to textures view.
   * Added option to view a background texture in texture viewer (only if alpha channel exists).
   * Removed option to save alpha channel, since it can be extracted from a full BMP or TGA.
   beta 3
   * Support of encrypted Army Operations packages.
   * Added support for UT2 UMODs.
   * Dropping a file frees explorer immediately.
   * Initial support for Unreal 2 and Splinter Cell.
   * Added autoformat to analize window.
   * Open As menu option to select the game of the package.
   beta 4
   * Uses UTPackages 2.2 (support for Devastation, Rainbow Six Raven Shield, Unreal Championship and Lineage 2).
   * Added display of line offsets in decompilation.
   * Added conversions window to analyzer.
   * Supports textures without mipmaps (some CubeMaps).
   * Support for StaticMesh class exporting (with some bugs in UV coords).
   beta 5
   * New interface.
   * Preview for meshes and Polys.
   * Field colors and hints in analysis windows.
   * Multi file support.
   * Added support for (dragging&)dropping multiple files.
   * Syntax highlighting.
   * Array properties show in tree.
   * Uses a prerelease of UTPackages 2.3 with support for UT2004, Lineage2 and other games.

v1.6
   beta 1
   * Allows file dropping to load packages
   * File Explorer (supports UMOD and ZIP files).
   * Can view text and html files.
   * Fixed sound exporting in package with version >= 63.
   * Textures tab.
   * Sounds preview.
   beta 2
   * Fixed function reading/decompilation in version 61 packages.
   * Fixed texture reading when the non-compressed mipmaps *are* compressed.
   * Added default properties to class decompilation.
   * Properties are read more completely (structs and enums).
   * Fixed bug: removed unused jump labels in decompilation.
   * Fixed bug: reading plane and sphere struct properties.
   * Added special faces (weapon) export from LodMesh objects.
   beta 3
   * Fixed bug saving "Beautify Code" option.
   * Added option to allow reading other packages when needed.
   * Reads Enums better.
   * Fixed bug that prevented several classes to be decompiled at the same time.
   * Fixed some bugs in the file explorer.
   * Fixed bug when using the "extract directory" setting.
   * Added option to see the script from the class object popup menu.
   * Can open an umod inside a zip file in file explorer.
   * Option to see object properties including the ones defined in ancestors.
   beta 4
   * Added end-quote characters to some object references in properties.
   * Added menu option to find classes with unknown extra data.
   * Fixed reading unknown type properties and added Array properties (Rune saved games).
   * Fixed reading Music objects.
   * Detects encrypted packages.
   * Extracting raw object will not give errors if the object can't be interpreted.
   * Added decompilation for TextBuffer objects (finds its class and decompiles it).
   * Added detected game label.
   * Displays/extracts compressed mipmaps if uncompressed does not exist and in real size.
   * Fixed bug printing properties.
   * Added file loading by parameter.
   * Added support for exporting multiple frames to 3DStudio format (in one or multiple files).
   * Added support for extracting compressed textures.
   * Added support for viewing and saving textures alpha channel.
   * Fixed saving of PAL palette files.

