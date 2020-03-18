package com.anderhurtado.java.discord.sans.comandos;

import com.anderhurtado.java.discord.sans.objetos.Boton;
import com.anderhurtado.java.discord.sans.objetos.Cantante;
import com.anderhurtado.java.discord.sans.objetos.Comando;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Saltar extends Comando{

    public Saltar(){
        super("Saltar una canción","saltar","skip");
    }

    @Override
    public void ejecuta(GuildMessageReceivedEvent e,String[] args){
        Cantante c=Cantante.getCantante(e.getGuild());
        if(c==null){
            e.getChannel().sendMessage("¡No estoy cantando nada aquí! ¿Karaoke? "+Boton.MICROFONO_NOTAS).complete();
            return;
        }c.saltar();
        e.getChannel().sendMessage("Ahora comenzará la siguiente canción.").complete();
    }
}
