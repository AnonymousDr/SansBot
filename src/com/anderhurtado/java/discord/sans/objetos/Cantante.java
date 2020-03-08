package com.anderhurtado.java.discord.sans.objetos;

import com.anderhurtado.java.discord.sans.Inicio;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Cantante extends AudioEventAdapter implements AudioSendHandler{

    public static YoutubeAudioTrack getTrack(String ID){
        try{
            AudioItem ai=Inicio.yasm.loadTrackWithVideoId(ID,false);
            if(!(ai instanceof YoutubeAudioTrack))return null;
            return(YoutubeAudioTrack)ai;
        }catch(Exception Ex){
            return null;
        }
    }

    public static boolean puedeCantar(VoiceChannel vc){
        Guild g=vc.getGuild();
        Cantante c=cantantes.get(g.getIdLong());
        if(c==null)return true;
        VoiceChannel conectado=c.am.getConnectedChannel();
        if(conectado==null&&(conectado=c.am.getQueuedAudioConnection())==null)return false;
        return vc.getIdLong()==conectado.getIdLong();
    }

    static final HashMap<Long,Cantante> cantantes=new HashMap<>();

    public static Cantante getCantante(long guildID){
        return cantantes.get(guildID);
    }
    public static Cantante getCantante(Guild g){
        return cantantes.get(g.getIdLong());
    }

    public static long cantar(VoiceChannel vc,String ID){
        return cantar(vc,getTrack(ID));
    }
    public static long cantar(VoiceChannel vc,AudioTrack at){
        Guild g=vc.getGuild();
        if(cantantes.containsKey(g.getIdLong()))return cantantes.get(g.getIdLong()).cantar(at);
        iniciarCantante(vc,at);
        return 0;
    }

    static Cantante iniciarCantante(VoiceChannel vc,String ID){
        return iniciarCantante(vc,getTrack(ID));
    }
    static Cantante iniciarCantante(VoiceChannel vc,AudioTrack at){
        Cantante c=new Cantante(vc);
        c.cantar(at);
        return c;
    }

    final List<AudioTrack> canciones=new ArrayList<>();
    public final AudioPlayer ap=Inicio.apm.createPlayer();
    public final AudioManager am;
    public final long id;
    AudioFrame af;

    private Cantante(VoiceChannel vc){
        Guild g=vc.getGuild();
        am=g.getAudioManager();
        am.openAudioConnection(vc);
        id=g.getIdLong();
        cantantes.put(id,this);
        am.setSendingHandler(this);
        ap.addListener(this);
    }

    public void cancelar(){
        canciones.clear();
        ap.stopTrack();
        am.closeAudioConnection();
        cantantes.remove(id);
    }

    public long cantar(AudioTrack at){
        if(ap.getPlayingTrack()==null){
            ap.playTrack(at);
            return 0;
        }long l=ap.getPlayingTrack().getDuration()-ap.getPlayingTrack().getPosition();
        for(AudioTrack c:canciones)l+=c.getDuration();
        canciones.add(at);
        return l;
    }

    public long cantar(String ID){
        return cantar(getTrack(ID));
    }

    public long cantar(AudioTrack at,int pos){
        if(ap.getPlayingTrack()==null){
            ap.playTrack(at);
            return 0;
        }long l=ap.getPlayingTrack().getDuration()-ap.getPlayingTrack().getPosition();
        for(AudioTrack c:canciones)l+=c.getDuration();
        canciones.add(pos,at);
        return l;
    }

    public long cantar(String ID,int pos){
        return cantar(getTrack(ID),pos);
    }

    @Override
    public void onTrackEnd(AudioPlayer ap,AudioTrack track,AudioTrackEndReason ater){
        if(!canciones.isEmpty()){
            ap.playTrack(canciones.get(0));
            canciones.remove(0);
            return;
        }am.closeAudioConnection();
        cantantes.remove(id);
    }

    public void aleatorizar(){
        AudioTrack[] cs=new AudioTrack[canciones.size()];
        for(int x=0;x<cs.length;x++)cs[x]=canciones.get((int)(Math.random()*(canciones.size())));
        canciones.clear();
        for(int x=0;x<cs.length;x++)canciones.add(cs[x]);
    }

    public void saltar(){
        ap.stopTrack();
    }

    @Override
    public boolean canProvide(){
        return(af=ap.provide())!=null;
    }

    public AudioTrack getCancion(){
        return ap.getPlayingTrack();
    }

    public AudioTrack[] getPlayList(){
        return canciones.toArray(new AudioTrack[0]);
    }

    @Override
    public ByteBuffer provide20MsAudio(){
        return ByteBuffer.wrap(af.getData());
    }

    @Override
    public boolean isOpus(){
        return true;
    }

}
