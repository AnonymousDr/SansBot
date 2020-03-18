package com.anderhurtado.java.discord.sans;


import com.anderhurtado.java.discord.sans.objetos.Cantante;
import com.anderhurtado.java.discord.sans.util.modelos.VideoBasico;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.SearchResult;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.audio.OpusPacket;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class Inicio{

    public static JDA discord;
    public static final YoutubeAudioSourceManager yasm=new YoutubeAudioSourceManager();
    public static final AudioPlayerManager apm=new DefaultAudioPlayerManager();
    public static Connection c;
    public static final YouTube YT=new YouTube.Builder(new NetHttpTransport(),new JacksonFactory(),new HttpRequestInitializer(){
        @Override
        public void initialize(HttpRequest httpRequest){}
    }).setApplicationName("SansBot").build();
    public static String YoutubeAPIKey;

    public static void main(String[] args)throws Exception{
        if(args.length<6){
            System.out.println("Para iniciar el programa, se deben especificar los siguientes argumentos: <Credencial del bot> <Credencial API de YouTube> <IP de base de datos> <Usuario de la base de datos> <Su contraseña>");
            return;
        }discord=new JDABuilder(AccountType.BOT).setToken(args[0]).build();

        new Thread(()->{
            try{
                do System.out.println("Escriba 's' para finalizar el programa.");
                while(System.in.read()!='s');
                discord.shutdown();
            }catch(Exception Ex){
                Ex.printStackTrace();
            }System.exit(0);
        }).start();

        YoutubeAPIKey=args[1];

        Properties prop=new Properties();
        prop.setProperty("user",args[4]);
        prop.setProperty("password",args[5]);
        prop.setProperty("useSSL","false");
        prop.setProperty("autoReconnect","true");
        Class.forName("com.mysql.jdbc.Driver");
        c=DriverManager.getConnection("jdbc:mysql://"+args[2]+"/"+args[3],prop);
        Statement s=c.createStatement();
        s.executeUpdate("CREATE TABLE IF NOT EXISTS `Registros` (`Nombre` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish2_ci NOT NULL PRIMARY KEY,`Enlace` varchar(16) NOT NULL);");
        s.executeUpdate("CREATE TABLE IF NOT EXISTS Listas (ID int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,Lista varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish2_ci NOT NULL,Cancion varchar(16) NOT NULL,UNIQUE(Lista,Cancion));");

        new Thread(()->{
            while(true){
                try{
                    long t1=System.currentTimeMillis();
                    c.createStatement().executeQuery("SELECT 1;");
                    long t2=System.currentTimeMillis();
                    System.out.println("Ping con la base de datos: "+(t2-t1)+"ms");
                    Thread.sleep(3600000);
                }catch(Exception Ex){
                    Ex.printStackTrace();
                }
            }
        }).start();

        discord.awaitReady();

        discord.addEventListener(new ComandListener());
    }

    public static String getID(String url){
        if(!url.startsWith("https://"))return null;
        url=url.substring(8);
        if(url.startsWith("www."))url=url.substring(4);
        if(url.startsWith("youtube.com/watch?")){
            String[] ds=url.substring(18).split("&");
            for(String s:ds)if(!s.startsWith("v="))continue;
            else return s.split("=",2)[1];
            return null;
        }if(url.startsWith("youtu.be/")){
            url=url.substring(9);
            if(url.contains("?"))return url.split("\\?")[0];
            return url;
        }return null;
    }

    public static AudioTrack[] getYoutubePlayList(String ID){
        try{
            PlaylistItem[] lista=Inicio.YT.playlistItems().list("snippet").setPlaylistId(ID).setMaxResults(50l).setKey(YoutubeAPIKey).execute().getItems().toArray(new PlaylistItem[0]);
            AudioTrack[] resultado=new AudioTrack[lista.length];
            for(int x=0;x<lista.length;x++)resultado[x]=Cantante.getTrack(lista[x].getSnippet().getResourceId().getVideoId());
            return resultado;
        }catch(Exception Ex){
            Ex.printStackTrace();
            return null;
        }
    }

    public static PreparedStatement getPS(String sql)throws Exception{
        return c.prepareStatement(sql);
    }
}
