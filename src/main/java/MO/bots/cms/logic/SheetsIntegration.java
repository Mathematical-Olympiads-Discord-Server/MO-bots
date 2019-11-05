package MO.bots.cms.logic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.core.entities.User;

public class SheetsIntegration {
	private static final String APPLICATION_NAME = "Mathematical Olympiad Discord Server Contest Management System";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens";
	
	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
	private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
	
	
    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = SheetsIntegration.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
    
    /**
     * Loads a Contest from a Google Sheet. Includes all timeslots and users. <br>
     * Info sheet: Name | channelID | messageID<br>
     * Timeslot sheet: name | start | end | reactID (everything in Unix time) <br>
     * Users sheet: Username | User ID | timeslot
     * @param spreadsheetId
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public static Contest loadContest(String spreadsheetId, CommandEvent event) throws GeneralSecurityException, IOException {
    	final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    	final String timeslotsRange = "Timeslots!A2:E";
    	final String generalInfoRange = "Info!A2:G";
    	final String usersRange = "Users!A2:C";
    	
    	Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
    			.setApplicationName(APPLICATION_NAME).build();
    	
    	
    	ValueRange generalInfoResponse = service.spreadsheets().values()
    			.get(spreadsheetId, generalInfoRange).execute();
    	List<List<Object>> info = generalInfoResponse.getValues();
    	
    	ValueRange timeslotsResponse = service.spreadsheets().values()
    			.get(spreadsheetId, timeslotsRange).execute();
    	List<List<Object>> timeslots = timeslotsResponse.getValues();
    	
    	ValueRange usersResponse = service.spreadsheets().values()
    			.get(spreadsheetId, usersRange).execute();
    	List<List<Object>> users = usersResponse.getValues();
    	
    	
    	try {
    		List<Object> infoRow = info.get(0);
    		Contest c = new Contest((String) infoRow.get(0),
    				Long.parseLong((String) infoRow.get(1)),
    				Long.parseLong((String) infoRow.get(2)),
    				Long.parseLong((String) infoRow.get(3)),
    				Long.parseLong((String) infoRow.get(4)),
    				(String) infoRow.get(5),
    				Long.parseLong((String) infoRow.get(6)));
    				
    		c.setSpreadsheetId(spreadsheetId);
    		for (List<Object> timeslot : timeslots) {
    			c.addTimeslot((String) timeslot.get(0), Long.parseLong((String) timeslot.get(1)), 
    					Long.parseLong((String) timeslot.get(2)), Long.parseLong((String) timeslot.get(3)));
    		}
    		if (users != null) {
    			for (List<Object> user : users) {
    				c.addContestant(event, (String) user.get(2), Long.parseLong((String) user.get(1)));
    			}
    		}
    		return c;
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw new IllegalArgumentException(e.getMessage());
    	}
    }
    
    /**
     * Appends a single user on to the end of the Users sheet. 
     * @param c the Contest to use 
     * @param u the User to append
     * @param timeslotName the name of the timeslot that the user is
     * sitting the contest in. 
     * @throws IOException 
     * @throws GeneralSecurityException 
     */
    public static void appendUser (Contest c, User u, String timeslotName) throws GeneralSecurityException, IOException {
    	final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    	Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
    			.setApplicationName(APPLICATION_NAME).build();
    	final String usersAppendRange = "Users!A1";
    	
    	List<List<Object>> values = Arrays.asList(
    				Arrays.asList(
    						u.getName() + "#" + u.getDiscriminator(),
    						u.getId(), 
    						timeslotName
    					)
    			);
    	
    	ValueRange body = new ValueRange().setValues(values);
    	AppendValuesResponse result = service.spreadsheets().values()
    			.append(c.getSpreadsheetId(), usersAppendRange, body)
    			.setValueInputOption("USER_ENTERED").execute();
    	System.out.printf("%d cells updated.", result.getUpdates().getUpdatedCells());
    }
    
    /**
     * Saves a contest to a Google Sheet. 
     * @param c The contest to save
     * @throws IOException 
     */
    public static void saveContest(Contest c) throws IOException {
    	if (c.getSpreadsheetId() == null) {
    		throw new IllegalArgumentException("Attempting to save with no spreadsheet ID");
    	}

    	NetHttpTransport HTTP_TRANSPORT = null;
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
			return;
		}
    	Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
    			.setApplicationName(APPLICATION_NAME).build();
    	
    	List<List<Object>> generalInfo = Arrays.asList(
			Arrays.asList(
					c.getName(),
					"" + c.getChannelID(),
					"" + c.getMessageId(),
					"" + c.getRoleId(),
					"" + c.getPcbChannelId(),
					c.getFormLink(),
					"" + c.getStaffMailId()
				)
		);
    	ValueRange infoRange = new ValueRange().setValues(generalInfo).setRange("Info!A2:G");
    	
    	List<List<Object>> timeslots = c.getTimeslotInfoAsList();
    	ValueRange timeslotsRange = new ValueRange().setValues(timeslots).setRange("Timeslots!A2:D");
    	
    	List<List<Object>> users = c.getUserInfoAsList();
    	ValueRange usersRange = new ValueRange().setValues(users).setRange("Users!A2:C");
    	
    	List<ValueRange> data = new ArrayList<ValueRange>(3);
    	data.add(infoRange);
    	data.add(timeslotsRange);
    	data.add(usersRange);
    	
    	BatchUpdateValuesRequest req = new BatchUpdateValuesRequest()
    			.setValueInputOption("USER_ENTERED").setData(data);
    	BatchUpdateValuesResponse response = service.spreadsheets().values()
    			.batchUpdate(c.getSpreadsheetId(), req).execute();
    	System.out.printf("%d cells updated.", response.getTotalUpdatedCells());
    }
}
