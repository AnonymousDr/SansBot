package com.anderhurtado.java.discord.sans;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.nio.ByteBuffer;

public class Audio implements AudioSendHandler{

    private final AudioPlayer ap;
    private AudioFrame af;

    public Audio(AudioPlayer audio,AudioManager am,AudioTrack at){
        ap=audio;
        ap.playTrack(at);
    }

    @Override
    public boolean canProvide(){
        return(af=ap.provide())!=null;
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
