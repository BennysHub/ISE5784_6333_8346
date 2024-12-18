package scene;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import geometries.*;
import lighting.AmbientLight;
import lighting.DirectionalLight;
import lighting.PointLight;
import lighting.SpotLight;
import primitives.*;
import renderer.QualityLevel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A specialized scene parser that reads a scene configuration from a JSON file and initializes
 * the scene with its corresponding elements such as background color, ambient light, lights,
 * geometries, and materials. This class supports complex scene configurations, including
 * hierarchical relationships and material properties.
 *
 * <p>The JSON file must follow a specific format to define all elements in the scene:</p>
 *
 * <pre>
 * {
 *   "scene": {
 *     "background-color": "R G B [S]",
 *     "ambient-light": {
 *       "color": "R G B",
 *       "kA": "value"
 *     },
 *     "lights": {
 *       "spotLight": [...],
 *       "pointLight": [...],
 *       "directionalLight": [...]
 *     },
 *     "geometries": {
 *       "sphere": [...],
 *       "triangle": [...],
 *       "cylinder": [...],
 *       "tube": [...],
 *       "polygon": [...],
 *       "mesh": [...]
 *     }
 *   }
 * }
 * </pre>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li><b>Background Color:</b> Parses the background color of the scene. Supports optional scaling.</li>
 *   <li><b>Ambient Light:</b> Configures ambient lighting with color and attenuation coefficients.</li>
 *   <li><b>Lights:</b> Supports spotlights, point lights, and directional lights with various properties such as
 *   intensity, attenuation, direction, and sampling quality.</li>
 *   <li><b>Geometries:</b> Creates geometries like spheres, triangles, cylinders, tubes, polygons, and meshes,
 *   with support for materials, scaling, and offsets.</li>
 *   <li><b>Meshes:</b> Parses STL files for complex meshes and integrates them into the scene.</li>
 *   <li><b>Material Properties:</b> Configures reflection, transparency, shininess, and emission for geometries.</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 * <pre>
 * {@code
 * Scene fromJson = new SceneJsonParser("path/to/scene.json", "MyScene");
 * }
 * </pre>
 *
 * <h2>Implementation Details</h2>
 * The class uses the Google Gson library to parse JSON and provides helper methods for
 * parsing colors, vectors, materials, and geometries. It validates input and ensures
 * that malformed JSON or unsupported configurations are handled gracefully with exceptions.
 *
 * <h2>Supported Elements</h2>
 * <b>Lights:</b>
 * <ul>
 *   <li><b>SpotLight:</b> Position, direction, intensity, narrow beam, size, and attenuation coefficients.</li>
 *   <li><b>PointLight:</b> Position, intensity, size, and attenuation coefficients.</li>
 *   <li><b>DirectionalLight:</b> Direction and intensity.</li>
 * </ul>
 *
 * <b>Geometries:</b>
 * <ul>
 *   <li><b>Sphere:</b> Center, radius, and material properties.</li>
 *   <li><b>Triangle:</b> Three vertices and material properties.</li>
 *   <li><b>Cylinder:</b> Base point, axis, direction, radius, height, and material properties.</li>
 *   <li><b>Tube:</b> Base point, axis direction, radius, and material properties.</li>
 *   <li><b>Polygon:</b> List of vertices and material properties.</li>
 *   <li><b>Mesh:</b> STL file path, offset, scaling, emission, and material properties.</li>
 * </ul>
 *
 * <h2>Design Considerations</h2>
 * The parser is designed to be flexible and extensible. For example:
 * <ul>
 *   <li>New geometry types can be added by extending the `parseGeometries` method and implementing
 *   a specific parser for the new type.</li>
 *   <li>Material and light properties are dynamically configurable via JSON.</li>
 *   <li>The class ensures efficient parsing and memory usage for large scenes.</li>
 * </ul>
 *
 * <h2>Error Handling</h2>
 * The parser validates the JSON structure and throws detailed exceptions when invalid configurations
 * are detected. This ensures that users receive immediate feedback on issues like missing required fields,
 * incorrect formatting, or unsupported configurations.
 *
 * @author Benny Avrahami
 */

public class SceneJsonParser extends Scene {

    private static final String BACKGROUND_COLOR = "background-color";
    private static final String AMBIENT_LIGHT = "ambient-light";
    private static final String LIGHTS = "lights";
    private static final String GEOMETRIES = "geometries";
    private static final String MESH = "mesh";

    /**
     * Constructs a {@code SceneJsonParser} instance and parses the scene from the specified file path.
     *
     * @param filePath  the path to the JSON file
     * @param sceneName the name of the scene
     */
    public SceneJsonParser(String filePath, String sceneName) {
        super(sceneName);
        parseScene(filePath);
    }

