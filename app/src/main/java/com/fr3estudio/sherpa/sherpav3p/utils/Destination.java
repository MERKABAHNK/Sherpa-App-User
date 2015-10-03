package com.fr3estudio.sherpa.sherpav3p.utils;

import java.io.Serializable;

public class Destination implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6967245371101686219L;

	
	public double latitude;
	public double longitude;
	public String address;
	public boolean isOrigin;
	
	public double total_length;
	public double total_price;
	
	public String service_detail;
	
	public String token;
	
	public Destination(double lat, double lon, String addr, String detail, double len) {
		latitude = lat;
		longitude = lon;
		address = addr;
		isOrigin = false;
		service_detail = detail;
		total_length = len;
		token = "";
	}
	public Destination(double lat, double lon, String addr, boolean origin) {
		latitude = lat;
		longitude = lon;
		address = addr;
		isOrigin = origin;
		token = "";
	}
	public Destination(double lat, double lon, String addr, boolean origin, double ttl_len, double ttl_prc, String detail) {
		latitude = lat;
		longitude = lon;
		address = addr;
		isOrigin = origin;
		total_length = ttl_len;
		total_price = ttl_prc;
		service_detail = detail;
		token = "";
	}
	
	@Override
	public String toString(){
		return ((isOrigin)?"Origen\n":"Destino\n")+"lat: "+latitude+"\nlon: "+longitude+"\n addre: "+address;
	}
}
