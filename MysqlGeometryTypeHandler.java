import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ByteOrderValues;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;

@MappedTypes({ Geometry.class, Point.class, Polygon.class, LineString.class, LinearRing.class, MultiPoint.class,
		MultiPolygon.class, MultiLineString.class })
public class MysqlGeometryTypeHandler extends BaseTypeHandler<Geometry> {

	@Override
	public Geometry getNullableResult(ResultSet paramResultSet, String paramString) throws SQLException {
		byte[] bytes = paramResultSet.getBytes(paramString);
		return fromMysqlWkb(bytes);
	}

	@Override
	public Geometry getNullableResult(ResultSet paramResultSet, int paramInt) throws SQLException {
		byte[] bytes = paramResultSet.getBytes(paramInt);
		return fromMysqlWkb(bytes);
	}

	@Override
	public Geometry getNullableResult(CallableStatement paramCallableStatement, int paramInt) throws SQLException {
		byte[] bytes = paramCallableStatement.getBytes(paramInt);
		return fromMysqlWkb(bytes);
	}

	@Override
	public void setNonNullParameter(PreparedStatement paramPreparedStatement, int paramInt, Geometry paramT,
			JdbcType paramJdbcType) throws SQLException {
		byte[] bytes = mysqlWkbFrom(paramT);
		paramPreparedStatement.setBytes(paramInt, bytes);
	}

	private Geometry fromMysqlWkb(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		try {
			ByteBuffer sridBuffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).put(bytes, 0, 4);
			sridBuffer.position(0);
			int srid = sridBuffer.getInt();
			byte[] geomBytes = ByteBuffer.allocate(bytes.length - 4).order(ByteOrder.LITTLE_ENDIAN)
					.put(bytes, 4, bytes.length - 4).array();
			if (GeoUtil.getFactory().getSRID() != srid) {
				return new WKBReader(GeoUtil.getFactory(srid)).read(geomBytes);
			}
			return GeoUtil.getWkbReader().read(geomBytes);
		} catch (Exception e) {
		}
		return null;
	}

	private byte[] mysqlWkbFrom(Geometry geometry) {
		int srid = geometry.getSRID();
		byte[] bytes = new WKBWriter(2, ByteOrderValues.LITTLE_ENDIAN).write(geometry);
		return ByteBuffer.allocate(bytes.length + 4).order(ByteOrder.LITTLE_ENDIAN).putInt(srid).put(bytes).array();
	}
}
