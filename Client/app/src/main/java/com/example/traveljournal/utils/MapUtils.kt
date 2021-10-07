package com.example.traveljournal.utils

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.data.geojson.GeoJsonFeature
import com.google.maps.android.data.geojson.GeoJsonMultiPolygon
import com.google.maps.android.data.geojson.GeoJsonPolygon

class MapUtils {

    private fun  getCoordinatesFromGeometry(geometry: GeoJsonMultiPolygon): MutableList<LatLng> {
        val coordinates: MutableList<LatLng> = ArrayList()
        var polygons: List<GeoJsonPolygon> = geometry.polygons
        for (polygon: GeoJsonPolygon in polygons) {
            for (coords:List<LatLng> in polygon.coordinates) {
                coordinates.addAll(coords)
            }
        }
        return coordinates
    }

    fun getLatLngBoundingBox(feature: GeoJsonFeature): LatLngBounds {
        val coordinates: MutableList<LatLng> = ArrayList()

        coordinates.addAll(getCoordinatesFromGeometry(feature.geometry as GeoJsonMultiPolygon))
        var builder: LatLngBounds.Builder = LatLngBounds.builder()

        for(latLng: LatLng in coordinates) {
            builder.include(latLng)
        }
        var boundingBox: LatLngBounds = builder.build()
        return boundingBox
    }
}