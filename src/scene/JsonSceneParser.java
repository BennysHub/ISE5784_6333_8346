package scene;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import geometries.Sphere;
import geometries.Triangle;
import lighting.AmbientLight;
import primitives.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Parses a JSON file to create a scene with geometries and materials.
 */
public class JsonSceneParser {
    /**
     * The scene created from the JSON file.
     */
    public Scene scene;

    /**
     * Constructs a JsonSceneParser instance and parses the scene from the specified file path.
     *
     * @param filePath the path to the JSON file
     */
    public JsonSceneParser(String filePath) {
        try {
            // Read the file content into a string
            String jsonString = new String(Files.readAllBytes(Paths.get(filePath)));

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
            JsonObject sceneJson = jsonObject.getAsJsonObject("scene");

            // Create scene
            scene = new Scene(filePath);

            // Parse background color
            if (sceneJson.has("background-color")) {
                Color backgroundColor = parseColor(sceneJson.get("background-color").getAsString());
                scene.setBackground(backgroundColor);
            }

            // Parse ambient light
            if (sceneJson.has("ambient-light")) {
                JsonObject ambientLightJson = sceneJson.getAsJsonObject("ambient-light");
                Color ambientLightColor = parseColor(ambientLightJson.get("color").getAsString());
                Double3 kA = ambientLightJson.has("kA") ?
                        new Double3(Double.parseDouble((ambientLightJson.get("kA").getAsString())))
                        : Double3.ONE;

                AmbientLight ambientLight = new AmbientLight(ambientLightColor, kA);
                scene.setAmbientLight(ambientLight);
            }

            // Parse geometries
            JsonObject geometriesJson = sceneJson.getAsJsonObject("geometries");
            parseGeometries(geometriesJson.get("sphere"), Sphere.class);
            parseGeometries(geometriesJson.get("triangle"), Triangle.class);

            // Parse meshes
            parseMeshes(geometriesJson.get("mesh"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses a Double3 from a string in the format "D D D".
     *
     * @param doubleString the json string
     * @return the parsed Double3 object
     */
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

    /**
     * Parses a Double3 from a string in the format "R G B".
     *
     * @param colorString the json string
     * @return the parsed Color object
     */
    private Color parseColor(String colorString) {
        String[] components = colorString.split(" ");
        if (components.length == 3)
            return new Color(
                    Double.parseDouble(components[0]),
                    Double.parseDouble(components[1]),
                    Double.parseDouble(components[2]));
        throw new IllegalArgumentException("the Color input must be 3 numbers");
    }

    /**
     * Parses a point from a string in the format "X Y Z".
     *
     * @param pointString the point string
     * @return the parsed Point object
     */
    private Point parsePoint(String pointString) {
        String[] components = pointString.split(" ");
        return new Point(
                Double.parseDouble(components[0]),
                Double.parseDouble(components[1]),
                Double.parseDouble(components[2])
        );
    }

    /**
     * Parses geometries from a JSON element and adds them to the scene.
     *
     * @param geometryElement the JSON element containing the geometries
     * @param geometryClass   the class of the geometry type (e.g., Sphere. Class, Triangle. Class)
     * @param <T>             the type of the geometry
     */
    private <T> void parseGeometries(JsonElement geometryElement, Class<T> geometryClass) {
        if (geometryElement == null) return;
        if (geometryElement.isJsonArray()) {
            JsonArray geometriesArray = geometryElement.getAsJsonArray();
            for (JsonElement element : geometriesArray) {
                parseAndAddGeometry(element.getAsJsonObject(), geometryClass);
            }
        } else if (geometryElement.isJsonObject()) {
            parseAndAddGeometry(geometryElement.getAsJsonObject(), geometryClass);
        }
    }

    /**
     * Parses and adds a geometry to the scene based on its JSON representation.
     *
     * @param geometryJson  the JSON object representing the geometry
     * @param geometryClass the class of the geometry type
     */
    private void parseAndAddGeometry(JsonObject geometryJson, Class<?> geometryClass) {
        if (geometryClass == Sphere.class) {
            parseAndAddSphere(geometryJson);
        } else if (geometryClass == Triangle.class) {
            parseAndAddTriangle(geometryJson);
        }
    }

    /**
     * Parses and adds a sphere to the scene based on its JSON representation.
     *
     * @param sphereJson the JSON object representing the sphere
     */
    private void parseAndAddSphere(JsonObject sphereJson) {
        Point sphereCenter = parsePoint(sphereJson.get("center").getAsString());
        double sphereRadius = sphereJson.get("radius").getAsDouble();
        Sphere sphere = new Sphere(sphereRadius, sphereCenter);

        if (sphereJson.has("emission")) {
            Color emissionColor = parseColor(sphereJson.get("emission").getAsString());
            sphere.setEmission(emissionColor);
        }

        if (sphereJson.has("material")) {
            sphere.setMaterial(parseMaterial(sphereJson.getAsJsonObject("material")));
        }

        scene.geometries.add(sphere);
    }

    /**
     * Parses and adds a triangle to the scene based on its JSON representation.
     *
     * @param triangleJson the JSON object representing the triangle
     */
    private void parseAndAddTriangle(JsonObject triangleJson) {
        Point p0 = parsePoint(triangleJson.get("p0").getAsString());
        Point p1 = parsePoint(triangleJson.get("p1").getAsString());
        Point p2 = parsePoint(triangleJson.get("p2").getAsString());
        Triangle triangle = new Triangle(p0, p1, p2);

        if (triangleJson.has("emission")) {
            Color emissionColor = parseColor(triangleJson.get("emission").getAsString());
            triangle.setEmission(emissionColor);
        }

        if (triangleJson.has("material")) {
            triangle.setMaterial(parseMaterial(triangleJson.getAsJsonObject("material")));
        }

        scene.geometries.add(triangle);
    }

    /**
     * Parses meshes from a JSON element and adds them to the scene.
     *
     * @param meshElement the JSON element containing the meshes
     */
    private void parseMeshes(JsonElement meshElement) {
        if (meshElement == null) return;
        if (meshElement.isJsonArray()) {
            JsonArray meshesArray = meshElement.getAsJsonArray();
            for (JsonElement element : meshesArray) {
                JsonObject meshJson = element.getAsJsonObject();
                String path = meshJson.get("path").getAsString();
                Point offset = meshJson.has("offset") ? parsePoint(meshJson.get("offset").getAsString()) : null;
                double scale = meshJson.has("scale") ? Double.parseDouble((meshJson.get("scale").getAsString())) : 1;
                Color emissionColor = meshJson.has("emission") ? parseColor(meshJson.get("emission").getAsString()) : null;
                Material material = meshJson.has("material") ? parseMaterial(meshJson.getAsJsonObject("material")) : new Material();
                // Call the STL parser with the path, emission, and material
                parseSTLMesh(path, offset, scale, emissionColor, material);
            }
        }
    }

    /**
     * Parses an STL file to create and add triangles to the scene.
     *
     * @param path          the path to the STL file
     * @param offset        the offset to apply to the mesh vertices (can be null)
     * @param scale         the scale factor to apply to the mesh vertices
     * @param emissionColor the emission color to apply to the triangles (can be null)
     * @param material      the material to apply to the triangles
     */
    private void parseSTLMesh(String path, Point offset, double scale, Color emissionColor, Material material) {
        Vector vOffset = null;
        if (offset != null)
            vOffset = new Vector(offset.getX(), offset.getY(), offset.getZ());

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            List<Point> vertices = new ArrayList<>();
            Pattern pattern = Pattern.compile("\\s+");

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("vertex")) {
                    String[] tokens = pattern.split(line.trim());
                    if (tokens.length == 4) {
                        double x = Double.parseDouble(tokens[1]) * scale;
                        double y = Double.parseDouble(tokens[2]) * scale;
                        double z = Double.parseDouble(tokens[3]) * scale;
                        vertices.add(new Point(x, y, z));
                    }
                } else if (line.startsWith("endfacet")) {
                    if (vertices.size() >= 3) {
                        Point p0 = vOffset == null ? vertices.get(0) : vertices.get(0).add(vOffset);
                        Point p1 = vOffset == null ? vertices.get(1) : vertices.get(1).add(vOffset);
                        Point p2 = vOffset == null ? vertices.get(2) : vertices.get(2).add(vOffset);
                        Triangle triangle = new Triangle(p0, p1, p2);

                        if (emissionColor != null) {
                            triangle.setEmission(emissionColor);
                        }
                        if (material != null) {
                            triangle.setMaterial(material);
                        }
                        scene.geometries.add(triangle);
                        vertices.clear();
                    }
                }
            }

            // Add all triangles to the scene

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses a material from a JSON object.
     *
     * @param materialJson the JSON object representing the material
     * @return the parsed Material object
     */
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
        material.setShininess(materialJson.has("shininess") ? materialJson.get("shininess").getAsInt() : 0);

        return material;
    }
}
