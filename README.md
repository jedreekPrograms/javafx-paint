# GeometryEditorFX — JavaFX Paint

A small Java 21 desktop editor for drawing and manipulating geometric shapes.
Built with JavaFX, Maven and Gson as a university programming project.

## Features

- Draw circles, rectangles and polygons.
- Move shapes with the mouse and resize them with the scroll wheel.
- Rotate a focused shape with the left/right arrow keys.
- Change fill colors through a right-click color picker.
- Save drawings as JSON and reopen them with their colors and transforms intact.
- Open drawings created by the original version of the application.

## Run locally

Requirements: **JDK 21**, Maven 3.8+ and a graphical desktop.
JavaFX dependencies are downloaded by Maven; no separate JavaFX SDK or preview flags are needed.

```shell
mvn clean verify
mvn javafx:run
```

In IntelliJ IDEA, open `pom.xml` as a Maven project, select JDK 21 and run the `javafx:run` Maven goal.
The application entry point is `pl.paint.app.PaintApplication`.

## Controls

The interface retains its original Polish labels.

| Action | Control |
| --- | --- |
| Circle | Choose **Okrąg**, press at the center and drag to set the radius |
| Rectangle | Choose **Prostokąt**, then drag between opposite corners |
| Polygon | Choose **Wielokąt**, click at least three vertices, then click near the first vertex or double-click to finish |
| Move | Drag a completed shape with the primary mouse button |
| Resize | Scroll over a completed shape |
| Rotate | Focus a shape by clicking it, then press **←** or **→** for 15-degree steps |
| Fill color | Right-click a shape and use the color picker |
| Cancel drawing | Press **Esc**, right-click or choose **Wybierz** |
| Save / open | **Pliki → Zapisz / Wczytaj** |

Drawing tools complete one shape at a time and then return to selection.
Switching tools discards an unfinished preview. A malformed file leaves the current drawing unchanged.

## Code structure

All packages are under `src/main/java/pl/paint/`.

| Package | Responsibility |
| --- | --- |
| [app](src/main/java/pl/paint/app/) | JavaFX application lifecycle |
| [ui](src/main/java/pl/paint/ui/) | Window, drawing area and file dialogs |
| [shapes](src/main/java/pl/paint/shapes/) | Editable JavaFX shapes, transforms and shared interaction handlers |
| [tools](src/main/java/pl/paint/tools/) | Shape creation and active-tool cancellation |
| [persistence](src/main/java/pl/paint/persistence/) | JSON representation, validation and file storage |

The editor keeps JavaFX shapes as its drawing objects. A separate JSON representation prevents file storage
from depending on the internal fields of JavaFX nodes. Existing field names remain compatible with original files;
translation and scale values are additional optional fields.

Saving uses a temporary file before replacing the destination. Loading constructs and validates the complete
drawing before changing the canvas.

## Validation and scope

```shell
mvn clean verify
```

Tests cover geometric transforms, drawing-tool completion/cancellation, JSON round trips, original-format
compatibility and invalid-file handling. Desktop smoke tests open the real JavaFX window when `DISPLAY` is set;
they are skipped without a display. [GitHub Actions](.github/workflows/ci.yml) runs the complete suite on Java 21 under Xvfb.

The application is a compact shape editor. Raster brushes, undo/redo, image export and layers are possible future extensions.

## Project history

This repository reorganizes the original [paint project](https://github.com/jedreekPrograms/paint).
The staged history was reconstructed from its imported snapshot; the November–December 2024 dates
form an assigned timeline rather than recovered original commit timestamps.
