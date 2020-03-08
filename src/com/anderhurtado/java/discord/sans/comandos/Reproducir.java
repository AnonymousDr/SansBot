package com.anderhurtado.java.discord.sans.comandos;

import com.anderhurtado.java.discord.sans.Inicio;
import com.anderhurtado.java.discord.sans.objetos.Cantante;
import com.anderhurtado.java.discord.sans.objetos.Comando;
import com.anderhurtado.java.discord.sans.util.Biblioteca;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Reproducir extends Comando{

    public Reproducir(){
        super("Reproducir una canción","reproducir","cantar","canta","reproduce","play");
    }

    @Override
    public void ejecuta(GuildMessageReceivedEvent e,String[] args){
        if(args.length<2){
            e.getChannel().sendMessage("Uso: "+args[0]+" <Alias o enlace de la canción>").complete();
            return;
        }String cancion,ID,pref=null;
        if(args.length>2&&args[1].startsWith("-")){
            pref=args[1].toLowerCase();
            cancion=args[2];
        }else cancion=args[1];
        if(cancion.startsWith("https://"))ID=Inicio.getID(cancion);
        else ID=Biblioteca.getID(cancion);
        if(ID==null){
            e.getChannel().sendMessage("No se ha podido localizar la canción.").complete();
            return;
        }Member m=e.getMember();
        GuildVoiceState gvs;
        if(m==null||(gvs=m.getVoiceState())==null){
            e.getChannel().sendMessage("Error interno.").complete();
            return;
        }VoiceChannel vc;
        if(!gvs.inVoiceChannel()||(vc=gvs.getChannel())==null){
            e.getChannel().sendMessage("Diría que no estás conectado a ningún canal de voz. ¿Puede que no tenga acceso a él?").complete();
            return;
        }if(!Cantante.puedeCantar(vc)){
            e.getChannel().sendMessage("Parece ser que no puedo cantar en ese canal ahora.").complete();
            return;
        }YoutubeAudioTrack yat=Cantante.getTrack(ID);
        if(yat==null){
            e.getChannel().sendMessage("No se ha podido encontrar el vídeo en Youtube.").complete();
            return;
        }AudioTrackInfo ati=yat.getInfo();
        if(ati.isStream){
            e.getChannel().sendMessage("Lo siento, no reproduzco vídeos en directo.").complete();
            return;
        }if(pref!=null){
            Cantante c=Cantante.getCantante(e.getGuild());
            if(c!=null)switch(pref){
            case "-s":
                c.cantar(yat,0);
                c.saltar();
                e.getChannel().sendMessage("¡Ya empieza la canción!").complete();
                return;
            case "-0":
                AudioTrack at=c.getCancion();
                c.cantar(yat,0);
                long rest=at.getDuration()-at.getPosition();
                rest/=1000;
                long mins=rest/60,secs=rest%60;
                e.getChannel().sendMessage("La canción comenzará en "+(mins==0?"":mins+" "+(mins==1?"minuto":"minutos")+(secs==0?"":" y "))+(secs==0?"":secs+" "+(secs==1?"segundo":"segundos"))+".").complete();
                return;
            case "-?":
                e.getChannel().sendMessage("-0 -> Establecer como siguiente canción.\n-s -> Cantar ahora.").complete();
                return;
            }
        }long tiempo=Cantante.cantar(vc,ID);
        if(tiempo<0){
            e.getChannel().sendMessage("Error interno.").complete();
            return;
        }tiempo/=1000;
        if(tiempo==0)e.getChannel().sendMessage("¡La canción comenzará ahora!").complete();
        else{
            int mins=(int)tiempo/60,secs=(int)tiempo%60;
            e.getChannel().sendMessage("La canción comenzará en "+(mins==0?"":mins+" "+(mins==1?"minuto":"minutos")+(secs==0?"":" y "))+(secs==0?"":secs+" "+(secs==1?"segundo":"segundos"))+".").complete();
        }
    }
}
