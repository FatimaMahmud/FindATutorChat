package com.exception.findatutor.conn;

public class ConnFields {

    private static final String USERNAME = "tutor";
    private static final String PASSWORD = "tutor";
    private static final String DB_ADDRESS = ".mlab.com";
    private static final int PORT = 21895;
    protected static final String DB_NAME = "find_tutor";
    protected static final String COLLECTION = "users";
    protected static final String COLLECTION_LOCATION = "location";
    protected static final String COLLECTION_TUTORS = "tutors";
    protected static final String connectionString = "mongodb://" + USERNAME + ":" + PASSWORD + "@ds0" + PORT
            + DB_ADDRESS + ":" + PORT + "/" + DB_NAME;


    public ConnFields() {
        super();
    }
}