    /**
     * Parses the JSON scene from the given file path.
     *
     * @param filePath the path to the JSON file
     */
    private void parseScene(String filePath) {
        try {
            String jsonString = Files.readString(Paths.get(filePath));
            Gson gson = new Gson();
            JsonObject root = gson.fromJson(jsonString, JsonObject.class);

            JsonObject sceneJson = validateAndExtract(root, "scene");

            // Parse individual scene elements
            parseBackgroundColor(sceneJson);
            parseAmbientLight(sceneJson);
            parseLights(sceneJson.getAsJsonObject(LIGHTS));
            parseGeometries(sceneJson.getAsJsonObject(GEOMETRIES));

        } catch (IOException e) {
            System.err.println("Error loading JSON file: " + e.getMessage());
        }
    }

    private void parseBackgroundColor(JsonObject sceneJson) {
        if (sceneJson.has(BACKGROUND_COLOR)) {
            this.setBackground(parseColor(sceneJson.get(BACKGROUND_COLOR).getAsString()));
        }
    }

    private void parseAmbientLight(JsonObject sceneJson) {
        if (sceneJson.has(AMBIENT_LIGHT)) {
            JsonObject ambientLightJson = sceneJson.getAsJsonObject(AMBIENT_LIGHT);
            Color color = parseColor(ambientLightJson.get("color").getAsString());
            Double3 kA = ambientLightJson.has("kA") ? parseDouble3(ambientLightJson.get("kA").getAsString()) : Double3.ONE;
            this.setAmbientLight(new AmbientLight(color, kA));
        }
    }

    private void parseLights(JsonObject lightsJson) {
        if (lightsJson == null) return;

        parseLightArray(lightsJson, "spotLight", this::parseSpotLight);
        parseLightArray(lightsJson, "pointLight", this::parsePointLight);
        parseLightArray(lightsJson, "directionalLight", this::parseDirectionalLight);
    }

    private void parseLightArray(JsonObject lightsJson, String key, LightParser parser) {
        if (lightsJson.has(key)) {
            for (JsonElement element : lightsJson.getAsJsonArray(key)) {
                parser.parse(element.getAsJsonObject());
            }
        }
    }

    private void parseSpotLight(JsonObject lightJson) {
        // Parse basic properties
        Color intensity = parseColor(lightJson.get("intensity").getAsString());
        Point position = parsePoint(lightJson.get("position").getAsString());
        Vector direction = parseVector(lightJson.get("direction").getAsString());

        // Create SpotLight instance
        SpotLight spotLight = new SpotLight(intensity, position, direction);

        // Optional: Set radius if provided
        if (lightJson.has("radius")) {
            spotLight.setRadius(lightJson.get("radius").getAsDouble());
        }

        // Optional: Set sampling quality if provided
        if (lightJson.has("sampleQuality")) {
            spotLight.setSamplingQuality(parseQuality(lightJson.get("sampleQuality").getAsString()));
        }

        // Configure attenuation coefficients
        configureLight(spotLight, lightJson);

        // Add the light to the scene's lights
        lights.add(spotLight);
    }

    private void parsePointLight(JsonObject lightJson) {
        // Parse basic properties
        Color intensity = parseColor(lightJson.get("intensity").getAsString());
        Point position = parsePoint(lightJson.get("position").getAsString());

        // Create PointLight instance
        PointLight pointLight = new PointLight(intensity, position);

        // Optional: Set radius if provided
        if (lightJson.has("radius")) {
            pointLight.setRadius(lightJson.get("radius").getAsDouble());
        }

        // Optional: Set sampling quality if provided
        if (lightJson.has("sampleQuality")) {
            pointLight.setSamplingQuality(parseQuality(lightJson.get("sampleQuality").getAsString()));
        }

        // Configure attenuation coefficients
        configureLight(pointLight, lightJson);

        // Add the light to the scene's lights
        lights.add(pointLight);
    }

    private void parseDirectionalLight(JsonObject lightJson) {
        // Parse basic properties
        Color intensity = parseColor(lightJson.get("intensity").getAsString());
        Vector direction = parseVector(lightJson.get("direction").getAsString());

        // Create and add DirectionalLight instance
        lights.add(new DirectionalLight(intensity, direction));
    }

    /**
     * Configures attenuation coefficients for a PointLight or SpotLight.
     *
     * @param light     The light to configure.
     * @param lightJson The JSON object containing the light's properties.
     */
    private void configureLight(PointLight light, JsonObject lightJson) {
        if (lightJson.has("kC")) light.setKc(lightJson.get("kC").getAsDouble());
        if (lightJson.has("kL")) light.setKl(lightJson.get("kL").getAsDouble());
        if (lightJson.has("kQ")) light.setKq(lightJson.get("kQ").getAsDouble());
    }


