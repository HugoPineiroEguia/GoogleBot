// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// [START drive_quickstart]
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;

import javax.swing.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

/* class to demonstarte use of Drive files list API */
public class DriveQuickstart {
    /** Application name. */
    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /** Directory to store authorization tokens for this application. */
    private static final String TOKENS_DIRECTORY_PATH = "resources";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     *
     * En el ejemplo original esta readonly metadatos, por lo tanto si lo dejamos asi
     * no podremos descargar ficheros, solo listarlos
     */
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = DriveQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
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
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("736476175694-qnbtukpdshk30equ1rl43fb20g8fabsn.apps.googleusercontent.com");
        //returns an authorized Credential object.
        return credential;
    }

    public static void main(String... args) throws IOException, GeneralSecurityException {

        final String token = "AQUI METES EL TOKEN DE DISCORD";
        final DiscordClient client = DiscordClient.create(token);
        final GatewayDiscordClient gateway = client.login().block();


        //Este comando coge un video al azar de una carpeta del pc
        gateway.on(MessageCreateEvent.class).subscribe(event -> {
           final Message message = event.getMessage();
           if("/UwU".equals((message.getContent()))){

               int random = (int) (Math.random()*(8)+1);
               System.out.println(random);
               String clip = "";

               switch (random){
                   case 1:
                       clip = "C:\\Users\\pinha\\Documents\\DiscordBot_Archivos\\Lake.mp4";
                   break;
                   case 2:
                       clip = "C:\\Users\\pinha\\Documents\\DiscordBot_Archivos\\Sand_Bill_Tower.mp4";
                   break;
                   case 3:
                       clip = "C:\\Users\\pinha\\Documents\\DiscordBot_Archivos\\Ice_Picture.mp4";
                       break;
                   case 4:
                       clip = "C:\\Users\\pinha\\Documents\\DiscordBot_Archivos\\Ice_BlowJob.mp4";
                       break;
                   case 5:
                       clip = "C:\\Users\\pinha\\Documents\\DiscordBot_Archivos\\Gorro_Mtropolitano.mp4";
                       break;
                   case 6:
                       clip = "C:\\Users\\pinha\\Documents\\DiscordBot_Archivos\\Goomba_Redwood.mp4";
                       break;
                   case 7:
                       clip = "C:\\Users\\pinha\\Documents\\DiscordBot_Archivos\\Sand_Double_Bill.mp4";
                       break;
                   case 8:
                       clip = "C:\\Users\\pinha\\Documents\\DiscordBot_Archivos\\Gorro_MetropolitanoAlto.mp4";
                       break;


               }

               try {
                   InputStream Lake = new FileInputStream(clip);
                   final MessageChannel channel = message.getChannel().block();
                   channel.createMessage("+++++UwU+++++").block();
                   channel.createMessage(MessageCreateSpec.builder()
                           .addFile("UwU.mp4", Lake)
                           .build()).subscribe();

               }            catch (FileNotFoundException e) {
                   e.printStackTrace();
               }












           }
        });

        //Este es el comando que se mete en el Drive y descarga el archivo para despues mandarlo por Discord
        gateway.on(MessageCreateEvent.class).subscribe(event -> { //Crea un evento.
            final Message message = event.getMessage(); // Lee lo que ponemos.
            if ("/trickjum".equals(message.getContent())) { //Comprueba si el mensaje es "!ping".
            try {


                // Build a new authorized API client service.
                final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
                Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                        .setApplicationName(APPLICATION_NAME)
                        .build();
                // Filtra para encontrar la carpeta que se llama imagenesBot
                FileList result = service.files().list()
                        .setQ("name contains 'imagenesBot' and mimeType = 'application/vnd.google-apps.folder'")
                        .setPageSize(100)
                        .setSpaces("drive")
                        .setFields("nextPageToken, files(id, name)")
                        .execute();
                List<File> files = result.getFiles();

                if (files == null || files.isEmpty()) {
                    System.out.println("No files found.");
                } else {
                    String dirImagenes = null;
                    System.out.println("Files:");
                    for (File file : files) {
                        System.out.printf("%s (%s)\n", file.getName(), file.getId());
                        dirImagenes = file.getId();
                    }
                    // busco la imagen en el directorio
                    FileList resultImagenes = service.files().list()
                            .setQ("name contains 'Lake.mp4' and parents in '"+dirImagenes+"'")
                            .setSpaces("drive")
                            .setFields("nextPageToken, files(id, name)")
                            .execute();
                    List<File> filesImagenes = resultImagenes.getFiles();
                    for (File file : filesImagenes) {
                        System.out.printf("Imagen: %s\n", file.getName());
                        // guardamos el 'stream' en el fichero aux.jpeg qieune qe existir
                        OutputStream outputStream = new FileOutputStream("C:\\Users\\pinha\\Pictures\\Drive\\base.mp4");
                        service.files().get(file.getId())
                                .executeMediaAndDownloadTo(outputStream);
                        outputStream.flush();
                        outputStream.close();
                    }
                }
            } catch (Exception e){

            }

                InputStream fileAsInputStream = null;
                try {
                    fileAsInputStream = new FileInputStream("C:\\Users\\pinha\\Pictures\\Drive\\base.mp4");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                InputStream finalFileAsInputStream = fileAsInputStream;
            final MessageChannel channel = message.getChannel().block(); // Crea un onjetp MessageChannel.
                channel.createMessage("Archivo descargado:").block(); //Nos devuelve "pong!".
                channel.createMessage(MessageCreateSpec.builder()
                        .addFile("base.mp4", finalFileAsInputStream)
                        .build()).subscribe();

            }
        });
        gateway.onDisconnect().block();




    }
}
// [END drive_quickstart]