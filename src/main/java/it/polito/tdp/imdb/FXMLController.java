/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.imdb;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

import it.polito.tdp.imdb.model.Actor;
import it.polito.tdp.imdb.model.Model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model = new Model();

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnSimili"
    private Button btnSimili; // Value injected by FXMLLoader

    @FXML // fx:id="btnSimulazione"
    private Button btnSimulazione; // Value injected by FXMLLoader

    @FXML // fx:id="boxGenere"
    private ComboBox<String> boxGenere; // Value injected by FXMLLoader

    @FXML // fx:id="boxAttore"
    private ComboBox<Actor> boxAttore; // Value injected by FXMLLoader

    @FXML // fx:id="txtGiorni"
    private TextField txtGiorni; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doAttoriSimili(ActionEvent event) {
    	Set<Actor> attori = this.model.getAttoriSimili(this.boxAttore.getValue());
    	String res = "";
    	for (Actor a: attori) {
    		res += a + "\n";
    	}
    	this.txtResult.setText(res);
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	model.creaGrafo(this.boxGenere.getValue());
    	this.boxAttore.setItems(FXCollections.observableArrayList(this.model.attoriNelGrafo()));
    }

    @FXML
    void doSimulazione(ActionEvent event) {
    	String res = "";
    	int numDays;
    	try {
    		numDays = Integer.parseInt(this.txtGiorni.getText());
    	} catch (NumberFormatException e){
    		e.printStackTrace();
    		return;
    	}
    	this.model.setSimulation(numDays);
    	this.model.runSimulation();
    	int countPausa = 0;
    	for (Actor a: this.model.getActorsCasted()) {
    		if (a == null) {
    			countPausa++;
    		} else {
    			res += a + "\n";
    		}
    	}
    	this.txtResult.setText(res);
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnSimili != null : "fx:id=\"btnSimili\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnSimulazione != null : "fx:id=\"btnSimulazione\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxGenere != null : "fx:id=\"boxGenere\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxAttore != null : "fx:id=\"boxAttore\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtGiorni != null : "fx:id=\"txtGiorni\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
        this.boxGenere.setItems(FXCollections.observableArrayList(this.model.getGenres()));
    }
    
    public void setModel(Model model) {
    	this.model = model;
    }
}
