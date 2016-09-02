package com.exception.findatutor.conn;

import android.util.Log;

import com.exception.findatutor.Activities.TutorInfoFull;
import com.exception.findatutor.Activities.UserTableInfoFull;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Projections.excludeId;

public class MongoDB extends ConnFields {

    private static MongoDB mongoDB;
    private MongoDatabase mongoDatabase;

    @SuppressWarnings("resource")
    private MongoDB() {
        MongoClientURI mongoClientURI = new MongoClientURI(connectionString);
        MongoClient mongoclient = new MongoClient(mongoClientURI);
        mongoDatabase = mongoclient.getDatabase(mongoClientURI.getDatabase());
    }

    public static MongoDB getInstance() {
        if (mongoDB == null) {
            mongoDB = new MongoDB();
        }
        return mongoDB;
    }

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    public MongoCollection<Document> GetCollection() {
        return mongoDatabase.getCollection(COLLECTION);
    }

    public MongoCollection<Document> GetLocationCollection() {
        return mongoDatabase.getCollection(COLLECTION_LOCATION);
    }

    public void showUsers(ArrayList<String> allUserUsernames, List<TutorInfoFull> alluserInfos) {
        for (Iterator<Document> iterator = ((Iterable<Document>) mongoDatabase.getCollection(COLLECTION).find()).iterator(); iterator.hasNext(); ) {
            Document doc = iterator.next();
            alluserInfos.add(new TutorInfoFull(doc.get("name").toString(), doc.get("edu").toString(), doc.get("edu").toString(), doc.get("courses").toString(),
                    doc.get("fee").toString(), doc.get("from").toString(), doc.get("to").toString(),
                    doc.get("lat").toString(), doc.get("lng").toString(), doc.get("notification").toString()));
            allUserUsernames.add(doc.get("name").toString());
        }
    }

    public void updateLocation(String uname, String lat, String lng) {
        BasicDBObject newDocument = new BasicDBObject();
        newDocument.append("$set", new BasicDBObject().append("lat", lat).append("lng", lng));
        BasicDBObject searchQuery = new BasicDBObject().append("name", uname);
        mongoDatabase.getCollection(COLLECTION_LOCATION).updateOne(searchQuery, newDocument);
    }

    public void UpdateTheTutorNOtification(String uname, String NotificationBoolean, String loginuser) {
        BasicDBObject newDocument = new BasicDBObject();
        newDocument.append("$set", new BasicDBObject().append("sender", loginuser).append("notification", NotificationBoolean));
        BasicDBObject searchQuery = new BasicDBObject().append("name", uname);
        mongoDatabase.getCollection(COLLECTION_TUTORS).updateOne(searchQuery, newDocument);
    }

    public void UpdateTheStudentNOtification(String uname, String NotificationBoolean, String loginuser) {
        BasicDBObject newDocument = new BasicDBObject();
        newDocument.append("$set", new BasicDBObject().append("sender", loginuser).append("notification", NotificationBoolean));
        BasicDBObject searchQuery = new BasicDBObject().append("uname", uname);
        mongoDatabase.getCollection(COLLECTION).updateOne(searchQuery, newDocument);
    }

    public String CheckTutorNotification(String uname, String NotificationBoolean) {
        MongoCursor<Document> mongoCursor = mongoDatabase.getCollection(COLLECTION_TUTORS)
                .find(or(eq("name", uname))).iterator();
        Document elem;
        if (mongoCursor.hasNext()) {
            elem = mongoCursor.next();
            if (((elem.get("name")).toString().equals(uname))) {
                if ((elem.get("notification").toString().equals("true"))) {

                    return "sent";
                }
                if ((elem.get("notification").toString().equals("accepted"))) {

                    return "accepted";
                }
                else {
                    return "notsent";
                }
            }
        }
        return "notsent";
    }

    public String CheckStudentNotification(String uname, String NotificationBoolean) {
        MongoCursor<Document> mongoCursor = mongoDatabase.getCollection(COLLECTION)
                .find(or(eq("uname", uname))).iterator();
        Document elem;
        if (mongoCursor.hasNext()) {
            elem = mongoCursor.next();
            if (((elem.get("uname")).toString().equals(uname))) {
                if ((elem.get("notification").toString().equals("true"))) {

                    return "sent";
                }
                if ((elem.get("notification").toString().equals("accepted"))) {

                    return "accepted";
                }
                else {
                    return "notsent";
                }
            }
        }
        return "notsent";
    }

