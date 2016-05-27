package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entity.Missing;
import entity.Photo;
import entity.User;
import exception.UnknownServerException;
import facade.MissingPeopleFacade;
import facade.UserFacade;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jose4j.base64url.internal.apache.commons.codec.binary.Base64;

@Path("missing")
public class MissingRest {

    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").setPrettyPrinting().create();
    MissingPeopleFacade mpFacade = new MissingPeopleFacade();
    UserFacade userFacade = new UserFacade();

    @Context
    private UriInfo context;

    public MissingRest() {
    }

    @GET
    @Produces("application/json")
    @Path("/all")
    public Response getAllMissingPeople() throws SQLException {
        JsonObject response = new JsonObject();
        JsonArray jsonArr = new JsonArray();

        List<Missing> missingList = mpFacade.getAll();
        
        for (Missing missingPerson : missingList) {
            JsonObject singlePerson = new JsonObject();
            
            String jsonImg = "";
            byte[] imgByteArr;
            
            if(missingPerson.getImage() != null) {
                jsonImg = Base64.encodeBase64String(missingPerson.getImage().getImg());
            }
            
            singlePerson.addProperty("Id", missingPerson.getId());
            singlePerson.addProperty("Name", missingPerson.getNameOfMissingPerson());
            singlePerson.addProperty("Description", missingPerson.getDescription());
            singlePerson.addProperty("DateOfMissing", missingPerson.getDateOfMissing().toString());
            singlePerson.addProperty("GeoPosition", missingPerson.getGeoPosition());
            singlePerson.addProperty("Photo", jsonImg);
            
            JsonArray followers = new JsonArray();
            JsonArray volunteers = new JsonArray();
            
            for(User usr : missingPerson.getFollowers()) {
                JsonObject follower = new JsonObject();
                
                follower.addProperty("id", usr.getId());
                follower.addProperty("email", usr.getEmail());
                follower.addProperty("firstname", usr.getFirstname());
                follower.addProperty("lastname", usr.getLastname());
                followers.add(follower);
            }
            for(User vol : missingPerson.getVolenteers()) {
                JsonObject volunteer = new JsonObject();
            
                volunteer.addProperty("Id", vol.getId());
                volunteer.addProperty("Email", vol.getEmail());
                volunteer.addProperty("Firstname", vol.getFirstname());
                volunteer.addProperty("Lastname", vol.getLastname());
            
                volunteers.add(volunteer);
            }
            singlePerson.add("Followers", followers);
            singlePerson.add("Volunteers", volunteers);

            jsonArr.add(singlePerson);
        }
        return Response.ok(gson.toJson(jsonArr), MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Produces("application/json")
    @Consumes("application/json")
    @Path("/create")
    public Response createNewSearch(String content) throws SQLException, IOException {
        JsonObject json = new JsonParser().parse(content).getAsJsonObject();

        System.out.println("## JSON ##");
        System.out.println(json);
        String[] imgArr = json.get("file").toString().split(",");
        byte[] imageBytes = Base64.decodeBase64(imgArr[1]);
        
        //Blob img = new javax.sql.rowset.serial.SerialBlob(imageBytes);
        Photo photoOfMissingPerson = new Photo();
        photoOfMissingPerson.setImg(imageBytes);
        photoOfMissingPerson.setImgType(imgArr[0]);
        Missing newSearch = gson.fromJson(content, Missing.class);

        newSearch.setImage(photoOfMissingPerson);
        
//        DateFormat dateForUpload = new SimpleDateFormat("dd-MM-yyyy'T'HH:mmZ");
//        String dateAsStr = dateForUpload.format(new Date());
//        BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
//        System.out.println("#### " + img);
//        File f = new File("/images/missingpeople/"+dateAsStr+"-"+newSearch.getId());
//        ImageIO.write(img, "png", f);

        return Response.ok(gson.toJson(mpFacade.createSearch(newSearch)), MediaType.APPLICATION_JSON).build();
    }
    
    
    // REST Call to follow a Missing.
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    @Path("/follow")
    public Response followMissingPerson(String content) throws UnknownServerException {
        JsonObject jsonGet = new JsonParser().parse(content).getAsJsonObject();
        JsonObject response = new JsonObject();
        
        String userId = jsonGet.get("uid").getAsString();
        String missingId = jsonGet.get("sid").getAsString();
        
        if(userId.equals("") || missingId.equals("") ) {
            throw new UnknownServerException("Something went wrong");
        }
        
        User user = userFacade.getUser(Long.parseLong(userId));
        if(user == null) {
            throw new UnknownServerException("Something went wrong");
        }
        
        Missing newMissing = mpFacade.followMissing(missingId, user);
        
        String jsonImg = "";
        byte[] imgByteArr;
            
        if(newMissing.getImage() != null) {
            jsonImg = Base64.encodeBase64String(newMissing.getImage().getImg());
        }
        
        response.addProperty("Id", newMissing.getId());
        response.addProperty("Name", newMissing.getNameOfMissingPerson());
        response.addProperty("Description", newMissing.getDescription());
        response.addProperty("DateOfMissing", newMissing.getDateOfMissing().toString());
        response.addProperty("GeoPosition", newMissing.getGeoPosition());
        response.addProperty("Photo", jsonImg);
        
        JsonArray followers = new JsonArray();
        JsonArray volunteers = new JsonArray();
        for(User usr : newMissing.getFollowers()) {
            JsonObject follower = new JsonObject();
            
            follower.addProperty("Id", usr.getId());
            follower.addProperty("Email", usr.getEmail());
            follower.addProperty("Firstname", usr.getFirstname());
            follower.addProperty("Lastname", usr.getLastname());
            
            followers.add(follower);
        }
        for(User vol : newMissing.getVolenteers()) {
            JsonObject volunteer = new JsonObject();
            
            volunteer.addProperty("Id", vol.getId());
            volunteer.addProperty("Email", vol.getEmail());
            volunteer.addProperty("Firstname", vol.getFirstname());
            volunteer.addProperty("Lastname", vol.getLastname());
            
            volunteers.add(volunteer);
        }
        response.add("Followers", followers);
        response.add("Volunteers", volunteers);
        
        return Response.ok(gson.toJson(response), MediaType.APPLICATION_JSON).build();
    }
    
    // REST to UnFollow Missing
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    @Path("/unfollow")
    public Response unFollowMissingPerson(String content) throws UnknownServerException {
        JsonObject jsonGet = new JsonParser().parse(content).getAsJsonObject();
        JsonObject response = new JsonObject();
        
        String userId = jsonGet.get("uid").getAsString();
        String missingId = jsonGet.get("sid").getAsString();
        
        if(userId.equals("") || missingId.equals("")) {
            throw new UnknownServerException("Something went wrong");
        }
        
        User user = userFacade.getUser(Long.parseLong(userId));
        if(user == null) {
            throw new UnknownServerException("Something went wrong");
        }
        
        Missing newMissing = mpFacade.unfollowMissing(missingId, user);
        
        String jsonImg = "";
        byte[] imgByteArr;
            
        if(newMissing.getImage() != null) {
            jsonImg = Base64.encodeBase64String(newMissing.getImage().getImg());
        }
        
        response.addProperty("Id", newMissing.getId());
        response.addProperty("Name", newMissing.getNameOfMissingPerson());
        response.addProperty("Description", newMissing.getDescription());
        response.addProperty("DateOfMissing", newMissing.getDateOfMissing().toString());
        response.addProperty("GeoPosition", newMissing.getGeoPosition());
        response.addProperty("Photo", jsonImg);
        
        JsonArray followers = new JsonArray();
        JsonArray volunteers = new JsonArray();
        
        // Listing followers to jsonarray 
        for(User usr : newMissing.getFollowers()) {
            JsonObject follower = new JsonObject();
            
            follower.addProperty("id", usr.getId());
            follower.addProperty("email", usr.getEmail());
            follower.addProperty("firstname", usr.getFirstname());
            follower.addProperty("lastname", usr.getLastname());
            
            followers.add(follower);
        }
        
        // Listing volunteers to jsonarray
        for(User vol : newMissing.getVolenteers()) {
            JsonObject volunteer = new JsonObject();
            
            volunteer.addProperty("Id", vol.getId());
            volunteer.addProperty("Email", vol.getEmail());
            volunteer.addProperty("Firstname", vol.getFirstname());
            volunteer.addProperty("Lastname", vol.getLastname());
            
            volunteers.add(volunteer);
        }
        response.add("Followers", followers);
        response.add("Volunteers", volunteers);
        
        return Response.ok(gson.toJson(response), MediaType.APPLICATION_JSON).build();
    }

    
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    @Path("/volunteer")
    public Response volunteerMissingPerson(String content) throws UnknownServerException {
        JsonObject jsonGet = new JsonParser().parse(content).getAsJsonObject();
        JsonObject response = new JsonObject();
        
        
        String userId = jsonGet.get("uid").getAsString();
        String missingId = jsonGet.get("sid").getAsString();
        
        if(userId.equals("") || missingId.equals("") ) {
            throw new UnknownServerException("Something went wrong");
        }
        
        User user = userFacade.getUser(Long.parseLong(userId));
        if(user == null) {
            throw new UnknownServerException("Something went wrong");
        }
        
        Missing newMissing = mpFacade.volunteerMissing(missingId, user);
        
        String jsonImg = "";
        byte[] imgByteArr;
            
        if(newMissing.getImage() != null) {
            jsonImg = Base64.encodeBase64String(newMissing.getImage().getImg());
        }
        
        response.addProperty("Id", newMissing.getId());
        response.addProperty("Name", newMissing.getNameOfMissingPerson());
        response.addProperty("Description", newMissing.getDescription());
        response.addProperty("DateOfMissing", newMissing.getDateOfMissing().toString());
        response.addProperty("GeoPosition", newMissing.getGeoPosition());
        response.addProperty("Photo", jsonImg);
        
        JsonArray followers = new JsonArray();
        JsonArray volunteers = new JsonArray();
        
        // Listing followers to jsonarray 
        for(User usr : newMissing.getFollowers()) {
            JsonObject follower = new JsonObject();
            
            follower.addProperty("id", usr.getId());
            follower.addProperty("email", usr.getEmail());
            follower.addProperty("firstname", usr.getFirstname());
            follower.addProperty("lastname", usr.getLastname());
            
            followers.add(follower);
        }
        
        // Listing volunteers to jsonarray
        for(User vol : newMissing.getVolenteers()) {
            JsonObject volunteer = new JsonObject();
            
            volunteer.addProperty("Id", vol.getId());
            volunteer.addProperty("Email", vol.getEmail());
            volunteer.addProperty("Firstname", vol.getFirstname());
            volunteer.addProperty("Lastname", vol.getLastname());
            
            volunteers.add(volunteer);
        }
        response.add("Followers", followers);
        response.add("Volunteers", volunteers);
        
        return Response.ok(gson.toJson(response), MediaType.APPLICATION_JSON).build();
    }
    
    // REST to UnVolunteer for Missing
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    @Path("/unfollow")
    public Response unVolunteerMissingPerson(String content) throws UnknownServerException {
        JsonObject jsonGet = new JsonParser().parse(content).getAsJsonObject();
        JsonObject response = new JsonObject();
        
        String userId = jsonGet.get("uid").getAsString();
        String missingId = jsonGet.get("sid").getAsString();
        
        if(userId.equals("") || missingId.equals("")) {
            throw new UnknownServerException("Something went wrong");
        }
        
        User user = userFacade.getUser(Long.parseLong(userId));
        if(user == null) {
            throw new UnknownServerException("Something went wrong");
        }
        
        Missing newMissing = mpFacade.unfollowMissing(missingId, user);
        
        String jsonImg = "";
        byte[] imgByteArr;
            
        if(newMissing.getImage() != null) {
            jsonImg = Base64.encodeBase64String(newMissing.getImage().getImg());
        }
        
        response.addProperty("Id", newMissing.getId());
        response.addProperty("Name", newMissing.getNameOfMissingPerson());
        response.addProperty("Description", newMissing.getDescription());
        response.addProperty("DateOfMissing", newMissing.getDateOfMissing().toString());
        response.addProperty("GeoPosition", newMissing.getGeoPosition());
        response.addProperty("Photo", jsonImg);
        
        JsonArray followers = new JsonArray();
        JsonArray volunteers = new JsonArray();
        
        // Listing followers to jsonarray 
        for(User usr : newMissing.getFollowers()) {
            JsonObject follower = new JsonObject();
            
            follower.addProperty("id", usr.getId());
            follower.addProperty("email", usr.getEmail());
            follower.addProperty("firstname", usr.getFirstname());
            follower.addProperty("lastname", usr.getLastname());
            
            followers.add(follower);
        }
        
        // Listing volunteers to jsonarray
        for(User vol : newMissing.getVolenteers()) {
            JsonObject volunteer = new JsonObject();
            
            volunteer.addProperty("Id", vol.getId());
            volunteer.addProperty("Email", vol.getEmail());
            volunteer.addProperty("Firstname", vol.getFirstname());
            volunteer.addProperty("Lastname", vol.getLastname());
            
            volunteers.add(volunteer);
        }
        response.add("Followers", followers);
        response.add("Volunteers", volunteers);
        
        return Response.ok(gson.toJson(response), MediaType.APPLICATION_JSON).build();
    }
}
