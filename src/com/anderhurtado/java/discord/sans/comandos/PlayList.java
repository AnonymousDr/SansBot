package com.anderhurtado.java.discord.sans.comandos;

import com.anderhurtado.java.discord.sans.objetos.Boton;
import com.anderhurtado.java.discord.sans.objetos.Cantante;
import com.anderhurtado.java.discord.sans.objetos.Comando;
import com.anderhurtado.java.discord.sans.util.Biblioteca;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayList extends Comando{

    public PlayList(){
        super("Cantar una playlist","playlist");
    }

    @Override
    public void ejecuta(GuildMessageReceivedEvent e,String[] args){
        if(args.length<2){
            e.getChannel().sendMessage(Boton.NUBE_BOCADILLO+" Uso: "+args[0]+" <Lista>").complete();
            return;
        }GuildVoiceState gvs=e.getMember().getVoiceState();
        VoiceChannel vc;
        if(gvs==null||((vc=gvs.getChannel())==null)){
            e.getChannel().sendMessage(Boton.CRUZ_ROJA+" ¡No estás en ningún canal de voz!").complete();
            return;
        }boolean aleatorio=false;
        String lista=args[1];
        if(args.length>2&&lista.equals("-?")){
            aleatorio=true;
            lista=args[2];
        }String[] canciones=Biblioteca.getCancionesListadas(lista,aleatorio);
        if(canciones.length==0){
            e.getChannel().sendMessage(Boton.CRUZ_ROJA+" ¡La lista no existe! Tal vez sea un fantasma... "+Boton.FANTASMA).complete();
            return;
        }List<AudioTrack> atsl=new ArrayList<>();
        for(String cancion:canciones){
            AudioTrack at=Cantante.getTrack(cancion);
            if(at==null)continue;
            atsl.add(at);
        }AudioTrack[] ats=atsl.toArray(new AudioTrack[0]);
        if(ats.length!=canciones.length){
            e.getChannel().sendMessage(Boton.CRUZ_ROJA+" "+(ats.length==0?"¡No se pudo cargar ninguna canción!":"¡No se pudieron cargar algunas canciones!")).complete();
            return;
        }for(AudioTrack at:ats)Cantante.cantar(vc,at);
        e.getChannel().sendMessage(Boton.NOTAS_MUSICALES+" ¡Hora de cantar!").complete();
    }
}
