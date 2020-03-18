package com.anderhurtado.java.discord.sans.util.modelos;

import com.anderhurtado.java.discord.sans.Inicio;
import com.anderhurtado.java.discord.sans.objetos.Cantante;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.List;

import static com.anderhurtado.java.discord.sans.Inicio.YT;

public class VideoBasico{

    public static int BUSCAR=10;

    public static VideoBasico[] buscar(String query){
        return buscar(query,BUSCAR);
    }
    public static VideoBasico[] buscar(String query,int cantidad){
        try{
            SearchResult[] rsts=YT.search().list("id,snippet").setKey(Inicio.YoutubeAPIKey).setQ(query).setType("video").setFields("items(id/videoId,snippet/title)").setMaxResults((long)cantidad).execute().getItems().toArray(new SearchResult[0]);
            VideoBasico[] resultados=new VideoBasico[rsts.length];
            for(int x=0;x<resultados.length;x++){
                String canal=null;
                resultados[x]=new VideoBasico(rsts[x].getId().getVideoId(),rsts[x].getSnippet().getTitle());
            }return resultados;
        }catch(GoogleJsonResponseException GJRE){
            return null;
        }catch(Exception Ex){
            Ex.printStackTrace();
            return null;
        }
    }

    public final String ID,titulo;
    AudioTrack at;

    VideoBasico(String id,String Titulo){
        ID=id;
        titulo=Titulo;
    }

    public AudioTrack getTrack(){
        if(at==null)at=Cantante.getTrack(ID);
        return at;
    }
}