    public void AddLocationInUsers(String uname, String lat, String lng) {
        MongoCursor<Document> mongoCursor = mongoDatabase.getCollection(COLLECTION_LOCATION)
                .find(or(eq("name", uname))).iterator();
        while (mongoCursor.hasNext()) {
            if (((String) mongoCursor.next().get("name")).equals(uname)) {
                BasicDBObject newDocument = new BasicDBObject();
                BasicDBObject searchQuery = new BasicDBObject().append("name", uname);
                newDocument.append("$set", new BasicDBObject().append("lat", lat).append("lng", lng));
                mongoDatabase.getCollection(COLLECTION_LOCATION).updateOne(searchQuery, newDocument);
                break;
            }
        }
    }

    public String CheckRegisteringAsForNotification(String username) {
        MongoCursor<Document> mongoCursor = mongoDatabase.getCollection(COLLECTION)
                .find(or(eq("uname", username))).iterator();
        Document elem;
        if (mongoCursor.hasNext()) {
            elem = mongoCursor.next();
            if (((elem.get("uname")).toString().equals(username))) {
                if ((elem.get("resgisteringAs").toString().equals("Tutor"))) {

                    return "tutor";
                } else {
                    return "student";
                }
            }
        }
        return "student";
    }

    public String TutorNotificationSenderName(String tutorname){
        String sender = "";
        MongoCursor<Document> dbCursor = mongoDatabase.getCollection(COLLECTION_TUTORS).find().projection(excludeId()).iterator();

        Document elem;
        while (dbCursor.hasNext()) {
            elem = dbCursor.next();
            if (((elem.get("name")).toString().equals(tutorname)))
            sender = elem.get("sender").toString();
        }
        return sender;
    }

    public String StudentNotificationSenderName(String tutorname){
        String sender = "";
        MongoCursor<Document> dbCursor = mongoDatabase.getCollection(COLLECTION).find().projection(excludeId()).iterator();

        Document elem;
        while (dbCursor.hasNext()) {
            elem = dbCursor.next();
            if (((elem.get("uname")).toString().equals(tutorname)))
                sender = elem.get("sender").toString();
        }
        return sender;
    }

    public ArrayList<TutorInfoFull> retrieveTutorDataList() {

        ArrayList<TutorInfoFull> arrayListTutors = new ArrayList<>();
        MongoCursor<Document> dbCursor = mongoDatabase.getCollection(COLLECTION_TUTORS).find().projection(excludeId()).iterator();

        Document elem;
        while (dbCursor.hasNext()) {
            elem = dbCursor.next();
            arrayListTutors.add(new TutorInfoFull(elem.get("name").toString(), elem.get("edu").toString(), elem.get("edu").toString(), elem.get("courses").toString(),
                    elem.get("fee").toString(), elem.get("from").toString(), elem.get("to").toString(),
                    elem.get("lat").toString(), elem.get("lng").toString(), elem.get("notification").toString()));
        }
        return arrayListTutors;
    }

    public ArrayList<String> retrieveUserList() {

        ArrayList<String> arrayListTutors = new ArrayList<>();
        MongoCursor<Document> dbCursor = mongoDatabase.getCollection(COLLECTION_TUTORS).find().projection(excludeId()).iterator();

        Document elem;
        while (dbCursor.hasNext()) {
            elem = dbCursor.next();
            arrayListTutors.add(elem.get("name").toString());
        }
        return arrayListTutors;
    }

    public ArrayList<TutorInfoFull> retrieveOneTutorData(String username) {
        ArrayList<TutorInfoFull> arrayListTutors = new ArrayList<>();
        MongoCursor<Document> dbCursor = mongoDatabase.getCollection(COLLECTION_TUTORS).find(or(eq("name", username))).iterator();

        Document elem;
        if (dbCursor.hasNext()) {
            elem = dbCursor.next();
            if (((String) elem.get("name")).equals(username)) {
                arrayListTutors.add(new TutorInfoFull(elem.get("name").toString(), elem.get("edu").toString(), elem.get("exp").toString(), elem.get("courses").toString(),
                        elem.get("fee").toString(), elem.get("from").toString(), elem.get("to").toString(),
                        elem.get("lat").toString(), elem.get("lng").toString(), elem.get("notification").toString()));
                return arrayListTutors;
            }

        }
        return arrayListTutors;
    }

