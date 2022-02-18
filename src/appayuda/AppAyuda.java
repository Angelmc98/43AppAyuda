/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appayuda;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Callback;
import netscape.javascript.JSObject;



/**
 *
 * @author ÁNGEL MEDNINA CANTOS
 */
public class AppAyuda extends Application {
    private Scene scene;
    @Override
    public void start(Stage stage) {
        
        //Creamos la escena
        stage.setTitle("Web View");
        scene = new Scene(new Browser(),750, 500, Color.web("#666970"));
        stage.setScene(scene);
        //scene.getStylesheets().add( AppAyuda.class.getResource("BrowserToolbar.css").toExternalForm());
        //Mostramos
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }

}

    class Browser extends Region 
    {
        private HBox toolBar;
        final private static String[] imageFiles = new String[]
        {"Images/moodle.jpg", 
            "Images/facebook.jpg", 
            "Images/documentation.png", 
            "Images/twitter.jpg",
            "Images/help.png"};
        final private static String[] captions = new String[]{
        "Moodle",
        "Facebook",
        "Topic",
        "Twitter ",
        "Ayuda"
        };
        final private static String[] urls = new String[]{
        "https://moodle.org/?lang=es",
        "https://www.facebook.com/",
        "/fuentes/TopicReservaHabitaciones.html",
        "https://twitter.com/home?lang=es",
        AppAyuda.class.getResource("help.html").toExternalForm()
        };
        
        final ImageView selectedImage = new ImageView();
        final Hyperlink[] hpls = new Hyperlink[captions.length];
        final Image[] images = new Image[imageFiles.length];
       
        
        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();
        
        final Button toggleHelpTopics = new Button("Toggle Help Topics");
        final WebView smallView = new WebView();
        private boolean needDocumentationButton = false;
        
       
        public Browser() 
        {
            //Aplicamos estilos
            getStyleClass().add("browser");
            //Para Tratar los tres enlaces
            for(int i=0; i<captions.length; i++)
            {
                Hyperlink hpl = hpls[i] = new Hyperlink(captions[i]);
                
                String ruta = AppAyuda.class.getResource(imageFiles[i]).toString();
                System.out.println("Ruta: " + ruta);
                //AppAyuda.class.getResourceAsStream(imageFiles[i])
                Image image = images[i] = new Image(ruta);
                
                hpl.setGraphic(new ImageView (image));
                final String url = urls[i];
                final boolean addButton = (hpl.getText().equals("Ayuda"));
                
                //Proces event
                hpl.setOnAction(new EventHandler<ActionEvent>(){
                    @Override
                    public void handle(ActionEvent e) {
                        needDocumentationButton = addButton;
                        webEngine.load(url);
                    }
                });
            }
            //Cargamos la página
            webEngine.load("http://www.ieslosmontecillos.es/");
            
            //Creamos el toolbar
            toolBar = new HBox();
            toolBar.setAlignment(Pos.CENTER);
            toolBar.getStyleClass().add("broswer-toolbar");
            toolBar.getChildren().addAll(hpls);
            toolBar.getChildren().add(createSpacer());
            
            //set action del boton
            toggleHelpTopics.setOnAction(new EventHandler(){
                @Override
                public void handle(Event t) {
                    webEngine.executeScript("toggle_visibility('help_topics')");
                }
            });
            
            smallView.setPrefSize(120,80);
            // Manejo de ventanas emergentes
            webEngine.setCreatePopupHandler(new Callback<PopupFeatures, WebEngine>(){
                @Override
                public WebEngine call(PopupFeatures config) {
                    smallView.setFontScale(0.8);
                    if(!toolBar.getChildren().contains(smallView))
                    {
                        toolBar.getChildren().add(smallView);
                    }
                    return smallView.getEngine();
                }                
            });
            
            //Proceso carga
            webEngine.getLoadWorker().stateProperty().addListener(
            new ChangeListener<State>(){
                @Override
                public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
                    toolBar.getChildren().remove(toggleHelpTopics);
                    if(newState == State.SUCCEEDED){
                        JSObject win = (JSObject) webEngine.executeScript("window");
                        win.setMember("AppAyuda", new JavaApp());
                        if(needDocumentationButton){
                            toolBar.getChildren().add(toggleHelpTopics);
                        }
                    }  
                }
            });
            
            //Cargamos la pagina
            webEngine.load("http://www.ieslosmontecillos.es/");
            
            // Añadimos la vista de la web a la escena
            getChildren().add(toolBar);
            getChildren().add(browser);
        }
        
        //JavaScript interface object
        public class JavaApp{
            public void exit(){
                Platform.exit();
            }
        }
        
        private Node createSpacer() 
        {
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            return spacer;
        }
        
        @Override
        protected void layoutChildren() 
        {
            double w = getWidth();
            double h = getHeight();
            double tbHeight = toolBar.prefHeight(w);
            layoutInArea(browser,0,0,w,h-tbHeight,0, HPos.CENTER, VPos.CENTER);
            layoutInArea(toolBar,0,h-tbHeight,w,tbHeight,0,HPos.CENTER,VPos.CENTER);
        }
        
        @Override
        protected double computePrefWidth(double heigth)
        {
            return 750;
        }
        
        @Override
        protected double computePrefHeight(double width) 
        {
            return 500;
        }
        
    }
    
    
      

