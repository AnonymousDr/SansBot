package com.anderhurtado.java.discord.sans.comandos;

import com.anderhurtado.java.discord.sans.objetos.Cantante;
import com.anderhurtado.java.discord.sans.objetos.Comando;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class Cantando extends Comando{

    public static final String BARRA="\u25AC",POSICION="\uD83D\uDD18";
    public static final byte BARRAS=25;

    public Cantando(){
        super("Ver la canción que se está cantando ahora","cantando","nombre","cancion","?","wtf","wtf?","what","what?");
    }

    @Override
    public void ejecuta(GuildMessageReceivedEvent e,String[] args){
        Cantante c=Cantante.getCantante(e.getGuild());
        AudioTrack at;
        if(c==null||(at=c.getCancion())==null){
            e.getChannel().sendMessage("¡No estoy cantando nada aquí! ¿Karaoke?").complete();
            return;
        }EmbedBuilder eb=new EmbedBuilder();
        eb.setColor(Color.cyan);
        AudioTrackInfo ati=at.getInfo();
        eb.setAuthor(ati.author);
        eb.setTitle(ati.title,ati.uri);
        if(at instanceof YoutubeAudioTrack)eb.setImage("https://img.youtube.com/vi/"+ati.identifier+"/maxresdefault.jpg");
        long act=at.getPosition(),dur=at.getDuration();
        byte pos=(byte)((BARRAS)*at.getPosition()/at.getDuration());
        String barra="";
        for(byte x=0;x<BARRAS;x++)barra+=x==pos?POSICION:BARRA;
        act/=1000;
        dur/=1000;
        String tiempo=String.format("%02d",act/60)+":"+String.format("%02d",act%60)+"/"+String.format("%02d",dur/60)+":"+String.format("%02d",dur%60);
        eb.setDescription(barra+"\nDuración: "+tiempo);
        e.getChannel().sendMessage(eb.build()).complete();
    }
}