    public ArrayList<UserTableInfoFull> RetrieveOtherInfoFromUserTable(String username) {
        ArrayList<UserTableInfoFull> arrayListTutors = new ArrayList<>();
        MongoCursor<Document> dbCursor = mongoDatabase.getCollection(COLLECTION).find(or(eq("uname", username))).iterator();

        Document elem;

        if (dbCursor.hasNext()) {
            elem = dbCursor.next();
            if (((String) elem.get("uname")).equals(username)) {
                arrayListTutors.add(new UserTableInfoFull(
                        elem.get("uname").toString(),
                        elem.get("email").toString(),
                        elem.get("phoneno").toString(),
                        elem.get("occupation").toString(),
                        elem.get("city").toString(),
                        elem.get("age").toString()));
            }
        }
        return arrayListTutors;
    }

    public String authenticateRegister(String username, String password, String email, String phoneno, String city, String age,
                                       String occupation, String registeringAs) {
        MongoCursor<Document> mongoCursor = mongoDatabase.getCollection(COLLECTION)
                .find(or(eq("email", email), eq("username", username))).iterator();
        if (mongoCursor.hasNext()) {
            if (((String) mongoCursor.next().get("email")).equals(email)) {
                return "a";
            } else {
                // if (((String) mongoCursor.next().get("username")).equals(username))
                return "b";
            }
        } else {
            /*
             * Fields are unique for registration; proceed insertion
			 */
            mongoDatabase.getCollection(COLLECTION).insertOne(new Document("uname", username)
                    .append("password", password).append("email", email).append("phoneno", phoneno)
                    .append("city", city).append("age", age).append("occupation", occupation).append("resgisteringAs", registeringAs).append("lat", "-1").append("lng", "-1"));
            return "c";
        }
    }

    public String authenticateAddTutor(String username, String name, String edu, String exp, String courses, String fee, String from, String to,
                                       String lat, String lng,String gender) {
        MongoCursor<Document> mongoCursor = mongoDatabase.getCollection(COLLECTION_TUTORS)
                .find(eq("uname", username)).iterator();
        if (mongoCursor.hasNext() && ((String) mongoCursor.next().get("uname")).equals(username)) {
            return "a";
        } else {
            /*
             * Fields are unique for registration; proceed insertion
			 */
            mongoDatabase.getCollection(COLLECTION_TUTORS).insertOne(new Document("uname", username).append("name", name)
                    .append("edu", edu).append("exp", exp).append("courses", courses).append("gender", gender)
                    .append("fee", fee).append("from", from).append("to", to).append("lat", lat).append("lng", lng).append("notification", "false"));
            return "j";
        }

    }


    public Object[] authenticateLogin(String username, String password) {
        Object pair[] = new Object[3];
        Document doc = mongoDatabase.getCollection(COLLECTION)
                .find(eq("uname", username)).first();
        if (doc != null) {
            if (doc.get("password").equals(password)) {
                /*
                 * Fields are authentic; user is authentic. Return true
				 */
                pair[0] = true;
                pair[1] = doc.getString("uname");
                pair[2] = doc.getString("password");


            } else {
                pair[0] = false;
                pair[1] = "b";
            }
        } else {
            pair[0] = false;
            pair[1] = "a";
        }
        return pair;
    }

