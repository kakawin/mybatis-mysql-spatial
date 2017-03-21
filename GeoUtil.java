
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

public class GeoUtil {
	private static GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

	private static WKTReader wktReader = new WKTReader(geometryFactory);

	private static WKTWriter wktWriter = new WKTWriter();

	private static WKBReader wkbReader = new WKBReader(geometryFactory);

	private static WKBWriter wkbWriter = new WKBWriter();

	/**
	 * SIRD:4326
	 * 
	 * @return
	 */
	public static GeometryFactory getFactory() {
		if (geometryFactory == null) {
			geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
		}
		return geometryFactory;
	}

	public static GeometryFactory getFactory(int srid) {
		return new GeometryFactory(new PrecisionModel(), srid);
	}

	public static WKTReader getWktReader() {
		if (wktReader == null) {
			wktReader = new WKTReader(getFactory());
		}
		return wktReader;
	}

	public static WKTWriter getWktWriter() {
		if (wktWriter == null) {
			wktWriter = new WKTWriter();
		}
		return wktWriter;
	}

	public static WKBReader getWkbReader() {
		if (wkbReader == null) {
			wkbReader = new WKBReader(getFactory());
		}
		return wkbReader;
	}

	public static WKBWriter getWkbWriter() {
		if (wkbWriter == null) {
			wkbWriter = new WKBWriter();
		}
		return wkbWriter;
	}

	public static Polygon getExtent(String coords) {
		String[] split = coords.split(",");
		double minX = Double.parseDouble(split[0]);
		double minY = Double.parseDouble(split[1]);
		double maxX = Double.parseDouble(split[2]);
		double maxY = Double.parseDouble(split[3]);
		Coordinate[] coordinates = new Coordinate[5];
		coordinates[0] = new Coordinate(minX, minY);
		coordinates[1] = new Coordinate(minX, maxY);
		coordinates[2] = new Coordinate(maxX, maxY);
		coordinates[3] = new Coordinate(maxX, minY);
		coordinates[4] = new Coordinate(minX, minY);
		return getFactory().createPolygon(coordinates);
	}

	public static Point getPoint(String coords) {
		String[] split = coords.split(",");
		double x = Double.parseDouble(split[0]);
		double y = Double.parseDouble(split[1]);
		return getFactory().createPoint(new Coordinate(x, y));
	}
}
