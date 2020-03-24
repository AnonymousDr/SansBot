package com.anderhurtado.java.discord.sans.comandos;

import com.anderhurtado.java.discord.sans.objetos.Boton;
import com.anderhurtado.java.discord.sans.objetos.Cantante;
import com.anderhurtado.java.discord.sans.objetos.Comando;
import com.anderhurtado.java.discord.sans.objetos.Paginacion;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class Cola extends Comando{

    public Cola(){
        super("Ver la lista de canciones","cola","lista","playlist");
    }

    @Override
    public void ejecuta(GuildMessageReceivedEvent e,String[] args){
        Cantante c=Cantante.getCantante(e.getGuild());
        if(c==null){
            e.getChannel().sendMessage("¡No estoy cantando nada aquí! ¿Karaoke? "+Boton.MICROFONO_NOTAS).complete();
            return;
        }int pag=0;
        if(args.length>1)try{
            pag=Math.max(0,Integer.parseInt(args[1])-1);
        }catch(Exception Ex){}
        mostrarCanciones(e.getChannel(),pag);
    }

    public static void mostrarCanciones(TextChannel tc,int pag){
        Cantante c=Cantante.getCantante(tc.getGuild());
        if(c==null)return;
        AudioTrack[] ats=c.getPlayList();
        int ini=pag*10;
        if(ini>=ats.length){
            tc.sendMessage("No hay más canciones para mostrar.").complete();
            return;
        }String txt="";
        int lim=Math.min(ini+10,ats.length);
        while(ini<lim){
            AudioTrackInfo at=ats[ini].getInfo();
            String nombre=at.title; //Forzar a 38 caracteres
            if(nombre.length()>38)nombre=nombre.substring(0,38)+"…";
            else while(nombre.length()<38)nombre+="\t\f";
            txt+=++ini+") "+nombre+" ";
            long dur=at.length/1000,min=dur/60,secs=dur%60;
            txt+=String.format("%02d",min)+":"+String.format("%02d",secs)+"\n";
        }txt+="\nPágina "+(pag+1)+"/"+(ats.length/10+1)+" · "+ats.length+" canciones en total.";
        Message msg=tc.sendMessage("```nimrod\n"+txt+"```").complete();
        new Paginacion(msg,(pag>0?Boton.ATRAS:null),(lim<ats.length?Boton.SEGUIR:null)){
            @Override
            public void evento(GuildMessageReactionAddEvent e,Boton b){
                mostrarCanciones(tc,pag+(b.equals(Boton.ATRAS)?-1:1));
            }
        };
    }
}