    public String[] getFromUsers(String username) {
        String registeringAs = "";
        String occupation = "";
        String city = "";
        String phoneno = "";
        String age = "";
        String returnNothing[] = new String[5];
        String returnValue[] = new String[5];
        returnValue[0] = "";

        MongoCursor<Document> mongoCursor = mongoDatabase.getCollection(COLLECTION)
                .find(eq("uname", username)).iterator();


        Document elem;
        if (mongoCursor.hasNext()) {
            elem = mongoCursor.next();
            registeringAs = elem.get("resgisteringAs").toString();
            occupation = elem.get("occupation").toString();
            city = elem.get("city").toString();
            phoneno = elem.get("phoneno").toString();
            age = elem.get("age").toString();

            returnValue[0] = registeringAs;
            returnValue[1] = occupation;
            returnValue[2] = city;
            returnValue[3] = phoneno;
            returnValue[4] = age;
            return returnValue;
        } else {
            return returnValue;
        }


    }


 public ArrayList<String> addtofavourites(String username) {
        ArrayList<String> arrayListTutors = new ArrayList<>();
        MongoCursor<Document> dbCursor = mongoDatabase.getCollection(COLLECTION_TUTORS).find(or(eq("uname", username))).iterator();

        Document elem;
        while (dbCursor.hasNext()) {
            elem = dbCursor.next();
            if ((elem.get("favourite").toString().equals("yes")))
            arrayListTutors.add(elem.get("name").toString());
        }
        return arrayListTutors;
    }

    public ArrayList<String> removefromfavorites(String username) {
        ArrayList<String> arrayListTutors = new ArrayList<>();
        MongoCursor<Document> dbCursor = mongoDatabase.getCollection(COLLECTION_TUTORS).find(or(eq("uname", username))).iterator();

        Document elem;
        while (dbCursor.hasNext()) {
            elem = dbCursor.next();
            if ((elem.get("favourite").toString().equals("no")))
            arrayListTutors.remove(elem.get("name").toString());
        }
        return arrayListTutors;
    }

    public void AddFavourite(String uname, String favourite) {
        MongoCursor<Document> mongoCursor = mongoDatabase.getCollection(COLLECTION_TUTORS)
                .find(or(eq("name", uname))).iterator();
        while (mongoCursor.hasNext()) {
            if (((String) mongoCursor.next().get("name")).equals(uname)) {
                BasicDBObject newDocument = new BasicDBObject();
                BasicDBObject searchQuery = new BasicDBObject().append("name", uname);
                newDocument.append("$set", new BasicDBObject().append("favourite", favourite));
                mongoDatabase.getCollection(COLLECTION_TUTORS).updateOne(searchQuery, newDocument);
                break;
            }
        }
    }
    public void RemoveFavourite(String uname, String favourite) {
        MongoCursor<Document> mongoCursor = mongoDatabase.getCollection(COLLECTION_TUTORS)
                .find(or(eq("name", uname))).iterator();
        while (mongoCursor.hasNext()) {
            if (((String) mongoCursor.next().get("name")).equals(uname)) {
                BasicDBObject newDocument = new BasicDBObject();
                BasicDBObject searchQuery = new BasicDBObject().append("name", uname);
                newDocument.append("$set", new BasicDBObject().append("favourite", favourite));
                mongoDatabase.getCollection(COLLECTION_TUTORS).updateOne(searchQuery, newDocument);
                break;
            }
        }
    }

    public ArrayList<String> retrieveFavouriteList() {

        ArrayList<String> arrayListTutors = new ArrayList<>();
        MongoCursor<Document> dbCursor = mongoDatabase.getCollection(COLLECTION_TUTORS).find().filter(eq("favourite", "yes")).iterator();

        Document elem;
        while (dbCursor.hasNext()) {
            elem = dbCursor.next();
            if ((elem.get("favourite").toString().equals("yes")))
            arrayListTutors.add(elem.get("name").toString());
        }
        return arrayListTutors;
    }

    public ArrayList<String> getTutors() {

        ArrayList<String> arrayListTutors = new ArrayList<>();
        MongoCursor<Document> dbCursor = mongoDatabase.getCollection(COLLECTION_TUTORS).find().projection(excludeId()).iterator();

        Document elem;
        while (dbCursor.hasNext()) {
            elem = dbCursor.next();
            arrayListTutors.add(elem.get("name").toString());
            arrayListTutors.add(elem.get("lat").toString());
            arrayListTutors.add(elem.get("lng").toString());
            arrayListTutors.add(elem.get("gender").toString());
            arrayListTutors.add(elem.get("courses").toString());
        }
        return arrayListTutors;
    }
}
