package geometries;

import primitives.Point;
import primitives.Ray;

import java.util.List;

public class Geometries implements Intersectable{

    private final List<Intersectable> geometries = null;

    public Geometries(){}

    public Geometries(Intersectable... geometries){
        add(geometries);
    }

    public void add(Intersectable... geometries){

    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        return List.of();
    }
}