    private void parseGeometries(JsonObject geometriesJson) {
        if (geometriesJson == null) return;

        parseGeometryArray(geometriesJson, "sphere", this::parseSphere);
        parseGeometryArray(geometriesJson, "triangle", this::parseTriangle);
        parseGeometryArray(geometriesJson, "cylinder", this::parseCylinder);
        parseGeometryArray(geometriesJson, "tube", this::parseTube);
        parseGeometryArray(geometriesJson, "polygon", this::parsePolygon);
        parseGeometryArray(geometriesJson, "mesh", this::parseMesh);
    }

    private void parseGeometryArray(JsonObject geometriesJson, String key, GeometryParser parser) {
        if (geometriesJson.has(key)) {
            for (JsonElement element : geometriesJson.getAsJsonArray(key)) {
                parser.parse(element.getAsJsonObject());
            }
        }
    }

    private void parseSphere(JsonObject sphereJson) {
        Point center = parsePoint(sphereJson.get("center").getAsString());
        double radius = sphereJson.get("radius").getAsDouble();
        Sphere sphere = new Sphere(radius, center);
        if (sphereJson.has("material")) sphere.setMaterial(parseMaterial(sphereJson.getAsJsonObject("material")));
        geometries.add(sphere);
    }

    private void parseTriangle(JsonObject triangleJson) {
        Point p0 = parsePoint(triangleJson.get("p0").getAsString());
        Point p1 = parsePoint(triangleJson.get("p1").getAsString());
        Point p2 = parsePoint(triangleJson.get("p2").getAsString());
        Triangle triangle = new Triangle(p0, p1, p2);
        if (triangleJson.has("material")) triangle.setMaterial(parseMaterial(triangleJson.getAsJsonObject("material")));
        geometries.add(triangle);
    }

    private void parseCylinder(JsonObject cylinderJson) {
        Point base = parsePoint(cylinderJson.get("base").getAsString());
        Vector axis = parseVector(cylinderJson.get("axis").getAsString());
        double radius = cylinderJson.get("radius").getAsDouble();
        double height = cylinderJson.get("height").getAsDouble();
        Cylinder cylinder = new Cylinder(radius, new Ray(base, axis), height);
        if (cylinderJson.has("material")) cylinder.setMaterial(parseMaterial(cylinderJson.getAsJsonObject("material")));
        geometries.add(cylinder);
    }

    private void parseTube(JsonObject tubeJson) {
        Point base = parsePoint(tubeJson.get("base").getAsString());
        Vector axis = parseVector(tubeJson.get("axis").getAsString());
        double radius = tubeJson.get("radius").getAsDouble();
        Tube tube = new Tube(radius, new Ray(base, axis));
        if (tubeJson.has("material")) tube.setMaterial(parseMaterial(tubeJson.getAsJsonObject("material")));
        geometries.add(tube);
    }

    private void parsePolygon(JsonObject polygonJson) {
        JsonArray verticesArray = polygonJson.getAsJsonArray("vertices");
        List<Point> vertices = new ArrayList<>();
        for (JsonElement vertex : verticesArray) {
            vertices.add(parsePoint(vertex.getAsString()));
        }
        Polygon polygon = new Polygon(vertices.toArray(Point[]::new));
        if (polygonJson.has("material")) polygon.setMaterial(parseMaterial(polygonJson.getAsJsonObject("material")));
        geometries.add(polygon);
    }

    private void parseMesh(JsonObject meshJson) {
        String path = meshJson.get("path").getAsString();
        Vector offset = meshJson.has("offset") ? parseVector(meshJson.get("offset").getAsString()) : null;
        double scale = meshJson.has("scale") ? meshJson.get("scale").getAsDouble() : 1;
        Color emissionColor = meshJson.has("emission") ? parseColor(meshJson.get("emission").getAsString()) : null;
        Material material = meshJson.has("material") ? parseMaterial(meshJson.getAsJsonObject("material")) : new Material();
        processStlFile(path, offset, scale, emissionColor, material);
    }

    private Color parseColor(String colorString) {
        String[] components = colorString.split(" ");
        if (components.length == 3) {
            return new Color(
                    Double.parseDouble(components[0]),
                    Double.parseDouble(components[1]),
                    Double.parseDouble(components[2])
            );
        } else if (components.length == 4) {
            Color color = new Color(
                    Double.parseDouble(components[0]),
                    Double.parseDouble(components[1]),
                    Double.parseDouble(components[2])
            );
            double scaleValue = Double.parseDouble(components[3]);
            return scaleValue >= 1 ? color.scale(scaleValue) : color.reduce(1 / scaleValue);
        } else {
            throw new IllegalArgumentException("The Color input must be 3 or 4 numbers");
        }
    }

