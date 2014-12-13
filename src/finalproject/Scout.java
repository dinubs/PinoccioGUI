/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finalproject;

// Pinoccio API
import io.pinocc.pinocico.java.*;
import com.google.gson.*;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;

/**
 *
 * @author Gavin
 */
public abstract class Scout {

    public String troopId;
    public String scoutId;
    public String name;
    public String token;
    public Boolean lead;
    public PinoccioAPI pinoccioApi = new PinoccioAPI();
    
    public Scout(String troopId, String scoutId, String name, String token) {
        this.troopId = troopId;
        this.scoutId = scoutId;
        this.token = token;
        this.name = name;
    }
    public Scout(String troopId, String scoutId, String name, String token, Boolean lead) {
        this.troopId = troopId;
        this.scoutId = scoutId;
        this.token = token;
        this.name = name;
        this.lead = true;
    }
    
    public void sendCommand(String command) {
        try {
            pinoccioApi.runBitlashCommand(Integer.parseInt(this.troopId), Integer.parseInt(this.scoutId), command, token);
        } catch (Exception ex) {
            
        }
    }
    
}
