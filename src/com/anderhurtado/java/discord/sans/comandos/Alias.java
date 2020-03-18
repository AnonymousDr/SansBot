package com.anderhurtado.java.discord.sans.comandos;

import com.anderhurtado.java.discord.sans.Inicio;
import com.anderhurtado.java.discord.sans.objetos.Boton;
import com.anderhurtado.java.discord.sans.objetos.Cantante;
import com.anderhurtado.java.discord.sans.objetos.Comando;
import com.anderhurtado.java.discord.sans.objetos.Paginacion;
import com.anderhurtado.java.discord.sans.util.Biblioteca;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import javax.annotation.Nullable;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Alias extends Comando{
    public static final byte CANCIONES_PAGINA=3;

    public Alias(){
        super("Obtener la lista de canciones y sus alias","alias");
    }

    @Override
    public void ejecuta(GuildMessageReceivedEvent e,String[] args){
        int pag=0;
        if(args.length>1)try{
            pag=Math.max(Integer.parseInt(args[1])-1,0);
        }catch(Exception Ex){}
        listado(e.getChannel(),pag,e.getMessage());
    }

    public static void listado(TextChannel ch,int pag,@Nullable Message origen){
        try(PreparedStatement ps=Inicio.getPS("SELECT DISTINCT Enlace,(SELECT GROUP_CONCAT(Nombre SEPARATOR '\\\\') FROM Registros WHERE Enlace=R.Enlace) AS Alias FROM Registros R LIMIT ?,?;")){
            ps.setInt(1,CANCIONES_PAGINA*pag);
            ps.setInt(2,CANCIONES_PAGINA);
            ResultSet rs=ps.executeQuery();
            MessageAction msg;
            int tot=0,max=Integer.MAX_VALUE;
            if(rs.next()){
                int[] conteo=Biblioteca.contarCanciones();
                if(origen!=null)origen.addReaction(Boton.CD.CODIGO).queue();
                List<MessageAction> lista=new ArrayList<>();
                lista.add(ch.sendMessage("Listado de canciones:"));
                do{
                    tot++;
                    String[] alias=rs.getString("Alias").split("\\\\");
                    String as=alias[0];
                    for(int x=1;x<alias.length;x++)as+=", "+alias[x];
                    EmbedBuilder eb=new EmbedBuilder();
                    AudioTrackInfo ati=Cantante.getTrack(rs.getString("Enlace")).getInfo();
                    eb.setColor(Color.RED);
                    eb.setAuthor(ati.author);
                    eb.setTitle(ati.title,ati.uri);
                    eb.setImage("https://img.youtube.com/vi/"+ati.identifier+"/maxresdefault.jpg");
                    eb.setDescription("Alias de la canción: "+as);
                    lista.add(ch.sendMessage(eb.build()));
                }while(rs.next());
                lista.forEach(ma->ma.queue());
                max=conteo[Biblioteca.CONTEO_CANCIONES];
                msg=ch.sendMessage("Página "+(pag+1)+"/"+(conteo[Biblioteca.CONTEO_CANCIONES]/CANCIONES_PAGINA+1)+" · "+conteo[Biblioteca.CONTEO_CANCIONES]+" "+(conteo[Biblioteca.CONTEO_CANCIONES]==1?"canción":"canciones")+" y "+conteo[Biblioteca.CONTEO_ALIAS]+" alias en total.");
            }else msg=ch.sendMessage("No hay más canciones.");
            Message m=msg.complete();
            new Paginacion(m,(pag>0?Boton.ATRAS:null),((pag*CANCIONES_PAGINA+tot<max)?Boton.SEGUIR:null)){
                @Override
                public void evento(GuildMessageReactionAddEvent e,Boton b){
                    detener();
                    listado(ch,pag+(b.equals(Boton.SEGUIR)?1:-1),null);
                }
            };

        }catch(Exception Ex){
            Ex.printStackTrace();
            ch.sendMessage("Error interno.").complete();
        }
    }

    private static class Ejecutable implements Runnable{

        final String[] alias;
        final String enlace;
        final TextChannel canal;
        boolean terminado;

        Ejecutable(TextChannel canal,String enlace,String... alias){
            this.alias=alias;
            this.canal=canal;
            this.enlace=enlace;
            new Thread(this).start();
            //run();
        }

        public void run(){

            terminado=true;
        }
    }
}
