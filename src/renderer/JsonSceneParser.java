package renderer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import geometries.Sphere;
import geometries.Triangle;
import lighting.AmbientLight;
import primitives.Color;
import primitives.Double3;
import primitives.Point;
import scene.Scene;


public class JsonSceneParser {
    public Scene scene = new Scene("");

    public JsonSceneParser(String filePath) {
        try {
            // Read the file content into a string
            String jsonString = new String(Files.readAllBytes(Paths.get(filePath)));

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
            JsonObject sceneJson = jsonObject.getAsJsonObject("scene");

            // Parse background color
            String[] bgColorComponents = sceneJson.get("background-color").getAsString().split(" ");
            Color backgroundColor = new Color(
                    Integer.parseInt(bgColorComponents[0]),
                    Integer.parseInt(bgColorComponents[1]),
                    Integer.parseInt(bgColorComponents[2])
            );

            // Parse ambient light
            JsonObject ambientLightJson = sceneJson.getAsJsonObject("ambient-light");
            String[] ambientLightColorComponents = ambientLightJson.get("color").getAsString().split(" ");
            Color ambientLightColor = new Color(
                    Integer.parseInt(ambientLightColorComponents[0]),
                    Integer.parseInt(ambientLightColorComponents[1]),
                    Integer.parseInt(ambientLightColorComponents[2])
            );
            AmbientLight ambientLight = new AmbientLight(ambientLightColor, Double3.ONE);

            // Create scene
            scene = new Scene(filePath);
            scene.setBackground(backgroundColor);
            scene.setAmbientLight(ambientLight);

            // Parse geometries
            JsonObject geometriesJson = sceneJson.getAsJsonObject("geometries");

            // Parse sphere
            JsonElement sphereElement = geometriesJson.get("sphere");
            if (sphereElement.isJsonArray()) {
                JsonArray spheresArray = sphereElement.getAsJsonArray();
                for (JsonElement element : spheresArray) {
                    JsonObject sphereJson = element.getAsJsonObject();
                    String[] sphereCenterComponents = sphereJson.get("center").getAsString().split(" ");
                    Point sphereCenter = new Point(
                            Double.parseDouble(sphereCenterComponents[0]),
                            Double.parseDouble(sphereCenterComponents[1]),
                            Double.parseDouble(sphereCenterComponents[2])
                    );
                    double sphereRadius = sphereJson.get("radius").getAsDouble();
                    Sphere sphere = new Sphere(sphereRadius, sphereCenter);
                    scene.geometries.add(sphere);
                }
            } else {
                JsonObject sphereJson = sphereElement.getAsJsonObject();
                String[] sphereCenterComponents = sphereJson.get("center").getAsString().split(" ");
                Point sphereCenter = new Point(
                        Double.parseDouble(sphereCenterComponents[0]),
                        Double.parseDouble(sphereCenterComponents[1]),
                        Double.parseDouble(sphereCenterComponents[2])
                );
                double sphereRadius = sphereJson.get("radius").getAsDouble();
                Sphere sphere = new Sphere(sphereRadius, sphereCenter);
                scene.geometries.add(sphere);
            }


            // Parse triangles
            JsonArray trianglesJson = geometriesJson.getAsJsonArray("triangle");
            for (int i = 0; i < trianglesJson.size(); i++) {
                JsonObject triangleJson = trianglesJson.get(i).getAsJsonObject();
                String[] p0Components = triangleJson.get("p0").getAsString().split(" ");
                Point p0 = new Point(
                        Double.parseDouble(p0Components[0]),
                        Double.parseDouble(p0Components[1]),
                        Double.parseDouble(p0Components[2])
                );
                String[] p1Components = triangleJson.get("p1").getAsString().split(" ");
                Point p1 = new Point(
                        Double.parseDouble(p1Components[0]),
                        Double.parseDouble(p1Components[1]),
                        Double.parseDouble(p1Components[2])
                );
                String[] p2Components = triangleJson.get("p2").getAsString().split(" ");
                Point p2 = new Point(
                        Double.parseDouble(p2Components[0]),
                        Double.parseDouble(p2Components[1]),
                        Double.parseDouble(p2Components[2])
                );
                Triangle triangle = new Triangle(p0, p1, p2);
                scene.geometries.add(triangle);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
