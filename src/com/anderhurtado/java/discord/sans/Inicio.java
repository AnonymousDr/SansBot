package com.anderhurtado.java.discord.sans;


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
import java.util.Properties;

public class Inicio{

    public static JDA discord;
    public static final YoutubeAudioSourceManager yasm=new YoutubeAudioSourceManager();
    public static final AudioPlayerManager apm=new DefaultAudioPlayerManager();
    public static Connection c;

    public static void main(String[] args)throws Exception{
        if(args.length<5){
            System.out.println("Para iniciar el programa, se deben especificar los siguientes argumentos: <Credencial del bot> <IP de base de datos> <Usuario de la base de datos> <Su contraseña>");
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

        Properties prop=new Properties();
        prop.setProperty("user",args[3]);
        prop.setProperty("password",args[4]);
        prop.setProperty("useSSL","false");
        prop.setProperty("autoReconnect","true");
        Class.forName("com.mysql.jdbc.Driver");
        c=DriverManager.getConnection("jdbc:mysql://"+args[1]+"/"+args[2],prop);
        Statement s=c.createStatement();
        s.executeUpdate("CREATE TABLE IF NOT EXISTS `Registros` (`Nombre` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish2_ci NOT NULL PRIMARY KEY,`Enlace` varchar(16) NOT NULL);");

        discord.awaitReady();

        discord.addEventListener(new ComandListener());

        /*VoiceChannel vc=discord.getVoiceChannelById("583323010069561349");
        AudioManager am=vc.getGuild().getAudioManager();
        am.openAudioConnection(vc);
        YoutubeAudioTrack ai=(YoutubeAudioTrack)yasm.loadTrackWithVideoId("VIDEO-ID",true);
        am.setSendingHandler(new Audio(apm.createPlayer(),am,ai));*/
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

    public static PreparedStatement getPS(String sql)throws Exception{
        return c.prepareStatement(sql);
    }
}