    private Double3 parseDouble3(String doubleString) {
        String[] components = doubleString.split(" ");
        if (components.length == 3)
            return new Double3(
                    Double.parseDouble(components[0]),
                    Double.parseDouble(components[1]),
                    Double.parseDouble(components[2]));
        else if (components.length == 1)
            return new Double3(Double.parseDouble(components[0]));
        throw new IllegalArgumentException("the Double3 input must be double or a double3");
    }

    private Point parsePoint(String pointString) {
        String[] components = pointString.split(" ");
        return new Point(
                Double.parseDouble(components[0]),
                Double.parseDouble(components[1]),
                Double.parseDouble(components[2])
        );
    }


    private Vector parseVector(String vectorString) {
        String[] components = vectorString.split(" ");
        return new Vector(
                Double.parseDouble(components[0]),
                Double.parseDouble(components[1]),
                Double.parseDouble(components[2])
        );
    }

    private QualityLevel parseQuality(String qualityString) {
        return switch (qualityString) {
            case "low" -> QualityLevel.LOW;
            case "medium" -> QualityLevel.MEDIUM;
            case "high" -> QualityLevel.HIGH;
            case "ultra" -> QualityLevel.ULTRA;
            default -> null;
        };
    }


    private Material parseMaterial(JsonObject materialJson) {
        Material material = new Material();
        if (materialJson.has("kD"))
            material.setKd(parseDouble3(materialJson.get("kD").getAsString()));
        if (materialJson.has("kS"))
            material.setKs(parseDouble3(materialJson.get("kS").getAsString()));
        if (materialJson.has("kT"))
            material.setKt(parseDouble3(materialJson.get("kT").getAsString()));
        if (materialJson.has("kR"))
            material.setKr(parseDouble3(materialJson.get("kR").getAsString()));
        if (materialJson.has("emission"))
            material.setEmission(parseColor(materialJson.get("emission").getAsString()));

        material.setShininess(materialJson.has("shininess") ? materialJson.get("shininess").getAsInt() : 0);

        return material;
    }


    /**
     * Parses an STL file to create triangles and adds them to the scene.
     * This method processes the STL file and constructs geometric triangles
     * using the specified transformations and material properties.
     *
     * @param path          the path to the STL file
     * @param offset        the offset to apply to the mesh vertices (can be null)
     * @param scale         the scale factor to apply to the mesh vertices
     * @param emissionColor the emission color to apply to the triangles (can be null)
     * @param material      the material to apply to the triangles
     */
    private void processStlFile(String path, Vector offset, double scale, Color emissionColor, Material material) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            List<Point> vertices = new ArrayList<>();
            Pattern pattern = Pattern.compile("\\s+");

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("vertex")) {
                    // Parse a vertex line (e.g., "vertex X Y Z")
                    String[] tokens = pattern.split(line);
                    if (tokens.length == 4) {
                        double x = Double.parseDouble(tokens[1]) * scale;
                        double y = Double.parseDouble(tokens[2]) * scale;
                        double z = Double.parseDouble(tokens[3]) * scale;

                        // Apply the offset if it exists
                        Point vertex = new Point(x, y, z);
                        if (offset != null)
                            vertex = vertex.add(offset);

                        vertices.add(vertex);
                    }
                } else if (line.startsWith("endfacet")) {
                    // Create a triangle once three vertices are collected
                    if (vertices.size() == 3) {
                        Point p0 = vertices.get(0);
                        Point p1 = vertices.get(1);
                        Point p2 = vertices.get(2);

                        try {
                            Triangle triangle = new Triangle(p0, p1, p2);

                            // Apply material and emission if specified
                            if (material != null)
                                triangle.setMaterial(material);

                            if (emissionColor != null)
                                triangle.getMaterial().setEmission(emissionColor);

                            this.geometries.add(triangle);
                        } catch (IllegalArgumentException e) {
                            // Skip invalid triangles
                            System.err.println("Invalid triangle ignored: " + e.getMessage());
                        }

                        // Clear vertices for the next triangle
                        vertices.clear();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to process STL file: " + e.getMessage());
        }
    }


    private JsonObject validateAndExtract(JsonObject root, String key) throws IOException {
        if (!root.has(key)) throw new IOException("Missing required key: " + key);
        return root.getAsJsonObject(key);
    }

    @FunctionalInterface
    private interface LightParser {
        void parse(JsonObject json);
    }

    @FunctionalInterface
    private interface GeometryParser {
        void parse(JsonObject json);
    }
}
