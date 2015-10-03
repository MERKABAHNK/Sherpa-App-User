package com.fr3estudio.sherpa.sherpav3p.utils;

public class CONSTANTS {
	
	public static final int MENU_OPTION = 0;
	public static final int CLOSE_SESSION = 1;
	public static final String CLOSE_SESSION_ST = "closeSession";

	public static final int CONFIRMATION_OVARLAY = 100;
	public static final int CANCEL_CONFIRMATION_OVARLAY = 101;
	public static final int MENU_OVARLAY = 102;
	public static final String INSERT_SERVICE = "INSERT_SERVICE";



	//tiempo en milisegundos.
	
	public static int TIP_INCREMENT = 1000;
	public static int TIME_BETWEEN_SERVICEA_CALLS = 20000;
	public static int TIME_BETWEEN_CALLS_LONG = 60000;
	
	public static int TIME_BETWEEN_ROUTE_UPDATE_CALLS = 180000;
	public static String contact_mail = "atencionalcliente@sherpa.com";
	
	
	public static final String initialZone = "New York, USA";
	public static final String queryServer = "http://sherpa.city/webs/android/sherpa_query.php";
	
	public static final String remPassServer = "http://sherpa.city/webs/android/recordar_passwd_sherpa.php";
	public static final String avatarServer = "http://sherpa.city/webs/fotos_sherpas/";
	public static final String googleMapService = "http://maps.google.com/maps/api/geocode/";
	public static final String googleDistService = "https://maps.googleapis.com/maps/api/directions/";
	
	public static final int retry_count = 10;
	public static final int time_out = 2000;

	public static final String ACTION_GEOCODE = "GEOCODE";
	public static final String ACTION_RGEOCODE = "RGEOCODE";
	public static final String ACTION_DMATRIX = "DMATRIX";

	public static final String PACKAGE_NAME =
			"com.fr3estudio.sherpa.sherpav3p";
	public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
	public static final String RESULT_DATA_KEY = PACKAGE_NAME +
			".RESULT_DATA_KEY";
	public static final String RESULT_POS_KEY = PACKAGE_NAME +
			".RESULT_POS_KEY";
	public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
			".LOCATION_DATA_EXTRA";
	public static final String ADDRESS_DATA_EXTRA = PACKAGE_NAME +
			".ADDRESS_DATA_EXTRA";
	public static final String CALLER = PACKAGE_NAME +
			".CALLER";
	public static final String CALLER_INDEX = PACKAGE_NAME +
			".CALLER_INDEX";
	public static final int SUCCESS_RESULT = 0;
	public static final int FAILURE_RESULT = 1;

	public static final String DESTINATIONS_NAME = PACKAGE_NAME + "DESTINATIONS";
	public static final String ID_CLIENT = PACKAGE_NAME + "ID_CLIENT";
	public static final String ID_SERVICE = PACKAGE_NAME + "ID_SERVICE";
	public static final String ID_SHERPA = PACKAGE_NAME + "ID_SHERPA";
}
