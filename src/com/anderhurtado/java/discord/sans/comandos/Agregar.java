package com.anderhurtado.java.discord.sans.comandos;

import com.anderhurtado.java.discord.sans.Inicio;
import com.anderhurtado.java.discord.sans.objetos.Boton;
import com.anderhurtado.java.discord.sans.objetos.Cantante;
import com.anderhurtado.java.discord.sans.objetos.Comando;
import com.anderhurtado.java.discord.sans.util.Biblioteca;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Agregar extends Comando{

    public Agregar(){
        super("Añadir un alias a una canción","agregar","añadir","registrar","add");
    }

    @Override
    public void ejecuta(GuildMessageReceivedEvent e,String[] args){
        if(args.length<3){
            e.getChannel().sendMessage("Sintaxis: "+args[0]+" <Enlace de Youtube> <Alias>").complete();
            return;
        }String ID=Inicio.getID(args[1]);
        if(ID==null){
            e.getChannel().sendMessage(Boton.CRUZ_ROJA+" No se ha podido recoger el identificador del vídeo del enlace.").complete();
            return;
        }YoutubeAudioTrack yat=Cantante.getTrack(ID);
        if(yat==null){
            e.getChannel().sendMessage(Boton.CRUZ_ROJA+" No se ha podido encontrar el vídeo en Youtube.").complete();
            return;
        }AudioTrackInfo ati=yat.getInfo();
        if(ati.isStream){
            e.getChannel().sendMessage("Lo siento, no reproduzco vídeos en directo "+Boton.ENFADADO).complete();
            return;
        }String[] alias=new String[args.length-2];
        for(int x=2;x<args.length;x++)alias[x-2]=args[x];
        String[] ok=Biblioteca.add(ID,alias);
        if(ok==null)e.getChannel().sendMessage("Hubo un error al conectar con la base de datos.").complete();
        else if(ok.length==alias.length)e.getChannel().sendMessage("¡Se "+(ok.length==1?"ha asignado dicho":"han asignado dichos")+" alias a esa canción con éxito!").complete();
        else if(ok.length==0)e.getChannel().sendMessage("No se ha podido registrar ninguna canción.").complete();
        else{
            String mal="";
            general:
            for(String s:alias){
                for(String o:ok)if(s.equals(o))continue general;
                mal+=(mal.isEmpty()?"":", ")+s;
            }e.getChannel().sendMessage("No se han podido enviar las siguientes canciones: "+mal+". El resto de alias han sido registrados.").complete();
        }
    }
}
