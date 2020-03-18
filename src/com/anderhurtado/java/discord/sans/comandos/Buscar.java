package com.anderhurtado.java.discord.sans.comandos;

import com.anderhurtado.java.discord.sans.Inicio;
import com.anderhurtado.java.discord.sans.objetos.Boton;
import com.anderhurtado.java.discord.sans.objetos.Comando;
import com.anderhurtado.java.discord.sans.util.modelos.VideoBasico;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class Buscar extends Comando{

    public Buscar(){
        super("Buscar una canción en YouTube","buscar","search");
    }

    @Override
    public void ejecuta(GuildMessageReceivedEvent e,String[] args){
        if(args.length<2){
            e.getChannel().sendMessage("Uso: "+args[0]+" <Argumentos a buscar>").complete();
            return;
        }String q=args[1];
        for(int x=2;x<args.length;x++)q+=" "+args[x];
        VideoBasico[] vbs=VideoBasico.buscar(q);
        if(vbs.length==0){
            e.getChannel().sendMessage(Boton.CRUZ_ROJA+" ¡Sin resultados!").complete();
            return;
        }String txt="";
        for(int x=0;x<vbs.length;){
            VideoBasico vb=vbs[x];
            String info=++x+") "+vb.titulo;
            if(info.length()>100)info=info.substring(0,100)+"…";
            txt+=info+"\n";
        }Message msg=e.getChannel().sendMessage("Mostrando "+args.length+" "+(args.length==1?"resultado":"resultados")+":```nimrod\n"+txt+"```Introduce el número de vídeo para continuar:").complete();
        long tc=e.getChannel().getIdLong(),userID=e.getAuthor().getIdLong();
        Inicio.discord.addEventListener(new ListenerAdapter(){
            @Override
            public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent e){
                if(e.getChannel().getIdLong()!=tc||e.getAuthor().getIdLong()!=userID)return;
                Inicio.discord.removeEventListener(this);
                msg.delete().complete();
                try{
                    int opc=Integer.parseInt(e.getMessage().getContentDisplay());
                    if(opc>0&&opc<=vbs.length)Reproducir.reproducir(e.getChannel(),e.getMember(),null,vbs[opc-1].getTrack());
                }catch(Exception Ex){}
            }
        });
    }
}
