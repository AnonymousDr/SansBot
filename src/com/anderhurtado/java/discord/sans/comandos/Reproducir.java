package com.anderhurtado.java.discord.sans.comandos;

import com.anderhurtado.java.discord.sans.Inicio;
import com.anderhurtado.java.discord.sans.objetos.Boton;
import com.anderhurtado.java.discord.sans.objetos.Cantante;
import com.anderhurtado.java.discord.sans.objetos.Comando;
import com.anderhurtado.java.discord.sans.util.Biblioteca;
import com.anderhurtado.java.discord.sans.util.modelos.VideoBasico;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nullable;

public class Reproducir extends Comando{

    public Reproducir(){
        super("Reproducir una canción","reproducir","cantar","canta","reproduce","play");
    }

    @Override
    public void ejecuta(GuildMessageReceivedEvent e,String[] args){
        if(args.length<2){
            e.getChannel().sendMessage("Uso: "+args[0]+" <Alias o enlace de la canción>").complete();
            return;
        }String cancion,pref=null;
        if(args.length>2&&args[1].startsWith("-")&&!args[1].equals("-b")){
            pref=args[1].toLowerCase();
            cancion=args[2];
        }else cancion=args[1];
        AudioTrack[] ats;
        e.getMessage().addReaction(Boton.CD.CODIGO).complete();
        if(cancion.equals("-b")&&args.length>2){
            String q=args[2];
            for(int x=3;x<args.length;x++)q+=" "+args[x];
            VideoBasico[] vbs=VideoBasico.buscar(q,1);
            if(vbs==null){
                e.getChannel().sendMessage(Boton.CRUZ_ROJA+" Error interno.").complete();
                return;
            }if(vbs.length==0){
                e.getChannel().sendMessage(Boton.ADVERTENCIA+" 0 resultados encontrados. "+Boton.OVNI).complete();
                return;
            }ats=new AudioTrack[]{vbs[0].getTrack()};
            e.getChannel().sendMessage(Boton.LUPA+" He encontrado esta canción: `"+vbs[0].titulo+"`").complete();
        }else ats=Biblioteca.procesarTodo(cancion);
        if(ats==null){
            e.getChannel().sendMessage(Boton.CRUZ_ROJA+" No se ha podido localizar la canción.").complete();
            System.out.println("!");
            return;
        }reproducir(e.getChannel(),e.getMember(),pref,ats);
    }

    public static boolean reproducir(TextChannel tc,Member m,@Nullable String pref,AudioTrack... ats){
        GuildVoiceState gvs;
        if(m==null||(gvs=m.getVoiceState())==null){
            tc.sendMessage(Boton.CRUZ_ROJA+" Error interno.").complete();
            return false;
        }VoiceChannel vc;
        if(!gvs.inVoiceChannel()||(vc=gvs.getChannel())==null){
            tc.sendMessage(Boton.OVNI+" Diría que no estás conectado a ningún canal de voz. ¿Puede que no tenga acceso a él?").complete();
            return false;
        }if(!Cantante.puedeCantar(vc)){
            tc.sendMessage(Boton.CRUZ_ROJA+" Parece ser que no puedo cantar en ese canal ahora.").complete();
            return false;
        }if(ats==null||ats.length==0||ats[0]==null){
            tc.sendMessage(Boton.CRUZ_ROJA+" No se ha podido encontrar el vídeo en Youtube.").complete();
            return false;
        }AudioTrackInfo ati=ats[0].getInfo();
        if(ati.isStream){
            tc.sendMessage(Boton.CRUZ_ROJA+" Lo siento, no reproduzco vídeos en directo.").complete();
            return false;
        }if(pref!=null){
            Cantante c=Cantante.getCantante(tc.getGuild());
            if(c!=null)switch(pref){
            case "-s":
                for(int x=ats.length-1;x>=0;x--)c.cantar(ats[x],0);
                c.saltar();
                tc.sendMessage(Boton.NOTA_MUSICAL+" ¡Ya empieza la canción!").complete();
                return true;
            case "-0":
                AudioTrack at=c.getCancion();
                for(int x=ats.length-1;x>=0;x--)c.cantar(ats[x],0);
                long rest=at.getDuration()-at.getPosition();
                rest/=1000;
                long mins=rest/60,secs=rest%60;
                tc.sendMessage("La canción comenzará en "+(mins==0?"":mins+" "+(mins==1?"minuto":"minutos")+(secs==0?"":" y "))+(secs==0?"":secs+" "+(secs==1?"segundo":"segundos"))+".").complete();
                return true;
            case "-?":
                tc.sendMessage("-0 -> Establecer como siguiente canción.\n-s -> Cantar ahora.").complete();
                return true;
            }
        }long tiempo=Cantante.cantar(vc,ats);
        if(tiempo<0){
            tc.sendMessage(Boton.CRUZ_ROJA+" Error interno.").complete();
            return false;
        }tiempo/=1000;
        if(tiempo==0)tc.sendMessage(Boton.NOTA_MUSICAL+" ¡La canción comenzará ahora!").complete();
        else{
            int mins=(int)tiempo/60,secs=(int)tiempo%60;
            tc.sendMessage(Boton.FLOPPY_DISK+" La canción comenzará en "+(mins==0?"":mins+" "+(mins==1?"minuto":"minutos")+(secs==0?"":" y "))+(secs==0?"":secs+" "+(secs==1?"segundo":"segundos"))+".").complete();
        }return true;
    }
}
